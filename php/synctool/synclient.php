<?php
set_time_limit(1200);

// by black zorro x15@tut.by
// ���� ������ � ������� ��� ����������� ����
// ��� ���������� ��� ������������� ��������� �� ��������� ������ � �� ������� ftp - � ������� �� ������ ������
// ������� ����������� ��������� � ��������������� ����� ������ � ���� php �������� ��� ������������ ����������
// ��� ����� ��� ����� ���� ��������� �� ������� � � ������� ����������
// ����� ����������� � ���, ��� ������ ������ ������ ����� ��� ���-������������� (�.�. ����� ����)
// �� ������� ����������� ������ php ������� ������ ���� ������� ���������� ���������� ��������� � ������������
// ������� ������ ������������������, ��� �������� ��������� ������ ������������ ���, ������, � md5 �� �� �����������
// ������� ��� � �������, ��� ��� �� ������, ������ � ������� � ����� ����������� �.�. ����� ���� ����� ������ ���������
// ��� ������� ����� ������ - ��� ��� ��� ����� ��� ����� ����� ����� / ����� �����������,  � ����� ���� ��������� ����
// ���������� �� ����������� (����� �� ������� md5) �� ��� ����� ������ ���� �����


$ftp_name = 'center'; // ��������� -- ��� ftp �������
$http_name = 'http://center:89/wget0/';//��� � ���� � �������� ��� ��������� ������ �������������

$ftp_user = 'student';// ��� ������������ ��� ������� � ftp
$ftp_password = '1';// ������ ��� ������� � ftp

$http_dir = '../fex/';// ��� �������� ������� �������� ������������� � ����� ������ http �������
$ftp_dir = 'fex';// ��� �������� ftp ����������� �������������
$local_dir = dirname(__FILE__) . '/../synczone/';// ��� ���������� �������� ����������� �������������

$direction_to_server = true;// ����������� �������� ������ � ��������� ������ �� ������ ��� ��������


include_once('syncommon.php');

$h = fopen($http_name . 'synserver.php?root=' .$http_dir , 'rb');
if (! $h){
	die ('cannot load info from remote server');
}

$buf = '';
while (! feof($h)){
	$buf .= fread($h, 4096);
}
fclose($h);


$arr  = unserialize(base64_decode($buf));
$arr_remote = $arr ['files'];

$arr_local = geo_scan($local_dir , '/');



function findRemote ($where , $short , $type){
	for ($i = 0; $i < count($where); $i++){
		$wh = $where [$i];
		if ($wh ['type'] == $type && $wh['short'] == $short ){
			return $wh;
		}
	}
	return false;
}



function generateTaskForAllDir ($source , $source_kind){
	$arr = array ();

	// ���� ��� ���� ���� ���� ��� ����� ������� ������
	if (isset ($source['type'])){
		$source = array ($source);
	}


	for ($i = 0; $i < count($source); $i++){
		$src = $source [$i];
		if (! is_array($src)) continue;
		if (! isset($src ['type'])) continue;

		if ($src ['type'] == 'd'){
			$arr [] = array (
			'type' => 'd',
			'short' => $src ['short'],
			'_kind_' => $source_kind,
			);
			$arr  = array_merge($arr, generateTaskForAllDir($src['_in_'], $source_kind));
		}
		else{
			$arr [] = array (
			'type' => 'f',
			'short' => $src ['short'],
			'_kind_' => $source_kind,
			);
		}
	}

	return $arr;

}

