import java.io.File;
import java.io.FileNotFoundException;

import java.util.*;


public class SolverWithNumMoves{

	private static Board myBoard;
	private static MoveTrie myTrie = new MoveTrie(); //Pointer to this moves around as we traverse,
	//but we don't lose track of parents due to myParent
	private static Stack<MoveTrie> fringe = new Stack<MoveTrie>();
	private static HashSet<HashSet<Block>> deadEnds = new HashSet<HashSet<Block>>();
	private static HashSet<HashSet<Block>> alreadySeen = new HashSet<HashSet<Block>>();
	private static HashSet<Block> goalConfig = new HashSet<Block>();
	private static int numGoals;
	private static long n; //Number of moves

	public static void solve(){
		//Check if current board is the solution
		if(initialBoardCorrect()){
			return;
			//If not, get first set of possible moves and start
		} else {
			//Push empty trie node before everything else to mark that we've reached the end
			//of a node's children during our traversal, so mark it as a dead end.
			MoveTrie marker = new MoveTrie(myTrie);
			fringe.push(marker);
			myTrie.addChild(marker);

			//Get all the possible moves from this board, add them to the current node's children.
			Iterator<Move> nextMoves = myBoard.getPossibleMoves().iterator();
			MoveTrie childNode;
			while(nextMoves.hasNext()){
				childNode = new MoveTrie(nextMoves.next(),myTrie);
				myTrie.addChild(childNode);
				fringe.push(childNode);
			}

			//If there's another move to try, then try it.
			if (!fringe.isEmpty()){
				myTrie=fringe.pop();
			} else {
				System.exit(1);
			}

			//Keep repeating this procedure until we find the solution (will exit if we do not)
			boolean found = false;
			while(!found){
				found = search();
			}

			//printSolution();
		}
	}

	public static boolean initialBoardCorrect(){
		HashSet<Block> currentBlocks = myBoard.getTray();
		Iterator<Block> goals = goalConfig.iterator();
		while (goals.hasNext()){
			if(!currentBlocks.contains(goals.next())){
				return false;
			}
		}
		return true;
	}


	//Main trie traversal method.  Returns true if processing the current node (myTrie) gives the goal config.
	public static boolean search(){
		Move currentMove = myTrie.getMove();

		//Corresponds to an end-of-children marker, so add myParent's board to dead ends
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

			//Keep undoing moves by referencing myParent until you reach the parent of nextNode.
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
			return false;


		} else {

			//Apply currentMove
			myBoard.move(currentMove.getMoveLength(),currentMove.getMoveWidth(),
					currentMove.getStartR(),currentMove.getStartC(),
					currentMove.getEndR(),currentMove.getEndC(),
					currentMove.getNumMatchGoal());
			n++;
			//Check if we have the correct board by checking if currentMove.getNumCorrectBlocks() == num of goals.  If so, end and print.
			if(currentMove.getNumMatchGoal()==numGoals){
				return true;
			}

			//Check if the new board lies in deadEnds or alreadySeen.  If so, undo move and search(fringe.pop()).
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

				//Push empty trie node before everything else to mark that we've reached the end
				//of a node's children during our traversal, so mark the parent node as a dead end.
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

				if(!fringe.isEmpty()){
					myTrie = fringe.pop();
				} else {
					System.exit(1);
				}
				return false;
			}

		}

	}


	public static void printSolution(){
		ArrayList<Move> backwardsSolution = new ArrayList<Move>();
		while(myTrie.getMove()!=null){
			backwardsSolution.add(myTrie.getMove());
			myTrie = myTrie.getParent();
		}
		for(int i=backwardsSolution.size()-1;i>=0;i--){
			System.out.println(backwardsSolution.get(i).toString());
		}
	}


	public static void main (String[] args){


		Scanner initConditions = null;
		Scanner goalList = null;
		String initialConfigString, goalConfigString;


		//Check to see if there is an option specified, then choose the input files accordingly
		if(args[0].startsWith("-o")){
			String debugArg = args[0].substring(2);
			if(debugArg.equals("Timer")){
				SolverWithTimer.main(args);
				return;
			} else if (debugArg.equals("NumOperations")){
				SolverWithNumOperations.main(args);
			} else if (debugArg.equals("NumMoves")){
				
			} else if (debugArg.equals("SolutionDepth")){
				
			} else if (debugArg.equals("NumBacktracks")){
				
			} else if (debugArg.equals("NumDeadEnds")){
				
			} else if (debugArg.equals("NumLoops")){
				
			} else if(debugArg.equals("isOK")){
				
			} else if (debugArg.equals("options")){
				System.out.println("Timer: Gives the time in msec to solve the puzzle");
				System.out.println("NumOperations: Gives the number of operations used to generate the solution");
				System.out.println("NumMoves: Gives the number of movements used to generate the solution");
				System.out.println("NumBacktracks: Gives the number of times a move is undone in the search for a solution");
				System.out.println("NumDeadEnds: Gives the number of dead end configurations encountered");
				System.out.println("NumLoops: Gives the number of times a move causes the board to return to a previously-seen state in the current path");
				System.out.println("isOK: Calls myBoard's isOK() method after each mutation");
				return;
			}
			initialConfigString = args[1];
			goalConfigString = args[2];
		}
		else{
			initialConfigString =  args[0];
			goalConfigString = args[1];
		}


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
		solve();
		System.out.println(n);
	}

}