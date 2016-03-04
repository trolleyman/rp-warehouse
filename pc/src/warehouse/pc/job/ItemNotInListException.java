package warehouse.pc.job;

/**
 * Exception for when an item trying to be found is not in a required list.
 */
public class ItemNotInListException extends Exception {

	public ItemNotInListException(String message) {
		super(message);
	}
}
