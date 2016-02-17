package warehouse.job;

import java.util.ArrayList;

/**
 * A job available for the robot.
 */
public class Job {
	
	private final int id;
	private final ArrayList<ItemQuantity> items;
	private final float totalWeight;
	private final float totalReward;
	
	public Job(int _id, ArrayList<ItemQuantity> _items, float _totalWeight, float _totalReward) {
		this.id = _id;
		this.items = _items;
		this.totalWeight = _totalWeight;
		this.totalReward = _totalReward;
	}
	
	public int getId() {
		return this.id;
	}
	
	public ArrayList<ItemQuantity> getItems() {
		return this.items;
	}
	
	public float getTotalWeight() {
		return this.totalWeight;
	}
	
	public float getTotalReward() {
		return this.totalReward;
	}
}
