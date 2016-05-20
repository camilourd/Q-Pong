package pong.agent;

import pong.environment.Ball;

public class Percept {
	
	public int left;
	public int right;
	public Ball ball;
	
	public Percept(int leftPosition, int rightPosition, Ball ball) {
		
		this.left = leftPosition;
		this.right = rightPosition;
		this.ball = ball;
	}
}
