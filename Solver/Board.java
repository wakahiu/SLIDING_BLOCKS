
import java.util.Iterator;
import java.util.HashSet;

public class Board
{
	private HashSet<Block> tray;
	private boolean[][] occupied;
	private int length;
	private int width;	
		
	private HashSet<Block> unoccupiedGoal;
	private HashSet<Block> occupiedGoal;
	
	private int numOfBlockMatchGoal;
	private int numOfBlockInGoal;

	public HashSet<Block> getTray(){
		return tray;
	}

	public int getLength(){
		return length;
	}
	public int getWidth(){
		return width;
	}
	
	// for testing purposes
	public int getNumOfBlockMatchGoal(){
		return numOfBlockMatchGoal;
	}
	public int getNumOfBlockInGoal(){
		return numOfBlockInGoal;
	}
	
	/**
	 * Generate the Hashcode of this object
	 */

	public int hashCode()
	{
		int rtn = this.length << 24 | this.width;
		Iterator <Block> iter = this.tray.iterator();
		int i = 0;
		while( iter.hasNext() )
		{
			Block B = iter.next();
			if( B != null )
			{
				rtn += B.hashCode();
				rtn <<= i * i;
			}
			i++;
		}
		return rtn | rtn << width ;
	}

	public String toString()
	{
		String str = new String("");
		for( int i = 0 ; i < length ; i++)
		{
			for( int j = 0 ; j < width  ; j++)
			{
				if( occupied[i][j] )
				{
					str += 1 + " ";
				}else
				{
					str += 0 + " ";
				}
			}
			str += "\n";
		}

		str += "\nThe Block: ";

		str += this.tray.toString();
		return str;
	}

	public Board(int length , int width)
	{
		if( length < 0 || length > 255 || width < 0 || width > 255 )
		{
			throw new IllegalArgumentException( "Dimensions have to be between 0 and 256" );
		}
		this.length = length;
		this.width = width;
		tray = new HashSet<Block>();
		occupied = new boolean[length][width];

	}

	/**
	 * Constructs the board. Puts the goalconfig into unoccupiedGoal (blocks in goal
	 * config that are not satisfied) and then sets goal to null.
	 * @param length The length of the goal.
	 * @param width The width of the goal.
	 * @param goalconfig The list of goals.
	 */
	public Board(int length , int width, HashSet<Block> goalconfig)
	{
		if( length < 0 || length > 255 || width < 0 || width > 255 )
		{
			throw new IllegalArgumentException( "Dimensions have to be between 0 and 256" );
		}

		this.length = length;
		this.width = width;
		tray = new HashSet<Block>();
		occupied = new boolean[length][width];		
		unoccupiedGoal = goalconfig;
		occupiedGoal = new HashSet<Block>();

		numOfBlockMatchGoal = 0; // number of blocks in board that match the goal
		numOfBlockInGoal = 0; // number of blocks on the board


		Iterator<Block> iter1 = unoccupiedGoal.iterator();
		while(iter1.hasNext()){
			iter1.next();
			numOfBlockInGoal++;					
		}
	}
	
	/**
	 * This updates numOfBlockMatchGoal in the beginning before populating the trie.
	 * It also addes the goal blocks to either occupiedGoal (blocks in the goal that
	 * are already satisfied) or unoccupiedGoal (blocks in the goal that aren't satisfied
	 * yet.
	 */
	public void calculateNumMatchGoal(){			
		numOfBlockMatchGoal = occupiedGoal.size();
	}

	/**
	 * Removes a block from the board
	 * @param L the length of the block to be removed
	 * @param W the width of the block to be removed.
	 * @param r the row of the block to be removed.
	 * @param c column of the block to be removed
	 */
	public void addBlock( int L , int W , int r , int c)
	{
		if( L < 0 || L > this.length || W < 0 || W > this.width )
		{
			throw new IllegalArgumentException( "The block dimension must be non-negative and mush be smaller than the tray" );
		}
		if( L < 0 || L > this.length || W < 0 || W > this.width )
		{
			throw new IllegalArgumentException( "The new position [" + r + ","+ c + "] is out of bounds." );
		}
		if(  L + r > this.length || W + c > this.width )
		{			
			throw new IllegalArgumentException( "The block cannot fit entirely at that new position [" + r + ","+ c + "]." );
		}

		for( int i = c ; i < c + W ; i++)
		{
			for( int j = r ; j < r + L ; j++)
			{
				if( occupied[j][i] )
				{
					throw new IllegalArgumentException( "The Block cannot be added at an position that is already occupied." );
				}


			}
		}

		Block B = new Block( (int)L , (int)W , (int)r , (int)c);
		this.tray.add(B);
		for( int i = r ; i < r + L ; i++)
		{
			for( int j = c ; j < c + W ; j++)
			{
				occupied[i][j] = true;				
			}
		}
		
		if(unoccupiedGoal.isEmpty()){ // or null?
			
		} else if(unoccupiedGoal.contains(B)){
			unoccupiedGoal.remove(B);
			occupiedGoal.add(B);
		}

	}

