
import java.util.HashSet;
import java.util.Iterator;

import junit.framework.TestCase;

public class BoardTest extends TestCase {

	HashSet<Block> goal = new HashSet<Block>();	

	/**
	 * Test of Board Constructors
	 */
	public void testConstuctors()
	{
		goal.add(new Block(1, 1, 3, 1));
		goal.add(new Block(1, 1, 0, 0));
		
		Board B = new Board( 5, 4 , goal);
		B.addBlock(1, 1, 3, 1);
		B.addBlock(1, 1, 0, 0);	
		assertEquals(2, B.getNumOfBlockInGoal());
		try
		{
			B = new Board( 4  , 256 , goal);
			fail();
		}catch( Exception e)
		{
			;
		}



	}

	/**
	 * Test of addBlock method, of class Board.
	 */
	public void testAddBlock()
	{
		Board B = new Board( 5  , 7 , goal);

		//general case. Try adding blocks.
		B.addBlock( 2 , 2 , 0 , 0);
		B.addBlock( 1 , 2 , 2 , 1);

		//Try adding a block at a position that is occupied.
		try
		{
			B.addBlock( 1 , 2 , 0 , 1);
			fail();
		}catch( Exception e)
		{
			;
		}

		//Try adding a block at a position that is out of bounds.
		try
		{
			B.addBlock( 1 , 2 , 2 , -3);
			fail();
		}catch( Exception e)
		{
			;
		}

		//Try adding a block at a position that is out of bounds.
		try
		{
			B.addBlock( 1 , 2 , 2 , 6);
			fail();
		}catch( Exception e)
		{
			;
		}

		//Try adding a block that will not fit.
		try
		{
			B.addBlock( 2 , 2 , 4 , 3);
			fail();
		}catch( Exception e)
		{
			;
		}

	}

	/**
	 * Test of removeBlock method, of class Board.
	 */
	public void testRemoveBlock()
	{

		Board B = new Board( 5  , 4 , goal);

		//general case. Add Blocks and remove them.
		B.addBlock( 2 , 2 , 0 , 0);
		B.addBlock( 1 , 2 , 2 , 1);
		B.removeBlock( 1 , 2 , 2 , 1);

		//Try removing a block at a position that is does not exist.
		B.removeBlock( 2 , 1 , 2 , 2);

		//Try removing a block at a position that is out of bounds.
		try
		{
			B.removeBlock( 1 , 2 , 2 , -3);
			fail();
		}catch( Exception e)
		{
			;
		}

		//Try removing a block at a position that is out of bounds.
		try
		{
			B.removeBlock( 2 , 2 , 2 , 6);
			fail();
		}catch( Exception e)
		{
			;
		}

		//Try removing a block that will not fit.
		try
		{
			B.removeBlock( 2 , 2 , 4 , 3);
			fail();
		}catch( Exception e)
		{
			;
		}
	}

	/**
	 * Test of move method, of class Board.
	 */
	public void testMove()
	{
		Board B = new Board( 5  , 4 , goal);

		//general case. Add Blocks and remove them.
		B.addBlock( 2 , 2 , 0 , 0);
		B.addBlock( 1 , 2 , 2 , 1);
		B.move( 2, 2, 0, 0, 0 , 1, 0);

		//Try moviing a block into a position that is occupied by another block.
		try
		{
			B.move( 2, 2, 0, 1, 2 , 1, 0);
			fail();
		}catch( Exception e )
		{
		}

		//Try moving a block such that it will not be entirely within the board.
		try
		{
			B.move( 1, 2, 2, 1, 2 , 3, 0);
			fail();
		}catch( Exception e )
		{
		}

				
		assertEquals(0, B.getNumOfBlockMatchGoal());
	}

