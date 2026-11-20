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
    public int hash(Node head, String st){
        if(head == null || st == null) throw new RuntimeException("Invalid SLL or string given");
        Node n = head;
        
        int sum = 0;
        while(n != null){
            int idx = -1;
            for(int i=st.length() - 1; i >= 0; --i){
                if(n.elem == st.charAt(i)){
                    idx = i;
                    break;
                }
            }

            sum += idx;

            n = n.next;
        }
        
        int result = sum % 10;
        if(result < 0) result += 10;

        return result;
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
