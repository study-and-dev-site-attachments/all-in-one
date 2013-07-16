<?php
 require ('xajax_0.2.4/xajax.inc.php');

 function populate_db_list($the_form)
 {
 	$objResponse = new xajaxResponse();
 	$text = false;
 	
 	ob_start();
 	$cnt = mysql_connect($the_form ['host'], $the_form ['user'], $the_form ['pass']);
 	ob_end_clean();
 	
 	if ($cnt){
 		$text = '';
 		$list_of = mysql_list_dbs($cnt);
 		if ($list_of){
 			while ($row = mysql_fetch_assoc($list_of)){
 				$text .= '<option value="'.$row ['Database'].'">'.$row ['Database'].'</option>' . "\n";
 			}
 		}
 	}
 	if ($text === false)
 		$objResponse->addAlert("������������� � ���� ������ �� ����� ���� ���������");
 	else
 		$objResponse->addAssign("helper_db_lists","innerHTML",$text);
 	return $objResponse;
 }


 // Instantiate the xajax object.  No parameters defaults requestURI to this page, method to POST, and debug to off
 $xajax = new xajax();
 //$xajax->debugOn(); // Uncomment this line to turn debugging on
 // Specify the PHP functions to wrap. The JavaScript wrappers will be named xajax_functionname
 $xajax->registerFunction("populate_db_list");

 // Process any requests.  Because our requestURI is the same as our html page,
 // this must be called before any headers or HTML output have been sent
 $xajax->processRequests();
?>
<html>
<head>
 <LINK rel="stylesheet" type="text/css" href="docstyles.css">
 <?php
 
	 $xajax->printJavascript('xajax_0.2.4');
 ?>
	
</head>
<body>

<div class="heading_rog">������ - ������������ - ������� ��� �������� �������� � ���������������� ��������� ���� ������</div>

<div class="div_comment">
 ��� ������ ������ ������� ���������� ������� ���� � ������� mysql <br />
 ��� ������ ������� ��������� ��������� ������� documentator - � ��� ������ ���� ������ ��� ������� ��� ������������ ����
 �� �� ������ �������� ��������� � ���������������� ����� documentator.config.ini <br />
 ����� ������� ������� ��� �������� ������ ���� ������ � ����� ���� � ������ ������������� ���� ����: <br/>
 "������� -> ������ �����" <br/>
 ��� ������� ���� ���� �� ������ ������� ���������� - � ������� ������������ html ��������� tinymce <br/>
 ��� ��������� ���� ����������� �� ������ ������������ ����������� ���� ��� ���� ����� ������������ �������� �� ������ �������� �������� ������ ��� ����� ���� ������  <br/>
 ����� ���������� �������������� �� ������ ��������� ������� "������� ���������", ����� ���� ����� ������������ ����� html ������ � ���������� ���� ������� � ����
</div>
<hr>

