<?php
function dexport ($x){
	die ('<pre>' . var_export($x, true ).'</pre>');
}

set_time_limit(600);// эта строка важна для установки лимита загрузки файлов по времени - на некоторых дешевых хостингах она запрещена

// ---------------------- пример конфигурационного файла ------------------------

// URL: http://www.ya.ru/abba.html?id={$1}&w={$2}&t={$3}&r={$4} -- здесь адрес который будет загружаться в имени адреса присутствуют символы подстановки {$1} и т.д.
// OUTPUT_DIR: ./wget0_output -- здесь задается имя выходного каталога куда будут помещаться сформированные mht файлы
// EMPTY_OUTPUT_DIR: true -- признак того что выходной каталог будет предварительно очищаться от старых файлов
// PARAM: $1 = (1..100) -- здесь и далее задаются правила диапазона значений для параметров из шаблона адреса страницы
// PARAM: $2 = ('abba', 'rabba', 'catta', 'latta')
// PARAM: $3 = (1..20, 30..40, 50..60)
// PARAM: $4 = ('a', 'e')
// EVAL: if (($1 > 20) && $2 == 'abba'){$3 = 71 + $i} -- здесь задается правило модификации позволяющее на основе некоторого условия выполнить модификацию переменных шаблона



// для работы программы необходимо указать параметр fname равный имени файла с настройками

$fname = $_REQUEST['fname'];
if (! isset($_REQUEST['fname'])){
	print ('required param "fname" are not provided');
	die ();
}
if (! file_exists($fname)) {
	print ('config file not found: "'.$fname.'"');
	die ();
}
if (! is_file($fname)) {
	print ('config file "'.$fname.'" not are file, but dir');
	die ();
}

$arr_fname = file ($fname);
$url = false;
$targetdir = false;
$emptyoutputdir = 'false';

$encoding = 'cp1251';

$arr_params = array ();
$arr_evals = array ();
for ($i = 0; $i < count($arr_fname); $i++){
	$line = $arr_fname [$i];
	$arr_got = array ();
	if (! preg_match('/^(\w+)\s*:\s*(.+)$/iUs', $line, $arr_got))
	continue;

	$cmd = strtoupper($arr_got[1]);
	if ($cmd == 'URL'){
		$url = trim($arr_got[2]);
	}
	if ($cmd == 'OUTPUT_DIR'){
		$targetdir = trim($arr_got[2]);
	}
	if ($cmd == 'ENCODING'){
		$encoding = trim($arr_got[2]);
	}
	if ($cmd == 'EMPTY_OUTPUT_DIR'){
		$emptyoutputdir = trim($arr_got[2]);
	}


	if ($cmd == 'PARAM'){
		$param = $arr_got[2];
		$arr_got = array ();
		if (!preg_match('/^(.+)=(.+)$/iUs', $param, $arr_got)) continue;
		$param_var = trim($arr_got [1]);
		$param_expr = trim($arr_got [2]);
		$arr_params [$param_var] = $param_expr;
	}
	if ($cmd == 'EVAL'){
		$param = trim($arr_got[2]);
		$arr_evals [] = $param;
	}

}

if ($url == false){
	print ('file doesnt contain special option: "URL"');
	die ();
}
if ($targetdir == false){
	print ('file doesnt contain special option: "OUTPUT_DIR"');
	die ();
}
if (! file_exists($targetdir)){
	print ('option "OUTPUT_DIR" points to invalid directory (not exists): "'.$targetdir.'" ');
	die ();
}
if (! is_dir($targetdir)){
	print ('option "OUTPUT_DIR" points to invalid directory (is file): "'.$targetdir.'" ');
	die ();
}

$emptyoutputdir = $emptyoutputdir == 'true';
$targetdir = $targetdir . '/';


print ('<pre>');
print_r($url);
print ('<br />');
print_r($targetdir);
print ('<br />');
print_r($arr_params);
print ('</pre>');


$arr_keys = array_keys($arr_params);


