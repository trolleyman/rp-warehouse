package warehouse.pc.search;

import java.util.ArrayList;
import java.util.HashSet;
import java.util.LinkedList;

import rp.util.Pair;
import warehouse.pc.shared.Command;
import warehouse.pc.shared.Junction;

public class CReserveTable {
	private ArrayList<HashSet<Junction>> reserve;
	private ArrayList<Pair<Junction, Integer>> reservedAfter;
	
	public CReserveTable() {
		reserve = new ArrayList<>();
		reservedAfter = new ArrayList<>();
	}
	
	/**
	 * Extend the internal reseve to a certain time.
	 * @param time
	 */
	private void extend(int time) {
		reserve.ensureCapacity(time + 1);
		for (int i = reserve.size(); i < time; i++) {
			HashSet<Junction> newReserved = new HashSet<>();
			/*for (Pair<Junction, Integer> p : reservedAfter) {
				if (time >= p.getItem2()) {
					newReserved.add(p.getItem1());
				}
			}*/
			reserve.add(newReserved);
		}
	}
	
	public boolean isPositionReserved(Junction pos, int time) {
		extend(time);
		if (time < reserve.size() && reserve.get(time).contains(pos)) {
			return true;
		} else {
			for (Pair<Junction, Integer> p : reservedAfter) {
				if (time >= p.getItem2()) {
					return true;
				}
			}
			return false;
		}
	}
	
	/**
	 * Reserves a position at a certain time
	 * @param pos the position
	 * @param time the time
	 */
	public void reservePosition(Junction pos, int time) {
		extend(time);
		reserve.get(time).add(pos);
	}
	
	/**
	 * Reserves a position for all time steps after a time
	 * @param pos the position
	 * @param afterTime the time
	 */
	public void reservePositionAfter(Junction pos, int time) {
		reservedAfter.add(Pair.makePair(pos, time));
	}
	
	/**
	 * Reserves a list of commands
	 * @param _allCommands
	 * @param _i
	 */
	public void reservePositions(Junction from, LinkedList<Command> commands, int startTime) {
		int currentX = from.getX();
		int currentY = from.getY();
		int time = startTime;
		reserve.ensureCapacity(startTime + commands.size());
		reservePosition(from, startTime);
		
		for (Command com : commands) {
			com.setFrom(currentX, currentY);
			currentX = com.getX();
			currentY = com.getY();
			
			reservePosition(new Junction(currentX, currentY), time);
		}
	}
}
