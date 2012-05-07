import java.util.Iterator;
import java.util.LinkedList;
import java.util.List;

public class Pente implements Game {

	/**
	 * @param args
	 */
	enum player {
		BLACK, WHITE;
	}

	private enum increment {
		INCREMENT, DECREMENT, SAME;
	}

	// class variables for playing Pente
	PenteBoard board;
	Pente.player turn;
	Pente.player winner;
	GameManager manager;
	int turns;
	private double[] gameWeights;
	protected double[] gameValue;
	// variables for restoring previous state
	private int[][] capturedCoord;
	//private double[] gameValueDiff;
	private boolean moveIsCapture;
	LinkedList<undoMoveObject> undoData;
	private boolean over;
	private boolean debug;
	private boolean[][] available;

	public static void main(String[] args) throws Exception {
		Pente pente = new Pente();
		pente.move(new Move(1, 1)); // B
		pente.diagnose();
		System.out.println(pente);
		pente.move(new Move(1, 10));// W
		pente.diagnose();
		System.out.println(pente);
		pente.move(new Move(2, 2)); // B
		pente.diagnose();
		System.out.println(pente);
		pente.move(new Move(1, 15));// W
		pente.diagnose();
		System.out.println(pente);
		pente.move(new Move(3, 3)); // B
		pente.diagnose();
		System.out.println(pente);
		pente.move(new Move(1, 19));// W
		pente.diagnose();
		System.out.println(pente);
		pente.move(new Move(4, 4)); // B
		pente.diagnose();
		System.out.println(pente);
		pente.move(new Move(1, 12));// W
		pente.diagnose();
		System.out.println(pente);
		pente.move(new Move(5, 5)); // B
		pente.diagnose();
		//pente.move(new Move(1, 9)); // W
		//System.out.println(pente);
		System.out.println("Value: " + pente.getGameValue());

	}
	
	public void diagnose(){
		System.out.println(board);
		System.out.println("Turns: "+ turns);
		System.out.println("Turn: "+turn);
		System.out.println("Over: "+ over);
		System.out.println("Winner: "+winner);
		for (int i = 0; i<gameValue.length;i++){
			System.out.println("Feature "+i+": "+gameValue[i]);
		}
		System.out.println("Capture: "+moveIsCapture);
		
		System.out.println("\t----UNDO DATA----");
		undoMoveObject undo = undoData.peekFirst();
		System.out.println("Move: "+undo.getMove());
		for (int i = 0; i<undo.getCapturedCoord().size();i++){
			System.out.println("Captured: ("+undo.getCapturedCoord().get(i)[0]+","+undo.getCapturedCoord().get(i)[1]+")");
		}
		System.out.println();
		
	}
	
	public Pente() {
		debug = false;
		board = new PenteBoard();
		board.board[8][8].setStatus(Space.status.WHITE);
		turn = player.BLACK; // arbitrarily decided black goes first
		// this.manager = manager;
		this.turns = 1;
		moveIsCapture = false;
		undoData = new LinkedList<undoMoveObject>();
		gameValue = new double[]{0,1,0,0,0,0,0,0};
		//gameValueDiff = new double[8]; // keep the increments that we are
										// doing to the array in here
		initGameWeights();
		over = false;
		available = new boolean[19][19];
		for (int i = 0; i < 19; i++)
			for (int j = 0; j < 19; j++)
				available[i][j] = true;
		available[8][8] = false;
	}
	

