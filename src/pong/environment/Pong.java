package pong.environment;

import pong.agent.Agent;
import pong.agent.Percept;
import pong.types.Coordinate;
import pong.utils;

public class Pong {
	
	public static final int PLAYER = 0;
	public static final int OPPONENT = 1;
	
	private final int[] deltaX = {-2, -1};
    private final int[] deltaY = {-1, 1};
	
	public Dimension dimension;
	public Agent[] players = new Agent[2];
	public Coordinate[] status = new Coordinate[2];
	public Ball ball;
	
	public Pong(Dimension dimension, Agent player, Agent opponent) {
        
		player.setSize(dimension.height / 5);
        player.setStatus(PLAYER);
        player.setBound(dimension);
        
		opponent.setSize(dimension.height / 5);
		opponent.setStatus(OPPONENT);
		opponent.setBound(dimension);
        
		this.dimension = dimension;
		this.players[PLAYER] = player;
		this.players[OPPONENT] = opponent;
        
        init();
	}
	
	public final void init() {
        
        players[PLAYER].init();
		players[OPPONENT].init();
        
		ball = new Ball(new Coordinate(dimension.width-2, utils.rand(dimension.height)), new Coordinate(deltaX[utils.rand(2)], deltaY[utils.rand(2)]));
		ball.location.y = utils.rand(dimension.height);
		int size = players[PLAYER].getSize();
		status[PLAYER] = new Coordinate(0, (dimension.height - size) / 2);
		status[OPPONENT] = new Coordinate(dimension.width-1, (dimension.height - size) / 2);
	}

	public Percept sense() {
		return new Percept(status[PLAYER].y, status[OPPONENT].y, ball);
	}
	
	public void update() {
        
        moveBall();
        if (isPlaying()) {
            moveAgent(OPPONENT, players[OPPONENT].compute(sense()));
            moveAgent(PLAYER, players[PLAYER].compute(sense()));
        }
	}

	public void moveBall() {
		ball.location.x = moveBallX(ball.location.x, ball.direction.x);
		ball.location.y = moveBallY(ball.location.y, ball.direction.y);
	}
	
	private int moveBallX(int ballX, int dx) {
		
        ballX += dx;
        
        if (ballX < 1
        		&& ball.location.y >= status[PLAYER].y 
        		&& ball.location.y < status[PLAYER].y+players[PLAYER].getSize()) {
        	
        	int sectionLength = players[PLAYER].getSize()/3;
            
            if (ball.location.y < status[PLAYER].y+sectionLength) {
                dx = 1;
            }
            else if (ball.location.y < status[PLAYER].y+2*sectionLength) {
                dx = 2;
            }
            else {
                dx = 1;
            }
        	ballX = 1;
        	ball.direction.x = dx;
        }
        else if (ballX > dimension.width-2
        		&& ball.location.y >= status[OPPONENT].y 
        		&& ball.location.y < status[OPPONENT].y+players[OPPONENT].getSize()) {
        	
        	int sectionLength = players[OPPONENT].getSize()/3;
            
            if (ball.location.y < status[OPPONENT].y+sectionLength) {
                dx = -1;
            }
            else if (ball.location.y < status[OPPONENT].y+2*sectionLength) {
                dx = -2;
            }
            else {
                dx = -1;
            }
            ballX = dimension.width-2;
        	ball.direction.x = dx;
        }
        return ballX;
    }
    
    private int moveBallY(int ballY, int dy) {   
    	
        if (ballY == 0 && dy < 0 || ballY == dimension.height-1 && dy > 0) {
            dy = -dy;
            ball.direction.y = dy;
        }
        ballY += dy;
        return ballY;
    }
    
    public boolean isPlaying() {
    	return dimension.isInside(ball.location);
    }
	
	public int getWinner() {
		return (ball.location.x < 0)? OPPONENT : ((ball.location.x > dimension.width-2) ? PLAYER : -1);
	}

	public void moveAgent(int player, int dy) {
        
		int y = status[player].y + dy;
		if(isValidPosition(player, y))
			status[player].y = y;
		else
			status[player].y = (y < 0)? 0 : dimension.height - players[player].getSize();
	}

	public boolean isValidPosition(int player, int y) {
		return y >= 0 && (y <= dimension.height - players[player].getSize());
	}

}
