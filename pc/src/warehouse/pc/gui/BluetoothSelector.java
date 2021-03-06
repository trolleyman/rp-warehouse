package warehouse.pc.gui;

import java.awt.Point;
import java.io.BufferedReader;
import java.io.FileReader;
import java.io.IOException;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.Comparator;
import java.util.Optional;

import javax.swing.JComboBox;
import javax.swing.JOptionPane;

import lejos.pc.comm.NXTComm;
import lejos.pc.comm.NXTCommException;
import lejos.pc.comm.NXTCommFactory;
import lejos.pc.comm.NXTInfo;
import warehouse.pc.shared.MainInterface;
import warehouse.pc.shared.Robot;

@SuppressWarnings("serial")
public class BluetoothSelector extends JComboBox<String> implements Runnable {
	private static final String SEARCHING = "Searching...";
	private static final String NO_ROBOTS_DETECTED = "No robots detected.";
	private static final Comparator<NXTInfo> infoComparator = new Comparator<NXTInfo>() {
		@Override
		public int compare(NXTInfo o1, NXTInfo o2) {
			int res = o1.name.compareToIgnoreCase(o2.name);
			if (res == 0) {
				return o1.deviceAddress.compareToIgnoreCase(o2.deviceAddress);
			} else {
				return res;
			}
		}
	};
	
	private Gui gui;
	
	private NXTInfo[] infos;
	private ArrayList<NXTInfo> defaultInfos;
	
	private volatile boolean running;
	
	private boolean error;
	private String errorMessage;
	
	private volatile boolean openingConnection;
	
	private static ArrayList<NXTInfo> readDefaultRobots() {
		BufferedReader br = null;
		try {
			br = new BufferedReader(new FileReader("robots.csv"));
			
			ArrayList<NXTInfo> robots = new ArrayList<>();
			
			String line = br.readLine();
			while (line != null) {
				String[] split = line.split(",");
				if (split.length != 2) {
					continue;
				}
				String name = split[0];
				String address = split[1];
				robots.add(new NXTInfo(NXTCommFactory.BLUETOOTH, name, address));
				
				line = br.readLine();
			}
			
			robots.sort(infoComparator);
			return robots;
		} catch (IOException e) {
			return new ArrayList<>();
		} finally {
			try {
				if (br != null)
					br.close();
			} catch (IOException e) {
				
			}
		}
	}
	
	public BluetoothSelector(Gui _gui) {
		super();
		gui = _gui;
		
		defaultInfos = readDefaultRobots();
		
		openingConnection = false;
		running = true;
		errorMessage = "";
		error = false;
		infos = new NXTInfo[0];
		
		//setMaximumSize(new Dimension(100, 10));
		if (defaultInfos.size() == 0) {
			this.addItem(SEARCHING);
		} else {
			for (NXTInfo info : defaultInfos) {
				this.addItem(info.deviceAddress + " - " + info.name);
			}
		}
		
		Thread t = new Thread(this);
		t.setDaemon(true);
		t.start();
	}
	
	/**
	 * Returns false if there was an error opening Bluetooth so bad that there is no point
	 * in trying anymore. (i.e. if there was no bluetooth installed.)
	 */
	public boolean isRunning() {
		return running;
	}
	
	/**
	 * Returns the currently selected robot, or null if no robot is eelected.
	 */
	public NXTInfo getSelectedRobot() {
		if (!running)
			return null;
		
		String name = this.getSelectedItem().toString().split("-")[1].trim();
		// Check found items first.
		for (int i = 0; i < infos.length; i++) {
			if (infos[i].name.equals(name)) {
				return infos[i];
			}
		}
		// Then check defaults
		for (NXTInfo info : defaultInfos) {
			if (info.name.equals(name)) {
				return info;
			}
		}
		return null;
	}
	
