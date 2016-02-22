package warehouse.pc.gui;

import java.awt.BorderLayout;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.awt.event.WindowEvent;
import java.awt.event.WindowListener;

import javax.swing.BorderFactory;
import javax.swing.Box;
import javax.swing.BoxLayout;
import javax.swing.JButton;
import javax.swing.JFrame;
import javax.swing.JPanel;
import javax.swing.Timer;
import javax.swing.UIManager;
import javax.swing.UnsupportedLookAndFeelException;

import warehouse.pc.shared.MainInterface;
import warehouse.pc.shared.RobotListener;
import warehouse.pc.shared.State;
import warehouse.shared.robot.Robot;

public class Gui implements Runnable, RobotListener {
	public static void main(String[] args) {
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
		
		Gui g = new Gui();
		g.run();
		Timer t = new Timer(20, new ActionListener() {
			@Override
			public void actionPerformed(ActionEvent _e) {
				State s = MainInterface.get().getCurrentState();
				Robot[] robots = s.getRobots();
				for (Robot r : robots) {
					r.setFacing(r.getFacing() + 1.0);
					MainInterface.get().updateRobot(r);
					break;
				}
			}
		});
		t.start();
	}
	
	private JFrame frame;
	
	public Gui() {
		MainInterface i = MainInterface.get();
		i.addRobotListener(this);
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
				MainInterface s = MainInterface.get();
				s.close();
				System.out.println("Quitting...");
				System.exit(0);
			}
		});
		
		
		JPanel panel = new JPanel();
		//panel.setLayout(new BoxLayout(panel, BoxLayout.LINE_AXIS));
		//panel.add(createToolbar());
		//panel.add(Box.createHorizontalStrut(10));
		//panel.add(new MapComponent());
		
		JPanel map = new JPanel();
		map.setLayout(new BorderLayout());
		MapComponent mapComponent = new MapComponent();
		map.add(mapComponent, BorderLayout.CENTER);
		map.setBorder(BorderFactory.createTitledBorder("Map View"));
		panel.setLayout(new BorderLayout());
		panel.add(createToolbar(), BorderLayout.LINE_START);
		panel.add(map, BorderLayout.CENTER);
		panel.setBorder(BorderFactory.createEmptyBorder(10, 10, 10, 10));
		frame.add(panel);
		frame.setSize(450, 500);
	}
	
	private JPanel createToolbar() {
		JPanel res = new JPanel();
		JPanel toolbar = new JPanel();
		JPanel inner = new JPanel();
		toolbar.setBorder(BorderFactory.createTitledBorder("Toolbar"));
		
		inner.setLayout(new BoxLayout(inner, BoxLayout.PAGE_AXIS));
		inner.setBorder(BorderFactory.createEmptyBorder(0, 0, 0, 0));
		JButton connectButton = new JButton("Connect");
		BluetoothSelector select = new BluetoothSelector();
		inner.add(select);
		inner.add(Box.createVerticalStrut(5));
		inner.add(connectButton);
		toolbar.add(inner);
		res.add(toolbar);
		res.add(Box.createVerticalGlue());
		return res;
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
