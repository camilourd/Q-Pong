package pong.agent;

import pong.environment.Ball;

public class Percept {
	
	public int player;
	public int opponent;
	public Ball ball;
	
	public Percept(int player, int opponent, Ball ball) {
		this.player = player;
		this.opponent = opponent;
		this.ball = ball;
	}

}
