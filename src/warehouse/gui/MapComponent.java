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
import warehouse.shared.Server;

@SuppressWarnings("serial")
public class MapComponent extends JComponent {
	private final int PADDING;
	
	private State state;
	public MapComponent() {
		PADDING = 20;
		state = Server.get().getCurrentState();
	}

	@Override
	public void paintComponent(Graphics _g) {
		Graphics2D g2 = (Graphics2D) _g.create();
		int width = getWidth() - PADDING * 2;
		int height = getHeight() - PADDING * 2;
		
		state = Server.get().getCurrentState();
		int mapWidth = state.getMap().getWidth() - 1;
		int mapHeight = state.getMap().getHeight() - 1;
				
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
	
	private void paintRobots(Graphics2D _g2, double _xScale, double _yScale) {
		_g2.setColor(Color.BLUE);
		for (Robot robot : state.getRobots()) {
			Graphics2D g = (Graphics2D) _g2.create();
			
			double x = robot.getX() * _xScale + PADDING;
			double y = robot.getY() * _yScale + PADDING;
			double w = 0.4 * _xScale;
			double h = 0.6 * _yScale;
			
			g.translate(x, y);
			g.rotate(Math.toRadians(robot.getFacing()));
			g.drawRect(-(int)(w / 2.0), -(int)(h / 2.0), (int)w, (int)h);
			
			g.dispose();
		}
	}

	private void paintGrid(Graphics2D _g2, double _xScale, double _yScale) {
		ArrayList<Line2D> lines = state.getMap().getGrid();
		_g2.setColor(Color.BLACK);
		for (Line2D line : lines) {
			_g2.drawLine((int) (line.getX1() * _xScale + PADDING), (int) (line.getY1() * _yScale + PADDING),
						 (int) (line.getX2() * _xScale + PADDING), (int) (line.getY2() * _yScale + PADDING));
		}
	}
	
	private void paintJunctions(Graphics2D _g2, double _xScale, double _yScale) {
		for (int y = 0; y < state.getMap().getHeight(); y++) {
			for (int x = 0; x < state.getMap().getWidth(); x++) {
				Junction j = state.getMap().getJunction(x, y);
				if (j == null)
					continue;
				
				double w = 7.0;
				double h = 7.0;
				_g2.fillOval((int) (j.getX() * _xScale + PADDING - w / 2.0) + 1,
							 (int) (j.getY() * _yScale + PADDING - h / 2.0) + 1,
							 (int) (w), (int) (h));
			}
		}
	}
	
	private void paintWalls(Graphics2D _g2, double _xScale, double _yScale) {
		Rectangle2D.Double[] walls = state.getMap().getWalls();
		_g2.setColor(Color.RED);
		
		for (Double wall : walls) {
			double minX = wall.getMinX();
			double minY = wall.getMinY();
			double maxX = wall.getMaxX();
			double maxY = wall.getMaxY();
			// Left
			_g2.drawLine((int) (minX * _xScale + PADDING), (int) (minY * _yScale + PADDING),
						 (int) (minX * _xScale + PADDING), (int) (maxY * _yScale + PADDING));
			// Right
			_g2.drawLine((int) (maxX * _xScale + PADDING), (int) (minY * _yScale + PADDING),
						 (int) (maxX * _xScale + PADDING), (int) (maxY * _yScale + PADDING));
			// Top
			_g2.drawLine((int) (minX * _xScale + PADDING), (int) (minY * _yScale + PADDING),
						 (int) (maxX * _xScale + PADDING), (int) (minY * _yScale + PADDING));
			// Bottom
			_g2.drawLine((int) (minX * _xScale + PADDING), (int) (maxY * _yScale + PADDING),
						 (int) (maxX * _xScale + PADDING), (int) (maxY * _yScale + PADDING));
		}
	}
}
