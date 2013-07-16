<?php

// переменная в которой кодируется признак того какая версия мускула используется 4.0 или выше для учета того что изменился апи для доступа и надо использовать i-расширения


//-- ***************************************************
if (! isset($glob_SiteRoot))
$glob_SiteRoot = realpath(dirname(__FILE__) . '/../') . '/';

include_once($glob_SiteRoot . 'db-support/tbasetable.inc');


define ("TYPE_NUMERIC_FIELD",  1);
define ("TYPE_STRING_FIELD",  2);
define ("TYPE_DATE_FIELD",  3);

define ("UNIQUE_FUNCTION_CALL_PREFIX",  'unq_fcall_001_67_qbnmyt_56_676_7786_qw');
define ("DIRECT_CALL", "direct_call_67_q_67_1_qe_89");
define ("NOT_FIELD", "not_field_expr_6ghj3_i");

define ("FLD_ONLYLESSTHAN", "less_cxvvc5");
define ("FLD_LESSOREQUALTHAN", "lesseq_truytr5");
define ("FLD_ONLYGREATERTHAN", "great_742f");
define ("FLD_GREATEROREQUALTHAN", "greateq_sdf56");

define ("FLD_REGEXP", "regexp_ladd6");
define ("FLD_LIKE", "like_zsdx1");



define ("NAME_IN_CACHE_PROVIDER", "cached:56i67gsa94523:");// константа хрянящая сведения об кэшированных объектах


function  swap (&$a , &$b){
	$tmp = $a;
	$a = $b;
	$b = $tmp;
}

function dump_die ($msg){
	print ('<PRE>');
	print ($msg . "\n");
	print (var_export(debug_backtrace() , true));
	print ('</PRE>');
	die ();
}


function MakeInCacheIdentity  ($object){
	// НО это только для объектов уже восставновленных из базы данных
	if ($object->GetWasStoredInDB()){
		$identity = $object->GetIDValue();
		//$object->SetInMemoryCacheID($identity);
		return $identity;
	}
	$memory_id = $object->GetInMemoryCacheID();
	if ($memory_id){
		return $memory_id;
	}

	// тогда надо взять фиктивный локатор записи такой какого нет среди всего списка создаваемых по требованию записей
	if (!isset ($_SESSION['memory_cache_ids'])){
		$_SESSION['memory_cache_ids'] = array ();
	}

	$id = uniqid('incache:id:', true);
	$id = str_replace('.' , '_' , $id);// при генерации опасно использовать имена зарезервированные с точки зрения http
	$was = $_SESSION['memory_cache_ids'];
	$was [] = $id;
	$_SESSION['memory_cache_ids'] = $was;

	//$object->SetInMemoryCacheID($id);
	return $id;
}


function IsRecordInCache ($value_of_id){
	if (!isset ($_SESSION['memory_cache_ids'])){
		$_SESSION['memory_cache_ids'] = array ();
	}
	return in_array($value_of_id, $_SESSION['memory_cache_ids']);
}


$globus_cached_flinks = null;
$globus_cached_discovered_tables_structs_info = array ();
$GLOBALS['globus_LazyLoadedTables'] = array ();
$GLOBALS['globus_cache_of_filtercachetgenset']  = array ();
$GLOBALS['globus_cache_optimize_hints_records_by_id']  = array ();
$GLOBALS['globus_cache_if_exists_optimize']  = array ();


/**
* данный класс служебный и описывает то какие связи существуют между таблицами
* в схеме многие-ко-многим.
*/
class FLink {
	var  $master_table_name;
	var  $slave_table_name;
	var  $master_field_name;
	var  $slave_field_name;

	function FLink ($mtn , $stn , $mfn , $sfn){
		$this->master_table_name = $mtn;
		$this->slave_table_name = $stn;
		$this->master_field_name = $mfn;
		$this->slave_field_name = $sfn;
	}

	function  GetMasterTableName (){
		return $this->master_table_name;
	}
	function  GetSlaveTableName (){
		return $this->slave_table_name;
	}

	function  GetMasterFieldName (){
		return $this->master_field_name;
	}
	function  GetSlaveFieldName (){
		return $this->slave_field_name;
	}

};

/**
Это класс пустышка служит для удобства работы в среде zend
*/
class FDummyLink extends FLink  {
	function FDummyLink  (){}
}

class TGenericRecord extends TBaseTable{

	var  $sub_mirror_fields = array ();

	var  $is_auto_increment_field_as_primary;

	var  $is_edit_mode;
	var  $is_append_mode;
	var  $is_deleted;

	var  $field_names; // массив с именами полей
	var  $field_types; // массив с типами полей
	var  $field_sizes; // размеры полей
	var  $field_cur_values; // массив со значениями полей  - старыми до модификации
	var  $field_new_values; // массив со значениями полей  - новыми в ходе модификации
	var  $field_not_null_names;// массив с именами полей которые не должны быть NULL

	var  $table_name; // имя таблицы к которой относится данная запись
	var  $id_field_name ; // имя поля являющегося идентификатором записи - первичным ключом
	var  $type_of_id_field;


	var $is_readonly = false;

	function GetReadOnly (){
		return $this->is_readonly;
	}

	function SetReadOnly ($mode){
		$this->is_readonly = $mode;
	}

	var $is_in_memory_cached = false;

	function SetInMemoryCached ($is_in_memory_cached){
		$this->is_in_memory_cached = $is_in_memory_cached;
	}
	function GetInMemoryCached (){
		return $this->is_in_memory_cached;
	}


	var $is_was_stored_in_db = false;

	function SetWasStoredInDB ($is_was_stored_in_db){
		$this->is_was_stored_in_db = $is_was_stored_in_db;
	}
	function  GetWasStoredInDB (){
		return $this->is_was_stored_in_db;
	}


	var $in_memory_cached_id = null;

	function SetInMemoryCacheID ($in_memory_cached_id){
		$this->in_memory_cached_id = $in_memory_cached_id;
	}
	function GetInMemoryCacheID (){
		return $this->in_memory_cached_id;
	}


	var $is_modified = false;

	function GetWasModified (){
		return $this->is_modified;
	}
	function SetWasModified ($is_modified){
		$this->is_modified = $is_modified;
	}


	function HasFieldByName ($field_name){
		for ($i = 0; $i < count ($this->field_names); $i++)
		if (strcmp($this->field_names[$i], $field_name) === 0) return true;
		return false;
	}


	function IsAcceptFilter ($arr_conditions){
		foreach ($arr_conditions as $field => $value){
			$invert = false;
			if (strpos($field,  NOT_FIELD) !== false){
				$invert  = true;
				$field = str_replace(  NOT_FIELD , $field);
			}
			if (is_array($value)){
				$val = $this->GetFieldValue($field);
				$ina = in_array($val , $value);
				if ($ina && (! $invert)) continue;
				if ((!$ina)  && ($invert)) continue;
			}
			else{
				$val = $this->GetFieldValue($field);
				//dexport($val . '=' . $value);
				if ($val == $value && (!$invert)) continue;
				if ($val != $value && ($invert)) continue;
			}
			return false;
		}
		return true;
	}



	function GetFinePrintDebug ($with_field_names = true){
		$rez = '';
		$rez .= ('<table border="1" width="100%">');
		if ($with_field_names){
			$rez .=('<tr>');
			for ($i = 0; $i < count ($this->field_names); $i++)
			$rez .= ('<td>' . $this->field_names[$i] . '</td>');
			$rez .=('</tr>');
		}

		$rez .=('<tr>');
		for ($i = 0; $i < count ($this->field_new_values); $i++){
			$rez .=('<td>' . $this->field_new_values[$i] . '</td>');
		}
		$rez .=('</tr>');
		$rez .=('</table>');
		return $rez;
	}

	function getListOfSubMirrorFields (){
		return $this->sub_mirror_fields;
	}

	function addSubMirrorField ($mirror){
		for ($i = 0; $i < count ($this->sub_mirror_fields) ; $i++)
		if ($mirror == $this->sub_mirror_fields[$i]) return true;

		for ($i = 0; $i < count ($this->field_names) ; $i++)
		if ($mirror == $this->field_names[$i]){
			$this->sub_mirror_fields [] = $mirror;
			return true;
		}
		return false;
	}

	function LoadAndGetSubMirrorField ($mirror_name){
		$sql = 'SELECT ' . $mirror_name . ' FROM '
		. $this->table_name . ' WHERE ' .  $this->CreateIdentitySQL();
		$this->SetSQLCommand($sql);
		$rSet = xxx_query($sql , $this->link_to_database);
		if (! $rSet){
			$this->InitErrors();
			return false;
		}
		$txt = null;
		if (($row = xxx_fetch_assoc($rSet))){
			$txt = $row [$mirror_name];
		}
		xxx_free_result($rSet);
		return $txt;
	}

	function GetFieldSize ($field_indicator_number_of_name){
		if (is_numeric($field_indicator_number_of_name)){
			$tpc = $this->field_sizes[$field_indicator_number_of_name];
			return $tpc;
		}
		for ($i = 0; $i < count ($this->field_names); $i++)
		if ($this->field_names[$i] === $field_indicator_number_of_name){
			$tpc = 	$this->field_sizes[$i];
			return $tpc;
		}
		return null;
	}


	function GetFieldsCount (){
		return count($this->field_names);
	}

	function GetFieldNames (){
		return $this->field_names;
	}


	function GetFieldName($index){
		return $this->field_names[$index];
	}

	function GetIndexOfField ($name){
		for ($i = 0; $i < count ($this->field_names); $i++){
			if ($this->field_names[$i] == $name) return $i;
		}
		return null;
	}


	function GetNotNullFieldNames (){
		return $this->field_not_null_names;
	}

	function IsFieldNotNull ($field_name){
		for ($i = 0; $i < count($this->field_not_null_names); $i++)
		if ($field_name == $this->field_not_null_names[$i])
		return true;
		return false;
	}

	function GetTypeName ($field_name){
		if (is_numeric($field_name)){
			$tpc = $this->field_types[$field_name];
			if ($tpc == TYPE_NUMERIC_FIELD) return 'numeric';
			if ($tpc == TYPE_STRING_FIELD) return 'string';
			if ($tpc == TYPE_DATE_FIELD) return 'date';
			return null;
		}
		for ($i = 0; $i < count ($this->field_names); $i++)
		if ($this->field_names[$i] == $field_name){
			$tpc = 	$this->field_types[$i];
			if ($tpc == TYPE_NUMERIC_FIELD) return 'numeric';
			if ($tpc == TYPE_STRING_FIELD) return 'string';
			if ($tpc == TYPE_DATE_FIELD) return 'date';
		}
		return null;
	}
	// функции для проверки того в каком состоянии находится набор записей и в случае не соответствия запись в журнал ошибки

	function TestIsNotDeleted ($message){
		if ($this->is_deleted){
			SetErrorMessage ("Ошибка не пройден тест на IsNotDeleted Для Класса TGenericRecord: уточняющее сообщение об ошибке: " . $message);
		}
		return ! $this->is_deleted;
	}

	function TestIsNotAppendMode ($message){
		if ($this->is_append_mode){
			$this->SetErrorMessage ("Ошибка не пройден тест на IsNotAppendMode Для Класса TGenericRecord: уточняющее сообщение об ошибке: " . $message);
		}
		return ! $this->is_append_mode;
	}

	var  $smart_mode_enabled = true;

	function InitFromAssoc ($assoc){
		for ($i = 0; $i < count ($this->field_names); $i++)
		if (isset ($assoc [$this->field_names[$i]]))
		$this->field_cur_values [$i]= $assoc [$this->field_names[$i]];
		else{
			if (! isset ($this->field_cur_values [$i])){
				$this->field_cur_values [$i] = null;
			}
		}
		$this->field_new_values = $this->field_cur_values;
		return true;
	}

