package warehouse.pc.job;

/**
 * The grid location of the drop-off point for jobs.
 */
public class Drop {

	//Having public attributes like this is bad practise,
	//But I can't think of a good way around it yet.
	private final int x;
	private final int y;
	
	public Drop(int _x, int _y) {
		this.x = _x;
		this.y = _y;
	}
	
	public int getX() {
		return this.x;
	}
	
	public int getY() {
		return this.y;
	}
	
	public String toString() {
		return x + ", " + y;
	}
}