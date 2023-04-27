
package org.jkiss.dbeaver.model.impl;

import org.jkiss.code.NotNull;
import org.jkiss.dbeaver.model.DBPExclusiveResource;
import org.jkiss.dbeaver.runtime.DBWorkbench;
import org.jkiss.dbeaver.utils.RuntimeUtils;

import java.util.HashMap;
import java.util.Map;
import java.util.concurrent.locks.ReentrantLock;

/**
 * Simple exclusive lock
 */
public class SimpleExclusiveLock implements DBPExclusiveResource {
    private static final String TASK_GLOBAL = "#global";

    private static class Lock {
        private volatile Thread lockThread;
        private int lockCount = 0;
        private final ReentrantLock internalLock = new ReentrantLock(true);
    }

    private final Map<String, Lock> locks = new HashMap<>();

    @Override
    public Object acquireExclusiveLock() {
        return acquireTaskLock(TASK_GLOBAL, false);
    }

    @Override
    public Object acquireTaskLock(@NotNull String taskName, boolean checkDup) {
        Thread curThread = Thread.currentThread();
        Lock lock;
        synchronized (this) {
            lock = locks.get(taskName);
            if (lock == null) {
                lock = new Lock();
                locks.put(taskName, lock);
            }
        }

        boolean taskRunning = false;
        for (;;) {
            synchronized (this) {
                if (lock.lockThread == curThread || lock.lockThread == null) {
                    if (checkDup && taskRunning) {
                        return TASK_PROCESED;
                    }
                    lock.internalLock.lock(); // Acquire internal lock
                    lock.lockThread = curThread;
                    lock.lockCount++;
                    return curThread;
                }
            }
            taskRunning = true;
            // Wait for a while
            if (!DBWorkbench.getPlatformUI().readAndDispatchEvents()) {
                RuntimeUtils.pause(50);
            }
        }
    }

    @Override
    public void releaseExclusiveLock(@NotNull Object lock) {
        releaseTaskLock(TASK_GLOBAL, lock);
    }

    @Override
    public void releaseTaskLock(@NotNull String taskName, @NotNull Object lockObj) {
        synchronized (this) {
            Lock lock = locks.get(taskName);
            if (lock == null) {
                throw new IllegalArgumentException("Wrong task name: " + taskName);
            }

            if (lock.lockThread == null) {
                throw new IllegalStateException("Lock thread is null. Cannot release lock.");
            }

            if (lock.lockThread != lockObj) {
                throw new IllegalArgumentException("Wrong exclusive lock passed");
            }
            lock.lockCount--;
            if (lock.lockCount == 0) {
                lock.internalLock.unlock(); // Release internal lock
                lock.lockThread = null;
            } else if (lock.lockCount < 0) {
                throw new IllegalStateException("Internal error: negative lock count. Restart application.");
            }
        }
    }
}
