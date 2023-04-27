
package org.jkiss.dbeaver.ext.db2.model.cache;

import org.jkiss.code.NotNull;
import org.jkiss.dbeaver.DBException;
import org.jkiss.dbeaver.ext.db2.model.fed.DB2RemoteServer;
import org.jkiss.dbeaver.ext.db2.model.fed.DB2RemoteServerOption;
import org.jkiss.dbeaver.model.exec.jdbc.JDBCPreparedStatement;
import org.jkiss.dbeaver.model.exec.jdbc.JDBCResultSet;
import org.jkiss.dbeaver.model.exec.jdbc.JDBCSession;
import org.jkiss.dbeaver.model.exec.jdbc.JDBCStatement;
import org.jkiss.dbeaver.model.impl.jdbc.cache.JDBCObjectCache;

import java.sql.SQLException;

/**
 * Cache for DB2 Federated Remote Server Options
 * 
 * @author Denis Forveille
 */
public class DB2RemoteServerOptionCache extends JDBCObjectCache<DB2RemoteServer, DB2RemoteServerOption> {

    private static final String SQL;

    static {
        SQL = "SELECT * " +
              "FROM SYSCAT.SERVEROPTIONS " +
              "WHERE SERVERNAME = ? " +
              "ORDER BY OPTION " +
              "WITH UR";
    }

    @NotNull
    @Override
    protected JDBCStatement prepareObjectsStatement(@NotNull JDBCSession session, @NotNull DB2RemoteServer remoteServer) throws SQLException
    {
        if (remoteServer.getName() == null || remoteServer.getName().isEmpty()) {
            throw new IllegalArgumentException("Remote server name must not be null or empty");
        }

        final JDBCPreparedStatement dbStat = session.prepareStatement(SQL);
        dbStat.setString(1, remoteServer.getName());

        return dbStat;
    }

    @Override
    protected DB2RemoteServerOption fetchObject(@NotNull JDBCSession session, @NotNull DB2RemoteServer remoteServer, @NotNull JDBCResultSet resultSet)
        throws SQLException, DBException
    {
        return new DB2RemoteServerOption(remoteServer, resultSet);
    }
}
