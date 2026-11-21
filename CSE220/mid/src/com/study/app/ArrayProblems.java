package com.study.app;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;

/**
 * ArrayProblems
 * Comprehensive, production-grade implementations of all core problems
 * from Array.pdf (CSE 220 Midterm & Lab Quiz Practice Set).
 */
public class ArrayProblems {

    // =========================================================================
    // Theory Reference: Multi-Dimensional Array Addressing
    // =========================================================================

    /**
     * Computes the linear index from multidimensional indices in Row-Major order.
     * Formula: LinearIndex = sum_{m=0}^{k-1} (indices[m] * prod_{j=m+1}^{k-1} dims[j])
     */
    public static int toLinearIndex(int[] dims, int[] indices) {
        int linear = 0;
        int stride = 1;
        for (int m = dims.length - 1; m >= 0; m--) {
            linear += indices[m] * stride;
            stride *= dims[m];
        }
        return linear;
    }

    /**
     * Computes the multidimensional index from a linear index in Row-Major order.
     */
    public static int[] toMultiIndex(int[] dims, int linear) {
        int k = dims.length;
        int[] indices = new int[k];
        for (int m = k - 1; m >= 0; m--) {
            indices[m] = linear % dims[m];
            linear /= dims[m];
        }
        return indices;
    }

    // =========================================================================
    // Core 1: 2x2 Spatial Block Pooling / Matrix Compression (Problem 1)
    // =========================================================================

    /**
     * Problem 1: compress_matrix
     * Groups elements in 2x2 blocks and sums elements within each block.
     * Dimensions are guaranteed even.
     * Time: O(M * N), Aux Space: O(M * N) for the compressed result.
     */
    public static int[][] compressMatrix(int[][] mat) {
        if (mat == null || mat.length == 0 || mat[0].length == 0) return new int[0][0];
        int m = mat.length;
        int n = mat[0].length;
        int[][] res = new int[m / 2][n / 2];

        for (int i = 0; i < m; i += 2) {
            for (int j = 0; j < n; j += 2) {
                res[i / 2][j / 2] = mat[i][j] + mat[i + 1][j] + mat[i][j + 1] + mat[i + 1][j + 1];
            }
        }
        return res;
    }

    // =========================================================================
    // Core 2: Column Reductions & Subsequent Differences (Problems 2, 14, 15)
    // =========================================================================

    /**
     * Problem 2: sum_diff
     * Column-wise summations and subsequent differences: diff[j] = colSum[j+1] - colSum[j].
     * Time: O(M * N), Aux Space: O(N)
     */
    public static int[] sumDiff(int[][] matrix) {
        if (matrix == null || matrix.length == 0 || matrix[0].length <= 1) return new int[0];
        int m = matrix.length;
        int n = matrix[0].length;

        int[] colSums = new int[n];
        for (int j = 0; j < n; j++) {
            int sum = 0;
            for (int i = 0; i < m; i++) {
                sum += matrix[i][j];
            }
            colSums[j] = sum;
        }

        int[] diff = new int[n - 1];
        for (int j = 0; j < n - 1; j++) {
            diff[j] = colSums[j + 1] - colSums[j];
        }
        return diff;
    }

    /**
     * Problem 14: quizDifferences
     * 1. Average score for each quiz (column).
     * 2. Difference between consecutive quiz averages (quiz[j+1] - quiz[j]).
     * Time: O(M * N), Aux Space: O(N)
     */
    public static double[] quizDifferences(int[][] matrix) {
        if (matrix == null || matrix.length == 0 || matrix[0].length <= 1) return new double[0];
        int m = matrix.length;
        int n = matrix[0].length;

        double[] avgs = new double[n];
        for (int j = 0; j < n; j++) {
            double sum = 0;
            for (int i = 0; i < m; i++) {
                sum += matrix[i][j];
            }
            avgs[j] = sum / m;
        }

        double[] diffs = new double[n - 1];
        for (int j = 0; j < n - 1; j++) {
            diffs[j] = avgs[j + 1] - avgs[j];
        }
        return diffs;
    }

