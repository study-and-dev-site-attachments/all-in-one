<pre>
<?php

	include_once('db-support/names.php');
	
	// ������� ���������� ���� ������
	// �������� false ������ ��� �������� ���� ��� ��� ������� �������� ������ ������� ���������� �������� - ������ ������� ����� ���
	$w_TREF_subscription->DeleteRecordsByExample(false);
	$w_TREF_user->DeleteRecordsByExample(false);
	$w_TREF_newspaper->DeleteRecordsByExample(false);
	
	
	// ������ ������� ������� �������
	$vasyan = new TDummyRecord();
	// ��� �� ����� ���� ��� ��� Zend-������, ����� �� ���������� ����� ������ ���� ������ ����������� ���������� vasyan � 
	// ����� ������ �������� "->" ����� ��������� ��������� � ���, ����� ������ �������� ��� ��� - ��� ������
	
	$vasyan = $w_TREF_user->GetFreeRecord();
	// �������� ������ �� ��������� ������ - ��������� �������� ��� ��� ����� �� ������� 
	// � ����������� � ���� ������ ��������, ��� ��� �������� �� ���� � �������� �� ������� � ������� �������� ����� ������
	$vasyan->AppendRecord ();
	// ��������� ������ � ����� ����������
	// ������� ������� ����� �������� ����� ��� �������
	// ��� ���� ����� ������� ������ � ���� ������ ���� ��� ������� ������� - SetFieldValue - 
	// � ���� ������ ������ � �������� ������� ��������� ��� ���� � � �������� ������� �������� ������� ����
	// ��� �� ����� ���������� �� ��������� ����������� ���� � ����������� �������� ����� � �������, � ����� 
	// ������������� ����. �������� � ������
	// ������ ������� SetFieldFunction - ����� �������� ������ ���������� ��� ���������� ����, � ������ �������� ��� ������ ������ � ������� ������������ 
	// mysql-������� -  ����� ���������� ���� ���� � ����������� �������������� ������ �����������
	$vasyan->SetFieldFunction ($x_TREF_user->BirthDate, 'NOW()');
	$vasyan->SetFieldValue ($x_TREF_user->Sex, 'male');
	$vasyan->SetFieldValue ($x_TREF_user->UserFIO, 'Vasily Pupkin');
	// ������ ������ ����� ��������� � ����
	
	$vasyan->UpdateRecord();
	
	// ����������� ��� ����� ����� �� ������� ������� ��� ���������� ��������� ��������� �������� �� �������
	
	$vasyan->EditRecord ();
	// ��������� ������ � ����� ��������������
	$vasyan->SetFieldValue ($x_TREF_user->Sex, 'female');
	$vasyan->UpdateRecord ();
	
	
	// ����� ���������� ������� Update - ����� ��� �������� ��������� ������ � ���������� ����� ������, 
	// ������������� ���������� �������� 
	// ������������� ������������ ����� - �������� ���� �������� id - ��� � ������� �������� auto_increment
	$vid = $vasyan->GetIDValue ();
	// ��� ������� � ��������� ����� ������������ ������� GetFieldValue � ������� ���������� - ������ ����
	print '�������� id ��� �������: ' . $vasyan->GetFieldValue ($x_TREF_user->UserFIO) . ' = ' . $vid . '<hr />';
	
	// ������ �������� ������ "������"
	
	$pravda = $w_TREF_newspaper->GetFreeRecord();
	$pravda->AppendRecord ();
	$pravda->SetFieldValue ($x_TREF_newspaper->Code, 'EA45');
	$pravda->SetFieldValue ($x_TREF_newspaper->DefaultPrice, 999.99);
	$pravda->SetFieldValue ($x_TREF_newspaper->NewspaperTitle, 'Pravda');
	$pravda->UpdateRecord();
	
	
	$pravda2 = $w_TREF_newspaper->GetFreeRecord();
	$pravda2->AppendRecord ();
	$pravda2->SetFieldValue ($x_TREF_newspaper->Code, 'EA45');
	// ����� ������ ���� ������ ���������� ������ �.�. ���� Code �������� ����������
	$pravda2->SetFieldValue ($x_TREF_newspaper->DefaultPrice, 279.99);
	$pravda2->SetFieldValue ($x_TREF_newspaper->NewspaperTitle, 'Trud');
	
	$pravda2->SetStrictErrorMode (false);
	// ��������� ��������� ������ -- ��� �����������, ������ ���� � ���� ������ � �� ������ ���� �� ������ 
	// ����� ������� �� ����� ��������, 
	// � ������ ������� ����������� ��������� �������� ������ ���� false
	
	if (!$pravda2->UpdateRecord())
		print '�� ���� �������� ����� ������' . '<hr />';
	else 
		print '������, ����� ��������� �������'. '<hr />';
		
	// ������ ������� ��� ����� �������� ������ �� ����������� ������ ���� ������
	// � ���������� ������ � ��� ���� id ���� ������, ����� ������ ���:
	$trud = $w_TREF_newspaper->GetLinkedRecord($pravda->GetIDValue());
	// ���� �� �������� ������ �� ������������ ������ �� �� ��� ����� ����� ��������� �� � ����� ����������
	$trud->AppendRecord ();
	$trud->SetFieldValue ($x_TREF_newspaper->Code, 'EA45');
	$trud->SetFieldValue ($x_TREF_newspaper->NewspaperTitle, 'Trud Updated');
	$trud->UpdateRecord ();
	// � ����� ������ ������� ����� ����� � ������ UpdateRecord � ������ ����� ������ - ��������� ����� ������
	
	// ������ ������� ������ ���� ����� ��������������� �� ���������� ����
	
	// ���� ��������� ������� ������� �������� � ��������� �������
	// ����� ������� �� ��� - FilterRecords - ���������� ��� ��������� - ���������� sql ������� ��� ������ ������, �
	// ����� ������ �������� - ��� ���� �� �������� ���� ����������
	$flist = $w_TREF_newspaper->FilterRecords(false, $x_TREF_newspaper->DefaultPrice);
	for ($i = 0; $i < count($flist); $i++){
		$dum = $flist[$i];
		print '������� ������: ' . $dum->GetFieldValue ($x_TREF_newspaper->NewspaperTitle) . '<hr />';

		$dum->EditRecord ();
		// ������ ��� ���� ������ �������� ������ � ������� - ��� ����� ������� �������� ��� ��������� �����, �� �������� ��������, 
		// ��� ����  ����� � �������� ������ �������� ��� ���� �� ������� ������ �� ����� �������������
		$dum->InitFromAssoc (array (
			$x_TREF_newspaper->DefaultPrice => 2000,
			'BAD_FIELD_NAME' => 1000
		));
		$dum->UpdateRecord ();
	}
	
	// ��������� ������ ������ ������ ����� ������������ ��� �� ������ ���� �������������� sql-������ ������
	// �� ����� ����� ������� �������� - ���������� ���������� ������� � ������� � ������� ����� ������ ���� �������� - 
	// ������������ ����������� mysql - LIMIT
	// ��������� ������� ������, ����� ������� � ���������� �������, ����� ���������� �� ����
	$flist = $w_TREF_newspaper->FilterRecordsInRange(false, 0, 1, $x_TREF_newspaper->DefaultPrice);
	print '�������� ������: ';
	print_r ($flist);
	print '<hr />';
	// ��-�� LIMIT (paging-�) ��� ����� �������� ����� ���� ������, ���� ��� ������� (��� ������� �� ������ �� ������� �������� ��� ������)
	// ������� ��� ��� ������ ����������� � �������
	print '������ ���������� ���������� �������: ' . $w_TREF_newspaper->GetCountOfLastQueryRows() . '<hr />';
	
	// ��������� ������� - ��� ������� - �� ����� ������ ������� ������ �� ����� ������� ������, 
	// � � ����� �-�������, ����� �������� ��� ����� ����� � �������� - �� ���� ������ ���� ����� ������ ��� ������
	
	$flist = $w_TREF_newspaper->FilterRecordsByExample(array (
		$x_TREF_newspaper->Code => 'EA45',
		FLD_ONLYLESSTHAN . $x_TREF_newspaper->DefaultPrice => 2000,
		NOT_FIELD . $x_TREF_newspaper->NewspaperID => array (101, 102, 103, 104),
	), 
	0, 1, $x_TREF_newspaper->DefaultPrice);
	// ����� ������ � ������ �������� ������� ��������� ������� ������� � ������� paging-�,
	// ��������� �������� - ������ ����������
	
	print '��� ����������� ��������� ������ �� �����: ' . $w_TREF_newspaper->GetSQLCommand() . '<hr />';
	
	// ������ ������ ������
	
	$vasyan->DeleteRecord ();
	

	// ������ ������ 
	// ������ ����� ������� ����������� ��� ����������
	print '������ ����� ������������ ��� ����������: ';
	print_r($vasyan->GetNotNullFieldNames ());
	print '<hr />';
	// �������� ��� ����
	print '��� ���� UserFIO: ' .$vasyan->GetTypeName ($x_TREF_user->UserFIO) . '<hr />';
	// �������� ����� SQL ������� ������� ������
	print '���������� ������������� ������: ' . $vasyan->CreateIdentitySQL () . '<hr />';
	// �������� ��� ���� Primary Key
	print '��� ���� ���������� ����� �������: ' . $vasyan->GetIDFieldName (). '<hr />';
	
	// �������� ������ ������� �� ������� �� Primary Key ID
	print '���� ������ ������� �� ������ �������� �� ������: (1,2,3): ';
	print_r($w_TREF_newspaper->GetLinkedRecords (array (10, 11, 103)));
	print '<hr />';
	// �������� ������������� ��������� �������
	if ($w_TREF_newspaper->IsTableExists ('megatable'))
		print '������� megatable ����������'. '<hr />';
	else 
		print '������� megatable ���'. '<hr />';
		
		
	// 	�������� �������� ���������� mysql
	print 'datadir = ' . $w_TREF_newspaper->GetMysqlVariable('datadir') . '<hr />';
	
	// ������� ���������� ������� �� ���������� �������
	
	print '����� ���������� ������� �����: ' . $w_TREF_newspaper->CountRecords() . ' � ����� ��� ���� ������� ����������� 1000 : '
	 . $w_TREF_newspaper->CountRecords(array (FLD_GREATEROREQUALTHAN . $x_TREF_newspaper->DefaultPrice => 1000) ) . '<hr />';
	 
	// ����� ����������� ��� ���������� ����� ������� � ������ -- ��, ������ ������� ������ ������������

	// ������� ����������� ������ � ������ ������� ����������� �������� �� �����: 
	//$w_TREF_newspaper->CopyAllDataToTable ($x_TREF_foo);
	
	$w_TREF_newspaper->DropTable('CopyOf');
	
	// �������� ������������ �������� ��� ���������� ����
	print '������������ ���� ������: ' . $w_TREF_newspaper->GetMaxValueForField($x_TREF_newspaper->DefaultPrice) . '<hr />';
	
	// �������� �������������� � ��������� ������� ��� ������ ���� �����������
	// ���� ������ ���������� ����� ������ �� ��� �������������� � ���� �������, ������ ������� �������� ������
	// ������ � ���� ������� - ��� ����� �-������
	print '��������� ���������� ������� sql-�������: ';
	print_r ($w_TREF_newspaper->ExecuteSQLCommandWithResultSet('select * from ' . $x_TREF_newspaper->table_name));
	print '<hr />';
	// ��� �� ���� ������� �� ���������� ������� ������
	if ($w_TREF_newspaper->ExecuteSQLCommandWithoutResultSet('truncate megatable'))
		print '��������� ������� ������� �������������� ������� <hr />';
	else
		print '������� ������� �������������� ������� ��� � ��������� �� ���� ���������<hr />';
	// ������ ����� ������� �� �������� ID
	if ($w_TREF_user->DeleteRecordByID ($vid))
		print '������� ������ ���� ������� �� ID = ' . $vid . '<hr />';
	else
		print '��� �� ������ ������� �� ������� �� ID = ' . $vid . '; �������: '.$w_TREF_user->GetLastError().'<hr />';
		
	// ��� �� ������ ������� ������ � ���� �������������� �������
	$w_TREF_user->DeleteRecordsByExample (array ($x_TREF_user->Sex => 'alien'));
	
?>
</pre>