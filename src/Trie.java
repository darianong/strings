import java.util.LinkedList;
import java.util.Queue;

/**
 * The purpose of this code is to illustrate the followings:
 * <ol>
 * <li> implementation of a trie.
 * </ol>
 * An <b><i>alphabet</i></b> <em>E</em> is the set of <b><i>symbols</i></b> or 
 * <b><i>characters</i></b> that may occur in a string. A <b><i>string</i></b>
 * is a <b><i>sequence</i></b> of symbols drawn from the alphabet set <em>E</em>.
 * There are various types of alphabets:<br>
 * <b>ordered alphabet</b>: <i>E = {c<sub>1</sub>, c<sub>2</sub>,..., 
 * c<sub>|E|</sub>}</i>, where c<sub>1</sub> < c<sub>2</sub> < c<sub>|E|</sub>.
 * </i><br>
 * <b>integer alphabet</b>: <i>E = {0, 1, 2, ..., |E|-1}</i>. 
 * <p>
 * A trie is a simple but powerful data structure for storing a set of strings.
 * It is a rooted tree with the following properties:
 * <ul>
 * <li> Edges are labeled with symbols from an alphabet <em>E</em>
 * <li> For every node <em>v</em>, the edges from <em>v</em> to its children
 * have different labels.
 * </ul>
 * Each node represents the string obtained by concatenating the symbols on the
 * path from the root to that node. A trie is a complete representation of the
 * strings it store. There is no need to store the strings separately. Since a
 * trie is a rooted tree, depth-first search can be used to traverse the trie.
 * <p>
 * The time and space complexity of a trie depends on the implementation of the
 * <em>child function</em>:<br>
 * For a node <em>v</em> and a symbol <em>c in E</em>, <em>child(v,c)</em> is
 * <em>u</em> if <em>u</em> is a child of <em>v</em> and the edge <em>(v,u)</em>
 * is labeled with <em>c</em>, and <em>child(v,c)</em> = null if <em>v</em> has
 * no such child.
 * <p>
 * The pseudo-code for inserting the string <em>S[0..m)</em> into the trie
 * <em>R</em> is as follows.
 * <pre>
 *     insert_trie(R, S)
 *         v = root
 *         j = 0
 *         while child(v, S[j]) != null do
 *             v = child(v, S[j])
 *             j++
 *         while j < m do    // create nodes for each symbol on S
 *             u = new node
 *             child(v, S[j]) = u
 *             v = u
 *             j++
 *         mark v as representing the string S
 * </pre>
 * There are different implementation options for the child function,
 * including:<br>
 * <b>Array</b>: Each node stores an array of size |E|. The space complexity is
 * O(|E|n), where n is the number of nodes in the trie. The time complexity of
 * the child operation is O(1). This implementation requires an integer
 * alphabet.
 * <br>
 * <b>Binary tree</b>: A binary tree is used instead of an array. The space
 * complexity is O(n), and the time complexity is O(log|E|). This implementation
 * requires an ordered alphabet.
 * <br>
 * <b>Hash table</b>: The values child(v,c) != null are sorted on a hash table.
 * The space complexity is O(n), and the time complexity of the child function
 * is O(1). This implementation requires an integer alphabet.
 * <p>
 * The implementation here uses the array as the child function implementation.
 * <p>
 * References:<br>
 * [1] Juha Karkkainen, "58093: String Processing Algorithms," lecture notes,
 *     Autumn 2013<br>
 * [2] Robert Sedgewick, Kevin Wayne, "Algorithms", 4th Edition, Addison-Wesley,
 *     2011.
 * <p>
 * @param <E> the type of the values to be associated with the keywords stored
 *   on the trie.
 */
public class Trie<E> {
	private final static int DEFAULT_RADIX = 256;  // extended ASCII
    private final int radix;  // the radix used for initializing this trie

	private Node root = null;  // root node of the trie
	private int count = 0;    // number of keywords on the trie

	/**
	 * The {@code Trie.Node} inner class encapsulates the implementation of an
	 * R-way trie node.
	 */
	private class Node {
		private E value = null;
		private Node[] child;
		private int nChildren = 0;
		
		@SuppressWarnings("unchecked")
		public Node(int radix) {
			child = new Trie.Node[radix];
		}
		
		/**
		 * Returns the child node that is on the edge with the given label
		 * <em>c</em>. Returns null if there is no child node with the given
		 * edge label.
		 * 
		 * @param c character on the edge.
		 * @return child node with the edge label <em>c</em>.
		 */
		public Node child(int c) {
			return child[c];
		}
		
