package warehouse.gui;

import java.awt.Color;
import java.awt.Graphics;
import java.awt.Graphics2D;
import java.awt.geom.Line2D;
import java.awt.geom.Rectangle2D;
import java.awt.geom.Rectangle2D.Double;
import java.util.ArrayList;

import javax.swing.JComponent;

import warehouse.shared.Robot;

@SuppressWarnings("serial")
public class MapComponent extends JComponent {
	private final int PADDING;
	
	private Robot[] robots;
	private Map m;
	public MapComponent(State state) {
		PADDING = 20;
		robots = state.getRobots();
		m = state.getMap();
	}

	@Override
	public void paintComponent(Graphics g) {
		Graphics2D g2 = (Graphics2D) g.create();
		int width = getWidth() - PADDING * 2;
		int height = getHeight() - PADDING * 2;
		
		int mapWidth = m.getWidth() - 1;
		int mapHeight = m.getHeight() - 1;
				
		double xScale = width / mapWidth;
		double yScale = height / mapHeight;
		xScale = Math.min(xScale, yScale);
		yScale = xScale;
		//g2.transform();
		
		paintGrid(g2, xScale, yScale);
		paintJunctions(g2, xScale, yScale);
		paintWalls(g2, xScale, yScale);
		paintRobots(g2, xScale, yScale);
	}
	
	private void paintRobots(Graphics2D g2, double xScale, double yScale) {
		g2.setColor(Color.BLUE);
		for (Robot robot : robots) {
			Graphics2D g = (Graphics2D) g2.create();
			
			double x = robot.getX() * xScale + PADDING;
			double y = robot.getY() * yScale + PADDING;
			double w = 0.4 * xScale;
			double h = 0.6 * yScale;
			
			g.translate(x, y);
			g.rotate(Math.toRadians(robot.getFacing()));
			g.drawRect(-(int)(w / 2.0), -(int)(h / 2.0), (int)w, (int)h);
			
			g.dispose();
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
				g2.fillOval((int) (j.getX() * xScale + PADDING - w / 2.0) + 1,
							(int) (j.getY() * yScale + PADDING - h / 2.0) + 1,
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
