<?php
  
  $flash_PacketName = 'src.beans.model';
  
  include_once    ('db_connection.inc');
  
  $output_dir = 'generator/';
  $output_dir2 = 'generator2/';
  $output_dir3 = 'generator3/';
  
  $rset = mysql_listtables(getGlob_DB_name() , getDBLink (RESOURCE_MYSQL_ANGLE));
  $ar_tables = array ();
  while ($row = mysql_fetch_array($rset)){
  	$fn = $output_dir . $row [0] . ".inc";
  	$fn2 = $output_dir2 . $row [0] . ".ser";
  	
  	$fn3  = 'xxx';
  	if (strpos($row[0] , $prefix . '_') === 0)
  		$fn3 = $output_dir3 . 'TREF_' . substr($row[0], 1 + strlen($prefix)) . ".as";
  	else 
  		$fn3 = $output_dir3 . 'TREF_' . $row [0] . ".as";
  	
  	$h = fopen ($fn, "w");
  	$h2 = fopen ($fn2, "w");
  	$h3 = fopen ($fn3, "w");
  	
  	fwrite ($h , "<?php \n");
  	fwrite ($h , "class TREF_{$row[0]}  extends TBase_TREF {\n");
  	

  	fwrite ($h3 , "package  " . $flash_PacketName . "\n");
  	fwrite ($h3 , "{\n");
  	if (strpos($row[0] , $prefix . '_') === 0)
  		fwrite ($h3 , "\tpublic class  TREF_" . substr($row[0], 1 + strlen($prefix)). " \n");
  	else 
  		fwrite ($h3 , "\tpublic class  TREF_" . $row[0] . " \n");
  	
  	fwrite ($h3 , "\t{\n");

  	$rsetFlds = mysql_query("SELECT * FROM  {$row[0]} WHERE 1 = 2" , getDBLink (RESOURCE_MYSQL_ANGLE));
  	$fields = array ();
  	print ("-- process -- SELECT * FROM  {$row[0]} WHERE 1 = 2" . '<br />');
	fwrite ($h , "\tvar \$table_name");
	fwrite ($h , " =  '" .$row[0] . "';\n");
	
	fwrite ($h , "\tfunction TREF_{$row[0]} () {}\n");
	
	
	
  	if (strpos($row[0] , $prefix . '_') === 0)
  		fwrite ($h3 , "\t\tpublic function  TREF_" . substr($row[0], 1 + strlen($prefix)). " ():void {} \n");
  	else 
  		fwrite ($h3 , "\t\tpublic function TREF_" . $row[0] . " ():void {} \n");

  		
  		
	$ar_tables  [] = "TREF_{$row[0]}";
	
  	for ($i = 0; $i < mysql_num_fields($rsetFlds); $i++){
  		$nam = mysql_field_name($rsetFlds , $i);
  		$typ = mysql_field_type($rsetFlds , $i);
  		$siz = mysql_field_len ($rsetFlds , $i);
  		fwrite ($h , "\tvar \$" . $nam);
  		fwrite ($h , " =  '" .$nam . "';\n");
  		$fields [] = array ('name' => $nam , 'type' => $typ, 'size' => $siz);

  		//public static const EFFECT_END:String = "effectEnd";
  		fwrite ($h3 , "\t\tpublic static const " . $nam . ' : String = "'.$nam.'"; ' . "\n");
  	}
  	
  	fwrite ($h , "\n}\n");
  	fwrite ($h , '
$x_TREF_'.$row[0].' = new TREF_'.$row[0].'();
$w_TREF_'.$row[0].' = new TGenericEntitySet(RESOURCE_MYSQL_ANGLE , $x_TREF_'.$row[0].'->table_name);
$glob_PHP_JNDI_Lookup_Cache [$x_TREF_'.$row[0].'->table_name] = $w_TREF_'.$row[0].';
makeFilterCacheEnabled ($w_TREF_'.$row[0].', \'$x_TREF_'.$row[0].'\');
');


  	fwrite ($h , "\n?>");
  	

  	fwrite ($h3 , "\t}\n");
  	fwrite ($h3 , "}\n");

  	
  	fwrite ($h2 , serialize(array ('table-name' => $row[0] , 'fields' => $fields) ));
  	fclose ($h);
  	fclose ($h2);
  }
  
   $h = fopen ("all_tref_includes.inc", "w");
   fwrite ($h , "<?php\n");
   foreach ($ar_tables as $i)
   	fwrite ($h , "\$dummy_null_{$i} = new " .  $i . "();\n");
   fwrite ($h , "\n?>");
   fclose ($h);
  
?>
