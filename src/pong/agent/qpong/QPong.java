package pong.agent.qpong;

import pong.agent.Agent;
import pong.agent.Percept;
import pong.environment.Pong;

/*
 * Q-Pong that knows where it is,
 * where the opponent is, and
 * where the ball is.
 */

public class QPong extends Agent {
	
    private final int[] deltaX = {-1};
    private final int[] deltaY = {-1, 1};
    private final int[] actions = {0, -1, 1};
    
    public int[][][][] endBallY;
    public int[][][][] endDeltaY;
    public int[][][][] startBallY;
    public int[][][][] startDeltaY;
    
    private double[][][][][] Q;
    private double[][][][][] nextQ;
    private int[][][][] P;
    
    boolean firstTime = true;
		
	@Override
	public void init() {
        
        if (firstTime) {
            firstTime = false;
            
            int playerRange = dimension.height-size+1;

            Q = new double[actions.length][playerRange][playerRange][dimension.height][dimension.width];
            P = new int[playerRange][playerRange][dimension.height][dimension.width];
            
            for (int i = 1; i <= 1000; i++) {
                System.err.println("qIteration: "+i);
                learn();
                System.gc();
            }
            
            for (int playerY = 0; playerY < playerRange; playerY++) {
                for (int opponentY = 0; opponentY < playerRange; opponentY++) {
                    for (int ballY = 0; ballY < dimension.height; ballY++) {
                        for (int ballX = 0; ballX < dimension.width; ballX++) {
                            double bestQ = Integer.MIN_VALUE;
                            for (int act : actions) {
                                if (bestQ < Q[act + 1][playerY][opponentY][ballY][ballX]) {
                                    bestQ = Q[act + 1][playerY][opponentY][ballY][ballX];
                                    P[playerY][opponentY][ballY][ballX] = act;
                                }
                            }
                        }
                    }
                }
            }
        } else {
            // learn();
        }
	}
	
	public void learn() {
        
        int playerRange = dimension.height-size+1;
        
        nextQ = new double[actions.length][playerRange][playerRange][dimension.height][dimension.width];
        for (int playerY = 0; playerY < playerRange; playerY++) {
            for (int opponentY = 0; opponentY < playerRange; opponentY++) {
                for (int ballY = 0; ballY < dimension.height; ballY++) {
                    for (int ballX = 0; ballX < dimension.width; ballX++) {
                        updateQ(playerY, opponentY, ballY, ballX);
                    }
                }
            }
        }
        Q = nextQ;
        System.gc();
    }
	
	private void updateQ(int playerY, int opponentY, int ballY, int ballX) {
        
        for (int act : actions) {
            nextQ[act + 1][playerY][opponentY][ballY][ballX] = 0;
            for (int dx : deltaX) {
                for (int dy : deltaY) {
                    int nextPlayerY = movePlayer(playerY, act);
                    int nextOpponentY = opponentY;
                    int nextBallX = moveBallX(ballX, dx);
                    int nextBallY = moveBallY(ballY, dy);
                    
                    int r = qReward(playerY, opponentY, ballY, ballX, nextPlayerY, nextOpponentY, nextBallY, nextBallX);
                    double v = Integer.MIN_VALUE;
                    for (int sact : actions) {
                        if (v < Q[sact + 1][nextPlayerY][nextOpponentY][nextBallY][nextBallX]) {
                            v = Q[sact + 1][nextPlayerY][nextOpponentY][nextBallY][nextBallX];
                        }
                    }
                    nextQ[act + 1][playerY][opponentY][ballY][ballX] += r + 0.5*v;
                }
            }
        }
    }
	
	private int qReward(int playerY, int opponentY, int ballY, int ballX, int nextPlayerY, int nextOpponentY, int nextBallY, int nextBallX) {

        if (nextBallX == 0) {
            return -1000;
        }
        if (nextBallX == 1 && nextBallY >= nextPlayerY && nextBallY < nextPlayerY+size) {
            return 1000;
        }
        int dist = playerY-ballY;
        if (ballY > playerY+size) {
            dist = ballY-(playerY+size);
        }
        int nextDist = nextPlayerY-nextBallY;
        if (nextBallY > nextPlayerY+size) {
            nextDist = nextBallY-(nextPlayerY+size);
        }
        return dist-nextDist;
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
