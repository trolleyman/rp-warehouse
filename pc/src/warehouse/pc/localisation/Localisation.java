package warehouse.pc.localisation;

import warehouse.pc.shared.Direction;
import warehouse.pc.shared.Map;
/***
 *Class to represent localisation
 *
 *Still need :
 *  some sort of procedure to follow when "lost". Maybe rotate 360 degrees, read distance every 90 degrees
 *  and call distance recieved to update the probabilities matrix.
 *  if finished() returns true after this (should be unlikely, need to move around to get good probability),
 *  then we should use getLikelyX / getLikelyY as our new accepted x,y coords. If finished returns false,
 *  then we need to carry on with a (recursive?) procedure until we get enough information to determine location.
 * 
 *  This means this class should be able to send commands to the robot(s), or at least invoke another class to tell
 *  the robots to follow the localise procedure.
 * 
 *  NOTE : Still need to account for the fact the robot will be moving during localisation. - done I think?
 *  
 *  could use vectors of the movement to translate it back to origin so coords are consistent. - done
 *  
 *  using vectors means we can remove edges from our probability array. 
 *  i.e, If the robot has moved one to the right, it can't be next to the left wall.
 *  
 *  we could use this ^ fact to make the "lost" procedure. for every unit the robot can move, we cancel out
 *  lots of possible positions. (if it's always moving in the same direction)
 *  
 *  ** need to check distanceRecieved function to make sure translations are correct **
 *  
 * 
 */
public class Localisation 
{
	private Map map;
	private Boolean[][] probabilities;
	private int numValidLocations;
	private Direction robotFacing;
	private ArrayList<Direction> robotMovements = new ArrayList<>();
	
	// We don't know the starting location
	// Assume we know starting orientation

	public Localisation(Map _map,Direction dir) 
	{
		this.map = _map;
		this.probabilities = new Boolean[map.getHeight()][map.getWidth()];
		numValidLocations = 0;
		robotFacing = dir;
		
		for (int y = 0; y < map.getHeight(); y++)
		{
			for (int x = 0; x < map.getWidth(); x++) 
			{
				if (map.getJunction(x, y) != null) 
				{
					numValidLocations += 1;
				}
			}
		}
		
		for (int y = 0; y < map.getHeight(); y++) 
		{
			for (int x = 0; x < map.getWidth(); x++) 
			{
				if (map.getJunction(x, y) != null) 
				{
					//so it can be in any valid location on the map initially
					probabilities[y][x] = true;
				}
				else
					//it can't be on a wall or junction node.
					probabilities[y][x] = false;
				}
			}
		}
	}
	
	public void distanceRecieved(int _dist) 
	{
		// Update probabilities at a junction. set probabilities [y][x] to false if not possible, to narrow down.
		for (int y = 0; y < map.getHeight(); y++) 
		{
			for (int x = 0; x < map.getWidth(); x++) 
			{
				if (probabilities[y-getYTranslation()][x-getXTranslation()])
				{
					if (!map.getProbability(x-getXTranslation(),y-getYTranslation(),robotFacing,_dist))
					{
						//if a previously 'true' position is now false, don't let it stay true.
						probabilities[y-getYTranslation()][x-getXTranslation()] = false;
					}
				}
				//converse of above doesn't matter. If it is false, we shouldn't let it become true.
			}
		}
	}
	
	/**
	 * should be called every time the robot changed location.
	 * one 'move' would be one junction away in a certain direction
	 * @param dir the direction the robot was moved in.
	 */
	public void robotMoved(Direction dir)
	{
		robotMovements.add(dir);
	}
	
	public void robotRotatedTo(Direction dir)
	{
		this.robotFacing = dir;
	}
	
	/**
	 * gets the horizontal displacement of the robot from where it started when this class was instantiated
	 */
	public int getXTranslation()
	{
		int xTranslation = 0;
		for (Direction dir : robotMovements)
		{
			if (dir == Direction.X_POS)
			{
				xTranslation++;
			}
			else if (dir == Direction.X_NEG)
			{
				xTranslation--;
			}
			else
			{
				continue;
			}
		}
		return xTranslation;
	}
	
	/**
	 * gets the vertical displacement of the robot from where it started when this class was instantiated
	 */
	public int getYTranslation()
	{
		int yTranslation = 0;
		for (Direction dir : robotMovements)
		{
			if (dir == Direction.Y_POS)
			{
				yTranslation++;
			}
			else if (dir == Direction.Y_NEG)
			{
				yTranslation--;
			}
			else
			{
				continue;
			}
		}
		return yTranslation;
	}
	
	public boolean isFinished() 
	{
		int count = 0;
		for (int y = 0; y < map.getHeight(); y++)
		{
			for (int x = 0; x < map.getWidth(); x++)
			{
				if (probabilities[y][x])
				{
					count++;
				}
			}
		}
		//only return finished when there is one possible location left
		return (count > 1? false : true);
	}
	
	public int getLikelyX() 
	{
		//get the first x coordinate of the array that has a true value
		for (int y = 0;y < map.getHeight();y++)
		{
			for (int x = 0; x < map.getWidth();x++)
			{
				if (probabilities[y][x])
				{
					return x;
				}
			}
		}
		return -1; //this SHOULDN'T happen (but could)
	}
	public int getLikelyY() 
	{
		// Get the first y coordinate of the array that has a true value
		for (int y = 0;y < map.getHeight();y++)
		{
			for (int x = 0; x < map.getWidth();x++)
			{
				if (probabilities[y][x])
				{
					return y;
				}
			}
		}
		return -1; //this SHOULDN'T happen (but could)
	}
}
