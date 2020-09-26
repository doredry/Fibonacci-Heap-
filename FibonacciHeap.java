/**
 * FibonacciHeap
 *
 * An implementation of fibonacci heap over integers.
 *
 */

public class FibonacciHeap
{
	public HeapNode first;
	public HeapNode min;
	public int size;
	public int num_trees;
	public int num_marked;
	public static int links_counter = 0;
	public static int cuts_counter = 0;

	public FibonacciHeap() {
		this.size = 0;
		this.num_trees = 0;
		this.num_marked = 0;
	}


	/**
	 * public boolean isEmpty()
	 *
	 * precondition: none
	 * 
	 * The method returns true if and only if the heap
	 * is empty.
	 *   
	 */

	public boolean isEmpty()                                                 
	{
		return size == 0;
	}

	/**
	 * public HeapNode insert(int key)
	 *
	 * Creates a node (of type HeapNode) which contains the given key, and inserts it into the heap. 
	 */
	public HeapNode insert(int key)                                              
	{
		HeapNode toInsert = new HeapNode(key);
		if (this.isEmpty()) {                     //Case A: Heap is empty
			this.first = toInsert;
			this.min = toInsert;
			this.first.prev = this.first;
			this.first.next = this.first;
			this.size ++;
			this.num_trees++;
			return toInsert;
		}


		toInsert.next = this.first;                 //Insert the node at the begining of the Heap
		HeapNode thisLast = this.first.prev;
		this.first.prev = toInsert;
		toInsert.prev = thisLast;
		thisLast.next = toInsert;
		this.first = toInsert;
		this.size ++;
		this.num_trees++;
		if (toInsert.key < this.min.key)           //Minimum update
			this.min = toInsert;

		return toInsert;
	}

	public void insertTreeAtStart(HeapNode rootToInsert) {          //Insert Node with children at the begining of the heap

		if (this.isEmpty()) {
			this.first = rootToInsert;
			this.min = rootToInsert;
			this.num_trees ++;
			rootToInsert.next = rootToInsert;
			rootToInsert.prev = rootToInsert;	
			size++;
			return;
		}

		rootToInsert.next = this.first;                            //Insert the Tree at the begining of the Heap
		HeapNode thisLast = this.first.prev;
		this.first.prev = rootToInsert;

		rootToInsert.prev = thisLast;
		thisLast.next = rootToInsert;
		this.first = rootToInsert;
		this.num_trees++;

		if(rootToInsert.key < this.min.key)
			this.min = rootToInsert;
		return;

	}
	/**
	 * public void deleteMin()
	 *
	 * Delete the node containing the minimum key.
	 *
	 */
	public void deleteMin() 
	{
		if(this.isEmpty())                                           //Case A: Tree is empty
			return;

		if (this.size == 1) {                                       //Case B: Only 1 node in the Heap
			this.size = 0;
			this.min = null;
			this.first = null;
			this.num_trees = 0;
			this.num_marked = 0;
			return;
		}

		HeapNode z = this.min;										
		HeapNode child = z.child;	
		HeapNode nodeBeforeDeletedMin = z.prev;
		HeapNode nodeAfterDeletedMin = z.next;
		if (this.first.key == z.key) {                     //The minimum is the first Tree in the Tree List
			if (num_trees == 1) {                                        //Only 1 tree in the heap (and he has children)
				this.first = child;
			}
			else {                                                       //Multiple trees in the heap		
				HeapNode last = nodeBeforeDeletedMin;
				if (child != null) {                                             //The minimum has children
					this.first = child;
					child.prev.next = nodeAfterDeletedMin;
					nodeAfterDeletedMin.prev = child.prev;
					last.next = child;

				}
				else {                                                           //The minimum has no children
					this.first = nodeAfterDeletedMin;
					nodeAfterDeletedMin.prev = last;
					last.next = nodeAfterDeletedMin;
				}
			}
		}
		else {                                             //else (the minimum isn't the first Tree)
			z.prev.next = z.next;
			z.next.prev = z.prev;
			if (child!= null) {
				nodeBeforeDeletedMin.next = child;
				child.prev.next = nodeAfterDeletedMin;
				nodeAfterDeletedMin.prev = child.prev;
				child.prev = nodeBeforeDeletedMin;
			}
		}
		num_trees += z.rank;
		this.size--;
		this.consolidate();
		return;

	}

