import java.util.Collections;

/**
 * BTree class for creating and managing a BTree
 * 
 * @author Jeff Allen, Abel Almedia, Andy Breland
 *
 */
public class BTree {

	private BTreeNode root;
	private int height, degree, seqLength, debugLevel, cacheSize;
	private String fileName;
	//TODO - Add unimplemented variables

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
		root = new BTreeNode(0, degree, true, true); // index = 0, degree, isRoot, isLeaf
		this.degree = degree;
		this.fileName = fileName;
		this.seqLength = seqLength;
		this.cacheSize = cacheSize;
		this.debugLevel = debugLevel;
		//TODO - Add unimplemented code
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
		//TODO - Add unimplemented code
	}

	/**
	 * 
	 * @param o - TreeObject to insert
	 */
	public void insert(TreeObject o) {
		//TODO - Add unimplemented method
	}

	/**
	 * 
	 * @param o - TreeObject to insert
	 */
	public void insertNotFull(TreeObject o) {
		//TODO - Add unimplemented method
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

		// add y's node's second  half to z node and reindex
		for (int j = 0; j < degree - 1; j++) {
			// add y's node's second  half to z node and reindex
			z.setKey(j, y.getKey(j + degree));
			// update number of keys for z and y
			z.increaseKeyCount();
			y.decreaseKeyCount();
		}
		//checking if y is a leaf
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
		//10 for loop
		for (int j = x.getKeyCount(); j > y.getKeyCount(); j++) {
			x.setChildPointer(j + 1, x.getChildPointer(j));
		}
		x.setChildPointer(i + 1, z.getIndex());

		//13 for loop
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
