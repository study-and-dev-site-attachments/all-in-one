package compareloading;

import javax.swing.*;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.awt.*;
import java.io.*;

/**
 * main class for app. contains GUI code & main method
 */
public class CompareLoadingDialog {
    private JPanel paneRoot;
    private JButton btnBrowse1;
    private JTextField txtBrowse1;
    private JTextField txtBrowse2;
    private JButton btnBrowse2;
    private JButton btnCompare;
    private JTabbedPane tabbedPane1;
    private JPanel paneFile1Log;
    private JPanel paneFile2Log;
    private JPanel paneCommonClasses;
    private JPanel paneExistsIn1Not2;
    private JPanel paneExistsIn2Not1;
    private JPanel paneDifferentStorages;
    private JProgressBar progressBar;

    StatusTablePair status_file1 = new StatusTablePair();
    StatusTablePair status_file2 = new StatusTablePair();
    StatusTablePair status_common = new StatusTablePair();
    StatusTablePair status_in_1_not_2 = new StatusTablePair();
    StatusTablePair status_in_2_not_1 = new StatusTablePair();
    StatusTableTroika status_different = new StatusTableTroika();

    public CompareLoadingDialog() {
    }

    /**
     * unified method which show 'browse file' dialog
     *
     * @param initial current file name
     * @return
     */
    private File getBrowsedFile(String initial) {
        JFileChooser chooser = new JFileChooser();
        try {
            if (initial != null && !"".equals(initial))
                chooser.setSelectedFile(new File(initial));
        } catch (Exception e) {
        }
        int returnVal = chooser.showOpenDialog(paneRoot);
        if (returnVal == JFileChooser.APPROVE_OPTION) {
            return chooser.getSelectedFile();
        }
        return null;
    }

    /**
     * event handler for 'start analyze' button
     */
    private void doCompare() {
        final File f1 = new File(txtBrowse1.getText());
        final File f2 = new File(txtBrowse2.getText());
        // disable 'start analyze' button
        btnCompare.setEnabled(false);
        progressBar.setIndeterminate(true);
        // this process may take valuable time so i am reqyuired to run this procedure in separate thread
        new Thread() {
            public void run() {
                CompareUtils.CompareContainer container = null;
                try {
                    // call helper method
                    container = CompareUtils.compare(getFileContent(f1), getFileContent(f2));
                    // double variable is required for call from anonymous thread
                    final CompareUtils.CompareContainer container1 = container;
                    SwingUtilities.invokeLater(
                            new Runnable() {
                                public void run() {
                                    status_file1.fillData(container1.pairs1);
                                    status_file2.fillData(container1.pairs2);
                                    status_common.fillData(container1.pairs_common);
                                    status_in_1_not_2.fillData(container1.in_1_not_in_2);
                                    status_in_2_not_1.fillData(container1.in_2_not_in_1);
                                    status_different.fillData(container1.different_files_for_one_clazz);
                                }
                            }
                    );
                } catch (Exception e) {
                    e.printStackTrace();
                }
                finally {
                    //
                    SwingUtilities.invokeLater(
                            new Runnable() {
                                public void run() {
                                    btnCompare.setEnabled(true);
                                    progressBar.setIndeterminate(false);
                                }
                            }
                    );
                }
            }
        }.start();

    }

    public void createUIComponents() {
        // build UI
        paneRoot = new JPanel(new BorderLayout());
        final JPanel paneToolBar = new JPanel(new FlowLayout());
        paneRoot.add(paneToolBar, BorderLayout.NORTH);

        // filling top panel with buttons & text fields
        paneToolBar.add(txtBrowse1 = new JTextField());
        paneToolBar.add(btnBrowse1 = new JButton("Browse"));
        paneToolBar.add(txtBrowse2 = new JTextField());
        paneToolBar.add(btnBrowse2 = new JButton("Browse"));
        paneToolBar.add(btnCompare = new JButton("Compare"));

        Dimension fieldsSize = new Dimension(250, 24);
        txtBrowse1.setMinimumSize(fieldsSize);
        txtBrowse2.setMinimumSize(fieldsSize);
        txtBrowse1.setPreferredSize(fieldsSize);
        txtBrowse2.setPreferredSize(fieldsSize);

        paneRoot.add(tabbedPane1 = new JTabbedPane(), BorderLayout.CENTER);

        tabbedPane1.addTab("File 1 Clazz log", paneFile1Log = new JPanel(new BorderLayout(0, 0)));
        tabbedPane1.addTab("File 2 Clazz log", paneFile2Log = new JPanel(new BorderLayout(0, 0)));

        tabbedPane1.addTab("Common Classes", paneCommonClasses = new JPanel(new BorderLayout(0, 0)));
        tabbedPane1.addTab("In 1 not 2", paneExistsIn1Not2 = new JPanel(new BorderLayout(0, 0)));
        tabbedPane1.addTab("In 2 not 1", paneExistsIn2Not1 = new JPanel(new BorderLayout(0, 0)));
        tabbedPane1.addTab("Different class storages", paneDifferentStorages = new JPanel(new BorderLayout(0, 0)));
        progressBar = new JProgressBar();
        progressBar.setIndeterminate(false);
        paneRoot.add(progressBar, BorderLayout.SOUTH);

        // attach event listeners
        btnBrowse1.addActionListener(new ActionListener() {
            public void actionPerformed(ActionEvent e) {
                File f = getBrowsedFile(txtBrowse1.getText());
                if (f != null)
                    txtBrowse1.setText(f.getAbsolutePath());
            }
        });
        btnBrowse2.addActionListener(new ActionListener() {
            public void actionPerformed(ActionEvent e) {
                File f = getBrowsedFile(txtBrowse2.getText());
                if (f != null)
                    txtBrowse2.setText(f.getAbsolutePath());
            }
        });
        btnCompare.addActionListener(new ActionListener() {
            public void actionPerformed(ActionEvent e) {
                doCompare();
            }
        });
        // attach special components
        paneFile1Log.add(status_file1);
        paneFile2Log.add(status_file2);
        paneCommonClasses.add(status_common);
        paneExistsIn1Not2.add(status_in_1_not_2);
        paneExistsIn2Not1.add(status_in_2_not_1);
        paneDifferentStorages.add(status_different);
    }

    /**
     * reads entire file content
     *
     * @param f file to read from
     * @return file contents
     */
    private static String getFileContent(File f) {
        StringBuilder buf = new StringBuilder();
        try {

            BufferedReader brin = new BufferedReader(new InputStreamReader(new FileInputStream(f)));
            String line;
            while ((line = brin.readLine()) != null) {
                buf.append(line).append('\n');
            }
            brin.close();
        } catch (IOException e) {
            // ignore error
            e.printStackTrace();
        }
        return buf.toString();
    }

    public static void main(String[] args) {
        JFrame jf = new JFrame("Uncover class loading details (by black zorro, http://black-zorro.com)");
        jf.setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);
        CompareLoadingDialog dialog = new CompareLoadingDialog();
        dialog.createUIComponents();
        jf.getContentPane().add(dialog.paneRoot);
        jf.setSize(800, 640);
        jf.setVisible(true);
    }

}
