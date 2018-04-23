/**
 * The purpose of this code is to illustrate the followings:
 * <ol>
 * <li> introduction to the substring exact matching problem.
 * <li> implementation of a naive substring matcher to illustrate string
 *  traversal.
 * </ol>
 * The <b>substring (exact) string matching problem</b> is as follows. Given a
 * text string <i>T</i> of length <i>n</i> and a pattern string <i>P</i> of 
 * length <i>m</i>, find an occurrence of the pattern within the text, i.e.,
 * determine if <i>P</i> is a substring of <i>T</i>.
 * <p>
 * We say that <i>P</i> occurs in <i>T</i> with shift <i>s</i> if {@literal 
 * P[1..m] = T[s+1..s+m]}. A simple algorithm simply considers all possible
 * shifts:
 * <pre> 
 *     naive-matcher(T, P)
 *         n + length[T]
 *         m = length[P]
 *         for s = 0 to n-m
 *           if P[1..m] == T[s+1..s+m]  // test if shift s is valid
 *             then return s  // pattern occurs at shift s
 * </pre>
 * The algorithm uses a loop to checks the condition {@literal P[1..m] =
 * T[s+1..s+m]} for each of the <i>n-m+1</i> possible values of <i>s</i>. The
 * <i>if</i> test involves an implicit loop to check each of the <i>m</i>
 * characters in the pattern for each shift <i>s</i>. The algorithm takes 
 * O((n-m+1)m) in the worst case, or O(nm) when <i>n</i> >> <i>m</i>.
 * <p>
 * The naive string-matcher is inefficient because information gained about the
 * text for one value of <i>s</i> is totally ignored in considering other values
 * of <i>s</i>. For more efficient substring matching algorithms, see the 
 * Boyer-Moore algorithm, Horspool's simplification to Boyer-Moore algorithm, 
 * and the Knuth-Morris-Pratt algorithm.
 * <p>
 * References:<br>
 * [1] Thomas H Cormen, Charles E Leiserson, Ronald L Rivest, Clifford Stein,
 *     "Introduction to Algorithms", Third Edition, McGraw-Hill, July 2011. 
 * <p>
 * @see BoyerMooreSimple
 * @see BoyerMooreHorspool
 * @see KnuthMorrisPratt
 */
public class SubstringMatcher {

	/**
	 * Searches for the given pattern in the given text. Returns the first 
	 * offset in the text where the pattern is found. If the pattern is not
	 * found in the text, {@literal -1} is returned.
	 * 
	 * @param text string to search in.
	 * @param pattern string to search for in text.
	 * @return offset/shift in text where pattern is found; -1 if pattern is not
	 *    found.
	 */
	public static int search(String text, String pattern) {
		int n = text.length();
		int m = pattern.length();
		
		// a simple optimization
		if (m > n) return -1;
		
		for (int s = 0; s < (n-m); s++) {
			int j = 0;
			while (j < m) {
				if (pattern.charAt(j) != text.charAt(s+j))
					break;
				j++;
			}
			if (j == m) return s;  // valid shift/offset found
		}
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
		int pos1 = SubstringMatcher.search(text, pat1);
		if (pos1 >= 0) {
			System.out.printf("Pattern: ");			
			printPattern(pos1, pat1);
		} else {
			System.out.println("No match found");
		}
		System.out.println();
		
		String pat2 = new String("braca");
		
		System.out.printf("Text   : %s\n", text);
		int pos2 = SubstringMatcher.search(text, pat2);
		if (pos2 >= 0) {
			System.out.printf("Pattern: ");			
			printPattern(pos2, pat2);
		} else {
			System.out.println("No match found");
		}
		System.out.println();
	}

}
