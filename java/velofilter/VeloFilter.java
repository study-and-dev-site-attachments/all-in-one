package black.ti.server;

import org.apache.velocity.Template;
import org.apache.velocity.VelocityContext;
import org.apache.velocity.app.VelocityEngine;

import javax.servlet.*;
import javax.servlet.http.Cookie;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import java.io.File;
import java.io.FileInputStream;
import java.io.IOException;
import java.io.PrintWriter;
import java.util.Enumeration;
import java.util.HashMap;
import java.util.Map;
import java.util.Properties;

/**
 * Сервлет фильтр. Его назначение выполнять прозрачную перезапись запрашиваемых адресов страниц с, например,
 * page.velo на реальный адрес страницы jsp или сервлета, например, page.jsp
 * затем выполнять запрос по новому адресу, и на основании сформированных данных (не текста html, он игнорируется),
 * а, именно, данных помещенных внутрь атрибутов запроса, этот фильтр должен создать html-документ. В качестве
 * системы шаблонификации используется velocity
 */

public class VeloFilter implements Filter {
    FilterConfig filterConfig;
    VelocityEngine engine = new VelocityEngine();

    // конфигурационные переменные, с такими же именами они объявлены и внутри web.xml как параметры фильтра
    // во-первых, регулярное выражение, которому должен соответствовать адрес запрошенной страницы
    public String inNameTemplate = "^/([^/]*)/(.*)\\.velo(.*)$";
    // затем правило замены запрошенного адреса на новый
    public String outNameTemplate = "/$2.jsp$3";
    // правило вычисления имени velocity шаблона на основании исходного имени,
    // примечание: код выполняющий формирование набора данных может переопределить правила по-умолчанию для
    // имени шаблона Velocity, для этого нужно поместить как атрибут запроса переменную с именем  in-velo-processor,
    // значением же переменной должно быть имя шаблона
    public String veloNameTemplate = "/$2.vm";
    // путь к файлу с настройками Velocity
    public String veloConfigLocation = "WEB-INF/veloconf.properties";
    // признак того будут ли переданы внутрь шаблона данные (кукусы, сессия, атрибуты запроса) не только в виде отдельных переменных,
    // но и сложносоставных объектов. Т.е. если эта переменная включена и на вход странице была передана переменная fio,
    // то к ней можно обратиться не только $fio, но и $param.fio
    public Boolean passToVeloCompoundObjects = false;

    public void init(FilterConfig filterConfig) throws ServletException {
        // нужно прочитать все переданные сервлету параметры, инициализировать внутренние переменные и ...
        this.filterConfig = filterConfig;
        if (filterConfig.getInitParameter("inNameTemplate") != null)
            inNameTemplate = filterConfig.getInitParameter("inNameTemplate");
        if (filterConfig.getInitParameter("outNameTemplate") != null)
            outNameTemplate = filterConfig.getInitParameter("outNameTemplate");
        if (filterConfig.getInitParameter("veloNameTemplate") != null)
            veloNameTemplate = filterConfig.getInitParameter("veloNameTemplate");
        if (filterConfig.getInitParameter("passToVeloCompoundObjects") != null)
            passToVeloCompoundObjects = Boolean.parseBoolean(filterConfig.getInitParameter("passToVeloCompoundObjects"));

        if (filterConfig.getInitParameter("veloConfigLocation") != null)
            veloConfigLocation = filterConfig.getInitParameter("veloConfigLocation");
        // ... и самое сложное нужно прочитать файл настроек для Velocity, затем найти в нем ссылки на пути ресурсов
        // и изменить их так, чтобы они указывали не на относительные пути (относительные в виде чего?), а на, именно,
        // абсолютные пути к каталогу с шаблонами
        try {
            Properties p = new Properties();
            p.load(new FileInputStream(filterConfig.getServletContext().getRealPath(veloConfigLocation)));
            Enumeration propNames = p.propertyNames();
            // перебираем все свойства внутри данного файла
            while (propNames.hasMoreElements()) {
                String pName = (String) propNames.nextElement();
                // если нашли свойство пути для некоторого вида ResourceLoader-а
                if (pName.endsWith("resource.loader.path")) {
                    String pValue = p.getProperty(pName);
                    // и при этом значение данного свойства не является абсолютным значением,
                    // то нужно выполнить его модификацию
                    if ((!pValue.startsWith("file:")) && (!pValue.startsWith("http:")) && (!pValue.startsWith("https:"))) {
                        pValue = filterConfig.getServletContext().getRealPath(pValue);
                        if (new File(pValue).exists()) {
                            p.put(pName, pValue);
                        }
                    }
                }
            }
            engine.init(p); // завершаем инициализацию velocity

        } catch (Exception e) {
            e.printStackTrace();
            throw new RuntimeException("cannot init velocity engine", e);
        }
    }

