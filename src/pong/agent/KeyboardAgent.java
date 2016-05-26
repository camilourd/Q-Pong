package pong.agent;

import java.awt.event.KeyEvent;
import java.awt.event.KeyListener;

import pong.environment.Pong;

public class KeyboardAgent extends Agent implements KeyListener {

	private int direction;
	
	@Override
	public int compute(Percept percept) {
		
		int d = direction;
		direction = 0;
		return d;
	}

	@Override
	public void init() {
		
	}

	@Override
	public void keyPressed(KeyEvent k) {
		
		if (side == Pong.LEFT) {
			if (k.getKeyCode() == KeyEvent.VK_A) {
				direction = -1;
			}
			else if (k.getKeyCode() == KeyEvent.VK_Z) {
				direction = 1;
			}
		}
		else {
			if (k.getKeyCode() == KeyEvent.VK_UP) {
				direction = -1;
			}
			else if (k.getKeyCode() == KeyEvent.VK_DOWN) {
				direction = 1;
			}
		}
	}

	@Override
	public void keyReleased(KeyEvent arg0) {
	}

	@Override
	public void keyTyped(KeyEvent k) {
	}
}
