
import java.util.LinkedList;
import java.util.List;

public class NewAlphaBetaPlayer extends AlphaBetaPlayer implements GamePlayer {


	/**
	 * @param args
	 */
	
	boolean maximize; //whether the player is trying to maximize or minimize the value of the game
	double alpha;
	double beta;
	GameManager mgmt;
	int GUID;
	Move bestMove;
	LinkedList <Move> moveList;
	int depth = 3;
	
	public NewAlphaBetaPlayer(boolean isMax){
		super(isMax);
		alpha = Double.NEGATIVE_INFINITY;
		beta = Double.POSITIVE_INFINITY;
		maximize = isMax;
	}
	
	public static void main(String[] args) {
		Game pente = new Pente();
		System.out.println(pente);
		AlphaBetaPlayer max = new NewAlphaBetaPlayer(true);
		AlphaBetaPlayer min = new NewAlphaBetaPlayer(false);
		
		AlphaBetaPlayer turn=max;
		int numTurns = 3;
		int turns = 1;
		while ( /*turns<numTurns*/!pente.isOver()){
			try {
				Move moveToMake = turn.move(pente);
				pente.move(moveToMake);
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
			++turns;
		}
	}
	
	public Move move(Game g) throws GameManagerUndefinedException {
		moveList = new LinkedList <Move>();
		if (maximize){
			maxValue (g, alpha, beta, depth, 0);
		}else {
			minValue (g, alpha, beta, depth, 0);
		}
		System.out.println("Returning move: "+ bestMove);
		return bestMove;
	}
	
	@Override
	public Move move() throws GameManagerUndefinedException {
		if (mgmt == null){
			throw new GameManagerUndefinedException();
		}
		bestMove = null;
		if (maximize){
			maxValue (mgmt.game, alpha, beta, depth, 0);
		}else {
			minValue (mgmt.game, alpha, beta, depth, 0);
		}
		return moveList.pop();
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
					double v = minValue(g, a, b, depthLimit, depth++);
					g.move(new Move("undo"));
					if (v > a){						
						a = v;
						// System.out.println("Depth @ MAX is : "+depth);
						if (depth == 0)
							System.out.println("DEPTH IS 0!");
						if(depth == 1){
							bestMove = moves.get(i);
							System.out.println("Best move is now: "+bestMove);
						}
						//System.out.println("Max pushing: "+ moves.get(i));
						//this.moveList.push(moves.get(i)); 
					}
					if (a >= b){
						
						return a;
					}
				} catch (MoveException e) {
					e.printStackTrace();
					System.exit(-500);
				}
			} // end for 
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
					double v = maxValue(g, a, b, depthLimit, depth++);
					g.move(new Move("undo"));
					if (v < b){
						b = v;
						// System.out.println("Depth @ MIN is : "+depth);
						//System.out.println(depth);
						if (depth == 0)
							System.out.println("DEPTH IS 0!");
						if(depth==1){
							bestMove = moves.get(i);
							System.out.println("Best move is now: "+bestMove);
						}
						//System.out.println("Min pushing: "+ moves.get(i));
						//this.moveList.push(moves.get(i));
					}
					if (a >= b){
						return b;
					}
				} catch (MoveException e) {
					e.printStackTrace();
					System.exit(-500);
				}
			}//end for 
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
