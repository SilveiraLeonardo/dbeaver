
package org.jkiss.dbeaver.ext.postgresql.model.data;

import org.jkiss.code.NotNull;
import org.jkiss.code.Nullable;
import org.jkiss.dbeaver.Log;
import org.jkiss.dbeaver.model.DBUtils;
import org.jkiss.dbeaver.model.data.DBDCursor;
import org.jkiss.dbeaver.model.exec.DBCException;
import org.jkiss.dbeaver.model.exec.DBCResultSet;
import org.jkiss.dbeaver.model.exec.DBCSession;
import org.jkiss.dbeaver.model.exec.DBCTransactionManager;
import org.jkiss.dbeaver.model.exec.jdbc.JDBCSession;
import org.jkiss.dbeaver.model.exec.jdbc.JDBCStatement;
import org.jkiss.dbeaver.model.impl.jdbc.JDBCUtils;

import java.sql.PreparedStatement;
import java.sql.SQLException;

public class PostgreRefCursor implements DBDCursor {

    private static final Log log = Log.getLog(PostgreRefCursor.class);

    private final JDBCSession session;
    private String cursorName;
    private boolean isOpen;
    private JDBCStatement cursorStatement;

    public PostgreRefCursor(JDBCSession session, @NotNull String cursorName) throws SQLException {
        this.session = session;
        this.cursorName = cursorName;
        this.isOpen = true;
    }

    @Override
    public Object getRawValue() {
        return cursorName;
    }

    @Override
    public boolean isNull() {
        return false;
    }

    @Override
    public boolean isModified() {
        return false;
    }

    @Override
    public void release() {
        if (this.isOpen) {
            try {
                PreparedStatement preparedStatement = session.prepareStatement("CLOSE ?");
                preparedStatement.setString(1, cursorName);
                preparedStatement.execute();
            } catch (Exception e) {
                log.error("Error closing the \"" + cursorName + "\" cursor", e);
            }
        }
        if (cursorStatement != null) {
            cursorStatement.close();
            cursorStatement = null;
        }
    }

    @Override
    public DBCResultSet openResultSet(DBCSession session) throws DBCException {
        try {
            DBCTransactionManager txnManager = DBUtils.getTransactionManager(session.getExecutionContext());
            if (txnManager != null && txnManager.isAutoCommit()) {
                throw new DBCException("Ref cursors are not available in auto-commit mode");
            }
            if (cursorStatement != null) {
                cursorStatement.close();
            }
            PreparedStatement preparedStatement = this.session.prepareStatement("MOVE ABSOLUTE 0 IN ?");
            preparedStatement.setString(1, cursorName);
            preparedStatement.execute();

            preparedStatement = this.session.prepareStatement("FETCH ALL IN ?");
            preparedStatement.setString(1, cursorName);
            cursorStatement = (JDBCStatement) preparedStatement;
            return cursorStatement.executeQuery();
        } catch (SQLException e) {
            throw new DBCException(e, session.getExecutionContext());
        }
    }

    @Nullable
    @Override
    public String getCursorName() {
        return cursorName;
    }

    @Override
    public String toString() {
        return cursorName;
    }

}
