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
    public int check(String key){
        int v = 0;

        for(char c : key.toCharArray()){
            if(Character.isDigit(c)){
                v += Character.getNumericValue(c);
            } else{
                v += (int) c;
            }
        }

        return v % 3;
    }

    public void insert(String key, String value){
        if(key == null || value == null) throw new RuntimeException("Invalid key or value or both"); 

        Entry newEntry = new Entry(key, value);

        int idx = this.check(key);

        if(this.table[idx] == null){
            this.table[idx] = newEntry;
        }else{
            Entry e = this.table[idx];
            Entry p = this.table[idx];

            // check for matching key
            while(e != null){
                if(e.key.equals(key)){
                    e.value = value;
                    return;
                }

                p = e;
                e = e.next;
            }

            p.next = newEntry;
        }

        this.size++;
    }

    public Entry remove(String key){
        int idx = this.check(key);

        Entry e = this.table[idx];
        Entry p = this.table[idx];

        while(e != null){
            if(e.key.equals(key)){
                if(p == e){
                    this.table[idx] = e.next;
                }else{
                    p.next = e.next;
                }

                e.next = null;
                this.size--;

                return e;
            }

            p = e;
            e = e.next;
        }

        return null;
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
