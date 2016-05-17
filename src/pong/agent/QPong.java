package pong.agent;

import java.util.ArrayList;
import java.util.List;

import pong.environment.Pong;

public class QPong extends Agent {
	
	private int[][][][][][] V;
    private int[][][][][][] P;
    
    private final int[] deltaX = {-2, -1, 1, 2};
    private final int[] deltaY = {-1, 1};
    private final int[] actions = {-1, 0, 1};
    
    private int[][][][] borderBallY;
    
    private int[][][][][][][] nextQ;
    private int[][][][][][] nextV;
    private int[][][][][][] nextP;
		
	@Override
	public void init() {
		borderBallY = new int[dimension.width][dimension.height][deltaX.length + 2][deltaY.length + 1];
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
                        borderBallY[ballX][ballY][dx + 2][dy + 1] = borderY;
                    }
                }
            }
        }
        learn();
	}
	
	public void learn() {
        
        int playerRange = dimension.height-size+1;
        
        nextQ = new int[playerRange][playerRange][dimension.width][dimension.height][deltaX.length + 2][deltaY.length + 1][deltaY.length + 1];
        nextV = new int[playerRange][playerRange][dimension.width][dimension.height][deltaX.length + 2][deltaY.length + 1];
        nextP = new int[playerRange][playerRange][dimension.width][dimension.height][deltaX.length + 2][deltaY.length + 1];
        
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
        
        int maxi = -1;
        int action = 0;
        for (int pAct : actions) {
            int nextPlayerY = movePlayer(playerY, pAct);
            int r = reward(nextPlayerY, opponentY, ballX, ballY, dx, dy);
            int v = V[nextPlayerY][opponentY][ballX][ballY][dx + 2][dy + 1];

            nextQ[playerY][opponentY][ballX][ballX][dx + 2][dy + 1][pAct + 1] = r +v;
            
            if (maxi < r + v) {
                maxi = r + v;
                action = pAct;
            }
        }
        nextV[playerY][opponentY][ballX][ballY][dx + 2][dy + 1] = maxi;
        nextP[playerY][opponentY][ballX][ballY][dx + 2][dy + 1] = action;
    }
	
	private int reward(int playerY, int opponentY, int ballX, int ballY, int dx, int dy) {
        
        int targetY = borderBallY[ballX][ballY][dx + 2][dy + 1];
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
            
            targetY = borderBallY[dimension.height-2][ballY][dx + 2][dy + 1];
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
        return P[playerY][opponentY][ballX][ballY][ballDeltaX + 2][ballDeltaY + 1];
	}

}
