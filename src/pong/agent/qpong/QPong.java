package pong.agent.qpong;

import pong.agent.Agent;
import pong.agent.Percept;
import pong.environment.Pong;

public class QPong extends Agent {
	
    private final int[] deltaX = {-2, -1, 1, 2};
    private final int[] deltaY = {-1, 1};
    private final int[] actions = {0, -1, 1};
    
    private double[][][][][][] nextV;
    private double[][][][][][] V;
    private int[][][][][][] P;
    
    boolean firstTime = true;
    
    private final double gamma;
    private final int iterations;
    
    public QPong(double gamma, int iterations) {
    	
    	this.gamma = gamma;
        this.iterations = iterations;
    }
		
	@Override
	public void init() {
        
        if (firstTime) {
            firstTime = false;
            
            int playerRange = dimension.height-size+1;

            V = new double[deltaY.length + 1][deltaX.length + 2][playerRange][playerRange][dimension.height][dimension.width];
            P = new int[deltaY.length + 1][deltaX.length + 2][playerRange][playerRange][dimension.height][dimension.width];
            
            for (int i = 1; i <= iterations; i++) {
                System.err.println("qIteration: "+i);
                learn();
            }
        }
	}
	
	public void learn() {
        
        int playerRange = dimension.height-size+1;
        
        nextV = new double[deltaY.length + 1][deltaX.length + 2][playerRange][playerRange][dimension.height][dimension.width];
        for (int playerY = 0; playerY < playerRange; playerY++) {
            for (int opponentY = 0; opponentY < playerRange; opponentY++) {
                for (int ballY = 0; ballY < dimension.height; ballY++) {
                    for (int ballX = 0; ballX < dimension.width; ballX++) {
                        for (int dx : deltaX) {
                            for (int dy : deltaY) {
                                calculateQ(playerY, opponentY, ballY, ballX, dy, dx);
                            }
                        }
                    }
                }
            }
        }
        V = nextV;
        System.gc();
    }
	
	private void calculateQ(int playerY, int opponentY, int ballY, int ballX, int dy, int dx) {
        
		double bestQ = Long.MIN_VALUE;
		
        int nextBallX = moveBallX(ballX, dx);
        int nextBallY = moveBallY(ballY, dy);
        int nextDx = updateDeltaX(playerY, opponentY, ballY, ballX, dx);
        int nextDy = updateDeltaY(ballY, dy);
        
        for (int act : actions) {
            int nextPlayerY = movePlayer(playerY, act);
            int nextOpponentY = opponentY;
            int r = qReward(playerY, opponentY, ballY, ballX, dy, dx,
                            nextPlayerY, nextOpponentY, nextBallY, nextBallX, nextDy, nextDx);
            double v = V[nextDy + 1][nextDx + 2][nextPlayerY][nextOpponentY][nextBallY][nextBallX];
            if (bestQ < r + gamma * v) {
            	bestQ = r + gamma * v;
            	P[dy + 1][dx + 2][playerY][opponentY][ballY][ballX] = act;
            }
        }
        nextV[dy + 1][dx + 2][playerY][opponentY][ballY][ballX] = bestQ;
    }
	
	private int qReward(int playerY, int opponentY, int ballY, int ballX, int dy, int dx,
                        int nextPlayerY, int nextOpponentY, int nextBallY, int nextBallX,int nextDy, int nextDx) {

        int reward = 0;
            // ball coming
        if (nextDx < 0) {
            if (nextBallX == 0) {
                reward = -1000;
            }
            else if (nextBallX == 1 && nextBallY >= nextPlayerY && nextBallY < nextPlayerY+size) {
                reward = 1000;
            }
        }
            // ball going away
        else {
            if (nextBallX == dimension.width-1) {
                reward = 1000;
            }
            else if (nextBallX == dimension.width-2 && nextBallY >= nextOpponentY && nextBallY < nextOpponentY+size) {
                reward = -1000;
            }
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

    private int updateDeltaY(int ballY, int dy) {
        
        if (ballY == 0 && dy < 0 || ballY == dimension.height-1 && dy > 0) {
            return -dy;
        }
        return dy;
    }
    
    private int updateDeltaX(int playerY, int opponentY, int ballY, int ballX, int dx) {
        
        if (dx < 0 && ballX == 1 && ballY >= playerY && ballY < playerY+size) {
            return -dx;
        }
        else if (dx > 0 && ballX == dimension.width-2 && ballY >= opponentY && ballY < opponentY+size) {
            return -dx;
        }
        return dx;
    }
    
	@Override
	public int compute(Percept percept) {
        
		int ballDeltaX = percept.ball.direction.x;
		int ballX = percept.ball.location.x;
        int ballY = percept.ball.location.y;
		int ballDeltaY = percept.ball.direction.y;
		int playerY = percept.player;
        int opponentY = percept.opponent;

		if (status == Pong.OPPONENT) {
            ballDeltaX *= -1;
            ballX = dimension.width-ballX-1;
            int tmp = playerY;
            playerY = percept.opponent;
            opponentY = tmp;
        }
        return P[ballDeltaY + 1][ballDeltaX + 2][playerY][opponentY][ballY][ballX];
	}
}
