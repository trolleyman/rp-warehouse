package warehouse.pc.gui;

import java.awt.BorderLayout;
import java.awt.Dimension;
import java.awt.Graphics;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.util.ArrayDeque;
import java.util.ArrayList;
import java.util.Comparator;

import javax.swing.BorderFactory;
import javax.swing.JButton;
import javax.swing.JPanel;
import javax.swing.JTable;
import javax.swing.SpringLayout;
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
	
	private JPanel tableContainer;
	private JTable table;
	private AbstractTableModel model;
	private JButton cancel;
	
	public JobInfo() {
		super();
		
		mi = MainInterface.get();
		
		cancel = new JButton("Cancel Job");
		cancel.setEnabled(false);
		cancel.addActionListener(new ActionListener() {
			@Override
			public void actionPerformed(ActionEvent _e) {
				cancelSelectedJob();
			}
		});
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
				case 1: {
					Job j = p.getItem2();
					if (j != null)
						return j.getId();
					return "";
				}
				case 2: {
					Job j = p.getItem2();
					if (j == null)
						return "";
					
					ArrayList<ItemQuantity> iqs = j.getItems();
					
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
				if (table.getSelectedRow() == -1) {
					cancel.setEnabled(false);
				} else {
					cancel.setEnabled(true);
				}
			}
		});
		
		//JScrollPane scrollpane = new JScrollPane(table, JScrollPane.VERTICAL_SCROLLBAR_ALWAYS, JScrollPane.HORIZONTAL_SCROLLBAR_NEVER);
		
		tableContainer = new JPanel();
		tableContainer.setLayout(new BorderLayout());
		tableContainer.add(table.getTableHeader(), BorderLayout.NORTH);
		tableContainer.add(table, BorderLayout.CENTER);
		table.getColumnModel().getColumn(0).setPreferredWidth(100);
		table.getColumnModel().getColumn(2).setPreferredWidth(200);
		//setPreferredSize(new Dimension(200, (int) getPreferredSize().getHeight()));
		tableContainer.setBorder(BorderFactory.createEmptyBorder(6, 6, 6, 6));
		
		tableContainer.doLayout();
		
		SpringLayout layout = new SpringLayout();
		setLayout(layout);
		add(tableContainer);
		add(cancel);
		
		layout.putConstraint(SpringLayout.NORTH, cancel, 6, BorderLayout.SOUTH, tableContainer);
		layout.putConstraint(SpringLayout.EAST, cancel, -7, BorderLayout.EAST, tableContainer);
		
		mi.addRobotListener(this);
		
		update();
		
		doLayout();
		
		update();
	}
	
	public void update() {
		// Update jobs
		synchronized (this) {
			ArrayList<Pair<Robot, Job>> newJobs = new ArrayList<>();
			
			RobotManager man = mi.getRobotManager();
			
			for (Robot r : mi.getRobots()) {
				ArrayDeque<Job> jobs = man.getJobs(r);
				Job j = null;
				if (jobs != null) {
					j = jobs.peekFirst();
				}
				newJobs.add(Pair.makePair(r, j));
			}
			
			newJobs.sort(new Comparator<Pair<Robot, Job>>() {
				@Override
				public int compare(Pair<Robot, Job> o1, Pair<Robot, Job> o2) {
					return o1.getItem1().compareTo(o2.getItem1());
				}
			});
			
			String robotName = "";
			if (table.getSelectedRow() != -1)
				robotName = table.getValueAt(table.getSelectedRow(), 0).toString();
			
			jobs = newJobs;
			
			// Update selection.
			for (int i = 0; i < jobs.size(); i++) {
				if (jobs.get(i).getItem1().getName().equalsIgnoreCase(robotName)) {
					table.setRowSelectionInterval(i, i);
					break;
				}
			}
			
			// Notify the table
			model.fireTableDataChanged();
			
			int w = tableContainer.getWidth() + 6 + 6;
			int h = tableContainer.getHeight() + 6 + cancel.getHeight() + 36;
			setPreferredSize(new Dimension(w, h));
		}
	}
	
	public void cancelSelectedJob() {
		synchronized (this) {
			int i = table.getSelectedRow();
			if (i != -1)
				mi.getRobotManager().cancelJobs(jobs.get(i).getItem1());
		}
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