	function ClearAndInitFromAssoc ($assoc){
		$this->field_cur_values = array ();
		return $this->InitFromAssoc($assoc);
	}


	function  TGenericRecord ($_link_to_database , $table_name, $smart_mode_enabled = true, $resultSet = null){
		$this->smart_mode_enabled = $smart_mode_enabled;
		parent::TBaseTable ($_link_to_database , $table_name, $resultSet);
		$this->is_deleted = false;
		$this->is_edit_mode = false;
		$this->is_append_mode = false;
	}

	function VirtualInitParametersAtSmartMode ($table_name, $_resultSet){
		$this->table_name = $table_name;

		global $globus_cached_discovered_tables_structs_info;
		/*
		if (isset ($globus_cached_discovered_tables_structs_info[$table_name])){
		// информация об структуре уже была получена и повторно ее получать не надо
		$rezid = $globus_cached_discovered_tables_structs_info[$table_name];
		$this->is_auto_increment_field_as_primary = $rezid[];
		}
		*/
		// фишка в том что где то возникает дубляж этого фрагмента кода

		$this->is_auto_increment_field_as_primary = false;
		$resultSet = null;
		$sql = 'N/A';

		if ($_resultSet == null){
			$sql =  "SELECT * FROM {$this->table_name} WHERE 1 <> 1";
			$resultSet = xxx_query($sql, $this->link_to_database);
		}
		else
		$resultSet = $_resultSet;

		if (! $resultSet){
			$this->InitErrors ();
			dump_die("Критическая ошибка, не возможно получить сведения о полях в таблице : {$this->table_name} : $sql : " . $this->GetLastError ());
		}
		$n = xxx_num_fields($resultSet);
		$this->id_field_name = '';
		$this->is_auto_increment_field_as_primary = false;

		for ($i = 0; $i < $n; $i++){
			$cur_field_name = xxx_field_name($resultSet , $i);
			$cur_field_type = xxx_field_type($resultSet , $i);
			$this->field_names [] = $cur_field_name;
			$this->field_sizes [] = xxx_field_len($resultSet , $i);
			if (xxx_is_numeric_field($cur_field_type))
			$tp = TYPE_NUMERIC_FIELD;
			else{
				if (xxx_is_datetime_field ($cur_field_type))
				$tp = TYPE_DATE_FIELD;
				else
				$tp = TYPE_STRING_FIELD;
			}

			$this->field_types [] = $tp;
			$line_ar_flags = xxx_field_flags($resultSet, $i);
			if ( xxx_is_primary_key_field($line_ar_flags)){
				$this->id_field_name = $cur_field_name;
				$this->type_of_id_field = $tp;
				if (xxx_is_autoincrement_field($line_ar_flags))
				$this->is_auto_increment_field_as_primary = true;
			}
			if ( xxx_is_not_null_field($line_ar_flags)){
				$this->field_not_null_names [] = $cur_field_name;
			}
		}

		if ($_resultSet == null){
			xxx_free_result($resultSet);
		}
		if ($this->id_field_name == '')
		dump_die ("Критическая ошибка, для таблицы : '{$table_name}' не обнаружен первичный ключ");
	}


	function VirtualInitParameters ($arr_ini_section, $_resultSet){
		parent::VirtualInitParameters ($arr_ini_section, $_resultSet);
		if ($this->smart_mode_enabled) return;
		$this->VirtualInitParametersAtSmartMode($arr_ini_section, $_resultSet);
	}


	function IsDeleted (){
		return $this->is_deleted;
	}

	function EditRecord (){
		ASSERT_VALID(! $this->GetReadOnly());
		// данная функция служит для перехода в режим редактирования записи
		// запись есть она существующая и здесь создается копия старых значений полей
		// чтобы иметь возможность отката к предыдущему состоянию
		// здесь требуется проверить то что запись валидна т.е. не была удалена а также то что
		// что запись не в режиме добавления.

		if (!$this->TestIsNotDeleted ("Переход в режим редактирования записи для таблицы: " + $this->table_name)) return false;

		if (!$this->TestIsNotAppendMode ("Переход в режим редактирования записи для таблицы: " + $this->table_name)) return false;

		$this->field_new_values = $this->field_cur_values;
		$this->is_edit_mode = true;
		$this->is_append_mode = false;

		return true;
	}

	function AppendRecord (){
		ASSERT_VALID(! $this->GetReadOnly());
		// данная функция служит для перехода в режим создания новой записи записи
		if (!$this->TestIsNotDeleted ("Переход в режим редактирования записи для таблицы: " + $this->table_name)) return false;
		$this->field_new_values = array (); // зануляем значения
		$this->is_edit_mode = false;
		$this->is_append_mode = true;
		$this->SetWasModified(true);
		return true;
	}


	function SetFieldValue ($field_name , $field_value){
		$this->SetWasModified (true);
		for ($i = 0; $i < count ($this->field_names); $i++)
		if ( $this->field_names [$i] == $field_name){
			$this->field_new_values [$i] = $field_value;
			return true;
		}
		trigger_error("SetFieldValue (Field Wasnt Found): Need Field Name: {$field_name} avaliable names = (".var_export($this->field_names , true).")", E_USER_ERROR);
		return false;
	}

	function SetFieldValueOverride ($field_name , $test_value , $new_value){
		$this->SetWasModified (true);
		for ($i = 0; $i < count ($this->field_names); $i++)
		if ( $this->field_names [$i] == $field_name){
			if (isset ($this->field_new_values [$i]) &&  $this->field_new_values [$i] == $test_value)
			$this->field_new_values [$i] = $new_value;
			elseif (( ! isset ($this->field_new_values [$i])) && $test_value === null)
			$this->field_new_values [$i] = $new_value;
			return true;
		}
		trigger_error("SetFieldValueOverride (Field Wasnt Found): Need Field Name: {$field_name} avaliable names = (".var_export($this->field_names , true).")", E_USER_ERROR);
		return false;
	}

	function SetFieldFunction ($field_name , $field_function){
		$this->SetWasModified (true);
		for ($i = 0; $i < count ($this->field_names); $i++)
		if ( $this->field_names [$i] == $field_name){
			$this->field_new_values [$i] = UNIQUE_FUNCTION_CALL_PREFIX . $field_function;
			return true;
		}
		trigger_error("SetFieldFunction (Field Wasnt Found): Need Field Name: {$field_name} avaliable names = (".var_export($this->field_names , true).")", E_USER_ERROR);
		return false;
	}

	function GetFieldValueByArrayOfNames ($arr_field_names){
		for ($i = 0; $i < count ($arr_field_names); $i++){
			$v = $this->GetFieldValue ($arr_field_names [$i]);
			if ($v !== false) return $v;
		}
		trigger_error("GetFieldValueByArrayOfNames (Field Wasnt Found): Need Field Name: {".var_export ($arr_field_names , true) ."} avaliable names = (".var_export($this->field_names , true).")", E_USER_ERROR);
		return false;
	}


	function GetFieldValue ($field_name){
		for ($i = 0; $i < count ($this->field_names); $i++)
		if ( $this->field_names [$i] == $field_name){
			if (isset ($this->field_new_values [$i]))
			return $this->field_new_values [$i];
			else
			return null;
		}
		trigger_error("GetFieldValue (Field Wasnt Found): Need Field Name: {$field_name}", E_USER_ERROR);
		return false;
	}

	function GetFieldType ($field_name){
		for ($i = 0; $i < count ($this->field_names); $i++)
		if ( $this->field_names [$i] == $field_name){
			return $this->field_types[$i];
		}
		trigger_error("GetFieldType (Field Wasnt Found): Need Field Name: {$field_name}", E_USER_ERROR);
		return null;
	}


	// данная функция возвращает строку служащую для идентификации записи в базе
	// в теории здесь реализуется единственное отличие между записями в таблице которые определяются
	// по одному ключу или же по нескольким полям ключам
	function CreateIdentitySQL (){
		$sql = "{$this->id_field_name} = ";
		$id_value = NULL;

		for ($i = 0; $i < count($this->field_names); $i++)
		if ($this->field_names[$i] == $this->id_field_name)
		$id_value = $this->field_cur_values [$i];

		if ($id_value === NULL){
			$this->SetErrorMessage("Общая ошибка формирования условия идентификации записи: DeleteRecord / TGenericRecord т.к. не обнаружено в таблице ключевого поля или он равен NULL что является еще большей глупостью");
			return false;
		}
		if ($this->type_of_id_field == TYPE_NUMERIC_FIELD )
		$sql .= $id_value;
		else
		$sql .= "'"  . xxx_real_escape_string($id_value) . "'";
		return $sql;
	}

	function DeleteRecord (){
		if (!$this->TestIsNotDeleted ("Выполнение команды DeleteRecord - удаление записи таблицы: " . $this->table_name)) return false;
		if (($this->is_edit_mode) || ($this->is_append_mode)){
			$this->SetErrorMessage ("Выполнение команды DeleteRecord для таблицы {$this->table_name} не возможно набор данных находится в режиме редактирования или добавления новой записи");
			return false;
		}


		if ($this->GetInMemoryCached()){
			if (!isset ($_SESSION['memory_cache_ids'])){
				$_SESSION['memory_cache_ids'] = array ();
			}
			$a_tmp = $_SESSION['memory_cache_ids'];
			$cid = $this->GetInMemoryCacheID();
			for($i = 0; $i < count($a_tmp); $i++){
				if ($a_tmp[$i] == $cid){
					unset ($_SESSION[$i]);
					break;
				}
			}
			$tname = $this->GetTableName();

			foreach ($_SESSION as $key => $object){
				if (strpos($key, NAME_IN_CACHE_PROVIDER) !== 0)continue;
				$ar_got = array ();

				if (!preg_match('/{{(.*)}}_{{(.*)}}/U' , $key , $ar_got)) continue;
				if (count($ar_got) != 3) continue;
				if ($ar_got [1] == $tname && $ar_got[2] == $cid){
					unset ($_SESSION [$key]);
				}
			}
		}		// конец удаления объекта из кэша памяти


		if ($this->GetWasStoredInDB()){

			$wh = $this->CreateIdentitySQL();
			if ($wh === false) return false;
			$sql = "DELETE FROM {$this->table_name} WHERE " . $wh;

			$this->SetSQLCommand($sql);
			if (!xxx_query($sql , $this->link_to_database)){
				$this->InitErrors();
				return false;
			}
		}
		return true;
	}
	// данная функция служит для получения номера записи являющейся первичным ключом и автосчетчиком-полем
	function GetIndexOfIdAutoIncField (){
		for ($i = 0; $i < count($this->field_names); $i++)
		if ($this->field_names [$i] == $this->id_field_name)
		return $i;
		return -1;
	}

