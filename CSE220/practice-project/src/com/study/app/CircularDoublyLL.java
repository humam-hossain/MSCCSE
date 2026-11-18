package com.study.app;

public class CircularDoublyLL {
    private Node dh;
    private int size;

    // =========================================================================
    // 1. CONSTRUCTORS & INITIALIZATION
    // =========================================================================

    /**
     * Default constructor: initializes a dummy head node whose next and prev
     * pointers loop back to itself.
     */
    public CircularDoublyLL() {
        this.dh = new Node(-1);
        this.dh.next = dh;
        this.dh.prev = dh;
        this.size = 0;
    }

    /**
     * Constructs a circular doubly linked list containing all elements from arr in order.
     *
     * @param arr array of integers to initialize the list with
     *
     * Hints:
     * - First call this() to set up the dummy head.
     * - Loop through arr and append each element.
     * - Handle edge case when arr is null or empty.
     */
    public CircularDoublyLL(int[] arr) {
        this();
        if(arr == null) return;

        Node n = this.dh;

        for(int i = 0; i < arr.length; ++i){
            Node newNode = new Node(arr[i]);

            newNode.prev = n;
            newNode.next = n.next;
            n.next.prev = newNode;
            n.next = newNode;

            n = n.next;
            this.size++;
        }
    }

    /**
     * Creates a new CircularDoublyLL with identical elements from another list.
     *
     * @param other the list to copy from
     *
     * Hints:
     * - Traverse the 'other' list from its first node to the end (until reaching other.dh).
     * - Append each node's element to this list.
     */
    public CircularDoublyLL(CircularDoublyLL other) {
        this();
        if(other == null) return;

        Node n = this.dh;
        Node m = other.dh.next;

        while(m != other.dh){
            Node newNode = new Node(m.elem);

            newNode.prev = n;
            newNode.next = n.next;
            n.next.prev = newNode;
            n.next = newNode;

            n = n.next;
            m = m.next;

            this.size++;
        }
    }


    // =========================================================================
    // 2. BASIC QUERY & ACCESSOR METHODS
    // =========================================================================

    /**
     * Returns the total number of elements stored in the list.
     *
     * @return number of non-dummy elements
     */
    public int size() {
        return this.size;
    }

    /**
     * Checks if the list has zero elements.
     *
     * @return true if list is empty, false otherwise
     *
     * Hints:
     * - Can check if this.size == 0 or if dh.next == dh.
     */
    public boolean isEmpty() {
        return dh.next == dh;
    }

    /**
     * TODO: Implement nodeAt(int idx).
     * Helper method to return the Node object at a specific 0-based index.
     *
     * @param idx index of the node to retrieve (0 <= idx < size)
     * @return Node at index idx, or null / throw exception if invalid
     *
     * Hints & Optimization:
     * - Check bounds: 0 <= idx < this.size.
     * - Optimization: If idx < size / 2, start from dh.next and move forward.
     *   Otherwise, start from dh.prev and move backward.
     */
    public Node nodeAt(int idx) {
        // TODO: Implement nodeAt(int idx)
        throw new UnsupportedOperationException("TODO: Implement nodeAt(int idx)");
    }

    /**
     * TODO: Implement get(int idx).
     * Returns the element at the specified 0-based index.
     *
     * @param idx index of the element to retrieve
     * @return value stored at index idx
     *
     * Hints:
     * - Validate index bounds.
     * - Utilize nodeAt(idx) or traverse.
     */
    public int get(int idx) {
        // TODO: Implement get(int idx)
        throw new UnsupportedOperationException("TODO: Implement get(int idx)");
    }

    /**
     * TODO: Implement getFirst().
     * Returns the first non-dummy element in the list.
     *
     * @return element of the first node
     *
     * Hints:
     * - Check if list is empty.
     * - First node is dh.next.
     */
    public int getFirst() {
        // TODO: Implement getFirst()
        throw new UnsupportedOperationException("TODO: Implement getFirst()");
    }

    /**
     * TODO: Implement getLast().
     * Returns the last non-dummy element in the list.
     *
     * @return element of the last node (tail)
     *
     * Hints:
     * - Check if list is empty.
     * - Last node is dh.prev.
     */
    public int getLast() {
        // TODO: Implement getLast()
        throw new UnsupportedOperationException("TODO: Implement getLast()");
    }

