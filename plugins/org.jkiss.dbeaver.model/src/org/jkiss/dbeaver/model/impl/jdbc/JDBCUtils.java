
package org.jkiss.dbeaver.model.impl.jdbc;

// ... (other import statements)

import java.sql.SQLSyntaxErrorException;

public class JDBCUtils {
    // ... (rest of the code)

    @Nullable
    public static String safeGetString(ResultSet dbResult, String columnName, boolean throwException) throws SQLException {
        try {
            return dbResult.getString(columnName);
        } catch (Exception e) {
            if (throwException && e instanceof SQLException) {
                throw (SQLException) e;
            }
            debugColumnRead(dbResult, columnName, e);
            return null;
        }
    }

    // Similar changes for other safeGet* methods

    private static void debugColumnRead(ResultSet dbResult, String columnName, Exception error) {
        if (error instanceof SQLSyntaxErrorException) {
            log.warn("SQL syntax error while reading column '" + columnName + "'");
        } else {
            String colFullId = columnName;
            if (dbResult instanceof JDBCResultSet) {
                colFullId += ":" + ((JDBCResultSet) dbResult).getSession().getDataSource().getContainer().getId();
            }
            synchronized (badColumnNames) {
                final Integer errorCount = badColumnNames.get(colFullId);
                if (errorCount == null) {
                    log.debug("Can't get column '" + columnName + "': " + "Error occurred");
                }
                badColumnNames.put(colFullId, errorCount == null ? 0 : errorCount + 1);
            }
        }
    }

    // Add throwException parameter to other debugColumnRead method
}
