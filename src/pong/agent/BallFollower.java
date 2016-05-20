package pong.agent;

public class BallFollower extends Agent {

	private double errorProbability;
	
	public BallFollower(double errorProbability) {
		
		super();
		this.errorProbability = errorProbability;
	}

	@Override
	public int compute(Percept percept) {
		
		if(percept.ball.direction.y > 0)
			return decide(1);
		return decide(-1);
	}

	private int decide(int i) {
		
		if(Math.random() > errorProbability)
			return i;
		return -i;
	}

	@Override
	public void init() {
		
	}
}
