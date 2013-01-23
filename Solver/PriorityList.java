
import java.util.Iterator;

public class PriorityList {

	private Node head;
	private Node tail;
	private int size;

	public PriorityList(){
		head = null;
		tail = null;
		size = 0;
	}

	public boolean isEmpty(){
		return size == 0;
	}

	// adds the higher priorities to the back
	public void add(Move move){	
		boolean done = false;
		
		if(head == null){
			Node newNode = new Node(move);
			head = newNode;
			tail = newNode;
			done = true;			
		} else if(move.compareTo(head.item) <= 0){									
			addToFront(move);
			done = true;
		}
		else {			
			for(Node current = head; current != null; current = current.next){				
				if(move.compareTo(current.item) <= 0){
					Node newNode = new Node(move, current, current.previous);
					
					current.previous.next = newNode;
					current.previous = newNode;
					done = true;
					break;
				}
			}
						
		}
		size++;
		
		if(!done){
			addToBack(move);
		}
		
	}		

	private void addToFront(Move move){
		Node newNode = new Node (move, head, null);
		head.previous = newNode;
		head = newNode;
	}

	// not sure if work
	private void addToBack(Move move){
		Node newNode = new Node(move, null, tail);
		tail.next = newNode;
		tail = newNode;
	}

	public void clear(){
		head = null;
		tail = null;
		size = 0;
	}

	public boolean contains(Move m){
		Iterator<Move> iter = this.iterator();
		
		while(iter.hasNext()){
			if((iter.next()).equals(m)){
				return true;
			}
		}
		return false;
	}
	
	public Iterator<Move> iterator(){		
		Iterator<Move> output = new ElementIterator(head);
		return output;
	}
	
	// get from front and remove that node
	public Move poll(){
		// exception
		Move output = head.item;
		head = head.next;
		return output;
	}

	public int size(){
		return size;
	}



	public class ElementIterator implements Iterator<Move> {

		// State variable(s) to be provided.		
		Node counter;

		public ElementIterator ( Node node ) {
			counter = node;
		}

		public boolean hasNext(){
			return counter != null;
		}

		public Move next(){
			// exception?
			Move output = counter.item;
			counter = counter.next;
			return output;
		}

		public void remove ( ) {
			// not used; do not implement
		}
	}
	
	// node class
	private class Node {
		Node next;
		Move item;	
		Node previous;

		private Node(Move move){
			item = move;
			next = null;
		}

		private Node(Move move, Node nextNode, Node prev){
			item = move;
			next = nextNode;
			previous = prev;
		}
	}
}
