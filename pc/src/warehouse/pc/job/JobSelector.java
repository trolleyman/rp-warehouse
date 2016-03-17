package warehouse.pc.job;

import java.util.ArrayList;
import java.util.Optional;
import warehouse.pc.shared.Junction;
import warehouse.pc.shared.Map;
import warehouse.pc.shared.TestMaps;

/**
 * Creates a queue of Jobs to be assigned
 */
public class JobSelector {
	
	private ArrayList<Job> jobs;
	private ArrayList<Junction> dropLocations;
	private TSPDistance tsp;
	private Map map;
	
	//Currently: "locations.csv", "items.csv", "jobs.csv", "drops.csv"
	public JobSelector(String locationsLocation, String itemsLocation, String jobsLocation, String dropsLocation, Map map){
		LocationList locList = new LocationList(locationsLocation);
		ItemList itemList = new ItemList(itemsLocation, locList);
		JobList jobList = new JobList(jobsLocation, itemList);
		DropList dropList = new DropList(dropsLocation);
		dropLocations = dropList.getList();
		tsp = new TSPDistance(map, dropLocations);
		this.map = map;
		
		jobs = jobList.getList();
		jobs.sort(new RewardComparator()); //This sorts the jobs into reward order
	}
	
	public JobSelector(LocationList locList, ItemList itemList, JobList jobList, DropList dropList){
		jobs = jobList.getList();
		jobs.sort(new RewardComparator()); //This sorts the jobs into reward order
	}
		
	/**
	 * Returns the job that is supported by the robots free weight and has the highest reward per steps taken ratio and removes it from the queue
	 * @param x The x coordinates of the robot
	 * @param y The y coordinates of the robot
	 * @param freeWeight The amount of weight the robot is able to carry
	 * @return
	 */
	public Optional<Job> getJob(int x, int y, float freeWeight){
		
		//Returns the first job that is supported by the robots free weight and removes it from the queue
		/*
		for(int i = 0; i < jobs.size(); i++){
			if(jobs.get(i).getTotalWeight() <= freeWeight){
				Job next = jobs.get(i);
				jobs.remove(i);
				return Optional.of(next);
			}
		}
		return Optional.empty();
		
		*/

		//Filters out all Jobs that are not supported by the robot's free weight
		ArrayList<Job> available = new ArrayList<Job>();
		tsp = new TSPDistance(this.map, dropLocations);
		for(int i = 0; i < jobs.size(); i++){
			if(jobs.get(i).getTotalWeight() <= freeWeight){
				available.add(jobs.get(i));
			}
		}
		if(available.isEmpty()) return Optional.empty(); //Return some kind of error if all Jobs will lead to robot being overloaded
		else{
			Job next = available.get(0);
			float nextCost = next.getTotalReward()/tsp.getDistance(next, x, y);
			//The Job with the highest reward per step ratio will be selected from the remaining list
			for(int j = 1; j < available.size(); j++){
				float currentCost = (available.get(j).getTotalReward()/tsp.getDistance(available.get(j), x, y));
				if(currentCost > nextCost){
					next = available.get(j);
					nextCost = currentCost;
				}
			}
			//The selected Job is removed from the original list  
			jobs.remove(next);
			//The selected Job is returned
			return Optional.of(next);
		}
	}
	
	/**
	 * Removes a Job from the list of Jobs to be completed
	 * @param n The ID of the Job to be removed
	 */
	public void remove(int n){
		for(int i = 0; i < numberOfJobs(); i++){
			if(jobs.get(i).getId() == n){
				jobs.remove(i);
				break;
			}
		}
	}
	
	/**
	 * @return The number of Jobs available
	 */
	public int numberOfJobs(){
		return jobs.size();
	}
	
	/**
	 * @return The list of available Jobs
	 */
	public ArrayList<Job> getList(){
		return this.jobs;
	}
	
	public static void main(String[] args){
		JobSelector js = new JobSelector("locations.csv", "items.csv", "jobs.csv", "drops.csv", TestMaps.TEST_MAP4);
		
		long startTime = System.nanoTime();
		System.out.println(js.getJob(5, 4, 50));
		long endTime = System.nanoTime();
		long duration = (endTime - startTime);
		System.out.println(duration/1000000 + "ms");
	}
}
