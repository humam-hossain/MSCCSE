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

    public static void displayArray(int[] arr){
        for(int i = 0; i < arr.length; ++i){
            System.out.print(arr[i] + " ");
        }
        System.out.println();
    }

    // searching algorithms
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

        if(arr[i].equals(item)) return true;
        else return linearSearch(arr, item, i + 1);
    }

    public static void linearSearchPractice(){
        String[] arr = {
            "cxc",
            "dfd",
            "xyz",
            "dfd",
        };

        String item = "efg";
        System.out.println(linearSearch(arr, item, 0));
    }

    public static boolean binarySearch(int[] arr, int item, int l, int r){
        while(l <= r){
            int mid = l + (r - l) / 2;

            if(arr[mid] == item){
                return true;
            }else{
                if(item < arr[mid]){
                    r = mid - 1;
                } else{
                    l = mid + 1;
                }
            }
        }

        return false;
    }

    public static boolean recurrBinarySearch(int[] arr, int item, int l, int r){
        if(l <= r){
            int mid = l + (r - l) / 2;

            if(arr[mid] == item) {
                return true;
            } else{
                if(item < arr[mid]){
                    return recurrBinarySearch(arr, item, l, mid - 1);
                }else{
                    return recurrBinarySearch(arr, item, mid + 1, r);
                }
            }
        }

        return false;
    }

    public static boolean ternarySearch(int[] arr, int item, int l, int r){
        while(l <= r){
            int mid1 = l + (r - l) / 3;
            int mid2 = r - (r - l) / 3;

            if(item == arr[mid1] || item == arr[mid2]){
                return true;
            }else if(item < arr[mid1]){
                r = mid1 - 1;
            } else if(item > arr[mid2]){
                l = mid2 + 1;
            } else{
                l = mid1 + 1;
                r = mid2 - 1;
            }
        }

        return false;
    }

    // sorting algorithms
    public static void bubbleSort(int[] arr){
        int swapCount = 0;
        for(int i=0; i < arr.length; ++i){
            boolean swapped = false;
            for(int j=0; j < arr.length - i - 1; ++j){
                if(arr[j] > arr[j+1]){
                    int temp = arr[j + 1];
                    arr[j + 1] = arr[j];
                    arr[j] = temp;

                    swapped = true;
                    swapCount++;
                }
            }

            if(!swapped) break;
        }

        System.out.println("Bubble Sort swapcount: " + swapCount);
        displayArray(arr);
    }

    public static void selectionSort(int[] arr){
        int swapCount = 0;
        for(int i=0; i < arr.length; ++i){
            int min_idx = i;

            for(int j=i; j < arr.length; ++j){
                if(arr[j] < arr[min_idx]){
                    min_idx = j;
                }
            }

            //swap
            if(min_idx != i){
                int temp = arr[min_idx];
                arr[min_idx] = arr[i];
                arr[i] = temp;

                swapCount++;
            }
        }

        System.out.println("SelectionSort swapCount = " + swapCount);
        displayArray(arr);
    }

    public static void insertionSort(int[] arr){
        int swapCount = 0;

        for(int i = 1; i < arr.length; ++i){
            int key = arr[i];
            int j = i - 1;

            while(j >= 0 && key < arr[j]){
                arr[j+1] = arr[j];
                j--;

            }
            arr[j+1] = key;
        }

        System.out.println("insertionSort swapCount: " + swapCount);
        displayArray(arr);
    }

    // merge sort
    public static int[] merge(int[] left, int[] right){
        int[] a = new int[left.length + right.length];

        int i = 0;
        int j = 0;
        int k = 0;

        while(i < left.length && j < right.length){
            if(left[i] <= right[j]){
                a[k] = left[i];
                i++;
            }else{
                a[k] = right[j];
                j++;
            }

            k++;
        }

        while(i < left.length){
            a[k] = left[i];
            i++;
            k++;
        }

        while(j < right.length){
            a[k] = right[j];
            j++;
            k++;
        }

        return a;
    }

    public static int[] mergeSort(int[] arr){
        if(arr.length <= 1){
            return arr;
        }

        int mid = arr.length / 2;

        int[] leftArr = new int[mid];
        int[] rightArr = new int[arr.length - mid];

        for(int i=0; i < arr.length; ++i){
            if(i < mid){
                leftArr[i] = arr[i];
            }else{
                rightArr[i - mid] = arr[i];
            }
        }

        int[] left = mergeSort(leftArr);
        int[] right = mergeSort(rightArr);

        return merge(left, right);
    }

    // quick sort
    public static int quickSwaps = 0;
    public static int partition(int[] arr, int start, int end){
        int pivot = arr[end];
        int pIndex = start;

        for(int i = start; i < end; ++i){
            if(arr[i] < pivot){
                if(i != pIndex){
                    // swap
                    int temp = arr[i];
                    arr[i] = arr[pIndex];
                    arr[pIndex] = temp;
                    quickSwaps++;
                }

                pIndex++;
            }
        }

        if(end != pIndex){
            int temp = arr[end];
            arr[end] = arr[pIndex];
            arr[pIndex] = temp;
            quickSwaps++;
        }

        return pIndex;
    }

    public static int[] quickSort(int[] arr, int start, int end){
        if(start >= end){
            return arr;
        }

        int pIndex = partition(arr, start, end);
        quickSort(arr, start, pIndex - 1);
        quickSort(arr, pIndex + 1, end);

        return arr;
    }

    public static void countSort(int[] arr){
        int max = arr[0];
        for(int i=0; i < arr.length; ++i){
            if(arr[i] > max){
                max = arr[i];
            }
        }

        int[] result = new int[arr.length];
        int[] count = new int[max + 1];
        int[] cu = new int[max + 1];

        for(int i=0; i < count.length; ++i){
            count[i] = 0;
            cu[i] = 0;
        }

        for(int i=0; i < arr.length; ++i){
            count[arr[i]]++;
        }

        cu[0] = count[0]; 
        for(int i=1; i < count.length; ++i){
            cu[i] = cu[i-1] + count[i];
        }

        for(int i=arr.length-1; i >= 0; --i){
            result[--cu[arr[i]]] = arr[i];
        }

        displayArray(result);
    }

    public static void main(String[] args) {
        int[] arr; 
        arr = new int[]{45, 11, 0, -2, -9};

        displayArray(arr);

        arr = new int[]{45, 11, 0, -2, -9};
        bubbleSort(arr);

        arr = new int[]{45, 11, 0, -2, -9};
        selectionSort(arr);

        arr = new int[]{45, 11, 0, -2, -9};
        insertionSort(arr);

        System.out.println("Merge Sort");
        arr = new int[]{45, 11, 0, -2, -9};
        arr = mergeSort(arr);
        displayArray(arr);

        System.out.println("Quick Sort");
        arr = new int[]{45, 11, 0, -2, -9};
        quickSort(arr, 0, arr.length - 1);
        System.out.println("QuickSwaps: " + quickSwaps);
        displayArray(arr);

        arr = new int[]{45, 11, 0, 2, 9};
        countSort(arr);
    }
}