	public double move(Move move) throws MoveException {
		if (over) {
			if (move.isUndoMove() == false) {
				throw new MoveException("Game's Over");
			}
		}
		if (move == null){
			throw new MoveException("Null Move");
		}
		int row = move.getRow();
		int col = move.getCol();
		if (!move.isUndoMove()) {
			boolean empty = board.checkEmpty(row, col);
			if (empty) {
				// make the move
				board.move(turn, row, col);
				available[row][col] = false;
				// increment the number of turns
				turns++;

				// update the game state
				updateGameState(move);

				// update the game value to include the value differential we
				// just calculated
				double[] valDiff = undoData.peek().getValueDifference();
				int len = valDiff.length;
				//took out a block comment here
				for (int i = 0; i < len; i++) {
					//System.out.print(valDiff[i]+" ");
					gameValue[i] += valDiff[i];
				}
				/*System.out.println();*/

				// move is no longer available
				available[move.getRow()][move.getCol()] = false;

				// change turns
				switch (turn) {
				case BLACK:
					turn = player.WHITE;
					break;
				case WHITE:
					turn = player.BLACK;
					break;
				}
			} else {
				System.out.println("Row: "+move.getRow()+"\tCol: "+move.getCol());
				throw new MoveException("Move has been taken");
			}
		} else {
			undoMove();
		}

		if (debug)
			diagnose();
		
		//give a reward if the game was just finished
		if (over)
			return 100;
		else
			return 0;
	}

	private void initGameWeights() {
		// 0:black pieces
		// 1:white pieces
		// 2:black doubles
		// 3:white doubles
		// 4:black triples
		// 5:white triples
		// 6:back quadruples
		// 7:white quadruples
		gameWeights = new double[gameValue.length];
		gameWeights[0] = 0.1;
		gameWeights[1] = -0.1;
		gameWeights[2] = 0.2;
		gameWeights[3] = -0.2;
		gameWeights[4] = 0.3;
		gameWeights[5] = -0.3;
		gameWeights[6] = 0.4;
		gameWeights[7] = -0.4;

	}

	public double getGameValue() {
		double valueofGame = 0.0;
		if (!over) {
			for (int i = 0; i < gameValue.length; i++)
				valueofGame += (gameValue[i] * gameWeights[i]);
		} else {
			if (winner==Pente.player.BLACK){
				valueofGame = 100;
			} else {
				valueofGame = -100;
			}
		}
		return valueofGame;
	}

	public void undoMove() {
		undoMoveObject undo;
		try {
			undo = undoData.pop();
		} catch (Exception e) {
			e.printStackTrace();
			System.out
					.println("Recursion on the undo stack is broken. Pop failed.");
			System.exit(-255);
			undo = null;
		}
		// clear the actual square that was moved into
		board.board[undo.getMove().getRow()][undo.getMove().getCol()]
				.setStatus(Space.status.EMPTY);
		available[undo.getMove().getRow()][undo.getMove().getCol()] = true;
		// add any captured pieces back to the board
		/*if (undo.getCapturedCoord() != null) {
			Iterator<int[]> putBack = undo.getCapturedCoord().iterator();
			int count = 0;
			while (putBack.hasNext()) {
				++count;
				int[] pos = putBack.next();
				Space.status opponent;
				if (turn == player.BLACK) {
					opponent = Space.status.WHITE;
					gameValue[0]++;
				} else {
					opponent = Space.status.BLACK;
					gameValue[1]++;
				}
				board.board[pos[0]][pos[1]].setStatus(opponent);
				available[pos[0]][pos[1]] = false;
			}
			if (turn == player.BLACK){
				gameValue[2] += (int)count/2;
			} else {
				gameValue[3] += (int)count/2;
			}
		}*/
		// switch the turn and decrement the total number of turns
		if (turn == player.BLACK) {
			turn = player.WHITE;
		} else {
			turn = player.BLACK;
		}
		turns--;

		// reduce the value of the game
		double[] valueDiff = undo.getValueDifference();
		int iMax = valueDiff.length;
		for (int i = 0; i < iMax; i++) {
			gameValue[i] -= valueDiff[i];
		}

		// if the game is over change the winner
		if (over) {
			over = false;
			winner = null;
		}
	}

	public String toString() {
		return board.toString();
	}


