package warehouse.pc.shared;

import rp.robotics.DifferentialDriveRobot;
import warehouse.shared.Command;

public class RobotUpdater extends Thread {
	private Robot robot;
	private Command com;
	private MainInterface mi;

	public RobotUpdater(Robot _robot, Command _com) {
		this.robot = _robot;
		this.com = _com;
		mi = MainInterface.get();
	}
	
	private void interpolateTravel(double travel, double speed) {
		double travelX = travel * Math.sin(robot.getFacing());
		double travelY = travel * Math.cos(robot.getFacing());
		
		double totalDist = Math.sqrt(travelX*travelX + travelY*travelY);
		long finishedTime = System.nanoTime() + (long) (totalDist / speed) * 1_000_000_000L;
		
		double startX = robot.getX();
		double startY = robot.getY();
		
		double percentDone = 0.0;
		long start = System.nanoTime();
		long now = System.nanoTime();
		while ((now = System.nanoTime()) < finishedTime) {
			percentDone = (start - now) / (start - finishedTime);
			
			double dx = mi.getMap().getGridX(travelX * percentDone);
			double dy = mi.getMap().getGridY(travelY * percentDone);
			
			robot.setX(startX + dx);
			robot.setX(startY + dy);
			
			try {
				Thread.sleep(100);
			} catch (InterruptedException e) {
				
			}
		}
		
		robot.setX(startX + travelX);
		robot.setX(startY + travelY);
	}
	
	public void interpolateRotate(double rotate, double rotationSpeed) {
		long finishedTime = System.nanoTime() + (long) (rotate / rotationSpeed) * 1_000_000_000L;
		
		double startRotate = robot.getFacing();
		
		double percentDone = 0.0;
		long start = System.nanoTime();
		long now = System.nanoTime();
		while ((now = System.nanoTime()) < finishedTime) {
			percentDone = (start - now) / (start - finishedTime);
			
			double dr = rotate * percentDone;
			
			robot.setFacing(startRotate + dr);
			
			try {
				Thread.sleep(100);
			} catch (InterruptedException e) {
				
			}
		}
		robot.setFacing(startRotate + rotate);
	}
	
	@Override
	public void run() {
		double speed = 0.1;
		double rotationSpeed = 70.0;
		
		double travelBefore = 0.0;
		double travelAfter = 0.0;
		double rotate = 0.0;
		
		switch (com) {
		case FORWARD:
			travelBefore = 0.05;
			break;
		case BACKWARD:
			rotate = 180.0;
			break;
		case LEFT:
			travelBefore = 0.07;
			rotate = -90.0;
			break;
		case RIGHT:
			travelBefore = 0.07;
			rotate = 90.0;
			break;
		default:
			return;
		}
		
		interpolateTravel(travelBefore, speed);
		interpolateRotate(rotate, rotationSpeed);
		interpolateTravel(travelAfter, speed);
	}
}
