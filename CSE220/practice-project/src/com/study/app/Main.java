package com.study.app;

public class Main {
    public static void practiceCDLL(){
        int[] arr = {1, 2, 3, 4, 5};
        CircularDoublyLL cdll = new CircularDoublyLL(arr);
        CircularDoublyLL cdll2 = new CircularDoublyLL(cdll);


        cdll.append(10);
        cdll.append(20);
        cdll.append(30);
        cdll.append(40);
        cdll.insert(70, 5);

        cdll.display();
        cdll2.display();
    }

    public static void practiceHashTable(){
        HashTable ht = new HashTable(5);

        // isEmpty / size on empty table
        System.out.println("=== Empty table ===");
        System.out.println("isEmpty: " + ht.isEmpty());    // true
        System.out.println("size: " + ht.size());           // 0

        // put — insert entries
        System.out.println("\n=== put() ===");
        ht.put("name", "Alice");
        ht.put("age", "25");
        ht.put("city", "Dhaka");
        ht.put("lang", "Java");
        ht.put("uni", "BRACU");
        ht.display();
        System.out.println("size: " + ht.size());           // 5

        // put — update existing key
        System.out.println("\n=== put() update ===");
        ht.put("city", "Chittagong");
        System.out.println("city: " + ht.get("city"));      // Chittagong

        // get — existing and missing keys
        System.out.println("\n=== get() ===");
        System.out.println("name: " + ht.get("name"));      // Alice
        System.out.println("missing: " + ht.get("xyz"));    // null

        // containsKey
        System.out.println("\n=== containsKey() ===");
        System.out.println("has 'lang': " + ht.containsKey("lang"));   // true
        System.out.println("has 'xyz': " + ht.containsKey("xyz"));     // false

        // remove
        System.out.println("\n=== remove() ===");
        System.out.println("remove 'age': " + ht.remove("age"));       // 25
        System.out.println("remove 'xyz': " + ht.remove("xyz"));       // null
        System.out.println("size after remove: " + ht.size());          // 4

        // final state
        System.out.println("\n=== Final table ===");
        ht.display();
        System.out.println("isEmpty: " + ht.isEmpty());    // false
    }

    public static void main(String[] args) {
        practiceHashTable();

    }
}
