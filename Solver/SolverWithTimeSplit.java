import java.io.File;
import java.io.FileNotFoundException;

import java.util.*;


public class SolverWithTimeSplit{

	private static Board myBoard;
	private static MoveTrie myTrie = new MoveTrie(); //Pointer to this moves around as we traverse,
	//but we don't lose track of parents due to myParent
	private static Stack<MoveTrie> fringe = new Stack<MoveTrie>();
	private static HashSet<HashSet<Block>> deadEnds = new HashSet<HashSet<Block>>();
	private static HashSet<HashSet<Block>> alreadySeen = new HashSet<HashSet<Block>>();
	private static HashSet<Block> goalConfig = new HashSet<Block>();
	private static int numGoals;
	
	private static long undoTime = 0;
	private static long moveTime = 0;
	private static long checkTime = 0;
	private static long deleteTime = 0;
	private static long configTime = 0;
	private static long start = 0;
	private static long end = 0;
	
	

	/**
	 * First checks if the initial board configuration (myBoard) satisfies all the goals.  If not, the 
	 * first set of possible moves is found and the search begins from these until a solution (if any)
	 * is found. This is done repeatedly calling search() until the solution is found.
	 * If so, the solution is then printed.
	 */
	public static void solve(){
		//Check if current board is the solution
		if(initialBoardCorrect()){
			return;
			//If not, get first set of possible moves and start (unless there's a block that will block critical moves)
		} else {

			//Push empty trie node before everything else to mark that we've reached the end
			//of a node's children during our traversal, so mark it as a dead end.
			start = System.currentTimeMillis();
			MoveTrie marker = new MoveTrie(myTrie);
			fringe.push(marker);
			myTrie.addChild(marker);

			end = System.currentTimeMillis();
			configTime = configTime + (end - start);
			
			
			start = System.currentTimeMillis();
			//Get all the possible moves from this board, add them to the current node's children.
			Iterator<Move> nextMoves = myBoard.getPossibleMoves().iterator();
			MoveTrie childNode;
			while(nextMoves.hasNext()){
				childNode = new MoveTrie(nextMoves.next(),myTrie);
				myTrie.addChild(childNode);
				fringe.push(childNode);
			}
			
			end = System.currentTimeMillis();
			moveTime = moveTime + (end-start);

			
			//If there's another move to try, then try it.
			start = System.currentTimeMillis();
			if (!fringe.isEmpty()){
				myTrie=fringe.pop();
			} else {
				System.exit(1);
			}
			end = System.currentTimeMillis();
			moveTime = moveTime + (end - start);

			//Keep repeating this procedure until we find the solution (will exit if we do not)
			boolean found = false;
			while(!found){
				found = search();
			}

			printSolution();
		}
	}

	/**
	 * Checks if the current board satisfies all the goals.  Only called at the beginning of the search,
	 * to check if any moves need to be attempted at all.
	 * @return True if the initial board satisfies all the goals, false otherwise.
	 */
	public static boolean initialBoardCorrect(){
		start = System.currentTimeMillis();
		HashSet<Block> currentBlocks = myBoard.getTray();
		Iterator<Block> goals = goalConfig.iterator();
		while (goals.hasNext()){
			if(!currentBlocks.contains(goals.next())){
				end = System.currentTimeMillis();
				checkTime = checkTime + (end - start);
				return false;
			}
		}
		end = System.currentTimeMillis();
		checkTime = checkTime + (end - start);
		return true;
	}


