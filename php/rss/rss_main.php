<?php
// wiki plugin -- by black zorro -- black-zorro@tut.by
//---- usage  example : -----
//Hello It's My Page
// @{rss kind="atom" encoding="utf-8" }
// your must supply params to plugin:
// kind - name rss format
// encoding - output encoding name
// ---- installation ---
// add line into LocalSettings.php
// include_once ('extensions/rss/rss_setup.php');
// and of course copy this plugin into extension dir



$glob_if_rss_got_from_cached = false;

function makeRSSFeed ($count, $username = ''){

	$if_by_user = '';
	if ($username != '')
	$if_by_user = ' AND rev_user_text = "'.mysql_escape_string(stripcslashes($username)).'" ';




	$arr_rez = array ();

	$dbr = wfGetDB(DB_SLAVE);

	extract($dbr->tableNames('recentchanges', 'page', 'text', 'revision'));


	$sql = "SELECT max(rc_timestamp) AS ts FROM $recentchanges,$page
        WHERE rc_cur_id=page_id AND rc_new=1 AND page_is_redirect=0";

	$got_from_cached = false;
	$requested_last_timestamp = null;
	$requested_fname = null;

	if ($result = $dbr->doQuery($sql)) {
		
		if ($dbr->numRows($result)) {
			$row = $dbr->fetchRow($result);
			$requested_last_timestamp = $row['ts'];
			$requested_fname = dirname (__FILE__) . '/tmp/' . $row['ts']. '.cached';
			if (file_exists ($requested_fname)){
				$got_from_cached = unserialize (file_get_contents ( $requested_fname ));
				//dexport ($row);
				global $glob_if_rss_got_from_cached;
				$glob_if_rss_got_from_cached = true;
			}
		}
	}



	if ($got_from_cached == false){
		$sql = "SELECT
        rev_id,
        rev_text_id,
        rev_user,
        rev_user_text ,
        rev_timestamp,

        page_id

        FROM mediawiki_revision ,`mediawiki_page`
        WHERE rev_page=page_id  and (page_is_redirect = 0 or page_is_redirect is null)
		" . $if_by_user . "
        ORDER BY rev_timestamp DESC";

		$old_pages = array ();

		if ($result = $dbr->doQuery($sql)) {
			if ($dbr->numRows($result)) {
				while ($row = $dbr->fetchRow($result)) {

					if (in_array ($row['page_id'], $old_pages)) continue;
					$old_pages [] = $row['page_id'];

					$title = Title::newFromID($row['page_id']);

					if ($title == null)
					continue;

					$fullurlforpage = $title->getFullURL();

					$mArticle = new Article($title);

					$mArticle->fetchContent($row['rev_id']);//$row['revid'], $row['rcid']
					$content = $mArticle->getContent(false);

					//dexport ($content);

					$arr_got =  array ();
					if (preg_match_all('/<rss-set>(.*)<\/rss-set>/iUms', $content, $arr_got)){

						if(count($arr_got[0]) > 0){
							for ($i=0; $i < count($arr_got[0]); $i++){
								$content = $arr_got[1][$i];


								$arr_got2 =  array ();
								if (preg_match_all('/<rss-item>(.*)<\/rss-item>/iUms', $content, $arr_got2)){

									if(count($arr_got2[0]) > 0){
										for ($j=0; $j < count($arr_got2[0]); $j++){
											$content = $arr_got2[1][$j];


											$title = '';
											$body = '';
											$date= '';

											$arr_got3 = array ();
											if (preg_match('/<title>(.*)<\/title>/iUms', $content, $arr_got3))
											$title = $arr_got3 [1];
											$arr_got3 = array ();
											if (preg_match('/<body>(.*)<\/body>/iUms', $content, $arr_got3))
											$body = $arr_got3 [1];
											$arr_got3 = array ();
											if (preg_match('/<date>(.*)<\/date>/iUms', $content, $arr_got3))
											$date = $arr_got3 [1];

											if ($date == '' || $title == '' || $body == '') continue;

											$arr_rez [] = array (
											'date' => $date,
											'title' => $title,
											'body' => $body,
											'url' => $fullurlforpage,
											);
										}
									}
								}//if preg_match

							}
						}
					}//if preg_match

				}
			}
		}
		// save to cache
		file_put_contents ($requested_fname, serialize ($arr_rez) );
	}
	else
	{
		$arr_rez = $got_from_cached;
	}


	usort ($arr_rez, 'cmp_feed_by_dates_4534');
	$arr_rez2 = array ();
	for ($i=0; $i < min(count($arr_rez), $count); $i++ )
	$arr_rez2 [] = $arr_rez [$i];
	$arr_rez = false;
	return $arr_rez2;

}


