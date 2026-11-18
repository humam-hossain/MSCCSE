package com.study.app;

class Entry {
    String key;
    String value;
    Entry next;

    Entry(String key, String value){
        this.key = key;
        this.value = value;
        this.next = null;
    }
}

public class HashTable {
    private Entry[] table;  // the bucket array
    private int capacity;   // number of buckets
    private int size;        // number of key-value pairs stored

    // ================================================================
    // TODO 1: Constructor
    // ----------------------------------------------------------------
    // Initialize the hash table with the given capacity.
    // - Set capacity, size = 0
    // - Create the bucket array
    // ================================================================
    public HashTable(int capacity) {
        this.capacity = capacity;
        this.table = new Entry[this.capacity];
        this.size = 0;
    }

    // ================================================================
    // TODO 2: Hash Function
    // ----------------------------------------------------------------
    // Convert a key into a valid bucket index (0 to capacity-1).
    // Use Java's built-in hashCode() and handle negative values.
    //
    // Hint: Math.abs(key.hashCode()) % capacity
    // ================================================================
    private int hash(String key) {
        return key.length() % this.table.length; // replace this
    }

    // ================================================================
    // TODO 3: put(key, value)
    // ----------------------------------------------------------------
    // Insert a key-value pair into the table.
    // - Hash the key to find the bucket index
    // - If the key already exists in that chain, UPDATE its value
    // - Otherwise, insert a new Entry at the HEAD of the chain
    // - Increment size only on new insertions
    // ================================================================
    public void put(String key, String value) {
        int idx = this.hash(key);

        Entry e = this.table[idx];
        while(e != null){
            if(e.key.equals(key)){
                e.value = value;
                return;
            }

            e = e.next;
        }

        Entry newEntry = new Entry(key, value);
        newEntry.next = this.table[idx];
        this.table[idx] = newEntry;

        this.size++;
    }

    // ================================================================
    // TODO 4: get(key)
    // ----------------------------------------------------------------
    // Retrieve the value associated with a key.
    // - Hash the key to find the bucket
    // - Walk the chain looking for a matching key
    // - Return the value if found, null otherwise
    // ================================================================
    public String get(String key) {
        int idx = this.hash(key);

        Entry e = this.table[idx];

        while(e != null){
            if(e.key.equals(key)){
                return e.value;
            }

            e = e.next;
        }

        return null;
    }

    // ================================================================
    // TODO 5: remove(key)
    // ----------------------------------------------------------------
    // Remove a key-value pair from the table.
    // - Hash the key to find the bucket
    // - Walk the chain, keeping track of the previous node
    // - Relink the chain to skip the removed node
    // - Decrement size and return the removed value
    // - Return null if key not found
    // ================================================================
    public String remove(String key) {
        int idx = this.hash(key);
        Entry e = this.table[idx];
        Entry prev = null;

        while(e != null){
            if(e.key.equals(key)){
                if(prev == null){
                    this.table[idx] = e.next;
                }else{
                    prev.next = e.next;
                    e.next = null;
                }

                --this.size;
                return e.value;
            }

            prev = e;
            e = e.next;
        }

        return null;
    }

    // ================================================================
    // TODO 6: containsKey(key)
    // ----------------------------------------------------------------
    // Return true if the key exists in the table, false otherwise.
    // (Hint: you can reuse get())
    // ================================================================
    public boolean containsKey(String key) {
        return this.get(key) != null;
    }

    // ================================================================
    // TODO 7: size()
    // ----------------------------------------------------------------
    // Return the number of key-value pairs currently stored.
    // ================================================================
    public int size() {
        return this.size; // replace this
    }

    // ================================================================
    // TODO 8: isEmpty()
    // ----------------------------------------------------------------
    // Return true if the table has no entries.
    // ================================================================
    public boolean isEmpty() {
        return this.size == 0; // replace this
    }

    // ================================================================
    // TODO 9: display()
    // ----------------------------------------------------------------
    // Print the entire hash table, bucket by bucket.
    // Format per bucket:
    //   Bucket 0: (key1, val1) -> (key2, val2) -> null
    //   Bucket 1: null
    //   ...
    // ================================================================
    public void display() {
        for(int i=0; i<this.table.length; ++i){
            System.out.print("Bucket " + i + ": ");

            Entry e = this.table[i];
            while(e != null){
                System.out.print("(" + e.key + ", " + e.value + ")");

                if(e.next != null){
                    System.out.print(" -> ");
                }

                e = e.next;
            }

            System.out.println();
        }
    }
}
