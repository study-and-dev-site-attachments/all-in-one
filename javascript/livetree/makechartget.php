<?php
if (! function_exists('dexport')){
	function dexport ($x){
		die ('<pre>'.var_export($x, true).'</pre>');
	}
}


function get_spaces_count ($s){
	return strlen($s) - strlen(ltrim($s));
}

function get_adapted_text_part ($s){
	for ($i = 0; $i < strlen($s); $i++){
		if ($s[$i] == '-' || $s[$i] == '+')
		return substr($s , $i);
		if ($s [$i] != ' ')
		return substr($s, $i);
	}
	return '';
}


$spaces_8 = '';
for ($i = 0; $i < 8; $i++)
$spaces_8 = $spaces_8 + ' ';

$font_file = 'arial.ttf';
$font_angle = 0;
$font_size = 14;

$maketag = false;
if (isset($_REQUEST['maketag'])){
	$maketag = $_REQUEST['maketag'] == 'true';
}

$loadimg = false;
if (isset($_REQUEST['loadimg'])){
	$loadimg = $_REQUEST['loadimg'] == 'true';
}

if ($loadimg){
	$guid = $_REQUEST['guid'];
	$fname = dirname(__FILE__). '/tmp/' . $guid;

	$loadimg = $_REQUEST['loadimg'] == 'true';

	$im = imagecreatefrompng ($fname);
	imagepng($im);
	imagedestroy($im);
	die ();
}