	function UpdateRecord (){

		ASSERT_VALID(! $this->GetReadOnly());

		if (!$this->TestIsNotDeleted ("Выполнение команды UPDATE - отправка измерений записи таблицы: " . $this->table_name)) return false;
		if ((!$this->is_edit_mode) && (! $this->is_append_mode)){
			$this->SetErrorMessage ("Выполнение команды UPDATE для таблицы {$this->table_name} не возможно набор данных не находится в режиме редактирования или добавления новой записи");
			return false;
		}

		if ($this->GetInMemoryCached())
		{

			$id_for_me = MakeInCacheIdentity ($this);// тут очень опасное место теоретически ID - Это ID в базе, НО
			$this->SetInMemoryCacheID($id_for_me);
			//dexport($id_for_me );

			$id_f = $this->GetIndexOfIdAutoIncField ();
			$this->field_new_values [$id_f]  = $id_for_me;

			$this->field_cur_values = $this->field_new_values;
			$this->is_edit_mode = false;
			$this->is_append_mode = false;

			$_SESSION [NAME_IN_CACHE_PROVIDER . '_{{' . $this->GetTableName () . '}}_{{' . $id_for_me  . '}}'] = $this;
			return true;
		}// сохранние в кэш памяти


		$sql = '';
		$auto_inc_was_set = false;

		$idxxval = $this->GetIDValue();

		if ($idxxval)
			$auto_inc_was_set = $this->GetWasStoredInDB();


		for ($i = 0; $i < count($this->field_names); $i++){
			if ($this->id_field_name == $this->field_names[$i] && $this->is_auto_increment_field_as_primary){
				continue;
			}
			/*
			if (
			$this->is_edit_mode &&
			isset ($this->field_new_values [$i]) &&
			isset ($this->field_cur_values [$i]) &&
			($this->field_new_values [$i] == $this->field_cur_values [$i])
			) continue;

			$count_modified++;
			*/
			$fl = "`" . $this->field_names [$i] . "` = ";
			// сначала проверка того что значение поля -- это значение а не вызов функции
			if (isset ($this->field_new_values [$i]) && $this->field_new_values [$i]!==null && strpos($this->field_new_values [$i] , UNIQUE_FUNCTION_CALL_PREFIX) !== false){
				$fl .= str_replace(UNIQUE_FUNCTION_CALL_PREFIX , '' , $this->field_new_values [$i]);
			}
			else{
				if ($this->field_types [$i] == TYPE_NUMERIC_FIELD)
				$fl .= ((isset ($this->field_new_values [$i]) && $this->field_new_values [$i]!==null
				&& $this->field_new_values [$i]!=="" && $this->field_new_values [$i]!==false) ? $this->field_new_values [$i]:' NULL ');
				else{
					if (isset($this->field_new_values [$i]) && $this->field_new_values [$i] !== null &&
					$this->field_new_values [$i] !== false
					)
					$fl .= "'" .  xxx_real_escape_string($this->field_new_values [$i] , $this->link_to_database) . "' ";
					else
					$fl .= " NULL ";
				}
			}
			if ($i != count ($this->field_names) - 1)
			$fl .= ', ';
			$sql .= $fl;
		}

		$sql = trim ($sql);
		if ($sql [strlen($sql) - 1] == ',')
		$sql = substr($sql , 0 , strlen($sql) -1);

		if ($auto_inc_was_set){
			//if ($count_modified == 0) return true; // операция обновления не требуется все поля без изменения
			$wh = $this->CreateIdentitySQL();
			if ($wh === false) return false;

			$sql .= " WHERE " .$wh;
			// данный фрагмент несколько не логичен дело в том, что если ключем является INT IDENTITY или AUTO_INCREMENT
			// поле то его невозможно модифицировать и следует брать из массива старых значений.
		}


		if ($auto_inc_was_set)
		$sql = "UPDATE $this->table_name SET " . $sql;
		else
		$sql = "INSERT INTO $this->table_name SET " . $sql;



		$this->SetSQLCommand($sql);
		$sql = trim ($sql);
		if ($sql [strlen($sql) - 1] == ',')
		$sql = substr($sql , 0 , strlen($sql) -1);


		// а теперь фигня объект может быть и не сохранен в бд
		if (!xxx_query($sql , $this->link_to_database)){
			$this->InitErrors ();
			return false;
		}
		$this->SetWasStoredInDB(true);



		if ((! $auto_inc_was_set) && $this->is_auto_increment_field_as_primary){
			$id_f = $this->GetIndexOfIdAutoIncField ();
			$xidentity =  xxx_insert_id($this->link_to_database);
			$this->field_new_values [$id_f]  = $xidentity;

			// теперь надо обновить Cache

			//фишка в том что надо будет сейчас
			//обновнить кэш записи так чтобы она указывала на новый идентфиикатор
			if (!isset ($_SESSION['memory_cache_ids'])){
				$_SESSION['memory_cache_ids'] = array ();
			}
			$was = $_SESSION['memory_cache_ids'];

			$tname = $this->GetTableName();
			$cachedid = $this->GetInMemoryCacheID();

			if (in_array($cachedid , $was)){

				foreach ($_SESSION as $key => $object){
					if (strpos($key, NAME_IN_CACHE_PROVIDER) !== 0)continue;
					$ar_got = array ();

					if (!preg_match('/{{(.*)}}_{{(.*)}}/U' , $key , $ar_got)) continue;
					if (count($ar_got) != 3) continue;

					if ($ar_got [1] == $tname && $ar_got[2] == $cachedid){
						unset ($_SESSION[$key]);

						$this->field_cur_values = $this->field_new_values;
						$this->is_edit_mode = false;
						$this->is_append_mode = false;
						$this->SetWasModified (false);

						$_SESSION ['{{'.$tname.'}}_{{'.$xidentity.'}}'] = $this;
						// упрощенная ветка когда объект был предварительно создан в кэше а затем его сделали постоянным
						return true;
					}
				}//for each
			}
		}//if
		$this->field_cur_values = $this->field_new_values;
		$this->is_edit_mode = false;
		$this->is_append_mode = false;

		$this->SetWasModified (false);

		return true;
	}

	function CancelChanges (){
		if (!$this->TestIsNotDeleted ("Выполнение команды CancelChanges - отмена измерений записи таблицы: " . $this->table_name)) return false;
		$this->is_edit_mode = false;
		$this->is_append_mode = false;
		$this->field_new_values = $this->field_cur_values;
		$this->SetWasModified (false);
		return true;
	}

	function LinkRecordToID ($id, $can_select_from_cache = false){
		if ($id === null) return  false;
		$what = ' * ';
		if (count ($this->sub_mirror_fields) > 0){
			$what = '';
			for ($i= 0 ; $i < count ($this->field_names); $i++){
				$must_include = true;
				for ($j = 0; $j < count ($this->sub_mirror_fields); $j++)
				if ($this->field_names [$i] == $this->sub_mirror_fields[$j]){
					$must_include = false;
					break;
				}
				if ($must_include)
				$what .=  ($this->field_names[$i] . ', ');
			}
			$what = substr($what , 0 , strlen ($what) - 2);
		}
		$sql = "SELECT {$what} FROM {$this->table_name} WHERE {$this->id_field_name} = ";
		$wh = $this->GetIDValueForSQL($id);
		$sql .= $wh;
		$this->SetSQLCommand($sql);

		$row = false;

		//***********************************
		if ($can_select_from_cache){
			// можно ли и есть ли объект с тем же id  в кэше
			if (! isset($GLOBALS['globus_cache_optimize_hints_records_by_id'][$this->table_name])){
				$GLOBALS['globus_cache_optimize_hints_records_by_id'][$this->table_name] = array ();
			}
			if (! isset($GLOBALS['globus_cache_optimize_hints_records_by_id'][$this->table_name][$id]))
			{

				//-----------------------------------------------------------
				if (!($resultSet = xxx_query($sql, $this->link_to_database))){
					$this->SetErrorMessage("Ошибка, не возможно присоединиться к записи с идентификатором: {$id} для таблицы {$this->table_name} " .  xxx_error($this->link_to_database));
					return false;
				}

				if (! ($row = xxx_fetch_assoc($resultSet)) ){
					xxx_free_result($resultSet);
					$this->SetErrorMessage("Ошибка, не возможно найти запись с идентификатором: {$id} для таблицы {$this->table_name} " .  xxx_error($this->link_to_database));
					return false;
				}
				xxx_free_result($resultSet);
				//------------------------------------------------------------
				$GLOBALS['globus_cache_optimize_hints_records_by_id'][$this->table_name][$id] = 			$row;
			}// if not found id
			$row = $GLOBALS['globus_cache_optimize_hints_records_by_id'][$this->table_name][$id];
		}
		//***********************************
		if (! $row){
			if (!($resultSet = xxx_query($sql, $this->link_to_database))){
				$this->SetErrorMessage("Ошибка, не возможно присоединиться к записи с идентификатором: {$id} для таблицы {$this->table_name} " .  xxx_error($this->link_to_database));
				return false;
			}

			if (! ($row = xxx_fetch_assoc($resultSet)) ){
				xxx_free_result($resultSet);
				$this->SetErrorMessage("Ошибка, не возможно найти запись с идентификатором: {$id} для таблицы {$this->table_name} " .  xxx_error($this->link_to_database));
				return false;
			}
			xxx_free_result($resultSet);
		}
		$this->field_new_values = array ();
		$this->field_cur_values = array ();
		for ($i = 0; $i < count($this->field_names); $i++){
			if (! isset ($row [$this->field_names[$i]])){
				$this->field_cur_values [] = null;
				$this->field_new_values [] = null;
			}
			else{
				$this->field_cur_values [] = $row [$this->field_names[$i]];
				$this->field_new_values [] = $row [$this->field_names[$i]];
			}
		}
		return true;
	}

	function GetTableName (){
		return $this->table_name;
	}

	function  GetIDFieldName (){
		return $this->id_field_name;
	}

	function GetAssocArray (){
		$h = array ();
		for ($i = 0; $i < count ($this->field_names); $i++)
		$h [$this->field_names [$i]] = isset ($this->field_cur_values [$i])?$this->field_cur_values [$i]:null;
		return  $h;
	}

	function GetSimpleArrayValues (){
		return  $this->field_cur_values;
	}

	function GetIDValue (){
		for ($i = 0; $i < count($this->field_names); $i++)
		if ($this->id_field_name == $this->field_names[$i])
		return $this->field_cur_values [$i];
		return null;
	}

	function GetIDValueForSQL ($idv = null){
		if ($idv === null){
			if ($this->type_of_id_field == TYPE_NUMERIC_FIELD)
			$sql = $idv;
			else
			$sql = "'"  . xxx_real_escape_string( $idv ) . "'";
			return $sql;
		}
		else{
			if ($this->type_of_id_field == TYPE_NUMERIC_FIELD)
			$sql = $idv;
			else
			$sql = "'"  . $idv . "'";
			return $sql;
		}// ситуация когда явно указано имя поля

	}


	//*********************************************************************************
	// ********* механизмы связывания записей друг с другом *******************
	//*********************************************************************************


	var  $flink_objects = array ();
	function SetFLinkObjects ($flinks){
		global $globus_cached_flinks;
		if (! $globus_cached_flinks)
		$globus_cached_flinks = $flinks;
	}

	function GetFLinkObjects (){
		global $globus_cached_flinks;
		return $globus_cached_flinks;
	}

