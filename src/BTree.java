import java.io.File;

/**
 * BTree class for creating and managing a BTree
 * 
 * @author Jeff Allen, Abel Almedia, Andy Breland
 *
 */
public class BTree {

	BTreeNode root;
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
		// root = new BTreeNode(nodeCount++, degree, true, true); // index = 0, degree,
		// isRoot, isLeaf
		File file = new File(fileName);
		file.delete();
		this.degree = degree;
		this.fileName = fileName;
		this.seqLength = seqLength;
		this.cacheSize = cacheSize;
		this.debugLevel = debugLevel;
		rw = new BTreeRW(fileName, cacheSize, seqLength);
		this.root = new BTreeNode(degree, true, true, fileName, 4);
		nodeCount++;
		// TODO - Add unimplemented code
	}

	/**
	 * Secondary constructor - Read and construct a BTree from file
	 * 
	 * @param //BTreeFile - file from which to read the tree from
	 * @param degree      - degree of the BTree. If value is 0, calculate optimal
	 *                    degree
	 * @param seqLength   - how many characters to include when reading. i.e. 3 ---
	 *                    [ATC]
	 * @param cacheSize   - size of the cache. The bigger the cache, the faster the
	 *                    program will run.
	 * @param debugLevel  - default value is 0. if debug level is 0 Any diagnostic
	 *                    messages, help and status messages must be be printed on
	 *                    standard error stream. If it is 1 the program writes a
	 *                    text file named dump. The dump file contains DNA string
	 *                    (corresponding to the key stored) and frequency in an in
	 *                    order traversal.
	 */
	public BTree(File bTreeOnDisk, int degree, int seqLength, int cacheSize, int debugLevel) {

		rw = new BTreeRW(fileName, cacheSize, seqLength);

		this.degree = degree;
		this.fileName = bTreeOnDisk.getName();
		this.seqLength = seqLength;
		this.cacheSize = cacheSize;
		this.debugLevel = debugLevel;

		// TODO - Add unimplemented code

	}

	/**
	 * 
	 * @param k - TreeObject to insert
	 */
	public void insert(TreeObject k) {
		BTreeNode r = root;
		if (r.isFull()) {
			BTreeNode s = new BTreeNode(degree, false, true, fileName, 4); // Allocate new node
			nodeCount++;
			root = s; // make s the new root
			s.addChild(r.getIndex()); // make the current root a child of s
			//s.childNodes.add(r);
			r.setParentPointer(s.getIndex());
			r.setIsRoot(false);
			splitChild(s, r.getIndex(), r); // split the node "root"

			insertNotFull(s, k);

		} else {
			insertNotFull(r, k);
		}
	}

	/**
	 * 
	 * @param x - Node to enter key value in
	 * @param k - Key value to enter into node x
	 */
	public void insertNotFull(BTreeNode x, TreeObject k) {
		// TODO - Add unimplemented method
		int i = x.getKeyCount() - 1; // start at the right most key in node x
		if (x.isLeaf()) {
			while (i >= 0 && k.compareTo(x.getKey(i)) <= 0) // find the correct position to insert k
			{
				if (k.compareTo(x.getKey(i)) == 0) {
					x.getKey(i).incrementDuplicates();
					rw.diskWrite(x);
					return;
				}
				i--;
			}
			x.insertKey(i + 1, k); // insert k at index i
			rw.diskWrite(x); // write to disk, done.
		} else {
			while (i >= 0 && k.compareTo(x.getKey(i)) <= 0) { // find the correct position to insert k
				if (k.compareTo(x.getKey(i)) == 0) {
					x.getKey(i).incrementDuplicates();
					rw.diskWrite(x);
					return;
				}
				i--; // line 10 - in class b-tree pseudo-code
			}
			i++;// line 11 - in class b-tree pseudo-code
			BTreeNode c = rw.diskRead(x.getChildPointer(i)); // read child node from disk at offset i
			if (c.isFull()) { // if node is full
				splitChild(x, i, c); // split node
				if (k.compareTo(x.getKey(i)) == 0) { // find the correct position to insert k
					x.getKey(i).incrementDuplicates();
					rw.diskWrite(x);
					return;
				}
				insertNotFull(c, k);
			}
		}
	}

