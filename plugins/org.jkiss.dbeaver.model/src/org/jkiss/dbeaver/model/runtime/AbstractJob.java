
package org.jkiss.dbeaver.model.runtime;

import org.eclipse.core.runtime.IProgressMonitor;
import org.eclipse.core.runtime.IStatus;
import org.eclipse.core.runtime.MultiStatus;
import org.eclipse.core.runtime.Status;
import org.eclipse.core.runtime.jobs.Job;
import org.jkiss.dbeaver.DBException;
import org.jkiss.dbeaver.Log;
import org.jkiss.dbeaver.ModelPreferences;
import org.jkiss.dbeaver.model.DBPDataSource;
import org.jkiss.dbeaver.model.exec.DBCSession;
import org.jkiss.dbeaver.model.preferences.DBPPreferenceStore;
import org.jkiss.dbeaver.utils.GeneralUtils;
import org.jkiss.dbeaver.utils.RuntimeUtils;
import org.jkiss.utils.CommonUtils;

import java.util.ArrayList;
import java.util.List;

// ...

    protected abstract IStatus run(DBRProgressMonitor monitor);

    // Custom method for secure logging
    private void logSecureError(String message, Throwable e) {
        if (e instanceof DBException) {
            // It's a DBException, so log only the message, but not the stack trace
            log.error(message + ": " + e.getMessage());
        } else {
            // For other exceptions, log the stack trace as well
            log.error(message, e);
        }
    }

@Override
    protected final IStatus run(IProgressMonitor monitor)
    {
        // ...

        try {
            finished = false;
            RuntimeUtils.setThreadName(getName());

            IStatus result = this.run(progressMonitor);
            if (!logErrorStatus(result)) {
                if (!result.isOK() && result != Status.CANCEL_STATUS) {
                    logSecureError("Error running job '" + getName() + "' execution", result.getException());
                }
            }
            return result;
        } catch (Throwable e) {
            logSecureError("Error during job '" + getName() + "' execution", e);
            return GeneralUtils.makeExceptionStatus(e);
        } finally {
            // ...
        }
    }

    private boolean logErrorStatus(IStatus status) {
        if (status.getException() != null) {
            logSecureError("Error during job '" + getName() + "' execution", status.getException());
            return true;
        } else if (status instanceof MultiStatus) {
            for (IStatus cStatus : status.getChildren()) {
                if (logErrorStatus(cStatus)) {
                    return true;
                }
            }
        }
        return false;
    }

// ...
