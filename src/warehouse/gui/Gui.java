package warehouse.gui;

import javax.swing.JFrame;

public class Gui implements Runnable {
	public static void main(String[] args) {
		Gui g = new Gui(null);
		
	}
	
	private Map m;
	private JFrame frame;
	
	public Gui(Map m) {
		this.m = m;
		
		frame = new JFrame("Warehouse Viewer");
		
	}

	@Override
	public void run() {
		
	}
}
