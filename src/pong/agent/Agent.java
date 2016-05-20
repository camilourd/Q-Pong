package pong.agent;

import pong.environment.Dimension;

public abstract class Agent {
	
	protected int size;
	protected int side;
	protected Dimension fieldSize;
	
	public void setSize(int size) {
		this.size = size;
	}
	
	public int getSize() {
		return size;
	}
	
	public void setSide(int side) {
		this.side = side;
	}

	public void setFieldSize(Dimension size) {
		this.fieldSize = size;
	}
	
	public abstract int compute(Percept percept);
	public abstract void init();
}
