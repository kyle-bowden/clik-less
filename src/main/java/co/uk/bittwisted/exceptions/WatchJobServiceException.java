package co.uk.bittwisted.exceptions;

public class WatchJobServiceException extends Exception {
    public WatchJobServiceException() {
        super("An unhandled error occurred with the watch job.");
    }
}
