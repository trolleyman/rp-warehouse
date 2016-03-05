package warehouse.pc.bluetooth.testing;

import org.junit.runner.RunWith;
import org.junit.runners.Suite;
import org.junit.runners.Suite.SuiteClasses;

@RunWith(Suite.class)
@SuiteClasses({ MultiConnectionTest.class, SingleConnectionTest.class, SingleSendingTest.class })
public class ConnectionTestSuite {

}
