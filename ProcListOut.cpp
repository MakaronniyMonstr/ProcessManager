#define _CRT_SECURE_NO_WARNINGS

#include <windows.h>
#include <stdio.h>
#include <wchar.h>
#include <locale.h>
#include <tlhelp32.h>
#include <sddl.h>
#include <aclapi.h>
#include <psapi.h>



#define MAX_NAME 256

void print_binary(DWORD number)
{
    if (number) {
        print_binary(number >> 1);
        putc((number & 1) ? '1' : '0', stdout);
    }
}

VOID printProcessMiligationPolicy(DWORD CONST processID);

BOOL is64bitSystem() {
    return sizeof(void*) * 8 == 64;
}

BOOL is64bitProcess(DWORD CONST processID) {
    HANDLE hProcess;
    BOOL is64bitSys = is64bitSystem();
    BOOL is64bitProcess;

    if (hProcess = OpenProcess(PROCESS_QUERY_INFORMATION, FALSE, processID)) {

        if (is64bitSys) {
            if (IsWow64Process(hProcess, &is64bitProcess)) {
                is64bitProcess =  FALSE;
            }
            else {
                is64bitProcess = TRUE;
            }
        }
        else {
            is64bitProcess = FALSE;
        }
    }
    else {
        is64bitProcess = is64bitSys;
    }

    CloseHandle(hProcess);
    return is64bitProcess;
}

VOID setProcessPrivilege(DWORD CONST processID, const char* lpszPrivilege, BOOL bEnablePrivilege)
{

    // Token privilege structure
    TOKEN_PRIVILEGES tp;
    // Used by local system to identify the privilege
    LUID luid;
    HANDLE hToken = NULL, hProcess = NULL;

    if ((hProcess = OpenProcess(PROCESS_QUERY_INFORMATION, FALSE, processID)) != NULL)
    {
        if (OpenProcessToken(hProcess, TOKEN_ADJUST_PRIVILEGES, &hToken))
        {

            if (!LookupPrivilegeValueA(
                NULL,                   // lookup privilege on local system
                lpszPrivilege,          // privilege to lookup
                &luid))                 // receives LUID of privilege
            {
                printf("WRONG PRIVILEGE NAME\n");
                goto stop;
            }

            // Number of privilege
            tp.PrivilegeCount = 1;
            // Assign luid to the 1st count
            tp.Privileges[0].Luid = luid;

            // Enable/disable
            if (bEnablePrivilege) {
                // Enable
                tp.Privileges[0].Attributes = SE_PRIVILEGE_ENABLED;
            }
            else {
                // Disable
                tp.Privileges[0].Attributes = SE_PRIVILEGE_REMOVED;
            }

            // Adjusting the new privilege
            if (!AdjustTokenPrivileges(
                hToken,
                FALSE,      // If TRUE, function disables all privileges, if FALSE the function modifies privilege based on the tp
                &tp,
                sizeof(TOKEN_PRIVILEGES),
                (PTOKEN_PRIVILEGES)NULL,
                (PDWORD)NULL))
            {
                printf("CAN'T SET THIS PRIVILEGE\n");
                goto stop;
            }

            DWORD a = GetLastError();
            if (GetLastError() == ERROR_NOT_ALL_ASSIGNED) {
                printf("ACCESS DENIED\n");
                goto stop;
            }

            printf("SUCCESS");
        }
        else
        {
            printf("ACCESS DENIED\n");
        }
    }
    else 
    {
        printf("ACCESS DENIED\n");
    }

stop:

    if (hProcess)
        CloseHandle(hProcess);
    if (hToken)
        CloseHandle(hToken);

    return;
}
VOID printProcessPrivileges(DWORD CONST processID) {
    DWORD i, dwSize = 0, dwResult = 0;
    PTOKEN_PRIVILEGES pPrivelegesInfo = NULL;
    char lpName[MAX_NAME] = { 0 };
    HANDLE hProcess = NULL, hToken = NULL;
    /*
    if (!OpenProcessToken(GetCurrentProcess(), TOKEN_QUERY, &hToken))
    {
        printf("OpenProcessToken Error %u\n", GetLastError());
        return FALSE;
    }
    */

    if ((hProcess = OpenProcess(PROCESS_QUERY_INFORMATION, FALSE, processID)) != NULL)
    {
        if (OpenProcessToken(hProcess, TOKEN_ADJUST_PRIVILEGES | TOKEN_QUERY, &hToken))
        {

            // Call GetTokenInformation to get the buffer size.
            if (!GetTokenInformation(hToken, TokenPrivileges, NULL, dwSize, &dwSize))
            {
                // Allocate the buffer.
                pPrivelegesInfo = (PTOKEN_PRIVILEGES)GlobalAlloc(GPTR, dwSize);

                // Call GetTokenInformation again to get the group information.
                if (GetTokenInformation(hToken, TokenPrivileges, pPrivelegesInfo,
                    dwSize, &dwSize))
                {
                    for (i = 0; i < pPrivelegesInfo->PrivilegeCount; i++) {
                        dwSize = MAX_NAME;

                        if (LookupPrivilegeNameA(NULL, &pPrivelegesInfo->Privileges[i].Luid, lpName, &dwSize)) {
                            printf("%s\n", lpName);
                        }
                    }
                }
            }
            else
            {
                printf("ACCESS DENIED\n");
            }
        }
        else
        {
            printf("ACCESS DENIED\n");
        }
    }
    else
    {
        printf("ACCESS DENIED\n");
    }
    
    if (pPrivelegesInfo)
        GlobalFree(pPrivelegesInfo);
    if (hProcess)
        CloseHandle(hProcess);
    if (hToken)
        CloseHandle(hToken);
}

