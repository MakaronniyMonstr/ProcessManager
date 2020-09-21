package sample.utils.processloader;

import sample.utils.processpipe.ProcessPipe;

import java.io.BufferedReader;
import java.io.IOException;
import java.nio.file.Paths;
import java.util.*;
import java.util.concurrent.*;

public class ProcessInfoLoader {
    private final int SERVICE_PERIOD_MS = 1500;
    private final String EXECUTABLE_NAME = "procapi.exe";

    private String execPath;
    //Singleton
    private static ProcessInfoLoader loader;
    //Listeners
    private OnProcessesInfoUpdatedListener processesListener;
    private OnTaskCompletedListener taskListener;
    //Service threads
    private ScheduledExecutorService processesUpdateService;
    private ScheduledExecutorService moduleLoadService;
    //Processes list
    private Map<String, ProcessEntry> processEntries = new HashMap<>();
    //Task list
    private BlockingDeque<EntryTask> tasks = new LinkedBlockingDeque<>();

    //Updates your UI.
    public interface OnProcessesInfoUpdatedListener {
        void onProcessesInfoLoaded(List<ProcessModifyTask> processModifyTasks);
    }

    //Updates your UI.
    public interface OnTaskCompletedListener {
        void onTaskCompleted(EntryTask task);
    }

    private ProcessInfoLoader() {
        execPath = Paths.get("")
                .toAbsolutePath()
                .toString() + EXECUTABLE_NAME;
    }

    public static ProcessInfoLoader getInstance() {
        if (loader == null) {
            loader = new ProcessInfoLoader();
        }

        return loader;
    }

    //Here you set up and run processes update service.
    //Use this one to update your UI.
    public void setOnProcessesUpdatedListener(OnProcessesInfoUpdatedListener processesListener) {
        loader.processesListener = processesListener;

        //Service is already started up
        if (processesUpdateService != null &&
                !processesUpdateService.isShutdown())
            return;

        processesUpdateService = Executors.newSingleThreadScheduledExecutor();
        processesUpdateService.scheduleAtFixedRate(
                () -> {
                    try {
                        ProcessPipe pipe;
                        List<ProcessEntry> processEntries;

                        pipe = new ProcessPipe(execPath, "");
                        processesListener
                                .onProcessesInfoLoaded(
                                        parseProcessOutput(pipe.getReader())
                                );

                    } catch (IOException e) {
                        e.printStackTrace();
                    }
                }
                , 0, SERVICE_PERIOD_MS, TimeUnit.MILLISECONDS);
    }

    //Additional functionality.
    //Allows you to receive additional system information.
    //Pay attention to EntryTask class.
    public void setOnTaskCompletedListener(OnTaskCompletedListener taskListener) {
        loader.taskListener = taskListener;
    }

    //Parse console output
    private List<ProcessModifyTask> parseProcessOutput(BufferedReader processReader) throws IOException {
        List<ProcessModifyTask> processTasksList = new ArrayList<>();
        Map<String, ProcessEntry> processEntriesUpdated = new HashMap<>(processEntries);

        while (processReader.ready()) {
            String line;
            String[] params;
            ProcessEntry process;

            line = processReader.readLine();
            params = line.split(" ");
            process = new ProcessEntry(params);

            //Process is already in the map
            if (processEntriesUpdated.containsKey(process.getProcessName())) {
                //Process has the same name and some new data.
                //Update process data to the map.
                if (!processEntriesUpdated.containsValue(process)) {
                    processEntriesUpdated.get(process.getProcessName()).update(process);
                }
            }
            //Absolutely new process
            else {
                processEntriesUpdated.put(process.getProcessName(), process);
                processTasksList.add(new ProcessModifyTask(
                        process,
                        ProcessModifyTask.ADD
                ));
            }
        }

        /*
        //Compare new and old processes maps to find difference and remove processes
         */
        processEntries.forEach((key, value) -> {
            if (!processEntriesUpdated.containsValue(key)) {
                processTasksList.add(new ProcessModifyTask(
                        value,
                        ProcessModifyTask.REMOVE
                ));
            }
        });

        //Update processes map
        processEntries.clear();
        processEntries = processEntriesUpdated;
        processTasksList.sort(ProcessModifyTask::compareTo);

        return processTasksList;
    }

    private ModuleEntry parseTask(BufferedReader moduleReader) {
        return null;
    }
}
