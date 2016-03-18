package warehouse.pc.gui;

import java.awt.Point;
import java.util.Optional;

import javax.swing.JLabel;
import javax.swing.JOptionPane;
import javax.swing.JPanel;
import javax.swing.JSpinner;
import javax.swing.SpinnerNumberModel;
import javax.swing.SpringLayout;

import warehouse.pc.shared.MainInterface;
import warehouse.pc.shared.Map;

public class StartingLocation {
	public static void main(String[] args) {
		Optional<Point> p = StartingLocation.getFromUser(MainInterface.get().getMap());
		System.out.println(p);
	}
	
	/**
	 * Gets a starting location from the user.
	 * @param map
	 * @return Optional.empty() when user cancels the operation.
	 *         Optional.of(Point) when user enters a valid starting position.
	 */
	public static Optional<Point> getFromUser(Map map) {
		JLabel message = new JLabel("Please enter the starting co-ordinates for the robot.");
		JLabel xLabel = new JLabel("x:");
		JSpinner xSpinner = new JSpinner(new SpinnerNumberModel(0, 0, map.getWidth() - 1 , 1));
		JLabel yLabel = new JLabel("y:");
		JSpinner ySpinner = new JSpinner(new SpinnerNumberModel(0, 0, map.getHeight() - 1, 1));
		
		JPanel panel = new JPanel();
		SpringLayout layout = new SpringLayout();
		panel.setLayout(layout);
		panel.add(message);
		panel.add(xLabel);
		panel.add(xSpinner);
		panel.add(yLabel);
		panel.add(ySpinner);
		
		layout.putConstraint(SpringLayout.NORTH, xSpinner, 6, SpringLayout.SOUTH, message);
		layout.putConstraint(SpringLayout.NORTH, ySpinner, 6, SpringLayout.SOUTH, message);
		
		layout.putConstraint(SpringLayout.NORTH, xLabel, 0, SpringLayout.NORTH, xSpinner);
		layout.putConstraint(SpringLayout.NORTH, yLabel, 0, SpringLayout.NORTH, ySpinner);
		
		layout.putConstraint(SpringLayout.WEST, xLabel, 0, SpringLayout.WEST, message);
		layout.putConstraint(SpringLayout.WEST, xSpinner, 6, SpringLayout.EAST, xLabel);
		layout.putConstraint(SpringLayout.WEST, yLabel, 6, SpringLayout.EAST, xSpinner);
		layout.putConstraint(SpringLayout.WEST, ySpinner, 6, SpringLayout.EAST, yLabel);
		
		while (true) {
			int res = JOptionPane.showConfirmDialog(null, panel, "Enter Starting Location",
				JOptionPane.OK_CANCEL_OPTION, JOptionPane.PLAIN_MESSAGE);
			
			if (res == JOptionPane.CANCEL_OPTION)
				return Optional.empty();
			
			int x = (Integer) xSpinner.getValue();
			int y = (Integer) ySpinner.getValue();
			
			if (map.getJunction(x, y) != null) {
				return Optional.of(new Point(x, y));
			}
			
			JOptionPane.showMessageDialog(null,
				"The co-ordinate " + x + ", " + y + " is not valid.",
				"Invalid Co-ordinate",
				JOptionPane.OK_OPTION);
		}
	}
}
