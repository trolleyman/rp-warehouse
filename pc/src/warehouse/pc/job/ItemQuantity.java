package warehouse.pc.job;

/**
 * The name of an item paired with the quantity of the item
 */
public class ItemQuantity {
	
	private final String name;
	private final int quantity;
	
	public ItemQuantity(String _name, int _quantity) {
		this.name = _name;
		this.quantity = _quantity;
	}
	
	public String getName() {
		return this.name;
	}
	
	public int getQuantity() {
		return this.quantity;
	}
}
