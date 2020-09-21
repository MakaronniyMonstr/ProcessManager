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
    private IntegerProperty parentProcessID;
    private IntegerProperty basePriority;
    private StringProperty exePath;

    private List<ModuleEntry> moduleEntries = new ArrayList<>();

    //Debug constructor
    public ProcessEntry(
            String name,
            int processID,
            int parentProcessID,
            int basePriority,
            String exePath)
    {
        this.processName = new SimpleStringProperty(name);
        this.processID = new SimpleIntegerProperty(processID);
        this.parentProcessID = new SimpleIntegerProperty(parentProcessID);
        this.basePriority = new SimpleIntegerProperty(basePriority);
        this.exePath = new SimpleStringProperty(exePath);
    }

    public ProcessEntry(String[] params) {
        this.processName = new SimpleStringProperty(
                params[4].substring(params[3].lastIndexOf('\\'))
        );
        this.processID = new SimpleIntegerProperty(Integer.parseInt(params[0]));
        this.parentProcessID = new SimpleIntegerProperty(Integer.parseInt(params[1]));
        this.basePriority = new SimpleIntegerProperty(Integer.parseInt(params[2]));
        this.exePath = new SimpleStringProperty(params[3]);
    }

    public ProcessEntry(ProcessEntry pe) {
        this.processName = new SimpleStringProperty(pe.getProcessName());
        this.processID = new SimpleIntegerProperty(pe.getProcessID());
        this.parentProcessID = new SimpleIntegerProperty(pe.getParentProcessID());
        this.basePriority = new SimpleIntegerProperty(pe.getBasePriority());
        this.exePath = new SimpleStringProperty(pe.getExePath());
    }

    public ProcessEntry() {
        this.processName = new SimpleStringProperty();
        this.processID = new SimpleIntegerProperty();
        this.parentProcessID = new SimpleIntegerProperty();
        this.basePriority = new SimpleIntegerProperty();
        this.exePath = new SimpleStringProperty();
    }

    public void update(String[] params) {
        this.processName.setValue(
                params[4].substring(params[3].lastIndexOf('\\'))
        );
        this.processID.setValue(Integer.parseInt(params[0]));
        this.parentProcessID.setValue(Integer.parseInt(params[1]));
        this.basePriority.setValue(Integer.parseInt(params[2]));
        this.exePath.setValue(params[3]);
    }

    public void update(ProcessEntry pe) {
        this.processName.setValue(pe.getProcessName());
        this.processID.setValue(pe.getProcessID());
        this.parentProcessID.setValue(pe.getParentProcessID());
        this.basePriority.setValue(pe.getBasePriority());
        this.exePath.setValue(pe.getExePath());
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
