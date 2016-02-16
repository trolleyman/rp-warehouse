package warehouse.shared;

public class Robot {
	private String name;
	private int xPos;
	private int yPos;
	private Direction facing;
	
	public Robot(String name, int xPos, int yPos, Direction facing) {
		this.name = name;
		this.xPos = xPos;
		this.yPos = yPos;
		this.facing = facing;
	}
	
	public String getName() {
		return name;
	}

	public int getX() {
		return xPos;
	}

	public void setX(int xPos) {
		this.xPos = xPos;
	}

	public int getY() {
		return yPos;
	}

	public void setY(int yPos) {
		this.yPos = yPos;
	}

	public Direction getFacing() {
		return facing;
	}

	public void setFacing(Direction facing) {
		this.facing = facing;
	}
}
