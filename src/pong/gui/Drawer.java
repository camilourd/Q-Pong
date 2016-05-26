package pong.gui;

import java.awt.BasicStroke;
import java.awt.Color;
import java.awt.Graphics;
import java.awt.Graphics2D;
import pong.environment.Bound;
import pong.environment.Pong;

public class Drawer {
	
	protected final Pong pong;
	
	protected int scale;
	
	private final float dashV[] = {10.0f};
    
	private final BasicStroke dashed = new BasicStroke(1.0f, BasicStroke.CAP_BUTT, 
                                                      BasicStroke.JOIN_MITER, 10.0f, dashV, 0.0f);
	
	public Drawer(Pong pong, int scale) {
        
		this.pong = pong;
		this.scale = scale;
	}

	public void paint(Graphics g) {
        
		Graphics2D graph = (Graphics2D) g;
        
		Bound bound = pong.fieldSize;
        
		graph.setColor(Color.BLACK);
		graph.fillRect(0, 0, (bound.width + 2) * scale, (bound.height + 2) * scale);
        
		graph.setColor(Color.white);
		graph.fillRect(scale, 0, bound.width * scale, scale);
		graph.fillRect(scale, (bound.height + 1) * scale, bound.width * scale, scale);
        
		if(bound.isInside(pong.ball.position)) {
            graph.setColor(Color.BLUE);
			graph.fillRect((pong.ball.position.x + 1) * scale, (pong.ball.position.y + 1) * scale, scale, scale);
        }
        
        graph.setColor(Color.WHITE);
		int size = pong.agents[Pong.LEFT].getSize();
		graph.fillRect(scale, (pong.agentPosition[Pong.LEFT].y + 1) * scale, scale, size * scale);
		graph.fillRect(bound.width * scale, (pong.agentPosition[Pong.RIGHT].y + 1) * scale, scale, size * scale);
        
		graph.setStroke(dashed);
		graph.drawLine((bound.width + 2) / 2 * scale, 0, (bound.width + 2) / 2 * scale, (bound.height + 2) * scale);
        
		graph.dispose();
	}

}
