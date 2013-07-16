/**
 * Created by IntelliJ IDEA.
 * User: Programmer
 * Date: 07.12.2007
 * Time: 16:49:30
  */

/**
 * �������� ����� ������ ��� �������� ������������� ���������� ����������
 */
public class LiveArray {
    /**
     * ���������� ���� � ������� ����� ��������� ������ ����������
     */
    private Object [] args;

    /**
     * ����������� ������
     * @param args ���������� ������ ����������
     */
    public LiveArray (Object ...args){
        this.args = args;
    }

    /**
     * ������� ���������� �� ��������� ������ �������� �������� � ���������� ��� ��� ������ ���� �������������� ����.
     * @param i ����� ��������. � ��� ������ ���� ������ �������� ���, �� ����� ��������� null.
     * @return  ������� ������� args ��� ������� i
     */
    public Object get (int i){
        return i>=0 && i < args.length?args [i]:null;
    }

    /**
     * ������� ���������� �� ��������� ������ �������� �������� � ���������� ��� � �������������� � ���� String
     * @param i ����� ��������. � ��� ������ ���� ������ �������� ���, �� ����� ��������� null.
     * @return  ������� ������� args ��� ������� i
     */
    public String getString (int i){
        return (String)(i>=0 && i < args.length?args [i]:null);
    }
    /**
     * ������� ���������� �� ��������� ������ �������� �������� � ���������� ��� � �������������� � ���� Integer
     * @param i ����� ��������. � ��� ������ ���� ������ �������� ���, �� ����� ��������� null.
     * @return  ������� ������� args ��� ������� i
     */
    public Integer getInteger (int i){
        return (Integer)(i>=0 && i < args.length?args [i]:null);
    }
    /**
     * ������� ���������� �� ��������� ������ �������� �������� � ���������� ��� � �������������� � ���� Double
     * @param i ����� ��������. � ��� ������ ���� ������ �������� ���, �� ����� ��������� null.
     * @return  ������� ������� args ��� ������� i
     */
    public Double getDouble (int i){
        return (Double)(i>=0 && i < args.length?args [i]:null);
    }
    /**
     * ������� ���������� �� ��������� ������ �������� �������� � ���������� ��� � �������������� � ���� Boolean
     * @param i ����� ��������. � ��� ������ ���� ������ �������� ���, �� ����� ��������� null.
     * @return  ������� ������� args ��� ������� i
     */
    public Boolean getBoolean(int i){
        return (Boolean)(i>=0 && i < args.length?args [i]:null);
    }
    /**
     * ������� ���������� �� ��������� ������ �������� �������� � ���������� ��� � �������������� � ���� LiveArray
     * @param i ����� ��������. � ��� ������ ���� ������ �������� ���, �� ����� ��������� null.
     * @return  ������� ������� args ��� ������� i
     */
    public LiveArray getLiveArray (int i){
        return (LiveArray)(i>=0 && i < args.length?args [i]:null);
    }



}//end of -- LiveArray -- 
