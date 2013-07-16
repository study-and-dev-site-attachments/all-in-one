import javax.swing.*;
import javax.swing.event.ChangeEvent;
import javax.swing.event.ChangeListener;
import javax.swing.filechooser.FileNameExtensionFilter;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.io.File;
import java.io.FileFilter;
import java.nio.charset.Charset;
import java.util.*;
import java.beans.PropertyChangeListener;
import java.beans.PropertyChangeEvent;


public class CSSCompactorForm {
    public JPanel getPaneroot() {
        return paneroot;
    }

    private JPanel paneroot;
    private JTextField txtOnlyFileForProcess;
    private JButton btnSelectFile_Input;
    private JProgressBar progressBar_Process;
    private JRadioButton radio_onlySelectedFile;
    private JRadioButton radio_entireDir;
    private JButton btnSelectCatalog_Input;
    private JTextField txtCatalogForProcess;
    private JCheckBox replaceMultipleSpacesWithCheckBox;
    private JCheckBox removeSpaceAroundCharsCheckBox;
    private JCheckBox leaveSpaceBetweenSelectorsCheckBox;
    private JButton btnGo;
    private JTextField txtSaveResultFileAs;
    private JTextField txtPutResultFilesInto;
    private JButton btnSelectFile_Output;
    private JButton btnSelectCatalog_Output;
    private JLabel labSaveProcessedFileAs;
    private JLabel labSaveProcessedCatalogAs;
    private JCheckBox removeTabsCheckBox;
    private JRadioButton leaveLinesAsTheyRadioButton;
    private JRadioButton replaceMultipleEmptyLinesRadioButton;
    private JRadioButton removeAllNewlinesRadioButton;
    private JRadioButton stripALLCommentsRadioButton;
    private JRadioButton stripCommentsAtLeastRadioButton;
    private JTextField a4TextField;
    private JCheckBox compressColorCodesWhereCheckBox;
    private JCheckBox removeUnnecessarySemiColonsCheckBox;
    private JCheckBox oneCommandPerLineCheckBox;
    private JRadioButton donTStripAnyRadioButton;
    private JComboBox combo_input_charset;
    private JComboBox combo_output_charset;
    private JCheckBox leaveSpaceBetweenPropertiesCheckBox;
    private JCheckBox skipCompressOnlyChangeCheckBox;

    public CSSCompactorForm() {
        radio_onlySelectedFile.addChangeListener(new ChangeListener() {
            public void stateChanged(ChangeEvent e) {
                onModifyRadio_WhatProcess(e);
            }
        });
        btnSelectFile_Input.addActionListener(new ActionListener() {
            public void actionPerformed(ActionEvent e) {
                invokeSelectSingleFileDialog(txtOnlyFileForProcess);
            }
        });
        btnSelectCatalog_Input.addActionListener(new ActionListener() {
            public void actionPerformed(ActionEvent e) {
                invokeSelectCatalogFileDialog(txtCatalogForProcess);
            }
        });
        btnSelectFile_Output.addActionListener(new ActionListener() {
            public void actionPerformed(ActionEvent e) {
                invokeSelectSingleFileDialog(txtSaveResultFileAs);
            }
        });
        btnSelectCatalog_Output.addActionListener(new ActionListener() {
            public void actionPerformed(ActionEvent e) {
                invokeSelectCatalogFileDialog(txtPutResultFilesInto);
            }
        });
        btnGo.addActionListener(new ActionListener() {
            public void actionPerformed(ActionEvent e) {
                invokeCSSCompactor();
            }
        });
        stripCommentsAtLeastRadioButton.addChangeListener(new ChangeListener() {
            public void stateChanged(ChangeEvent e) {
                onChangeCSSCommentStripParam(e);
            }
        });
        radio_entireDir.addChangeListener(new ChangeListener() {
            public void stateChanged(ChangeEvent e) {
                onModifyRadio_WhatProcess(e);
            }
        });
        skipCompressOnlyChangeCheckBox.addChangeListener(new ChangeListener() {
            public void stateChanged(ChangeEvent e) {
                onModifyGeneralProcessMode (e);
            }
        });
        txtCatalogForProcess.addPropertyChangeListener(new PropertyChangeListener() {
            public void propertyChange(PropertyChangeEvent evt) {
                if (evt.getPropertyName().equals("text")){
                    invokeTestButtonGoStatus();
                }
            }
        });
        txtOnlyFileForProcess.addPropertyChangeListener(new PropertyChangeListener() {

            public void propertyChange(PropertyChangeEvent evt) {
                if (evt.getPropertyName().equals("text")){
                    invokeTestButtonGoStatus();
                }
            }
        });
    }

