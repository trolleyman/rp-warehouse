package warehouse.job;

/**
 * The name of an item paired with the quantity of the item
 */
public class ItemQuantity {
	
	private final char name;
	private final int quantity;
	
	public ItemQuantity(char _name, int _quantity) {
		this.name = _name;
		this.quantity = _quantity;
	}
	
	public char getName() {
		return this.name;
	}
	
	public int getQuantity() {
		return this.quantity;
	}
}
