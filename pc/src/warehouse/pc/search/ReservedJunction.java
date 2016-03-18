package warehouse.pc.search;

import warehouse.pc.shared.Junction;

public class ReservedJunction {

	private Junction junction;
	private Integer timeStep;

	public ReservedJunction(Junction _junction, Integer _timeStep) {
		
		this.junction = _junction;
		this.timeStep = _timeStep;
		
	}
	
	public Junction getJunction()
	{
		return this.junction;
	}
	
	public Integer getTime()
	{
		return this.timeStep;
	}
}
