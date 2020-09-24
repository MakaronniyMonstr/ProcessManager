package sample.utils.processinfoloader;

import java.util.Objects;

public class ModuleEntry {
    private int processID;
    private String moduleBaseAddress;
    private int moduleSize;
    private String moduleName;
    private String exePath;

    public int getProcessID() {
        return processID;
    }

    public void setProcessID(int processID) {
        this.processID = processID;
    }

    public String getModuleBaseAddress() {
        return moduleBaseAddress;
    }

    public void setModuleBaseAddress(String moduleBaseAddress) {
        this.moduleBaseAddress = moduleBaseAddress;
    }

    public int getModuleSize() {
        return moduleSize;
    }

    public void setModuleSize(int moduleSize) {
        this.moduleSize = moduleSize;
    }

    public String getModuleName() {
        return moduleName;
    }

    public void setModuleName(String moduleName) {
        this.moduleName = moduleName;
    }

    public String getExePath() {
        return exePath;
    }

    public void setExePath(String exePath) {
        this.exePath = exePath;
    }

    @Override
    public boolean equals(Object o) {
        if (this == o) return true;
        if (o == null || getClass() != o.getClass()) return false;
        ModuleEntry that = (ModuleEntry) o;
        return processID == that.processID &&
                moduleName.equals(that.moduleName) &&
                exePath.equals(that.exePath);
    }

    @Override
    public int hashCode() {
        return Objects.hash(processID, moduleName, exePath);
    }
}
