package sample.Utils;

import java.io.IOException;
import java.io.InputStreamReader;
import java.io.Reader;
import java.util.List;
import java.util.concurrent.BlockingDeque;
import java.util.concurrent.LinkedBlockingDeque;

public class ProcessInfoLoader {
    private final String EXECUTABLE_NAME = "procapi.exe";
    //Singleton
    private static ProcessInfoLoader loader;
    //Hold processes list
    private BlockingDeque<ProcessEntry> processEntries = new LinkedBlockingDeque<>();
    private onProcessesInfoLoadedListener processesListener;
    private onModuleInfoLoadedListener moduleListener;

    public interface onProcessesInfoLoadedListener {
        void onProcessesInfoLoaded(List<ProcessEntry> processEntries);
    }

    public interface onModuleInfoLoadedListener {
        void onModuleInfoLoaded(ModuleEntry moduleEntry);
    }

    private ProcessInfoLoader() {}

    public ProcessInfoLoader getInstance() {
        if (loader == null) {
            loader = new ProcessInfoLoader();
        }

        return loader;
    }

    public void setProcessesListener(onProcessesInfoLoadedListener mProcessesListener) {
        loader.processesListener = mProcessesListener;
    }

    public void setModuleListener(onModuleInfoLoadedListener mModuleListener) {
        loader.moduleListener = mModuleListener;
    }

    public void startRunningProcessesLoadTask() {
        Thread task = new Thread(new Runnable() {
            @Override
            public void run() {
                try {
                    Process process = Runtime.getRuntime().exec(EXECUTABLE_NAME);

                    Reader reader = new InputStreamReader(process.getInputStream());
                } catch (IOException e) { e.printStackTrace(); }
            }
        });

        task.setDaemon(true);
        task.start();
    }

    public void startProcessModulesLoadTask(int processID) {
        Thread task = new Thread(new Runnable() {
            @Override
            public void run() {
                try {
                    Process process = Runtime.getRuntime().exec(EXECUTABLE_NAME + " " + processID);

                    Reader reader = new InputStreamReader(process.getInputStream());
                } catch (IOException e) { e.printStackTrace(); }
            }
        });

        task.setDaemon(true);
        task.start();
    }
}