	/**
	 * Main MoveTrie traversal method.  Given a pointer to the current location in the trie (myTrie),
	 * this method applies the current move to myBoard and decides whether to continue with another move
	 * (by finding the next set of possible moves and adding them as children nodes), or if the resulting
	 * board has already been seen in the traversal (alreadySeen) to undo and try the next sibling node, 
	 * or if it can be recognized as a board with no successful children moves (deadEnds) to undo as well.
	 * 
	 * A null currentMove corresponds to a "marker" node that denotes the end of a list of children of a node.
	 * Because myBoard is mutated as the trie is traversed, this node tells the method that it must undo moves
	 * to myBoard enough to return to the proper state before which the next actual move can be applied.
	 * 
	 * Traversal is done depth-first with a stack (fringe).  If fringe runs out of elements, then a solution
	 * is deemed impossible and the program exits.
	 * 
	 * @return True if the solution has been found, false otherwise.
	 */
	public static boolean search(){
		start = System.currentTimeMillis();
		Move currentMove = myTrie.getMove();
		end = System.currentTimeMillis();
		moveTime = moveTime + (end - start);

		//Corresponds to an end-of-children marker, so add myParent's board to dead ends
		start = System.currentTimeMillis();
		if(currentMove==null){
			//Add the parent board to deadEnds
			myTrie = myTrie.getParent();
			HashSet<Block> deadEnd = new HashSet<Block>();
			Iterator<Block> blocksToAdd = myBoard.getTray().iterator();
			Block blockToAdd;
			while(blocksToAdd.hasNext()){
				blockToAdd = blocksToAdd.next();
				deadEnd.add(new Block(blockToAdd.getLength(),blockToAdd.getWidth(),blockToAdd.getR(),blockToAdd.getC()));
			}
			deadEnds.add(deadEnd);

			//Remove children of this node (won't need anymore)
			myTrie.setChildren(null);

			end = System.currentTimeMillis();
			deleteTime = (deleteTime + (end - start));

			//Keep undoing moves by referencing myParent until you reach the parent of nextNode.
			start = System.currentTimeMillis();
			MoveTrie nextNode = new MoveTrie();
			if (!fringe.isEmpty()){
				nextNode=fringe.pop();
			} else {
				System.exit(1);
			}
			Move parentMove;
			while(myTrie!=nextNode.getParent()){
				alreadySeen.remove(myBoard.getTray());
				parentMove = myTrie.getMove();
				myBoard.move(parentMove.getMoveLength(), parentMove.getMoveWidth(),
						parentMove.getEndR(), parentMove.getEndC(),
						parentMove.getStartR(), parentMove.getStartC(),
						parentMove.getNumMatchGoal());

				myTrie = myTrie.getParent();
			}
			
			//Then step down into it.
			myTrie = nextNode;
			end = System.currentTimeMillis();
			undoTime = (undoTime + (end - start));
			return false;


		} else {
			//Apply currentMove
			myBoard.move(currentMove.getMoveLength(),currentMove.getMoveWidth(),
					currentMove.getStartR(),currentMove.getStartC(),
					currentMove.getEndR(),currentMove.getEndC(),
					currentMove.getNumMatchGoal());
			
			end = System.currentTimeMillis();
			moveTime = moveTime + (end - start);
			
			
			//Check if we have the correct board by checking if currentMove.getNumCorrectBlocks() == num of goals.  If so, end and print.
			start = System.currentTimeMillis();
			if(currentMove.getNumMatchGoal()==numGoals){
				end = System.currentTimeMillis();
				checkTime = checkTime + (end - start);
				return true;
			}
			end = System.currentTimeMillis();
			checkTime = checkTime + (end - start);


			//Check if the new board lies in deadEnds or alreadySeen.  If so, undo move and search(fringe.pop()).
			start = System.currentTimeMillis();
			if(deadEnds.contains(myBoard.getTray())||alreadySeen.contains(myBoard.getTray())){
				myBoard.move(currentMove.getMoveLength(),currentMove.getMoveWidth(),
						currentMove.getEndR(),currentMove.getEndC(),
						currentMove.getStartR(),currentMove.getStartC(),
						currentMove.getNumMatchGoal());
				if (!fringe.isEmpty()){
					myTrie=fringe.pop();
				} else {
					System.exit(1);
				}
				end = System.currentTimeMillis();
				deleteTime = deleteTime + (end - start);
				return false;		
				

				//If ok, add board to alreadySeen, then continue with its children
			} else {

				HashSet<Block> boardSeen = new HashSet<Block>();
				Iterator<Block> blocksToAdd = myBoard.getTray().iterator();
				Block blockToAdd;
				while(blocksToAdd.hasNext()){
					blockToAdd = blocksToAdd.next();
					boardSeen.add(new Block(blockToAdd.getLength(),blockToAdd.getWidth(),blockToAdd.getR(),blockToAdd.getC()));
				}
				alreadySeen.add(boardSeen);
				end = System.currentTimeMillis();
				moveTime = moveTime + (end - start);

				//Push empty trie node before everything else to mark that we've reached the end
				//of a node's children during our traversal, so mark the parent node as a dead end.
				start = System.currentTimeMillis();
				MoveTrie marker = new MoveTrie(myTrie);
				fringe.push(marker);
				myTrie.addChild(marker);

				Iterator<Move> nextMoves = myBoard.getPossibleMoves().iterator();
				MoveTrie childNode;
				while(nextMoves.hasNext()){
					childNode = new MoveTrie(nextMoves.next(),myTrie);
					myTrie.addChild(childNode);
					fringe.push(childNode);
				}
				end = System.currentTimeMillis();
				moveTime = moveTime + (end - start);
				
				
				start = System.currentTimeMillis();
				if(!fringe.isEmpty()){
					myTrie = fringe.pop();
				} else {
					System.exit(1);
				}
				end = System.currentTimeMillis();
				moveTime = moveTime + (end - start);
				
				return false;
			}

		}

	}


