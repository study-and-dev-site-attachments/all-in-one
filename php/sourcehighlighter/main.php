<?php
// wiki plugin -- by black zorro -- black-zorro@tut.by
//---- usage  example : -----
//Hello It's My Page
//@{sourceshighlight css="shc_basic.css" base="adodb" }
// Hello, Plugin correct place 2 or more sourceslists in one page
//@{sourceshighlight css="shc_basic.css" base="wiki/extensions" }
// your must supply params to plugin:
// css - name of css file (must be in plugin dir)
// base - root catalog name  (not relative from wiki)
// optional: file or index - emulate first user action
// ---- installation ---
// add line into LocalSettings.php
// include_once ('extensions/sourcehighlighter/setup.php');
// and of course copy this plugin into extension dir



// ����������� ��� ��������� ����������� ����������� ������ ��������� ���������� � ��������

// ���� � ������������� �����
define('PATH_TO_GESHI', dirname (__FILE__) . '/../SyntaxHighlight_GeSHi/geshi');

// ������� ���������� ������� css
define('PRE_CSS_SELECTOR', 'css_whi_smp_');

// ����� ����������� ����������� ���������� ������ �� �� �������� ���� - �� �������� ���� � ����������� ��������� ����
$MAP_EXT_TO_SOURCE  = array (
'as' => 'actionscript',
'as2' => 'actionscript',
'as3' => 'actionscript',

'c' => 'c',
'cpp' => 'cpp',

'css' => 'css',

'html' => 'html4strict',
'htm' => 'html4strict',
'shtml' => 'html4strict',

'php' => 'php',
'php3' => 'php',
'php4' => 'php',
'php5' => 'php',
'phtml' => 'php',
);

// ����������� ����, ��� ��� ����� �������� �����������
function is_2_files_are_same ($f1 , $f2){
  $f1= str_replace ('\\', '/', $f1);
  $f2= str_replace ('\\', '/', $f2);
  return $f1 == $f2;
}

// ����������� ���� �������� �� ������ ���� ���������
function  isFileImage ($img){
	$arr_got = array ();
	if (! preg_match('/\.([^.]+)$/iUs', $img, $arr_got)) return false;
	return in_array (strtolower($arr_got [1]), array ('gif', 'png', 'jpg', 'jpeg') );
}

// ������� ��������� ��������� �� ������ ���� ��� �� �� ���
function THROW_ERROR ($msg){
	trigger_error($msg,  E_USER_ERROR);
}



// ����� ����������� ���������������� �������
class SourceHighlightManager {
	var $base = '/';
	var $base_of_page_url = '';
	var $m = '';
	var $base_web_root = '/';

	// ����������� ������ �� ��������� ������� �������� ����� ���������� ���������� ��� ���������� � �������� ����, 
	// ��� ����������� ��� ��������� ���� � �������� ������������� ����������
	function SourceHighlightManager ($m, $base, $base_of_page_url, $base_web_root){
		$this->m = $m;
		$this->base = $base;
		$this->base_of_page_url = $base_of_page_url;
		$this->base_web_root = $base_web_root;

		if (! file_exists($this->base))
		THROW_ERROR ('������ �������������, ����������� ���� ����� ��������� �� ����� ���� ��������� (c:001)');
	}

	// ��������������� ������� ������ ��� ���������� ���� "��������� �� ���� ������� ����"
	function get_parent_index_href ($fullname){
		$fshort = $this->get_fname_from_root ($fullname);
		$arr0 = explode ('/', $fshort);
		$arr2 = array ();
		for ($i = 0; $i < count ($arr0); $i++){
			if (trim ($arr0[$i]) != '')
			    $arr2 [] = trim ($arr0[$i]);
		}
		if (count ($arr2) > 0){
			array_pop ($arr2);
		}
		return '?index=/' . join ('/', $arr2) . '&m='.$this->m;
	}

	// ��������������� ������� ��������� ������������� ���� � ����� ��� �������� (������������ ����� base)
	function get_fname_from_root ($full){
	  $full= substr ($full, strlen($this->base) - 1);
	  $full= str_replace ('\\', '/', $full);
	  return $full;
	}

	// ��������������� ������� ���������� �� ����� ���������� � �����
	function get_ext ($file){
		if (strpos($file , '.') === false)
		return false;
		$rev = strrev($file);
		return  strrev( substr($rev , 0, strpos($rev , '.') ) );
	}

	// ������� ��������� �� ��������� ����� ����� (������ ��� ����������) ��� ���������� (java, php, c++ ...)
	function mapping_extensions ($file){
		$ext = $this->get_ext ($file);
		if (! $ext)
		return 'text';
		global $MAP_EXT_TO_SOURCE;
		return  isset($MAP_EXT_TO_SOURCE [$ext])?$MAP_EXT_TO_SOURCE [$ext]:'text';

	}