function cmp_feed_by_dates_4534 ($a, $b){
	$a_d = $a['date'];
	$b_d = $b['date'];

	$arr_got  = array ();
	$a_year = 0;
	$a_month = 0;
	$a_day = 0;
	$a_hours = 0;
	$a_minutes =0;
	$a_seconds = 0;

	if (preg_match('/(\d+)[.\/-](\d+)[.\/-](\d+) (\d+)[:](\d+)[:](\d+)/i' ,$a_d, $arr_got)){
		$a_year = $arr_got [1];
		$a_month = $arr_got [2];
		$a_day = $arr_got [3];

		$a_hours = $arr_got [4];
		$a_minutes = $arr_got [5];
		$a_seconds = $arr_got [6];
	}
	else{
		$arr_got = array ();
		if (preg_match('/(\d+)[.\/-](\d+)[.\/-](\d+)/i' ,$a_d, $arr_got)){
			$a_year = $arr_got [1];
			$a_month = $arr_got [2];
			$a_day = $arr_got [3];
		}
	}


	$arr_got  = array ();
	$b_year = 0;
	$b_month = 0;
	$b_day = 0;
	$b_hours = 0;
	$b_minutes =0;
	$b_seconds = 0;

	if (preg_match('/(\d+)[.\/-](\d+)[.\/-](\d+) (\d+)[:](\d+)[:](\d+)/i' ,$b_d, $arr_got)){
		$b_year = $arr_got [1];
		$b_month = $arr_got [2];
		$b_day = $arr_got [3];

		$b_hours = $arr_got [4];
		$b_minutes = $arr_got [5];
		$b_seconds = $arr_got [6];
	}
	else{
		$arr_got = array ();
		if (preg_match('/(\d+)[.\/-](\d+)[.\/-](\d+)/i' ,$b_d, $arr_got)){
			$b_year = $arr_got [1];
			$b_month = $arr_got [2];
			$b_day = $arr_got [3];
		}
	}
	$a_d = mktime($a_hours, $a_minutes, $a_seconds, $a_month, $a_day, $a_year);
	$b_d = mktime($b_hours, $b_minutes, $b_seconds, $b_month, $b_day, $b_year);
	if ($a_d == $b_d) return 0;
	if ($a_d < $b_d) return 1;
	return -1;
}

function convertRSSFeedToFormat ($rss , $kind, $encoding, $title_feed){
	$rez = '';
	if (isset ($_REQUEST['as_json_force']) && $_REQUEST['as_json_force'] == 'true'){
		return json_encode (array('title' => $title_feed, 'items' => $rss));
	}
	if ($kind =='atom'){
		$rez = '<?xml version="1.0" encoding="'.$encoding.'"?>
<feed xmlns="http://www.w3.org/2005/Atom">
<title><![CDATA['.$title_feed.']]></title>
		'. "\n";

		for ($i = 0; $i < count($rss);$i++){
			$rez .= '<entry>
		<title><![CDATA['.$rss[$i]['title'].']]></title>
		<link href="'.$rss[$i]['url'].'"/>
		<id>'.md5($rss[$i]['title']). '_' . md5($rss[$i]['date']). '</id>
		<updated>'.$rss[$i]['date'].'</updated>
		<summary><![CDATA['.$rss[$i]['body'].']]></summary>
		</entry>'. "\n";
		}

		$rez .= '</feed>' . "\n";
	}


	if ($kind == 'rss'){

		$rez = '<?xml version="1.0" encoding="'.$encoding.'"?>
<rss version="2.0">
<channel>
<title><![CDATA['.$title_feed.']]></title>
		'. "\n";

		for ($i = 0; $i < count($rss);$i++){
			$rez .= '<item>
		<title><![CDATA['.$rss[$i]['title'].']]></title>
		<link><![CDATA['.$rss[$i]['url'].']]></link>
		<guid><![CDATA['.md5($rss[$i]['title']). '_' . md5($rss[$i]['date']). ']]></guid>
		<pubDate><![CDATA['.$rss[$i]['date'].']]></pubDate>
		<description><![CDATA['.$rss[$i]['body'].']]></description>
		</item>'. "\n";
		}

		$rez .= '</channel></rss>' . "\n";
	}
	return $rez;
}

?>