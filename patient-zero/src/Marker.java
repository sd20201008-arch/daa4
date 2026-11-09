import java.util.*;

/**
 * Missions 3 and 4. Find a word in a long text.
 */
public class Marker {

    /**
     * Mission 3a. The prefix function of p. pi[i] = the length of the longest proper prefix of p[0..i] that is
     * also a suffix of p[0..i] ("proper" means shorter than p[0..i] itself).
     * Example: p = "ACGAC" gives [0, 0, 0, 1, 2]. The last number is the "Border length" line.
     */
    public static int[] prefixFunction(String p) {
        int m = p.length();
        int[] pi = new int[m];
        for (int i = 1, k = 0; i < m; i++) {
            while (k > 0 && p.charAt(i) != p.charAt(k)) {
                k = pi[k - 1];
            }
            if (p.charAt(i) == p.charAt(k)) {
                k++;
            }
            pi[i] = k;
        }
        return pi;
    }

    /**
     * Mission 3b. KMP. All start positions (counting from 0, in increasing order) where p occurs in text.
     * Overlapping occurrences count: "ACG" occurs in "ACGACG" at 0 and 3, and "AA" in "AAA" at 0 and 1.
     * Use the prefix function: the text pointer never goes back. The number of positions is "Marker hits",
     * the first one is "First hit" (-1 when there is none).
     */
    public static int[] findAll(String text, String p) {
        if (p.isEmpty()) return new int[0];
        int[] pi = prefixFunction(p);
        List<Integer> hits = new ArrayList<>();

        for (int i = 0, k = 0; i < text.length(); i++) {
            while (k > 0 && text.charAt(i) != p.charAt(k)) {
                k = pi[k - 1];
            }
            if (text.charAt(i) == p.charAt(k)) {
                k++;
            }
            if (k == p.length()) {
                hits.add(i - p.length() + 1);
                k = pi[k - 1];
            }
        }

        int[] res = new int[hits.size()];
        for (int i = 0; i < hits.size(); i++) {
            res[i] = hits.get(i);
        }
        return res;
    }

    /**
     * Mission 4. Rabin-Karp. The number of positions i in a such that the word of k letters starting at i
     * (a[i..i+k-1]) also occurs somewhere in b. Count positions, not different words. This is the "Shared words" line.
     * The idea: give every window of k letters a number (a rolling hash, updated in O(1) when the window moves by one),
     * look the number up among the windows of b, and when it is found COMPARE THE LETTERS, because two different words
     * can have the same hash.
     * Example: a = "ACGTAC", b = "TTACGT", k = 4: the words of a are ACGT, CGTA, GTAC; only ACGT is in b, so the answer is 1.
     */
    public static int sharedWords(String a, String b, int k) {
        if (a.length() < k || b.length() < k) return 0;

        long MOD = 1_000_000_007L;
        long BASE = 131L;
        long top = 1;
        for (int i = 1; i < k; i++) {
            top = (top * BASE) % MOD;
        }

        // Сохраняем хеши всех окон длины k из строки B
        Map<Long, List<Integer>> bHashes = new HashMap<>();
        long hB = 0;
        for (int i = 0; i < k; i++) {
            hB = (hB * BASE + b.charAt(i)) % MOD;
        }
        bHashes.computeIfAbsent(hB, x -> new ArrayList<>()).add(0);

        for (int i = 1; i + k <= b.length(); i++) {
            hB = (hB - (b.charAt(i - 1) * top) % MOD + MOD) % MOD;
            hB = (hB * BASE + b.charAt(i + k - 1)) % MOD;
            bHashes.computeIfAbsent(hB, x -> new ArrayList<>()).add(i);
        }

        // Проверяем окна длины k в строке A
        int count = 0;
        long hA = 0;
        for (int i = 0; i < k; i++) {
            hA = (hA * BASE + a.charAt(i)) % MOD;
        }

        for (int i = 0; i + k <= a.length(); i++) {
            if (i > 0) {
                hA = (hA - (a.charAt(i - 1) * top) % MOD + MOD) % MOD;
                hA = (hA * BASE + a.charAt(i + k - 1)) % MOD;
            }

            List<Integer> candidates = bHashes.get(hA);
            if (candidates != null) {
                for (int posB : candidates) {
                    boolean match = true;
                    for (int j = 0; j < k; j++) {
                        if (a.charAt(i + j) != b.charAt(posB + j)) {
                            match = false;
                            break;
                        }
                    }
                    if (match) {
                        count++;
                        break;
                    }
                }
            }
        }
        return count;
    }
}

