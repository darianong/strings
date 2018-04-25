import java.util.HashMap;
import java.util.HashSet;
import java.util.LinkedList;
import java.util.Map;
import java.util.Queue;
import java.util.Set;

/**
 * The purpose of this code is to illustrate the followings:
 * <ol>
 * <li> implementation of the Aho-Corasick algorithm for multiple exact string
 * matching.
 * </ol>
 * Given a text <em>T[1..m]</em> and a set <em>P={P<sub>1</sub>, P<sub>2</sub>,
 * ..., P<sub>k</sub>}</em> of patterns, the <b>multiple exact string matching 
 * problem</b>, also known as the <b>exact set matching problem</b>, asks for
 * the occurrences of all patterns in the text.
 * <br>
 * Let <em>n</em> = sum<sub>i=1..k</sub>|P<sub>i</sub>|. The multiple exact
 * string matching problem can be solved in time<br>
 * <em><pre>
 *   O(|P<sub>1</sub>| + m + ... + |P<sub>k</sub>| + m) = O(n +km)
 * </pre></em>
 * by applying any linear-time exact matching <em>k</em> times.
 * <p>
 * The Aho-Corasick algorithm, which was invented by Alfred V. Aho and Margaret 
 * J. Corasick [1], is a classic solution to the exact set matching problem. It
 * works in time <em>O(n+m+z)</em>, where <em>z</em> is the number of pattern
 * occurrences in <em>T</em>. The algorithm is an extension of the Morris-Pratt
 * algorithm for multiple exact string matching. It uses a trie <em>trie(P)</em>
 * (see {@link Trie}) as an automaton and augments it with a failure function
 * similar to the Morris-Pratt failure function (see {@link KnuthMorrisPratt}).
 * <p>
 * The pseudo-code for the Aho-Corasick algorithm is as follows.
 * <pre>
 *   aho_corasick(T, P)
 *     input: 
 *       text <em>T</em> of length <em>m</em>
 *       pattern set <em>P={P<sub>1</sub>, P<sub>2</sub>,..., P<sub>k</sub>}</em>
 *     output: 
 *       all pairs <em>(i,j)</em> such that P<sub><i>i</i></sub> occurs in
 *       <em>T</em> ending at <em>j</em> 
 * 
 *     construct_AC_automaton(P)
 *     
 *     v = root    // starts at the root node/state
 *     for j = 0 to m-1 do
 *         while child(v, T[j]) = null do
 *             v = fail(v)  // follow a fail transition.
 *
 *         v = child(v, T[j])  // follow a child/goto transition
 *         for i in patterns(v) do
 *             output(i, j)
 * </pre>
 * Let <em>T<sub>v</sub></em> denotes the string that trie node <em>v</em>
 * represents.<br>
 * Let <em>root</em> be the root of the standard trie.<br>
 * Let <em>child(c)</em> be the child function of the standard trie. In the
 * context of the Aho-Corasick algorithm, the child function is also known as
 * the <b>goto</b> function, since gives the node (state) to enter from the
 * current state by matching a single character <em>c</em>.<br>
 * Let <em>fail()</em> be the failure function. It gives the node (state) to
 * enter at a mismatch. For a pair of trie nodes <em>v</em> and <em>u</em>, the
 * function <em>fail(v)=u</em> is such that <em>T<sub>u</sub></em> is the
 * <b>longest proper suffix</b> of <em>T<sub>v</sub></em>.<br>
 * Let <em>patterns(v)</em> be the set of pattern indices <em>i</em> such that
 * <em>P<sub>i</sub></em> is a suffix of <em>T<sub>v</sub></em>.
 * <p>
 * The algorithm starts at the root node, follows the path labeled by characters
 * of <em>P</em> as long as possible. For each character on <em>T[0..m-1]</em>,
 * the automaton performs 0 or more fail transitions, followed by a goto
 * transition. Each goto transition either causes the automaton to stay at the
 * root node, or increases the depth of the state <em>v</em> of the automaton by
 * 1. The depth of <em>v</em> is increased by at most <em>m</em> times.
 * <p>
 * Each fail transition moves the automaton closer to the root. The total number
 * lf fail transitions is at most <em>m</em>.
 * At each stage, the algorithm computes the node <em>v</em> such that 
 * <em>T<sub>v</sub></em> is the longest suffix of <em>T[0..j]</em> represented
 * by any node.
 * <p>
 * Let <em>z</em> be the number of pattern occurrences in the text 
 * <em>T[0..j]</em>. The <em>z</em> occurrences can be reported in 
 * <em>z</em> x O(1) = O(<em>z</em>) time. Thus, searching the text with the
 * Aho-Corasick automaton takes O(<em>m+z</em>).
 * <p>
 * If the alphabet <em>E</em> is fixed, the automaton can be constructed in
 * O(<em>n</em>) time, where 
 * <em><pre>
 *     O(|P<sub>1</sub>| + |P<sub>2</sub>| + ... + |P<sub>k</sub>|) = O(n)
 * </em></pre>
 * The pseudo-code for constructing the Aho-Corasick automaton is as follows.
 * <pre>
 *   construct_aho_corasick_automaton(P)
 *     input: 
 *       pattern set <em>P={P<sub>1</sub>, P<sub>2</sub>,..., P<sub>k</sub>}</em>
 *
 *     // phase 1: construct trie
 *     root = new node
 *     for i 1 to k do
 *         v = root
 *         j = 0
 *         while child(v, P<sub>i</sub>[j]) != null do
 *             v = child(v, P<sub>i</sub>[j])
 *             j++
 *          
 *          while j < |P<sub>i</sub>| do
 *              u = new node
 *              child(v, P<sub>i</sub>[j]) = u
 *              v = u
 *              j++
 *          
 *          patterns(v) = {i}
 *     
 *     // phase 2: compute fail transitions
 *     compute_fail_transition(root)
 * </pre>
 * Phase 1 of the automaton construction algorithm is the same as the standard 
 * trie construction algorithm. Phase 1 takes O(<em>n</em>) time, where <em>n</em>
 * is the number of nodes on the trie. At the end of phase 1, we have a standard
 * trie for all the patterns. Phase 2 adds the fail transitions to the standard
 * trie. The pseudo-code for phase 2 is as follows.
 * <pre>
 *   compute_fail_transition(root)
 *       q = new FIFO queue
 *       for c in E do
 *           if child(root, c) != null then
 *               s = child(root, c)
 *               fail(s) = root
 *               enqueue(q, s)
 *       
 *        while not isEmpty(q) do
 *            u = dequeue(q)
 *            for c in E such that child(u, c) != null do 
 *                w = fail(u)
 *                while child(w, c) = null do
 *                    w = fail(w)
 *                    
 *                v = child(u, c)
 *                fail(v) = child(w, c)
 *                patterns(v) = patterns(v) U patterns(fail(v))
 *                enqueue(q, v)
 * </pre>
 * The fail transition computation starts by setting the fail transition for all
 * immediate child nodes of the root to root. These child nodes are also added
 * to a FIFO queue. The trie is then visited in breadth-first order starting at
 * depth 1 (depth(root) = 0). Let <em>u = parent(v)</em> and <em>child(u,c) = v
 * </em>. <em>u</em> has been processed, i.e., its fail transition has been
 * computed either before the breadth-first search, or in the previous iteration
 * of the breadth-first search. To determine the fail transition for <em>v</em>,
 * we need to look for the deepest node that is the longest proper suffix of
 * <em>S<sub>v</sub></em>. The nodes that represent the suffixes of
 * <em>S<sub>v</sub></em> are in the set
 * <pre><em>
 *     fail*(v) = {v, fail(v), fail(fail(v)), ..., root}
 * </em></pre>
 * Thus, for any node <em>w</em>, we have
 * <ul>
 * <li> if <em>w in fail*(v)</em>, then <em>parent(fail(v)) in fail*(u)</em> 
 * <li> if <em>w in fail*(u)</em> and <em>child(w,c)</em> != null, then 
 *   <em>child(w,c) in fail*(v)</em>
 * </ul>
 * Therefore, <em>fail(v) = child(w,c)</em>, where <em>w</em> is the first node
 * in <em>fail*(u)</em> other than <em>u</em> such that <em>child(w,c)</em> !=
 * null.
 * <p>
 * A suitable data structure is required for implementing <em>patterns</em> so
 * that the union operation takes constant time.  
 * <p>
 * References:<br>
 * [1] Aho, Alfred V., Corasick, Margaret J., "Efficient string matching: An 
 *     aid to bibliographic search". Communications of the ACM, 18(6): 333–340,
 *     1975.<br>
 * [2] Juha Karkkainen, "58093: String Processing Algorithms," lecture notes,
 *     Autumn 2013<br>
 * [3] Pekka Kilpelainen, "Biosequence Algorithms," lecture notes, Spring 2005.   
 * <p>
 * @see Trie
 * @see KnuthMorrisPratt
 * @see BoyerMooreSimple
 */