<form method='post' action='docman.php' id="simple_form_login">
	<table cellpadding="0" cellspacing="0" border="0" class="table_from_db_params">
		<tr>
			<td align="left" valign="top">
				<strong>������� ��� ������� ���� ������</strong>
			</td>
			<td align="left" valign="top">
				<input type="text" name='host' value='localhost' size="30" />
			</td>
		</tr>
		
		<tr>
			<td align="left" valign="top">
				<strong>������� ��� ������������ ��� ����������� � ����</strong>
			</td>
			<td align="left" valign="top">
				<input type="text" name='user' value='root' size="30" />
			</td>
		</tr>
		
		<tr>
			<td align="left" valign="top">
				<strong>������� ������ ������������</strong>
			</td>
			<td align="left" valign="top">
				<input type="password" name='pass' value='' size="30" />
			</td>
		</tr>
		
		<tr>
			<td align="left" valign="top">
				<strong>������� ��� ���� ������</strong>
			</td>
			<td align="left" valign="top">
				<input type="text"  id="db" name='db' value=''   size="30" />
			</td>
		</tr>

		<tr>
			<td align="left" valign="top">
				<strong>��������� �� ��������� ����� �� ������ (ajax - ��� ������������ ��������)</strong>
			</td>
			<td align="left" valign="top">
				<select id="helper_db_lists" onchange="document.getElementById('db').value = this.value;">
				</select>
				<input type="button" onclick="xajax_populate_db_list(xajax.getFormValues('simple_form_login'))" value="�������� ������ ��� ������ �� �������" class="button"/>
			</td>
		</tr>
		
				
		<tr>
			<td align="left" valign="top">
				<strong>������� �������� ��������� ��� ������������� � ������� (mysql 4.1 � ����)</strong>
			</td>
			<td align="left" valign="top">
				<select name="encoding">
					<option value="">��������� ��-���������</option>
					
					<option value="big5">Big5 Traditional Chinese    | big5_chinese_ci</option>
					<option value="dec8">DEC West European           | dec8_swedish_ci</option>
					<option value="cp850"> DOS West European           | cp850_general_ci</option>
					<option value="hp8"> HP West European            | hp8_english_ci</option>
					<option value="koi8r"> KOI8-R Relcom Russian       | koi8r_general_ci</option>
					<option value="latin1"> cp1252 West European        | latin1_swedish_ci</option>
					<option value="latin2"> ISO 8859-2 Central European | latin2_general_ci</option>
					<option value="swe7"> 7bit Swedish                | swe7_swedish_ci</option>
					<option value="ascii"> US ASCII                    | ascii_general_ci</option>
					<option value="ujis"> EUC-JP Japanese             | ujis_japanese_ci</option>
					<option value="sjis"> Shift-JIS Japanese          | sjis_japanese_ci</option>
					<option value="hebrew"> ISO 8859-8 Hebrew           | hebrew_general_ci</option>
					<option value="tis620"> TIS620 Thai                 | tis620_thai_ci</option>
					<option value="euckr"> EUC-KR Korean               | euckr_korean_ci</option>
					<option value="koi8u"> KOI8-U Ukrainian            | koi8u_general_ci</option>
					<option value="gb2312"> GB2312 Simplified Chinese   | gb2312_chinese_ci</option>
					<option value="greek"> ISO 8859-7 Greek            | greek_general_ci</option>
					<option value="cp1250"> Windows Central European    | cp1250_general_ci</option>
					<option value="gbk"> GBK Simplified Chinese      | gbk_chinese_ci</option>
					<option value="latin5"> ISO 8859-9 Turkish          | latin5_turkish_ci</option>
					<option value="armscii8"> ARMSCII-8 Armenian          | armscii8_general_ci</option>
					<option value="ucs2"> UCS-2 Unicode               | ucs2_general_ci</option>
					<option value="cp866"> DOS Russian                 | cp866_general_ci</option>
					<option value="keybcs2"> DOS Kamenicky Czech-Slovak  | keybcs2_general_ci</option>
					<option value="macce"> Mac Central European        | macce_general_ci</option>
					<option value="macroman"> Mac West European           | macroman_general_ci</option>
					<option value="cp852"> DOS Central European        | cp852_general_ci</option>
					<option value="latin7"> ISO 8859-13 Baltic          | latin7_general_ci</option>
					<option value="cp1251"> Windows Cyrillic            | cp1251_general_ci</option>
					<option value="cp1256"> Windows Arabic              | cp1256_general_ci</option>
					<option value="cp1257"> Windows Baltic              | cp1257_general_ci</option>
					<option value="binary"> Binary pseudo charset       | binary</option>
					<option value="geostd8"> GEOSTD8 Georgian            | geostd8_general_ci</option>
					<option value="cp932"> SJIS for Windows Japanese   | cp932_japanese_ci</option>
					<option value="eucjpms"> UJIS for Windows Japanese   | eucjpms_japanese_ci</option>
				</select>
			</td>
		</tr>
		
		<tr>
			<td align="left" valign="top">
				<input type="submit" value="Log-in" class="button" />
			</td>
			<td align="left" valign="top">
				&nbsp;
			</td>
		</tr>
				
		

		
	</table>
	
	
</form>

</body>
</html>