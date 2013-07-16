<?php
/*
'OutputPageBeforeHTML': a page has been processed by the parser and
the resulting HTML is about to be displayed.
$parserOutput: the parserOutput (object) that corresponds to the page
$text: the text that will be displayed, in HTML (string)
*/

include_once ('rss_main.php');


/*
$wgExtensionFunctions[] = "makeRSSTag_Setup";
$wgExtensionCredits['parserhook'][] = array(
        'name' => 'makeRSSTag',
        'url' => 'http://black-zorro.jino-net.ru',
        'author' => 'black zorro',
        'description' => 'RSS ATOM feeds support',
);
 
function makeRSSTag_Setup() {
    global $wgParser;
    $wgParser->setHook( "rss-set", "makeRSSTag" );
}
 
*/

$wgHooks['OutputPageBeforeHTML'][] = 'makeRSSTag';

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


function cmpl_e45_und_er12 ($arr_got){
	return '<!-- dropped out rss feed -->';
}

function cmpl_e44_rss_345ic ($arr_got){

	$params = $arr_got [1];

	$m = md5($params);

	$kind = 'atom';
	$arr_got = array ();
	if (preg_match('/kind="([^"]+)"/iUs',$params, $arr_got))
	$kind = $arr_got [1];

	$count = 10000;
	$arr_got = array ();
	if (preg_match('/count="([^"]+)"/iUs',$params, $arr_got))
	$count = $arr_got [1];


	$encoding = 'utf-8';
	$arr_got = array ();
	if (preg_match('/encoding="([^"]+)"/iUs',$params, $arr_got))
	$encoding = $arr_got [1];

	$title_feed = 'Example Feed';
	$arr_got = array ();
	if (preg_match('/title="([^"]+)"/iUs',$params, $arr_got))
	$title_feed = $arr_got [1];


	//dexport ($params);

	$username_feed = '';
	$arr_got = array ();
	if (preg_match('/username="([^"]+)"/iUs',$params, $arr_got))
	$username_feed = $arr_got [1];


	//die(var_export($_SERVER, true));
	if (strpos($_SERVER['QUERY_STRING'] , 'go') === 0 ){
		$username = '';
		if (isset ($_REQUEST['forceusername']))
			$username_feed = $_REQUEST['forceusername'];


		if (isset ($_REQUEST['forcecount']) && is_numeric($_REQUEST['forcecount']))
			$count = $_REQUEST['forcecount'];

		$rss = makeRSSFeed ($count, $username_feed);
		$rss = convertRSSFeedToFormat ($rss, $kind, $encoding, $title_feed);
		$rss = iconv ('utf-8', $encoding, $rss);

		/*
		global $glob_if_rss_got_from_cached;
		if ($glob_if_rss_got_from_cached)
			die ('cached');
		*/

		if (isset ($_REQUEST['as_json_force']) && $_REQUEST['as_json_force'] == 'true'){
	  	  header('Content-Type: text/javascript');
		  die ($rss);
		}

		header('Content-Type: application/rss+xml');
		die($rss);
	}
	if (strpos($_SERVER['QUERY_STRING'] , 'showallashtml') === 0 ){
		$rss = makeRSSFeed (100000);
		include_once ('simple_engine.inc');
		$tmp =  EngineSimpleRequest::createTemplate(dirname(__FILE__) . '/templ_rss_all.shablon', 
			array (
			'@items' => array ('@FOR2' => $rss , '@NAME' => 'items'),
			'title' => $title_feed,
			)
		);
		$tmp = iconv ('utf-8', $encoding, $tmp);
		return $tmp;
	}
	

	return '---RSS Feed---' . 'count=' . $count . '; encoding='.$encoding . '; title_feed='.$title_feed.'; kind='. $kind . '; username=' . $username_feed;


}

function makeRSSTag  (& $paout, & $text){
	//@{rss kind="atom" encoding="utf-8" count="20"}
	$text = preg_replace_callback('/@{rss([^}]+)}/iUsm', 'cmpl_e44_rss_345ic', $text);
	$text = preg_replace_callback('/&lt;rss-set&gt;(.*)&lt;\/rss-set&gt;/iUsm', 'cmpl_e45_und_er12', $text);
	return true;
}

?>