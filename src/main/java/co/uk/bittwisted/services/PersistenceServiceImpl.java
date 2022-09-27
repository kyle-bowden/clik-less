package co.uk.bittwisted.services;

import co.uk.bittwisted.domain.WatchJob;

import java.io.IOException;
import java.util.List;

public interface PersistenceServiceImpl {
    void saveWatchJob(WatchJob watchJob) throws IOException;
    void saveWatchJobs(List<WatchJob> watchJobs) throws IOException;
    void removeWatchJob(WatchJob watchJob) throws IOException;
}
