package warehouse.gui;

import java.awt.Color;
import java.awt.Component;
import java.awt.Graphics;
import java.awt.Graphics2D;
import java.awt.geom.Line2D;
import java.awt.geom.Rectangle2D;
import java.awt.geom.Rectangle2D.Double;
import java.util.ArrayList;

import warehouse.shared.Robot;

@SuppressWarnings("serial")
public class MapComponent extends Component {
	private final int PADDING;
	
	private Robot[] robots;
	private Map m;
	public MapComponent(Map m, Robot[] robots) {
		PADDING = 20;
		this.robots = robots;
		this.m = m;
	}
	
	@Override
	public void paint(Graphics g) {
		Graphics2D g2 = (Graphics2D) g;
		int width = getWidth() - PADDING * 2;
		int height = getHeight() - PADDING * 2;
		
		int mapWidth = m.getWidth() - 1;
		int mapHeight = m.getHeight() - 1;
				
		double xScale = width / mapWidth;
		double yScale = height / mapHeight;
		xScale = Math.min(xScale, yScale);
		yScale = xScale;
		
		paintGrid(g2, xScale, yScale);
		paintJunctions(g2, xScale, yScale);
		paintWalls(g2, xScale, yScale);
		paintRobots(g2, xScale, yScale);
	}
	
	private void paintRobots(Graphics2D g2, double xScale, double yScale) {
		g2.setColor(Color.BLUE);
		for (Robot robot : robots) {
			double x = robot.getX() * xScale + PADDING;
			double y = robot.getY() * yScale + PADDING;
			double w = 0.2 * xScale;
			double h = 0.2 * yScale;
			g2.drawRect((int) (x - w / 2), (int)(y - h / 2), (int)w, (int)h);
		}
	}

	private void paintGrid(Graphics2D g2, double xScale, double yScale) {
		ArrayList<Line2D> lines = m.getGrid();
		g2.setColor(Color.BLACK);
		for (Line2D line : lines) {
			g2.drawLine((int) (line.getX1() * xScale + PADDING), (int) (line.getY1() * yScale + PADDING),
						(int) (line.getX2() * xScale + PADDING), (int) (line.getY2() * yScale + PADDING));
		}
	}
	
	private void paintJunctions(Graphics2D g2, double xScale, double yScale) {
		for (int y = 0; y < m.getHeight(); y++) {
			for (int x = 0; x < m.getWidth(); x++) {
				Junction j = m.getJunction(x, y);
				if (j == null)
					continue;
				
				double w = 7.0;
				double h = 7.0;
				g2.fillOval((int) (j.getX() * xScale + PADDING - w / 2) + 1,
							(int) (j.getY() * yScale + PADDING - h / 2) + 1,
							(int) (w), (int) (h));
			}
		}
	}
	
	private void paintWalls(Graphics2D g2, double xScale, double yScale) {
		Rectangle2D.Double[] walls = m.getWalls();
		g2.setColor(Color.RED);
		
		for (Double wall : walls) {
			double minX = wall.getMinX();
			double minY = wall.getMinY();
			double maxX = wall.getMaxX();
			double maxY = wall.getMaxY();
			// Left
			g2.drawLine((int) (minX * xScale + PADDING), (int) (minY * yScale + PADDING),
						(int) (minX * xScale + PADDING), (int) (maxY * yScale + PADDING));
			// Right
			g2.drawLine((int) (maxX * xScale + PADDING), (int) (minY * yScale + PADDING),
						(int) (maxX * xScale + PADDING), (int) (maxY * yScale + PADDING));
			// Top
			g2.drawLine((int) (minX * xScale + PADDING), (int) (minY * yScale + PADDING),
						(int) (maxX * xScale + PADDING), (int) (minY * yScale + PADDING));
			// Bottom
			g2.drawLine((int) (minX * xScale + PADDING), (int) (maxY * yScale + PADDING),
						(int) (maxX * xScale + PADDING), (int) (maxY * yScale + PADDING));
		}
	}
}
