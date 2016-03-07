/**
 * Display interface
 */
public interface DisplayInterface {

	public void show();						// shows the whole interface on the robot
	public void pickUp(); 					// draws the pick up phase
	public void dropOff(); 					// draws the drop off phase
	public void setDropOff(boolean value);  // in drop off phase?
	public void setPickUp(boolean value);	// in pick up phase?
	public void setJobName(String value);	// sets the job name
	public void setQuantity(int value);		// sets quantity of the items to be picked
	public void setWeight(int value);		// sets the weight per item
}
