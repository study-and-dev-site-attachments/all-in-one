<?php
include_once('syncommon.php');

if (! isset($_REQUEST ['root'])){
	die ('fatl error: root param are not provided');
}

$root = $_REQUEST ['root'];

print base64_encode(serialize(array (
	'start_process' => time (),
	'root' => $root,
	'files' => geo_scan(dirname(__FILE__) .'/'. $root . '/', '/')
)));


?>