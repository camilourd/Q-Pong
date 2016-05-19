package pong.agent.qpong;

import pong.agent.Agent;
import pong.agent.Percept;
import pong.environment.Pong;

public class QPongNoDirection extends Agent {
	
    private static final int[] deltaX = {-2, -1};
    private static final int[] deltaY = {-1, 1};
    private static final int[] actions = {0, -1, 1};
    
    public static final double T = deltaX.length*deltaY.length*actions.length;
    
    private double[][][][] nextV;
    private double[][][][] V;
    private int[][][][] P;
    
    boolean firstTime = true;
    
    private final double gamma;
    private final int iterations;
    
    public QPongNoDirection(double gamma, int iterations) {
    	
    	this.gamma = gamma;
        this.iterations = iterations;
    }
    
	@Override
	public void init() {
        
        if (firstTime) {
            firstTime = false;
            
            int playerRange = dimension.height-size+1;

            V = new double[playerRange][playerRange][dimension.height][dimension.width];
            P = new int[playerRange][playerRange][dimension.height][dimension.width];
            
            for (int i = 1; i <= iterations; i++) {
                System.err.println("qIteration: " + i);
                learn();
            }
        }
	}
	
	public void learn() {
        
        int playerRange = dimension.height - size + 1;
        
        nextV = new double[playerRange][playerRange][dimension.height][dimension.width];
        for (int playerY = 0; playerY < playerRange; playerY++) {
            for (int opponentY = 0; opponentY < playerRange; opponentY++) {
                for (int ballY = 0; ballY < dimension.height; ballY++) {
                    for (int ballX = 0; ballX < dimension.width; ballX++) {
                        calculateQ(playerY, opponentY, ballY, ballX);
                    }
                }
            }
        }
        V = nextV;
        System.gc();
    }
	
	private void calculateQ(int playerY, int opponentY, int ballY, int ballX) {
        
		double bestQ = Long.MIN_VALUE;
		
        for (int act : actions) {
            int nextPlayerY = movePlayer(playerY, act);
            double Q = 0;
            for (int dx : deltaX) {
                for (int dy : deltaY) {
                    for (int oact : actions) {
                        int nextBallX = moveBallX(ballX, dx);
                        int nextBallY = moveBallY(ballY, dy);
                        int nextOpponentY = movePlayer(opponentY, oact);
                        int r = qReward(playerY, opponentY, ballY, ballX,
                                        nextPlayerY, nextOpponentY, nextBallY, nextBallX);
                        double v = V[nextPlayerY][nextOpponentY][nextBallY][nextBallX];
                        Q += T * (r + gamma * v);
                    }
                }
            }
            if (bestQ < Q) {
            	bestQ = Q;
            	P[playerY][opponentY][ballY][ballX] = act;
            }
        }
        nextV[playerY][opponentY][ballY][ballX] = bestQ;
    }
	
	private int qReward(int playerY, int opponentY, int ballY, int ballX,
                        int nextPlayerY, int nextOpponentY, int nextBallY, int nextBallX) {

        int reward = 0;

        if (nextBallX == 0) {
        	reward = -1000;
        }
        else if (nextBallX == 1 && nextBallY >= nextPlayerY && nextBallY < nextPlayerY+size) {
        	reward = 1000;
        }
        else if (nextBallX == dimension.width-1 && (nextBallY < nextOpponentY || nextBallY >= nextOpponentY+size)) {
        	reward = 1000;
        }
        return reward;
    }
    
    private int movePlayer(int y, int dy) {
        
        y += dy;
        if (y < 0) {
            y = 0;
        }
        else if (y > dimension.height-size) {
            y = dimension.height-size;
        }
        return y;
    }
    
    private int moveBallX(int ballX, int dx) {
        
        ballX += dx;
        if (ballX < 0) ballX = 0;
        if (ballX >= dimension.width) {
            ballX = dimension.width-1;
        }
        return ballX;
    }
    
    private int moveBallY(int ballY, int dy) {
        
        if (ballY == 0 && dy < 0 || ballY == dimension.height-1 && dy > 0) {
            dy = -dy;
        }
        ballY += dy;
        return ballY;
    }
    
	@Override
	public int compute(Percept percept) {
        
		int ballX = percept.ball.location.x;
        int ballY = percept.ball.location.y;
		int playerY = percept.player;
        int opponentY = percept.opponent;

		if (status == Pong.OPPONENT) {
            ballX = dimension.width-ballX-1;
            int tmp = playerY;
            playerY = percept.opponent;
            opponentY = tmp;
        }
        return P[playerY][opponentY][ballY][ballX];
	}
}