public class AhoCorasick {
	private final static int DEFAULT_RADIX = 256;  // extended ASCII
    private final int radix = DEFAULT_RADIX;

    private Map<Integer, Node> child = new HashMap<Integer, Node>();
	private Node root = null;  // root node of the trie

    private Map<Integer, String> patternMap = new HashMap<Integer, String>();

	/**
	 * The {@code AhoCorasick.Node} inner class encapsulates the implementation
	 * of a trie node based on a hash table child function. It assumes a single
	 * global hash table <em>child</em> is available. It encapsulates the logic
	 * to hash each child trie node to the global hash table.
	 * 
	 * @author Darian Ong
	 */
	private class Node {
		private int key = 0;
		private Node fallbackNode = null;
		private Set<Integer> patterns = new HashSet<Integer>();
		private int nChildren = 0;

		/**
		 * Creates a {@code HashTrie.Node} for the root of the trie tree. This
		 * constructor should only be used to initialize the root node of the
		 * trie tree.
		 */
		public Node() {
		}
		
		/**
		 * Creates a {@code HashTrie.Node} with the given parent node. The edge
		 * from the parent node to this node is labeled with the symbol 
		 * <em>c</em>. If the parent node is null, then this node is interpreted
		 * as the root node of the trie tree.
		 * 
		 * @param parent the parent {@code HashTrie.Node}.
		 * @param c symbol to label the edge from parent node to this node.
		 */
		public Node(Node parent, char c) {
			if (parent == null) {
				throw new IllegalArgumentException("parent node cannot be null");
			}
			this.key = parent.key() * 31 + c;
		}