	private void consolidate() {
		int length = (int)Math.ceil((Math.log(this.size + 1))/ Math.log((1+Math.sqrt(5))/2));
		HeapNode [] consArray = new HeapNode [length];
		int rank;
		HeapNode tmp = this.first;
		HeapNode next;
		HeapNode k = tmp;
		do {                                                //Successive linking
			rank = tmp.rank;
			next = tmp.next;
			if(tmp.mark == 1) {
				tmp.mark = 0;
				this.num_marked --;
			}
			if(consArray[rank]==null)
				consArray[rank] = tmp;
			else {
				while(consArray[rank]!=null) {
					tmp =link(tmp, consArray[rank]);
					consArray[rank] = null;
					rank +=1;
				}
				consArray[rank] = tmp;
			}
			tmp = next;
		}

		while (tmp != k);

		FibonacciHeap legal_heap = new FibonacciHeap();                //New heap which will contain the new trees
		int counter =0;                                               //Will contain the new number of trees
		HeapNode toInsert;
		for (int i = length -1;i>=0;i--) {
			toInsert = consArray[i];
			if (toInsert!=null) {
				if(toInsert.parent !=null)
					toInsert.parent = null;
				legal_heap.insertTreeAtStart(toInsert);
				counter ++;
			}

		}
		this.first = legal_heap.first;
		this.min = legal_heap.min;
		this.num_trees = counter;
	}

	private static HeapNode link (HeapNode H1 ,HeapNode H2) {
		links_counter++;
		HeapNode smaller;
		HeapNode bigger;
		if (H1.key > H2.key)
		{
			smaller = H2;
			bigger = H1;
		}

		else {
			smaller = H1;
			bigger = H2;
		}

		if (smaller.child == null) {   //Check if the rank of the linked trees is 0
			smaller.next = smaller;
			smaller.prev = smaller;
			bigger.next = bigger;
			bigger.prev = bigger;
		}
		else {
			HeapNode old_first_child = smaller.child;
			HeapNode last_child = smaller.child.prev;
			bigger.next = old_first_child;
			old_first_child.prev = bigger;
			last_child.next = bigger;
			bigger.prev = last_child;
		}

		smaller.child = bigger;
		bigger.parent = smaller;
		smaller.rank +=1;
		return smaller;


	}


	/**
	 * public HeapNode findMin()
	 *
	 * Return the node of the heap whose key is minimal. 
	 *
	 */
	public HeapNode findMin()
	{
		return this.min;
	} 

	/**
	 * public void meld (FibonacciHeap heap2)
	 *
	 * Meld the heap with heap2
	 *
	 */
	public void meld (FibonacciHeap heap2)                                              
	{
		if (heap2 == null || heap2.isEmpty()) {
			return;
		}

		if (this.isEmpty()) {                         //Case A: Heap is empty
			this.first = heap2.first;
			this.min = heap2.min;
			this.size = heap2.size;
			this.num_trees = heap2.num_trees;
			this.num_marked = heap2.num_marked;
			return;
		}


		HeapNode thisLast = this.first.prev;
		HeapNode heap2Last = heap2.first.prev;
		thisLast.next = heap2.first;
		heap2.first.prev = thisLast;
		heap2Last.next = this.first;
		this.first.prev = heap2Last;
		this.size += heap2.size;
		this.num_trees += heap2.num_trees;
		this.num_marked += heap2.num_marked;
		if (heap2.min.key < this.min.key)
			this.min = heap2.min;
		return;
	}

	/**
	 * public int size()
	 *
	 * Return the number of elements in the heap
	 *   
	 */
	public int size()
	{
		return this.size; // should be replaced by student code
	}

	/**
	 * public int[] countersRep()
	 *
	 * Return a counters array, where the value of the i-th entry is the number of trees of order i in the heap. 
	 * 
	 */
	public int[] countersRep()
	{
		int maxDegree = -1;
		HeapNode temp = this.first;
		if (this.isEmpty()){                     //Return empty array if the Heap is empty
			return new int[0];
		}
		int k = temp.key;
		do {                                     //Find max Degree in the Heap
			if(temp.rank >maxDegree)
				maxDegree = temp.rank;
			temp=temp.next;
		}
		while(temp.key!=k);
		int[] counters = new int[maxDegree+1];
		temp = this.first;
		do {                                       //Create the counters array
			counters[temp.rank]++;
			temp=temp.next;
		}
		while(temp.key!=k);
		return counters;
	}

	/**
	 * public void delete(HeapNode x)
	 *
	 * Deletes the node x from the heap. 
	 *
	 */
	public void delete(HeapNode x)      
	{    
		int k = this.min.key;
		int delta = x.key - k + 1;
		this.decreaseKey(x, delta);
		this.deleteMin();
	}

