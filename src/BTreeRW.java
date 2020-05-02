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
	private int seqLength;
	
	
	
	/**
	 * Constructor
	 * @param fileName the name of the random access file
	 * @param cacheSize the size of the cache
	 */
	public BTreeRW(String fileName, int cacheSize)
	{
		this.fileName = fileName;
		this.cacheSize = cacheSize;
	public BTreeRW(String fileName, int cacheSize, int seqLength)
	{
		this.fileName = fileName;
		this.cacheSize = cacheSize;
		this.seqLength = seqLength;
		
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
		//cache.addObject(n);
		int keyCount = n.getKeyCount();
		int maxKeys = n.getMaxKeys();
		if(n!=null){
		try {
			
			
			randFile.seek(n.getIndex());
			randFile.writeInt(n.getIndex());
			randFile.writeInt((n.getMaxKeys()+1)/2);
			randFile.writeBoolean(n.isLeaf());
			randFile.writeBoolean(n.isRoot());
			randFile.writeInt(n.getParentPointer());
			randFile.writeInt(n.keys.size());
			int i;
			for(i =0; i<maxKeys; i++){
				if(i<keyCount && !n.isLeaf()){
				randFile.writeInt(n.getChildPointer(i));
				} else if(i>=keyCount || n.isLeaf()){
					randFile.seek(randFile.getFilePointer()+4);
				}
				if(i<keyCount){
					randFile.writeLong(n.getKey(i).getKey());
					randFile.writeInt(n.getKey(i).getDuplicates());
				}
			}
			if(!n.isLeaf()){
			if(!n.isLeaf() && i < n.childPointers.size()){
				randFile.writeInt(n.getChildPointer(i));
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
		/*
		if(cache!=null){
			newNode = cache.getAtIndex(pointer);
		}
		if(newNode!=null){
			return newNode;
		}
		*/
		try {
			randFile.seek(pointer);
			newNode =new BTreeNode(randFile.readInt(), randFile.readInt(), randFile.readBoolean(), randFile.readBoolean());
			newNode.setParentPointer(randFile.readInt());
			int keyCount = newNode.getKeyCount();
			int keyCount = randFile.readInt();
			int maxDegree = newNode.getMaxKeys();

			for(int i =0; i<maxDegree; i++){
				if(i<keyCount && !newNode.isLeaf()){
				newNode.addChild(randFile.readInt());
				} else if(i>=keyCount || newNode.isLeaf()){
					randFile.seek(randFile.getFilePointer()+4);
				}
				if(i<keyCount){
					TreeObject t = new TreeObject(randFile.readLong());
					TreeObject t = new TreeObject(randFile.readLong(), seqLength);
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