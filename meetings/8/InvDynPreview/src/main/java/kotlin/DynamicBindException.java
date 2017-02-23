package kotlin;

/**
 * Created by user on 2/23/17.
 */
public class DynamicBindException extends RuntimeException {
    public DynamicBindException(String message) {
        super(message);
    }

    public DynamicBindException(String message, Throwable cause) {
        super(message, cause);
    }

    public DynamicBindException(Throwable cause) {
        super(cause);
    }
}