		/**
		 * Returns the key of this trie node. The key is computed during the
		 * node construction. A root node has the key value of 0. For a non-root
		 * node, the key is computed using integer arithmetic as:
         * <blockquote><pre>
         *     parent.key * 31 + c
         * </pre></blockquote>
         * where <em>c</em> is the {@code char} value of the symbol labeling
         * the edge from the parent node to this node.
         * <p>
         * This is the same arithmetic formula used for computing the hash code
         * of Java {@code String} objects. Hence, the key for this node is the
         * Java {@code String} hash code for the string represented by this
         * node, i.e., the string obtained by concatenating the symbols on the
         * path from the root to this node.
         * 
		 * @return key of this trie node.
		 */
		public int key() {
			return key;
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
			return child.get(31 * key + c);
		}
		
		/**
		 * Adds the given node <em>u</em> as a child node and label the edge
		 * to <em>u</em> with <em>c</em>.
		 * 
		 * @param c character on the edge to <em>u</em>.
		 * @param u child node to add.
		 */
		public void addChild(int c, Node u) {
			if (u == null)
				throw new NullPointerException("Can't add null as child node");
			if (u == this)
				throw new IllegalArgumentException(
							"Can't add self as child node!");
			child.put(u.key(), u);
			nChildren++;
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
		 * Returns an {@code Iterable} of characters that are represented on the
		 * edges from this node and its child nodes.
		 * 
		 * @return {@code Iterable} of characters on edges to child nodes.
		 */
		public Iterable<Character> labels() {
			Queue<Character> children = new LinkedList<Character>();			
			if (isLeaf()) return children;

			for (char c = 0; c < radix; c++) {
				if (child.get(31 * key + c) != null)
					children.add(c);
			}
		    return children;
		}

		/**
		 * Returns true if this node is marked, i.e., this is not a prefix node,
		 * but a node that represents a full pattern stored on the trie.
		 * 
		 * @return true if this node is marked; false otherwise.
		 */		
		public boolean isMarked(int i) {
			return patterns.contains(i);
		}

		/**
		 * Returns the failure transition node.
		 * 
		 * @return failure transition node.
		 */
		public Node fail() {
			return fallbackNode;
		}

		/**
		 * Sets the failure transition node to the given node 
		 * <em>fallbackNode</em>.
		 * 
		 * @param fallbackNode the failure transition node to set to.
		 */
		public void fail(Node fallbackNode) {
			this.fallbackNode = fallbackNode;
		}

		/**
		 * Adds the given pattern index <em>i</em> to the set of patterns 
		 * represented by this trie node.
		 * 
		 * @param i the pattern index to add.
		 */
		public void patterns(int i) {
			patterns.add(i);
		}
		
		/**
		 * Adds the given set of pattern indices <em>si</em> to the set of 
		 * patterns represented by this trie node.
		 * 
		 * @param si the set of pattern indices to add.
		 */
		public void patterns(Set<Integer> si) {
			patterns.addAll(si);
		}

		/**
		 * Returns the set of pattern indices represented by this trie node.
		 * 
		 * @return set of pattern indices represented by this node.
		 */
		public Set<Integer> patterns() {
			return patterns;
		}
	}
	
