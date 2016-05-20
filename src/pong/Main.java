package pong;

import javax.swing.JFrame;

import pong.agent.KeyboardAgent;
import pong.agent.qpong.QPongNoDirection;
import pong.environment.Dimension;
import pong.environment.Pong;
import pong.gui.MainFrame;

public class Main {

	public static void main(String[] args) {
        
		KeyboardAgent human = new KeyboardAgent();
		QPongNoDirection computer = new QPongNoDirection(0.5, 20);
		
		MainFrame main = new MainFrame(new Pong(new Dimension(49, 30), computer, human));
		
		main.addKeyListener(human);
		
		main.setFocusable(true);
        main.setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);
		main.pack();
		main.setLocationRelativeTo(null);
		main.setVisible(true);
		
        main.play();
	}
}