    /**
     * Problem 15: salesPercentages
     * 1. Total sales for each product (column sum).
     * 2. Percentage contribution of each product to grand total sales.
     * 3. Difference between consecutive product percentages.
     * Time: O(M * N), Aux Space: O(N)
     */
    public static double[] salesPercentages(int[][] matrix) {
        if (matrix == null || matrix.length == 0 || matrix[0].length <= 1) return new double[0];
        int m = matrix.length;
        int n = matrix[0].length;

        double[] prodSums = new double[n];
        double grandTotal = 0;
        for (int j = 0; j < n; j++) {
            double sum = 0;
            for (int i = 0; i < m; i++) {
                sum += matrix[i][j];
            }
            prodSums[j] = sum;
            grandTotal += sum;
        }

        double[] pcts = new double[n];
        for (int j = 0; j < n; j++) {
            pcts[j] = (prodSums[j] / grandTotal) * 100.0;
        }

        double[] diffs = new double[n - 1];
        for (int j = 0; j < n - 1; j++) {
            diffs[j] = pcts[j + 1] - pcts[j];
        }
        return diffs;
    }

    // =========================================================================
    // Core 3: Boundary Ring & Column Safe Rotations (Problems 3, 19)
    // =========================================================================

    /**
     * Problem 3: rotate (Outer Ring Anticlockwise In-Place)
     * Rotates only the outermost boundary of an N x N matrix 90 degrees anticlockwise.
     * Internal cells remain untouched.
     * Time: O(N), Aux Space: O(1)
     */
    public static int[][] rotateOuterRingAnticlockwise(int[][] matrix) {
        if (matrix == null || matrix.length <= 1) return matrix;
        int n = matrix.length;

        // Perform 4-way cyclical replacement for each element along the perimeter
        for (int k = 0; k < n - 1; k++) {
            int temp = matrix[0][k];
            matrix[0][k] = matrix[k][n - 1];
            matrix[k][n - 1] = matrix[n - 1][n - 1 - k];
            matrix[n - 1][n - 1 - k] = matrix[n - 1 - k][0];
            matrix[n - 1 - k][0] = temp;
        }
        return matrix;
    }

    /**
     * Problem 19: unlockSafe
     * 9x5 combination lock safe. Each column rotated independently upward/downward
     * so that the middle row (index 4) matches combination 1D array com (length 5).
     * Constraint: O(1) extra space (no new arrays).
     * Time Complexity: O(R * C) where R = 9, C = 5.
     * Space Complexity: O(1) Auxiliary.
     */
    public static int[][] unlockSafe(int[][] mat, int[] com) {
        if (mat == null || com == null) return mat;
        int rows = mat.length;     // 9
        int cols = mat[0].length;  // 5
        int targetRow = 4;

        for (int c = 0; c < cols; c++) {
            int curRow = -1;
            for (int r = 0; r < rows; r++) {
                if (mat[r][c] == com[c]) {
                    curRow = r;
                    break;
                }
            }
            if (curRow != -1 && curRow != targetRow) {
                // Number of positions to shift UPWARD so that curRow lands on targetRow
                int k = (curRow - targetRow + rows) % rows;
                rotateColumnUpward(mat, c, k, rows);
            }
        }
        return mat;
    }

    /**
     * In-place column cyclic shift upward by k positions using 3-step reversal.
     * Auxiliary Space: O(1).
     */
    private static void rotateColumnUpward(int[][] mat, int col, int k, int rows) {
        k = k % rows;
        if (k == 0) return;
        reverseColRange(mat, col, 0, k - 1);
        reverseColRange(mat, col, k, rows - 1);
        reverseColRange(mat, col, 0, rows - 1);
    }

