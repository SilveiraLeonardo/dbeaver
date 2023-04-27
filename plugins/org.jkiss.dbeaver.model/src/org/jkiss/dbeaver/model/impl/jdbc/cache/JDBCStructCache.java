
public synchronized void loadChildren(DBRProgressMonitor monitor, OWNER owner, @Nullable final OBJECT forObject) throws DBException
{
    // ...
    synchronized (childrenCache) {
        // Original method code...
    }
}
