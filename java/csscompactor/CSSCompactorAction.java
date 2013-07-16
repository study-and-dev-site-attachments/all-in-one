import java.io.*;
import java.util.Iterator;
import java.util.Map;
import java.util.Properties;
import java.util.regex.Pattern;
import java.util.regex.Matcher;
import java.util.logging.Logger;
import java.awt.event.ActionListener;
import java.awt.event.ActionEvent;

/**
 * Created by IntelliJ IDEA.
 * User: Programmer
 * Date: 07.12.2007
 * Time: 13:03:33
  */
public class CSSCompactorAction {

    /**
     * ����� �������� ����������� ���������� �������� ������ css
     * ���������� ��� ��������� ������������ ���� ������ ������� ����� ������� � ���� ��������
     */
    public static final String REPLACE_MULTIPLE_SPACES_WITH_JUST_ONE_SPACE = "REPLACE_MULTIPLE_SPACES_WITH_JUST_ONE_SPACE";
    public static final String REMOVE_SPACE_AROUND_CHARS = "REMOVE_SPACE_AROUND_CHARS";
    public static final String LEAVE_SPACE_BETWEEN_SELECTORS = "LEAVE_SPACE_BETWEEN_SELECTORS";
    public static final String REMOVE_TABS = "REMOVE_TABS";
    public static final String DONT_STRIP_ANY_COMMENTS = "DONT_STRIP_ANY_COMMENTS";
    public static final String STRIP_ALL_COMMENTS = "STRIP_ALL_COMMENTS";
    public static final String STRIP_COMMENTS_AT_LEAST_CHARS_LONG = "STRIP_COMMENTS_AT_LEAST_CHARS_LONG";

    public static final String VALUE_FOR_STRIP_COMMENTS_AT_LEAST_CHARS_LONG = "VALUE_FOR_STRIP_COMMENTS_AT_LEAST_CHARS_LONG";
    public static final String LEAVE_LINES_AS_THEY_ARE = "LEAVE_LINES_AS_THEY_ARE";
    public static final String REPLACE_MULTIPLE_EMPTY_LINES_WITH_JUST_ONE_EMPTY_LINE = "REPLACE_MULTIPLE_EMPTY_LINES_WITH_JUST_ONE_EMPTY_LINE";
    public static final String REMOVE_ALL_NEWLINES = "REMOVE_ALL_NEWLINES";
    public static final String COMPRESS_COLOR_CODES_WHERE_POSSIBLE = "COMPRESS_COLOR_CODES_WHERE_POSSIBLE";
    public static final String REMOVE_UNNECESSARY_SEMI_COLONS = "REMOVE_UNNECESSARY_SEMI_COLONS";
    public static final String ONE_COMMAND_PER_LINE = "ONE_COMMAND_PER_LINE";

    public static final String CHARSET_IN = "CHARSET_IN";
    public static final String CHARSET_OUT = "CHARSET_OUT";

    public static final String LEAVE_SPACE_BETWEEN_PROPERTIES = "LEAVE_SPACE_BETWEEN_PROPERTIES";

    public static final String SKIP_COMPRESS_ONLY_CHANGE_CHARSET = "SKIP_COMPRESS_ONLY_CHANGE_CHARSET";

    /**
     * ������ ������� ����������� ������
     */
    Logger lo = Logger.getLogger("CSSCompactorAction");

    /**
     * ������� ������� ����������� ���������� ��������������
     * @param alist ����� � ������������� ���� ����� ����� ��������� �������������, ������ ����  Map<File, File>
     * ������ ������������ �������� ����� � ���������
     * @param compset ����� ���������� ��������������, ����� ������� Properties - ��� ������������� ���� ���������
     * @param actionListener ������ ��������� ���������������� � ���, ����� �������� ��������� ����� ��� ���������
     * ��������� ���� ��� ��������� ���� ������ ���� ���������
      */

