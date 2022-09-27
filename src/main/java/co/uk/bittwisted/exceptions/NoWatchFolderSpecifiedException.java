package co.uk.bittwisted.exceptions;

public class NoWatchFolderSpecifiedException extends Exception {
    public NoWatchFolderSpecifiedException() {
        super("Please specify a watch folder to scan periodically.");
    }
}
