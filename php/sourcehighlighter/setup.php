<?php
/*
'OutputPageBeforeHTML': a page has been processed by the parser and
the resulting HTML is about to be displayed.
$parserOutput: the parserOutput (object) that corresponds to the page
$text: the text that will be displayed, in HTML (string)
*/

include_once ('main.php');

global $wgHooks;

// ������������ ������� ����������� ��������� ���� --������ ������ ��� ���������--

$wgHooks['OutputPageBeforeHTML'][] = 'makeSourcesHighlightTag';


// ��������� ������ ��� ������ ������� �������:
// ���� � �������� ������� � �������� � ������� ��������� ��� ���������
// ���� � http ����� ��������, ��� ��������� ����� ���������� (����� ��� ������������ ���-�������)
define (FS_SOURCES_SUPER_DIR , realpath( dirname(__FILE__). '/../../../sources/') . '/');
define (WEB_SOURCES_SUPER_DIR , '/sources/');


// --------------------- ��� ���������� -------------------------------------

if (! function_exists('win2utf'))    {
	function win2utf($str)
	{
		$utf = "";
		for($i = 0; $i < strlen($str); $i++)
		{
			$donotrecode = false;
			$c = ord(substr($str, $i, 1));
			if ($c == 0xA8) $res = 0xD081;
			elseif ($c == 0xB8) $res = 0xD191;
			elseif ($c < 0xC0) $donotrecode = true;
			elseif ($c < 0xF0) $res = $c + 0xCFD0;
			else $res = $c + 0xD090;
			$utf .= ($donotrecode) ? chr($c) : (chr($res >> 8) . chr($res & 0xff));
		}
		return $utf;
	}
}

function is_file_in_catalog ($file_t, $dir_t){
  $file_t  = realpath ($file_t);
  $dir_t  = realpath ($dir_t);

  $file_t  = str_replace ('\\', '/', $file_t);
  $dir_t  = str_replace ('\\', '/', $dir_t);
  return $dir_t == substr($file_t, 0, strlen($dir_t));
}

// ������� ��������� �� makeSourcesHighlightTag ��� ������ ���� @{sourceshighlight ....
// �� ��������������� html-������������� ������ ������ ��� ����������� ������ ����� � �����������
// �� �������������� ���������� ���������� �������������
function cmpl_2321_reg_shhic ($arr_got){

	$params = $arr_got [1];

	$m = md5($params);
	// ���������� ��������� ��������� ������������� �� ����� �������� ����� @{sourceshighlight ....

	$css = 'shc_basic.css';
	// ��� ��������� ����� ��-���������
	$arr_got = array ();
	if (preg_match('/css="([^"]+)"/iUs',$params, $arr_got))
	$css = $arr_got [1];

	$base = false;
	// ���� - ������� ������� ����� ����������
	$arr_got = array ();
	if (preg_match('/base="([^"]+)"/iUs',$params, $arr_got))
	$base = $arr_got [1];
	if ($base == false)
		return 'fatality: config invalid, attribute base is required';


	// ������ ����� �� ������� ����, ��� ��� ����������� ������ ��� ������ (����� � �������� ��������� ������ ������������� �����-��������)

	$base2clean = WEB_SOURCES_SUPER_DIR . $base;
	$base = realpath (FS_SOURCES_SUPER_DIR . $base) . '/';


	if (! is_file_in_catalog($base, FS_SOURCES_SUPER_DIR)){
	 return 'hack detected: invalid (too toplevel) catalog url in config';
	}

	$tmp_f = false;
	$tmp_d = false;
	if (isset ($_REQUEST['file'])){	
		$tmp_f  = realpath ($base . $_REQUEST['file']) ;
		$tmp_f  = str_replace ('\\', '/', $tmp_f);
		$tmp_f  = str_replace ('\\', '/', $tmp_f);

		if (! is_file_in_catalog($tmp_f, $base)){
		 return 'hack detected: invalid (too toplevel) file url in request';
		}
	}
	if (isset ($_REQUEST['index'])){	
		$tmp_d  = realpath ($base . $_REQUEST['index']). '/';
		$tmp_d  = str_replace ('\\', '/', $tmp_d);
		$tmp_d  = str_replace ('\\', '/', $tmp_d);

		if (! is_file_in_catalog($tmp_d, $base)){
		 return 'hack detected: invalid (too toplevel) file subdir in request';
		}
	}

			
	// ������� ������ ������� ��������� � � ��� ������������ ����������� ��� �������� �� ��������� ��� ����������� ������
	// ��������� ������ ���-����� ��� �������� (��� �������� ����� ������������ �������� �����)

	$sc = new SourceHighlightManager ($m,$base,'', $base2clean);
	// ������ �� �������������� � ����������� ���������������� � utf8
	// TODO ���������� ��� ��� utf8 (�������������� ��� ����� ���������� ��� utf8)
	$content = $sc->service(
              isset($_REQUEST['m'])?$_REQUEST['m']:'',  
              isset($_REQUEST['act'])?$_REQUEST['act']:'',  
              $tmp_f, $tmp_d);
	return win2utf($content) ;

}

// ������� �������, ���������� �� mediawiki, �� ������ - ����� ���� @{sourceshighlight ....
// �������� � ��� ��������� ���������:
// css - ��� css-����� ������������� ��� ���������� �������� ���� ������� � ������������� ������ (���� �� ������������)
// base - ��� �������� (������������ �������� �������� ��� ���� ��������)
function makeSourcesHighlightTag (& $paout, & $text){
	//@{sourceshighlight css="shc_basic.css" base="adodb" }
	$text = preg_replace_callback('/@{sourceshighlight([^}]+)}/iUs', 'cmpl_2321_reg_shhic', $text);
	return true;
}

?>