    private static void reverseColRange(int[][] mat, int col, int start, int end) {
        while (start < end) {
            int temp = mat[start][col];
            mat[start][col] = mat[end][col];
            mat[end][col] = temp;
            start++;
            end--;
        }
    }

    // =========================================================================
    // Core 4: Full In-Place Matrix Rotations 90° (Problems 5, 6)
    // =========================================================================

    /**
     * Problem 5: Rotate image 90 degrees clockwise in-place.
     * Algorithm: Transpose then Reverse each row.
     * Time: O(N^2), Aux Space: O(1)
     */
    public static void rotateImage90Clockwise(int[][] matrix) {
        if (matrix == null || matrix.length <= 1) return;
        int n = matrix.length;

        // 1. Transpose: swap(matrix[i][j], matrix[j][i]) for j > i
        for (int i = 0; i < n; i++) {
            for (int j = i + 1; j < n; j++) {
                int temp = matrix[i][j];
                matrix[i][j] = matrix[j][i];
                matrix[j][i] = temp;
            }
        }
        // 2. Reverse each row
        for (int i = 0; i < n; i++) {
            int left = 0, right = n - 1;
            while (left < right) {
                int temp = matrix[i][left];
                matrix[i][left] = matrix[i][right];
                matrix[i][right] = temp;
                left++;
                right--;
            }
        }
    }

    /**
     * Problem 6: Rotate image 90 degrees counter-clockwise in-place.
     * Algorithm: Reverse each row then Transpose (or Transpose then Reverse columns).
     * Time: O(N^2), Aux Space: O(1)
     */
    public static void rotateImage90CounterClockwise(int[][] matrix) {
        if (matrix == null || matrix.length <= 1) return;
        int n = matrix.length;

        // 1. Reverse each row
        for (int i = 0; i < n; i++) {
            int left = 0, right = n - 1;
            while (left < right) {
                int temp = matrix[i][left];
                matrix[i][left] = matrix[i][right];
                matrix[i][right] = temp;
                left++;
                right--;
            }
        }
        // 2. Transpose
        for (int i = 0; i < n; i++) {
            for (int j = i + 1; j < n; j++) {
                int temp = matrix[i][j];
                matrix[i][j] = matrix[j][i];
                matrix[j][i] = temp;
            }
        }
    }

    // =========================================================================
    // Core 5: Anti-Diagonal & Symmetric Palindromic Scans (Problems 4, 7, 8)
    // =========================================================================

    /**
     * Problem 4: zig_zag
     * Traverses NxN array in anti-diagonal lines (Line 1 to 2N-1).
     * Direction along each diagonal: bottom-left to top-right (r--, c++).
     * Time: O(N^2), Aux Space: O(1) (prints / returns list)
     */
    public static List<String> zigzagAntiDiagonal(String[][] arr) {
        List<String> result = new ArrayList<>();
        if (arr == null || arr.length == 0) return result;
        int n = arr.length;

        // Upper triangle: starts at (startRow, 0) for startRow = 0 to n - 1
        for (int startRow = 0; startRow < n; startRow++) {
            int r = startRow, c = 0;
            while (r >= 0 && c < n) {
                result.add(arr[r][c]);
                r--;
                c++;
            }
        }

        // Lower triangle: starts at (n - 1, startCol) for startCol = 1 to n - 1
        for (int startCol = 1; startCol < n; startCol++) {
            int r = n - 1, c = startCol;
            while (r >= 0 && c < n) {
                result.add(arr[r][c]);
                r--;
                c++;
            }
        }
        return result;
    }