		/**
		 * Adds the given node <em>u</em> as a child node and label the edge
		 * to <em>u</em> with <em>c</em>.
		 * 
		 * @param c character on the edge to child node <em>u</em>.
		 * @param u child node to add.
		 */
		public void addChild(int c, Node u) {
			if (u == null)
				throw new NullPointerException("Can't add null as child node");
			if (u == this)
				throw new IllegalArgumentException(
							"Can't add self as child node!");
			child[c] = u;
			nChildren++;
		}

		/**
		 * Deletes the child node that is on the edge with the given label
		 * <em>c</em>.
		 * 
		 * @param c character on the edge to child node.
		 */
		public void deleteChild(int c) {
			child[c] = null;
			nChildren--;
		}

		/**
		 * Returns true if this node has no child node and false otherwise.
		 * 
		 * @return true if this node has no child node; false otherwise.
		 */
		public boolean isLeaf() {
			return nChildren == 0;
		}
		
		/**
		 * Returns true if this node is unmarked, i.e., this is a prefix node
		 * and does not store any value.
		 * 
		 * @return true if this node is unmarked; false otherwise.
		 */		
		public boolean isUnmarked() {
			return value == null;
		}
		
		/**
		 * Returns an {@code Iterable} of characters that are represented on the
		 * edges from this node and its child nodes.
		 * 
		 * @return {@code Iterable} of characters on edges to child nodes.
		 */
		public Iterable<Character> labels() {
			Queue<Character> children = new LinkedList<Character>();
			if (isLeaf()) return children;

			for (char c = 0; c < radix; c++) {
				if (child[c] != null)
					children.add(c);
			}
		    return children;
		}
	}

	/**
	 * Creates an instance of {@code Trie} with the given radix value.
	 * 
	 * @param radix the radix value to initialize this trie.
	 */
	public Trie(int radix) {
		if (radix <= 0)
			throw new IllegalArgumentException("Radix must be positive integer");

		this.radix = radix;
		this.root = new Node(radix);
	}
	
	/**
	 * Creates an instance of {@code Trie} with the default radix of 256 
	 * (extended ASCII).	 
	 */
	public Trie() {
		this(DEFAULT_RADIX);
	}
	
	/**
	 * Returns a new {@code Trie} with the given radix and using the given node
	 * <em>x</em> as its root node.
	 *  
	 * @param radix the radix to initialize this instance.
	 * @param x node to use as root.
	 */
	private Trie(int radix, Node x) {
		this.radix = radix;
		this.root = x;
	}
	
	/**
	 * Adds the given keyword to the trie, and associates it with the given
	 * value.
	 * 
	 * @param keyword the keyword to add to the trie.
	 * @param value value to associate with keyword.
	 */
	public void add(String keyword, E value) {
		Node v = get(root, keyword);
		if (v != null) {
			// keyword already exists, just update its value
			v.value = value;
			return;
		}
		
		v = root;
		int j = 0;
		while (v.child(keyword.charAt(j)) != null) {
			v = v.child(keyword.charAt(j));
			if (v == null) break;
			if (++j == keyword.length()) break;
		} 

		while (j < keyword.length()) {
			Node u = new Node(radix);
			v.addChild(keyword.charAt(j), u);
			v = u;
			j++;
		}
		v.value = value;
		count++;
	}

	/**
	 * Deletes the given keyword from the trie. If the keyword does not exist on
	 * the trie, the method returns with no action performed.
	 * 
	 * @param keyword the keyword to delete.
	 */
	public void delete(String keyword) {
		Node x = get(root, keyword);
		if (x == null) {
			// keyword not found
			return;
		}		
		delete(root, keyword, 0);
	}
	
	/**
	 * Performs a modified depth-first visit starting at the given trie node
	 * <em>x</em>, looking for the given keyword. There are several cases to
	 * consider:
	 * <ol>
	 * <li> the given keyword is not on the trie. No node is deleted.
	 * <li> given keyword is also a prefix of another keyword. The node is
	 *   unmarked to remove the keyword from the trie.
	 * <li> there exists another shorter keyword that is a prefix of the given
	 *   keyword. The leaf node marked with the given keyword is deleted, and
	 *   the path to the node containing the next longest prefix is retraced,
	 *   deleting all intermediate nodes that only have 1 element in them.
	 * <li> there is no shorter keyword that is a prefix of the given keyword.
	 *   In this case, the leaf node marked with the given keyword is deleted,
	 *   and all intermediate nodes from the leaf node to the root are also
	 *   deleted.
	 * <ol>
	 * 
	 * @param x the trie node to begin depth-first search.
	 * @param keyword the keyword to delete.
	 * @param depth the current depth of the depth-first search.
	 * @return true if the trie node is an empty unmarked leaf node; false
	 *   otherwise. 
	 */
	private boolean delete(Node x, String keyword, int depth) {
		if (x == null) return false;
		if (depth == keyword.length()) {
			if (x.value != null) x.value = null;
			
			if (x.isLeaf()) return true;
			else return false;
		}

		boolean isLeafnUnmarked = delete(x.child(keyword.charAt(depth)), 
									keyword, depth+1);
		// backtracking to root node, delete a child node on the path up only
		// if the child node is leaf node and is unmarked.
	    if (isLeafnUnmarked) {
	    	x.deleteChild(keyword.charAt(depth));
	    }
	    return x.isUnmarked() && x.isLeaf();
	}

