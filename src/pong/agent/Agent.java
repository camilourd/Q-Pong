package pong.agent;

import pong.environment.Dimension;

public abstract class Agent {
	
	protected int size;
    
	protected int status;
    
	protected Dimension dimension;
	
	public abstract int compute(Percept percept);
	public abstract void init();
	
	public void setSize(int size) {
		this.size = size;
	}
	
	public int getSize() {
		return size;
	}
	
	public void setStatus(int status) {
		this.status = status;
	}

	public void setBound(Dimension dimension) {
		this.dimension = dimension;
	}

}
