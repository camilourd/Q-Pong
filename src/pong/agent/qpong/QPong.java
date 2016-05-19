package pong.agent.qpong;

import pong.agent.Agent;
import pong.agent.Percept;
import pong.environment.Pong;

public class QPong extends Agent {
	
    private final int[] deltaX = {-2, -1, 1, 2};
    private final int[] deltaY = {-1, 1};
    private final int[] actions = {0, -1, 1};
    
    private double[][][][][][][] Q;
    private double[][][][][][][] nextQ;
    private int[][][][][][] P;
    
    boolean firstTime = true;
    private final int iterations;
    
    public QPong(int iterations) {
        this.iterations = iterations;
    }
		
	@Override
	public void init() {
        
        if (firstTime) {
            firstTime = false;
            
            int playerRange = dimension.height-size+1;

            Q = new double[actions.length][deltaY.length + 1][deltaX.length + 2][playerRange][playerRange][dimension.height][dimension.width];
            P = new int[deltaY.length + 1][deltaX.length + 2][playerRange][playerRange][dimension.height][dimension.width];
            
            for (int i = 1; i <= iterations; i++) {
                System.err.println("qIteration: "+i);
                learn();
                System.gc();
            }
            
            for (int playerY = 0; playerY < playerRange; playerY++) {
                for (int opponentY = 0; opponentY < playerRange; opponentY++) {
                    for (int ballY = 0; ballY < dimension.height; ballY++) {
                        for (int ballX = 0; ballX < dimension.width; ballX++) {
                            for (int dx : deltaX) {
                                for (int dy : deltaY) {
                                    double bestQ = Integer.MIN_VALUE;
                                    for (int act : actions) {
                                        if (bestQ < Q[act + 1][dy + 1][dx + 2][playerY][opponentY][ballY][ballX]) {
                                            bestQ = Q[act + 1][dy + 1][dx + 2][playerY][opponentY][ballY][ballX];
                                            P[dy + 1][dx + 2][playerY][opponentY][ballY][ballX] = act;
                                        }
                                    }
                                }
                            }
                        }
                    }
                }
            }
        }
	}
	
	public void learn() {
        
        int playerRange = dimension.height-size+1;
        
        nextQ = new double[actions.length][deltaY.length + 1][deltaX.length + 2][playerRange][playerRange][dimension.height][dimension.width];
        for (int playerY = 0; playerY < playerRange; playerY++) {
            for (int opponentY = 0; opponentY < playerRange; opponentY++) {
                for (int ballY = 0; ballY < dimension.height; ballY++) {
                    for (int ballX = 0; ballX < dimension.width; ballX++) {
                        for (int dx : deltaX) {
                            for (int dy : deltaY) {
                                updateQ(playerY, opponentY, ballY, ballX, dy, dx);
                            }
                        }
                    }
                }
            }
        }
        Q = nextQ;
        System.gc();
    }
	
	private void updateQ(int playerY, int opponentY, int ballY, int ballX, int dy, int dx) {
        
        int nextBallX = moveBallX(ballX, dx);
        int nextBallY = moveBallY(ballY, dy);
        int nextDx = updateDeltaX(playerY, opponentY, ballY, ballX, dx);
        int nextDy = updateDeltaY(ballY, dy);
        
        for (int act : actions) {
            int nextPlayerY = movePlayer(playerY, act);
            int nextOpponentY = opponentY;
            int r = qReward(playerY, opponentY, ballY, ballX, dy, dx,
                            nextPlayerY, nextOpponentY, nextBallY, nextBallX, nextDy, nextDx);
            double v = Integer.MIN_VALUE;
            for (int sact : actions) {
                if (v < Q[sact + 1][nextDy + 1][nextDx + 2][nextPlayerY][nextOpponentY][nextBallY][nextBallX]) {
                    v = Q[sact + 1][nextDy + 1][nextDx + 2][nextPlayerY][nextOpponentY][nextBallY][nextBallX];
                }
            }
            nextQ[act + 1][dy + 1][dx + 2][playerY][opponentY][ballY][ballX] = r + 0.5*v;
        }
    }
	
	private int qReward(int playerY, int opponentY, int ballY, int ballX, int dy, int dx,
                        int nextPlayerY, int nextOpponentY, int nextBallY, int nextBallX,int nextDy, int nextDx) {

        int nextReward = 0;
            // ball coming
        if (nextDx < 0) {
            if (nextBallX == 0) {
                nextReward = -1000;
            }
            else if (nextBallX == 1 && nextBallY >= nextPlayerY && nextBallY < nextPlayerY+size) {
                nextReward = 1000;
            }
        }
            // ball going away
        else {
            if (nextBallX == dimension.width-1) {
                nextReward = 1000;
            }
            else if (nextBallX == dimension.width-2 && nextBallY >= opponentY && nextBallY < opponentY+size) {
                nextReward = -1000;
            }
        }
        return nextReward;
            // approximated distance
        /*int dist = playerY-ballY;
        if (ballY > playerY+size) {
            dist = ballY-(playerY+size);
        }
        int nextDist = nextPlayerY-nextBallY;
        if (nextBallY > nextPlayerY+size) {
            nextDist = nextBallY-(nextPlayerY+size);
        }
        return dist-nextDist;*/
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
