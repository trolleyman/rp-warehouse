package warehouse.pc.job;

import java.util.List;

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

	public static String listToString(List<ItemQuantity> iqs) {
		StringBuilder b = new StringBuilder();
		for (ItemQuantity iq : iqs) {
			b.append(iq.getQuantity());
			b.append(" ");
			b.append(iq.getItem().getName());
			b.append(", ");
		}
		if (iqs.size() > 0) {
			// Delete last ", "
			b.deleteCharAt(b.length() - 1);
			b.deleteCharAt(b.length() - 1);
		}
		return b.toString();
	}
	
	@Override
	public String toString() {
		return quantity + " " + item;
	}
}
