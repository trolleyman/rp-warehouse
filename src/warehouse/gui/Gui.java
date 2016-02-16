package warehouse.gui;

import javax.swing.JFrame;

import warehouse.shared.Robot;

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
		frame.add(new MapComponent(m, new Robot[] {
			new Robot("Jeff" , 0, 0, 0.0),
			new Robot("Nigel", 3, 0, 90.0),
			new Robot("Dave" , 0, 3, 180.0 + 45.0),
			new Robot("Other", 4, 2, 270.0 + 45.0),
		}));
		frame.setSize(450, 500);
		frame.setVisible(true);
	}
	
	public void update() {
		frame.repaint();
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