    /**
     * TODO: Implement indexOf(int elem).
     * Returns the index of the first occurrence of elem, or -1 if not found.
     *
     * @param elem value to search for
     * @return 0-based index or -1
     */
    public int indexOf(int elem) {
        // TODO: Implement indexOf(int elem)
        throw new UnsupportedOperationException("TODO: Implement indexOf(int elem)");
    }

    /**
     * TODO: Implement lastIndexOf(int elem).
     * Returns the index of the last occurrence of elem, or -1 if not found.
     *
     * @param elem value to search for
     * @return last 0-based index or -1
     *
     * Hints:
     * - Walk backwards starting from dh.prev towards dh using prev pointers!
     */
    public int lastIndexOf(int elem) {
        // TODO: Implement lastIndexOf(int elem)
        throw new UnsupportedOperationException("TODO: Implement lastIndexOf(int elem)");
    }

    /**
     * TODO: Implement contains(int elem).
     * Returns true if the list contains the given element.
     *
     * @param elem value to check
     * @return true if present, false otherwise
     */
    public boolean contains(int elem) {
        // TODO: Implement contains(int elem)
        throw new UnsupportedOperationException("TODO: Implement contains(int elem)");
    }

    /**
     * TODO: Implement count(int elem).
     * Counts and returns how many times elem appears in the list.
     *
     * @param elem value to count
     * @return frequency of elem
     */
    public int count(int elem) {
        // TODO: Implement count(int elem)
        throw new UnsupportedOperationException("TODO: Implement count(int elem)");
    }


    // =========================================================================
    // 3. INSERTION METHODS
    // =========================================================================

    /**
     * Appends a new node with data to the end of the list (just before dummy head).
     */
    public void append(int data) {
        Node newNode = new Node(data);
        Node tail = this.dh.prev;

        newNode.prev = tail;
        newNode.next = this.dh;
        tail.next = newNode;
        this.dh.prev = newNode;

        this.size++;
    }

    /**
     * Prepends a new node with data to the beginning of the list (just after dummy head).
     */
    public void prepend(int data) {
        Node newNode = new Node(data);

        newNode.next = this.dh.next;
        newNode.prev = this.dh;
        this.dh.next.prev = newNode;
        this.dh.next = newNode;

        this.size++;
    }

    /**
     * Inserts a new node with data at the specified index.
     */
    public void insert(int data, int idx) {
        if (idx < 0 || idx > this.size) {
            System.out.println("[ERROR] index out of bound!");
            return;
        }

        Node newNode = new Node(data);

        Node n;
        if (idx < this.size / 2) {
            n = this.dh;
            for (int i = 0; i < idx; ++i) {
                n = n.next;
            }
        } else {
            n = this.dh.prev;
            for (int i = this.size - 1; i >= idx; --i) {
                n = n.prev;
            }
        }

        newNode.prev = n;
        newNode.next = n.next;
        n.next.prev = newNode;
        n.next = newNode;

        this.size++;
    }

    /**
     * TODO: Implement insertBefore(int target, int elem).
     * Finds the first node with value == target and inserts elem immediately before it.
     *
     * @param target value of the existing node to find
     * @param elem value of the new node to insert
     * @return true if target was found and elem inserted, false if target not found
     */
    public boolean insertBefore(int target, int elem) {
        // TODO: Implement insertBefore(int target, int elem)
        throw new UnsupportedOperationException("TODO: Implement insertBefore(int target, int elem)");
    }

    /**
     * TODO: Implement insertAfter(int target, int elem).
     * Finds the first node with value == target and inserts elem immediately after it.
     *
     * @param target value of the existing node to find
     * @param elem value of the new node to insert
     * @return true if target was found and elem inserted, false if target not found
     */
    public boolean insertAfter(int target, int elem) {
        // TODO: Implement insertAfter(int target, int elem)
        throw new UnsupportedOperationException("TODO: Implement insertAfter(int target, int elem)");
    }

    /**
     * TODO: Implement insertSorted(int elem).
     * Inserts elem into the list assuming the list is already sorted in ascending order,
     * maintaining sorted order.
     *
     * @param elem value to insert
     *
     * Hints:
     * - Traverse the list to find the first node whose value is >= elem.
     * - Insert the new node right before that node (or at the end if none found).
     * - Don't forget this.size++.
     */
    public void insertSorted(int elem) {
        // TODO: Implement insertSorted(int elem)
        throw new UnsupportedOperationException("TODO: Implement insertSorted(int elem)");
    }

