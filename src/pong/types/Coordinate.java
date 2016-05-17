package pong.types;

public class Coordinate {
	
	public int x, y;
	
	public Coordinate() {}

	public Coordinate(int x, int y) {
		this.x = x;
		this.y = y;
	}
	
	public Coordinate midpoint(Coordinate coord) {
		return new Coordinate((x + coord.x) >> 1, (y + coord.y) >> 1);
	}
	
	public Coordinate sum(Coordinate coord) {
		return new Coordinate(x + coord.x, y + coord.y);
	}
	
	public double dist(Coordinate coord) {
		return Math.sqrt((double) (((x - coord.x) * (x - coord.x)) + ((y - coord.y) * (y - coord.y))));
	}
	
}
