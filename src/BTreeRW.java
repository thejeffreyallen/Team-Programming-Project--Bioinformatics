import java.io.FileNotFoundException;
import java.io.RandomAccessFile;

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
	
	public void diskWrite()
	{
		
	}
	
	public BTreeNode diskRead()
	{
		return null;
	}
	
}
