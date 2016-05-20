package pong.gui;

import javax.swing.JFrame;

import pong.environment.Pong;

public class MainFrame extends JFrame {

	private static final long serialVersionUID = -4760789909255896958L;
	
	private final Canvas canvas;
	private final Pong pong;
	
	public MainFrame(Pong pong) {
		
        this.pong = pong;
		canvas = new Canvas(pong, 20);
		
		super.add(canvas);
	}
	
	public void play() {
		
		Thread tBall = new Thread(new Runnable() {
			@Override
			public void run() {
				
				while (true) {
					pong.moveBall();
					try {
						Thread.sleep(80);
					} catch (InterruptedException e) {}
				}
			}
		});
		
		
		Thread tAgents = new Thread(new Runnable() {
			@Override
			public void run() {
				
				while (true) {
			        if (pong.isPlaying()) {
			        	pong.moveAgent(Pong.RIGHT);
			        	pong.moveAgent(Pong.LEFT);
			            try {
							Thread.sleep(1);
						} catch (InterruptedException e) {}
			        } else {
			        	pong.init();
			        }
				}
			}
		});
		tAgents.setPriority(Thread.MAX_PRIORITY);
		
		Thread fps = new Thread(new Runnable() {
			@Override
			public void run() {
				
				while (true) {
					canvas.repaint();
					try {
						Thread.sleep(5);
					} catch (InterruptedException e) {}
				}
			}
		});
		
		fps.start();
		tBall.start();
		tAgents.start();
	}
}
