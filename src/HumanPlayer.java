public class HumanPlayer implements GamePlayer{
	public HumanPlayer(){
		return;
	}
	
	public Move move(){
		try {
			return new Move(0,0);
		} catch (Exception e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
			return null;
		}
	}
	
	public static void main(String args[]){
		System.out.println("HumanPlayer.");
	}

	public void setGameManager(GameManager mgmt) {
		// TODO Auto-generated method stub
		
	}

	@Override
	public void setGUID(int GUID) {
		// TODO Auto-generated method stub
		
	}

	@Override
	public void updateWeights() {
		// TODO Auto-generated method stub
		
	}
}