    public void execute(Map<File, File> alist, Properties compset, ActionListener actionListener) {
        // ���������� �������� �������������� �������� �� ������������ ������:
        // �� �����������, ������ � ������ � ����� ��������������
        int stat_count_files = 0;
        int size_all_files_orig = 0;
        int size_all_files_after = 0;
        // ��������� ��� ����� � ������ �������
        Iterator<File> iterator = alist.keySet().iterator();
        while (iterator.hasNext()) {
            File fin = iterator.next();
            File fout = alist.get(fin);
            // ��������� ������� ����
            String sin = readFile(fin, compset.getProperty(CSSCompactorAction.CHARSET_IN));
            if (sin == null) {
                lo.warning("skip file: " + fin);
                continue;
            }
            // ���� ������� ����� ������ css, �� ������� � �������  makeCSSCompact
            if (compset.get(SKIP_COMPRESS_ONLY_CHANGE_CHARSET).equals(Boolean.FALSE)){
                LiveArray sin2 = makeCSSCompact(sin, compset);

                saveFile(fout, compset.getProperty(CSSCompactorAction.CHARSET_OUT), sin2.getString(0));
                stat_count_files++;
                size_all_files_orig += sin2.getInteger(1);
                size_all_files_after += sin2.getInteger(2);
                // ����� ��������� ���������� ����� �� �������� ��������� ���������, ��� ��� ���� ����� "�����"
                actionListener.actionPerformed(new ActionEvent(new LiveArray(stat_count_files),
                    -1, "CSSCOMPACTOR_NEXT_FILE"));
            }// ��������, ��� ����� ���� �������������� css-����� �� ����� ��������� � ������
            else{
                saveFile(fout, compset.getProperty(CSSCompactorAction.CHARSET_OUT), sin);
            }

        }
        // �� ��������� ����� �������� ���� ������ ��������� ��������� ���������������� ���������� � ���, ��� ��������� ���� ������ ���� ���������
        actionListener.actionPerformed(new ActionEvent(new LiveArray(
                stat_count_files, size_all_files_orig, size_all_files_after),
                -1, "CSSCOMPACTOR_FINISHED"));
    }

