<?php
 if (! isset ($GLOBALS['globus_count_of_query']))
	 $GLOBALS['globus_count_of_query'] = 0;

 include_once('mysql_40_41.inc');

 $db_link_for_lite_engine = null;
 $lite_last_sql_command = null;
 class Lite_Engine {
 	
 	// присоединение к некоторому источнику данных
 	function AttachToDBConnection ($dbe_link){
 		global $db_link_for_lite_engine;
 		$db_link_for_lite_engine = $dbe_link;
 	}
 	
 	
 	function FilterRecords ($table_name , $where = null, $order_by = null){
 		global $db_link_for_lite_engine;
 		$sql = 'SELECT * FROM ' . $table_name;
 		if ($where)
 		 $sql .= ' WHERE ' . $where;
 		if ($order_by)
 		 $sql .= ' ORDER BY ' . $order_by;
 		
 		global $lite_last_sql_command;
 		$lite_last_sql_command = $sql;
 		$rset = xxx_query($sql , $db_link_for_lite_engine);
 		if (! $rset) {
 			die ('Lite_Engine:Error: sql=('.$sql.') err=' .  xxx_error($db_link_for_lite_engine));
 		}
 		$rows = array ();
 		while (($row = xxx_fetch_assoc($rset))){
 			$rows [] = $row;
 		}
 		xxx_free_result($rset);
 		return $rows;
 	}

  	function ExecuteSelect ($sql){
 		global $db_link_for_lite_engine;
 		global $lite_last_sql_command;
 		$lite_last_sql_command = $sql;
 		$rset = xxx_query($sql , $db_link_for_lite_engine);
 		if (! $rset) {
 			die ('Lite_Engine:Error: sql=('.$sql.') err=' .  xxx_error($db_link_for_lite_engine));
 		}
 		$rows = array ();
 		while (($row = xxx_fetch_assoc($rset))){
 			$rows [] = $row;
 		}
 		xxx_free_result($rset);
 		return $rows;
 	}
	
 	function ExecuteUpdate ($whole_update_sql){
 		global $db_link_for_lite_engine;
 		global $lite_last_sql_command;
 		$lite_last_sql_command = $whole_update_sql;
 		
 		$rez =  xxx_query($whole_update_sql, $db_link_for_lite_engine);		
 		if (! $rez)
 			die ('FatalError:Lite_Engine:ExecuteUpdate sql=' . $whole_update_sql . ' err = ' . xxx_error($db_link_for_lite_engine));
 		return $rez?true:false;
 	}

 	
 	function EncodeString2 ($string_value){
 		global $db_link_for_lite_engine;
 		return '\'' . xxx_real_escape_string($string_value) . '\'';
 	}
 	
	function GetLiteVariableValue($variable_name ,  $table_name , $ConfigName , $ConfigValue){
	 	$rows = Lite_Engine::FilterRecords ($table_name , $ConfigName . ' = ' . Lite_Engine::EncodeString2($variable_name));
	 	if (count ($rows) == 0) {
	 		global $lite_last_sql_command;
	 		die ('Lite_Engine::VariableNotFound: varName=' .$variable_name . ' tab = ' . $table_name . ' keyfield = '. $ConfigName . ' lookupfield = ' . $ConfigValue. ' last sql = ' . $lite_last_sql_command);
	 		return false;
	 	}
	 	$rows = $rows [0];
	 	return $rows [$ConfigValue];
	}
 	
 }
?>