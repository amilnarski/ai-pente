import java.util.List;

public interface Game{
	public double move(Move moveToMake) throws MoveException;
	public void undoMove();
	public boolean isOver();
	public double getGameValue();
	public List<Move> getAvailableMoves();
	public char[][] getState();
	public double[] getFeatures();
}