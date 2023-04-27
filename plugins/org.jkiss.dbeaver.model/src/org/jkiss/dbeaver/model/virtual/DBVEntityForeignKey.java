
@NotNull
public DBSEntityConstraint getRealReferenceConstraint(@NotNull DBRProgressMonitor monitor) throws DBException {
    if (refEntityId == null) {
        throw new DBException("Reference entity ID not set for virtual foreign key");
    }
    DBNNode refNode = DBWorkbench.getPlatform().getNavigatorModel().getNodeByPath(monitor, refEntityId);
    if (!(refNode instanceof DBNDatabaseNode)) {
        throw new DBException("Unable to find reference node for virtual foreign key");
    }
    DBSObject object = ((DBNDatabaseNode) refNode).getObject();
    if (object instanceof DBSEntity) {
        List<DBSEntityConstraint> constraints = DBVUtils.getAllConstraints(monitor, (DBSEntity) object);
        DBSObject refEntityConstraint = DBUtils.findObject(constraints, refConstraintId);
        if (refEntityConstraint == null) {
            throw new DBException("Unable to find constraint in the entity");
        }
        return (DBSEntityConstraint) refEntityConstraint;
    } else {
        throw new DBException("Object is not an entity");
    }
}

@Nullable
@Override
public DBSEntityConstraint getReferencedConstraint() {
    try {
        return getRealReferenceConstraint(new VoidProgressMonitor());
    } catch (DBException e) {
        log.error("Failed to get the real referenced constraint");
        return null;
    }
}
