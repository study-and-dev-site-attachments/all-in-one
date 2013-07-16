<?php
define('GLOB_POLICY_FILTER_AND_LINK_TO_CACHE_ENABLED',1);


$globus_ListOfCachUsesRecordsets = array ();

if (preg_match('/^5/', PHP_VERSION)){
	function makeFilterCacheEnabled ($objbyref, $descriptor_object){
		global $globus_ListOfCachUsesRecordsets;
		if (GLOB_POLICY_FILTER_AND_LINK_TO_CACHE_ENABLED){
			$objbyref->SetOptimizeHints_EnableFilterCache (true);
		}
	}
	function makeFilterCacheDisabled ($objbyref, $descriptor_object){
		global $globus_ListOfCachUsesRecordsets;
		if (GLOB_POLICY_FILTER_AND_LINK_TO_CACHE_ENABLED){
			$objbyref->SetOptimizeHints_EnableFilterCache (false);
		}
	}
}
else{
	function makeFilterCacheEnabled (& $objbyref, $descriptor_object){
		global $globus_ListOfCachUsesRecordsets;
		if (GLOB_POLICY_FILTER_AND_LINK_TO_CACHE_ENABLED){
			$objbyref->SetOptimizeHints_EnableFilterCache (true);
		}
	}
	function makeFilterCacheDisabled (& $objbyref, $descriptor_object){
		global $globus_ListOfCachUsesRecordsets;
		if (GLOB_POLICY_FILTER_AND_LINK_TO_CACHE_ENABLED){
			$objbyref->SetOptimizeHints_EnableFilterCache (false);
		}
	}
}

function discardAllObjectsCachePolicy (){
	global $globus_ListOfCachUsesRecordsets;
	foreach ($globus_ListOfCachUsesRecordsets as $table => $policy){
		if (preg_match('/^5/', PHP_VERSION)){
			$foo =  $GLOBALS ['x_TREF_' . $table];
			if ($foo)
			$foo->SetOptimizeHints_EnableFilterCache (false);

			global $glob_PHP_JNDI_Lookup_Cache;
			$foo2 =  $glob_PHP_JNDI_Lookup_Cache[$table];
			if ($foo2)
			$foo2->SetOptimizeHints_EnableFilterCache (false);

		}
		else{
			$foo = & $GLOBALS [ $table];
			if ($foo){
				$foo->SetOptimizeHints_EnableFilterCache (false);
			}
			else{
				//print 'fail for ' . '' . $table . '<br />';
			}
			global $glob_PHP_JNDI_Lookup_Cache;
			$foo2 = & $glob_PHP_JNDI_Lookup_Cache[$table];
			if ($foo2)
			$foo2->SetOptimizeHints_EnableFilterCache (false);
		}
	}
}


if (! function_exists('getmicrotime')){
	function getmicrotime()
	{
		list($usec, $sec) = explode(" ", microtime());
		return ((float)$usec + (float)$sec);
	}
}


if  (! isset($glob_SiteRoot))
$glob_SiteRoot = realpath(dirname(__FILE__) . '/../'). '/';


include_once ($glob_SiteRoot . 'db-support/db_connection.inc');
include_once ($glob_SiteRoot . 'db-support/tbasetable.inc');
include_once ($glob_SiteRoot . 'db-support/mysql_40_41.inc');
include_once ($glob_SiteRoot . 'db-support/tgenericentityset.inc');
include_once ($glob_SiteRoot . 'db-support/lite_db_engine.inc');


$_start_of_init_db_connections = getmicrotime();

//******************************************************************************************************************
//******************************************************************************************************************
//******************************************************************************************************************
//******************************************************************************************************************
//******************************************************************************************************************


class TBase_TREF{
	function  GetTableName (){
		return $this->table_name;// полагаясь на будущие реализации кода
	}
	function GetRecordSet ($tref_object = false){
		if ($tref_object == false)
		return $GLOBALS['glob_PHP_JNDI_Lookup_Cache'][$this->GetTableName()];
		else
		return $GLOBALS['glob_PHP_JNDI_Lookup_Cache'][$tref_object->GetTableName()];
	}

	var $table_name_original = null;
	
}

//**************************************************************************
$glob_PHP_JNDI_Lookup_Cache = array ();// в этом массиве будут храниться клоны созданных объектов таблиц для ускорения доступа
//**************************************************************************


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


$GLOBALS['glob_PHP_JNDI_Lookup_Cache'] = $glob_PHP_JNDI_Lookup_Cache;

// завершаем оценку времени установления первоначальных соединений с БД
$_end_of_init_db_connections = getmicrotime();
$_SESSION ['internals:profiler:timetoinitdbconnections'] = ($_end_of_init_db_connections - $_start_of_init_db_connections);

//************************************************************************************************
function dexport ($x){
	die ('<pre>' .  var_export($x , true) . '</pre>');
}

?>