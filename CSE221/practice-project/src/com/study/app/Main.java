package com.study.app;

public class Main {
    public static void bfs(Graph g, String s){
        if(g == null) throw new RuntimeException("Graph cannot be null");
        if(s == null) throw new RuntimeException("Source vertex cannot be null");

        Queue q = new Queue();
        boolean[] visited = new boolean[g.getTableMaxSize()];
        int[] distance = new int[g.getTableMaxSize()];
        for(int i=0; i<distance.length; ++i){
            distance[i] = -1;
        }
        String[] parent = new String[g.getTableMaxSize()]; 

        q.enqueue(s);
        visited[g.getIdx(s)] = true;
        parent[g.getIdx(s)] = null;
        distance[g.getIdx(s)] = 0;

        while(!q.isEmpty()){
            String u = q.dequeue();

            for(String vertex : g.get(u)){
                int idx = g.getIdx(vertex);
                if(!visited[idx]){
                    visited[idx] = true;
                    distance[idx] = distance[g.getIdx(u)] + 1;
                    parent[idx] = u;

                    q.enqueue(vertex);
                }
            }
        }
        
        // show result
        System.out.println("V: visited\tdistance\tparent");
        for(String vertex : g.getVertices()){
            int i = g.getIdx(vertex);

            System.out.println(vertex + ": " + visited[i] + "\t\t" + distance[i] + "\t\t" + parent[i]);
        }

        System.out.println(s + " to t: " + distance[g.getIdx("t")]);

        String vertex = "t";
        if(!visited[g.getIdx(vertex)]){
            System.out.println("No path exists to " + vertex);
        }else{
            String path = vertex;
            while(!(parent[g.getIdx(vertex)] == null & visited[g.getIdx(vertex)])){
                vertex = parent[g.getIdx(vertex)];

                path += " -> " + vertex;
            }

            System.out.println(path);
        }
    }

    public static boolean bipartite(Graph g, String s){
        Queue q = new Queue();
        boolean[] visited = new boolean[g.getTableMaxSize()];
        String[] color = new String[g.getTableMaxSize()];

        q.enqueue(s);
        visited[g.getIdx(s)] = true;
        color[g.getIdx(s)] = "B";

        while(!q.isEmpty()){
            String u = q.dequeue();
            for(String vertex : g.get(u)){
                int i = g.getIdx(vertex);

                if(!visited[i]){
                    visited[i] = true;
                    if(color[g.getIdx(u)] == "B") color[i] = "G";
                    else color[i] = "B";

                    q.enqueue(vertex);
                }else{
                    if(color[g.getIdx(u)] == color[i]) return false;
                }
            }
        }

        return true;
    }

    public static void bfsPractive(){
        String[] V = {"r", "s", "t", "u", "v", "w", "x", "y"};

        String[][] E = {
            {"r", "s"},
            {"r", "v"},
            {"s", "w"},
            {"t", "u"},
            {"t", "w"},
            {"t", "x"},
            {"u", "y"},
            {"w", "x"},
            {"x", "y"}
        }; 

        Graph g = new Graph(V, E);

        bfs(g, V[0]);
        System.out.println(bipartite(g, V[0]));
    }

    public static boolean linearSearch(String[] arr, String item){
        for(int i=0; i<arr.length; ++i){
            if(arr[i].equals(item)){
                return true;
            }
        }

        return false;
    }

    public static boolean linearSearch(String[] arr, String item, int i){
        if(i >= arr.length){
            return false;
        }

        if(arr[i] == item) return true;
        else return linearSearch(arr, item, ++i);
    }

    public static void main(String[] args) {
        String[] arr = {
            "cxc",
            "dfd",
            "xyz",
            "dfd",
        };

        String item = "efg";
        System.out.println(linearSearch(arr, item, 0));
    }
}
