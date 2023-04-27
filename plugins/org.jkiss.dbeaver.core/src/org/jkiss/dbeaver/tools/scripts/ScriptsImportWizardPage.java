
import java.io.File;
import java.util.regex.Pattern;
import org.jkiss.dbeaver.ui.controls.CSmartCombo;

/**
 * Add this method to ScriptsImportWizardPage class to validate directory input
 * */
private boolean isValidDirectory(String directory) {
    if (CommonUtils.isEmpty(directory)) {
        return false;
    }
    File dir = new File(directory);
    return dir.exists() && dir.isDirectory();
}

/**
 * Add this method to ScriptsImportWizardPage class to validate extensions input
 * */
private boolean isValidExtensions(String extensions) {
    if (CommonUtils.isEmpty(extensions)) {
        return false;
    }
    Pattern pattern = Pattern.compile("^\\*\\.\\w+(?:,\\*\\.\\w+)*$");
    return pattern.matcher(extensions).matches();
}

/**
 * Modify the createControl method to use the validation methods
 * */
@Override
public void createControl(Composite parent) {
    // ...
    // Inside the createControl method, add the following change to directoryText.addModifyListener

    directoryText.addModifyListener(new ModifyListener() {
        @Override
        public void modifyText(ModifyEvent e) {
            if (!isValidDirectory(directoryText.getText())) {
                setErrorMessage("Invalid directory");
            } else {
                setErrorMessage(null);
            }
            updateState();
        }
    });

    // Add the following change to extensionsText inside the createControl method
    extensionsText.addModifyListener(new ModifyListener() {
        @Override
        public void modifyText(ModifyEvent e) {
            if (!isValidExtensions(extensionsText.getText())) {
                setErrorMessage("Invalid file extensions format");
            } else {
                setErrorMessage(null);
            }
            updateState();
        }
    });

    // ...
}

/**
 * Update the isPageComplete method to utilize the validation methods
 * */
@Override
public boolean isPageComplete() {
    return isValidDirectory(directoryText.getText()) &&
            isValidExtensions(extensionsText.getText()) &&
            importRoot instanceof DBNResource && ((DBNResource) importRoot).getResource() instanceof IFolder;
}
