import java.util.ArrayList;
import java.util.LinkedList;
import java.util.List;

public class AlphaBetaPlayer implements GamePlayer {

	/**
	 * @param args
	 */
	
	boolean maximize; //whether the player is trying to maximize or minimize the value of the game
	double alpha;
	double beta;
	GameManager mgmt;
	int GUID;
	Move bestMove;
	
	public AlphaBetaPlayer(boolean isMax){
		alpha = Double.NEGATIVE_INFINITY;
		beta = Double.POSITIVE_INFINITY;
		maximize = isMax;
	}
	
	public static void main(String[] args) {
		Game pente = new Pente();
		AlphaBetaPlayer max = new AlphaBetaPlayer(true);
		AlphaBetaPlayer min = new AlphaBetaPlayer(false);
		
		AlphaBetaPlayer turn=max;
		while (!pente.isOver()){
			try {
				pente.move(turn.move(pente));
				((Pente)pente).diagnose();
				if (turn == max){
					turn = min;
				} else {
					turn = max;
				}
			} catch (MoveException e) {
				// TODO Auto-generated catch block
				e.printStackTrace();
			} catch (GameManagerUndefinedException e) {
				// TODO Auto-generated catch block
				e.printStackTrace();
			}
		}
		
		

	}
	public Move move(Game g) throws GameManagerUndefinedException {
		bestMove = null;
		if (maximize){
			maxValue (g, alpha, beta, 20, 0);
		}else {
			minValue (g, alpha, beta, 20, 0);
		}
		return bestMove;
	}
	
	@Override
	public Move move() throws GameManagerUndefinedException {
		if (mgmt == null){
			throw new GameManagerUndefinedException();
		}
		bestMove = null;
		if (maximize){
			maxValue (mgmt.game, alpha, beta, 2, 0);
		}else {
			minValue (mgmt.game, alpha, beta, 2, 0);
		}
		return bestMove;
	}
	


	public double maxValue(Game g, double a, double b, int depthLimit, int depth) {
		if (depth >= depthLimit){
			return a;
		}
		if (g.isOver()) {
			return g.getGameValue();
		} else {
			List<Move> moves = g.getAvailableMoves();
			for (int i = 0; i < moves.size(); i++) {
				try {
					g.move(moves.get(i));
					double v = minValue(g, a, b, depthLimit, ++depth);
					g.move(new Move("undo"));
					if (v > a){
						a = v;
						bestMove = moves.get(i);
					}
					if (a >= b){
						return a;
					}
				} catch (MoveException e) {
					e.printStackTrace();
					System.exit(-500);
				}
				
				
				
			}
		}
		return a;
	}

	public double minValue(Game g, double a, double b, int depthLimit, int depth) {
		if (depth >= depthLimit){
			return b;
		}
		if (g.isOver()) {
			return g.getGameValue();
		} else {
			List<Move> moves = g.getAvailableMoves();
			for (int i = 0; i < moves.size(); i++) {
				try {
					g.move(moves.get(i));
					double v = maxValue(g, a, b, depthLimit, ++depth);
					g.move(new Move("undo"));
					if (v < b){
						b = v;
						bestMove = moves.get(i);
					}
					if (a >= b){
						return b;
					}
				} catch (MoveException e) {
					e.printStackTrace();
					System.exit(-500);
				}
				
				
				
			}
		}
		return b;
	}

	@Override
	public void setGUID(int GUID) {
		this.GUID = GUID;

	}

	@Override
	public void setGameManager(GameManager mgmt) {
		this.mgmt = mgmt;

	}

	@Override
	public void updateWeights() {
		// TODO Auto-generated method stub

	}

}
