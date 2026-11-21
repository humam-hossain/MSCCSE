# CSE 221: Graph Practice Problems — Categorization & Minimal Solving Guide

This guide analyzes all **38 problems** in [`resources/Graph Practice Problems.pdf`](resources/Graph%20Practice%20Problems.pdf). 

Over **55% of the problems in the document are direct duplicates or template clones** differing only in narrative theme or minor numbers. Furthermore, large sections of the PDF cover topics outside the current syllabus (such as Dijkstra's Algorithm, Kruskal/Prim Minimum Spanning Trees, and Kosaraju's SCC algorithm).

By filtering for the current syllabus and selecting the **most complex, multi-concept variations**, you can achieve 100% preparation by solving **only 7 core problems** (plus 2 quick theoretical checks).

---

## 1. Syllabus Scope & Exclusions

### Target Topics (In-Syllabus)
* **Graph Representations & Fundamentals**: Adjacency Matrix vs. Adjacency List, Handshaking Lemma ($\sum \deg(v) = 2m$).
* **Breadth-First Search (BFS)**:
  * Traversal & step-by-step queue simulation
  * Shortest path in unweighted graphs
  * Bipartite graphs (2-coloring & odd-cycle detection)
* **Depth-First Search (DFS)**:
  * Traversal & discovery/finishing times
  * Edge classification (Tree, Back, Forward, Cross edges)
  * Cycle detection (directed & undirected)
  * Directed Acyclic Graphs (DAGs) & Topological Sorting

