import java.io.*;
import java.util.Hashtable;
import java.util.Iterator;
import java.util.LinkedList;
import java.util.List;
import java.util.Random;

public class ReinforcementPlayer implements GamePlayer {
	// private Iterator<Move> iter;
	// private Hashtable<char[][], Double> q;
	double gameValue;
	GameManager mgmt;
	char[][] state;
	private boolean isMaxPlayer;
	double[] weights;
	private double vS;
	private double vSPrime;
	private double[] sFeatures = new double[8];
	private Random dora = new Random();
	private int GUID;
	BufferedWriter bw;
	BufferedReader br;
	private double[] prevFeatures;
	double reward = 0;

	public ReinforcementPlayer(boolean isMax, int GUID) {
		// q = new Hashtable<char[][], Double>();
		if (isMax)
			weights = new double[] { 0.5, -0.5, 0.5, -0.5, 0.5, -0.5, 0.5, -0.5 };
		else
			weights = new double[] { -0.5, 0.5, -0.5, 0.5, -0.5, 0.5, -0.5, 0.5 };

		this.GUID = GUID;

		sFeatures = new double[8];

		/*
		 * // CHECK IF A FILE EXISTS // TRY TO READ THE FILE, CREATE IT IF IT
		 * EXISTS String filename = GUID + ".txt"; try { bw = new
		 * BufferedWriter(new FileWriter(filename)); // read in the weights,
		 * since the file exists br = new BufferedReader(new
		 * FileReader(filename)); for (int i = 0; i<weights.length; i++){
		 * weights[i] = Double.parseDouble(br.readLine().trim()); } } catch
		 * (Exception e) { File fFile = new File(filename); try { bw = new
		 * BufferedWriter(new FileWriter(filename)); br = new BufferedReader(new
		 * FileReader(filename)); } catch (IOException e1) {
		 * e1.printStackTrace(); System.exit(-1 * GUID); } e.printStackTrace();
		 * }
		 */
	}

	public void setGameManager(GameManager manager) {
		mgmt = manager;
	}

	public Move move() throws GameManagerUndefinedException {
		if (mgmt == null)
			throw new GameManagerUndefinedException();

		// PROCESS THE CURRENT STATE
		System.arraycopy(mgmt.game.getFeatures(),0, sFeatures,0, sFeatures.length);
//		for (int i=0; i<sFeatures.length; i++)
//			System.out.println("Feature: "+i+"\t"+sFeatures[i]);
		// GET THE LIST OF AVAILABLE MOVES
		List<Move> moves = mgmt.game.getAvailableMoves();
		
		System.out.println("Number of Moves: "+moves.size());
		
		Move maxMove = null;
		double maxValue = Double.NEGATIVE_INFINITY;
		for (int i = 0; i < moves.size(); i++) {
			//System.out.println(i);
			try {
				mgmt.game.move(moves.get(i));
				//System.out.println(moves.get(i));
			} catch (MoveException e) {
				e.printStackTrace();
				continue;
			}
			double moveVal = stateValue(mgmt.game.getFeatures());
			//System.out.println("Move Value: "+moveVal+"\tMaxValue: "+maxValue);
			// System.out.println("GameValue: " + gameValue);
			if (maxValue < moveVal) {
				maxMove = moves.get(i);
				maxValue = moveVal;
			}
			try {
				mgmt.game.move(new Move("undo"));
			} catch (MoveException e) {
				e.printStackTrace();
				System.out.println("Unable to undo move at "+moves.get(i));
				System.exit(-14);
			}
		}// end for
			// Exploration Logic

		if (dora.nextDouble() < 0.10) {
			//System.out.println(GUID + " is Exploring!");
			maxMove = moves.get(dora.nextInt(moves.size()));
			System.out.println("exploring");
		}
		// updateWeights();
		if (maxMove == null){
			System.out.println("MaxValue: "+maxValue);
			((Pente)mgmt.game).diagnose();
		}
		return maxMove;
	}

	public void updateWeights() {
		vS = stateValue(sFeatures);
//		System.out.println("vS:\t\t" + vS);
		// sFeatures = new double[] {0,0,0,0,0,0,0,0};
		vSPrime = stateValue(mgmt.game.getFeatures());
		
//		System.out.println("vSPrime:\t" + vSPrime);
//		System.out.println("vDiff: "+(vSPrime - vS));
		double [] feat = mgmt.game.getFeatures();
		System.out.println(feat[0]+", "+feat[1]+", "+feat[2]+", "+feat[3]+", "+feat[4]+", "+feat[5]+", "+feat[6]+", "+feat[7]);
		for (int i = 0; i < weights.length; i++) {
//			System.out.print(weights[i]+"\t");
			weights[i] = weights[i] + ((0.01) * (reward + vSPrime - vS))
					* sFeatures[i];
			//System.out.println(weights[i]);
		}
		reward = 0;
	}

	private double stateValue(double[] featureVec) {
		// double[] features = mgmt.game.getFeatures();
		double value = 0.0;

		for (int i = 0; i < featureVec.length; i++) {
			value += featureVec[i] * weights[i];
		}
		return value;
	}

	public static void main(String[] args) {
		double[] weights = new double[3];
		Double one = 1.0;
		Double two = 2.0;
		Double three = 3.0;
		File f = new File("test.txt");
		try {
			BufferedWriter bw = new BufferedWriter(new FileWriter("test.txt"));
			bw.write(one.toString());
			bw.newLine();
			bw.write(two.toString());
			bw.newLine();
			bw.write(three.toString());
			bw.newLine();
			bw.flush();
		} catch (IOException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}

		try {
			BufferedReader br = new BufferedReader(new FileReader("test.txt"));
			weights[0] = Double.parseDouble(br.readLine().trim());
			weights[1] = Double.parseDouble(br.readLine().trim());
			weights[2] = Double.parseDouble(br.readLine().trim());
		} catch (FileNotFoundException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		} catch (NumberFormatException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		} catch (IOException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}

		for (int i = 0; i < weights.length; i++)
			System.out.println(weights[i]);

	}

	public void setGUID(int GUID) {
		this.GUID = GUID;
	}
	
	public void receiveReward(double reward){
		this.reward = reward;
	}

	public void dumpToFile() {
		for (int i = 0; i < weights.length; i++)
			try {
				bw.write("" + weights[i]);
				bw.newLine();
			} catch (IOException e) {
				System.out.println("oops");
				e.printStackTrace();
			}
	}
	
	public double[] getWeights(){
		return weights;
	}

}