VOID printProcessPath(DWORD processID)
{
    HANDLE hProcess = NULL;
    TCHAR filename[MAX_PATH];

    if ((hProcess = OpenProcess(PROCESS_QUERY_INFORMATION | PROCESS_VM_READ, FALSE, processID)) != NULL)
    {
        if (GetModuleFileNameEx(hProcess, NULL, filename, MAX_PATH))
        {
            printf("%ls\n", filename);
        }
        else
        {
            printf("UNKNOWN\n");
        }
    }
    else
    {
        printf("SYSTEM\n");
    }

    if (hProcess != NULL)
        CloseHandle(hProcess);
}

VOID printProcessOwner(DWORD CONST processID) {
    DWORD i, dwSize = 0, dwResult = 0;
    PTOKEN_OWNER pOwnerInfo = NULL;
    SID_NAME_USE SidType;
    LPSTR lpSid = NULL;
    char lpName[MAX_NAME] = "ACCESS DENIED";
    char lpDomain[MAX_NAME] = "ACCESS DENIED";
    HANDLE hProcess = NULL, hToken = NULL;

    if ((hProcess = OpenProcess(PROCESS_QUERY_INFORMATION, FALSE, processID)) != NULL)
    {
        if (OpenProcessToken(hProcess, TOKEN_ADJUST_PRIVILEGES | TOKEN_QUERY, &hToken))
        {

            // Call GetTokenInformation to get the buffer size.
            if (!GetTokenInformation(hToken, TokenOwner, NULL, dwSize, &dwSize))
            {

                // Allocate the buffer.
                pOwnerInfo = (PTOKEN_OWNER)GlobalAlloc(GPTR, dwSize);

                // Call GetTokenInformation again to get the group information.
                if (GetTokenInformation(hToken, TokenOwner, pOwnerInfo,
                    dwSize, &dwSize))
                {

                    if (ConvertSidToStringSidA(pOwnerInfo->Owner, &lpSid))
                    {

                        if (!LookupAccountSidA(NULL, pOwnerInfo->Owner,
                            lpName, &dwSize, lpDomain, &dwSize, &SidType))
                        {
                            dwResult = GetLastError();
                            if (dwResult == ERROR_NONE_MAPPED)
                                strcpy_s(lpName, dwSize, "NONE_MAPPED");
                        }
                    }
                }
            }
        }
    }
    DWORD a = GetLastError();

    printf("%s\n"
            "%s\n"
            "%s\n", 
            lpName, lpDomain, lpSid);

    if (pOwnerInfo)
        GlobalFree(pOwnerInfo);
    if (lpSid)
        GlobalFree(lpSid);
    if (hProcess)
        CloseHandle(hProcess);
    if (hToken)
        CloseHandle(hToken);
}

VOID printProcessMiligationPolicy(DWORD CONST processID) {
    HANDLE hProcess;
    PROCESS_MITIGATION_DEP_POLICY dep;
    PROCESS_MITIGATION_ASLR_POLICY aslr;

    if (is64bitProcess(processID))
    {
        printf("DEP\n");

        return;
    }

    if ((hProcess = OpenProcess(PROCESS_QUERY_INFORMATION, FALSE, processID)) != NULL)
    {

        if (GetProcessMitigationPolicy(hProcess, ProcessDEPPolicy, &dep, sizeof(dep)))
        {
            if (ProcessDEPPolicy) 
            {
                printf("DEP\n");
                if (hProcess)
                    CloseHandle(hProcess);

                return;
            }
        }

        if (GetProcessMitigationPolicy(hProcess, ProcessASLRPolicy, &aslr, sizeof(aslr)))
        {
            if (ProcessASLRPolicy)
            {
                printf("ASLR\n");
                if (hProcess)
                    CloseHandle(hProcess);

                return;
            }
        }
    }
    printf("UNKNOWN\n");

    if (hProcess)
        CloseHandle(hProcess);
}