    /**
     * TODO: Implement addAll(int[] arr).
     * Appends all elements from an array to the end of this list.
     *
     * @param arr array of elements to append
     */
    public void addAll(int[] arr) {
        // TODO: Implement addAll(int[] arr)
        throw new UnsupportedOperationException("TODO: Implement addAll(int[] arr)");
    }

    /**
     * TODO: Implement addAll(CircularDoublyLL other).
     * Appends copies of all elements from another CircularDoublyLL to the end of this list.
     *
     * @param other list whose elements will be appended
     */
    public void addAll(CircularDoublyLL other) {
        // TODO: Implement addAll(CircularDoublyLL other)
        throw new UnsupportedOperationException("TODO: Implement addAll(CircularDoublyLL other)");
    }


    // =========================================================================
    // 4. DELETION & REMOVAL METHODS
    // =========================================================================

    /**
     * TODO: Implement removeFirst().
     * Removes and returns the first element in the list.
     *
     * @return the value of the removed node
     *
     * Hints:
     * - Handle empty list case (throw exception or return sentinel).
     * - Identify node to delete: Node toRemove = dh.next.
     * - Relink dh.next and toRemove.next.prev.
     * - Decrement size.
     */
    public int removeFirst() {
        // TODO: Implement removeFirst()
        throw new UnsupportedOperationException("TODO: Implement removeFirst()");
    }

    /**
     * TODO: Implement removeLast().
     * Removes and returns the last element in the list.
     *
     * @return the value of the removed node
     *
     * Hints:
     * - Handle empty list case.
     * - Identify node to delete: Node toRemove = dh.prev.
     * - Relink toRemove.prev and dh.
     * - Decrement size.
     */
    public int removeLast() {
        // TODO: Implement removeLast()
        throw new UnsupportedOperationException("TODO: Implement removeLast()");
    }

    /**
     * TODO: Implement removeAt(int idx).
     * Removes and returns the element at the specified 0-based index.
     *
     * @param idx index of the node to remove (0 <= idx < size)
     * @return the value of the removed node
     *
     * Hints:
     * - Check bounds: idx < 0 || idx >= this.size.
     * - Find the target node using nodeAt(idx).
     * - Update target.prev.next and target.next.prev.
     * - Clean up target references (optional but good practice).
     * - Decrement size.
     */
    public int removeAt(int idx) {
        // TODO: Implement removeAt(int idx)
        throw new UnsupportedOperationException("TODO: Implement removeAt(int idx)");
    }

    /**
     * TODO: Implement removeByValue(int elem).
     * Removes the FIRST occurrence of elem from the list.
     *
     * @param elem value to remove
     * @return true if found and removed, false otherwise
     */
    public boolean removeByValue(int elem) {
        // TODO: Implement removeByValue(int elem)
        throw new UnsupportedOperationException("TODO: Implement removeByValue(int elem)");
    }

    /**
     * TODO: Implement removeAll(int elem).
     * Removes ALL occurrences of elem from the list.
     *
     * @param elem value to remove
     * @return total count of nodes removed
     *
     * Hints:
     * - Traverse the list with a pointer. Be careful when updating pointers:
     *   advance to the next node before or after deletion properly.
     */
    public int removeAll(int elem) {
        // TODO: Implement removeAll(int elem)
        throw new UnsupportedOperationException("TODO: Implement removeAll(int elem)");
    }

    /**
     * TODO: Implement clear().
     * Removes all elements from the list, resetting it to empty state.
     *
     * Hints:
     * - Reset dh.next = dh, dh.prev = dh.
     * - Reset size = 0.
     */
    public void clear() {
        // TODO: Implement clear()
        throw new UnsupportedOperationException("TODO: Implement clear()");
    }


    // =========================================================================
    // 5. UPDATE & SWAPPING METHODS
    // =========================================================================

    /**
     * TODO: Implement set(int idx, int elem).
     * Replaces the value at index idx with elem, returning the old value.
     *
     * @param idx index of the element to replace
     * @param elem new value
     * @return old value previously at idx
     */
    public int set(int idx, int elem) {
        // TODO: Implement set(int idx, int elem)
        throw new UnsupportedOperationException("TODO: Implement set(int idx, int elem)");
    }

