package warehouse.job;

import java.util.ArrayList;
import java.io.BufferedReader;
import java.io.FileReader;
import java.io.FileNotFoundException;
import java.io.IOException;

/**
 * A list of jobs to be selected by the robot.
 */
public class JobList {

	private ArrayList<Job> jobList;
	
	public JobList(String _fileLocation) {
		parseFile(_fileLocation);
	}
	
	public ArrayList<Job> getJobList() {
		return this.jobList;
	}
	
	private void parseFile(String _fileLocation) {
		BufferedReader br = null;
		FileReader fr;
		String line;
		String[] splitLine;
		ArrayList<ItemQuantity> items;
		jobList = new ArrayList<Job>();
		
		try {
			fr = new FileReader(_fileLocation);
			br = new BufferedReader(fr);
			line = br.readLine();
			
			while (line != null) {
				splitLine = line.split(",");
				items = new ArrayList<ItemQuantity>();
				
				for(int i = 1; i < splitLine.length; i += 2) {
					char itemName = splitLine[i].charAt(0);
					int quantity = Integer.valueOf(splitLine[i + 1]);
					ItemQuantity iq = new ItemQuantity(itemName, quantity);
					items.add(iq);
				}
				
				jobList.add(new Job(Integer.valueOf(splitLine[0]), items));
				line = br.readLine();
			}
		} catch (FileNotFoundException e) {
			System.err.println("Job file not found: " + e);
		} catch (IOException e) {
			System.err.println("Problem reading file: " + e);
		} finally {
			try {
				br.close();
			} catch (IOException e) {
				System.err.println("Problem closing job file: " + e);
			}
		}
	}
}