	/**
	 * Removes the a block from a tray.
	 *
	 * @param L The length of the block to be removed
	 * @param W the width of the block to be removed
	 * @param r the row of the block to be removed.
	 * @param c the column of the block to be removed.
	 */
	public void removeBlock(  int L , int W , int r , int c )
	{
		if( r < 0 || r > this.length || c < 0 || c > this.width )
		{
			throw new IllegalArgumentException( "The block dimension must be non-negative and mush be smaller than the tray" );
		}
		if(  L + r > this.length || W + c > this.width )
		{
			throw new IllegalArgumentException( "The block cannot fit entirely at that new position [" + r + ","+ c + "]." );
		}
		this.tray.remove( new Block( (int)L , (int)W , (int)r , (int)c ) );

		for( int i = r ; i < r + L ; i++)
		{
			for( int j = c ; j < c + W ; j++)
			{
				occupied[i][j] = false;
				
			}
		}
		
		Block current = new Block(L, W, r, c);
		if(occupiedGoal.isEmpty()){		 			
			
		} else if(occupiedGoal.contains(current)){
			occupiedGoal.remove(current);
			unoccupiedGoal.add(current);
		}
	}

	/**
	 * Moves a block from one location to another.
	 * @param startR :  the current row
	 * @param startC :  the current column
	 * @param length :  the blocks length;
	 * @param width :   the blocks width;
	 * @param endR  :   the row in which to move the block.
	 * @param endC  :   the column in which to move the block
	 */

	public void move(int length , int width, int startR, int startC , int endR , int endC, int numMatch)
	{
		try
		{
			this.removeBlock( length , width , startR , startC );
		}catch( Exception e)
		{
			throw new IllegalArgumentException( "The Block cound not be moved because it is not conatained in the tray" );
		}

		try
		{
			this.addBlock( length, width, endR, endC);
		}catch( Exception e)
		{
			this.addBlock( length, width, startR, startC);
			throw new IllegalArgumentException( "The Block could not be moved since the position it is tryig to move into is either occupied, out of bounds or it cannot fit entirely" );
		}
		numOfBlockMatchGoal = numMatch;
	}

	/**
	 * Returns all the possible moves at the current tray configuration.
	 * It returns the possible moves in priority order, moves more likely to solve the puzzle
	 * are returned towards the back of the list.
	 * @return posslbeMoves = a PriorityList of Move objects.
	 */
	public PriorityList getPossibleMoves() {
		PriorityList possibleMoves = new PriorityList(); // gonna be returned						

		Iterator<Block> iter = this.tray.iterator();
		while(iter.hasNext()){
			boolean canMove = true;
			Block current = (Block) iter.next();

			int blockLength = current.getLength();
			int blockWidth = current.getWidth();
			int R = current.getR();
			int C = current.getC();						

			boolean up = false;
			boolean down = false;
			boolean left = false;
			boolean right = false;

			Object[] closestBlock = findClosestGoalBlock(current);	
			
			// which direction/directions is the goal block in			
			if(closestBlock[0] != null){
				if(((Block)closestBlock[0]).getR() > current.getR()){
					down = true;
				} else if(((Block)closestBlock[0]).getR() < current.getR()){
					up = true;
				}
				if(((Block)closestBlock[0]).getC() > current.getC()){
					right = true;
				} else if(((Block)closestBlock[0]).getC() < current.getC()){
					left = true;
				}
			}
			
			// check four sides:
			// top side
			for(int i = C; i < C+blockWidth; i++){
				if(R-1 < 0 || occupied[R-1][i] == true) {
					canMove = false;
					break;
				}
			}
			getPossibleMovesHelper(possibleMoves, up, current, canMove, blockLength, blockWidth, R, C, R-1, C, (Integer)closestBlock[1]);
			

			// left side
			canMove = true;
			for(int j = R; j < R+blockLength; j++){
				if(C-1 < 0 || occupied[j][C-1] == true) {
					canMove = false;
					break;
				}
			}
			
			getPossibleMovesHelper(possibleMoves, left, current, canMove, blockLength, blockWidth, R, C, R, C-1, (Integer)closestBlock[1]);

			// bottom side
			canMove = true;
			for(int k = C; k < C+blockWidth; k++){						
				if(R+blockLength >= length || occupied[R+blockLength][k] == true) {
					canMove = false;
					break;
				}
			}
			
			getPossibleMovesHelper(possibleMoves, down, current, canMove, blockLength, blockWidth, R, C, R+1, C, (Integer)closestBlock[1]);

			// right side
			canMove = true;
			for(int s = R; s < R+blockLength; s++){				
				if(C+blockWidth >= width || occupied[s][C+blockWidth] == true) {					
					canMove = false;
					break;
				}
			}
			
			getPossibleMovesHelper(possibleMoves, right, current, canMove, blockLength, blockWidth, R, C, R, C+1, (Integer)closestBlock[1]);

		}
		return possibleMoves;
	}
	
