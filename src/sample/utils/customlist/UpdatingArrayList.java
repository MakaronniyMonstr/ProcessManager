package sample.utils.customlist;

import sample.utils.processloader.ProcessEntry;

import java.util.ArrayList;

public class UpdatingArrayList extends ArrayList<ProcessEntry> {
    public boolean updated(ProcessEntry processEntry) {
        if (!contains(processEntry)) {
            for (int i = 0; i < size(); i++)
                if (processEntry.isUpdated((ProcessEntry) get(i))) {
                    get(i).update(processEntry);

                    return true;
                }
        }
        return false;
    }
}
