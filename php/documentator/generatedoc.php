<?
ob_start();

include_once('docdescriptor.inc');

session_start();

$arr_ini = parse_ini_file('documentator.config.ini', true);
$documentator_docs = $arr_ini ['documentator_docs'];

$d = new TDummyDescriptor  ();
$d = TDummyDescriptor::getConnection();
$d->Connect();

function foo_repl_link($matches)
{
	global $documentator_docs;
	// as usual: $matches[0] is the complete match
	// $matches[1] the match for the first subpattern
	// enclosed in '(...)' and so on
	$matches[1] = trim ($matches[1]);
	$matches[2] = trim ($matches[2]);
	if (strlen($matches[1]) > 0)
	return "<a href='table_{$matches[1]}.html#{$matches[2]}'>{$matches[1]}#{$matches[2]}</a>";
	else
	return "<a href='#{$matches[2]}'>{$matches[1]}#{$matches[2]}</a>";

}


function getDocument ($d, $table_name , $field_name = null){
	global $documentator_docs;

	$condition = 'field_name IS NULL';
	if ($field_name){
		$condition = "field_name = '".mysql_real_escape_string($field_name, $d->db_link)."'";
	}

	$rset = mysql_query('SELECT * FROM '.$documentator_docs.' WHERE table_name = "' .
	mysql_real_escape_string($table_name) . '" AND ' . $condition, $d->db_link);
	if (! $rset)  return null;
	$row = mysql_fetch_assoc($rset);
	mysql_free_result($rset);
	if (!$row) return null;
	$doc =  $row ['document'];
	//не жадный поиск
	return preg_replace_callback('{\[jump\](.*):(.*)\[/jump\]}U' , 'foo_repl_link' , $doc);
}


$dir = 'tmp';
if ((! file_exists($dir))  && (!mkdir ($dir, 0777 )))
die ('cannot create dir for documents: ' . $dir);

$dir = 'tmp/' . $d->db_name . '/';
if ((! file_exists($dir))  && (!mkdir ($dir, 0777 )))
die ('cannot create dir for documents: ' . $dir);

$rSetTbs = mysql_query('SHOW TABLES', $d->db_link);
$arr_tables = array ();
while (($rowTbl = mysql_fetch_array($rSetTbs))){
	if (strcmp($documentator_docs , $rowTbl [0])==0) continue;

	$rSetFlds =  mysql_query('DESCRIBE ' . $rowTbl [0]);
	$array_of_fields = array ();
	while (($rowFlds = mysql_fetch_assoc($rSetFlds))){
		$array_of_fields [] = $rowFlds;
	}
	mysql_free_result($rSetFlds);
	$arr_tables [ $rowTbl [0] ] = $array_of_fields;
}



mysql_free_result($rSetTbs);
$arr_prepared = array ();

foreach ($arr_tables as $table_name => $tab_fields){

	$x = getDocument($d , $table_name);

	$tmp_arr_flds = array ();

	for ($i = 0; $i < count ($tab_fields); $i++){
		$fcolumn = $tab_fields [$i];
		$field_name = $fcolumn ['Field'];
		$field_null = $fcolumn ['Null'];
		$field_type = $fcolumn ['Type'];
		$field_key = $fcolumn ['Key'];
		$field_default = $fcolumn ['Default'];
		$field_extra = $fcolumn ['Extra'];

		if (! $field_default)
		$field_default = '&nbsp;';

		if (! $field_extra)
		$field_extra = '&nbsp;';

		if (! $field_key)
		$field_key = '&nbsp;';

		if (! $field_type)
		$field_type = '&nbsp;';


		if ($field_null == 'YES')
		$field_null = ' checked="checked" ';
		else
		$field_null = '';


		$x = getDocument($d , $table_name, $field_name);

		$tmp_arr_flds [] = array (
		'NAME' => $field_name,
		'NULL' => $field_null,
		'TYPE' => $field_type,
		'KEY' => $field_key,
		'DEFAULT' => $field_default,
		'EXTRA' => $field_extra,
		'DESCRIPTION' => $x,
		);

	}

	$tmp_arr_data = array ();

	$datamustbegen = isset($_REQUEST['mustincludedata']) && $_REQUEST['mustincludedata'] == 'true';
	
	if ($datamustbegen){
		$data = mysql_query('select * from ' . $table_name);
		$arr_data = array ();
		while ($row = mysql_fetch_assoc($data)){
			$arr_data [] = $row;
		}
		mysql_free_result($data);



		for ($i = 0; $i < count($arr_data); $i++){
			$tmp_arr_data_row = array ();
			$row = $arr_data[$i];
			for ($j = 0; $j < count ($tab_fields); $j++){
				$fcolumn = $tab_fields [$j];
				$field_name = $fcolumn ['Field'];
				$tmp_arr_data_row [] = $row[$field_name];
			}
			$tmp_arr_data [] = $tmp_arr_data_row;
		}

	}

	$h = fopen ($dir .'table_'. $table_name . '.html', 'w');
	if (! $h) die ('cannot open file to write');
	
	$INFOABOUT_BASE = $d->toString ();
	$INFOABOUT_TNAME = $table_name;
	$INFOABOUT_FIELD = $tmp_arr_flds;
	$INFOABOUT_DATA_MUSTBE = $datamustbegen;
	$INFOABOUT_DATA = $tmp_arr_data;
	
	//dexport($tmp_arr_data);
	
	ob_start();
	include('template_table.php');
	$table_cnt = ob_get_clean();
	fwrite($h , $table_cnt);
	fclose($h);
}



$h = fopen ($dir . 'index.html', 'w');
ob_start();
$INFOABOUT_BASE = $d->toString ();
$INFOABOUT_TABLE = $arr_tables;
include_once('template_index.php');
fwrite ($h,ob_get_clean());
header("Location: " . $dir . 'index.html' ); /* Redirect browser */

ob_end_flush();
?>