/**
 * The purpose of this code is to illustrate the followings:
 * <ol>
 * <li> implementation of least significant digit (LSD) radix sorts
 * </ol>
 * Radix sort is a non-comparative integer sorting algorithm that sorts data
 * with integer keys by grouping keys by the individual digits which share the
 * same significant position and value. A positional notation is required. 
 * Since integers can represent strings of characters, radix sort is not limited
 * to integers, and is frequently applied to sorting strings.
 * <p>
 * There are two types of radix sorts, namely <b>least significant digit (LSD)
 * </b> radix sort and <b>most significant digit (MSD) radix sort</b>. LSD radix
 * sort processes integer representation starting from the least significant 
 * digit and moves towards the most significant digit. MSD radix sort works the
 * other way round.
 * <p>
 * LSD radix sort algorithm is simple, and is implemented using a stable sorting
 * algorithm, such as counting sort. The pseudo-code for LSD radix sort is as
 * follows.
 * <pre>
 *     lsd_radix_sort(a)
 *         // input: a = set of strings of length m over the alphabet [0..k),
 *         //   assume equal length for simplicity
 *         // output: r in increasing lexicographical order
 *         
 *         // sort strings m times with a stable sort, using each of the 
 *         // positions as the key, proceeding from right to left.
 *         for d = m-1 downto 0
 *             stable_sort(a, d)
 * </pre>
 * The time complexity of the algorithm is O(|a| + k). LSD radix sort is best
 * suited for sorting short strings and integers.
 * <p>
 * References:<br>
 * [1] http://en.wikipedia.org/wiki/Radix_sort<br>
 * [2] Thomas H Cormen, Charles E Leiserson, Ronald L Rivest, Clifford Stein,
 *     "Introduction to Algorithms", Third Edition, McGraw-Hill, July 2011.<br> 
 * [3] Robert Sedgewick, Kevin Wayne, "Algorithms", 4th Edition, Addison-Wesley,
 *     2011.
 * <p>
 * @see CountingSort
 * @see StringQuickSort
 */
public class LSDRadixSort {

	/**
	 * Sorts the given array of {@code String} in increasing lexicographical
	 * order.
	 * 
	 * @param array input array of {@code String} to sort.
	 */
	public static void sort(String[] a) {
		if (a.length <= 1) return;

		int minlen = Integer.MAX_VALUE;
		for (String s : a) {
			if (s.length() < minlen)
				minlen = s.length();
		}

		// Sorts the input string by the characters at position l using 
		// counting sort
		for (int l = minlen - 1; l >= 0; l--)
			CountingSort.sort(a, l);
	}

	/**
	 * Sorts the given array of {@code String} in increasing lexicographical
	 * order on the leading <em>w</em> characters, from right to left.
	 * 
	 * @param array input array of {@code String} to sort.
	 * @throws IllegalArgumentException if <em>w</em> is negative, or equal
	 *   or greater than the length of the shortest input String. 
	 */
	public static void sort(String[] a, int w) {
		if (a.length <= 1) return;

		int minlen = Integer.MAX_VALUE;
		for (String s : a) {
			if (s.length() < minlen)
				minlen = s.length();
		}

		if (w >= minlen)
			throw new IllegalArgumentException(
					"Char position exceeds smallest string with length " +
							minlen);

		// Sorts the input string by the characters at position l using 
		// counting sort
		for (int l = w - 1; l >= 0; l--)
			CountingSort.sort(a, l);
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

		System.out.println("After sorting...");
		sort(a);
		for (String s : a) {
			System.out.printf("%s ", s);
		}
		System.out.println();
	}
}