	/**
	 * Test of getPossibleMoves method, of class Board.
	 */
	public void testGetPossibleMoves() {
		
		// empty board
		Board test = new Board(2, 2, goal);		
		PriorityList output = test.getPossibleMoves();		
		assertEquals(output.size(), 0);
		
		// top left corner, bottom right corner = test all 4 edges  
		Board test1 = new Board(5, 4, goal);
		
		test1.addBlock(1, 1, 0, 0); // top left corner
		test1.addBlock(1, 1, 4, 3); // bottom right corner
		
		PriorityList output1 = test1.getPossibleMoves();					
		
		assertEquals(output1.size(), 4);
		assertTrue(output1.contains(new Move(1, 1, 0, 0, 0, 1, 0)));
		assertTrue(output1.contains(new Move(1, 1, 0, 0, 1, 0, 0)));
		assertTrue(output1.contains(new Move(1, 1, 4, 3, 4, 2, 0)));
		assertTrue(output1.contains(new Move(1, 1, 4, 3, 3, 3, 0)));
		
		// nothing surrounding it		
		Board test2 = new Board(5, 4, goal);
		test2.addBlock(1, 1, 1, 2);
		
		PriorityList output2 = test2.getPossibleMoves();
		assertEquals(output2.size(), 4);
		assertTrue(output2.contains(new Move(1, 1, 1, 2, 0, 2, 0)));
		assertTrue(output2.contains(new Move(1, 1, 1, 2, 2, 2, 0)));
		assertTrue(output2.contains(new Move(1, 1, 1, 2, 1, 3, 0)));
		assertTrue(output2.contains(new Move(1, 1, 1, 2, 1, 1, 0)));
		
		// blocks preventing other blocks to move in a certain direction
		Board test3 = new Board(5, 4, goal);
		test3.addBlock(1, 1, 0, 0);
		test3.addBlock(1, 1, 0, 1);
		test3.addBlock(2, 2, 1, 0);
		
		PriorityList output3 = test3.getPossibleMoves();
		assertEquals(output3.size(), 3);
		assertTrue(output3.contains(new Move(1, 1, 0, 1, 0, 2, 0)));
		assertTrue(output3.contains(new Move(2, 2, 1, 0, 1, 1, 0)));
		assertTrue(output3.contains(new Move(2, 2, 1, 0, 2, 0, 0)));
		
		// test the priority
		// goals
		HashSet<Block> theGoal = new HashSet<Block>();
		theGoal.add(new Block(1, 1, 2, 3));
		
		// test just returning the winning move		
		Board test4 = new Board(5, 4, theGoal);
		test4.addBlock(1, 1, 2, 2);
		
		PriorityList output4 = test4.getPossibleMoves();		
		assertEquals(output4.size(), 1); // should just return the winning move
		assertTrue(output4.contains(new Move(1, 1, 2, 2, 2, 3, 1)));
		
		// testing if it returns them in the right order - the one that moves into a goal position should be returned last
		HashSet<Block> theGoal2 = new HashSet<Block>();		
		theGoal2.add(new Block(1, 1, 2, 3));
		theGoal2.add(new Block(2, 2, 1, 1));
		
		Board test5 = new Board(5, 4, theGoal2);
		test5.addBlock(1, 1, 2, 2);
		test5.addBlock(2, 2, 0, 0);
			
		PriorityList output5 = test5.getPossibleMoves();				
		
		assertEquals(output5.size(), 6);
	
		Iterator<Move> iter2 = output5.iterator();
		for(int i = 0; i < 5; i++){
			iter2.next();				
		}
		assertEquals((Move)iter2.next(), new Move(1, 1, 2, 2, 2, 3, 1));
		
		
		// test if it doesn't just return one if possibly it has 2 moves that could win the game
		HashSet<Block> theGoal3 = new HashSet<Block>();		
		theGoal3.add(new Block(1, 1, 2, 3));
		theGoal3.add(new Block(2, 2, 0, 1));
		
		Board test6 = new Board(5, 4, theGoal3);
		test6.addBlock(1, 1, 1, 3);
		test6.addBlock(2, 2, 0, 0);
		
		PriorityList output6 = test6.getPossibleMoves();		 
				
		assertEquals(output6.size(), 5);				
		
		// test if it returns in the right order
		Iterator<Move> iter7 = output6.iterator();
		for(int i = 0; i<3; i++){
			iter7.next();
		}
		
		// these match a goal block so they comes last
		assertEquals(iter7.next(), new Move(1, 1, 1, 3, 2, 3, 1));
		assertEquals(iter7.next(), new Move(2, 2, 0, 0, 0, 1, 1));
		
		
		// testing the priority
		HashSet<Block> theGoal4 = new HashSet<Block>();
		theGoal4.add(new Block(1, 1, 1, 0));
		theGoal4.add(new Block(2, 3, 1, 3));
		theGoal4.add(new Block(1, 1, 0, 2));
		
		Board test7 = new Board(255, 255, theGoal4);
		test7.addBlock(1, 1, 0, 0);
		test7.addBlock(2, 3, 1, 1);
		test7.addBlock(1, 1, 3, 1);		
		
		PriorityList output7 = test7.getPossibleMoves();				
		
		Iterator<Move> iter1 = output7.iterator();
		assertEquals(4, ((Move) iter1.next()).getDistance());
		assertEquals(4, ((Move) iter1.next()).getDistance());
		assertEquals(3, ((Move) iter1.next()).getDistance());
		assertEquals(3, ((Move) iter1.next()).getDistance());
		assertEquals(2, ((Move) iter1.next()).getDistance());
		assertEquals(2, ((Move) iter1.next()).getDistance());
		assertEquals(1, ((Move) iter1.next()).getDistance());
		assertEquals(0, ((Move) iter1.next()).getDistance()); // because it moves into the goal position		
								
		
		// testing updating the numMatchGoal variable
		HashSet<Block> theGoal5 = new HashSet<Block>();
		theGoal5.add(new Block(1, 1, 1, 0));
		theGoal5.add(new Block(2, 3, 0, 2));
		
		Board test8 = new Board(10, 10, theGoal5);
		test8.addBlock(1, 1, 1, 0);
		test8.addBlock(2, 3, 0, 1);
		
		
		PriorityList output8 = test8.getPossibleMoves();
		Iterator<Move> iter5 = output8.iterator();	
		
		assertEquals(-1, ((Move) iter5.next()).getNumMatchGoal());
		assertEquals(-1, ((Move) iter5.next()).getNumMatchGoal());
		assertEquals(0, ((Move) iter5.next()).getNumMatchGoal());
		assertEquals(1, ((Move) iter5.next()).getNumMatchGoal());						
	}
	
	
	public void equals(){
		Board b = new Board(5, 4, goal);
		b.addBlock(1, 1, 2, 2);
		b.addBlock(2, 2, 0, 0);
		b.addBlock(1, 1, 3, 0);
		
		Board b2 = new Board(5, 4, goal);
		b2.addBlock(1, 1, 2, 2);
		b2.addBlock(2, 2, 0, 0);
		b2.addBlock(1, 1, 3, 0);
		
		assertTrue(b.equals(b2));
		
		Board b3 = new Board(10, 10, goal);
		b3.addBlock(1, 1, 2, 2);
		b3.addBlock(2, 2, 0, 0);
		b3.addBlock(1, 1, 3, 0);
		
		assertTrue(b.equals(b3));
	}
	

	public void testHelpers()
	{
		assertTrue(Board.helpersWork());

	}
	
}
