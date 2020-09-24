package sample.utils.processloader;

import sample.utils.processpipe.ProcessPipe;

import java.io.BufferedReader;
import java.io.IOException;
import java.nio.file.Paths;
import java.util.*;
import java.util.concurrent.*;

public class ProcessInfoLoader {

    private final String EXECUTABLE_NAME = "procapi.exe";
    private final int SERVICE_PERIOD_MS = 2000;

    private String execPath;
    //Singleton
    private static ProcessInfoLoader loader;
    //Listeners
    private OnProcessesInfoUpdatedListener processesListener;
    private OnUtilTaskCompletedListener utilListener;
    //Service threads
    private ScheduledExecutorService processesUpdateService;
    private ScheduledExecutorService utilExecuteService;
    //Processes list
    private List<ProcessEntry> processEntries = new ArrayList<>(200);

    //Updates your UI.
    public interface OnProcessesInfoUpdatedListener {
        void onProcessesInfoLoaded(List<ProcessEntry> processModifyTasks);
    }

    //Updates your UI.
    public interface OnUtilTaskCompletedListener {
        void onTaskCompleted(UtilTask task);
    }

    private ProcessInfoLoader() {
        execPath = Paths.get("")
                .toAbsolutePath()
                .toString() + "\\" + EXECUTABLE_NAME;
    }

    public static ProcessInfoLoader getInstance() {
        if (loader == null) {
            loader = new ProcessInfoLoader();
        }

        return loader;
    }

    //Additional functionality.
    //Allows you to receive additional system information.
    //Pay attention to UtilTask class.
    public void setOnUtilTaskCompletedListener(OnUtilTaskCompletedListener utilListener) {
        loader.utilListener = utilListener;
    }

    //Here you set up and run processes update service.
    //Use this one to update your UI.
    public void setOnProcessesUpdatedListener(OnProcessesInfoUpdatedListener processesListener) {
        loader.processesListener = processesListener;
    }

    public void runService() {
        //Service is already started up
        if (loader.processesUpdateService != null &&
                !loader.processesUpdateService.isShutdown())
            return;

        if (loader.processesListener == null)
            return;

        loader.processesUpdateService = Executors.newSingleThreadScheduledExecutor();
        loader.processesUpdateService.scheduleAtFixedRate(
                () -> {
                    try {
                        ProcessPipe pipe;

                        pipe = new ProcessPipe(
                                execPath,
                                UtilTask.commandToString(UtilTask.GET_PROCESSES_LIST)
                        );
                        loader.processesListener
                                .onProcessesInfoLoaded(
                                        parseProcessOutput(pipe.getReader())
                                );

                        pipe.destroy();

                    } catch (IOException e) {
                        e.printStackTrace();
                    }
                }
                , 0, SERVICE_PERIOD_MS, TimeUnit.MILLISECONDS);
    }

    public void stopService() {
        loader.utilExecuteService.shutdown();
        try {
            loader.utilExecuteService.awaitTermination(500, TimeUnit.MILLISECONDS);
        } catch (InterruptedException e) { e.printStackTrace(); }
    }

    //Run new task
    public void runNewTask(UtilTask task) {
        if (loader.utilListener == null)
            return;

        loader.utilExecuteService = Executors.newSingleThreadScheduledExecutor();
        loader.utilExecuteService.execute(
                () -> {
                    try {
                        ProcessPipe pipe;

                        pipe = new ProcessPipe(execPath, task.getStringCommand());
                        loader.utilListener
                                .onTaskCompleted(parseTask(pipe.getReader(), task));

                        pipe.destroy();

                    } catch (IOException e) { e.printStackTrace(); }
                }
        );
    }

    public void destroy() {
        if (loader.processesUpdateService != null)
            loader.processesUpdateService.shutdownNow();
    }

    //Parse console output
    private List<ProcessEntry> parseProcessOutput(BufferedReader reader) throws IOException {
        LinkedList<String> params = new LinkedList<>();
        String line;

        processEntries.clear();

        while ((line = reader.readLine()) != null) {

            if (!line.isEmpty()) {
                params.offer(line);
            }
            else {
                ProcessEntry process;

                process = new ProcessEntry(params);
                params.clear();

                loader.processEntries.add(process);
            }
        }

        loader.processEntries.sort(ProcessEntry::compareTo);

        System.out.println("" + loader.processEntries.size() + " processes loaded.");
        return loader.processEntries;
    }

    private UtilTask parseTask(BufferedReader reader, UtilTask task) throws IOException {
        String line;

        task.getData().clear();
        while ((line = reader.readLine()) != null) {
            task.getData().add(line);
        }

        return task;
    }
}
