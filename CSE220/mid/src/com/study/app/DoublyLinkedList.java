package com.study.app;

public class DoublyLinkedList {

    // ==========================================
    // Core 1 (Problems 2 & 3): Modular Client Removal
    // Linear Doubly Linked List with Dummy Head
    // ==========================================
    public static int getLength(Node head) {
        int count = 0;
        Node curr = head.next;
        while (curr != null) {
            count++;
            curr = curr.next;
        }
        return count;
    }

    public static Node removesClient(Node head, boolean removeDivisible) {
        int len = getLength(head);
        if (len == 0) return head;

        Node curr = head.next;
        while (curr != null) {
            Node nextNode = curr.next;
            boolean shouldRemove = removeDivisible ? (curr.elem % len == 0) : (curr.elem % len != 0);
            if (shouldRemove) {
                // Unlink curr
                curr.prev.next = curr.next;
                if (curr.next != null) {
                    curr.next.prev = curr.prev;
                }
            }
            curr = nextNode;
        }
        return head;
    }

    // ==========================================
    // Core 2 (Problem 4): SLL to CDLL Intersplicing at Index k
    // headX: Dummy-headed Circular DLL
    // headA: Singly Linear Linked List (no dummy)
    // ==========================================
    public static Node insertList(Node headX, Node headA, int k) {
        if (headA == null) return headX;

        // Traverse A to wire its internal prev pointers and locate tailA
        Node currA = headA;
        Node prevA = null;
        while (currA != null) {
            currA.prev = prevA;
            prevA = currA;
            currA = currA.next;
        }
        Node tailA = prevA;

        // Find the node currently at index k in CDLL X
        // Index 0 corresponds to headX.next
        Node targetNode = headX.next;
        for (int i = 0; i < k; i++) {
            targetNode = targetNode.next;
        }
        Node prevNode = targetNode.prev;

        // Splice A between prevNode and targetNode
        prevNode.next = headA;
        headA.prev = prevNode;

        tailA.next = targetNode;
        targetNode.prev = tailA;

        return headX;
    }

    // ==========================================
    // Core 3 (Problem 5): Consecutive Duplicate Deduplication in CDLL
    // ==========================================
    public static Node multiDelete(Node head) {
        if (head == null || head.next == head || head.next.next == head) {
            return head;
        }

        Node curr = head.next;
        while (curr.next != head) {
            if (curr.elem == curr.next.elem) {
                Node dup = curr.next;
                curr.next = dup.next;
                dup.next.prev = curr;
                // Do not advance curr; check next consecutive element
            } else {
                curr = curr.next;
            }
        }
        return head;
    }

    // ==========================================
    // Core 4 (Problems 6 & 7): In-Place Subsegment Pointer Reversal
    // Dummy-headed Circular DLL
    // ==========================================
    public static Node reverseDLLBetweenTwoPositions(Node head, int m, int n) {
        if (m >= n) return head;

        // Locate node at position m and node at position n (1-based from head.next)
        Node nodeM = head.next;
        for (int i = 1; i < m; i++) {
            nodeM = nodeM.next;
        }

        Node nodeN = head.next;
        for (int i = 1; i < n; i++) {
            nodeN = nodeN.next;
        }

        return reverseSubsegmentBetweenNodes(head, nodeM, nodeN);
    }

    public static Node reverseDLLBetweenTwoValues(Node head, int x, int y) {
        Node nodeX = getNodeByValue(head, x);
        Node nodeY = getNodeByValue(head, y);

        if (nodeX == null || nodeY == null || nodeX == nodeY) return head;

        return reverseSubsegmentBetweenNodes(head, nodeX, nodeY);
    }

    public static Node getNodeByValue(Node head, int value) {
        Node curr = head.next;
        while (curr != head) {
            if (curr.elem == value) return curr;
            curr = curr.next;
        }
        return null;
    }

    private static Node reverseSubsegmentBetweenNodes(Node head, Node startNode, Node endNode) {
        Node leftBoundary = startNode.prev;
        Node rightBoundary = endNode.next;

        // Invert pointers for all nodes in [startNode, endNode]
        Node curr = startNode;
        Node stopAfter = endNode;
        boolean reachedEnd = false;

        while (!reachedEnd) {
            if (curr == stopAfter) {
                reachedEnd = true;
            }
            Node nextOriginal = curr.next;
            // Swap next and prev
            curr.next = curr.prev;
            curr.prev = nextOriginal;
            curr = nextOriginal;
        }

        // Reconnect boundaries:
        // leftBoundary connects to endNode
        leftBoundary.next = endNode;
        endNode.prev = leftBoundary;

        // startNode connects to rightBoundary
        startNode.next = rightBoundary;
        rightBoundary.prev = startNode;

        return head;
    }

