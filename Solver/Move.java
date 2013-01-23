
/**
 * 
 * This class is an object that contains information getPossibleMoves() from the board
 * class passes to the solver class.
 * The move objects contains information on the block length, block width, starting left x coordinate,
 * starting left y coordinate, ending left x coordinate(after move), ending left y coordinate(after move).
 *
 */
public class Move implements Comparable<Move>{

	private int startR;
	private int startC;
	private int moveLength;
	private int moveWidth;

	private int endR;
	private int endC;

	private int numMatchGoal;
	private int distance;

	public Move(int l, int w, int r, int c, int mR, int mC, int numOfBlockMatchGoal){
		startR = r;
		startC = c;
		moveLength = l;
		moveWidth = w;
		endR = mR;
		endC = mC;
		numMatchGoal = numOfBlockMatchGoal;
		distance = 0;
	}

	public void setDistance(int d){
		distance = d;
	}
	
	public int getDistance(){
		return distance;
	}
	
	public int getStartR(){
		return startR;
	}

	public int getStartC(){
		return startC;
	}

	public int getEndR(){
		return endR;
	}

	public int getEndC(){
		return endC;
	}

	public int getMoveLength(){
		return moveLength;
	}

	public int getMoveWidth(){
		return moveWidth;
	}

	public void setNumMatchGoal(int x){
		numMatchGoal = x;
	}

	public int getNumMatchGoal(){
		return numMatchGoal;
	}	

	public boolean equals(Object o){
		if(o instanceof Move){
			return this.moveLength == ((Move)o).moveLength & this.moveWidth == ((Move)o).moveWidth & this.startR == ((Move)o).startR
			& this.startC == ((Move)o).startC & this.endR == ((Move)o).endR & this.endC == ((Move)o).endC;
		} else {
			return false;
		}
	}

	public String toString(){
		return "" + startR + " " + startC + " " + endR + " " + endC;
	}
	
	/**
	 * A move is bigger than another move when its total priority number is bigger than
	 * the other move. The totalPriority is comprised of the numMatchGoal variable and the
	 * priority number; both of which are updated in getPossibleMoves.
	 */
	public int compareTo(Move o) {				
		if(this.numMatchGoal == o.numMatchGoal){
			if(this.distance == o.distance){
				return 0;
			} else if(this.distance < o.distance){
				return 1;
			} else {
				return -1;	
			}
		} else if(this.numMatchGoal < o.numMatchGoal) {
			return -1;
		} else {
			return 1;
		}
	}

}