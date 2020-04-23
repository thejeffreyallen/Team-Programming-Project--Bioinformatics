/**
 * GenBankSwitch will switch a Long to String and String to a Long
 *
 * @Author Abel Almeida
 */

public class GenBankSwitch {
    private long key = 0;

    public long getKey() {
        return key;
    }

    public String switchLongToString(long seq, int seqLength) {
        String s = "";
        long num;
        StringBuilder string = new StringBuilder();
        for (int i = 1; i <= seqLength; i++) {
            num = (seq & 3L << (seqLength - i) * 2);
            num = num >> (seqLength - i) * 2;

            switch ((int) num) {
                case (0):
                    string.append("a");
                    break;
                case (1):
                    string.append("c");
                    break;
                case (2):
                    string.append("g");
                    break;
                case (3):
                    string.append("t");
                    break;
                default:

            }

        }
        return string.toString();
    }

    public long switchStringToLong(String seq) {
        char[] list = seq.toLowerCase().toCharArray();
        int i = 0;
        for (char c : list) {
            switch (c) {
                case ('a'):
                    if (i == 0) {
                        key = 0;
                    } else {
                        key = key << 2;
                        key = key | 0;
                    }
                    i++;
                    break;
                case ('c'):
                    if (i == 0) {
                        key = 1;
                    } else {
                        key = key << 2;
                        key = key | 1;
                    }
                    i++;
                    break;
                case ('g'):
                    if (i == 0) {
                        key = 2;
                    } else {
                        key = key << 2;
                        key = key | 2;
                    }
                    i++;
                    break;
                case ('t'):
                    if (i == 0) {
                        key = 3;
                    } else {
                        key = key << 2;
                        key = key | 3;
                    }
                    i++;
                    break;
            }
        }
        return key;
    }

}
