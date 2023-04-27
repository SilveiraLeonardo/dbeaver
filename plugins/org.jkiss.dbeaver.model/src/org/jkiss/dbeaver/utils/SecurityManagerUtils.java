
public class CustomWrapperException extends RuntimeException {
    public CustomWrapperException(Throwable cause) {
        super(cause);
    }
}

public <T> T executeWithAccessControlContext(Callable<T> callable, AccessControlContext controlContext) {
    try {
        return AccessController.doPrivileged((PrivilegedAction<T>) () -> {
            try {
                return callable.call();
            } catch (Exception e) {
                throw new CustomWrapperException(e);
            }
        }, controlContext);
    } catch (Throwable e) {
        Throwable throwable = e;
        if (throwable instanceof CustomWrapperException && throwable.getCause() != null) {
            throwable = throwable.getCause();
        }
        throw throwable;
    }
}
