package co.uk.bittwisted.services;

import co.uk.bittwisted.domain.WatchJob;
import co.uk.bittwisted.exceptions.WatchJobServiceException;

public interface WatchJobServiceImpl {
    WatchJob createJob(String name);
    void removeJob(WatchJob watchJob) throws WatchJobServiceException;
    void startJob(WatchJob watchJob) throws WatchJobServiceException;
    void stopJob(WatchJob watchJob) throws WatchJobServiceException;
    void stopAllJobs();
}