	/**
	 * Prints the solution to the puzzle once it has been found.  Beginning from the MoveTrie node which
	 * brought us to the solution, the trie is traversed upward with myParent pointers (and each Move element
	 * stored into backwardsSolution) until the top node is reached.  This ArrayList is then printed in reverse
	 * order, which is the solution.
	 */
	public static void printSolution(){	
		System.out.println("Time for initial configuration  (seconds): " + (configTime / 1000.0));
		System.out.println("Time for moving (seconds): " + (moveTime / (1000.0)));
		System.out.println("Time for undoing (seconds): " + (undoTime / (1000.0)));
		System.out.println("Time for checking (seconds): " + (checkTime / (10000.0)));
		System.out.println("Time for pruning (seconds): " + (deleteTime / (1000.0)));
		
		System.out.println("\nTime for initial configuration (milliseconds): " + (configTime));
		System.out.println("Time for moving (milliseconds): " + (moveTime));
		System.out.println("Time for undoing (milliseconds): " + (undoTime ));
		System.out.println("Time for checking (milliseconds): " + (checkTime));
		System.out.println("Time for pruning (milliseconds): " + (deleteTime));
	}


	public static void main (String[] args){

		start = System.currentTimeMillis();
		Scanner initConditions = null;
		Scanner goalList = null;
		String initialConfigString, goalConfigString;

		initialConfigString = args[1];
		goalConfigString = args[2];

		try {
			initConditions = new Scanner(new File(initialConfigString));
		} catch (FileNotFoundException e) {
			System.out.println(e);
		}


		try {
			goalList = new Scanner(new File(goalConfigString));
		} catch (FileNotFoundException e) {
			System.out.println(e);
		}

		//Reads the goal configuration file and builds the goalConfig hashset from it
		String goal;
		int goalL, goalW, goalX, goalY;
		while(goalList.hasNext()){
			goal = goalList.nextLine();
			goalL = Integer.parseInt(goal.substring(0,goal.indexOf(' ')));
			goal = goal.substring(goal.indexOf(' ')+1);
			goalW = Integer.parseInt(goal.substring(0,goal.indexOf(' ')));
			goal = goal.substring(goal.indexOf(' ')+1);
			goalX = Integer.parseInt(goal.substring(0,goal.indexOf(' ')));
			goal = goal.substring(goal.indexOf(' ')+1);
			if(goal.indexOf(' ')!=-1){
				throw new IllegalArgumentException("Each line in goal configurations file must consist of 4 numbers");
			}
			goalY = Integer.parseInt(goal);
			goalConfig.add(new Block(goalL,goalW,goalX,goalY));
		}
		numGoals = goalConfig.size();


		//Reads the dimensions from the initial board file
		String dimensions = initConditions.nextLine();
		int numRows = Integer.parseInt(dimensions.substring(0,dimensions.indexOf(' ')));
		dimensions = dimensions.substring(dimensions.indexOf(' ')+1);
		if(dimensions.indexOf(' ')!=-1){
			throw new IllegalArgumentException("Must supply two dimensions in first line.");
		}
		int numCols = Integer.parseInt(dimensions);

		myBoard = new Board(numRows,numCols,goalConfig);

		//Adds the blocks specified in the initial board file to the myBoard object
		String blockString;
		int blockL, blockW, blockR, blockC;
		while(initConditions.hasNext()){
			blockString = initConditions.nextLine();
			blockL = Integer.parseInt(blockString.substring(0,blockString.indexOf(' ')));
			blockString = blockString.substring(blockString.indexOf(' ')+1);
			blockW = Integer.parseInt(blockString.substring(0,blockString.indexOf(' ')));
			blockString = blockString.substring(blockString.indexOf(' ')+1);
			blockR = Integer.parseInt(blockString.substring(0,blockString.indexOf(' ')));
			blockString = blockString.substring(blockString.indexOf(' ')+1);
			if(blockString.indexOf(' ')!=-1){
				throw new IllegalArgumentException("Each line in initial config file must consist of 4 numbers.");
			}
			blockC = Integer.parseInt(blockString);
			myBoard.addBlock(blockL, blockW, blockR, blockC);
		}
		
		myBoard.calculateNumMatchGoal(); 
		end = System.currentTimeMillis();
		configTime = end - start;
		
		solve();
	}

}