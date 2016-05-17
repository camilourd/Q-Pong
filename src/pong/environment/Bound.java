package pong.environment;

import pong.types.Coordinate;

public class Bound {
	
	public int width;
	public int height;
	
	public Bound(int width, int height) {
		this.width = width;
		this.height = height;
	}
	
	public Coordinate center() {
		return new Coordinate(width / 2, height / 2);
	}

}
