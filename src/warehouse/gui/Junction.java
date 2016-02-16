package warehouse.gui;

import java.util.HashMap;

import warehouse.shared.Direction;

public class Junction {
	private int x;
	private int y;
	public HashMap<Direction, Junction> js;
	
	public Junction(int x, int y) {
		this.x = x;
		this.y = y;
		js = new HashMap<>();
	}
	
	public int getX() {
		return x;
	}
	public int getY() {
		return y;
	}
	
	public Junction getJunction(Direction d) {
		return js.get(d);
	}
	public void setJunction(Direction d, Junction j) {
		js.put(d, j);
	}
}
