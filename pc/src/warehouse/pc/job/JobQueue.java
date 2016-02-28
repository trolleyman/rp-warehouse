package warehouse.pc.job;

import java.util.ArrayList;

/**
 * Creates a queue of Jobs to be assigned
 */
public class JobQueue {
	
	private ArrayList<Job> jobs;
	
	public JobQueue(){
		String locationsLocation = "locations.csv";
		String itemsLocation = "items.csv";
		String jobsLocation = "jobs.csv";
		String dropsLocation = "drops.csv";
		
		LocationList locList = new LocationList(locationsLocation);
		ItemList itemList = new ItemList(itemsLocation, locList);
		JobList jobList = new JobList(jobsLocation, itemList);
		Drop.setDropPoint(dropsLocation);
		
		jobs = jobList.getList();
		//jobs.sort(new RewardComparator()); //This sorts the jobs into reward order
	}
		
	
	public Job getJob(int x, int y, float freeWeight){
		//Returns first job and removes it from the queue
		/*
		Job next = jobs.get(0);
		jobs.remove(0);
		return next; 
		*/
		
		
		//Returns the first job that is supported by the robots free weight and removes it from the queue
		/*
		for(int i = 0; i < jobs.size(); i++){
			if(jobs.get(i).getTotalWeight() <= freeWeight){
				Job next = jobs.get(i);
				jobs.remove(i);
				return next;
			}
		}
		//Return some kind of error if all jobs will lead to robot being overloaded
		*/
		
		
		//Returns the job that is closest to the robot and removes it from the queue
		
		
		return null;
	}
	
	public void removeJob(int n){
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
		//Test to see if the list is ordered by reward
		JobQueue q = new JobQueue();
		for(int i=0; i<q.getList().size(); i++){
			System.out.println(q.getList().get(i).getTotalReward());
		}
		
	}
}
