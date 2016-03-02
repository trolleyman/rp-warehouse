package warehouse.pc.job;

import java.io.BufferedReader;
import java.io.FileNotFoundException;
import java.io.FileReader;
import java.io.IOException;
import java.util.ArrayList;

/**
 * A list of items to be available for pickup in jobs.
 */
public class ItemList implements FileList {

	private ArrayList<Item> itemList;
	private LocationList locList;
	
	public ItemList(String _fileLocation, LocationList _locList) {
		this.locList = _locList;
		parseFile(_fileLocation);
	}
	
	@Override
	public ArrayList<Item> getList() {
		return this.itemList;
	}
	
	/**
	 * Takes a csv file and reads it into an ArrayList of items.
	 */
	private void parseFile(String _fileLocation) {
		//Initialise variables.
		BufferedReader br = null;
		FileReader fr;
		String line;
		String[] splitLine;
		itemList = new ArrayList<Item>();
		
		try {
			//Create file readers.
			fr = new FileReader(_fileLocation);
			br = new BufferedReader(fr);
			line = br.readLine();
			
			//Create an item object from each line and add to array.
			while (line != null) {
				//Split line into array.
				splitLine = line.split(",");
				
				String name = splitLine[0];
				float reward = Float.valueOf(splitLine[1]);
				float weight = Float.valueOf(splitLine[2]);
				
				//Find x, y coordinates of item from name (go through list until found).
				boolean found = false;
				int count = 0;
				while (!found && count < locList.getList().size()) {
					if(name.equals(locList.getList().get(count).getItemName())) {
						found = true;
					} else {
						count++;
					}
				}
				
				//If found, set co-ords, else throw an exception.
				//The exception shouldn't be thrown if the files are correct.
				int x;
				int y;
				if (found) {
					x = locList.getList().get(count).getX();
					y = locList.getList().get(count).getY();
				} else {
					throw new ItemNotInListException(name);
				}
				
				//Create item and add to list.
				Item item = new Item(name, reward, weight, x, y);
				itemList.add(item);
				
				line = br.readLine();
			}
		} catch (ItemNotInListException e) {
			//Item in the list wasn't in location list.
			System.err.println("Item " + e + " not found in location file");
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
