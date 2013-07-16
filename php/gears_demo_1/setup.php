<?php
  // скрипт выполняющий создание тестовой базы данных с заполнением ее случайным набором записей

  //открываем подключение к базе данных
  $conn = new PDO('sqlite:notebook.db3');

  // удаляем таблицы 'notes' (если, конечно, она существует)
  $conn->query ('DROP TABLE IF EXISTS notes');
  $conn->query ('CREATE TABLE notes (id INTEGER PRIMARY KEY, category varchar(100), dateof datetime, title varchar(100), comment TEXT) ');

  // создаем подготовленный запрос
  $stmt = $conn->prepare("INSERT INTO notes (category, dateof, title, comment) values (:category,:dateof,:title,:comment)");

  $categs = array ('Покупки', 'Отдых', 'Работа', 'Семья');
  for ($i = 0; $i < 15; $i++){
    // внимание, нужно чтобы входные данные для базы данных sqlite были в кодировке utf8

    $stmt->bindValue(':category', iconv('windows-1251', 'utf-8',$categs[$i % 4]), PDO::PARAM_STR);
    $stmt->bindValue(':dateof', '2007.'. (1+($i % 11)) . '.25', PDO::PARAM_STR);
    $stmt->bindValue(':title', iconv('windows-1251', 'utf-8', 'Заметка_' . $i ), PDO::PARAM_STR);
    $stmt->bindValue(':comment', iconv('windows-1251', 'utf-8', 'Информация_' . $i ), PDO::PARAM_STR);
    $stmt->execute();

  }

?>

<h1>Создание тестовой базы данных завершено</h1>