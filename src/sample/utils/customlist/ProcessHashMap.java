package sample.utils.customlist;

import sample.utils.processloader.ProcessEntry;

import java.util.HashMap;
import java.util.LinkedList;

public class ProcessHashMap {
    HashMap<String, LinkedList<ProcessEntry>> map = new HashMap<>();

    public void put(String key, ProcessEntry value) {
        if (map.containsKey(key)) {
            map.get(key).add(value);
        }
        else {
            map.put(key, new LinkedList<>());
        }
    }
}