    /**
     * Problem 7: decodeMessage (Primary Diagonal Mismatches)
     * Compares primary diagonal alphabets from top-left (i, i) and bottom-right (j, j)
     * moving towards the center. Mismatches are potential apartment names.
     * Odd-length solitary center element is included.
     * Time: O(N), Aux Space: O(N)
     */
    public static List<String> decodeMessagePrimary(String[][] codeword) {
        List<String> mismatches = new ArrayList<>();
        if (codeword == null || codeword.length == 0) return mismatches;
        int n = codeword.length;
        int i = 0, j = n - 1;

        while (i < j) {
            String left = codeword[i][i];
            String right = codeword[j][j];
            if (!left.equals(right)) {
                mismatches.add(left);
                mismatches.add(right);
            }
            i++;
            j--;
        }
        if (i == j) {
            // Center element in odd-dimension matrix has no counterpart
            mismatches.add(codeword[i][i]);
        }
        return mismatches;
    }

    /**
     * Problem 8: decodeMessage (Secondary Diagonal Mismatches)
     * Compares secondary diagonal alphabets from top-right (i, n-1-i) and bottom-left (j, n-1-j)
     * moving towards center. Mismatches are potential apartment names.
     * Time: O(N), Aux Space: O(N)
     */
    public static List<String> decodeMessageSecondary(String[][] codeword) {
        List<String> mismatches = new ArrayList<>();
        if (codeword == null || codeword.length == 0) return mismatches;
        int n = codeword.length;
        int i = 0, j = n - 1;

        while (i < j) {
            String top = codeword[i][n - 1 - i];
            String bot = codeword[j][n - 1 - j];
            if (!top.equals(bot)) {
                mismatches.add(top);
                mismatches.add(bot);
            }
            i++;
            j--;
        }
        if (i == j) {
            mismatches.add(codeword[i][n - 1 - i]);
        }
        return mismatches;
    }

    // =========================================================================
    // Core 6: Extremum Intersection Localization (Problems 9, 10)
    // =========================================================================

    /**
     * Problem 9: findQueenAge
     * Queen's age is at the intersection of row with smallest sum and column with smallest sum.
     * Time: O(M * N), Aux Space: O(1)
     */
    public static int findQueenAge(int[][] matrix) {
        int m = matrix.length;
        int n = matrix[0].length;

        int minRow = 0, minRowSum = Integer.MAX_VALUE;
        for (int i = 0; i < m; i++) {
            int sum = 0;
            for (int j = 0; j < n; j++) {
                sum += matrix[i][j];
            }
            if (sum < minRowSum) {
                minRowSum = sum;
                minRow = i;
            }
        }

        int minCol = 0, minColSum = Integer.MAX_VALUE;
        for (int j = 0; j < n; j++) {
            int sum = 0;
            for (int i = 0; i < m; i++) {
                sum += matrix[i][j];
            }
            if (sum < minColSum) {
                minColSum = sum;
                minCol = j;
            }
        }
        return matrix[minRow][minCol];
    }

    /**
     * Problem 10: findStrongSource
     * Strongest source is at intersection of row with largest sum and column with largest sum.
     * Time: O(N^2), Aux Space: O(1)
     */
    public static int findStrongSource(int[][] matrix) {
        int m = matrix.length;
        int n = matrix[0].length;

        int maxRow = 0, maxRowSum = Integer.MIN_VALUE;
        for (int i = 0; i < m; i++) {
            int sum = 0;
            for (int j = 0; j < n; j++) {
                sum += matrix[i][j];
            }
            if (sum > maxRowSum) {
                maxRowSum = sum;
                maxRow = i;
            }
        }

        int maxCol = 0, maxColSum = Integer.MIN_VALUE;
        for (int j = 0; j < n; j++) {
            int sum = 0;
            for (int i = 0; i < m; i++) {
                sum += matrix[i][j];
            }
            if (sum > maxColSum) {
                maxColSum = sum;
                maxCol = j;
            }
        }
        return matrix[maxRow][maxCol];
    }

    // =========================================================================
    // Core 7: In-Place Triangular Matrix Folding (Problems 12, 13)
    // =========================================================================

