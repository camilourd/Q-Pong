package pong.agent;

public abstract class Agent {
	
	protected int size = 0;
	
	public abstract int compute(Percept percept);
	
	public void setSize(int size) {
		this.size = size;
	}
	
	public int getSize() {
		return size;
	}

}