    public void doFilter(ServletRequest req, ServletResponse resp, FilterChain filterChain) throws IOException, ServletException {
        if (!(req instanceof HttpServletRequest)) {
            filterChain.doFilter(req, resp);
        }
        // после того как я отсек нестандартные виды запросов, не HTTP, пора заняться проверками того,
        // должен ли именно мой сервлет выполнять задачу обработки Velocity-запроса
        HttpServletRequest hreq = (HttpServletRequest) req;
        HttpServletResponse hresp = (HttpServletResponse) resp;

        String uri = hreq.getRequestURI();
        String shortname = hreq.getServletPath();
        if (shortname.length() > 0 && shortname.charAt(0) == '/')
            shortname = shortname.substring(1);
        // проверяем подходит ли запрошеный адрес, также проверяем нету ли рекурсии в ходе обработки запроса
        if (!(uri.matches(inNameTemplate) && hreq.getAttribute("in-velo-processor") == null)) {
            filterChain.doFilter(req, resp);
            return;
        }
        // в противном случае вычисляем новое имя файла, который нужно запросить
        String targetFileName = uri.replaceFirst(inNameTemplate, outNameTemplate);
        hreq.setAttribute("in-velo-processor", true);
        VeloResponse veloresponse = new VeloResponse(hresp);
        filterConfig.getServletContext().getRequestDispatcher(targetFileName).include(hreq, veloresponse);

        // теперь нажно заполнить VelocityContext данными 
        VelocityContext vc = new VelocityContext();

        // проверяем нужно ли выполнить дополнительную передачу параметров в контекст Velocity
        if (passToVeloCompoundObjects) {
            // передать входные переменные скрипту очень просто, они уже и так в форме Map
            // вот только этот неудобный формат с массивом как значением параметра ... будем исправлять


            Map<String, Object> mparam = new HashMap<String, Object>();
            Enumeration paramsNames = hreq.getParameterNames();
            while (paramsNames.hasMoreElements()) {
                String si = (String) paramsNames.nextElement();
                String s = hreq.getParameter(si);
                mparam.put(si, s);
            }
            vc.put("param", mparam);

            // остальные же источники данных нужно унифицировать, преобразовать из их форм представления, именно, в Map
            // Cookie представлены в виде массива, это плохо и их нужно переписать в виде Map
            Cookie[] cookies = hreq.getCookies();
            Map<String, String> mcookie = new HashMap<String, String>();
            for (Cookie cooky : cookies) {
                String value = cooky.getValue();
                mcookie.put(cooky.getName(), value);
            }
            vc.put("cookie", mcookie);

            // Сессия также имеет не обычный формат и подлежит преобразованию в Map
            Enumeration sessionNames = hreq.getSession().getAttributeNames();
            Map<String, Object> msession = new HashMap<String, Object>();
            while (sessionNames.hasMoreElements()) {
                String si = (String) sessionNames.nextElement();
                Object value = hreq.getSession().getAttribute(si);
                msession.put(si, value);
            }
            vc.put("session", msession);

            // также нужно унифицировать все переменные полученные внутри Request-а
            Enumeration requestNames = hreq.getAttributeNames();
            Map<String, Object> mrequest = new HashMap<String, Object>();
            while (requestNames.hasMoreElements()) {
                String si = (String) requestNames.nextElement();
                Object value = hreq.getAttribute(si);
                mrequest.put(si, value);
            }
            vc.put("request", mrequest);
        }

        // теперь передадим остальные переменные, вот только если у вас совпали имена переменных
        // в сессии, кукисах или параметрах запроса, то я не виноват
        // лучше всего обращаться через сложные имена объектов
        Enumeration paramsNames = hreq.getParameterNames();
        while (paramsNames.hasMoreElements()) {
            String si = (String) paramsNames.nextElement();
            String s = hreq.getParameter(si);
            vc.put(si, s);
        }

        Enumeration requestNames = hreq.getAttributeNames();
        while (requestNames.hasMoreElements()) {
            String si = (String) requestNames.nextElement();
            Object value = hreq.getAttribute(si);
            vc.put(si, value);
        }

        Enumeration sessionNames = hreq.getSession().getAttributeNames();
        while (sessionNames.hasMoreElements()) {
            String si = (String) sessionNames.nextElement();
            Object value = hreq.getSession().getAttribute(si);
            vc.put(si, value);
        }

        Cookie[] cookies = hreq.getCookies();
        for (Cookie cooky : cookies) {
            String value = cooky.getValue();
            vc.put(cooky.getName(), value);
        }

        // последний шаг - нужно найти шаблон для velocity и сформировать итоговый html-документ
        Object templateName = hreq.getAttribute("in-velo-processor");
        String veloName = null;
        if (templateName instanceof String) {
            veloName = (String) templateName;
        } else {
            veloName = uri.replaceFirst(inNameTemplate, veloNameTemplate);
        }
        String enc = veloresponse.getCharacterEncoding();
        if (enc == null)
            enc = "utf-8";
        Template t = getTemplate(veloName, enc);
        if (t != null) {
            //hresp.setCharacterEncoding(enc);
            //hresp.setContentType(veloresponse.getContentType());
            //veloresponse.copyHeadersTo (hresp);
            t.merge(vc, hresp.getWriter());
        } else {
            sendErrorMsgNoVeloTemplate(hresp.getWriter());
        }
    }

    private void sendErrorMsgNoVeloTemplate(PrintWriter writer) {
        writer.println("Velo Template not found");
    }

    private Template getTemplate(String veloName, String enc) {
        try {
            return engine.getTemplate(veloName, enc);
        } catch (Exception e) {
            e.printStackTrace();
            return null;
        }
    }

    public void destroy() {
    }
}
