package sample.Utils;

public class ModuleEntry {
    private int moduleID;
    private int processID;
    private int globalCountUsage;
    private int processCountUsage;
    private String moduleBaseAddress;
    private int moduleSize;
    private String moduleName;
    private String exePath;

    public int getModuleID() {
        return moduleID;
    }

    public void setModuleID(int moduleID) {
        this.moduleID = moduleID;
    }

    public int getProcessID() {
        return processID;
    }

    public void setProcessID(int processID) {
        this.processID = processID;
    }

    public int getGlobalCountUsage() {
        return globalCountUsage;
    }

    public void setGlobalCountUsage(int globalCountUsage) {
        this.globalCountUsage = globalCountUsage;
    }

    public int getProcessCountUsage() {
        return processCountUsage;
    }

    public void setProcessCountUsage(int processCountUsage) {
        this.processCountUsage = processCountUsage;
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
}
