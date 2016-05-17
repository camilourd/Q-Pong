package pong.gui;

import pong.agent.QPong;
import pong.agent.RandomPlayer;
import pong.environment.Dimension;
import pong.environment.Pong;

public class Main {

	public static void main(String[] args) {
		MainFrame main = new MainFrame(new Pong(new Dimension(49, 30), new QPong(), new RandomPlayer()));
		//MainFrame main = new MainFrame(new Pong(new Dimension(49, 30), new RandomPlayer(), new RandomPlayer()));
		main.setVisible(true);
		main.play();
	}

}
