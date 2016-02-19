package warehouse.pc.shared.robot;

public class Identity {

	public final String name;	// Name of the Robot
	public final String ID;		// ID of the Robot
	public final double xPos;	// Starting Position of the Robot ( horizontal axis )
	public final double yPos;	// Starting Position of the Robot ( vertical axis )
	
	public Identity( String _name, String _ID, double _xPos, double _yPos ) {
		this.xPos = _xPos;
		this.yPos = _yPos;
		this.name = _name;
		this.ID = _ID;
	}
	
}
