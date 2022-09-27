package co.uk.bittwisted.ui;

import co.uk.bittwisted.ClickLessApp;
import co.uk.bittwisted.utils.ProcessHandler;

import javax.swing.*;
import javax.swing.border.EmptyBorder;
import javax.swing.filechooser.FileNameExtensionFilter;
import java.awt.*;
import java.io.File;

public class UIBuilder {
    private static final int padding = 20;
    private static final int textFieldHeight = 22;

    public static class UIWatchJobFolderSetup {
        public JPanel cardWatchFolderSetup;
        public JTextField txtWatchFolderPath;
        public JButton btnChooseWatchFolder;

        public UIWatchJobFolderSetup(JPanel cardWatchFolderSetup, JTextField txtWatchFolderPath, JButton btnChooseWatchFolder) {
            this.cardWatchFolderSetup = cardWatchFolderSetup;
            this.txtWatchFolderPath = txtWatchFolderPath;
            this.btnChooseWatchFolder = btnChooseWatchFolder;
        }
    }

    public static class UIWatchJobApplicationExeSetup {
        public JPanel cardApplicationExeSetup;
        public JTextField txtExeToRunPath;
        public JButton btnChooseApplicationExe;

        public UIWatchJobApplicationExeSetup(JPanel cardApplicationExeSetup, JTextField txtExeToRunPath, JButton btnChooseApplicationExe) {
            this.cardApplicationExeSetup = cardApplicationExeSetup;
            this.txtExeToRunPath = txtExeToRunPath;
            this.btnChooseApplicationExe = btnChooseApplicationExe;
        }
    }

    public static class UIWatchJobFileTypeSetup {
        public JPanel cardFileTypeSetup;
        public JLabel lblFileType;
        public JTextField txtFileTypes;
        public JButton btnAudioFileTypes;
        public JButton btnImageFileTypes;

        public UIWatchJobFileTypeSetup(JPanel cardFileTypeSetup, JLabel lblFileType, JTextField txtFileTypes, JButton btnAudioFileTypes, JButton btnImageFileTypes) {
            this.cardFileTypeSetup = cardFileTypeSetup;
            this.lblFileType = lblFileType;
            this.txtFileTypes = txtFileTypes;
            this.btnAudioFileTypes = btnAudioFileTypes;
            this.btnImageFileTypes = btnImageFileTypes;
        }
    }

    public static class UIWatchJobControls {
        public JPanel cardWatchJobControls;
        public JButton btnStart;
        public JButton btnDelete;

        public UIWatchJobControls(JPanel cardWatchJobControls, JButton btnStart, JButton btnDelete) {
            this.cardWatchJobControls = cardWatchJobControls;
            this.btnStart = btnStart;
            this.btnDelete = btnDelete;
        }
    }

    public static class UIWatchJobTitleSetup {
        public JPanel cardTitleSetup;
        public JTextField txtTitle;
        public JButton btnSet;

        public UIWatchJobTitleSetup(JPanel cardTitleSetup, JTextField txtTitle, JButton btnSet) {
            this.cardTitleSetup = cardTitleSetup;
            this.txtTitle = txtTitle;
            this.btnSet = btnSet;
        }
    }

    public static class UIWatchJobPreferencesSetup {
        public JPanel cardPreferencesSetup;
        public JCheckBox cbAutoStart;
        public JCheckBox cbWaitForAppToClose;
        public JSpinner spinPollingSeconds;

        public UIWatchJobPreferencesSetup(JPanel cardPreferencesSetup, JCheckBox cbAutoStart, JCheckBox cbWaitForAppToClose, JSpinner spinPollingSeconds) {
            this.cardPreferencesSetup = cardPreferencesSetup;
            this.cbAutoStart = cbAutoStart;
            this.cbWaitForAppToClose = cbWaitForAppToClose;
            this.spinPollingSeconds = spinPollingSeconds;
        }
    }

    public static class UIMenuOptions {
        public JPanel menuOptions;
        public JButton btnAdd;
        public JButton btnHide;
        public JButton btnExit;

        public UIMenuOptions(JPanel menuOptions, JButton btnAdd, JButton btnHide, JButton btnExit) {

            this.menuOptions = menuOptions;
            this.btnAdd = btnAdd;
            this.btnHide = btnHide;
            this.btnExit = btnExit;
        }
    }

