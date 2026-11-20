package com.study.app;

public class HashMap{
    private Entry[] table; 
    private int size;

    public HashMap(int size){
        this.table = new Entry[size];
        this.size = 0;
    }

    public HashMap(){
        this(100);
    }

    // hash function
    public int hash(int key){
        return key % this.table.length;
    }

    public int insert(int key, int value){
        Entry newEntry = new Entry(key, value);

        int idx = this.hash(key);
        int result = Integer.MAX_VALUE;

        if(this.table[idx] == null){
            this.table[idx] = newEntry;
        } else if (this.table[idx].next == null){
            Entry e = this.table[idx];
            e.next = newEntry;

            if(e.key == key) result = value - e.value;
        }

        this.size++;

        return result;
    }

    @Override 
    public String toString(){
        if(this.size == 0) return "No entries exists";
        String text = "HashMap size = " + this.size + "\n";

        for(int i=0; i < table.length; ++i){
            if(this.table[i] != null){
                text += i + ": ";

                Entry e = this.table[i];
                while(e != null){
                    text += "(" + e.key + "," + e.value + ") -> ";

                    e = e.next;
                }

                text += "\n";
            }
        }

        return text;
    }
}
