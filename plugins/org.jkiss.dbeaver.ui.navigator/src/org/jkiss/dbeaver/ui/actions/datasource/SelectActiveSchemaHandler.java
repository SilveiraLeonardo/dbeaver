
import org.eclipse.core.runtime.jobs.Job;
import org.eclipse.swt.widgets.Display;

public class SelectActiveSchemaHandler extends AbstractDataSourceHandler implements IElementUpdater {

    @Override
    public void updateElement(UIElement element, Map parameters) {
        Display.getDefault().asyncExec(new Runnable() {
            @Override
            public void run() {
                element.setText(schemaName);
                element.setIcon(DBeaverIcons.getImageDescriptor(schemaIcon));
                element.setTooltip(schemaTooltip);
            }
        });
    }

    public static class MenuContributor extends DataSourceMenuContributor {

        @Override
        protected void fillContributionItems(List<IContributionItem> menuItems) {
            Job readDatabaseListJob = Job.create("Read database list", monitor -> {
                contextDefaultObjectsReader.run(monitor);
            });
            readDatabaseListJob.setSystem(true);
            readDatabaseListJob.setUser(false);
            readDatabaseListJob.schedule(DB_LIST_READ_TIMEOUT);

            try {
                readDatabaseListJob.join();
            } catch (InterruptedException e) {
                log.error("Error while reading database list: ", e);
                return;
            }
        }
    }
}
