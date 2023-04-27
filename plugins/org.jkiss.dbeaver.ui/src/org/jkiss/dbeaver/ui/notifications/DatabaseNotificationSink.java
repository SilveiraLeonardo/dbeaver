
private final Object notificationLock = new Object();

// Replace synchronized (DatabaseNotificationSink.class) with:
synchronized (notificationLock) {
    // ...
}
