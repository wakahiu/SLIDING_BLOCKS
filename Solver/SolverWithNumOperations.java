import java.io.File;
import java.io.FileNotFoundException;

import java.util.*;


public class SolverWithNumOperations{

	private static Board myBoard;
	private static MoveTrie myTrie = new MoveTrie(); //Pointer to this moves around as we traverse,
	//but we don't lose track of parents due to myParent
	private static Stack<MoveTrie> fringe = new Stack<MoveTrie>();
	private static HashSet<HashSet<Block>> deadEnds = new HashSet<HashSet<Block>>();
	private static HashSet<HashSet<Block>> alreadySeen = new HashSet<HashSet<Block>>();
	private static HashSet<Block> goalConfig = new HashSet<Block>();
	private static int numGoals;
	private static long n; //Number of operations

	public static void solve(){
		//Check if current board is the solution
		if(initialBoardCorrect()){
			n++;
			return;
			//If not, get first set of possible moves and start
		} else {
			//Push empty trie node before everything else to mark that we've reached the end
			//of a node's children during our traversal, so mark it as a dead end.
			MoveTrie marker = new MoveTrie(myTrie);
			n++;
			fringe.push(marker);
			n++;
			myTrie.addChild(marker);
			n++;

			//Get all the possible moves from this board, add them to the current node's children.
			Iterator<Move> nextMoves = myBoard.getPossibleMoves().iterator();
			n++;
			MoveTrie childNode;
			n++;
			while(nextMoves.hasNext()){
				n++;
				childNode = new MoveTrie(nextMoves.next(),myTrie);
				n++;
				myTrie.addChild(childNode);
				n++;
				fringe.push(childNode);
				n++;
			}

			//If there's another move to try, then try it.
			if (!fringe.isEmpty()){
				n++;
				myTrie=fringe.pop();
				n++;
			} else {
				System.exit(1);
			}

			//Keep repeating this procedure until we find the solution (will exit if we do not)
			boolean found = false;
			n++;
			while(!found){
				n++;
				found = search();
				n++;
			}

			printSolution();
			n++;
			
		}
	}

	public static boolean initialBoardCorrect(){
		HashSet<Block> currentBlocks = myBoard.getTray();
		n++;
		Iterator<Block> goals = goalConfig.iterator();
		n++;
		while (goals.hasNext()){
			n++;
			if(!currentBlocks.contains(goals.next())){
				n++;
				n++;
				n++;
				return false;
			}
		}
		n++;
		return true;
	}


	//Main trie traversal method.  Returns true if processing the current node (myTrie) gives the goal config.
	public static boolean search(){
		Move currentMove = myTrie.getMove();
		n++;

		//Corresponds to an end-of-children marker, so add myParent's board to dead ends
		if(currentMove==null){
			n++;
			//Add the parent board to deadEnds
			myTrie = myTrie.getParent();
			n++;
			HashSet<Block> deadEnd = new HashSet<Block>();
			n++;
			Iterator<Block> blocksToAdd = myBoard.getTray().iterator();
			n++;
			Block blockToAdd;
			n++;
			while(blocksToAdd.hasNext()){
				n++;
				blockToAdd = blocksToAdd.next();
				n++;
				deadEnd.add(new Block(blockToAdd.getLength(),blockToAdd.getWidth(),blockToAdd.getR(),blockToAdd.getC()));
				n+=6;
			}
			deadEnds.add(deadEnd);
			n++;

			//Remove children of this node (won't need anymore)
			myTrie.setChildren(null);
			n++;

			//Keep undoing moves by referencing myParent until you reach the parent of nextNode.
			MoveTrie nextNode = new MoveTrie();
			n++;
			if (!fringe.isEmpty()){
				n++;
				nextNode=fringe.pop();
				n++;
			} else {
				n++;
				System.exit(1);
			}
			Move parentMove;
			n++;
			while(myTrie!=nextNode.getParent()){
				n++;
				alreadySeen.remove(myBoard.getTray());
				n++;
				n++;
				parentMove = myTrie.getMove();
				n++;
				myBoard.move(parentMove.getMoveLength(), parentMove.getMoveWidth(),
						parentMove.getEndR(), parentMove.getEndC(),
						parentMove.getStartR(), parentMove.getStartC(),
						parentMove.getNumMatchGoal());
				n+=8;
				myTrie = myTrie.getParent();
				n++;
			}
			//Then step down into it.
			myTrie = nextNode;
			n++;
			return false;


		} else {
			n++;
			//Apply currentMove
			myBoard.move(currentMove.getMoveLength(),currentMove.getMoveWidth(),
					currentMove.getStartR(),currentMove.getStartC(),
					currentMove.getEndR(),currentMove.getEndC(),
					currentMove.getNumMatchGoal());
			n+=8;
			//Check if we have the correct board by checking if currentMove.getNumCorrectBlocks() == num of goals.  If so, end and print.
			if(currentMove.getNumMatchGoal()==numGoals){
				n++;
				n++;
				return true;
			}

			//Check if the new board lies in deadEnds or alreadySeen.  If so, undo move and search(fringe.pop()).
			if(deadEnds.contains(myBoard.getTray())||alreadySeen.contains(myBoard.getTray())){
				n+=5;
				myBoard.move(currentMove.getMoveLength(),currentMove.getMoveWidth(),
						currentMove.getEndR(),currentMove.getEndC(),
						currentMove.getStartR(),currentMove.getStartC(),
						currentMove.getNumMatchGoal());
				n+=8;
				if (!fringe.isEmpty()){
					n++;
					myTrie=fringe.pop();
					n++;
				} else {
					System.exit(1);
				}
				return false;

				//If ok, add board to alreadySeen, then continue with its children
			} else {

				n+=5;
				HashSet<Block> boardSeen = new HashSet<Block>();
				n++;
				Iterator<Block> blocksToAdd = myBoard.getTray().iterator();
				n++;
				Block blockToAdd;
				n++;
				while(blocksToAdd.hasNext()){
					n++;
					blockToAdd = blocksToAdd.next();
					n++;
					boardSeen.add(new Block(blockToAdd.getLength(),blockToAdd.getWidth(),blockToAdd.getR(),blockToAdd.getC()));
					n+=5;
				}
				alreadySeen.add(boardSeen);
				n++;

				//Push empty trie node before everything else to mark that we've reached the end
				//of a node's children during our traversal, so mark the parent node as a dead end.
				MoveTrie marker = new MoveTrie(myTrie);
				n++;
				fringe.push(marker);
				n++;
				myTrie.addChild(marker);
				n++;

				Iterator<Move> nextMoves = myBoard.getPossibleMoves().iterator();
				n++;
				MoveTrie childNode;
				n++;
				while(nextMoves.hasNext()){
					n++;
					childNode = new MoveTrie(nextMoves.next(),myTrie);
					n++;
					myTrie.addChild(childNode);
					n++;
					fringe.push(childNode);
					n++;
				}

				if(!fringe.isEmpty()){
					n++;
					myTrie = fringe.pop();
					n++;
				} else {
					System.exit(1);
				}
				n++;
				return false;
			}

		}

	}


