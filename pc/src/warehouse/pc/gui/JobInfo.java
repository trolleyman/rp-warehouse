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
import javax.swing.JScrollPane;
import javax.swing.JTable;
import javax.swing.SpringLayout;
import javax.swing.event.ListSelectionEvent;
import javax.swing.event.ListSelectionListener;
import javax.swing.table.AbstractTableModel;

import rp.util.Pair;
import sun.java2d.pipe.SpanClipRenderer;
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
		
		JButton cancel = new JButton("Cancel Job");
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
				if (table.getSelectedRow() == -1) {
					cancel.setEnabled(false);
				} else {
					cancel.setEnabled(true);
				}
			}
		});
		
		//JScrollPane scrollpane = new JScrollPane(table, JScrollPane.VERTICAL_SCROLLBAR_ALWAYS, JScrollPane.HORIZONTAL_SCROLLBAR_NEVER);
		
		JPanel pan = new JPanel();
		pan.setLayout(new BorderLayout());
		pan.add(table.getTableHeader(), BorderLayout.NORTH);
		pan.add(table, BorderLayout.CENTER);
		//setPreferredSize(new Dimension(200, (int) getPreferredSize().getHeight()));
		pan.setBorder(BorderFactory.createEmptyBorder(6, 6, 6, 6));
		
		SpringLayout layout = new SpringLayout();
		setLayout(layout);
		add(pan);
		add(cancel);
		
		mi.addRobotListener(this);
		
		update();
	}
	
	public void update() {
		// Update jobs
		synchronized (this) {
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