	// ������� ��������� ������ �� ���������� �������� � ��� ��������� ����� � ����������� ���������� � ������ ������� ������������ ��� ������������ "����������"
	function index_data ($folder){

		$arr_files = array ();
		if ($folder [strlen($folder) - 1] != '/'){
			$folder .= '/';
		}
		if (is_dir($folder)){
			if ($h = opendir($folder)){
				while ( ($file = readdir($h)) !== false ){
					if ($file == '..' || $file == '.') continue;
					$fullname = $folder . $file;
					$arr_files []= array (
					'name' => $file,
					'realname' => $file,
					'fullname' => $this->get_fname_from_root ($fullname),
					'fullname_fs' => $fullname,
					'size' => (is_dir($fullname)?0:filesize($fullname)),
					'mtime' => (is_dir($fullname)?0:filemtime($fullname)),
					'kind' => (is_dir($fullname)?'d':'f'),
					'ext' => $this->get_ext ($file)
					);
				}
				closedir($h);
			}

		}
		return $arr_files;
	}

	// ������� ������ ��� index_data. �� ���������� ���������� ������ ������ � ����������� ��������������� � ���� �������
	function index_render ($folder){
		$data = $this->index_data ($folder);

		if ($data === false)
		return '<div class="'.PRE_CSS_SELECTOR.'error_not_found">������, ����������� ���� ���� ������������ �������� �� ����� ���� ������"'.$this->get_fname_from_root ($folder).'" </div>';

		$html = '';
		if (! is_2_files_are_same($folder , $this->base)){
			$html .= '<a href="'.$this->get_parent_index_href($folder) . '" >��������� ����</a>'. "\n";
		}

		$html .= '<div class="'.PRE_CSS_SELECTOR.'heading_index_sourcesh">������ ������/������������ � ������� �������� "'.$this->get_fname_from_root ($folder).'" </div>'. "\n";

		$html .= '<table cellpadding="0" cellspacing="0" border="1" class="'.PRE_CSS_SELECTOR.'table_scrlist_index">' . "\n";
		$html .= '<tr>' . "\n";
		$html .= '<th>��� �����</th>' .  "\n";
		$html .= '<th>��� �����</th>' .  "\n";
		$html .= '<th>������</th>' .  "\n";
		$html .= '<th>�������</th>' .  "\n";
		$html .= '<th>���������</th>' .  "\n";
		$html .= '<th>�����</th>' .  "\n";
		$html .= '</tr>' . "\n";

		for ($i = 0; $i < count($data); $i++){
			$idata = $data [$i];
			$url_special = '';
			if ($idata ['kind'] == 'f')
			$url_special = $this->base_of_page_url.'?file='.$idata ['fullname'] .'&m=' . $this->m;
			else
			$url_special = $this->base_of_page_url.'?index='.$idata ['fullname'] .'&m=' . $this->m;


			$html .= '<tr>' . "\n";

			$html .= '<td>' . ($idata ['kind']=='d'?'�������':'����') . '</td>' . "\n";
			$html .= '<td><a href="'.$url_special.'">' . $idata ['name'] .'</a></td>' . "\n";
			$html .= '<td>' . ($idata ['size']) . '</td>' . "\n";
			$html .= '<td>' . date('d.m.Y H:i:s', $idata ['mtime']) . '</td>' . "\n";


			$html .= '<td><a href="'.$url_special.'&act=down"><img src="/mediawiki/extensions/sourcehighlighter/img_file.png" /></a></td>' . "\n";
			$html .= '<td><a href="'.$url_special.'&act=zip"><img src="/mediawiki/extensions/sourcehighlighter/img_zip.png" /></a></td>' . "\n";

			$html .= '</tr>' . "\n";
		}
		$html .= '</table>' . "\n";

		$url_special = $this->base_of_page_url.'?index='.$this->get_fname_from_root($folder) .'&m=' . $this->m . '&act=zipall';
		$html .= '<div class="gzip_all"><a href="'.$url_special.'"><img src="/mediawiki/extensions/sourcehighlighter/img_zip.png" />��������� ��� ���������� �������� � ���� ������</a></div>';
		return $html;
	}


