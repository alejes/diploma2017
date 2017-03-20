package kotlin;

public final class DynamicBindException extends RuntimeException {
    /* package */ DynamicBindException(String message) {
        super(message);
    }

    /* package */ DynamicBindException(String message, Throwable cause) {
        super(message, cause);
    }

    /* package */ DynamicBindException(Throwable cause) {
        super(cause);
    }
}
