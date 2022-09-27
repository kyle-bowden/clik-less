package co.uk.bittwisted.utils;

import java.io.*;
import java.util.concurrent.*;
import java.util.function.Consumer;

import static java.lang.System.out;

public class ProcessHandler {
    public static boolean isWindows;
    private final ProcessBuilder builder;

    public ProcessHandler() {
        isWindows = System.getProperty("os.name").toLowerCase().startsWith("windows");
        builder = new ProcessBuilder();
    }

    private record StreamGobbler(InputStream inputStream, Consumer<String> consumer) implements Runnable {
        @Override
        public void run() {
            new BufferedReader(new InputStreamReader(inputStream)).lines()
                    .forEach(consumer);
        }
    }

    public int exec(String exeToRunPath, String fileToOpenPath, boolean waitForExitCode)
            throws IOException, InterruptedException, TimeoutException, ExecutionException {
        if (isWindows) {
            builder.command(exeToRunPath, fileToOpenPath);
        } else {
            // TODO: linux support needs testing
            builder.command(exeToRunPath, fileToOpenPath);
        }
        builder.directory(new File(System.getProperty("user.home")));

        Process process = builder.start();
        StreamGobbler streamGobbler = new StreamGobbler(process.getInputStream(), out::println);
        Future<?> future = Executors.newSingleThreadExecutor().submit(streamGobbler);

        int exitCode = 0;
        if(waitForExitCode) {
            exitCode = process.waitFor();
            assert exitCode == 0;
            future.get(10, TimeUnit.SECONDS);
        }

        return exitCode;
    }
}
