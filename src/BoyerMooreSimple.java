import java.util.LinkedList;
import java.util.NoSuchElementException;

/**
 * The purpose of this code is to illustrate the followings:
 * <ol>
 * <li> introduction to the Boyer-Moore algorithm for the exact matching problem.
 * <li> implementation of a simplified Boyer-Moore algorithm.
 * </ol>
 * String and pattern matching problems are fundamental to any computer
 * application involving text processing, as well as bioinformatics. The
 * <b>substring (exact) string matching problem</b> is as follows. Given a text
 * string <i>T</i> of length <i>n</i> and a pattern string <i>P</i> of length
 * <i>m</i>, find an occurrence of the pattern within the text. The problem can
 * be formalized as follows.<br>
 * <ul>
 * <li>Let <i>P</i> be the <b>pattern</b>, i.e., the string to be searched for.
 * <li>Let <i>T</i> be the <b>text</b>, i.e., the string to be searched in.
 * <li>Let <i>n</i> be the length of <i>P</i>.
 * <li>Let <i>m</i> be the length of <i>T</i>.
 * <li>Let the elements of <i>P</i> and <i>T</i> be characters drawn from a
 *  finite set of alphabets, such as {0, 1} or {a, b, ...., z}.
 * </ul> 
 * We say that pattern <i>P</i> <b><i>occurs with shift s</i></b> in text
 * <i>T</i> (or, equivalently, that pattern <i>P</i> <b><i>occurs beginning at
 * position s+1</i></b> in <i>T</i>) if 0 <= <i>s</i> <= <i>n</i> - <i>m</i> and
 * <i>T[s+1..s+m]</i> = <i>P[1..m]</i>, i.e., <b><i>T[s..j]</i> = <i>P[j]</i>,
 * for 1 <= <i>j</i> <= <i>m</i></b>. If <i>P</i> occurs with shift <i>s</i> in
 * <i>T</i>, we call <i>s</i> a valid shift; otherwise, we call <i>s</i> an 
 * <b><i>invalid shift</i></b>. The string-matching problem is the problem of
 * finding all valid shifts with which a given pattern <i>P</i> occurs in a
 * given text <i>T</i>.
 * <p>
 * A naive string matching algorithm finds all valid shifts using a loop that
 * checks the condition <i>P[1..m]</i> = <i>T[s+1..s+m]</i> for each of the 
 * <i>n-m+1</i> possible values of <i>s</i>, (see {@link SubstringMatcher}).<br>
 * <pre>
 *     naive-matcher(T, P)
 *         n <- length[T]
 *         m <- length[P]
 *         for s <- 0 to n-m
 *           if P[1..m] = T[s+1..s+m]  // test if shift s is valid
 *             then return s  // pattern occurs at shift s
 * </pre>
 * The <i>if</i> test involves an test involves an implicit loop to check 
 * corresponding character positions until all positions match successfully or
 * a mismatch is found. The naive algorithm takes O((n-m+1)m) time in the worst
 * case. The naive string-matcher is inefficient because information gained 
 * about the text for one value of <i>s</i> is totally ignored in considering
 * other values of <i>s</i>.
 * <p>
 * If the pattern <i>P</i> is relatively long and the alphabet is reasonably 
 * large, then the Boyer-Moore algorithm is considered the most efficient string
 * matching algorithm for natural language, which has a relatively large 
 * alphabet. The algorithm was developed by Robert S. Boyer and J Strother Moore
 * in 1977 [1]. Boyer-Moore algorithm involves a pre-processing stage where the
 * pattern is used to create 2 lookup tables to be used in the actual search
 * stage. The pseudo-code for Boyer-Moore algorithm is as follows.<br>
 * <pre>
 *     boyer_moore_matcher(T, P)
 *         // pre-processing stage
 *         n = length[T]
 *         m = length[P]
 *         last[] = compute_last_occurrence_function(P, m)
 *         suffix[] = compute_good_suffix_function(P, m)
 *         
 *         // search stage
 *         s = 0
 *         while s < n - m do
 *
 *             j <- m - 1
 *             while j >= 0 and P[j] == T[s + j]
 *                 j = j - 1    // compare previous character; right-to-left
 *                 
 *             if j == 0 then
 *                 // pattern occurs at shift s
 *                 // shift to continue match
 *                 s = s + suffix[0]
 *             else
 *                 // mismatch occurs, compute shift amount based on 
 *                 // "bad-character heuristic" and "good-suffix heuristic"
 *                 s = s + max(suffix[j], j - last[T[s+j]])
 * </pre>
 * Boyer-Moore algorithm looks similar to the naive algorithm with a few
 * important differences. Firstly, the Boyer-Moore algorithm compares the
 * pattern against the text from <b>right to left</b> and secondly when a 
 * mismatch occurs, it increases the shift <i>s</i> by a value that is not
 * necessarily 1. The algorithm uses two heuristics to determine the largest
 * amount of shift it can make without missing a valid shift. In particular, it
 * uses the "bad-character heuristic" to determine an increment of <i>s</i> by
 * the amount {@literal j - last[T[s+j]]} while it uses the "good-suffix 
 * heuristic" to determine an increment of <i>s</i> by the amount 
 * {@literal suffix[j]}.
 * <p> 
 * Intuitively, the bad character heuristic tells you how far you should slide
 * the pattern forward when characters do not match, while the good suffix 
 * heuristic uses matched characters (in the suffix because the algorithm
 * matches backwards) to tell you how far you should slide the pattern forward.
 * <p>
 * <b>The bad-character heuristic</b><br>
 * When a mismatch occurs, the bad-character heuristic uses information about
 * where the bad text character {@literal T[s+j]} occurs in the pattern (if it
 * occurs at all) to propose a new shift. Suppose a mismatch occurs at 
 * {@literal P[j] != T[s+j]} for some <i>j</i>, where 1 <= <i>j</i> <= <i>m</i>.
 * Let <i>k</i> be the largest index in the range 1 <= <i>k</i> <= <i>m</i>, 
 * such that {@literal T[s+j] = P[k]}, if such <i>k</i> exists. Thus, we can
 * safely increase <i>s</i> by <i>j-k</i>. The key idea is that if a mismatch
 * occurs at some character in <i>T</i>, and the next occurrence of that
 * character to the left of <i>P</i> is found, then the shift should bring that
 * occurrence in line with the mismatched occurrence in <i>T</i>. On the other
 * hand, if the mismatched character does not occur in <i>P</i>, then the shift
 * can safely move past the entirely of <i>P</i> at the point of mismatch, i.e.,
 * shift by <i>m</i>. The bad-character heuristic allows the pattern to be slide 
 * very far down, thus can make the algorithm runs very fast.
 * <pre>
 *     T =    ................x......
 *     P =          ..x...x...y...
 *                         ^^^
 * </pre>
 * In the above example, suppose a mismatch occurs at position P[j]=<i>y</i>,
 * then all characters marked with ^ will also cause a mismatch <i>x</i> in 
 * <i>T</i>. The bad character heuristic tells us that we should shift <i>P</i>
 * so that right-most occurrence of <i>x</i> in <i>P</i> aligns with character
 * <i>x</i> in <i>T</i>. On the other hand, if a mismatch occurs at a character
 * that is not in <i>P</i>, then we shift pass the entire <i>P</i>, and restart
 * the matching at the last character of <i>P</i>.
 * <pre>
 *            s   mismatch				     s = s+j-last[['x'] => s = s+j-(-1) 
 *            v   v      				            v           => s = s+j+1
 *     T = abaabbaxabacba		-->		T = abaabbaxabacba      => s = s+4
 *     P =    abcaa     				P =        |abcaa
 *                ^      				           |    ^
 *            j = 3      				       new j = m-1 = 4; last char of P
 * </pre>
 * <p>
 * However, the bad character heuristic may sometimes fail and cause the pattern
 * <i>P</i> to slide backward, i.e., negative shift, instead of forward, as
 * illustrated below.
 * <pre>
 *                     mismatch
 *                       v
 *   T =    .............x............
 *   P =          ..x........x....			<--- slide backwards
 *                           ^
 *                 right-most occurrence of mismatched character in P
 * </pre>
 * Since the algorithm only remembers the right-most occurrence of each
 * character in <i>P</i>, the bad character heuristic will cause the pattern to
 * slide backward towards the right, instead of forward towards the left. To fix
 * this bad situation, the "good suffix" heuristic is used. However, the good
 * suffix heuristic in the original Boyer-Moore algorithm is fairly complicated,
 * and difficult to implement. Several simpler alternatives have been proposed.
 * One of them is proposed by Goodrich [4] and is implemented here. Goodrich
 * proposed that if the bad character heuristic proposes a backward shift, i.e.,
 * {@literal j - last[T[s+j]]} becomes negative, then, we shift forward by a
 * single position, i.e.,
 * <pre>
 * 				s <- s + max(1, j - last[T[s+j]])
 * </pre>
 * Similarly, when we find a match, we shift forward by a single position.
 * <pre>
 * 				s <- s + 1
 * </pre>
 * The worst-case running time of the Boyer-Moore algorithm is O((n - m + 1)m + 
 * |radix|). The {@literal compute_last_occurrence_function} takes O(m+|radix|)
 * time, and the algorithm spends O(m) time validating each valid shift <i>s</i>.
 * However, in practice, it often runs faster than the naive algorithm due to
 * its bad character heuristic. Hence, the Boyer-Moore algorithm is often the
 * algorithm of choice in practice.
 * <p>
 * References:<br>
 * [1] Boyer, Robert S.; Moore, J Strother, "A Fast String Searching Algorithm."
 *     Communications of ACM 20(10), Oct 1977, pp. 762–772.<br>
 * [2] Michael T. Goodrich, Roberto Tamassia, "Data Structures and Algorithms in
 *     Java", 5th Edition, Wiley, February 2010.<br>
 * [3] Robert Sedgewick, Kevin Wayne, "Algorithms", 4th Edition, Addison-Wesley,
 *     2011.
 * <p>
 * @see SubstringMatcher
 * @see BoyerMooreHorspool
 * @see KnuthMorrisPratt
 */
