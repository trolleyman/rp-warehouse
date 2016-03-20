package warehouse.pc.job;

import java.util.ArrayList;
import java.io.BufferedReader;
import java.io.FileReader;
import java.io.FileNotFoundException;
import java.io.IOException;

/**
 * A list of locations of items on the grid.
 */
public class LocationList implements FileList {
	
	private ArrayList<Location> locationList;
	
	public LocationList(String _fileLocation) {
		parseFile(_fileLocation);
	}
	
	@Override
	public ArrayList<Location> getList() {
		return this.locationList;
	}
	
	/**
	 * Takes a csv file and reads it into an ArrayList of locations.
	 */
	private void parseFile(String _fileLocation) {
		//Initialise variables.
		BufferedReader br = null;
		FileReader fr;
		String line;
		String[] splitLine;
		locationList = new ArrayList<Location>();
		
		try {
			//Start file readers.
			fr = new FileReader(_fileLocation);
			br = new BufferedReader(fr);
			line = br.readLine();
			
			//Create a location object from each line and add to array.
			while (line != null) {
				//Split line into array.
				splitLine = line.split(",");
				
				int x = Integer.valueOf(splitLine[0]);
				int y = Integer.valueOf(splitLine[1]);
				String itemName = splitLine[2];
				
				//Create location and add to list.
				locationList.add(new Location(x, y, itemName));
				
				line = br.readLine();
			}
		} catch (FileNotFoundException e) {
			System.err.println("Location file not found: " + e);
			System.exit(1);
		} catch (IOException e) {
			System.err.println("Problem reading Location file: " + e);
			System.exit(1);
		} finally {
			try {
				br.close();
			} catch (IOException e) {
				System.err.println("Problem closing Location file: " + e);
			}
		}
	}
}
