package warehouse.pc.job;

import java.io.BufferedReader;
import java.io.FileNotFoundException;
import java.io.FileReader;
import java.io.IOException;
import java.util.ArrayList;

/**
 * A list of items to be available for pickup in jobs.
 */
public class ItemList {

	private ArrayList<Item> itemList;
	
	public ItemList(String _fileLocation) {
		parseFile(_fileLocation);
	}
	
	public ArrayList<Item> getItemList() {
		return this.itemList;
	}
	
	/**
	 * Takes a csv file and reads it into an ArrayList of items.
	 */
	private void parseFile(String _fileLocation) {
		BufferedReader br = null;
		FileReader fr;
		String line;
		String[] splitLine;
		itemList = new ArrayList<Item>();
		
		try {
			fr = new FileReader(_fileLocation);
			br = new BufferedReader(fr);
			line = br.readLine();
			
			//Create an item object from each line and add to array.
			while (line != null) {
				splitLine = line.split(",");
				
				char name = splitLine[0].charAt(0);
				float reward = Float.valueOf(splitLine[1]);
				float weight = Float.valueOf(splitLine[2]);
				
				Item item = new Item(name, reward, weight);
				itemList.add(item);
				
				line = br.readLine();
			}
		} catch (FileNotFoundException e) {
			System.err.println("Item file not found: " + e);
		} catch (IOException e) {
			System.err.println("Problem reading item file: " + e);
		} finally {
			try {
				br.close();
			} catch (IOException e) {
				System.err.println("Problem closing item file: " + e);
			}
		}
	}
}
