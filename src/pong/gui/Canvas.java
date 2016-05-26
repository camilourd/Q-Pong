package pong.gui;

import java.awt.Dimension;
import java.awt.Graphics;

import javax.swing.JPanel;

import pong.environment.Pong;

public class Canvas extends JPanel {
 
	private static final long serialVersionUID = 6665053824579884888L;
	
	private Drawer drawer;
	
	public Canvas(Pong pong, int scale) {
		drawer = new Drawer(pong, scale);
	}
	
    @Override
	public void paint(Graphics g) {
        
	    super.paint(g);
	    if(drawer != null)
	        drawer.paint(g);
	}

	@Override
	public Dimension getPreferredSize() {
		return new Dimension((drawer.pong.fieldSize.width+2)*drawer.scale, (drawer.pong.fieldSize.height+2)*drawer.scale);
	}

}