    public static class UIPreferences {
        public JPanel cardPreferences;
        public JCheckBox startupOnLogin;
        public JCheckBox startupHidden;

        public UIPreferences(JPanel cardPreferences, JCheckBox startupOnLogin, JCheckBox startupHidden) {
            this.cardPreferences = cardPreferences;
            this.startupOnLogin = startupOnLogin;
            this.startupHidden = startupHidden;
        }
    }

    public static UIWatchJobPreferencesSetup buildPreferencesSetup(JFrame frame, boolean isAutoStart, boolean waitForAppToClose, long pollingTime) {
        GridBagConstraints c = new GridBagConstraints();
        JPanel cardPreferencesSetup = new JPanel(new GridBagLayout());
        cardPreferencesSetup.setBorder(new EmptyBorder(new Insets(padding/2, padding, padding/2, padding)));

        JLabel lblAutoStart = new JLabel("Auto start");
        lblAutoStart.setToolTipText("When the app is opened this job will be started automatically.");
        c.fill = GridBagConstraints.HORIZONTAL;
        c.gridx = 0;
        c.gridy = 0;
        c.insets = new Insets(0,0,0,10);
        cardPreferencesSetup.add(lblAutoStart, c);

        JCheckBox cbAutoStart = new JCheckBox();
        cbAutoStart.setSelected(isAutoStart);
        c.fill = GridBagConstraints.HORIZONTAL;
        c.gridx = 1;
        c.gridy = 0;
        c.insets = new Insets(0,0,0,10);
        cardPreferencesSetup.add(cbAutoStart, c);

        JLabel lblWaitForAppToClose = new JLabel("One app at a time");
        lblWaitForAppToClose.setToolTipText("Only one file will be opened at a time, until the app is closed the next file wont be opened.");
        c.fill = GridBagConstraints.HORIZONTAL;
        c.gridx = 2;
        c.gridy = 0;
        c.insets = new Insets(0,0,0,10);
        cardPreferencesSetup.add(lblWaitForAppToClose, c);

        JCheckBox cbWaitForAppToClose = new JCheckBox();
        cbWaitForAppToClose.setSelected(waitForAppToClose);
        c.fill = GridBagConstraints.HORIZONTAL;
        c.gridx = 3;
        c.gridy = 0;
        c.insets = new Insets(0,0,0,10);
        cardPreferencesSetup.add(cbWaitForAppToClose, c);

        JLabel lblPollingTime = new JLabel("Scan time (seconds)");
        lblPollingTime.setToolTipText("The scan time determines how long the watch folder is scanned in seconds.");
        c.fill = GridBagConstraints.HORIZONTAL;
        c.gridx = 4;
        c.gridy = 0;
        c.insets = new Insets(0,0,0,10);
        cardPreferencesSetup.add(lblPollingTime, c);

        SpinnerModel spinnerModel = new SpinnerNumberModel(pollingTime,1,60,1);
        JSpinner spinPollingSeconds = new JSpinner(spinnerModel);
        spinPollingSeconds.setPreferredSize(new Dimension(50, textFieldHeight));
        c.fill = GridBagConstraints.HORIZONTAL;
        c.gridx = 5;
        c.gridy = 0;
        c.insets = new Insets(0,0,0,0);
        cardPreferencesSetup.add(spinPollingSeconds, c);

        return new UIWatchJobPreferencesSetup(cardPreferencesSetup, cbAutoStart, cbWaitForAppToClose, spinPollingSeconds);
    }

    public static UIWatchJobTitleSetup buildWatchJobTitleSetup(JFrame frame, String title) {
        GridBagConstraints c = new GridBagConstraints();
        JPanel cardTitleSetup = new JPanel(new GridBagLayout());
        cardTitleSetup.setBorder(new EmptyBorder(new Insets(padding, padding, 0, padding)));

        JTextField txtTitle = new JTextField();
        txtTitle.setToolTipText("The name given for this job.");
        txtTitle.setEnabled(false);
        txtTitle.setText(title);
        txtTitle.setPreferredSize(new Dimension(200, textFieldHeight));
        c.fill = GridBagConstraints.HORIZONTAL;
        c.gridx = 0;
        c.gridy = 0;
        c.weightx = 0.9;
        c.insets = new Insets(0,0,0, 10);
        cardTitleSetup.add(txtTitle, c);

        JButton btnSet = new JButton("Rename");
        c.fill = GridBagConstraints.HORIZONTAL;
        c.gridx = 1;
        c.gridy = 0;
        c.weightx = 0.1;
        c.insets = new Insets(0,0,0, 0);
        cardTitleSetup.add(btnSet, c);

        return new UIWatchJobTitleSetup(cardTitleSetup, txtTitle, btnSet);
    }

