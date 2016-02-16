package warehouse.gui;

import javax.swing.JFrame;

public class Gui implements Runnable {
	public static void main(String[] args) {
		Gui g = new Gui(TestMaps.TEST_MAP1);
		
		g.run();
	}
	
	//private Map m;
	private JFrame frame;
	
	public Gui(Map m) {
		//this.m = m;
		
		frame = new JFrame("Warehouse Viewer");
		frame.setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);
		frame.add(new MapComponent(m));
		frame.setSize(450, 500);
		frame.setVisible(true);
	}

	@Override
	public void run() {
		while (true) {
			try {
				Thread.sleep(50);
			} catch (InterruptedException e) {
				
			}
		}
	}
}
