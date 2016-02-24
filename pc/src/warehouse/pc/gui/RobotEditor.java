package warehouse.pc.gui;

import javax.swing.JLabel;
import javax.swing.JPanel;

import warehouse.shared.robot.Robot;

@SuppressWarnings("serial")
public class RobotEditor extends JPanel {
	private Robot selectedRobot = null;
	
	private JLabel selectedRobotLabel;
	
	public RobotEditor() {
		super();
		
		selectedRobotLabel = new JLabel();
		
		
		
		this.add(selectedRobotLabel);
		
		update();
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
		} else {
			selectedRobotLabel.setText(
					"Selected Robot: " + selectedRobot.getName() + " (" + selectedRobot.getID() + ")");
		}
		
		
	}
}