public class BoyerMooreSimple {
	private final String pattern;
	private final int radix;
	
	// The last variable holds the lookup table for the last occurrence function
	// The last lookup table is also called the shift table.
	private final int[] last;
	
	// The pos variable holds the offsets in the text where the pattern is found
	private LinkedList<Integer> pos = null;
	private int cur = 0;

	/**
	 * Creates an instance of {@code BoyerMooreSimple} with the given pattern.
	 * The default radix of 256 is assumed.
	 * 
	 * @param pattern pattern for substring matching.
	 */
	public BoyerMooreSimple(String pattern) {
		this.pattern = pattern;
		this.radix = 256;
		
		last = computeLastOccurrence(pattern);
	}
	
	/**
	 * Computes the lookup table for the last occurrence function base on the
	 * bad-character heuristic. Suppose, we have P = tomato.
	 * <pre>
	 *        012345
	 *    P = potato
     * </pre>
     * Then, the last[] function would be:
     * <pre>
     *     last['a'] = 3
     *     last['o'] = 5
     *     last['p'] = 0
     *     last['t'] = 4
     * and for all other characters, we have last[.] = -1.
     * </pre>
	 * @param pattern the pattern string to match.
	 * @return lookup table for last occurrence function.
	 */
	private int[] computeLastOccurrence(String pattern) {
		int[] l = new int[radix];

		// Initialize all last occurrence to -1
		// The -1 will cause pattern P to slide pass a mismatch character c, if
		// c does not occur in the pattern
		for (int a = 0; a < radix; a++) {
			l[a] = -1;
		}

		// Update last[c] with position of character c, since we perform update
		// from left to right, only the LAST position will be retained
		for (int j = 0; j < pattern.length(); j++) {
			l[pattern.charAt(j)] = j;
		}
		return l;
	}

