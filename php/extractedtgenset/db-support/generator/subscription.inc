<?php 
class TREF_subscription  extends TBase_TREF {
	var $table_name =  'subscription';
	function TREF_subscription () {}
	var $SubscriptionID =  'SubscriptionID';
	var $NewspaperID =  'NewspaperID';
	var $UserID =  'UserID';
	var $StartRange =  'StartRange';
	var $EndRange =  'EndRange';
	var $RealPrice =  'RealPrice';

}

$x_TREF_subscription = new TREF_subscription();
$w_TREF_subscription = new TGenericEntitySet(RESOURCE_MYSQL_ANGLE , $x_TREF_subscription->table_name);
$glob_PHP_JNDI_Lookup_Cache [$x_TREF_subscription->table_name] = $w_TREF_subscription;
makeFilterCacheEnabled ($w_TREF_subscription, '$x_TREF_subscription');

?>