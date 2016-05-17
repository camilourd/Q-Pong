package pong.gui;

import java.awt.Dimension;

import javax.swing.JFrame;

import pong.environment.Pong;

public class MainFrame extends JFrame implements Runnable {

	private static final long serialVersionUID = -2471338664392279677L;
	
	private Canvas canvas;
	private Pong pong;
	
	public MainFrame(Pong pong) {
		setSize(new Dimension(715, 485));
		canvas = new Canvas(pong);
		this.add(canvas);
		this.pong = pong;
	}
	
	public void play() {
		
		Thread t = new Thread(this);
		t.setPriority(Thread.MAX_PRIORITY);
		t.start();
	}
	
	@Override
	public void run() {
		
		while (true) {
			if (!pong.isPlaying())
				pong.init();
			else
				pong.update();
			try {
				Thread.sleep(80);
			} catch (InterruptedException e) {
				continue;
			}
			draw();
		}
	}
	
	public void draw() {
		canvas.repaint();
	}

}