	/**
	 * Searches the given text for the pattern created with this instance of
	 * {@code BoyerMooreSimple} using only the "bad character" heuristic of the
	 * Boyer-Moore algorithm. If pattern is found, the offset to the first
	 * occurrence of pattern in the given text is returned. If pattern is not
	 * found, -1 is returned. 
	 * 
	 * @param text string to search in.
	 * @return offset to the first occurrence of pattern in text if pattern is
	 *   found, otherwise, return -1.
	 */
	public int search(String text) {
		pos = new LinkedList<Integer>();
		cur = 0;

		int n = text.length();
		int m = pattern.length();
		
		int s = 0;
		while (s < (n - m)) {
			int j = m - 1;  // Start at last character of pattern

			// Compares the pattern against the text from right to left
			while (j >= 0 && pattern.charAt(j) == text.charAt(s+j))
				j--;  // Check next ("previous") character of pattern

			if (j <= -1) {
				// Found a match at offset s of text
				// Use Goodrich's proposal and advance s by 1.
				pos.add(s);
				s++;
			} else {
				// Mismatch occurs. Either use the "bad-character heuristic" or
				// Goodrich's proposal to determine shift distance.
				s += Math.max(1, j - last[text.charAt(s+j)]);
			}
		}

		// Return the first position found or -1 if no match is found
		if (pos.size() > cur) return pos.get(cur++);
		return -1;
	}

