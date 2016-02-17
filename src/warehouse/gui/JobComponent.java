package warehouse.gui;

import java.awt.Dimension;
import java.util.ArrayList;
import java.util.Comparator;
import javax.swing.Box;
import javax.swing.BoxLayout;
import javax.swing.JPanel;
import javax.swing.JTable;
import javax.swing.ListSelectionModel;
import javax.swing.table.AbstractTableModel;
import warehouse.job.ItemQuantity;
import warehouse.job.Job;
import warehouse.job.JobList;
import warehouse.shared.JobListener;
import warehouse.shared.Server;

@SuppressWarnings("serial")
public class JobComponent extends JPanel implements JobListener {
	
	JobList jobList;
	ArrayList<Job> jobs;
	ArrayList<String> itemStrings;
	AbstractTableModel model;
	JTable table;
	
	public JobComponent() {
		super();
		jobList = Server.get().getJobList();
		
		model = new AbstractTableModel() {
			@Override
			public Object getValueAt(int _row, int _col) {
				synchronized (jobList) {
					if (_row < 0 || _row >= jobs.size())
						return "";
					
					switch (_col) {
					case 0:
						return jobs.get(_row).getId();
					case 1:
						return itemStrings.get(_row);
					}
					return "";
				}
			}
			
			@Override
			public int getRowCount() {
				synchronized (jobList) {
					return jobList.getJobList().size();
				}
			}
			
			@Override
			public int getColumnCount() {
				synchronized (jobList) {
					return 2;
				}
			}
			
			@Override
			public String getColumnName(int _col) {
				switch (_col) {
				case 0:
					return "ID";
				case 1:
					return "Items";
				}
				return "";
			}
		};
		table = new JTable(model);
		table.setSelectionMode(ListSelectionModel.SINGLE_SELECTION);
		table.getColumnModel().getColumn(0).setPreferredWidth(60);
		this.setLayout(new BoxLayout(this, BoxLayout.PAGE_AXIS));
		this.add(table.getTableHeader());
		this.add(table);
		this.add(Box.createVerticalGlue());
		
		setPreferredSize(new Dimension(300, 0));
		Server.get().addJobListener(this);
		updateJobs();
	}
	
	private void updateJobs() {
		synchronized (jobList) {
			jobList = Server.get().getJobList();
			jobs = new ArrayList<Job>(jobList.getJobList());
			
			jobs.sort(new Comparator<Job>() {
				@Override
				public int compare(Job _o1, Job _o2) {
					return new Integer(_o1.getId()).compareTo(_o2.getId());
				}
			});
			
			itemStrings = new ArrayList<>(jobs.size());
			for (Job j : jobs) {
				ArrayList<ItemQuantity> items = j.getItems();
				StringBuilder build = new StringBuilder(32);
				for (int i = 0; i < items.size(); i++) {
					ItemQuantity item = items.get(i);
					build.append(item.getName());
					build.append(": ");
					build.append(item.getQuantity());
					if (i < items.size() - 1)
						build.append(" & ");
				}
				itemStrings.add(build.toString());
			}
			model.fireTableDataChanged();
		}
	}
	
	@Override
	public void jobUpdated(Job _job) {
		updateJobs();
	}
}
