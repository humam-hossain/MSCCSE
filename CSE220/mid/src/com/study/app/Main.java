package com.study.app;

public class Main{
    public static void main(String[] args) {
        HashMap ht = new HashMap();
        ht.insert("Alice", 123456);
        ht.insert("Bob", 135927);
        ht.insert("Charlie", 348247);
        ht.insert("David", 124382);

        System.out.println("\nHash table after insertions and rehashing:");
        System.out.println(ht);

        ht.insert("Alice", 123456);
        ht.search("David");
    }
}