	/**
	 * Returns the number of keywords on the trie.
	 * 
	 * @return number of keywords on trie.
	 */
	public int size() {
		return count;
	}

	/**
	 * Returns true if this trie is empty and false otherwise.
	 * 
	 * @return true if trie is empty; false otherwise.
	 */
	public boolean isEmpty() {
		return size() == 0;
	}
	
	/**
	 * Returns the value associated with the given keyword. If the keyword is
	 * not on the trie, null is returned.
	 *  
	 * @param keyword the keyword to look for
	 * @return the value associated with the given keyword, or null if keyword
	 *   is not found.
	 */
	public E get(String keyword) {
		Node x = get(root, keyword);

		if (x == null) return null;
		return x.value;
	}
	
	/**
	 * Searches the trie at the given node <em>x</em> for the given pattern,
	 * and returns the node associated with the pattern. If the keyword is
	 * not on the trie, null is returned.
	 * 
	 * @param x the trie node to start the search.
	 * @param pattern the pattern to look for.
	 * @return the trie node where the pattern is found, or null if the pattern
	 *   is not on the trie.
	 */
	private Node get(Node x, String keyword) {
		if (x == null) return null;

		Node v = x;
		int j = 0;		
		while (j < keyword.length()) {
			v = v.child(keyword.charAt(j));
			if (v == null) break;
			j++;
		}		
		return v;
	}

	/**
	 * Returns true if the given keyword is on the trie, and false otherwise.
	 * 
	 * @param keyword the keyword to look for.
	 * @return true if keyword is on trie; false otherwise.
	 */
	public boolean contains(String keyword) {
		return (get(root, keyword) != null);
	}

	/**
	 * Returns true if the given prefix is a prefix of a keyword on the trie.
	 * 
	 * @param prefix the prefix to look for.
	 * @return true if prefix is a prefix of a keyword on the trie; false
	 *   otherwise.
	 */
	public boolean isPrefix(String prefix) {
		Node x = get(root, prefix);
		if (x == null) return false;  // prefix not found on trie
		return true;
	}
	
	/**
	 * Returns all keys in the trie that start with the given prefix.
	 * 
	 * @param prefix the prefix to look for.
	 * @return {@code Iterable} of all keys in the trie that starts with prefix.
	 */
	public Iterable<String> keysWithPrefix(String prefix) {
		Node x = get(root, prefix);
		
		Queue<String> queue = new LinkedList<String>();
		dfsVisit(x, new StringBuilder(prefix), queue);
		return queue;
	}

	/**
	 * Performs a depth-first search on the trie starting at the given node
	 * <em>x</em> and looks for keys with the given prefix.
	 *  
	 * @param x node of trie to start depth-first search.
	 * @param prefix the prefix to look for.
	 * @param queue queue to store keywords found.
	 */
	private void dfsVisit(Node x, StringBuilder prefix, Queue<String> queue) {
		if (x == null) return;
		if (x.value != null) queue.add(prefix.toString());
		if (x.isLeaf()) return;

		for (char c : x.labels()) {
			prefix.append(c);
			dfsVisit(x.child(c), prefix, queue);
			prefix.deleteCharAt(prefix.length() - 1);
		}
	}

	/**
	 * Returns the keyword in the trie that is the longest prefix of the given
	 * word. An empty string is returned if the longest prefix for the given
	 * word does not exist in the trie.
	 * 
	 * @param word the word to look for longest prefix.
	 * @return the keyword in the trie that is the longest prefix of word
	 */
	public String longestPrefixOf(String word) {
		int len = longestPrefixDFSVisit(root, word, 0, 0);
		return word.substring(0, len);
	}

	/**
	 * Performs a modified depth-first visit starting at the given trie node
	 * <em>x</em>, looking for the longest keyword that is a prefix of the given
	 * word.
	 * 
	 * @param x the trie node to begin depth-first search.
	 * @param word the word to look for longest prefix.
	 * @param depth the current depth of the depth-first search.
	 * @param len the length of the keyword found so far; 0 if no keyword is
	 *   found.
	 * @return the length of the keyword that is the longest prefix of word
	 *   found so far.
	 */
	private int longestPrefixDFSVisit(Node x, String word, int depth, int len) {
		// We reach the end of dfs tree; just return whatever we have found				
		if (x == null) return len;
		
		// We found a keyword that is a prefix of 'word' 
		if (x.value != null) len = depth;
		
		// We have reached the required depth, so we should terminate our search
		if (depth == word.length()) return len;
		
		// Else explore deeper...
		return longestPrefixDFSVisit(x.child(word.charAt(depth)), word,
					depth+1, len);
	}

