package pong.environment;

import pong.agent.Agent;
import pong.agent.Percept;
import pong.types.Coordinate;
import pong.utils;

public class Pong {
	
	public static final int PLAYER = 0;
	public static final int OPPONENT = 1;
	
	protected Dimension dimension;
	protected Agent[] players = new Agent[2];
	protected Coordinate[] status = new Coordinate[2];
	protected Ball ball;
	
	public Pong(Dimension dimension, Agent player, Agent opponent) {
		player.setSize(dimension.height / 4);
		opponent.setSize(dimension.height / 4);
		player.setStatus(PLAYER);
		opponent.setStatus(OPPONENT);
		player.setBound(dimension);
		opponent.setBound(dimension);
		this.dimension = dimension;
		this.players[PLAYER] = player;
		this.players[OPPONENT] = opponent;
		this.status[PLAYER] = new Coordinate(2, dimension.height / 2);
		this.status[OPPONENT] = new Coordinate(dimension.width - 2, dimension.height / 2);
		this.ball = new Ball(dimension.center(), new Coordinate(1 + utils.rand(2), 1 + utils.rand(2)));
	}
	
	public Percept sense() {
		return new Percept(status[PLAYER].y, status[OPPONENT].x, ball);
	}
	
	public void update() {
		moveAgent(OPPONENT, players[OPPONENT].compute(sense()));
		moveAgent(PLAYER, players[PLAYER].compute(sense()));
		moveBall();
	}

	public void moveBall() {
		
	}
	
	public int getWinner() {
		return -1;
	}

	public void moveAgent(int player, int dy) {
		int y = status[player].y + dy;
		if(isValidPosition(player, y))
			status[player].y = y;
	}

	public boolean isValidPosition(int player, int y) {
		return (y >= (players[player].getSize() / 2)) && (y <= dimension.height - (players[player].getSize() / 2));
	}

}
