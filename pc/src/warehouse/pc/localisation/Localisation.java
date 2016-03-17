package warehouse.pc.localisation;

import warehouse.pc.shared.Direction;
import warehouse.pc.shared.Map;

public class Localisation {
	private static final double THRESHOLD = 0.9;
	private Map map;
	private Double[][] probabilities;
	private int numValidLocations;
	
	// We don't know the starting location
	public Localisation(Map _map) {
		this.map = _map;
		this.probabilities = new Float[map.getHeight()][map.getWidth()];
		numValidLocations = 0;
		for (int y = 0; y < map.getHeight(); y++) {
			for (int x = 0; x < map.getWidth(); x++) {
				if (map.getJunction(x, y) != null) {
					numValidLocations += 1;
				}
			}
		}
		
		double init = 1.0 / numValidLocations;
		for (int y = 0; y < map.getHeight(); y++) {
			for (int x = 0; x < map.getWidth(); x++) {
				if (map.getJunction(x, y) != null) {
					probabilities[y][x] = init;
				}
			}
		}
	}
	
	public void distanceRecieved(Direction dir, int _dist) {
		// Update probabilities at a junction
		for (int y = 0; y < map.getHeight(); y++) {
			for (int x = 0; x < map.getWidth(); x++) {
				// Get probability for x, y
				map.getProbability(x, y, dir, _dist);
			}
		}
	}
	
	public boolean isFinished() {
		// If one location is above THRESHOLD
		for (int y = 0;y < map.getHeight();y++)
		{
			for (int x = 0;x < width;x++)
			{
				if (probabilities[y][x] > THRESHOLD)
				{
					return true;
				}
			}
		}
		return false;
	}
	
	public int getLikelyX() {
		// Get maximum probability x-coordinate
		double max = 0.0;
		int likelyX = -1; //convention for unknown yet.
		for (int y = 0;y < map.getHeight();y++)
		{
			for (int x = 0; x < map.getWidth();x++)
			{
				if (probabilities[y][x] > max)
				{
					max = probabilities[y][x];
					likelyX = x;		
				}
			}
		}
		return likelyX;
	}
	public int getLikelyY() {
		// Get maximum probability y-coordinate
		double max = 0.0;
		int likelyY = -1; //convention for unknown yet.
		for (int y = 0;y < map.getHeight();y++)
		{
			for (int x = 0; x < map.getWidth();x++)
			{
				if (probabilities[y][x] > max)
				{
					max = probabilities[y][x];
					likelyY = y;		
				}
			}
		}
		return likelyY;
	}
}
