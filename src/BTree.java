import java.io.BufferedWriter;
import java.io.File;
import java.io.FileOutputStream;
import java.io.IOException;
import java.io.OutputStreamWriter;
import java.util.ArrayList;
import java.util.Stack;

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
	private ArrayList<Integer> nodeIndexes;

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
		nodeCount = -1;
		File file = new File(fileName);
		this.degree = degree;
		this.fileName = fileName;
		this.seqLength = seqLength;
		this.cacheSize = cacheSize;
		this.debugLevel = debugLevel;
		rw = new BTreeRW(fileName, cacheSize, seqLength);
		this.root = new BTreeNode(0, degree, true, true);
		nodeCount++;
		this.height = 0;
		nodeIndexes = new ArrayList<Integer>();
	}

	/**
	 * Secondary constructor - Read and construct a BTree from file
	 * 
	 * @param            //file - file from which to read the tree from

	 * @param cacheSize  - size of the cache. The bigger the cache, the faster the
	 *                   program will run.
	 * @param debugLevel - default value is 0. if debug level is 0 Any diagnostic
	 *                   messages, help and status messages must be be printed on
	 *                   standard error stream. If it is 1 the program writes a text
	 *                   file named dump. The dump file contains DNA string
	 *                   (corresponding to the key stored) and frequency in an in
	 *                   order traversal.
	 */
	public BTree(File file, int cacheSize, int debugLevel) {
		this.fileName = file.getName();
		this.cacheSize = cacheSize;
		this.debugLevel = debugLevel;
		rw = new BTreeRW(fileName, cacheSize, this);
	}

	public void writeRootToFile() {
		rw.writeMetaData(this);
	}

	public void setRoot(BTreeNode n) {
		this.root = n;
	}

	/**
	 * 
	 * @param k - TreeObject to insert
	 */
	public void insert(TreeObject k) {
		// TODO - Add unimplemented method
		BTreeNode r = root;
		if (r.isFull()) {
			BTreeNode s = new BTreeNode(0, degree, true, false); // Allocate new node
			nodeCount++;
			root = s; // make s the new root
			s.setIsLeaf(false);

			// s.childNodes.add(r);
			r.setParentPointer(s.getIndex());
			r.setIsRoot(false);
			r.setIndex(nodeCount);
			s.childPointers.add(0, r.getIndex()); // make the current root a child of s
			nodeIndexes.add(r.getIndex());
			splitChild(s, 0, r); // split the node "root"

			insertNotFull(s, k);
			height++;

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
		if (x.isLeaf()) {
			while (i >= 0 && key <= x.getKey(i).getKey()) // find the correct position to insert k
			{
				if (key == x.getKey(i).getKey()) {
					x.getKey(i).incrementDuplicates();
					rw.diskWrite(x);
					return;
				}
				i--;
			}
			x.insertKey(i + 1, k); // insert k at index i
			rw.diskWrite(x); // write to disk, done.

		} else {
			while (i >= 0 && key <= x.getKey(i).getKey()) { // find the correct position to insert k
				if (key == x.getKey(i).getKey()) {
					x.getKey(i).incrementDuplicates();
					rw.diskWrite(x);
					return;
				}
				i--;
			}
			i++; // line 11 - in class b-tree pseudo-code
			BTreeNode c = rw.diskRead(x.getChildPointer(i), this.degree); // read child node from disk at offset i
			for (int j = 0; j < c.keys.size(); j++) {
				if (key == c.getKey(j).getKey()) {
					c.getKey(j).incrementDuplicates();
					rw.diskWrite(c);
					return;
				}
			}
			if (c.isFull()) { // if node is full
				splitChild(x, i, c); // split node
				if (key > x.keys.get(i).getKey()) {

					i++;
				}
				c = rw.diskRead(x.getChildPointer(i), this.degree);

			}
			insertNotFull(c, k);
		}

	}

	/**
	 * THIS METHOD IS A MODIFIED METHOD BY JEFF
	 * 
	 * @param x - BTreeNode (parent)
	 * @param y - BTreeNode to split (child)
	 */
	public void splitChild(BTreeNode x, int index, BTreeNode y) {

		BTreeNode zRightNode = new BTreeNode(nodeCount + 1, degree, false, y.isLeaf()); // allocate the new B-Tree node
		nodeCount++;
		zRightNode.setParentPointer(y.getParentPointer());
		int count = 0; // used to count the number of keys & children to remove from node y
		for (int j = 0; j < degree - 1; j++) {
			zRightNode.insertKey(j, y.keys.get(j + degree)); // add last half of y's keys to zRightNode
			count++;
		}
		while (count > 0) {
			y.keys.remove(y.keys.size() - 1); // remove last half of y's keys
			count--;
		}
		// checking if y is a leaf
		if (y.isLeaf() != true) {
			count = 0; // make sure count is reset for child pointers
			for (int j = 0; j < degree; j++) {
				zRightNode.childPointers.add(j, y.childPointers.get(j + degree)); // add y's child pointers
				count++;
			}
			while (count > 0) {
				y.childPointers.remove(y.childPointers.size() - 1); // remove last half of y's child pointers
				count--;
			}
		}

		x.childPointers.add(index + 1, zRightNode.getIndex()); // Make zRightNode a child of x
		nodeIndexes.add(index + 1, zRightNode.getIndex());
		zRightNode.setParentPointer(x.getIndex()); // make x the parent of zRightNode
		x.insertKey(index, y.keys.remove(degree - 1)); // Move the appropriate key up to x
		// disk write for y
		rw.diskWrite(y);
		// disk write for zRightNode
		rw.diskWrite(zRightNode);
		// disk write for x
		rw.diskWrite(x);

	}

	/**
	 * Basic
	 * 
	 * @param n - Node to start search from
	 * @param k - Key value to search for
	 * @return - Node containing key value, null if not found
	 */
	public BTreeNode search(BTreeNode n, long k) {
		int i = 0;
		long val = 0;

		// Loop through the keys in the node until the end is reached or key found is
		while (i < n.keys.size() && k > n.keys.get(i).getKey()) {

			i++;
			val = n.keys.get(i).getKey();
		}

		if (k == val) {
			return n;
		}

		if (n.isLeaf()) {
			return null;
		}

		return rw.diskRead(n.getChildPointer(i), degree);
	}

	public int getDegree() {
		return this.degree;
	}
	
	public void setDegree(int degree)
	{
		this.degree = degree;
	}
	
	public void setSeqLength(int seq)
	{
		this.seqLength = seq;
	}

	/**
	 * 
	 * @return - height of the tree
	 */
	public int getHeight() {
		return this.height;
	}
	
	public void setHeight(int height)
	{
		this.height = height;
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

			int keys = 18 * (2 * foundDegree - 1);
			int children = 4 * keys + 1;
			int nodes = (18 + 8 * (2 * degree - 1)) + 4 * (2 * degree) * children;
			int metaData = 12 + (18 + 8 * (2 * degree - 1)) + 4 * (2 * degree);
			result = (metaData + keys + children + nodes);
			foundDegree++;
		}
		return foundDegree;
	}

	public String toString() {
		StringBuilder sb = new StringBuilder();
		BTreeNode start = rw.diskRead(0, degree);
		sb.append("______\n\n").append(start.toString()).append("______\n" + "ROOT\n");
		for (Integer i : nodeIndexes) {
			start = rw.diskRead(i, degree);
			sb.append("______\n\n").append(start.toString()).append("______\n" + "Node: " + start.getIndex() + "\n");
		}
		return sb.toString();
	}

	/**
	 * writes a text file named dump, that has the following line format: DNA
	 * string: frequency.
	 * 
	 * @throws IOException
	 */
	public void writeTreeDump() throws IOException {
		BTreeNode temp = root; // Temporary node to iterate with
		Stack<Integer> childPointers = new Stack<>(); // stack to hold childPointers
		Stack<Integer> indexes = new Stack<>(); // stack to hold key indexes
		OutputStreamWriter writer = new OutputStreamWriter(new FileOutputStream("dump"), "UTF-8");
		BufferedWriter bufWriter = new BufferedWriter(writer);
		int keyIndex = 0;
		while (true) {
			if (temp.isLeaf()) {
				for (int i = 0; i < temp.keys.size(); i++) {
					bufWriter.write(temp.keys.get(i).toString() + "\n"); // Write all keys of current node to file
				}

				if (temp.isRoot()) // If this is the case, the root is the only node in the tree
					break; // done, break out of the loop.
				temp = this.rw.diskRead(childPointers.pop(), degree); // go to previous node in the tree
				keyIndex = indexes.pop();
				if (keyIndex < temp.keys.size()) {
					bufWriter.write(temp.keys.get(keyIndex).toString() + "\n");
				}
				keyIndex++;
			} else if (!temp.isLeaf() && keyIndex == temp.childPointers.size()) { // if node is an internal node and
																					// there are no more child pointers
																					// left
				if (childPointers.isEmpty() && childPointers.isEmpty()) { // if both stacks are empty
					break; // done, break out of the loop.
				} else {
					temp = this.rw.diskRead(childPointers.pop(), degree); // go to previous node in the tree
					keyIndex = indexes.pop();

					if (keyIndex < temp.keys.size()) {
						bufWriter.write(temp.keys.get(keyIndex).toString() + "\n");
					}
					keyIndex++;
				}
			} else { // none of the previous if statements were true so push node and key index to stack
				indexes.push(keyIndex);
				childPointers.push(temp.getIndex());
				temp = rw.diskRead(temp.childPointers.get(keyIndex), degree); // go to next node
				keyIndex = 0; // reset key index
			}

		}
		bufWriter.close();
	}

}
