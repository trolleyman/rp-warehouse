package warehouse.pc.search;

import org.junit.runner.RunWith;
import org.junit.runners.Suite;
import org.junit.runners.Suite.SuiteClasses;

@RunWith(Suite.class)
@SuiteClasses({CMultiRouteTests.class, MultipleTests.class, MultiRoutePlannerTests.class, PlanningTests.class, 
	RoutePlanningTest.class, RouteTest.class, SearchTests.class})
public class SearchTestSuite {

}
