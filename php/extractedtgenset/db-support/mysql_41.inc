<?php
// все следующие далее функции зависят от использования дополнительного слоя абстракции
// от используемого интерфейса для доступа к базе данных - mysql или mysqli
// $glob_mysql_version_api = 41;


if (! isset ($GLOBALS['glob_mysql_version_api']) ){
 die ('fatl error, mysql 40-41 engine cannot work "glob_mysql_version_api" is not defined ');
}

function xxx_query ($command , $link){
	$GLOBALS['globus_count_of_query']++;
	return mysqli_query ( getDBLink($link) , $command);
}

function xxx_free_result ($resource){
	return mysqli_free_result($resource);
}

function xxx_fetch_assoc ($resource){
	return mysqli_fetch_assoc($resource);
}

function xxx_num_fields ($resource){
	return mysqli_num_fields($resource);
}

function xxx_field_name ($resource, $idx){
	$foo = $resource->fetch_field_direct ($idx);
	return $foo->name;
}

function xxx_field_type ($resource, $idx){
	$foo = $resource->fetch_field_direct ($idx);
	return $foo->type;
}


function xxx_field_len ($resource, $idx){
	$foo = $resource->fetch_field_direct ($idx);
	return $foo->max_length;
}

function xxx_field_flags ($resource, $idx){
	$foo = $resource->fetch_field_direct ($idx);
	return $foo->flags;
}


function xxx_real_escape_string ($string, $link = null){
	global $glob_mysql_version_api;
	if ($link){
		return mysqli_real_escape_string( getDBLink($link), $string);
	}
	else{
		return addslashes($string);
	}
}


function xxx_insert_id ($link){
	return mysqli_insert_id( getDBLink($link));
}


function xxx_error ($link){
	return mysqli_error( getDBLink($link));
}

function xxx_errno ($link){
	return mysqli_errno( getDBLink($link));
}


function xxx_num_rows ($link){
	return mysqli_num_rows($link);
}


function xxx_fetch_array ($link, $resulttype){
	return mysqli_fetch_array($link, $resulttype);
}

function xxx_fetch_row ($resultset){
	return mysqli_fetch_row($resultset);
}


function xxx_is_primary_key_field ($parameters){
	return ($parameters & MYSQLI_PRI_KEY_FLAG)  === MYSQLI_PRI_KEY_FLAG ;
}

function xxx_is_autoincrement_field ($parameters){
	return ($parameters & MYSQLI_AUTO_INCREMENT_FLAG)  === MYSQLI_AUTO_INCREMENT_FLAG ;
}

function xxx_is_not_null_field ($parameters){
	return ($parameters & MYSQLI_NOT_NULL_FLAG) === MYSQLI_NOT_NULL_FLAG ;
}


function xxx_is_numeric_field ($parameters){
	return 
	$parameters  === MYSQLI_NUM_FLAG  || 
	$parameters  === MYSQLI_TYPE_DECIMAL  || 
	$parameters  === MYSQLI_TYPE_TINY   || 
	$parameters  === MYSQLI_TYPE_SHORT   || 
	$parameters  === MYSQLI_TYPE_LONG  || 
	$parameters  === MYSQLI_TYPE_FLOAT  || 
	$parameters  === MYSQLI_TYPE_DOUBLE || 
	$parameters  === MYSQLI_TYPE_LONGLONG  || 
	$parameters  === MYSQLI_TYPE_INT24  
	;
}


function xxx_is_datetime_field ($parameters){
	return 
	$parameters  === MYSQLI_TYPE_DATE  || 
	$parameters  === MYSQLI_TYPE_TIME   || 
	$parameters  === MYSQLI_TYPE_DATETIME   || 
	$parameters  === MYSQLI_TYPE_YEAR   || 
	$parameters  === MYSQLI_TYPE_NEWDATE 
	;
}
?>