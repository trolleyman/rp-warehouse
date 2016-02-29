package warehouse.pc.gui;

import java.awt.BasicStroke;
import java.awt.Color;
import java.awt.Font;
import java.awt.Graphics;
import java.awt.Graphics2D;
import java.awt.Toolkit;
import java.awt.event.MouseEvent;
import java.awt.event.MouseListener;
import java.awt.geom.AffineTransform;
import java.awt.geom.Line2D;
import java.awt.geom.Rectangle2D;
import java.awt.geom.Rectangle2D.Double;
import java.util.ArrayList;

import javax.swing.JComponent;

import warehouse.pc.job.Location;
import warehouse.pc.job.LocationList;
import warehouse.pc.shared.Junction;
import warehouse.pc.shared.MainInterface;
import warehouse.pc.shared.RobotListener;
import warehouse.pc.shared.State;
import warehouse.shared.robot.Robot;

@SuppressWarnings("serial")
public class MapComponent extends JComponent implements MouseListener, RobotListener {
	private State state;
	private LocationList locList;
	
	private double xScale = 1.0;
	private double yScale = 1.0;
	
	private double xTrans;
	private double yTrans;
	
	private Gui gui;
	
	public MapComponent(Gui _gui) {
		super();
		this.gui = _gui;
		addMouseListener(this);
		state = MainInterface.get().getCurrentState();
		locList = MainInterface.get().getLocationList();
		MainInterface.get().addRobotListener(this);
	}

	@Override
	public void paintComponent(Graphics _g) {
		Toolkit.getDefaultToolkit().sync();
		Graphics2D g2 = (Graphics2D) _g.create();
		double sf = Math.min((double) getWidth() / state.getMap().getWidth(),
						     (double) getHeight() / state.getMap().getHeight());
		int padding = (int) (20.0 * (sf * 0.025));
		int width = (int) (getWidth() - padding * 2.5);
		int height = (int) (getHeight() - padding * 2.1);
		
		state = MainInterface.get().getCurrentState();
		locList = MainInterface.get().getLocationList();
		int mapWidth = state.getMap().getWidth() - 1;
		int mapHeight = state.getMap().getHeight() - 1;
		
		xScale = width / mapWidth;
		yScale = height / mapHeight;
		xScale = Math.min(xScale, yScale);
		yScale = xScale;
		
		//actualW = (int) (xScale * mapWidth  + padding * 2.5) + 1;
		//actualH = (int) (yScale * mapHeight + padding * 2.1) + 1;
		//g2.drawRect(0, 0, actualW, actualH);
		//this.setSize(actualW, actualH);
		
		AffineTransform at = new AffineTransform();
		at.setToScale(1.0, -1.0);
		
		xTrans = padding * 1.75;
		yTrans = padding * 0.5 + yScale * mapHeight;
		g2.translate(padding * 1.75, padding * 0.5);
		g2.translate(0.0, yScale * mapHeight);
		g2.transform(at);
		
		paintGrid(g2);
		paintJunctions(g2);
		paintWalls(g2);
		paintRobots(g2);
	}
	
	private final double ROBOT_W = 0.4;
	private final double ROBOT_H = 0.6;
	private Robot selected;
	
