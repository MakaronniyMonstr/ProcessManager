package sample.utils.processloader;

import java.io.BufferedReader;
import java.io.IOException;
import java.util.HashSet;
import java.util.List;
import java.util.concurrent.*;

public class ProcessInfoLoader {
    private final String EXECUTABLE_NAME = "procapi.exe";

    private static ProcessInfoLoader loader;

    //Listeners
    private OnProcessesInfoUpdatedListener processesListener;
    private OnModuleInfoLoadedListener moduleListener;

    //Service threads
    private ScheduledExecutorService processesUpdateService;
    private ScheduledExecutorService moduleLoadService;

    //Processes list
    private HashSet<ProcessEntry> processEntries = new HashSet<>();

    //Task list
    private BlockingDeque<EntryTask> tasks = new LinkedBlockingDeque<>();

    public interface OnProcessesInfoUpdatedListener {
        void onProcessesInfoLoaded(List<ProcessEntry> processEntries);
    }

    public interface OnModuleInfoLoadedListener {
        void onModuleInfoLoaded(ProcessEntry processEntry);
    }

    private ProcessInfoLoader() {}

    public ProcessInfoLoader getInstance() {
        if (loader == null) {
            loader = new ProcessInfoLoader();
        }

        return loader;
    }

    public void addOnProcessesUpdatedListener(OnProcessesInfoUpdatedListener processesListener) {
        loader.processesListener = processesListener;

        if (processesUpdateService != null &&
                !processesUpdateService.isTerminated()) {
            processesUpdateService.shutdownNow();
        }

        processesUpdateService = Executors.newSingleThreadScheduledExecutor();
        processesUpdateService.scheduleAtFixedRate(
                () -> {
                    processesListener.onProcessesInfoLoaded(null);
                }
                , 0, 500, TimeUnit.MILLISECONDS);
    }

    public void addOnModuleLoadedListener(OnModuleInfoLoadedListener moduleListener) {
        loader.moduleListener = moduleListener;
    }

    public void loadProcessModules() {
        moduleLoadService.execute(
                () -> {
                    moduleListener.onModuleInfoLoaded(null);
                }
        );
    }

    public EntryTask getTask() {
        return tasks.poll();
    }

    private ProcessEntry parseProcessOut(BufferedReader processReader) throws IOException {
        while (processReader.ready()) {
            String line = processReader.readLine();
            String[] params = line.split(" ");


        }

        return null;
    }

    private ModuleEntry parseModuleOut(BufferedReader moduleReader) {
        return null;
    }
}