### Out-of-Syllabus Topics (Skip These Completely)
You can safely skip over **25+ questions** in the PDF:
* ❌ **Dijkstra's Algorithm / Single-Source Shortest Paths in Weighted Graphs**: Problems 14 (part 1), 17, 21, 23, 24, 26, 28, 30 (parts a, b), 35 (parts 1, 2).
* ❌ **Minimum Spanning Trees (Kruskal & Prim)**: Problems 13, 15, 19 (bottom), 20 (bottom), 25, 27, 30 (parts c, d), 32, 35 (parts 3, 4), 36.
* ❌ **Strongly Connected Components (Kosaraju's Algorithm / SCC)**: Problems 14 (part 2), 16, 18, 19, 20 (part b), 29 (part b), 31, 33 (parts c, d, e), 34, 38 (part 1).

---

## 2. The "Lean Master Problem Set" (Only 7 Problems)

To solve the minimum number of problems while covering every pattern, edge case, and variation:

| # | Selected Problem | Category & Core Concept | What Makes It the Most Complete / Complicated Variant |
|:---:|:---|:---|:---|
| **1** | **Problem 3** | Graph Representations & Memory Analysis | Reconstructs an incomplete adjacency matrix from reachability clues and degree constraints; requires comprehensive theoretical memory comparison between Matrix and List across all 4 axes *(directed/undirected, weighted/unweighted, sparse/dense)*. *(Supersedes clone Problem 10).* |
| **2** | **Problem 4** | Graph Generation, $C_3$ Triangles & BFS | Requires generating a 10-node graph using modular arithmetic rules, identifying triangles ($C_3$ cycles), running BFS shortest path, and computing maximum edge capacity for a simple graph. *(Supersedes simple BFS problems 2, 7, 8, 9).* |
| **3** | **Problem 5** | DFS Traversal & Full Edge Classification | Evaluates an arithmetic formula $( (6+2)+5=8 )$ via DFS path tracing from `'('`, computes DFS tree edges, and classifies **all non-tree edges into Tree, Back, Forward, and Cross edges**. *(Supersedes standard DFS problems 6c, 20a).* |
| **4** | **Problem 1 (c, d)** | Bipartite Graph (2-Coloring) & Odd Cycles | Part (c) simulates 2-coloring to partition vertices into two independent sets $A$ and $B$. Part (d) tests the core theoretical equivalence: *A graph contains an odd-length cycle if and only if it is not bipartite*. |
| **5** | **Problem 22** | DAG Modeling & Topological Sorting | Translates real-world clothing dependencies into a DAG, simulates topological sorting with **alphabetical tie-breaking**, and formally tests for the existence of non-trivial SCCs/cycles in a DAG. *(Supersedes Problem 38).* |
| **6** | **Problem 33 (a, b)** | Cycle Detection Theory & $O(V+E)$ DAG Check | Critical theoretical questions: (a) proves why the number of back edges does **not** equal the number of cycles; (b) details the most asymptotically efficient $O(V+E)$ DFS/Kahn cycle detection algorithm. |
| **7** | **Problem 37** | BFS with Obstacles & **Balanced Bipartite Partition** | Part 1 finds shortest paths in an unweighted maze avoiding blocked corridors. Part 2 is the most advanced bipartite problem: requires 2-coloring **plus** an equal-group size constraint ($|A| = |B|$), which links 2-coloring with Subset-Sum / Knapsack DP. |

### Optional Quick Checks (1–2 minutes each)
* **Problem 6 (Part a)**: Standard proof of the Handshaking Lemma ($\sum_{v \in V} \deg(v) = 2m$).
* **Problem 38 (Part 2)**: Practice finding **two distinct valid topological orderings** and explaining why DFS finishing times produce a topological sort ($finish[u] > finish[v]$ for edge $u \to v$).
* **Problem 12**: Tree/DAG file navigator breadcrumbs (`Drive->Work->Tasklist->task1.txt`) using BFS/DFS path reconstruction.

---

## 3. Duplicate Analysis & Head-to-Head Comparisons

| Concept | Problems in PDF | Duplicate Grouping & Decision |
|---|---|---|
| **Matrix Reconstruction from Clues** | Problems 3, 10 | **Problem 3** and **Problem 10** are exact clones. Problem 3 is chosen because its reachability clue deductions are slightly tighter. |
| **Basic BFS Shortest Path** | Problems 2, 4, 7(c), 8, 9, 11(iii) | **Problem 8** (Real Madrid) and **Problem 9** (Barcelona) are trivial 1-pass clones. **Problem 7(c)** and **Problem 11(iii)** are plain BFS runs. **Problem 4** supersedes all of them by integrating modular graph generation, triangle cycle detection, and combinatorial edge limits. |
| **Plain DFS Traversal** | Problems 5, 6(c), 20(a) | **Problem 6(c)** and **20(a)** are basic traversals. **Problem 5** is chosen because it tests the complete classification of **Tree, Back, Forward, and Cross edges**, which is the core mechanism of cycle detection. |
| **Bipartite Graphs / 2-Coloring** | Problems 1(c, d), 11(ii), 37(part 2) | **Problem 11(ii)** is a basic 8-node 2-coloring. **Problem 1** is selected for the odd-cycle equivalence proof, and **Problem 37** is selected for the advanced equal-cardinality constraint ($|A| = |B|$). |
| **Topological Sorting** | Problems 22, 38(part 2) | **Problem 22** is chosen for comprehensive simulation with strict alphabetical tie-breaking and DAG cycle analysis. |

---

## 4. Key Concept Summaries & Algorithmic Blueprints

### A. Graph Representations & Memory (Problem 3)
* **Adjacency Matrix**: Space $O(V^2)$.
  * Checking if edge $(u, v)$ exists: $O(1)$.
  * Finding all neighbors of $u$: $O(V)$.
  * Best for dense graphs ($|E| \approx |V|^2$). For undirected graphs, the matrix is symmetric ($A = A^T$).
* **Adjacency List**: Space $O(V + E)$ for directed, $O(V + 2E) = O(V + E)$ for undirected.
  * Checking if edge $(u, v)$ exists: $O(\deg(u))$.
  * Finding all neighbors of $u$: $O(\deg(u))$.
  * Best for sparse graphs ($|E| \ll |V|^2$).
* **Weighted Graphs**: Matrix stores weights instead of $0/1$ (unconnected pairs use $\infty$ or $0$). List stores pairs `(neighbor, weight)`.

---

### B. BFS: Shortest Path & Cycle Detection (Problem 4)
* **Shortest Path Property**: BFS guarantees that when a node $v$ is first dequeued/visited from source $s$, the recorded level/distance is the true shortest path distance in an unweighted graph ($O(V + E)$).
* **Cycle Detection in BFS (Undirected)**: If during traversal of node $u$, we encounter an already-visited neighbor $v$ such that $v \neq \text{parent}[u]$, a cycle exists.
* **Triangle Counting ($C_3$)**: Triangles are cycles of length 3 ($u - v - w - u$). Found by checking if adjacent nodes share common neighbors:
  $$\text{Triangles} = \frac{1}{6} \operatorname{Tr}(A^3) = \frac{1}{3} \sum_{(u, v) \in E} |N(u) \cap N(v)|$$
* **Simple Graph Edge Capacity**: An undirected simple graph with $n$ nodes has at most $\frac{n(n-1)}{2}$ edges. If it currently has $m$ edges, you can add at most $\frac{n(n-1)}{2} - m$ additional edges.

---

### C. Bipartite Graphs & Odd Cycles (Problems 1 & 37)
* **Fundamental Theorem**: An undirected graph is bipartite (2-colorable) **if and only if it contains no odd-length cycles**.
* **2-Coloring Algorithm (BFS)**:
  1. Assign color `RED` (0) to the starting node.
  2. For every neighbor $v$ of $u$:
     * If $v$ is uncolored, assign $\text{color}[v] = 1 - \text{color}[u]$ and push to queue.
     * If $v$ is already colored and $\text{color}[v] == \text{color}[u]$, an odd cycle is detected $\implies$ **Not Bipartite**.
* **Balanced Bipartite Partition (Problem 37)**:
  * When disconnected components exist, 2-coloring component $i$ yields two color sets of sizes $a_i$ and $b_i$.
  * To make global group sizes equal ($|A| = |B| = |V|/2$), formulate a **0-1 Knapsack / Subset-Sum DP** over the component size differences $|a_i - b_i|$ to determine if components can be flipped such that the sum of vertices in both groups equals exactly $|V|/2$.

---

### D. DFS: Traversal & Edge Classification (Problem 5)
In a directed graph, running DFS partitions edges into four distinct types based on discovery ($d[u]$) and finishing ($f[u]$) times:
1. **Tree Edges**: Edges in the DFS forest that lead to an undiscovered vertex (`WHITE`).
2. **Back Edges**: Edges $(u, v)$ pointing from a descendant $u$ to an ancestor $v$ currently in the recursion stack (`GRAY`). $d[v] < d[u] < f[u] < f[v]$. **A directed graph contains a cycle if and only if DFS produces at least one back edge.**
3. **Forward Edges**: Non-tree edges $(u, v)$ pointing from an ancestor $u$ to a descendant $v$ already completed (`BLACK`). $d[u] < d[v] < f[v] < f[u]$.
4. **Cross Edges**: Edges $(u, v)$ between two nodes where neither is an ancestor of the other ($v$ is already completed (`BLACK`)). $d[v] < f[v] < d[u] < f[u]$.

---

### E. Cycle Detection & DAG Verification Theory (Problem 33)
* **Does the number of back edges equal the number of cycles?**
  * **NO!** 
    * A single back edge can complete multiple distinct simple cycles if the DFS tree path branches.
    * Different DFS starting points or neighbor exploration orders on the exact same graph can produce different numbers of back edges, while the intrinsic set of cycles in the graph remains invariant.
* **$O(V+E)$ DAG Verification**:
  * **Method 1 (DFS 3-Coloring)**: Track states `WHITE` (unvisited), `GRAY` (in recursion stack), `BLACK` (finished). If any edge leads to a `GRAY` vertex, a back edge exists $\implies$ **Not a DAG**. Time: $O(V + E)$.
  * **Method 2 (Kahn's Algorithm)**: Compute in-degrees of all vertices. Push all vertices with in-degree 0 into a queue. Repeatedly dequeue a vertex and decrement the in-degrees of its neighbors. If the total number of dequeued vertices $< |V|$, a cycle exists $\implies$ **Not a DAG**. Time: $O(V + E)$.

---

### F. Topological Sorting (Problems 22 & 38)
* **Applicability**: Valid **only on Directed Acyclic Graphs (DAGs)**. If the graph contains a directed cycle, no topological ordering exists.
* **DFS Post-Order Reversal**:
  * For any directed edge $u \to v$ in a DAG, $f[u] > f[v]$ (node $u$ finishes after node $v$).
  * Therefore, inserting nodes into a linked list/stack upon completion and reversing produces a valid topological sort.
* **Kahn's Algorithm (BFS-based)**:
  * Always select vertices with in-degree 0.
  * When multiple vertices have in-degree 0, breaking ties alphabetically produces a unique, deterministic topological sequence.
* **SCC Relationship in DAGs**:
  * Since a DAG has no directed cycles, every strongly connected component consists of exactly **one single vertex** (trivial SCC).


---
---

# CSE 221: Searching Practice Problems — Categorization & Minimal Solving Guide

This section analyzes all **10 problems** in [`resources/Searching Practice Problems.pdf`](resources/Searching%20Practice%20Problems.pdf).

Out of 10 problems, there are **4 duplicate clusters** where problems are structurally identical (only the narrative theme or min/max direction flips). By selecting the more complex variant from each cluster, you only need to solve **5 problems** to cover every concept and pattern.

---

## 1. The "Lean Master Problem Set" (Only 5 Problems)

| # | Selected Problem | Category & Core Concept | What Makes It the Best Variant |
|:---:|:---|:---|:---|
| **1** | **Problem 4** | Binary Search on Answer Space (Integer Square Root) | Tests 3 algorithms side-by-side: brute-force $O(N)$, jump search $O(\sqrt{N})$, and binary search $O(\log N)$. Forces you to design a binary search where the search space is not an array but a **value range** $[1, N]$. *(Unique — no duplicates.)* |
| **2** | **Problem 5** | Peak Element Finding via Modified Binary Search | Asks you to verify which arrays satisfy single-peak constraints, design an $O(\log N)$ peak-finding algorithm, and reason about correctness when **multiple peaks** exist. *(Supersedes duplicate Problem 10.)* |
| **3** | **Problem 6** | First Missing Number via Binary Search on Sorted Array | A guided 5-part Socratic walkthrough that builds binary search intuition from scratch: determining array bounds, choosing halves based on index-vs-value gaps, and computing middle elements. *(Unique — no duplicates.)* |
| **4** | **Problem 3** | Searching in a Rotated Sorted Array + Merge Sort Simulation | Given a sorted array split at index $i$ (two sorted halves), design an $O(\log N)$ search using binary search on two segments. Part (c) also requires a full **Merge Sort simulation**. *(Unique — no duplicates.)* |
| **5** | **Problem 7** | Amortized Analysis: When to Sort Before Searching | Theoretical justification: why $O(N \log N)$ sorting + $O(\log N)$ binary search beats $O(N)$ linear search when performing **multiple queries** (amortized cost $O(\log N)$ per query after $O(N \log N)$ one-time sort). *(Unique — no duplicates.)* |

---

## 2. Duplicate Analysis & Head-to-Head Comparisons

### Duplicate Cluster 1: Wave Array — Find Maximum (Ascending then Descending)

| Problem | Description | Verdict |
|:---:|:---|:---|
| **Problem 1** | Array ascends then descends (e.g., `[1,3,4,5,9,6,2,-1]`). Find the **maximum** (the peak). | **Clone of Problem 10 and identical concept to Problem 5.** |
| **Problem 10** | Hourly shipment volume increases then decreases. Find the **peak hour** (maximum). Linear + binary search. | **Clone of Problem 1.** |
| **Problem 5** | Find peak element (greater than adjacent elements). Verifies edge cases, designs $O(\log N)$ algorithm, and asks about **multi-peak correctness**. | **Winner** — has the extra multi-peak analysis and array validation sub-questions. |

**Decision**: Solve **Problem 5** only. Problems 1 and 10 are strictly simpler subsets of the same peak-finding binary search.

**Algorithm (all three problems)**:
```
lo, hi = 0, N-1
while lo < hi:
    mid = (lo + hi) // 2
    if arr[mid] < arr[mid + 1]:   # still ascending → peak is to the right
        lo = mid + 1
    else:                          # descending or at peak → peak is to the left (inclusive)
        hi = mid
return lo   # lo == hi == peak index
```
Time: $O(\log N)$.

---

### Duplicate Cluster 2: Wave Array — Find Minimum (Descending then Ascending)

| Problem | Description | Verdict |
|:---:|:---|:---|
| **Problem 2** | Array descends then ascends (e.g., `[9,7,6,4,2,1,3,5,8]`). Find the **minimum** (the valley). | **Clone of Problem 9.** |
| **Problem 9** | Night temperature drops then rises. Find the **coldest hour** (minimum). Linear + binary search. | **Clone of Problem 2.** |

**Decision**: **Skip both.** This is the exact mirror of the peak-finding problem (Problems 1/5/10) with the comparison flipped (`arr[mid] > arr[mid+1]` instead of `<`). If you can solve Problem 5, you can solve Problems 2 and 9 by inverting the comparison.

**Algorithm (mirror of peak-finding)**:
```
lo, hi = 0, N-1
while lo < hi:
    mid = (lo + hi) // 2
    if arr[mid] > arr[mid + 1]:   # still descending → valley is to the right
        lo = mid + 1
    else:                          # ascending or at valley → valley is to the left (inclusive)
        hi = mid
return lo   # lo == hi == valley index
```
Time: $O(\log N)$.

---

### Duplicate Cluster 3: First Occurrence in a Partitioned Array (Binary Search on Transition Point)

| Problem | Description | Verdict |
|:---:|:---|:---|
| **Problem 8** | Farm with white cows followed by black cows (sorted binary array `[0,0,...,0,1,1,...,1]`). Find the serial number of the **first black cow** (first `1`). | **Trivial special case of binary search for first occurrence / lower bound.** |

**Decision**: **Skip.** This is the simplest possible binary search problem — finding the transition point in a `[0,0,...,1,1,...]` array. It's a strict subset of Problem 6 (first missing number) and standard `lower_bound` binary search.

**Algorithm**:
```
lo, hi = 0, N-1
while lo < hi:
    mid = (lo + hi) // 2
    if arr[mid] == 0:   # still white → first black is to the right
        lo = mid + 1
    else:               # black cow → might be the first one, search left
        hi = mid
return lo
```
Time: $O(\log N)$.

---

## 3. Comprehensive Categorization of All 10 Problems

### Category A: Peak / Valley Finding (Modified Binary Search on Unimodal Arrays)

**Core Concept**: Given a unimodal array (increases then decreases, or vice versa), find the extremum in $O(\log N)$ by comparing `arr[mid]` with `arr[mid+1]` to determine which half contains the peak/valley.

| Problem | Variant | Complexity | Redundancy |
|:---:|:---|:---:|:---|
| 1 | Find max in ascending-then-descending array | $O(\log N)$ | Clone of 5 and 10 |
| 2 | Find min in descending-then-ascending array | $O(\log N)$ | Clone of 9 (mirror of 1) |
| 5 | Find peak element + multi-peak analysis | $O(\log N)$ | **Master problem** |
| 9 | Find min temperature (descending-then-ascending) | $O(\log N)$ | Clone of 2 |
| 10 | Find max shipment volume (ascending-then-descending) | $O(\log N)$ | Clone of 1 |

**Solve only**: **Problem 5**.

---

### Category B: Binary Search on Sorted / Partially-Sorted Arrays

**Core Concept**: Standard binary search ($O(\log N)$) or modified binary search on arrays with specific structural properties (rotated, missing elements, partitioned).

| Problem | Variant | Complexity | Redundancy |
|:---:|:---|:---:|:---|
| 3 | Search in a rotated sorted array (two sorted halves) + Merge Sort simulation | $O(\log N)$ | Unique |
| 6 | Find first missing number in sorted sequence using index-value gap analysis | $O(\log N)$ | Unique |
| 8 | Find first `1` in a `[0,...,0,1,...,1]` binary array | $O(\log N)$ | Trivial — subset of 6 |

**Solve only**: **Problem 3** and **Problem 6**. Skip Problem 8.

---

### Category C: Binary Search on Non-Array Search Spaces (Answer Space)

**Core Concept**: Binary search is not restricted to arrays — it works on any **monotonic predicate** over a value range. Here, the search space is $[1, N]$ and the predicate is $\text{mid}^2 \leq \text{key}$.

| Problem | Variant | Complexity | Redundancy |
|:---:|:---|:---:|:---|
| 4 | Integer square root: brute-force $O(N)$, jump $O(\sqrt{N})$, binary search $O(\log N)$ | $O(\log N)$ | Unique |

**Solve**: **Problem 4**.

---

### Category D: Searching Theory & Amortized Cost

**Core Concept**: Why is sorting ($O(N \log N)$) + binary search ($O(\log N)$) preferable to linear search ($O(N)$) even though sorting alone costs more than a single linear scan?

| Problem | Variant | Redundancy |
|:---:|:---|:---|
| 7 | Justify sort-then-binary-search vs. repeated linear search (amortized analysis) | Unique |

**Answer**: For a single query, linear search ($O(N)$) beats sort + binary search ($O(N \log N + \log N)$). However, if you perform $Q$ queries:
* Linear search total: $O(Q \cdot N)$.
* Sort + binary search total: $O(N \log N + Q \cdot \log N)$.

Sort + binary search wins when $Q > \frac{N \log N}{N - \log N} \approx \log N$. For any $Q \gg \log N$, the amortized cost per query is $O(\log N)$ instead of $O(N)$.

**Solve**: **Problem 7**.

---

## 4. Key Concept Summaries & Algorithmic Blueprints

### A. Standard Binary Search Template
```python
def binary_search(arr, target):
    lo, hi = 0, len(arr) - 1
    while lo <= hi:
        mid = (lo + hi) // 2
        if arr[mid] == target:
            return mid
        elif arr[mid] < target:
            lo = mid + 1
        else:
            hi = mid - 1
    return -1  # not found
```
Time: $O(\log N)$. **Precondition**: array must be sorted.

---

### B. Peak Finding (Problem 5) — Modified Binary Search on Unimodal Array
```python
def find_peak(arr):
    lo, hi = 0, len(arr) - 1
    while lo < hi:
        mid = (lo + hi) // 2
        if arr[mid] < arr[mid + 1]:
            lo = mid + 1       # peak is in the right half
        else:
            hi = mid           # peak is at mid or in the left half
    return lo                  # lo == hi == peak index
```
Time: $O(\log N)$.

**Multi-peak correctness** (Problem 5 part iii): If the array has multiple peaks, the algorithm still finds **one valid peak** but not necessarily the global maximum. It converges to whichever peak the binary decisions lead to, since at every step it moves toward a local increase.

---

### C. Integer Square Root (Problem 4) — Binary Search on Answer Space

**Algorithm P** ($O(\sqrt{N})$ — Jump Search):
```python
def sqrt_jump(key):
    i = 1
    while i * i <= key:
        i += 1
    return i - 1
```

**Algorithm R** ($O(\log N)$ — Binary Search):
```python
def sqrt_binary(key):
    lo, hi = 1, key
    result = 0
    while lo <= hi:
        mid = (lo + hi) // 2
        if mid * mid <= key:
            result = mid       # mid might be the answer, try larger
            lo = mid + 1
        else:
            hi = mid - 1       # mid is too big, try smaller
    return result
```

---

### D. Search in Rotated Sorted Array (Problem 3)

Given index $i$ where the rotation occurs, the array has two sorted segments: `arr[0..i-1]` and `arr[i..N-1]`.

```python
def search_rotated(arr, i, key):
    # Determine which segment contains the key
    if key >= arr[0] and key <= arr[i - 1]:
        return binary_search(arr, 0, i - 1, key)      # search left segment
    else:
        return binary_search(arr, i, len(arr) - 1, key) # search right segment
```
Time: $O(\log N)$ — one comparison to pick the half, then standard binary search on one sorted segment.

---

### E. First Missing Number (Problem 6) — Binary Search on Index-Value Gap

**Key Insight**: In a sorted array with no missing numbers, `arr[mid] - arr[0]` should equal `mid`. If `arr[mid] - arr[0] > mid`, there is a missing number in the left half (including mid). Otherwise, the first missing number is in the right half.

```python
def first_missing(arr):
    lo, hi = 0, len(arr) - 1
    while lo < hi:
        mid = (lo + hi) // 2
        # expected value at mid if no gaps: arr[0] + mid
        if arr[mid] - arr[0] == mid:
            lo = mid + 1       # no gap in left half → search right
        else:
            hi = mid           # gap exists in left half → search left
    # The first missing number is arr[lo - 1] + 1
    return arr[lo - 1] + 1
```
Time: $O(\log N)$.

**Problem 6 specific answers**:
* **(a)**: $N = 9$, max = 15. If no numbers were missing, the array would span 9 consecutive values ending at 15, so the first value would be $15 - 9 + 1 = 7$. Since numbers **are** missing, the first value must be $\leq 7$. Among choices 6, 8, 10: only **8** works (starting at 8, ending at 15 gives range 8, but $15 - 8 + 1 = 8 < 9$... actually, with 9 elements spanning from first to 15 with gaps: first = $15 - 9 + 1 = 7$ if no gaps, so with gaps first must be smaller. Among 6, 8, 10: starting at 10 gives only 6 possible values (10–15) but we need 9 elements — impossible. Starting at 8 gives 8 possible values (8–15) but we need 9 — impossible. Starting at **6** gives 10 possible values (6–15) for 9 slots — works with exactly 1 missing number). Answer: **6**.
* **(c)**: $N = 7$, min = 2, max = 12, middle value = 5. Expected value at index 3 if no gaps: $2 + 3 = 5$. Since $5 == 5$, the left half has no missing numbers → first missing is on the **right**.


---
---

# CSE 221: Sorting Practice Problems — Categorization & Minimal Solving Guide

This section analyzes all **14 problems** in [`resources/Sorting Practice Problems.pdf`](resources/Sorting%20Practice%20Problems.pdf).

Over **50% of the problems in this document are redundant duplicates or simpler subsets** of each other:
* **Problems 2, 4, and 10(c)** are the exact same QuickSort pivot-identification puzzle.
* **Problems 12 and 13** are verbatim clones (identical ternary merge sort and counting sort questions, only changing star ratings to exam marks).
* **Problems 7(e), 8(b), and 9** ask for the exact same QuickSort worst-case construction.
* **Problem 6** is a simpler subset of **Problem 3**.

By selecting the **most comprehensive, multi-concept variants**, you can achieve 100% mastery of all syllabus sorting topics by solving **only 7 core problems** (plus 2 optional quick checks).

---

## 1. The "Lean Master Problem Set" (Only 7 Problems)

| # | Selected Problem | Category & Core Concept | What Makes It the Most Complete / Complicated Variant |
|:---:|:---|:---|:---|
| **1** | **Problem 10** | Bubble vs. Insertion Mechanics & QuickSort Partitioning | **Triple-concept master problem**: (1) Proves why Jimmy’s 5-iteration Bubble Sort correctly extracts the 5 largest elements while 5-iteration Insertion Sort **fails** to extract the 5 smallest; (2) Identifies the hidden pivot from an intermediate QuickSort array; (3) Simulates partitioning with an explicitly chosen pivot value. *(Completely supersedes clone Problems 2 and 4).* |
| **2** | **Problem 8** | Systems Design: Stability, Memory Constraints & Algorithm Trade-offs | Connects real-world warehouse constraints with core sorting mechanics: analyzes why **Merge Sort** is mandated for stability and guaranteed $O(n \log n)$ time, explains why **QuickSort** degrades to $O(n^2)$ when memory runs out, and proposes **HeapSort** as the in-place $O(n \log n)$ solution. *(Supersedes simple worst-case questions in Problem 9 and Problem 7e).* |
| **3** | **Problem 11** | Defeating Median-of-Three QuickSort & Custom Oracles | **Highest theoretical depth**: (1) Requires constructing an explicit 7-element permutation of $\{1..7\}$ that defeats the median-of-three heuristic and forces worst-case QuickSort; (2) Applies $O(n)$ Counting Sort to graph node degrees ($k \le n-1$); (3) Exploits an $O(1)$ `find_tallest` oracle to boost Selection Sort from $O(n^2)$ to $O(n)$. |
| **4** | **Problem 12** | Ternary Merge Sort & Zero-Comparison Counting Sort | Analyzes discrete small-range sorting ($k=5$ ratings) with costly comparisons ($O(n)$ Counting Sort, 0 comparisons). Implements the dividing step for **3-way (Ternary) Merge Sort** and proves via the Master Theorem why ternary division **fails** to improve asymptotic complexity. *(100% supersedes verbatim clone Problem 13).* |
| **5** | **Problem 14** | In-Place Sign Partitioning, Boundary Binary Search & 4-Scenario Selector | Tests 3 distinct skills: (1) Reorganizing positive/negative integers in-place in $O(N)$ time and $O(1)$ space using two-pointer partitioning; (2) Applying modified Binary Search to find the leftmost positive boundary in $O(\log N)$; (3) Selecting the optimal sorting algorithm across 4 extreme input distributions. |
| **6** | **Problem 7** | Counting Sort Extensions (Negatives & Floats) & Query Amortization | Extends Counting Sort beyond basic natural numbers: (1) Handles negative integers via minimum-offset shifting; (2) Handles floating-point numbers via decimal scaling; (3) Analyzes the memory threshold between QuickSort and Merge Sort; (4) Proves the mathematical query threshold $M$ where sorting + binary search beats repeated linear search. |
| **7** | **Problem 3** | Exploiting Alternating Array Structure ($O(n^2) \to O(n)$) | Given an array with even indices decreasing and odd indices increasing, shows how to transform an $O(n^2)$ sorting task into an optimal $O(n)$ linear sort by de-interleaving, reversing, and running the two-pointer `merge` step from Merge Sort. Includes $O(\log n)$ binary search insertion. *(Completely supersedes the simpler 2-segment Problem 6).* |

### Optional Quick Checks (1–2 minutes each)
* **Problem 1**: Simulate standard in-place sorting (Selection Sort / Insertion Sort) on an 8-item table of YouTube views in descending order, and analyze best-case runtime when rerun on sorted input ($O(n)$ for Insertion Sort vs. $O(n^2)$ for Selection Sort).
* **Problem 5**: Standard QuickSort recursion trace on `[51, 70, 28, 12, 75, 62, 88, 43]` always picking the last element as pivot.

---

## 2. Duplicate Analysis & Head-to-Head Comparisons

| Concept / Pattern | Problems in PDF | Duplicate Grouping & Selection Decision |
|---|---|---|
| **QuickSort Pivot Identification** | Problems 2, 4, 10(c) | **Problem 4** and **Problem 10(c)** use the *exact same array* `[13, 11, 19, 7, 23, 37, 29, 53, 59, 41]`. **Problem 2** uses `[31, 19, 3, 17, 23, 37, 59, 61, 71, 43]`. **Problem 10** is chosen because it includes this exact puzzle *plus* adds subproblem 10(d) (simulating partitioning with pivot 13) and 10(b) (Jimmy’s Bubble vs. Insertion sort partial-pass proof). |
| **Ternary Merge Sort & Discrete Range Sort** | Problems 12, 13 | **Problem 12** and **Problem 13** are 100% clone duplicates. Problem 12 uses 5-star ratings ($k=5$) while Problem 13 uses exam scores ($k=100$). Subproblems (b) and (c) (Ternary Merge Sort dividing step and Master Theorem asymptotic proof) are word-for-word identical. Solving **Problem 12** completely covers both. |
| **QuickSort Worst-Case Analysis** | Problems 7(e), 8(b), 9, 11(c) | **Problem 9** is a basic definition check ($O(N^2)$ for sorted array with last element pivot). **Problem 7(e)** and **8(b)** embed this into memory and system constraints. **Problem 11(c)** is far more advanced, requiring you to construct a worst-case input that defeats the **Median-of-Three** pivot selection heuristic. |
| **Two Pre-Sorted Subarrays / Structure Exploitation** | Problems 3, 6 | **Problem 6** gives two pre-sorted contiguous segments $A[0..i-1]$ and $A[i..N-1]$. **Problem 3** is more challenging and creative: even indices are decreasing and odd indices are increasing, requiring de-interleaving and an $O(n)$ reversal before the two-pointer merge. **Problem 3** supersedes Problem 6. |
| **Counting Sort Domain Extensions** | Problems 7(b, c), 11(d), 12(a), 14(c) | **Problem 11(d)** and **12(a)** test recognizing bounded integer ranges ($k \le n-1$ connections or $k=5$ ratings). **Problem 7(b, c)** tests algorithmic modifications: shifting by the minimum value to handle negatives, and scaling by $10^d$ to handle floating-point decimals. |

---

## 3. Comprehensive Solutions & Technical Blueprints

### A. QuickSort Mechanics: Pivot Identification & Partition Simulation (Problem 10)

#### 1. Identifying the Pivot (Problem 10c / Problem 4)
* **Given Array after 1st Partition**: `[13, 11, 19, 7, 23, 37, 29, 53, 59, 41]` (first element was chosen as pivot).
* **Core Invariant of Partitioning**: The pivot element $P$ must end up at an index $k$ such that:
  $$\max(A[0 \dots k-1]) \le P \le \min(A[k+1 \dots N-1])$$
* **Evaluation**:
  * Check $A[4] = 23$:
    * Left subarray $A[0 \dots 3] = [13, 11, 19, 7] \implies \max = 19 \le 23$ (All elements to the left are smaller).
    * Right subarray $A[5 \dots 9] = [37, 29, 53, 59, 41] \implies \min = 29 \ge 23$ (All elements to the right are larger).
  * Check candidate $A[5] = 37$: On its right sits $29 < 37$, violating the invariant.
  * Check candidate $A[7] = 53$ or $A[8] = 59$: On their right sits $41$, violating the invariant.
* **Conclusion**: **23** was the pivot element before partitioning. It was originally at index 0 and swapped to index 4 during partitioning.
*(For Problem 2: in `[31, 19, 3, 17, 23, 37, 59, 61, 71, 43]`, the pivot is **37** at index 5).*

#### 2. Partitioning Simulation with Pivot = 13 (Problem 10d)
Starting from `[13, 11, 19, 7, 23, 37, 29, 53, 59, 41]` using pivot $P = 13$:
* Elements strictly smaller than 13: `11, 7` (count = 2)
* Pivot element: `13` (placed at index 2)
* Elements strictly larger than 13: `19, 23, 37, 29, 53, 59, 41`
* **Resulting Array**: `[11, 7, 13, 19, 23, 37, 29, 53, 59, 41]` (or any valid permutation where $\{11, 7\}$ precede $13$ and the rest follow $13$).

---

### B. Partial Iterations: Bubble Sort vs. Insertion Sort (Problem 10b)

Jimmy wants to find the 5 largest and 5 smallest numbers from $N > 10$ distinct integers using 5 iterations of Bubble Sort and Insertion Sort:

* **Bubble Sort (5 iterations) $\to$ 5 Largest Numbers: SUPPORTED (VALID)**
  * **Proof**: In ascending Bubble Sort, pass $k$ compares adjacent elements from index 0 to $N-k$. Each pass bubbles the largest unsorted element into its final position at the right end.
  * Pass 1 places the 1st largest at $A[N-1]$.
  * Pass 2 places the 2nd largest at $A[N-2]$.
  * By mathematical induction, after $k=5$ passes, the subarray $A[N-5 \dots N-1]$ is guaranteed to contain the **globally 5 largest numbers** in sorted order.

* **Insertion Sort (5 iterations) $\to$ 5 Smallest Numbers: NOT SUPPORTED (INVALID)**
  * **Proof**: Insertion Sort operates locally from left to right. In iteration $i$, it inserts $A[i]$ into its correct relative position within the prefix $A[0 \dots i-1]$.
  * After 5 iterations (inserting $A[1]$ through $A[5]$), only the prefix $A[0 \dots 5]$ (size 6) is sorted among itself.
  * The remaining $N-6$ elements ($A[6 \dots N-1]$) have **never been inspected or compared**.
  * **Counterexample**: Let $A = [50, 60, 70, 80, 90, 100, 1, 2, 3, 4, 5]$ ($N=11$). After 5 iterations of Insertion Sort, the first 6 elements are `[50, 60, 70, 80, 90, 100]`. The true 5 smallest numbers (`1, 2, 3, 4, 5`) are sitting completely untouched at indices 6–10.

---

### C. Logistics System Design: Stability & Memory Trade-offs (Problem 8)

| Feature | Merge Sort (Problem 8a) | QuickSort (Problem 8b.i) | HeapSort (Problem 8b.ii) |
|---|---|---|---|
| **Worst-Case Time** | $\Theta(n \log n)$ | $\Theta(n^2)$ | $\Theta(n \log n)$ |
| **Average Time** | $\Theta(n \log n)$ | $\Theta(n \log n)$ | $\Theta(n \log n)$ |
| **Auxiliary Space** | $O(n)$ (Out-of-place) | $O(\log n)$ stack (In-place) | $O(1)$ (In-place) |
| **Stability** | **Stable** (preserves ID order) | **Unstable** | **Unstable** |

#### Why Merge Sort for 8(a)?
* Packages must be sorted by delivery deadline, with ties broken by smaller package ID (packages are already sorted by ID initially).
* **Stability requirement**: An algorithm is stable if elements with identical keys maintain their relative initial order.
* Merge Sort maintains stability by using $\le$ when merging: if `L[i].deadline <= R[j].deadline`, choose `L[i]`.
* Guaranteed $O(n \log n)$ runtime in all cases with unconstrained memory.

#### Merge Sort Python Implementation:
```python
def merge_sort(arr):
    if len(arr) <= 1:
        return arr
    mid = len(arr) // 2
    left = merge_sort(arr[:mid])
    right = merge_sort(arr[mid:])
    
    # Merge step: preserve stability using <=
    merged = []
    i = j = 0
    while i < len(left) and j < len(right):
        if left[i]["deadline"] <= right[j]["deadline"]:
            merged.append(left[i])
            i += 1
        else:
            merged.append(right[j])
            j += 1
    merged.extend(left[i:])
    merged.extend(right[j:])
    return merged
```

#### Why QuickSort fails 8(b) and HeapSort succeeds:
* When memory runs out, Merge Sort crashes because its $O(n)$ auxiliary buffer exceeds capacity.
* Standard QuickSort is in-place ($O(1)$ data memory, $O(\log n)$ stack), but degrades to $O(n^2)$ in the worst case (e.g., when deadlines are already sorted or identical).
* **HeapSort** solves both constraints simultaneously:
  1. Guaranteed $O(n \log n)$ worst-case time by building a max-heap in $O(n)$ and performing $n-1$ deletions in $O(n \log n)$.
  2. Operates strictly **in-place** with $O(1)$ auxiliary memory (rearranging elements within the input array).
  3. **Trade-off**: HeapSort is **unstable**, so ties with identical deadlines must include package ID as an explicit composite comparison key `(deadline, package_id)`.

---

### D. Advanced QuickSort: Defeating Median-of-Three (Problem 11c)

Median-of-three chooses:
$$\text{pivot} = \operatorname{median}(A[st], A[mid], A[end]) \quad \text{where } mid = \lfloor (st + end) / 2 \rfloor$$

#### Worst-Case Construction for 7-element permutation of $\{1, 2, 3, 4, 5, 6, 7\}$:
* **Worst-Case Input Array**: `A = [1, 2, 3, 6, 4, 5, 7]`
* **Trace of Failure**:
  * For $N=7$: $st = 0, mid = 3, end = 6$.
  * Sampled elements: $A[0] = 1, A[3] = 6, A[6] = 7$.
  * Triplet: `(1, 6, 7)`. The median is **6**.
  * Notice that 6 is the **2nd largest element** in the entire array!
  * Partitioning around 6 splits the array into:
    * Left subarray: `[1, 2, 3, 4, 5]` (size 5)
    * Right subarray: `[7]` (size 1)
  * Instead of a balanced $3 \text{ vs } 3$ split, median-of-three produced an extremely unbalanced $5 \text{ vs } 1$ split!
  * Repeating this pattern on the remaining subarray continues to produce near-maximal unbalance, driving comparisons toward $O(N^2)$.
  *(Alternative valid worst-case permutations: `[1, 2, 3, 7, 4, 5, 6]`, `[2, 1, 4, 5, 6, 7, 3]`)*.

---

### E. Non-Comparison Sorting & Custom Oracles (Problems 11d, 11e, 7b, 7c, 12a)

#### 1. Graph Node Degree Sorting in $O(n)$ (Problem 11d)
* A factory has $n$ robots; each forms at most $n-1$ connections. Thus, degree $d_i \in [0, n-1]$.
* Since the range of keys $k = n - 1 = O(n)$, **Counting Sort** is optimal.
* Time Complexity: $O(n + k) = O(n + n) = O(n)$.
* Space Complexity: $O(k) = O(n)$ auxiliary array.

#### 2. Boosting Selection Sort to $O(n)$ via $O(1)$ Max-Oracle (Problem 11e)
* Standard Selection Sort runs in $O(n^2)$ because finding the maximum element in an unsorted subarray of size $m$ takes $O(m)$ time.
* If an oracle `find_tallest(A)` finds the tallest robot in $O(1)$ time:
```python
def oracle_selection_sort(A):
    n = len(A)
    for i in range(n - 1):
        # find_tallest on remaining elements A[i...n-1] takes O(1)
        max_idx = find_tallest(A, start=i)
        # Swap tallest into position i
        A[i], A[max_idx] = A[max_idx], A[i]
    return A
```
* **Complexity**: $n-1$ passes $\times O(1)$ per pass = **$O(n)$ total time**, using **$O(1)$ auxiliary space**.

#### 3. Counting Sort for Negative Integers (Problem 7b)
* Standard Counting Sort uses array indices, which cannot be negative.
* **Algorithm**:
  1. Find $min\_val = \min(A)$ and $max\_val = \max(A)$.
  2. Shift all elements: $A'[i] = A[i] - min\_val$. Now every $A'[i] \ge 0$, spanning range $[0, max\_val - min\_val]$.
  3. Run standard Counting Sort on $A'$.
  4. Restore original values: $A[i] = A'[i] + min\_val$.

#### 4. Counting Sort for Floating-Point Numbers (Problem 7c)
* For array `[2.5, 1.2, 6.7, 1.7, 9.3, 2.2, 7.7, 0, 4, -5.1, 2, 5, 5.2]`:
* All numbers have at most 1 decimal digit.
* **Algorithm**:
  1. Multiply each number by 10 to convert to integers: e.g., $2.5 \to 25$, $-5.1 \to -51$.
  2. Apply the negative-integer offset from 7(b): $min\_val = -51$, shift by $+51$.
  3. Sort with Counting Sort.
  4. Invert operations: subtract 51, then divide by 10.0 to restore floating-point values.

#### 5. Expensive Comparisons Analytics (Problem 12a / 13a)
* E-commerce platform sorting millions of 1-to-5 star ratings where comparisons are computationally expensive.
* **Counting Sort** is ideal: keys are discrete integers $\{1, 2, 3, 4, 5\}$.
* Counting sort tallies frequencies into 5 buckets using direct array indexing: **zero comparisons are performed**, eliminating comparison overhead entirely. Time: $O(N + 5) = O(N)$.

---

### F. Ternary Merge Sort & Master Theorem Proof (Problem 12b, 12c)

#### 1. Dividing Step Implementation (Problem 12b)
```python
def ternary_merge_sort(arr, low, high):
    if low >= high:
        return
    
    # Calculate two trisection points
    third = (high - low) // 3
    mid1 = low + third
    mid2 = low + 2 * third
    
    # Recursively sort the 3 thirds
    ternary_merge_sort(arr, low, mid1)
    ternary_merge_sort(arr, mid1 + 1, mid2)
    ternary_merge_sort(arr, mid2 + 1, high)
    
    # Merge 3 sorted subarrays into arr[low...high]
    merge_three(arr, low, mid1, mid2, high)
```

#### 2. Does Dividing into 3 Make It Asymptotically Faster? (Problem 12c)
* **Answer: NO.**
* **Mathematical Proof via Master Theorem**:
  * Recurrence relation: $T(n) = 3 T(n/3) + cn$ (merging 3 sorted arrays of total size $n$ takes linear $O(n)$ time).
  * Master Theorem parameters: $a = 3, b = 3, f(n) = cn = \Theta(n^1)$.
  * Critical exponent: $n^{\log_b a} = n^{\log_3 3} = n^1 = n$.
  * Since $f(n) = \Theta(n^{\log_b a})$, Case 2 of the Master Theorem applies:
    $$T(n) = \Theta(n \log_3 n) = \Theta\left(\frac{n \log_2 n}{\log_2 3}\right) = \Theta(n \log n)$$
* **Practical Comparison**:
  * Tree height shrinks from $\log_2 n$ to $\log_3 n = \frac{\log_2 n}{1.585} \approx 0.63 \log_2 n$.
  * However, merging 3 subarrays requires finding the minimum among 3 elements at each step, taking **2 comparisons per element** placed (compared to 1 comparison in 2-way merge).
  * Total comparisons $\approx 2 \times 0.63 n \log_2 n \approx 1.26 n \log_2 n$, which is actually **$\approx 26\%$ more comparisons** than standard 2-way Merge Sort!

---

### G. In-Place Sign Partitioning & Binary Search Boundary (Problem 14a, 14b)

#### 1. Linear Time, Constant Space Segregation (Problem 14a)
Given $A = [15, -13, 3, -4, 5, -6]$, push negatives to front and positives to back in $O(N)$ time and $O(1)$ space using a two-pointer partition:

```python
def segregate_pos_neg(arr):
    left = 0
    for right in range(len(arr)):
        if arr[right] < 0:
            arr[left], arr[right] = arr[right], arr[left]
            left += 1
    return arr
```
* **Step-by-step Execution**:
  * Pointer `left` marks the boundary for negative elements.
  * Encounter `-13`: swap with index 0 $\to [-13, 15, 3, -4, 5, -6]$, `left = 1`.
  * Encounter `-4`: swap with index 1 $\to [-13, -4, 3, 15, 5, -6]$, `left = 2`.
  * Encounter `-6`: swap with index 2 $\to [-13, -4, -6, 15, 5, 3]$, `left = 3`.
* Time: Single pass $O(N)$. Space: $O(1)$ in-place swaps.

#### 2. Modified Binary Search for Leftmost Positive Number (Problem 14b)
In the segregated array `[-13, -4, -6, 15, 5, 3]`, all negatives precede all positives. The boolean condition `arr[i] >= 0` forms a monotonic sequence `[False, False, False, True, True, True]`.

```python
def find_leftmost_positive(arr):
    lo, hi = 0, len(arr) - 1
    first_pos_idx = -1
    while lo <= hi:
        mid = (lo + hi) // 2
        if arr[mid] >= 0:
            first_pos_idx = mid   # potential candidate; try finding earlier one to the left
            hi = mid - 1
        else:
            lo = mid + 1          # negative value; first positive must be to the right
    return first_pos_idx
```
* Time Complexity: $O(\log N)$. Space: $O(1)$.

---

### H. Algorithm Selection Across 4 Extreme Input Scenarios (Problem 14c)

| Input Scenario | Most Efficient Algorithm | Time Complexity | Why It Outperforms the Others |
|---|---|:---:|---|
| **Case 1: Largest number in any index, all others sorted** | **Bubble Sort** (or Insertion Sort) | **$O(N)$** | A single forward pass of Bubble Sort compares adjacent pairs; when it reaches the misplaced maximum, it carries it forward to the final index in exactly $N-1$ comparisons ($O(N)$). Standard Merge/Quick take $O(N \log N)$. |
| **Case 2: Smallest is 0, largest is $2^N$** | **Merge Sort** (or QuickSort) | **$O(N \log N)$** | Key range $K = 2^N$ is exponential. Counting Sort would require catastrophic $O(2^N)$ memory and time. Comparison sorts are oblivious to value range, guaranteeing $O(N \log N)$. |
| **Case 3: Array contains all integers from 1 to $N$** | **Counting Sort** (or Cycle Sort) | **$O(N)$** | Key range $K = N$ is strictly bounded and linear. Counting Sort runs in $O(N + K) = O(N)$ time and $O(N)$ space, beating the $\Omega(N \log N)$ comparison lower bound. |
| **Case 4: Numbers in reverse sorted order** | **Merge Sort** | **$O(N \log N)$** | Reverse order is the worst-case input for Bubble Sort ($O(N^2)$), Insertion Sort ($O(N^2)$), and naive QuickSort ($O(N^2)$). Merge Sort guarantees $O(N \log N)$ regardless of initial arrangement. |

---

### I. Exploiting Alternating Array Structure ($O(n^2) \to O(n)$) (Problem 3)

Given array of $n=8$ integers where:
* Even indices are strictly decreasing: `A[0]=23, A[2]=19, A[4]=7, A[6]=5`
* Odd indices are strictly increasing: `A[1]=2, A[3]=3, A[5]=11, A[7]=13`
* Combined: `[23, 2, 19, 3, 7, 11, 5, 13]`

#### 1. Quadratic Sorting $O(n^2)$ (Part 1)
Run standard **Insertion Sort** or **Bubble Sort** on the array, requiring $\frac{n(n-1)}{2} = 28$ comparisons in $O(n^2)$ time.

#### 2. Jack’s Linear Sorting in $O(n)$ (Part 2)
```python
def sort_alternating(arr):
    n = len(arr)
    # Step 1: De-interleave into two subarrays of size n/2 -> O(n)
    even_part = [arr[i] for i in range(0, n, 2)]  # [23, 19, 7, 5] (decreasing)
    odd_part = [arr[i] for i in range(1, n, 2)]   # [2, 3, 11, 13] (increasing)
    
    # Step 2: Reverse even_part to make it increasing -> O(n/2) = O(n)
    even_part.reverse()                           # [5, 7, 19, 23] (increasing)
    
    # Step 3: Merge two sorted arrays of size n/2 -> O(n)
    result = []
    i = j = 0
    while i < len(even_part) and j < len(odd_part):
        if even_part[i] <= odd_part[j]:
            result.append(even_part[i])
            i += 1
        else:
            result.append(odd_part[j])
            j += 1
    result.extend(even_part[i:])
    result.extend(odd_part[j:])
    return result
```
* **Total Time**: $O(n) + O(n) + O(n) = \mathbf{O(n)}$.

#### 3. Inserting 15 into the Sorted List in $O(\log n)$ (Part 3)
Sorted array: `[2, 3, 5, 7, 11, 13, 19, 23]`.
* Apply **Binary Search** (`bisect_left`):
  * $lo = 0, hi = 7$. $mid = 3 \implies A[3] = 7 < 15 \implies lo = 4$.
  * $mid = 5 \implies A[5] = 13 < 15 \implies lo = 6$.
  * $mid = 6 \implies A[6] = 19 > 15 \implies hi = 5$.
  * Loop terminates at index **6**.
* 15 should be inserted at index 6 (between 13 and 19). Search time: $O(\log n)$.

---

### J. Repeated Query Amortization: Linear Search vs. Sort + Binary Search (Problem 7a)

* Given an unsorted dataset of size $N$ and $M$ search queries:
  * **Option 1: Linear Search per Query**: $M \times O(N) = \mathbf{O(M \cdot N)}$.
  * **Option 2: Sort Once + Binary Search per Query**: $O(N \log N) + M \times O(\log N) = \mathbf{O(N \log N + M \log N)}$.
* **Crossover Point**:
  $$M \cdot N > N \log N + M \log N \iff M(N - \log N) > N \log N \iff M > \frac{N \log N}{N - \log N} \approx \log N$$
* **Conclusion**:
  * If searching only once or twice ($M = 1$), linear search is faster ($O(N) < O(N \log N)$).
  * For repeated queries ($M > \log N$), the initial $O(N \log N)$ sorting cost is **rapidly amortized**, saving massive computational time across queries.


---

# Part IV: Divide & Conquer Practice Problems

## Executive Summary & Deduplication Matrix

The **Divide & Conquer Practice Problems** set consists of **16 problems**. An in-depth algorithmic audit reveals massive structural redundancy: **8 of the 16 problems** are identical variations of the classic Maximum Subarray Sum problem, **2 problems** are identical cross-sum recursion tree trackers, **3 problems** revolve around Karatsuba multiplication and squaring mechanics, and **2 problems** are identical two-level strategy game problems.

By clustering identical algorithmic patterns and selecting the most comprehensive, theoretically challenging problem from each cluster, the entire set of **16 problems is reduced to 6 Core Problems** (plus 1 Base Generalization Synthesis).

### Deduplication & Equivalence Mapping

| Original Problem | Core Algorithmic Concept | Redundant With / Clone Of | Recommended Status | Technical Justification |
| :--- | :--- | :--- | :--- | :--- |
| **Problem 1** (Sparkling Ideas) | Max Subarray Sum ($O(n \log n)$ D&C) | Problems 2, 3, 6, 11, 12, 14, 15 | **Skip** | Standard Max Subarray Sum. Subsumed by Problem 14 (Level 2). |
| **Problem 2** (Farmer Sam Fields) | Max Subarray Sum ($O(n \log n)$ D&C) | Problems 1, 3, 6, 11, 12, 14, 15 | **Skip** | Standard Max Subarray Sum simulation. Subsumed by Problem 14. |
| **Problem 3** (Sparkling Ideas - II) | Max Subarray Sum ($O(n \log n)$ D&C) | Problem 1 (100% clone) | **Skip** | Exact clone of Problem 1 with slight array variation. |
| **Problem 4** (Karatsuba Math & Binary) | Karatsuba Derivation & Base-2 Extension | Problem 8 | **Quick Synthesis** | Synthesized with Problem 8 for arbitrary base $B \in \{2, 16\}$. |
| **Problem 5** (Max Consecutive Zeros) | D&C String Boundary Tracking ($O(n)$) | *None (Unique)* | **CORE (Solve)** | Requires 4-tuple prefix/suffix/max/length state tracking across recursive boundaries. |
| **Problem 6** (Poker Player John) | Max Subarray Sum ($O(n \log n)$ D&C) | Problems 1, 2, 3, 11, 12, 14, 15 | **Skip** | Standard Max Subarray Sum simulation. Subsumed by Problem 14. |
| **Problem 7** (KARAT-SQUARE & Recurrence) | Karatsuba Squaring & Asymptotic Proof | Problems 4, 8 | **CORE (Solve)** | Master Karatsuba problem: algebra completion, recurrence, and Master Theorem comparison against 6-way split. |
| **Problem 8** (Karatsuba Math & Hex) | Karatsuba Derivation & Base-16 Extension| Problem 4 | **Quick Synthesis** | Identical derivation to Problem 4; hex base generalization. |
| **Problem 9** (Football Performance Streak)| Cross-Sum Exceedance & Positive Streak | Problem 10 | **CORE (Solve)** | Master problem: full recursion tree decision count + D&C longest positive run. |
| **Problem 10** (Stock Price Changes) | Cross-Sum Exceedance & Positive Streak | Problem 9 (100% clone) | **Skip** | Exact clone of Problem 9 with identical subquestions. Subsumed by Problem 9. |
| **Problem 11** (Taylor Swift Eras Tour) | Max Subarray Sum ($O(n \log n)$ D&C) | Problems 1, 2, 3, 6, 12, 14, 15 | **Skip** | Standard Max Subarray Sum simulation. Subsumed by Problem 14. |
| **Problem 12** (Dr. Eleanor Manuscripts) | Max Subarray Sum ($O(n \log n)$ D&C) | Problems 1, 2, 3, 6, 11, 14, 15 | **Skip** | Standard Max Subarray Sum simulation. Subsumed by Problem 14. |
| **Problem 13** (Rivendell Voting Majority) | D&C Majority Element ($O(n \log n)$) | *None (Unique)* | **CORE (Solve)** | Multi-part: impossibility proof, $O(N)$ candidate verification, subproblem divergence, and recursive merge completion. |
| **Problem 14** (Strategy Game - Level 1 & 2)| D&C Min-Element + Max Subarray Sum | Problem 15 | **CORE (Solve)** | Tests both extremum D&C ($O(n)$) and contiguous subarray D&C ($O(n \log n)$). |
| **Problem 15** (Strategy Game - Clone) | D&C Min-Element + Max Subarray Sum | Problem 14 (100% clone) | **Skip** | Exact clone of Problem 14 with minor number tweaks. |
| **Problem 16** (Max-Product Subarray Bug) | Algorithmic Correctness & Karatsuba Recurrence | *None (Unique)* | **CORE (Solve)** | Deepest theoretical problem: bug evaluation with counterexample proof, Karatsuba recurrence modeling $T(N) = 2T(N/2) + O(N^{2.585})$, and Master Theorem Case 3 solution. |

---

## Minimal Essential Problem Set (6 Core Problems + 1 Synthesis)

| Problem # | Algorithmic Archetype | Key Concepts & Complexity | Why It Supersedes Others |
| :---: | :--- | :--- | :--- |
| **14** | **Extremum D&C + Contiguous Max Subarray** | Level 1: $T(n) = 2T(n/2) + O(1) = \Theta(n)$<br>Level 2: $T(n) = 2T(n/2) + O(n) = \Theta(n \log n)$ | Supersedes Problems 1, 2, 3, 6, 11, 12, and 15 by testing both single-element D&C selection and full contiguous interval simulation. |
| **9** | **Recursion Tree Cross-Sum Decision Tracker** | Tree node classification ($Cross > Left \land Cross > Right$)<br>Max consecutive positive elements in $\Theta(n)$ | Supersedes Problem 10. Eliminates blind simulation by requiring an explicit audit of all 7 internal nodes in the recursive call tree. |
| **7** | **Algebraic Karatsuba Squaring & Asymptotic Proof** | Recurrence: $T(n) = 3T(n/2) + O(n) = \Theta(n^{\log_2 3})$<br>Comparison vs. $T(n) = 6T(n/3) + O(n) = \Theta(n^{\log_3 6})$ | Master problem on Karatsuba algebra. Covers binomial expansion, recursive blank filling, and asymptotic Master Theorem comparison. |
| **4 & 8** | **Karatsuba Arbitrary Base Generalization** | Base $B \in \{2, 16\}$; Bit-shifts (`<< k`) and Hex-digit shifts | Combines Problems 4 & 8 into a single unified mathematical proof showing Karatsuba is base-agnostic. |
| **5** | **Binary String Boundary D&C (Consecutive 0s)** | 4-tuple state: `(max_zeros, pref_zeros, suff_zeros, len)`<br>$T(n) = 2T(n/2) + O(1) = \Theta(n)$ | Unique D&C boundary merging pattern; prevents $O(n^2)$ naive string merging. |
| **13** | **Rivendell Majority Element via Divide & Conquer** | Majority impossibility proof, $O(N)$ candidate verification, merge step synthesis, $T(n) = 2T(n/2) + O(n) = \Theta(n \log n)$ | Classic Boyer-Moore/D&C algorithm. Explores why subproblem majorities can diverge from the global majority. |
| **16** | **Max-Product Subarray Bug & Karatsuba Modeling** | Counterexample proof for negative product failure<br>Recurrence: $T(N) = 2T(N/2) + O(N^{2.585}) = \Theta(N^{2.585})$ | Most rigorous theoretical problem. Proves why naive D&C fails on non-monotonic operators and applies Master Theorem Case 3. |

---

## Complete Solutions & Technical Blueprints

### A. Multiplayer Strategy Game: D&C Extremum & Contiguous Subarray (Problem 14)

Given health boxes: `A = [-2, 3, -4, -3, 5, -1, 2, -2]` ($N = 8$).

#### 1. Level 1: Maximum Enemy Health Reduction via D&C (Global Minimum)
* **Goal**: Selecting exactly one box that causes the maximum reduction in opponent's health. Since negative values correspond to opponent damage, maximizing damage means finding the **minimum (most negative) element** in $A$.
* **Algorithm**:
  ```python
  def find_min_dc(A, low, high):
      if low == high:
          return A[low]
      mid = (low + high) // 2
      left_min = find_min_dc(A, low, mid)
      right_min = find_min_dc(A, mid + 1, high)
      return min(left_min, right_min)
  ```
* **Recurrence & Complexity**:
  $$T(N) = 2T(N/2) + O(1) \implies T(N) = \mathbf{\Theta(N)}$$
  By Master Theorem ($a = 2, b = 2, f(N) = O(1) \implies \log_2 2 = 1 > 0$, Case 1).
* **Result**:
  $$\min([-2, 3, -4, -3, 5, -1, 2, -2]) = \mathbf{-4} \quad (\text{Box index } 2)$$
  Choosing the box with value **-4** inflicts the maximum damage of 4 points to the enemy.

#### 2. Level 2: Maximum Health Gain via Contiguous Subarray (Max Subarray Sum)
* **Goal**: Select a contiguous block of boxes to maximize own health in $O(N \log N)$ time.
* **Algorithm Simulation Trace**:
  * **Base Cases ($N=1$)**: Each single box returns its own value.
  * **Size 2 Subarrays**:
    * `[0..1] = [-2, 3]`: Left=-2, Right=3, Cross=1 $\implies$ **Best = 3** at `[1..1]`.
    * `[2..3] = [-4, -3]`: Left=-4, Right=-3, Cross=-7 $\implies$ **Best = -3** at `[3..3]`.
    * `[4..5] = [5, -1]`: Left=5, Right=-1, Cross=4 $\implies$ **Best = 5** at `[4..4]`.
    * `[6..7] = [2, -2]`: Left=2, Right=-2, Cross=0 $\implies$ **Best = 2** at `[6..6]`.
  * **Size 4 Subarrays**:
    * `[0..3] = [-2, 3, -4, -3]`, $mid = 1$:
      * Left max = 3 (`[1..1]`), Right max = -3 (`[3..3]`).
      * Cross sum: Left from 1: $A[1]=3, A[1]+A[0]=1 \implies$ max left border = 3. Right from 2: $A[2]=-4, A[2]+A[3]=-7 \implies$ max right border = -4. Cross = $3 + (-4) = -1$.
      * **Best = 3** at `[1..1]` (`[3]`).
    * `[4..7] = [5, -1, 2, -2]`, $mid = 5$:
      * Left max = 5 (`[4..4]`), Right max = 2 (`[6..6]`).
      * Cross sum: Left from 5: $A[5]=-1, A[5]+A[4]=4 \implies$ max left border = 4 ($A[4..5]$) or $A[4]=5$. Wait, scanning left from mid=5: $i=5 \implies -1$, $i=4 \implies -1+5 = 4$. So max left border = 4. Scanning right from 6: $i=6 \implies 2$, $i=7 \implies 2 + (-2) = 0$. Max right border = 2. Cross = $4 + 2 = 6$ (`[4..6]`).
      * **Best = 6** at `[4..6]` (`[5, -1, 2]`).
  * **Size 8 (Full Array `[0..7]`)**, $mid = 3$:
    * Left half max (`[0..3]`) = 3.
    * Right half max (`[4..7]`) = 6.
    * Cross sum:
      * Scanning left from 3: $A[3]=-3, A[3]+A[2]=-7, \dots \implies$ max left border sum = 0 (or $-3$ if non-empty).
      * Scanning right from 4: $A[4]=5, A[4]+A[5]=4, A[4..6]=6, A[4..7]=4 \implies$ max right border = 6.
      * Cross sum = $-3 + 6 = 3$.
    * $\max(3, 6, 3) = \mathbf{6}$.
* **Optimal Solution**:
  * **Contiguous Block**: Indices `[4..6]` $\implies$ `[5, -1, 2]`.
  * **Maximum Health Gain**: **6 points**.

---

### B. Recursion Tree Cross-Sum Decision Tracker & Longest Positive Run (Problem 9)

Given net goal difference array: `A = [1, 4, 3, -5, 5, 6, 1, -4]` ($N = 8$).

#### 1. Part 1: Counting When Cross-Sum Exceeds BOTH Left-Sum and Right-Sum
A divide-and-conquer recursion tree on an array of size 8 contains **7 internal (non-base case) nodes**. At each node, the algorithm computes `Left-Sum`, `Right-Sum`, and `Cross-Sum`.

Let us trace every internal node:

| Node Index Range | Mid | Left-Sum (Subarray) | Right-Sum (Subarray) | Cross-Sum (Subarray) | Cross > Left AND Cross > Right? |
| :---: | :---: | :---: | :---: | :---: | :---: |
| `[0..1]` | 0 | 1 (`[1]`) | 4 (`[4]`) | $1 + 4 = \mathbf{5}$ (`[1, 4]`) | **YES** ($5 > 1 \land 5 > 4$) |
| `[2..3]` | 2 | 3 (`[3]`) | -5 (`[-5]`) | $3 + (-5) = \mathbf{-2}$ (`[3, -5]`) | **NO** ($-2 \ngtr 3$) |
| `[0..3]` | 1 | 5 (`[1, 4]`) | 3 (`[3]`) | Left border ($i=1..0$) = 5, Right border ($i=2..3$) = 3 $\implies \mathbf{8}$ (`[1, 4, 3]`) | **YES** ($8 > 5 \land 8 > 3$) |
| `[4..5]` | 4 | 5 (`[5]`) | 6 (`[6]`) | $5 + 6 = \mathbf{11}$ (`[5, 6]`) | **YES** ($11 > 5 \land 11 > 6$) |
| `[6..7]` | 6 | 1 (`[1]`) | -4 (`[-4]`) | $1 + (-4) = \mathbf{-3}$ (`[1, -4]`) | **NO** ($-3 \ngtr 1$) |
| `[4..7]` | 5 | 11 (`[5, 6]`) | 1 (`[1]`) | Left border ($i=5..4$) = 11, Right border ($i=6..7$) = 1 $\implies \mathbf{12}$ (`[5, 6, 1]`) | **YES** ($12 > 11 \land 12 > 1$) |
| `[0..7]` | 3 | 8 (`[1, 4, 3]`) | 12 (`[5, 6, 1]`) | Left border ($i=3..0$) = 3 (`[1, 4, 3, -5]`), Right border ($i=4..7$) = 12 (`[5, 6, 1]`) $\implies \mathbf{15}$ (`[1, 4, 3, -5, 5, 6, 1]`) | **YES** ($15 > 8 \land 15 > 12$) |

* **Total Non-Base Case Evaluations**: 7
* **Number of Times Cross-Sum Exceeds Both Left and Right Sum**: **5 times** (at `[0..1]`, `[0..3]`, `[4..5]`, `[4..7]`, and `[0..7]`).
* **Overall Maximum Goal Difference Streak**: Subarray `[1, 4, 3, -5, 5, 6, 1]` with sum **15**.

#### 2. Part 2: D&C Algorithm for Maximum Consecutive Positive Matches
* **Problem**: Find the maximum length of contiguous matches where every match has a strictly positive goal difference ($A[i] > 0$).
* **Divide & Conquer Strategy**:
  Just like segment tree boundary tracking, return a 4-tuple: `(max_len, prefix_len, suffix_len, is_all_positive)`.
* **Pseudocode**:
  ```python
  def max_consecutive_positive(A, low, high):
      if low == high:
          if A[low] > 0:
              return (1, 1, 1, True)
          else:
              return (0, 0, 0, False)
      
      mid = (low + high) // 2
      L = max_consecutive_positive(A, low, mid)
      R = max_consecutive_positive(A, mid + 1, high)
      
      # Crossing contiguous positive segment
      cross_len = L.suffix_len + R.prefix_len
      max_len = max(L.max_len, R.max_len, cross_len)
      
      # Prefix extends across boundary only if entire left half is positive
      prefix_len = (L.length + R.prefix_len) if L.is_all_positive else L.prefix_len
      suffix_len = (R.length + L.suffix_len) if R.is_all_positive else R.suffix_len
      is_all_positive = L.is_all_positive and R.is_all_positive
      
      return (max_len, prefix_len, suffix_len, is_all_positive)
  ```
* **Complexity**: $T(n) = 2T(n/2) + O(1) = \mathbf{\Theta(n)}$.
* **Output on `[1, 4, 3, -5, 5, 6, 1, -4]`**:
  * Left half positive run: `[1, 4, 3]` $\implies$ length 3.
  * Right half positive run: `[5, 6, 1]` $\implies$ length 3.
  * Maximum consecutive positive matches = **3 matches**.

---

### C. Karatsuba Squaring (`KARAT-SQUARE`) & Recurrence Comparison (Problem 7)

An $n$-digit integer $x$ is partitioned into two $n/2$-digit halves:
$$x = a \cdot 10^{n/2} + b$$
where $a$ represents the leftmost $n/2$ digits and $b$ represents the rightmost $n/2$ digits.

#### 1. Algebraic Expansion (Parts i, ii, iii, iv, v)
* **Part (i)**: $x = \mathbf{a \cdot 10^{n/2} + b}$.
* **Part (ii) - Squaring $x$**:
  $$x^2 = (a \cdot 10^{n/2} + b)^2 = \mathbf{a^2 \cdot 10^n + 2ab \cdot 10^{n/2} + b^2}$$
* **Part (iii) - Line 8 Blank**:
  Notice line 6 computes $S_1 = a^2$ and line 7 computes $S_2 = b^2$.
  To compute $2ab$ with only ONE additional recursive square, we compute:
  $$S_3 = \text{KARAT-SQUARE}(\mathbf{a + b}) = (a + b)^2 = a^2 + 2ab + b^2$$
  Therefore, Line 8 blank is: **`a + b`**.
* **Part (iv) - Line 9 Blank**:
  From $S_3 = a^2 + 2ab + b^2 = S_1 + 2ab + S_2$, we isolate $P = 2ab$:
  $$P = \mathbf{S_3 - S_1 - S_2}$$
* **Part (v) - Line 10 Return Statement**:
  $$\text{return } \mathbf{S_1 \cdot 10^n + P \cdot 10^{n/2} + S_2}$$

#### 2. Running Time of `KARAT-SQUARE` (Part vi)
* The algorithm makes **3 recursive squarings** of numbers with $n/2$ (or at most $n/2+1$) digits.
* Digit addition, subtraction, and power-of-10 shifts (padding zeros) take linear time $O(n)$.
* Recurrence relation:
  $$T(n) = 3T(n/2) + O(n)$$
* Solving via Master Theorem ($a = 3, b = 2, f(n) = O(n)$):
  * Critical exponent: $\log_b a = \log_2 3 \approx 1.585$.
  * Since $f(n) = O(n^1)$ and $1 < 1.585$, Master Theorem Case 1 applies:
  $$T(n) = \mathbf{\Theta(n^{\log_2 3}) \approx \Theta(n^{1.585})}$$

#### 3. Asymptotic Comparison: `KARAT-SQUARE` vs. `KARAT-SQUARE-2` (Part vii)
* `KARAT-SQUARE-2(x)` makes **6 recursive squarings** on $n/3$-digit numbers and combines them in $O(n)$ time:
  $$T_2(n) = 6T(n/3) + O(n)$$
* Solving $T_2(n)$ via Master Theorem ($a = 6, b = 3, f(n) = O(n)$):
  * Critical exponent:
    $$\log_3 6 = \frac{\ln 6}{\ln 3} \approx \frac{1.79176}{1.09861} \approx \mathbf{1.631}$$
  * Since $1 < 1.631$, Master Theorem Case 1 applies:
    $$T_2(n) = \mathbf{\Theta(n^{\log_3 6}) \approx \Theta(n^{1.631})}$$
* **Conclusion**:
  $$\mathbf{\Theta(n^{1.585}) < \Theta(n^{1.631})} \implies \mathbf{KARAT\text{-}SQUARE(x) \text{ is asymptotically faster}}$$
  Dividing into 2 halves with 3 subproblems grows slower than dividing into 3 thirds with 6 subproblems.

---

### D. Karatsuba Generalization: Binary ($B=2$) & Hexadecimal ($B=16$) (Problems 4 & 8)

#### 1. Mathematical Foundation of Karatsuba's Trick
Given two $N$-digit numbers $X$ and $Y$ in base $B$:
$$X = a \cdot B^{N/2} + b, \quad Y = c \cdot B^{N/2} + d$$
Standard multiplication requires 4 subproducts: $ac, ad, bc, bd$.
Karatsuba computes 3 subproducts:
1. $z_0 = bd$
2. $z_2 = ac$
3. $z_1 = (a + b)(c + d) = ac + (ad + bc) + bd$
Then the middle coefficient is recovered by subtraction:
$$ad + bc = z_1 - z_2 - z_0$$
$$X \cdot Y = z_2 \cdot B^N + (z_1 - z_2 - z_0) \cdot B^{N/2} + z_0$$

#### 2. Base Generalization
* **Multiplying Two $N$-Bit Binary Numbers (Base $B=2$, Problem 4)**:
  * **Feasibility**: **YES**, completely valid.
  * Splitting: $X = a \cdot 2^{N/2} + b$ and $Y = c \cdot 2^{N/2} + d$.
  * Shift Operations: Multiplication by $2^N$ and $2^{N/2}$ translates directly to **hardware bit-shifts** (`<< N` and `<< N/2`), which execute in $O(N)$ time.
* **Multiplying Two $N$-Digit Hexadecimal Numbers (Base $B=16$, Problem 8)**:
  * **Feasibility**: **YES**, completely valid.
  * Splitting: $X = a \cdot 16^{N/2} + b$ and $Y = c \cdot 16^{N/2} + d$.
  * Shift Operations: Multiplication by $16^{N/2}$ translates to shifting by $N/2$ hexadecimal digits (equivalent to a binary shift of $4 \times \frac{N}{2} = 2N$ bits).
* **Takeaway**: Karatsuba's identity is an algebraic property of polynomial rings over any positional number system; the algorithm is completely base-independent.

---

### E. Binary String Divide & Conquer: Maximum Consecutive Zeros (Problem 5)

Given binary string $B$ of length $N$ (e.g., `100100000111` $\implies$ 5 consecutive zeros).

#### 1. Divide & Conquer Strategy
A contiguous sequence of zeros in $B[lo..hi]$ either:
1. Lies entirely in the left half $B[lo..mid]$.
2. Lies entirely in the right half $B[mid+1..hi]$.
3. Spans across the midpoint, formed by the suffix zeros of the left half combined with the prefix zeros of the right half.

To merge two halves in $O(1)$ time, each recursive invocation returns a 4-tuple:
$$\text{State} = (\text{max\_zeros}, \text{prefix\_zeros}, \text{suffix\_zeros}, \text{total\_length})$$

#### 2. Complete Pseudocode
```python
def max_consecutive_zeros_dc(B, low, high):
    # Base Case: Single Character
    if low == high:
        is_zero = 1 if B[low] == '0' else 0
        return (is_zero, is_zero, is_zero, 1)
    
    mid = (low + high) // 2
    L_max, L_pref, L_suff, L_len = max_consecutive_zeros_dc(B, low, mid)
    R_max, R_pref, R_suff, R_len = max_consecutive_zeros_dc(B, mid + 1, high)
    
    # Cross consecutive zeros
    cross_zeros = L_suff + R_pref
    max_zeros = max(L_max, R_max, cross_zeros)
    
    # Prefix extends into right half if and only if left half is all zeros
    prefix_zeros = (L_len + R_pref) if (L_pref == L_len) else L_pref
    
    # Suffix extends into left half if and only if right half is all zeros
    suffix_zeros = (R_len + L_suff) if (R_suff == R_len) else R_suff
    
    total_length = L_len + R_len
    return (max_zeros, prefix_zeros, suffix_zeros, total_length)
```

#### 3. Time Complexity
* At each level, merging two 4-tuples involves simple arithmetic comparisons ($O(1)$).
* Recurrence:
  $$T(N) = 2T(N/2) + O(1)$$
* By Master Theorem ($a=2, b=2, \log_2 2 = 1 > 0$):
  $$T(N) = \mathbf{\Theta(N)}$$

---

### F. Rivendell Majority Element via Divide & Conquer (Problem 13)

In an array of $N$ votes, a party has a strict majority if it receives strictly more than $\lfloor N/2 \rfloor$ votes.

#### 1. Impossibility Proof: Can Two Different Parties Both Win a Majority?
* **Proof by Contradiction**:
  * Let party $A$ receive $V_A$ votes and party $B$ receive $V_B$ votes ($A \neq B$).
  * Assume both parties secure a strict majority:
    $$V_A > \frac{N}{2} \quad \text{and} \quad V_B > \frac{N}{2}$$
  * Summing the votes:
    $$V_A + V_B > \frac{N}{2} + \frac{N}{2} = N$$
  * But the total votes cast across all parties cannot exceed $N$ ($V_A + V_B \le N$).
  * Contradiction: $N < V_A + V_B \le N$.
  * **Conclusion**: **No**, two different parties can never both secure a strict majority.

#### 2. $O(N)$ Candidate Verification Algorithm
```python
def verify_majority(arr, party_id):
    count = 0
    for vote in arr:
        if vote == party_id:
            count += 1
    return count > (len(arr) // 2)
```
* **Complexity**: Exactly 1 pass over $N$ elements $\implies \mathbf{O(N)}$ time, $O(1)$ space.

#### 3. Subproblem Divergence Justification & Counterexample
* **Question**: If we split the vote array into two halves, can the majority element of a half differ from the global majority?
* **Answer**: **YES**.
* **Concrete Example**:
  Let $A = [1, 1, 2, 2, 2, 2]$ of size $N=6$.
  * Global majority: **2** (appears 4 times, $4 > 6/2 = 3$).
  * Split into two halves of size 3:
    * Left half `[1, 1, 2]`: Majority is **1** (appears 2 times, $2 > 3/2 = 1$).
    * Right half `[2, 2, 2]`: Majority is **2** (appears 3 times, $3 > 3/2 = 1$).
  * The majority of the left half is 1, which completely differs from the global majority 2.

#### 4. Complete Recursive Algorithm & Merge Step
* **Crucial Insight**: If an element is the global majority in $A$, it **must** be the majority in at least one of the two halves. Thus, we only need to test at most two candidates in the merge step!
```python
def majority_dc(arr, low, high):
    # Base Case: single voter
    if low == high:
        return arr[low]
    
    mid = (low + high) // 2
    left_maj = majority_dc(arr, low, mid)
    right_maj = majority_dc(arr, mid + 1, high)
    
    # If both halves agree, that candidate dominates
    if left_maj == right_maj:
        return left_maj
    
    # Merge Step: Verify candidates using linear scan in arr[low..high]
    half_len = (high - low + 1) // 2
    
    if left_maj != -1:
        left_count = sum(1 for i in range(low, high + 1) if arr[i] == left_maj)
        if left_count > half_len:
            return left_maj
            
    if right_maj != -1:
        right_count = sum(1 for i in range(low, high + 1) if arr[i] == right_maj)
        if right_count > half_len:
            return right_maj
            
    return -1  # No majority
```
* **Recurrence & Complexity**:
  $$T(N) = 2T(N/2) + 2N = 2T(N/2) + O(N)$$
  By Master Theorem Case 2 ($a=2, b=2, c=1 \implies \log_2 2 = 1$):
  $$T(N) = \mathbf{\Theta(N \log N)}$$

---

### G. Max-Product Subarray Algorithmic Failure & Karatsuba Modeling (Problem 16)

Given proposed algorithm:
```python
function MaxProdSubarray(A, left, right):
    if left == right: return A[left]
    mid = (left + right) / 2
    LPS = MaxProdSubarray(A, left, mid)
    RPS = MaxProdSubarray(A, mid + 1, right)
    CPS = CrossProduct(A, left, mid, right)
    return max(LPS, RPS, CPS)

function CrossProduct(A, left, mid, right):
    leftProd = -inf; prod = 1
    for i from mid to left:
        prod = prod * A[i]
        if prod > leftProd: leftProd = prod
    rightProd = -inf; prod = 1
    for i from mid+1 to right:
        prod = prod * A[i]
        if prod > rightProd: rightProd = prod
    return leftProd * rightProd
```

#### 1. Differences from Max-Sum Subarray
1. **Accumulator Operator**: Summation `sum = sum + A[i]` is replaced by multiplication `prod = prod * A[i]`.
2. **Identity Initialization**: Neutral additive element `sum = 0` is replaced by neutral multiplicative element `prod = 1`.
3. **Crossing Combination**: Crossing combine step `leftSum + rightSum` is replaced by `leftProd * rightProd`.

#### 2. Critical Evaluation: Why Naive D&C FAILS (with Counterexample)
* **Verdict**: **The algorithm is INCORRECT and FAILS.**
* **Mathematical Flaw**:
  The product of two negative numbers is **positive**. The maximum crossing product can be produced by multiplying the **most negative** prefix on the left by the **most negative** prefix on the right. However, `CrossProduct` only greedily stores `leftProd` and `rightProd` (the maximum products), discarding highly negative products that would have combined to yield an enormous positive product.
* **Concrete Counterexample**:
  Let $A = [3, -5, -4, 2]$ of size $N=4$ ($left = 0, right = 3, mid = 1$).
  * Left subarray: $A[0..1] = [3, -5] \implies LPS = 3$.
  * Right subarray: $A[2..3] = [-4, 2] \implies RPS = 2$.
  * `CrossProduct(A, 0, 1, 3)` with $mid = 1$:
    * Left scan ($i = 1 \to 0$):
      * $i = 1: prod = 1 \times (-5) = -5 \implies leftProd = -5$.
      * $i = 0: prod = -5 \times 3 = -15 \implies$ since $-15 < -5$, $leftProd$ remains **-5**.
    * Right scan ($i = 2 \to 3$):
      * $i = 2: prod = 1 \times (-4) = -4 \implies rightProd = -4$.
      * $i = 3: prod = -4 \times 2 = -8 \implies$ since $-8 < -4$, $rightProd$ remains **-4**.
    * Combined Crossing Product:
      $$CPS = leftProd \times rightProd = (-5) \times (-4) = \mathbf{20}$$
    * Algorithm returns $\max(LPS, RPS, CPS) = \max(3, 2, 20) = \mathbf{20}$.
  * **Actual True Maximum Product**:
    The entire array $A[0..3]$ has product:
    $$3 \times (-5) \times (-4) \times 2 = \mathbf{120}$$
  * **Failure**: The algorithm returns **20**, missing the true maximum product **120**.

#### 3. Karatsuba Integration: Recurrence Formulation
* In `CrossProduct`, there are $O(N)$ scalar multiplications.
* If every standard multiplication operation (`*`) is replaced by Karatsuba's multiplication function taking $O(N^{1.585})$ time, the combining cost becomes:
  $$O(N) \times O(N^{1.585}) = O(N^{1 + 1.585}) = O(N^{2.585})$$
* **Recurrence Relation**:
  $$T(N) = \mathbf{2T(N/2) + O(N^{2.585})}$$

#### 4. Solving the Recurrence via Master Theorem
* We have:
  $$T(N) = a T(N/b) + f(N), \quad \text{where } a = 2, b = 2, f(N) = \Theta(N^{2.585})$$
* Compute critical exponent:
  $$\log_b a = \log_2 2 = 1$$
* Compare $f(N)$ with $N^{\log_b a} = N^1$:
  $$2.585 > 1 \implies f(N) = \Omega(N^{1 + \epsilon}) \quad \text{for } \epsilon = 1.585 > 0$$
* Check the Regularity Condition for Case 3:
  $$a \cdot f(N/b) = 2 \left(\frac{N}{2}\right)^{2.585} = 2 \cdot 2^{-2.585} N^{2.585} = 2^{1 - 2.585} N^{2.585} = 2^{-1.585} N^{2.585} \approx 0.333 N^{2.585} \le c f(N)$$
  Holds with constant $c = 0.35 < 1$.
* **Final Asymptotic Complexity**:
  $$T(N) = \mathbf{\Theta(N^{2.585})}$$
