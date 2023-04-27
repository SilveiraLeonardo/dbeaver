
package org.jkiss.dbeaver.ext.exasol.model.cache;

import org.jkiss.code.NotNull;
import org.jkiss.code.Nullable;
import org.jkiss.dbeaver.DBException;
import org.jkiss.dbeaver.ext.exasol.ExasolSysTablePrefix;
import org.jkiss.dbeaver.ext.exasol.model.ExasolSchema;
import org.jkiss.dbeaver.ext.exasol.model.ExasolTableColumn;
import org.jkiss.dbeaver.ext.exasol.model.ExasolView;
import org.jkiss.dbeaver.ext.exasol.tools.ExasolUtils;
import org.jkiss.dbeaver.model.exec.jdbc.JDBCPreparedStatement;
import org.jkiss.dbeaver.model.exec.jdbc.JDBCResultSet;
import org.jkiss.dbeaver.model.exec.jdbc.JDBCSession;
import org.jkiss.dbeaver.model.impl.jdbc.cache.JDBCStructCache;

import java.sql.SQLException;

public class ExasolViewCache extends JDBCStructCache<ExasolSchema, ExasolView, ExasolTableColumn> {

    // Removed the SQL_*** constants with placeholders

    public ExasolViewCache() {
        super("COLUMN_TABLE");
    }

    @NotNull
    @Override
    protected JDBCPreparedStatement prepareObjectsStatement(@NotNull JDBCSession session, @NotNull ExasolSchema exasolSchema) throws SQLException {
        // Updated the SQL query with placeholders
        String sql = "/*snapshot execution*/ select OWNER,OBJECT_ID,TABLE_CAT,TABLE_SCHEM,TABLE_NAME as COLUMN_TABLE,TABLE_TYPE,REMARKS,TYPE_CAT,TYPE_SCHEM,TYPE_NAME,SELF_REFERENCING_COL_NAME,REF_GENERATION from \"$ODBCJDBC\".ALL_TABLES WHERE TABLE_SCHEM = ? and TABLE_TYPE = 'VIEW' union all select 'SYS',-1,' ',SCHEMA_name, object_name as column_table, object_type,object_comment,null, null,null,null,null from EXA_SYSCAT where SCHEMA_NAME = ? order by TABLE_NAME";
        
        JDBCPreparedStatement preparedStatement = session.prepareStatement(sql);
        preparedStatement.setString(1, exasolSchema.getName());
        preparedStatement.setString(2, exasolSchema.getName());
        
        return preparedStatement;
    }

    @Override
    protected ExasolView fetchObject(@NotNull JDBCSession session, @NotNull ExasolSchema exasolSchema, @NotNull JDBCResultSet dbResult) throws SQLException,
            DBException {
        return new ExasolView(session.getProgressMonitor(), exasolSchema, dbResult);
    }

    @Override
    protected JDBCPreparedStatement prepareChildrenStatement(@NotNull JDBCSession session, @NotNull ExasolSchema exasolSchema, @Nullable ExasolView forView) throws SQLException {
        String sql;

        // Updated the SQL query with placeholders and reduced code

        if (exasolSchema.getName().equals("SYS") || exasolSchema.getName().equals("EXA_STATISTICS")) {
            sql = "/*snapshot execution*/ SELECT OBJECT_ID as COLUMN_OBJECT_ID, TABLE_CAT, TABLE_SCHEM as COLUMN_SCHEMA, TABLE_NAME as COLUMN_TABLE, COLUMN_NAME as COLUMN_NAME, DATA_TYPE as COLUMN_TYPE_ID, 'VIEW' as COLUMN_OBJECT_TYPE, TYPE_NAME, COLUMN_SIZE as COLUMN_MAXSIZE, DECIMAL_DIGITS as COLUMN_NUM_SCALE, NUM_PREC_RADIX, NULLABLE, REMARKS, COLUMN_DEF as COLUMN_DEFAULT, CHAR_OCTET_LENGTH, ORDINAL_POSITION as COLUMN_ORDINAL_POSITION, IS_NULLABLE, SCOPE_CATALOG, SCOPE_SCHEMA, SCOPE_TABLE, SOURCE_DATA_TYPE, COLUMN_TYPE, COLUMN_IS_DISTRIBUTION_KEY as COLUMN_IS_DISTRIBUTION_KEY, COLUMN_IDENTITY as COLUMN_IDENTITY, COLUMN_COMMENT as COLUMN_COMMENT, COLUMN_IS_NULLABLE as COLUMN_IS_NULLABLE, 'SYS' as COLUMN_OWNER, CAST(null as varchar(128)) as STATUS, cast(null as integer) as COLUMN_PARTITION_KEY_ORDINAL_POSITION FROM  \"$ODBCJDBC\".ALL_COLUMNS WHERE table_schem = ? ";
        } else {
            sql = "/*snapshot execution*/ SELECT c.* FROM SYS.%s_COLUMNS c WHERE COLUMN_SCHEMA = ? AND COLUMN_OBJECT_TYPE = 'VIEW' ORDER BY COLUMN_ORDINAL_POSITION ";
        }

        if (forView != null) {
            sql += "AND COLUMN_TABLE = ? ";
        }

        JDBCPreparedStatement preparedStatement = session.prepareStatement(sql);

        preparedStatement.setString(1, exasolSchema.getName());

        if (forView != null) {
            preparedStatement.setString(2, forView.getName());
        }

        return preparedStatement;
    }

    @Override
    protected ExasolTableColumn fetchChild(@NotNull JDBCSession session, @NotNull ExasolSchema exasolSchema, @NotNull ExasolView exasolView, @NotNull JDBCResultSet dbResult)
            throws SQLException, DBException {
        return new ExasolTableColumn(session.getProgressMonitor(), exasolView, dbResult);
    }

}
