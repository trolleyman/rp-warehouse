package warehouse.pc.shared;

import java.awt.Rectangle;
import java.awt.geom.Line2D;
import java.awt.geom.Point2D;
import java.awt.geom.Rectangle2D;
import java.util.ArrayList;

import lejos.geom.Line;
import lejos.geom.Point;
import rp.robotics.mapping.GridMap;

/**
 * A map that represents a grid of junctions.
 * Some junctions can be disabled by being blocked by walls, that are calculated in the constructor.
 */
public class Map {
	//sensorOffset is the displacement between the center
	//of the robot and the sensor, measured as positive in the forward direction of the robot
	private static final int sensorOffset = 0;

	//allowedError is the threshold for deciding if a robot is in a certain position. used when
	//comparing the raw distanced recieved by the robot sensors and the expected distance.
	private static final int allowedError = 10;

	private Junction[][] js;
	private ArrayList<Line2D> grid;
	private Rectangle.Double[] walls;
	
	private int width;
	private int height;
	
	private double xOffset;
	private double yOffset;
	private double cellSize;
	// The bounds of the map.
	private Rectangle.Double bounds;
	
	private static Rectangle.Double[] linesToWalls(Line[] lines, double cellSize, double xOffset, double yOffset) {
		ArrayList<Rectangle.Double> rects = new ArrayList<>();
		for (Line line : lines) {
			Rectangle2D bounds = line.getBounds2D();
			
			rects.add(new Rectangle.Double(
					(bounds.getMinX() - xOffset) / cellSize,
					(bounds.getMinY() - yOffset) / cellSize,
					bounds.getWidth() / cellSize,
					bounds.getHeight() / cellSize));
		}
		return rects.toArray(new Rectangle.Double[rects.size()]);
	}
	
	public Map(GridMap map) {
		this.walls = linesToWalls(map.getLines(),
				map.getCellSize(),
				map.getCoordinatesOfGridPosition(0, 0).getX(),
				map.getCoordinatesOfGridPosition(0, 0).getY());
		this.width = map.getXSize();
		this.height = map.getYSize();
		this.cellSize = map.getCellSize();
		Point zero = map.getCoordinatesOfGridPosition(0, 0);
		this.xOffset = zero.getX();
		this.yOffset = zero.getY();
		this.bounds = calculateBounds();
		
		js = new Junction[height][width];
		for (int y = 0; y < height; y++) {
			for (int x = 0; x < width; x++) {
				if (!wallsContainPoint(x, y) && !map.isObstructed(x, y)) {
					js[y][x] = new Junction(x, y);
				}
			}
		}
		
		linkJunctions();
		
		constructGrid();
	}
	
	public Map(int _width, int _height, Rectangle.Double[] _walls, double _cellSize) {
		this(_width, _height, _walls, _cellSize, _cellSize / 2.0, _cellSize / 2.0);
	}
	
	public Map(int _width, int _height, Rectangle.Double[] _walls, double _cellSize, double _xOffset, double _yOffset) {
		this.walls = _walls;
		this.width = _width;
		this.height = _height;
		this.cellSize = _cellSize;
		this.xOffset = _xOffset;
		this.yOffset = _yOffset;
		this.bounds = calculateBounds();
		
		js = new Junction[height][width];
		for (int y = 0; y < height; y++) {
			for (int x = 0; x < width; x++) {
				if (!wallsContainPoint(x, y)) {
					js[y][x] = new Junction(x, y);
				}
			}
		}
		
		linkJunctions();
		
		constructGrid();
	}
	
	private Rectangle.Double calculateBounds() {
		Rectangle.Double bounds = new Rectangle.Double(
				-xOffset,
				-yOffset,
				xOffset * 2 + (width - 1),
				yOffset * 2 + (height - 1));
		
		for (Rectangle.Double wall : walls) {
			// src1, src2, dest
			Rectangle.Double.union(bounds, wall, bounds);
		}
		
		return bounds;
	}
	
	private void linkJunctions() {
		for (int y = 0; y < height; y++) {
			for (int x = 0; x < width; x++) {
				if (js[y][x] != null) {
					if (y + 1 < height && !wallsIntersectLine(x, y, x, y + 1))
						js[y][x].setJunction(Direction.Y_POS, js[y + 1][x]);
					if (y - 1 >= 0     && !wallsIntersectLine(x, y, x, y - 1))
						js[y][x].setJunction(Direction.Y_NEG, js[y - 1][x]);
					if (x + 1 < width  && !wallsIntersectLine(x, y, x + 1, y))
						js[y][x].setJunction(Direction.X_POS, js[y][x + 1]);
					if (x - 1 >= 0     && !wallsIntersectLine(x, y, x - 1, y))
						js[y][x].setJunction(Direction.X_NEG, js[y][x - 1]);
				}
			}
		}
	}
	
	private void constructGrid() {
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
		grid = lines;
	}
	
	private boolean wallsIntersectLine(int _x1, int _y1, int _x2, int _y2) {
		for (Rectangle.Double wall : walls) {
			if (wall.intersectsLine(_x1, _y1, _x2, _y2)) {
				return true;
			}
		}
		return false;
	}
	
	private boolean wallsContainPoint(int _x, int _y) {
		double w = 0.1;
		double h = 0.1;
		
		double h2 = h / 2.0;
		double w2 = w / 2.0;
		
		for (Rectangle.Double wall : walls) {
			if (wall.intersects(_x-w2, _y-h2, w, h)) {
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
	 * Transforms Grid x co-ordinate into real-life x coordinate.
	 * Real-life coordinates start at the 0,0 junction.
	 */
	public double getRealX(double gridX) {
		return gridX * cellSize;
	}
	
	/**
	 * Transforms Grid y co-ordinate into real-life y coordinate.
	 * Real-life coordinates start at the 0,0 junction.
	 */
	public double getRealY(double gridY) {
		return gridY * cellSize;
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
					Integer[] pos = new Integer[2];
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
			//if the x and y coords are in possible positions, return 1/numPossiblePositions
			{
				return (1/possPositions.size());
			}
		}
		//return 0 if these x,y coords correspond to an impossible position based on the distance recieved.
		return 0;
	}

	/**
	 * Returns the bounding box of the map
	 */
	public Rectangle.Double getBounds() {
		return bounds;
	}

	public double getCellSize() {
		return cellSize;
	}

	public double getGridX(double realX) {
		return realX / cellSize;
	}
	
	public double getGridY(double realY) {
		return realY / cellSize;
	}
}
