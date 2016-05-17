package pong.environment;

import pong.types.Coordinate;

public class Ball {
	
	protected Coordinate location;
	protected Coordinate direction;
	
	public Ball(Coordinate location, Coordinate direction) {
		this.location = location;
		this.direction = direction;
	}

}
