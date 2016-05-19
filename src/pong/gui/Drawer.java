package pong.gui;

import java.awt.BasicStroke;
import java.awt.Color;
import java.awt.Graphics;
import java.awt.Graphics2D;
import pong.environment.Dimension;
import pong.environment.Pong;

public class Drawer {
	
	private final Pong pong;
	
	public int CELL_SIZE;

	public int width, height;
	
	public final float dashV[] = {10.0f};
    
	public final BasicStroke dashed = new BasicStroke(1.0f, BasicStroke.CAP_BUTT, 
                                                      BasicStroke.JOIN_MITER, 10.0f, dashV, 0.0f);
	
	public Drawer(Pong pong, int width, int height) {
        
		this.pong = pong;
		this.width = width;
		this.height = height;
        
		CELL_SIZE = Math.min(width / (pong.dimension.width + 2), height / (pong.dimension.height + 2));
	}

	public void paint(Graphics g) {
        
		Graphics2D graph = (Graphics2D) g;
        
		Dimension bound = pong.dimension;
        
		graph.setColor(Color.BLACK);
		graph.fillRect(0, 0, (bound.width + 2) * CELL_SIZE, (bound.height + 2) * CELL_SIZE);
        
		graph.setColor(Color.white);
		graph.fillRect(CELL_SIZE, 0, bound.width * CELL_SIZE, CELL_SIZE);
		graph.fillRect(CELL_SIZE, (bound.height + 1) * CELL_SIZE, bound.width * CELL_SIZE, CELL_SIZE);
        
		if(bound.isInside(pong.ball.location)) {
            graph.setColor(Color.BLUE);
			graph.fillRect((pong.ball.location.x + 1) * CELL_SIZE, (pong.ball.location.y + 1) * CELL_SIZE, CELL_SIZE, CELL_SIZE);
        }
        
        graph.setColor(Color.WHITE);
		int size = pong.players[Pong.PLAYER].getSize();
		graph.fillRect(CELL_SIZE, (pong.status[Pong.PLAYER].y + 1) * CELL_SIZE, CELL_SIZE, size * CELL_SIZE);
		graph.fillRect(bound.width * CELL_SIZE, (pong.status[Pong.OPPONENT].y + 1) * CELL_SIZE, CELL_SIZE, size * CELL_SIZE);
        
		graph.setStroke(dashed);
		graph.drawLine((bound.width + 2) / 2 * CELL_SIZE, 0, (bound.width + 2) / 2 * CELL_SIZE, (bound.height + 2) * CELL_SIZE);
        
		graph.dispose();
	}

}
