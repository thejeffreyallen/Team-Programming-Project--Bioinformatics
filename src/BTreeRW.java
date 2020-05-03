
import java.io.*;

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
			randFile = new RandomAccessFile(fileName, "rw");
		} catch (FileNotFoundException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}
		cache = new <BTreeNode>Cache(cacheSize);
	}

	/** Writes metaData to file
	 * 
	 * @param tree - tree metaData to write
	 */
	public void writeMetaData(BTree tree) {
		try {
			randFile.seek(0L);
			randFile.writeInt(tree.getSequenceLength());
			randFile.writeInt(tree.getHeight());
			randFile.writeInt(tree.getRoot().getDegree());
			diskWrite(tree.getRoot());
		} catch (IOException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}

	}

	/**
	 * Total size of a node n on disk
	 * 
	 * @param n - node to calculate size
	 * @return
	 */
	public int nodeSizeOnDisk(BTreeNode n) {
		return 24 + (8 * n.keys.size()) + (4 * (n.keys.size()) + 1);
	}

	/**
	 *  Jeff's method to write the metadata of a BTreeNode to a file
	 * 
	 * @param n the BTreeNode to write to a disk
	 */
	public void diskWrite(BTreeNode n) {
		if (n != null) {
			try {

				// seek to node n offset
				randFile.seek(getOffset(n.getIndex(), n.getDegree()));
				
				// Write bytes to file 
				randFile.writeInt(n.getIndex());
				randFile.writeBoolean(n.isLeaf());
				randFile.writeBoolean(n.isRoot());
				randFile.writeInt(n.getParentPointer());
				randFile.writeInt(n.keys.size());
				randFile.writeInt(n.childPointers.size());
				
				// write the same amount of data regardless of children / keys for easy calculation of offset
				for (int i = 0; i < (2 * n.getDegree() - 1) + 1; i++) {
					if (i < n.childPointers.size()) {
						randFile.writeInt(n.childPointers.get(i));
					} else {
						randFile.writeInt(-1);
					}
					
				}
				for (int j = 0; j < 2 * n.getDegree() - 1; j++) {
					if (j < n.keys.size()) {
						randFile.writeLong(n.keys.get(j).getKey());
					} else {
						randFile.writeLong(-1L);
					}
				}

			} catch (IOException e) {
				// TODO Auto-generated catch block
				e.printStackTrace();
			}
		}
	}

	/** Jeff's method to read and return a node from disk
	 * 
	 * @param index - index of node. Offset will be calculated from this.
	 * @param degree - degree of tree. Offset calculation also depends on this.
	 * @return
	 */
	public BTreeNode diskRead(int index, int degree) {
		BTreeNode newNode = new BTreeNode(index, degree, false, false);

		try {
			randFile.seek(getOffset(index, degree));
			newNode.setIndex(randFile.readInt());
			newNode.setIsLeaf(randFile.readBoolean());
			newNode.setIsRoot(randFile.readBoolean());
			newNode.setParentPointer(randFile.readInt());
			int numKeys = randFile.readInt();
			int numChildPointers = randFile.readInt();
			TreeObject tempObject = new TreeObject(0L, seqLength);
			for (int j = 0; j < numKeys; j++) {
				newNode.addKey(tempObject);
			}
			for (int i = 0; i < (2 * degree - 1) + 1; i++) {
				if (i < numChildPointers) {
					newNode.addChild(randFile.readInt());
				} else {
					randFile.readInt();
				}
			}

			for (int j = 0; j < 2 * degree - 1; j++) {
				if (j < numKeys) {
					newNode.keys.get(j).setData(randFile.readLong());
				} else {
					randFile.readLong();
				}
			}

		} catch (IOException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}

		return newNode;
	}

	public int nodeSizeOnDisk(int degree) {
		return (28 + (12 * (2 * degree - 1)) + (4 * (2 * degree - 1) + 1));
	}

	public int getOffset(int index, int degree) {
		return 12 + nodeSizeOnDisk(degree) + index * nodeSizeOnDisk(degree);
	}
	
//	/**
//	 * Writes the metadata of a BTreeNode to a file
//	 * 
//	 * @param n the BTreeNode to write to a disk
//	 */
//	public void diskWrite(BTreeNode n, int offset) {
//		// cache.addObject(n);
//		// cache.addObject(n);
//		int keyCount = n.keys.size();
//		int maxKeys = n.getMaxKeys();
//		if (n != null) {
//			try {
//
//				randFile.seek(offset);
//				randFile.writeInt(n.getIndex());
//				randFile.writeInt((n.getMaxKeys() + 1) / 2);
//				randFile.writeBoolean(n.isLeaf());
//				randFile.writeBoolean(n.isRoot());
//				randFile.writeInt(n.getParentPointer());
//				randFile.writeInt(keyCount);
//				int i;
//				for (i = 0; i < maxKeys; i++) {
//					if (i < keyCount && !n.isLeaf()) {
//						randFile.writeInt(n.getChildPointer(i));
//					} else if (i >= keyCount || n.isLeaf()) {
//						randFile.seek(randFile.getFilePointer() + 4);
//					}
//					if (i < keyCount) {
//						randFile.writeLong(n.getKey(i).getKey());
//						randFile.writeInt(n.getKey(i).getDuplicates());
//					}
//				}
//				if (!n.isLeaf() && i < keyCount + 1) {
//					randFile.writeInt(n.getChildPointer(i));
//				}
//
//			} catch (IOException e) {
//				// TODO Auto-generated catch block
//				e.printStackTrace();
//			}
//		}
//	}

//	/**
//	 * reads and returns a BTreeNode from a disk
//	 */
//	public BTreeNode diskRead(int offset) {
//		BTreeNode newNode = null;
//		/*
//		 * if(cache!=null){ newNode = cache.getAtIndex(pointer); } if(newNode!=null){
//		 * return newNode; }
//		 */
//		try {
//			randFile.seek(offset);
//			newNode = new BTreeNode(randFile.readInt(), randFile.readInt(), randFile.readBoolean(),
//					randFile.readBoolean());
//			newNode.setParentPointer(randFile.readInt());
//			int keyCount = randFile.readInt();
//			int maxDegree = newNode.getMaxKeys();
//
//			for (int i = 0; i < maxDegree; i++) {
//				if (i < keyCount && !newNode.isLeaf()) {
//					newNode.addChild(randFile.readInt());
//				} else if (i >= keyCount || newNode.isLeaf()) {
//					randFile.seek(randFile.getFilePointer() + 4);
//				}
//				if (i < keyCount) {
//					TreeObject t = new TreeObject(randFile.readLong(), seqLength);
//					t.setDuplicates(randFile.readInt());
//					newNode.addKey(t);
//				}
//			}
////			if(!newNode.isLeaf()){
////				newNode.addChild(randFile.readInt());
////			}
//		} catch (IOException e) {
//			// TODO Auto-generated catch block
//			e.printStackTrace();
//		}
//
//		return newNode;
//
//	}

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