	function TestIsFlinkExists($table_name_3d, $table_name_2d ,$message){
		$_1d =  $this->GetTableName();
		$flink_objects =   & $this->GetFLinkObjects();

		if ($table_name_3d == '' || $table_name_3d === null || $table_name_3d === false){
			// ситуация когда связь между таблицами может быть установлена напрямую
			for ($i = 0; $i < count ($flink_objects); $i++){
				$f = new FDummyLink();
				$f = $flink_objects[$i];

				$_fmaster = $f->GetMasterTableName();
				$_fslave = $f->GetSlaveTableName();
				if (
				(strcasecmp($_fmaster , $_1d) == 0)
				&&
				(strcasecmp($_fslave , $table_name_2d)==0)
				)
				return $f;
			}
			$this->SetErrorMessage ("Ошибка не пройден тест на TestIsFlinkExists Для Класса TGenericRecord: уточняющее сообщение об ошибке: " . $message);
			return null;
		}
		// усложняем ситуацию - теперь связь идет через промежуточную таблицу ---
		for ($i = 0; $i < count ($flink_objects); $i++)
		for ($j = 0; $j < count ($flink_objects); $j++)
		if ($i != $j)
		{
			$fi = new FDummyLink();
			$fi = $flink_objects[$i];
			$fj = new FDummyLink();
			$fj = $flink_objects[$j];

			if (
			(strcasecmp($fi->GetMasterTableName(), $_1d) == 0)
			&&
			(strcasecmp($fj->GetMasterTableName() , $table_name_2d) == 0)
			&&
			(strcasecmp($fi->GetSlaveTableName() , $table_name_3d) == 0)
			&&
			(strcasecmp($fj->GetSlaveTableName() , $table_name_3d) == 0)
			){
				$far = array ();
				$far [] = $fi;
				$far [] = $fj;
				return $far;
			}
		}
		$this->SetErrorMessage ("Ошибка не пройден тест на TestIsFlinkExists Для Класса TGenericRecord (связь через промежуточную таблицу {$table_name_3d}): уточняющее сообщение об ошибке: " . $message);
		return null;

	}

	function GetMasterAttachmentEntityByEntitySet ($another_entity_set){
		return $this->GetMasterAttachmentEntityByEntity($another_entity_set->GetFreeRecord());
	}
	/**
	* данный метод служит для противоположного обращения - когда имея
	* подчиненную запись мы пытаемся найти одну главную запись - ссылка на 
	* справочник передается как параметр
	*/
	function GetMasterAttachmentEntityByEntity ($another_entity){
		$fob =new  FDummyLink ();
		$tname = $another_entity->GetTableName ();
		$iname = $this->GetTableName ();
		//var_dump($another_entity_set);

		$fob = $another_entity->TestIsFlinkExists(null , $iname , "Получение списка приложений для таблицы : " . $tname);
		if ($fob === null) return null;
		if (is_array($fob)) return null;

		// связь без промежуточных таблиц
		$mfn = $fob->GetMasterFieldName ();
		//$mtn = $fob->GetMasterTableName ();

		$sfn = $fob->GetSlaveFieldName ();
		//$stn = $fob->GetSlaveTableName ();


		$where = "{$mfn} = " . $this->GetValueByNameForSQL($this->GetFieldValue($sfn), $sfn);
		$brecsset = new TGenericEntitySet ($this->link_to_database,$tname);
		$rz = $brecsset->FilterRecords($where);
		if ($rz === null) return null;
		if (count ($rz)!=1)return null;
		return $rz [0];
	}



	function GetAttachmentsByEntitySet ($eset, $table_3d_name){
		return $this->GetAttachmentsByTableName ($eset->GetTableName (), $table_3d_name);
		/**
	* @return unknown
	* @param unknown $value
	* @param unknown $field_name
	* @desc данный метод кодирует значение переданное как первый параметр в зависимости 
	от его типа данных согласно имени поля - заданного как  второй параметр
	*/
	}

	function GetValueByNameForSQL ($value , $field_name){
		if ($value === null) return ' NULL ';
		$tup = $this->GetFieldType($field_name);
		if ($tup == TYPE_NUMERIC_FIELD)
		$sql = $value;
		else
		$sql = "'"  . xxx_real_escape_string( $value , $this->link_to_database) . "'";
		return $sql;
	}

	/**
	* данный метод позволяет получить список записей в промежуточной таблице
	*/
	function GetAttachmentsByEntityObject ($eobject, $table_3d_name){
		$tname = $eobject->GetTableName ();
		$fob = new FDummyLink ();
		$fob = $this->TestIsFlinkExists($table_3d_name , $tname , "Получение списка приложений для таблицы : " . $tname);
		if ($fob === null) return null;
		if (! is_array($fob)) return null;
		// ситуация когда связь идете через промежуточную таблицу
		$fi = new FDummyLink ();
		$fj = new  FDummyLink ();
		$fi = $fob [0];
		$fj = $fob [1];

		$sql = "SELECT * FROM {$table_3d_name} WHERE " . $fi->GetMasterFieldName () . ' = ' .
		$this->GetIDValueForSQL() . ' AND ' . $fj->GetSlaveFieldName () . ' = ' . $eobject->GetIDValueForSQL();
		$this->SetSQLCommand($sql);

		if (! ($resultSet = xxx_query($sql , $this->link_to_database))){
			$this->InitErrors();
			return null;
		}

		$rbs = new TGenericEntitySet($this->link_to_database , $table_3d_name);

		$where = '';
		//$numba  = xxx_num_rows($resultSet);
		$cur_row = 0;
		while (($row = xxx_fetch_assoc($resultSet))){
			$nami = $fi->GetSlaveFieldName();
			$namj = $fj->GetSlaveFieldName();
			$sid = $row [$nami];
			$sjd = $row [$namj];

			$where .= '(' . $nami . ' = ' . $rbs->GetValueByNameForSQL ($sid, $nami);
			$where .= ' AND ';
			$where .=  $namj . ' = ' . $rbs->GetValueByNameForSQL ($sjd, $namj);
			//$where .= ' ) OR ';

			$cur_row++;
		}
		//$where .= ' 1 = 2';
		xxx_free_result($resultSet);
		return $rbs->FilterRecords($where);
	}


	function GetAttachmentsByTableName ($tname, $table_3d_name){
		$fob =new  FDummyLink ();
		$fob = $this->TestIsFlinkExists($table_3d_name , $tname , "Получение списка приложений для таблицы : " . $tname);
		if ($fob === null) return null;
		if (! is_array($fob)){
			// связь без промежуточных таблиц
			$mfn = $fob->GetMasterFieldName ();
			$where = "{$mfn} = " . $this->GetIDValueForSQL();
			$brecsset = new TGenericEntitySet ($this->link_to_database,$tname);
			return $brecsset->FilterRecords($where);
		}
		// ситуация когда связь идете через промежуточную таблицу
		$fi = new FDummyLink ();
		$fj = new FDummyLink ();
		$fi = $fob [0];
		$fj = $fob [1];

		$sql = "SELECT * FROM {$table_3d_name} WHERE " . $fi->GetSlaveFieldName () . ' = ' .
		$this->GetIDValueForSQL();
		$this->SetSQLCommand($sql);

		if (! ($resultSet = xxx_query($sql , $this->link_to_database))){
			$this->InitErrors();
			return null;
		}

		$rbs = new TGenericEntitySet($this->link_to_database , $tname);
		$rz = array ();
		while (($row = xxx_fetch_assoc($resultSet))){
			$idmaster_field_at_2d_table  = $fj->GetMasterFieldName();
			$idmaster_field_value_at_2d_table  = $row[$idmaster_field_at_2d_table];
			$nova_3drecord = $rbs->GetLinkedRecord($idmaster_field_value_at_2d_table);
			$rz  [] = $nova_3drecord;
		}
		xxx_free_result($resultSet);
		return $rz;
	}

	function SmartAttach ($att, $_3d){
		if ($this->Attach  ($att , $_3d))return true;
		return $att->Attach ($this , $_3d);
	}

	function SmartDeAttach ($att, $_3d){
		if ($this->DeAttach  ($att , $_3d))return true;
		return $att->DeAttach ($this , $_3d);
	}

	function Attach ($att, $_3d){
		$f = new FDummyLink();
		$_1d = $this->GetTableName();
		$_2d = $att->GetTableName();

		$f = $this->TestIsFlinkExists($_3d , $att->GetTableName () , "Операция присоединения записи к таблице {$_1d} записи из таблицы: {$_2d} посредством: {$_3d}");
		if ($f === null) {
			$this->SetErrorMessage("не возможно выполнить присоединение записи, возможно в настройках не указаны правила");
			return false;
		}

		if (!is_array($f)){// режим связи напрямую без посредников
			$_2d_m = $f->GetSlaveFieldName();
			//$_2dprimary = $att->GetIDFieldName ();
			$sql = "UPDATE {$_2d} SET {$_2d_m} = ";

			$sql .= $this->GetIDValueForSQL();

			$sql .= ' WHERE ' . $att->CreateIdentitySQL();

			$this->SetSQLCommand($sql);

			if (xxx_query($sql , $this->link_to_database)){
				return true;
			}
			else{// значит произошел сбой наиболее вероятно что это из-за того что
				// был дубляж записи поэтому проверим наличие в таблице уже существующей записи
				// с такими же полями как и добавляемое
				$this->InitErrors();

				$sql = "SELECT * FROM {$_2d} WHERE {$_2d_m} = ";
				$sql .= $this->GetIDValueForSQL();

				$this->SetSQLCommand($sql);

				if (($resultSet = xxx_query($sql , $this->link_to_database))){
					if (xxx_num_rows($resultSet) > 0)
					xxx_free_result($resultSet);
					return true;
				}
				else
				return false;
			}//else если не удалось выполнить обновление записи в подчиненной - присоединяемой таблице

		}// ситуация когда присоединенение идет напрямую

		// теперь если связь идет через таблицу посредника
		$fi = new FDummyLink();
		$fj = new FDummyLink();
		$fi = $f [0];
		$fj = $f [1];

		$sql = "INSERT INTO {$_3d} SET " . $fi->GetSlaveFieldName () . ' = ' .
		$this->GetIDValueForSQL() . ' , ' . $fj->GetSlaveFieldName () . ' = ' . $att->GetIDValueForSQL();

		$this->SetSQLCommand($sql);

		if (($resultSet = xxx_query($sql , $this->link_to_database)))
		return true;

		// значит произошел сбой наиболее вероятно что это из-за того что
		// был дубляж записи поэтому проверим наличие в таблице уже существующей записи
		// с такими же полями как и добавляемое
		$this->InitErrors();

		$sql = "SELECT * FROM {$_3d} WHERE " .
		$fi->GetSlaveFieldName () . ' = ' .
		$this->GetIDValueForSQL() . ' AND ' . $fj->GetSlaveFieldName ()
		. ' = ' . $att->GetIDValueForSQL();


		$this->SetSQLCommand($sql);

		if (($resultSet = xxx_query($sql , $this->link_to_database))){
			if (xxx_num_rows($resultSet) > 0)
			xxx_free_result($resultSet);
			return true;
		}
		else
		return false;
		//else если не удалось выполнить обновление записи в подчиненной - присоединяемой таблице

	}// конец функции Attach



	function DeAttach ($att, $_3d){
		$f = new FDummyLink();
		$_1d = $this->GetTableName();
		$_2d = $att->GetTableName();

		$f = $this->TestIsFlinkExists($_3d , $att->GetTableName () , "Операция отсоединения записи от таблице {$_1d} записи из таблицы: {$_2d} посредством: {$_3d}");
		if ($f === null) dump_die ("не возможно выполнить отсоединение записи, возможно в настройках не указаны правила");

		if (!is_array($f)){// режим связи напрямую без посредников
			$_2d_m = $f->GetSlaveFieldName();
			//$_2dprimary = $att->GetIDFieldName ();
			$sql = "UPDATE {$_2d} SET {$_2d_m} = NULL ";
			$sql .= ' WHERE ' . $att->CreateIdentitySQL();

			$this->SetSQLCommand($sql);

			if (xxx_query($sql , $this->link_to_database))
			return true;
			else
			$this->InitErrors();

			return false;
		}// ситуация когда присоединенение идет напрямую

		// теперь если связь идет через таблицу посредника
		$fi = new FDummyLink();
		$fj = new FDummyLink();
		$fi = $f [0];
		$fj = $f [1];

		$sql = "DELETE FROM {$_3d} WHERE " . $fi->GetSlaveFieldName () . ' = ' .
		$this->GetIDValueForSQL() . ' AND ' . $fj->GetSlaveFieldName () . ' = ' . $att->GetIDValueForSQL();

		$this->SetSQLCommand($sql);

		if (xxx_query($sql , $this->link_to_database))
		return true;
		else
		$this->InitErrors();
		return false;

	}// конец функции DeAttach

}//end of class <<TGenericRecord>>