	private void updateGameState(Move move) {
		int direction = 0;
		undoMoveObject moveData = new undoMoveObject(move);
		while (direction < 8) {
			switch (direction) {
			case 0:
				moveData.concatonate(updateGameStateHelper(increment.DECREMENT,
						increment.SAME, move));
//				System.out.println("Down");
				break;
			case 1:
				moveData.concatonate(updateGameStateHelper(increment.DECREMENT,
						increment.INCREMENT, move));
//				System.out.println("Down Right");
				break;
			case 2:
				moveData.concatonate(updateGameStateHelper(increment.SAME,
						increment.INCREMENT, move));
//				System.out.println("Right");
				break;
			case 3:
				moveData.concatonate(updateGameStateHelper(increment.INCREMENT,
						increment.INCREMENT, move));
//				System.out.println("Up Right");
				break;
			case 4:
				moveData.concatonate(updateGameStateHelper(increment.INCREMENT,
						increment.SAME, move));
//				System.out.println("Up");
				break;
			case 5:
				moveData.concatonate(updateGameStateHelper(increment.INCREMENT,
						increment.DECREMENT, move));
//				System.out.println("Down Left");
				break;
			case 6:
				moveData.concatonate(updateGameStateHelper(increment.SAME,
						increment.DECREMENT, move));
//				System.out.println("Left");
				break;
			case 7:
				moveData.concatonate(updateGameStateHelper(increment.DECREMENT,
						increment.DECREMENT, move));
				//System.out.println("Up Left");
				break;
			default:
				System.out.println("Your direction is messed up.");
				System.exit(100);
				break;
			}
			direction++;
		}
		// walk through the value difference and add it to the undo o
		/*
		 * SUSPECT THAT THIS IS VERY MUCH SOMETHING WE DON'T WANT TO DO
		 * SINCE WE CONCATONATED ALL THE DATA FROM THE DIRECTIONS INTO
		 * THE MOVEDATA OBJECT
		 * for (int i = 0; i < gameValueDiff.length; i++) {
			moveData.addValueDiff(i, gameValueDiff[i]);
		}*/
		// update for the piece that we just added
		if (turn == player.BLACK) {
			moveData.addValueDiff(0, 1);
		} else {
			moveData.addValueDiff(1, 1);
		}
		// handle any captured pieces
		/*LinkedList<int[]> remove = moveData.getCapturedCoord();
		int count = 0;
		while (!remove.isEmpty()) {
			++count;
			int[] removeCoord = remove.pop();
			board.clear(removeCoord[0], removeCoord[1]);
			available[removeCoord[0]][removeCoord[1]] = true;
			if (turn == player.BLACK)
				gameValue[0] -= 1;
			else
				gameValue[1] -= 1;

		}
		if (turn == player.BLACK)
			gameValue[2] -= (int)count/2;
		else
			gameValue[3] -= (int)count/2;
*/
		undoData.push(moveData);
		// System.out.println("Pushing a move at " + turns);
	}

