package warehouse.pc.gui;

import java.util.Arrays;
import java.util.Comparator;

import javax.swing.JComboBox;
import javax.swing.JOptionPane;

import lejos.pc.comm.NXTComm;
import lejos.pc.comm.NXTCommException;
import lejos.pc.comm.NXTCommFactory;
import lejos.pc.comm.NXTInfo;
import warehouse.pc.shared.MainInterface;

@SuppressWarnings("serial")
public class BluetoothSelector extends JComboBox<String> implements Runnable {
	private final String SEARCHING = "Searching...";
	private final String NO_ROBOTS_DETECTED = "No robots detected.";
	
	private volatile boolean openingConnection;
	
	private NXTInfo[] oldInfos;
	private NXTInfo[] infos;
	private String errorMessage;
	private boolean error;
	private boolean running;
	
	public BluetoothSelector() {
		super();
		
		openingConnection = false;
		running = true;
		errorMessage = "";
		error = false;
		oldInfos = new NXTInfo[0];
		infos = new NXTInfo[0];
		
		//setMaximumSize(new Dimension(100, 10));
		this.addItem(SEARCHING);
		
		Thread t = new Thread(this);
		t.setDaemon(true);
		t.start();
	}
	
	/**
	 * Returns the currently selected robot, or null if no robot is eelected.
	 */
	public NXTInfo getSelectedRobot() {
		int i = this.getSelectedIndex();
		if (i == -1 || infos.length == 0 || i >= infos.length)
			return null;
		
		return infos[i];
	}
	
	/**
	 * Connects to the currently selected robot
	 */
	public void connect() {
		synchronized (this) {
			openingConnection = true;
			int i = this.getSelectedIndex();
			if (i == -1 || infos.length == 0 || i >= infos.length)
				return;
			
			NXTInfo info = infos[i];
			
			// Call open() in communication module to connect to a new robot.
			Thread t = new Thread(() -> {
				boolean result = MainInterface.get().getServer().open(info);
				openingConnection = false;
				if (!result) {
					JOptionPane.showMessageDialog(null,
						"Could not connect to " + info.name + " (" + info.deviceAddress + ").",
						"Connection Error",
						JOptionPane.WARNING_MESSAGE);
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
			if (infos.length != 0 && oldInfos.length == infos.length) {
				boolean equal = true;
				for (int i = 0; i < infos.length; i++) {
					if (!infos[i].name.equals(oldInfos[i].name)) {
						equal = false;
						break;
					}
				}
				if (equal)
					return;
			}
			
			Arrays.sort(infos, new Comparator<NXTInfo>() {
				@Override
				public int compare(NXTInfo o1, NXTInfo o2) {
					return o1.name.compareToIgnoreCase(o2.name);
				}
			});
			
			// Calculate new selection
			String selectedName = null;
			if (this.getSelectedIndex() != -1) {
				selectedName = (String) this.getSelectedItem();
			}
			
			// Add to combo box
			this.removeAllItems();
			for (NXTInfo i : infos)
				this.addItem(i.name);
			
			if (selectedName != null) {
				for (int i = 0; i < infos.length; i++) {
					if (selectedName.equals(infos[i].name)) {
						this.setSelectedIndex(i);
						break;
					}
				}
			}
			
			if (error) {
				this.addItem("Error: " + errorMessage);
			} else if (infos.length == 0) {
				this.addItem(NO_ROBOTS_DETECTED);
			}
			
			repaint();
			
			oldInfos = infos;
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
				System.out.println("Finished searching for robots!");
				
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