    private void onModifyGeneralProcessMode(ChangeEvent e) {
        replaceMultipleSpacesWithCheckBox.setEnabled(! skipCompressOnlyChangeCheckBox.isSelected());
        removeSpaceAroundCharsCheckBox.setEnabled(! skipCompressOnlyChangeCheckBox.isSelected());
        leaveSpaceBetweenSelectorsCheckBox.setEnabled(! skipCompressOnlyChangeCheckBox.isSelected());
        leaveSpaceBetweenPropertiesCheckBox.setEnabled(! skipCompressOnlyChangeCheckBox.isSelected());
        removeTabsCheckBox.setEnabled(! skipCompressOnlyChangeCheckBox.isSelected());
        donTStripAnyRadioButton.setEnabled(! skipCompressOnlyChangeCheckBox.isSelected());
        stripALLCommentsRadioButton.setEnabled(! skipCompressOnlyChangeCheckBox.isSelected());
        stripCommentsAtLeastRadioButton.setEnabled(! skipCompressOnlyChangeCheckBox.isSelected());
        a4TextField.setEnabled(! skipCompressOnlyChangeCheckBox.isSelected());
        leaveLinesAsTheyRadioButton.setEnabled(! skipCompressOnlyChangeCheckBox.isSelected());
        replaceMultipleEmptyLinesRadioButton.setEnabled(! skipCompressOnlyChangeCheckBox.isSelected());
        removeAllNewlinesRadioButton.setEnabled(! skipCompressOnlyChangeCheckBox.isSelected());
        compressColorCodesWhereCheckBox.setEnabled(! skipCompressOnlyChangeCheckBox.isSelected());
        removeUnnecessarySemiColonsCheckBox.setEnabled(! skipCompressOnlyChangeCheckBox.isSelected());
        oneCommandPerLineCheckBox.setEnabled(! skipCompressOnlyChangeCheckBox.isSelected());
    }

    private void onChangeCSSCommentStripParam(ChangeEvent e) {
        a4TextField.setEnabled(stripCommentsAtLeastRadioButton.isSelected());
    }