    /**
     * TODO: Implement swapNodes(int idx1, int idx2).
     * Swaps the nodes at idx1 and idx2 by ADJUSTING POINTERS (not merely swapping .elem values).
     *
     * @param idx1 index of first node
     * @param idx2 index of second node
     *
     * Critical CSE220 concepts:
     * - Handle when idx1 == idx2.
     * - Handle when nodes are adjacent (node1.next == node2 or node2.next == node1).
     * - Handle when nodes are non-adjacent.
     * - Ensure 4 next and 4 prev pointer links are updated properly.
     */
    public void swapNodes(int idx1, int idx2) {
        // TODO: Implement swapNodes(int idx1, int idx2)
        throw new UnsupportedOperationException("TODO: Implement swapNodes(int idx1, int idx2)");
    }

    /**
     * TODO: Implement swapByValue(int val1, int val2).
     * Finds the first occurrence of val1 and val2 and swaps their node positions by adjusting pointers.
     *
     * @param val1 first value
     * @param val2 second value
     * @return true if both values were found and swapped, false otherwise
     */
    public boolean swapByValue(int val1, int val2) {
        // TODO: Implement swapByValue(int val1, int val2)
        throw new UnsupportedOperationException("TODO: Implement swapByValue(int val1, int val2)");
    }


    // =========================================================================
    // 6. TRAVERSAL & CONVERSION METHODS
    // =========================================================================

    /**
     * Displays the circular doubly linked list visually (with forward and backward loops).
     */
    public void display() {
        StringBuilder line = new StringBuilder("[dh]");
        Node n = this.dh.next;

        while (n != this.dh) {
            line.append(" ⇄ [").append(n.elem).append("]");
            n = n.next;
        }

        int len = line.length();
        int start = 1; // Points to the middle of [dh]
        int end = len - 2; // Points to the middle of the last node
        int span = end - start;

        if (span <= 0) {
            System.out.println(" " + "─".repeat(3));
            System.out.println(" ↓ ↑");
            System.out.println(line);
            return;
        }

        // Top loop: Tail -> DummyHead (next pointer)
        System.out.println(" ".repeat(start) + "┌" + "─".repeat(span - 1) + "┐");
        System.out.println(" ".repeat(start) + "↓" + " ".repeat(span - 1) + "│");

        // Main chain
        System.out.println(line);

        // Bottom loop: DummyHead -> Tail (prev pointer)
        System.out.println(" ".repeat(start) + "│" + " ".repeat(span - 1) + "↑");
        System.out.println(" ".repeat(start) + "└" + "─".repeat(span - 1) + "┘");
    }

    /**
     * TODO: Implement displayBackward().
     * Traverses and prints all elements starting from the last node (dh.prev)
     * backwards to the first node (dh.next) using .prev pointers.
     *
     * Example output:
     * Tail -> [40] ⇄ [30] ⇄ [20] ⇄ [10] -> Head
     */
    public void displayBackward() {
        // TODO: Implement displayBackward()
        throw new UnsupportedOperationException("TODO: Implement displayBackward()");
    }

    /**
     * TODO: Implement toArray().
     * Converts the list into a standard int[] array containing the elements in forward order.
     *
     * @return array of list elements
     */
    public int[] toArray() {
        // TODO: Implement toArray()
        throw new UnsupportedOperationException("TODO: Implement toArray()");
    }

    /**
     * TODO: Implement toString().
     * Returns a string representation of the list in the format:
     * "[dh] <-> 10 <-> 20 <-> 30 <-> [dh]"
     *
     * @return formatted string
     */
    @Override
    public String toString() {
        // TODO: Implement toString()
        throw new UnsupportedOperationException("TODO: Implement toString()");
    }


    // =========================================================================
    // 7. REVERSAL & ROTATION METHODS
    // =========================================================================

    /**
     * TODO: Implement reverse().
     * Reverses the circular doubly linked list in-place by swapping the next and prev
     * pointers of every node (including the dummy head).
     *
     * Time Complexity: O(n)
     * Space Complexity: O(1)
     *
     * Hints:
     * - Node curr = dh;
     * - Loop through all nodes (do-while or count up to size + 1 nodes):
     *     Node temp = curr.next;
     *     curr.next = curr.prev;
     *     curr.prev = temp;
     *     curr = curr.prev; // (since curr.prev is the old curr.next!)
     * - Ensure dummy head's pointers are also swapped.
     */
    public void reverse() {
        // TODO: Implement reverse()
        throw new UnsupportedOperationException("TODO: Implement reverse()");
    }

