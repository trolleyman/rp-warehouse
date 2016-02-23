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
	
	public ArrayList<Location> getList() {
		return this.locationList;
	}
	
	/**
	 * Takes a csv file and reads it into an ArrayList of locations.
	 */
	private void parseFile(String _fileLocation) {
		BufferedReader br = null;
		FileReader fr;
		String line;
		String[] splitLine;
		locationList = new ArrayList<Location>();
		
		try {
			fr = new FileReader(_fileLocation);
			br = new BufferedReader(fr);
			line = br.readLine();
			
			//Create a job object from each line and add to array.
			while (line != null) {
				splitLine = line.split(",");
				
				int x = Integer.valueOf(splitLine[0]);
				int y = Integer.valueOf(splitLine[1]);
				char itemName = splitLine[2].charAt(0);
				
				locationList.add(new Location(x, y, itemName));
				
				line = br.readLine();
			}
		} catch (FileNotFoundException e) {
			System.err.println("Location file not found: " + e);
		} catch (IOException e) {
			System.err.println("Problem reading Location file: " + e);
		} finally {
			try {
				br.close();
			} catch (IOException e) {
				System.err.println("Problem closing Location file: " + e);
			}
		}
	}
}
