package team.floracore.common.exception;

/**
 * 当目标物品堆不能应用数据值时抛出
 */
public class ItemStackNonApplicableDataValueException extends RuntimeException {
	public ItemStackNonApplicableDataValueException() {
		super();
	}

	public ItemStackNonApplicableDataValueException(String message) {
		super(message);
	}

	public ItemStackNonApplicableDataValueException(String message, Throwable cause) {
		super(message, cause);
	}

	public ItemStackNonApplicableDataValueException(Throwable cause) {
		super(cause);
	}
}
