package warehouse.pc.shared;

public class RobotIdentity implements Comparable<RobotIdentity> {
	public final String name;	// Name of the Robot
	public final String id;		// ID of the Robot
	
	public RobotIdentity(String _name, String _id) {
		this.name = _name;
		this.id = _id;
	}
	
	@Override
	public boolean equals(Object o) {
		return o instanceof RobotIdentity
				&& ((RobotIdentity) o).name.equals(this.name)
				&& ((RobotIdentity) o).id.equals(this.id);
	}
	
	@Override
	public int hashCode() {
		return name.hashCode() + id.hashCode();
	}
	
	@Override
	public String toString() {
		return name + " (" + id + ")";
	}

	@Override
	public int compareTo(RobotIdentity o) {
		int res = this.name.compareToIgnoreCase(o.name);
		if (res == 0)
			return this.id.compareToIgnoreCase(o.id);
		return res;
	}
}
