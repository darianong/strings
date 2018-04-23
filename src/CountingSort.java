/**
 * The purpose of this code is to illustrate the followings:
 * <ol>
 * <li> implementation of counting sort.
 * </ol>
 * Counting sort is an algorithm for sorting a collection of items, each of
 * which is associated with a non-negative integer key whose maximum value is at
 * most <em>k</em>, where in most applications <em>k</em> is usually small. It
 * operates by counting the number of items that have distinct key values, and
 * using arithmetic on those counts to determine the positions of each key value
 * in the output sequence. Unlike sorting algorithms such as Quick Sort, Merge
 * Sort, Insertion Sort, Heap Sort, counting sort is a non-comparative sort,
 * meaning that it does not compare elements to each other. Instead, counting
 * sort uses the key values of the items as indexes into an array to sort the
 * items. Hence the Omega(n log n) lower bound for comparison-based sort does
 * not apply to counting sort. Counting sort is also known as keyed-index sort,
 * or keyed-indexed counting [3].
 * <p>
 * The algorithm works by looping over the items, and using their key value to
 * create a histogram of the number of times each key value occurs. It then
 * performs a prefix sum computation to determine, for each key, the starting
 * position in the output array of the items having that key. Finally, it loops
 * over the items again, moving each item into its sorted position in the
 * output array. The pseudo-code for counting sort is as follows.
 * <pre>
 *     counting_sort(a, k)
 *         let count[0..k] be a new array
 *         
 *         // Initialize histogram; takes time Theta(k)
 *         for i = 0 to k-1
 *             c[i] = 0
 *
 *         // Create histogram; takes time Theta(n)
 *         for j = 1 to a.length
 *             count[a[j]] = count[a[j]] + 1
 *
 *         // Compute cumulative sums; takes time Theta(k)
 *         sum = 0
 *         for i = 0 to k-1
 *             tmp = count[i]
 *             count[i] = sum
 *             sum = sum + tmp
 *
 *         // Distribute items into their sorted position; takes time Theta(n)
 *         for j = 1 to a.length
 *             aux[count[a[j]]] = a[j]
 *             count[a[j]] = count[a[j]] + 1
 * </pre>
 * <p>
 * The time complexity of counting sort depends on the range of possible key
 * values of the input items. For keys in the range [0..k) and <em>n</em> items
 * to sort, the time complexity is O(n + k). In practice, counting sort is often
 * used when we have <em>k = O(n)</em>, in which case the running time of
 * counting sort is Theta(n), beating the lower bound of Omega(n log n) for
 * comparison based sorts.
 * <p>
 * Counting sort is a stable sorting algorithm, i.e., the relative order of 
 * equal elements stays the same. Counting sort is often used as a subroutine in
 * another sorting algorithm, such as radix sort, which can handle larger keys
 * more efficiently. 
 * <p>
 * References:<br>
 * [1] http://en.wikipedia.org/wiki/Counting_sort<br>
 * [2] Thomas H Cormen, Charles E Leiserson, Ronald L Rivest, Clifford Stein,
 *     "Introduction to Algorithms", Third Edition, McGraw-Hill, July 2011.<br> 
 * [3] Robert Sedgewick, Kevin Wayne, "Algorithms", 4th Edition, Addison-Wesley,
 *     2011.
 * <p>
 * @see LSDRadixSort
 */
public class CountingSort {

	/**
	 * Sorts the given array of {@code String} according to the lexicographical
	 * order of the character at the given position <em>pos</em>.
	 * 
	 * @param array input array to sort.
	 * @throws IllegalArgumentException if <em>pos</em> is negative, or equal
	 *   or greater than the length of the shortest input String.
	 */
	public static void sort(String[] a, int pos) {
		if (a.length <= 1) return;

		if (pos < 0)
			throw new IllegalArgumentException(
				"Invalid char position specified; value must be 0 or greater.");
		
		int minlen = Integer.MAX_VALUE;
		for (String s : a) {
			if (s.length() < minlen)
				minlen = s.length();
		}

		if (pos >= minlen)
			throw new IllegalArgumentException(
					"Char position exceeds smallest string with length " +
							minlen);

		int n = a.length;
		int radix = 256;  // 256 for ASCII, 65,536 for UNICODE
		int[] count = new int[radix];
		for (int r = 0; r < radix; r++)
			count[r] = 0;

		// Step 1: create histogram for key frequencies
		for (int i = 0; i < n; i++)
			count[a[i].charAt(pos)]++;

		// Step 2: perform prefix sums
		int sum = 0;
		for (int r = 0; r < radix; r++) {
			int tmp = count[r];
			count[r] = sum;
			sum += tmp;
		}
		
		// Step 3: distribute records to auxiliary output array; preserving
		// order of inputs with equal keys
		String[] aux = new String[n];
		for (int i = 0; i < n; i++) {
			aux[count[a[i].charAt(pos)]++] = a[i];
		}

		// Step 4: copy sorted records back to input array
		for (int i = 0; i < n; i++) {
			a[i] = aux[i];
		}
	}

	/**
	 * @param args
	 */
	public static void main(String[] args) {
		String[] a = {"cat", "him", "ham", "bat"};
		System.out.println("Before sorting...");
		for (String s : a) {
			System.out.printf("%s ", s);
		}
		System.out.println();

		for (int pos = 0; pos < 3; pos++) {
			System.out.printf("After sorting at position %d...\n", pos);
			sort(a, pos);
			for (String s : a) {
				System.out.printf("%s ", s);
			}
			System.out.println();
		}
	}
}
