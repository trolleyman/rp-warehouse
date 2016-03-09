package warehouse.nxt.motion;

import java.util.ArrayList;

public class SetPath implements PathProvider {
	
	private ArrayList<String> path;
	
	public SetPath( ArrayList<String> _path ) {
		this.path = _path;
	}
	
	@Override
	public String getNextDirection() {
		
		if( path.size() == 0 ) { return null; }
		
		String direction = this.path.get( 0 ); this.path.remove( 0 );
		return direction;
	}

	@Override
	public boolean isFinished() { return path.isEmpty(); }

}
