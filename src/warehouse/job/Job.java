package warehouse.job;

import java.util.ArrayList;

/**
 * A job available for the robot.
 */
public class Job {
	
	private final int id;
	private final ArrayList<ItemQuantity> items;
	
	public Job(int _id, ArrayList<ItemQuantity> _items) {
		this.id = _id;
		this.items = _items;
	}
	
	public int getId() {
		return this.id;
	}
	
	public ArrayList<ItemQuantity> getItems() {
		return this.items;
	}
}
