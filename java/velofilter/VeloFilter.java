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
 * ������� ������. ��� ���������� ��������� ���������� ���������� ������������� ������� ������� �, ��������,
 * page.velo �� �������� ����� �������� jsp ��� ��������, ��������, page.jsp
 * ����� ��������� ������ �� ������ ������, � �� ��������� �������������� ������ (�� ������ html, �� ������������),
 * �, ������, ������ ���������� ������ ��������� �������, ���� ������ ������ ������� html-��������. � ��������
 * ������� �������������� ������������ velocity
 */

public class VeloFilter implements Filter {
    FilterConfig filterConfig;
    VelocityEngine engine = new VelocityEngine();

    // ���������������� ����������, � ������ �� ������� ��� ��������� � ������ web.xml ��� ��������� �������
    // ��-������, ���������� ���������, �������� ������ ��������������� ����� ����������� ��������
    public String inNameTemplate = "^/([^/]*)/(.*)\\.velo(.*)$";
    // ����� ������� ������ ������������ ������ �� �����
    public String outNameTemplate = "/$2.jsp$3";
    // ������� ���������� ����� velocity ������� �� ��������� ��������� �����,
    // ����������: ��� ����������� ������������ ������ ������ ����� �������������� ������� ��-��������� ���
    // ����� ������� Velocity, ��� ����� ����� ��������� ��� ������� ������� ���������� � ������  in-velo-processor,
    // ��������� �� ���������� ������ ���� ��� �������
    public String veloNameTemplate = "/$2.vm";
    // ���� � ����� � ����������� Velocity
    public String veloConfigLocation = "WEB-INF/veloconf.properties";
    // ������� ���� ����� �� �������� ������ ������� ������ (������, ������, �������� �������) �� ������ � ���� ��������� ����������,
    // �� � ��������������� ��������. �.�. ���� ��� ���������� �������� � �� ���� �������� ���� �������� ���������� fio,
    // �� � ��� ����� ���������� �� ������ $fio, �� � $param.fio
    public Boolean passToVeloCompoundObjects = false;

    public void init(FilterConfig filterConfig) throws ServletException {
        // ����� ��������� ��� ���������� �������� ���������, ���������������� ���������� ���������� � ...
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
        // ... � ����� ������� ����� ��������� ���� �������� ��� Velocity, ����� ����� � ��� ������ �� ���� ��������
        // � �������� �� ���, ����� ��� ��������� �� �� ������������� ���� (������������� � ���� ����?), � ��, ������,
        // ���������� ���� � �������� � ���������
        try {
            Properties p = new Properties();
            p.load(new FileInputStream(filterConfig.getServletContext().getRealPath(veloConfigLocation)));
            Enumeration propNames = p.propertyNames();
            // ���������� ��� �������� ������ ������� �����
            while (propNames.hasMoreElements()) {
                String pName = (String) propNames.nextElement();
                // ���� ����� �������� ���� ��� ���������� ���� ResourceLoader-�
                if (pName.endsWith("resource.loader.path")) {
                    String pValue = p.getProperty(pName);
                    // � ��� ���� �������� ������� �������� �� �������� ���������� ���������,
                    // �� ����� ��������� ��� �����������
                    if ((!pValue.startsWith("file:")) && (!pValue.startsWith("http:")) && (!pValue.startsWith("https:"))) {
                        pValue = filterConfig.getServletContext().getRealPath(pValue);
                        if (new File(pValue).exists()) {
                            p.put(pName, pValue);
                        }
                    }
                }
            }
            engine.init(p); // ��������� ������������� velocity

        } catch (Exception e) {
            e.printStackTrace();
            throw new RuntimeException("cannot init velocity engine", e);
        }
    }

    public void doFilter(ServletRequest req, ServletResponse resp, FilterChain filterChain) throws IOException, ServletException {
        if (!(req instanceof HttpServletRequest)) {
            filterChain.doFilter(req, resp);
        }
        // ����� ���� ��� � ����� ������������� ���� ��������, �� HTTP, ���� �������� ���������� ����,
        // ������ �� ������ ��� ������� ��������� ������ ��������� Velocity-�������
        HttpServletRequest hreq = (HttpServletRequest) req;
        HttpServletResponse hresp = (HttpServletResponse) resp;

        String uri = hreq.getRequestURI();
        String shortname = hreq.getServletPath();
        if (shortname.length() > 0 && shortname.charAt(0) == '/')
            shortname = shortname.substring(1);
        // ��������� �������� �� ���������� �����, ����� ��������� ���� �� �������� � ���� ��������� �������
        if (!(uri.matches(inNameTemplate) && hreq.getAttribute("in-velo-processor") == null)) {
            filterChain.doFilter(req, resp);
            return;
        }
        // � ��������� ������ ��������� ����� ��� �����, ������� ����� ���������
        String targetFileName = uri.replaceFirst(inNameTemplate, outNameTemplate);
        hreq.setAttribute("in-velo-processor", true);
        VeloResponse veloresponse = new VeloResponse(hresp);
        filterConfig.getServletContext().getRequestDispatcher(targetFileName).include(hreq, veloresponse);

        // ������ ����� ��������� VelocityContext ������� 
        VelocityContext vc = new VelocityContext();

        // ��������� ����� �� ��������� �������������� �������� ���������� � �������� Velocity
        if (passToVeloCompoundObjects) {
            // �������� ������� ���������� ������� ����� ������, ��� ��� � ��� � ����� Map
            // ��� ������ ���� ��������� ������ � �������� ��� ��������� ��������� ... ����� ����������


            Map<String, Object> mparam = new HashMap<String, Object>();
            Enumeration paramsNames = hreq.getParameterNames();
            while (paramsNames.hasMoreElements()) {
                String si = (String) paramsNames.nextElement();
                String s = hreq.getParameter(si);
                mparam.put(si, s);
            }
            vc.put("param", mparam);

            // ��������� �� ��������� ������ ����� �������������, ������������� �� �� ���� �������������, ������, � Map
            // Cookie ������������ � ���� �������, ��� ����� � �� ����� ���������� � ���� Map
            Cookie[] cookies = hreq.getCookies();
            Map<String, String> mcookie = new HashMap<String, String>();
            for (Cookie cooky : cookies) {
                String value = cooky.getValue();
                mcookie.put(cooky.getName(), value);
            }
            vc.put("cookie", mcookie);

            // ������ ����� ����� �� ������� ������ � �������� �������������� � Map
            Enumeration sessionNames = hreq.getSession().getAttributeNames();
            Map<String, Object> msession = new HashMap<String, Object>();
            while (sessionNames.hasMoreElements()) {
                String si = (String) sessionNames.nextElement();
                Object value = hreq.getSession().getAttribute(si);
                msession.put(si, value);
            }
            vc.put("session", msession);

            // ����� ����� ������������� ��� ���������� ���������� ������ Request-�
            Enumeration requestNames = hreq.getAttributeNames();
            Map<String, Object> mrequest = new HashMap<String, Object>();
            while (requestNames.hasMoreElements()) {
                String si = (String) requestNames.nextElement();
                Object value = hreq.getAttribute(si);
                mrequest.put(si, value);
            }
            vc.put("request", mrequest);
        }

        // ������ ��������� ��������� ����������, ��� ������ ���� � ��� ������� ����� ����������
        // � ������, ������� ��� ���������� �������, �� � �� �������
        // ����� ����� ���������� ����� ������� ����� ��������
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

        // ��������� ��� - ����� ����� ������ ��� velocity � ������������ �������� html-��������
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
