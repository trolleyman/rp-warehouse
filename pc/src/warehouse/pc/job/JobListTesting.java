package warehouse.pc.job;

/**
 * Code just for local testing.
 */
public class JobListTesting {
	
	public static void main(String[] args) {
		//Set file locations
		String locationsLocation = "locations.csv";
		String itemsLocation = "items.csv";
		String jobsLocation = "jobs.csv";
		String dropsLocation = "drops.csv";
		
		//Create lists
		LocationList locList = new LocationList(locationsLocation);
		ItemList itemList = new ItemList(itemsLocation, locList);
		JobList jobList = new JobList(jobsLocation, itemList);
		DropList dropList = new DropList(dropsLocation);
		
		//Print drop locations
		for (int i = 0; i < dropList.getList().size(); i++) {
			System.out.println("Drop: " + dropList.getList().get(i).getX() + ", " + dropList.getList().get(i).getY());
		}
			
		System.out.println();
		
		//Print job list
		for (int i = 0; i < jobList.getList().size(); i++) {
			System.out.println(jobList.getList().get(i).toString());
		}
	}
}
