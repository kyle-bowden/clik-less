package co.uk.bittwisted;

import co.uk.bittwisted.domain.WatchJob;
import co.uk.bittwisted.exceptions.NoExeFileSpecifiedException;
import co.uk.bittwisted.exceptions.NoWatchFolderSpecifiedException;
import co.uk.bittwisted.exceptions.WatchJobServiceException;
import co.uk.bittwisted.services.PersistenceService;
import co.uk.bittwisted.services.WatchJobService;

import javax.imageio.ImageIO;
import javax.sound.sampled.AudioInputStream;
import javax.sound.sampled.AudioSystem;
import javax.sound.sampled.Clip;
import javax.swing.*;
import javax.swing.border.EmptyBorder;
import java.awt.*;
import java.awt.event.*;
import java.awt.geom.RoundRectangle2D;
import java.awt.image.BufferedImage;
import java.io.BufferedInputStream;
import java.io.IOException;
import java.io.InputStream;
import java.util.Arrays;
import java.util.List;
import java.util.Objects;
import java.util.stream.Collectors;

import co.uk.bittwisted.ui.DragListener;
import co.uk.bittwisted.ui.UIBuilder;
import com.formdev.flatlaf.FlatDarkLaf;
import com.sun.jna.platform.win32.Advapi32Util;
import com.sun.jna.platform.win32.WinReg;

import javax.swing.UIManager;

public class ClickLessApp extends JFrame {
    private int watchJobCount = 0;
    private final int padding = 20;
    private String appVersion = "DEV";
    private static final String appTitle = "Clik Less";
    private static final String appExeName = "ClikLess";

    public static final String[] defaultAudioExtensions = new String[]{"mp3", "wav", "ogg"};
    public static final String[] defaultImageExtensions = new String[]{"gif", "png", "jpeg", "jpg"};
    private final WatchJobService watchJobService;

    private final PersistenceService persistenceService;

    private static boolean startupHidden;

    public ClickLessApp() throws IOException, AWTException, WatchJobServiceException {
        watchJobService = new WatchJobService();
        persistenceService = new PersistenceService();

        startupHidden = persistenceService.readPreferenceStartupHidden();

        String version = getClass().getPackage().getImplementationVersion();
        if(null != version) appVersion = "V" + version;

        initUI();
    }

    public static void main(String[] args) {
        EventQueue.invokeLater(() -> {
            try {
                UIManager.setLookAndFeel( new FlatDarkLaf() );
                ClickLessApp app = new ClickLessApp();
                if(startupHidden)
                    app.minimizeApplication();
                else
                    app.setVisible(true);
            } catch (IOException | AWTException | WatchJobServiceException | UnsupportedLookAndFeelException e) {
                e.printStackTrace();
            }
        });
    }

