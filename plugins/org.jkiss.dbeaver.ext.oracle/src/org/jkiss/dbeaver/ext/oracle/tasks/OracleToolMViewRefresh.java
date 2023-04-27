
package org.jkiss.dbeaver.ext.oracle.tasks;

import org.jkiss.dbeaver.ext.oracle.model.OracleMaterializedView;
import org.jkiss.dbeaver.model.DBPEvaluationContext;
import org.jkiss.dbeaver.model.edit.DBEPersistAction;
import org.jkiss.dbeaver.model.exec.DBCException;
import org.jkiss.dbeaver.model.exec.DBCSession;
import org.jkiss.dbeaver.model.impl.edit.SQLDatabasePersistAction;
import org.jkiss.dbeaver.model.sql.task.SQLToolExecuteHandler;

import java.sql.CallableStatement;
import java.sql.SQLException;
import java.util.List;

public class OracleToolMViewRefresh extends SQLToolExecuteHandler<OracleMaterializedView, OracleToolMViewRefreshSettings> {
    @Override
    public OracleToolMViewRefreshSettings createToolSettings() {
        return new OracleToolMViewRefreshSettings();
    }

    @Override
    public void generateObjectQueries(DBCSession session, OracleToolMViewRefreshSettings settings, List<DBEPersistAction> queries, OracleMaterializedView object) throws DBCException {
        String method = "";
        if (settings.isFast()) method += "f";
        if (settings.isForce()) method += "?";
        if (settings.isComplete()) method += "c";
        if (settings.isAlways()) method += "a";
        if (settings.isRecomputed()) method += "p";

        // Using CallableStatement to avoid SQL injection
        String sql = "CALL DBMS_MVIEW.REFRESH(?, ?)";

        try (CallableStatement stmt = session.getExecutionContext().getConnection().prepareCall(sql)) {
            stmt.setString(1, object.getFullyQualifiedName(DBPEvaluationContext.DDL));
            stmt.setString(2, method);
            stmt.execute();
        } catch (SQLException e) {
            throw new DBCException("Error executing refresh: " + e.getMessage(), e);
        }
    }

    public boolean needsRefreshOnFinish() {
        return true;
    }
}