	/**
	 * It finds the goal block that is closest to the current block and the distance. 
	 *
	 * @param current The current block being looked at in the tray.
	 * 
	 * @return output An array of objects where the first element is the closest goal block to the current block being looked at
	 * and the second element is the distance from the current block to the goal block.
	 */

	private Object[] findClosestGoalBlock(Block current){	
		Iterator<Block> goalBlocks = unoccupiedGoal.iterator();
		int sofarMinDistance = Integer.MAX_VALUE;
		Block sofarClosestBlock = null;		

		while(goalBlocks.hasNext()){

			Block currentInTray = goalBlocks.next();
			if(current.getLength() == currentInTray.getLength()
					&& current.getWidth() == currentInTray.getWidth()){					
				int distance = Math.abs(current.getR() - currentInTray.getR()) + Math.abs(current.getC() - currentInTray.getC());
				if(distance < sofarMinDistance){
					sofarMinDistance = distance;
					sofarClosestBlock = currentInTray;
				}
			}
		}	
		Object[] output = new Object[2];
		output[0] = sofarClosestBlock;
		output[1] = sofarMinDistance;
		return output;
	}
	
	/**
	 *It updates the Move's numMatchGoal variable to reflect how many goals
	 * will be met if the possible move was made. It also gives it a priority 
	 * (the distance the block will be from the goal after the move). 
	 * After all this is updated, it adds the move into possibleMoves.
	 * If the current move is the the last move needed to solve the game, 
	 * it just returns that move. 
	 * @param possibleMoves The list of possible moves added so far.
	 * @param direction The boolean that says whether this direction will bring the block closer to the goal.
	 * @param current The current block being looked at in the tray.
	 * @param canMove A boolean indicating whether the move in this direction is possible.
	 * @param blockLength The length of the block.
	 * @param blockWidth The width of the block.
	 * @param startR The row the left corner of the block is at before the move.
	 * @param startC The column the left corner of the block is at before the move.
	 * @param endR The row the left corner of the block is at after the move.
	 * @param endC The column the left corner of the block is at after the move. 
	 * @param distance The distance from the current block to the closest goal block.
	 */
	
	private void getPossibleMovesHelper(PriorityList possibleMoves, boolean direction, Block current, boolean canMove, int blockLength, int blockWidth, int startR, int startC, int endR, int endC, int distance){
		if(canMove == true){
			Move output = new Move (blockLength, blockWidth, startR, startC, endR, endC, numOfBlockMatchGoal);
			Block compareBlock = new Block(output.getMoveLength(), output.getMoveWidth(), output.getEndR(), output.getEndC()); 

			
			// if the current block is already in a goal position, the next move would decrease the number of blocks
			// in the tray that match the goals
			if(occupiedGoal.contains(current)){
				output.setNumMatchGoal(output.getNumMatchGoal()-1); 
			}				
			// if the next move moves the block into a goal position, update numMatchGoal
			if(unoccupiedGoal.contains(compareBlock)){
				output.setNumMatchGoal(output.getNumMatchGoal()+1);					
				if(output.getNumMatchGoal() == numOfBlockInGoal){ // just return the winning move
					possibleMoves.clear();
					possibleMoves.add(output);
					return;
				}
			} else {
				// for priority - those with higher priority moves to back of list				
				
				if(direction){
					output.setDistance(distance-1); 
				} else {
					output.setDistance(distance+1);
				}

			}
			possibleMoves.add(output);
		}
	}
	