    private void initUI() throws IOException, AWTException {
        JPanel window = new JPanel();
        window.setLayout(new BoxLayout(window, BoxLayout.Y_AXIS));
        window.setBorder(new EmptyBorder(new Insets(padding, padding, padding, padding)));

        JPanel title = new JPanel();
        title.add(new JLabel(new ImageIcon(getImageFromResource("/clik_lezz_logo.png"))));

        JPanel version = new JPanel();
        version.setBorder(new EmptyBorder(new Insets(0, 0, 0, 0)));
        JLabel lblVersion = new JLabel("DEVELOPED BY BIT TWISTED LTD - " + appVersion);
        lblVersion.setFont(new Font("Aerial", Font.BOLD, 12));
        lblVersion.setForeground(Color.gray);
        version.add(lblVersion);

        UIBuilder.UIMenuOptions uiMenuOptions = UIBuilder.buildMenuOptions(this);
        JPanel menuOptions = uiMenuOptions.menuOptions;
        JButton btnAdd = uiMenuOptions.btnAdd;
        JButton btnHide = uiMenuOptions.btnHide;
        JButton btnExit = uiMenuOptions.btnExit;

        btnAdd.addActionListener(e -> addWatchJob(window));
        btnHide.addActionListener(e -> minimizeApplication());
        btnExit.addActionListener(e -> {
            try {
                exitApplication();
            } catch (IOException ex) {
                throw new RuntimeException(ex);
            }
        });

        UIBuilder.UIPreferences uiPreferences = UIBuilder.buildPreferences(this,
                persistenceService.readPreferenceStartupOnLogin(), persistenceService.readPreferenceStartupHidden());
        JPanel preferences = uiPreferences.cardPreferences;
        JCheckBox cbStartupOnLogin = uiPreferences.startupOnLogin;
        JCheckBox cbStartupHidden = uiPreferences.startupHidden;
        cbStartupOnLogin.addItemListener(e -> {
            try {
                persistenceService.savePreferenceStartupOnLogin(e.getStateChange() == ItemEvent.SELECTED);

                if(persistenceService.readPreferenceStartupOnLogin()) {
                    Advapi32Util.registrySetStringValue(WinReg.HKEY_CURRENT_USER,
                            "SOFTWARE\\Microsoft\\Windows\\CurrentVersion\\Run", appExeName, System.getProperty("user.dir") + "\\" + appExeName + ".exe");
                } else {
                    Advapi32Util.registryDeleteValue(WinReg.HKEY_CURRENT_USER,
                            "SOFTWARE\\Microsoft\\Windows\\CurrentVersion\\Run", appExeName);
                }
            } catch (IOException ex) {
                JOptionPane.showMessageDialog(window, ex.getMessage());
            }
        });
        cbStartupHidden.addItemListener(e -> {
            try {
                persistenceService.savePreferenceStartupHidden(e.getStateChange() == ItemEvent.SELECTED);
            } catch (IOException ex) {
                JOptionPane.showMessageDialog(window, ex.getMessage());
            }
        });

        window.add(title);
        window.add(version);
        window.add(Box.createRigidArea(new Dimension(padding, 0)));
        window.add(preferences);
        window.add(Box.createRigidArea(new Dimension(0, padding/2)));
        window.add(menuOptions);

        add(window);

        setUndecorated(true);
        addComponentListener(new ComponentAdapter() {
            @Override
            public void componentResized(ComponentEvent e) {
                setShape(new RoundRectangle2D.Double(0, 0, getWidth(), getHeight(), 15, 15));
            }
        });
        DragListener drag = new DragListener();
        addMouseListener(drag);
        addMouseMotionListener(drag);

        pack();
        setTitle(appTitle);
        setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);
        setLocationRelativeTo(null);
        Dimension screenSize = Toolkit.getDefaultToolkit().getScreenSize();
        setLocation(screenSize.width/2 - window.getWidth()/2, 20);
        setIconImage(getImageFromResource("/clik_lezz_icon.png"));

        List<WatchJob> watchJobs = persistenceService.getCachedWatchJobs();
        watchJobs.forEach(job -> addWatchJob(window, job));

