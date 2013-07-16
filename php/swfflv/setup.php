<?php
// добавляем в список функций установки расширений свою собственную
$wgExtensionFunctions[] = 'SwfAndFlvSetup';

// когда функция "установки" будет вызвана, то нужно зарегистрировать все используемые теги
$wgExtensionCredits['parserhook']['SwfAndFlvSetup'] = array(
	'name'          => 'SwfAndFlvSetup',
	'author'        => 'black zorro',
	'description'   => 'embed into mediawiki pages swf and flv files',
);

function SwfAndFlvSetup() {
	global $wgParser;
	$wgParser->setHook( 'swf', 'swfflvHook' );
	$wgParser->setHook( 'flv', 'swfflvHook' );
}


// возможно, что на одной странице будет расположено несколько swf или flv-файлов, 
// очевидно, что нет необходимости каждый раз возвращать полный фрагмент с подключением нужных js-библиотек
function got_js_for_swf_flv (){
  static $loaded = false;
  if ( $loaded ) {
	return '';
  }
  $loaded = true;
  global $wgScriptPath;
  $path_TO = $wgScriptPath . '/js/';
  if (true){
    $dir = dirname($_SERVER['SCRIPT_FILENAME']);
    $mydirname = str_replace ('\\', '/', dirname (__FILE__)) . '/';
    $path_TO = str_replace($dir , $wgScriptPath, $mydirname );

    
  }

  return 
'
<script type="text/javascript" src="'.$path_TO.'swfobject.js"> </script>
<script type="text/javascript" src="'.$path_TO.'jquery-1.2.1.pack.js"> </script>
<script type="text/javascript" src="'.$path_TO.'swf_and_flv_process.js"> </script>
';
}

function swfflvHook( $text, $params = array(), $parser ) {
   $text = trim ($text);
   $img = Image::newFromName( $text );
   if ( $img == null ) 
	return "SWF|FLV File not found (".$text.")";
   $img->load();
   if ( ! $img->imagePath ) 
	return "SWF|FLV File hasn't path (".$text.")";
   global $wgScriptPath;
   $img->imagePath = str_replace ('\\', '/', $img->imagePath);
   $dir = dirname($_SERVER['SCRIPT_FILENAME']);
   $url_file = str_replace($dir , $wgScriptPath, $img->imagePath);

   $mydirname = str_replace ('\\', '/', dirname (__FILE__)) . '/';
   $url_player = str_replace($dir , $wgScriptPath, $mydirname . 'flvplayer.swf');

   $attrs = '';
   foreach ($params as $key => $val){
      if ($key != 'dummy')
        $attrs .= $key .  ':"' . $val. '", ';
   }

   $dummy_url = '';

   if (isset($params['dummy'])){
     $dummy_img = Image::newFromName( $params['dummy'] );
     if ( $dummy_img == null ) 
	return "SWF|FLV Dummy File not found (".$params['dummy'].")";
     $dummy_img->load();
     if ( ! $dummy_img->imagePath ) 
	return "SWF|FLV Dummy File hasn't path (".$params['dummy'].")";

     $dummy_img->imagePath = str_replace ('\\', '/', $dummy_img->imagePath);
     $dummy_dir = dirname($_SERVER['SCRIPT_FILENAME']);
     $dummy_url = str_replace($dummy_dir , $wgScriptPath, $dummy_img->imagePath);
     
   }

   $filenamelen = mb_strlen($text, 'utf-8');
   $kind = mb_substr ($text, $filenamelen - 3, 3, 'utf-8');  
   $id = $kind.'_'.md5($text);

   $attrs .= ' __filesource:"'.$url_file.'", __flvplayer:"'.$url_player.'", __dummy:"'.$dummy_url.'" ';

   $tag = '<div id="'.$id.'" style=""><!-- empty --></div><script>var '.$id. ' = {'.$attrs.'};</script>';

   $js = got_js_for_swf_flv ();
   return $js . $tag;
}


?>