// данный класс не несет никакой смысловой нагрузки и служит только для удобства вызова методов
// класса предка в средах типа Zend Studio с подсказками кода.
class TDummyRecord extends TGenericRecord {
	function TDummyRecord (){}
}

class TGenericEntitySet extends TBaseTable {
	var  $table_name;
	var  $dummy_record_clone;
	var  $is_multikeys;


	var $is_lazyload_loaded = false;


	function prepareLazyLoadMetaData  ($_link_to_database , $_table_name){
		$this->is_lazyload_loaded = false;

		$this->table_name = $_table_name;
		$this->link_to_database = $_link_to_database;
	}

	function LazyLoadMetaData(){
		ASSERT_VALID($this->is_lazyload_loaded === false , 'Повторный вызов инициализации объектов TGenericEntitySet::LazyLoadMetaData не возможен');

		$this->is_lazyload_loaded = true;
		$GLOBALS['globus_LazyLoadedTables'] [] = $this->table_name;

		$this->is_multikeys = false;


		$sql =  "SELECT * FROM {$this->table_name} WHERE 1 <> 1";
		$resultSet = xxx_query($sql , $this->link_to_database);
		if (! $resultSet){
			dump_die ("Критическая ошибка, не возможно получить сведения о полях в таблице : {$this->table_name} : $sql");
		}
		$n = xxx_num_fields($resultSet);
		$this->id_field_name = array ();

		for ($i = 0; $i < $n; $i++){
			$line_ar_flags = xxx_field_flags($resultSet, $i);

			if (xxx_is_primary_key_field($line_ar_flags)){
				$this->id_field_name [] = xxx_field_name($resultSet , $i);
			}
		}
		if (count($this->id_field_name) == 0)
		dump_die ("Критическая ошибка, для таблицы : '{$this->table_name}' не обнаружен первичный ключ");

		$this->is_multikeys =  count($this->id_field_name) > 1;

		if (! $this->is_multikeys)
		$this->dummy_record_clone = new TGenericRecord($this->link_to_database , $this->table_name, false , $resultSet);
		else
		$this->dummy_record_clone = new TAdvancedRecord($this->link_to_database , $this->table_name, false, $resultSet);


		// fix for php4
		global $glob_PHP_JNDI_Lookup_Cache;
		if (isset($glob_PHP_JNDI_Lookup_Cache [$this->table_name])){
			$tmp = $glob_PHP_JNDI_Lookup_Cache [$this->table_name];
			if ($tmp->is_lazyload_loaded === false){
				$tmp->is_lazyload_loaded = true;
				$tmp->is_multikeys  =  $this->is_multikeys;
				$tmp->id_field_name = $this->id_field_name;
				$tmp->dummy_record_clone = $this->dummy_record_clone;
				$glob_PHP_JNDI_Lookup_Cache [$this->table_name] = $tmp;
				unset ($tmp);
			}
		}

	}//foo LazyLoad ----

	function TGenericEntitySet ($_link_to_database , $table_name){
		$this->prepareLazyLoadMetaData ($_link_to_database , $table_name);

		if (0){
			$this->LazyLoadMetaData($_link_to_database, $table_name);


			// ************ получение списка связей между таблицами **************
			//____CRITICAL__
			if (0 && (! $this->dummy_record_clone->GetFlinkObjects ())){

				$rset = xxx_query('SHOW TABLES', $this->link_to_database);
				$arf = array ();
				while (($row_tables = xxx_fetch_array($rset,  XXX_NUM))){

					$slave_table_name = $row_tables [0];
					if ((!$rset_sql = xxx_query ("SHOW CREATE TABLE $slave_table_name" , $this->link_to_database))) dump_die (xxx_error($this->link_to_database));
					$row = xxx_fetch_array($rset_sql);

					$sql = $row [1];

					$lines = explode ("\n"   , $sql);


					for ($idx = 0; $idx < count($lines); $idx++){
						$l = $lines[$idx];
						if (strpos($l , 'FOREIGN KEY') !== false){
							$p = strpos($l , 'FOREIGN KEY');
							$l = substr($l , $p + (strlen('FOREIGN KEY')));
							$l = trim ($l);
							$p = strpos($l , ' ');

							$slave_field =  trim (substr($l , 0 , $p) , "('`\"[{}]`') ");

							$p = strpos($l , 'REFERENCES');
							$l = trim(substr($l , $p + (strlen('REFERENCES'))));

							$p = strpos($l , ' ');
							$master_table = trim(substr($l , 0 ,  $p), "('`\"[{}]`') ");
							$l = substr($l , $p + 1);
							$p = strpos($l , ')');
							$master_field= trim(substr($l , 0 ,  $p), "('`\"[{}]`') ");

							$fl = new FLink ($master_table , $slave_table_name , $master_field , $slave_field);
							$arf [] = $fl;

						}
					}//foreach (lines) - разбор инструкции SQL - CREATE
				}// цикл по таблицам в базе данных

				$this->dummy_record_clone->SetFLinkObjects ($arf);
			}// отключение режима исследования связей между таблицами
			//____CRITICAL__
		}//if Lazy Load 0 - zero handler
	}//foo



	function HasFieldByName ($field_name){
		if (! $this->is_lazyload_loaded)	$this->LazyLoadMetaData ();//load meta data
		return $this->dummy_record_clone->HasFieldByName ($field_name);
	}

	function GetTableName (){
		if (! $this->is_lazyload_loaded)	$this->LazyLoadMetaData ();//load meta data
		return $this->dummy_record_clone->GetTableName ();
	}

	function GetFieldType ($field_name){
		if (! $this->is_lazyload_loaded)	$this->LazyLoadMetaData ();//load meta data
		return $this->dummy_record_clone->GetFieldType($field_name);
	}

	function GetIDFieldName (){
		if (! $this->is_lazyload_loaded)	$this->LazyLoadMetaData ();//load meta data
		return $this->dummy_record_clone->GetIDFieldName();
	}
	/**
	* @return void
	* @desc  данный метод возвращает свободную запись т.е. псевдо-запись которая не
	* привязана ни к одной физической записи
	*/
	function GetFreeRecord ($is_in_memory_cached = false){
		if (! $this->is_lazyload_loaded)	$this->LazyLoadMetaData ();//load meta data

		if (IS_PHP_5 ())
		eval ('$zu = clone $this->dummy_record_clone;');
		else
		$zu = $this->dummy_record_clone;

		// теперь финт ушами возможно созданных объект не будет обладать способностью к сохранению собственного состояния в постоянно хранилище
		// а выполнять кэширование себя в сессии например
		$zu->SetInMemoryCached ($is_in_memory_cached);
		$zu->SetWasStoredInDB(false);
		return $zu;
	}

	//
	/**
	* @return void
	* @param unknown $id - все записи в общем случае отличаются благодаря первичным ключам
	* @desc получение записи но уже не свободной а реально существующей в базе - физической записи
	*/
	function GetLinkedRecord ($id, $is_in_memory_cached = false){
		if (! $this->is_lazyload_loaded)	$this->LazyLoadMetaData ();//load meta data

		if (IS_PHP_5 ())
		eval ('$zu = clone $this->dummy_record_clone;');
		else
		$zu = $this->dummy_record_clone;


		$zu->SetInMemoryCached ($is_in_memory_cached);
		$zu->SetWasStoredInDB(true);

		if ($zu->LinkRecordToID ($id, $this->GetOptimizeHints_EnableFilterCache())){
			MakeInCacheIdentity($zu);
			return $zu;
		}
		else
		return null;
	}


	function GetLinkedRecords ($array_of_ids , $sort_mode = null){
		if ($sort_mode != null){
			$sql_in = '';
			$dum = $this->GetMetaInfo ();
			$type = $dum->GetFieldType($dum->id_field_name);
			for ($i = 0 ; $i < count ($array_of_ids); $i++){
				if ($type == TYPE_NUMERIC_FIELD)
				$sql_in .= $array_of_ids [$i] . ' ';
				else
				$sql_in .= EncodeString2 ($array_of_ids [$i]);
				if ($i != (count($array_of_ids) - 1))
				$sql_in .= ', ';
			}
			$idf = $dum->id_field_name;


			return $this->FilterRecords("{$idf} IN ( $sql_in )" , $sort_mode);
		}
		else{
			//ручная имитация цикла отбора данных по их номерами именно номера и важны для сортировки
			$rez  = array ();
			for($j = 0; $j < count($array_of_ids); $j++){
				$x = $this->GetLinkedRecord( $array_of_ids [$j] );
				if ($x)
					$rez [] = $x;
			}
			return $rez;
		}
	}


	/// проверка существования определенной таблицы по имени
	function IsTableExists ($table_name){
		$row = $this->ExecuteSQLCommandWithResultSet('show tables like \''. $table_name .'\'');
		if ($row && count ($row) > 0) return true;
		return false;
	}

	function GetMysqlVariable ($var_name){
		$rez = $this->ExecuteSQLCommandWithResultSet("SHOW VARIABLES LIKE '$var_name'");
		ASSERT_VALID ($rez);
		if (count ($rez) == 1){
			$rez = $rez [0];
			return $rez ['Value'];
		}
		else{
			return false;
		}
	}

	function ConvertDateTime_Mysql_2_PHP ($value_as_string, $format = 'yyyy-mm-dd hh:mm:ss'){
		if ($value_as_string == '') return time();
		$format = $this->GetMysqlVariable('datetime_format');
		if ($format === false)
		$format = '%Y-%m-%d %H:%i:%s';

		$format = str_replace('%' , '' , $format);
		if ($format != '')
		return strtotime($value_as_string);
		else{
			// если формат родной мускула не известен то предположим что это //year-month-day hh:mm:ss
			if ($format == 'yyyy-mm-dd hh:mm:ss'){
				$arr_got_dt = array ();
				$arr_got_tm = array ();
				$is_date = preg_match('/(\d+)\.(\d+)\.(\d+)/',$str_datetime ,$arr_got_dt);
				$is_time = preg_match('/(\d+):(\d+):(\d+)/', $str_datetime,$arr_got_tm);
				ASSERT_VALID($is_date);
				$rez = $arr_got_dt[3]	 . '.' . $arr_got_dt[2] . '.' . $arr_got_dt[1];

				if ($is_time){
					return mktime($arr_got_tm[1] , $arr_got_tm[2] , $arr_got_tm[3] ,  $arr_got_dt[2] , $arr_got_dt[3] , $arr_got_dt[1] );
				}
				return mktime(0 ,0 ,0 ,  $arr_got_dt[2] , $arr_got_dt[3] , $arr_got_dt[1] );
			}
			return  strtotime($value_as_string);
		}
	}

	function ConvertDateTime_PHP_2_Mysql ($timestamp_int){
		$format = $this->GetMysqlVariable('datetime_format');
		//die ($format);
		$format = str_replace('%i' , '%I' , $format);
		$format = str_replace('%s' , '%S' , $format);
		return strftime($format , $timestamp_int);
	}

