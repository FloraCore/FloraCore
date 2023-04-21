package team.floracore.common.exception;

public class NoSuchGameModeException extends RuntimeException {
    public NoSuchGameModeException() {
        super();
    }

    public NoSuchGameModeException(String message) {
        super(message);
    }

    public NoSuchGameModeException(String message, Throwable cause) {
        super(message, cause);
    }

    public NoSuchGameModeException(Throwable cause) {
        super(cause);
    }
}
