
import org.jkiss.dbeaver.model.exec.DBCCallableStatement;

// ...

public class OracleToolsTableGatherStatistics extends SQLToolExecuteHandler<DBSObject, OracleToolTableGatherStatisticsSettings> {
    // ...
    
    @Override
    public void generateObjectQueries(DBCSession session, OracleToolTableGatherStatisticsSettings settings, List<DBEPersistAction> queries, DBSObject object) throws DBCException {
        if (object instanceof OracleTable) {
            OracleTable table = (OracleTable) object;
            int percent = settings.getSamplePercent();
            String sql = "{ CALL DBMS_STATS.GATHER_TABLE_STATS(OWNNAME => ?, TABNAME => ?, estimate_percent => ?) }";

            DBEPersistAction action = new DBEPersistAction(sql) {
                @Override
                public DBCCallableStatement prepareStatement(DBCSession session) throws DBCException {
                    try {
                        final CallableStatement callableStatement = session.get JDBCSession().prepareCall(getSQL());
                        callableStatement.setString(1, DBUtils.getQuotedIdentifier(table.getSchema()));
                        callableStatement.setString(2, DBUtils.getQuotedIdentifier(table));
                        callableStatement.setInt(3, percent);
                        return session.prepareStatement(callableStatement, DBCStatementType.UNKNOWN, getVersion());
                    } catch (SQLException e) {
                        throw new DBCException(e, session.getDataSource());
                    }
                }
            };

            queries.add(action);
        }
    }
}