if ($maketag){

	// drop all tmp pics ----
	if (1){
		$dir = dirname(__FILE__) . '/tmp/';
		$dh = opendir($dir);
		while (($file = readdir($dh))){
			if ($file == '.' || $file == '..') continue;
			if (is_file($dir . $file))
			unlink($dir . $file);
		}
		closedir($dh);
	}
	// ----------------------


	$spec_w = $_REQUEST['spec_w'];
	$spec_h = $_REQUEST['spec_h'];

	if (! is_numeric($spec_w)){
		$spec_w = -1;
	}
	if (! is_numeric($spec_h)){
		$spec_h = -1;
	}



	$content_orig = $_REQUEST['content'];
	$content = explode("\n", $content_orig);



	$max_font_size_w = 0;
	$max_font_size_h = 0;
	$sum_font_size_h = 0;
	$max_spaces_count = 0;

	$matrix = array ();

	for ($i = 0; $i < count($content); $i++){
		if (trim($content[$i]) == '') continue;
		$tmp = str_replace("\t" , $spaces_8, rtrim($content[$i]));

		$tmp_text = get_adapted_text_part ($tmp);
		//dexport($tmp_text);


		$cmb = ' ';
		$clean_text = '';
		$count_spaces = get_spaces_count($tmp);
		if ($tmp_text [0] == '+'){
			$cmb = '+';
			$clean_text = substr($tmp_text, 1);
		}
		elseif ($tmp_text [0] == '-'){
			$cmb = '-';
			$clean_text = substr($tmp_text, 1);
		}
		else{
			$cmb = ' ';
			$clean_text = $tmp_text;
			$count_spaces--;
		}





		$font_info =  imagettfbbox($font_size, $font_angle, $font_file, $clean_text);
		$maxi = max ($font_info);
		for ($k = 0; $k < count($font_info); $k++)
		$font_info [$k] += $maxi;
		//dexport($font_info);
		$nova_w = abs($font_info [4] - $font_info [0]);
		$nova_h = abs($font_info [5] - $font_info [1]);
		$max_font_size_w = max ($max_font_size_w, $nova_w);
		$max_font_size_h = max ($max_font_size_h, $nova_h);
		$sum_font_size_h += $nova_h;


		$max_spaces_count = max ($max_spaces_count, $count_spaces);

		$matrix [] = array ('clean_text' => $clean_text, 'symbol' => $cmb, 'count_spaces' => $count_spaces, 'text_w' => $nova_w, 'text_h' => $nova_h);
	}

	$pref_spec_w = 20 + $max_font_size_w + $max_spaces_count * 20 + 40;
	$pref_spec_h = 20 + $max_font_size_h * count($matrix);

	if ($spec_w == -1){
		$spec_w = $pref_spec_w;
	}
	if ($spec_h == -1){
		$spec_h = $pref_spec_h;
	}



	//dexport($matrix);

	$guid = md5($content_orig) . '_' . $spec_w . '_' . $spec_h;
	$fname = dirname(__FILE__). '/tmp/' . $guid;
	if (file_exists($fname)){
		die ('<img src="makechartget.php?loadimg=true&guid='.$guid.'" width="'.$spec_w.'" height="'.$spec_h.'" alt="" border="0" />');
	}

	$koeff_w = $spec_w / $pref_spec_w;
	$koeff_h = $spec_h / $pref_spec_h;



	$im = imagecreatetruecolor($pref_spec_w, $pref_spec_h);
	$COLOR_WHITE = imagecolorallocate($im , 255, 255, 255);
	$COLOR_BLACK = imagecolorallocate($im , 0, 0, 0);
	
	$IMAGE_PLUS = imagecreatefrompng(dirname(__FILE__) . '/img_folder_plus.png');
	$size_w_image_folder_plus = imagesx($IMAGE_PLUS);
	$size_h_image_folder_plus = imagesy($IMAGE_PLUS);
	
	$IMAGE_MINUS = imagecreatefrompng(dirname(__FILE__) . '/img_folder_minus.png');
	$size_w_image_folder_minus = imagesx($IMAGE_MINUS);
	$size_h_image_folder_minus = imagesy($IMAGE_MINUS);
	
	$IMAGE_NONE = imagecreatefrompng(dirname(__FILE__) . '/img_folder_none.png');
	$size_w_image_folder_none = imagesx($IMAGE_NONE);
	$size_h_image_folder_none = imagesy($IMAGE_NONE);
	


	imagefilledrectangle ($im, 0, 0, $pref_spec_w, $pref_spec_h, $COLOR_WHITE);

	for ($i = 0; $i < count($matrix); $i++){
		if ($matrix[$i]['symbol'] == '+'){
			imagecopy($im , $IMAGE_PLUS, 20 * $matrix[$i]['count_spaces'] + 10, 0 + $max_font_size_h*$i, 0, 0, $size_w_image_folder_plus, $size_h_image_folder_plus);
		}
		if ($matrix[$i]['symbol'] == '-'){
			imagecopy($im , $IMAGE_MINUS, 20 * $matrix[$i]['count_spaces'] + 10, 0 + $max_font_size_h*$i, 0, 0, $size_w_image_folder_minus, $size_h_image_folder_minus);			
		}
		if ($matrix[$i]['symbol'] == ' '){
			imagecopy($im , $IMAGE_NONE, 20 * $matrix[$i]['count_spaces'] + 10, 0 + $max_font_size_h*$i, 0, 0, $size_w_image_folder_none, $size_h_image_folder_none);						
		}
		
		imagettftext($im, $font_size, $font_angle, 20 * $matrix[$i]['count_spaces'] + 40 + 10, 15 + $max_font_size_h*$i, $COLOR_BLACK, $font_file, $matrix[$i]['clean_text']);
	}


	imagedestroy($IMAGE_PLUS);
	imagedestroy($IMAGE_MINUS);
	imagedestroy($IMAGE_NONE);
	imagecolordeallocate ($im , $COLOR_BLACK);
	imagecolordeallocate ($im , $COLOR_WHITE);

	
	if ($pref_spec_h != $spec_h && $pref_spec_w != $spec_w){
		// Resample
		$image_p = imagecreatetruecolor($spec_w, $spec_h);
		imagecopyresampled($image_p, $im, 0, 0, 0, 0, $spec_w, $spec_h, $pref_spec_w, $pref_spec_h);
		imagepng ($image_p , $fname);
		imagedestroy($image_p);
		imagedestroy($im);
	}
	else{
		imagepng ($im , $fname);
		imagedestroy($im);

	}

	
	
	die ('<img src="makechartget.php?loadimg=true&guid='.$guid.'" width="'.$spec_w.'" height="'.$spec_h.'" alt="" border="0" />');

}
?>