<?php
  // скрипт отбирающий данные из серверной базы данных и отправл€ющий их клиенту в формате JSON

  header ('Content-Type: text/javascript');
  $conn = new PDO('sqlite:notebook.db3');

  $notes = array ();
  foreach ($conn->query('SELECT * from notes') as $row) {
     $notes [] = array ('id' => $row['id'],
     'category' =>  $row['category'],
     'dateof' => $row['dateof'],
     'title' =>  $row['title'],
     'comment' => $row['comment']);
  }
  print json_encode ($notes);

?>