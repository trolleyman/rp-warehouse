package warehouse.planning;

public class Node implements Comparable{

	private int x;
	private int y;
	private int heuristic;
	private int pathCost;
	private int totalCost;
	private Node parent;
	private Node goal;
	private int[][] map;
	
	public Node(int _x, int _y, Node _parent, Node _goal, int _currentPathCost, WarehouseMap _map){
		
		this.x = _x;
		this.y = _y;
		
		this.parent = _parent;
		this.goal = _goal;
		
		int goalX = goal.getX();
		int goalY = goal.getY();
		
		int xDiff = Math.abs(x - goalX);
		int yDiff = Math.abs(y - goalY);

		this.heuristic = xDiff + yDiff;
		this.map = _map.getMap();
		this.pathCost = _currentPathCost + map[x][y];
		this.totalCost = heuristic + pathCost;
		

	}
	
	public int getTotalCost(){
		return totalCost;
	}
	
	public Node getParent(){
		return parent;
	}
	
	public int getX(){
		return x;
	}
	
	public int getY(){
		return y;
	}

	@Override
	public int compareTo(Object o) {
		if(((Node) o).getTotalCost() < this.getTotalCost()){
			return -1;
		}
		else if(((Node) o).getTotalCost() == this.getTotalCost()){
			return 0;
		}
		else if(((Node) o).getTotalCost() > this.getTotalCost()){
			return 1;
		}
		
		return 0;
	}
	
}
