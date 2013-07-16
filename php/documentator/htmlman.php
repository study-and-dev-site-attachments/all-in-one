<?php
include_once('docdescriptor.inc');
session_start();

$arr_ini = parse_ini_file('documentator.config.ini', true);
$documentator_docs = $arr_ini ['documentator_docs'];
$documentator_docs_encoding = $arr_ini ['documentator_docs_encoding'];

$crt_enc = '';
if ($documentator_docs_encoding != '')
	$crt_enc = ' default character set ' . $documentator_docs_encoding;
$sql_create = 'CREATE TABLE '.$documentator_docs.' (table_name VARCHAR(200) NOT NULL , field_name VARCHAR(200) NULL, document MEDIUMTEXT NULL) ' . $crt_enc;
	

$d = new TDummyDescriptor  ();
$d = TDummyDescriptor::getConnection();
$d->Connect();

?>
<html>
 <head>
	<title>documentator: <?php print $d->toString() ?></title>

	<link rel="stylesheet" href="docstyles.css" />


    <script language="javascript" type="text/javascript" src="tinymce/tiny_mce.js">
    <!-- aa -->
	</script>

    <script language="javascript" type="text/javascript">
    tinyMCE.init({
    	mode : "textareas",
    	theme : "simple"
    });
</script>

</head>

<BODY>

<?php

foreach ($_REQUEST as $k => $v){
	//$str = iconv("UTF-8",  "windows-1251", $v);
	$_REQUEST [$k] =stripslashes($_REQUEST [$k]);
}

if (isset($_REQUEST ['bigtext'])){
	$doc = $_REQUEST ['bigtext'];

	$table_name = $_REQUEST ['table_name'];
	$field_name = null;
	$condition = 'field_name IS NULL';
	if (isset ($_REQUEST['field_name']) && $_REQUEST['field_name']){
		$field_name =  $_REQUEST['field_name'];
		$condition = "field_name = '".mysql_real_escape_string($field_name, $d->db_link)."'";
	}

	$rset = mysql_query('SELECT * FROM '.$documentator_docs.' WHERE table_name = \'' .
	mysql_real_escape_string($table_name) . '\' AND ' . $condition, $d->db_link);
	if (! $rset)  {
		mysql_query($sql_create , $d->db_link);
		$rset = mysql_query('SELECT * FROM '.$documentator_docs.' WHERE table_name = \'' .
		mysql_real_escape_string($table_name) . '\ AND ' . $condition, $d->db_link);
	}
	if (mysql_fetch_array($rset))
	$rset = mysql_query('UPDATE '.$documentator_docs.' SET document = \'' . $doc . '\' WHERE table_name = \'' .
	mysql_real_escape_string($table_name) . '\' AND ' . $condition, $d->db_link);
	else{
		if ((!isset ($_REQUEST['field_name'])) || (!$_REQUEST['field_name']))
			$condition = 'field_name = NULL ';
		$rset = mysql_query('INSERT INTO '.$documentator_docs.' SET document = \'' . $doc . '\' , table_name = \'' .
		mysql_real_escape_string($table_name) . '\' , ' . $condition, $d->db_link);
	}
	
	print '<div class="msg_about_save">';
	if ($rset)
		print ("Порядок, изменения были успешно внесены");
	else
		print ("Проблемы, сохранить изменения не удалось: "  . mysql_error($d->db_link));
	print '</div>';		
}
?>
  
<form action='htmlman.php' method='post'>
	
<div align="center">
<?php


$table_name = $_REQUEST ['table_name'];
$field_name = null;
$condition = 'field_name IS NULL';
if (isset ($_REQUEST['field_name']) && $_REQUEST['field_name']){
	$field_name =  $_REQUEST['field_name'];
	$condition = "field_name = '".mysql_real_escape_string($field_name, $d->db_link)."'";
}


$rset = mysql_query('SELECT * FROM '.$documentator_docs.' WHERE table_name = "' .
mysql_real_escape_string($table_name) . '" AND ' . $condition, $d->db_link);
if (! $rset)  {
	mysql_query($sql_create , $d->db_link);
	$rset = mysql_query('SELECT * FROM '.$documentator_docs.' WHERE table_name = "' .
	mysql_real_escape_string($table_name) . '" AND ' . $condition, $d->db_link);
}


print ("<input type='hidden' name='table_name' value='{$table_name}'>\n");
print ("<input type='hidden' name='field_name' value='{$field_name}'>\n");
print ("<div class='msg_about_what_edit'>Коментарий к таблице: '{$table_name}' ");
if ($field_name)
	print (" / Поле : '{$field_name}'");
print '</div>';



$html = '';
if (($row = mysql_fetch_assoc($rset)))
$html = $row ['document'];


print ("<TEXTAREA style='margin-left: auto; margin-right: auto; width: 500px;' name='bigtext' id='bigtext' rows='25' cols='70'>{$html}</TEXTAREA>");

?> 
<table width="100%" border="0">
	<tr>
		<td align="center" valign="top" width="50%">
			<input type='submit' value='Save (Сохранить комментарий)' class="btn_save_doc" /> 			
		</td>
		<td align="center" valign="top" width="50%">
			<input type='reset' value='(Reset) Сбросить' class="btn_cancel_doc"/>
		</td>
		
	</tr>
</table>



</div>
</form>  
</body>
</html>