	public static void printSolution(){
		n++;
		ArrayList<Move> backwardsSolution = new ArrayList<Move>();
		n++;
		while(myTrie.getMove()!=null){
			n++;
			backwardsSolution.add(myTrie.getMove());
			n++;
			myTrie = myTrie.getParent();
			n++;
		}
		for(int i=backwardsSolution.size()-1;i>=0;i--){
			n++;
			n++;
			//System.out.println(backwardsSolution.get(i).toString());
			n+=3;
			n++;
		}
	}


	public static void main (String[] args){


		Scanner initConditions = null;
		n++;
		Scanner goalList = null;
		n++;
		String initialConfigString, goalConfigString;
		n++;


		initialConfigString =  args[1];
		n++;
		goalConfigString = args[2];
		n++;


		try {
			initConditions = new Scanner(new File(initialConfigString));
			n++;
		} catch (FileNotFoundException e) {
			System.out.println(e);
		}


		try {
			goalList = new Scanner(new File(goalConfigString));
			n++;
		} catch (FileNotFoundException e) {
			System.out.println(e);
		}

		//Reads the goal configuration file and builds the goalConfig hashset from it
		String goal;
		n++;
		int goalL, goalW, goalX, goalY;
		n++;
		while(goalList.hasNext()){
			n++;
			goal = goalList.nextLine();
			n++;
			goalL = Integer.parseInt(goal.substring(0,goal.indexOf(' ')));
			n++;
			goal = goal.substring(goal.indexOf(' ')+1);
			n++;
			goalW = Integer.parseInt(goal.substring(0,goal.indexOf(' ')));
			n++;
			goal = goal.substring(goal.indexOf(' ')+1);
			n++;
			goalX = Integer.parseInt(goal.substring(0,goal.indexOf(' ')));
			n++;
			goal = goal.substring(goal.indexOf(' ')+1);
			n++;
			if(goal.indexOf(' ')!=-1){
				throw new IllegalArgumentException("Each line in goal configurations file must consist of 4 numbers");
			}
			goalY = Integer.parseInt(goal);
			n++;
			goalConfig.add(new Block(goalL,goalW,goalX,goalY));
			n++;
			n++;
		}
		numGoals = goalConfig.size();
		n++;


		//Reads the dimensions from the initial board file
		String dimensions = initConditions.nextLine();
		n++;
		int numRows = Integer.parseInt(dimensions.substring(0,dimensions.indexOf(' ')));
		n++;
		dimensions = dimensions.substring(dimensions.indexOf(' ')+1);
		n++;
		if(dimensions.indexOf(' ')!=-1){
			throw new IllegalArgumentException("Must supply two dimensions in first line.");
		}
		int numCols = Integer.parseInt(dimensions);
		n++;

		myBoard = new Board(numRows,numCols,goalConfig);
		n++;
		//Adds the blocks specified in the initial board file to the myBoard object
		String blockString;
		n++;
		int blockL, blockW, blockR, blockC;
		n++;
		while(initConditions.hasNext()){
			n++;
			blockString = initConditions.nextLine();
			n++;
			blockL = Integer.parseInt(blockString.substring(0,blockString.indexOf(' ')));
			n++;
			blockString = blockString.substring(blockString.indexOf(' ')+1);
			n++;
			blockW = Integer.parseInt(blockString.substring(0,blockString.indexOf(' ')));
			n++;
			blockString = blockString.substring(blockString.indexOf(' ')+1);
			n++;
			blockR = Integer.parseInt(blockString.substring(0,blockString.indexOf(' ')));
			n++;
			blockString = blockString.substring(blockString.indexOf(' ')+1);
			n++;
			if(blockString.indexOf(' ')!=-1){
				throw new IllegalArgumentException("Each line in initial config file must consist of 4 numbers.");
			}
			blockC = Integer.parseInt(blockString);
			n++;
			myBoard.addBlock(blockL, blockW, blockR, blockC);
			n++;
		}

		myBoard.calculateNumMatchGoal(); 
		n++;
		solve();
		n++;
		System.out.println(n);
	}

}