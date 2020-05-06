
/**
 * 
 * @author Andy Breland
 * 
 *         BTree Object class defines an object to be inserted into a BTree.
 *
 */
public class TreeObject {
	private long data;
	private int duplicates;
	private int seqLength;


	/**
	 * Constructor
	 * @param data the long that the object contains
	 * @param seqLength the length of the genome sequence
	 * initializes duplicates to 1
	 */
	public TreeObject(long data, int seqLength) {
		this.data = data;
		this.duplicates = 1;
		this.seqLength= seqLength;
		
	}

	/**
	 * Constructor
	 * @param data the long that the object contains
	 * initializes duplicates to 1
	 */
	public TreeObject(long data) {
		this.data = data;
		this.duplicates = 1;

	}

	/**
	 * Increase the number of duplicates of objects
	 */
	public void incrementDuplicates() {
		duplicates++;
	}
	/**
	 * sets the number of duplicates
	 */
	public void setDuplicates(int i){
		this.duplicates = i;
	}

	/**
	 * @return the long key in the object
	 */
	public long getKey() {
		return data;
	}

	/**
	 * @return the number of duplicates
	 */
	public int getDuplicates() {
		return duplicates;
	}
	
	public void setData(long data)
	{
		this.data = data;
	}
	
	/**
	 * @param t the tree object to compare to
	 * @return 1 if current object is greater
	 * @return -1 if current object is less
	 * @return 0 if they are the same
	 */
	public int compareTo(TreeObject t) {
		if(this.data>t.getKey()) {
			return 1;
		} else if(this.data<t.getKey()) {
			return -1;
		} else
			return 0;
	}


	/**
	 * @return the genome string and number of duplicates
	 */
	
	public String toString() {
	String s = "";
		GenBankSwitch convert = new GenBankSwitch();
		s += convert.switchLongToString(this.data, this.seqLength);
	

		return s + " " + getDuplicates();
	}
}
