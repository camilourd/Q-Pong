package pong;

import javax.swing.JFrame;

import pong.agent.qpong.QPong;
import pong.agent.qpong.QPongNoDirection;
import pong.environment.Dimension;
import pong.environment.Pong;
import pong.gui.MainFrame;

public class Main {

	public static void main(String[] args) {
        
		MainFrame main = new MainFrame(new Pong(new Dimension(49, 30), new QPongNoDirection(0.5, 100), new QPong(0.5, 10)));
        main.setSize(660, 415);
        main.setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);
		main.setVisible(true);
        
        main.play();
	}
}
