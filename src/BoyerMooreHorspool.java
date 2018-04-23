import java.util.LinkedList;
import java.util.NoSuchElementException;

/**
 * The purpose of this code is to illustrate the followings:
 * <ol>
 * <li> implementation of Horspool's simplification to the original Boyer-Moore
 *  algorithm.
 * </ol>
 * The problem of exact matching or substring matching has been discussed in
 * {@link SubstringMatcher} and {@link BoyerMooreSimple}. The Boyer-Moore's
 * algorithm is discussed in {@link BoyerMooreSimple}. It was mentioned that
 * there are simplification to the good suffix heuristic in the original
 * Boyer-Moore algorithm, and one of them is Horspool's simplification.
 * <p>
 * In the original Boyer-Moore algorithm, whenever a mismatch is found, the
 * mismatched (bad) character is used to determine the shift distance. This
 * aligns the right most occurrence of the mismatch character in <i>P</i> under
 * <i>T</i>. 
 * <pre>
 *               s  mismatch       		   		             s
 *               v    v           		   		             v
 *     T = ...aabbab88c012acb00b...		-->		T = ...aabbab88c012acb00b...
 *     P =       dac2ba012         		-->		P =          dac2ba012
 *                                 		   		               ^
 *                                 		   align at right most occurrence of 'c'
 * </pre>
 * Horspool's proposal is that if there is a mismatch, any one of the characters
 * in the <i>suffix</i> can be used to perform the shift, not just the 
 * mismatched character. In fact, the last character of the matched suffix can
 * be used to perform the shift.
 * <pre>
 *                 mismatch
 *               s    | last char  		                       s
 *               v    v  v         		                       v
 *     T = ...aabbab88c012acb00b...		-->     T = ...aabbab88c012acb00b...
 *     P =       dac2ba012         		-->     P =            dac2ba012
 *                                 		                          ^
 *                                 		      align right most occurrence of '2'
 *                                 		         (excluding the last char of P)
 * </pre>
 * Horspool's observation is that while the good suffix heuristic in Boyer-Moore
 * algorithm is optimized to handle repetitive patterns so as to avoid a worst
 * case running time of O(nm), repetitive patterns are not too common in
 * practice and hence, it is not worth the effort to implement the good suffix
 * heuristic. Instead, a simplified approach could be taken to replace the
 * lookup table required by the good suffix heuristic. 
 * <p>
 * Horspool's algorithm requires a few simple modifications to the Boyer-Moore
 * algorithm. Firstly, during the pre-processing stage to create the last
 * occurrence function, the last character of the pattern is ignored from the
 * computation. If the same character appears earlier in the pattern, then that
 * position will be used.
 * <p>
 * Secondly, the shift distance calculation is replaced with:<br>
 * <pre>
 * 			s = s + (m-1) + last[T[s+(m-1)]]
 * </pre>
 * In the case of a suffix match, the last character of the pattern 
 * {@literal P[m-1]} is aligned with the character in the text position
 * {@literal T[s+(m-1)]}. We use this last character to find the shift distance.
 * Since the last character is used to compute the shift distance, instead of
 * {@literal j - last[T[s+j]]} as in the Boyer-Moore algorithm, we have 
 * {@literal (m-1) - T[s+(m-1)]}, since the last character occurs at {@literal 
 * j = m-1}.
 * <p> 
 * References:<br>
 * [1] R.N. Horspool, "Practical Fast Searching in Strings", Software - Practice
 *     and Experience 10, 1980, pp. 501-506.<br>
 * [2] Boyer, Robert S.; Moore, J Strother, "A Fast String Searching Algorithm."
 *     Communications of ACM 20(10), Oct 1977, pp. 762–772.<br>
 * [3] http://www.iti.fh-flensburg.de/lang/algorithmen/pattern/horsen.htm
 * <p>
 * @see BoyerMooreSimple
 */
public class BoyerMooreHorspool {
	private final String pattern;
	private final int radix;
	
	// The last variable holds the lookup table for the last occurrence function
	// The last lookup table is also called the shift table.
	private final int[] last;
	
	private LinkedList<Integer> pos = null;
	private int cur = 0;

	/**
	 * Creates an instance of {@code BoyerMooreHorspool} with the given pattern.
	 * The default radix of 256 is assumed.
	 * 
	 * @param pattern pattern for substring matching.
	 */
	public BoyerMooreHorspool(String pattern) {
		this.pattern = pattern;
		this.radix = 256;
		
		last = computeLastOccurrence(pattern);
	}
	
	/**
	 * Computes the lookup table for the last occurrence function base on the
	 * bad-character heuristic. Suppose, we have P = tomato. Then
	 * <pre>
	 *        012345                  
	 *    P = potato
     * </pre>
     * Then, we have last[] function:
     *   last['a'] = 3
     *   last['o'] = 1    // last character in P ignored
     *   last['p'] = 0
     *   last['t'] = 4
     * and for all other characters, we have last[.] = -1.
	 * 
	 * @param pattern the pattern string to match
	 * @return lookup table for last occurrence function.
	 */
	private int[] computeLastOccurrence(String pattern) {
		int[] l = new int[radix];

		// Initialize all last occurrence to -1
		// The -1 will cause pattern P to slide pass the character c, if c does
		// not occur in the pattern
		for (int a = 0; a < radix; a++) {
			l[a] = -1;
		}

		// Update last[c] with position of character c but omit the last 
		// character of the pattern for Horspool's simplification.
		for (int j = 0; j < pattern.length() - 1; j++) {
			l[pattern.charAt(j)] = j;
		}
		return l;
	}

	/**
	 * Searches the given text for the pattern created with this instance of
	 * {@code BoyerMooreHorspool} using the "bad character" heuristic of the
	 * Boyer-Moore algorithm and Horspool's "good suffix" simplification. If
	 * pattern is found, the offset to the first occurrence of pattern in the
	 * given text is returned. If pattern is not found, -1 is returned. 
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
				pos.add(s);
				s++;
			} else {
				// Mismatch occurs. Use Horspool's simplification; i.e., use
				// the last character of the matched suffix to determine shift
				// distance.
				s += (m-1) - last[text.charAt(s+(m-1))];
			}
		}

		// Return the first position found or -1 if no match found
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
		System.out.printf("Pattern: ");

		BoyerMooreHorspool matcher1 = new BoyerMooreHorspool(pat1);
		int pos1 = matcher1.search(text);
		if (pos1 >= 0) {
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
		System.out.printf("Pattern: ");
		BoyerMooreHorspool matcher2 = new BoyerMooreHorspool(pat2);
		int pos2 = matcher2.search(text);
		if (pos2 >= 0) {
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
 