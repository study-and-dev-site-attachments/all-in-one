<?php
function dexport ($x){
	die (var_export ($x , true));
}


class TDescriptor {
	var $db_link;
	var $host_name ;
	var $db_name ;
	var $user_name ;
	var $user_pass ;
	var $encoding;

	function TDescriptor  ($host_name , $db_name , $user_name , $user_pass, $encoding){
		$this->host_name  = $host_name;
		$this->db_name  = $db_name;
		$this->user_name = $user_name;
		$this->user_pass = $user_pass;
		$this->encoding = $encoding;
	}

	function Connect (){
		$this->db_link = mysql_connect($this->host_name , $this->user_name , $this->user_pass) ;
		if (! $this->db_link)
		die  ("cannot connect to database: {host='$this->host_name', db='$this->db_name', user='$this->user_name', pass='$this->user_pass'}");
		mysql_select_db($this->db_name) or die ('cannot select db: ' . mysql_error($this->db_link));

		if ($this->encoding != ''){
			mysql_query('set names ' . $this->encoding , $this->db_link) or die('set names = "' .$this->encoding . '" failed');
		}

	}

	function getConnection (){
		if ((isset ($_REQUEST ['host'])) && ( isset ($_REQUEST ['db']))&&( isset ($_REQUEST ['user']))&&( isset ($_REQUEST ['pass'])) &&( isset ($_REQUEST ['encoding']))){
			$host = $_REQUEST ['host'];
			$db = $_REQUEST ['db'];
			$user = $_REQUEST ['user'];
			$pass = $_REQUEST ['pass'];
			$encoding = $_REQUEST ['encoding'];

			//die($_REQUEST ['encoding']);

			$_SESSION ['docman-connector'] = new TDescriptor($host , $db , $user , $pass, $encoding);
			return $_SESSION ['docman-connector'];

		}
		if (isset($_SESSION['docman-connector'])){
			return $_SESSION['docman-connector'];
		}
		else{
			die ('данный документ должен использоваться совместно с формой для указания параметров соединения с бд.');
		}
		return false;
	}// --

	
	function toString (){
		return $this->db_name . ' at ' . $this->host_name;
	}

}

class TDummyDescriptor extends TDescriptor {
	function  TDummyDescriptor () {}
}

 ?>