function scan_go ($arr_params, $arr_keys ,$idx, $url, $targetdir, $arr_prepared_params, $evals){
	if ($idx > count($arr_keys)) return;
	if ($idx < 0)return;

	if ($idx == count($arr_keys)){
		// make remote call

		// next -- evals --
		foreach ($evals as $key => $val){
			$val = preg_replace('/(\$\d+)/', ' $arr_prepared_params[ \'\\1\' ]', $val);
			//print ('eval:' . $val. '<br />');
			eval($val);
		}
		// --  prepare url --

		foreach ($arr_prepared_params as $key => $val){
			$url = str_replace('{'.$key.'}', str_replace('\'', '', $val), $url);
		}

		makeSaveCall ($url, $rez);

		return;
	}

	$var  = $arr_keys[$idx];
	$expr = $arr_params [$arr_keys[$idx]];

	$drop = str_replace('(', '', $expr);
	$drop = str_replace(')', '', $drop);

	$arr_vars = explode(',', $drop);
	$arr_must_be_values = array ();

	for ($j = 0; $j < count($arr_vars); $j++){
		$arr_values_exp = array ();
		$val = trim($arr_vars [$j]);
		$arr_min_max = explode('..', $val);

		if (count($arr_min_max) == 2){
			for ($t = $arr_min_max [0]; $t <= $arr_min_max [1]; $t++ )
			$arr_must_be_values [] = $t;
		}
		else{
			$arr_must_be_values [] = $val;
		}

	}// -- j

	for ($j = 0; $j < count($arr_must_be_values); $j++){
		$tmp_arr_prepared_params = $arr_prepared_params;
		$tmp_arr_prepared_params [$var] = $arr_must_be_values[$j];
		scan_go ($arr_params, $arr_keys, $idx + 1, $url, $targetdir,$tmp_arr_prepared_params , $evals);
	}//

}


function extensionToMimeType ($ext){
	if ($ext == '.jpg') return 'image/jpg';
	if ($ext == '.jpeg') return 'image/jpg';

	if ($ext == '.gif') return 'image/gif';

	if ($ext == '.png') return 'image/png';

	if ($ext == '.txt') return 'text/plain';
	if ($ext == '.htm') return 'text/html';
	if ($ext == '.html') return 'text/html';

	if ($ext == '.js') return 'text/javascript';

	if ($ext == '.css') return 'text/stylesheet';

	return 'text/plain';
}

function rpl_obj_to_id_oj ($arr_got){
	//dexport($arr_got);

	$all_lower = strtolower($arr_got[0]);

	$rev = strrev($arr_got[1]);
	$pos_slash = strpos($rev , '.');
	if ($pos_slash !== false){
		$rev = substr($rev , 0, $pos_slash);
		$rev = strrev($rev);
	}
	else
	$rev = '';

	$extension = '.' . $rev;


	if (strpos($all_lower,'<img' ) === 0){
		$GLOBALS ['gtmp_repla_tasks'] ['img'] [] =array ('url' => $arr_got [1], 'mime' => extensionToMimeType($extension));
		$GLOBALS ['gtmp_repla_regtypes'] [$GLOBALS ['gtmp_repla_numobj']] = extensionToMimeType($extension);
		$GLOBALS ['gtmp_repla_numobj']++;
		//return str_replace($arr_got[1], 'internal_img_'.($GLOBALS ['gtmp_repla_numobj'] - 1) . $extension, $arr_got[0]);
	}

	if (strpos($all_lower,'<script' ) === 0){
		$GLOBALS ['gtmp_repla_tasks'] ['javascript'] [] =array ('url' => $arr_got [1], 'mime' => extensionToMimeType($extension));
		$GLOBALS ['gtmp_repla_regtypes'] [$GLOBALS ['gtmp_repla_numobj']] = extensionToMimeType($extension);
		$GLOBALS ['gtmp_repla_numobj']++;
		//return str_replace($arr_got[1], 'internal_javacript_'.($GLOBALS ['gtmp_repla_numobj'] - 1). $extension, $arr_got[0]);
	}

	if (strpos($all_lower,'<link' ) === 0){
		$GLOBALS ['gtmp_repla_tasks'] ['link'] [] =array ('url' => $arr_got [1], 'mime' => extensionToMimeType($extension));
		$GLOBALS ['gtmp_repla_regtypes'] [$GLOBALS ['gtmp_repla_numobj']] = extensionToMimeType($extension);
		$GLOBALS ['gtmp_repla_numobj']++;
		//return str_replace($arr_got[1], 'internal_link_'.($GLOBALS ['gtmp_repla_numobj'] - 1). $extension, $arr_got[0]);
	}
	return $arr_got [0];
}

