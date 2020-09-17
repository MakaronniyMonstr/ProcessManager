package sample.Utils;

import java.util.ArrayList;
import java.util.List;

public class ProcessEntry {
    private int usageCount;
    private int ProcessID;
    private long defaultHeapID;
    private int moduleID;
    private int countThreads;
    private int parentProcessID;
    private long basePriority;
    private String exePath;

    private List<ModuleEntry> moduleEntries = new ArrayList<>();

    public int getUsageCount() {
        return usageCount;
    }

    public void setUsageCount(int usageCount) {
        this.usageCount = usageCount;
    }

    public int getProcessID() {
        return ProcessID;
    }

    public void setProcessID(int processID) {
        ProcessID = processID;
    }

    public long getDefaultHeapID() {
        return defaultHeapID;
    }

    public void setDefaultHeapID(long defaultHeapID) {
        this.defaultHeapID = defaultHeapID;
    }

    public int getModuleID() {
        return moduleID;
    }

    public void setModuleID(int moduleID) {
        this.moduleID = moduleID;
    }

    public int getCountThreads() {
        return countThreads;
    }

    public void setCountThreads(int countThreads) {
        this.countThreads = countThreads;
    }

    public int getParentProcessID() {
        return parentProcessID;
    }

    public void setParentProcessID(int parentProcessID) {
        this.parentProcessID = parentProcessID;
    }

    public long getBasePriority() {
        return basePriority;
    }

    public void setBasePriority(long basePriority) {
        this.basePriority = basePriority;
    }

    public String getExePath() {
        return exePath;
    }

    public void setExePath(String exePath) {
        this.exePath = exePath;
    }

    public void addModule(ModuleEntry moduleEntry) {
        moduleEntries.add(moduleEntry);
    }

    public List<ModuleEntry> getModuleEntries() {
        return moduleEntries;
    }
}
