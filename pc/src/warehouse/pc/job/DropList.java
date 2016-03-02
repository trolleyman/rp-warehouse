package warehouse.pc.job;

import java.util.ArrayList;
import java.io.BufferedReader;
import java.io.FileReader;
import java.io.FileNotFoundException;
import java.io.IOException;

/**
 * A list of locations of items on the grid.
 */
public class DropList implements FileList {
	
	private ArrayList<Drop> dropList;
	
	public DropList(String _fileLocation) {
		parseFile(_fileLocation);
	}
	
	@Override
	public ArrayList<Drop> getList() {
		return this.dropList;
	}
	
	/**
	 * Takes a csv file and reads it into an ArrayList of drop locations.
	 */
	private void parseFile(String _fileLocation) {
		BufferedReader br = null;
		FileReader fr;
		String line;
		String[] splitLine;
		dropList = new ArrayList<Drop>();
		
		try {
			fr = new FileReader(_fileLocation);
			br = new BufferedReader(fr);
			line = br.readLine();
			
			//Create a drop object from each line and add to array.
			while (line != null) {
				splitLine = line.split(",");
				
				int x = Integer.valueOf(splitLine[0]);
				int y = Integer.valueOf(splitLine[1]);
				
				dropList.add(new Drop(x, y));
				
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
