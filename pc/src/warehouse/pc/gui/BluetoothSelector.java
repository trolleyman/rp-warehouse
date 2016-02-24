package warehouse.pc.gui;

import java.util.Arrays;
import java.util.Comparator;

import javax.swing.JComboBox;

import lejos.pc.comm.NXTComm;
import lejos.pc.comm.NXTCommException;
import lejos.pc.comm.NXTCommFactory;
import lejos.pc.comm.NXTInfo;
import warehouse.pc.shared.MainInterface;

@SuppressWarnings("serial")
public class BluetoothSelector extends JComboBox<String> implements Runnable {
	private final String NO_ROBOTS_DETECTED = "No robots detected.";
	
	private NXTInfo[] oldInfos;
	private NXTInfo[] infos;

	public BluetoothSelector() {
		super();
		
		oldInfos = new NXTInfo[] {null};
		infos = new NXTInfo[0];
		
		//setMaximumSize(new Dimension(100, 10));
		this.addItem(NO_ROBOTS_DETECTED);
		
		Thread t = new Thread(this);
		t.setDaemon(true);
		t.start();
	}
	
	public void connect() {
		synchronized (this) {
			int i = this.getSelectedIndex();
			if (i == -1 || infos.length == 0 || i >= infos.length)
				return;
			
			NXTInfo info = infos[i];
			// Call connect() in communication module to connect to a new robot.
			MainInterface.get().getServer().open(info);
		}
	}
	
	private void updateOptions() {
		synchronized (this) {
			// If infos hasn't changed, return.
			if (oldInfos.length == infos.length) {
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
			
			if (infos.length == 0) {
				this.addItem(NO_ROBOTS_DETECTED);
			}
			
			repaint();
			
			oldInfos = infos;
		}
	}

	@Override
	public void run() {
		while (true) {
			int protocol = NXTCommFactory.BLUETOOTH;
			NXTComm comm;
			try {
				comm = NXTCommFactory.createNXTComm(protocol);
				
				infos = comm.search(null);
				
				updateOptions();
			} catch (NXTCommException e1) {
				infos = new NXTInfo[0];
				
				updateOptions();
			}
			
			try {
				Thread.sleep(1000);
			} catch (InterruptedException e) {
				
			}
		}
	}
}
