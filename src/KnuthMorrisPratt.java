import java.util.LinkedList;
import java.util.NoSuchElementException;

/**
 * The purpose of this code is to illustrate the followings:
 * <ol>
 * <li> implementation of the Knuth-Morris-Pratt algorithm for substring 
 *  matching.
 * </ol> 
 * The Knuth-Morris-Pratt (KMP) algorithm was invented by Donald E. Knuth, James
 * H. Morris and Vaughan R. Pratt in 1977 [1]. Like the naive string algorithm
 * (see {@link SubstringMatcher}) the KMP algorithm considers shifts in order
 * from 1 to <i>n-m</i>. Like the Boyer-Moore algorithm, it uses knowledge about
 * the pattern and partial matches of the pattern and the text to skip over
 * shifts that are guaranteed not to result in a valid match.
 * <p>
 * For example, consider a particular shift <i>s</i> that is matching the
 * pattern <i>P</i> = {@literal ababaca} against a text <i>T</i>. <i>q</i>=5 of
 * the characters have matched successfully, but the 6th pattern character fails
 * to match the corresponding text character <i>a</i>. Knowing that these 
 * <i>q</i> characters have successful match allows us to determine immediately
 * that certain shifts are invalid. For example, the shift <i>s+1</i> would
 * bring the first character of the pattern <i>a</i> under the text character
 * <i>b</i> which is known to match with the second character in the pattern.
 * Thus, this shift would not be valid, since the first two characters of the
 * pattern are not the same. The shift <i>s+2</i>, however, would align the
 * first three pattern characters with the three text characters that must 
 * necessarily match.
 * <pre>
 *             s  mismatch				        s=s+2 
 *             v    v      		s+2		          v
 *     T = bacbababaabcbab		-->		T = bacbababaabcbab
 *     P =     ababaca     				P =       ababaca
 *             <--->           			          ^^^
 *              q=5            			          k=3  
 * </pre>
 * In general, given that the pattern <i>P[1..q]</i> matches the text at
 * <i>T[s+1..s+q]</i>, it is useful to know the least shift <i>s' > s</i> such
 * that
 * <pre>
 * 		P[1..k] = T[s'+1..s'+k]		where s'+ k = s + q.	----(1)
 * </pre>
 * In general, equation (1) says that at the new shift <i>s'</i> we have a shift
 * such that the first <i>k</i> characters of <i>P</i> is guaranteed to match
 * the corresponding characters of <i>T</i>. In the above example, we can deduce
 * that a shift of <i>s'</i>=<i>s</i>+2 could potentially be a valid shift. So,
 * given that <i>q</i> characters have matched successfully at shift <i>s</i>,
 * the next potentially valid shift is at {@literal s' = s + (q - pi[q])}.  
 * <p>
 * The useful information for such deductions can be precomputed by comparing 
 * the pattern with itself and stored in an auxiliary array {@literal pi} that
 * is defined as follows.<br>
 * <br>
 * pi[q] is the largest integer <i>k</i> smaller than <i>q</i> such that
 * P<sub>1</sub>...P<sub>pi[q]</sub> is a suffix of P<sub>1</sub>...
 * P<sub>q</sub>
 * <br>For example, consider the pattern <i>P</i> = {@literal ababababca}.
 * <pre>
 *    q 	1  2  3  4  5  6  7  8  9  10
 *   Pq 	a  b  a  b  a  b  a  b  c   a
 * pi[q]	0  0  1  2  3  4  5  6  0   1
 * </pre>
 * pi[2] = 0 since no prefix of length 1 (P<sub>1</sub> = a) and ends with 
 * {@literal b}<br>
 * pi[4] = 2 since {@literal ab} is a suffix of {@literal abab}<br>
 * pi[5] = 3 since {@literal aba} is a suffix of {@literal ababa}<br>
 * pi[6] = 4 since {@literal abab} is a suffix of {@literal ababab}<br>
 * pi[8] = 6 since {@literal ababab} is a suffix of {@literal abababab}<br>
 * pi[9] = 0 since no prefix of length <= 8 ends with {@literal c}<br>
 * pi[10] = 1 since {@literal a} is a suffix of {@literal ababababca}<br>
 * pi[1] = 0 by definition. Note that the index of <i>pi</i> starts at 1. 
 * <p>
 * The required function {@literal pi[q]} can be formalized as follows. Given a
 * pattern <i>P[1..m]</i>, the <b><i>pi function</i></b> for the pattern
 * <i>P</i> is the function pi : {1, 2, ..., m} -> {0, 1, ..., m-1} such that
 * <pre>
 *	pi[q] = max{ k : k < q and P<sub>k</sub> is a suffix of P<sub>q</sub> } 
 * </pre>
 * That is, {@literal pi[q]} is the length of the longest prefix of <i>P</i>
 * that is a proper suffix of P<sub>q</sub>.
 * <p>
 * Given the {@literal pi} function, the pseudo-code for the Knuth-Morris-Pratt
 * algorithm is as follows:
 * <pre>
 * 	knuth_morris_pratt(T, P)
 * 	    n = length[T]
 * 	    m = length[P]
 *	    pi = compute_pi_function(P)
 *	    q = 0                              // number of characters matched
 *	    for i = 1 to n                     // scan the text from left to right
 *	        while q > 0 and P[q+1] != T[i]
 *	            q = pi[q]                  // next character does not match
 *	        if P[q+1] == T[i]
 *	            q = q + 1                  // next character matches
 *	        if q == m                      // is all of P matched?
 *	            // pattern occurs with shift i - m
 *	            q = pi[q]                   // look for next match
 * </pre>
 * The pseudo-code for the {@literal compute_pi_function} is as follows.
 * <pre>
 * 	compute_pi_function(P)
 * 	    m = length[p]
 * 	    let pi[1..m] be a new array
 * 	    pi[1] = 0
 * 	    k = 0
 * 	    for q = 2 to m
 * 	        while k > 0 and P[k+1] != P[q]
 * 	            k = pi[k]
 *	        if P[k+1] == P[q]
 *	            k = k + 1
 *	        pi[q] = k
 *	    return pi
 * </pre>
 * By using aggregate method of amortized analysis [2], it can be shown that the
 * running time of the {@literal compute_pi_function} function is O(m) and the
 * running time of the {@literal knuth_morris_pratt} function is O(n), giving a
 * total running time of O(n+m).
 * <p>
 * The Knuth-Morris-Pratt algorithm never needs to move backwards in the input
 * text. This makes it good for processing very large files.
 * <p>
 * References:<br>
 * [1] Donald E. Knuth, James H. Morris, Vaughan R. Pratt, "Fast Pattern
 *     Matching in Strings.", SIAM Journal on Computing, 6(2), 1977,
 *     pp. 323-350.<br>
 * [2] Thomas H Cormen, Charles E Leiserson, Ronald L Rivest, Clifford Stein,
 *     "Introduction to Algorithms", Third Edition, McGraw-Hill, July 2011.
 * <p>
 * @see SubstringMatcher
 * @see BoyerMooreSimple
 */
