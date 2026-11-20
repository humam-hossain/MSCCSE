package com.study.app;

import java.util.Scanner;

public class Main{
    public static void main(String[] args) {
        Scanner in = new Scanner(System.in);
        
        int numInputs = in.nextInt();
        int[] inputs = new int[numInputs];

        for(int i = 0; i < numInputs; ++i){
            inputs[i] = in.nextInt();
        }

        in.close();

        HashMap ht = new HashMap();

        int result = -1;
        int minimum = Integer.MAX_VALUE;

        for(int i=0; i < inputs.length; ++i){
            int diff = ht.insert(inputs[i], i);
            if(diff < minimum) {
                result = inputs[i];
                minimum = diff;
            }
        }

        System.out.println(ht);

        System.out.println(result);
    }
}
