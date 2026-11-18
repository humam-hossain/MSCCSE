package com.study.app;

public class LinkedList {
    public Node head;
    public int size;

    public LinkedList(){
        this.head = null;
        this.size = 0;
    }

    public Node nodeAt(int idx){
        if(idx < 0){
            System.out.println("[ERROR] idx cannot be negative.");
            return null;
        }

        int c = 0;

        Node n = this.head;
        while(n != null){
            if (c == idx){
                return n;
            }
            c += 1;
            n = n.next;
        }

        return null;
    }

    public void addNode(int elem){
        Node newNode = new Node(elem);

        if(this.size == 0){
            this.head = newNode;
        }else{
            Node n = this.head;
            while(n.next != null){
                n = n.next;
            }

            n.next = newNode;
        }

        this.size++;
    }

    public void addArr(int[] arr){
        for(int i=0; i < arr.length; ++i){
            this.addNode(arr[i]);
        }
    }

    public boolean insert(int elem, int idx){
        Node before = nodeAt(idx-1);

        if(before != null){
            Node after = before.next;
            Node newNode = new Node(elem);

            before.next = newNode;
            newNode.next = after;

            this.size++;

            return true;
        }else{
            System.out.println("[ERROR] idx is not valid!");
            return false;
        }
    }

    public Node remove(int idx){
        Node n = nodeAt(idx);

        if(n != null){
            Node before = nodeAt(idx - 1);
            before.next = n.next;

            n.next = null;
            return n;
        }else{
            System.out.println("[ERROR] index out of bound! remove operation cannot be done.");
            return null;
        }
    }

    public void inplaceReverse(){
        Node n = this.head;

        while(n != null){
            Node after = n.next;
            
            if(n == this.head){
                n.next = null;
            }else{
                n.next = head;
                this.head = n;
            }
            n = after;
        }
    }

    @Override 
    public String toString(){
        System.out.println("Elements in the LinkedList:");
        System.out.println("---------------------------");
        if (this.head == null){
            return "No elements added to the list! Please add more elements";
        }
        String print = "size = " + this.size + "\n";

        Node n = this.head;
        while(n != null){
            print += n.elem;

            if(n.next != null){
                print += " -> ";
            }

            n = n.next;
        }

        print += "\n|";
        print += "\nhead";

        return print;
    }
};
