package warehouse.nxt.utils;

import java.util.ArrayList;

import lejos.geom.Line;

/**
 * This describes the geometric and sensor properties of a robot.
 * 
 * @author Nick Hawes
 *
 */
public interface MobileRobotDescription {

	/**
	 * The width of the robot. This assumes the width-wise centre point of the
	 * robot is at the halfway point of the robot's width. This is also the y
	 * origin of the robot's coordinate frame.
	 * 
	 * @return
	 */
	public float getTrackWidth();

	/**
	 * The length of the robot. This assumes the length-wise centre point of the
	 * robot is at the halfway point of the robot length. This is also the x
	 * origin of the robot's coordinate frame.
	 * 
	 * @return
	 */
	public double getRobotLength();

	/**
	 * Returns a shape which describes the outline of the robot relative to its
	 * centre point.
	 * 
	 * @return
	 */
	public Line[] getFootprint();

	/**
	 * Returns the relative footprint of any touch sensors.
	 * 
	 * @return
	 */
	public ArrayList<Line[]> getTouchSensors();

	/**
	 * Returns the description of any range sensors.
	 * 
	 * @return
	 */
	public ArrayList<RangeScannerDescription> getRangeScanners();
}
