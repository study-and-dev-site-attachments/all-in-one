<?php 
class TREF_newspaper  extends TBase_TREF {
	var $table_name =  'newspaper';
	function TREF_newspaper () {}
	var $NewspaperID =  'NewspaperID';
	var $NewspaperTitle =  'NewspaperTitle';
	var $Code =  'Code';
	var $DefaultPrice =  'DefaultPrice';

}

$x_TREF_newspaper = new TREF_newspaper();
$w_TREF_newspaper = new TGenericEntitySet(RESOURCE_MYSQL_ANGLE , $x_TREF_newspaper->table_name);
$glob_PHP_JNDI_Lookup_Cache [$x_TREF_newspaper->table_name] = $w_TREF_newspaper;
makeFilterCacheEnabled ($w_TREF_newspaper, '$x_TREF_newspaper');

?>