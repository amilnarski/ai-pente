
public interface GamePlayer {
	public Move move() throws GameManagerUndefinedException;
	public void setGUID(int GUID);
	public void setGameManager(GameManager mgmt);
	public void updateWeights();
}
