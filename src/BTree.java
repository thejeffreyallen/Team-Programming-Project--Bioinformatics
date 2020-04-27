import java.util.ArrayList;
import java.util.Collections;

/**
 * BTree class for creating and managing a BTree
 * 
 * @author Jeff Allen, Abel Almedia, Andy Breland
 *
 */
public class BTree {

	private BTreeNode root, parent, leftSibling, rightSibling; // Project instructions suggest keeping a few more nodes in memory
	private int height, degree, seqLength, debugLevel, cacheSize, nodeCount;
	private String fileName;
	private BTreeRW rw;
	// TODO - Add unimplemented variables

	/**
	 * BTree constructor - initializes a new BTree object for writing to file
	 * 
	 * @param degree     - degree of the BTree. If value is 0, calculate optimal
	 *                   degree
	 * @param fileName   - name of file to write to
	 * @param seqLength  - how many characters to include when reading. i.e. 3 ---
	 *                   [ATC]
	 * @param cacheSize  - size of the cache. The bigger the cache, the faster the
	 *                   program will run.
	 * @param debugLevel - default value is 0. if debug level is 0 Any diagnostic
	 *                   messages, help and status messages must be be printed on
	 *                   standard error stream. If it is 1 the program writes a text
	 *                   file named dump. The dump file contains DNA string
	 *                   (corresponding to the key stored) and frequency in an in
	 *                   order traversal.
	 * 
	 */
	public BTree(int degree, String fileName, int seqLength, int cacheSize, int debugLevel) {
		root = new BTreeNode(nodeCount++, degree, true, true); // index = 0, degree, isRoot, isLeaf
		this.degree = degree;
		this.fileName = fileName;
		this.seqLength = seqLength;
		this.cacheSize = cacheSize;
		this.debugLevel = debugLevel;
		rw = new BTreeRW(fileName, cacheSize);
		// TODO - Add unimplemented code
	}

	/**
	 * Secondary constructor - Read and construct a BTree from file
	 * 
	 * @param bTreeFile  - file from which to read the tree from
	 * @param degree     - degree of the BTree. If value is 0, calculate optimal
	 *                   degree
	 * @param seqLength  - how many characters to include when reading. i.e. 3 ---
	 *                   [ATC]
	 * @param cacheSize  - size of the cache. The bigger the cache, the faster the
	 *                   program will run.
	 * @param debugLevel - default value is 0. if debug level is 0 Any diagnostic
	 *                   messages, help and status messages must be be printed on
	 *                   standard error stream. If it is 1 the program writes a text
	 *                   file named dump. The dump file contains DNA string
	 *                   (corresponding to the key stored) and frequency in an in
	 *                   order traversal.
	 */
	public BTree(String bTreeFile, int degree, int seqLength, int cacheSize, int debugLevel) {
		this.degree = degree;
		this.fileName = bTreeFile;
		this.seqLength = seqLength;
		this.cacheSize = cacheSize;
		this.debugLevel = debugLevel;
		rw = new BTreeRW(fileName, cacheSize);
		// TODO - Add unimplemented code
	}

	/**
	 * 
	 * @param o - TreeObject to insert
	 */
	public void insert(TreeObject k) {
		// TODO - Add unimplemented method
		if (this.root == null) // if tree is initially empty
		{
			this.root = new BTreeNode(nodeCount++, this.degree, true, false); // Allocate new node as root
			this.root.addKey(k); // key added, done
		} else// if tree is not empty
		{
			if (this.root.isFull()) {
				BTreeNode s = new BTreeNode(nodeCount++, this.degree, true, false); // Allocate new node
				s.addChild(root.getIndex()); // make the current root a child of s
				root.setParentPointer(s.getIndex());
				splitChild(s, root.getIndex(), root); // split the node "root"
				// TODO find correct child node to insert not full
				BTreeNode child = null; // need to set to correct child node
				insertNotFull(child, k);

				root = s; // make s the new root

			} else {
				insertNotFull(root, k);
			}
		}
	}

	/**
	 * 
	 * @param x - Node to enter key value in
	 * @param k - Key value to enter into node x
	 */
	public void insertNotFull(BTreeNode x, TreeObject k) {
		// TODO - Add unimplemented method
		int i = x.getKeyCount(); // start at the right most key in node x
		if (x.isLeaf()) {
			while (i >= 0 && k.compareTo(x.getKey(i)) == -1) // find the correct position to insert k
			{
				i--;
			}
			x.insertKey(i, k); // insert k at index i
			rw.diskWrite(x); // write to disk, done.
		} else {
			while (i >= 1 && k.compareTo(x.getKey(i)) == -1) { // find the correct position to insert k
				i--; // line 10 - in class btree pseudocode
			}
			i++;// line 11 - in class btree pseudocode
			BTreeNode c = rw.diskRead(x.getChildPointer(i)); // read child node from disk at offset i
			if (c.getKeyCount() == (2 * degree) - 1) { // if node is full
				splitChild(x, i, c); // split node
				if (k.compareTo(x.getKey(i)) == -1) { // find the correct position to insert k
					i++;// line 16 - in class btree pseudocode
				}
				insertNotFull(c, k);
			}
		}
	}

	/**
	 * @param x - BTreeNode (parent)
	 * @param y - BTreeNode to split (child)
	 */
	public void splitChild(BTreeNode x, int i, BTreeNode y) {
		BTreeNode z = null;

		z.setIsLeaf(y.isLeaf());

		// assign n[z] = t-1 ??
		z.setKeyCount(degree - 1);

		x.setParentPointer(y.getParentPointer());

		// add y's node's second half to z node and reindex
		for (int j = 0; j < degree - 1; j++) {
			// add y's node's second half to z node and reindex
			z.setKey(j, y.getKey(j + degree));
			// update number of keys for z and y
			z.increaseKeyCount();
			y.decreaseKeyCount();
		}
		// checking if y is a leaf
		if (y.isLeaf() != true) {

			// reindexing y's childern to z's childen
			for (int j = 0; j < degree; j++) {
				z.setChildPointer(i, y.getChildPointer(j + degree));
				// sort the children Todo test THIS
				Collections.sort(z.getChildren());
			}
		}
		// n[y] = t-1
		y.setKeyCount(degree - 1);
		// 10 for loop
		for (int j = x.getKeyCount(); j > y.getKeyCount(); j++) {
			x.setChildPointer(j + 1, x.getChildPointer(j));
		}
		x.setChildPointer(i + 1, z.getIndex());

		// 13 for loop
		for (int j = x.getKeyCount() - 1; j > y.getKeyCount() - 1; j++) {
			x.setKey(j + 1, x.getKey(j));
		}
		//
		x.setKey(i, z.getKey(degree - 1));
		x.setKeyCount(x.getKeyCount() + 1);

		// disk write for y
		// disk write for z
		// disk write for x

	}

	/**
	 * 
	 * @return - height of the tree
	 */
	public int getHeight() {
		return this.height;
	}

	/**
	 * 
	 * @return - the root node of the BTree
	 */
	public BTreeNode getRoot() {
		return this.root;
	}

	/**
	 * 
	 * @return - FileName associated with BTree on disk
	 */
	public String getFileName() {
		return this.fileName;
	}

	/**
	 * 
	 * @return - The optimal degree of the BTree
	 */
	public int calculateDegree() {
		int result = 0;
		// TODO - add calculations
		return result;
	}
}
