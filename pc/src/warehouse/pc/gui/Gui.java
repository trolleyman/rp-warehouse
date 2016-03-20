package warehouse.pc.gui;

import java.awt.BorderLayout;
import java.awt.Dimension;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.awt.event.WindowEvent;
import java.awt.event.WindowListener;

import javax.swing.BorderFactory;
import javax.swing.Box;
import javax.swing.JButton;
import javax.swing.JFrame;
import javax.swing.JPanel;
import javax.swing.JScrollPane;
import javax.swing.JTable;
import javax.swing.SpringLayout;
import javax.swing.UIManager;
import javax.swing.UnsupportedLookAndFeelException;
import javax.swing.event.ListSelectionEvent;
import javax.swing.event.ListSelectionListener;
import javax.swing.table.AbstractTableModel;
import javax.swing.table.TableModel;

import rp.robotics.mapping.GridMap;
import rp.robotics.mapping.LineMap;
import rp.robotics.simulation.MapBasedSimulation;
import rp.robotics.visualisation.ExampleGridMapVisualisation;
import rp.robotics.visualisation.GridMapVisualisation;
import rp.robotics.visualisation.MapVisualisationComponent;

import warehouse.pc.job.Item;
import warehouse.pc.job.ItemList;
import warehouse.pc.shared.MainInterface;
import warehouse.pc.shared.RobotListener;
import warehouse.pc.shared.RobotManager;
import warehouse.pc.shared.Robot;

public class Gui implements Runnable, RobotListener {
	public static void main(String[] args) {
		try {
			UIManager.setLookAndFeel(UIManager.getSystemLookAndFeelClassName());
		} catch (ClassNotFoundException | InstantiationException | IllegalAccessException
				| UnsupportedLookAndFeelException e1) {
			try {
				UIManager.setLookAndFeel(UIManager.getCrossPlatformLookAndFeelClassName());
			} catch (ClassNotFoundException | InstantiationException | IllegalAccessException
					| UnsupportedLookAndFeelException e2) {
				// hmm.. Hope things work out.
			}
		}
		
		//displayMap(MapUtils.create2014Map2(), 2.0f);
		//displayMap(MapUtils.createRealWarehouse(), 200.0f);
		
		// Robot Manager starts off paused.
		MainInterface.get().getRobotManager().pause();
		Thread t = new Thread(MainInterface.get().getRobotManager(), "RobotManager");
		t.start();
		Gui g = new Gui();
		g.run();
	}
	
	// Used for testing - Displays a LineMap
	@SuppressWarnings("unused")
	private static void displayMap(LineMap lineMap, float scale) {
		// Grid map configuration

		// Grid junction numbers
		int xJunctions = 10;
		int yJunctions = 7;

		float junctionSeparation = 30;

		int xInset = 14;
		int yInset = 31;

		displayMap(new GridMap(xJunctions, yJunctions, xInset, yInset,
				junctionSeparation, lineMap), scale);
	}
	
	private static void displayMap(GridMap gridMap, float scale) {
		GridMapVisualisation mapVis = new GridMapVisualisation(gridMap,
				(LineMap) gridMap, scale);
		
		MapBasedSimulation sim = new MapBasedSimulation((LineMap) gridMap);
		
		MapVisualisationComponent.populateVisualisation(mapVis, sim);
		
		ExampleGridMapVisualisation.displayVisualisation(mapVis);
	}
	
	private static final int LEFT_WIDTH = 250;
	
	public String selectedItemName;
	private JFrame frame;
	private RobotEditor editor;
	
	private BluetoothSelector selector;
	private JButton connectionButton;
	
	private JobInfo jobInfo;
	
