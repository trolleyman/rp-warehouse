package warehouse.gui;

import java.awt.Component;
import java.awt.Graphics;
import java.awt.Graphics2D;

@SuppressWarnings("serial")
public class MapComponent extends Component {
	private Map m;
	public MapComponent(Map m) {
		this.m = m;
	}
	
	@Override
	public void paint(Graphics g) {
		Graphics2D g2 = (Graphics2D) g;
		int width = getWidth();
		int height = getHeight();
		
		int mapWidth = m.getWidth();
		int mapHeight = m.getHeight();
				
		double xscale = width / mapWidth;
		double yscale = height / mapHeight;
	}
}
