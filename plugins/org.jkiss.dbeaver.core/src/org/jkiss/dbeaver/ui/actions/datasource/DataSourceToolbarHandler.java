
package org.jkiss.dbeaver.ui.actions.datasource;

// omitted imports for brevity

import org.eclipse.ui.IActionBars;
import org.eclipse.ui.IEditorActionBarContributor;
import org.eclipse.ui.IEditorPart;
import org.eclipse.ui.IPerspective;
import org.eclipse.ui.IWorkbenchPage;
import org.eclipse.ui.IWorkbenchPart;
import org.eclipse.ui.IWorkbenchWindow;

public class DataSourceToolbarHandler implements DBPRegistryListener, DBPEventListener, INavigatorListener {

    private final List<DBPDataSourceRegistry> handledRegistries = new ArrayList<>();
    private final IWorkbenchWindow workbenchWindow;
    private IWorkbenchPart activePart;
    private IPageListener pageListener;
    private IPartListener partListener;

    // Rest of the class implementation remains the same

    @Override
    public void handleDataSourceEvent(final DBPEvent event) {
        if (workbenchWindow.getWorkbench().isClosing()) {
            return;
        }
        // Rest of the handleDataSourceEvent implementation remains the same
    
        // Modification: Update action bars through a non-internal approach
        if (event.getAction() == DBPEvent.Action.OBJECT_UPDATE && event.getEnabled() != null) {
            UIUtils.asyncExec(() -> {
                IWorkbenchPage activePage = workbenchWindow.getActivePage();
                if (activePage != null) {
                    IEditorPart editor = activePage.getActiveEditor();
                    if (editor != null) {
                        IEditorActionBarContributor contributor = activePage.getEditorActionBarContributor();
                        if (contributor != null) {
                            IActionBars actionBars = contributor.getActionBars();
                            if (actionBars != null) {
                                actionBars.updateActionBars();
                            }
                        }
                    }
                }
            });
        }

        UIUtils.asyncExec(DataSourceToolbarUtils::triggerRefreshReadonlyElement);
    }

    // Rest of the class implementation remains the same
}
