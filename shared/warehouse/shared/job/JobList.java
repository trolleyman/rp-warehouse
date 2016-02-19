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
	private ArrayList<Item> itemList;
	
	public JobList(String _fileLocation, ItemList items) {
		this.itemList = items.getItemList();
		parseFile(_fileLocation);
	}
	
	public ArrayList<Job> getJobList() {
		return this.jobList;
	}
	
	/**
	 * Takes a csv file and reads it into an ArrayList of jobs.
	 */
	private void parseFile(String _fileLocation) {
		BufferedReader br = null;
		FileReader fr;
		String line;
		String[] splitLine;
		ArrayList<ItemQuantity> iqs;
		jobList = new ArrayList<Job>();
		
		try {
			fr = new FileReader(_fileLocation);
			br = new BufferedReader(fr);
			line = br.readLine();
			
			//Create a job object from each line and add to array.
			while (line != null) {
				splitLine = line.split(",");
				iqs = new ArrayList<ItemQuantity>();
				
				for(int i = 1; i < splitLine.length; i += 2) {
					char itemName = splitLine[i].charAt(0);
					int quantity = Integer.valueOf(splitLine[i + 1]);
					ItemQuantity iq = new ItemQuantity(itemName, quantity);
					iqs.add(iq);
				}
				
				Job job = new Job(Integer.valueOf(splitLine[0]), iqs, calculateTotalWeight(iqs), calculateTotalReward(iqs));
				jobList.add(job);
				
				line = br.readLine();
			}
		} catch (FileNotFoundException e) {
			System.err.println("Job file not found: " + e);
		} catch (IOException e) {
			System.err.println("Problem reading job file: " + e);
		} finally {
			try {
				br.close();
			} catch (IOException e) {
				System.err.println("Problem closing job file: " + e);
			}
		}
	}
	
	/**
	 * Calculate the total weight of all items in a job.
	 */
	private float calculateTotalWeight(ArrayList<ItemQuantity> _iqs) {
		float totalWeight = 0;
		
		for (int i = 0; i < _iqs.size(); i++) {
			 char name = _iqs.get(i).getName();
			 boolean itemFound = false;
			 int count = 0;
			 
			 while (!itemFound) {
				 if (itemList.get(count).getName() == name) {
					 int quantity = _iqs.get(i).getQuantity();
					 float weight = itemList.get(count).getWeight() * quantity;
					 totalWeight += weight;
					 itemFound = true;
				 } else {
					 count++;
				 }
			 }
		}
		
		return totalWeight;
	}
	
	/**
	 * Calculate the total reward of all items in a job
	 */
	private float calculateTotalReward(ArrayList<ItemQuantity> _iqs) {
		float totalReward = 0;
		
		for (int i = 0; i < _iqs.size(); i++) {
			 char name = _iqs.get(i).getName();
			 boolean itemFound = false;
			 int count = 0;
			 
			 while (!itemFound) {
				 if (itemList.get(count).getName() == name) {
					 int quantity = _iqs.get(i).getQuantity();
					 float reward = itemList.get(count).getReward() * quantity;
					 totalReward += reward;
					 itemFound = true;
				 } else {
					 count++;
				 }
			 }
		}
		
		return totalReward;
	}
}
