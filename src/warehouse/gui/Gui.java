package warehouse.gui;

import java.awt.event.WindowEvent;
import java.awt.event.WindowListener;

import javax.swing.JFrame;

import warehouse.shared.Robot;
import warehouse.shared.RobotListener;
import warehouse.shared.Server;

public class Gui implements Runnable, RobotListener {
	public static void main(String[] args) {
		Gui g = new Gui();
		g.run();
	}
	
	private JFrame frame;
	
	public Gui() {
		Server s = Server.get();
		s.addRobotListener(this);
		frame = new JFrame("Warehouse Viewer");
		frame.setDefaultCloseOperation(JFrame.DISPOSE_ON_CLOSE);
		frame.addWindowListener(new WindowListener() {
			@Override public void windowOpened(WindowEvent _e) {}
			@Override public void windowIconified(WindowEvent _e) {}
			@Override public void windowDeiconified(WindowEvent _e) {}
			@Override public void windowDeactivated(WindowEvent _e) {}
			@Override public void windowActivated(WindowEvent _e) {}
			@Override public void windowClosed(WindowEvent _e) {}
			@Override
			public void windowClosing(WindowEvent _e) {
				Server s = Server.get();
				s.close();
			}
		});
		
		frame.add(new MapComponent());
		frame.setSize(450, 500);
	}
	
	public void update() {
		frame.repaint();
	}
	@Override
	public void robotChanged(Robot _r) {
		update();
	}

	@Override
	public void run() {
		frame.setVisible(true);
	}
}