function makeSyncTask ($local, $remote, $direction_to_server){
	if ((!$local) && $remote){
		return generateTaskForAllDir ($remote, 'R');
	}
	if ($local && (!$remote) ){
		return generateTaskForAllDir ($local, 'L');
	}

	$arr_tasks = array ();

	$added_quest = array ();

	// ---------------------------- remote ---------------------------

	for ($i = 0; $i < count($remote); $i++){
		$rem = $remote[$i];
		$rem_short = $rem ['short'];
		$rem_type = $rem ['type'];

		$loc = findRemote ($local, $rem_short, $rem_type);
		if ($loc === false){
			$arr_tasks = array_merge($arr_tasks , makeSyncTask(
			false, $rem, $direction_to_server
			));
			continue;
		}// if not found remote file||dir


		if ($rem_type == 'f'){
			if ( ($rem ['size'] != $loc['size']) || ($rem['md5'] != $loc['md5'] ) ){
				$arr_tasks [] = array (
				'type' => $rem_type,
				'short' => $rem_short,
				'_kind_' => '?',
				);
				$added_quest [] = $rem_short;
			}
		}
		else{
			$arr_tasks = array_merge($arr_tasks, makeSyncTask ($loc['_in_'], $rem['_in_'], $direction_to_server) );
		}

	}

	// ---------------------------- local ---------------------------
	for ($i = 0; $i < count($local); $i++){
		$loc = $local[$i];
		$loc_short = $loc ['short'];
		$loc_type = $loc ['type'];

		$rem = findRemote ($remote, $loc_short, $loc_type);
		if ($rem === false){
			$arr_tasks = array_merge($arr_tasks , makeSyncTask(
			$loc, false, $direction_to_server
			));
			continue;
		}// if not found remote file||dir


		if ($loc_type == 'f'){
			if ( ($rem ['size'] != $loc['size']) || ($rem['md5'] != $loc['md5'] ) ){
				if (! in_array($loc_short, $added_quest)){
					$arr_tasks [] = array (
					'type' => $loc_type,
					'short' => $loc_short,
					'_kind_' => '?',
					);
				}
			}
		}
		else{
			$arr_tasks = array_merge($arr_tasks, makeSyncTask ($loc['_in_'], $rem['_in_'], $direction_to_server) );
		}

	}

	// -----------------------------------------------------------------------------------------
	// -----------------------------------------------------------------------------------------
	// -----------------------------------------------------------------------------------------

	return $arr_tasks;
}



function dexport ($x){
	die ('<pre>'.var_export($x, true).'</pre>');
}


$tasks  = makeSyncTask($arr_local, $arr_remote, $direction_to_server);

//dexport($tasks);


$conn_id = ftp_connect($ftp_name);

// ���� � ������ ������������ � �������
$login_result = ftp_login($conn_id, $ftp_user, $ftp_password);

// �������� ����������
if ((!$conn_id) || (!$login_result)) {
	die ("�� ������� ���������� ���������� � FTP ��������!");
}
else {
	//
}




for ($i = 0; $i < count($tasks); $i++){

	$task = $tasks [$i];
	if ($task ['_kind_'] == 'R' && $direction_to_server) continue;
	if ($task ['_kind_'] == 'L' && (!$direction_to_server)) continue;



	if ($task ['type'] == 'f'){
		// ����� ������ ���������� �� ��������� ������
		// ����������� �����
		$destination_file = $ftp_dir . $task ['short'];
		$source_file = $local_dir .  $task ['short'];


		if ($direction_to_server){
			$upload = ftp_put($conn_id, $destination_file, $source_file, FTP_BINARY);
			if (!$upload) {
				echo "�� ������� ������ �� ������ ����! '" .$source_file. "' <br />";
			} else {
				echo "���� '".$source_file."' ������� ����� �� ������ ��� ������ '".$destination_file."'  <br />";
			}
		}
		else{
			$upload = ftp_get($conn_id, $source_file,$destination_file, FTP_BINARY);
			if (!$upload) {
				echo "�� ������� �������� ����! '" .$source_file. "' � ������� <br />";
			} else {
				echo "���� '".$source_file."' ������� �������� � ������� ��� ������ '".$destination_file."'  <br />";
			}
		}
	}
	else{
		// ���� ��� ������� �� ��� ���� ������� ��� ��� ���

		$destination_file = $ftp_dir . $task ['short'];
		$source_file = $local_dir .  $task ['short'];


		if ($direction_to_server){
			$upload = ftp_mkdir($conn_id, $destination_file);
			if (!$upload) {
				echo "�� ������� ������� ������� �� ������� '" .$source_file. "' <br />";
			} else {
				echo "������� ������� ������� �� ������� '".$destination_file."'  <br />";
			}
		}
		else{
			$upload = mkdir($source_file, 0777);
			if (!$upload) {
				echo "�� ������� ������� ������� �� ��������� ������ '" .$source_file. "' <br />";
			} else {
				echo "������� ������� ������� �� ��������� ������ '".$destination_file."'  <br />";
			}
		}

	}


}

// �������� ����������
ftp_close($conn_id);

?>