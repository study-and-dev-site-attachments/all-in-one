<?php
  // скрипт выполн€ющий сохранение пришедших от клиента данных в серверную базу данных
  $records = json_decode ($_REQUEST['records']);

  $conn = new PDO('sqlite:notebook.db3');

  $conn->query ('DELETE FROM notes');

  $stmt = $conn->prepare("INSERT INTO notes (id, category, dateof, title, comment) values (:id,:category, :dateof,:title,:comment)");

  for ($i = 0; $i < count($records); $i++){
    $r = $records[$i];
    $stmt->bindValue(':id', $r->id, PDO::PARAM_INT);
    $stmt->bindValue(':category', urldecode($r->category), PDO::PARAM_STR);
    $stmt->bindValue(':title', urldecode($r->title), PDO::PARAM_STR);
    $stmt->bindValue(':dateof', $r->dateof, PDO::PARAM_STR);
    $stmt->bindValue(':comment', urldecode($r->comment), PDO::PARAM_STR);
    $stmt->execute();
  }
 
  die (json_encode (array ('status'=>'true')));
?>