public class KnuthMorrisPratt {
	private final String pattern;

	private final int[] pi;  	// the precomputed pi function
	
	// The pos variable holds the offsets in the text where the pattern is found
	private LinkedList<Integer> pos = null;
	private int cur = 0;  // index into pos for next() call
	
	/**
	 * Creates an instance of {@code KnuthMorrisPratt} for the given pattern.
	 * 
	 * @param pattern the pattern to match.
	 */
	public KnuthMorrisPratt(String pattern) {
		this.pattern = pattern;
		pi = computePiFunction(pattern);
	}
	
	/**
	 * Computes the {@literal pi} function for the Knuth-Morris-Pratt algorithm.
	 * The code for the {@literal compute_pi_function} can be simplified by
	 * starting at {@code k = -1} and {@code q = 1}. Then, the assignment
	 * {@literal pi[1] <- 0} can be handled inside the for loop, and we can also
	 * do away with the <i>if</i> statement. Also note that String in Java
	 * starts at index 0, rather than 1.
	 * 
	 * @param pattern the pattern to compute the {@literal pi} function
	 * @return array containing the {@literal pi} function
	 */
	private int[] computePiFunction(String pattern) {
		int m = pattern.length();
		// The index for the pi function starts at 1, but we include a 0 index
		// for handling the case where 0 character match.
		int[] p = new int[m+1];
		
		// For convenient handling of the case where 0 char match.
		// See search operation below for use of pi[0].
		p[0] = -1;
		int k = -1;
		for (int q = 1; q <= m; q++) {
			while (k >= 0 && pattern.charAt(k) != pattern.charAt(q-1))
				k = p[k];
			p[q] = ++k;
		}
		return p;
	}

	/**
	 * Searches the given text for the pattern created with this instance of
	 * {@code KnuthMorrisPratt} using the Knuth-Morris-Pratt algorithm. If
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

		int m = pattern.length();
		int n = text.length();
		
		/**
		 * Compare the strings from left to right, just like in the naive
		 * algorithm. Whenever we have a mismatch, we use the pi table to
		 * determine the new shift. If we have a match of q=0 character, then
		 * the q++ will set q=0 and the for loop will advance i to the next
		 * position for a new matching. If we have a partial successful match,
		 * i.e., q < m, then we just advance q by 1 to compare the next 
		 * character in the pattern. If we have a full match of q==m characters,
		 * then we have a full successful match at shift i-m.
		 */
		int q = 0;
		for (int i = 1; i <= n; i++) {
			while (q >= 0 && pattern.charAt(q) != text.charAt(i-1))
				q = pi[q];

			q++;
			if (q == m) {
				pos.add(i-m);
				// Use the longest prefix that is a proper suffix of Pm to
				// start our next matching, if such prefix is available, 
				// otherwise q would be 0.
				q = pi[q];  
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
		String text1 = new String("00ababababca1234");
		System.out.printf("Text   : %s\n", text1);

		String pat1 = new String("ababababca");		
		KnuthMorrisPratt kmp1 = new KnuthMorrisPratt(pat1);		
		int pos1 = kmp1.search(text1);
		if (pos1 >= 0) {
			System.out.printf("Pattern: ");
			printPattern(pos1, pat1);

			while (kmp1.hasNext()) {
				System.out.printf("Pattern: ");
				printPattern(kmp1.next(), pat1);
			}
		} else {
			System.out.println("No match found");
		}
		System.out.println();

		String text2 = new String("abacadabrabracabracadabrabrabracad");
		System.out.printf("Text   : %s\n", text2);

		String pat2 = new String("braca");
		KnuthMorrisPratt kmp2 = new KnuthMorrisPratt(pat2);
		int pos2 = kmp2.search(text2);
		if (pos2 >= 0) {
			System.out.printf("Pattern: ");
			printPattern(pos2, pat2);

			while (kmp2.hasNext()) {
				System.out.printf("Pattern: ");
				printPattern(kmp2.next(), pat2);
			}
		} else {
			System.out.println("No match found");
		}
		System.out.println();		
	}
}
