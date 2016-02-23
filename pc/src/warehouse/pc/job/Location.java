package warehouse.pc.job;

/**
 * An item being at a location (junction) on a grid.
 */
public class Location {
	
	private final int x;
	private final int y;
	private final char itemName;
	
	public Location(int _x, int _y, char _itemName) {
		this.x = _x;
		this.y = _y;
		this.itemName = _itemName;
	}
	
	public int getX() {
		return this.x;
	}
	
	public int getY() {
		return this.y;
	}
	
	public char getItemName() {
		return this.itemName;
	}
}