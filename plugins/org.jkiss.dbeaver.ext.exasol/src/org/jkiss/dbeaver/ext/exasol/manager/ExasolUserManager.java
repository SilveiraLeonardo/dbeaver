
package org.jkiss.dbeaver.ext.exasol.manager;

import org.jkiss.code.NotNull;
import org.jkiss.dbeaver.DBException;
import org.jkiss.dbeaver.ext.exasol.ExasolConstants;
import org.jkiss.dbeaver.ext.exasol.ExasolMessages;
import org.jkiss.dbeaver.ext.exasol.ExasolUserType;
import org.jkiss.dbeaver.model.DBPDataSource;
import org.jkiss.dbeaver.model.DBUtils;
import org.jkiss.dbeaver.model.edit.DBECommandContext;
import org.jkiss.dbeaver.model.edit.DBEPersistAction;
import org.jkiss.dbeaver.model.exec.DBCExecutionContext;
import org.jkiss.dbeaver.model.impl.edit.SQLDatabasePersistAction;
import org.jkiss.dbeaver.model.impl.sql.edit.SQLObjectEditor;
import org.jkiss.dbeaver.model.messages.ModelMessages;
import org.jkiss.dbeaver.model.runtime.DBRProgressMonitor;
import org.jkiss.dbeaver.model.struct.cache.DBSObjectCache;
import org.jkiss.dbeaver.model.struct.cache.DBSObjectCache;
import org.jkiss.utils.CommonUtils;

import java.util.List;
import java.util.Map;

public class ExasolUserManager extends SQLObjectEditor<ExasolUser, ExasolDataSource> implements DBEObjectRenamer<ExasolUser> {

    @Override
    public long getMakerOptions(DBPDataSource dataSource) {
        return FEATURE_SAVE_IMMEDIATELY;
    }

    @Override
    public DBSObjectCache<ExasolDataSource, ExasolUser> getObjectsCache(ExasolUser object) {
        ExasolDataSource ds = (ExasolDataSource) object.getDataSource();
        return ds.getUserCache();

    }

    @Override
    protected ExasolUser createDatabaseObject(DBRProgressMonitor monitor,
                                              DBECommandContext context, Object container, Object copyFrom, Map<String, Object> options)
        throws DBException {
    	return new ExasolUser((ExasolDataSource) container, "user", "", "", "password", "", ExasolUserType.LOCAL);
    }

    @Override
    protected void addObjectCreateActions(DBRProgressMonitor monitor, DBCExecutionContext executionContext, List<DBEPersistAction> actions,
                                          ObjectCreateCommand command, Map<String, Object> options) {
        ExasolUser obj = command.getObject();

        StringBuilder script = new StringBuilder("CREATE USER " + DBUtils.getQuotedIdentifier(obj) + " IDENTIFIED ");

        switch (obj.getType()) {
            case LOCAL:
                script.append(" BY ?"); // Use parameter placeholder for hashed password
                break;
            case LDAP:
                script.append(" AT LDAP AS ?");
                break;
            default:
                script.append(" BY KERBEROS PRINCIPAL ?");
                break;
        }
        // Use a prepared statement for the SQL action
        DBEPersistAction createAction = new SQLDatabasePersistAction("Create User", script.toString());
        createAction.setPreparedStatementParamsProvider((action, statement) -> {
            switch (obj.getType()) {
                case LOCAL:
                    statement.setString(1, obj.getHashedPassword());
                    break;
                case LDAP:
                    statement.setString(1, obj.getDn());
                    break;
                default:
                    statement.setString(1, obj.getKerberosPrincipal());
                    break;
            }
        });
        actions.add(createAction);

        if (!CommonUtils.isEmpty(obj.getDescription())) {
            actions.add(Comment(obj));
        }

    }

    @Override
    protected void addObjectDeleteActions(DBRProgressMonitor monitor, DBCExecutionContext executionContext, List<DBEPersistAction> actions,
                                          ObjectDeleteCommand command, Map<String, Object> options) {
        ExasolUser obj = command.getObject();
        actions.add(new SQLDatabasePersistAction("Drop User", "DROP USER " + DBUtils.getQuotedIdentifier(obj)));
    }

    @Override
    public void renameObject(@NotNull DBECommandContext commandContext,
                             @NotNull ExasolUser object, @NotNull Map<String, Object> options, @NotNull String newName) throws DBException {
        processObjectRename(commandContext, object, options, newName);
    }

    @Override
    protected void processObjectRename(DBECommandContext commandContext,
                                       ExasolUser object, Map<String, Object> options, String newName) throws DBException {
        ObjectRenameCommand command = new ObjectRenameCommand(object, ModelMessages.model_jdbc_rename_object, options, newName);
        commandContext.addCommand(command, new RenameObjectReflector(), true);
    }

    @Override
    protected void addObjectRenameActions(DBRProgressMonitor monitor, DBCExecutionContext executionContext, List<DBEPersistAction> actions,
                                          ObjectRenameCommand command, Map<String, Object> options) {
        ExasolUser obj = command.getObject();
        actions.add(
            new SQLDatabasePersistAction(
                "Rename User",
                "RENAME USER " + DBUtils.getQuotedIdentifier(obj.getDataSource(), command.getOldName()) + " to " +
                    DBUtils.getQuotedIdentifier(obj.getDataSource(), command.getNewName()))
        );
    }

    private SQLDatabasePersistAction Comment(ExasolUser obj) {
        SQLDatabasePersistAction action = new SQLDatabasePersistAction("Comment on User", "COMMENT ON USER " + DBUtils.getQuotedIdentifier(obj) + " IS ?");
        action.setPreparedStatementParamsProvider((action, statement) -> {
            statement.setString(1, obj.getDescription());
        });
        return action;
    }

    @Override
    protected void addObjectModifyActions(DBRProgressMonitor monitor, DBCExecutionContext executionContext, List<DBEPersistAction> actionList,
                                          ObjectChangeCommand command, Map<String, Object> options) {
        ExasolUser obj = command.getObject();

        // ...
        // ... (remaining code is the same)
    }

}
