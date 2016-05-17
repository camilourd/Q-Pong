package pong.gui;

import java.awt.Dimension;
import java.awt.Graphics;

import javax.swing.JPanel;

import pong.environment.Pong;

public class Canvas extends JPanel {

	private static final long serialVersionUID = 8083254851294821748L;
	
	protected Drawer drawer;
	
	public Canvas(Pong pong) {
		drawer = new Drawer(pong, 700, 600);
	}
	
	public void paint(Graphics g){
	    super.paint(g);
	    if(drawer != null)
	        drawer.paint(g);
	}

	@Override
	public Dimension getPreferredSize() {
		return new Dimension(700, 600);
	}

}