VOID printProcessIntegrityLevel(DWORD CONST processID)
{
    DWORD i, dwSize = 0, dwResult = 0;
    PTOKEN_MANDATORY_LABEL pMandatoryInfo = NULL;
    char lpName[MAX_NAME] = { 0 };
    HANDLE hProcess = NULL, hToken = NULL;

    if ((hProcess = OpenProcess(PROCESS_QUERY_INFORMATION, FALSE, processID)) != NULL)
    {
        if (OpenProcessToken(hProcess, TOKEN_ADJUST_PRIVILEGES | TOKEN_QUERY, &hToken))
        {
            // Call GetTokenInformation to get the buffer size.
            if (!GetTokenInformation(hToken, TokenIntegrityLevel, NULL, dwSize, &dwSize))
            {
                // Allocate the buffer.
                pMandatoryInfo = (PTOKEN_MANDATORY_LABEL)GlobalAlloc(GPTR, dwSize);

                // Call GetTokenInformation again to get the group information.
                if (GetTokenInformation(hToken, TokenIntegrityLevel, pMandatoryInfo,
                    dwSize, &dwSize))
                {
                    DWORD dwIntegrityLevel = *GetSidSubAuthority(
                        pMandatoryInfo->Label.Sid, 
                        *GetSidSubAuthorityCount(pMandatoryInfo->Label.Sid) - 1
                        );

                    if (dwIntegrityLevel == SECURITY_MANDATORY_UNTRUSTED_RID)
                    {
                        printf("Untrusted\n");
                    }
                    else if (dwIntegrityLevel == SECURITY_MANDATORY_LOW_RID)
                    {
                        printf("Low\n");
                    }
                    else if (dwIntegrityLevel == SECURITY_MANDATORY_MEDIUM_RID)
                    {
                        printf("Medium\n");
                    }
                    else if (dwIntegrityLevel == SECURITY_MANDATORY_HIGH_RID)
                    {
                        printf("High\n");
                    }
                    else if (dwIntegrityLevel == SECURITY_MANDATORY_SYSTEM_RID)
                    {
                        printf("System\n");
                    }
                }
            }
            else
            {
                printf("ACCESS DENIED\n");
            }
        }
        else
        {
            printf("ACCESS DENIED\n");
        }
    }
    else
    {
        printf("ACCESS DENIED\n");
    }
}
VOID setProcessIntegrityLevel(DWORD CONST processID, const char* level)
{
    LPCWSTR SLowIntegritySid = L"S-1-16-4096";
    LPCWSTR SMediumIntegritySid = L"S-1-16-8192";
    LPCWSTR SHighIntegritySid = L"S-1-16-12288";
    LPCWSTR SSystemIntegritySid = L"S-1-16-16384";
    LPCWSTR wszIntegritySid;

    BOOL bRet;
    HANDLE hToken;
    HANDLE hNewToken;

    if (!strcmp(level, "Low"))
    {
        wszIntegritySid = L"S-1-16-4096";
    }
    else if (!strcmp(level, "Medium"))
    {
        wszIntegritySid = L"S-1-16-8192";
    }
    else if (!strcmp(level, "High"))
    {
        wszIntegritySid = L"S-1-16-12288";
    }
    else
    {
        printf("WRONG ARGS\n");
        return;
    }
    
    PSID pIntegritySid = NULL;
    TOKEN_MANDATORY_LABEL TIL = { 0 };
    TOKEN_MANDATORY_POLICY POL = { 0 };
    PROCESS_INFORMATION ProcInfo = { 0 };
    STARTUPINFO StartupInfo = { 0 };
    ULONG ExitCode = 0;

    if (OpenProcessToken(GetCurrentProcess(), MAXIMUM_ALLOWED, &hToken))
    {
        if (DuplicateTokenEx(hToken, MAXIMUM_ALLOWED, NULL, SecurityImpersonation, TokenPrimary, &hNewToken))
        {
            if (ConvertStringSidToSidW(wszIntegritySid, &pIntegritySid))
            {
                TIL.Label.Attributes = SE_GROUP_INTEGRITY;
                TIL.Label.Sid = pIntegritySid;

                // Set the process integrity level
                if (SetTokenInformation(hNewToken, TokenIntegrityLevel, &TIL, sizeof(TOKEN_MANDATORY_LABEL) + GetLengthSid(pIntegritySid)))
                {
                    POL.Policy = TOKEN_MANDATORY_POLICY_NO_WRITE_UP;
                    //   в следующей строке SetTokenInformation не возвращает 0 и ошибку 1314.
                    SetTokenInformation(hNewToken, TokenMandatoryPolicy, &POL, sizeof(TOKEN_MANDATORY_POLICY));
                    printf("%i\n", GetLastError());
                }

                LocalFree(pIntegritySid);
            }
            CloseHandle(hNewToken);
        }
        CloseHandle(hToken);
    }
    /*
    PSID sid;
    const char* sidStr = "S-1-16-4096";

    if (!strcmp(level, "Low"))
    {
        sidStr = "S-1-16-4096";
    }
    else if (!strcmp(level, "Medium"))
    {
        sidStr = "S-1-16-8192";
    }
    else if (!strcmp(level, "High"))
    {
        sidStr = "S-1-16-12288";
    }
    else
    {
        printf("WRONG ARGS\n");
        return;
    }

    ConvertStringSidToSidA(sidStr, &sid);

    DWORD returnLength = 0;
    //HANDLE hToken;
    HANDLE hProcess = OpenProcess(PROCESS_ALL_ACCESS, false, processID);// по номеру процесса получаю его дескриптор
    //HANDLE hProcess = OpenProcess(PROCESS_QUERY_INFORMATION, false, it->num_PID);
    PTOKEN_MANDATORY_LABEL mandatoryLabel = NULL;

    if (hProcess == NULL)
    {
        printf("ACCESS DENIED\n");
    }

    OpenProcessToken(hProcess, TOKEN_QUERY | TOKEN_ADJUST_DEFAULT, &hProcess);// создаю токен 
    //OpenProcessToken(hProcess, TOKEN_ADJUST_DEFAULT, &hToken);
    GetTokenInformation(hProcess, TokenIntegrityLevel, NULL, 0, &returnLength);// 902 // c NULL возвращает длину буффера
    returnLength += 500;
    mandatoryLabel = (PTOKEN_MANDATORY_LABEL)malloc(returnLength);
    GetTokenInformation(hProcess, TokenIntegrityLevel, mandatoryLabel, returnLength, &returnLength); //беру инфу о токене
    mandatoryLabel->Label.Attributes = SE_GROUP_INTEGRITY;
    mandatoryLabel->Label.Sid = sid;

    SetTokenInformation(hProcess, TokenIntegrityLevel, mandatoryLabel, returnLength);// вношу изменения в целостность
    CloseHandle(hProcess);

    printf("SUCCESS\n");
    */
}

