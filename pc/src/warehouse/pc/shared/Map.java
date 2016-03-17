package warehouse.pc.shared;

import java.awt.Rectangle;
import java.awt.geom.Line2D;
import java.awt.geom.Point2D;
import java.util.ArrayList;

/**
 * A map that represents a grid of junctions.
 * Some junctions can be disabled by being blocked by walls, that are calculated in the constructor.
 */
public class Map {
	//sensorOffset is the displacement between the center 
	//of the robot and the sensor, measured as positive in the forward direction of the robot
	private final int sensorOffset;

	//allowedError is the threshold for deciding if a robot is in a certain position. used when
	//comparing the raw distanced recieved by the robot sensors and the expected distance.
	private final int allowedError;

	private Junction[][] js;
	private ArrayList<Line2D> grid;
	private Rectangle.Double[] walls;
	
	private int width;
	private int height;

	public Map(int _width, int _height, Rectangle.Double[] _walls) {
		this.walls = _walls;
		this.width = _width;
		this.height = _height;
		
		js = new Junction[height][width];
		for (int y = 0; y < height; y++) {
			for (int x = 0; x < width; x++) {
				if (!rectanglesContainsPoint(walls, x, y)) {
					js[y][x] = new Junction(x, y);
				}
			}
		}
		
		for (int y = 0; y < height; y++) {
			for (int x = 0; x < width; x++) {
				if (js[y][x] != null) {
					if (y + 1 < height && !rectanglesIntersectLine(walls, x, y, x, y + 1))
						js[y][x].setJunction(Direction.Y_POS, js[y + 1][x]);
					if (y - 1 >= 0     && !rectanglesIntersectLine(walls, x, y, x, y - 1))
						js[y][x].setJunction(Direction.Y_NEG, js[y - 1][x]);
					if (x + 1 < width  && !rectanglesIntersectLine(walls, x, y, x + 1, y))
						js[y][x].setJunction(Direction.X_POS, js[y][x + 1]);
					if (x - 1 >= 0     && !rectanglesIntersectLine(walls, x, y, x - 1, y))
						js[y][x].setJunction(Direction.X_NEG, js[y][x - 1]);
				}
			}
		}
		
		grid = constructGrid();
	}
	
	private ArrayList<Line2D> constructGrid() {
		ArrayList<Line2D> lines = new ArrayList<>();
		for (int y = 0; y < js.length; y++) {
			for (int x = 0; x < js[y].length; x++) {
				Junction j = js[y][x];
				if (j == null)
					continue;
				Junction ypos = js[y][x].getJunction(Direction.Y_POS);
				Junction xpos = js[y][x].getJunction(Direction.X_POS);
				if (ypos != null)
					lines.add(new Line2D.Double(j.getX(), j.getY(), ypos.getX(), ypos.getY()));
				if (xpos != null)
					lines.add(new Line2D.Double(j.getX(), j.getY(), xpos.getX(), xpos.getY()));
			}
		}
		return lines;
	}
	
	private boolean rectanglesIntersectLine(Rectangle.Double[] _rects, int _x1, int _y1, int _x2, int _y2) {
		for (Rectangle.Double r : _rects) {
			if (r.intersectsLine(_x1, _y1, _x2, _y2)) {
				return true;
			}
		}
		return false;
	}
	
	private boolean rectanglesContainsPoint(Rectangle.Double[] _rects, int _x, int _y) {
		double w = 0.1;
		double h = 0.1;
		
		double h2 = h / 2.0;
		double w2 = w / 2.0;
		
		for (Rectangle.Double rect : _rects) {
			if (rect.intersects(_x-w2, _y-h2, w, h)) {
				return true;
			}
		}
		return false;
	}
	
	/**
	 * Returns the width of the map
	 */
	public int getWidth() {
		return width;
	}
	/**
	 * Returns the height of the map
	 */
	public int getHeight() {
		return height;
	}
	
	/**
	 * Returns the junction located at x, y on the map, or null if it does not exist.
	 */
	public Junction getJunction(int _x, int _y) {
		if (_x >= 0 && _x < width && _y >= 0 && _y < height)
			return js[_y][_x];
		return null;
	}
	
	/**
	 * Returns a list of lines that represent the grid.
	 */
	public ArrayList<Line2D> getGrid() {
		return grid;
	}
	
	/**
	 * Gets the walls that are in this map
	 */
	public Rectangle.Double[] getWalls() {
		return walls;
	}
	
	/**
	 * Gets the distance in units from (x,y) to the closest wall in a certain direction.
	 * 1 unit = 1 unit on grid. convert outside of this class.
	 */
	public double getRangeAt(double _x, double _y, Direction _dir) {
		return getRangeAt(_x, _y, _dir.toFacing());
	}
	
