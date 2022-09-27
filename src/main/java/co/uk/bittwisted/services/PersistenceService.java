package co.uk.bittwisted.services;

import co.uk.bittwisted.domain.ClikLessData;
import co.uk.bittwisted.domain.WatchJob;
import co.uk.bittwisted.exceptions.WatchJobServiceException;
import com.google.gson.Gson;
import com.google.gson.GsonBuilder;
import com.google.gson.stream.JsonReader;

import java.io.*;
import java.lang.reflect.Modifier;
import java.util.ArrayList;
import java.util.List;

public class PersistenceService implements PersistenceServiceImpl {
    private final Gson gson;
    private final String saveFilePath;

    private ClikLessData clikLessData;

    public PersistenceService() throws WatchJobServiceException, IOException {
        gson = new GsonBuilder()
            .excludeFieldsWithModifiers(Modifier.STATIC, Modifier.TRANSIENT)
            .setPrettyPrinting()
            .create();

        File saveDir = new File(System.getProperty("user.home") + "/ClikLess");
        if(!saveDir.exists()) saveDir.mkdir();
        File saveFile = new File(System.getProperty("user.home") + "/ClikLess/data.json");
        if(!saveFile.exists()) {
            boolean saved = saveFile.createNewFile();
            if(!saved) throw new WatchJobServiceException();
            clikLessData = new ClikLessData();
            clikLessData.watchJobs = new ArrayList<>();
            this.saveFilePath = saveFile.getAbsolutePath();
            saveClikLessData(clikLessData);
        } else {
            this.saveFilePath = saveFile.getAbsolutePath();
            clikLessData = readClikLessData();
        }
    }

    @Override
    public void saveWatchJob(WatchJob watchJob) throws IOException {
        if(clikLessData.watchJobs.contains(watchJob)) {
            clikLessData.watchJobs.set(clikLessData.watchJobs.indexOf(watchJob), watchJob);
        } else {
            clikLessData.watchJobs.add(watchJob);
        }
        saveWatchJobs(clikLessData.watchJobs);
    }

    @Override
    public void removeWatchJob(WatchJob watchJob) throws IOException {
        clikLessData.watchJobs.remove(watchJob);
        saveWatchJobs(clikLessData.watchJobs);
    }

    @Override
    public void saveWatchJobs(List<WatchJob> watchJobs) throws IOException {
        clikLessData.watchJobs = watchJobs;
        saveClikLessData(clikLessData);
    }

    public void saveCachedWatchJobs() throws IOException {
        saveWatchJobs(clikLessData.watchJobs);
    }

    public List<WatchJob> getCachedWatchJobs() {
        return clikLessData.watchJobs;
    }

    public void savePreferenceStartupOnLogin(boolean startupOnLogin) throws IOException {
        clikLessData.startupOnLogin = startupOnLogin;
        saveClikLessData(clikLessData);
    }

    public boolean readPreferenceStartupOnLogin() {
        return clikLessData.startupOnLogin;
    }

    public void savePreferenceStartupHidden(boolean startupHidden) throws IOException {
        clikLessData.startupHidden = startupHidden;
        saveClikLessData(clikLessData);
    }

    public boolean readPreferenceStartupHidden() {
        return clikLessData.startupHidden;
    }

    public ClikLessData readClikLessData() throws FileNotFoundException {
        JsonReader reader = new JsonReader(new FileReader(saveFilePath));
        return gson.fromJson(reader, ClikLessData.class);
    }

    public void saveClikLessData(ClikLessData clikLessData) throws IOException {
        try (Writer writer = new FileWriter(saveFilePath)) {
            gson.toJson(clikLessData, writer);
        }
    }
}
