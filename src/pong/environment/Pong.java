package pong.environment;

import pong.agent.Agent;
import pong.agent.Percept;
import pong.types.Coordinate;
import pong.utils;

public class Pong {
	
	public static final int LEFT = 0;
	public static final int RIGHT = 1;
	
	private final int[] deltaX = {-2, -1};
    private final int[] deltaY = {-1, 1};
	
	public Bound fieldSize;
	public Agent[] agents = new Agent[2];
	public Coordinate[] agentPosition = new Coordinate[2];
	public Ball ball;
	
	public Pong(Bound fieldSize, Agent leftSide, Agent rightSide) {
        
		leftSide.setSize(fieldSize.height / 5);
        leftSide.setSide(LEFT);
        leftSide.setFieldSize(fieldSize);
        
		rightSide.setSize(fieldSize.height / 5);
		rightSide.setSide(RIGHT);
		rightSide.setFieldSize(fieldSize);
        
		this.fieldSize = fieldSize;
		this.agents[LEFT] = leftSide;
		this.agents[RIGHT] = rightSide;
        
        init();
	}
	
	public final void init() {
        
		ball = new Ball(new Coordinate(fieldSize.width-2, utils.rand(fieldSize.height)), new Coordinate(deltaX[utils.rand(2)], deltaY[utils.rand(2)]));
		ball.position.y = utils.rand(fieldSize.height);
		
		int size = agents[LEFT].getSize();
		agentPosition[LEFT] = new Coordinate(0, (fieldSize.height - size) / 2);
		
		size = agents[RIGHT].getSize();
		agentPosition[RIGHT] = new Coordinate(fieldSize.width-1, (fieldSize.height - size) / 2);
		
		agents[LEFT].init();
		agents[RIGHT].init();
	}

	public Percept sense() {
		return new Percept(agentPosition[LEFT].y, agentPosition[RIGHT].y, ball);
	}

	public synchronized void moveAgent(int player) {
        
		int dy = agents[player].compute(sense());
		int y = agentPosition[player].y + dy;
		
		if(isValidPosition(player, y))
			agentPosition[player].y = y;
		else
			agentPosition[player].y = (y < 0)? 0 : fieldSize.height - agents[player].getSize();
	}

	public void moveBall() {
		
		ball.position.x = moveBallX(ball.position.x, ball.direction.x);
		ball.position.y = moveBallY(ball.position.y, ball.direction.y);
	}
	
	
	private int moveBallX(int ballX, int dx) {
        ballX += dx;
        
        if (ballX < 1
        		&& ball.position.y >= agentPosition[LEFT].y 
        		&& ball.position.y < agentPosition[LEFT].y+agents[LEFT].getSize()) {
        	
        	int sectionLength = agents[LEFT].getSize()/3;
            
            if (ball.position.y < agentPosition[LEFT].y+sectionLength) {
                dx = 1;
            }
            else if (ball.position.y < agentPosition[LEFT].y+2*sectionLength) {
                dx = 2;
            }
            else {
                dx = 1;
            }
        	ballX = 1;
        	ball.direction.x = dx;
        }
        else if (ballX > fieldSize.width-2
        		&& ball.position.y >= agentPosition[RIGHT].y 
        		&& ball.position.y < agentPosition[RIGHT].y+agents[RIGHT].getSize()) {
        	
        	int sectionLength = agents[RIGHT].getSize()/3;
            
            if (ball.position.y < agentPosition[RIGHT].y+sectionLength) {
                dx = -1;
            }
            else if (ball.position.y < agentPosition[RIGHT].y+2*sectionLength) {
                dx = -2;
            }
            else {
                dx = -1;
            }
            ballX = fieldSize.width-2;
        	ball.direction.x = dx;
        }
        return ballX;
    }
    
    private int moveBallY(int ballY, int dy) {   
    	
        if (ballY == 0 && dy < 0 || ballY == fieldSize.height-1 && dy > 0) {
            dy = -dy;
            ball.direction.y = dy;
        }
        ballY += dy;
        return ballY;
    }
    
    public boolean isPlaying() {
    	return fieldSize.isInside(ball.position);
    }
	
	public int getWinner() {
		return (ball.position.x <= 0)? RIGHT : ((ball.position.x >= fieldSize.width-1) ? LEFT : -1);
	}

	public boolean isValidPosition(int player, int y) {
		return y >= 0 && (y <= fieldSize.height - agents[player].getSize());
	}
}
