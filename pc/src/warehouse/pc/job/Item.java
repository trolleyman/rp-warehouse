package warehouse.pc.job;

/**
 * An item to be picked up in a job.
 */
public class Item {
	
	private final String name;
	private final float reward;
	private final float weight;
	private final int x;
	private final int y;
	
	public Item(String _name, float _reward, float _weight, int _x, int _y) {
		this.name = _name;
		this.reward = _reward;
		this.weight = _weight;
		this.x = _x;
		this.y = _y;
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
	
	public int getX() {
		return this.x;
	}
	
	public int getY() {
		return this.y;
	}
	
	@Override
	public String toString() {
		return name + ", " + reward + ", " + weight + ", " + x + ", " + ", " + y;
	}
}