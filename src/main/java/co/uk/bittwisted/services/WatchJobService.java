package co.uk.bittwisted.services;

import co.uk.bittwisted.domain.WatchJob;
import co.uk.bittwisted.exceptions.WatchJobServiceException;
import co.uk.bittwisted.utils.ProcessHandler;

import java.util.ArrayList;
import java.util.List;

public class WatchJobService implements WatchJobServiceImpl {
    public static ProcessHandler processHandler = new ProcessHandler();

    private final List<WatchJob> watchJobs;

    public WatchJobService() {
        watchJobs = new ArrayList<>();
    }

    @Override
    public WatchJob createJob(String name) {
        return new WatchJob(name);
    }

    @Override
    public void removeJob(WatchJob watchJob) throws WatchJobServiceException {
        if(watchJob == null) throw new WatchJobServiceException();
        watchJob.stop();
        watchJobs.remove(watchJob);
    }

    @Override
    public void startJob(WatchJob watchJob) throws WatchJobServiceException {
        if(watchJob == null) throw new WatchJobServiceException();
        watchJob.start();
    }

    @Override
    public void stopJob(WatchJob watchJob) throws WatchJobServiceException {
        if(watchJob == null) throw new WatchJobServiceException();
        watchJob.stop();
    }

    @Override
    public void stopAllJobs() {
        watchJobs.forEach(WatchJob::stop);
    }
}
