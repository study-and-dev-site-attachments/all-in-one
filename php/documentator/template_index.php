<html>
	<head>
		<title>
			index of tables in <?php print $INFOABOUT_BASE?>, docman by black zorro (x15@tut.by) 
		</title>
		<style>
				body {font-size: 12px; font-family: arial;}
				.div_table_here {
					font-size: 14px;
					margin: 5px;
					padding: 3px;
					border-left: 2px solid black;
				}
				.outer_all_tables{
					margin-left: 100px;
				}
				
				.div_heading {
				  font-size: 16px;
				  font-weight: bold;
				  color: orange;
				  background-color: black;
				  padding: 5px;
				  margin: 5px;
				}
		</style>
	</head>
 	 <body>
 		<div class="div_heading">Список всех таблиц в базе данных: <strong><?php print $INFOABOUT_BASE?></strong></div>
 		<div class="outer_all_tables">
 	 <?php 
 	 foreach ($INFOABOUT_TABLE as $table_name => $arr_fields){
			?>
			<div class="div_table_here">
				<a href='table_<?php print $table_name ;?>.html'><?php print $table_name ;?></a>
			</div>
			<?php
 	 }
	?>
	</div>
		
</body>
</html>