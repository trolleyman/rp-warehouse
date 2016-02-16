package warehouse.gui;

import javax.swing.JFrame;

import warehouse.shared.Direction;
import warehouse.shared.Robot;

public class Gui implements Runnable {
	public static void main(String[] args) {
		Gui g = new Gui(TestMaps.TEST_MAP2);
		
		g.run();
	}
	
	//private Map m;
	private JFrame frame;
	
	public Gui(Map m) {
		//this.m = m;
		
		frame = new JFrame("Warehouse Viewer");
		frame.setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);
		frame.add(new MapComponent(m, new Robot[] {
			new Robot("Jeff", 0, 0, Direction.YPos),
			new Robot("Nigel", 0, 3, Direction.XPos),
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
