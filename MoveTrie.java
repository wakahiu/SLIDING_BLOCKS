import java.util.*;

//Trie object used for building the tree of possible moves in solver.  No wrapper class is
//necessary because every node object has a myParent pointer (used for easy back-tracking and
//printing of the solution).

public class MoveTrie{
	private Move currentMove;
	private ArrayList<MoveTrie> myChildren;
	private MoveTrie myParent;

	public MoveTrie(){
		myChildren = new ArrayList<MoveTrie>();
	}


	public MoveTrie(MoveTrie parent){
		myParent = parent;
	}

	public MoveTrie(Move move, MoveTrie parent){
		currentMove = move;
		myChildren = new ArrayList<MoveTrie>();
		myParent = parent;
	}

	public MoveTrie(Move move, ArrayList<MoveTrie> children, MoveTrie parent){
		currentMove = move;
		myChildren = children;
		myParent = parent;
	}

	public void addChild(MoveTrie child){
		myChildren.add(child);
	}

	public Move getMove(){
		return this.currentMove;
	}
	
	public ArrayList<MoveTrie> getChildren(){
		return this.myChildren;
	}
	
	public MoveTrie getParent(){
		return this.myParent;
	}
	
	public void setChildren(ArrayList<MoveTrie> children){
		this.myChildren = children;
	}

	public boolean isEmpty(){
		return currentMove == null;
	}



}