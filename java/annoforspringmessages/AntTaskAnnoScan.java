package blz.server.utils.anno;

import org.apache.tools.ant.BuildException;
import org.apache.tools.ant.Task;
import org.springframework.context.support.FileSystemXmlApplicationContext;

import java.io.File;
import java.io.IOException;
import java.util.logging.Logger;


/**
 * ����� �������������� ����� ���������� ��� ��������� ��������� ��� ������������� ���
 * � ������ ����� ant
 */
public class AntTaskAnnoScan extends Task {

    private static Logger logger = Logger.getLogger(AntTaskAnnoScan.class.getName());

    /**
     * ������� ���� ����� �� ���������� �������� ��������� ���������� ant-������� � ������
     * ���� ��� ������ ������� �� ���� �������
     */
    Boolean failOnErrors = false;
    /**
     * ��� �������� ����� (����� � ���������� spring)
     */
    File targetFileName = null;
    /**
     * ��� ����� ���� ����� ��������� ����� �� ���������� ������������
     */
    File pathToSpringFile = null;

    /**
     * ��� �������� ��������� ���� ����� �������, ������ ���� ����� ������������ �������� ������ ����������� �������� ��������
     */
    File pathToBaseResourcesDir = null;

    /**
     * ������� ����, ��� ����� ��������� ����� � �������� �������� �� ���� ���������,
     * � ��������� ������ ����� ������������ �������� �������������� � ������� ResourceBundle
     */
    Boolean useContext = true;


    public void setFailOnErrors(Boolean failOnErrors) {
        this.failOnErrors = failOnErrors;
    }

    public void setTargetFileName(File targetFileName) {
        this.targetFileName = targetFileName;
    }

    public void setPathToSpringFile(File pathToSpringFile) {
        this.pathToSpringFile = pathToSpringFile;
    }

    public void setPathToBaseResourcesDir(File pathToBaseResourcesDir) {
        this.pathToBaseResourcesDir = pathToBaseResourcesDir;
    }

    public void setUseContext(Boolean useContext) {
        this.useContext = useContext;
    }


    /**
     * ��������������� �����, ������ ��� �������� ����, ��� ��� ������ ��� ������ task-� ���� ���� �����������  
     */
    private void testFields() {
        logger.info("AntTaskAnnoScan.testFields starts test for properties");
        if (targetFileName == null)
            throw new IllegalArgumentException("property 'targetFileName' is mandatory");
        if (pathToSpringFile == null)
            throw new IllegalArgumentException("property 'pathToSpringFile' is mandatory");
        if (useContext == false && pathToBaseResourcesDir == null)
            throw new IllegalArgumentException("property 'pathToBaseResourcesDir' is mandatory in 'useContext == false' mode ");

        logger.info("AntTaskAnnoScan.testFields finished test for properties");
    }

    /**
     * ������ ant-���� �� ���������� 
     * @throws IOException
     */
    public void execute() throws BuildException {
        logger.info("AntTaskAnnoScan.execute preparing ...");
        try {
            innerExecute();
        } catch (IOException e) {
            throw new BuildException (e);
        }

    }

    /**
     * ��������������� �����, ���������� � �������������� �������� �� ������������ ��������
     * @throws IOException
     */
    private void innerExecute() throws IOException {
        testFields();
        final String inputURL = pathToSpringFile.toURI().toURL().toExternalForm();
        final String targetURL = targetFileName.toURI().toURL().toExternalForm();

        logger.info("AntTaskAnnoScan.execute use inputURL = "+ inputURL);
        logger.info("AntTaskAnnoScan.execute use targetURL = "+ targetURL);

        final ClassLoader loader = getClass().getClassLoader();
        FileSystemXmlApplicationContext context = new FileSystemXmlApplicationContext(new String[]{inputURL}, false);
        context.setClassLoader(loader);
        context.refresh();


        logger.info("AntTaskAnnoScan.execute context loaded = "+ context);

        final boolean status = AnnoProcessor.runScanner(context, targetFileName, useContext, pathToBaseResourcesDir);

        logger.info("AntTaskAnnoScan.execute scanner finished "+ status);

        if (status == false && failOnErrors == true)
            throw new RuntimeException ("missing resources were found");

        
    }

}
