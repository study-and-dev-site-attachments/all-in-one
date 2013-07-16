<?php
 ob_start();
 include_once('docdescriptor.inc');
?>
<?php

 foreach ($_REQUEST as $k => $v){
	//$str = iconv("UTF-8",  "windows-1251", $v);
	$_REQUEST [$k] =stripslashes($_REQUEST [$k]);
 }

 //die (var_export($_REQUEST , true)); 
 session_start();

 $d = new TDummyDescriptor  ();
 $d = $_SESSION ['docman-connector'];
 $d->Connect ();
 
 $doc = $_REQUEST ['bigtext'];
 
 $table_name = $_REQUEST ['table_name'];
 $field_name = null;
 $condition = 'field_name IS NULL';
 if (isset ($_REQUEST['field_name']) && $_REQUEST['field_name']){
  $field_name =  $_REQUEST['field_name'];
  $condition = "field_name = '".mysql_real_escape_string($field_name, $d->db_link)."'";
 }
  
 //die ('SELECT * FROM documentator_docs WHERE table_name = \'' .
//   mysql_real_escape_string($table_name) . '\' AND ' . $condition);
 
 $rset = mysql_query('SELECT * FROM documentator_docs WHERE table_name = \'' .
   mysql_real_escape_string($table_name) . '\' AND ' . $condition, $d->db_link);
 if (! $rset)  {
 	mysql_query('CREATE TABLE documentator_docs (table_name VARCHAR(200) , field_name VARCHAR(200), document MEDIUMTEXT)' , $d->db_link);
	$rset = mysql_query('SELECT * FROM documentator_docs WHERE table_name = \'' .
   	mysql_real_escape_string($table_name) . '\ AND ' . $condition, $d->db_link);
 }
 if (mysql_fetch_array($rset))
	$rset = mysql_query('UPDATE documentator_docs SET document = \'' . $doc . '\' WHERE table_name = \'' .
   	mysql_real_escape_string($table_name) . '\' AND ' . $condition, $d->db_link);
 else{
   if ((!isset ($_REQUEST['field_name'])) || (!$_REQUEST['field_name']))
      $condition = 'field_name = NULL ';
    //die ('INSERT INTO documentator_docs SET document = \'' . $doc . '\' , table_name = \'' .
   	//mysql_real_escape_string($table_name) . '\' , ' . $condition) 	;
	$rset = mysql_query('INSERT INTO documentator_docs SET document = \'' . $doc . '\' , table_name = \'' .
   	mysql_real_escape_string($table_name) . '\' , ' . $condition, $d->db_link);
 }
 if ($rset) 
  print ("<a href='htmlman.php?table_name={$table_name}&field_name={$field_name}'> Порядок (Success), назад:  </a>");
 else 
  print ("<a href='htmlman.php?table_name={$table_name}&field_name={$field_name}'> Проблемы (Failed) "  . mysql_error($d->db_link) . ", назад: </a>"); 
?>

<?php
 ob_end_flush();
?>