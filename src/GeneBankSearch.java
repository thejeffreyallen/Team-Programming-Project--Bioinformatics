import java.io.File;
import java.io.FileNotFoundException;
import java.util.Scanner;

public class GeneBankSearch {
    private static int sequenceLength;
    private static Cache cache;
    private static int degree;
    private static File treeFile;
    private static File query;
    private static boolean isCache;
    public static void main(String args[]) {

        if (args.length < 3 || args.length > 5) {
           usageError();
        } else {
        boolean isCache;
        treeFile = new File(args[1]);
        query = new File(args[2]);
        sequenceLength = 0;

        isCache=false;
        try{
        if(Integer.parseInt(args[0])==1){
            isCache= true;
        } 
    } catch(NumberFormatException e){
        usageError();
    }

        cache= null;
        try{
        if(isCache && args[4]!=null){
            cache = new Cache(Integer.parseInt(args[4]));
        }
    } catch(NumberFormatException e){
        usageError();
    }

        

        Scanner treeScan;
        int count= 0;
        try {
            treeScan = new Scanner(treeFile);
            while(treeScan.hasNext()){
                if(treeScan.next().charAt(count)==','){
                    break;
                } else{
                    sequenceLength++;
                }
                count++;
            }
            treeScan.close();
        } catch (FileNotFoundException e) {
            e.printStackTrace();
            usageError();
        }
       
        degree=((sequenceLength+1)/2);

        BTree b = new BTree(degree, args[2], sequenceLength, cache.getSize(), 0);
        GenBankSwitch genSwitch = new GenBankSwitch();
    try{
        Scanner queryScan = new Scanner(query);
        while(queryScan.hasNextLine()){
            Long queryData = genSwitch.switchStringToLong(queryScan.nextLine());
            TreeObject t = new TreeObject(queryData);
            
            TreeObject result = search(b.getRoot(), t);
            if(result!=null){
                System.out.println(result.toString());
            }

        }
        queryScan.close();
    } catch(FileNotFoundException e){
        e.printStackTrace();
        usageError();
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

    private static void usageError(){
        String s =   "Java GeneBankSearch <0/1(no/with Cache)> <btree file> <query file> [<cache size>] [<debug level>]";
        System.out.println(s);
        System.exit(1);
    }
}
