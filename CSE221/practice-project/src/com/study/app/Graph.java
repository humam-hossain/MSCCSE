package com.study.app;

import java.util.ArrayList;
import java.util.List;

public class Graph {
    private HashMap adj;
    private boolean isDirected;

    public Graph(String[] V, String[][] E){
        this.adj = new HashMap();

        for(String vertex : V){
            adj.put(vertex, new ArrayList<>());
        }

        for(String[] edge : E){
            String u = edge[0];
            String v = edge[1];

            this.adj.get(u).add(v);
            this.adj.get(v).add(u);
        }
    }

    public int getIdx(String v){
        return this.adj.hash(v);
    }

    public List<String> get(String v){
        return this.adj.get(v);
    }

    // getters
    public int getTableMaxSize(){
        return this.adj.getMaxSize();
    }

    public int getTableSize(){
        return this.adj.getSize();
    }

    public String[] getVertices(){
        return this.adj.getKeys();
    }

    @Override 
    public String toString(){
        return "Adjacent List of Graph :\n" + this.adj.toString();
    }
}