	private undoMoveObject updateGameStateHelper(Pente.increment x,
			Pente.increment y, Move move) {
		int row = move.getRow();
		int col = move.getCol();
		// System.out.println("Move: "+row+","+col);

		int currentRow = row; // this will be the row that we are comparing
								// against
		int currentCol = col; // this will be the col that we are comparing
								// against
		// private double[] gameValue;
		// variables for restoring previous state
		// private int[][] capturedCoord;
		// private double[]gameValueDifference;
		// private boolean moveIsCapture;

		undoMoveObject undo = new undoMoveObject(move);
		double[] gameValueDiff = new double[] {0.0,0.0,0.0,0.0,0.0,0.0,0.0,0.0};
		char[] area = { 'e', 'e', 'e', 'e', 'e' };
		//int[] pos1Coord = null; // the coordinates of the first piece that could
								// be capt.
		//int[] pos2Coord = null; // the coord of the second piece that could be
								// capt.
		// set everything here to nope, then change as we move through the code.
		for (int distance = 1; distance < 5; distance++) {
			// update the indices that we are looking at
			switch (x) {
			case INCREMENT:
				currentRow = row + distance;
				break;
			case DECREMENT:
				currentRow = row - distance;
				break;
			case SAME:
				currentRow = row;
				break;
			}
			switch (y) {
			case INCREMENT:
				currentCol = col + distance;
				break;
			case DECREMENT:
				currentCol = col - distance;
				break;
			case SAME:
				currentCol = col;
				break;
			}

			// use the indices to look at the board
			if (currentRow < 0 || currentRow > 18 || currentCol < 0
					|| currentCol > 18) {
				// bail out, leaving the array as 'e': empty
			} else {
				// write the value of the space to the area[]
				if (board.board[currentRow][currentCol].getStatus() == Space.status.BLACK) {
					area[distance] = 'b';
				} else if (board.board[currentRow][currentCol].getStatus() == Space.status.WHITE) {
					area[distance] = 'w';
				}
				/*if (distance == 1) {
					pos1Coord = new int[] { currentRow, currentCol };
				} else if (distance == 2) {
					pos2Coord = new int[] { currentRow, currentCol };
				}*/
			}

		}// end for

		// set up any temp variables we may need
		//moveIsCapture = false;
		char player = '\n';
		// System.out.println(row+", "+col);
		// System.out.println(move.undoOrPos);
		switch (board.board[row][col].getStatus()) {
		case BLACK:
			player = 'b';
			break;
		case WHITE:
			player = 'w';
			break;
		}
	
		
		area[0] = player; // set the first position in the array to be the
							// player who made the move

//		//DEBUG <-------------------------------------------------------------
//		for (int i = 0; i<area.length;i++)
//			System.out.print(area[i]+" ");
//		System.out.println();
//		//DEBUG <-------------------------------------------------------------
		
		// for all of the following lines just update the value difference
		// array, we'll apply the changes after this block.
		boolean two = (area[0] == player && area[1] == player);
		boolean three = (two && area[2] == player);
		boolean four = (three && area[3] == player);
		boolean five = (four && area[4] == player);
		//System.out.println("2,3,4,5: "+" "+two+" "+three+" "+four+" "+five);
		if (five) {
			over = true;// game is over
			winner = turn;
			// value of the game is now the maximum value
			// we'll handle changing the value in getValue()
		} else if (four) {
			if (area[4] == 'e') {
				// quadruple
				if (area[0] == 'b') {
					gameValueDiff[6] += 1;// add a quad
					gameValueDiff[4] -= 1; // subtract a triple
				} else {
					gameValueDiff[7] += 1; // add a quad
					gameValueDiff[5] -= 1; // subtract a triple
				}
			} else {
				// nada, since the next move is occupied by our enemy
				// decrement the number of quads since we lost one here?
				if (area[0] == 'b') {
					gameValueDiff[6] -= 1;
				} else {
					gameValueDiff[7] -= 1;
				}
			}
		} else if (three) {
			if (area[3] == 'e') {
				// triple
				if (area[0] == 'b') {
					gameValueDiff[4] += 1;// add a triple
					gameValueDiff[2] -= 1; // subtract a double
				} else {
					gameValueDiff[5] += 1; // add a triple
					gameValueDiff[3] -= 1; // subtract a double
				}
			} else {
				// nada, since the next move is occupied by our enemy
				// decrement the number of triples since we lost one here?
				if (area[0] == 'b') {
					gameValueDiff[4] -= 1;
				} else {
					gameValueDiff[5] -= 1;
				}
			}
		} else if (two) {
			if (area[2] == 'e') {
				// double
				if (area[0] == 'b') {
					gameValueDiff[2] += 1; // add a double
				} else {
					gameValueDiff[3] += 1; // add a double
				}
			} else {
				// nada, since the next move is occupied by our enemy
				// decrement the number of doubles since we lost one here?
				if (area[0] == 'b') {
					gameValueDiff[2] -= 1; // subtract a double
				} else {
					gameValueDiff[3] -= 1; // subtract a double
				}
			}
		} else {
			if (area[0] == area[3] && area[1] == area[2] && area[1] != 'e') {
				// this is a capture
				//MAY 05 - MADE DECISION TO ABANDON CAPTURES FROM THE GAME LOGIC
				//moveIsCapture = true;
				// System.out.println("Capture!");
				//undo.addCoord(pos1Coord);
				//undo.addCoord(pos2Coord);
			} else {
				// we either have just this one piece or something crazy is
				// happening

				// update the single piece in updateGameState() so we don't over
				// count
				
				 // if (player == 'b') { gameValueDiff[0]++; } else {
				 // gameValueDiff[1]++; }
				 
			}
		}
		
		
		undo.setValueDifference(gameValueDiff);

		return undo;
	}

