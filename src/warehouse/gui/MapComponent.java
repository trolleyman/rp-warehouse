package warehouse.gui;

import java.awt.Component;
import java.awt.Graphics;
import java.awt.Graphics2D;
import java.awt.geom.Line2D;
import java.util.ArrayList;

@SuppressWarnings("serial")
public class MapComponent extends Component {
	private Map m;
	public MapComponent(Map m) {
		this.m = m;
	}
	
	@Override
	public void paint(Graphics g) {
		Graphics2D g2 = (Graphics2D) g;
		final int PADDING = 10;
		int width = getWidth() - PADDING * 2;
		int height = getHeight() - PADDING * 2;
		
		int mapWidth = m.getWidth() - 1;
		int mapHeight = m.getHeight() - 1;
				
		double xscale = width / mapWidth;
		double yscale = height / mapHeight;
		xscale = Math.min(xscale, yscale);
		yscale = xscale;
		
		ArrayList<Line2D> lines = m.getGrid();
		for (Line2D line : lines) {
			g2.drawLine((int) (line.getX1() * xscale + PADDING), (int) (line.getY1() * yscale + PADDING),
						(int) (line.getX2() * xscale + PADDING), (int) (line.getY2() * yscale + PADDING));
		}
	}
}
