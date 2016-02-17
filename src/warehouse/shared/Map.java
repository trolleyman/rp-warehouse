package warehouse.shared;

import java.awt.Rectangle;
import java.awt.geom.Line2D;
import java.util.ArrayList;

/**
 * A map that represents a grid of junctions.
 * Some junctions can be diabled by being blocked by walls, that are calculated in the constructor.
 */
public class Map {
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
						js[y][x].setJunction(Direction.YPos, js[y + 1][x]);
					if (y - 1 > 0      && !rectanglesIntersectLine(walls, x, y, x, y - 1))
						js[y][x].setJunction(Direction.YNeg, js[y - 1][x]);
					if (x + 1 < width  && !rectanglesIntersectLine(walls, x, y, x + 1, y))
						js[y][x].setJunction(Direction.XPos, js[y][x + 1]);
					if (x - 1 > 0      && !rectanglesIntersectLine(walls, x, y, x - 1, y + 1))
						js[y][x].setJunction(Direction.XNeg, js[y][x - 1]);
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
				Junction ypos = js[y][x].getJunction(Direction.YPos);
				Junction xpos = js[y][x].getJunction(Direction.XPos);
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
}
