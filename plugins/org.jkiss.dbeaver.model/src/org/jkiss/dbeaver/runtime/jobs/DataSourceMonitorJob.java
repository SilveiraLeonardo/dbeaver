
import java.util.concurrent.ConcurrentHashMap;
import java.util.concurrent.ConcurrentSkipListSet;

public class DataSourceMonitorJob extends AbstractJob {
    private static final int MONITOR_INTERVAL = 3000;
    private static final long SYSTEM_SUSPEND_INTERVAL = 30000;

    private static final Log log = Log.getLog(DataSourceMonitorJob.class);

    private static final int MAX_FAILED_ATTEMPTS_BEFORE_DISCONNECT = 5;
    private static final int MAX_FAILED_ATTEMPTS_BEFORE_IGNORE = 10;

    private final DBPPlatform platform;
    private final Map<String, Long> checkCache = new ConcurrentHashMap<>();
    private final Set<String> pingCache = new ConcurrentSkipListSet<>();
    private long lastPingTime = -1;

    public DataSourceMonitorJob(DBPPlatform platform) {
        super("Keep-Alive monitor");
        setUser(false);
        setSystem(true);
        this.platform = platform;
    }

    // ... (rest of the code remains unchanged)

    private void checkDataSourceAlive(final DBPDataSourceContainer dataSourceDescriptor) {
        if (!dataSourceDescriptor.isConnected()) {
            return;
        }

        final String dsId = dataSourceDescriptor.getId();

        if (pingCache.contains(dsId)) {
            return;
        }

        // End long transactions
        if (dataSourceDescriptor.isAutoCloseTransactions() ||
            dataSourceDescriptor.getConnectionConfiguration().getCloseIdleInterval() > 0)
        {
            endIdleTransactions(dataSourceDescriptor);
        }

        // Perform keep alive request
        final int keepAliveInterval = dataSourceDescriptor.getConnectionConfiguration().getKeepAliveInterval();
        if (keepAliveInterval <= 0) {
            return;
        }

        final DBPDataSource dataSource = dataSourceDescriptor.getDataSource();
        if (dataSource == null) {
            return;
        }
        Long lastCheckTime = checkCache.get(dsId);
        if (lastCheckTime == null) {
            final Date connectTime = dataSourceDescriptor.getConnectTime();
            if (connectTime != null) {
                lastCheckTime = connectTime.getTime();
            }
        }
        if (lastCheckTime == null) {
            log.debug("Can't determine last check time for " + dsId);
            return;
        }
        long curTime = System.currentTimeMillis();
        if ((curTime - lastCheckTime) / 1000 > keepAliveInterval) {
            boolean disconnectOnError = false;
            int failedAttemptCount = KeepAlivePingJob.getFailedAttemptCount(dataSource);
            if (failedAttemptCount >= MAX_FAILED_ATTEMPTS_BEFORE_IGNORE) {
                return;
            }
            if (failedAttemptCount > MAX_FAILED_ATTEMPTS_BEFORE_DISCONNECT) {
                disconnectOnError = true;
            }
            final KeepAlivePingJob pingJob = new KeepAlivePingJob(dataSource, disconnectOnError);
            pingJob.addJobChangeListener(new JobChangeAdapter() {
                @Override
                public void done(IJobChangeEvent event) {
                    checkCache.put(dsId, System.currentTimeMillis());
                    pingCache.remove(dsId);
                }
            });

            pingCache.add(dsId);
            pingJob.schedule();
        }
    }

    // ... (rest of the code remains unchanged)
}