    /**
     * TODO: Implement rotateForward(int k).
     * Rotates the list forward (clockwise / right) by k positions.
     *
     * Example:
     * [10, 20, 30, 40, 50] rotated forward by 2 becomes:
     * [40, 50, 10, 20, 30]
     *
     * Hints:
     * - k = k % size (handle k < 0 or k > size).
     * - If size <= 1 or k == 0, nothing changes.
     * - In a circular list, rotation only requires moving the dummy head dh
     *   to a new position in the ring!
     */
    public void rotateForward(int k) {
        // TODO: Implement rotateForward(int k)
        throw new UnsupportedOperationException("TODO: Implement rotateForward(int k)");
    }

    /**
     * TODO: Implement rotateBackward(int k).
     * Rotates the list backward (counter-clockwise / left) by k positions.
     *
     * Example:
     * [10, 20, 30, 40, 50] rotated backward by 2 becomes:
     * [30, 40, 50, 10, 20]
     *
     * Hints:
     * - Rotating backward by k is equivalent to rotating forward by (size - (k % size)).
     */
    public void rotateBackward(int k) {
        // TODO: Implement rotateBackward(int k)
        throw new UnsupportedOperationException("TODO: Implement rotateBackward(int k)");
    }


    // =========================================================================
    // 8. PARTITIONING, SLICING & CONCATENATION
    // =========================================================================

    /**
     * TODO: Implement splitHalves().
     * Splits this circular doubly linked list into two separate CircularDoublyLL objects:
     * - First half: elements from 0 to (size + 1)/2 - 1
     * - Second half: remaining elements
     *
     * @return an array of two CircularDoublyLL [firstHalf, secondHalf]
     *
     * Hints:
     * - Can be implemented either by copying into two new lists, or by splitting the
     *   original ring into two rings with their own dummy heads (CSE220 exam favorite).
     */
    public CircularDoublyLL[] splitHalves() {
        // TODO: Implement splitHalves()
        throw new UnsupportedOperationException("TODO: Implement splitHalves()");
    }

    /**
     * TODO: Implement splitOddEven().
     * Splits the list into two separate lists based on 0-based node index:
     * - list[0]: nodes at even indices (0, 2, 4, ...)
     * - list[1]: nodes at odd indices (1, 3, 5, ...)
     *
     * @return array of two lists [evenIndexedList, oddIndexedList]
     */
    public CircularDoublyLL[] splitOddEven() {
        // TODO: Implement splitOddEven()
        throw new UnsupportedOperationException("TODO: Implement splitOddEven()");
    }

    /**
     * TODO: Implement subList(int fromIdx, int toIdx).
     * Returns a new CircularDoublyLL containing elements between fromIdx (inclusive)
     * and toIdx (exclusive).
     *
     * @param fromIdx starting index (inclusive)
     * @param toIdx ending index (exclusive)
     * @return a new CircularDoublyLL with the sublist
     */
    public CircularDoublyLL subList(int fromIdx, int toIdx) {
        // TODO: Implement subList(int fromIdx, int toIdx)
        throw new UnsupportedOperationException("TODO: Implement subList(int fromIdx, int toIdx)");
    }

    /**
     * TODO: Implement concatenate(CircularDoublyLL other).
     * Merges another CircularDoublyLL to the end of this list.
     * After concatenation, 'other' should either be emptied or unaffected (depending on design).
     *
     * Hints for O(1) in-place concatenation:
     * - If other is empty, do nothing.
     * - If this list is empty, adopt other's chain.
     * - Connect this.tail with other.head, and other.tail with this.dh!
     * - Update this.size += other.size.
     */
    public void concatenate(CircularDoublyLL other) {
        // TODO: Implement concatenate(CircularDoublyLL other)
        throw new UnsupportedOperationException("TODO: Implement concatenate(CircularDoublyLL other)");
    }

    /**
     * TODO: Implement alternateMerge(CircularDoublyLL other).
     * Interleaves nodes of this list and another list alternatively:
     * e.g., this: [1, 3, 5], other: [2, 4, 6] -> result: [1, 2, 3, 4, 5, 6]
     *
     * @param other the second list
     * @return a new CircularDoublyLL with interleaved elements
     */
    public CircularDoublyLL alternateMerge(CircularDoublyLL other) {
        // TODO: Implement alternateMerge(CircularDoublyLL other)
        throw new UnsupportedOperationException("TODO: Implement alternateMerge(CircularDoublyLL other)");
    }


    // =========================================================================
    // 9. SORTING & ORDER VERIFICATION
    // =========================================================================

    /**
     * TODO: Implement isSorted().
     * Checks whether the elements in the list are sorted in non-decreasing (ascending) order.
     *
     * @return true if sorted, false otherwise
     */
    public boolean isSorted() {
        // TODO: Implement isSorted()
        throw new UnsupportedOperationException("TODO: Implement isSorted()");
    }

