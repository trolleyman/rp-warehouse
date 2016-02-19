package warehouse.pc.shared;

import java.util.HashMap;

public class Junction {
	private int x;
	private int y;
	public HashMap<Direction, Junction> js;
	
	public Junction(int _x, int _y) {
		this.x = _x;
		this.y = _y;
		js = new HashMap<>();
	}
	
	public int getX() {
		return x;
	}
	public int getY() {
		return y;
	}
	
	public Junction getJunction(Direction _d) {
		return js.get(_d);
	}
	public void setJunction(Direction _d, Junction _j) {
		js.put(_d, _j);
	}
}
