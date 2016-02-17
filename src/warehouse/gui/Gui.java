package warehouse.gui;

import java.awt.BorderLayout;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.awt.event.WindowEvent;
import java.awt.event.WindowListener;

import javax.swing.JComponent;
import javax.swing.JFrame;
import javax.swing.Timer;
import javax.swing.UIManager;
import javax.swing.UnsupportedLookAndFeelException;

import warehouse.job.Job;
import warehouse.shared.JobListener;
import warehouse.shared.Robot;
import warehouse.shared.RobotListener;
import warehouse.shared.Server;
import warehouse.shared.State;

public class Gui implements Runnable, RobotListener, JobListener {
	private static void setNativeLAF() {
		try {
			UIManager.setLookAndFeel(UIManager.getSystemLookAndFeelClassName());
		} catch (ClassNotFoundException | InstantiationException | IllegalAccessException
				| UnsupportedLookAndFeelException e1) {
			try {
				UIManager.setLookAndFeel(UIManager.getCrossPlatformLookAndFeelClassName());
			} catch (ClassNotFoundException | InstantiationException | IllegalAccessException
					| UnsupportedLookAndFeelException e2) {
				// hmm.. Hope things work out.
			}
		}
	}
	
	public static void main(String[] args) {
		setNativeLAF();
		Gui g = new Gui();
		g.run();
		Timer t = new Timer(20, new ActionListener() {
			@Override
			public void actionPerformed(ActionEvent _e) {
				State s = Server.get().getCurrentState();
				Robot[] robots = s.getRobots();
				for (Robot r : robots) {
					r.setFacing(r.getFacing() + 1.0);
					Server.get().updateRobot(r);
					break;
				}
			}
		});
		t.start();
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
				System.exit(0);
			}
		});
		
		JComponent map = new MapComponent();
		JComponent jobs = new JobComponent();
		
		frame.setLayout(new BorderLayout());
		frame.add(map, BorderLayout.CENTER);
		frame.add(jobs, BorderLayout.EAST);
		frame.setSize(450, 500);
	}
	
	@Override
	public void run() {
		frame.setVisible(true);
	}
	
	public void update() {
		frame.repaint();
	}
	
	@Override
	public void robotChanged(Robot _r) {
		update();
	}

	@Override
	public void jobUpdated(Job _job) {
		update();
	}
}