    /**
     * Problem 12: toSymLowerTri
     * Adds elements above main diagonal to symmetric counterparts below diagonal,
     * then sets elements above diagonal to zero. Non-square returned unchanged.
     * Time: O(N^2), Aux Space: O(1)
     */
    public static int[][] toSymLowerTri(int[][] matrix) {
        if (matrix == null || matrix.length == 0) return matrix;
        int n = matrix.length;
        for (int i = 0; i < n; i++) {
            if (matrix[i].length != n) return matrix; // Non-square
        }

        for (int i = 0; i < n; i++) {
            for (int j = i + 1; j < n; j++) {
                matrix[j][i] += matrix[i][j];
                matrix[i][j] = 0;
            }
        }
        return matrix;
    }

    /**
     * Problem 13: toSymUpperTri
     * Adds elements below main diagonal to symmetric counterparts above diagonal,
     * then sets elements below diagonal to zero. Non-square returned unchanged.
     * Time: O(N^2), Aux Space: O(1)
     */
    public static int[][] toSymUpperTri(int[][] matrix) {
        if (matrix == null || matrix.length == 0) return matrix;
        int n = matrix.length;
        for (int i = 0; i < n; i++) {
            if (matrix[i].length != n) return matrix; // Non-square
        }

        for (int i = 0; i < n; i++) {
            for (int j = i + 1; j < n; j++) {
                matrix[i][j] += matrix[j][i];
                matrix[j][i] = 0;
            }
        }
        return matrix;
    }

    // =========================================================================
    // Core 8: Dual-Diagonal Symmetric Pin Decryption (Problems 16, 17)
    // =========================================================================

    /**
     * Problem 16: decodeMessage (Absolute Difference between Diagonal & Counter-Diagonal)
     * Res[i] = |message[i][i] - message[i][n-1-i]|.
     * Time: O(N), Aux Space: O(N)
     */
    public static int[] decodePinDiff(int[][] message) {
        if (message == null || message.length == 0) return new int[0];
        int n = message.length;
        int[] pin = new int[n];
        for (int i = 0; i < n; i++) {
            pin[i] = Math.abs(message[i][i] - message[i][n - 1 - i]);
        }
        return pin;
    }

    /**
     * Problem 17: decodeMessage (Absolute Average between Diagonal & Counter-Diagonal)
     * Res[i] = |message[i][i] + message[i][n-1-i]| / 2.0.
     * Time: O(N), Aux Space: O(N)
     */
    public static double[] decodePinAvg(int[][] message) {
        if (message == null || message.length == 0) return new double[0];
        int n = message.length;
        double[] pin = new double[n];
        for (int i = 0; i < n; i++) {
            pin[i] = Math.abs(message[i][i] + message[i][n - 1 - i]) / 2.0;
        }
        return pin;
    }

    // =========================================================================
    // Core 9: Snake Dungeon Traversal to Singly Linked List (Problem 18)
    // =========================================================================

    /**
     * Problem 18: Zigzag_List
     * Traverses dungeon grid in snake movement:
     * - Down until obstacle or bottom edge
     * - Right 1 step
     * - Up until obstacle or top edge
     * - Right 1 step
     * Repeats until cannot move right.
     * Collects values into Singly Linked List.
     * deadlyValue represents black tiles (e.g. -1).
     * Time: O(M * N), Aux Space: O(K) for K nodes in linked list.
     */
    public static Node zigzagList(int[][] matrix, int deadlyValue) {
        if (matrix == null || matrix.length == 0 || matrix[0].length == 0) return null;
        int rows = matrix.length;
        int cols = matrix[0].length;

        int r = 0, c = 0;
        if (matrix[r][c] == deadlyValue) return null;

        Node head = new Node(matrix[r][c], null);
        Node tail = head;
        boolean goingDown = true;

        while (true) {
            if (goingDown) {
                // Move down while safe
                while (r + 1 < rows && matrix[r + 1][c] != deadlyValue) {
                    r++;
                    tail.next = new Node(matrix[r][c], null);
                    tail = tail.next;
                }
                // Step right
                if (c + 1 < cols && matrix[r][c + 1] != deadlyValue) {
                    c++;
                    tail.next = new Node(matrix[r][c], null);
                    tail = tail.next;
                    goingDown = false; // Next move upward
                } else {
                    break;
                }
            } else {
                // Move up while safe
                while (r - 1 >= 0 && matrix[r - 1][c] != deadlyValue) {
                    r--;
                    tail.next = new Node(matrix[r][c], null);
                    tail = tail.next;
                }
                // Step right
                if (c + 1 < cols && matrix[r][c + 1] != deadlyValue) {
                    c++;
                    tail.next = new Node(matrix[r][c], null);
                    tail = tail.next;
                    goingDown = true; // Next move downward
                } else {
                    break;
                }
            }
        }
        return head;
    }

