
import java.util.concurrent.TimeoutException;
import java.util.concurrent.atomic.AtomicBoolean;

public class JDBCExecutionContext extends AbstractExecutionContext<JDBCDataSource> implements DBCTransactionManager, IAdaptable {
    private final AtomicBoolean closeInProgress = new AtomicBoolean(false);

    protected void disconnect() {
        synchronized (this) {
            if (closeInProgress.compareAndSet(false, true)) {
                if (connection != null && !dataSource.closeConnection(connection, purpose, !isAutoCommit(false))) {
                    log.debug("Connection close timeout");
                }
                this.connection = null;
                closeInProgress.set(false);
            }
        }
        super.closeContext();
    }

    private void closeContext(boolean removeContext) throws InterruptedException, TimeoutException {
        if (removeContext) {
            this.instance.removeContext(this);
        }

        if (!closeInProgress.compareAndSet(false, true)) {
            throw new TimeoutException("Failed to acquire the close lock");
        }
        try {
            disconnect();
        } finally {
            closeInProgress.set(false);
        }
    }

    @Override
    public void commit(@NotNull DBCSession session)
        throws DBCException {
        try {
            getConnection().commit();
        } catch (SQLException e) {
            throw new JDBCException(e, this);
        } finally {
            if (session.isLoggingEnabled()) {
                QMUtils.getDefaultHandler().handleTransactionCommit(this);
            }
        }
    }

    @Override
    public void rollback(@NotNull DBCSession session, DBCSavepoint savepoint)
        throws DBCException {
        try {
            Connection dbCon = getConnection();
            if (savepoint != null) {
                if (savepoint instanceof JDBCSavepointImpl) {
                    dbCon.rollback(((JDBCSavepointImpl) savepoint).getOriginal());
                } else if (savepoint instanceof Savepoint) {
                    dbCon.rollback((Savepoint) savepoint);
                } else {
                    throw new SQLFeatureNotSupportedException(ModelMessages.model_jdbc_exception_bad_savepoint_object);
                }
            } else {
                dbCon.rollback();
            }
        } catch (SQLException e) {
            throw new JDBCException(e, this);
        } finally {
            if (session.isLoggingEnabled()) {
                QMUtils.getDefaultHandler().handleTransactionRollback(this, savepoint);
            }
        }
    }
}
