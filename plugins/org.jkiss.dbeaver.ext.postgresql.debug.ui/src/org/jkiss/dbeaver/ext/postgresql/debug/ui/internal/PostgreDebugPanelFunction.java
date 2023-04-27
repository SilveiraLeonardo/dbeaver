
// Import statements remain unchanged

public class PostgreDebugPanelFunction implements DBGConfigurationPanel {
    // Existing class members remain unchanged

    // A new method to validate the process ID input
    private boolean isProcessIdValid(String processId) {
        try {
            int pid = Integer.parseInt(processId);
            return pid > 0;
        } catch (NumberFormatException e) {
            return false;
        }
    }

    // Updated createPanel, changes are in the listener
    @Override
    public void createPanel(Composite parent, DBGConfigurationPanelContainer container) {
        // ...
        //{
        //    Group kindGroup = ...
            SelectionListener listener = new SelectionAdapter() {
                @Override
                public void widgetSelected(SelectionEvent e) {
                    processIdText.setEnabled(kindGlobal.getSelection());
                    parametersTable.setEnabled(kindLocal.getSelection());
                    container.updateDialogState();
                    container.setErrorMessage(null); // Clear error message
                }
            };
        // ...
        //} // end of createPanel method

    // Updated loadConfiguration, added error handling, updated processId validation
    @Override
    public void loadConfiguration(DBPDataSourceContainer dataSource, Map<String, Object> configuration) {
        // ...
        processIdText.setText(processId == null ? "" : processId.toString());

        boolean validProcessId = isProcessIdValid(processIdText.getText());
        if (!validProcessId) {
            container.setErrorMessage("Invalid process ID");
        } else {
            container.setErrorMessage(null);
        }
        // ...
    }

    // Updated saveConfiguration, added processId validation
    @Override
    public void saveConfiguration(DBPDataSourceContainer dataSource, Map<String, Object> configuration) {
        // ...
        if (isValid()) {
            configuration.put(PostgreDebugConstants.ATTR_ATTACH_PROCESS, processIdText.getText());
        } else {
            configuration.remove(PostgreDebugConstants.ATTR_ATTACH_PROCESS);
        }
        // ...
    }

    // Updated isValid method to also validate processId
    @Override
    public boolean isValid() {
        return selectedFunction != null && isProcessIdValid(processIdText.getText());
    }
}
