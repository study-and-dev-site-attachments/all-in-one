<pre>
<?php

	include_once('db-support/names.php');
	
	// очищаем содержимое всех таблиц
	// параметр false служит для указания того что нет особого критерия отбора записей подлежащих удалению - значит удалить нужно все
	$w_TREF_subscription->DeleteRecordsByExample(false);
	$w_TREF_user->DeleteRecordsByExample(false);
	$w_TREF_newspaper->DeleteRecordsByExample(false);
	
	
	// теперь добавим парочку записей
	$vasyan = new TDummyRecord();
	// это на самом деле хак для Zend-студии, чтобы ее анализатор понял какому типу данных принадлежит переменная vasyan и 
	// после набора символов "->" чтобы возникала подсказка о том, какие методы доступны для вас - это удобно
	
	$vasyan = $w_TREF_user->GetFreeRecord();
	// получаем ссылку на свободную запись - свободная означает что она никак не связана 
	// с хранящимися в базе данных записями, так что заполнив ее поля и сохранив мы получим в таблице клиентов новую запись
	$vasyan->AppendRecord ();
	// переводим запись в режим добавления
	// следует указать набор значений полей для клиента
	// для того чтобы вносить данные в поля записи есть две базовые функции - SetFieldValue - 
	// в этом случае указав в качестве первого параметра имя поля а в качестве второго значение данного поля
	// вам не нужно заботиться об генерации корректного кода с заключением тексовых полей в ковычки, а также 
	// экранирования спец. символов в строке
	// вторая функция SetFieldFunction - также получает первым параметром имя некоторого поля, а второй параметр это строка текста с вызовом произвольной 
	// mysql-функции -  здесь анализатор типа поля с последующим декорированием данных отключается
	$vasyan->SetFieldFunction ($x_TREF_user->BirthDate, 'NOW()');
	$vasyan->SetFieldValue ($x_TREF_user->Sex, 'male');
	$vasyan->SetFieldValue ($x_TREF_user->UserFIO, 'Vasily Pupkin');
	// теперь запись нужно сохранить в базу
	
	$vasyan->UpdateRecord();
	
	// предположим что после этого вы приняли решение что необходимо исправить некоторые сведения об клиенте
	
	$vasyan->EditRecord ();
	// переводим запись в режим редактирования
	$vasyan->SetFieldValue ($x_TREF_user->Sex, 'female');
	$vasyan->UpdateRecord ();
	
	
	// после выполнения команды Update - общей для операции изменения данных и добавления новой записи, 
	// автоматически получаются значения 
	// автоматически генерируемых полей - например поля счетчика id - оно в таблице является auto_increment
	$vid = $vasyan->GetIDValue ();
	// для доступа к значениям полей используется функция GetFieldValue с входным параметром - именем поля
	print 'значение id для клиента: ' . $vasyan->GetFieldValue ($x_TREF_user->UserFIO) . ' = ' . $vid . '<hr />';
	
	// теперь создадим объект "газета"
	
	$pravda = $w_TREF_newspaper->GetFreeRecord();
	$pravda->AppendRecord ();
	$pravda->SetFieldValue ($x_TREF_newspaper->Code, 'EA45');
	$pravda->SetFieldValue ($x_TREF_newspaper->DefaultPrice, 999.99);
	$pravda->SetFieldValue ($x_TREF_newspaper->NewspaperTitle, 'Pravda');
	$pravda->UpdateRecord();
	
	
	$pravda2 = $w_TREF_newspaper->GetFreeRecord();
	$pravda2->AppendRecord ();
	$pravda2->SetFieldValue ($x_TREF_newspaper->Code, 'EA45');
	// здесь должна быть ошибка добавления записи т.к. поле Code является уникальным
	$pravda2->SetFieldValue ($x_TREF_newspaper->DefaultPrice, 279.99);
	$pravda2->SetFieldValue ($x_TREF_newspaper->NewspaperTitle, 'Trud');
	
	$pravda2->SetStrictErrorMode (false);
	// отключаем обработку ошибок -- как критическую, теперь если в ходе работы с БД возник сбой то работа 
	// всего скрипта не будет прервана, 
	// а просто функция выполнившая ошибочное действие вернет флаг false
	
	if (!$pravda2->UpdateRecord())
		print 'не смог добавить дубль газеты' . '<hr />';
	else 
		print 'ошибка, дубль добавился успешно'. '<hr />';
		
	// теперь покажем как можно получить ссылку на добавленную внутрь базы запись
	// в простейшем случае у нас есть id этой записи, тогда делаем так:
	$trud = $w_TREF_newspaper->GetLinkedRecord($pravda->GetIDValue());
	// хотя мы получили ссылку на существующую запись но мы все равно можем перевести ее в режим добавления
	$trud->AppendRecord ();
	$trud->SetFieldValue ($x_TREF_newspaper->Code, 'EA45');
	$trud->SetFieldValue ($x_TREF_newspaper->NewspaperTitle, 'Trud Updated');
	$trud->UpdateRecord ();
	// в таком случае изменив часть полей и сделав UpdateRecord я получу новую запись - частичную копию газеты
	
	// теперь получим список всех газет отсортированных по некоторому полю
	
	// есть несколько функций которые отбирают и сортируют даннные
	// самая простая из них - FilterRecords - получающая два параметра - корректное sql условие для отбора данных, а
	// также второй параметр - имя поля по которому идет сортировка
	$flist = $w_TREF_newspaper->FilterRecords(false, $x_TREF_newspaper->DefaultPrice);
	for ($i = 0; $i < count($flist); $i++){
		$dum = $flist[$i];
		print 'Найдена газета: ' . $dum->GetFieldValue ($x_TREF_newspaper->NewspaperTitle) . '<hr />';

		$dum->EditRecord ();
		// теперь еще один способ изменить данные в таблице - так можно указать значения для множества полей, но обратите внимание, 
		// что если  подам в качестве данных неверное имя поля то никаких ошибок не будет сгенерировано
		$dum->InitFromAssoc (array (
			$x_TREF_newspaper->DefaultPrice => 2000,
			'BAD_FIELD_NAME' => 1000
		));
		$dum->UpdateRecord ();
	}
	
	// следующий способ отбора данных также предполагает что вы должны сами сформилировать sql-запрос отбора
	// но также можно указать параметр - количество отбираемых записей и позицию с которой отбор должен быть выполнен - 
	// используется возможность mysql - LIMIT
	// указываем условие отбора, номер позиции и количество записей, затем сортировку по полю
	$flist = $w_TREF_newspaper->FilterRecordsInRange(false, 0, 1, $x_TREF_newspaper->DefaultPrice);
	print 'Отобраны записи: ';
	print_r ($flist);
	print '<hr />';
	// из-за LIMIT (paging-а) так будет отобрана всего одна запись, хотя под условие (раз условие не задано то слудует отобрать все записи)
	// подошли все две записи находящиеся в таблице
	print 'Полное количество отобранных записей: ' . $w_TREF_newspaper->GetCountOfLastQueryRows() . '<hr />';
	
	// следующая фукнция - моя любимая - мы можем задать условие отбора не ввиде простой строки, 
	// а в форме А-массива, ключи которого это имена полей а значения - то чему должны быть равны записи при отборе
	
	$flist = $w_TREF_newspaper->FilterRecordsByExample(array (
		$x_TREF_newspaper->Code => 'EA45',
		FLD_ONLYLESSTHAN . $x_TREF_newspaper->DefaultPrice => 2000,
		NOT_FIELD . $x_TREF_newspaper->NewspaperID => array (101, 102, 103, 104),
	), 
	0, 1, $x_TREF_newspaper->DefaultPrice);
	// также второй и третий параметр функции управляет отбором записей с помощью paging-а,
	// четвертый параметр - задает сортировку
	
	print 'Был сформирован следующий запрос на поиск: ' . $w_TREF_newspaper->GetSQLCommand() . '<hr />';
	
	// теперь удалим записи
	
	$vasyan->DeleteRecord ();
	

	// всякое разное 
	// список полей которые обязательны для заполнения
	print 'Список полей обязательных для заполнения: ';
	print_r($vasyan->GetNotNullFieldNames ());
	print '<hr />';
	// получить тип поля
	print 'Тип поля UserFIO: ' .$vasyan->GetTypeName ($x_TREF_user->UserFIO) . '<hr />';
	// получить текст SQL условия отобора записи
	print 'Уникальный идентификатор записи: ' . $vasyan->CreateIdentitySQL () . '<hr />';
	// получаем имя поля Primary Key
	print 'Имя поля первичного ключа таблицы: ' . $vasyan->GetIDFieldName (). '<hr />';
	
	// получить список записей по массиву их Primary Key ID
	print 'Ищем массив записей по набору значений их ключей: (1,2,3): ';
	print_r($w_TREF_newspaper->GetLinkedRecords (array (10, 11, 103)));
	print '<hr />';
	// проверка существования некоторой таблицы
	if ($w_TREF_newspaper->IsTableExists ('megatable'))
		print 'Таблица megatable существует'. '<hr />';
	else 
		print 'Таблицы megatable нет'. '<hr />';
		
		
	// 	получаем значение переменной mysql
	print 'datadir = ' . $w_TREF_newspaper->GetMysqlVariable('datadir') . '<hr />';
	
	// подсчет количества записей по некоторому условию
	
	print 'всего количество записей газет: ' . $w_TREF_newspaper->CountRecords() . ' а среди них цена которых превосходит 1000 : '
	 . $w_TREF_newspaper->CountRecords(array (FLD_GREATEROREQUALTHAN . $x_TREF_newspaper->DefaultPrice => 1000) ) . '<hr />';
	 
	// можно скопировать все содержимое одной таблицы в другую -- та, вторая таблица должан существовать

	// команда копирования данных в другую таблицу естественно работать не будет: 
	//$w_TREF_newspaper->CopyAllDataToTable ($x_TREF_foo);
	
	$w_TREF_newspaper->DropTable('CopyOf');
	
	// получить максимальное значение для некоторого поля
	print 'Максимальная цена газеты: ' . $w_TREF_newspaper->GetMaxValueForField($x_TREF_newspaper->DefaultPrice) . '<hr />';
	
	// возможно конструировать и выполнять запросы без помощи моей библиотечки
	// если запрос возвращает набор данных то они представляются в виде массива, каждый элемент которого запись
	// запись в свою очередь - это снова А-массив
	print 'результат выполнения прямого sql-запроса: ';
	print_r ($w_TREF_newspaper->ExecuteSQLCommandWithResultSet('select * from ' . $x_TREF_newspaper->table_name));
	print '<hr />';
	// или же если команда не возвращает никаких данных
	if ($w_TREF_newspaper->ExecuteSQLCommandWithoutResultSet('truncate megatable'))
		print 'Выполнена команда очистки несуществующей таблицы <hr />';
	else
		print 'Команда очистки несуществующей таблицы как и ожидалось не была выполнена<hr />';
	// записи можно удалять по значению ID
	if ($w_TREF_user->DeleteRecordByID ($vid))
		print 'Порядок запись была удалена по ID = ' . $vid . '<hr />';
	else
		print 'Увы но запись удалить не удалось по ID = ' . $vid . '; причина: '.$w_TREF_user->GetLastError().'<hr />';
		
	// или же задать условие отбора в виде ассоциативного массива
	$w_TREF_user->DeleteRecordsByExample (array ($x_TREF_user->Sex => 'alien'));
	
?>
</pre>