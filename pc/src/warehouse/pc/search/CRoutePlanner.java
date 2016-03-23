package warehouse.pc.search;

import java.util.ArrayList;
import java.util.Comparator;
import java.util.HashMap;
import java.util.LinkedList;
import java.util.Map.Entry;
import java.util.Optional;

import rp.util.Pair;
import warehouse.pc.job.ItemQuantity;
import warehouse.pc.job.Job;
import warehouse.pc.shared.Command;
import warehouse.pc.shared.CommandType;
import warehouse.pc.shared.Junction;
import warehouse.pc.shared.Map;
import warehouse.pc.shared.Robot;

public class CRoutePlanner {
	
	private CReserveTable reserve;
	private Map map;
	private ArrayList<Junction> bases;
	private RouteFinder finder;
	private CRouteFinder multiFinder;
	
	public CRoutePlanner(Map _map, ArrayList<Junction> _bases, RouteFinder _finder) {
		reserve = new CReserveTable();
		map = _map;
		bases = _bases;
		finder = _finder;
		multiFinder = new CRouteFinder(map, finder);
	}
	
	public HashMap<Robot, LinkedList<Command>> routeRobots(HashMap<Robot, LinkedList<Job>> jobs) {
		HashMap<Robot, LinkedList<Command>> commands = new HashMap<>();
		for (Entry<Robot, LinkedList<Job>> e : jobs.entrySet()) {
			commands.put(e.getKey(), routeRobot(e.getKey(), e.getValue()));
		}
		return commands;
	}
	
	public LinkedList<Command> routeRobot(Robot r, LinkedList<Job> jobs) {
		float weight = 0.0f;
		
		int currentX = r.getGridX();
		int currentY = r.getGridY();
				
		LinkedList<Command> allCommands = new LinkedList<>();
		
		if (jobs.isEmpty()) {
			// Path to nearest available base
			LinkedList<Command> commands = null;
			for (Junction base : getSortedBases(currentX, currentY)) {
				Optional<LinkedList<Command>> oc =
					calculateCommands(new Junction(currentX, currentY), base, allCommands.size());
				
				if (oc.isPresent()) {
					commands = oc.get();
					break;
				}
			}
			return commands;
		}
		
		outer: for (Job j : jobs) {
			for (ItemQuantity i : j.getItems()) {
				if (weight + i.getItem().getWeight() * i.getQuantity() > Robot.MAX_WEIGHT) {
					// Path to nearest available base
					Optional<Pair<Junction, LinkedList<Command>>> op = routeToNearestBase(currentX, currentY, allCommands.size());
					if (!op.isPresent()) {
						// Error - no bases are available
						System.err.println("No bases available for " + r.getIdentity() + ".");
						break outer;
					}
					LinkedList<Command> commands = op.get().getItem2();
					Junction base = op.get().getItem1();
					allCommands.addAll(commands);
					currentX = base.getX();
					currentY = base.getY();
					weight = 0;
				}
				
				// Path from current position to item position
				Optional<LinkedList<Command>> op = calculateCommands(new Junction(currentX, currentY), i.getItem().getJunction(), allCommands.size());
				if (!op.isPresent()) {
					System.err.println("Could not route to item " + i.getItem().getName() + " from " + currentX + ", " + currentY + " at timestep " + allCommands.size());
					break outer;
				}
				
				Junction at = i.getItem().getJunction();
				LinkedList<Command> commands = op.get();
				currentX = at.getX();
				currentY = at.getY();
				weight += i.getItem().getWeight() * i.getQuantity();
				allCommands.addAll(commands);
				allCommands.add(Command.pickUp(i.getQuantity(), i.getItem().getWeight()));
			}
			
			// Drop off all items.
			Optional<Pair<Junction, LinkedList<Command>>> op = routeToNearestBase(currentX, currentY, allCommands.size());
			if (!op.isPresent()) {
				// Error - no bases are available
				System.err.println("No bases available for " + r.getIdentity() + ".");
				break outer;
			}
			LinkedList<Command> commands = op.get().getItem2();
			Junction base = op.get().getItem1();
			allCommands.addAll(commands);
			currentX = base.getX();
			currentY = base.getY();
			weight = 0;
			allCommands.add(new Command(CommandType.COMPLETE_JOB));
		}
		
		// Reserve all commands
		reserve.reservePositions(new Junction(currentX, currentY), allCommands, 0);
		
		return allCommands;
	}
	
	private Optional<Pair<Junction, LinkedList<Command>>> routeToNearestBase(int fromX, int fromY, int currentTime) {
		LinkedList<Command> commands = null;
		Junction selectedBase = null;
		for (Junction base : getSortedBases(fromX, fromY)) {
			Optional<LinkedList<Command>> oc =
				calculateCommands(new Junction(fromX, fromY), base, currentTime);
			
			if (oc.isPresent()) {
				commands = oc.get();
				selectedBase = base;
				break;
			}
		}
		
		if (commands == null) {
			return Optional.empty();
		} else {
			return Optional.of(Pair.makePair(selectedBase, commands));
		}
	}
	
	private ArrayList<Junction> getSortedBases(int _fromX, int _fromY) {
		Junction from = new Junction(_fromX, _fromY);
		bases.sort(new Comparator<Junction>() {
			private double getDistSq(Junction a, Junction b) {
				double xdiff = a.getX() - b.getX();
				double ydiff = a.getY() - b.getY();
				return (xdiff*xdiff) + (ydiff*ydiff);
			}
			@Override
			public int compare(Junction _o1, Junction _o2) {
				double dist1 = getDistSq(from, _o1);
				double dist2 = getDistSq(from, _o2);
				if (dist1 < dist2) {
					return -1;
				} else if (dist1 > dist2) {
					return 1;
				} else {
					return 0;
				}
			}
		});
		return bases;
	}
	
	// Calculates commands necessary to get between two junctions.
	public Optional<LinkedList<Command>> calculateCommands(Junction _from, Junction _to, int time) {
		if (_from == null || _to == null) {
			return Optional.empty();
		}
		
		Junction from = map.getJunction(_from.getX(), _from.getY());
		Junction to = map.getJunction(_to.getX(), _to.getY());
		
		if (from == null || to == null) {
			return Optional.empty();
		}
		
		// Find route using A*
		return multiFinder.findRoute(from, to, reserve, time);
	}
}
