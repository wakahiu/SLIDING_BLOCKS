
import static org.junit.Assert.*;

import java.util.Iterator;

import org.junit.Test;

public class PriorityListTest {

	@Test
	public void testPriorityList() {
		PriorityList newList = new PriorityList();
		assertEquals(0, newList.size()); // also tests size
	}

	@Test
	public void testAdd() {
		PriorityList newList = new PriorityList();
		Move move1 = new Move(1, 2, 3, 4, 5, 6, 7);
		Move move2 = new Move(2, 3, 4, 5, 6, 7, 7);
		Move move3 = new Move(1, 4, 2, 5, 6, 2, 9);
		move1.setDistance(2);
		move2.setDistance(6);
		move3.setDistance(1);
		
		newList.add(move1);
		newList.add(move2);
		newList.add(move3);
		
		Iterator<Move> iter = newList.iterator();
		assertEquals(iter.next(), new Move(2, 3, 4, 5, 6, 7, 7)); // move2 b/c have most distance to goal
		assertEquals(iter.next(), new Move(1, 2, 3, 4, 5, 6, 7)); // move1 b/c have shorter distance to goal
		assertEquals(iter.next(), new Move(1, 4, 2, 5, 6, 2, 9)); // move3 b/c have more winning goals
	}

	@Test
	public void testClear() {
		PriorityList newList = new PriorityList();
		newList.add(new Move(1, 2, 3, 4, 5, 2, 3));
		newList.add(new Move(2, 3, 4, 5, 2, 1, 4));
		newList.add(new Move(1, 45, 2, 1, 5, 2, 5));
		assertEquals(3, newList.size()); // also tests size
		newList.clear();
		assertEquals(0, newList.size()); // also tests size
		Iterator<Move> iter = newList.iterator();
		assertFalse(iter.hasNext());
	}

	@Test
	public void testContains() {
		PriorityList newList = new PriorityList();
		newList.add(new Move(1, 2, 3, 4, 5, 6, 7));
		newList.add(new Move(0, 2, 4, 1, 3, 5, 3));
		
		assertTrue(newList.contains(new Move(1, 2, 3, 4, 5, 6, 7)));
		assertFalse(newList.contains(new Move(2, 4, 1, 6, 3, 2, 1)));
	}

	@Test
	public void testIterator() {
		PriorityList newList = new PriorityList();
		newList.add(new Move(1, 2, 3, 4, 5, 6, 7));
		newList.add(new Move(2, 5, 2, 5, 8, 7, 3));
		
		Iterator<Move> iter = newList.iterator();
		assertTrue(iter.hasNext());
		assertEquals(new Move(2, 5, 2, 5, 8, 7, 3), iter.next());
		assertTrue(iter.hasNext());
		assertEquals(new Move(1, 2, 3, 4, 5, 6, 7), iter.next());
		assertFalse(iter.hasNext());
	}

}
