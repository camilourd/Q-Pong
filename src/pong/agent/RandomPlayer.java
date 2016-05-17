package pong.agent;

import pong.utils;

public class RandomPlayer extends Agent {

	@Override
	public int compute(Percept percept) {
		return utils.rand(5) - 2;
	}

	@Override
	public void init() {
		
	}

}
