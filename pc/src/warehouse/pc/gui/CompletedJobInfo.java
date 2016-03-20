package warehouse.pc.gui;

import java.awt.BorderLayout;
import java.awt.Dimension;
import java.util.ArrayList;

import javax.swing.BorderFactory;
import javax.swing.JPanel;
import javax.swing.JScrollPane;
import javax.swing.JTable;
import javax.swing.table.AbstractTableModel;

import warehouse.pc.job.Job;
import warehouse.pc.shared.MainInterface;
import warehouse.pc.shared.Robot;
import warehouse.pc.shared.RobotListener;

public class CompletedJobInfo extends JPanel implements RobotListener {
	private final MainInterface mi;
	
	private ArrayList<Job> jobs;
	
	private AbstractTableModel model;
	private JTable table;
	
	@SuppressWarnings("serial")
	public CompletedJobInfo() {
		mi = MainInterface.get();
		
		jobs = new ArrayList<>();
		
		model = new AbstractTableModel() {
			@Override
			public Object getValueAt(int row, int col) {
				switch (col) {
				case 0: return jobs.get(row).getId();
				case 1: return jobs.get(row).getItems();
				}
				return "";
			}
			
			@Override
			public int getRowCount() {
				return jobs.size();
			}
			
			@Override
			public int getColumnCount() {
				return 2;
			}
			
			@Override
			public String getColumnName(int col) {
				switch (col) {
				case 0: return "Job ID";
				case 1: return "Items";
				}
				return "";
			}
		};
		
		table = new JTable(model);
		JScrollPane scrollpane = new JScrollPane(table,
			JScrollPane.VERTICAL_SCROLLBAR_AS_NEEDED, JScrollPane.HORIZONTAL_SCROLLBAR_NEVER);
		table.getColumnModel().getColumn(0).setMaxWidth(110);//.setPreferredWidth(50);
		
		setLayout(new BorderLayout());
		add(table.getTableHeader(), BorderLayout.PAGE_START);
		add(scrollpane, BorderLayout.CENTER);
		
		doLayout();
		scrollpane.getViewport().setPreferredSize(
			new Dimension((int)scrollpane.getViewport().getPreferredSize().getWidth(), 100));
		scrollpane.setBorder(BorderFactory.createEmptyBorder(6, 6, 6, 6));
		
		update();
	}
	
	@Override
	public void doLayout() {
		super.doLayout();
		
		
	}
	
	public void update() {
		jobs = mi.getRobotManager().getCompletedJobs();
		
		model.fireTableDataChanged();
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

}
