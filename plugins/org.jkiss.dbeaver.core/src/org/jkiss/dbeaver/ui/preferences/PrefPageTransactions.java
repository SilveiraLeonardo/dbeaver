
public class PrefPageTransactions extends PreferencePage {
	
	private Button autoCloseTransactionsButton;
	private Text autoCloseTransactionsTtlText;
	
	public PrefPageTransactions() {
		setTitle("Transactions");
	}
	
	@Override
	protected Control createContents(Composite parent) {
		GridComposite main = new GridComposite(parent, 2);
		
		autoCloseTransactionsButton = UIUtils.createCheckbox(main, "Auto-close transactions");
		autoCloseTransactionsTtlText = UIUtils.createIntegerInput(main, "Time To Live (seconds):");
		
		autoCloseTransactionsButton.addSelectionListener(new SelectionAdapter() {
			public void widgetSelected(SelectionEvent e) {
				autoCloseTransactionsTtlText.setEnabled(autoCloseTransactionsButton.getSelection());
			}
		});
		
		autoCloseTransactionsTtlText.addVerifyListener(UIUtils.getIntegerVerifyListener());
		
		loadPreferences();
		
		return main;
	}
	
	private void loadPreferences() {
		autoCloseTransactionsButton.setSelection(getPreferenceStore().getBoolean(PreferenceConstants.AUTO_CLOSE_TRANSACTIONS));
		autoCloseTransactionsTtlText.setText(String.valueOf(getPreferenceStore().getInt(PreferenceConstants.AUTO_CLOSE_TRANSACTIONS_TTL)));
		autoCloseTransactionsTtlText.setEnabled(autoCloseTransactionsButton.getSelection());
	}
	
	@Override
	protected void performDefaults() {
		// Load the default preferences
		autoCloseTransactionsButton.setSelection(getPreferenceStore().getDefaultBoolean(PreferenceConstants.AUTO_CLOSE_TRANSACTIONS));
		autoCloseTransactionsTtlText.setText(String.valueOf(getPreferenceStore().getDefaultInt(PreferenceConstants.AUTO_CLOSE_TRANSACTIONS_TTL)));
		autoCloseTransactionsTtlText.setEnabled(autoCloseTransactionsButton.getSelection());
	}
	
	@Override
	public boolean performOk() {
		// Save the preferences
		getPreferenceStore().setValue(PreferenceConstants.AUTO_CLOSE_TRANSACTIONS, autoCloseTransactionsButton.getSelection());
		getPreferenceStore().setValue(PreferenceConstants.AUTO_CLOSE_TRANSACTIONS_TTL, Integer.parseInt(autoCloseTransactionsTtlText.getText()));
		return true;
	}
}
