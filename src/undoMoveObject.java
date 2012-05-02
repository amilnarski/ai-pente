import java.util.Iterator;
import java.util.LinkedList;

public class undoMoveObject{
		private LinkedList <int[]> capturedCoord;
		private double[] valueDiff;
		private Move move;
		
		public undoMoveObject(Move move){
			this.move = move;
			capturedCoord = new LinkedList <int[]>();
			valueDiff = new double[] {0,0,0,0,0,0,0,0};
		}
		
		public void addCoord(int[] capturedCoord){
			this.capturedCoord.add(capturedCoord);
			}
		public void addValueDiff(int index, double value){
			valueDiff[index]+=value;
		}
		public void concatonate(undoMoveObject other){
			if (this.move.equals(other.move)) {
				Iterator<int[]> otherList = other.getCapturedCoord().iterator();
				while (otherList.hasNext()) {
					capturedCoord.add((int[]) otherList.next());
				}
				double[] otherValue = other.getValueDifference();
				for (int i = 0; i < valueDiff.length; i++) {
					this.valueDiff[i] += otherValue[i];
				}
			}
		}
		
		public Move getMove(){
			return move;
		}
		
		public LinkedList<int[]> getCapturedCoord() {
			return capturedCoord;
		}
		
		public double[] getValueDifference() {
			return valueDiff;
		}
		
		public void setValueDifference(double[] valueDifference) {
			this.valueDiff = valueDifference;
		}		
	}