        setupSystemTray();
        playIntroSound();
    }

    public void exitApplication() throws IOException {
        watchJobService.stopAllJobs();
        persistenceService.saveCachedWatchJobs();
        System.exit(0);
    }

    public void setupSystemTray() throws IOException, AWTException {
        if(SystemTray.isSupported()) {
            SystemTray tray = SystemTray.getSystemTray();
            PopupMenu popup = new PopupMenu();

            TrayIcon trayIcon = new TrayIcon(getImageFromResource("/clik_lezz_icon.png"), "Clik Less", popup);

            MenuItem defaultItem = new MenuItem("Exit");
            defaultItem.addActionListener(e -> {
                try {
                    exitApplication();
                } catch (IOException ex) {
                    throw new RuntimeException(ex);
                }
            });
            popup.add(defaultItem);

            defaultItem = new MenuItem("Open");
            defaultItem.addActionListener(e -> maximizeApplication());
            popup.add(defaultItem);

            trayIcon.addActionListener(e -> maximizeApplication());
            trayIcon.setImageAutoSize(true);

            tray.add(trayIcon);
        }
    }

    public void minimizeApplication() {
        if(SystemTray.isSupported()) {
            setExtendedState(ICONIFIED);
        }
        setVisible(false);
    }

    public void maximizeApplication() {
        EventQueue.invokeLater(() -> {
            if(SystemTray.isSupported()) {
                setExtendedState(JFrame.NORMAL);
            }
            setVisible(true);
            toFront();
            repaint();
        });
    }

    public void startJob(WatchJob watchJob, String watchFolderPath, String exeToRunPath, List<String> fileTypes)
            throws WatchJobServiceException, NoWatchFolderSpecifiedException, NoExeFileSpecifiedException, IOException {
        watchJob.setCheckForFileTypes(fileTypes);
        watchJob.setWatchFolderPath(watchFolderPath);
        watchJob.setExeToRunPath(exeToRunPath);
        watchJob.setPartiallyCreated(false);
        watchJobService.startJob(watchJob);

        persistenceService.saveWatchJob(watchJob);
    }

    private void stopJob(WatchJob watchJob) throws WatchJobServiceException, IOException {
        watchJobService.stopJob(watchJob);
        persistenceService.saveWatchJob(watchJob);
    }

    public void removeJobAndStop(WatchJob watchJob) throws WatchJobServiceException, IOException {
        watchJobService.removeJob(watchJob);
        persistenceService.removeWatchJob(watchJob);
    }

    private List<String> sanitiseFileTypeText(String fileTypes) {
        String[] arrTypes = fileTypes.split(";");
        List<String> types = Arrays.asList(arrTypes);
        return types.stream().filter(t -> !t.equals("")).collect(Collectors.toList());
    }

    public void addWatchJob(JPanel window) {
        try {
            watchJobCount++;
            String watchJobTitle = "WatchJob-" + watchJobCount;
            WatchJob watchJob = watchJobService.createJob(watchJobTitle);
            persistenceService.saveWatchJob(watchJob);
            createWatchJobUI(window, watchJob);
        } catch (IOException e) {
            throw new RuntimeException(e);
        }
    }

    public void addWatchJob(JPanel window, WatchJob watchJob) {
        createWatchJobUI(window, watchJob);
    }

    private void createWatchJobUI(JPanel window, WatchJob watchJob) {
        final WatchJob[] cloneWatchJob = new WatchJob[1];
        cloneWatchJob[0] = watchJob;

        String watchJobTitle;
        String chosenFileTypes = "";
        String exeToRunPath = "Application to open with...";
        String watchJobFolderPath = "Folder path to scan...";
        long pollingTime;
        boolean isAutoStart;
        boolean waitForAppToClose;
        if(!cloneWatchJob[0].isPartiallyCreated()) {
            watchJobTitle = cloneWatchJob[0].getName();
            exeToRunPath = cloneWatchJob[0].getExeToRunPath();
            watchJobFolderPath = cloneWatchJob[0].getWatchFolderPath();
            chosenFileTypes = UIBuilder.fileTypesText(cloneWatchJob[0].getCheckForFileTypes().toArray(new String[0]));
            isAutoStart = cloneWatchJob[0].isAutoStart();
            waitForAppToClose = cloneWatchJob[0].isWaitForAppToClose();
            pollingTime = cloneWatchJob[0].getPollingMillis() / 1000;
        } else {
            watchJobTitle = cloneWatchJob[0].getName();
            isAutoStart = cloneWatchJob[0].isAutoStart();
            waitForAppToClose = cloneWatchJob[0].isWaitForAppToClose();
            pollingTime = cloneWatchJob[0].getPollingMillis() / 1000;
        }

        Component gap = Box.createRigidArea(new Dimension(padding, padding/2));

        JPanel cards = new JPanel();
        cards.setBorder(BorderFactory.createLineBorder(Color.gray, 2, true));

        UIBuilder.UIWatchJobTitleSetup uiWatchJobTitleSetup = UIBuilder.buildWatchJobTitleSetup(this, watchJobTitle);
        JPanel cardWatchJobTitleSetup = uiWatchJobTitleSetup.cardTitleSetup;
        JTextField txtTitle = uiWatchJobTitleSetup.txtTitle;
        JButton btnSet = uiWatchJobTitleSetup.btnSet;
        btnSet.addActionListener(e -> {
            if(e.getActionCommand().equals("Rename")) {
                txtTitle.setEnabled(true);
                btnSet.setText("Set");
            } else {
                txtTitle.setEnabled(false);
                btnSet.setText("Rename");

                cloneWatchJob[0].setName(txtTitle.getText());
                try {
                    persistenceService.saveWatchJob(cloneWatchJob[0]);
                } catch (IOException ex) {
                    JOptionPane.showMessageDialog(cards, ex.getMessage());
                }
            }
        });

        UIBuilder.UIWatchJobFolderSetup uiWatchJobFolderSetup = UIBuilder.buildWatchJobFolderSetup(this, watchJobFolderPath);
        JPanel cardWatchFolderSetup = uiWatchJobFolderSetup.cardWatchFolderSetup;
        JTextField txtWatchFolderPath = uiWatchJobFolderSetup.txtWatchFolderPath;
        JButton btnChooseWatchFolder = uiWatchJobFolderSetup.btnChooseWatchFolder;

        UIBuilder.UIWatchJobApplicationExeSetup uiWatchJobApplicationExeSetup = UIBuilder.buildWatchJobApplicationExeSetup(this, exeToRunPath);
        JPanel cardApplicationExeSetup = uiWatchJobApplicationExeSetup.cardApplicationExeSetup;
        JTextField txtExeToRunPath = uiWatchJobApplicationExeSetup.txtExeToRunPath;
        JButton btnChooseApplicationExe = uiWatchJobApplicationExeSetup.btnChooseApplicationExe;

        UIBuilder.UIWatchJobFileTypeSetup uiWatchJobFileTypeSetup = UIBuilder.buildWatchJobFileTypeSetup(this);
        JPanel cardFileTypeSetup = uiWatchJobFileTypeSetup.cardFileTypeSetup;
        JTextField txtFileTypes = uiWatchJobFileTypeSetup.txtFileTypes;
        JButton btnAudioFileTypes = uiWatchJobFileTypeSetup.btnAudioFileTypes;
        JButton btnImageFileTypes = uiWatchJobFileTypeSetup.btnImageFileTypes;

        JPanel cardWatchJobFileTypeTextSetup = new JPanel();
        cardWatchJobFileTypeTextSetup.setBorder(new EmptyBorder(new Insets(5, 5, 5, 5)));
        txtFileTypes.setText(chosenFileTypes);
        txtFileTypes.setToolTipText("The extensions you wish to scan for, you can manually type them in separated by `;`");
        cardWatchJobFileTypeTextSetup.add(txtFileTypes);

        UIBuilder.UIWatchJobPreferencesSetup uiWatchJobPreferencesSetup = UIBuilder.buildPreferencesSetup(this, isAutoStart, waitForAppToClose, pollingTime);
        JPanel cardWatchJobPreferencesSetup = uiWatchJobPreferencesSetup.cardPreferencesSetup;
        JCheckBox cbAutoStart = uiWatchJobPreferencesSetup.cbAutoStart;
        JCheckBox cbWaitForAppToClose = uiWatchJobPreferencesSetup.cbWaitForAppToClose;
        JSpinner spinPollingSeconds = uiWatchJobPreferencesSetup.spinPollingSeconds;
        cbAutoStart.addItemListener(e -> {
            cloneWatchJob[0].setAutoStart(e.getStateChange() == ItemEvent.SELECTED);
            try {
                persistenceService.saveWatchJob(cloneWatchJob[0]);
            } catch (IOException ex) {
                JOptionPane.showMessageDialog(cards, ex.getMessage());
            }
        });
        cbWaitForAppToClose.addItemListener(e -> {
            cloneWatchJob[0].setWaitForAppToClose(e.getStateChange() == ItemEvent.SELECTED);
            try {
                persistenceService.saveWatchJob(cloneWatchJob[0]);
            } catch (IOException ex) {
                JOptionPane.showMessageDialog(cards, ex.getMessage());
            }
        });
        spinPollingSeconds.addChangeListener(e -> {
            double pollingSeconds = (double)((JSpinner)e.getSource()).getValue();
            cloneWatchJob[0].setPollingMillis((long)pollingSeconds * 1000L);
            try {
                persistenceService.saveWatchJob(cloneWatchJob[0]);
            } catch (IOException ex) {
                JOptionPane.showMessageDialog(cards, ex.getMessage());
            }
        });

        UIBuilder.UIWatchJobControls uiWatchJobControls = UIBuilder.buildWatchJobControls(this);
        JPanel cardWatchJobControls = uiWatchJobControls.cardWatchJobControls;
        JButton btnStart = uiWatchJobControls.btnStart;
        JButton btnDelete = uiWatchJobControls.btnDelete;
        btnStart.addActionListener(e -> {
            if(e.getActionCommand().equals("Start")) {
                List<String> fileTypes = sanitiseFileTypeText(txtFileTypes.getText());
                txtFileTypes.setText(String.join(";", fileTypes));

                if(txtFileTypes.getText().equals("") || fileTypes.size() == 0) {
                    JOptionPane.showMessageDialog(cards, "Please select what file types you wish to scan for?" +
                            " (you may type the file types in manually, remember to separate them by `;`)");
                } else {
                    try {
                        startJob(cloneWatchJob[0], txtWatchFolderPath.getText(), txtExeToRunPath.getText(), fileTypes);
                        btnStart.setText("Stop");

                        UIBuilder.disableWatchJobUI(btnChooseWatchFolder, btnChooseApplicationExe, txtFileTypes, btnAudioFileTypes, btnImageFileTypes, btnSet, spinPollingSeconds);
                    } catch (WatchJobServiceException | NoWatchFolderSpecifiedException |
                             NoExeFileSpecifiedException | IOException ex) {
                        JOptionPane.showMessageDialog(cards, ex.getMessage());
                    }
                }
            } else {
                try {
                    stopJob(cloneWatchJob[0]);
                    btnStart.setText("Start");

                    UIBuilder.enableWatchJobUI(btnChooseWatchFolder, btnChooseApplicationExe, txtFileTypes, btnAudioFileTypes, btnImageFileTypes, btnSet, spinPollingSeconds);
                } catch (WatchJobServiceException | IOException ex) {
                    JOptionPane.showMessageDialog(cards, ex.getMessage());
                }
            }
        });
        btnDelete.addActionListener(e -> {
            try {
                removeJobAndStop(cloneWatchJob[0]);
            } catch (WatchJobServiceException | IOException ex) {
                JOptionPane.showMessageDialog(cards, ex.getMessage());
            }

            window.remove(gap);
            window.remove(cards);
            pack();
        });

        cards.setLayout(new BoxLayout(cards, BoxLayout.Y_AXIS));
        cards.add(cardWatchJobTitleSetup);
        cards.add(cardFileTypeSetup);
        cards.add(cardWatchJobFileTypeTextSetup);
        cards.add(cardWatchFolderSetup);
        cards.add(cardApplicationExeSetup);
        cards.add(cardWatchJobControls);
        cards.add(cardWatchJobPreferencesSetup);

        window.add(gap);
        window.add(cards);
        pack();

        if(cloneWatchJob[0].isAutoStart()) {
            btnStart.doClick();
        }
    }

    private BufferedImage getImageFromResource(String path) throws IOException {
        return ImageIO.read(Objects.requireNonNull(getClass().getResource(path)));
    }

    private static synchronized void playIntroSound() {
        new Thread(() -> {
            try {
                Clip clip = AudioSystem.getClip();
                InputStream audioSrc = Objects.requireNonNull(ClickLessApp.class.getResourceAsStream("/click_intro.wav"));
                InputStream bufferedIn = new BufferedInputStream(audioSrc);
                AudioInputStream inputStream = AudioSystem.getAudioInputStream(bufferedIn);
                clip.open(inputStream);
                clip.start();
            } catch (Exception e) {
                e.printStackTrace();
            }
        }).start();
    }
}
