package sample.utils.processloader;

public class EntryTask {
    private Type type;
    private ProcessEntry processEntry;

    public enum Type {
        UPDATE_PROCESS,
        UPDATE_MODULES,
        REMOVE
    }

    public EntryTask(Type type, ProcessEntry processEntry) {
        this.type = type;
        this.processEntry = processEntry;
    }
}
