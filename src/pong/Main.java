package pong;

import javax.swing.JFrame;

import pong.agent.BallFollower;
import pong.agent.qpong.QPong;
import pong.environment.Dimension;
import pong.environment.Pong;
import pong.gui.MainFrame;

public class Main {

	public static void main(String[] args) {
        
		MainFrame main = new MainFrame(new Pong(new Dimension(49, 30), new QPong(10), new BallFollower(0.2)));
        main.setSize(660, 415);
        main.setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);
		main.setVisible(true);
        
        main.play();
	}
}
