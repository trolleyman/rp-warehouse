package warehouse.pc.job;

import warehouse.pc.shared.Junction;

/**
 * An item to be picked up in a job.
 */
public class Item {
	
	private final String name;
	private final float reward;
	private final float weight;
	private final Junction j;
	
	public Item(String _name, float _reward, float _weight, int _x, int _y) {
		this.name = _name;
		this.reward = _reward;
		this.weight = _weight;
		this.j = new Junction(_x, _y);
	}
	
	public String getName() {
		return this.name;
	}
	
	public float getReward() {
		return this.reward;
	}
	
	public float getWeight() {
		return this.weight;
	}
	
	public Junction getJunction() {
		return this.j;
	}
	
	public int getX() {
		return this.j.getX();
	}
	
	public int getY() {
		return this.j.getY();
	}
	
	@Override
	public String toString() {
		return name + ", " + reward + ", " + weight + ", " + j;
	}
}