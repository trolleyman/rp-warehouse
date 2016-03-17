package warehouse.pc.gui;

import java.io.BufferedReader;
import java.io.File;
import java.io.FileReader;
import java.io.IOException;
import java.nio.file.Files;
import java.nio.file.Path;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.Comparator;
import java.util.List;

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
	
	private volatile boolean openingConnection;
	
	private NXTInfo[] infos;
	private String errorMessage;
	private boolean error;
	private boolean running;
	
	private ArrayList<NXTInfo> defaultRobots;
	
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
	
	public BluetoothSelector() {
		super();
		
		defaultRobots = readDefaultRobots();
		
		openingConnection = false;
		running = true;
		errorMessage = "";
		error = false;
		infos = new NXTInfo[0];
		
		//setMaximumSize(new Dimension(100, 10));
		if (defaultRobots.size() == 0) {
			this.addItem(SEARCHING);
		} else {
			for (NXTInfo info : defaultRobots) {
				this.addItem(info.name);
			}
		}
		
		// Debugging stuff
		/*
		NXTInfo info = new NXTInfo(NXTCommFactory.BLUETOOTH, "Dobot", "0016530FD7F4");
		{boolean result = MainInterface.get().getServer().open(info);
		openingConnection = false;
		if (!result) {
			JOptionPane.showMessageDialog(null,
				"Could not connect to " + info.name + " (" + info.deviceAddress + ").",
				"Connection Error",
				JOptionPane.WARNING_MESSAGE);
		} else {
			MainInterface.get().updateRobot(new Robot(info.name, info.deviceAddress, 0, 0, 0));
		}}
		//*/
		
		Thread t = new Thread(this);
		t.setDaemon(true);
		t.start();
	}
	
	/**
	 * Returns the currently selected robot, or null if no robot is eelected.
	 */
	public NXTInfo getSelectedRobot() {
		String name = this.getSelectedItem().toString();
		// Check defaults first.
		for (NXTInfo info : defaultRobots) {
			if (info.name.equals(name)) {
				return info;
			}
		}
		for (int i = 0; i < infos.length; i++) {
			if (infos[i].name.equals(name)) {
				return infos[i];
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
				boolean result = MainInterface.get().getServer().open(info);
				openingConnection = false;
				if (!result) {
					JOptionPane.showMessageDialog(null,
						"Could not connect to " + info.name + " (" + info.deviceAddress + ").",
						"Connection Error",
						JOptionPane.WARNING_MESSAGE);
				} else {
					MainInterface.get().updateRobot(new Robot(info.name, info.deviceAddress, 0, 0, 0));
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
			combined.addAll(defaultRobots);
			for (int i = 0; i < infos.length; i++) {
				if (!defaultRobots.contains(infos[i])) {
					combined.add(infos[i]);
				}
			}
			
			combined.sort(new Comparator<NXTInfo>() {
				@Override
				public int compare(NXTInfo o1, NXTInfo o2) {
					return o1.name.compareToIgnoreCase(o2.name);
				}
			});
			
			// Add to combo box
			this.removeAllItems();
			for (NXTInfo i : combined)
				this.addItem(i.name);
			
			// TODO: Keep same selected item across updates
			this.setSelectedIndex(-1);
			
			if (error) {
				this.addItem("Error: " + errorMessage);
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
				}
			}
			
			try {
				Thread.sleep(2000);
			} catch (InterruptedException e) {
				
			}
		}
	}
}
