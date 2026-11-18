package com.study.app;

public class StackArr{
    private int[] arr;
    private int top;
    private int capacity;

    public StackArr(int capacity){
        this.capacity = capacity;
        this.arr = new int[this.capacity];
        this.top = -1;
    }

    public StackArr(){
        this(10);
    }

    public boolean isEmpty(){
        return this.top == -1;
    }

    public boolean isFull(){
        return this.top == this.capacity - 1;
    }

    private void putCapacity(int capacity){
        this.capacity = capacity;

        if(this.top == -1){
            this.arr = new int[this.capacity];
        } else {
            int[] temp = new int[this.capacity];

            for(int i=0; i <= this.top; ++i){
                temp[i] = this.arr[i];
            }

            this.arr = temp;
        }
    }

    public int peek(){
        if(this.isEmpty()) throw new RuntimeException("Stack is empty!");

        return this.arr[top];
    }

    public void push(int data){
        if(this.isFull()){
            this.putCapacity(this.capacity*2);
        }
        this.arr[++top] = data;
    }

    public int pop(){
        if(this.isEmpty()) throw new RuntimeException("Stack is empty");

        return this.arr[top--];
    }

    public void display(){
        for(int i=this.top; i >= 0; --i){
            System.out.println(this.arr[i]);
        }
    }
};
