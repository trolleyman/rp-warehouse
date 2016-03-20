package warehouse.pc.gui;

import java.awt.BasicStroke;
import java.awt.Color;
import java.awt.Font;
import java.awt.Graphics;
import java.awt.Graphics2D;
import java.awt.Rectangle;
import java.awt.Toolkit;
import java.awt.event.MouseEvent;
import java.awt.event.MouseListener;
import java.awt.geom.AffineTransform;
import java.awt.geom.Line2D;
import java.awt.geom.Rectangle2D;
import java.util.ArrayDeque;
import java.util.ArrayList;

import javax.swing.JComponent;

import warehouse.pc.job.Location;
import warehouse.pc.job.LocationList;
import warehouse.pc.shared.Command;
import warehouse.pc.shared.CommandQueue;
import warehouse.pc.shared.Junction;
import warehouse.pc.shared.MainInterface;
import warehouse.pc.shared.Map;
import warehouse.pc.shared.RobotListener;
import warehouse.pc.shared.Robot;

@SuppressWarnings("serial")
public class MapComponent extends JComponent implements MouseListener, RobotListener {
	private final MainInterface mi = MainInterface.get();
	
	private Map map;
	
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
		map = mi.getMap();
		locList = mi.getLocationList();
		mi.addRobotListener(this);
	}

	@Override
	public void paintComponent(Graphics _g) {
		super.paintComponent(_g);
		
		Toolkit.getDefaultToolkit().sync();
		Graphics2D g2 = (Graphics2D) _g.create();
		double sf = Math.min((double) getWidth() / map.getBounds().getWidth(),
						     (double) getHeight() / map.getBounds().getHeight());
		int padding = (int) (20.0 * (sf * 0.025));
		int width = (int) (getWidth() - padding * 2);
		int height = (int) (getHeight() - padding * 2);
		
		locList = mi.getLocationList();
		double mapWidth = map.getBounds().getWidth();
		double mapHeight = map.getBounds().getHeight();
		
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
		
		xTrans = padding * 2;
		yTrans = padding * 0 + yScale * mapHeight;
		g2.translate(xTrans, yTrans);
		g2.transform(at);
		
		// Debugging code for bounds checking
		// g2.setColor(Color.GREEN);
		// Rectangle.Double bounds = map.getBounds();
		// g2.drawRect((int) (bounds.getMinX() * xScale),
		// 		(int) (bounds.getMinY() * yScale),
		// 		(int) (bounds.getWidth() * xScale),
		// 		(int) (bounds.getHeight() * yScale));
		
		paintGrid(g2);
		paintJunctions(g2);
		paintRobotTrails(g2);
		paintJunctionsText(g2);
		paintWalls(g2);
		paintRobots(g2);
	}

	private final double ROBOT_W = 0.4;
	private final double ROBOT_H = 0.6;
	private Robot selected;
	
	private void paintRobotTrails(Graphics2D _g2) {
		for (Robot robot : mi.getRobots()) {
			_g2.setColor(robot.getColor());
			CommandQueue cq = mi.getRobotManager().getCommands(robot);
			if (cq == null)
				return;
			ArrayDeque<Command> coms = new ArrayDeque<>(cq.getCommands());
			double prevX = robot.getGridX();
			double prevY = robot.getGridY();
			double x = prevX;
			double y = prevY;
			
			_g2.drawLine(
				(int)(robot.getX() * xScale),
				(int)(robot.getY() * yScale),
				(int)(x * xScale),
				(int)(y * yScale));
			
			for (Command c : coms) {
				if (c.toDirection().isPresent()) {
					// Update x & y
					switch (c.toDirection().get()) {
					case Y_POS:
						y += 1;
						break;
					case Y_NEG:
						y -= 1;
						break;
					case X_POS:
						x += 1;
						break;
					case X_NEG:
						x -= 1;
						break;
					}
					
					// Draw line from prev to updated pos
					_g2.drawLine(
						(int)(prevX * xScale),
						(int)(prevY * yScale),
						(int)(x * xScale),
						(int)(y * yScale));
					
					// Update prev
					prevX = x;
					prevY = y;
				} else {
					// Draw circle at junction to signify PICKUP/DROPOFF etc.
					int size = 8;
					double size2 = size / 2.;
					_g2.fillOval((int)(x * xScale - size2), (int)(y * yScale - size2),
						size, size);
				}
			}
		}
	}
	
	private void paintRobots(Graphics2D _g2) {
		for (Robot robot : mi.getRobots()) {
			_g2.setColor(robot.getColor());
			Graphics2D g = (Graphics2D) _g2.create();
			if (robot == selected) {
				g.setStroke(new BasicStroke(2));
			}
			
			double x = robot.getX() * xScale;
			double y = robot.getY() * yScale;
			double w = ROBOT_W * xScale;
			double h = ROBOT_H * yScale;
			
			// Simplify calculations by transforming everything
			g.translate(x, y);
			
			Graphics2D fg = (Graphics2D) g.create();
			fg.setColor(Color.BLACK);
			AffineTransform trans = new AffineTransform();
			trans.scale(1.0, -1.0);
			trans.scale(xScale * 0.015, yScale * 0.015);
			fg.transform(trans);
			int nameW = g.getFontMetrics().stringWidth(robot.getName());
			// Centre text
			fg.translate(- nameW / 2.0, 14.0);
			
			g.rotate(-Math.toRadians(robot.getFacing()));
			g.fillRect(-(int)(w / 2.0), -(int)(h / 2.0), (int)w, (int)h);
			
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
			
			fg.drawString(robot.getName(), 0, 0);
			fg.dispose();
		}
	}

	private void paintGrid(Graphics2D _g2) {
		// Draw grid first
		ArrayList<Line2D> lines = map.getGrid();
		_g2.setColor(Color.BLACK);
		for (Line2D line : lines) {
			_g2.drawLine((int) (line.getX1() * xScale), (int) (line.getY1() * yScale),
						 (int) (line.getX2() * xScale), (int) (line.getY2() * yScale));
		}
		
		// Drink numbers along the side
		Graphics2D fg = (Graphics2D) _g2.create();
		// Scale text
		fg.scale(xScale * 0.015, yScale * 0.015);
		// Flip y-axis, to make text upright
		fg.scale(1.0, -1.0);
		for (int x = 0; x < map.getWidth(); x++) {
			// This one weird formula!
			fg.drawString(Integer.toString(x), (int)(x * 66.666666666666667 - 3.0), (int)(50.0));
		}
		for (int y = 0; y < map.getHeight(); y++) {
			// Mathematicians hate him!
			fg.drawString(Integer.toString(y), (int)(-50.0), (int)(-y * 66.666666666666667 + 4.0));
		}
		fg.dispose();
	}
	
	private void paintJunctions(Graphics2D _g2) {
		for (int y = 0; y < map.getHeight(); y++) {
			for (int x = 0; x < map.getWidth(); x++) {
				Junction j = map.getJunction(x, y);
				if (j == null)
					continue;
				// For each junction j that is valid...
				
				double w = 5.0;
				double h = 5.0;
				
				_g2.setColor(Color.BLACK);
				for (Junction drop : mi.getDropList().getList()) {
					if (drop.getX() == x && drop.getY() == y) {
						w *= 1.5;
						h *= 1.5;
						_g2.setColor(Color.RED);
						break;
					}
				}
				
				_g2.fillOval((int) (x * xScale - w / 2.0) + 0,
							 (int) (y * yScale - h / 2.0) + 0,
							 (int) (w), (int) (h));
			}
		}
	}
	
	private void paintJunctionsText(Graphics2D _g2) {
		for (Location loc : locList.getList()) {
			int x = loc.getX();
			int y = loc.getY();
			
			if (map.getJunction(x, y) == null)
				continue;
			
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
	
	private void paintWalls(Graphics2D _g2) {
		Rectangle2D.Double[] walls = map.getWalls();
		Graphics2D g = (Graphics2D) _g2.create();
		g.setColor(Color.RED);
		g.setStroke(new BasicStroke(2f));
		
		for (Rectangle.Double wall : walls) {
			int x = (int) (wall.getX() * xScale);
			int y = (int) (wall.getY() * yScale);
			int w = (int) (wall.getWidth() * xScale);
			int h = (int) (wall.getHeight() * yScale);
			
			if (w == 0) {
				x -= 1;
				w  = 2;
				
				y -= 1;
				h += 2;
			}
			if (h <= 2) {
				y -= 1;
				h  = 2;
				
				x -= 1;
				w += 2;
			}
			
			g.fillRect(x, y, w, h);
		}
		
		g.dispose();
	}

	@Override
	public void mouseClicked(MouseEvent e) {
		// Get if a robot was clicked on.
		selected = null;
		for (Robot robot : mi.getRobots()) {
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

	@Override
	public void robotAdded(Robot _r) {
		this.repaint();
	}

	@Override
	public void robotRemoved(Robot _r) {
		if (selected == _r) {
			selected = null;
		}
		this.repaint();
	}
}