    private void invokeCSSCompactor() {
        final Map<File, File> alist = new HashMap<File, File>();
        if (radio_onlySelectedFile.isSelected()) {
            alist.put(new File(txtOnlyFileForProcess.getText()), new File(txtSaveResultFileAs.getText()));
        } else {
            File[] listAll = new File(txtCatalogForProcess.getText()).listFiles(
                    new FileFilter() {
                        public boolean accept(File pathname) {
                            return pathname.getName().endsWith(".css");
                        }
                    }
            );
            for (int i = 0; i < listAll.length; i++) {
                File file = listAll[i];
                alist.put(file, new File(txtPutResultFilesInto.getText(), file.getName()));
            }
        }

        final Properties compset = new Properties();
        compset.put(CSSCompactorAction.SKIP_COMPRESS_ONLY_CHANGE_CHARSET, skipCompressOnlyChangeCheckBox.isSelected());

        compset.put(CSSCompactorAction.REPLACE_MULTIPLE_SPACES_WITH_JUST_ONE_SPACE, replaceMultipleSpacesWithCheckBox.isSelected());
        compset.put(CSSCompactorAction.REMOVE_SPACE_AROUND_CHARS, removeSpaceAroundCharsCheckBox.isSelected());
        compset.put(CSSCompactorAction.LEAVE_SPACE_BETWEEN_SELECTORS, leaveSpaceBetweenSelectorsCheckBox.isSelected());
        compset.put(CSSCompactorAction.LEAVE_SPACE_BETWEEN_PROPERTIES, leaveSpaceBetweenPropertiesCheckBox.isSelected());
        compset.put(CSSCompactorAction.REMOVE_TABS, removeTabsCheckBox.isSelected());
        compset.put(CSSCompactorAction.DONT_STRIP_ANY_COMMENTS, donTStripAnyRadioButton.isSelected());
        compset.put(CSSCompactorAction.STRIP_ALL_COMMENTS, stripALLCommentsRadioButton.isSelected());
        compset.put(CSSCompactorAction.STRIP_COMMENTS_AT_LEAST_CHARS_LONG, stripCommentsAtLeastRadioButton.isSelected());
        if (stripCommentsAtLeastRadioButton.isSelected()){
            int pi = 0;
            try {
                pi = Integer.parseInt(a4TextField.getText());
            } catch (NumberFormatException e) {}
            if (pi <= 0){
                JOptionPane.showMessageDialog(paneroot, "error: VALUE_FOR_STRIP_COMMENTS_AT_LEAST_CHARS_LONG is invalid (must be bigger than zero)");
                return;
            }
        }
        compset.put(CSSCompactorAction.VALUE_FOR_STRIP_COMMENTS_AT_LEAST_CHARS_LONG, a4TextField.getText());
        compset.put(CSSCompactorAction.LEAVE_LINES_AS_THEY_ARE, leaveLinesAsTheyRadioButton.isSelected());
        compset.put(CSSCompactorAction.REPLACE_MULTIPLE_EMPTY_LINES_WITH_JUST_ONE_EMPTY_LINE, replaceMultipleEmptyLinesRadioButton.isSelected());
        compset.put(CSSCompactorAction.REMOVE_ALL_NEWLINES, removeAllNewlinesRadioButton.isSelected());
        compset.put(CSSCompactorAction.COMPRESS_COLOR_CODES_WHERE_POSSIBLE, compressColorCodesWhereCheckBox.isSelected());
        compset.put(CSSCompactorAction.REMOVE_UNNECESSARY_SEMI_COLONS, removeUnnecessarySemiColonsCheckBox.isSelected());
        compset.put(CSSCompactorAction.ONE_COMMAND_PER_LINE, oneCommandPerLineCheckBox.isSelected());

        compset.put(CSSCompactorAction.CHARSET_IN, combo_input_charset.getSelectedItem().toString());
        compset.put(CSSCompactorAction.CHARSET_OUT, combo_output_charset.getSelectedItem().toString());


        SwingUtilities.invokeLater(
                new Runnable() {
                    public void run() {
                        paneroot.setEnabled(false);
                        progressBar_Process.setValue(0);
                        progressBar_Process.setMaximum(alist.size());
                        new CSSCompactorAction().execute(alist, compset, new ActionListener() {
                            public void actionPerformed(ActionEvent e) {
                                if (e.getActionCommand().equals("CSSCOMPACTOR_NEXT_FILE")){
                                    progressBar_Process.setValue(((LiveArray)e.getSource()).getInteger(0));
                                }
                                if (e.getActionCommand().equals("CSSCOMPACTOR_FINISHED")){
                                    paneroot.setEnabled(true);
                                    JOptionPane.showMessageDialog(paneroot, "CSS compactor has completed task: statistics:\n"+
                                    "processed files: " + ((LiveArray) e.getSource()).getInteger(0)+"\n"+
                                    "original size all files : " + ((LiveArray) e.getSource()).getInteger(1)+"\n"+
                                    "compressed size all files : " + ((LiveArray) e.getSource()).getInteger(2)+"\n"
                                    );
                                    progressBar_Process.setValue(progressBar_Process.getMaximum());
                                }
                            }
                        });
                    }
                }
        );


    }

