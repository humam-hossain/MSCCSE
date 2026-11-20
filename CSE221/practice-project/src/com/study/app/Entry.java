package com.study.app;

import java.util.List;

public class Entry{
    String key;
    List<String> value;
    Entry next;

    public Entry(String key, List<String> value){
        this.key = key;
        this.value = value;
        this.next = null;
    }
}
