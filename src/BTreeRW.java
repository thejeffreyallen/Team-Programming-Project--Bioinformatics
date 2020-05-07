
import java.io.*;
import java.nio.ByteBuffer;

/**
 * 
 * @author AndyBreland, Jeff Allen
 * 
 *         Class that reads and writes to disk.
 *
 */
public class BTreeRW {

	private RandomAccessFile randFile;
	private Cache<BTreeNode> cache;
	private String fileName;
	private int cacheSize;
	private int seqLength;
	private int debugLevel;

	/**
	 * Constructor
	 * 
	 * @param fileName  the name of the random access file
	 * @param cacheSize the size of the cache
	 */
	public BTreeRW(String fileName, int cacheSize, int seqLength) {
		this.fileName = fileName;
		this.cacheSize = cacheSize;
		this.seqLength = seqLength;

		try {
			randFile = new RandomAccessFile(fileName, "rwd");
		} catch (FileNotFoundException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}
		cache = new Cache<BTreeNode>(cacheSize);
	}

	public BTreeRW(String fileName, int cacheSize, BTree tree) {
		this.fileName = fileName;
		this.cacheSize = cacheSize;
		try {
			randFile = new RandomAccessFile(fileName, "rwd");
		} catch (FileNotFoundException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}
		cache = new Cache<BTreeNode>(cacheSize);
		readMetaData(tree);
	}

	/**
	 * Writes metaData to file
	 * 
	 * @param tree - tree metaData to write
	 */
	public void writeMetaData(BTree tree) {
		try {
			randFile.seek(0); // Start at beginning of file

			// Write B-tree meta data
			randFile.writeInt(tree.getDegree());
			randFile.writeInt(tree.getSequenceLength());
			randFile.writeInt(tree.getHeight());
			randFile.seek(12);

			// Write root directly after B-tree meta data
			diskWrite(tree.getRoot());
		} catch (IOException e) {
			System.err.println("An error occured when attempting to write BTree meta data");
			e.printStackTrace();
		}

	}

	public void readMetaData(BTree tree) {
		try {
			randFile.seek(0); // Start at beginning of file
			// Read B-tree meta data
			int foundDegree = randFile.readInt();
			this.seqLength = randFile.readInt();
			int height = randFile.readInt();
			
			tree.setDegree(foundDegree);
			tree.setHeight(height);
			tree.setSeqLength(seqLength);
			tree.setRoot(diskRead(0, foundDegree));
			
		} catch (IOException e) {
			System.err.println("An error occured when attempting to read BTree meta data");
			e.printStackTrace();
		}
	}

	/**
	 * Method to write the metadata of a BTreeNode to a file
	 * 
	 * @param n the BTreeNode to write to disk
	 */
	public void diskWrite(BTreeNode n) {
		if (n != null) {

			if (cacheSize > 0) {
				cache.addObject(n);
			}

			try {
				if (n.isRoot())
					randFile.seek(12); // Offset for root is total size of tree meta data 4 * 4 * 4 = 12 bytes
				else
					randFile.seek(getOffset(n.getIndex(), n.getDegree())); // Calculate node offset using node index

				// Write meta data for node
				randFile.writeInt(n.getIndex()); // 4 bytes
				randFile.writeBoolean(n.isLeaf()); // 1 byte
				randFile.writeBoolean(n.isRoot()); // 1 byte
				randFile.writeInt(n.getParentPointer()); // 4 bytes
				randFile.writeInt(n.keys.size()); // 4 bytes
				randFile.writeInt(n.childPointers.size()); // 4 bytes

				// write the same amount of data regardless of children / keys for easy
				// calculation of offset
				for (int i = 0; i < (2 * n.getDegree()); i++) {
					if (i < n.childPointers.size()) {
						randFile.writeInt(n.childPointers.get(i)); // 4 bytes * (2*degree)
					} else {
						randFile.seek(randFile.getFilePointer() + 4); // skip ahead in file 4 bytes in order to keep
																		// node size the same regardless of child / key
																		// size
					}

				}
				// Write keys and duplicate counts
				for (int j = 0; j < 2 * n.getDegree() - 1; j++) { // (8 + 4) bytes * (2*degree-1)
					if (j < n.keys.size()) {
						randFile.writeLong(n.keys.get(j).getKey());
						randFile.writeInt(n.keys.get(j).getDuplicates());
					} else {
						randFile.seek(randFile.getFilePointer() + 12); // skip ahead in file 12 bytes in order to keep
																		// node size the same regardless of child / key
																		// size

					}
				}
			} catch (IOException e) {
				System.err.println("An error occured when attempting to write data at node " + n.getIndex());
				e.printStackTrace();
			}
		}

	}