	/**
	 * Returns true if there are further occurrence of the pattern in the text
	 * in the last call to {@code search} and false otherwise.
	 * 
	 * @return true if there are more occurrence of pattern in text; false
	 *   otherwise.
	 */
	public boolean hasNext() {
		if (pos == null) return false;
		if (cur < pos.size()) return true;
		return false;
	}

	/**
	 * Returns the next offset of the pattern in the text found in the last call
	 * to {@code search}. If no more offset is found, -1 is returned. 
	 * 
	 * @return next offset of pattern in text; -1 otherwise.
	 * @throws NoSuchElementException if pattern is not found in text in the
	 *   last call to {@code search}.
	 */
	public int next() {
		if (pos == null) 
			throw new NoSuchElementException(
								"No match found or search not performed.");

		if (cur < pos.size())
			return pos.get(cur++);
		return -1;
	}

	// for debugging purpose only
	private static void printPattern(int offset, String pat) {
		for (int i = 0; i <= offset; i++) {
			if (i < offset) {
				System.out.print(" ");
				continue;
			}
			System.out.printf("%s\n", pat);
		}
	}

	/**
	 * @param args
	 */
	public static void main(String[] args) {
		String text = new String("abacadabrabracabracadabrabrabracad");
		String pat1 = new String("abacad");
		
		System.out.printf("Text   : %s\n", text);

		BoyerMooreSimple matcher1 = new BoyerMooreSimple(pat1);
		int pos1 = matcher1.search(text);
		if (pos1 >= 0) {
			System.out.printf("Pattern: ");
			printPattern(pos1, pat1);

			while (matcher1.hasNext()) {
				System.out.printf("Pattern: ");
				printPattern(matcher1.next(), pat1);
			}
		} else {
			System.out.println("No match found");
		}
		System.out.println();

		String pat2 = new String("braca");

		System.out.printf("Text   : %s\n", text);
		BoyerMooreSimple matcher2 = new BoyerMooreSimple(pat2);
		int pos2 = matcher2.search(text);
		if (pos2 >= 0) {
			System.out.printf("Pattern: ");
			printPattern(pos2, pat2);
			while (matcher2.hasNext()) {
				System.out.printf("Pattern: ");
				printPattern(matcher2.next(), pat2);
			}
		} else {
			System.out.println("No match found");
		}
		System.out.println();		
	}
}
 