    public static UIWatchJobFolderSetup buildWatchJobFolderSetup(JFrame frame, String watchJobFolderPath) {
        GridBagConstraints c = new GridBagConstraints();
        JPanel cardWatchFolderSetup = new JPanel(new GridBagLayout());
        cardWatchFolderSetup.setBorder(new EmptyBorder(new Insets(0, padding, 0, padding)));

        JTextField txtWatchFolderPath = new JTextField();
        txtWatchFolderPath.setEnabled(false);
        txtWatchFolderPath.setText(watchJobFolderPath);
        txtWatchFolderPath.setPreferredSize(new Dimension(200, textFieldHeight));
        c.fill = GridBagConstraints.HORIZONTAL;
        c.gridx = 0;
        c.gridy = 0;
        c.weightx = 0.9;
        c.insets = new Insets(0,0,0, 10);
        cardWatchFolderSetup.add(txtWatchFolderPath, c);

        JButton btnChooseWatchFolder = new JButton("Select");
        btnChooseWatchFolder.addActionListener(e -> {
            JFileChooser fileChooser = new JFileChooser();
            fileChooser.setFileSelectionMode(JFileChooser.DIRECTORIES_ONLY);
            int returnVal = fileChooser.showOpenDialog(frame);
            if (returnVal == JFileChooser.APPROVE_OPTION) {
                File file = fileChooser.getSelectedFile();
                txtWatchFolderPath.setText(file.getAbsolutePath());
            }
        });
        c.fill = GridBagConstraints.HORIZONTAL;
        c.gridx = 1;
        c.gridy = 0;
        c.weightx = 0.1;
        c.insets = new Insets(0,0,0, 0);
        cardWatchFolderSetup.add(btnChooseWatchFolder, c);

        return new UIWatchJobFolderSetup(cardWatchFolderSetup, txtWatchFolderPath, btnChooseWatchFolder);
    }

    public static UIWatchJobApplicationExeSetup buildWatchJobApplicationExeSetup(JFrame frame, String exeToRunPath) {
        GridBagConstraints c = new GridBagConstraints();
        JPanel cardApplicationExeSetup = new JPanel(new GridBagLayout());
        cardApplicationExeSetup.setBorder(new EmptyBorder(new Insets(padding/2, padding, 0, padding)));

        JTextField txtExeToRunPath = new JTextField();
        txtExeToRunPath.setEnabled(false);
        txtExeToRunPath.setPreferredSize(new Dimension(200, textFieldHeight));
        txtExeToRunPath.setText(exeToRunPath);
        c.fill = GridBagConstraints.HORIZONTAL;
        c.gridx = 0;
        c.gridy = 0;
        c.weightx = 0.9;
        c.insets = new Insets(0,0,0, 10);
        cardApplicationExeSetup.add(txtExeToRunPath, c);

        JButton btnChooseApplicationExe = new JButton("Select");
        btnChooseApplicationExe.addActionListener(e -> {
            FileNameExtensionFilter filter = new FileNameExtensionFilter("Application", "exe");
            JFileChooser fileChooser = new JFileChooser();
            if(ProcessHandler.isWindows) fileChooser.setFileFilter(filter);
            fileChooser.setFileSelectionMode(JFileChooser.FILES_ONLY);
            int returnVal = fileChooser.showOpenDialog(frame);
            if (returnVal == JFileChooser.APPROVE_OPTION) {
                File file = fileChooser.getSelectedFile();
                txtExeToRunPath.setText(file.getAbsolutePath());
            }
        });
        c.fill = GridBagConstraints.HORIZONTAL;
        c.gridx = 1;
        c.gridy = 0;
        c.weightx = 0.1;
        c.insets = new Insets(0,0,0, 0);
        cardApplicationExeSetup.add(btnChooseApplicationExe, c);

        return new UIWatchJobApplicationExeSetup(cardApplicationExeSetup, txtExeToRunPath, btnChooseApplicationExe);
    }