// ------------------------------------------------------------------------------
function makeUrl ($url , $base_of, $link){
	$small_link = strtolower($link);
	if (
	strpos($small_link, 'http://') !== false ||
	strpos($small_link, 'https://') !== false ||
	strpos($small_link, 'ftp://') !== false
	){
		return  $link;
	}

	$uni_base = $url;
	if ($base_of != false && $base_of !=null && $base_of != '')
	$uni_base = $base_of;
	$uni_pa = parse_url($uni_base);

	//Array ( [scheme] => http [host] => aaa.com [port] => 89 [path] => /ru/index.html [query] => a=123&a=4/2 )
	//$path_parts['dirname'],
	$clear_dir = pathinfo($uni_pa ['path']);
	$clear_dir = $clear_dir ['dirname'];
	$next_url = $uni_pa['scheme'] . '://' . $uni_pa['host'] . (isset($uni_pa['port'])?':'.$uni_pa['port']:'') .  $clear_dir . '/' . $link;
	return $next_url;
}

// ------------------------------------------------------------------------------
function getNameFromUrl ($uni_base){
	$uni_pa = parse_url($uni_base);
	$clear_dir = pathinfo($uni_pa ['path']);
	return $clear_dir ['basename'];
}

// ------------------------------------------------------------------------------

