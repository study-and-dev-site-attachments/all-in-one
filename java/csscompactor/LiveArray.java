/**
 * Created by IntelliJ IDEA.
 * User: Programmer
 * Date: 07.12.2007
 * Time: 16:49:30
  */

/**
 * Полезный класс служит для хранения произвольного количества переменных
 */
public class LiveArray {
    /**
     * Внутреннее поле в котором будет храниться массив переменных
     */
    private Object [] args;

    /**
     * Конструктор класса
     * @param args Переменный список аргументов
     */
    public LiveArray (Object ...args){
        this.args = args;
    }

    /**
     * Функция получающая по заданному номеру значение элемента и возвращает его без какого либо преобразования типа.
     * @param i Номер элемента. В том случае если такого элемента нет, то будет возвращен null.
     * @return  Элемент массива args под номером i
     */
    public Object get (int i){
        return i>=0 && i < args.length?args [i]:null;
    }

    /**
     * Функция получающая по заданному номеру значение элемента и возвращает его с преобразование к типу String
     * @param i Номер элемента. В том случае если такого элемента нет, то будет возвращен null.
     * @return  Элемент массива args под номером i
     */
    public String getString (int i){
        return (String)(i>=0 && i < args.length?args [i]:null);
    }
    /**
     * Функция получающая по заданному номеру значение элемента и возвращает его с преобразование к типу Integer
     * @param i Номер элемента. В том случае если такого элемента нет, то будет возвращен null.
     * @return  Элемент массива args под номером i
     */
    public Integer getInteger (int i){
        return (Integer)(i>=0 && i < args.length?args [i]:null);
    }
    /**
     * Функция получающая по заданному номеру значение элемента и возвращает его с преобразование к типу Double
     * @param i Номер элемента. В том случае если такого элемента нет, то будет возвращен null.
     * @return  Элемент массива args под номером i
     */
    public Double getDouble (int i){
        return (Double)(i>=0 && i < args.length?args [i]:null);
    }
    /**
     * Функция получающая по заданному номеру значение элемента и возвращает его с преобразование к типу Boolean
     * @param i Номер элемента. В том случае если такого элемента нет, то будет возвращен null.
     * @return  Элемент массива args под номером i
     */
    public Boolean getBoolean(int i){
        return (Boolean)(i>=0 && i < args.length?args [i]:null);
    }
    /**
     * Функция получающая по заданному номеру значение элемента и возвращает его с преобразование к типу LiveArray
     * @param i Номер элемента. В том случае если такого элемента нет, то будет возвращен null.
     * @return  Элемент массива args под номером i
     */
    public LiveArray getLiveArray (int i){
        return (LiveArray)(i>=0 && i < args.length?args [i]:null);
    }



}//end of -- LiveArray -- 