BOOL printIsAdmin(HANDLE hToken) {
    DWORD i, dwSize = 0, dwResult = 0;
    PTOKEN_GROUPS pGroupInfo;
    SID_NAME_USE SidType;
    char lpName[MAX_NAME];
    char lpDomain[MAX_NAME];
    PSID pSID = NULL;
    SID_IDENTIFIER_AUTHORITY SIDAuth = SECURITY_NT_AUTHORITY;

    // Call GetTokenInformation to get the buffer size.
    if (!GetTokenInformation(hToken, TokenGroups, NULL, dwSize, &dwSize))
    {
        dwResult = GetLastError();
        if (dwResult != ERROR_INSUFFICIENT_BUFFER) {
            printf("GetTokenInformation Error %u\n", dwResult);
            return FALSE;
        }
    }

    // Allocate the buffer.

    pGroupInfo = (PTOKEN_GROUPS)GlobalAlloc(GPTR, dwSize);

    // Call GetTokenInformation again to get the group information.

    if (!GetTokenInformation(hToken, TokenGroups, pGroupInfo,
        dwSize, &dwSize))
    {
        printf("GetTokenInformation Error %u\n", GetLastError());
        return FALSE;
    }

    // Create a SID for the BUILTIN\Administrators group.
    if (!AllocateAndInitializeSid(&SIDAuth, 2,
        SECURITY_BUILTIN_DOMAIN_RID,
        DOMAIN_ALIAS_RID_ADMINS,
        0, 0, 0, 0, 0, 0,
        &pSID))
    {
        printf("AllocateAndInitializeSid Error %u\n", GetLastError());
        return FALSE;
    }

    // Loop through the group SIDs looking for the administrator SID.

    for (i = 0; i < pGroupInfo->GroupCount; i++)
    {
        if (EqualSid(pSID, pGroupInfo->Groups[i].Sid))
        {

            // Lookup the account name and print it.

            dwSize = MAX_NAME;
            if (!LookupAccountSidA(NULL, pGroupInfo->Groups[i].Sid,
                lpName, &dwSize, lpDomain,
                &dwSize, &SidType))
            {
                dwResult = GetLastError();
                if (dwResult == ERROR_NONE_MAPPED)
                    strcpy_s(lpName, dwSize, "NONE_MAPPED");
                else
                {
                    printf("LookupAccountSid Error %u\n", GetLastError());
                    return FALSE;
                }
            }
            printf("Current user is a member of the %s\\%s group\n",
                lpDomain, lpName);

            // Find out whether the SID is enabled in the token.
            if (pGroupInfo->Groups[i].Attributes & SE_GROUP_ENABLED)
                printf("The group SID is enabled.\n");
            else if (pGroupInfo->Groups[i].Attributes & SE_GROUP_USE_FOR_DENY_ONLY)
                printf("The group SID is a deny-only SID.\n");
            else
                printf("The group SID is not enabled.\n");
        }
    }

    if (pSID)
        FreeSid(pSID);
    if (pGroupInfo)
        GlobalFree(pGroupInfo);

    return TRUE;
}

