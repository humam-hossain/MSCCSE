package com.study.app;

public class Stack {
    private Node top;

    public boolean isEmpty() {
        return this.top == null;
    }

    public int peek(){
        if(this.isEmpty()) throw new RuntimeException("Stack is empty");
        return this.top.elem;
    }

    public void push(int data){
        Node newNode = new Node(data);

        newNode.next = this.top;
        this.top = newNode;
    }

    public int pop(){
        if(this.isEmpty()) throw new RuntimeException("Stack is empty");

        Node rm = this.top;

        this.top = this.top.next;

        return rm.elem;
    }

    public void display(){
        if(this.isEmpty()) throw new RuntimeException("Stack is empty");
        Node n = this.top;

        while(n != null){
            System.out.println(n.elem);

            n = n.next;
        }
    }
}
