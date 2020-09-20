package sample.utils.processloader;

public class EntryTask {
    private EntryTaskType type;
    private ProcessEntry processEntry;

    public enum EntryTaskType {
        ADD_PROCESS,
        ADD_MODULE,
        REMOVE,
        ERROR
    }

    public EntryTask(EntryTaskType type, ProcessEntry processEntry) {
        this.type = type;
        this.processEntry = processEntry;
    }

    public EntryTaskType getType() {
        return type;
    }

    public ProcessEntry getProcessEntry() {
        return processEntry;
    }
}