VOID printModuleList(DWORD CONST dwProcessId) {
    MODULEENTRY32 meModuleEntry;
    TCHAR szBuff[1024];
    DWORD dwTemp;
    HANDLE CONST hSnapshot = CreateToolhelp32Snapshot(
        TH32CS_SNAPMODULE, dwProcessId);
    if (INVALID_HANDLE_VALUE == hSnapshot) {
        printf("ACCESS DENIED\n");
        return;
    }

    meModuleEntry.dwSize = sizeof(MODULEENTRY32);
    if (!Module32Next(hSnapshot, &meModuleEntry))
    {
        printf("NO DLLS\n");
    }

    Module32First(hSnapshot, &meModuleEntry);
    do {
        /*
        wsprintf(szBuff, L"  ba: %08X, bs: %08X, %s\r\n",
            meModuleEntry.modBaseAddr, meModuleEntry.modBaseSize,
            meModuleEntry.szModule);
        WriteConsole(hStdOut, szBuff, lstrlen(szBuff), &dwTemp, NULL);
        */
        printf("%ls\n", meModuleEntry.szModule);
    } while (Module32Next(hSnapshot, &meModuleEntry));

    CloseHandle(hSnapshot);
}

VOID printProcessList() {
    PROCESSENTRY32 peProcessEntry;
    DWORD dwTemp;

    HANDLE CONST hSnapshot = CreateToolhelp32Snapshot(
        TH32CS_SNAPPROCESS, 0);
    if (INVALID_HANDLE_VALUE == hSnapshot) {
        return;
    }

    peProcessEntry.dwSize = sizeof(PROCESSENTRY32);
    Process32First(hSnapshot, &peProcessEntry);
    do {
        //Process ID, Exe path, Parent ID
        printf("%ls\n", peProcessEntry.szExeFile);
        printf("%lu\n", peProcessEntry.th32ProcessID);
        printProcessPath(peProcessEntry.th32ProcessID);

        printf("%lu\n", peProcessEntry.th32ParentProcessID);
        //Owner name, domain, SID
        printProcessOwner(peProcessEntry.th32ProcessID);
        // 64x/32x
        printf("%s\n", is64bitProcess(peProcessEntry.th32ProcessID) ? "64x":"86x");
        //DEP/ASLR
        printProcessMiligationPolicy(peProcessEntry.th32ProcessID);
        //Threads count, base priority
        printf(
            "%u\n"
            "%u\n",
            peProcessEntry.cntThreads,
            peProcessEntry.pcPriClassBase);
        printf("\n");

    } while (Process32Next(hSnapshot, &peProcessEntry));

    CloseHandle(hSnapshot);
}

