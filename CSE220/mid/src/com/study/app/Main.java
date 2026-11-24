package com.study.app;

public class Main{
    public static Stack conditional_reverse(Stack stack){
        Stack temp = new Stack();

        while(!stack.isEmpty()){
            int value = stack.pop();

            if(temp.isEmpty() || temp.peek() != value){
                temp.push(value);
            }
        }

        return temp;
    }

    public static void practice1() {
        Stack s = new Stack();

        s.push(10);
        s.push(10);
        s.push(20);
        s.push(20);
        s.push(30);
        s.push(10);
        s.push(50);

        System.out.println(s);

        System.out.println("\n\nconditional reverse");
        System.out.println(conditional_reverse(s));
    }

    public static void arrayPractice2(){
        char[][] arr = {
            {'D', 'B', 'G', 'S'},
            {'A', 'G', 'T', 'S'},
            {'W', 'U', 'R', 'N'},
            {'O', 'H', 'R', 'O'}
        };
        int length = arr.length;

        for(int i = 0; i < length; ++i){
            for(int j = i, k = 0; j >= 0 && k < length; --j, ++k){
                System.out.print(arr[j][k] + " ");
            }
        }

        for(int i = 1; i < length; ++i){
            for(int j=length-1, k = i; j >= 0 && k < length; ++k, --j){
                System.out.print(arr[j][k] + " ");
            }
        }

        System.out.println();
    }

    public static void main(String[] args) {
        int[][] grid = {
            {1,  2,  3,  4},
            {5,  6,  7,  8},
            {9,  10, 11, 12},
            {13, 14, 15, 16}
        };

        String[][] directions = {
            {"SE", "SE", "SW", "NE"},
            {"SW", "SW", "NE", "SE"},
            {"NW", "SW", "NE", "NW"},
            {"SE", "NW", "SW", "NE"}
        };

        int[][] energy = new int[grid.length][grid[0].length];

        int length = grid.length;

        for(int i = 0; i < length; ++i){
            for(int j=0; j < length; ++j){
                energy[i][j] = 0;

                String direction = directions[i][j];

                for(int m = i, n =j; m < length && m >= 0 && n < length && n >= 0; m += (direction.charAt(0) == 'S' ? 1 : -1), n += (direction.charAt(1) == 'E' ? 1 : -1)){
                    energy[i][j] += grid[m][n];
                }
            }
        }

        for(int i=0; i< length; ++i){
            for(int j=0; j < length; ++j){
                System.out.print(energy[i][j] + "\t");
            }

            System.out.println();
        }
    }
}