    // Overload with default deadlyValue = -1
    public static Node zigzagList(int[][] matrix) {
        return zigzagList(matrix, -1);
    }

    // =========================================================================
    // Core 10: Rover Local Minimum with Noise Filtering (Problem 20)
    // =========================================================================

    /**
     * Problem 20: localMinima
     * Finds local minimum: strictly less than 4-directional valid neighbors.
     * Neighbors / cells above threshold are ignored as noise.
     * If multiple exist, chooses the last one from bottom-right (largest r, then largest c).
     * Returns int[]{r, c} or null if none.
     * Time: O(M * N), Aux Space: O(1)
     */
    public static int[] localMinima(int[][] matrix, int threshold) {
        if (matrix == null || matrix.length < 3 || matrix[0].length < 3) return null;
        int rows = matrix.length;
        int cols = matrix[0].length;

        int[] dr = {-1, 1, 0, 0};
        int[] dc = {0, 0, -1, 1};

        // Scan backwards from bottom-right across interior cells having 4 cardinal directions
        for (int r = rows - 2; r >= 1; r--) {
            for (int c = cols - 2; c >= 1; c--) {
                int val = matrix[r][c];
                if (val > threshold) continue;

                boolean isMin = true;
                for (int d = 0; d < 4; d++) {
                    int nr = r + dr[d];
                    int nc = c + dc[d];
                    int nVal = matrix[nr][nc];
                    if (nVal <= threshold && val >= nVal) {
                        isMin = false;
                        break;
                    }
                }

                if (isMin) {
                    return new int[]{r, c};
                }
            }
        }
        return null;
    }

    // =========================================================================
    // Core 11: Directional Diagonal Raycasting Energy Grid (Problem 21)
    // =========================================================================

    /**
     * Problem 21: blockEnergySum
     * Raycasts diagonally from each cell following directions[x][y] ("NE", "NW", "SE", "SW")
     * until exiting the grid boundary, summing all visited cell values.
     * Time: O(M * N * min(M, N)), Aux Space: O(M * N) for the returned energy matrix.
     */
    public static int[][] blockEnergySum(int[][] grid, String[][] directions) {
        if (grid == null || directions == null || grid.length == 0 || grid[0].length == 0) {
            return new int[0][0];
        }
        int m = grid.length;
        int n = grid[0].length;
        int[][] energy = new int[m][n];

        for (int r = 0; r < m; r++) {
            for (int c = 0; c < n; c++) {
                String dir = directions[r][c];
                int dr = 0, dc = 0;
                if ("NE".equals(dir)) {
                    dr = -1; dc = 1;
                } else if ("NW".equals(dir)) {
                    dr = -1; dc = -1;
                } else if ("SE".equals(dir)) {
                    dr = 1; dc = 1;
                } else if ("SW".equals(dir)) {
                    dr = 1; dc = -1;
                }

                int sum = 0;
                int curR = r;
                int curC = c;
                while (curR >= 0 && curR < m && curC >= 0 && curC < n) {
                    sum += grid[curR][curC];
                    curR += dr;
                    curC += dc;
                }
                energy[r][c] = sum;
            }
        }
        return energy;
    }
}
