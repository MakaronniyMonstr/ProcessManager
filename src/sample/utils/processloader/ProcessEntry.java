package sample.utils.processloader;

import javafx.beans.property.IntegerProperty;
import javafx.beans.property.SimpleIntegerProperty;
import javafx.beans.property.SimpleStringProperty;
import javafx.beans.property.StringProperty;

import java.util.ArrayList;
import java.util.List;

public class ProcessEntry {

    private StringProperty processName;
    private IntegerProperty processID;
    private IntegerProperty moduleID;
    private IntegerProperty parentProcessID;
    private IntegerProperty basePriority;
    private StringProperty exePath;

    private List<ModuleEntry> moduleEntries = new ArrayList<>();


    public ProcessEntry(String name, int processID, int moduleID, int parentProcessID, int basePriority, String exePath)
    {
        this.processName = new SimpleStringProperty(name);
        this.processID = new SimpleIntegerProperty(processID);
        this.moduleID = new SimpleIntegerProperty(moduleID);
        this.parentProcessID = new SimpleIntegerProperty(parentProcessID);
        this.basePriority = new SimpleIntegerProperty(basePriority);
        this.exePath = new SimpleStringProperty(exePath);
    }

    public void setProcessName(String name)
    {
        this.processName.set(name);
    }

    public String getProcessName()
    {
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

    public void setProcessID(int processID) {
        this.processID.set(processID);
    }

    public int getModuleID() {
        return moduleID.get();
    }

    public void setModuleID(int moduleID) {
        this.moduleID.set(moduleID);
    }

    public int getParentProcessID() {
        return parentProcessID.get();
    }

    public void setParentProcessID(int parentProcessID) {
        this.parentProcessID.set(parentProcessID);
    }

    public int getBasePriority() {
        return basePriority.get();
    }

    public void setBasePriority(int basePriority) {
        this.basePriority.set(basePriority);
    }

    public void setExePath(String exePath) {
        this.exePath.set(exePath);
    }

    public String getExePath() {
        return exePath.get();
    }

    public StringProperty getExePathProperty() {
        return exePath;
    }

    public void addModule(ModuleEntry moduleEntry) {
        moduleEntries.add(moduleEntry);
    }

    public List<ModuleEntry> getModuleEntries() {
        return moduleEntries;
    }
}
