import java.io.*;

/**
 * 
 * @author AndyBreland
 * 
 *         Class that reads and writes to disk.
 *
 */
public class BTreeRW {
	
	private RandomAccessFile randFile;
	private Cache<BTreeNode> cache;
	private String fileName;
	private int cacheSize;
	
	/**
	 * Constructor
	 * @param fileName the name of the random access file
	 * @param cacheSize the size of the cache
	 */
	public BTreeRW(String fileName, int cacheSize)
	{
		this.fileName = fileName;
		this.cacheSize = cacheSize;
		try {
			randFile = new RandomAccessFile(fileName, "rw");
		} catch (FileNotFoundException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}
		cache = new <BTreeNode> Cache(cacheSize);
	}
	
	/**
	 * Writes the metadata of a BTreeNode to a file
	 * @param n the BTreeNode to write to a disk
	 */
	public void diskWrite(BTreeNode n)
	{
		cache.addObject(n);
		if(n!=null){
		try {
			randFile.seek(n.getIndex());
			randFile.writeBoolean(n.isLeaf());
			randFile.writeInt(n.getKeyCount());
			randFile.writeInt(n.getMaxKeys());
			randFile.writeInt(n.getParentPointer());
			for(int i =0;i< n.getMaxKeys(); i++){
				if(i<n.getKeyCount() && !n.isLeaf()){
					randFile.writeInt(n.getChildPointer(i));
				} else if(i>=n.getKeyCount() || n.isLeaf()){
					randFile.writeInt(0);
				}
				if(i<n.getKeyCount()){
				randFile.writeLong(n.getKey(i).getKey());
				randFile.writeInt(n.getKey(i).getDuplicates());
				} else if(i >= n.getKeyCount() && !n.isLeaf()){
					randFile.writeLong(0);
				}

			}
			if (!n.isLeaf()){
				randFile.writeInt(n.getChildPointer(n.getMaxKeys()));
			}
		} catch (IOException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}
	}
	}
	
	/**
	 * reads and returns a BTreeNode from a disk
	 */
	public BTreeNode diskRead(int pointer)
	{
		BTreeNode newNode = null;
		if(cache!=null){
			newNode = cache.getAtIndex(pointer);
		}
		if(newNode!=null){
			return newNode;
		}
		newNode = new BTreeNode();
		try {
			randFile.seek(pointer);
			newNode.setIsLeaf(randFile.readBoolean());
			int keyCount = randFile.readInt();
			int maxDegree = randFile.readInt();
			newNode.setParentPointer(randFile.readInt());

			for(int i =0; i<maxDegree; i++){
				if(i<keyCount && !newNode.isLeaf()){
				newNode.addChild(randFile.readInt());
				} else if(i>=keyCount || newNode.isLeaf()){
					randFile.seek(randFile.getFilePointer()+4);
				}
				if(i<keyCount){
					TreeObject t = new TreeObject(randFile.readLong());
					t.setDuplicates(randFile.readInt());
					newNode.addKey(t);
				}
			}
			if(!newNode.isLeaf()){
				newNode.addChild(randFile.readInt());
			}
		} catch (IOException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}
		
		return newNode;
		
	}
	
	private int parent(int i) {
		int p = i/2;
		return p;
	}
	private int left(int i) {
		return 2 * i;
	}

	private int right(int i) {
		return (2 * i) + 1;
	}
}