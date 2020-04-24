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
	 * 
	 * @param node - BTreeNode to split
	 */
	public void splitChild(BTreeNode node) {
		//TODO - Add unimplemented method
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
