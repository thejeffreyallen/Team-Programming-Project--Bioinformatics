import java.io.File;

/**
 * BTree class for creating and managing a BTree
 * 
 * @author Jeff Allen, Abel Almedia, Andy Breland
 *
 */
public class BTree {

	private BTreeNode root;
	private int height, degree, seqLength, debugLevel, cacheSize, nodeCount; // 4 bytes each
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
		this.root = new BTreeNode(nodeCount++, degree, true, true);
		rw.writeMetaData(this);

		// rw.writeMetaData(degree, root, seqLength);
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
		this.degree = degree;
		this.fileName = bTreeOnDisk.getName();
		this.seqLength = seqLength;
		this.cacheSize = cacheSize;
		this.debugLevel = debugLevel;
		rw = new BTreeRW(fileName, cacheSize, seqLength);
		// TODO - Add unimplemented code

	}

	/**
	 * 
	 * @param k - TreeObject to insert
	 */
	public void insert(TreeObject k) {
		// TODO - Add unimplemented method
		BTreeNode r = root;
		if (r.isFull()) {
			BTreeNode s = new BTreeNode(nodeCount++, degree, true, false); // Allocate new node

			root = s; // make s the new root
			s.setIsLeaf(false);
			s.childPointers.add(r.getIndex()); // make the current root a child of s
			// s.childNodes.add(r);
			r.setParentPointer(s.getIndex());
			r.setIsRoot(false);

			splitChild(s, 0, r); // split the node "root"

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
		long key = k.getKey();
		boolean foundDup = false;
		if (x.isLeaf()) {
			while (i >= 0 && key <= x.getKey(i).getKey()) // find the correct position to insert k
			{
				if (k.compareTo(x.getKey(i)) == 0) {
					x.keys.get(i).incrementDuplicates();
					foundDup = true;
				}
				i--;
			}
			if (!foundDup) {
				x.keys.add(i + 1, k); // insert k at index i
				rw.diskWrite(x); // write to disk, done.
			}
			
		} else {
			while (i >= 0 && key <= x.getKey(i).getKey()) { // find the correct position to insert k
				if (k.compareTo(x.getKey(i)) == 0) { // find the correct position to insert k
					x.keys.get(i).incrementDuplicates();
				}
				i--;
			}
			i++; // line 11 - in class b-tree pseudo-code
			BTreeNode c = rw.diskRead(x.getChildPointer(i), this.degree); // read child node from disk at offset i
			if (c.isFull()) { // if node is full
				splitChild(x, i, c); // split node
				if (k.compareTo(x.keys.get(i)) == -1) {
					i++;
				}
				c = rw.diskRead(x.getChildPointer(i), this.degree);
				insertNotFull(c, k);
			}
		}

	}

// todo
//	private int allocateNode(){
//	}

	/**
	 * THIS METHOD IS A MODIFIED METHOD BY JEFF
	 * 
	 * @param x - BTreeNode (parent)
	 * @param y - BTreeNode to split (child)
	 */
	public void splitChild(BTreeNode x, int index, BTreeNode y) {

		BTreeNode zRightNode = new BTreeNode(nodeCount++, degree, false, y.isLeaf()); // allocate the new B-Tree node

		zRightNode.setParentPointer(y.getParentPointer());

		for (int j = 0; j < degree - 1; j++) {
			zRightNode.keys.add(y.keys.remove(j + (degree - 1)));
		}
		// checking if y is a leaf
		if (y.isLeaf() != true) {
			for (int j = 0; j < degree; j++) {
				zRightNode.childPointers.add(y.childPointers.remove(j + 1));
			}

		}
		for (int j = x.keys.size(); j > index; j--) {
			x.childPointers.set(j + 1, x.childPointers.remove(j));
		}
		x.childPointers.add(index+1, zRightNode.getIndex());
		for(int j = x.keys.size(); j > index; j--)
		{
			x.keys.set(j + 1, x.keys.remove(j));
		}
		x.keys.add(index, y.keys.remove(degree-1));
		// disk write for y
		rw.diskWrite(y);
		// disk write for zRightNode
		rw.diskWrite(zRightNode);
		// disk write for x
		rw.diskWrite(x);

	}

//	/**
//	 * @param x - BTreeNode (parent)
//	 * @param y - BTreeNode to split (child)
//	 */
//	public void splitChild(BTreeNode x, int index, BTreeNode y) {
//		// allocate the new B-Tree
//		BTreeNode zRightNode = new BTreeNode(nodeCount++, degree, false, false); // Allocate new node
//
//		// need to pulls full child
//		zRightNode.setIsLeaf(y.isLeaf());
//
//		x.setParentPointer(y.getParentPointer());
//
//		// add y's node's second half to zRightNode node and reindex
//		for (int j = y.keys.size() - 1; j >= degree; j--) {
//			// add y's node's second half to zRightNode node and reindex
//			zRightNode.keys.add(0, y.getKey(j));
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
//		for (int j = 0; j < index; j++) {
//			x.childPointers.set(j, x.getChildPointer(j + 1));
//		}
//		// add zRightNode as a child for parent node
//		x.childPointers.add(index + 1, zRightNode.getOffset());
//
//		// create a spot for middle object to go up
//		for (int j = x.getKeyCount() - 1; j >= index; j--) {
//			x.setKey(j, x.getKey(j));
//		}
//
////		// moving middle objects from y node to x node
////		x.setKey(index, y.getKey(degree));
////		x.setKeyCount(x.getKeyCount() + 1);
//
//		for (int j = y.getKeyCount() - 1; j > degree; j--) {
//			y.removekey(j);
//		}
//
//		if (!y.isLeaf()) {
//			for (int j = y.getNumChildPtrs(); j > degree; j--) {
//				y.removeChild(j);
//			}
//		}
//		y.removekey(degree);
//		// update obj counts
//		x.setIsLeaf(false);
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
		while (result < blockSize) {

			int keys = 12 * (2 * foundDegree - 1);
			int children = 4 * keys + 1;
			int nodes = 48 * children;
			int metaData = 24 + keys + children;
			result = (metaData + keys + children + nodes);
			foundDegree++;
		}
		return foundDegree;
	}

	public String toString() {
		StringBuilder sb = new StringBuilder();
		sb.append(root.toString()).append("root\n\n");
		for (int i = 0; i < root.childPointers.size(); i++) {
			sb.append(rw.diskRead(root.getChildPointer(i), degree).toString()).append("child " + i + "\n\n");
		}
		return sb.toString();
	}

	/**
	 * 
	 * 
	 * @param t - BTree Node to print
	 * @return
	 */
	public String printTree(BTreeNode t) {
		StringBuilder sb = new StringBuilder();
		BTreeNode next = null;
		sb.append("______\n\n").append(t.toString()).append("______\n");
		for (int i = 0; i < t.childPointers.size(); i++) {
			next = rw.diskRead(t.childPointers.get(i), degree);
			sb.append(next.toString()).append("______\n\n");
			if (next.isLeaf()) {
				sb.append(printTree(next));
			}
		}

		return sb.toString();
	}
}
