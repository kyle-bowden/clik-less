package co.uk.bittwisted.exceptions;

public class NoExeFileSpecifiedException extends Exception {
    public NoExeFileSpecifiedException() {
        super("Please specify an exe to open when a file is found in the watched folder.");
    }
}
