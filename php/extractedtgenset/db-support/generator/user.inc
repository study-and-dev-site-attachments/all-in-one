<?php 
class TREF_user  extends TBase_TREF {
	var $table_name =  'user';
	function TREF_user () {}
	var $UserID =  'UserID';
	var $UserFIO =  'UserFIO';
	var $BirthDate =  'BirthDate';
	var $Sex =  'Sex';

}

$x_TREF_user = new TREF_user();
$w_TREF_user = new TGenericEntitySet(RESOURCE_MYSQL_ANGLE , $x_TREF_user->table_name);
$glob_PHP_JNDI_Lookup_Cache [$x_TREF_user->table_name] = $w_TREF_user;
makeFilterCacheEnabled ($w_TREF_user, '$x_TREF_user');

?>