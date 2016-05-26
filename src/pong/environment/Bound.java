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
	
	public boolean isInside(Coordinate coord) {
		return (coord.x >= 0 && coord.x < width) && (coord.y >= 0 && coord.y < height);
	}

}
