package co.uk.bittwisted.domain;

import co.uk.bittwisted.exceptions.NoExeFileSpecifiedException;
import co.uk.bittwisted.exceptions.NoWatchFolderSpecifiedException;
import co.uk.bittwisted.exceptions.WatchJobServiceException;
import co.uk.bittwisted.services.WatchJobService;
import org.apache.commons.codec.digest.DigestUtils;

import java.io.File;
import java.io.IOException;
import java.io.InputStream;
import java.nio.file.Files;
import java.nio.file.Paths;
import java.util.*;
import java.util.concurrent.ExecutionException;
import java.util.concurrent.TimeoutException;

import static java.lang.System.out;

public class WatchJob  {
    private transient Timer timer;
    private transient File watchFolder;
    private String uuid;
    private String name;
    private List<String> checkForFileTypes;
    private String exeToRunPath;
    private String watchFolderPath;
    private long pollingMillis = 5000L;

    private boolean autoStart;
    private boolean waitForAppToClose;
    private boolean skipFirstFoundFiles = true;
    private final List<String> filesAlreadyInteractedWith;
    private boolean partiallyCreated;

    public WatchJob(String name) {
        this.name = name;
        this.partiallyCreated = true;
        this.uuid = UUID.randomUUID().toString();
        filesAlreadyInteractedWith = new ArrayList<>();
    }

    public void start() {
        if(timer == null)
            timer = new Timer("watchJob-" + hashCode());

        WatchJobTimerTask timerTask = new WatchJobTimerTask();
        timer.scheduleAtFixedRate(timerTask, 1000, pollingMillis);
    }

    public void stop() {
        if(timer != null) {
            timer.cancel();
            timer.purge();
            timer = null;
        }
    }

    private Optional<String> getExtensionByStringHandling(File file) {
        String fileName = file.getAbsoluteFile().toPath().toString();
        return Optional.of(fileName)
                .filter(f -> f.contains("."))
                .map(f -> f.substring(fileName.lastIndexOf(".") + 1));
    }

    private boolean hasNotInteractedWithFile(File file) throws IOException {
        return !filesAlreadyInteractedWith.contains(generateMD5FromFile(file));
    }

    private void interactedWithFile(File file) throws IOException {
        String md5 = generateMD5FromFile(file);
        filesAlreadyInteractedWith.add(md5);
    }

    private String generateMD5FromFile(File file) throws IOException {
        try (InputStream is = Files.newInputStream(Paths.get(file.getAbsolutePath()))) {
            return DigestUtils.md5Hex(is);
        }
    }

    class WatchJobTimerTask extends TimerTask {
        private boolean init = true;

        @Override
        public void run() {
            out.println("WATCH JOB RUNNING [ " + name + " ] EVERY [ " + pollingMillis + " ] milliseconds");
            int exitCode;
            String maybeExtension;
            for(File file : Objects.requireNonNull(watchFolder.listFiles())) {
                try {
                    if(file.isFile() && hasNotInteractedWithFile(file)) {
                        maybeExtension = getExtensionByStringHandling(file).orElse("");
                        if(checkForFileTypes.contains(maybeExtension)) {
                            out.println("FILE FOUND WITH TYPE [ " + maybeExtension + " ]");
                            if(init && skipFirstFoundFiles) {
                                out.println("SKIP FILE ON START [ " + file.getAbsolutePath() + " ]");
                                interactedWithFile(file);
                            } else {

                                out.println("START EXE WITH FILE [ " + file.getAbsolutePath() + " ] ");
                                exitCode = WatchJobService.processHandler.exec(exeToRunPath, file.getAbsolutePath(), waitForAppToClose);
                                if(exitCode == 0) interactedWithFile(file);
                            }
                        }
                    }
                } catch (IOException | InterruptedException | TimeoutException | ExecutionException e) {
                    e.printStackTrace();
                }
            }
            init = false;
        }
    }

    public String getName() {
        return name;
    }

    public void setName(String name) {
        this.name = name;
    }

    public void setWaitForAppToClose(boolean waitForAppToClose) {
        this.waitForAppToClose = waitForAppToClose;
    }

    public boolean isWaitForAppToClose() {
        return waitForAppToClose;
    }

    public void setPollingMillis(long pollingMillis) {
        this.pollingMillis = pollingMillis;
    }

    public long getPollingMillis() {
        return pollingMillis;
    }

    public void setSkipFirstFoundFiles(boolean skipFirstFoundFiles) {
        this.skipFirstFoundFiles = skipFirstFoundFiles;
    }

    public boolean isSkipFirstFoundFiles() {
        return skipFirstFoundFiles;
    }

    public boolean isPartiallyCreated() {
        return partiallyCreated;
    }

    public void setPartiallyCreated(boolean partiallyCreated) {
        this.partiallyCreated = partiallyCreated;
    }

    public boolean isAutoStart() {
        return autoStart;
    }

    public void setAutoStart(boolean autoStart) {
        this.autoStart = autoStart;
    }

    public void setCheckForFileTypes(List<String> checkForFileTypes) {
        this.checkForFileTypes = checkForFileTypes;
    }

    public List<String> getCheckForFileTypes() {
        return checkForFileTypes;
    }

    public void setWatchFolderPath(String watchFolderPath) throws NoWatchFolderSpecifiedException, WatchJobServiceException {
        this.watchFolderPath = watchFolderPath;

        watchFolder = new File(watchFolderPath);
        if(!watchFolder.exists()) {
            throw new NoWatchFolderSpecifiedException();
        } else if(!watchFolder.isDirectory()) {
            throw new WatchJobServiceException();
        }
    }

    public String getWatchFolderPath() {
        return watchFolderPath;
    }

    public void setExeToRunPath(String exeToRunPath) throws NoExeFileSpecifiedException, WatchJobServiceException {
        this.exeToRunPath = exeToRunPath;

        File exeToRun = new File(exeToRunPath);
        if(!exeToRun.exists()) {
            throw new NoExeFileSpecifiedException();
        } else if(!exeToRun.isFile()) {
            throw new WatchJobServiceException();
        }
    }

    public String getExeToRunPath() {
        return exeToRunPath;
    }
}
