package sample.Utils;

import java.util.List;
import java.util.concurrent.BlockingDeque;
import java.util.concurrent.LinkedBlockingDeque;

public class ProcessInfoLoader {
    private final String EXECUTABLE_NAME = "procapi.exe";
    //Singleton
    private static ProcessInfoLoader loader;
    //Hold processes list
    private BlockingDeque<ProcessEntry> processEntries = new LinkedBlockingDeque<>();
    private OnProcessesInfoLoadedListener processesListener;
    private OnModuleInfoLoadedListener moduleListener;

    public interface OnProcessesInfoLoadedListener {
        void onProcessesInfoLoaded(List<ProcessEntry> processEntries);
    }

    public interface OnModuleInfoLoadedListener {
        void onModuleInfoLoaded(ModuleEntry moduleEntry);
    }

    private ProcessInfoLoader() {}

    public ProcessInfoLoader getInstance() {
        if (loader == null) {
            loader = new ProcessInfoLoader();
        }

        return loader;
    }

    public void addOnProcessesLoadedListener(OnProcessesInfoLoadedListener mProcessesListener) {
        loader.processesListener = mProcessesListener;
    }

    public void addOnModuleLoadedListener(OnModuleInfoLoadedListener mModuleListener) {
        loader.moduleListener = mModuleListener;
    }
}
