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
    private static boolean isDebug;
    public static void main(String args[]) {

        if (args.length < 3 || args.length > 5) {
           usageError();
        } else {
        boolean isCache;
        treeFile = new File(args[1]);
        query = new File(args[2]);

        isCache=false;
        try{
        if(Integer.parseInt(args[0])==1){
            isCache= true;
        } 
    } catch(NumberFormatException e){
        usageError();
    }

        cache= null;
        if(isCache){
        try{
        if(isCache && args[4]!=null){
            cache = new Cache(Integer.parseInt(args[3]));
        } else{
            usageError();
        }
    } catch(NumberFormatException e){
        usageError();
    }
        }
    isDebug = false;
        try{
            if(args.length==5){
                if(Integer.parseInt(args[4])==0){
                    isDebug =true;
                }
            }
        } catch(NumberFormatException e){
            usageError();
        }
        

        String sequenceScan = args[1];
        sequenceLength = 0;
        degree = 0;
        int count =0;  
        int sequenceCount = 0;
        int degreeCount=0;
       while(count<sequenceScan.length()) {
           if(sequenceCount==3 && sequenceLength == 0) {
               int i = count;
               String s ="";
               while(sequenceScan.charAt(i)!='.') {
                   s += sequenceScan.charAt(i);
                   i++;
               }
               sequenceLength= Integer.parseInt(s);
           }
           
           if(degreeCount==4 && degree == 0) {
               int i = count;
               String s = "";
               while(sequenceScan.charAt(i)!='.') {
                   s += sequenceScan.charAt(i);
                   i++;
               }
               degree = Integer.parseInt(s);
               
           }
           if(sequenceScan.charAt(count)=='.') {
               sequenceCount++;
               degreeCount++;
           }
           count++;
           
       }

        BTree b = new BTree(degree, args[2], sequenceLength, cache.getSize(), 0);
        GenBankSwitch genSwitch = new GenBankSwitch();
    try{
        Scanner queryScan = new Scanner(query);
        while(queryScan.hasNextLine()){
            Long queryData = genSwitch.switchStringToLong(queryScan.nextLine());
            TreeObject t = new TreeObject(queryData);
            
            TreeObject result = b.search(b.getRoot(), t);
            if(result!=null){
                if(isDebug){
                System.out.println(result.toString());
                }
            }

        }
        queryScan.close();
    } catch(FileNotFoundException e){
        e.printStackTrace();
        usageError();
    }
    }
    }
    /*

    public TreeObject search(BTreeNode root, TreeObject t){
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
    */

    private static void usageError(){
        String s =   "Java GeneBankSearch <0/1(no/with Cache)> <btree file> <query file> [<cache size>] [<debug level>]";
        System.out.println(s);
        System.exit(1);
    }
}