	/**
	 * public void decreaseKey(HeapNode x, int delta)
	 *
	 * The function decreases the key of the node x by delta. The structure of the heap should be updated
	 * to reflect this chage (for example, the cascading cuts procedure should be applied if needed).
	 */
	public void decreaseKey(HeapNode x, int delta)
	{    
		x.key -= delta;
		if (x.isRoot()) {
			if (x.key < this.min.key)
				this.min = x;
			return;
		}

		if (x.parent.key < x.key) {
			return;	
		}

		this.cascading_cut(x, x.parent);	
		return; 
	}

	public void cascading_cut(HeapNode x, HeapNode y){
		this.cut(x,y);
		if (y.parent != null) {
			if (y.mark == 0) {
				y.mark = 1;
				num_marked++;
			}
			else
				cascading_cut(y,y.parent);

		}
	}

	public void cut(HeapNode x, HeapNode y) {
		cuts_counter++;
		x.parent = null;
		if (x.mark == 1)
			num_marked--;
		x.mark = 0;
		y.rank --;
		if (x.next == x)
			y.child = null;
		else {
			if (y.child == x) {
				y.child = x.next;
			}
			x.prev.next = x.next;
			x.next.prev = x.prev;
		}
		this.insertTreeAtStart(x);
	}

	/**
	 * public int potential() 
	 *
	 * This function returns the current potential of the heap, which is:
	 * Potential = #trees + 2*#marked
	 * The potential equals to the number of trees in the heap plus twice the number of marked nodes in the heap. 
	 */
	public int potential() 
	{    
		return (num_trees + 2*num_marked);
	}

	/**
	 * public static int totalLinks() 
	 *
	 * This static function returns the total number of link operations made during the run-time of the program.
	 * A link operation is the operation which gets as input two trees of the same rank, and generates a tree of 
	 * rank bigger by one, by hanging the tree which has larger value in its root on the tree which has smaller value 
	 * in its root.
	 */
	public static int totalLinks()
	{    
		return links_counter; 
	}

	/**
	 * public static int totalCuts() 
	 *
	 * This static function returns the total number of cut operations made during the run-time of the program.
	 * A cut operation is the operation which diconnects a subtree from its parent (during decreaseKey/delete methods). 
	 */
	public static int totalCuts()
	{    
		return cuts_counter;
	}

	public static int[] kMin(FibonacciHeap H, int k){

		FibonacciHeap tmp = new FibonacciHeap();										
		int [] result = new int [k];							//sorted array of the k minimum numbers
		if (k==0)												//Case A: empty tree
			return result;
		HeapNode currNode;
		HeapNode copyOf;
		int currRank;
		currNode = H.findMin();
		copyOf =tmp.insert(currNode.key);								
		copyOf.prevMe = currNode;
		currRank = currNode.rank;
		tmp.deleteMin();
		HeapNode currChild;
		if(currNode.child!=null ) {	                            //inserting the children of the minimum to temp heap
			currChild = currNode.child;
			for(int i =0; i<currRank;i++) {
				copyOf = tmp.insert(currChild.key);
				copyOf.prevMe = currChild;
				currChild = currChild.next;
			}
			result[0] = currNode.key;							//inserting the k'th number to result array
		}
		else {													//Case B: rank 0 Tree
			result[0] = currNode.key;
			return result;
		}											
		for(int j = 1; j<k ; j++) {								//Case C: repeating the process for the other k-1 nodes
			copyOf = tmp.findMin();
			result[j] = copyOf.key;
			currNode = copyOf.prevMe;
			tmp.deleteMin();
			if(currNode.child != null) {
				currRank = currNode.rank;
				currChild=currNode.child;
				for(int l =0;l<currRank;l++) {
					copyOf = tmp.insert(currChild.key);
					copyOf.prevMe = currChild;
					currChild = currChild.next;
				}
			}
		}


		return result;
	}
	/**
	 * public class HeapNode
	 * 
	 * If you wish to implement classes other than FibonacciHeap
	 * (for example HeapNode), do it in this file, not in 
	 * another file 
	 *  
	 */
	public class HeapNode{
		public int key;
		public int rank;
		public int mark;                        //unmarked = 0, marked = 1
		public HeapNode child;
		public HeapNode next;
		public HeapNode prev;
		public HeapNode parent;
		public HeapNode prevMe;					//used in kMin function - contains pointer to original node

		public HeapNode(int key) {
			this.key = key;
			this.rank = 0;
			this.mark = 0;
		}

		public int getKey() {
			return this.key;
		}

		public boolean isRoot() {
			return this.parent == null;
		}


	}
}
