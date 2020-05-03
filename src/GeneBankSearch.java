import java.util.Scanner;

public class GeneBankSearch {
    private static int sequenceLength;
    private static Cache cache;
    private static int degree;
    public static void main(String args[]) {

        if (args.length < 3 || args.length > 5) {
            System.out.println("Java GeneBankSearch <0/1(no/with Cache)> <btree file> <query file> [<cache size>] [<debug level>]");
        } else {
        boolean isCache;
        String treeFile = args[1];
        String query = args[2];
        sequenceLength = 0;
        if(Integer.parseInt(args[0])==1){
            isCache= true;
        } else{
            isCache = false;
        }
        cache= null;
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
        degree=((sequenceLength+1)/2);

        BTree b = new BTree(degree, treeFile, sequenceLength, cache.getSize(), 0);
        Scanner fileScan = new Scanner(query);
        while(fileScan.hasNextLine()){
            Long queryData = genSwitch.switchStringToLong(fileScan.nextLine());
            TreeObject t = new TreeObject(queryData);
            
            TreeObject result = search(b.getRoot(), t);
            if(result!=null){
                System.out.println(result.toString());
            }

        }
    }
    }

    private static TreeObject search(BTreeNode root, TreeObject t){
       int i =0;
       BTreeRW diskWriter = new BTreeRW("diskWrite", cache.getSize(), sequenceLength);
       while(i<root.getKeyCount() && (t.compareTo(root.getKey(i))>0)){
           i++;
       }
           if(i<root.getKeyCount() && t.compareTo(root.getKey(i))==0){
               return root.getKey(i);
           }
           if(root.isLeaf()){
               return null;
           } else{
               BTreeNode child = diskWriter.diskRead(root.getChildPointer(i), degree);
               return search(child, t);
           }
           
       
    }
}
