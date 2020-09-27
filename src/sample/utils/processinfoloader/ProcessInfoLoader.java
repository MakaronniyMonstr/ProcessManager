package sample.utils.processinfoloader;

import sample.utils.processpipe.ProcessPipe;

import java.io.BufferedReader;
import java.io.File;
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
    private List<OnUtilTaskCompletedListener> utilListeners = new LinkedList<>();
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

    public boolean isProcApiInstalled() {
        File file = new File(Paths.get("")
                .toAbsolutePath()
                .toString());

        return Arrays.stream(
                Objects.requireNonNull(
                        file.list((dir, name) -> name.equals(EXECUTABLE_NAME)))
                )
                .count() > 0;
    }

    //Additional functionality.
    //Allows you to receive additional system information.
    //Pay attention to UtilTask class.
    public void addOnUtilTaskCompletedListener(OnUtilTaskCompletedListener utilListener) {
        loader.utilListeners.add(utilListener);
    }

    public void removeOnUtilTaskCompletedListener(OnUtilTaskCompletedListener utilListener) {
        loader.utilListeners.remove(utilListener);
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
        loader.utilExecuteService.shutdownNow();
        try {
            loader.utilExecuteService.awaitTermination(500, TimeUnit.MILLISECONDS);
        } catch (InterruptedException e) { e.printStackTrace(); }
    }

    //Run new task
    public void runNewTask(UtilTask task) {
        if (loader.utilListeners.size() == 0)
            return;

        loader.utilExecuteService = Executors.newSingleThreadScheduledExecutor();
        loader.utilExecuteService.execute(
                () -> {
                    try {
                        ProcessPipe pipe;

                        pipe = new ProcessPipe(execPath, task.getStringCommand());
                        UtilTask outTask = parseTask(pipe.getReader(), task);

                        loader.utilListeners.forEach(onUtilTaskCompletedListener -> {
                            onUtilTaskCompletedListener.onTaskCompleted(outTask);
                        });

                        pipe.destroy();

                    } catch (IOException e) {
                        e.printStackTrace();
                    }
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

        System.out.println(loader.processEntries.size() + " processes loaded.");
        return loader.processEntries;
    }

    private UtilTask parseTask(BufferedReader reader, UtilTask task) throws IOException {
        String line;

        task.getData().clear();
        while ((line = reader.readLine()) != null) {
            task.getData().add(line + " ");
        }

        System.out.println(task.getStringCommand() + " command was received.");

        return task;
    }
}
