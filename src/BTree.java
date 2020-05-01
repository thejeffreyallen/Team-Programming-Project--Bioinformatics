import java.io.File;

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
		//root = new BTreeNode(nodeCount++, degree, true, true); // index = 0, degree, isRoot, isLeaf
		File file = new File(fileName);
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
	 * @param //BTreeFile  - file from which to read the tree from
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
	public BTree(File bTreeOnDisk, int degree, int seqLength, int cacheSize, int debugLevel) {
		this.degree = degree;
		this.fileName = bTreeOnDisk.getName();
		this.seqLength = seqLength;
		this.cacheSize = cacheSize;
		this.debugLevel = debugLevel;
		rw = new BTreeRW(fileName, cacheSize);
		// TODO - Add unimplemented code
		
		
	}

	/**
	 * 
	 * @param k - TreeObject to insert
	 */
	public void insert(TreeObject k) {
		// TODO - Add unimplemented method
		if (this.root == null) // if tree is initially empty
		{
			this.root = new BTreeNode(nodeCount++, this.degree, true, true); // Allocate new node as root
			this.root.addKey(k); // key added, done
			this.root.setKeyCount(1); // set the key count to zero

		} else// if tree is not empty
		{
			if (this.root.isFull()) {
				BTreeNode s = new BTreeNode(nodeCount++, this.degree, true, false); // Allocate new node
				s.addChild(root.getIndex()); // make the current root a child of s
				root.setParentPointer(s.getIndex());
				splitChild(s, root.getIndex(), root); // split the node "root"
			
				insertNotFull(s, k);

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
		int i = x.getKeyCount()-1; // start at the right most key in node x
		if (x.isLeaf()) {
			while (i >= 1 && k.compareTo(x.getKey(i)) == -1) // find the correct position to insert k
			{
				i--;
			}
			x.insertKey(i, k); // insert k at index i
			x.setKeyCount(x.getKeyCount() + 1);// add one to the keyCount
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

// todo
//	private int allocateNode(){
//	}
	/**
	 * @param x - BTreeNode (parent)
	 * @param y - BTreeNode to split (child)
	 */
	public void splitChild(BTreeNode x, int index, BTreeNode y) {
		// allocate the  new B-Tree
		BTreeNode zRightNode = new BTreeNode(degree,false, true, fileName, 4);

		// need to pulls full child
		zRightNode.setIsLeaf(y.isLeaf());

		x.setParentPointer(y.getParentPointer());

		// add y's node's second half to zRightNode node and reindex
		for (int j = 1; j < degree ; j++) {
			// add y's node's second half to zRightNode node and reindex
			zRightNode.setKey(j, y.getKey(j + degree));
			// update number of keys for zRightNode and y - Array List updates
		}
		// checking if y is a leaf
		if (y.isLeaf() != true) {

			// reindexing y's children to zRightNode's children
			for (int j = 1; j <= degree; j++) {
				x.setChildPointer(j, x.getChildPointer(j + degree));
			}
		}
		// move the x node child pointers to add the zRight node
		for(int j = x.getKeyCount() +1; j> index; j--){
			x.setChildPointer(j+1,x.getChildPointer(j));
		}
		//add zRightNode as a child for parent node
		x.setChildPointer(index + 1, zRightNode.byteOffSet);

		// create a spot for middle object to go up
		for(int j = x.getKeyCount(); j >= index;j--){
			x.setKey(j+1, x.getKey(j));
		}

		// moving middle objects from y node to x node
		x.setKey(index, y.getKey(degree));
		x.setKeyCount(x.getKeyCount()+1);

		for(int j = y.getKeyCount(); j > degree; j--){
			y.removekey(j);
		}

		if(!y.isLeaf()){
			for(int j = y.getNumChildPtrs(); j > degree; j--){
				y.removeChild(j);
			}
		}
		y.removekey(degree);
		// update obj counts
		x.setIsLeaf(false);
		zRightNode.setKeyCount(degree -1);
		y.setKeyCount(degree -1);

		// disk write for y
		rw.diskWrite(y);
		// disk write for zRightNode
		rw.diskWrite(zRightNode);
		// disk write for x
		rw.diskWrite(x);

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
	
	public int getSequenceLength()
	{
		return this.seqLength;
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
	
	public String toString()
	{
		StringBuilder sb = new StringBuilder();
		sb.append(root.toString());
		return sb.toString();
	}
}