	function ConvertDateTime_PHPString_2_Mysql($str_datetime, $format = 'russian'){
		// предполагается что формат в виде
		//year-month-day
		if ($format == 'russian'){
			$arr_got_dt = array ();
			$arr_got_tm = array ();
			$is_date = preg_match('/(\d+)\.(\d+)\.(\d+)/',$str_datetime ,$arr_got_dt);
			$is_time = preg_match('/(\d+):(\d+):(\d+)/', $str_datetime,$arr_got_tm);
			ASSERT_VALID($is_date);
			$rez = $arr_got_dt[3]	 . '.' . $arr_got_dt[2] . '.' . $arr_got_dt[1];
			if ($is_time){
				$rez .= ' ' . $arr_got_tm[1]	 . ':' . $arr_got_tm[2] . ':' . $arr_got_tm[3];
			}
			return $rez;
		}
		return $str_datetime;
	}

	function GetAllRecordsIds (){
		$id_name = $this->dummy_record_clone->GetIDFieldName();
		$tabn = $this->dummy_record_clone->GetTableName();
		$sql = "SELECT {$id_name} FROM {$tabn}";

		$this->SetSQLCommand($sql);
		$resultSet = xxx_query($sql);
		if (!$resultSet){
			$this->InitErrors();
			return null;
		}
		$ar_ids = array ();
		while (($row = xxx_fetch_assoc($resultSet))){
			if (!$this->is_multikeys)
			$ar_ids [] =  $row [$id_name];
			else{
				$a = array ();
				for ($j = 0; $j < count ($id_name); $j++)
				$a [$id_name[$j]] =  $row [$id_name[$j]];
				$ar_ids [] =  $a;
			}
		}
		xxx_free_result($resultSet);
		return $ar_ids;
	}


	function GetMetaInfo (){
		if (! $this->is_lazyload_loaded)	$this->LazyLoadMetaData ();//load meta data
		$zu = null;
		if (IS_PHP_5 ()){
			eval ('$zu = clone $this->dummy_record_clone;');
		}
		else{
			$zu = $this->dummy_record_clone;
		}
		return $zu;
	}


	function CountRecords ( $arr_of_expression = array () ){
		$sql_cond = $this->CreateSQLExpressionForFilterArray ($arr_of_expression);
		if ($sql_cond != ''){
			$sql_cond  = ' WHERE ' . $sql_cond;
		}
		$cnta = $this->ExecuteSQLCommandWithResultSet('SELECT COUNT(*) AS cnt_it FROM ' . $this->GetTableName()  . $sql_cond);
		$cnta = $cnta [0];
		return $cnta ['cnt_it'];
	}

	function CountRecordsInTable ( $arr_of_expression  , $another_table_name ){

		$recset = null;
		if ( isset ($GLOBALS['glob_PHP_JNDI_Lookup_Cache'][$another_table_name]))
		$recset = $GLOBALS['glob_PHP_JNDI_Lookup_Cache'][$another_table_name];
		else{
			$recset = new TGenericEntitySet(RESOURCE_MYSQL_ANGLE, $another_table_name);
			$GLOBALS['glob_PHP_JNDI_Lookup_Cache'][$another_table_name] = $recset;
		}

		return $recset->CountRecords($arr_of_expression);
	}

	function GetFieldNames (){
		$ftmp = $this->dummy_record_clone;
		return $ftmp->field_names;
	}


	function CopyAllDataToTable ($recset_obj){
		$to_table_name = $recset_obj->GetTableName ();
		$from_table_name = $this->GetTableName ();
		$sql = 'insert into ' . $to_table_name . ' ('.join(', ', $recset_obj->GetFieldNames()).') select ' .
		join(', ', $this->GetFieldNames ()) . ' from '  . $from_table_name;
		return $this->ExecuteSQLCommandWithoutResultSet($sql);
	}


	function DropTable ($table_name){
		$sql = 'DROP TABLE IF EXISTS ' . $table_name;
		$this->SetSQLCommand($sql);
		$recSet = xxx_query($sql, $this->link_to_database);
		if (! $recSet){
			$this->InitErrors();
			return false;
		}
		return true;
	}



    function exportDataAsSimpleArray ($arrayofvalues){
    	$dummy = $this->GetMetaInfo ();
    	
    
    	$rez = array ();
    	$rez ['metadata'] = array ();
    	$rez ['data'] = array ();
    	$rez ['metadata']['fields'] = $dummy->GetFieldNames();
    	$rez ['metadata']['notnullfields'] = $dummy->GetNotNullFieldNames();
    	
    	for ($i = 0; $i < count($arrayofvalues); $i++){
    		$dum = $arrayofvalues [$i];
    		$rez ['data'] [] = $dum->GetAssocArray ();
    	}
    	return $rez;
    }


	function GetMaxValueForField ($field_name){
		$arr_max = $this->ExecuteSQLCommandWithResultSet ("SELECT MAX({$field_name}) as maxi89_b_56 FROM {$this->table_name}");
		if (! $arr_max) return false;
		return $arr_max [0]['maxi89_b_56'];
	}

	function ExecuteSQLCommandWithoutResultSet ($sql){
		$this->SetSQLCommand($sql);
		$recSet = xxx_query($sql, $this->link_to_database);
		if (! $recSet){
			$this->InitErrors();
			return false;
		}
		return true;
	}


	function ExecuteSQLCommandWithResultSet ($sql){
		$this->SetSQLCommand($sql);
		$recSet = xxx_query($sql, $this->link_to_database);
		if (! $recSet){
			$this->InitErrors();
			return null;
		}
		$ar = array ();
		while (($row = xxx_fetch_assoc($recSet))){
			$ar [] = $row;
		}
		xxx_free_result($recSet);
		return $ar;
	}

	/**
	эта функция служит для сжатия повторяющихся объектов записей в масссиве критерий сжатия -- первичный ключ
	*/
	function CompressDistinct ($arr_of_records){
		$arr_of_unique_ids = array ();
		$arr_of_unique_records = array ();
		for ($i = 0; $i < count ($arr_of_records); $i++){
			$dum = new TDummyRecord();
			$dum = $arr_of_records [$i];
			$idv = $dum->GetIDValue();
			//print ("\n" . '~~~~~~~~' . $idv . ' ~~~~~~~~~~~~~~ ' . "\n");
			if (! in_array($idv , $arr_of_unique_ids , true)){
				$arr_of_unique_ids [] = $idv;
				$arr_of_unique_records [] = $dum;
			}
		}
		return $arr_of_unique_records;
	}

	function GetFinePrintDebug ($arr_of_records){
		$rez = '';
		$meta = new TDummyRecord();
		$meta = $this->GetMetaInfo();
		$rez .=('<h2>' . $this->GetTableName() . '</h2>');
		$rez .=('<table border="1">');
		for ($i = 0; $i < $meta->GetFieldsCount (); $i++){
			$tp = 'STRING';
			$idtype = $this->GetFieldType($meta->GetFieldName($i));
			if ($idtype == TYPE_NUMERIC_FIELD)
			$tp = 'NUMERIC';
			if ($idtype == TYPE_DATE_FIELD)
			$tp = 'DATE';
			$rez .=('<td>' .  $meta->GetFieldName($i) .' <=> ' . $tp . '</td>');
		}

		for ($i = 0; $i < count ($arr_of_records); $i++){
			$dum = new TDummyRecord();
			$dum = $arr_of_records [$i];
			$rez .= '<tr>';
			$rez .= '<td colspan="' . $meta->GetFieldsCount ()  . '">';
			$rez .= $dum->GetFinePrintDebug (false);
			$rez .= '</td>';
			$rez .= '</tr>';
		}

		$rez .=('</table>');
		return $rez;
	}

	function CreateSQLExpressionForFilterArray ($assoc_values){
		if (! $this->is_lazyload_loaded)	$this->LazyLoadMetaData ();//load meta data

		global $glob_mysql_version_api;


		$dum2 = new TDummyRecord();
		$dum2 = $this->dummy_record_clone;

		$rezz = '';
		if ($assoc_values == null) return '';
		if ($assoc_values == false) return '';

		foreach ($assoc_values as $k => $v){
			if ($k == '@and_expression' || $k == '@or_expression') continue;
			if ($v === null){
				$rezz .= $k . ' IS NULL '  .  ' AND ';
			}
			else{
				$k_cleared = str_replace(NOT_FIELD , '' , $k);

				$k_cleared = str_replace(FLD_ONLYLESSTHAN , '' , $k_cleared);
				$k_cleared = str_replace(FLD_LESSOREQUALTHAN , '' , $k_cleared);
				$k_cleared = str_replace(FLD_ONLYGREATERTHAN , '' , $k_cleared);
				$k_cleared = str_replace(FLD_GREATEROREQUALTHAN , '' , $k_cleared);
				$k_cleared = str_replace(FLD_REGEXP , '' , $k_cleared);
				$k_cleared = str_replace(FLD_LIKE , '' , $k_cleared);



				if (is_array($v)){

					$sql_in = '';
					$type = $this->GetFieldType($k_cleared);

					if ($glob_mysql_version_api == 41){
						for ($i = 0 ; $i < count ($v); $i++){
							if ($type == TYPE_NUMERIC_FIELD)
							$sql_in .= $v [$i] . ' ';
							else
							$sql_in .= EncodeString2 ($v [$i]);
							if ($i != (count($v) - 1))
							$sql_in .= ', ';
						}
						if (strpos($k,  NOT_FIELD) === false){
							if ($sql_in != '')
							$rezz .= $k .' IN (' . $sql_in .  ') AND ';
							else
							{
								$rezz .= ' (1 = 2) AND ';
							}
						}
						else{
							$k = $k_cleared;
							if ($sql_in != '')
							$rezz .= $k .' NOT IN (' . $sql_in .  ') AND ';
							else
							$rezz .= ' (1 = 1) AND ';
						}
					}
					if ($glob_mysql_version_api == 40){

						$k_cleared = $k;
						if (strpos($k,  NOT_FIELD) !== false)
						$k_cleared = str_replace(NOT_FIELD , '' , $k);

						for ($i = 0 ; $i < count ($v); $i++){
							if ($type == TYPE_NUMERIC_FIELD)
							$sql_in .= ' ( ' . $k_cleared . ' = ' . $v [$i] . ' ) ';
							else
							$sql_in .= ' ( ' . $k_cleared . ' = ' . EncodeString2 ($v [$i]) . ' ) ';
							if ($i != (count($v) - 1))
							$sql_in .= ' OR ';
						}

						if (strpos($k,  NOT_FIELD) === false){
							if ($sql_in != '')
							$rezz .= ' (' . $sql_in . ')  AND ';
							else
							{
								$rezz .= ' (1 = 2) AND ';
							}
						}
						else{
							if ($sql_in != '')
							$rezz .= '( NOT (' . $sql_in . ') )  AND ';
							else
							{
								$rezz .= ' (1 = 2) AND ';
							}
						}

					}
				}//if argument is not array --> scalar value
				else{
					$v2 = $dum2->GetValueByNameForSQL($v , $k_cleared);
					$cmd_operation = ' = ';
					if (strpos($k,  FLD_ONLYLESSTHAN) !== false)
					$cmd_operation = ' < ';
					elseif (strpos($k,  FLD_LESSOREQUALTHAN) !== false)
					$cmd_operation = ' < ';
					elseif (strpos($k,  FLD_ONLYGREATERTHAN) !== false)
					$cmd_operation = ' > ';
					elseif (strpos($k,  FLD_GREATEROREQUALTHAN) !== false)
					$cmd_operation = ' >= ';
					elseif (strpos($k,  FLD_REGEXP) !== false)
					$cmd_operation = ' regexp ';
					elseif (strpos($k,  FLD_LIKE) !== false)
					$cmd_operation = ' like ';



					if (strpos($k,  NOT_FIELD) === false)
					$rezz .= $k_cleared . $cmd_operation . $v2 .  ' AND ';
					else{
						$rezz .= ' NOT('.$k_cleared . $cmd_operation . $v2. ')' .  ' AND ';
					}

				}
			}
		}
		if ($rezz != '')
		$rezz =substr($rezz , 0 , strlen($rezz) - 5);

		if (isset ($assoc_values['@and_expression']))
		{
			if ($rezz == '')
			$rezz = $assoc_values['@and_expression'];
			else
			$rezz .= ' AND ' . $assoc_values['@and_expression'];
		}

		if (isset ($assoc_values['@or_expression']))
		{
			if ($rezz == '')
			$rezz = $assoc_values['@or_expression'];
			else
			$rezz .= ' OR ' . $assoc_values['@or_expression'];
		}
		return $rezz;
	}


