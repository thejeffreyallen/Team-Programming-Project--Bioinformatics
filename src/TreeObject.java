
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


	/**
	 * Constructor
	 * @param l the long that the object contains
	 * initializes duplicates to 0
	 */
	public TreeObject(long l) {
		data = l;
		duplicates = 0;
		
	}


	/**
	 * Increase the number of duplicates of objects
	 */
	public void incrementDuplicates() {
		duplicates++;
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
		
/*
		for (int i = 0; i < dataString.length() - 1; i = i + 2) {

			String check = "";
			check += dataString.charAt(i);
			check += dataString.charAt(i + 1);
			switch (check) {
			case "00":
				s += "A";
				break;
			case "11":
				s += "T";
				break;
			case "01":
				s += "C";
				break;
			case "10":
				s += "G";
				break;
			default:

			}

		}
*/
		return s + " " + getDuplicates();
	}
}
