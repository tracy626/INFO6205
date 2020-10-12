package edu.neu.coe.info6205.union_find;

import java.util.Arrays;
import java.util.Random;

public class WQU {
    private final int[] parent;   // parent[i] = parent of i
    private final int[] depth;  // depth[i[ = depth of subtree rooted at i
    private int count;  // number of components

    /**
     * Initializes an empty union–find data structure with {@code n} sites
     * {@code 0} through {@code n-1}. Each site is initially in its own
     * component.
     *
     * @param n the number of sites
     * @throws IllegalArgumentException if {@code n < 0}
     */
    public WQU(int n) {
        count = n;
        parent = new int[n];
        depth = new int[n];
        for (int i = 0; i < n; i++) {
            parent[i] = i;
            depth[i] = 0;
        }
    }

    public void show() {
        for (int i = 0; i < parent.length; i++) {
            System.out.printf("%d: %d, %d\n", i, parent[i], depth[i]);
        }
    }

    /**
     * Returns the number of components.
     *
     * @return the number of components (between {@code 1} and {@code n})
     */
    public int count() {
        return count;
    }

    /**
     * Returns the component identifier for the component containing site {@code p}.
     *
     * @param p the integer representing one site
     * @return the component identifier for the component containing site {@code p}
     * @throws IllegalArgumentException unless {@code 0 <= p < n}
     */
    public int find(int p) {
        validate(p);
        int root = p;
        while (root != parent[root]) {
            root = parent[root];
        }
        return root;
    }

    @Override
    public String toString() {
        return "WQU:" + "\n  count: " + count +
                "\n  parents: " + Arrays.toString(parent) +
                "\n  depths: " + Arrays.toString(depth);
    }

    // validate that p is a valid index
    private void validate(int p) {
        int n = parent.length;
        if (p < 0 || p >= n) {
            throw new IllegalArgumentException("index " + p + " is not between 0 and " + (n - 1));
        }
    }

    /**
     * Returns true if the the two sites are in the same component.
     *
     * @param p the integer representing one site
     * @param q the integer representing the other site
     * @return {@code true} if the two sites {@code p} and {@code q} are in the same component;
     * {@code false} otherwise
     * @throws IllegalArgumentException unless
     *                                  both {@code 0 <= p < n} and {@code 0 <= q < n}
     */
    public boolean connected(int p, int q) {
        return find(p) == find(q);
    }

    /**
     * Merges the component containing site {@code p} with the
     * the component containing site {@code q}.
     *
     * @param p the integer representing one site
     * @param q the integer representing the other site
     * @throws IllegalArgumentException unless
     *                                  both {@code 0 <= p < n} and {@code 0 <= q < n}
     */
    public void union(int p, int q) {
        int rootP = find(p);
        int rootQ = find(q);
        if (rootP == rootQ) return;
        // make the root with the deepest leaf be the new root of other
        if (deepestLeaf(p) < deepestLeaf(q)) {
            parent[rootP] = rootQ;
            depth[rootP] = 1;
            updateDepth(rootP);
        } else {
            parent[rootQ] = rootP;
            depth[rootQ] = depth[rootP] + 1;
            updateDepth(rootQ);
        }
        count--;
    }

    /**
     * Update the depth of all nodes/leaves of this site
     * @param p the integer representing one site
     */
    public void updateDepth(int p) {
        for (int i = 0; i < parent.length; i++) {
            if (parent[i] == p && i != p) {
                depth[i]++;
                updateDepth(i);
            }
        }
    }

    /**
     * Calculate the depth of this node/subtree
     * @param p the integer representing one site
     * @return the depth of the deepest leaf of this root
     */
    public int deepestLeaf(int p) {
        int root = p;
        int result = 0;
        for (int i = 0; i < depth.length; i++) {
            if (find(i) == find(root) && i != root) {
                if (depth[i] > result) {
                    result = depth[i];
                }
            }
        }

        return result;
    }

    public static void main(String[] args) {
        int countCon = 0;
        if (args.length == 0)
            throw new RuntimeException("Need input number of sites to create the HWQUPC");
        int n = Integer.parseInt(args[0]);
        System.out.println("Number of sites n = " + n);
        WQU h = new WQU(n);
        while (h.count > 1) {
            Random random = new Random();
            int p = random.nextInt(n);
            int q = random.nextInt(n);

            System.out.println("union (" + p + ", " + q + ")");
            h.union(p, q);
            countCon++;
        }

        System.out.println(h);
        h.show();
        System.out.println("Initiate with " + n + " sites and generate " + countCon + " connections");
    }
}
