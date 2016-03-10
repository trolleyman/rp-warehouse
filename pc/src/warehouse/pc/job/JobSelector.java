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
	
	//Currently: "locations.csv", "items.csv", "jobs.csv", "drops.csv"
	public JobSelector(String locationsLocation, String itemsLocation, String jobsLocation, String dropsLocation, Map map){
		
		LocationList locList = new LocationList(locationsLocation);
		ItemList itemList = new ItemList(itemsLocation, locList);
		JobList jobList = new JobList(jobsLocation, itemList);
		DropList dropList = new DropList(dropsLocation);
		dropLocations = dropList.getList();
		//dropLocations = new ArrayList<Junction>();
		//dropLocations.add(new Junction(3,3));
		tsp = new TSPDistance(map, dropLocations);
		
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
		for(int i = 0; i < jobs.size(); i++){
			if(jobs.get(i).getTotalWeight() <= freeWeight){
				available.add(jobs.get(i));
			}
		}
		if(available.isEmpty()) return Optional.empty(); //Return some kind of error if all Jobs will lead to robot being overloaded
		else{
			Job next = available.get(0);
			//The Job with the highest reward per step ratio will be selected from the remaining list
			for(int j = 1; j < available.size(); j++){
				if((available.get(j).getTotalReward()/tsp.getDistance(available.get(j), x, y) > next.getTotalReward()/tsp.getDistance(next, x, y))){
					next = available.get(j);
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
		System.out.println(js.getJob(5, 3, 100));
		long endTime = System.nanoTime();
		long duration = (endTime - startTime);
		System.out.println(duration/1000000 + "ms");
		
		long twostartTime = System.nanoTime();
		System.out.println(js.getJob(0, 0, 5));
		long twoendTime = System.nanoTime();
		long twoduration = (twoendTime - twostartTime);
		System.out.println(twoduration/1000000 + "ms");
	}
}
