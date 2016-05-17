package pong.environment;

import pong.types.Coordinate;

public class Dimension {
	
	public int width;
	public int height;
	
	public Dimension(int width, int height) {
		this.width = width;
		this.height = height;
	}
	
	public Coordinate center() {
		return new Coordinate(width / 2, height / 2);
	}

}
