package warehouse.pc.job;

import static org.junit.Assert.assertTrue;

import org.junit.After;
import org.junit.Before;
import org.junit.Test;

import warehouse.pc.shared.Junction;

public class InputTest {
	
	private LocationList locList;
	private DropList dropList;
	private ItemList itemList;
	private JobList jobList;
	
	@Before
	public void setUp() throws Exception {
		//Using the test files in the repo as of 06/03/16
		locList = new LocationList("locations.csv");
		dropList = new DropList("drops.csv");
		itemList = new ItemList("items.csv", locList);
		jobList = new JobList("jobs.csv", itemList);
	}

	@After
	public void tearDown() throws Exception {
		locList = null;
		dropList = null;
		itemList = null;
		jobList = null;
	}

	@Test
	public void test() {
		//Tests here are done by checking the first and last items in list are same as in file.
		
		//Test DropList
		Junction dropFirst = dropList.getList().get(0);
		Junction dropLast = dropList.getList().get(dropList.getList().size() - 1);
		assertTrue(dropFirst.getX() == 4 
				&& dropFirst.getY() == 7);
		assertTrue(dropLast.getX() == 7 
				&& dropLast.getY() == 7);
		
		//Test LocationList
		Location locFirst = locList.getList().get(0);
		Location locLast = locList.getList().get(locList.getList().size() - 1);
		assertTrue(locFirst.getX() == 5 
				&& locFirst.getY() == 3 
				&& locFirst.getItemName() == "aa");
		assertTrue(locLast.getX() == 2 
				&& locLast.getY() == 5 
				&& locLast.getItemName() == "cj");
		
		//Test ItemList
		Item itemFirst = itemList.getList().get(0);
		Item itemLast = itemList.getList().get(itemList.getList().size() - 1);
		assertTrue(itemFirst.getName() == "aa" 
				&& itemFirst.getReward() == 12.78 
				&& itemFirst.getWeight() == 0.36);
		assertTrue(itemLast.getName() == "cj" 
				&& itemLast.getReward() == 14.08 
				&& itemLast.getWeight() == 1.0);
		
		//Test JobList
		Job jobFirst = jobList.getList().get(0);
		Job jobLast = jobList.getList().get(jobList.getList().size() - 1);
		assertTrue(jobFirst.getId() == 10000 
				&& jobFirst.getItems().get(0).getItem().getName() == "ba"
				&& jobFirst.getItems().get(0).getQuantity() == 2
				&& jobFirst.getItems().get(1).getItem().getName() == "bi"
				&& jobFirst.getItems().get(1).getQuantity() == 3
				&& jobFirst.getItems().get(2).getItem().getName() == "cb"
				&& jobFirst.getItems().get(2).getQuantity() == 3);
		assertTrue(jobLast.getId() == 29999 
				&& jobLast.getItems().get(0).getItem().getName() == "ce"
				&& jobLast.getItems().get(0).getQuantity() == 1
				&& jobLast.getItems().get(1).getItem().getName() == "ab"
				&& jobLast.getItems().get(1).getQuantity() == 2
				&& jobLast.getItems().get(2).getItem().getName() == "af"
				&& jobLast.getItems().get(2).getQuantity() == 4
				&& jobLast.getItems().get(3).getItem().getName() == "ac"
				&& jobLast.getItems().get(3).getQuantity() == 1
				&& jobLast.getItems().get(4).getItem().getName() == "ad"
				&& jobLast.getItems().get(4).getQuantity() == 1
				&& jobLast.getItems().get(5).getItem().getName() == "ah"
				&& jobLast.getItems().get(5).getQuantity() == 1);
	}

}
