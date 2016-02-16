package warehouse.gui;

import java.awt.Rectangle;
import java.awt.geom.Line2D;
import java.util.ArrayList;

import warehouse.shared.Direction;

public class Map {
	private Junction[][] js;
	private int width;
	private int height;

	public Map(int width, int height, Rectangle.Double[] doubles) {
		this.width = width;
		this.height = height;
		
		js = new Junction[height][width];
		for (int y = 0; y < height; y++) {
			for (int x = 0; x < width; x++) {
				if (!rectanglesContainsPoint(doubles, x, y)) {
					js[y][x] = new Junction(x, y);
				}
			}
		}
		
		for (int y = 0; y < height; y++) {
			for (int x = 0; x < width; x++) {
				if (js[y][x] != null) {
					if (y + 1 < height)
						js[y][x].setJunction(Direction.YPos, js[y + 1][x]);
					if (y - 1 > 0)
						js[y][x].setJunction(Direction.YNeg, js[y - 1][x]);
					if (x + 1 < width)
						js[y][x].setJunction(Direction.XPos, js[y][x + 1]);
					if (x - 1 > 0)
						js[y][x].setJunction(Direction.XNeg, js[y][x - 1]);
				}
			}
		}
	}
	
	private boolean rectanglesContainsPoint(Rectangle.Double[] rects, int x, int y) {
		for (Rectangle.Double rect : rects) {
			if (rect.contains(x, y)) {
				return true;
			}
		}
		return false;
	}
	
	public int getWidth() {
		return width;
	}
	public int getHeight() {
		return height;
	}
	
	public ArrayList<Line2D> getGrid() {
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
}
