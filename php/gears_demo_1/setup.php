<?php
  // ������ ����������� �������� �������� ���� ������ � ����������� �� ��������� ������� �������

  //��������� ����������� � ���� ������
  $conn = new PDO('sqlite:notebook.db3');

  // ������� ������� 'notes' (����, �������, ��� ����������)
  $conn->query ('DROP TABLE IF EXISTS notes');
  $conn->query ('CREATE TABLE notes (id INTEGER PRIMARY KEY, category varchar(100), dateof datetime, title varchar(100), comment TEXT) ');

  // ������� �������������� ������
  $stmt = $conn->prepare("INSERT INTO notes (category, dateof, title, comment) values (:category,:dateof,:title,:comment)");

  $categs = array ('�������', '�����', '������', '�����');
  for ($i = 0; $i < 15; $i++){
    // ��������, ����� ����� ������� ������ ��� ���� ������ sqlite ���� � ��������� utf8

    $stmt->bindValue(':category', iconv('windows-1251', 'utf-8',$categs[$i % 4]), PDO::PARAM_STR);
    $stmt->bindValue(':dateof', '2007.'. (1+($i % 11)) . '.25', PDO::PARAM_STR);
    $stmt->bindValue(':title', iconv('windows-1251', 'utf-8', '�������_' . $i ), PDO::PARAM_STR);
    $stmt->bindValue(':comment', iconv('windows-1251', 'utf-8', '����������_' . $i ), PDO::PARAM_STR);
    $stmt->execute();

  }

?>

<h1>�������� �������� ���� ������ ���������</h1>