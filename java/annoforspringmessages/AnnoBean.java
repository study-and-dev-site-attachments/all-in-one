package blz.server.utils.anno;

import org.springframework.context.ApplicationContext;
import org.springframework.context.ApplicationEvent;
import org.springframework.context.ApplicationListener;
import org.springframework.context.event.ContextRefreshedEvent;
import org.springframework.web.context.ServletContextAware;

import javax.servlet.ServletContext;
import java.io.File;
import java.io.IOException;
import java.util.logging.Logger;
import java.lang.reflect.Method;
import java.lang.reflect.InvocationTargetException;

/**
 * ����������� bean ���������������� � ��������� ��������� �� ��������� ����� ���������� spring
 */
public class AnnoBean implements ApplicationListener , ServletContextAware {
    static Logger logger = Logger.getLogger(AnnoBean.class.getName());
    String reportName;

    /**
     * ����������� ��� �������� bean-� ������ ������� ���� � ����� � ����������� �������
     * @param reportName ��� ����� ���� ����� ������� �����
     */
    public AnnoBean(String reportName) {
        this.reportName = reportName;
    }


    ServletContext servletContext;
    public void setServletContext(ServletContext servletContext) {
        this.servletContext = servletContext;        
    }

    /**
     * ��������� ������� ���������� ����� ���������� spring
     * @param event
     */
    public void onApplicationEvent(ApplicationEvent event) {
        // ��� ���������� ������ ������� �� �������� ��� ���������� ���������
        if (!(event instanceof ContextRefreshedEvent)) return;
        logger.info("event: "+event);
        File rName = new File (reportName);
        logger.info("use reportName: "+reportName);
        // ����������� ������������ ��� ��������� ��� �����
        if (reportName.indexOf ("file:") == -1){
            // � ������ ���� ��� �� ���������� ���� �� ��� ������� - �� �� ����� �������� ����� ����-������
            // ������� � ������� ���������� � �������� ��������� ������� - ������� ���-��������
            // ���� ������� ������� �, � ��� ����������� ������ �� ������ � ���-���������
            String pathToContextRoot = null;
            try {
                pathToContextRoot = servletContext.getRealPath("/");
            } catch (Exception e) {
                //��������� ���������� �.�. � ����� ������ ������� �������� ��������� �� �����������
            }
            logger.info("get path to context root: "+pathToContextRoot);
            // �������� ���� � ����� ���-���������, � ���� �� ����� �� ����� ���� ����������� �� ��� ����
            if (pathToContextRoot != null)
                rName = new File (pathToContextRoot , reportName);
            else{
                // � ��������� ������ ���� ������ ����� ������ �� ��������� �������� 
                logger.info("try create tmp file");
                try {
                    rName = File.createTempFile(reportName, ".html");
                    logger.info("tmp file name: "+ rName);
                } catch (IOException e) {
                    e.printStackTrace();
                    logger.severe(e.getMessage());
                    return;
                }
            }
        }
        // ������ ����� ��������� ��� ������� ��������� spring
        logger.info("AnnoProcessor.runScanner");
        AnnoProcessor.runScanner((ApplicationContext) event.getSource(), rName, true, null);
    }
}
