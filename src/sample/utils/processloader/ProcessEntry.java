package sample.utils.processloader;

import javafx.beans.property.IntegerProperty;
import javafx.beans.property.SimpleIntegerProperty;
import javafx.beans.property.SimpleStringProperty;
import javafx.beans.property.StringProperty;

import java.util.ArrayList;
import java.util.LinkedList;
import java.util.List;
import java.util.Objects;

public class ProcessEntry implements Comparable<ProcessEntry> {

    private String processName;
    private int processID;
    private String exePath;
    private int parentProcessID;
    //Owner and SID information
    private String ownerName;
    private String ownerDomain;
    private String SID;
    //Process type (32/64 bit)
    private String processType;
    //Native/CLR.NET
    private String runtime;
    //DEP/ASLR
    private String spaceLayout;
    private List<ModuleEntry> moduleEntries = new ArrayList<>();

    //Additional Information
    private int basePriority;
    private int countThreads;


    //Debug constructor
    public ProcessEntry(
            String name,
            int processID,
            String exePath,
            int parentProcessID,
            String ownerName,
            String ownerDomain,
            String SID,
            String processType,
            String runtime,
            String spaceLayout,
            int countThreads,
            int basePriority
            )
    {
        this.processName = name;
        this.processID = processID;
        this.exePath = exePath;
        this.parentProcessID = parentProcessID;
        this.ownerName = ownerName;
        this.ownerDomain = ownerDomain;
        this.SID = SID;
        this.runtime = runtime;
        this.spaceLayout = spaceLayout;
        this.processType = processType;
        this.countThreads = countThreads;
        this.basePriority = basePriority;
    }

    public ProcessEntry(LinkedList<String> params) {
        this.processName = params.poll() + System.currentTimeMillis();
        this.processID = Integer.parseInt(params.poll());
        this.exePath = params.poll();
        this.parentProcessID = Integer.parseInt(params.poll());
        this.ownerName = params.poll();
        this.ownerDomain = params.poll();
        this.SID = params.poll();
        this.processType = params.poll();
        this.spaceLayout = params.poll();
        this.countThreads = Integer.parseInt(params.poll());
        this.basePriority = Integer.parseInt(params.poll());
        this.runtime = "UNKNOWN";
    }

    public void update(ProcessEntry pe) {
        this.processName = pe.getProcessName();
        this.processID = pe.getProcessID();
        this.exePath = pe.getExePath();
        this.parentProcessID = pe.getParentProcessID();
        this.ownerName = pe.getOwnerName();
        this.ownerDomain = pe.getOwnerDomain();
        this.SID = pe.getSID();
        this.runtime = pe.getRuntime();
        this.processType = pe.getProcessType();
        this.countThreads = pe.getCountThreads();
        this.basePriority = pe.getBasePriority();
    }

    public String getProcessName() {
        return processName;
    }

    public void setProcessName(String processName) {
        this.processName = processName;
    }

    public int getProcessID() {
        return processID;
    }

    public void setProcessID(int processID) {
        this.processID = processID;
    }

    public String getExePath() {
        return exePath;
    }

    public void setExePath(String exePath) {
        this.exePath = exePath;
    }

    public int getParentProcessID() {
        return parentProcessID;
    }

    public void setParentProcessID(int parentProcessID) {
        this.parentProcessID = parentProcessID;
    }

    public String getOwnerName() {
        return ownerName;
    }

    public void setOwnerName(String ownerName) {
        this.ownerName = ownerName;
    }

    public String getOwnerDomain() {
        return ownerDomain;
    }

    public void setOwnerDomain(String ownerDomain) {
        this.ownerDomain = ownerDomain;
    }

    public String getSID() {
        return SID;
    }

    public void setSID(String SID) {
        this.SID = SID;
    }

    public String getProcessType() {
        return processType;
    }

    public void setProcessType(String processType) {
        this.processType = processType;
    }

    public String getRuntime() {
        return runtime;
    }

    public void setRuntime(String runtime) {
        this.runtime = runtime;
    }

    public String getSpaceLayout() {
        return spaceLayout;
    }

    public void setSpaceLayout(String spaceLayout) {
        this.spaceLayout = spaceLayout;
    }

    public void setModuleEntries(List<ModuleEntry> moduleEntries) {
        this.moduleEntries = moduleEntries;
    }

    public int getBasePriority() {
        return basePriority;
    }

    public void setBasePriority(int basePriority) {
        this.basePriority = basePriority;
    }

    public int getCountThreads() {
        return countThreads;
    }

    public void setCountThreads(int countThreads) {
        this.countThreads = countThreads;
    }

    public void addModule(ModuleEntry moduleEntry) {
        moduleEntries.add(moduleEntry);
    }

    public List<ModuleEntry> getModuleEntries() {
        return moduleEntries;
    }

    @Override
    public int compareTo(ProcessEntry o) {
        return Integer.compare(this.processID, o.getProcessID());
    }
}
