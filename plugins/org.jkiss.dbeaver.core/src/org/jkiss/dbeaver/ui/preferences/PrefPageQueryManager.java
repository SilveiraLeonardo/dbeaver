
import org.jkiss.dbeaver.ui.NumericVerifyListener; // Add this at the beginning of the file

textHistoryDays = UIUtils.createLabelText(storageSettings, CoreMessages.pref_page_query_manager_label_days_to_store_log, "", SWT.BORDER, new GridData(50, SWT.DEFAULT)); //$NON-NLS-2
textHistoryDays.addVerifyListener(new NumericVerifyListener()); // Add this line

textEntriesPerPage = UIUtils.createLabelText(viewSettings, CoreMessages.pref_page_query_manager_label_entries_per_page, "", SWT.BORDER, new GridData(50, SWT.DEFAULT)); //$NON-NLS-2
textEntriesPerPage.addVerifyListener(new NumericVerifyListener()); // Add this line
