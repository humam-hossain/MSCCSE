package com.study.app;

public class Node {
    public int elem;
    public Node next;
    public Node prev;

    public Node(int elem) {
        this.elem = elem;
        this.next = null;
        this.prev = null;
    }

    public Node(int elem, Node next) {
        this.elem = elem;
        this.next = next;
        this.prev = null;
    }

    public Node(int elem, Node next, Node prev) {
        this.elem = elem;
        this.next = next;
        this.prev = prev;
    }
}

