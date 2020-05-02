import java.util.Scanner;

public class GeneBankSearch {
    public static void main(String args[]) {

        if (args.length < 3 || args.length > 5) {
            System.out.println("Java GeneBankSearch <0/1(no/with Cache)> <btree file> <query file> [<cache size>] [<debug level>]");
        } else {
        boolean isCache;
        String treeFile = args[1];
        String query = args[2];
        int sequenceLength = 0;
        if(Integer.parseInt(args[0])==1){
            isCache= true;
        } else{
            isCache = false;
        }
        Cache cache= null;
        if(isCache && args[4]!=null){
            cache = new Cache(Integer.parseInt(args[4]));
        }

        GenBankSwitch genSwitch = new GenBankSwitch();

        for(int i =0; i<treeFile.length(); i++){
            if(treeFile.charAt(i)!=','){
                i++;
            }
            if(treeFile.charAt(i)==','){
                sequenceLength = i;
                break;
            }
        }
        int degree=((sequenceLength+1)/2);

        BTree b = new BTree(degree, treeFile, sequenceLength, cache.getSize(), 0);
        Scanner fileScan = new Scanner(query);
        while(fileScan.hasNextLine()){
            Long queryData = genSwitch.switchStringToLong(fileScan.nextLine());
            TreeObject t = new TreeObject(queryData);
            
            search(b, t);

        }
    }
    }

    private static void search(BTree tree, TreeObject t){
        
       
    }
}
