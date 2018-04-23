import java.util.Random;

/**
 * The purpose of this code is to illustrate the followings:
 * <ol>
 * <li> implementation of the string quicksort (multikey quicksort) algorithm
 * </ol>
 * Let A[0..<em>m</em>) and B[0..<em>n</em>) be two strings on an ordered
 * alphabet <em>E</em>. We say that <em>A</em> is lexicographically smaller than
 * <em>B</em>, denoted <em>A < B</em>, if an only if either
 * <ul>
 * <li> <em>m < n</em> and A = B[0..<em>n</em>), i.e., A is a proper prefix of
 *   B, or
 * <li> A[0..<em>i</em>) = B[0..<em>i</em>) and <em>A[i] < B[i]</em> for some
 *   <em>i</em> in [0..min{<em>m,n</em>}).
 * </ul>
 * Determining the order of <em>A</em> and <em>B</em> needs 
 * Theta(min{<em>m,n</em>}) symbol comparisons in the worst case. 
 * <p>
 * String Quicksort is a variant of quicksort and is specialized for sorting 
 * strings [1]. String quicksort is also known as multikey quicksort and 3-way
 * string quicksort [1,2]. String quicksort uses a ternary partitioning
 * algorithm that partitions the input into three parts instead of the usual
 * two parts, and then recursively sorts the three parts. The pseudo-code for
 * string quicksort is as follows.
 * <pre>
 *   Let R be the set of Strings.
 *   Let d be the length of their common prefix.
 *   
 *   StringQuickSort(R, d)
 *       if |R| <= 1 return R
 *       select pivot X from R
 *       R<sub><</sub> <- {S in R | S[d] < X[d]}
 *       R<sub>=</sub> <- {S in R | S[d] = X[d]}
 *       R<sub>></sub> <- {S in R | S[d] > X[d]}
 *       
 *       StringQuickSort(R<sub><</sub>, d)
 *       StringQuickSort(R<sub>=</sub>, d+1)
 *       StringQuickSort(R<sub>></sub>, d)
 *       return R<sub><</sub>.R<sub>=</sub>.R<sub>></sub>
 * </pre>
 * In the initial call, <em>d</em> = 0. The partitioning value can be chosen in
 * many ways, such as using a random pivot selection rule or median-of-three
 * pivot selection rule. The expected number of comparisons required by the
 * string quicksort algorithm is given in Bentley and Sedgewick's paper [1].  
 * <p>
 * The implementation in this class uses a random pivot selection rule. It can
 * easily be modified to use other pivot selection rule, such as median-of-three.
 * In a practice implementation, the algorithm can cut-off to use insertion sort
 * if the size of the input array is small. The insertion sort would then be a
 * modified version to take into account the length of the prefix <em>d</em> 
 * that is assumed to be sorted, so that comparison would start at <em>d</em>+1. 
 * <p>
 * References:<br>
 * [1] Jon L. Bentley, Robert Sedgewick, "Fast algorithms for sorting and
 *     searching strings," In Proceedings of the ACM-SIAM Symposium on Discrete
 *     Algorithms, pp. 360-369, 1997<br>
 * [2] Robert Sedgewick, Kevin Wayne, "Algorithms", 4th Edition, Addison-Wesley,
 *     2011.
 * <p>
 * @see LSDRadixSort
 */
public class StringQuickSort {
	private static final Random randomGen = new Random();

	/**
	 * Sorts the given array of {@code String} in increasing lexicographical
	 * order.
	 * 
	 * @param array input array of {@code String} to sort.
	 */
	public static void sort(String[] array) {
		if (array.length <= 1) 
			return;

		strQuicksort(array, 0, array.length-1, 0);
	}
	
