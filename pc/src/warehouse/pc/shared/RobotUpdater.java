package warehouse.pc.shared;

import java.util.Optional;

import warehouse.shared.Constants;

public class RobotUpdater extends Thread {
	private volatile Object notifyStopped = new Object();
	private volatile boolean stop = false;
	private volatile boolean stopped = false;
	
	private Robot robot;
	private Command com;
	private MainInterface mi;

	public RobotUpdater(Robot _robot, Command _com) {
		this.robot = _robot;
		this.com = _com;
		mi = MainInterface.get();
	}
	
	private void interpolateTravel(double travel, double speed) {
		double travelX = travel * Math.sin(Math.toRadians(robot.getFacing()));
		double travelY = travel * Math.cos(Math.toRadians(robot.getFacing()));
		
		double totalDist = Math.sqrt(travelX*travelX + travelY*travelY);
		long finishedTime = System.currentTimeMillis() + (long) (totalDist / speed * 1000.0);
		
		double startX = robot.getX();
		double startY = robot.getY();
		
		double percentDone = 0.0;
		long start = System.currentTimeMillis();
		long now = System.currentTimeMillis();
		while (now < finishedTime && !stop) {
			percentDone = ((double)(start - now)) / (start - finishedTime);
			
			double dx = travelX * percentDone;
			double dy = travelY * percentDone;
			
			//System.out.println("p:" + percentDone + ", x:" + (startX + dx) + ", y:" + (startY + dy));
			robot.setX(startX + dx);
			robot.setY(startY + dy);
			
			try {
				Thread.sleep(50);
			} catch (InterruptedException e) {
				
			}
			now = System.currentTimeMillis();
		}
		
		robot.setX(startX + travelX);
		robot.setY(startY + travelY);
	}
	
	private void interpolateRotate(double rotate, double rotationSpeed) {
		long finishedTime = System.currentTimeMillis() + (long)(Math.abs(rotate) / rotationSpeed * 1000.0);
		
		double startRotate = robot.getFacing();
		
		double percentDone = 0.0;
		long start = System.currentTimeMillis();
		long now = System.currentTimeMillis();
		while (now < finishedTime && !stop) {
			percentDone = (start - now) / (double) (start - finishedTime);
			
			double dr = rotate * percentDone;
			//System.out.println("p:" + percentDone + ", dr:" + (dr) + ", r:" + (startRotate + dr));
			robot.setFacing(startRotate + dr);
			
			try {
				Thread.sleep(50);
			} catch (InterruptedException e) {
				
			}
			now = System.currentTimeMillis();
		}
		robot.setFacing(startRotate + rotate);
	}
	
	@Override
	public void run() {
		try {
			Optional<Direction> od = com.toDirection();
			if (!od.isPresent())
				return;
			
			Direction d = od.get();
			RelativeDirection relDir = RelativeDirection.fromTo(robot.getDirection(), d);
			
			double cellSize = MainInterface.get().getMap().getCellSize();
			double speed = Constants.ROBOT_SPEED / cellSize;
			double rotationSpeed = Constants.ROBOT_ROTATION_SPEED;
			
			double travel = 1.0;
			double rotate = 0.0;
			
			switch (relDir) {
			case FORWARD:
				break;
			case BACKWARD:
				rotate = 180.0;
				break;
			case LEFT:
				rotate = -90.0;
				break;
			case RIGHT:
				rotate = 90.0;
				break;
			default:
				return;
			}
			
			interpolateRotate(rotate, rotationSpeed);
			interpolateTravel(travel, speed);
		} finally {
			synchronized (notifyStopped) {
				stopped = true;
				notifyStopped.notifyAll();
			}
		}
	}
	
	/**
	 * Ends the RobotUpdater gracefully, and waits for it to stop.
	 */
	public void end() {
		if (stopped) {
			return;
		}
		stop = true;
		synchronized (notifyStopped) {
			try {
				if (stopped) {
					return;
				}
				notifyStopped.wait();
			} catch (InterruptedException e) {
				
			}
		}
	}
}
