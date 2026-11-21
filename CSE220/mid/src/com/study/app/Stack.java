package com.study.app;

public class Stack {
    private Node top;

    public boolean isEmpty() {
        return this.top == null;
    }

    public Integer peek(){
        if(this.isEmpty()) return null;

        return this.top.elem;
    }

    public void push(int element){
        Node newNode = new Node(element);
        
        newNode.next = this.top;
        this.top = newNode;
    }

    public Integer pop(){
        if(this.isEmpty()) return null;

        Node rm = this.top;
        this.top = this.top.next;

        rm.next = null;

        return rm.elem;
    }

    @Override 
    public String toString(){
        String text = "";

        Node n = this.top;

        while(n  != null){
            text += "|" + n.elem + "|\n";

            n = n.next;
        }

        return text;
    }
}
