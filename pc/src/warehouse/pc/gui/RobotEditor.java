package warehouse.pc.gui;

import java.awt.Dimension;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;

import javax.swing.BoxLayout;
import javax.swing.JButton;
import javax.swing.JLabel;
import javax.swing.JPanel;
import javax.swing.JSpinner;
import javax.swing.SpinnerNumberModel;
import javax.swing.SpringLayout;

import warehouse.pc.shared.MainInterface;
import warehouse.shared.robot.Robot;

@SuppressWarnings("serial")
public class RobotEditor extends JPanel {
	private Robot selectedRobot = null;
	
	private JLabel selectedRobotLabel;
	private JLabel robotIDLabel;
	
	private JSpinner xSpinner;
	private JSpinner ySpinner;

	private JButton posButton;

	private JSpinner headingSpinner;

	private JButton headingButton;
	
	public RobotEditor() {
		super();
		
		this.setLayout(new BoxLayout(this, BoxLayout.PAGE_AXIS));
		selectedRobotLabel = new JLabel("", JLabel.LEADING);
		robotIDLabel = new JLabel("", JLabel.LEADING);
		selectedRobotLabel.setAlignmentX(RIGHT_ALIGNMENT);
		robotIDLabel.setAlignmentX(RIGHT_ALIGNMENT);
		
		SpringLayout layout = new SpringLayout();
		
		this.setLayout(layout);
		
		xSpinner = new JSpinner(new SpinnerNumberModel(0.0, 0.0, MainInterface.get().getCurrentState().getMap().getWidth() - 1, 1.0));
		xSpinner.setPreferredSize(new Dimension(50, (int) xSpinner.getPreferredSize().getHeight()));
		
		ySpinner = new JSpinner(new SpinnerNumberModel(0.0, 0.0, MainInterface.get().getCurrentState().getMap().getHeight() - 1, 1.0));
		ySpinner.setPreferredSize(new Dimension(50, (int) ySpinner.getPreferredSize().getHeight()));
		
		posButton = new JButton("Set Position");
		posButton.addActionListener(new ActionListener() {
			@Override
			public void actionPerformed(ActionEvent e) {
				if (selectedRobot != null) {
					selectedRobot.setX((double)xSpinner.getValue());
					selectedRobot.setY((double)ySpinner.getValue());
					MainInterface.get().updateRobot(selectedRobot);
				}
			}
		});
		
		JLabel xLabel = new JLabel("x:");
		JLabel yLabel = new JLabel("y:");
		
		JLabel headingLabel = new JLabel("heading:");
		headingSpinner = new JSpinner(new SpinnerNumberModel(0.0, 0.0, 360.0, 10.0));
		
		headingButton = new JButton("Set Heading");
		headingButton.addActionListener(new ActionListener() {
			@Override
			public void actionPerformed(ActionEvent e) {
				if (selectedRobot != null) {
					selectedRobot.setFacing((double)headingSpinner.getValue());
					MainInterface.get().updateRobot(selectedRobot);
				}
			}
		});
		
		this.add(selectedRobotLabel);
		this.add(robotIDLabel);
		this.add(xLabel);
		this.add(yLabel);
		this.add(xSpinner);
		this.add(ySpinner);
		this.add(posButton);
		this.add(headingLabel);
		this.add(headingSpinner);
		this.add(headingButton);
		
		//layout.putConstraint(SpringLayout.NORTH, selectedRobotLabel, 6, SpringLayout.NORTH, this);
		layout.putConstraint(SpringLayout.NORTH, robotIDLabel, 6, SpringLayout.SOUTH, selectedRobotLabel);
		layout.putConstraint(SpringLayout.NORTH, xSpinner, 6, SpringLayout.SOUTH, robotIDLabel);
		layout.putConstraint(SpringLayout.NORTH, ySpinner, 6, SpringLayout.SOUTH, xSpinner);
		layout.putConstraint(SpringLayout.NORTH, xLabel, 3, SpringLayout.NORTH, xSpinner);
		layout.putConstraint(SpringLayout.NORTH, yLabel, 3, SpringLayout.NORTH, ySpinner);
		//layout.putConstraint(SpringLayout.EAST, xLabel, 6, SpringLayout.WEST, xSpinner);
		//layout.putConstraint(SpringLayout.EAST, yLabel, 6, SpringLayout.WEST, ySpinner);
		layout.putConstraint(SpringLayout.WEST, xSpinner, 6, SpringLayout.EAST, xLabel);
		layout.putConstraint(SpringLayout.WEST, ySpinner, 6, SpringLayout.EAST, yLabel);
		layout.putConstraint(SpringLayout.NORTH, posButton, 6, SpringLayout.SOUTH, ySpinner);
		layout.putConstraint(SpringLayout.NORTH, headingSpinner, 6, SpringLayout.SOUTH, posButton);
		layout.putConstraint(SpringLayout.NORTH, headingLabel, 3, SpringLayout.NORTH, headingSpinner);
		layout.putConstraint(SpringLayout.WEST, headingSpinner, 6, SpringLayout.EAST, headingLabel);
		layout.putConstraint(SpringLayout.NORTH, headingButton, 6, SpringLayout.SOUTH, headingSpinner);
		//layout.putConstraint(SpringLayout.HEIGHT, this, 6, SpringLayout.SOUTH, headingButton);
		
	    //this.setPreferredSize(this.getPreferredSize());
		setPreferredSize(new Dimension(200, 180));
	    
		update();
	}
	
	@Override
	public void doLayout() {
		super.doLayout();
		
		setPreferredSize(new Dimension(200, (int)
			(headingButton.getY() + 6 + headingButton.getPreferredSize().getHeight())));
	}
	
	
	/**
	 * Selects a new robot to be edited. Can be null.
	 */
	public void selectRobot(Robot _selectedRobot) {
		selectedRobot = _selectedRobot;
		update();
	}
	
	private void update() {
		if (selectedRobot == null) {
			selectedRobotLabel.setText("Selected Robot: None");
			robotIDLabel.setText("ID: ?");
		} else {
			selectedRobotLabel.setText(
					"Selected Robot: " + selectedRobot.getName());
			robotIDLabel.setText("ID: " + selectedRobot.getID());
		}
		
		boolean enabled = selectedRobot != null;
		xSpinner.setEnabled(enabled);
		ySpinner.setEnabled(enabled);
		posButton.setEnabled(enabled);
		headingSpinner.setEnabled(enabled);
		headingButton.setEnabled(enabled);
		
		if (selectedRobot == null) {
			xSpinner.setValue(0.0);
			ySpinner.setValue(0.0);
			headingSpinner.setValue(0.0);
		} else {
			xSpinner.setValue(selectedRobot.getX());
			ySpinner.setValue(selectedRobot.getY());
			headingSpinner.setValue(selectedRobot.getFacing());
		}
		
		repaint();
	}
}
