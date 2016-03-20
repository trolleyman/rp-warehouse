package warehouse.pc.job;

import java.util.ArrayList;

import warehouse.pc.search.RouteFinder;
import warehouse.pc.shared.Junction;
import warehouse.pc.shared.Map;

public class TSPDistance {
	
	private RouteFinder routeFinder;
	ArrayList<Junction> dropLocations;
	
	public TSPDistance(Map map, ArrayList<Junction> dropLocations){
		routeFinder = new RouteFinder(map);
		this.dropLocations = dropLocations;
	}
	
	public int getDistance(Job job, int x, int y){
		ArrayList<ItemQuantity> items = new ArrayList<>(job.getItems());
		int[] dToItem = new int[items.size()];
		for(int i=0; i<items.size(); i++){
			dToItem[i] = (routeFinder.findRoute(new Junction(x, y), items.get(i).getItem().getJunction()).size());
		}
		ArrayList<ItemQuantity> res = new ArrayList<ItemQuantity>();
		
		int first = 0;
		int firstValue = dToItem[0];
		for(int i=1; i<items.size(); i++){
			if(dToItem[i] < firstValue){
				first = i;
				firstValue = dToItem[i];
			}
		}
		ItemQuantity firstSelected = items.get(first);
		int totalDistance = firstValue;
		res.add(firstSelected);
		Object[] itemArray = items.toArray();
		int[][] itemToItem = new int[items.size()][items.size()];
		for(int i=0; i<items.size(); i++){
			for(int j=0; j<items.size(); j++){
				itemToItem[i][j] = (routeFinder.findRoute(items.get(i).getItem().getJunction(), items.get(j).getItem().getJunction()).size());
			}
		}
		int[] itemToDrop = new int[items.size()];
		for(int i=0; i<items.size(); i++){
			int temp = Integer.MAX_VALUE;
			for(int j=0; j<dropLocations.size(); j++){
				int len = routeFinder.findRoute(items.get(i).getItem().getJunction(), dropLocations.get(j)).size();
				if(len < temp){
					temp = len;
				}
			}
			itemToDrop[i] = temp;
		}
		items.remove(firstSelected);
		
		while(!items.isEmpty()){
			int next = Integer.MAX_VALUE;
			int nextIndex = 0;
			int resIndex = 0;
			for(int i=0; i<items.size(); i++){
				int firstItem = 0;
				int firstRes = 0;
				int secondRes = 0;
				for(int k=0; k<itemArray.length; k++){
					if(itemArray[k].equals(items.get(i))){
						firstItem = k;
						break;
					}
				}
				for(int l=0; l<itemArray.length; l++){
					if(itemArray[l].equals(res.get(0))){
						firstRes = l;
						break;
					}
				}
				for(int m=0; m<itemArray.length; m++){
					if(itemArray[m].equals(res.get(res.size()-1))){
						secondRes = m;
						break;
					}
				}
				int distance1 = itemToItem[firstItem][firstRes];
				int distance2 = itemToItem[firstItem][secondRes];
				if(distance1 < distance2 && distance1 < next){
					next = distance1;
					nextIndex = i;
					resIndex = 0;
				}
				else if (distance2 < distance1 && distance2 < next){
					next = distance2;
					nextIndex = i;
					resIndex = res.size()-1;
				}
			}
			
			ItemQuantity nextSelected = items.get(nextIndex);
			totalDistance += next;
			res.add(resIndex, nextSelected);;
			items.remove(nextSelected);
		}
		
		int lastValue = Integer.MAX_VALUE;
		for(int i=0; i<res.size(); i++){
			int current = 0;
			for(int j=0; j<itemArray.length; j++){
				if(itemArray[j].equals(res.get(i))){
					current = j;
					break;
				}
			}
			if(itemToDrop[current] < lastValue) {
				lastValue = itemToDrop[current];
			}
		}
		
		totalDistance += lastValue;
		
		return totalDistance;
	}
}
