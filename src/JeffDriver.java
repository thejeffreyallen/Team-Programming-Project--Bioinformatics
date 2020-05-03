import java.io.IOException;

public class JeffDriver {
	public static void main(String[] args) throws IOException
	{
		BTree tree = new BTree(3, "test", 3, 100, 0);
		TreeObject o = new TreeObject(32L, tree.getSequenceLength()); //GAA
		TreeObject o1 = new TreeObject(38L, tree.getSequenceLength()); //GCG
		TreeObject o2 = new TreeObject(40L, tree.getSequenceLength()); //GGA
		TreeObject o3 = new TreeObject(44L, tree.getSequenceLength()); //GTA 
		TreeObject o4 = new TreeObject(48L, tree.getSequenceLength()); //TAA
		TreeObject o5 = new TreeObject(52L, tree.getSequenceLength()); //TCA
		TreeObject o6 = new TreeObject(56L, tree.getSequenceLength()); //TGA
		TreeObject o7 = new TreeObject(62L, tree.getSequenceLength()); //TTG
		
		TreeObject o8 = new TreeObject(54L, tree.getSequenceLength()); //GAA
		TreeObject o9 = new TreeObject(64L, tree.getSequenceLength()); //GCG
		TreeObject o10 = new TreeObject(30L, tree.getSequenceLength()); //GGA
		TreeObject o11 = new TreeObject(46L, tree.getSequenceLength()); //GTA 
		TreeObject o12 = new TreeObject(34L, tree.getSequenceLength()); //TAA
		TreeObject o13 = new TreeObject(42L, tree.getSequenceLength()); //TCA
		TreeObject o14 = new TreeObject(58L, tree.getSequenceLength()); //TGA
		TreeObject o15 = new TreeObject(60L, tree.getSequenceLength()); //TTG
		

		
		

		
		tree.insert(o);
		tree.insert(o1);
		tree.insert(o2);
		tree.insert(o3);
		tree.insert(o4);
		tree.insert(o5);
		tree.insert(o6);
		tree.insert(o7);
		tree.insert(o8);
		tree.insert(o9);
		tree.insert(o10);
		tree.insert(o11);
		tree.insert(o12);
		tree.insert(o13);
		tree.insert(o14);
		tree.insert(o15);
		
		
		
		
		
		
		
		
		System.out.println(tree.printTree(tree.getRoot()));
		System.out.println("Optimal degree of " + tree.calculateDegree() +" found");
	}
}
