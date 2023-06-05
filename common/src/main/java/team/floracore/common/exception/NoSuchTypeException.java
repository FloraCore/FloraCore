package team.floracore.common.exception;

public class NoSuchTypeException extends RuntimeException {
    public NoSuchTypeException() {
        super();
    }

    public NoSuchTypeException(String message) {
        super(message);
    }

    public NoSuchTypeException(String message, Throwable cause) {
        super(message, cause);
    }

    public NoSuchTypeException(Throwable cause) {
        super(cause);
    }
}