	/**
	 * Returns the distance from the position {@code x,y} on the grid in the direction {@code facing}. If no object
	 * is in the way, returns Infinity.
	 * @param _x the y position
	 * @param _y the x position
	 * @param _facing the degrees clockwise from Y+
	 * @return the distance to the nearest obstacle, or Infinity if it doesn't reach an obstacle.
	 */
	public double getRangeAt(double _x, double _y, double _facing) {
		double minDistSquared = Double.POSITIVE_INFINITY;
		double facingRads = Math.toRadians(_facing);
		
		for (Rectangle.Double wall : walls) {
			Point2D.Double p = rectRaycast(wall, _x, _y, facingRads);
			if (p != null) {
				double distSq = p.distanceSq(_x, _y);
				
				if (distSq < minDistSquared) {
					minDistSquared = distSq;
				}
			}
		}
		
		return Math.sqrt(minDistSquared);
	}
	
	/**
	 * Returns the point where a ray starting at x,y and continuing at angle {@code _facing} (radians clockwise from Y+)
	 * hits the rectangle {@code _rect}, or null if it doesn't hit.
	 */
	private Point2D.Double rectRaycast(Rectangle.Double _rect, double _x, double _y, double _facing) {
		ArrayList<Point2D.Double> ps = new ArrayList<Point2D.Double>(4);
		
		double minX = _rect.getMinX();
		double maxX = _rect.getMaxX();
		double minY = _rect.getMinY();
		double maxY = _rect.getMaxY();
		
		ps.add(lineRaycastVertical(_x, _y, _facing, minX, minY, maxY));
		ps.add(lineRaycastVertical(_x, _y, _facing, maxX, minY, maxY));
		ps.add(lineRaycastHorizontal(_x, _y, _facing, minY, minX, maxX));
		ps.add(lineRaycastHorizontal(_x, _y, _facing, maxY, minX, maxX));
		
		// Get min distance point.
		Point2D.Double ret = null;
		double minDistSq = Double.POSITIVE_INFINITY;
		for (Point2D.Double p : ps) {
			if (p != null) {
				double distSq = p.distanceSq(_x, _y);
				
				if (ret == null || distSq < minDistSq) {
					ret = p;
					minDistSq = distSq;
				}
			}
		}
		return ret;
	}
	
	private Point2D.Double lineRaycastVertical(double fromX, double fromY, double facing, double toX, double minY, double maxY) {
		double m = 1 / Math.tan(facing);
		
		if (Double.isInfinite(m)) {
			return null;
		}
		
		double toY = m * (toX - fromX);
		
		if (toY < minY || toY > maxY) {
			return null;
		}
		
		return new Point2D.Double(toX, toY);
	}
	
	private Point2D.Double lineRaycastHorizontal(double fromX, double fromY, double facing, double toY, double minX, double maxX) {
		double m = Math.tan(facing);
		
		if (Double.isInfinite(m)) {
			return null;
		}
		
		double toX = m * (toY - fromY);
		
		if (toX < minX || toX > maxX) {
			return null;
		}
		
		return new Point2D.Double(toX, toY);
	}

	/**
	 *Gets a list of the possible positions the robot could be in based on it's current position and facing direction
	 *Returns an arraylist of integer arrays of size 2. [x_coord,y_coord]
	 */
	public ArrayList<Integer[]> possiblePositions(int distanceRecieved,Direction facing)
	{
		ArrayList<Integer[]> positions = new ArrayList<Integer[]>();
		for (int i = 0;i < width;i++)
		{
			for (int j = 0; j < height; j++)
			{
				if (Math.abs((distanceRecieved + sensorOffset) - getRangeAt(width,height,facing)) < allowedError)
				{
					int[] pos = new Int[2];
					pos[0] = i;
					pos[1] = j;
					positions.add(pos);
				}
			}	
		}
		return positions;
	}

	/**
	 *Gets the probability of being at position x,y,facing...given the distance the sensor is reading.
	 *Currently ignores previous probabilities and sets all possible locations to equal probabilities.
	 *e.g - if there are 5 positions the robot can be in based on the distance it recieves and the direction it is facing,
	 *this will return 0.2 if we call it with any of these positions, or 0 for a position that isn't possible.
	 */
	public double getProbability(int x,int y,Direction facing,int dist)
	{
		ArrayList<Integer[]> possPositions = possiblePositions(dist,facing);
		for (int i = 0;i < possPositions.size();i++)
		{
			if (possPositions.get(i)[0] == x && possPositions.get(i)[1] == y)
			{
				return (1/possPositions.size());
			}
		}
		return 0;
	}



}