	public boolean isOver() {
		return over;
	}

	public char[][] getState() {

		char[][] state = new char[19][19];
		for (int i = 0; i < 19; i++) {
			for (int j = 0; j < 19; j++) {
				switch (board.board[i][j].getStatus()) {
				case WHITE:
					state[i][j] = 'W';
					break;
				case BLACK:
					state[i][j] = 'B';
					break;
				case EMPTY:
					state[i][j] = 'E';
					break;
				}
			}
		}
		return state;
	}

	public void displayState() {
		for (int i = 0; i < available.length; i++) {
			System.out.println();
			for (int j = 0; j < available[0].length; j++) {
				if (available[i][j])
					System.out.print('T');
				else
					System.out.print('F');
			}
		}
	};

	public List<Move> getAvailableMoves() {
		List<Move> moves = new LinkedList<Move>();
		for (int i = 0; i < available.length; i++) {
			for (int j = 0; j < available[0].length; j++) {
				if (available[i][j]) {
					try {
						moves.add(new Move(i + 1, j + 1));
					} catch (Exception e) {
						e.printStackTrace();
						System.exit(-333);
					}
				}
			}
		}
//		System.out.println("Avail Moves: "+moves.size());
		return moves;
	}

	public double[] getFeatures() {
		return gameValue;
	}
}
class PenteBoard {
	Space[][] board;

	public PenteBoard() {
		this.board = new Space[19][19];

		// init all spaces on the board so that it's empty
		for (int i = 0; i < board.length; i++)
			for (int j = 0; j < board.length; j++)
				board[i][j] = new Space();

	}

	public void move(Pente.player player, int row, int col) {
		// row = row - 1;
		// col = col - 1;
		switch (player) {
		case BLACK: {
			board[row][col].setStatus(Space.status.BLACK);
			break;
		}
		case WHITE: {
			board[row][col].setStatus(Space.status.WHITE);
			break;
		}
		}
	}

	public void clear(int row, int col) {
		// row = row - 1;
		// col = col - 1;
		board[row][col].currentStatus = Space.status.EMPTY;
	}

	public boolean checkEmpty(int row, int col) {
		boolean empty = false;
		// row = row - 1;
		// col = col - 1;
		switch (board[row][col].currentStatus) {
		case EMPTY:
			empty = true;
			break;
		default:
			break;
		}
		return empty;
	}

	public String toString() {
		String display = "";
		display += "-----------------------------------------------------------------------------------" + '\n';
		display += "\t 0   1   2   3   4   5   6   7   8   9   10  11  12  13  14  15  16  17  18\n\n";
		for (int row = 0; row < board.length; row++) {
			display += row + "\t";
			for (int col = 0; col < board.length; col++) {
				// for each column in a row
				display += "[";
				switch (board[row][col].currentStatus) {
				case BLACK:
					display += "B] ";
					break;
				case WHITE:
					display += "W] ";
					break;
				default:
					display += " ] ";
					break;
				}
			}
			display += '\n';

		}
		display += "-----------------------------------------------------------------------------------" + '\n';
		return display;
	}

}

class Space {
	static enum status {
		EMPTY, WHITE, BLACK;
	}

	Space.status currentStatus;

	public Space() {
		this.currentStatus = status.EMPTY;
	}

	public Space.status getStatus() {
		return currentStatus;
	}

	public void setStatus(Space.status newStatus) {
		currentStatus = newStatus;
	}
}