	/**
	 * Connects to the currently selected robot
	 */
	public void connect() {
		synchronized (this) {
			openingConnection = true;
			NXTInfo info = getSelectedRobot();
			if (info == null)
				return;
			
			// Call open() in communication module to connect to a new robot.
			Thread t = new Thread(() -> {
				Optional<Point> op = StartingLocation.getFromUser(MainInterface.get().getMap());
				if (!op.isPresent()) {
					return;
				}
				
				boolean result = MainInterface.get().getServer().open(info);
				openingConnection = false;
				if (!result) {
					JOptionPane.showMessageDialog(null,
						"Could not connect to " + info.name + " (" + info.deviceAddress + ").",
						"Connection Error",
						JOptionPane.WARNING_MESSAGE);
				} else {
					Point p = op.get();
					MainInterface.get().updateRobot(new Robot(info.name, info.deviceAddress, p.getX(), p.getY(), 0));
				}
			});
			t.start();
		}
	}
	
	/**
	 * Updates the options list with the new robots.
	 */
	private void updateOptions() {
		synchronized (this) {
			if (openingConnection && infos.length == 0) {
				// This is to preotect against a case where whenever a connection is being opened on one thread
				// comm.search(null) is interrupted and returns a zero-length array on the other.
				return;
			}
			// If infos hasn't changed, return.
			ArrayList<NXTInfo> combined = new ArrayList<>();
			combined.addAll(defaultInfos);
			for (int i = 0; i < infos.length; i++) {
				boolean defaultContains = false;
				for (NXTInfo info : defaultInfos) {
					if (info.name.equals(infos[i].name) && info.deviceAddress.equalsIgnoreCase(infos[i].deviceAddress)) {
						defaultContains = true;
					}
				}
				if (!defaultContains) {
					combined.add(infos[i]);
				}
			}
			
			combined.sort(infoComparator);
			
			String selectedRobotName = this.getSelectedRobot().name;
			
			// Add to combo box
			int selectedIndex = 0;
			this.removeAllItems();
			for (int i = 0; i < combined.size(); i++) {
				this.addItem(combined.get(i).deviceAddress + " - " + combined.get(i).name);
				if (combined.get(i).name.equals(selectedRobotName)) {
					selectedIndex = i;
				}
			}
			
			// Keep selection across updates
			this.setSelectedIndex(selectedIndex);
			
			if (error) {
				this.removeAllItems();
				this.addItem("Error: " + errorMessage);
				this.setSelectedIndex(0);
			} else if (combined.size() == 0) {
				this.addItem(NO_ROBOTS_DETECTED);
			}
			
			repaint();
		}
	}
	
	/**
	 * Continually search for robots, then update the options list with the new robots.
	 */
	@Override
	public void run() {
		while (running) {
			int protocol = NXTCommFactory.BLUETOOTH;
			error = false;
			NXTComm comm;
			try {
				comm = NXTCommFactory.createNXTComm(protocol);
				
				System.out.println("Searching for robots...");
				infos = comm.search(null);
				Arrays.sort(infos, (NXTInfo i1, NXTInfo i2) -> {
					return i1.name.compareTo(i2.name);
				});
				if (infos.length == 0) {
					System.out.println("Finished searching for robots.");
				} else {
					System.out.print("Finished searching for robots; found ");
					for (int i = 0; i < infos.length; i++) {
						System.out.print(infos[i].name);
						if (i == infos.length - 2)
							System.out.print(" and ");
						else if (i != infos.length - 1)
							System.out.print(", ");
					}
					System.out.println(".");
				}
				
				updateOptions();
			} catch (NXTCommException e) {
				infos = new NXTInfo[0];
				System.err.println("Error searching for robots: " + e.getMessage());
				errorMessage = e.getMessage();
				error = true;
				updateOptions();
				if (errorMessage.equals("Bluetooth stack not detected")) {
					running = false;
					gui.update();
				}
			}
			
			try {
				Thread.sleep(2000);
			} catch (InterruptedException e) {
				
			}
		}
		
		gui.update();
	}
}
