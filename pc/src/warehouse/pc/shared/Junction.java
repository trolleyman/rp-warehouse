package warehouse.pc.shared;

public class Junction {
	private int x;
	private int y;
	public Junction[] js;
	
	public Junction(int _x, int _y) {
		this.x = _x;
		this.y = _y;
		js = new Junction[Direction.values().length];
	}
	
	public int getX() {
		return x;
	}
	public int getY() {
		return y;
	}
	
	public Junction getJunction(Direction _d) {
		return js[_d.ordinal()];
	}
	public void setJunction(Direction _d, Junction _j) {
		js[_d.ordinal()] = _j;
	}
	
	public Junction[] getNeighbours() {
		return js;
	}
	
	@Override
	public boolean equals(Object o) {
		return o instanceof Junction
			&& ((Junction) o).x == x
			&& ((Junction) o).y == y;
	}
	
	@Override
	public int hashCode() {
		// Cantor pairing function
		return ((Integer.hashCode(x) + Integer.hashCode(y)) *
			((Integer.hashCode(x) + Integer.hashCode(y) + 1) / 2)
			+ Integer.hashCode(y));
	}
	
	@Override
	public String toString() {
		return "[" + x + ", " + y + "]";


	}
}