	function GetSortExpressionsForFieldsAsArrayKeepOrder ($arr_pairs_field_name_to_array_value){
		$arr_sorts = array();
		foreach ($arr_pairs_field_name_to_array_value as $k => $v){
			$candi = $this->GetSortExpressionForFieldAsArrayKeepOrder($k , $v);
			if ($candi != '')
			$arr_sorts [] = $candi;
		}
		return join (', ' , $arr_sorts);
	}

	function GetSortExpressionForFieldAsArrayKeepOrder ($field_name , $array_of_ids){
		$sort_expression = '';
		if (count ($array_of_ids)){
			$sort_expression = join (', ' , $array_of_ids);
			$sort_expression = ' FIELD( ' . $field_name . ', ' .  $sort_expression . ' ) ';
		}
		return $sort_expression;
	}

	function DeleteRecordByID ($idvalue){
		$tname = $this->GetTableName();
		$sql_c = 'DELETE FROM ' . $tname . ' WHERE ' . $this->GetIDFieldName() . ' = ' . $this->GetIDValueForSQL($idvalue);
		return $this->ExecuteSQLCommandWithoutResultSet($sql_c);
	}

	function DeleteRecordsByExample ($assoc_values){
		if ($assoc_values){
			$sql_c = $this->CreateSQLExpressionForFilterArray ($assoc_values);
			return $this->ExecuteSQLCommandWithoutResultSet('DELETE FROM ' . $this->GetTableName() . ' WHERE ' . $sql_c);
		}
		else{
			return $this->ExecuteSQLCommandWithoutResultSet('TRUNCATE ' . $this->GetTableName() );
		}
	}


	function IfHasRecordsByExample ($assoc_values){


		if ($this->GetOptimizeHints_EnableFilterCache()){
			ksort($assoc_values);
			$md  = md5(var_export($assoc_values, true));
			if (! isset($GLOBALS['globus_cache_if_exists_optimize'][$this->table_name]))				{
				$GLOBALS['globus_cache_if_exists_optimize'][$this->table_name] = array ();
			}//if first time at table
			if (! isset($GLOBALS['globus_cache_if_exists_optimize'][$this->table_name][$md]))			{

				$sql_c = $this->CreateSQLExpressionForFilterArray ($assoc_values);
				$wx = $this->ExecuteSQLCommandWithResultSet('SELECT 1 FROM ' . $this->GetTableName() . ' WHERE ' . $sql_c);
				;

				$GLOBALS['globus_cache_if_exists_optimize'][$this->table_name][$md] = count ($wx) != 0;
			}//if specific record
			return $GLOBALS['globus_cache_if_exists_optimize'][$this->table_name][$md];
		}

		$sql_c = $this->CreateSQLExpressionForFilterArray ($assoc_values);
		$wx = $this->ExecuteSQLCommandWithResultSet('SELECT 1 FROM ' . $this->GetTableName() . ' WHERE ' . $sql_c);
		return count ($wx) != 0;
	}


	// ********  MEMORY CACHE ********************
	function FilterRecordsByExampleFromCache ($arr_conditions, $page_start = false, $page_size = false, $sort_expression = false){

		// получаем список всех записей в кэше указанного типа -- моего типа
		//$_SESSION [NAME_IN_CACHE_PROVIDER . '_{{' . $this->GetTableName () . '}}_{{' . $id_for_me  . '}}'] = $this;
		$tname = $this->GetTableName();
		$rez = array ();
		foreach ($_SESSION as $key => $object){
			if (strpos($key, NAME_IN_CACHE_PROVIDER) !== 0)continue;
			$ar_got = array ();
			//dexport($key);
			if (!preg_match('/{{(.*)}}_{{(.*)}}/U' , $key , $ar_got)) continue;
			if (count($ar_got) != 3) continue;
			if ($ar_got [1] == $tname){
				//dexport($object);
				//dexport($arr_conditions);
				if ($object->IsAcceptFilter ($arr_conditions))
				$rez [] = $object;
			}
		}


		// сортировку выполнять до того как будут создано постраничное разбиение
		if ($sort_expression !=  false){



			$arr_fields  = explode(',' , $sort_expression);
			$arr_names  = array ();
			for ($i = 0; $i < count ($arr_fields  ); $i++)
			if (trim ($arr_fields[$i]) != '')
			{
				$candi = trim ($arr_fields[$i]);
				$pos_sep_asc = strpos($candi , ' asc');
				if ($pos_sep_asc  === false)
				$pos_sep_asc = strpos($candi , ' ASC');

				$pos_sep_desc = strpos($candi , ' desc');
				if ($pos_sep_desc  === false)
				$pos_sep_desc = strpos($candi , ' DESC');


				if ($pos_sep_asc === false && $pos_sep_desc === false)
				$arr_names [] = array ('field' => $candi, 'sort' => 'up');
				elseif ($pos_sep_asc !== false)
				$arr_names [] = array ('field' => substr($candi , 0 , $pos_sep_asc), 'sort' => 'up');
				else
				$arr_names [] = array ('field' => substr($candi , 0 , $pos_sep_desc), 'sort' => 'down');
			}

			//dexport($arr_names);

			if (count ($arr_names)){


				$_SESSION ['smpt_89_f_90_cmpt:sys:algo_sort'] = $arr_names;
				if (! function_exists('cmp_90_56_gh_jk_io_98_po_45_as_f')){
					function cmp_90_56_gh_jk_io_98_po_45_as_f ($a , $b){
						$arr_names = $_SESSION ['smpt_89_f_90_cmpt:sys:algo_sort'];
						$result = 0;
						for ($i = 0; $i < count ($arr_names); $i++){
							$criteria = $arr_names [$i];
							$sort = $criteria['sort'];
							$a_val = $a->GetFieldValue ($criteria['field']);
							$b_val = $b->GetFieldValue ($criteria['field']);
							if ($sort == 'up' ){
								if ($a_val > $b_val)
								$result = 1;
								if ($a_val < $b_val)
								$result = -1;
							}
							else{
								if ($a_val > $b_val)
								$result = -1;
								if ($a_val < $b_val)
								$result = 1;
							}
							if ($result !== 0)
							break;

						}//for
						return $result;
					}//function
				}//if ! function exists

				usort($rez , 'cmp_90_56_gh_jk_io_98_po_45_as_f');
				//dexport($rez);
			}//if SORT Expression Contains This Fields
		}//IF sort

		$rez2 = array ();
		if (
		$page_start !== false && $page_size !== false &&
		$page_start !== null && $page_size !== null &&
		$page_start !== null && $page_size !== false &&
		$page_start !== false && $page_size !== null &&
		$page_size != 0
		){
			//dexport($page_start);

			$to_go = min (count ($rez) , $page_size + $page_start);
			for ($i = $page_start; $i < $to_go; $i++)
			$rez2 [] = $rez [$i];
			return $rez2;
		}
		else{
			return $rez;
		}
	}

	function GetLinkedRecordFromCache ($cachedid){
		// получаем список всех записей в кэше указанного типа -- моего типа
		//$_SESSION [NAME_IN_CACHE_PROVIDER . '_{{' . $this->GetTableName () . '}}_{{' . $id_for_me  . '}}'] = $this;
		$tname = $this->GetTableName();

		foreach ($_SESSION as $key => $object){
			if (strpos($key, NAME_IN_CACHE_PROVIDER) !== 0)continue;
			$ar_got = array ();

			if (!preg_match('/{{(.*)}}_{{(.*)}}/U' , $key , $ar_got)) continue;
			if (count($ar_got) != 3) continue;


			if ($ar_got [1] == $tname && $ar_got[2] == $cachedid){
				return $object;
			}
			//if ($ar_got [1] == $tname){
			//die('f' . $cachedid . '=           '. var_export($ar_got, true));
			//}
		}
		return null;
	}

	// ********  MEMORY CACHE ********************


	function FilterRecordsByExample ($assoc_values, $page_start = -1, $page_size = -1, $sort_mode = null){
		$rezz = $this->CreateSQLExpressionForFilterArray ($assoc_values);

		if ($this->GetDebuggerEnabled()){
			dexport($rezz);
		}

		return $this->FilterRecordsInRange($rezz , $page_start , $page_size, $sort_mode);
	}

	function FilterRecords ($condition = null, $sort_mode = null){
		if (! $this->is_lazyload_loaded)	$this->LazyLoadMetaData ();//load meta data

		$tam = $this->dummy_record_clone->GetTableName ();
		$sql = '';
		if ($condition == "" || $condition === null || $condition === false)
		$sql = "SELECT SQL_CALC_FOUND_ROWS * FROM {$tam}";
		else{
			if (strpos($condition , DIRECT_CALL) === false)
			$sql = "SELECT SQL_CALC_FOUND_ROWS * FROM {$tam} WHERE $condition";
			else {
				$condition = str_replace(DIRECT_CALL , '' , $condition);
				$sql = $condition;
			}
		}

		if ($sort_mode != "" && $sort_mode !== false && $sort_mode !== null)
		$sql .= ' ORDER BY ' . $sort_mode;

		$this->SetSQLCommand($sql);



		//************ optimize ****************
		if ($this->GetOptimizeHints_EnableFilterCache()){
			$result_of  = $this->GetFromFilterCacheQueryResult($sql);
			if ($result_of !== false) return $result_of;
		}
		//**********************************


		$recSet = xxx_query($sql, $this->link_to_database);
		if (! $recSet){
			$this->InitErrors();
			return null;
		}
		$ar = array ();
		while (($row = xxx_fetch_assoc($recSet))){
			$a = $this->GetFreeRecord();
			$a->InitFromAssoc($row);
			$a->SetWasStoredInDB(true);
			$ar [] = $a;
		}
		xxx_free_result($recSet);

		if ($this->GetOptimizeHint_LastQueryCountRows())
		$this->PreCacheCountOfLastQueryRows ();
		else
		$this->precachedRowsCount = false;

		//********** optimize hints *************
		if ($this->GetOptimizeHints_EnableFilterCache()){
			$this->SetToFilterCacheQueryResult ($sql, $ar);
		}
		//********** optimize hints *************


		return $ar;
	}



