<?php

function xxx_query ($command , $link){
	$GLOBALS['globus_count_of_query']++;
	logger_write ($command);
	return mysql_query($command , getDBLink($link));
}

function xxx_free_result ($resource){
	return mysql_free_result($resource);
}

function xxx_fetch_assoc ($resource){
	return mysql_fetch_assoc($resource);
}

function xxx_num_fields ($resource){
	return mysql_num_fields($resource);
}

function xxx_field_name ($resource, $idx){
	return mysql_field_name($resource, $idx);
}

function xxx_field_type ($resource, $idx){
	return mysql_field_type($resource, $idx);
}


function xxx_field_len ($resource, $idx){
	return mysql_field_len($resource, $idx);
}

function xxx_field_flags ($resource, $idx){
	return mysql_field_flags($resource, $idx);
}


function xxx_real_escape_string ($string, $link = null){
	//dexport ($GLOBALS);
	//dexport (getDBLink($link));
	if ($link){
		return mysql_real_escape_string($string,  getDBLink($link));
	}
	else{
		return mysql_real_escape_string($string);
	}
}


function xxx_insert_id ($link){
	return mysql_insert_id( getDBLink($link));
}


function xxx_error ($link){
	return mysql_error( getDBLink($link));
}

function xxx_errno ($link){
	return mysql_errno( getDBLink($link));
}


function xxx_num_rows ($link){
	return mysql_num_rows($link);
}


function xxx_fetch_array ($link, $resulttype){
	return mysql_fetch_array($link, $resulttype);
}

function xxx_fetch_row ($resultset){
	return mysql_fetch_row($resultset);
}


function xxx_is_primary_key_field ($parameters){
	return stristr($parameters , 'primary_key') !== false;
}

function xxx_is_autoincrement_field ($parameters){
	return stristr($parameters , 'auto_increment') !== false;
}

function xxx_is_not_null_field ($parameters){
	return stristr($parameters , 'not_null') !== false;
}


function xxx_is_numeric_field ($parameters){
	return $parameters == 'int' || $parameters == 'real';
}


function xxx_is_datetime_field ($parameters){
	return $parameters == 'date' || $parameters == 'datetime' || $parameters == 'timestamp' || $parameters == 'time';
}

?>