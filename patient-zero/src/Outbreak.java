import java.util.*;

/**
 * Mission 5. Dynamic programming on the tree of infections. Root the tree in person 0.
 * Input.Tree.neighbours() gives the neighbours of every person. The number of people is at most about 100, so a recursive DFS is fine here.
 */
public class Outbreak {

    /**
     * Choose people to test so that for every link (somebody infected somebody) at least one of the two is tested,
     * and the total cost of testing is as small as possible. cost[v] is the price of testing person v.
     * Returns tested[v] = true if person v is tested.
     *
     * State for every person v (looking only at v and everything below it): the smallest cost when v IS tested, and
     * the smallest cost when v is NOT tested. What must then be true about the children of v in each case?
     * Do not forget to rebuild the answer (who exactly is tested), not only the cost: the picture needs it.
     * Main calls this twice: with all costs equal to 1 (the line "Tests needed") and with the real costs ("Cheapest testing").
     */
    public static boolean[] cheapestTests(int n, int[][] nb, int[] cost) {
        long[] take = new long[n];
        long[] skip = new long[n];
        boolean[] tested = new boolean[n];

        dfsDP(0, -1, nb, cost, take, skip);

        tested[0] = take[0] <= skip[0];
        reconstruct(0, -1, nb, take, skip, tested);

        return tested;
    }

    private static void dfsDP(int v, int parent, int[][] nb, int[] cost, long[] take, long[] skip) {
        take[v] = cost[v];
        skip[v] = 0;
        for (int u : nb[v]) {
            if (u == parent) continue;
            dfsDP(u, v, nb, cost, take, skip);
            take[v] += Math.min(take[u], skip[u]);
            skip[v] += take[u];
        }
    }

    private static void reconstruct(int v, int parent, int[][] nb, long[] take, long[] skip, boolean[] tested) {
        for (int u : nb[v]) {
            if (u == parent) continue;
            if (!tested[v]) {
                tested[u] = true;
            } else {
                tested[u] = take[u] <= skip[u];
            }
            reconstruct(u, v, nb, take, skip, tested);
        }
    }

    /**
     * The number of links on the longest chain of infections, that is the longest path in the tree (counted in links).
     * A path goes up from one person and down to another; for every person you need the two longest ways down.
     * Example: a line 0-1-2-3 has chain 3; a star with centre 0 and leaves 1, 2, 3 has chain 2.
     */
    public static int longestChain(int n, int[][] nb) {
        int[] best = {0};
        dfsChain(0, -1, nb, best);
        return best[0];
    }

    private static int dfsChain(int v, int parent, int[][] nb, int[] best) {
        int top1 = 0, top2 = 0;
        for (int u : nb[v]) {
            if (u == parent) continue;
            int h = dfsChain(u, v, nb, best) + 1;
            if (h > top1) {
                top2 = top1;
                top1 = h;
            } else if (h > top2) {
                top2 = h;
            }
        }
        best[0] = Math.max(best[0], top1 + top2);
        return top1;
    }
}
