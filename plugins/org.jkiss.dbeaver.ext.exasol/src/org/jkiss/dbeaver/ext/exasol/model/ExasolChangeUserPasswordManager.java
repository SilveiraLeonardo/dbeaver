
package org.jkiss.dbeaver.ext.exasol.model;

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

public class ExasolChangeUserPasswordManager implements DBAUserPasswordManager {

    private ExasolDataSource dataSource;

    ExasolChangeUserPasswordManager(ExasolDataSource dataSource) {
        this.dataSource = dataSource;
    }

    @Override
    public void changeUserPassword(DBRProgressMonitor monitor, String userName, String newPassword, String oldPassword) throws DBException {
        try (JDBCSession session = DBUtils.openMetaSession(monitor, dataSource, "Change user password")) {
            session.enableLogging(false);

            String sql = "ALTER USER ? IDENTIFIED BY ? REPLACE ?";
            try (PreparedStatement preparedStatement = session.prepareStatement(sql)) {
                preparedStatement.setString(1, userName);

                if (!CommonUtils.isEmpty(newPassword)) {
                    preparedStatement.setString(2, newPassword);
                } else {
                    preparedStatement.setString(2, "");
                }

                if (!CommonUtils.isEmpty(oldPassword)) {
                    preparedStatement.setString(3, oldPassword);
                } else {
                    preparedStatement.setString(3, "");
                }

                preparedStatement.executeUpdate();
            } catch (SQLException e) {
                throw new DBCException("Error changing user password", e);
            }
        } catch (SQLException e) {
            throw new DBCException("Error opening session", e);
        }
    }
}
