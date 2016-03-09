package warehouse.shared;

import java.util.Optional;

/**
 * Directions the robot can execute in the field
 *
 */

public enum Command {
	LEFT,
	RIGHT,
	FORWARD,
	BACKWARD,
	PICK,
	DROP,
	WAIT;
	
	@Override
	public String toString() {
		switch (this) {
		case LEFT:     return "left";
		case RIGHT:    return "right";
		case FORWARD:  return "forward";
		case BACKWARD: return "backward";
		case PICK:     return "pick";
		case DROP:     return "drop";
		case WAIT:     return "wait";
		}
		
		System.err.println("Unknown Command");
		return null;
	}
	
	public Optional<Command> fromString(String msg) {
		switch (msg.toLowerCase()) {
		case "left":     return Optional.of(LEFT);
		case "right":    return Optional.of(RIGHT);
		case "forward":  return Optional.of(FORWARD);
		case "backward": return Optional.of(BACKWARD);
		case "pick":     return Optional.of(PICK);
		case "drop":     return Optional.of(DROP);
		case "wait":     return Optional.of(WAIT);
		}
		
		return Optional.empty();
	}
	
	/**
	 * Returns true if the robot will reply "ready" back to the server after it has completed the command.
	 */
	public boolean replyReady() {
		switch (this) {
		case LEFT:
		case RIGHT:
		case FORWARD:
		case BACKWARD:
			return true;
		case PICK:
		case DROP:
		case WAIT:
			return false; // For now the NXTs don't reply ready to these commands.
		}
		return false;
	}
	
	
}
