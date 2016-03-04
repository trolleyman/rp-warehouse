package warehouse.pc.job;

import java.io.BufferedReader;
import java.io.FileNotFoundException;
import java.io.FileReader;
import java.io.IOException;
import java.util.ArrayList;

import warehouse.pc.shared.Junction;

/**
 * A list of locations of items on the grid.
 */
public class DropList implements FileList {
	
	private ArrayList<Junction> dropList;
	
	public DropList(String _fileLocation) {
		parseFile(_fileLocation);
	}
	
	@Override
	public ArrayList<Junction> getList() {
		return this.dropList;
	}
	
	/**
	 * Takes a csv file and reads it into an ArrayList of drop locations.
	 */
	private void parseFile(String _fileLocation) {
		//Initialise variables.
		BufferedReader br = null;
		FileReader fr;
		String line;
		String[] splitLine;
		dropList = new ArrayList<Junction>();
		
		try {
			//Start file readers.
			fr = new FileReader(_fileLocation);
			br = new BufferedReader(fr);
			line = br.readLine();
			
			//Create a drop object from each line and add to array.
			while (line != null) {
				//split line into array.
				splitLine = line.split(",");
				
				try {
					int x = Integer.valueOf(splitLine[0]);
					int y = Integer.valueOf(splitLine[1]);
					
					//Create drop point and add to list.
					dropList.add(new Junction(x, y));
				} catch (NumberFormatException e) {
					// Ignore line.
				}
				
				line = br.readLine();
			}
		} catch (FileNotFoundException e) {
			System.err.println("Drop file not found: " + e);
		} catch (IOException e) {
			System.err.println("Problem reading Drop file: " + e);
		} finally {
			try {
				br.close();
			} catch (IOException e) {
				System.err.println("Problem closing Drop file: " + e);
			}
		}
	}
}
