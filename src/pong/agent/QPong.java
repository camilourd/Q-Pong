package pong.agent;

import java.util.ArrayList;
import java.util.List;

import pong.environment.Pong;

public class QPong extends Agent {

	private final double gamma;
	
	private double[][][][][][] V;
    private int[][][][][][] P;
    
    private final int[] deltaX = {-2, -1, 1, 2};
    private final int[] deltaY = {-1, 1};
    private final int[] actions = {-1, 0, 1};
    
    private int[][][][] borderBallY;
    
    private double[][][][][][][] nextQ;
    private double[][][][][][] nextV;
    private int[][][][][][] nextP;
	
	public QPong(double gamma) {
		this.gamma = gamma;
	}
	
	@Override
	public void init() {
		borderBallY = new int[dimension.width][dimension.height][deltaX.length][deltaY.length];
		int[] DX = {-2, -1};
        int[] DY = {-1, +1};
        
        for (int startBallY = 0; startBallY < dimension.height; startBallY++) {
            for (int startDy : DY) {
                for (int dx : DX) {
                    
                    int ballY = startBallY;
                    int ballX = dimension.width-2;
                    int dy = startDy;
                    
                    List<Integer> pathBallX = new ArrayList<Integer>();
                    List<Integer> pathBallY = new ArrayList<Integer>();
                    List<Integer> pathDeltaY = new ArrayList<Integer>();

                    while (ballX > 1) {
                        pathBallX.add(ballX);
                        pathBallY.add(ballY);
                        pathDeltaY.add(dy);

                        ballX += dx;
                        if (ballX < 1) {
                            ballX = 1;
                        }
                        if (ballY == 0 && dy < 0 || ballY == dimension.height-1 && dy > 0) {
                            dy = -dy;
                        }
                        ballY += dy;
                    }
                    int borderY = ballY;
                    
                    for (int i = 0; i < pathBallX.size(); i++) {
                        ballX = pathBallX.get(i);
                        ballY = pathBallY.get(i);
                        dy = pathDeltaY.get(i);
                        borderBallY[ballX][ballY][dx][dy] = borderY;
                    }
                }
            }
        }
	}
	
	public void learn() {
        
        int playerRange = dimension.height-size+1;
        
        nextQ = new double[playerRange][playerRange][dimension.width][dimension.height][deltaX.length][deltaY.length][deltaY.length];
        nextV = new double[playerRange][playerRange][dimension.width][dimension.height][deltaX.length][deltaY.length];
        nextP = new int[playerRange][playerRange][dimension.width][dimension.height][deltaX.length][deltaY.length];
        
        for (int playerY = 0; playerY < playerRange; playerY++)
            for (int opponentY = 0; opponentY < playerRange; opponentY++)
                for (int ballX = 1; ballX < dimension.width-1; ballX++)
                    for (int ballY = 1; ballY < dimension.width-1; ballY++)
                        for (int dx : deltaX)
                            for (int dy : deltaY)
                                updateQ(playerY, opponentY, ballX, ballY, dx, dy);
        V = nextV;
        P = nextP;
    }
	
	private void updateQ(int playerY, int opponentY, int ballX, int ballY, int dx, int dy) {    
        ballX = moveBallX(ballX, dx);
        ballY = moveBallY(ballY, dy);
        
        double maxi = -1;
        int action = 0;
        for (int pAct : actions) {
            int nextPlayerY = movePlayer(playerY, pAct);
            double r = reward(nextPlayerY, opponentY, ballX, ballY, dx, dy);
            double v = V[nextPlayerY][opponentY][ballX][ballY][dx][dy];

            nextQ[playerY][opponentY][ballX][ballX][dx][dy][pAct] = r+gamma*v;
            
            if (maxi < r+gamma*v) {
                maxi = r+gamma*v;
                action = pAct;
            }
        }
        nextV[playerY][opponentY][ballX][ballY][dx][dy] = maxi;
        nextP[playerY][opponentY][ballX][ballY][dx][dy] = action;
    }
	
	private int reward(int playerY, int opponentY, int ballX, int ballY, int dx, int dy) {
        
        int targetY = borderBallY[ballX][ballY][dx][dy];
        int playerDistance = targetDistance(playerY, targetY);
        
            // ball above the player
        if (playerDistance < 0) {
            return dimension.width+playerDistance;
        }
            // ball under the player
        else if (playerDistance > 0) {
            return dimension.width-playerDistance;
        }
            // in front the player
        else {
            int sectionLength = dimension.height/3;
            
            if (ballY <= playerY+sectionLength) {
                dx = -2;
            }
            else if (ballY <= playerY+2*sectionLength) {
                dx = -1;
            }
            else {
                dx = -2;
            }
            
            targetY = borderBallY[dimension.height-2][ballY][dx][dy];
            int opponentDistance = targetDistance(opponentY, targetY);
            
            return opponentDistance;
        }
    }
	
	private int targetDistance(int playerY, int ballY) {
        
        if (ballY < playerY) {
            return -(playerY-ballY);
        }
        else if (ballY >= playerY+dimension.height) {
            return ballY-(playerY+dimension.height-1);
        }
        return 0;
    }
    
    private int movePlayer(int y, int dy) {
        
        y += dy;
        if (y < 0) {
            y = 0;
        }
        else if (y >= dimension.height) {
            y = dimension.height-1;
        }
        return y;
    }
    
    private int moveBallX(int ballX, int dx) {
        
        ballX += dx;
        if (ballX < 1) {
            ballX = 1;
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
		int ballDeltaX = percept.ball.direction.x;
		int ballX = percept.ball.location.x;
		int playerY = percept.player;
		int opponentY = percept.opponent;
		int ballY = percept.ball.location.y;
		int ballDeltaY = percept.ball.direction.y;
		
		if (status == Pong.OPPONENT) {
            ballDeltaX = -1 * percept.ball.direction.x;
            ballX = dimension.width-percept.ball.location.x-1;
            
            int tmp = percept.player;
            playerY = percept.opponent;
            opponentY = tmp;
        }
        return P[playerY][opponentY][ballX][ballY][ballDeltaX][ballDeltaY];
	}

}
