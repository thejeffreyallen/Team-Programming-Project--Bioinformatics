import java.util.ArrayList;

/**
 * 
 * @author Jeff Allen
 * 
 *         BTree Node Class to define a node and manage the data within.
 *
 */

public class BTreeNode implements Comparable<BTreeNode> {

	private int degree, numKeys, maxKeys, index, parent, nodeLocation; //is nodeLocation not the same as either index or offset?
	int byteOffSet; // points to the first byte of the node
	private boolean isLeaf, isRoot;
	ArrayList<TreeObject> keys;
	ArrayList<Integer> childPointers;

	/**
	 * Constructor
	 * 
	 * @param index  - index of this node within the b-tree
	 * @param degree - degree of b-tree
	 * @param isRoot - indicates whether the node is the root or not
	 * @param isLeaf - indicates whether the node has any children.
	 */
	public BTreeNode(int index, int degree, boolean isRoot, boolean isLeaf) {
		this.degree = degree;
		this.index = index;
		this.isRoot = isRoot;
		this.isLeaf = isLeaf;
		this.maxKeys = (2 * degree) - 1; // max 2t-1 keys for degree t
		this.nodeLocation = 0;
		keys = new ArrayList<TreeObject>(maxKeys);
		childPointers = new ArrayList<Integer>(maxKeys + 1);
		numKeys = 0;
		parent = -1;
	}

	public BTreeNode(int degree, boolean isRoot, boolean isLeaf, String filename, int byteOffSet) {
		this.degree = degree;
		this.isRoot = isRoot;
		this.isLeaf = isLeaf;
		this.maxKeys = (2 * degree) - 1; // max 2t-1 keys for degree t
		keys = new ArrayList<TreeObject>(maxKeys);
		childPointers = new ArrayList<Integer>(maxKeys + 1);
		numKeys = 0;
		parent = -1;
		this.byteOffSet = byteOffSet;
	}
	
	public int getDegree()
	{
		return this.degree;
	}
	
	public void setIndex(int i)
	{
		this.index = i;
	}
	

	/**
	 * Return the number of keys stored in node
	 * 
	 * @return key count - number of keys stored in the node
	 */
	public int getKeyCount() {
		return this.keys.size();
	}

	/**
	 * Set a parent pointer
	 *
	 * @param pointer - int pointer of the parent
	 */
	public void setParentPointer(int pointer) {
		this.parent = pointer;
	}

	/**
	 * get a parent pointer
	 *
	 * @return pointer - int pointer of the parent
	 */
	public int getParentPointer() {

		return this.parent;
	}

	/**
	 * Adds a child pointer to the list of pointers
	 * 
	 * @param i - index of child to add
	 */
	public void addChild(int i) {
		this.childPointers.add(i);
	}

	public void removeChild(int i) {
		this.childPointers.remove(i);
	}

	public void insertKey(int index, TreeObject k) {
		this.keys.add(index, k);
	}

	public void removekey(int i) {
		keys.remove(i);
	}

	/**
	 * Sets a child pointer to the list of pointers
	 *
	 * @param index   - index of child to set
	 * @param pointer - pointer of child to set
	 */
	public void setChildPointer(int index, int pointer) {
		this.childPointers.set(index, pointer);
	}

	/**
	 * returns a child pointer from the list of pointers
	 *
	 * @return index - index of child at an index
	 */
	public int getChildPointer(int i) {
		return childPointers.get(i).intValue();
	}

	public int getNumChildPtrs() {
		return childPointers.size() - 1;
	}

	/**
	 * returns a children from the list of pointers
	 *
	 * @return list - list of child pointers
	 */
	public ArrayList getChildren() {
		ArrayList<Integer> childPointers = this.childPointers;
		return childPointers;
	}

	/**
	 * Add a key to the list of keys
	 * 
	 * @param o - TreeObject (key) to add
	 */
	public void addKey(TreeObject o) {
		this.keys.add(o);
	}

	/**
	 * Set a key in specific index the list
	 *
	 * @param obj - TreeObject (key) to add
	 * @param i   - int (i) to add index
	 */
	public void setKey(int i, TreeObject obj) {
		keys.set(i, obj);
	}

	/**
	 * Return a key in an index
	 *
	 * @return index - return the key
	 */
	public TreeObject getKey(int index) {
		TreeObject obj = keys.get(index);
		return obj;
	}

	/**
	 * Get the index of this node in the b-tree
	 * 
	 * @return - index
	 */
	public int getIndex() {
		return this.index;
	}

	/**
	 * Find out if the node is full or not
	 * 
	 * @return - True if full, false otherwise
	 */
	public boolean isFull() {
		return keys.size() == 2*degree-1;
	}

	/**
	 * Get the maximum number of keys the node can hold.
	 * 
	 * @return - maxKeys
	 */
	public int getMaxKeys() {
		return this.maxKeys;
	}

	/**
	 * Find out if the node is the root of the b-tree
	 * 
	 * @return - true if the node is the root, false otherwise
	 */
	public boolean isRoot() {
		return this.isRoot;
	}

	/**
	 * Find out if the node has any children
	 * 
	 * @return - true if the node has no children, false otherwise
	 */
	public boolean isLeaf() {
		return this.isLeaf;
	}

	/**
	 * Set this node as the root of the B-tree
	 * 
	 * @param val - Pass in true to set as root, false otherwise
	 */
	public void setIsRoot(boolean val) {
		this.isRoot = val;
	}

	/**
	 * Set this node as a leaf node
	 * 
	 * @param val - Pass in true to set as leaf node, false otherwise
	 */
	public void setIsLeaf(boolean val) {
		this.isLeaf = val;
	}

	/**
	 * Set the location of the node in memory
	 * 
	 * @param i the location of the node i memory
	 */
	public void setLocation(int i){
		this.nodeLocation = i;
	}
	/**
	 * 
	 * @return the location of the node
	 */
	public int getLocation(){
		return nodeLocation;
	}

	public String toString() {
		StringBuilder sb = new StringBuilder();
		for (int i = 0; i < keys.size(); i++) {
			sb.append(keys.get(i).toString()).append("\n");
		}
		return sb.toString();
	}

	@Override
	public int compareTo(BTreeNode o) {
		if (index > o.getIndex()) {
			return -1;
		} else if (index < o.getIndex()) {
			return 1;
		}
		return 0;
	}
}
