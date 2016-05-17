package pong.environment;

import pong.types.Coordinate;

public class Ball {
	
	public Coordinate location;
	public Coordinate direction;
	
	public Ball(Coordinate location, Coordinate direction) {
		this.location = location;
		this.direction = direction;
	}

}
