public class MoveException extends Exception {

	public MoveException(int row, int col) {
		super();	
		System.out.println("Exception Coordinates: (" + row + ", " + col + ")");
	}
	public MoveException(String reason) {
		super();	
		System.out.println(reason);
	}

	public void MoveException() {
		

	}

}
