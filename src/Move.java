public class Move {
	private int row;
	private int col;
	private boolean isUndoMove;
	public String undoOrPos;
	//public double value;

	public Move(int passedRow, int passedCol) throws MoveException {
		// System.out.println("Move constructor called with " + passedRow + ","
		// + passedCol);
		this.row = --passedRow; // subtract one to go from board coord to array
								// index
		this.col = --passedCol; // subtract one to go from board coord to array
								// index
		// System.out.println("after assignment " + this.row + "," + this.col);
		isUndoMove = false;
		if (this.row > 18 || this.row < 0 || this.col > 18 || this.col < 0) {
			System.out.println("throwing exception " + this.row + ","
					+ this.col);
			System.out.flush();
			throw new MoveException(this.row, this.col);
		}
		undoOrPos = "pos";
	}

	public Move(String undo) {
		isUndoMove = true;
		undoOrPos = "undo";
	}

	public int getRow() {
		return this.row;
	}

	public int getCol() {
		return this.col;
	}

	public boolean isUndoMove() {
		return isUndoMove;
	}

	public boolean equals(Move move) {
		boolean same = false;

		if (isUndoMove = move.isUndoMove()) {
			same = true;
		} else if (row == move.getRow() && col == move.getCol()) {
			same = true;
		}

		return same;
	}
	
	public String toString(){
		return "BoardCoords: ("+row+", "+col+")\tGameCoords: ("+(row+1)+", "+(col+1)+")";
	}
}
