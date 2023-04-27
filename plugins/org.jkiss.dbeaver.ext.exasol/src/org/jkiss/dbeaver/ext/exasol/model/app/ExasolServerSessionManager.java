
import org.jkiss.dbeaver.DBException;
import org.jkiss.dbeaver.Log;
import org.jkiss.dbeaver.ext.exasol.model.ExasolDataSource;
import org.jkiss.dbeaver.model.DBPDataSource;
import org.jkiss.dbeaver.model.admin.sessions.DBAServerSessionManager;
import org.jkiss.dbeaver.model.admin.sessions.DBAServerSessionManagerSQL;
import org.jkiss.dbeaver.model.exec.DBCSession;
import org.jkiss.dbeaver.model.exec.jdbc.JDBCResultSet;
import org.jkiss.dbeaver.model.exec.jdbc.JDBCSession;
import org.jkiss.dbeaver.model.exec.jdbc.JDBCStatement;

import java.sql.PreparedStatement;
import java.sql.SQLException;
import java.util.ArrayList;
import java.util.Collection;
import java.util.List;
import java.util.Map;

public class ExasolServerSessionManager implements DBAServerSessionManager<ExasolServerSession>, DBAServerSessionManagerSQL {

    public static final String PROP_KILL_QUERY = "killQuery";
    private static final String KILL_APP_CMD = "kill session ?";
    private static final String KILL_STMT_CMD = "kill statement in session ?";

    private static final Log log = Log.getLog(ExasolServerSessionManager.class);

    // list sessions
    private static final String SESS_DBA_QUERY = "/*snapshot execution*/ select * from exa_dba_sessions";
    private static final String SESS_ALL_QUERY = "/*snapshot execution*/ select * from exa_ALL_sessions";

    private final ExasolDataSource dataSource;

    public ExasolServerSessionManager(ExasolDataSource dataSource) {
        this.dataSource = dataSource;
    }

    @Override
    public DBPDataSource getDataSource() {
        return this.dataSource;
    }

    @Override
    public Collection<ExasolServerSession> getSessions(DBCSession session, Map<String, Object> options)
        throws DBException {
        try {
            return readSessions((JDBCSession) session);
        } catch (SQLException e) {
            throw new DBException(e, session.getDataSource());
        }
    }

    @Override
    public void alterSession(DBCSession session, ExasolServerSession sessionType, Map<String, Object> options)
        throws DBException {
        try {
            String cmd;
            if (Boolean.TRUE.equals(options.get(PROP_KILL_QUERY))) {
                cmd = KILL_STMT_CMD;
            } else {
                cmd = KILL_APP_CMD;
            }
            PreparedStatement dbStat = ((JDBCSession) session).prepareStatement(cmd);
            dbStat.setInt(1, sessionType.getSessionID());

            dbStat.execute();
        } catch (SQLException e) {
            throw new DBException(e, session.getDataSource());
        }
    }

    public static Collection<ExasolServerSession> readSessions(JDBCSession session) throws SQLException {
        log.debug("read sessions");

        List<ExasolServerSession> listSessions = new ArrayList<>();

        //check dba view
        try (JDBCStatement dbStat = session.createStatement()) {
            try (JDBCResultSet dbResult = dbStat.executeQuery(SESS_DBA_QUERY)) {
                while (dbResult.next()) {
                    listSessions.add(new ExasolServerSession(dbResult));
                }
            }

        } catch (SQLException e) {
            //now try all view
            try (JDBCStatement dbStat = session.createStatement()) {
                try (JDBCResultSet dbResult = dbStat.executeQuery(SESS_ALL_QUERY)) {
                    while (dbResult.next()) {
                        listSessions.add(new ExasolServerSession(dbResult));
                    }
                }
            }
        }

        return listSessions;
    }

    @Override
    public boolean canGenerateSessionReadQuery() {
        return true;
    }

    @Override
    public String generateSessionReadQuery(Map<String, Object> options) {
        return SESS_ALL_QUERY;
    }
}
