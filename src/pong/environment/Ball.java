package pong.environment;

import pong.types.Coordinate;

public class Ball {
	
	public Coordinate position;
	public Coordinate direction;
	
	public Ball(Coordinate location, Coordinate direction) {
		
		this.position = location;
		this.direction = direction;
	}
}
