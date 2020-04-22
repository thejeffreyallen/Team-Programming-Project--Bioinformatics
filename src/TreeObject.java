


public class TreeObject {
	private long data;
	private int duplicates;


	public TreeObject(long l) {
		data = l;
		duplicates = 0;
		
	}


	public void incrementDuplicates() {
		duplicates++;
	}

	public long getKey() {
		return data;
	}
	public int getDuplicates() {
		return duplicates;
	}
	
	public int compareTo(TreeObject t) {
		if(this.data>t.getKey()) {
			return 1;
		} else if(this.data<t.getKey()) {
			return -1;
		} else
			return 0;
	}
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
