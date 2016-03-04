package warehouse.pc.job;

import warehouse.pc.shared.Junction;

/**
 * An item being at a location (junction) on a grid.
 */
public class Location {
	
	private final Junction j;
	private final String itemName;
	
	public Location(int _x, int _y, String _itemName) {
		this.j = new Junction(_x, _y);
		this.itemName = _itemName;
	}
	
	public Junction getJunction() {
		return this.j;
	}
	
	public String getItemName() {
		return this.itemName;
	}
	
	public int getX() {
		return this.j.getX();
	}
	
	public int getY() {
		return this.j.getY();
	}
	
	@Override
	public String toString() {
		return j + ", " + itemName;
	}
}