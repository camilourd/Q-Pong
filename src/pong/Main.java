package pong;

import javax.swing.JFrame;

import pong.gui.MainFrame;

public class Main {

	public static void main(String[] args) {
		MainFrame main = new MainFrame();
		main.setFocusable(true);
        main.setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);
		main.pack();
		main.setLocationRelativeTo(null);
		main.setVisible(true);
	}
}
