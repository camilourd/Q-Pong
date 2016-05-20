package pong.agent.qpong;

import pong.agent.Agent;
import pong.agent.Percept;
import pong.environment.Pong;

public class QPongNoDirection extends Agent {
	
    private static final int[] deltaX = {-2, -1};
    private static final int[] deltaY = {-1, 1};
    private static final int[] actions = {0, -1, 1};
    
    public static final double T = deltaX.length*deltaY.length;
    
    private double[][][] nextV;
    private double[][][] V;
    private int[][][] P;
    
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
            
            int playerRange = fieldSize.height-size+1;

            V = new double[playerRange][fieldSize.height][fieldSize.width];
            P = new int[playerRange][fieldSize.height][fieldSize.width];
            
            for (int i = 1; i <= iterations; i++) {
                System.err.println("qIteration: " + i);
                learn();
            }
        }
	}
	
	public void learn() {
        
        int playerRange = fieldSize.height - size + 1;
        
        nextV = new double[playerRange][fieldSize.height][fieldSize.width];
        for (int playerY = 0; playerY < playerRange; playerY++) {
            for (int ballY = 0; ballY < fieldSize.height; ballY++) {
                for (int ballX = 0; ballX < fieldSize.width; ballX++) {
                    calculateQ(playerY, ballY, ballX);
                }
            }
        }
        V = nextV;
        System.gc();
    }
	
	private void calculateQ(int playerY, int ballY, int ballX) {
        
		double bestQ = Long.MIN_VALUE;
		
        for (int act : actions) {
            int nextPlayerY = movePlayer(playerY, act);
            double Q = 0;
            for (int dx : deltaX) {
                for (int dy : deltaY) {
                    int nextBallX = moveBallX(ballX, dx);
                    int nextBallY = moveBallY(ballY, dy);
                    int r = qReward(playerY, ballY, ballX,
                                    nextPlayerY, nextBallY, nextBallX);
                    double v = V[nextPlayerY][nextBallY][nextBallX];
                    Q += T * (r + gamma * v);
                }
            }
            if (bestQ < Q) {
            	bestQ = Q;
            	P[playerY][ballY][ballX] = act;
            }
        }
        nextV[playerY][ballY][ballX] = bestQ;
    }
	
	private int qReward(int playerY, int ballY, int ballX,
                        int nextPlayerY, int nextBallY, int nextBallX) {

        int reward = 0;

        if (nextBallX == 0) {
        	reward = -1000;
        }
        else if (nextBallX == 1 && nextBallY >= nextPlayerY && nextBallY < nextPlayerY+size) {
        	reward = 1000;
        }
        return reward;
    }
    
    private int movePlayer(int y, int dy) {
        
        y += dy;
        if (y < 0) {
            y = 0;
        }
        else if (y > fieldSize.height-size) {
            y = fieldSize.height-size;
        }
        return y;
    }
    
    private int moveBallX(int ballX, int dx) {
        
        ballX += dx;
        if (ballX < 0) ballX = 0;
        if (ballX >= fieldSize.width) {
            ballX = fieldSize.width-1;
        }
        return ballX;
    }
    
    private int moveBallY(int ballY, int dy) {
        
        if (ballY == 0 && dy < 0 || ballY == fieldSize.height-1 && dy > 0) {
            dy = -dy;
        }
        ballY += dy;
        return ballY;
    }
    
	@Override
	public int compute(Percept percept) {
        
		int ballX = percept.ball.position.x;
        int ballY = percept.ball.position.y;
		int playerY = percept.left;

		if (side == Pong.RIGHT) {
            ballX = fieldSize.width-ballX-1;
            playerY = percept.right;
        }
        return P[playerY][ballY][ballX];
	}
}
