<?php

function geo_scan ($dir, $short_name){
	$arr = array ();

	$h = opendir($dir);
	if ($h){
		while (($file = readdir($h)) !== false){
			if ($file == '.' || $file == '..') continue;

			$fullname = $dir . $file;

			if (is_dir($fullname)){

				$arr [] = array (
				'type' => 'd',
				'name' => $fullname,
				'short' => $short_name . $file,
				'_in_' =>  geo_scan($fullname . '/', $short_name . $file . '/')
				)		;

			}
			else{
				$arr [] = array (
				'type' => 'f',
				'name' => $fullname,
				'short' => $short_name . $file,
				'size' => filesize($fullname),
				'lastmod' =>  filemtime($fullname),
				'md5' => md5_file($fullname)
				);
			}

		}
		closedir($h);
	}
	return $arr;
}

?>