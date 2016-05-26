package pong.agent;

import pong.environment.Bound;

public abstract class Agent {
	
	protected int size;
	protected int side;
	protected Bound fieldSize;
	
	public void setSize(int size) {
		this.size = size;
	}
	
	public int getSize() {
		return size;
	}
	
	public void setSide(int side) {
		this.side = side;
	}

	public void setFieldSize(Bound size) {
		this.fieldSize = size;
	}
	
	public abstract int compute(Percept percept);
	public abstract void init();
}
