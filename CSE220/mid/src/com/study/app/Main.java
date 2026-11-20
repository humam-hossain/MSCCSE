package com.study.app;

public class Main{

    public static int conFun(String name){
        int sum = 0;
        for(char c : name.toCharArray()){
            sum += c - 'A' + 1;
        }

        sum = sum % 10;

        return sum;
    }

    public static void main(String[] args) {
        String[] S = {
            "ABC",
            "EBC",
            "JKL",
            "MLK",
            "BAC"
        };

        int[] EL = new int[S.length];

        for(int i = 0; i < S.length; ++i){
            EL[i] = conFun(S[i]);

            System.out.println(S[i] + ": " + EL[i]);
        }
    }
}
