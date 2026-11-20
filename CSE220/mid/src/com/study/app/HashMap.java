package com.study.app;

public class HashMap{
    private final int MAX_TABLE_SIZE = 3;
    private Entry[] table; 
    private int size;

    public HashMap(){
        this.table = new Entry[this.MAX_TABLE_SIZE];
        this.size = 0;
    }

    // hash function
    public void insert(String key, String value){

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
