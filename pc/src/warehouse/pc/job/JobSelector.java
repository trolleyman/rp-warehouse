package warehouse.pc.job;

import java.util.List;
import java.util.ArrayList;
import java.util.Optional;

import warehouse.pc.search.RouteFinder;
import warehouse.pc.shared.Direction;
import warehouse.pc.shared.Junction;
import warehouse.pc.shared.Map;

/**
 * Creates a queue of Jobs to be assigned
 */
public class JobSelector {
	
	private ArrayList<Job> jobs;
	private RouteFinder routeFinder;
	private ArrayList<Junction> dropLocations;
	
	//Currently: "locations.csv", "items.csv", "jobs.csv", "drops.csv"
	public JobSelector(String locationsLocation, String itemsLocation, String jobsLocation, String dropsLocation, Map map){
		
		LocationList locList = new LocationList(locationsLocation);
		ItemList itemList = new ItemList(itemsLocation, locList);
		JobList jobList = new JobList(jobsLocation, itemList);
		DropList dropList = new DropList(dropsLocation);
		dropLocations = dropList.getList();
		routeFinder = new RouteFinder(map);
		
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
		
//		for(int i = 0; i < jobs.size(); i++){
//			if(jobs.get(i).getTotalWeight() <= freeWeight){
//				Job next = jobs.get(i);
//				jobs.remove(i);
//				return Optional.of(next);
//			}
//		}
//		return Optional.empty();
		
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
				if((available.get(j).getTotalReward()/Distance(available.get(j), x, y) > next.getTotalReward()/Distance(next, x, y))){
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
	 * Helper method for the Job Selector that uses A* to find the minimum number of steps required by the robot to complete a certain Job
	 * @param job The Job used in calculations
	 * @param x The starting x-coordinate of the robot
	 * @param y The starting y-coordinate of the robot
	 * @return The minimum number of steps required to finish the Job
	 */
	private int Distance(Job job, int x, int y){
		//Retrieves the list of items from the Job
		ArrayList<ItemQuantity> items = job.getItems();
		
		int best = 0;
		int total = 0;
		
		int[] numbers = new int[items.size()];
		for(int n = 0; n < items.size(); n++) numbers[n] = n;
		//Finds all possible orderings in which the items can be picked up
		List<List<Integer>> permutations = permute(numbers);
		
		//Finds the number of steps required for every item ordering
		for(int i = 0; i < permutations.size(); i++){
			//Uses A* to find the distance (number of steps) between two nodes on the map
			//Distance from start position to first item:
			total = routeFinder.findRoute(new Junction(x, y), items.get(permutations.get(i).get(0)).getItem().getJunction(), Direction.X_POS).size();
			for(int j = 1; j < items.size(); j++){
				//Distance between every item after the first:
				total += routeFinder.findRoute(items.get(permutations.get(i).get(j-1)).getItem().getJunction(), items.get(permutations.get(i).get(j)).getItem().getJunction(), Direction.X_POS).size();	 
			}
			//Distance between final item and the closest drop off point
			int dropdistance = routeFinder.findRoute(items.get(permutations.get(i).get(items.size() - 1)).getItem().getJunction(), dropLocations.get(0) , Direction.X_POS).size();
			for(int k = 1; k < dropLocations.size(); k++){
				if (routeFinder.findRoute(items.get(permutations.get(i).get(items.size() - 1)).getItem().getJunction(), dropLocations.get(k), Direction.X_POS).size() < dropdistance){
					dropdistance = routeFinder.findRoute(items.get(permutations.get(i).get(items.size() - 1)).getItem().getJunction(), dropLocations.get(k), Direction.X_POS).size();
				}
			}
			//Total distance of current permutation
			total += dropdistance;
			//If the current permutation is more efficient than the best one calculated so far, then the current one is set to be the best
			if (total < best) best = total;
		}
		//Returns the minimal distance (number of steps) needed to complete the Job
		return best;
		
	}
	
	/**
	 * Helper method to the above helper method
	 * @param numbers An array of numbers to be ordered
	 * @return A list of all possible permutations of the numbers in the array
	 */
	private List<List<Integer>> permute(int[] numbers) {
	    List<List<Integer>> permutations = new ArrayList<List<Integer>>();
	    permutations.add(new ArrayList<Integer>());

	    for (int i = 0; i < numbers.length; i++) {
	        List<List<Integer>> current = new ArrayList<List<Integer>>();
	        for (List<Integer> permutation : permutations) {
	            for (int j = 0, n = permutation.size() + 1; j < n; j++) {
	                List<Integer> temp = new ArrayList<Integer>(permutation);
	                temp.add(j, numbers[i]);
	                current.add(temp);
	            }
	        }
	        permutations = new ArrayList<List<Integer>>(current);
	    }

	    return permutations;
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
}
