import java.util.ArrayList;
import java.util.LinkedList;
import java.util.List;

public class AlphaBetaPlayer implements GamePlayer {

	/**
	 * @param args
	 *            Doing terrible unchecked things with the ArrayLists here.
	 *            Should be a custom object at some point
	 */

	boolean maximize; // whether the player is trying to maximize or minimize
						// the value of the game
	@SuppressWarnings("rawtypes")
	ArrayList alpha;
	@SuppressWarnings("rawtypes")
	ArrayList beta;
	GameManager mgmt;
	int GUID;
	Move best;
	LinkedList <Move> moveStack = new LinkedList <Move>();
	
	@SuppressWarnings({ "unchecked", "rawtypes" })
	public AlphaBetaPlayer(boolean isMax) {
		try {
			alpha = new ArrayList();
			beta = new ArrayList();
			alpha.add(new Move(1,1));
			alpha.add(Double.NEGATIVE_INFINITY);
			beta.add(new Move(1,1));
			beta.add(Double.POSITIVE_INFINITY);
			maximize = isMax;
		} catch (MoveException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
			System.exit(-3);
		}
	}

	public static void main(String[] args) {
		Game pente = new Pente();
		AlphaBetaPlayer max = new AlphaBetaPlayer(true);
		AlphaBetaPlayer min = new AlphaBetaPlayer(false);

		AlphaBetaPlayer activePlayer = min;
		while (!pente.isOver()) {
			try {
				Move toMake = activePlayer.move(pente);
				System.out.println("Player submitted: " + toMake);
				pente.move(toMake);
				((Pente) pente).diagnose();
				
				if (activePlayer == max) {
					activePlayer = min;
				} else {
					activePlayer = max;
				}
				
			} catch (MoveException e) {
				// TODO Auto-generated catch block
				e.printStackTrace();
				System.exit(-1);
			} catch (GameManagerUndefinedException e) {
				// TODO Auto-generated catch block
				e.printStackTrace();
			}
		}

	}

	public Move move(Game g) throws GameManagerUndefinedException {
		@SuppressWarnings("rawtypes")
		ArrayList best = null;
		if (maximize) {
			best = maxValue(g, alpha, beta, 10, 0);
		} else {
			best = minValue(g, alpha, beta, 10, 0);
		}
		return (Move) best.get(0);
	}

	/*
	 * @Override public Move move() throws GameManagerUndefinedException { if
	 * (mgmt == null){ throw new GameManagerUndefinedException(); }
	 * 
	 * if (maximize){ best = maxValue (mgmt.game, alpha, beta, 20, 0); }else {
	 * best = minValue (mgmt.game, alpha, beta, 20, 0); } return best; }
	 */

	@SuppressWarnings({ "rawtypes", "unchecked" })
	public ArrayList maxValue(Game g, ArrayList a, ArrayList b, int depthLimit,
			int depth) {
		if (depth >= depthLimit) {
			return a;
		}
		if (g.isOver()) {
			ArrayList r = new ArrayList();
			r.add(a.get(0));
			r.add(g.getGameValue());
			return r; // g.getGameValue(); // what should this case be?
		} else {
			List<Move> moves = g.getAvailableMoves();
			for (int i = 0; i < moves.size(); i++) {
				try {
					ArrayList m = new ArrayList();
					Move toMake = moves.get(i);
					g.move(toMake);
					m.add(toMake);
					m.add(g.getGameValue());
					ArrayList v = minValue(g, a, b, depthLimit, ++depth);
					g.move(new Move("undo"));
					if ((Double) v.get(1) >= (Double) a.get(1)) {
						a = v;
					}
					if ((Double) a.get(1) >= (Double) b.get(1)) {
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

	@SuppressWarnings({ "rawtypes", "unchecked" })
	public ArrayList minValue(Game g, ArrayList a, ArrayList b, int depthLimit,
			int depth) {
		if (depth >= depthLimit) {
			return b;
		}
		if (g.isOver()) {
			ArrayList r = new ArrayList();
			r.add(b.get(0));
			r.add(g.getGameValue());
			return r; // g.getGameValue(); // what should this case be?
		} else {
			List<Move> moves = g.getAvailableMoves();
			for (int i = 0; i < moves.size(); i++) {
				try {
					ArrayList m = new ArrayList();
					Move toMake = moves.get(i);
					g.move(toMake);
					m.add(toMake);
					m.add(1, g.getGameValue());
					ArrayList v = maxValue(g, a, b, depthLimit, ++depth);
					g.move(new Move("undo"));
					if ((Double) v.get(1) <= (Double) b.get(1)) {
						b = v;
					}
					if ((Double) a.get(1) >= (Double) b.get(1)) {
						return b;
					}
				} catch (MoveException e) {
					e.printStackTrace();
					System.exit(-501);
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

	@Override
	public Move move() throws GameManagerUndefinedException {
		// TODO Auto-generated method stub
		return null;
	}

}
