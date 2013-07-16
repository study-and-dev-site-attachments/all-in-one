<html>
	<head>
		<title>
			Таблица <?php print $INFOABOUT_TNAME ?> в <?php print $INFOABOUT_BASE?>, docman by black zorro (x15@tut.by) 
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
				
				.div_goback {
				  font-size: 12px;
				  font-weight: bold;
				  color: orange;
				  background-color: black;
				  padding: 5px;
				  margin: 5px;
				}
				
				.table_fields_list {
					font-size: 12px;
					font-family: arial;
					border-collapse: collapse;
				}
				
				.table_fields_list th {
				  font-weight: bold;
				  background-color: #BFDBAB;
				  border: 1px solid black;
				}
				
				.table_fields_list td {
				 border: 1px solid black;
				}
				
				a {
					font-size: 12px;
					font-family: arial;
					color: orange;
				}
				
				.outer_all_fields {
					margin-left: 100px;
				}
				
				.outer_all_data {
					margin-left: 100px;
				}
				
				.table_data_list {
					font-size: 12px;
					font-family: arial;
					border-collapse: collapse;
				}
				
				.table_data_list th {
				  font-weight: bold;
				  background-color: #BFDBAB;
				  border: 1px solid black;
				}
				
				.table_data_list td {
				 border: 1px solid black;
				}				
				
				
				.div_heading_meta {
				  font-size: 16px;
				  font-weight: bold;
				  color: orange;
				  background-color: black;
				  padding: 5px;
				  margin: 5px;
				  text-align: center;
				  margin-left: 50px; margin-right: 50xp;
				}

				.div_heading_data {
				  font-size: 16px;
				  font-weight: bold;
				  color: orange;
				  background-color: black;
				  padding: 5px;
				  margin: 5px;
				  text-align: center;
				  margin-left: 50px; margin-right: 50xp;
				}
				
		</style>
	</head>
 	 <body>
 		<div class="div_heading">Таблица <?php  print $INFOABOUT_TNAME ?> в <?php print $INFOABOUT_BASE?></div>
 		<div class="div_goback"><a href="index.html">Вернуться назад к списку всех таблиц</a></div>
 		
			<div class="div_heading_meta">
				Информация о структуре таблицы
			</div>

			
 		<div class="outer_all_fields">
 		
 		<table  border='0' cellspacing='0' cellpadding='0' class="table_fields_list">
			<tr>
				<th align="left" valign="top">Название поля</th>
				<th align="left" valign="top">Тип поля</th>
				<th align="left" valign="top">Null</th>
				<th align="left" valign="top">Ключ</th>
				<th align="left" valign="top">По-умолчанию</th>
				<th align="left" valign="top">Дополнительно</th>
			</tr>

 	 <?php 
 	 foreach ($INFOABOUT_FIELD as $idx => $field){
			?>
  				<tr>
  					<td align="left" valign="top"><a name='<?php print $field['NAME']?>' ><!-- * --></a><?php print $field['NAME']?></td>
  					<td align="left" valign="top"><?php print $field['TYPE']?></td>
  					<td align="left" valign="top"><input type="checkbox" disabled="disabled" <?php print $field['NULL']?> /></td>
  					<td align="left" valign="top"><?php print $field['KEY']?></td>
  					<td align="left" valign="top"><?php print $field['DEFAULT']?></td>
  					<td align="left" valign="top"><?php print $field['EXTRA']?></td>
  				</tr>
  				<tr>
  					<td colspan="6">
  						<?php print $field['DESCRIPTION']?>
  					</td>
  				</tr>

  				<?php
 	 }
	?>
	
	</table>
	</div>
			<?php
				if ($INFOABOUT_DATA_MUSTBE){
			?>
	

			<div class="div_heading_data">
				Данные содержащиеся в таблице на момент документирования
			</div>
			
	 		<div class="outer_all_data">
 		
 		<table  border='0' cellspacing='0' cellpadding='0' class="table_data_list">
			<tr>
				<?php
				foreach ($INFOABOUT_FIELD as $idx => $field){
				?>
  				<th align="left" valign="top"><?php print $field['NAME']?></th>
  				<?php
				}
				?>
			</tr>

 	 <?php 
 	 foreach ($INFOABOUT_DATA as $idx => $data){
	 ?>
  				<tr>
  					<?php
  					foreach ($data as $idx_2 => $value) {
							?>
								<td align="left" valign="top"><?php print $value?></td>  							
							<?php
  					}
  					?>
  				</tr>
  	 <?php
 	  }
	 ?>
	
	</table>
	</div>
	
	<?php
				}
	?>

		
</body>
</html>