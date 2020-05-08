import java.io.File;
import java.io.FileNotFoundException;
import java.util.Scanner;

/**
 * Gene Bank Search Driver Class
 * 
 * Reads in a set of queries in the form of keys and searches the B-Tree for
 * matching values
 * 
 * 
 * @author Andy Breland
 *
 */

public class GeneBankSearch {
	private static int sequenceLength;
	private static Cache cache;
	private static int degree, debugLevel;
	private static int cacheSize = 0;
	private static File treeFile;
	private static File query;
	private static boolean isCache;
	private static boolean isDebug;

	@SuppressWarnings("rawtypes")
	public static void main(String args[]) {

		if (args.length < 3 || args.length > 5) {
			usageError();
		} else {
			boolean isCache;
			treeFile = new File(args[1]);
			query = new File(args[2]);

			isCache = false;
			try {
				if (Integer.parseInt(args[0]) == 1) {
					isCache = true;
				}
			} catch (NumberFormatException e) {
				usageError();
			}

			cache = null;
			if (isCache) {
				try {
					if (isCache && args[3] != null) {
						cache = new Cache(Integer.parseInt(args[3]));
						cacheSize = cache.getSize();
					} else {
						usageError();
					}
				} catch (NumberFormatException e) {
					usageError();
				}
			}
			else
			{
				try{
				if(!isCache && Integer.parseInt(args[3]) != 0)
				{
					if(args[3]!=null){
					System.err.println("Cache size argument is invalid due to arg[0] = 0\n");
					usageError();
					}
				}
			} catch(NumberFormatException e){
				usageError();
			}
			}
			isDebug = false;
			try {
				if (args.length == 5) {
					debugLevel = Integer.parseInt(args[4]);
					if (debugLevel == 0) {
						isDebug = true;
					}
				}
			} catch (NumberFormatException e) {
				usageError();
			}

			if(args[3]==null){
				cacheSize = 0;
			}

			String sequenceScan = args[1];

			int count = 0;
			int sequenceCount = 0;
			int degreeCount = 0;
			try {
				while (count < sequenceScan.length()) {
					if (sequenceCount == 4 && sequenceLength == 0) {
						int i = count;
						String s = "";
						while (sequenceScan.charAt(i) != '.') {
							s += sequenceScan.charAt(i);
							i++;
						}
						sequenceLength = Integer.parseInt(s);
					}

					if (degreeCount == 5 && degree == 0) {
						int i = count;
						String s = "";
						while (sequenceScan.charAt(i) != '.' && i < sequenceScan.length()) {
							s += sequenceScan.charAt(i);
							i++;
							if (i == sequenceScan.length()) {
								break;
							}
						}
						degree = Integer.parseInt(s);

					}
					if (sequenceScan.charAt(count) == '.') {
						sequenceCount++;
						degreeCount++;
					}
					count++;

				}
			} catch (NumberFormatException e) {
				System.out.println("Make sure file is in this format: xyz.gbk.btree.data.k.t.");
				System.out.println("Where sequence length is k and BTree Degree is t");
			}

			BTree b = new BTree(treeFile, cacheSize, debugLevel);
			GenBankSwitch genSwitch = new GenBankSwitch();
			try {
				Scanner queryScan = new Scanner(query);
				while (queryScan.hasNextLine()) {
					Long queryData = genSwitch.switchStringToLong(queryScan.nextLine());
					TreeObject t = new TreeObject(queryData);

					TreeObject result = b.search(b.getRoot(), t);
					if (result != null) {
						if (isDebug) {
							System.out.println(result.toString());
						}
					}

				}
				queryScan.close();
			} catch (FileNotFoundException e) {
				e.printStackTrace();
				usageError();
			}
		}
	}
	
	/**
	 * To be printed when args are not as expected
	 */
	private static void usageError() {
		String s = "Java GeneBankSearch <0/1(no/with Cache)> <btree file> <query file> [<cache size>] [<debug level>]";
		System.err.println(s);
		System.exit(1);
	}
}