	/**
	 * Returns a subtrie starting at the given prefix.
	 * 
	 * @param prefix the prefix to look for.
	 * @return trie starting at the given prefix.
	 * @throws IllegalArgumentException if given prefix is not a valid prefix
	 *   for any keyword on the trie.
	 */
	public Trie<E> getSubtrie(String prefix) {
		Node x = get(root, prefix);
		if (x == null)
			throw new IllegalArgumentException(prefix + 
					" is not a valid prefix on this Trie");		

		return new Trie<E>(radix, x);
	}

	/**
	 * @param args
	 */
	public static void main(String[] args) {
		String[] words = {"pot", "potato", "pottery", "tattoo", "tempo"};
		Trie<Integer> trie = new Trie<Integer>();
		
		System.out.println("---- Test: add and get ----");
		int i = 0;
		for (String word : words) {
			trie.add(word, i++);
		}
		for (String word : words) {
			System.out.printf("Added '%s' with value %d\n", word, trie.get(word));
		}
		System.out.println();
		
		System.out.println("---- Test: size ----");
		System.out.printf("Expected size=%d, trie size=%d\n", 
				words.length, trie.size());
		System.out.println();

		System.out.println("---- Test: contain ----");
		for (String word : words) {
			System.out.printf("Contains '%s'? %b\n", word, trie.contains(word));	
		}
		String invalid = "invalid";
		System.out.printf("Contains '%s'? %b\n", invalid, trie.contains(invalid));
		System.out.println();

		System.out.println("---- Test: isPrefix ----");
		for (int j = 0; j < words[2].length(); j++) {
			System.out.printf("prefix '%s'? %b\n",
					words[2].substring(0, j), 
					trie.isPrefix(words[2].substring(0, j)));
		}
		String notPrefix = "DGF";
		System.out.printf("isPrefix '%s'? %b\n", notPrefix,
				trie.isPrefix(notPrefix));
		System.out.println();

		System.out.println("---- Test: keysWithPrefix ----");
		String prefix = "po";
		System.out.printf("Keys with prefix '%s':\n", prefix);
		for (String s : trie.keysWithPrefix(prefix)) {
			System.out.printf("%s\n", s);
		}
		System.out.println();
		
		System.out.println("---- Test: getSubtrie ----");
		String subtriePrefix = words[1].substring(0, 3);
		System.out.printf("Subtrie of '%s'\n", subtriePrefix);

		Trie<Integer> subtrie0 = trie.getSubtrie(subtriePrefix);
		for (int j = 4; j < words[1].length(); j++) {
			System.out.printf("isPrefix '%s'? %b\n",
					words[1].substring(3, j), 
					subtrie0.isPrefix(words[1].substring(3, j)));			
		}
		for (int j = 4; j < words[2].length(); j++) {
			System.out.printf("isPrefix '%s'? %b\n",
					words[2].substring(3, j), 
					subtrie0.isPrefix(words[2].substring(3, j)));			
		}
		String notSubtriePrefix = "att";
		System.out.printf("isPrefix '%s'? %b\n", notSubtriePrefix,
				subtrie0.isPrefix(notSubtriePrefix));
		System.out.println();

		System.out.println("---- Test: update value ----");
		System.out.printf("Old value of '%s' = %d\n", 
				words[0], trie.get(words[0]));
		trie.add(words[0], 100);
		System.out.printf("New value of '%s' = %d\n", 
				words[0], trie.get(words[0]));
		System.out.println();

		System.out.println("---- Test: longestPrefixOf ----");
		String[] longestPrefixWords = {"potstone", "potato chip", "invalid"};
		for (String s : longestPrefixWords) {
			System.out.printf("Longest prefix of '%s' = '%s'\n", 
					s, trie.longestPrefixOf(s));
		}
		System.out.println();
		
		System.out.println("---- Test: delete ----");
		trie.delete(words[1]);
		System.out.printf("'%s' deleted from trie\n", words[1]);
		trie.delete(invalid);
		System.out.printf("Contains '%s'? %b\n", 
				words[1], trie.contains(words[1]));
		System.out.printf("Contains '%s'? %b\n", 
				words[0], trie.contains(words[0]));
		System.out.printf("Contains '%s'? %b\n", 
				words[2], trie.contains(words[2]));
	}
}
