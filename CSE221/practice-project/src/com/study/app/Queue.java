package com.study.app;

public class Queue{
    Node head;
    Node tail;

    public Queue(){
        this.head = null;
        this.tail = null;
    }

    public boolean isEmpty(){
        return this.head == null; 
    }

    public String peek(){
        if(this.isEmpty()) throw new RuntimeException("Queue is empty!");
        return this.head.elem;
    }

    public void enqueue(String data){
        Node newNode = new Node(data);

        if(this.head == null){
            this.head = newNode;
            this.tail = newNode;
        }else{
            this.tail.next = newNode;
            this.tail = this.tail.next;
        }
    }

    public String dequeue(){
        if(this.isEmpty()) throw new RuntimeException("Queue is empty!");
        Node rm = this.head;

        if(this.head.next == null){
            this.tail = null;
        }

        this.head = this.head.next;

        // cleanup
        rm.next = null;

        return rm.elem;
    }

    @Override 
    public String toString(){
        if(this.isEmpty()) return "Queue is empty!";

        String text = "";

        Node n = this.head;

        while(n != null){
            text += n.elem;

            if(n != this.tail){
                text += " -> ";
            }

            n = n.next;
        }

        return text;
    }
}
