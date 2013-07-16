<?php

$GLOBALS['glob_mysql_version_api'] = 40;

// надо определить какая версия mysql используется
//***************************************************************


$param_db_host  = 'localhost';
$param_db_user = 'root';
$param_db_password = '';
$param_db_db = 'pochta';
$param_db_setnames = 'cp1251';


// ***********  BEGIN: connect to server  ****************************
$glob_DB_name = $param_db_db;
$GLOBALS['glob_DB_name'] = $glob_DB_name;
function getGlob_DB_name (){
	return $GLOBALS['glob_DB_name'];
}


$f_90_78_link = mysql_connect($param_db_host , $param_db_user, $param_db_password);
$GLOBALS['f_90_78_link'] = $f_90_78_link;

if (! $f_90_78_link) die ('не возможно установить соединение с базой данных');
if (!mysql_select_db($glob_DB_name, $f_90_78_link)) die ('не могу выбрать базу данных');

if ($param_db_setnames){
	mysql_query('set character_set_client=' . $param_db_setnames) or die("set character_set_client=". $param_db_setnames);
	mysql_query('set character_set_connection=' . $param_db_setnames) or  die("set character_set_client=". $param_db_setnames);
	mysql_query('set character_set_database=' . $param_db_setnames )  or die("set character_set_client=" . $param_db_setnames);
	mysql_query('set character_set_results=' . $param_db_setnames) or die("set character_set_client=" . $param_db_setnames);
	mysql_query('set character_set_server=' . $param_db_setnames) or die("set character_set_client=" . $param_db_setnames);
}
// ***********  END: connect to server  ****************************

define ('RESOURCE_MYSQL_ANGLE' , 'resource:mysql:angle');


function getDBLink ($link_name_url){
	global $f_90_78_link;
	if ($link_name_url == RESOURCE_MYSQL_ANGLE)
	return $GLOBALS['f_90_78_link'];
	trigger_error("getDBLink Unknown Resource: \"" . $link_name_url . "\" trace: " .
	var_export (debug_backtrace(), true) , E_USER_ERROR);
}


?>