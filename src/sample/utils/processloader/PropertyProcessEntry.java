package sample.utils.processloader;

import javafx.beans.property.IntegerProperty;
import javafx.beans.property.SimpleIntegerProperty;
import javafx.beans.property.SimpleStringProperty;
import javafx.beans.property.StringProperty;

import java.util.ArrayList;
import java.util.List;

public class PropertyProcessEntry {
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

    public PropertyProcessEntry(ProcessEntry processEntry) {
        this.processName = new SimpleStringProperty(processEntry.getProcessName());
        this.processID = new SimpleIntegerProperty(processEntry.getProcessID());
        this.exePath = new SimpleStringProperty(processEntry.getExePath());
        this.parentProcessID = new SimpleIntegerProperty(processEntry.getParentProcessID());
        this.ownerName = new SimpleStringProperty(processEntry.getOwnerDomain());
        this.ownerDomain = new SimpleStringProperty(processEntry.getOwnerDomain());
        this.SID = new SimpleStringProperty(processEntry.getSID());
        this.processType = new SimpleStringProperty(processEntry.getProcessType());
        this.runtime = new SimpleStringProperty(processEntry.getRuntime());
        this.spaceLayout = new SimpleStringProperty(processEntry.getSpaceLayout());
        this.basePriority = new SimpleIntegerProperty(processEntry.getBasePriority());
        this.countThreads = new SimpleIntegerProperty(processEntry.getCountThreads());
    }

    public void update(ProcessEntry processEntry) {
        this.processName.setValue(processEntry.getProcessName());
        this.processID.setValue(processEntry.getProcessID());
        this.exePath.setValue(processEntry.getExePath());
        this.parentProcessID.setValue(processEntry.getParentProcessID());
        this.ownerName.setValue(processEntry.getOwnerName());
        this.ownerDomain.setValue(processEntry.getOwnerDomain());
        this.SID.setValue(processEntry.getSID());
        this.processType.setValue(processEntry.getProcessType());
        this.runtime.setValue(processEntry.getRuntime());
        this.spaceLayout.setValue(processEntry.getSpaceLayout());
        this.basePriority.setValue(processEntry.getBasePriority());
        this.countThreads.setValue(processEntry.getCountThreads());
    }

    public String getProcessName() {
        return processName.get();
    }

    public StringProperty processNameProperty() {
        return processName;
    }

    public int getProcessID() {
        return processID.get();
    }

    public IntegerProperty processIDProperty() {
        return processID;
    }

    public String getExePath() {
        return exePath.get();
    }

    public StringProperty exePathProperty() {
        return exePath;
    }

    public int getParentProcessID() {
        return parentProcessID.get();
    }

    public IntegerProperty parentProcessIDProperty() {
        return parentProcessID;
    }

    public String getOwnerName() {
        return ownerName.get();
    }

    public StringProperty ownerNameProperty() {
        return ownerName;
    }

    public String getOwnerDomain() {
        return ownerDomain.get();
    }

    public StringProperty ownerDomainProperty() {
        return ownerDomain;
    }

    public String getSID() {
        return SID.get();
    }

    public StringProperty SIDProperty() {
        return SID;
    }

    public String getProcessType() {
        return processType.get();
    }

    public StringProperty processTypeProperty() {
        return processType;
    }

    public String getRuntime() {
        return runtime.get();
    }

    public StringProperty runtimeProperty() {
        return runtime;
    }

    public String getSpaceLayout() {
        return spaceLayout.get();
    }

    public StringProperty spaceLayoutProperty() {
        return spaceLayout;
    }

    public List<ModuleEntry> getModuleEntries() {
        return moduleEntries;
    }

    public void setModuleEntries(List<ModuleEntry> moduleEntries) {
        this.moduleEntries = moduleEntries;
    }

    public int getBasePriority() {
        return basePriority.get();
    }

    public IntegerProperty basePriorityProperty() {
        return basePriority;
    }

    public int getCountThreads() {
        return countThreads.get();
    }

    public IntegerProperty countThreadsProperty() {
        return countThreads;
    }
}
