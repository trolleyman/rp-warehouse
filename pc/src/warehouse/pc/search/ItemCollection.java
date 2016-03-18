package warehouse.pc.search;

import java.util.ArrayList;

import warehouse.pc.job.ItemQuantity;

public class ItemCollection {

	private ArrayList<ItemQuantity> list;
	private float weight;
	
	public ItemCollection(){
		list = new ArrayList<ItemQuantity>();
		weight = 0;
	}
	
	public void addItem(ItemQuantity item){
		list.add(item);
		weight = weight + (item.getItem().getWeight() * item.getQuantity());
	}
	
	public ItemQuantity getItem(int i){
		return list.get(i);
	}
	
	public ArrayList<ItemQuantity> getCollection(){
		return this.list;
	}
	
	public float getWeight(){
		return this.weight;
	}
	
	public String toString(){
		
		String items = "";
		
		for(int i = 0; i < list.size(); i++){
			items = items + ", " + list.get(i);
		}
		
		return "[" + items + "] : weight " + weight;
		
	}
	
	
}