	private void paintRobots(Graphics2D _g2) {
		_g2.setColor(Color.BLUE);
		for (Robot robot : state.getRobots()) {
			Graphics2D g = (Graphics2D) _g2.create();
			if (robot == selected) {
				g.setStroke(new BasicStroke(2));
			}
			
			double x = robot.getX() * xScale;
			double y = robot.getY() * yScale;
			double w = ROBOT_W * xScale;
			double h = ROBOT_H * yScale;
			
			g.translate(x, y);
			
			Graphics2D fg = (Graphics2D) g.create();
			fg.setColor(Color.BLACK);
			AffineTransform trans = new AffineTransform();
			trans.scale(1.0, -1.0);
			trans.scale(xScale * 0.015, yScale * 0.015);
			fg.transform(trans);
			int nameW = g.getFontMetrics().stringWidth(robot.getName());
			fg.translate(- nameW / 2.0, 14.0);
			fg.drawString(robot.getName(), 0, 0);
			fg.dispose();
			
			g.rotate(-Math.toRadians(robot.getFacing()));
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

	private void paintGrid(Graphics2D _g2) {
		ArrayList<Line2D> lines = state.getMap().getGrid();
		_g2.setColor(Color.BLACK);
		for (Line2D line : lines) {
			_g2.drawLine((int) (line.getX1() * xScale), (int) (line.getY1() * yScale),
						 (int) (line.getX2() * xScale), (int) (line.getY2() * yScale));
		}
		
		Graphics2D fg = (Graphics2D) _g2.create();
		fg.scale(xScale * 0.015, yScale * 0.015);
		fg.scale(1.0, -1.0);
		for (int x = 0; x < state.getMap().getWidth(); x++) {
			fg.drawString(Integer.toString(x), (int)(x * 66.666666666666667 - 3.0), (int)(50.0));
		}
		for (int y = 0; y < state.getMap().getHeight(); y++) {
			fg.drawString(Integer.toString(y), (int)(-50.0), (int)(-y * 66.666666666666667 + 4.0));
		}
		fg.dispose();
	}
	
	private void paintJunctions(Graphics2D _g2) {
		for (int y = 0; y < state.getMap().getHeight(); y++) {
			for (int x = 0; x < state.getMap().getWidth(); x++) {
				for (Location loc : locList.getList()) {
					if (loc.getX() == x && loc.getY() == y) {
						Graphics2D fg = (Graphics2D) _g2.create();
						if (loc.getItemName().equals(gui.selectedItemName)) {
							Font f = fg.getFont();
							f = f.deriveFont(Font.BOLD, (float) (f.getSize() * 1.5));
							fg.setFont(f);
						}
						fg.setColor(Color.RED);
						AffineTransform trans = new AffineTransform();
						trans.scale(1.0, -1.0);
						trans.scale(xScale * 0.015, yScale * 0.015);
						
						int nameW = (int) (fg.getFontMetrics().stringWidth(loc.getItemName()) * xScale * 0.015);
						fg.translate(-nameW / 2.0, 14.0);
						fg.translate(x * xScale, y * yScale);
						fg.transform(trans);
						fg.drawString(loc.getItemName(), 0, 0);
						fg.dispose();
					}
				}
				
				Junction j = state.getMap().getJunction(x, y);
				if (j == null)
					continue;
				
				double w = 7.0;
				double h = 7.0;
				_g2.fillOval((int) (j.getX() * xScale - w / 2.0) + 0,
							 (int) (j.getY() * yScale - h / 2.0) + 0,
							 (int) (w), (int) (h));
			}
		}
	}
	
	private void paintWalls(Graphics2D _g2) {
		Rectangle2D.Double[] walls = state.getMap().getWalls();
		_g2.setColor(Color.RED);
		
		for (Double wall : walls) {
			double minX = wall.getMinX();
			double minY = wall.getMinY();
			double maxX = wall.getMaxX();
			double maxY = wall.getMaxY();
			// Left
			_g2.drawLine((int) (minX * xScale), (int) (minY * yScale),
						 (int) (minX * xScale), (int) (maxY * yScale));
			// Right
			_g2.drawLine((int) (maxX * xScale), (int) (minY * yScale),
						 (int) (maxX * xScale), (int) (maxY * yScale));
			// Top
			_g2.drawLine((int) (minX * xScale), (int) (minY * yScale),
						 (int) (maxX * xScale), (int) (minY * yScale));
			// Bottom
			_g2.drawLine((int) (minX * xScale), (int) (maxY * yScale),
						 (int) (maxX * xScale), (int) (maxY * yScale));
		}
	}

	@Override
	public void mouseClicked(MouseEvent e) {
		// Get if a robot was clicked on.
		selected = null;
		for (Robot robot : MainInterface.get().getCurrentState().getRobots()) {
			double x = e.getX();
			double y = e.getY();
			
			double cx = robot.getX() * xScale + xTrans;
			double cy = -robot.getY() * yScale + yTrans;
			
			double s = Math.sin(-Math.toRadians(robot.getFacing()));
			double c = Math.cos(-Math.toRadians(robot.getFacing()));
			
			// translate point back to origin:
			x -= cx;
			y -= cy;
			
			// rotate point
			double xnew = x * c - y * s;
			double ynew = x * s + y * c;
			
			// translate point back:
			x = xnew + cx;
			y = ynew + cy;
			
			double w = ROBOT_W * xScale;
			double h = ROBOT_H * yScale;
			
			double w2 = w / 2.0;
			double h2 = h / 2.0;
			
			if (x >= cx - w2 && x <= cx + w2
			 && y >= cy - h2 && y <= cy + h2) {
				// Intersection!
				selected = robot;
				break;
			}
		}
		
		// Select holds the robot that was clicked on.
		gui.selectRobot(selected);
		this.repaint();
	}
	
	@Override public void mouseEntered(MouseEvent e) {}
	@Override public void mouseExited(MouseEvent e) {}
	@Override public void mousePressed(MouseEvent e) {}
	@Override public void mouseReleased(MouseEvent e) {}

	@Override
	public void robotChanged(Robot _r) {
		this.repaint();
	}
}