    public static UIWatchJobFileTypeSetup buildWatchJobFileTypeSetup(JFrame frame) {
        GridBagConstraints c = new GridBagConstraints();
        JPanel cardFileTypeSetup = new JPanel(new GridBagLayout());
        cardFileTypeSetup.setBorder(new EmptyBorder(new Insets(padding/2, padding, 0, padding)));

        JLabel lblFileType = new JLabel("Scan for file types");
        c.fill = GridBagConstraints.HORIZONTAL;
        c.gridx = 0;
        c.gridy = 0;
        c.weightx = 0.5;
        c.insets = new Insets(0,0,0, 10);
        cardFileTypeSetup.add(lblFileType, c);

        JTextField txtFileTypes = new JTextField();
        txtFileTypes.setPreferredSize(new Dimension(390, textFieldHeight));

        JButton btnAudioFileTypes = new JButton("Audio");
        btnAudioFileTypes.addActionListener(e -> txtFileTypes.setText(fileTypesText(ClickLessApp.defaultAudioExtensions)));
        c.fill = GridBagConstraints.HORIZONTAL;
        c.gridx = 1;
        c.gridy = 0;
        c.weightx = 0.2;
        c.insets = new Insets(0,0,0, 10);
        cardFileTypeSetup.add(btnAudioFileTypes, c);

        JButton btnImageFileTypes = new JButton("Image");
        btnImageFileTypes.addActionListener(e -> txtFileTypes.setText(fileTypesText(ClickLessApp.defaultImageExtensions)));
        c.fill = GridBagConstraints.HORIZONTAL;
        c.gridx = 2;
        c.gridy = 0;
        c.weightx = 0.2;
        c.insets = new Insets(0,0,0, 0);
        cardFileTypeSetup.add(btnImageFileTypes, c);

        return new UIWatchJobFileTypeSetup(cardFileTypeSetup, lblFileType, txtFileTypes, btnAudioFileTypes, btnImageFileTypes);
    }

    public static UIWatchJobControls buildWatchJobControls(JFrame frame) {
        GridBagConstraints c = new GridBagConstraints();
        JPanel cardWatchJobControls = new JPanel(new GridBagLayout());
        cardWatchJobControls.setBorder(new EmptyBorder(new Insets(0, padding, 0, padding)));

        JButton btnStart = new JButton("Start");
        c.fill = GridBagConstraints.HORIZONTAL;
        c.gridx = 0;
        c.gridy = 0;
        c.weightx = 0.5;
        c.insets = new Insets(10,0,0,10);
        cardWatchJobControls.add(btnStart, c);

        JButton btnDelete = new JButton("Delete");
        c.fill = GridBagConstraints.HORIZONTAL;
        c.gridx = 1;
        c.gridy = 0;
        c.weightx = 0.5;
        c.insets = new Insets(10,0,0,0);
        cardWatchJobControls.add(btnDelete, c);

        return new UIWatchJobControls(cardWatchJobControls, btnStart, btnDelete);
    }

    public static UIMenuOptions buildMenuOptions(JFrame frame) {
        GridBagConstraints c = new GridBagConstraints();
        JPanel menuOptions = new JPanel(new GridBagLayout());
        menuOptions.setBorder(BorderFactory.createLineBorder(Color.gray, 2, true));

        menuOptions.add(Box.createRigidArea(new Dimension(200, padding)));

        JButton btnAdd = new JButton("Add Job");
        btnAdd.setSize(new Dimension(80, padding));
        c.fill = GridBagConstraints.HORIZONTAL;
        c.gridx = 0;
        c.gridy = 0;
        c.weightx = 0.3;
        c.insets = new Insets(10,10,10, 0);
        menuOptions.add(btnAdd, c);

        menuOptions.add(Box.createRigidArea(new Dimension(padding, padding)));

        JButton btnHide = new JButton("Hide");
        btnHide.setSize(new Dimension(80, padding));
        c.fill = GridBagConstraints.HORIZONTAL;
        c.gridx = 1;
        c.gridy = 0;
        c.weightx = 0.3;
        c.insets = new Insets(10,10,10, 0);
        menuOptions.add(btnHide, c);

        menuOptions.add(Box.createRigidArea(new Dimension(padding, padding)));

        JButton btnExit = new JButton("Exit");
        btnExit.setSize(new Dimension(80, padding));
        c.fill = GridBagConstraints.HORIZONTAL;
        c.gridx = 2;
        c.gridy = 0;
        c.weightx = 0.3;
        c.insets = new Insets(10,10,10, 10);
        menuOptions.add(btnExit, c);

        return new UIMenuOptions(menuOptions, btnAdd, btnHide, btnExit);
    }

