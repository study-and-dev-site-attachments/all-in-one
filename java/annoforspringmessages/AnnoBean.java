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
 * Специальный bean заинтересованный в получении сообщений об жизненном цикле контейнера spring
 */
public class AnnoBean implements ApplicationListener , ServletContextAware {
    static Logger logger = Logger.getLogger(AnnoBean.class.getName());
    String reportName;

    /**
     * Обязательно при создании bean-а нужноу указать путь к файлу с создаваемым отчетом
     * @param reportName имя файла куда будет помещен отчет
     */
    public AnnoBean(String reportName) {
        this.reportName = reportName;
    }


    ServletContext servletContext;
    public void setServletContext(ServletContext servletContext) {
        this.servletContext = servletContext;        
    }

    /**
     * Обработка событий жизненного цикла контейнера spring
     * @param event
     */
    public void onApplicationEvent(ApplicationEvent event) {
        // нас интересуют только события об загрузке или обновлении контекста
        if (!(event instanceof ContextRefreshedEvent)) return;
        logger.info("event: "+event);
        File rName = new File (reportName);
        logger.info("use reportName: "+reportName);
        // анализируем поставленное для обработки имя файла
        if (reportName.indexOf ("file:") == -1){
            // в случае если это не абсолютный путь то все сложнее - мы не можем сбросить отчет куда-попало
            // сначала я пытаюсь обратиться к сервлету слушателю события - запущен веб-контекст
            // этот сервлет написал я, в нем сохраняется ссылка на корень к веб-контексту
            String pathToContextRoot = null;
            try {
                pathToContextRoot = servletContext.getRealPath("/");
            } catch (Exception e) {
                //игнорирую исключения т.к. в общем случае наличие сервлета слушателя не обязательно
            }
            logger.info("get path to context root: "+pathToContextRoot);
            // получили путь к корню веб-контекста, и если он задан то новый путь вычисляется от его базы
            if (pathToContextRoot != null)
                rName = new File (pathToContextRoot , reportName);
            else{
                // в противном случае файл отчета будет создан во временном каталоге 
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
        // теперь можно запустить код анализа контекста spring
        logger.info("AnnoProcessor.runScanner");
        AnnoProcessor.runScanner((ApplicationContext) event.getSource(), rName, true, null);
    }
}