	function FilterRecordsInRange ($condition = null, $start_position = 0 , $count_on_page = 10, $sort_mode = null){


		if (! $this->is_lazyload_loaded)	$this->LazyLoadMetaData ();//load meta data


		$tam = $this->dummy_record_clone->GetTableName ();
		$sql = '';
		if ($condition == "" || $condition === null || $condition === false)
		$sql = "SELECT SQL_CALC_FOUND_ROWS * FROM {$tam}";
		else{
			if (strpos($condition , DIRECT_CALL) === false)
			$sql = "SELECT SQL_CALC_FOUND_ROWS * FROM {$tam} WHERE $condition";
			else{
				$condition = str_replace(DIRECT_CALL , '' , $condition);
				$sql = $condition;
			}
		}

		if ($sort_mode != "" && $sort_mode !== false && $sort_mode !== null)
		$sql .= ' ORDER BY ' . $sort_mode;

		if ($start_position >= 0 && $count_on_page > 0)
		$sql .= ' LIMIT ' .  $start_position . ', ' . $count_on_page;


		if ($this->GetDebuggerEnabled());//dexport ($sql);
		$this->SetSQLCommand($sql);

		//************ optimize ****************
		if ($this->GetOptimizeHints_EnableFilterCache()){
			$result_of  = $this->GetFromFilterCacheQueryResult($sql);
			if ($result_of !== false){
				//dexport($result_of);
				return $result_of;
			}
		}
		//**********************************


		$recSet = xxx_query($sql, $this->link_to_database);

		if (! $recSet){
			$this->InitErrors();
			return null;
		}
		$ar = array ();
		while (($row = xxx_fetch_assoc($recSet))){
			$a = $this->GetFreeRecord();
			$a->InitFromAssoc($row);
			$a->SetWasStoredInDB(true);
			$ar [] = $a;
		}
		xxx_free_result($recSet);

		if ($this->GetOptimizeHint_LastQueryCountRows())
		$this->PreCacheCountOfLastQueryRows ();
		else
		$this->precachedRowsCount = false;

		//********** optimize hints *************
		if ($this->GetOptimizeHints_EnableFilterCache()){
			$this->SetToFilterCacheQueryResult ($sql, $ar);
		}
		//********** optimize hints *************

		return $ar;
	}

	// возвращает  инструкцию CREATE для таблицы
	function GetCreateSQLCommand  (){
		$c_sql = 'SHOW CREATE TABLE ' . $this->GetTableName();
		if ((!$rset_sql = xxx_query ($c_sql, $this->link_to_database))) dump_die (xxx_error($this->link_to_database));
		$row = xxx_fetch_array($rset_sql , MYSQL_NUM);
		$sql = $row [1];
		xxx_free_result($rset_sql);
		return $sql;
	}

	var  $precachedRowsCount = -1;
	function PreCacheCountOfLastQueryRows (){
		$sql = 'SELECT FOUND_ROWS() as cntprc';
		$recSet = xxx_query($sql, $this->link_to_database);
		if (! $recSet){
			$this->InitErrors();
			return false;
		}
		$ar = array ();
		while (($row = xxx_fetch_assoc($recSet))){
			$ar [] = $row;
		}
		xxx_free_result($recSet);
		$this->precachedRowsCount =  $ar[0]['cntprc'];
		return $this->precachedRowsCount;
	}

	function GetCountOfLastQueryRows (){
		return $this->precachedRowsCount;
	}


	function UpdateFieldValuesByExample ($filter , $updates){
		$is_ok = true;
		$rows = $this->FilterRecordsByExample($filter);
		$dum_t = new TDummyRecord();
		for ($i = 0; $i < count($rows); $i++){
			$dum_t  = $rows[$i];
			$dum_t->EditRecord();
			foreach ($updates as $key => $val){
				$dum_t->SetFieldValue ($key , $val);
			}
			$is_ok  = $is_ok  && $dum_t->UpdateRecord();
		}
		return $is_ok ;
	}

	function GetListOfOneFieldValues ($field_name , $as_distinct = true, $sql_where = null){
		$sql = "SELECT ";
		if ($as_distinct)
		$sql .= 'DISTINCT ';
		$sql .=  $field_name;
		$sql .= ' FROM ' . $this->GetTableName();
		if ($sql_where != null)
		$sql  .= ' WHERE '  . $sql_where;

		if (($resultSet = xxx_query($sql , $this->dummy_record_clone->link_to_database))){
			$arr = array ();
			if (xxx_num_fields($resultSet) == 1){
				while (($row = xxx_fetch_row($resultSet))){
					$arr [] = $row [0];
				}
				xxx_free_result($resultSet);
				return $arr;
			}
			else{
				$arr = array ();
				while (($row = xxx_fetch_assoc($resultSet))){
					$arr [] = $row;
				}
				xxx_free_result($resultSet);
				return $arr;
			}
		}
		else
		return false;
	}


	function  GetCountOfRecords (){
		$sql = 'SELECT COUNT(*) FROM ' .  $this->GetTableName();
		$this->SetSQLCommand ($sql);
		if (! $resultSet = xxx_query($sql)){
			$this->InitErrors ();
			return false;
		}
		$row = xxx_fetch_array($resultSet);
		xxx_free_result($resultSet);
		return $row [0];
	}

	function GetIDValueForSQL ($idv){
		return $this->dummy_record_clone->GetIDValueForSQL ($idv);
	}

	var $optimizer_if_enabled_filtercache = false;
	function SetOptimizeHints_EnableFilterCache ($if_enabled){
		$this->optimizer_if_enabled_filtercache = $if_enabled;
		global $glob_PHP_JNDI_Lookup_Cache;
		$tmp = $glob_PHP_JNDI_Lookup_Cache [$this->table_name];
		// warning may be recursive
		$tmp->optimizer_if_enabled_filtercache  = $if_enabled;
		$GLOBALS['globus_cache_of_filtercachetgenset'] = array ();
	}
	function GetOptimizeHints_EnableFilterCache (){
		return $this->optimizer_if_enabled_filtercache;
	}
	function GetFromFilterCacheQueryResult ($sql){
		$md = md5($sql);
		if (isset($GLOBALS['globus_cache_of_filtercachetgenset'][$md]))
		return $GLOBALS['globus_cache_of_filtercachetgenset'][$md];
		return false;
	}
	function SetToFilterCacheQueryResult ($sql, $result){
		$md = md5($sql);
		$GLOBALS['globus_cache_of_filtercachetgenset'][$md] = $result;
	}



	function SortFields ($nova_names){
		// если какие то из имен полей не указаны то выполняется перемещение не указанных полей в конец массива имен
		$nova_position  = 0;
		foreach ($nova_names as $idx => $item){
			$idx =  $idx;
			$index = $this->dummy_record_clone->GetIndexOfField ($item);
			if ($index === null) dump_die ("не возможно выполнить сортировку, не известно поле:  " . var_export($item, true));

			swap ($this->dummy_record_clone->field_names[$index] , $this->dummy_record_clone->field_names[$nova_position] );
			swap ($this->dummy_record_clone->field_types[$index] , $this->dummy_record_clone->field_types[$nova_position] );
			swap ($this->dummy_record_clone->field_sizes[$index] , $this->dummy_record_clone->field_sizes[$nova_position] );
			swap ($this->dummy_record_clone->field_cur_values[$index] , $this->dummy_record_clone->field_cur_values[$nova_position] );
			swap ($this->dummy_record_clone->field_new_values[$index] , $this->dummy_record_clone->field_new_values[$nova_position] );

			$nova_position++;
		}
	}

	function GetValueByNameForSQL ($value , $field_name){
		return $this->dummy_record_clone->GetValueByNameForSQL ($value , $field_name);
	}


	function Lookup ($first_field_name , $value_of_field , $second_field_name){
		$freez = $this->FilterRecords($first_field_name . ' = ' . $value_of_field);
		if ($freez === null || count ($freez)===0) return null;
		return $freez [0]->GetFieldValue ($second_field_name);
	}
};


class TDummyEntitySet extends TGenericEntitySet{
	function TDummyEntitySet (){}
}

//****************************************************************************
//****************************************************************************
// ****** Запись которая идентифицируется по нескольким полям --- ключ состоит из нескольких полей *****
//****************************************************************************
//****************************************************************************


class TAdvancedRecord extends TGenericRecord {

	function TAdvancedRecord ($_link_to_database , $table_name, $smart_mode_enabled = true ){
		$this->smart_mode_enabled = $smart_mode_enabled;
		parent::TBaseTable ($_link_to_database , $table_name);
		$this->is_deleted = false;
		$this->is_edit_mode = false;
		$this->is_append_mode = false;
	}

	function VirtualInitParametersAtSmartMode ($table_name, $_resultSet){

		//parent::VirtualInitParametersAtSmartMode ($table_name);

		$this->is_auto_increment_field_as_primary = false;
		$this->table_name = $table_name;

		$resultSet = null;
		$sql = 'N/A';

		if ($_resultSet == null){
			$sql =  "SELECT * FROM {$this->table_name} WHERE 1 <> 1";
			$resultSet = xxx_query($sql, $this->link_to_database);
		}
		else
		$resultSet = $_resultSet;


		if (! $resultSet){
			dump_die ("Критическая ошибка, не возможно получить сведения о полях в таблице : {$this->table_name} : $sql");
		}
		$n = xxx_num_fields($resultSet);
		$this->id_field_name = array ();
		$this->type_of_id_field = array ();
		$this->is_auto_increment_field_as_primary = false;

		for ($i = 0; $i < $n; $i++){
			$line_ar_flags = xxx_field_flags($resultSet, $i);

			if (xxx_is_primary_key_field($line_ar_flags)){


				$cur_field_type = xxx_field_type ($resultSet , $i);
				// "int", "real", "string", "blob",
				if (xxx_is_numeric_field($cur_field_type))
				$tp = TYPE_NUMERIC_FIELD;
				else{
					if (xxx_is_datetime_field($cur_field_type))
					$tp = TYPE_DATE_FIELD;
					else
					$tp = TYPE_STRING_FIELD;
				}

				$this->id_field_name [] = xxx_field_name($resultSet , $i);
				$this->type_of_id_field []= $tp;
				if (xxx_is_autoincrement_field($line_ar_flags ))
				$this->is_auto_increment_field_as_primary = true;
			}

		}
		if ($_resultSet == null){
			xxx_free_result($resultSet);
		}

		if ($this->id_field_name == '')
		dump_die ("Критическая ошибка, для таблицы : {$this->table_name} не обнаружен первичный ключ");
	}



	// данная функция возвращает строку служащую для идентификации записи в базе
	// в теории здесь реализуется единственное отличие между записями в таблице которые определяются
	// по одному ключу или же по нескольким полям ключам
	function CreateIdentitySQL (){
		$sql = '';
		for ($i = 0; $i < count ($this->id_field_name); $i++){
			$nam = $this->id_field_name[$i];
			$sql .= "$nam = ";
			$id_value = NULL;


			$id_value = $this->GetFieldValue($nam);

			if ($id_value === NULL){
				$this->SetErrorMessage("Общая ошибка формирования условия идентификации записи: DeleteRecord / TGenericRecord т.к. не обнаружено в таблице ключевого поля или он равен NULL что является еще большей глупостью");
				return false;
			}
			if ($this->type_of_id_field[$i] == TYPE_NUMERIC_FIELD )
			$sql .= $id_value;
			else
			$sql .= "'"  . xxx_real_escape_string($id_value) . "'";
			if ($i != (count ($this->id_field_name)-1))
			$sql .= ' AND ';
		}
		return $sql;
	}


	function GetIDFieldName (){// особой необходимости в этом нет, метод аналогичен методу предка
		return $this->id_field_name;
	}


	function GetFieldValue ($field_name){// особой необходимости в этом нет, метод аналогичен методу предка
		for ($i = 0; $i < count ($this->field_names); $i++)
		if ($this->field_names[$i] == $field_name)
		return  $this->field_cur_values [$i];
		return null;
	}

	function GetIDValue (){// особой необходимости в этом нет, метод аналогичен методу предка
		$arr_of_ids = array ();
		for ($i = 0; $i < count ($this->id_field_name); $i++)
		$arr_of_ids [$this->id_field_name[$i]] = $this->GetFieldValue($this->id_field_name[$i]);
		return$arr_of_ids;

	}

}