package warehouse.pc.job;

import java.util.ArrayList;
import java.io.BufferedReader;
import java.io.FileReader;
import java.io.FileNotFoundException;
import java.io.IOException;

/**
 * A list of jobs to be selected by the robot.
 */
public class JobList implements FileList {

	private ArrayList<Job> jobList;
	private ArrayList<Item> itemList;
	
	public JobList(String _fileLocation, ItemList items) {
		this.itemList = items.getList();
		parseFile(_fileLocation);
	}
	
	@Override
	public ArrayList<Job> getList() {
		return this.jobList;
	}
	
	/**
	 * Takes a csv file and reads it into an ArrayList of jobs.
	 * @throws ItemNotInListException
	 */
	private void parseFile(String _fileLocation) {
		//Initialise variables.
		BufferedReader br = null;
		FileReader fr;
		String line;
		String[] splitLine;
		ArrayList<ItemQuantity> iqs;
		jobList = new ArrayList<Job>();
		
		try {
			//Start file readers.
			fr = new FileReader(_fileLocation);
			br = new BufferedReader(fr);
			line = br.readLine();
			
			//Create a job object from each line and add to array.
			while (line != null) {
				//split line into array.
				splitLine = line.split(",");
				iqs = new ArrayList<ItemQuantity>();
				
				//For each pair of item and quantity, create new ItemQuantity.
				for(int i = 1; i < splitLine.length; i += 2) {
					String itemName = splitLine[i];
					int quantity = Integer.valueOf(splitLine[i + 1]);
					
					//Find item from name in itemList.
					boolean itemFound = false;
					int count = 0;
					Item item = null;
					try {
						while (!itemFound) {
							if (itemList.get(count).getName().equals(itemName)) {
								item = itemList.get(count);
								itemFound = true;
							} else {
								count++;
							}
							
							if (count > itemList.size()) {
								throw new ItemNotInListException("Item not found in itemList.");
							}
						}
					} catch (ItemNotInListException e) {
						System.err.println(e);
					}
					
					ItemQuantity iq = new ItemQuantity(item, quantity);
					iqs.add(iq);
				}
				
				//Create job and add to list.
				Job job = new Job(Integer.valueOf(splitLine[0]), iqs, calculateTotalWeight(iqs), calculateTotalReward(iqs));
				jobList.add(job);
				
				line = br.readLine();
			}
		} catch (FileNotFoundException e) {
			System.err.println("Job file not found: " + e);
			System.exit(1);
		} catch (IOException e) {
			System.err.println("Problem reading job file: " + e);
			System.exit(1);
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
		
		//For each ItemQuantity in a job, add its weight to the total.
		for (int i = 0; i < _iqs.size(); i++) {
			 String name = _iqs.get(i).getItem().getName();
			 boolean itemFound = false;
			 int count = 0;
			 
			 //Search the item list to find the item.
			 while (!itemFound) {
				 if (itemList.get(count).getName().equals(name)) {
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
		
		//For each ItemQuantity in a job, add its reward to the total.
		for (int i = 0; i < _iqs.size(); i++) {
			 String name = _iqs.get(i).getItem().getName();
			 boolean itemFound = false;
			 int count = 0;
			 
			//Search the item list to find the item.
			 while (!itemFound) {
				 if (itemList.get(count).getName().equals(name)) {
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