    /**
     * ������ ������� �� ������������� ��� ������� ������ � �������� ��������������� ��� execute
     * @param sin ������� ������ (���������� ����� ������� ����� "�����")
     * @param compset ����� ���������� ����������� ��� ��� ������ ����� ����������� ��������������
     * @return  ������������ ������ ���������� (��������� ��������������, �������� ������ ����� � ��������)
     */
    private LiveArray makeCSSCompact(String sin, Properties compset) {
        int size_orig = sin.length();
        lo.info("processed next source");
        if (compset.get(REPLACE_MULTIPLE_SPACES_WITH_JUST_ONE_SPACE).equals(Boolean.TRUE)) {
            sin = sin.replaceAll("\\s{2,}", " ");
        }
        if (compset.get(REMOVE_SPACE_AROUND_CHARS).equals(Boolean.TRUE)) {
            //Remove space around chars ;:{},
            String x  = "(\\{|}|,|:|;)";
            sin = sin.replaceAll(x+" ", "$1");
            sin = sin.replaceAll(" "+x, "$1");

            sin = sin.replaceAll(" \\{", "{");
            sin = sin.replaceAll(" }", "}");
            sin = sin.replaceAll("\\{ ", "{");
            sin = sin.replaceAll("} ", "}");

            sin = sin.replaceAll(" ,", ",");
            sin = sin.replaceAll(", ", ",");

            sin = sin.replaceAll(" :", ":");
            sin = sin.replaceAll(": ", ":");

            sin = sin.replaceAll(" ;", ";");
            sin = sin.replaceAll("; ", ";");
        }


        if (compset.get(STRIP_ALL_COMMENTS).equals(Boolean.TRUE)) {
            //Replace multiple empty lines with just one empty line
            sin = sin.replaceAll("(?is)/\\*.*?\\*/", "");
        }

        if (compset.get(STRIP_COMMENTS_AT_LEAST_CHARS_LONG).equals(Boolean.TRUE)) {
            //Replace multiple empty lines with just one empty line
            Integer pi = Integer.parseInt(compset.getProperty(VALUE_FOR_STRIP_COMMENTS_AT_LEAST_CHARS_LONG));

            StringBuffer sb = null;
            Pattern p = null;
            Matcher m = null;
            try {
                p = Pattern.compile("(?is)/\\*.*?\\*/");
                m = p.matcher(sin);
                sb = new StringBuffer();
                while (m.find()) {
                    try {
                        String gr_0 = m.group(0);
                        if (gr_0.length() - 4 <= pi)
                            m.appendReplacement(sb, gr_0);
                        else
                            m.appendReplacement(sb, "");
                    } catch (Exception e) {
                        e.printStackTrace();
                    }
                }
                m.appendTail(sb);
            } catch (Exception e) {
                e.printStackTrace();
            }
            sin = sb.toString();

        }

        if (compset.get(LEAVE_SPACE_BETWEEN_SELECTORS).equals(Boolean.TRUE)) {
            sin = sin.replaceAll("(?is)(,)(?!\\s)(?!\")", "$1 ");
        }

        if (compset.get(LEAVE_SPACE_BETWEEN_PROPERTIES).equals(Boolean.TRUE)) {
            sin = sin.replaceAll("(?is);(?!\\s)", "; ");
            sin = sin.replaceAll("(?is);\\s+}", ";{");

            //sin = sin.replaceAll("(?is)(;)\\s", "$1");
        }
        if (compset.get(COMPRESS_COLOR_CODES_WHERE_POSSIBLE).equals(Boolean.TRUE)) {
            sin = sin.replaceAll("([A-Fa-f0-9])\\1([A-Fa-f0-9])\\2([A-Fa-f0-9])\\3", "$1$2$3 ");
        }

        if (compset.get(ONE_COMMAND_PER_LINE).equals(Boolean.TRUE)) {
            StringBuffer sb = null;
            Pattern p = null;
            Matcher m = null;
            try {
                p = Pattern.compile("(?ims)^[\\t ]*(.*?)(\\n?)\\{(.*?)(\\n*)\\}");
                m = p.matcher(sin);
                sb = new StringBuffer();
                while (m.find()) {
                    try {
                        String gr_1 = m.group(1);
                        String gr_3 = m.group(3);
                        m.appendReplacement(sb, gr_1.replace("\n", "") + "{" + gr_3.replace("\n", " ") + "}\n");
                    } catch (Exception e) {
                        e.printStackTrace();
                    }
                }
                m.appendTail(sb);
            } catch (Exception e) {
                e.printStackTrace();
            }
            sin = sb.toString();
        }

        if (compset.get(REMOVE_TABS).equals(Boolean.TRUE)) {
            sin = sin.replaceAll("\t", "");
        }
        if (compset.get(REPLACE_MULTIPLE_EMPTY_LINES_WITH_JUST_ONE_EMPTY_LINE).equals(Boolean.TRUE)) {
            //Replace multiple empty lines with just one empty line
            sin = sin.replaceAll("(\n){2,}", "\n");
        }

        if (compset.get(REMOVE_ALL_NEWLINES).equals(Boolean.TRUE)) {
            //Replace multiple empty lines with just one empty line
            sin = sin.replaceAll("\n", "");
        }

        sin = sin.replace(" ;}", "}");
        sin = sin.replace(";} ", "}");
        sin = sin.replace(" }", "}");
        sin = sin.replace("} ", "}");

        sin = sin.replace("{ ", "{");
        sin = sin.replace(" {", "{");

        int size_after = sin.length();
        return new LiveArray(sin, size_orig, size_after);
    }

    /**
     * ������� ����������� ���������� ��������� ������ � ���� � �������� ���������
     * @param fout ������-����, ���� ���� ��������� ������ ������
     * @param charset ��������� � ������� ���������
     * @param sin ����������, ������ ���������� ����������
     */
    public static void saveFile(File fout, String charset, String sin) {
        try {
            OutputStreamWriter ow = new OutputStreamWriter(new FileOutputStream(fout), charset);
            ow.write(sin);
            ow.close();
        } catch (IOException e) {
            e.printStackTrace();
            //lo.severe(e.getMessage());
        }
    }

    /**
     * ������� ����������� ������ �� ����� ����� ����������� � �������� ���������
     * @param fin ������-���� ������ ������� ��������� ������
     * @param encName ��������� ��� ������ ����������� �����
     * @return ������ ���������� ��� ���������� �����
     */
    public static String readFile(File fin, String encName) {
        try {
            LineNumberReader brin = new LineNumberReader(new InputStreamReader(new FileInputStream(fin), encName));
            StringBuffer buf = new StringBuffer();
            String line;
            String CR_LF = "\n";
            while ((line = brin.readLine()) != null) {
                buf.append(line);
                buf.append(CR_LF);
            }
            brin.close();
            return buf.toString();
        }
        catch (Exception e) {
            e.printStackTrace();
            //lo.severe(fin + "-> " + e.getMessage());
            return null;
        }

    }
}
