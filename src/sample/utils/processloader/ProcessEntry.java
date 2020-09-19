package sample.utils.processloader;

import java.util.HashSet;
import java.util.Objects;

public class ProcessEntry {
    private int processID;
    private int countThreads;
    private int parentProcessID;
    private long basePriority;
    private String exePath;

    private HashSet<ModuleEntry> moduleEntries = new HashSet<>();

    public ProcessEntry(String[] params) {
        this.processID = Integer.parseInt(params[0]);
        this.countThreads = Integer.parseInt(params[1]);
        this.parentProcessID = Integer.parseInt(params[2]);
        this.basePriority = Integer.parseInt(params[3]);
        this.exePath = params[4];
    }

    public int getProcessID() {
        return processID;
    }

    public void setProcessID(int processID) {
        this.processID = processID;
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

    public HashSet<ModuleEntry> getModuleEntries() {
        return moduleEntries;
    }

    @Override
    public boolean equals(Object o) {
        if (this == o) return true;
        if (o == null || getClass() != o.getClass()) return false;
        ProcessEntry that = (ProcessEntry) o;

        return processID == that.processID &&
                countThreads == that.countThreads &&
                parentProcessID == that.parentProcessID &&
                basePriority == that.basePriority &&
                exePath.equals(that.exePath) &&
                moduleEntries.equals(that.moduleEntries);
    }

    @Override
    public int hashCode() {
        return Objects.hash(processID, countThreads, parentProcessID, basePriority, exePath, moduleEntries);
    }
}