	public Gui() {
		selectedItemName = "";
		MainInterface i = MainInterface.get();
		i.addRobotListener(this);
		frame = new JFrame("Warehouse Viewer");
		frame.setDefaultCloseOperation(JFrame.DISPOSE_ON_CLOSE);
		frame.addWindowListener(new WindowListener() {
			@Override public void windowOpened(WindowEvent _e) {}
			@Override public void windowIconified(WindowEvent _e) {}
			@Override public void windowDeiconified(WindowEvent _e) {}
			@Override public void windowDeactivated(WindowEvent _e) {}
			@Override public void windowActivated(WindowEvent _e) {}
			@Override public void windowClosed(WindowEvent _e) {}
			@Override
			public void windowClosing(WindowEvent _e) {
				MainInterface s = MainInterface.get();
				s.close();
				System.out.println("Quitting...");
				System.exit(0);
			}
		});
		
		JPanel map = new JPanel();
		map.setLayout(new BorderLayout());
		MapComponent mapComponent = new MapComponent(this);
		map.add(mapComponent, BorderLayout.CENTER);
		map.setBorder(BorderFactory.createTitledBorder("Map View"));
		
		JPanel inner = new JPanel();
		inner.setLayout(new BorderLayout());
		inner.add(map, BorderLayout.CENTER);
		inner.add(createBottomToolbar(), BorderLayout.PAGE_END);
		
		JPanel panel = new JPanel();
		panel.setLayout(new BorderLayout());
		panel.add(createLeftToolbar(), BorderLayout.LINE_START);
		panel.add(inner, BorderLayout.CENTER);
		
		panel.setBorder(BorderFactory.createEmptyBorder(10, 10, 10, 10));
		frame.add(panel);
		frame.pack();
		frame.setSize(1200, 800);
		frame.setLocationRelativeTo(null);
	}
	
	private JPanel createConnect() {
		JPanel connectBox = new JPanel();
		JPanel inner = new JPanel();
		connectBox.setBorder(BorderFactory.createTitledBorder("Bluetooth Connection"));
		
		SpringLayout layout = new SpringLayout();
		inner.setLayout(layout);
		inner.setBorder(BorderFactory.createEmptyBorder(0, 0, 0, 0));
		connectionButton = new JButton("Connect");
		selector = new BluetoothSelector(this);
		connectionButton.addActionListener(new ActionListener() {
			@Override
			public void actionPerformed(ActionEvent e) {
				selector.connect();
			}
		});
		
		inner.add(selector);
		inner.add(connectionButton);
		selector.setPreferredSize(new Dimension(LEFT_WIDTH, selector.getPreferredSize().height));
		layout.putConstraint(SpringLayout.EAST, connectionButton, 0, SpringLayout.EAST, selector);
		layout.putConstraint(SpringLayout.NORTH, connectionButton, 6, SpringLayout.SOUTH, selector);
		
		connectBox.add(inner);
		inner.doLayout();
		
		inner.setPreferredSize(new Dimension(LEFT_WIDTH, connectionButton.getY() + connectionButton.getHeight()));
		inner.doLayout();
		connectBox.doLayout();
		connectBox.setMinimumSize(connectBox.getPreferredSize());
		connectBox.setMaximumSize(connectBox.getPreferredSize());
		return connectBox;
	}
	
	private JPanel createRobotEditor() {
		JPanel editorBox = new JPanel();
		editorBox.setBorder(BorderFactory.createTitledBorder("Robot Editor"));
		editor = new RobotEditor(LEFT_WIDTH);
		editorBox.add(editor);
		editorBox.doLayout();
		editorBox.setMinimumSize(editorBox.getPreferredSize());
		editorBox.setMaximumSize(editorBox.getPreferredSize());
		//editorBox.setPreferredSize(new Dimension(LEFT_WIDTH, max.height));
		return editorBox;
	}
	