    private void invokeSelectSingleFileDialog(JTextField anyTxtControl) {
        JFileChooser jfc = new JFileChooser(anyTxtControl.getText());
        FileNameExtensionFilter filter = new FileNameExtensionFilter("CSS Files", "css");
        jfc.setFileFilter(filter);
        jfc.setFileSelectionMode(JFileChooser.FILES_ONLY);
        int returnVal = jfc.showOpenDialog(null);
        if (returnVal == JFileChooser.APPROVE_OPTION) {
            anyTxtControl.setText(jfc.getSelectedFile().getAbsolutePath());
            invokeTestButtonGoStatus();
        }
    }

    private void invokeSelectCatalogFileDialog(JTextField anyTxtControl) {
        JFileChooser jfc = new JFileChooser(anyTxtControl.getText());
        FileNameExtensionFilter filter = new FileNameExtensionFilter("CSS Files", "css");
        jfc.setFileFilter(filter);
        jfc.setFileSelectionMode(JFileChooser.DIRECTORIES_ONLY);
        int returnVal = jfc.showOpenDialog(null);
        if (returnVal == JFileChooser.APPROVE_OPTION) {
            anyTxtControl.setText(jfc.getSelectedFile().getAbsolutePath());
            invokeTestButtonGoStatus();
        }
    }


    private void onModifyRadio_WhatProcess(ChangeEvent e) {
        txtOnlyFileForProcess.setEnabled(radio_onlySelectedFile.isSelected());
        btnSelectFile_Input.setEnabled(radio_onlySelectedFile.isSelected());
        labSaveProcessedFileAs.setEnabled(radio_onlySelectedFile.isSelected());
        txtSaveResultFileAs.setEnabled(radio_onlySelectedFile.isSelected());
        btnSelectFile_Output.setEnabled(radio_onlySelectedFile.isSelected());


        labSaveProcessedCatalogAs.setEnabled(radio_entireDir.isSelected());
        btnSelectCatalog_Input.setEnabled(radio_entireDir.isSelected());
        txtPutResultFilesInto.setEnabled(radio_entireDir.isSelected());
        btnSelectCatalog_Output.setEnabled(radio_entireDir.isSelected());
        txtCatalogForProcess.setEnabled(radio_entireDir.isSelected());

        invokeTestButtonGoStatus();
    }

    private void invokeTestButtonGoStatus() {
        btnGo.setEnabled(false);
        if (radio_onlySelectedFile.isSelected()) {
            if ((!txtOnlyFileForProcess.getText().isEmpty()) && (!txtSaveResultFileAs.getText().isEmpty())) {
                if (new File(txtOnlyFileForProcess.getText()).exists()) {
                    btnGo.setEnabled(true);
                }
            }
        }
        if (radio_entireDir.isSelected()) {
            if ((!txtCatalogForProcess.getText().isEmpty()) && (!txtPutResultFilesInto.getText().isEmpty())) {
                if (new File(txtCatalogForProcess.getText()).exists() && new File(txtPutResultFilesInto.getText()).exists()) {
                    btnGo.setEnabled(true);
                }
            }
        }

    }


    public void postCreateSteps() {
        onModifyRadio_WhatProcess(null);
        onChangeCSSCommentStripParam(null);
        onModifyGeneralProcessMode (null);
        SortedMap<String, Charset> map = Charset.availableCharsets();
        Set<String> ks = map.keySet();
        String objUtf8 = null;
        for (String k : ks) {
            combo_input_charset.addItem(k);
            combo_output_charset.addItem(k);
            if (k.equalsIgnoreCase("utf-8"))
                objUtf8 = k;
        }// ---- for ----
        combo_input_charset.setSelectedItem(objUtf8);
        combo_output_charset.setSelectedItem(objUtf8);
    }

}
