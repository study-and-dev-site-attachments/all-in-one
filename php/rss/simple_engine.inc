<?php

if (! class_exists ('EngineSimpleRequest')){


$SIMPLE_ENGINE_CACHE_ENABLED = 0;
define('SKIN_ENGINE_ENABLED' , 0);

$glob_precached_template_filename = null;
$simple_engine_skin_dir = null;

class EngineSimpleRequest {

 static function CreateCoreTemplate ($page_self , $assoc_of_values, $spec_delimeter = '#'){
 	if ($page_self == '') return '';
 	global $SIMPLE_ENGINE_CACHE_ENABLED;
 	if ($SIMPLE_ENGINE_CACHE_ENABLED){
 		
		$rez_cached = EngineSimpleRequest::FindValueInCache ($page_self , $assoc_of_values, $spec_delimeter);
		if ($rez_cached!==false) return $rez_cached;
 	}
	
	foreach ($assoc_of_values as $k => $v){
		if (strpos($k, '!') !== false){
			$k = substr($k , 1);
			$page_self = preg_replace_callback("{#{$k}#}" , $v , $page_self);
		}
/*
'@SECTIONS' => array ( // подсекции например секцию где находится сайт не нужно показывать если у чела нет сайта -- вообще придумывать спец. способности секциям не стоит хватит показа по условию и все
	    array ('@VISIBLE' => (strlen (trim($url)) > 1), '@NAME' => 'SECTION_SITE'),
	   )
*/		
		elseif (strpos($k, '@') !== false){
					
			//if ($k = '@SECTIONS'){
				$sect_commands = $v;
				//foreach ($arr_sections as $idx => $sect_commands){
					if (! isset ($sect_commands ['@NAME'])){
						die ('Invalid SimpleEngine Template Call (@ declared): ' . var_export($sect_commands, true));
					}
					
					$name_of_sect = $sect_commands ['@NAME'];

					if (array_key_exists ('@DEFAULT' , $sect_commands)){
						
						$default = $sect_commands ['@DEFAULT'];
						
						
						$by_1 = $spec_delimeter. 'BEGIN:'  . $name_of_sect . $spec_delimeter;
						$by_2 = $spec_delimeter. 'END:'  . $name_of_sect . $spec_delimeter;
						
						if (! $default){
							//die ('- default' .$name_of_sect . ' ' .  $default);
							$page_self = preg_replace("|{$by_1}|U" , '' , $page_self);
							$page_self = preg_replace("|{$by_2}|U" , '' , $page_self);									}
						else{
							// удаляем секцию с концами
							//die ('+ default' .$name_of_sect . ' ' .  $default);
							$page_self = preg_replace("{{$by_1}(.*){$by_2}}isU" , $default , $page_self);
						}
					}elseif (isset ($sect_commands ['@VISIBLE'])){
						$visible = $sect_commands ['@VISIBLE'];
						
						$by_1 = $spec_delimeter. 'BEGIN:'  . $name_of_sect . $spec_delimeter;
						$by_2 = $spec_delimeter. 'END:'  . $name_of_sect . $spec_delimeter;
						
						if ($visible){
							$page_self = preg_replace("|{$by_1}|U" , '' , $page_self);
							$page_self = preg_replace("|{$by_2}|U" , '' , $page_self);									}
						else{
							// удаляем секцию с концами
							$page_self = preg_replace("{{$by_1}(.*){$by_2}}isU" , '' , $page_self);
						}
					}
					elseif (isset ($sect_commands ['@XML']) ){
						
						
						$by_1 = $spec_delimeter. 'BEGIN:'  . $name_of_sect . $spec_delimeter;
						$by_2 = $spec_delimeter. 'END:'  . $name_of_sect . $spec_delimeter;
						
						
						$arr_fragment = array ();
						if (!preg_match("{{$by_1}(.*){$by_2}}isU" , $page_self , $arr_fragment)) continue;
						$fragment = $arr_fragment [1]; // содержимое секции
						//dexport($fragment);
						$rez_xml = '';
						
						$xml = $sect_commands ['@XML'];
						//dexport($xml);
						
						if (isset ($sect_commands ['@XSL_FROM_EXTERNALFILE']))
							$rez_xml = EngineXsltRequest::getTransformedSourceFromStringAndXsltFile(
								$xml , TSimpleHttpRequest::GetGlobSiteRoot() . trim($fragment) 
							);
						if (isset ($sect_commands ['@XSL_FROM_TEMPLATEXSL']))
							$rez_xml = EngineXsltRequest::getTransformedSourceFromString(
								$xml , trim($fragment)
							);
						
						$page_self = preg_replace("{{$by_1}(.*){$by_2}}isU" , $rez_xml , $page_self);
					}					
					elseif (isset ($sect_commands ['@FOR'])) {
						$by_1 = $spec_delimeter. 'BEGIN:'  . $name_of_sect . $spec_delimeter;
						$by_2 = $spec_delimeter. 'END:'  . $name_of_sect . $spec_delimeter;
						$arr_fragment = array ();
						preg_match("{{$by_1}(.*){$by_2}}isU" , $page_self , $arr_fragment);
						$fragment = $arr_fragment [1]; // содержимое секции
						$page_self = preg_replace("{{$by_1}(.*){$by_2}}isU" , "{$by_1}{$by_2}" , $page_self);
						
						$arr_of_records = $sect_commands ['@FOR'];
						$arr_rez_changes = array ();
						$arr_of_names = array_keys($arr_of_records);
						$cnt_records =  count($arr_of_records [$arr_of_names[0]]);
						for ($j =0; $j < $cnt_records; $j++){
							$arr_row = array ();
							for ($f  = 0; $f < count($arr_of_names); $f++)
								$arr_row [$arr_of_names [$f]] = $arr_of_records[$arr_of_names [$f]] [$j];
							$arr_rez_changes [] = $arr_row;
						}
						$instead_of	= '';
						for ($f = 0; $f < count($arr_rez_changes); $f++)
							//die (var_export ($arr_rez_changes, true));
							$instead_of .= EngineSimpleRequest::CreateCoreTemplate(
						 	$fragment , $arr_rez_changes [$f] , $spec_delimeter
						);
						$page_self = preg_replace("{{$by_1}{$by_2}}isU" , $instead_of , $page_self);
					}
						elseif (isset ($sect_commands ['@FOR2'])) {

						//die (var_export($sect_commands, true));

						$by_1 = $spec_delimeter. 'BEGIN:'  . $name_of_sect . $spec_delimeter;
						$by_2 = $spec_delimeter. 'END:'  . $name_of_sect . $spec_delimeter;
						$arr_fragment = array ();
						preg_match("{{$by_1}(.*){$by_2}}isU" , $page_self , $arr_fragment);
						$fragment = $arr_fragment [1]; // содержимое секции
						$page_self = preg_replace("{{$by_1}(.*){$by_2}}isU" , "{$by_1}{$by_2}" , $page_self);
						$arr_of_records = $sect_commands ['@FOR2'];
						$instead_of	= '';
						for ($f = 0; $f < count($arr_of_records); $f++)
							$instead_of .= EngineSimpleRequest::CreateCoreTemplate(
						 	$fragment , $arr_of_records [$f] , $spec_delimeter
						);
						$page_self = preg_replace("{{$by_1}{$by_2}}isU" , $instead_of , $page_self);
					}
				//}
			//}// end of SECTION
		}
		else{
			//die ($k);
			$page_self = preg_replace("{{$spec_delimeter}{$k}{$spec_delimeter}}" , $v , $page_self);
		}
	}
	
	if ($SIMPLE_ENGINE_CACHE_ENABLED){	
		EngineSimpleRequest::SaveValueInCache($page_self);
	}
	return $page_self;	
 }

 
 
 function ReOrder ($arr_what){
 	return ksort($arr_what);
 }
 
 function createTemplate ($file_name, $assoc_of_values, $spec_delimeter = '#'){
 	global $simple_engine_skin_dir;
 	if (SKIN_ENGINE_ENABLED && (! file_exists($file_name)))
 		$file_name = $simple_engine_skin_dir . $file_name;
 	
	if (! file_exists ($file_name)){
		$arr_of_mods = array ('../' , '..');
		$is_ok = false;
		for ($i = 0; $i < count ($arr_of_mods); $i++){
			if (file_exists($arr_of_mods[$i] . $file_name)){
				$is_ok = true;
				$file_name = $arr_of_mods[$i] . $file_name;
				break;
			}
		}
		if (! $is_ok){
			print ("DEBUG: TemplateEngine File Was Requested: " . $file_name . '<br />');
			print ("DEBUG: TemplateEngine Current Dir: " . getcwd() . '<br />');
		}
		ASSERT_VALID ($is_ok);
	}
	$h = fopen( $file_name , 'r');
	if (!$h)
	return 'Ошибка, невозможно выполнить чтение шаблона: ' . $file_name;
	$txt = fread ($h , filesize($file_name));
	fclose($h);
	return EngineSimpleRequest::CreateCoreTemplate($txt, $assoc_of_values, $spec_delimeter);
 }

 
 
 
 function FindValueInCache (& $page_self ,& $assoc_of_values, $spec_delimeter){
 	global $glob_precached_template_filename;
 	$changes = strlen(var_export($assoc_of_values , true));
 	$glob_precached_template_filename = false;
 	if (strlen($changes ) + strlen($page_self) < 200) return false;
 	// для небольших шаблонов и небольших замен не имеет смыла это делать
 	$m5 = md5($page_self);
 	$m52 = md5($changes);
 	$fullfile = dirname(__FILE__) . '/tmp/' . $m5 . '-' . $m52 . '-' . ord($spec_delimeter);
 	$glob_precached_template_filename = $fullfile;
 	if (!file_exists($fullfile)) return false;
 	return join (file($fullfile));
 }
 
 function SaveValueInCache (& $rezult_value){
 	global $glob_precached_template_filename;
 	if ($glob_precached_template_filename === false) return;
 	$h = fopen($glob_precached_template_filename, 'w');
	if (flock($h, LOCK_EX)) { // do an exclusive lock
    	fwrite($h , $rezult_value);
    	flock($h, LOCK_UN); // release the lock
	}
 	fclose($h);
 }

 
 function SetBaseSkinDir ($skin_name_dir){
 	global $simple_engine_skin_dir;
 	$simple_engine_skin_dir = $skin_name_dir;
 }
 
 
 function LazyReadFile ($fname){
 	global $simple_engine_skin_dir;
 	$fname = $simple_engine_skin_dir .  $fname;
 	if (file_exists($fname)){
 		$h = fopen ($fname, 'r');
 		$data = fread ($h , filesize($fname));
 		fclose ($h);
 		return $data;
 	}
 	return '';
 }
}

} // -- if class Exists
?>