
int paramIndex = 1;
StringBuilder queryBuilder = new StringBuilder(
    "SELECT sql FROM " +
    (sourceObject.getParentObject() instanceof GenericSchema ?
        DBUtils.getQuotedIdentifier(sourceObject.getParentObject()) + "." : "") +
    "sqlite_master WHERE type=? AND tbl_name=?");
if (sourceObjectName != null) {
    queryBuilder.append(" AND name=?");
}
queryBuilder.append(
    "\n" + "UNION ALL\n" +
    "SELECT sql FROM " + "sqlite_temp_master WHERE type=? AND tbl_name=?");
if (sourceObjectName != null) {
    queryBuilder.append(" AND name=?");
}
queryBuilder.append("\n");

try (JDBCPreparedStatement dbStat = session.prepareStatement(queryBuilder.toString())) {
    // ...
}
