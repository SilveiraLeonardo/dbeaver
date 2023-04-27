
import java.sql.PreparedStatement;
import org.jkiss.dbeaver.model.impl.jdbc.JDBCUtils;

public final class ExasolTableCache
        extends JDBCStructCache<ExasolSchema, ExasolTable, ExasolTableColumn> {

    // ... (keep the existing code until the prepareObjectsStatement method) ...

    @NotNull
    @Override
    protected PreparedStatement prepareObjectsStatement(
            @NotNull JDBCSession session, @NotNull ExasolSchema exasolSchema)
            throws SQLException
    {
        String sql = "/*snapshot execution*/ select OWNER,OBJECT_ID,TABLE_CAT,TABLE_SCHEM,TABLE_NAME as COLUMN_TABLE,TABLE_TYPE,REMARKS,TYPE_CAT,TYPE_SCHEM,TYPE_NAME,SELF_REFERENCING_COL_NAME,REF_GENERATION from \"$ODBCJDBC\".ALL_TABLES WHERE TABLE_SCHEM = ? and TABLE_TYPE = 'TABLE' order by TABLE_NAME";

        PreparedStatement pstmt = session.getConnection().prepareStatement(sql);
        pstmt.setString(1, exasolSchema.getName());

        return pstmt;
    }

    @SuppressWarnings("rawtypes")
    @Override
    protected PreparedStatement prepareChildrenStatement(
            @NotNull JDBCSession session, @NotNull ExasolSchema exasolSchema,
            @Nullable ExasolTable exasolTable) throws SQLException
    {

        String tablePrefix = exasolSchema.getDataSource().getTablePrefix(ExasolSysTablePrefix.ALL);
        String sql;

        if (exasolTable != null)
            sql = "/*snapshot execution*/ SELECT c.* FROM SYS." + tablePrefix + "_COLUMNS c WHERE COLUMN_SCHEMA = ? AND COLUMN_TABLE = ? ORDER BY COLUMN_ORDINAL_POSITION";
        else
            sql = "/*snapshot execution*/ SELECT c.* FROM SYS." + tablePrefix + "_COLUMNS c WHERE COLUMN_SCHEMA = ? AND COLUMN_OBJECT_TYPE = 'TABLE' ORDER BY COLUMN_ORDINAL_POSITION";

        PreparedStatement pstmt = session.getConnection().prepareStatement(sql);
        pstmt.setString(1, exasolSchema.getName());

        if (exasolTable != null) {
            pstmt.setString(2, exasolTable.getName());
        }

        return pstmt;
    }

    // ... (keep the existing fetchObject and fetchChild methods) ...

}
