package warehouse.nxt.motion;

import lejos.util.Delay;

/**
 * A dummy class to simulate a move taking place. The class should block until
 * the 'move' is completed.
 * 
 * @author Reece
 *
 */
public class Controller {

	public void doMove() {
		System.out.println("Doing a move");
		Delay.msDelay(3000);
	}
}
