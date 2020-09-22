package sample.utils.processloader;

import javafx.beans.property.IntegerProperty;
import javafx.beans.property.SimpleIntegerProperty;
import javafx.beans.property.SimpleStringProperty;
import javafx.beans.property.StringProperty;

import java.util.ArrayList;
import java.util.List;
import java.util.Objects;

public class ProcessEntry {

    private StringProperty processName;
    private IntegerProperty processID;
    private StringProperty exePath;
    private IntegerProperty parentProcessID;
    //Owner and SID information
    private StringProperty ownerName;
    private StringProperty ownerDomain;
    private StringProperty SID;
    //Process type (32/64 bit)
    private StringProperty processType;
    //Native/CLR.NET
    private StringProperty runtime;
    //DEP/ASLR
    private StringProperty spaceLayout;
    private List<ModuleEntry> moduleEntries = new ArrayList<>();

    //Additional Information
    private IntegerProperty basePriority;
    private IntegerProperty countThreads;


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
        this.processName = new SimpleStringProperty(name);
        this.processID = new SimpleIntegerProperty(processID);
        this.exePath = new SimpleStringProperty(exePath);
        this.parentProcessID = new SimpleIntegerProperty(parentProcessID);
        this.ownerName = new SimpleStringProperty(ownerName);
        this.ownerDomain = new SimpleStringProperty(ownerDomain);
        this.SID = new SimpleStringProperty(SID);
        this.runtime = new SimpleStringProperty(runtime);
        this.spaceLayout = new SimpleStringProperty(spaceLayout);
        this.processType = new SimpleStringProperty(processType);
        this.countThreads = new SimpleIntegerProperty(countThreads);
        this.basePriority = new SimpleIntegerProperty(basePriority);
    }

    public ProcessEntry(String[] params) {
        this.processName = new SimpleStringProperty(params[0]);
        this.processID = new SimpleIntegerProperty(Integer.parseInt(params[1]));
        this.exePath = new SimpleStringProperty(params[2]);
        this.parentProcessID = new SimpleIntegerProperty(Integer.parseInt(params[3]));
        this.ownerName = new SimpleStringProperty(params[4]);
        this.ownerDomain = new SimpleStringProperty(params[5]);
        this.SID = new SimpleStringProperty(params[6]);
        this.processType = new SimpleStringProperty(params[7]);
        this.runtime = new SimpleStringProperty(params[8]);
        this.countThreads = new SimpleIntegerProperty(Integer.parseInt(params[9]));
        this.basePriority = new SimpleIntegerProperty(Integer.parseInt(params[10]));
        this.spaceLayout = new SimpleStringProperty("UNKNOWN");
    }

    public void update(ProcessEntry pe) {
        this.processName.setValue(pe.getProcessName());
        this.processID.setValue(pe.getProcessID());
        this.exePath.setValue(pe.getExePath());
        this.parentProcessID.setValue(pe.getParentProcessID());
        this.ownerName.setValue(pe.getOwnerName());
        this.ownerDomain.setValue(pe.getOwnerDomain());
        this.SID.setValue(pe.getSID());
        this.runtime.setValue(pe.getRuntime());
        this.processType.setValue(pe.getProcessType());
        this.countThreads.setValue(pe.getCountThreads());
        this.basePriority.setValue(pe.getBasePriority());
    }

    public void setBasePriority(int basePriority) {
        this.basePriority.setValue(basePriority);
    }

    public void setExePath(String path) {
        this.exePath.setValue(path);
    }

    public void setProcessName(String name) {
        this.processName.setValue(name);
    }

    public String getProcessName() {
        return processName.get();
    }

    public StringProperty getProcessNameProperty() {
        return processName;
    }

    public int getProcessID() {
        return processID.get();
    }

    public IntegerProperty getProcessIDProperty() {
        return processID;
    }

    public String getExePath() {
        return exePath.get();
    }

    public StringProperty getExePathProperty() {
        return exePath;
    }

    public int getParentProcessID() {
        return parentProcessID.get();
    }

    public IntegerProperty getParentProcessIDProperty() {
        return parentProcessID;
    }

    public String getOwnerName() {
        return ownerName.get();
    }

    public StringProperty getOwnerNameProperty() {
        return ownerName;
    }

    public String getOwnerDomain() {
        return ownerDomain.get();
    }

    public StringProperty getOwnerDomainProperty() {
        return ownerDomain;
    }

    public String getSID() {
        return SID.get();
    }

    public StringProperty getSIDProperty() {
        return SID;
    }

    public String getProcessType() {
        return processType.get();
    }

    public String getRuntime() {
        return runtime.get();
    }

    public StringProperty getRuntimeProperty() {
        return runtime;
    }

    public String getSpaceLayout() {
        return spaceLayout.get();
    }

    public StringProperty getSpaceLayoutProperty() {
        return spaceLayout;
    }

    public int getBasePriority() {
        return basePriority.get();
    }

    public IntegerProperty getBasePriorityProperty() {
        return basePriority;
    }

    public int getCountThreads() {
        return countThreads.get();
    }

    public IntegerProperty getCountThreadsProperty() {
        return countThreads;
    }

    public void addModule(ModuleEntry moduleEntry) {
        moduleEntries.add(moduleEntry);
    }

    public List<ModuleEntry> getModuleEntries() {
        return moduleEntries;
    }

    @Override
    public boolean equals(Object o) {
        if (this == o) return true;
        if (o == null || getClass() != o.getClass()) return false;
        ProcessEntry that = (ProcessEntry) o;

        return processName.get().equals(that.processName.get()) &&
                processID.get() == that.processID.get() &&
                exePath.get().equals(exePath.get()) &&
                parentProcessID.get() == that.parentProcessID.get() &&
                ownerName.get().equals(that.ownerName.get()) &&
                ownerDomain.get().equals(that.ownerDomain.get()) &&
                SID.get().equals(that.SID.get()) &&
                processType.get().equals(that.processType.get()) &&
                runtime.get().equals(that.runtime.get()) &&
                spaceLayout.get().equals(that.spaceLayout.get()) &&
                basePriority.get() == that.basePriority.get() &&
                countThreads.get() == that.countThreads.get();
    }

    @Override
    public int hashCode() {
        return Objects.hash(
                processName.get(),
                processID.get(),
                exePath.get(),
                parentProcessID.get(),
                ownerName.get(),
                ownerDomain.get(),
                SID.get(),
                processType.get(),
                runtime.get(),
                spaceLayout.get(),
                basePriority.get(),
                countThreads.get());
    }

    public boolean isUpdated(ProcessEntry processEntry) {
        if (processEntry == null)
            return false;

        return processName.get().equals(processEntry.processName.get()) &&
                processID.get() == processEntry.processID.get() &&
                exePath.get().equals(exePath.get()) &&
                parentProcessID.get() == processEntry.parentProcessID.get() &&
                ownerName.get().equals(processEntry.ownerName.get()) &&
                ownerDomain.get().equals(processEntry.ownerDomain.get()) &&
                SID.get().equals(processEntry.SID.get()) &&
                processType.get().equals(processEntry.processType.get()) &&
                runtime.get().equals(processEntry.runtime.get()) &&
                spaceLayout.get().equals(processEntry.spaceLayout.get()) &&
                (basePriority.get() != processEntry.basePriority.get() ||
                countThreads.get() != processEntry.countThreads.get());
    }
}
