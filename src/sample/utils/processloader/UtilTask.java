package sample.utils.processloader;

public class UtilTask {
    public static final int GET_PROCESS_INTEGRITY_LEVEL = 0x0;
    public static final int SET_PROCESS_INTEGRITY_LEVEL = 0x1;
    public static final int GET_PROCESS_PRIVILEGES      = 0x2;
    public static final int SET_PROCESS_PRIVILEGES      = 0x3;
    public static final int GET_FILE_ACL                = 0x4;
    public static final int SET_FILE_ACL                = 0x5;
    public static final int GET_FILE_OWNER              = 0x6;
    public static final int SET_FILE_OWNER              = 0x7;
    public static final int GET_FILE_PRIVILEGES         = 0x8;
    public static final int SET_FILE_PRIVILEGES         = 0x9;

    private int type;
    private String data;

    public UtilTask(String data, int type) {
        this.data = data;
        this.type = type;
    }

    public int getType() {
        return type;
    }
}