	/**
	 * It checks if two boards are equal or not.
	 */
	public boolean equals(Object o){
		//Check if o is a Board
		if (!(o instanceof Board)){
			return false;
		}
		//Check if dimensions are the same
		if ((this.tray.size()!=((Board) o).tray.size())||
				(this.length!=((Board) o).length)||
				(this.width!=((Board) o).width)){
			return false;
		}
		//If there are a lot of blocks, compare occupied array, else compare the tray of blocks
		if(4*this.tray.size()>this.length*this.width){
			return this.occupied.equals(((Board) o).occupied);
		} else {
			return this.tray.equals(((Board) o).tray);
		}
	}

	public boolean isOK(){
		if( length < 1 || length > 256 || width < 1 || width > 256 )
		{
			throw new IllegalStateException( "Tray Dimensions have to be between 0 and 256" );
		}
		Iterator <Block> iter = this.tray.iterator();
		while( iter.hasNext() )
		{
			Block B = iter.next();
			boolean[][] occupiedRecheck = new boolean [length][width];
			if( B != null )
			{
				if( B.getC() < 0 || B.getC() >= this.width || B.getR() < 0 && B.getR() >= this.length )
				{
					throw new IllegalStateException( "The Block must be positioned in the tray." );
				}
				if( B.getWidth() < 1 ||  B.getWidth() > this.width || B.getLength() < 1 || B.getLength() > this.length )
				{
					throw new IllegalStateException( "The Block dimensions should be less than the tray dimensions " );
				}
				if( B.getC() + B.getWidth() > this.getWidth() ||  B.getLength() + B.getR() > this.getLength() )
				{
					throw new IllegalStateException( "The block has to be within the confines of the tray, i.e it should not \'stick out\' of the tray.");
				}
				for( int i = B.getC() ; i < B.getC() + B.getWidth() ; i++)
				{
					for( int j = B.getR() ; j < B.getR() + B.getLength() ; j++)
					{
						if( occupiedRecheck[j][i] )
						{
							throw new IllegalStateException( "Two Blocks overlap at position (" + j + "," + i + ")" );
						}

					}
					continue;
				}

			}
		}
		return true;
	}
	
	// tests for findClosestGoalBlock
	private static boolean findClosestGoalBlockWorks(){
		HashSet<Block> testGoal = new HashSet<Block>();
		testGoal.add(new Block(1, 2, 3, 4));
		testGoal.add(new Block(1, 2, 0, 0));
		testGoal.add(new Block(2, 2, 6, 2));
		Board test = new Board(10, 10, testGoal);
		boolean output = true;

		Object[] answer = new Object[2];
		answer[0] = new Block(1, 2, 3, 4);
		answer[1] = 5;
		output = output && test.findClosestGoalBlock(new Block(1, 2, 6, 6))[0].equals(answer[0]) &&
		test.findClosestGoalBlock(new Block(1, 2, 6, 6))[1] == answer[1] &&
		test.findClosestGoalBlock(new Block(1, 1, 0, 0))[0] == null; // if the current block doesn't have a goal block		

		Object[]answer2 = new Object[2];
		answer2[0] = new Block(2, 2, 6, 2);
		answer2[1] = 1;
		output = output && test.findClosestGoalBlock(new Block(2, 2, 6, 1))[0].equals(answer2[0]) &&
		test.findClosestGoalBlock(new Block(2, 2, 6, 1))[1] == answer2[1];
			
		return output;
	}
	
	// tests for getPossibleMovesHelper
	public static boolean getPossibleMovesHelperWorks(){
		HashSet<Block> testGoal = new HashSet<Block>();		
		Board test = new Board(10, 10, testGoal);			
		boolean output = true;
		PriorityList possibleMoves = new PriorityList();

		test.getPossibleMovesHelper(possibleMoves, false, new Block(1, 1, 0, 0), false, 1, 1, 0, 0, -1, 0, 0); // distance doesn't matter b/c can't move in this direction
		test.getPossibleMovesHelper(possibleMoves, true, new Block(1, 2, 0, 1), true, 1, 2, 0, 1, 2, 1, 6); 
		test.getPossibleMovesHelper(possibleMoves, false, new Block(1, 1, 0, 0), true, 1, 1, 0, 0, 1, 0, Integer.MAX_VALUE-1); // distance doesn't matter b/c no goal block, its just a big number

		Iterator<Move> iter = possibleMoves.iterator();	
				
		output = output && iter.next().equals(new Move(1, 1, 0, 0, 1, 0, 0)); // comes first because no goal block
		output = output && iter.next().equals(new Move(1, 2, 0, 1, 2, 1, 0)); // comes second because there is a goal block

	return output;
	
}
	
	public static boolean helpersWork(){
		if(!findClosestGoalBlockWorks()){
			return false;	
		}

		if(!getPossibleMovesHelperWorks()){
			return false;
		}
		return true;
	}
}