	private static void strQuicksort(String[] a, int left, int right, int d) {
		// Base case: 1 element subarray
		if ((right - left + 1) <= 1) return;
		
		// Choose a random pivot and move it to the left.
		// Then, partition around the pivot.
		int p = chooseRandomPivot(left, right);
		swap(a, left, p);
		int[] partition = partition(a, left, right, d);	

		// Quick sort the 3 partitions:
		// a[left..p[0]-1], a[p[0]..p[1]], a[p[1]+1..right]
		strQuicksort(a, left, partition[0]-1, d);

		if (!isEndOfString(a[partition[0]], d))
			strQuicksort(a, partition[0], partition[1], d+1);

		strQuicksort(a, partition[1]+1, right, d);
	}
	
	/**
     * Returns the {@code char} value at the specified index <em>d</em> for the
     * given string {@code str}. An index ranges from {@code 0} to
     * {@code length() - 1}, where {@code length()} is the length of the string
     * {@code str}. If <em>d</em> is outside the index range, the value -1 is
     * returned.
	 * 
	 * @param str string.
	 * @param d position of string.
	 * @return the {@code char} value in integer at the specified index of the
	 *   given string.
	 */
	private static int charAt(String str, int d) {
		if (d < 0 || d >= str.length()) return -1;
		return str.charAt(d);
	}
	
	/**
	 * Returns true if position <em>d</em> is off the end of the given string,
	 * and false otherwise.
	 * 
	 * @param str string.
	 * @param d position of string.
	 * @return true if position <em>d</em> is off the end of the string.
	 */
	private static boolean isEndOfString(String str, int d) {
		return (charAt(str, d) < 0);
	}

	/**
	 * Generates a random pivot position between the index position
	 * {@code left} and {@code right} and return the generated position.
	 * 
	 * @param left start index position.
	 * @param right end index position.
	 * @return randomly chosen pivot position.
	 */
	private static int chooseRandomPivot(int left, int right) {
		int r = randomGen.nextInt(right - left);
		return left + r;
	}

    /**
     * Partitions the input set of {@code String} between positions 
     * {@code a[left]} and {@code a[right]} based on the lexicographical order
     * of the <em>d</em>th character of the strings. The string to be used as
     * the pivot is assumed to be at position <em>left</em>.
     * <p>
     * precondition: pivot at position <em>left</em>.<br>
     * postcondition: strings with equal <em>d</em>th character as the pivot are
     *   placed next to each other. 
     * 
     * @param str the input array.
     * @param left start index of input array to work on.
     * @param right end index of input array to work on.
     * @param d the <em>d</em>th character of the string to determine the
     *  partition.
     * @return the range that contains strings with equal <em>d</em>th character
     *   as the pivot
     */
	private static int[] partition(String[] str, int left, int right, int d) {
		int pivot = charAt(str[left], d);

		// Set initial range for partition; we then scan from left+1 to right
    	// and moving the pivot to its rightful position, and also all items
    	// with values equal to the pivot together as well, maintaining the
    	// invariant:
    	//     a[left..i-1] < pivot < a[k+1..right]
    	// where the pivot and items with the same value as the pivot are 
    	// between the pointers i and k.		
		int i = left + 1;
		int k = right;
		for (int j = left + 1; j <= k; ) {
			int cmp = charAt(str[j], d) - pivot;
			if (cmp < 0) {
				swap(str, i++, j);
			}
			else if (cmp > 0) {
				swap(str, j, k--);
				continue;
			}
			j++;
		}
		swap(str, left, i - 1);

		int[] result = new int[2];
		result[0] = i - 1;
		result[1] = k;
		return result;
	}

    /**
     * Swaps the elements in the positions {@code x} and {@code y} in the given
     * array {@code str}.
     * <p>
     * precondition: x and y are within the boundary of input array<br>
     * postcondition: positions x and y in input array swapped
     * 
     * @param str the input array.
     * @param x first position.
     * @param y second position.
     */
    private static void swap(String[] str, int x, int y) {
    	String temp = str[x];
    	str[x] = str[y];
    	str[y] = temp;
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
