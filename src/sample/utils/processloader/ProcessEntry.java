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

        this.processName = new SimpleStringProperty(
                params[1].substring(params[1].lastIndexOf('\\'))
        );
        this.processID = new SimpleIntegerProperty(Integer.parseInt(params[0]));
        this.exePath = new SimpleStringProperty(params[1]);
        this.parentProcessID = new SimpleIntegerProperty(Integer.parseInt(params[2]));
        this.ownerName = new SimpleStringProperty(params[3]);
        this.ownerDomain = new SimpleStringProperty(params[4]);
        this.SID = new SimpleStringProperty(params[5]);
        this.processType = new SimpleStringProperty(params[6]);
        this.spaceLayout = new SimpleStringProperty(params[7]);
        this.countThreads = new SimpleIntegerProperty(Integer.parseInt(params[8]));
        this.basePriority = new SimpleIntegerProperty(Integer.parseInt(params[9]));
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

    public String getProcessName() {
        return processName.get();
    }

    public StringProperty getProcessNameProperty() {
        return processName;
    }

    public void setProcessName(String processName) {
        this.processName.set(processName);
    }

    public int getProcessID() {
        return processID.get();
    }

    public IntegerProperty getProcessIDProperty() {
        return processID;
    }

    public void setProcessID(int processID) {
        this.processID.set(processID);
    }

    public String getExePath() {
        return exePath.get();
    }

    public StringProperty getExePathProperty() {
        return exePath;
    }

    public void setExePath(String exePath) {
        this.exePath.set(exePath);
    }

    public int getParentProcessID() {
        return parentProcessID.get();
    }

    public IntegerProperty getParentProcessIDProperty() {
        return parentProcessID;
    }

    public void setParentProcessID(int parentProcessID) {
        this.parentProcessID.set(parentProcessID);
    }

    public String getOwnerName() {
        return ownerName.get();
    }

    public StringProperty getOwnerNameProperty() {
        return ownerName;
    }

    public void setOwnerName(String ownerName) {
        this.ownerName.set(ownerName);
    }

    public String getOwnerDomain() {
        return ownerDomain.get();
    }

    public StringProperty getOwnerDomainProperty() {
        return ownerDomain;
    }

    public void setOwnerDomain(String ownerDomain) {
        this.ownerDomain.set(ownerDomain);
    }

    public String getSID() {
        return SID.get();
    }

    public StringProperty getSIDProperty() {
        return SID;
    }

    public void setSID(String SID) {
        this.SID.set(SID);
    }

    public String getProcessType() {
        return processType.get();
    }

    public StringProperty getProcessTypeProperty() {
        return processType;
    }

    public void setProcessType(String processType) {
        this.processType.set(processType);
    }

    public String getRuntime() {
        return runtime.get();
    }

    public StringProperty getRuntimeProperty() {
        return runtime;
    }

    public void setRuntime(String runtime) {
        this.runtime.set(runtime);
    }

    public String getSpaceLayout() {
        return spaceLayout.get();
    }

    public StringProperty getSpaceLayoutProperty() {
        return spaceLayout;
    }

    public void setSpaceLayout(String spaceLayout) {
        this.spaceLayout.set(spaceLayout);
    }

    public void setModuleEntries(List<ModuleEntry> moduleEntries) {
        this.moduleEntries = moduleEntries;
    }

    public int getBasePriority() {
        return basePriority.get();
    }

    public IntegerProperty getBasePriorityProperty() {
        return basePriority;
    }

    public void setBasePriority(int basePriority) {
        this.basePriority.set(basePriority);
    }

    public int getCountThreads() {
        return countThreads.get();
    }

    public IntegerProperty getCountThreadsProperty() {
        return countThreads;
    }

    public void setCountThreads(int countThreads) {
        this.countThreads.set(countThreads);
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

        return processName.equals(that.processName) &&
                processID.equals(that.processID) &&
                parentProcessID.equals(that.parentProcessID) &&
                basePriority.equals(that.basePriority) &&
                exePath.equals(that.exePath);
    }

    @Override
    public int hashCode() {
        return Objects.hash(processName, processID, parentProcessID, basePriority, exePath);
    }
}
