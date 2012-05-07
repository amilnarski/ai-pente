import java.util.ArrayList;
import java.util.Date;
import java.util.LinkedList;

public class GameManager {
	protected Game game;
	private ArrayList<GamePlayer> player;
	private final LinkedList<Move> moves;

	public GameManager(Game game, GamePlayer player) {
		this.game = game;
		this.player = new ArrayList<GamePlayer>();
		this.player.add(player);

		// set up the list of possible moves
		moves = new LinkedList<Move>();
		for (int i = 1; i < 20; i++) {
			for (int j = 1; j < 20; j++) {
				try {
					moves.add(new Move(i, j));
				} catch (Exception e) {
					e.printStackTrace();
				}
			}
		}

	}

	public void addPlayer (GamePlayer gp){
		player.add(gp);
	}
	public boolean isValidMove(Move move) {
		boolean validity = false;

		return validity;
	}

	public void refreshGame(Game game) {
		this.game = game;
	}

	public static void main(String args[]) throws Exception {
		int numPlayers = 2;
		ReinforcementPlayer rpMax = new ReinforcementPlayer(true, 0);
		ReinforcementPlayer rpMin = new ReinforcementPlayer(false, 1);
		Pente pente = new Pente();
		GameManager mgmt = new GameManager(pente, rpMax);
		long start = new Date().getTime();
		for (int games = 0; games < 1; games++) {

			// create a new copy of the game for each run
			pente = new Pente();
			mgmt.refreshGame(pente);
			// HumanPlayer dummy = new HumanPlayer();
			mgmt.player.add(rpMin);
			mgmt.player.get(0).setGameManager(mgmt);
			// mgmt.player.get(0).setGUID(0);
			mgmt.player.get(1).setGameManager(mgmt);
			// mgmt.player.get(1).setGUID(1);
			int lastPlayer = -1;
			int turn = 0;
			int playerTurn = -1;
			while (!mgmt.game.isOver() && turn < 362) {
				//System.out.println("↓↓↓↓↓↓↓↓↓↓↓↓↓↓↓↓↓↓↓↓↓↓↓↓↓↓↓↓↓↓↓↓↓↓↓↓↓↓↓↓↓↓↓↓↓↓↓↓↓↓");
				//if (turn > 2)
					//mgmt.player.get(playerTurn).updateWeights();
				playerTurn = turn % numPlayers;
//				System.out.println("PlayerTurn: "+playerTurn);
				Move make = mgmt.player.get(playerTurn).move();
				/*while (make == null){
					System.out.println("null loop, player "+ playerTurn );
					make = mgmt.player.get(playerTurn).move();
				}*/
				System.out.println("Move Returned: "+ make);
				try {
					((ReinforcementPlayer) mgmt.player.get(playerTurn)).receiveReward(mgmt.game.move(make));
					if (lastPlayer != -1){
						mgmt.player.get(lastPlayer).updateWeights(); //after we commit the other player's move, update the weights
					}
				}
				catch (MoveException e){
					//System.out.println("Cause: "+e.getCause());
					e.printStackTrace();
					System.exit(1337);
				}
				//INCREMENT TURN INFORMATION
				lastPlayer = playerTurn;
				turn++;
				

				
//				System.out.println("Turn: " + turn);
//				System.out.println("Over: "+((Pente)mgmt.game).isOver());
//				System.out.println("Winner: "+((Pente)mgmt.game).winner);
//				System.out.println("Value: "+mgmt.game.getGameValue());
//				System.out.println(mgmt.game);
				//System.out.println("↑↑↑↑↑↑↑↑↑↑↑↑↑↑↑↑↑↑↑↑↑↑↑↑↑↑↑↑↑↑↑↑↑↑↑↑↑↑↑↑↑↑↑↑↑↑↑↑↑↑");
			}
//			System.out.println("↓↓↓↓↓↓↓↓↓↓↓↓↓↓↓↓↓↓↓↓↓↓↓↓↓↓↓↓↓↓↓↓↓↓↓↓↓↓↓↓↓↓↓↓↓↓↓↓↓↓");
//			System.out.println("Game Number: " + games);
//			System.out.println("Turn: " + turn);
//			System.out.println("Over: " + ((Pente) mgmt.game).isOver());
//			System.out.println("Winner: " + ((Pente) mgmt.game).winner);
//			System.out.println("Value: " + mgmt.game.getGameValue());
//			System.out.println(pente);
//			Move move = ((Pente) mgmt.game).undoData.peekFirst().getMove();
//			System.out.println("Winning Move: " + move);
//			System.out.println("↑↑↑↑↑↑↑↑↑↑↑↑↑↑↑↑↑↑↑↑↑↑↑↑↑↑↑↑↑↑↑↑↑↑↑↑↑↑↑↑↑↑↑↑↑↑↑↑↑↑");
			if (games%100 == 0){
				long gameTime = new Date().getTime();
				//System.out.println(games+", "+(gameTime-start)/60000); //print the time diff in minutes for each 100 games.
				double[] one = ((ReinforcementPlayer) mgmt.player.get(0)).getWeights();
				double[] two = ((ReinforcementPlayer) mgmt.player.get(1)).getWeights();
				System.out.println(gameTime);
				System.out.println("One, "+one[0]+", "+one[1]+", "+one[2]+", "+one[3]+", "+one[4]+", "+one[5]+", "+one[6]+", "+one[7]);
				System.out.println("Two, "+two[0]+", "+two[1]+", "+two[2]+", "+two[3]+", "+two[4]+", "+two[5]+", "+two[6]+", "+two[7]);
			}
		}

		ReinforcementPlayer one = (ReinforcementPlayer) mgmt.player.get(0);
		ReinforcementPlayer two = (ReinforcementPlayer) mgmt.player.get(1);

		System.out.println("WEIGHTS");
		for (int i = 0; i < 8; i++) {
			System.out.println("\tMax: " + one.weights[i] + "\tMin: " + two.weights[i]);
			
		}

	}
}