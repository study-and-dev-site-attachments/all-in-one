<?php

$GLOBALS['globus_profiler_logger_file'] = realpath(dirname(__FILE__) . '/../tmp/.log/'). '/sqlqueries.log';
$GLOBALS['globus_profiler_logger_file_raw'] = realpath(dirname(__FILE__) . '/../tmp/.log/'). '/sqlqueries_raw.log';
define('__DEBUG__', 0);

function logger_init (){
	if (! __DEBUG__) return;
	if (file_exists($GLOBALS['globus_profiler_logger_file']))
	unlink($GLOBALS['globus_profiler_logger_file']);
	if (file_exists($GLOBALS['globus_profiler_logger_file'])){
		$h = fopen($GLOBALS['globus_profiler_logger_file'] ,  'w');
		fwrite($h , "");
		fclose($h);
	}
	
	if (file_exists($GLOBALS['globus_profiler_logger_file_raw']))
	unlink($GLOBALS['globus_profiler_logger_file_raw']);
	if (file_exists($GLOBALS['globus_profiler_logger_file_raw'])){
		$h = fopen($GLOBALS['globus_profiler_logger_file_raw'] ,  'w');
		fwrite($h , "");
		fclose($h);
	}
	
}
function logger_write ($command){
	if (! __DEBUG__) return;
	$h = fopen($GLOBALS['globus_profiler_logger_file'] ,  'a');
	fwrite($h , "#(beginlog:sql)\n" . $command . "\n#(endlog:sql)\n");
	fwrite($h , "#(beginlog:trace)\n" . var_export(debug_backtrace(), true). "\n#(endlog:trace)\n");
	fclose($h);
	$h = fopen($GLOBALS['globus_profiler_logger_file_raw'] ,  'a');
	fwrite($h , "#(beginlog:sql)\n" . $command . "\n#(endlog:sql)\n");
	fclose($h);
}

logger_init ();// инициализация подсистемы журналирования



// все следующие далее функции зависят от использования дополнительного слоя абстракции
// от используемого интерфейса для доступа к базе данных - mysql или mysqli
// $glob_mysql_version_api = 41;


if (! isset ($GLOBALS['glob_mysql_version_api']) ){
	die ('fatl error, mysql 40-41 engine cannot work "glob_mysql_version_api" is not defined ');
}

if ($GLOBALS['glob_mysql_version_api'] == 40)
include_once ('mysql_40.inc');
else
include_once ('mysql_41.inc');

?>