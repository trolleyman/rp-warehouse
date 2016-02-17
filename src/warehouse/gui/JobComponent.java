package warehouse.gui;

import java.awt.BorderLayout;
import java.awt.Dimension;
import java.util.ArrayList;
import java.util.Comparator;
import java.util.Enumeration;

import javax.swing.JComponent;
import javax.swing.JFrame;
import javax.swing.JPanel;
import javax.swing.JTable;
import javax.swing.ListSelectionModel;
import javax.swing.event.TableColumnModelListener;
import javax.swing.table.AbstractTableModel;
import javax.swing.table.JTableHeader;
import javax.swing.table.TableColumn;
import javax.swing.table.TableColumnModel;

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
	JTable table;
	
	public JobComponent() {
		super();
		jobList = Server.get().getJobList();
		
		jobUpdated(null);
		
		AbstractTableModel model = new AbstractTableModel() {
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
		JPanel inner = new JPanel();
		inner.setLayout(new BorderLayout());
		inner.add(table.getTableHeader(), BorderLayout.NORTH);
		inner.add(table, BorderLayout.CENTER);
		this.add(inner, BorderLayout.NORTH);
		this.add(new JPanel(), BorderLayout.CENTER);
		table.setEnabled(true);
		
		setMinimumSize(new Dimension(300, 200));
	}

	@Override
	public void jobUpdated(Job _job) {
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
		}
	}
}
