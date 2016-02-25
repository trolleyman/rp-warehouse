package warehouse.pc.job;

/**
 * Code just for local testing.
 */
public class JobListTest {
	
	public static void main(String[] args) {
		String locationsLocation = "locations.csv";
		String itemsLocation = "items.csv";
		String jobsLocation = "jobs.csv";
		String dropsLocation = "drops.csv";
		
		LocationList locList = new LocationList(locationsLocation);
		ItemList itemList = new ItemList(itemsLocation, locList);
		JobList jobList = new JobList(jobsLocation, itemList);
		Drop.setDropPoint(dropsLocation);
		
		System.out.println("Drop: " + Drop.xPos + ", " + Drop.yPos);
		System.out.println();
		
		for (int i = 0; i < jobList.getList().size(); i++) {
			System.out.println(jobList.getList().get(i).toString());
		}
	}
}
