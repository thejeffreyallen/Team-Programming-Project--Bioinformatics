
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
		cache = new <BTreeNode>Cache(cacheSize);
	}

	/**
	 * Writes metaData to file
	 * 
	 * @param tree - tree metaData to write
	 */
	public void writeMetaData(BTree tree) {
		try {
			randFile.seek(0);
			// randFile.writeUTF("METADATA"); // For debugging
			randFile.writeInt(tree.getSequenceLength());
			randFile.writeInt(tree.getHeight());
			randFile.writeInt(tree.getRoot().getDegree());
			randFile.seek(12);
			diskWrite(tree.getRoot());
			// randFile.writeUTF("END"); // For debugging
		} catch (IOException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}

	}

	/**
	 * Jeff's method to write the metadata of a BTreeNode to a file
	 * 
	 * @param n the BTreeNode to write to a disk
	 */
	public void diskWrite(BTreeNode n) {
		if (n != null) {
			try {
				if (n.isRoot())
					randFile.seek(12);
				else
					randFile.seek(getOffset(n.getIndex(), n.getDegree()));

				// Write bytes to file
				//randFile.writeUTF("NODE START"); // For debugging
				randFile.writeInt(n.getIndex());
				randFile.writeBoolean(n.isLeaf());
				randFile.writeBoolean(n.isRoot());
				randFile.writeInt(n.getParentPointer());
				randFile.writeInt(n.keys.size());
				randFile.writeInt(n.childPointers.size());

				// write the same amount of data regardless of children / keys for easy
				// calculation of offset
				for (int i = 0; i < (2 * n.getDegree()); i++) {
					if (i < n.childPointers.size()) {
						randFile.writeInt(n.childPointers.get(i));
					} else {
						randFile.writeInt(-1);
					}

				}
				for (int j = 0; j < 2 * n.getDegree() - 1; j++) {
					if (j < n.keys.size()) {
						randFile.writeLong(n.keys.get(j).getKey());
						randFile.writeInt(n.keys.get(j).getDuplicates());
//						System.out.println(n.keys.get(j).getDuplicates());
					} else {
						randFile.writeLong(-1);
						randFile.writeInt(-1);
					}
				}

				//randFile.writeUTF("END"); // For debugging

			} catch (IOException e) {
				// TODO Auto-generated catch block
				e.printStackTrace();
			}
//			System.out.println("------");
//			System.out.println(n.toString());
//			System.out.println("------");
		}

	}

	/**
	 * Jeff's method to read and return a node from disk
	 * 
	 * @param index  - index of node. Offset will be calculated from this.
	 * @param degree - degree of tree. Offset calculation also depends on this.
	 * @return
	 */
	public BTreeNode diskRead(int index, int degree) {
		BTreeNode newNode = new BTreeNode(index, degree, false, false);

		try {
			if (index > 0) {
				randFile.seek(getOffset(index, degree));
				//randFile.readUTF(); // for debugging
				newNode.setIndex(randFile.readInt());
				newNode.setIsLeaf(randFile.readBoolean());
				newNode.setIsRoot(randFile.readBoolean());
				newNode.setParentPointer(randFile.readInt());
				int numKeys = randFile.readInt();
				int numChildPointers = randFile.readInt();
				for (int j = 0; j < numKeys; j++) {
					newNode.keys.add(j, new TreeObject(0L, seqLength));
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
						newNode.keys.get(j).setDuplicates(randFile.readInt());
					} else {
						randFile.readLong();
						randFile.readInt();
					}
				}
			}
		} catch (IOException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}
		if(newNode.keys.size() == 0)
			return null;
//		System.out.println("------");
//		System.out.println(newNode.toString());
//		System.out.println("------");

		return newNode;
	}

	public int nodeSizeOnDisk(int degree) {
		return (18 + 12 * (2 * degree - 1) + 4 * (2 * degree));
	}

	public int getOffset(int index, int degree) {
		return 12 + nodeSizeOnDisk(degree) + (index - 1) * nodeSizeOnDisk(degree);
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
