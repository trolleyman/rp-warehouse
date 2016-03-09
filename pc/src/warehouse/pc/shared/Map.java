package warehouse.pc.shared;

import java.awt.Rectangle;
import java.awt.geom.Line2D;
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
		return new Rectangle.Double(-xOffset, -yOffset, xOffset * 2 + width * cellSize, yOffset * 2 + height * cellSize);
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
}
