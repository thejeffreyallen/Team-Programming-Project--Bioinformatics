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
	
	public long getLength(){
		long l = 0L;
		try{
		 l= randFile.length();
		}
		catch(IOException e){
			e.printStackTrace();
		}
		return l;
	}
	/**
	 * Writes the metadata of a BTreeNode to a file
	 * @param n the BTreeNode to write to a disk
	 */
	public void diskWrite(BTreeNode n)
	{
		
		int keyCount = n.getKeyCount();
		int maxKeys = n.getMaxKeys();
		if(n!=null){
		try {
			
			randFile.seek(n.getLocation());
			randFile.writeInt(n.getIndex());
			randFile.writeInt((n.getMaxKeys()+1)/2);
			randFile.writeBoolean(n.isLeaf());
			randFile.writeInt(n.getLocation());
			
			int i;
			for(i =0; i<maxKeys; i++){
				if(i<keyCount && !n.isLeaf()){
				randFile.writeInt(n.getChildPointer(i));
				} 
				if(i<keyCount){
					randFile.writeLong(n.getKey(i).getKey());
					randFile.writeInt(n.getKey(i).getDuplicates());
				}
			}
			if(!n.isLeaf()){
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
		
		try {
			randFile.seek(pointer);
			if(pointer!=0){
			newNode = new BTreeNode(randFile.readInt(), randFile.readInt(), false, randFile.readBoolean());
			} else{
				newNode = new BTreeNode(randFile.readInt(), randFile.readInt(), true, randFile.readBoolean());
			}
			int keyCount = newNode.getKeyCount();
			int maxKeys = newNode.getMaxKeys();

			for(int i =0; i<maxKeys; i++){
				if(i<keyCount && !newNode.isLeaf()){
				newNode.addChild(randFile.readInt());
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