	private JPanel createItemInfo() {
		JPanel info = new JPanel();
		info.setBorder(BorderFactory.createTitledBorder("Item Information"));
		ItemList items = MainInterface.get().getItemList();
		@SuppressWarnings("serial")
		TableModel dataModel = new AbstractTableModel() {
			@Override
			public int getColumnCount() { return 3; }
			@Override
			public int getRowCount() { return items.getList().size(); }
			@Override
			public Object getValueAt(int row, int col) {
				Item i = items.getList().get(row);
				switch (col) {
				case 0: return i.getName();
				case 1: return i.getWeight();
				case 2: return i.getReward();
				}
				return "";
			}
			@Override
			public String getColumnName(int index) {
				switch (index) {
				case 0: return "Name";
				case 1: return "Weight";
				case 2: return "Reward";
				}
				return "";
			}
		};
		JTable table = new JTable(dataModel);
		table.getSelectionModel().addListSelectionListener(new ListSelectionListener() {
			@Override
			public void valueChanged(ListSelectionEvent e) {
				int i = table.getSelectedRow();
				if (i == -1) {
					selectedItemName = "";
					return;
				}
				selectedItemName = table.getValueAt(i, 0).toString();
				update();
			}
		});
		JScrollPane scrollpane = new JScrollPane(table,
			JScrollPane.VERTICAL_SCROLLBAR_ALWAYS, JScrollPane.HORIZONTAL_SCROLLBAR_NEVER);
		
		info.setLayout(new BorderLayout());
		info.add(table.getTableHeader(), BorderLayout.NORTH);
		info.add(scrollpane, BorderLayout.CENTER);
		//info.setMaximumSize(new Dimension(300, 10000000));
		info.setPreferredSize(new Dimension(LEFT_WIDTH, (int) 300));
		return info;
	}
	
	private JPanel createManagerControls() {
		JPanel res = new JPanel();
		res.setBorder(BorderFactory.createTitledBorder("Robot Manager Controls"));
		JButton pause = new JButton("Pause");
		JButton resume = new JButton("Resume");
		pause.setEnabled(false);
		
		pause.addActionListener(new ActionListener() {
			@Override
			public void actionPerformed(ActionEvent _e) {
				RobotManager man = MainInterface.get().getRobotManager();
				man.pause();
				pause.setEnabled(false);
				resume.setEnabled(true);
				editor.setIsPaused(true);
			}
		});
		resume.addActionListener(new ActionListener() {
			@Override
			public void actionPerformed(ActionEvent _e) {
				RobotManager man = MainInterface.get().getRobotManager();
				man.resume();
				pause.setEnabled(true);
				resume.setEnabled(false);
				editor.setIsPaused(false);
			}
		});
		res.add(pause);
		res.add(resume);
		
		SpringLayout layout = new SpringLayout();
		res.setLayout(layout);
		SpringUtilities.makeCompactGrid(res, 1, 2, 6, 6, 6, 6);
		res.doLayout();
		res.setMinimumSize(res.getPreferredSize());
		res.setMaximumSize(res.getPreferredSize());
		return res;
	}
	
	private JPanel createJobInfo() {
		jobInfo = new JobInfo();
		jobInfo.setBorder(BorderFactory.createTitledBorder("Job Information"));
		return jobInfo;
	}
	
	private JPanel createLeftToolbar() {
		JPanel res = new JPanel();
		res.setLayout(new SpringLayout());
		res.add(createConnect());
		res.add(createRobotEditor());
		res.add(createManagerControls());
		res.add(createItemInfo());
		//res.add(Box.createVerticalGlue());
		SpringUtilities.makeCompactGrid(res, res.getComponentCount(), 1, 0, 0, 6, 6);
		return res;
	}
	
	private JPanel createBottomToolbar() {
		JPanel res = new JPanel();
		res.setLayout(new SpringLayout());
		res.add(createJobInfo());
		res.add(Box.createHorizontalGlue());
		SpringUtilities.makeCompactGrid(res, 1, res.getComponentCount(), 0, 0, 6, 6);
		return res;
	}
	
	/**
	 * Selects a new robot to be edited. Can be null.
	 */
	public void selectRobot(Robot _selected) {
		editor.selectRobot(_selected);
	}

	public void update() {
		if (!selector.isRunning()) {
			connectionButton.setEnabled(false);
		}
		frame.repaint();
	}
	
	@Override
	public void robotChanged(Robot _r) {
		update();
	}
	
	@Override
	public void robotAdded(Robot _r) {
		update();
	}

	@Override
	public void robotRemoved(Robot _r) {
		if (_r == editor.getSelectedRobot()) {
			editor.selectRobot(null);
		}
		update();
	}

	@Override
	public void run() {
		frame.setVisible(true);
	}
}
