package team.floracore.common.exception;

public class NBTSyntaxException extends RuntimeException {
    public NBTSyntaxException() {
        super();
    }

    public NBTSyntaxException(String message) {
        super(message);
    }

    public NBTSyntaxException(String message, Throwable cause) {
        super(message, cause);
    }

    public NBTSyntaxException(Throwable cause) {
        super(cause);
    }
}
