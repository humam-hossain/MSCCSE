package com.study.app;

import java.util.List;

public class HashMap {
    private Entry[] table;
    private final int MAX_SIZE = 100; 
    private int size;

    public HashMap(){
        this.table = new Entry[this.MAX_SIZE];
        this.size = 0;
    }

    public int hash(String k){
        return Character.toLowerCase(k.charAt(0)) - 'a';
    }

    public void put(String k, List<String> v){
        int idx = this.hash(k);

        this.table[idx] = new Entry(k, v);
        this.size++;
    }

    public List<String> get(String k){
        return this.table[this.hash(k)].value;
    }

    // gettters
    public int getMaxSize(){
        return this.MAX_SIZE;
    }

    public int getSize(){
        return this.size;
    }
    
    public String[] getKeys(){
        String[] keys = new String[this.size];
        int count = 0;

        for(Entry e : this.table){
            if(e != null){
                keys[count++] = e.key;
            }
        }

        return keys;
    }

    @Override 
    public String toString(){
        String text = "";
        
        for(int i = 0; i < this.table.length; ++i){
            if(this.table[i] != null){
                text += this.table[i].key + ": ";

                for(String elem : this.table[i].value){
                    text += elem + ", ";
                }

                text += "\n";
            }
        }

        return text;
    }
}
