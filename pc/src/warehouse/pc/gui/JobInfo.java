package warehouse.pc.gui;

import java.awt.BorderLayout;
import java.awt.Dimension;
import java.awt.Graphics;
import java.util.ArrayDeque;
import java.util.ArrayList;
import java.util.Comparator;

import javax.swing.BorderFactory;
import javax.swing.JPanel;
import javax.swing.JScrollPane;
import javax.swing.JTable;
import javax.swing.event.ListSelectionEvent;
import javax.swing.event.ListSelectionListener;
import javax.swing.table.AbstractTableModel;

import rp.util.Pair;
import warehouse.pc.job.ItemQuantity;
import warehouse.pc.job.Job;
import warehouse.pc.shared.MainInterface;
import warehouse.pc.shared.Robot;
import warehouse.pc.shared.RobotListener;
import warehouse.pc.shared.RobotManager;

@SuppressWarnings("serial")
public class JobInfo extends JPanel implements RobotListener {
	private final MainInterface mi;
	
	private ArrayList<Pair<Robot, Job>> jobs;
	
	private JTable table;
	private AbstractTableModel model;
	
	public JobInfo() {
		super();
		
		mi = MainInterface.get();
		
		model = new AbstractTableModel() {
			@Override
			public int getColumnCount() { return 3; }
			@Override
			public int getRowCount() { return jobs.size(); }
			@Override
			public Object getValueAt(int row, int col) {
				if (row < 0 || row >= jobs.size())
					return "";
				
				Pair<Robot, Job> p = jobs.get(row);
				switch (col) {
				case 0: return p.getItem1().getName();
				case 1: return p.getItem2().getId();
				case 2: {
					ArrayList<ItemQuantity> iqs = p.getItem2().getItems();
					StringBuilder b = new StringBuilder();
					for (ItemQuantity iq : iqs) {
						b.append(iq.getQuantity());
						b.append(" ");
						b.append(iq.getItem().getName());
						b.append(", ");
					}
					if (iqs.size() > 0) {
						// Delete last ", "
						b.deleteCharAt(b.length() - 1);
						b.deleteCharAt(b.length() - 1);
					}
					return b.toString();
				}
				default: return "";
				}
			}
			@Override
			public String getColumnName(int index) {
				switch (index) {
				case 0: return "Robot Name";
				case 1: return "Job ID";
				case 2: return "Items";
				}
				return "";
			}
		};
		table = new JTable(model);
		table.getSelectionModel().addListSelectionListener(new ListSelectionListener() {
			@Override
			public void valueChanged(ListSelectionEvent e) {
				// TODO: Select robot
			}
		});
		
		//JScrollPane scrollpane = new JScrollPane(table, JScrollPane.VERTICAL_SCROLLBAR_ALWAYS, JScrollPane.HORIZONTAL_SCROLLBAR_NEVER);
		
		setLayout(new BorderLayout());
		add(table.getTableHeader(), BorderLayout.NORTH);
		add(table, BorderLayout.CENTER);
		//setPreferredSize(new Dimension(200, (int) getPreferredSize().getHeight()));
		setBorder(BorderFactory.createEmptyBorder(6, 6, 6, 6));
		
		mi.addRobotListener(this);
		
		update();
	}
	
	public void update() {
		// Update jobs TODO: Synchronize
		ArrayList<Pair<Robot, Job>> newJobs = new ArrayList<>();
		
		RobotManager man = mi.getRobotManager();
		
		for (Robot r : mi.getRobots()) {
			ArrayDeque<Job> jobs = man.getJobs(r);
			if (jobs == null)
				continue;
			Job j = jobs.peekFirst();
			newJobs.add(Pair.makePair(r, j));
		}
		
		newJobs.sort(new Comparator<Pair<Robot, Job>>() {
			@Override
			public int compare(Pair<Robot, Job> o1, Pair<Robot, Job> o2) {
				return o1.getItem1().compareTo(o2.getItem1());
			}
		});
		
		jobs = newJobs;
		
		// TODO: Sort out so that selection doesn't dissapear on update.
		
		// Notify the table
		model.fireTableDataChanged();
	}
	
	@Override
	public void paintComponent(Graphics g) {
		super.paintComponent(g);
		
		update();
	}
	
	@Override
	public void robotAdded(Robot _r) {
		update();
	}

	@Override
	public void robotRemoved(Robot _r) {
		update();
	}

	@Override
	public void robotChanged(Robot _r) {
		update();
	}

	public int getSelectedIndex() {
		return table.getSelectedRow();
	}
}
