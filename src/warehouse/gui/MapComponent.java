package warehouse.gui;

import java.awt.Color;
import java.awt.Graphics;
import java.awt.Graphics2D;
import java.awt.Toolkit;
import java.awt.geom.AffineTransform;
import java.awt.geom.Line2D;
import java.awt.geom.Rectangle2D;
import java.awt.geom.Rectangle2D.Double;
import java.util.ArrayList;

import javax.swing.JComponent;

import warehouse.shared.Junction;
import warehouse.shared.Robot;
import warehouse.shared.Server;
import warehouse.shared.State;

@SuppressWarnings("serial")
public class MapComponent extends JComponent {
	private State state;
	public MapComponent() {
		state = Server.get().getCurrentState();
	}

	@Override
	public void paintComponent(Graphics _g) {
		Toolkit.getDefaultToolkit().sync();
		Graphics2D g2 = (Graphics2D) _g.create();
		double sf = Math.min((double) getWidth() / state.getMap().getWidth(),
						     (double) getHeight() / state.getMap().getHeight());
		int padding = (int) (20.0 * (sf * 0.025));
		int width = getWidth() - padding * 3;
		int height = getHeight() - padding * 3;
		
		state = Server.get().getCurrentState();
		int mapWidth = state.getMap().getWidth() - 1;
		int mapHeight = state.getMap().getHeight() - 1;
		
		double xScale = width / mapWidth;
		double yScale = height / mapHeight;
		xScale = Math.min(xScale, yScale);
		yScale = xScale;
		AffineTransform at = new AffineTransform();
		at.setToScale(1.0, -1.0);
		
		g2.translate(padding * 2, padding);
		g2.translate(0.0, yScale * mapHeight);
		g2.transform(at);
		
		paintGrid(g2, xScale, yScale);
		paintJunctions(g2, xScale, yScale);
		paintWalls(g2, xScale, yScale);
		paintRobots(g2, xScale, yScale);
	}
	
	private void paintRobots(Graphics2D _g2, double _xScale, double _yScale) {
		_g2.setColor(Color.BLUE);
		for (Robot robot : state.getRobots()) {
			Graphics2D g = (Graphics2D) _g2.create();
			
			double x = robot.getX() * _xScale;
			double y = robot.getY() * _yScale;
			double w = 0.4 * _xScale;
			double h = 0.6 * _yScale;
			
			g.translate(x, y);
			
			Graphics2D fg = (Graphics2D) g.create();
			fg.setColor(Color.BLACK);
			AffineTransform trans = new AffineTransform();
			trans.scale(1.0, -1.0);
			trans.scale(_xScale * 0.015, _yScale * 0.015);
			fg.transform(trans);
			int nameW = g.getFontMetrics().stringWidth(robot.getName());
			fg.translate(- nameW / 2.0, 14.0);
			fg.drawString(robot.getName(), 0, 0);
			fg.dispose();
			
			g.rotate(Math.toRadians(robot.getFacing()));
			g.drawRect(-(int)(w / 2.0), -(int)(h / 2.0), (int)w, (int)h);
			
			double robotEndY = h / 2.0;
			double arrowLength = h * 0.4;
			double arrowEndY = robotEndY + arrowLength;
			double arrowHeadLength = h * 0.2;
			double arrowHeadEndX = arrowHeadLength;
			double arrowHeadEndY = arrowEndY - arrowHeadLength;
			
			// Arrow base
			g.drawLine(0, (int) robotEndY, 0, (int) arrowEndY);
			// Left arrow head
			g.drawLine(0, (int) arrowEndY,
				(int) (arrowHeadEndX), (int) (arrowHeadEndY));
			// Right arrow head
			g.drawLine(0, (int) arrowEndY,
				(int) (-arrowHeadEndX), (int) (arrowHeadEndY));
			
			g.dispose();
		}
	}

	private void paintGrid(Graphics2D _g2, double _xScale, double _yScale) {
		ArrayList<Line2D> lines = state.getMap().getGrid();
		_g2.setColor(Color.BLACK);
		for (Line2D line : lines) {
			_g2.drawLine((int) (line.getX1() * _xScale), (int) (line.getY1() * _yScale),
						 (int) (line.getX2() * _xScale), (int) (line.getY2() * _yScale));
		}
		
		Graphics2D fg = (Graphics2D) _g2.create();
		fg.scale(_xScale * 0.015, _yScale * 0.015);
		fg.scale(1.0, -1.0);
		for (int x = 0; x < state.getMap().getWidth(); x++) {
			fg.drawString(Integer.toString(x), (int)(x * 66.666666666666667 - 3.0), (int)(50.0));
		}
		for (int y = 0; y < state.getMap().getHeight(); y++) {
			fg.drawString(Integer.toString(y), (int)(-50.0), (int)(-y * 66.666666666666667 + 4.0));
		}
		fg.dispose();
	}
	
	private void paintJunctions(Graphics2D _g2, double _xScale, double _yScale) {
		for (int y = 0; y < state.getMap().getHeight(); y++) {
			for (int x = 0; x < state.getMap().getWidth(); x++) {
				Junction j = state.getMap().getJunction(x, y);
				if (j == null)
					continue;
				
				double w = 7.0;
				double h = 7.0;
				_g2.fillOval((int) (j.getX() * _xScale - w / 2.0) + 1,
							 (int) (j.getY() * _yScale - h / 2.0) + 1,
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
			_g2.drawLine((int) (minX * _xScale), (int) (minY * _yScale),
						 (int) (minX * _xScale), (int) (maxY * _yScale));
			// Right
			_g2.drawLine((int) (maxX * _xScale), (int) (minY * _yScale),
						 (int) (maxX * _xScale), (int) (maxY * _yScale));
			// Top
			_g2.drawLine((int) (minX * _xScale), (int) (minY * _yScale),
						 (int) (maxX * _xScale), (int) (minY * _yScale));
			// Bottom
			_g2.drawLine((int) (minX * _xScale), (int) (maxY * _yScale),
						 (int) (maxX * _xScale), (int) (maxY * _yScale));
		}
	}
}
