import java.io.*;

public class BTreeRW {
	
	private RandomAccessFile randFile;
	private Cache<BTreeNode> cache;
	private String fileName;
	private int cacheSize;
	
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
	
	public void diskWrite(BTreeNode n)
	{
		int degree = ((n.getMaxKeys()+1)/2);
		try {
			randFile.seek(n.getIndex());
			randFile.writeInt(n.getIndex());
			randFile.writeInt(degree);
			randFile.writeBoolean(n.isRoot());
			randFile.writeBoolean(n.isLeaf());
		} catch (IOException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}
	}
	
	public BTreeNode diskRead(int pointer)
	{
		try {
			randFile.seek(pointer);
			int location = randFile.readInt();
			int degree = randFile.readInt();
			boolean isRoot = randFile.readBoolean();
			boolean isLeaf = randFile.readBoolean();
			BTreeNode retVal = new BTreeNode(location, degree, isRoot, isLeaf);
			return retVal;
		} catch (IOException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}
		
		return null;
		
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