    public static UIPreferences buildPreferences(JFrame frame, boolean startupLogin, boolean startupHidden) {
        GridBagConstraints c = new GridBagConstraints();
        JPanel cardPreferences = new JPanel(new GridBagLayout());
        cardPreferences.setBorder(BorderFactory.createLineBorder(new Color(180, 195, 196, 37), 2, true));

        JLabel lblStarupOnLogin = new JLabel("Startup on login");
        lblStarupOnLogin.setToolTipText("Open app on windows startup.");
        lblStarupOnLogin.setBorder(new EmptyBorder(new Insets(padding/2, padding/2, padding/2, 0)));
        c.fill = GridBagConstraints.HORIZONTAL;
        c.gridx = 0;
        c.gridy = 0;
        c.insets = new Insets(0,0,0,10);
        cardPreferences.add(lblStarupOnLogin, c);

        JCheckBox cbStartupOnLogin = new JCheckBox();
        cbStartupOnLogin.setSelected(startupLogin);
        c.fill = GridBagConstraints.HORIZONTAL;
        c.gridx = 1;
        c.gridy = 0;
        c.insets = new Insets(0,0,0,10);
        cardPreferences.add(cbStartupOnLogin, c);

        JLabel lblStartHidden = new JLabel("Startup hidden");
        lblStartHidden.setToolTipText("Minimize app to system tray on startup.");
        c.fill = GridBagConstraints.HORIZONTAL;
        c.gridx = 2;
        c.gridy = 0;
        c.insets = new Insets(0,0,0,10);
        cardPreferences.add(lblStartHidden, c);

        JCheckBox cbStartHidden = new JCheckBox();
        cbStartHidden.setSelected(startupHidden);
        c.fill = GridBagConstraints.HORIZONTAL;
        c.gridx = 3;
        c.gridy = 0;
        c.insets = new Insets(0,0,0,10);
        cardPreferences.add(cbStartHidden, c);

        return new UIPreferences(cardPreferences, cbStartupOnLogin, cbStartHidden);
    }

    public static String fileTypesText(String[] arrFileTypes) {
        StringBuilder fileTypes = new StringBuilder();
        for(String fileType : arrFileTypes) {
            fileTypes.append(fileType).append(";");
        }
        return fileTypes.toString();
    }

    public static void enableWatchJobUI(JButton btnChooseWatchFolder, JButton btnChooseApplicationExe, JTextField txtFileTypes, JButton btnAudioFileTypes, JButton btnImageFileTypes, JButton btnSet, JSpinner spinPollingTime) {
        btnAudioFileTypes.setEnabled(true);
        btnImageFileTypes.setEnabled(true);
        txtFileTypes.setEnabled(true);
        btnChooseWatchFolder.setEnabled(true);
        btnChooseApplicationExe.setEnabled(true);
        btnSet.setEnabled(true);
        spinPollingTime.setEnabled(true);
    }

    public static void disableWatchJobUI(JButton btnChooseWatchFolder, JButton btnChooseApplicationExe, JTextField txtFileTypes, JButton btnAudioFileTypes, JButton btnImageFileTypes, JButton btnSet, JSpinner spinPollingTime) {
        btnAudioFileTypes.setEnabled(false);
        btnImageFileTypes.setEnabled(false);
        txtFileTypes.setEnabled(false);
        btnChooseWatchFolder.setEnabled(false);
        btnChooseApplicationExe.setEnabled(false);
        btnSet.setEnabled(false);
        spinPollingTime.setEnabled(false);
    }
}