    // ==========================================
    // Core 5 (Problems 8 & 9): Stable Parity Segregation
    // Linear Doubly Linked List (no dummy)
    // ==========================================
    public static Node rearrangeByParity(Node head, boolean oddFirst) {
        if (head == null || head.next == null) return head;

        Node bucket1Dummy = new Node(0);
        Node bucket1Tail = bucket1Dummy;
        Node bucket2Dummy = new Node(0);
        Node bucket2Tail = bucket2Dummy;

        Node curr = head;
        while (curr != null) {
            Node nextNode = curr.next;
            curr.next = null;
            curr.prev = null;

            boolean inBucket1 = oddFirst ? (curr.elem % 2 != 0) : (curr.elem % 2 == 0);
            if (inBucket1) {
                bucket1Tail.next = curr;
                curr.prev = bucket1Tail;
                bucket1Tail = curr;
            } else {
                bucket2Tail.next = curr;
                curr.prev = bucket2Tail;
                bucket2Tail = curr;
            }
            curr = nextNode;
        }

        if (bucket1Dummy.next == null) {
            Node newHead = bucket2Dummy.next;
            if (newHead != null) newHead.prev = null;
            return newHead;
        }

        // Splice bucket1 and bucket2
        bucket1Tail.next = bucket2Dummy.next;
        if (bucket2Dummy.next != null) {
            bucket2Dummy.next.prev = bucket1Tail;
        }

        Node resultHead = bucket1Dummy.next;
        resultHead.prev = null;
        return resultHead;
    }

    // ==========================================
    // Core 6 (Problems 10 & 11): Bidirectional Two-Pointer Convergence on CDLL
    // ==========================================
    public static boolean isPalindrome(Node head) {
        if (head == null || head.next == head || head.next.next == head) {
            return true;
        }

        Node left = head.next;
        Node right = head.prev;

        while (left != right && left.prev != right) {
            if (left.elem != right.elem) {
                return false;
            }
            left = left.next;
            right = right.prev;
        }
        return true;
    }

    public static Node[] findPair(Node head, int target) {
        if (head == null || head.next == head || head.next.next == head) {
            return null;
        }

        Node left = head.next;
        Node right = head.prev;

        while (left != right && left.prev != right && left != head && right != head) {
            int sum = left.elem + right.elem;
            if (sum == target) {
                return new Node[]{left, right};
            } else if (sum < target) {
                left = left.next;
            } else {
                right = right.prev;
            }
        }
        return null;
    }

    // ==========================================
    // Core 7 (Problem 14): Subsequence Overlap Deletion
    // Linear Doubly Linked Lists
    // ==========================================
    public static Node[] removeOverlaps(Node head1, Node head2) {
        if (head1 == null || head2 == null) {
            return new Node[]{head1, head2};
        }

        Node curr2 = head2;
        while (curr2 != null) {
            Node p1 = head1;
            Node p2 = curr2;

            while (p1 != null && p2 != null && p1.elem == p2.elem) {
                p1 = p1.next;
                p2 = p2.next;
            }

            if (p1 == null) {
                // Full match found starting at curr2 and ending before p2
                Node matchStart = curr2;
                Node matchEnd = (p2 != null) ? p2.prev : getTail(curr2);

                Node before = matchStart.prev;
                Node after = matchEnd.next;

                if (before != null) {
                    before.next = after;
                } else {
                    head2 = after; // Match was at head
                }

                if (after != null) {
                    after.prev = before;
                }

                break; // Remove only first occurrence
            }

            curr2 = curr2.next;
        }

        return new Node[]{head1, head2};
    }

    private static Node getTail(Node node) {
        while (node != null && node.next != null) {
            node = node.next;
        }
        return node;
    }

    // ==========================================
    // Core 8 (Problem 15): Minimum Adjacent Pair Merge
    // Linear Doubly Linked List
    // ==========================================
    public static Node minAdjacentPairMerge(Node head) {
        if (head == null || head.next == null) return head;

        int minSum = Integer.MAX_VALUE;
        Node minPairStart = null;

        Node curr = head;
        while (curr.next != null) {
            int sum = curr.elem + curr.next.elem;
            if (sum < minSum) {
                minSum = sum;
                minPairStart = curr;
            }
            curr = curr.next;
        }

        if (minPairStart == null) return head;

        Node before = minPairStart.prev;
        Node after = minPairStart.next.next;

        Node newNode = new Node(minSum);
        newNode.prev = before;
        newNode.next = after;

        if (before != null) {
            before.next = newNode;
        } else {
            head = newNode;
        }

        if (after != null) {
            after.prev = newNode;
        }

        return head;
    }

