
package org.jkiss.dbeaver.ext.vertica.model;

import org.jkiss.dbeaver.DBException;
import org.jkiss.dbeaver.model.DBUtils;
import org.jkiss.dbeaver.model.access.DBAUserPasswordManager;
import org.jkiss.dbeaver.model.exec.DBCException;
import org.jkiss.dbeaver.model.exec.jdbc.JDBCSession;
import org.jkiss.dbeaver.model.impl.jdbc.JDBCUtils;
import org.jkiss.dbeaver.model.runtime.DBRProgressMonitor;
import org.jkiss.utils.CommonUtils;

import java.sql.PreparedStatement;
import java.sql.SQLException;

public class VerticaChangeUserPasswordManager implements DBAUserPasswordManager {

    private VerticaDataSource dataSource;

    VerticaChangeUserPasswordManager(VerticaDataSource dataSource) {
        this.dataSource = dataSource;
    }

    @Override
    public void changeUserPassword(DBRProgressMonitor monitor, String userName, String newPassword, String oldPassword) throws DBException {
        try (JDBCSession session = DBUtils.openMetaSession(monitor, dataSource, "Change user password")) {
            session.enableLogging(false);
            String quotedUserName = DBUtils.getQuotedIdentifier(dataSource, userName);
            String sql = "ALTER USER " + quotedUserName + " IDENTIFIED BY ? REPLACE ?";
            try (PreparedStatement stmt = session.prepareStatement(sql)) {
                stmt.setString(1, CommonUtils.notEmpty(newPassword));
                stmt.setString(2, CommonUtils.notEmpty(oldPassword));
                stmt.execute();
            } catch (SQLException e) {
                throw new DBCException("Error changing user password", e);
            }
        } catch (SQLException e) {
            throw new DBCException("Error establishing meta session", e);
        }
    }
}
