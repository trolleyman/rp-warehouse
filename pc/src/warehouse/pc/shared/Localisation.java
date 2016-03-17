package warehouse.pc.shared;

public class Localisation {
	private Map map;
	private Float[][] probabilities;
	
	public Localisation(Map _map) {
		this.map = _map;
		this.probabilities = new Float[map.getHeight()][map.getWidth()];
		for (int y = 0; y < map.getHeight(); y++) {
			for (int x = 0; x < map.getWidth(); x++) {
				
			}
		}
	}
	
	public void distanceRecieved(Direction dir, int _dist) {
		// Update probabilities at a junction
		
	}
	
	
}