    // ==========================================
    // Core 8 (Problem 16): Move Minimum Node to Median
    // Dummy-headed Doubly Linked List (odd number of data nodes)
    // Pure pointer manipulation without value mutation or new lists
    // ==========================================
    public static Node moveMinToMiddle(Node head) {
        if (head == null || head.next == null || head.next.next == null) {
            return head;
        }

        // Step 1: Find minNode and count total data nodes N
        int count = 0;
        int minVal = Integer.MAX_VALUE;
        Node minNode = null;

        Node curr = head.next;
        while (curr != null) {
            count++;
            if (curr.elem < minVal) {
                minVal = curr.elem;
                minNode = curr;
            }
            curr = curr.next;
        }

        if (count <= 1 || minNode == null) return head;

        // Step 2: Unlink minNode from its current position
        minNode.prev.next = minNode.next;
        if (minNode.next != null) {
            minNode.next.prev = minNode.prev;
        }

        // Step 3: Find target median position in the original list
        // Median index (0-based) in a list of size count is count / 2
        // We need to insert minNode after the node at 0-based index (count/2 - 1)
        int targetPredecessorIndex = (count / 2) - 1;
        Node pred = head.next;
        for (int i = 0; i < targetPredecessorIndex; i++) {
            pred = pred.next;
        }

        // Step 4: Splice minNode right after pred
        minNode.next = pred.next;
        minNode.prev = pred;
        if (pred.next != null) {
            pred.next.prev = minNode;
        }
        pred.next = minNode;

        return head;
    }

    // ==========================================
    // Core 9 (Problem 17): Matrix Anti-Diagonal to DLL Synthesis
    // ==========================================
    public static Node autonomousMapping(int[][] matrix) {
        int n = matrix.length;
        Node dh = new Node(-1); // dummy head

        boolean isEven = (n % 2 == 0);

        for (int i = 0; i < n; i++) {
            int val = matrix[i][n - 1 - i];
            boolean matches = isEven ? (val % 2 == 0) : (val % 2 != 0);

            if (matches) {
                // Insert at front (immediately after dh) to reverse the order
                Node newNode = new Node(val);
                newNode.next = dh.next;
                newNode.prev = dh;
                if (dh.next != null) {
                    dh.next.prev = newNode;
                }
                dh.next = newNode;
            }
        }

        return dh;
    }

    // ==========================================
    // Helper Construction & Pretty Printing Utilities
    // ==========================================
    public static Node createCDLL(int[] arr) {
        Node dh = new Node(-1);
        dh.next = dh;
        dh.prev = dh;
        for (int val : arr) {
            Node newNode = new Node(val);
            Node tail = dh.prev;
            tail.next = newNode;
            newNode.prev = tail;
            newNode.next = dh;
            dh.prev = newNode;
        }
        return dh;
    }

    public static Node createLinearDLL(int[] arr, boolean dummyHead) {
        Node head = dummyHead ? new Node(-1) : null;
        Node tail = head;

        for (int val : arr) {
            Node newNode = new Node(val);
            if (head == null) {
                head = newNode;
                tail = newNode;
            } else {
                tail.next = newNode;
                newNode.prev = tail;
                tail = newNode;
            }
        }
        return head;
    }

    public static Node createSLL(int[] arr) {
        Node head = null;
        Node tail = null;
        for (int val : arr) {
            Node newNode = new Node(val);
            if (head == null) {
                head = newNode;
                tail = newNode;
            } else {
                tail.next = newNode;
                tail = newNode;
            }
        }
        return head;
    }

    public static String toCDLLString(Node head) {
        if (head == null) return "null";
        StringBuilder sb = new StringBuilder("DH");
        Node curr = head.next;
        while (curr != head) {
            sb.append(" <-> ").append(curr.elem);
            curr = curr.next;
        }
        sb.append(" <-> (back to DH)");
        return sb.toString();
    }

    public static String toLinearDLLString(Node head, boolean hasDummy) {
        if (head == null) return "null";
        StringBuilder sb = new StringBuilder();
        Node curr = hasDummy ? head.next : head;
        if (hasDummy) sb.append("DH <-> ");
        while (curr != null) {
            sb.append(curr.elem);
            if (curr.next != null) sb.append(" <-> ");
            curr = curr.next;
        }
        return sb.toString();
    }
}
