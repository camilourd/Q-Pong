package pong.gui;

import java.awt.event.WindowAdapter;
import java.awt.event.WindowEvent;

import javax.swing.JFrame;

import pong.environment.Pong;

public class GameFrame extends JFrame {

	private static final long serialVersionUID = -4760789909255896958L;
	
	private final Canvas canvas;
	private final Pong pong;
	
	private Thread tBall = null;
	private Thread tAgents = null;
	private Thread fps = null;
	
	public GameFrame(Pong pong) {
		
        this.pong = pong;
		canvas = new Canvas(pong, 20);
		
		super.add(canvas);
		this.addWindowListener( new WindowAdapter(){
			public void windowClosing( WindowEvent e ){
				performExitAction();
			}
	    });
	}
	
	public void performExitAction() {
		tBall = null;
		tAgents = null;
		fps = null;
	}

	public void play() {
		
		tBall = new Thread(new Runnable() {
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
		
		
		tAgents = new Thread(new Runnable() {
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
		
		fps = new Thread(new Runnable() {
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