// todo
//	private int allocateNode(){
//	}
	
	/** THIS METHOD IS A MODIFIED METHOD BY JEFF
	 * 
	 * @param x - BTreeNode (parent)
	 * @param y - BTreeNode to split (child)
	 */
	public void splitChild(BTreeNode x, int index, BTreeNode y) {
		// allocate the new B-Tree
		BTreeNode zRightNode = new BTreeNode(degree, false, true, fileName, 4);
		nodeCount++;

		// need to pulls full child
		zRightNode.setIsLeaf(y.isLeaf());

		x.setParentPointer(y.getParentPointer());

		// add y's node's second half to zRightNode node and reindex
		for (int j = y.keys.size() - 1; j > degree - 1; j--) {
			// add y's node's second half to zRightNode node and reindex
			zRightNode.keys.add(0, y.keys.remove(j));
			// update number of keys for zRightNode and y - Array List updates
		}
		// checking if y is a leaf
		if (y.isLeaf() != true) {

			// reindexing y's children to zRightNode's children
			for (int j = y.childPointers.size() - 1; j >= degree; j--) {
				x.childPointers.add(0, y.childPointers.remove(j));
			}
		}
//		// move the x node child pointers to add the zRight node
//		for (int j = x.getKeyCount() - 1; j > index; j--) {
//			x.childPointers.add(0, x.getChildPointer(j));
//		}

		// create a spot for middle object to go up
		for (int j = x.getKeyCount() - 1; j >= index; j--) {
			x.keys.set(j, x.getKey(j));
		}
		// add zRightNode as a child for parent node
		x.childPointers.add(index + 1, zRightNode.byteOffSet);
		//x.childNodes.add(zRightNode);
		zRightNode.setParentPointer(x.byteOffSet);
		x.keys.add(index, y.keys.remove(degree - 1));

//		// moving middle objects from y node to x node
//		x.setKey(index, y.getKey(degree - 1));
//		x.setKeyCount(x.getKeyCount() + 1);

		for (int j = y.getKeyCount(); j > degree; j--) {
			y.removekey(j);
		}

		if (!y.isLeaf()) {
			for (int j = y.getNumChildPtrs(); j > degree; j--) {
				y.removeChild(j);
			}
		}
		// y.removekey(degree);
		// update obj counts
		x.setIsLeaf(false);
		zRightNode.setKeyCount(degree - 1);
		y.setKeyCount(degree - 1);

		// disk write for y
		rw.diskWrite(y);
		// disk write for zRightNode
		rw.diskWrite(zRightNode);
		// disk write for x
		rw.diskWrite(x);

	}
	
	/**
//	 * @param x - BTreeNode (parent)
//	 * @param y - BTreeNode to split (child)
//	 */
//	public void splitChild(BTreeNode x, int index, BTreeNode y) {
//		// allocate the  new B-Tree
//		BTreeNode zRightNode = new BTreeNode(degree,false, true, fileName, 4);
//
//		// need to pulls full child
//		zRightNode.setIsLeaf(y.isLeaf());
//
//		x.setParentPointer(y.getParentPointer());
//
//		// add y's node's second half to zRightNode node and reindex
//		for (int j = 1; j < degree ; j++) {
//			// add y's node's second half to zRightNode node and reindex
//			zRightNode.keys.add(j, y.getKey(j + degree));
//			// update number of keys for zRightNode and y - Array List updates
//		}
//		// checking if y is a leaf
//		if (y.isLeaf() != true) {
//
//			// reindexing y's children to zRightNode's children
//			for (int j = 1; j <= degree; j++) {
//				x.setChildPointer(j, x.getChildPointer(j + degree));
//			}
//		}
//		// move the x node child pointers to add the zRight node
//		for(int j = x.getKeyCount() +1; j> index; j--){
//			x.setChildPointer(j+1,x.getChildPointer(j));
//		}
//		//add zRightNode as a child for parent node
//		x.setChildPointer(index + 1, zRightNode.byteOffSet);
//
//		// create a spot for middle object to go up
//		for(int j = x.getKeyCount(); j >= index;j--){
//			x.setKey(j+1, x.getKey(j));
//		}
//
//		// moving middle objects from y node to x node
//		x.setKey(index, y.getKey(degree));
//		x.setKeyCount(x.getKeyCount()+1);
//
//		for(int j = y.getKeyCount(); j > degree; j--){
//			y.removekey(j);
//		}
//
//		if(!y.isLeaf()){
//			for(int j = y.getNumChildPtrs(); j > degree; j--){
//				y.removeChild(j);
//			}
//		}
//		y.removekey(degree);
//		// update obj counts
//		x.setIsLeaf(false);
//		zRightNode.setKeyCount(degree -1);
//		y.setKeyCount(degree -1);
//
//		// disk write for y
//		rw.diskWrite(y);
//		// disk write for zRightNode
//		rw.diskWrite(zRightNode);
//		// disk write for x
//		rw.diskWrite(x);
//
//	}

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

	public int getSequenceLength() {
		return this.seqLength;
	}

	/**
	 * 
	 * @return - The optimal degree of the BTree
	 */
	public int calculateDegree() {
		int result = 0;
		int foundDegree = 0;
		int blockSize = 4096;
		while(result < blockSize)
		{
			int metaData = 48;
			int keys = 12 * (2 * foundDegree - 1);
			int children = 4 * ((2 * foundDegree - 1) - 1);
			int nodes = 48 * children;
			result = (metaData + keys + children + nodes);
			foundDegree++;
		}
		return foundDegree;
	}

	public String toString() {
		StringBuilder sb = new StringBuilder();
		sb.append(root.toString()).append("root\n\n");
		for (int i = 0; i < root.childPointers.size(); i++) {
			sb.append(rw.diskRead(root.childPointers.get(i)).toString()).append("child " + i + "\n\n");
		}
		return sb.toString();
	}

	/**
	 * DO NOT USE - Gives a stack overflow error for the time being
	 * 
	 * @param t - BTree Node to print
	 * @return
	 */
//	public String printTree(BTreeNode t) {
//		StringBuilder sb = new StringBuilder();
//		sb.append(t.toString()).append("root\n\n");
//		for (int i = 0; i < t.childNodes.size(); i++) {
//			BTreeNode next = t.childNodes.get(i);
//			sb.append(next.toString()).append("child " + i + "\n\n");
//			if (next.childNodes.size() != 0) {
//				sb.append(printTree(next));
//			}
//		}
//		return sb.toString();
//	}

}
