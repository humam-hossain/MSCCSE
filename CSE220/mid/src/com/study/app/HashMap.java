package com.study.app;

public class HashMap{
    private final int MAX_TABLE_SIZE = 5;
    private Entry[] table; 
    private int size;

    public HashMap(){
        this.table = new Entry[this.MAX_TABLE_SIZE];
        this.size = 0;
    }

    // hash function
    public int hash(String name){
        int sum = 0;
        for(char c : name.toCharArray()){
            sum += (int) c;
        }

        if(sum % 2 == 0){
            return (sum / 2) % this.table.length;
        }

        return sum % this.table.length;
    }

    public void insert(String name, int id){
        if(name == null) throw new RuntimeException("name or id or both invalid");

        Entry newEntry = new Entry(name, id);

        int idx = this.hash(name);

        if(this.table[idx] == null){
            this.table[idx] = newEntry;
        }else{
            Entry e = this.table[idx];
            Entry p = this.table[idx];

            while(e != null){
                if(e.key == name){
                    System.out.println("Student already exists");

                    return;
                }

                p = e;
                e = e.next;
            }

            p.next = newEntry;
        }

        this.size++;
    }

    public int search(String name){
        if(name == null) throw new RuntimeException("Invalid name");

        int idx = this.hash(name);

        Entry e = this.table[idx];

        while(e != null){
            if(e.key == name){
                System.out.println("ID of " + name + " is " + e.value);
                return e.value;
            }

            e = e.next;
        }

        System.out.println(name + " Not found");
        return -1;
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
            }else{
                text += i + ": None\n";
            }
        }

        return text;
    }
}
