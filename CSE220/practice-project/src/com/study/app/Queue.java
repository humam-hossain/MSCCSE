package com.study.app;

public class Queue{
    private Node head;

    public boolean isEmpty(){
        return this.head == null; 
    }

    public int peek(){
        if(this.isEmpty()) throw new RuntimeException("Queue is empty!");

        return this.head.elem;
    }

    public void enqueue(int data){
        Node newNode = new Node(data);

        if(this.head == null){
            this.head = newNode;
            this.head.next = this.head;
            this.head.prev = this.head;
            return;
        }

        newNode.next = this.head;
        newNode.prev = this.head.prev;
        this.head.prev.next = newNode;
        this.head.prev = newNode;
    }

    public int dequeue(){
        if(this.isEmpty()) throw new RuntimeException("Queue is empty!");

        Node rm = this.head;

        if(this.head.next == this.head){
            this.head = null;
        }else{
            this.head = this.head.next;
            rm.prev.next = this.head;
            this.head.prev = rm.prev;
        }

        rm.next = null;
        rm.prev = null;

        return rm.elem;
    }

    public void display(){
        if(this.isEmpty()) throw new RuntimeException("Queue is empty");

        Node n = this.head;

        do{
            System.out.println(n.elem);

            n = n.next;
        }while(n != this.head);
    }
}
