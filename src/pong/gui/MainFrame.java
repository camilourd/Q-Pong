package pong.gui;

import javax.swing.JFrame;

import pong.environment.Pong;

public class MainFrame extends JFrame implements Runnable {

	private static final long serialVersionUID = -4760789909255896958L;
	
	private final Canvas canvas;
	private final Pong pong;
	
	public MainFrame(Pong pong) {
        this.pong = pong;
		canvas = new Canvas(pong);
		super.add(canvas);
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
				Thread.sleep(10);
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