function makeSaveCall ($url, $content){
	//
	$GLOBALS ['cnt_requests']++;
	$encoding = $GLOBALS ['encoding'];
	$emptyoutputdir = $GLOBALS['emptyoutputdir'];
	$targetdir= $GLOBALS['targetdir'];

	print ('got: ' . $url.  '<br />');
	$h = @fopen($url, 'r');
	if (! $h) return;

	$rez = '';
	while (! feof($h)){
		$rez .= fread($h , 1024);
	}
	fclose($h);

	$arr_got = array ();
	$title  = 'File_' . time() . '_' . rand ();
	if (preg_match('/<title>(.+)<\/title>/iUs', $rez , $arr_got)){
		$title = $arr_got [1];
	}

	$base_of = false;
	if (preg_match('/<base href=["\']([^"\']+)["\']/iUs', $rez , $arr_got)){
		$base_of = $arr_got [1];
	}




	$title = str_replace(array ('/', '\\', ':', ';', '&', '@' , "\n", "\r", "\t"), array ('', '', '', '', '', '', '', '', '') , $title);
	$title = str_replace('  ', ' ', $title);
	$fname = $targetdir .  $title . '.mht';
	while (file_exists($fname)){
		$fname = $targetdir .  'File_' . time() . '_' . rand ();
	}
	// img
	// css
	// javascript

	$arr_images = array ();
	$GLOBALS ['gtmp_repla_numobj'] = 0;
	$GLOBALS ['gtmp_repla_regtypes'] = array ();
	$GLOBALS ['gtmp_repla_tasks'] = array ('img' => array () , 'javascript' => array () , 'link' => array ());
	$rez = preg_replace_callback('/<img[^>]src=["\']([^"\']+)["\']/iUs', 'rpl_obj_to_id_oj', $rez);
	$rez = preg_replace_callback('/<script[^>]src=["\']([^"\']+)["\']/iUs', 'rpl_obj_to_id_oj', $rez);
	$rez = preg_replace_callback('/<link[^>]href=["\']([^"\']+)["\']/iUs', 'rpl_obj_to_id_oj', $rez);

	define('BOUNDARY' , '----------LFGcPbBiiZ58X33ZWbYGW0');

	$super_rez = 'Content-Type: multipart/related; start=<FEGEREATED_7876_RTRECBJHG_657657_HUTYRDRDF>; boundary=----------LFGcPbBiiZ58X33ZWbYGW0
Content-Location: '.$url.'
Subject: '.$title.'
MIME-Version: 1.0

--'.BOUNDARY.'
Content-Disposition: inline; filename='.getNameFromUrl($url).'
Content-Type: text/html; name='.getNameFromUrl($url).'
Content-Id: <FEGEREATED_7876_RTRECBJHG_657657_HUTYRDRDF>
Content-Location: '.$url.'
Content-Transfer-Encoding: 8bit
		
';


	$super_rez .= $rez;

// ---- images --

	foreach ($GLOBALS ['gtmp_repla_tasks']['img'] as $idx => $value){
		$url_next = makeUrl ($url, $base_of, $value['url']);
		//dexport($url_next);
		$h = @fopen($url_next, 'rb');
		if (! $h) continue;
		$resource = '';
		while (! feof($h)){
			$resource .= fread($h, 1024);
		}
		fclose($h);

		$super_rez .= "\n\n";

		$super_rez .= '--'.BOUNDARY.'
Content-Disposition: inline; filename='.getNameFromUrl($value ['url']) .'
Content-Type: '.$value['mime'].'; name='.getNameFromUrl ($value ['url']) .'
Content-Location: '.$value ['url'] .'
Content-Transfer-Encoding: Base64

';
		$super_rez .= base64_encode($resource);

	}

	
// ----- css link  ---

	foreach ($GLOBALS ['gtmp_repla_tasks']['link'] as $idx => $value){
		$url_next = makeUrl ($url, $base_of, $value['url']);
		$h = @fopen($url_next, 'rb');
		if (! $h) continue;
		$resource = '';
		while (! feof($h)){
			$resource .= fread($h, 1024);
		}
		fclose($h);

		$super_rez .= "\n\n";

		$super_rez .= '--'.BOUNDARY.'
Content-Disposition: inline; filename='.getNameFromUrl($value ['url']) .'
Content-Type: '.$value['mime'].'; name='.getNameFromUrl ($value ['url']) .'
Content-Location: '.$value ['url'] .'
Content-Transfer-Encoding: 8bit

';
		$super_rez .= $resource;

	}


// ----- javascript  ---


	foreach ($GLOBALS ['gtmp_repla_tasks']['javascript'] as $idx => $value){
		$url_next = makeUrl ($url, $base_of, $value['url']);
		$h = @fopen($url_next, 'rb');
		if (! $h) continue;
		$resource = '';
		while (! feof($h)){
			$resource .= fread($h, 1024);
		}
		fclose($h);

		$super_rez .= "\n\n";

		$super_rez .= '--'.BOUNDARY.'
Content-Disposition: inline; filename='.getNameFromUrl($value ['url']) .'
Content-Type: '.$value['mime'].'; name='.getNameFromUrl ($value ['url']) .'
Content-Location: '.$value ['url'] .'
Content-Transfer-Encoding: 8bit

';
		$super_rez .= $resource;

	}

	
	$super_rez .= "\n" . '
--'.BOUNDARY.'--
';
	
	$rez = false;
	
	$h = fopen($fname, 'w');
	fwrite($h, $super_rez);
	fclose($h);

	// parse -- file --
}


// -- clear output dir --
if ($emptyoutputdir){
	$h = opendir($targetdir  );
	while($file = readdir($h)){
		if ($file == '..' || $file == '.') continue;
		$fullname = $targetdir . $file;
		@unlink($fullname);
	}
	closedir($h);
}

$GLOBALS ['cnt_requests'] = 0;
// -- make call --
$arr_prepared_params = array ();
scan_go ($arr_params, $arr_keys, 0, $url, $targetdir,$arr_prepared_params , $arr_evals);
?>