VOID printFileOwner(const char* path)
{
    LPSTR lpSid = (LPSTR) "UNKNOWN";
    PSID psid = NULL;
    PACL pl = NULL;
    PSECURITY_DESCRIPTOR pDescr;

    if (!GetNamedSecurityInfoA(path, SE_FILE_OBJECT, OWNER_SECURITY_INFORMATION, &psid, NULL, NULL, NULL, &pDescr))
    {

        SID_NAME_USE snu;
        char name[512] = { 0 }, domain[512] = { 0 };
        DWORD nameLen = 512, domainLen = 512;

        if (LookupAccountSidA(NULL, psid, name, &nameLen, domain, &domainLen, &snu))
        {
            ConvertSidToStringSidA(psid, &lpSid);

            printf("Name: %s\n"
                "Domain: %s\n"
                "Sid: %s\n",
                name, domain, lpSid);
        }
    }
    else
    {
        printf("Name: ERROR"
            "Domain: ERROR"
            "Sid: ERROR");
    }

    if (pDescr)
        LocalFree(pDescr);
    if (lpSid)
        LocalFree(lpSid);
}
VOID printFileAcl(const char* path)
{
    LPSTR lpSid = (LPSTR)"UNKNOWN_SID";
    PACL pl = NULL;
    PSECURITY_DESCRIPTOR pDescr;
    ACL_SIZE_INFORMATION aclSize;
    LPVOID aceInfo;

    if (!GetNamedSecurityInfoA(path, SE_FILE_OBJECT, DACL_SECURITY_INFORMATION, NULL, NULL, &pl, NULL, &pDescr))
    {

        GetAclInformation(pl, &aclSize, sizeof(aclSize), AclSizeInformation);

        for (DWORD i = 0; i < aclSize.AceCount; i++)
        {
            PSID psid = NULL;
            ACCESS_ALLOWED_ACE* accAllowedAce;
            ACE_HEADER* header;
            SID_NAME_USE snu;
            char name[512] = { 0 }, domain[512] = { 0 };
            DWORD nameLen = 512, domainLen = 512;

            GetAce(pl, i, &aceInfo);
            accAllowedAce = (ACCESS_ALLOWED_ACE*)aceInfo;
            psid = (PSID) &(accAllowedAce->SidStart);

            if (LookupAccountSidA(NULL, psid, name, &nameLen, domain, &domainLen, &snu))
            {
                ACCESS_MASK mask;

                mask = accAllowedAce->Mask;
                if (accAllowedAce->Header.AceType == ACCESS_DENIED_ACE_TYPE)
                {
                    printf("Denied for:\n");
                }
                else if (accAllowedAce->Header.AceType == ACCESS_ALLOWED_ACE_TYPE)
                {
                    printf("Allowed for:\n");
                }

                ConvertSidToStringSidA(psid, &lpSid);
                printf("%s\\%s %s\n", domain, name, lpSid);
                LocalFree(lpSid);

                if (mask & FILE_READ_ATTRIBUTES)
                {
                    printf("FILE_READ_ATTRIBUTES\n");
                }
                if (mask & FILE_READ_DATA)
                {
                    printf("FILE_READ_DATA\n");
                }
                if (mask & FILE_APPEND_DATA)
                {
                    printf("FILE_APPEND_DATA\n");
                }
                if (mask & FILE_WRITE_ATTRIBUTES)
                {
                    printf("FILE_WRITE_ATTRIBUTES\n");
                }
                if (mask & FILE_WRITE_EA)
                {
                    printf("FILE_WRITE_EA\n");
                }
                if (mask & FILE_EXECUTE)
                {
                    printf("FILE_EXECUTE\n");
                }
            }
        }
    }
    else
    {
        printf("ERROR");
    }
}
VOID printFileIntegrityLevel(const char* path)
{
    DWORD integrityLevel = SECURITY_MANDATORY_UNTRUSTED_RID;
    PSECURITY_DESCRIPTOR pSD = NULL;
    PACL acl = 0;

    if (!GetNamedSecurityInfoA(path, SE_FILE_OBJECT, LABEL_SECURITY_INFORMATION, 0, 0, 0, &acl, &pSD))
    {
        if (0 != acl && 0 < acl->AceCount)
        {
            SYSTEM_MANDATORY_LABEL_ACE* ace = 0;
            if (GetAce(acl, 0, reinterpret_cast<void**>(&ace)))
            {
                SID* sid = reinterpret_cast<SID*>(&ace->SidStart);
                integrityLevel = sid->SubAuthority[0];
            }
        }

        PWSTR stringSD;
        ULONG stringSDLen = 0;

        ConvertSecurityDescriptorToStringSecurityDescriptor(pSD, SDDL_REVISION_1, LABEL_SECURITY_INFORMATION, &stringSD, &stringSDLen);

        if (integrityLevel == 4096)
            printf("Low\n");
        else if (integrityLevel == 8192)
            printf("Medium\n");
        else if (integrityLevel == 12288)
            printf("High\n");
        else
            printf("UNKNOWN\n");

        if (pSD)
        {
            LocalFree(pSD);
        }
    }
    else
    {
        printf("ACCESS DENIED\n");
    }
}
VOID setFileOwner(const char* path, const char* user)
{
    char cmd[256] = { 0 };
    strcat(cmd, "takeown /F \"");
    strcat(cmd, path);
    strcat(cmd, "\"");


    if (!strcmp(user, "owner")) {
        strcat(cmd, " /A");
        system(cmd);
    }
    else if (!strcmp(user, "cur")) 
    {
        system(cmd);
    }
    else
    {
        printf("WRONG ARGS\n");
        return;
    }
}
VOID setFileAcl(const char* path, const char* spermissions, ACCESS_MODE mode)
{
    PSID pEveryoneSID = NULL;
    PACL pACL = NULL;
    EXPLICIT_ACCESS ea[1];
    SID_IDENTIFIER_AUTHORITY SIDAuthWorld = SECURITY_WORLD_SID_AUTHORITY;
    DWORD permissions;

    for (int i = 0; i < strlen(spermissions); i++)
    {
        if (spermissions[i] == 'r')
        {
            permissions |= FILE_READ_DATA | FILE_READ_ATTRIBUTES;
        }
        else if (spermissions[i] == 'w')
        {
            permissions |= FILE_WRITE_DATA | FILE_APPEND_DATA | FILE_WRITE_ATTRIBUTES | FILE_WRITE_EA;
        }
        else if (spermissions[i] == 'e')
        {
            permissions |= FILE_READ_DATA | FILE_EXECUTE;
        }
        else
        {
            printf("WRONG ARGS\n");
            return;
        }
    }

    // Create a well-known SID for the Everyone group.
    AllocateAndInitializeSid(&SIDAuthWorld, 1,
        SECURITY_WORLD_RID,
        0, 0, 0, 0, 0, 0, 0,
        &pEveryoneSID);

    // Initialize an EXPLICIT_ACCESS structure for an ACE.
    ZeroMemory(&ea, 1 * sizeof(EXPLICIT_ACCESS));
    ea[0].grfAccessPermissions = permissions;
    ea[0].grfAccessMode = mode;
    ea[0].grfInheritance = NO_INHERITANCE;
    ea[0].Trustee.TrusteeForm = TRUSTEE_IS_SID;
    ea[0].Trustee.TrusteeType = TRUSTEE_IS_WELL_KNOWN_GROUP;
    ea[0].Trustee.ptstrName = (LPTSTR)pEveryoneSID;

    // Create a new ACL that contains the new ACEs.
    SetEntriesInAcl(1, ea, NULL, &pACL);

    // Initialize a security descriptor.  
    PSECURITY_DESCRIPTOR pSD = (PSECURITY_DESCRIPTOR)LocalAlloc(LPTR,
        SECURITY_DESCRIPTOR_MIN_LENGTH);

    InitializeSecurityDescriptor(pSD, SECURITY_DESCRIPTOR_REVISION);

    // Add the ACL to the security descriptor. 
    SetSecurityDescriptorDacl(pSD,
        TRUE,     // bDaclPresent flag   
        pACL,
        FALSE);   // not a default DACL 


    //Change the security attributes
    SetFileSecurityA(path, DACL_SECURITY_INFORMATION, pSD);
    printf("SUCCEESS\n");

    if (pEveryoneSID)
        FreeSid(pEveryoneSID);
    if (pACL)
        LocalFree(pACL);
    if (pSD)
        LocalFree(pSD);
}
VOID setFileIntegrityLevel(const char* path, DWORD CONST level)
{
    #define LOW_INTEGRITY_SDDL_SACL_W L"S:AI(ML;;NW;;;LW)"
    #define MEDIUM_INTEGRITY_SDDL_SACL_W L"S:AI(ML;;NW;;;ME)"
    #define HIGH_INTEGRITY_SDDL_SACL_W L"S:AI(ML;;NW;;;HI)"
    DWORD dwErr = ERROR_SUCCESS;
    PSECURITY_DESCRIPTOR pSD = NULL;

    PACL pSacl = NULL; // not allocated
    BOOL fSaclPresent = FALSE;
    BOOL fSaclDefaulted = FALSE;
    LPCWSTR sddl;

    if (level == 1)
        sddl = LOW_INTEGRITY_SDDL_SACL_W;
    else if (level == 2)
        sddl = MEDIUM_INTEGRITY_SDDL_SACL_W;
    else if (level == 3)
        sddl = HIGH_INTEGRITY_SDDL_SACL_W;
    else
    {
        printf("WRONG ARGS\n");
        return;
    }

    if (ConvertStringSecurityDescriptorToSecurityDescriptorW(
        sddl, SDDL_REVISION_1, &pSD, NULL))
    {
        if (GetSecurityDescriptorSacl(pSD, &fSaclPresent, &pSacl,
            &fSaclDefaulted))
        {
            // Note that psidOwner, psidGroup, and pDacl are 
            // all NULL and set the new LABEL_SECURITY_INFORMATION
            if (!SetNamedSecurityInfoA((char*)path,
                SE_FILE_OBJECT, LABEL_SECURITY_INFORMATION,
                NULL, NULL, NULL, pSacl))
            {
                printf("SUCCESS\n");
            }
            else
            {
                printf("ACCESS DENIED\n");
            }
        }
        LocalFree(pSD);
    }

}

