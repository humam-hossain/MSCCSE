package com.study.app;

public class Main{
    public static void main(String[] args) {
        HashMap m = new HashMap();

        System.out.println("4G14 = " + m.check("4G14"));

        m.insert("13D", "ZMD");
        m.insert("10A", "ABW");
        m.insert("31B", "NZF");
        m.insert("2A4", "RAK");
        m.insert("C7B", "FAF");
        m.insert("1A2", "MNY");

        System.out.println(m);
        
        m.remove("C7B");

        System.out.println(m);
    }
}
