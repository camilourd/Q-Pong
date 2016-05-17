package pong;

import java.util.ArrayList;
import java.util.List;

/**
 *
 * @author eduarc
 */
public class QPong {
    
    public static final int PLAYER_LEFT = 0;
    public static final int PLAYER_RIGHT = 1;
    
    private final int player;
    
    private final int fieldWidth;
    
    private final int fieldHeight;
    
    private final int playerHeight;
    
    private final double gamma;
    
    private double[][][][][][] V;
    private int[][][][][][] P;
    
    private final int[] deltaX = {-2, -1, 1, 2};
    private final int[] deltaY = {-1, 1};
    private final int[] actions = {-1, 0, 1};
        
    private final int[][][][] borderBallY;
    
    public QPong(double gamma, int player, int playerHeight, int fieldWidth, int fieldHeight) {
        
        this.player = player;
        this.playerHeight = playerHeight;
        this.fieldWidth = fieldWidth;
        this.fieldHeight = fieldHeight;
        this.gamma = gamma;
        
        borderBallY = new int[fieldWidth][fieldHeight][deltaX.length][deltaY.length];
    }
    
    public void init() {
        
        int[] DX = {-2, -1};
        int[] DY = {-1, +1};
        
        for (int startBallY = 0; startBallY < fieldHeight; startBallY++) {
            for (int startDy : DY) {
                for (int dx : DX) {
                    
                    int ballY = startBallY;
                    int ballX = fieldWidth-2;
                    int dy = startDy;
                    
                    List<Integer> pathBallX = new ArrayList();
                    List<Integer> pathBallY = new ArrayList();
                    List<Integer> pathDeltaY = new ArrayList();

                    while (ballX > 1) {
                        pathBallX.add(ballX);
                        pathBallY.add(ballY);
                        pathDeltaY.add(dy);

                        ballX += dx;
                        if (ballX < 1) {
                            ballX = 1;
                        }
                        if (ballY == 0 && dy < 0 || ballY == fieldHeight-1 && dy > 0) {
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
    
    private double[][][][][][][] nextQ;
    private double[][][][][][] nextV;
    private int[][][][][][] nextP;
        
        // ----------------
        // PUBLIC INTERFACE
        // ----------------
    
    public void learn() {
        
        int playerRange = fieldHeight-playerHeight+1;
        
        nextQ = new double[playerRange][playerRange][fieldWidth][fieldHeight][deltaX.length][deltaY.length][deltaY.length];
        nextV = new double[playerRange][playerRange][fieldWidth][fieldHeight][deltaX.length][deltaY.length];
        nextP = new int[playerRange][playerRange][fieldWidth][fieldHeight][deltaX.length][deltaY.length];
        
        for (int playerY = 0; playerY < playerRange; playerY++) {
            for (int opponentY = 0; opponentY < playerRange; opponentY++) {
                for (int ballX = 1; ballX < fieldWidth-1; ballX++) {
                    for (int ballY = 1; ballY < fieldWidth-1; ballY++) {
                        for (int dx : deltaX) {
                            for (int dy : deltaY) {
                                updateQ(playerY, opponentY, ballX, ballY, dx, dy);
                            }
                        }
                    }
                }
            }
        }
        V = nextV;
        P = nextP;
    }
    
    public int getAction(int playerY, int opponentY, int ballX, int ballY, int ballDeltaX, int ballDeltaY) {
        
        if (player == 1) {
            ballDeltaX *= -1;
            ballX = fieldWidth-ballX-1;
            
            int tmp = playerY;
            playerY = opponentY;
            opponentY = tmp;
        }
        return P[playerY][opponentY][ballX][ballY][ballDeltaX][ballDeltaY];
    }

    public int getPlayer() {
        return player;
    }

    public int getFieldWidth() {
        return fieldWidth;
    }

    public int getFieldHeight() {
        return fieldHeight;
    }

    public int getPlayerHeight() {
        return playerHeight;
    }
    
        // ----------------
        // PRIVATE INTERFACE
        // ----------------
    private void updateQ(int playerY, int opponentY, int ballX, int ballY, int dx, int dy) {
        
        ballX = moveBallX(ballX, dx);
        ballY = moveBallX(ballY, dy);
        
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
            return fieldWidth+playerDistance;
        }
            // ball under the player
        else if (playerDistance > 0) {
            return fieldWidth-playerDistance;
        }
            // in front the player
        else {
            int sectionLength = playerHeight/3;
            
            if (ballY <= playerY+sectionLength) {
                dx = -2;
            }
            else if (ballY <= playerY+2*sectionLength) {
                dx = -1;
            }
            else {
                dx = -2;
            }
            
            targetY = borderBallY[fieldHeight-2][ballY][dx][dy];
            int opponentDistance = targetDistance(opponentY, targetY);
            
            return opponentDistance;
        }
    }
    
    private int targetDistance(int playerY, int ballY) {
        
        if (ballY < playerY) {
            return -(playerY-ballY);
        }
        else if (ballY >= playerY+playerHeight) {
            return ballY-(playerY+playerHeight-1);
        }
        return 0;
    }
    
    private int movePlayer(int y, int dy) {
        
        y += dy;
        if (y < 0) {
            y = 0;
        }
        else if (y >= fieldHeight) {
            y = fieldHeight-1;
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
        
        if (ballY == 0 && dy < 0 || ballY == fieldHeight-1 && dy > 0) {
            dy = -dy;
        }
        ballY += dy;
        return ballY;
    }
}
