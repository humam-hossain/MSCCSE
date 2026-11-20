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

    public static void main(String[] args) {
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
}
