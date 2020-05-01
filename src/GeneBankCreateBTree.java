import java.io.BufferedReader;
import java.io.File;
import java.io.FileReader;
import java.io.IOException;

public class GeneBankCreateBTree {
    private static String file;
    private static int subSeqLen;
    public static final int MAX_SEQUENCE_LENGTH = 31;

    public static void main(String args[]) {

        file = args[2];
        subSeqLen = Integer.parseInt(args[3]);
        // usage
        if (args.length < 3 || args.length > 6) {
            // print usage message
        }
        // degree
        int BTreeDegree;
        try {
            int deg = Integer.parseInt(args[1]);
            if (deg < 0) badUsage();
            else if (deg == 0) BTreeDegree = getOptimalDegree();
            else BTreeDegree = deg;
        } catch (NumberFormatException e) {
            badUsage();
        }

        BufferedReader bReader = null;
        try {
            bReader = new BufferedReader(new FileReader(args[2]));
            boolean sequenceFound = false;
            String line = null;
            line = bReader.readLine();
            StringBuilder dnaSubStr = new StringBuilder(subSeqLen);
            StringBuilder dnaSubBinary = new StringBuilder(subSeqLen * 2);
            String str;

            while (line != null) {
                if (sequenceFound == true && !line.startsWith("ORIGIN")) {
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
                            System.out.printf("[%s]%n", dnaSubStr.toString());
                            System.out.printf("Binary : %s%n", dnaSubBinary.toString());
                            System.out.printf("Key: %s%n", key.switchStringToLong(dnaSubStr.toString()));
                            dnaSubStr.deleteCharAt(0);
                            dnaSubBinary.delete(0, 2);
                        }
                    }
                } else if(line.startsWith("ORIGIN")) {
                    // clean up remove numbers and spaces  and  catonate
                    sequenceFound = true;

                }
                line = bReader.readLine();

            }
        } catch (IOException e) {
            System.err.println("ERROR:: Failed to open file '" + file + "' for reading.\n");

        } finally {
            try {
                if (bReader != null)
                    bReader.close();
            } catch (IOException e) {
                System.err.println("ERROR:: Failed to close file '" + file + "'.\n");

            }

        }

    }
    private static void badUsage() {
        StringBuilder str = new StringBuilder();
        str.append("Usage: java GeneBankCreateBTree <cache> <degree> <gbk file> <sequence length> [<cache size>] [<debuglevel>]");
        System.exit(1);
    }

    private static int getOptimalDegree(){
       return 0;
    }
}