	/**
	 * Creates an instance of {@code AhoCorasick} string matcher for the given
	 * array of pattern strings. This initializes the Aho-Corasick automaton.
	 * 
	 * @param patterns array of pattern strings.
	 */
	public AhoCorasick(String[] patterns) {
		root = new Node();

		// Create a mapping of pattern id to pattern string. 
		int i = 1;
		for (String pattern : patterns) {
			patternMap.put(i++, pattern);
		}
		constructAutomaton();
	}
	
	/**
	 * Constructs the Aho-Corasick automaton for string matching.
	 * <br>
	 * pre-condition: the pattern id mapping <em>patternMap</em> has been
	 *   created.<br>
	 * post-condition: the Aho-Corasick automaton is created.
	 * 
	 * @param patterns array of pattern strings.
	 */
	private void constructAutomaton() {
		// In the first phase of the automaton construction, we create a
		// standard trie.
		for (Integer i : patternMap.keySet()) {
			addKeyword(patternMap.get(i), i);
		}
		
		// In the second phase of the automaton construction, we complete the
		// automaton by computing the fail transitions from each node on the
		// trie.
		computeFailTransitions();
	}
	
	/**
	 * Adds the given pattern to the trie, and associates it with the given
	 * value.
	 * <br>
	 * pre-condition: root node is not null.
	 * 
	 * @param pattern the keyword to add to the trie.
	 * @param value value to associate with keyword.
	 */
	private void addKeyword(String pattern, int i) {
		Node v = get(root, pattern);
		if (v != null && v.isMarked(i)) {
			return;
		}

		v = root;
		int j = 0;
		while (v.child(pattern.charAt(j)) != null) {
			v = v.child(pattern.charAt(j));
			if (v == null) break;
			if (++j == pattern.length()) break;
		}

		while (j < pattern.length()) {
			Node u = new Node(v, pattern.charAt(j));
			v.addChild(pattern.charAt(j), u);
			v = u;
			j++;
		}

		// mark node v as a representative of pattern i
		v.patterns(i); 
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
	private Node get(Node x, String pattern) {
		if (x == null) return null;

		Node v = x;
		int j = 0;		
		while (j < pattern.length()) {
			v = v.child(pattern.charAt(j));
			if (v == null) break;
			j++;
		}		
		return v;
	}

	/**
	 * Computes the fail transition function for the Aho-Corasick automaton.
	 * <br>
	 * pre-condition: 1) root is non-null, 2) the trie for the Aho-Corasick
	 *   automaton has been created.<br>
	 * post-condition: the Aho-Corasick automaton is created.   
	 */
	private void computeFailTransitions() {
		Queue<Node> queue = new LinkedList<Node>();
		
		// All nodes of depth 1 have their fail() transition set to root
		// For all these nodes, we add them to our queue to compute the fail()
		// transitions for their child nodes.
		for (char c : root.labels()) {
			Node s = root.child(c);
			s.fail(root);
			queue.add(s);
		}

		// For all other symbols that does not have an edge labeled out of root,
		// we create a child node for each of these symbols and set its fail()
		// transition to go back to root.
		for (char c = 0; c < radix; c++) {
			if (root.child(c) == null) {
				Node u = new Node(root, c);
				u.fail(root);
				root.addChild(c, u);
			}
		}

		// Processes the trie from depth 1 in breadth-first order. Failure
		// transitions for immediate descendants of root have been computed
		// above before the breadth-first traversal.
		while (!queue.isEmpty()) {
			Node u = queue.remove();

			for (char c : u.labels()) {
				Node w = u.fail();
				while (w.child(c) == null) {
					w = w.fail();
	  			}

				Node v = u.child(c);
				v.fail(w.child(c));
                v.patterns(v.fail().patterns());
                queue.add(v);
			}
		}
	}

	/**
	 * Searches the given text for all occurrences of the patterns used to
	 * create this instance of {@code AhoCorasick}. The search result is a
	 * mapping from each pattern that occurs in the text and the set of ending
	 * positions in the text where the corresponding pattern occurs. 
	 * 
	 * @param text the text to search.
	 * @return a map containing (pattern, j*) pairs, where j* is the set of
	 *   ending positions in the text where pattern occurs.
	 */
	public Map<String, Set<Integer>> search(String text) {
		Map<String, Set<Integer>> searchResult = new
				HashMap<String, Set<Integer>>();

		Node state = root;
		for (int j = 0; j < text.length(); j++) {

			while (state.child(text.charAt(j)) == null)
				state = state.fail();

			state = state.child(text.charAt(j));
			for (Integer i : state.patterns()) {
				addOutput(searchResult, i, j);
			}
		}
		return searchResult;
	}
	
	private void addOutput(Map<String, Set<Integer>> searchResult, 
					int i, int j) {
		String pattern = patternMap.get(i);
		if (!searchResult.containsKey(pattern)) {
			searchResult.put(pattern, new HashSet<Integer>());
		}
		
		Set<Integer> indices = searchResult.get(pattern);
		indices.add(j);
	}

	/**
	 * @param args
	 */
	public static void main(String[] args) {
		String[] patterns = {"he", "she", "his", "hers", "her"};
		//String[] patterns = {"her"};
		AhoCorasick matcher = new AhoCorasick(patterns);
		
		String text1 = "cipher";
		Map<String, Set<Integer>> matches = matcher.search(text1);
		System.out.printf("Text: %s\n", text1);
		for (String s : matches.keySet()) {
			System.out.printf("Found pattern '%s' ending at positions: ", s);
			Set<Integer> indices = matches.get(s);
			for (Integer i: indices) {
				System.out.printf("%d,  ", i);
			}
			System.out.println();
		}
		System.out.println();
	}
}
