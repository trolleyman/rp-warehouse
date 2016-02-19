package warehouse.pc.job;

/**
 * An item to be picked up in a job.
 */
public class Item {
	
	private final char name;
	private final float reward;
	private final float weight;
	
	public Item(char _name, float _reward, float _weight) {
		this.name = _name;
		this.reward = _reward;
		this.weight = _weight;
	}
	
	public char getName() {
		return this.name;
	}
	
	public float getReward() {
		return this.reward;
	}
	
	public float getWeight() {
		return this.weight;
	}
}