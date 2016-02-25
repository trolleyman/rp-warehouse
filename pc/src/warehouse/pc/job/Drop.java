package warehouse.pc.job;

import java.io.BufferedReader;
import java.io.FileNotFoundException;
import java.io.FileReader;
import java.io.IOException;

/**
 * The grid location of the drop-off point for jobs.
 */
public class Drop {

	//Having public attributes like this is bad practise,
	//But I can't think of a good way around it yet.
	public static int xPos;
	public static int yPos;
	
	/**
	 * Takes a csv file and extracts the X and Y co-ordinates of the drop zone.
	 */
	public static void setDropPoint(String _fileLocation) {
		BufferedReader br = null;
		FileReader fr;
		String line;
		String[] splitLine;
		
		try {
			fr = new FileReader(_fileLocation);
			br = new BufferedReader(fr);
			line = br.readLine();
			
			splitLine = line.split(",");
			
			Drop.xPos = Integer.valueOf(splitLine[0]);
			Drop.yPos = Integer.valueOf(splitLine[1]);
		} catch (FileNotFoundException e) {
			System.err.println("Drop file not found: " + e);
		} catch (IOException e) {
			System.err.println("Drop reading item file: " + e);
		} finally {
			try {
				br.close();
			} catch (IOException e) {
				System.err.println("Drop closing item file: " + e);
			}
		}
	}
}