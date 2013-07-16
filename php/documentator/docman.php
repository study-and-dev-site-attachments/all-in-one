<?php
 include_once('docdescriptor.inc');
 
 session_start();
 
 $arr_ini = parse_ini_file('documentator.config.ini', true);
 $documentator_docs = $arr_ini ['documentator_docs'];
 
 $d = new TDummyDescriptor  ();
 $d = TDummyDescriptor::getConnection();
 $d->Connect();
 
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
 
 
?>

<html>
<head>
<title>documentator: <?php print $d->toString() ?></title>

<link rel="stylesheet" href="docstyles.css" />

<script>
 function do_popup_table (table_name){
 	w = window.open ('htmlman.php?table_name=' + table_name, 'editor');
 }
 
 function do_popup_field (table_name, field_name){
 	w = window.open ('htmlman.php?table_name=' + table_name + '&field_name='+field_name, 'editor' );
 } 
</script>

</head>

<body>

<div class="make_doc">
	<a href='generatedoc.php'> DOC&rsquo;it (Выполнить генерацию конечного html документа, но только для структуры базы данных)</a>	
</div>

<div class="make_doc_and_data">
	<a href='generatedoc.php?mustincludedata=true'> DOC&rsquo;it (Выполнить генерацию конечного html документа, в том числе с примерами данных из таблиц)</a>	
</div>

<?php
 $arr_widths = array ();
 $arr_widths [] = 300;
 $arr_widths [] = 100;
 $arr_widths [] = 100;
 $arr_widths [] = 100;
 $arr_widths [] = 75;
 $arr_widths [] = 100;
 $sum_of_width = array_sum($arr_widths);
 
print ("
<table style='width: {$sum_of_width}px;' border='0' cellspacing='0' cellpadding='0' class='table_fields_tables_idx'>
	<tr>
		<th class='td_fields_br' >Таблица</td>
		<th class='td_fields_br' width='{$arr_widths[0]}'>Имя поля</th>
		<th class='td_fields_br' width='{$arr_widths[1]}'>Тип поля</th>
		<th class='td_fields_br' width='{$arr_widths[2]}'>Null</th>
		<th class='td_fields_br' width='{$arr_widths[3]}'>Key</th>
		<th class='td_fields_br' width='{$arr_widths[4]}'>Default</th>
		<th class='td_fields_br' width='{$arr_widths[5]}'>Разное</th>
	</tr>
");
?>  			

<?php

 
 foreach ($arr_tables as $tab_name => $tab_fields){
 	print ('<tr>');
 	
 	print ('<td align="left" valign="top" class="td_fields_br" colspan="7">');
 	print ("
	<div title='{$tab_name}' id='id_table_{$tab_name}' class='div_table_start' >
  		<a class='html_editor' href='javascript:do_popup_table(\"{$tab_name}\");'><img src='resources/godoc.jpg' border='0' /></a>{$tab_name}
 	</div> 	
 	");
 	print ("</td>");
 	print ("</tr>");

 	
 	for ($i = 0; $i < count ($tab_fields); $i++){
 		$fcolumn = $tab_fields [$i];
 		$field_name = $fcolumn ['Field'];
 		$field_null = $fcolumn ['Null'];
 		$field_null_ui = '<input type="checkbox" disabled="disabled" '.($field_null == 'YES'?' checked="checked" ':'').'/>';
 		$field_type = $fcolumn ['Type'];
 		$field_key = $fcolumn ['Key'];
 		$field_key_ui = $field_key?$field_key:'&nbsp;';
 		$field_default = $fcolumn ['Default'];
 		$field_default_ui = $field_default?$field_default:'&nbsp;';
 		
 		$field_extra = $fcolumn ['Extra'];
 		$field_extra_ui = $field_extra?$field_extra:'&nbsp;';
 		print ("
  				<tr>
  					<td class='td_fields_br_indent'><a class='html_editor' href='javascript:do_popup_field(\"{$tab_name}\", \"$field_name\");'><img src='resources/godoc.jpg' border='0' /></a></td>
  					<td class='td_fields_br' width='{$arr_widths[0]}'>{$field_name}</td>
  					<td class='td_fields_br' width='{$arr_widths[1]}'>{$field_type}</td>
  					<td class='td_fields_br' width='{$arr_widths[2]}'>{$field_null_ui}</td>
  					<td class='td_fields_br' width='{$arr_widths[3]}'>{$field_key_ui}</td>
  					<td class='td_fields_br' width='{$arr_widths[4]}'>{$field_default_ui}</td>
  					<td class='td_fields_br' width='{$arr_widths[5]}'>{$field_extra_ui}</td>
  				</tr>
  				");
 	}
 }
?>
</table>
 
</body>
</html>