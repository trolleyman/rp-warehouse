package warehouse.gui;

import javax.swing.JFrame;

public class Gui implements Runnable {
	public static void main(String[] args) {
		Gui g = new Gui(TestStates.TEST_STATE1);
		
		g.run();
	}
	
	//private Map m;
	private JFrame frame;
	
	public Gui(State state) {
		//this.m = m;
		
		frame = new JFrame("Warehouse Viewer");
		frame.setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);
		frame.add(new MapComponent(state));
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
