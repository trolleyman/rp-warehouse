package warehouse.pc.job;

import java.util.ArrayList;
import java.util.Optional;

/**
 * Creates a queue of Jobs to be assigned
 */
public class JobSelector {
	
	private ArrayList<Job> jobs;
	
	public JobSelector(String locationsLocation, String itemsLocation, String jobsLocation, String dropsLocation){
		LocationList locList = new LocationList(locationsLocation);
		ItemList itemList = new ItemList(itemsLocation, locList);
		JobList jobList = new JobList(jobsLocation, itemList);
		@SuppressWarnings("unused")
		DropList dropList = new DropList(dropsLocation);
		
		jobs = jobList.getList();
		jobs.sort(new RewardComparator()); //This sorts the jobs into reward order
	}
	
	public JobSelector(LocationList locList, ItemList itemList, JobList jobList, DropList dropList){
		jobs = jobList.getList();
		jobs.sort(new RewardComparator()); //This sorts the jobs into reward order
	}
		
	/**
	 * Returns the first job that is supported by the robots free weight and removes it from the queue
	 * @param x The x coordinates of the robot
	 * @param y The y coordinates of the robot
	 * @param freeWeight The amount of weight the robot is able to carry
	 * @return
	 */
	public Optional<Job> getJob(int x, int y, float freeWeight){
		
		//Returns the first job that is supported by the robots free weight and removes it from the queue
		
		for(int i = 0; i < jobs.size(); i++){
			if(jobs.get(i).getTotalWeight() <= freeWeight){
				Job next = jobs.get(i);
				jobs.remove(i);
				return Optional.of(next);
			}
		}
		return Optional.empty(); //Return some kind of error if all jobs will lead to robot being overloaded
		
		//Returns the job that is closest to the robot and removes it from the queue
		
		
	}
	
	public void remove(int n){
		for(int i = 0; i < numberOfJobs(); i++){
			if(jobs.get(i).getId() == n){
				jobs.remove(i);
				break;
			}
		}
	}
	
	public int numberOfJobs(){
		return jobs.size();
	}
	
	public ArrayList<Job> getList(){
		return this.jobs;
	}
	
	public static void main(String[] args){
		//Testing
		//JobSelector q = new JobSelector("locations.csv", "items.csv", "jobs.csv", "drops.csv");
		//System.out.println(q.getJob(0, 0, 0.2f));
	}
}
