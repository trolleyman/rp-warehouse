package warehouse.pc.job;

/**
 * The name of an item paired with the quantity of the item
 */
public class ItemQuantity {
	
	private final Item item;
	private final int quantity;
	
	public ItemQuantity(Item _item, int _quantity) {
		this.item = _item;
		this.quantity = _quantity;
	}
	
	public Item getItem() {
		return this.item;
	}
	
	public int getQuantity() {
		return this.quantity;
	}
	
	public String toString(){
		return item.getName() + ": " + quantity;
	}
	
	public boolean equals(Object obj){
		if(obj == null){
			return false;
		}
		if(((ItemQuantity)obj).getItem().getName().equals(this.getItem().getName())){
			return true;
		}
		else
		{
			return false;
		}
	}
	
	public int hashCode(){
		return item.getName().hashCode();
	}
}