	// ������� ������������ ������ ����������� ���������� �����, � ���������� ��� � ����������������� ���� � ������� geshi
	function file_data ($fullname){


		if (! file_exists($fullname))
		return false;


		if (isFileImage($fullname)){
			list($width, $height, $type, $attr) = getimagesize( $fullname );

			$picname = $this->base_web_root .  $this->get_fname_from_root($fullname);

			$picname = str_replace ('//', '/' , $picname);
			$picname = str_replace ('\\' ,'/',  $picname);

			return ( '
			������� �����������: '.$width. ' * ' .$height . ' px; �������� �� �����: '. filesize ($fullname). ' �. <br />
			<img src="'.$picname.'" />' );
		}

		require_once(PATH_TO_GESHI .  '/geshi.php');

		$language = $this->mapping_extensions ($fullname);

		$source = file_get_contents ($fullname);
		$geshi = new GeSHi($source, $language);
		$geshi->enable_line_numbers(GESHI_FANCY_LINE_NUMBERS);
		return $geshi->parse_code();
	}

	// ������� �������� ������ ��� file_data. �� ���������� - ������� �������� html � ���������� ������ ��� ��������������� ���������� �����
	function file_render ($fullname){
		$data = $this->file_data ($fullname);
		if (! $data)
		return '<div class="'.PRE_CSS_SELECTOR.'error_not_found">������, ����������� ���� ���� ��� ������������ �� ����� ���� ������"'.$this->get_fname_from_root($fullname).'" </div>';

                $html = '';

		$html .= '<a href="'.$this->get_parent_index_href($fullname) .'" >��������� ����</a>'. "\n";

		$html .= '<div class="'.PRE_CSS_SELECTOR.'heading_index_sourcesh">���������� �����: "'.$this->get_fname_from_root ($fullname).'" </div>'. "\n";

		$html .= '<div class="'.PRE_CSS_SELECTOR.'div_sourcebox">'.$data.'</div>';
		return $html;
	}

	// ������� ��������� ������������� ����� � �������� ������ � ���������� ��� ������� - ������������� �� die
	function make_zip ($fullname){
		
		include_once('zip.lib.php');
		$zip = new zipfile ();
		header("Content-type: application/zip");
		header('Content-Disposition: attachment; filename="'. basename($fullname) . '.zip' . '"');
		header("Expires: 0");
		header("Cache-Control: must-revalidate, post-check=0,pre-check=0");
		header("Pragma: public");
		$zip->addFile (file_get_contents($fullname) ,basename($fullname) );
		die ($zip->file());
		
		/*
		header("Content-type: application/gzip");
		header('Content-Disposition: attachment; filename="'. basename($fullname) . '.zip' . '"');
		header("Expires: 0");
		header("Cache-Control: must-revalidate, post-check=0,pre-check=0");
		header("Pragma: public");
		die (gzencode (file_get_contents($fullname), 7));
		*/

	}

	// ������� ������ ��� �������� �����, �������� ������� ��� ���������� ��� �������������� � ������������� �� die
	function make_down ($fullname){
		header("Content-type: application/octet-stream");
		header('Content-Disposition: attachment; filename="'. basename($fullname) . '"');
		header("Expires: 0");
		header("Cache-Control: must-revalidate, post-check=0,pre-check=0");
		header("Pragma: public");
		die (file_get_contents($fullname));

	}

	// ������� ����������� ������������� ������� �������� � ���� ��� ����������� � ����� zip
	function rec_scan_dir_to_zip($dir, $zip){

		$dir = realpath ($dir . '/' ) . '/';
		
		$h = opendir ($dir);
		if (! $h) { print 'cannot open:' . $dir ;return;}
		while (($file = readdir ($h))!==false){
			$fullname = $dir . $file;

			if ($file == '..' || $file == '.') continue;
			if (! is_readable ($fullname) ) continue;

			//die($fullname);
			
			if (is_dir ($fullname)){
				$this->rec_scan_dir_to_zip ($fullname , $zip);
			}
			else{
				$locfilename = substr ($fullname , strlen($this->base));
				
				//$zip->addFile($fullname, $locfilename);
				$zip->addFile (file_get_contents($fullname) , $locfilename);
			}
		}
		closedir ($h);
	}

	// �������-�������������. �� ���������� - ������� ���� �� ��������� ���� ������� �� ��������� ���������� ����������
	function service ($m, $act, $tmp_f, $tmp_d){
		if ($m == $this->m || $act != ''){
			
			if ($act == 'zip'){
				if (! file_exists($tmp_f))
					return false;
				return $this->make_zip($tmp_f);
			}


			if ($act == 'down'){
				if (! file_exists($tmp_f))
					return false;
				return $this->make_down($tmp_f);
			}


			if ($act == 'zipall'){
				
				
				include_once('zip.lib.php');
				
				//$zip = new ZipArchive();
				//$tmpfname = tempnam(null, 'alldirshhhc');
				
				$zip = new zipfile ();
				

				//if ($zip->open($tmpfname, ZIPARCHIVE::OVERWRITE)!==TRUE) {
					//return false;
				//}
				//$zip->addFromString("readme_zip.txt", "This file is autogenerated, and contains directory: " . $this->base);

				$this->rec_scan_dir_to_zip ( $tmp_d, $zip );
				
				//$zip->close();

				header("Content-type: application/zip");
				header('Content-Disposition: attachment; filename="'.'all.zip' . '"');
				header("Expires: 0");
				header("Cache-Control: must-revalidate, post-check=0,pre-check=0");
				header("Pragma: public");
				//$h = fopen ($tmpfname, 'rb');
				//$fc = fread ($h , filesize ($tmpfname));
				//fclose($h);
				//die ($fc);
				die ($zip->file());
			}

			if ($tmp_d != false){
				return $this->index_render($tmp_d);
			}
			elseif ($tmp_f != false ){
				return $this->file_render($tmp_f);
			}
			else{
				die ('fatality');				
			}


		}
		else{
			return $this->index_render($this->base);
		}
	}


}
?>