package warehouse.pc.job;

/**
 * An item being at a location (junction) on a grid.
 */
public class Location {
	
	private final int x;
	private final int y;
	private final Item item;
	
	public Location(int _x, int _y, Item _item) {
		this.x = _x;
		this.y = _y;
		this.item = _item;
	}
	
	public int getX() {
		return this.x;
	}
	
	public int getY() {
		return this.y;
	}
	
	public Item getItem() {
		return this.item;
	}
}