
import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.SQLException;

@Override
protected void addObjectCreateActions(DBRProgressMonitor monitor, DBCExecutionContext executionContext, List<DBEPersistAction> actions, ObjectCreateCommand command, Map<String, Object> options) throws DBException {
    actions.add(new DBEPersistAction() {
        @Override
        public void execute(DBCExecutionContext executionContext, Throwable error) throws DBException {
            if (error != null) return;

            try (Connection connection = executionContext.getConnection().getOriginal()) {
                final String query = "INSERT INTO pgagent.pga_jobstep(jstjobid, jstname, jstdesc, jstenabled, jstkind, jstonerror, jstcode, jstconnstr, jstdbname) VALUES (?, ?, ?, ?, ?, ?, ?, ?, ?)";
                try (PreparedStatement statement = connection.prepareStatement(query)) {
                    PostgreJobStep step = command.getObject();
                    statement.setLong(1, step.getParentObject().getObjectId());
                    statement.setString(2, step.getName());
                    statement.setString(3, step.getDescription());
                    statement.setBoolean(4, step.isEnabled());
                    statement.setString(5, step.getKind().name());
                    statement.setString(6, step.getOnError().name());
                    statement.setString(7, step.getObjectDefinitionText(monitor, options));
                    statement.setString(8, step.getRemoteConnectionString());
                    statement.setString(9, step.getTargetDatabase() == null ? "" : step.getTargetDatabase().getName());
                    statement.executeUpdate();
                }
            } catch (SQLException e) {
                throw new DBException("Error executing create step SQL: " + e.getMessage(), e);
            }
        }
    });
}
