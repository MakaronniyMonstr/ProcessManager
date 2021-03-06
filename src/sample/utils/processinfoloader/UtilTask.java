package sample.utils.processinfoloader;

import java.util.LinkedList;
import java.util.List;

public class UtilTask {
    public static final int GET_PROCESSES_LIST          = 0x0;
    public static final int GET_MODULES_LIST            = 0x1;
    public static final int GET_PROCESS_INTEGRITY_LEVEL = 0x2;
    public static final int SET_PROCESS_INTEGRITY_LEVEL = 0x3;
    public static final int GET_PROCESS_PRIVILEGES      = 0x4;
    public static final int SET_PROCESS_PRIVILEGES      = 0x5;
    public static final int GET_FILE_ACL                = 0x6;
    public static final int SET_FILE_ACL                = 0x7;
    public static final int GET_FILE_OWNER              = 0x8;
    public static final int SET_FILE_OWNER              = 0x9;
    public static final int GET_FILE_INTEGRITY_LEVEL    = 0xA;
    public static final int SET_FILE_INTEGRITY_LEVEL    = 0xB;

    private static String stringCommands[] = {
            "-pl",//printProcessList
            "-ml",//printModuleList
            "-pip",//get process inegr level
            "-pis",//set process inegr level
            "-ppp",//get process privill
            "-pps",//Set process privileges
            "-fap",//printFileAcl
            "-fas",//setFileAcl
            "-fop",//printFileOwner
            "-fos",//setFileOwner
            "-fip",//printFileIntegrityLevel
            "-fis"//setFileIntegrityLevel
    };

    public static String privileges[] = {
            "SeAssignPrimaryTokenPrivilege",
            "SeAuditPrivilege",
            "SeBackupPrivilege",
            "SeChangeNotifyPrivilege",
            "SeCreateGlobalPrivilege",
            "SeCreatePagefilePrivilege",
            "SeCreatePermanentPrivilege",
            "SeCreateSymbolicLinkPrivilege",
            "SeCreateTokenPrivilege",
            "SeDebugPrivilege",
            "SeDelegateSessionUserImpersonatePrivilege",
            "SeEnableDelegationPrivilege",
            "SeImpersonatePrivilege",
            "SeIncreaseBasePriorityPrivilege",
            "SeIncreaseQuotaPrivilege",
            "SeIncreaseWorkingSetPrivilege",
            "SeLoadDriverPrivilege",
            "SeLockMemoryPrivilege",
            "SeMachineAccountPrivilege",
            "SeManageVolumePrivilege",
            "SeProfileSingleProcessPrivilege",
            "SeRelabelPrivilege",
            "SeRemoteShutdownPrivilege",
            "SeRestorePrivilege",
            "SeSecurityPrivilege",
            "SeShutdownPrivilege",
            "SeSyncAgentPrivilege",
            "SeSystemEnvironmentPrivilege",
            "SeSystemProfilePrivilege",
            "SeSystemtimePrivilege",
            "SeTakeOwnershipPrivilege",
            "SeTcbPrivilege",
            "SeTimeZonePrivilege",
            "SeTrustedCredManAccessPrivilege",
            "SeUndockPrivilege",
            "SeUnsolicitedInputPrivilege"
    };

    private int command;
    private List<String> data;

    public UtilTask(List<String> data, int command) {
        this.data = data;
        this.command = command;
    }

    public static String commandToString(int command) {
        return stringCommands[command];
    }

    public UtilTask(int command, String data) {
        this.data = new LinkedList<>();
        this.data.add(data);
        this.command = command;
    }

    public List<String> getData() {
        return data;
    }

    public void addData(String data) {
        this.data.add(data);
    }


    public int getCommand() {
        return command;
    }

    public void setCommand(int command) {
        this.command = command;
    }

    public String getStringData()
    {
        StringBuilder stringBuilder = new StringBuilder();
        data.forEach(s -> {
            stringBuilder.append(s);
        });

        return stringBuilder.toString();
    }

    public String getStringCommand() {
        return stringCommands[getCommand()] + " " + data.get(0);
    }
}
