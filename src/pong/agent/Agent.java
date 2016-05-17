package pong.agent;

import pong.environment.Dimension;

public abstract class Agent {
	
	protected int size = 0;
	protected int status = 0;
	protected Dimension dimension = null;
	
	public abstract int compute(Percept percept);
	
	public void setSize(int size) {
		this.size = size;
	}
	
	public int getSize() {
		return size;
	}
	
	public void setStatus(int status) {
		this.status = status;
	}

	public void setBound(Dimension bound) {
		this.dimension = bound;
	}

}
