import java.io.*;
import java.nio.Buffer;

public class GeneBankCreateBTree {
    static String file;
    static BTree tree;
    private static int degree;
    private static int subSeqLen;
    static int blockSize = 4096;
    public static final int MAX_SEQUENCE_LENGTH = 31;
    static File file1;
    static int cacheSize;
    static boolean withCache;
    static int debugLevel;
    static int BTreeDegree;

    public static void main(String args[]) {

        if (args.length < 3 || args.length > 6) {
            System.err.println("Wrong number of arguments");
            badUsage();
        }
        // degree

        try {
            int degree = Integer.parseInt(args[1]);
            if (degree < 0) badUsage();
            else if (degree == 0) BTreeDegree = getOptimalDegree();
            else BTreeDegree = degree;
        } catch (NumberFormatException e) {
            badUsage();
        }

        // cache

        try {
            if (args[3].equals("1")) {
                withCache = false;
            } else {
                withCache = true;
                cacheSize = Integer.parseInt(args[4]);
            }

        } catch (NumberFormatException e) {
            System.err.println("Cache argument should be 0 or 1 ");
            badUsage();
        }

        // sequencLength
        try {
            subSeqLen = Integer.parseInt(args[3]);
            if (subSeqLen < 1 && subSeqLen > 31) {
                System.err.println("DNA Squence length should be between 1 - 31");
                badUsage();
            }

        } catch (Exception e) {

            e.printStackTrace();
        }


        // debugLevel
        try {
            debugLevel = Integer.parseInt(args[5]);
        } catch (Exception e) {
            e.printStackTrace();
            System.err.println("Debuglevel argument should be 0 or 1 ");
            badUsage();
        }


        // get file
        try {
            file = args[2];
            //file1 = new File(args[2]);
        } catch (Exception e) {
            System.err.println("File does not exist");
            badUsage();
        }

        BufferedReader bReader = null;
        try {
            //RandomAccessFile randomAccess = new RandomAccessFile(file, "rw");
            tree = new BTree(BTreeDegree, file, subSeqLen, 0, debugLevel);
            bReader = new BufferedReader(new FileReader(file));
            boolean sequenceFound = false;
            String line = null;
            line = bReader.readLine();
            StringBuilder dnaSubStr = new StringBuilder(subSeqLen);
            StringBuilder dnaSubBinary = new StringBuilder(subSeqLen * 2);
            String str;
            TreeObject object;

            while (line != null) {
                if (sequenceFound == true) {
                    if (line.matches("^//.*")) {
                        sequenceFound = false;   //  reset the length
                        dnaSubStr.setLength(0);
                        dnaSubBinary.setLength(0);
                        continue;
                    }
                    String newline = line.replaceAll("\\s+|\\d+", "").toUpperCase();
                    char[] charArray = newline.toCharArray();
                    int invalidCharCount = 0;
                    GenBankSwitch key = new GenBankSwitch();
                    for (char c : charArray) {
                        switch (c) {
                            case 'A':
                                dnaSubStr.append(c);
                                dnaSubBinary.append("00");
                                break;
                            case 'T':
                                dnaSubStr.append(c);
                                dnaSubBinary.append("11");
                                break;
                            case 'C':
                                dnaSubStr.append(c);
                                dnaSubBinary.append("01");
                                break;
                            case 'G':
                                dnaSubStr.append(c);
                                dnaSubBinary.append("10");
                                break;
                            default:
                                invalidCharCount++;
                                dnaSubStr.setLength(0);
                                dnaSubBinary.setLength(0);
                        }
                        if (dnaSubStr.length() == subSeqLen) {
                              long numb = key.switchStringToLong(dnaSubStr.toString());
                              object = new TreeObject(numb);
                              tree.insert(object);

                            System.out.printf("[%s]%n", dnaSubStr.toString());
                            System.out.printf("Binary : %s%n", dnaSubBinary.toString());
                            System.out.printf("Key: %s%n", key.switchStringToLong(dnaSubStr.toString()));
                            dnaSubStr.deleteCharAt(0);
                            dnaSubBinary.delete(0, 2);
                        }
                    }
                } else if (line.startsWith("ORIGIN")) {
                    // clean up remove numbers and spaces  and  catonate
                    sequenceFound = true;

                }
                line = bReader.readLine();

            }
        } catch (IOException e) {
            System.err.println("ERROR:: Failed to open file '" + file + "' for reading.\n");

        } finally {
            try {
                if (bReader == null)
                    bReader.close();
            } catch (IOException e) {
                System.err.println("ERROR:: Failed to close file '" + file + "'.\n");

            }

        }

    }

    private static void badUsage() {
        StringBuilder str = new StringBuilder();
        str.append("Usage: java GeneBankCreateBTree <0/1(no/with Cache)> <degree> <gbk file> <sequence length> [<cache size>] [<debuglevel>]");
        System.exit(1);
    }

    private static int getOptimalDegree() {
        return 0;
    }
}
