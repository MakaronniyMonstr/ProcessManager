package sample.utils.processloader;

public class ProcessModifyTask implements Comparable<ProcessModifyTask> {
    public static final int ADD    = 0x0;
    public static final int REMOVE = 0x1;
    public static final int UPDATE = 0x2;

    private int type;
    private ProcessEntry processEntry;

    public ProcessModifyTask(ProcessEntry processEntry, int type) {
        this.processEntry = processEntry;
        this.type = type;
    }

    public int getType() {
        return type;
    }

    public ProcessEntry getProcessEntry() {
        return processEntry;
    }

    @Override
    public int compareTo(ProcessModifyTask o) {
        return ((Integer)this.processEntry.getProcessID())
                .compareTo(o.processEntry.getProcessID());
    }
}
