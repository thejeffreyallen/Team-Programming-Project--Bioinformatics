*********************
* Project BTree
* Class CS321
* Date 5/8/2020
* Authors:
*   - Jeffrey Allen 
*   - Abel Almeida
*   - Andy Breland 
*********************

OVERVIEW:
-------------------------------------------------------------------------------------------------
The BTree project has two main important programs to highlight.
The first one is the "GeneBankCreateBTree", which was designed to create a BTree from the GeneBank file and keep a BTree to a binary file for other usages.
The second program highlights the "GeneBankSearch".
The GeneBankSearch program will take in the saved BTree Binary to find a list of Gene Sequences in a BTree, by receiving a file containing a Gene Sequences to find.


COMPILING AND RUNNING:
-------------------------------------------------------------------------------------------

 To compile all the files enter the following commands from the directory where they are
 located:

    javac *.java

 After the files are compiled you can run the GeneBankCreateBTree with the commands along with these arguments:

    java GeneBankCreateBTree <0/1(no/with Cache)> <degree> <gbk file> <sequence length> [<cache size>] [<debug level>]

    Or you can run the GeneBankSearch with the following command along with these arguments:

    java GeneBankSearch <0/1(no/with Cache)> <btree file> <query file> [<cache size>] [<debug level>]


PROGRAM DESIGN AND IMPORTANT CONCEPTS:
-------------------------------------------------------------------------------------------

 The following is a diagram of the generic setup of our BTree and it's individual nodes
 complete with necessary instance variables and their max space consumption.


   Data Type Calculations
   ----------------------
        - boolean:      Takes up 1 bit (1 true, 0 false), so 1 Byte is needed at maximum
        - int:          Takes up 32 bits, so 32 / 8 = 4 Bytes of data needed
        - long:         Takes up 64 bits, so 64 / 8 = 8 Bytes of data needed
        - ArrayList<n>: Takes up n * S Bytes, so nS Bytes of data are needed
        - BTreeObject:  Contains a long and an two ints, so 16 Bytes of data are needed

   BTree Class
   -----------
   
	++ Instance Variables ++
	
	- int seqLength:	4 Bytes
	- int degree:   	4 Bytes
	- int height:   	4 Bytes
        -------------------------------
        Total Meta Data:        12 Bytes	

        ++ Instance Variable Desc++
       
        seqLength:  The sequence length for GeneSequences, it is four bytes. 

        degree:     Degree of the BTree 	
   
        height:     Height of the BTree

        ++ Diagram ++

                BTree - META DATA
                +-------------------------------+------------+------------------------+
                | ~~ Variable ~~ : ~~ Offset ~~ |            |           |            |
                |      degree    :     0x00     |  Node - 1  |           |  Node - N  |
                |    seqLength   :     0x04     |            |           |            |
                |      height    :     0x08     |            |           |            |
                |      root      :     0x12     |  BTree     |   BTree   |            |
                +-------------------------------+------------+------------------------+
       
    BTreeNode Class
    --------------- 

            ++ Instance Variables ++

                - int index:                            4 Bytes
                - boolean isLeafNode:                   1 Byte   +
                - boolean isRoot:                       1 Byte   +
                - int parentNodePointer:                4 Bytes
                ----------------------------------------------------
                Total Meta Data:                        10 Bytes

                - ArrayList<Integer> childNodePointers:  4 * (2T) Bytes
                - ArrayList<BTreeObject> keys:          16 * (2T - 1) Bytes  +
                ----------------------------------------------------------------
                Node Objects and Child Pointers:        40T - 16 Bytes

                - META DATA:                           10 Bytes
                - Node Objects and Child Pointers:     40T - 16 Bytes  +
                ----------------------------------------------------------
                Total BTreeNode Size:                  40T - 6 Bytes

            ++ Instance Variable Descriptions ++

                isLeafNode:         BTreeNode is a leaf node or not.

                parentNodePointer:  Pointer to the Parent Node of this node in the file.

                childNodePointers:  An array list of Pointers to child nodes in the file. All
                                    existing pointers will be stored in sorted order
                                    starting at the offset 4 Bytes from the
                                    beginning of the BTreeNode.

                keys:               An array list of Node keys within this node will have an offset of
 				                    12 bytes.

            ++ Diagram ++

                       BTreeNode - META DATA
                +----------------------------------+-----------------+-----------------+
                | ~~   Variable  ~~ : ~~ Offset ~~ |  Child Pointer  | TreeObject keys |
                |     index         :     0x12     |   Arraylist     |   ArrayList     |
                |     isLeaf        :     0x16     | 1, 2, ..., 2T   | 1, 2, ..., 2T-1 |
                |     isRoot        :     0x17     |                 |                 |
                | parentNodePointer :     0x18     |                 |                 |
                |     key size      :     0x22     |                 |                 |
                |    childNodePt    :     0x26     |                 |                 |
                |    key size       :     0x30     |                 |                 |
                +----------------------------------+-----------------+-----------------+

   TreeObject Class
   ----------------

            ++ Instance Variables ++

                - long data:                8 Byte
                - int seqLength:            4 Bytes  +
                - int duplicates:           4 Bytes  +
                ----------------------------------------
                Total BTreeObject size:     16 Bytes

            ** Instance Variable Descriptions **

                data:             Contains an encoded Gene-Sequence for the values
                                  <A, T, C, G> encoded in-order as <00, 11, 01, 10>.

                seqLength:        The sequence length for GeneSequences, it is four bytes. 

                duplicates:       Contains the number of occasions in which the
                                  GeneSequence was found within the GeneBank.

            ** Diagram **

                              BTreeObject
                +------------------------------------+
                | ~~   Variable   ~~  : ~~ Offset ~~ |
                |        data         :     0x12     |
                |     seqLength       :     0x20     |
                |     duplicates      :     0x24     |
                +------------------------------------+


    Optimal BTree Degree
        ====================

            Since the block size of the operating system in which this BTree will reside
            contains 4096 bytes and the Max Size of a BTreeNode is 40T - 6 Bytes, the
            following calculations will provide the optimal degree for the BTree.

                     4096 >= 40T - 6
                     4102 >= 40T
                   102.55 >= T

                floor(102.55) = T
                          102 = T

            Given the above calculations the optimal degree of this BTree for a block
            size of 4096 is 102.

            If in the future each BTreeNode is used to it's fullest potential with the
            BTree having a degree of 102 and the block size being 4096 the following
            calculations will show the number of unused bytes within a BTreeNode:

                BTreeNodeSize(T)   = 40T - 6
                BTreeNodeSize(102) = 40(102) - 6
                                   = 4080 - 6
                                   = 4074

                Extra Space = 4096 - BTreeNodeSize(102)
                            = 24 Bytes

    Cache Implementation 
        ====================

        We implemented a cache in our reading and writing to disk. In the read 
        method we search for a TreeNode based on index in the cache and return 
        it if it exists. We add the TreeNode to the cache in the write method
        and at the end of read method.

	Local Machine:
        Times are run with test3.gbk with a degree of 4 and sequence length 11
        
        GeneBankCreateBTree:
            Without Cache: 1021 ms

            Cache Size 100: 978 ms

            Cache Size 500: 1692 ms

        Run using the query4 file with a BTreeFile of sequence length 4
        GeneBankSearch:
            Without cache: 34 ms
            
            Cache Size 100: 33 ms

            Cache Size 500: 47 ms
	   
	Onyx:
	 Times are run with test3.gbk with a degree of 4 and sequence length 11
        
        GeneBankCreateBTree:
            Without Cache: 46348 ms

            Cache Size 100: 49234 ms

            Cache Size 500: 61604 ms

        Run using the query4 file with a BTreeFile of sequence length 4
        GeneBankSearch:
            Without cache: 72 ms
            
            Cache Size 100: 78 ms

            Cache Size 500: 71 ms
	    
	 Test5.gbk GeneBankCreateBTree runtime:
	 2 hrs 48 minutes
	