int main(int argc, char* argv[])
{
    setlocale(LC_ALL, "Russian");

    //printPriveleges(GetCurrentProcessId());
    //setFileIntegrityLevel("C:\\check.txt", 1);
    //printFileIntegrityLevel("C:\\check.txt");
    //setFileAcl("C:\\check.txt", "rwe", DENY_ACCESS);
    //printFileAcl("C:\\check.txt");
    //setFileIntegrityLevel("C:\\check.txt");
    //printProcessPrivileges(GetCurrentProcessId());
    //setProcessPrivilege(GetCurrentProcessId(), "SeDebugPrivilege", FALSE);
    //printProcessPrivileges(GetCurrentProcessId());
    //setFileOwner("C:\\check.txt", "owner");
    //printFileOwner("C:\\check.txt");
    //return 0;

    const char* lpszPrivilege = "SeTcbPrivilege";
    HANDLE hToken;
    BOOL bRetVal;

    //setProcessPrivilege(GetCurrentProcessId(), lpszPrivilege, TRUE);
    
    if (argc == 2)
    {
        /*
        -pl
        */
        if (!strcmp(argv[1], "-pl"))
        {
            printProcessList();
        }
        else
        {
            printf("WRONG ARGS\n");
        }
    }
    //Get process and file info
    else if (argc == 3)
    {
        //Get process integrity level
        /*
        -pip [processID]
        */
        if (!strcmp(argv[1], "-pip"))
        {
            printProcessIntegrityLevel(atoi(argv[2]));
        }
        //Get process's priveleges level
        /*
        -ppp [processID]
        */
        else if (!strcmp(argv[1], "-ppp"))
        {
            printProcessPrivileges(atoi(argv[2]));
        }
        //Get process's module list
        /*
        -ml [processID]
        */
        else if (!strcmp(argv[1], "-ml"))
        {
            printModuleList(atoi(argv[2]));
        }
        //Get file ACL
        /*
        -fap [file path]
        */
        else if (!strcmp(argv[1], "-fap"))
        {
            printFileAcl(argv[2]);
        }
        //Get file owner
        /*
        -fop [file path]
        */
        else if (!strcmp(argv[1], "-fop"))
        {
            printFileOwner(argv[2]);
        }
        //Get file priveleges
        /*
        -fip [file path]
        */
        else if (!strcmp(argv[1], "-fip"))
        {
            printFileIntegrityLevel(argv[2]);
        }
        //Get file integrity
        /*
        -fip [file path]
        */
        else if (!strcmp(argv[1], "-fip"))
        {
            printFileIntegrityLevel(argv[2]);
        }
        else
        {
            printf("WRONG ARGS\n");
        }
    }
    //Set file and process info
    else if (argc == 4)
    {
        //Set file owner
        /*
        -fos [file path] [owner/cur]
        */
        if (!strcmp(argv[1], "-fos"))
        {
            setFileOwner(argv[2], argv[3]);
        }
        //Set process integrity level
        /*
        -pis [processID] [level] //Not implemented
        */
        else if (!strcmp(argv[1], "-pis"))
        {
            setProcessIntegrityLevel(GetCurrentProcessId(), argv[3]);
        }
        //Set file integrity
        /*
        -fis [file path] [low/middle/high]
        */
        else if (!strcmp(argv[1], "-fis"))
        {
            DWORD level = 0;

            if (!strcmp(argv[3], "low"))
                level = 1;
            else if (!strcmp(argv[3], "medium"))
                level = 2;
            else if (!strcmp(argv[3], "high"))
                level = 3;

            
            setFileIntegrityLevel(argv[2], level);
            printFileIntegrityLevel(argv[2]);
        }
        else
        {
            printf("WRONG ARGS\n");
        }
    }
    else if (argc == 5)
    {
        //Set file Acl
        /*
        -fas [file path] [access level: combination of r/w/e letters as a string] [access mode: deny/set]
        */
        if (!strcmp(argv[1], "-fas"))
        {
            if (!strcmp(argv[4], "deny"))
                setFileAcl(argv[2], argv[3], DENY_ACCESS);
            else if (!strcmp(argv[4], "set"))
                setFileAcl(argv[2], argv[3], SET_ACCESS);
        }

        else if (!strcmp(argv[1], "-pps"))
        {
            BOOL enablePrivilege;

            if (!strcmp(argv[4], "true"))
                enablePrivilege = TRUE;
            else
                enablePrivilege = FALSE;
            //atoi(argv[2])
            setProcessPrivilege(atoi(argv[2]), argv[3], enablePrivilege);
        }
        else
        {
            printf("WRONG ARGS\n");
        }
    }

    ExitProcess(0);
}