    /**
     * TODO: Implement sort().
     * Sorts the circular doubly linked list in ascending order.
     *
     * Suggested implementations:
     * - Bubble sort on nodes/values
     * - Insertion sort (unlinking each node and calling insertSorted or relinking)
     * - Merge sort for doubly linked lists (O(n log n))
     */
    public void sort() {
        // TODO: Implement sort()
        throw new UnsupportedOperationException("TODO: Implement sort()");
    }

    /**
     * TODO: Implement mergeSorted(CircularDoublyLL list1, CircularDoublyLL list2).
     * Takes two already-sorted circular doubly linked lists and merges them into a
     * single new sorted CircularDoublyLL.
     *
     * @param list1 first sorted list
     * @param list2 second sorted list
     * @return new merged and sorted CircularDoublyLL
     */
    public static CircularDoublyLL mergeSorted(CircularDoublyLL list1, CircularDoublyLL list2) {
        // TODO: Implement mergeSorted(CircularDoublyLL list1, CircularDoublyLL list2)
        throw new UnsupportedOperationException("TODO: Implement mergeSorted(CircularDoublyLL list1, CircularDoublyLL list2)");
    }


    // =========================================================================
    // 10. CLASSIC ALGORITHMIC & INTERVIEW METHODS
    // =========================================================================

    /**
     * TODO: Implement isPalindrome().
     * Checks if the circular doubly linked list reads the same forward and backward.
     *
     * @return true if palindrome, false otherwise
     *
     * Hints & Beauty of CDLL:
     * - Start two pointers: 'left' at dh.next (head) and 'right' at dh.prev (tail).
     * - Compare left.elem and right.elem.
     * - Move left = left.next, right = right.prev until they meet in the middle!
     * - Runs in O(n/2) time!
     */
    public boolean isPalindrome() {
        // TODO: Implement isPalindrome()
        throw new UnsupportedOperationException("TODO: Implement isPalindrome()");
    }

    /**
     * TODO: Implement removeDuplicatesSorted().
     * Assumes the list is sorted and removes all duplicate values so each element appears only once.
     * Time Complexity: O(n)
     *
     * Hints:
     * - Compare curr.elem with curr.next.elem.
     * - If equal, unlink curr.next and decrement size.
     */
    public void removeDuplicatesSorted() {
        // TODO: Implement removeDuplicatesSorted()
        throw new UnsupportedOperationException("TODO: Implement removeDuplicatesSorted()");
    }

    /**
     * TODO: Implement removeDuplicatesUnsorted().
     * Removes duplicate values from an unsorted list.
     *
     * Approach 1: Nested loops (O(n^2) time, O(1) space).
     * Approach 2: Use a HashSet to track seen elements (O(n) time, O(n) space).
     */
    public void removeDuplicatesUnsorted() {
        // TODO: Implement removeDuplicatesUnsorted()
        throw new UnsupportedOperationException("TODO: Implement removeDuplicatesUnsorted()");
    }

    /**
     * TODO: Implement findMiddle().
     * Finds and returns the element at the middle of the list.
     * If the list has an even number of elements, returns the second middle (or first, specify convention).
     *
     * Hints:
     * - Standard approach: Two pointers (slow moves 1 step, fast moves 2 steps).
     * - CDLL unique approach: Pointer 'start' at head, 'end' at tail, move start.next and end.prev
     *   towards each other until start == end or start.next == end!
     *
     * @return middle node value
     */
    public int findMiddle() {
        // TODO: Implement findMiddle()
        throw new UnsupportedOperationException("TODO: Implement findMiddle()");
    }

    /**
     * TODO: Implement equals(CircularDoublyLL other).
     * Checks if this list and 'other' list contain the exact same elements in the exact same order.
     *
     * @param other list to compare with
     * @return true if identical, false otherwise
     */
    public boolean equals(CircularDoublyLL other) {
        // TODO: Implement equals(CircularDoublyLL other)
        throw new UnsupportedOperationException("TODO: Implement equals(CircularDoublyLL other)");
    }

    /**
     * TODO: Implement copy().
     * Creates and returns a complete deep copy of this list.
     *
     * @return deep copy of this CircularDoublyLL
     */
    public CircularDoublyLL copy() {
        // TODO: Implement copy()
        throw new UnsupportedOperationException("TODO: Implement copy()");
    }
}
