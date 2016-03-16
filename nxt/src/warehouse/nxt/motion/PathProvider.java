package warehouse.nxt.motion;

public interface PathProvider {
	public String getNextDirection();
	public boolean isFinished();
}
