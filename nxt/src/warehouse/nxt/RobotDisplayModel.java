package warehouse.nxt;

public class RobotDisplayModel {
	private RobotDisplay display;
	
	public RobotDisplayModel(RobotDisplay display) {
		this.display = display;
	}
	
	public void setDropOff(boolean value) {
		display.setDropOff(value);
	}
	
	public void setPickUp(boolean value) {
		display.setPickUp(value);
	}
	
	public void setQuantity(int value) {
		display.setQuantity(value);
	}
	
	public void setJobName(String value) {
		display.setJobName(value);
	}
	
	public void setWeight(int value) {
		display.setWeight(value);
	}
}