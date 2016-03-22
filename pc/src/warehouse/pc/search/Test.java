package warehouse.pc.search;

import java.util.HashMap;

/**
 * Messing about with hash maps
 * @author George Kaye
 *
 */

public class Test {

	public static void main(String args[]){
		
		HashMap<Integer, Boolean> map = new HashMap<>();
		
		boolean kek = true;
		
		map.put(10, kek);
		
		System.out.println(map.get(10));
		
		map.put(10, false);
		
		System.out.println(kek);
		System.out.println(map.get(10));
		
		kek = true;
		
		System.out.println(map.get(10));
		
		
		
	}
	
	
	
}