	/**
	 * Method to read and return a node from disk
	 * 
	 * @param index  - index of node. Offset will be calculated from this.
	 * @param degree - degree of tree. Offset calculation also depends on this.
	 * @return
	 */
	public BTreeNode diskRead(int index, int degree) {
		BTreeNode newNode = null;

		BTreeNode newNode;

		if (cacheSize > 0) {
			newNode = readNode(index);
			if (newNode != null) {
				return newNode;
			}
		}
		newNode = new BTreeNode(index, degree, false, false);

		try {
			if (degree == 0) {
				randFile.seek(0);
				int findDegree = randFile.readInt();
				int seq = randFile.readInt();
				newNode = new BTreeNode(index, findDegree, true, false);
			} else
				newNode = new BTreeNode(index, degree, false, false);
			if (index > 0) { // Check if the node is not root
				randFile.seek(getOffset(index, degree)); // Calculate node offset using node index
			} else {
				randFile.seek(12); // Offset for root is total size of tree meta data 4 * 4 * 4 = 12 bytes
			}

			// randFile.readUTF(); // for debugging
			// Read node meta data
			newNode.setIndex(randFile.readInt());
			newNode.setIsLeaf(randFile.readBoolean());
			newNode.setIsRoot(randFile.readBoolean());
			newNode.setParentPointer(randFile.readInt());
			int numKeys = randFile.readInt();
			int numChildPointers = randFile.readInt();

			// Assign empty keys to be populated in the lowest for loop
			for (int j = 0; j < numKeys; j++) {
				newNode.keys.add(j, new TreeObject(0L, seqLength));
			}

			// Read and assign child pointers
			for (int i = 0; i < (2 * degree - 1) + 1; i++) {
				if (i < numChildPointers) {
					newNode.addChild(randFile.readInt());
				} else {
					randFile.seek(randFile.getFilePointer() + 4);
				}
			}

			// Read and assign key values to the empty keys
			for (int j = 0; j < 2 * degree - 1; j++) {
				if (j < numKeys) {
					newNode.keys.get(j).setData(randFile.readLong());
					newNode.keys.get(j).setDuplicates(randFile.readInt());
				} else {
					randFile.seek(randFile.getFilePointer() + 12);
				}
			}

		} catch (

		IOException e) {
			System.err.println("An error occured when attempting to read data at node " + index
					+ ". Offset in file is: " + getOffset(index, degree));
			e.printStackTrace();
		}
		if (newNode.keys.size() == 0)
			return null;
//		System.out.println("------");
//		System.out.println(newNode.toString());
//		System.out.println("------");


		if (cacheSize > 0) {
			cache.addObject(newNode);
		}
		return newNode;
	}

	public int nodeSizeOnDisk(int degree) {
		return (18 + 12 * (2 * degree - 1) + 4 * (2 * degree));
	}

	public int getOffset(int index, int degree) {
		return 12 + nodeSizeOnDisk(degree) + (index - 1) * nodeSizeOnDisk(degree);
	}

	public BTreeNode readNode(int index) {
		for (int i = 0; i < cache.getSize(); i++) {
			BTreeNode n = cache.getAtIndex(index);
			if (n.getIndex() == index) {
				cache.removeObject(n);
				cache.addObject(n);
				return n;
			}

		}
		return null;
	}

	private int parent(int i) {
		int p = i / 2;
		return p;
	}

	private int left(int i) {
		return 2 * i;
	}

	private int right(int i) {
		return (2 * i) + 1;
	}

}
