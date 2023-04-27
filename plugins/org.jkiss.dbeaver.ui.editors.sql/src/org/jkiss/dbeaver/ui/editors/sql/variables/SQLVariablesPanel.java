
public class SQLVariablesPanel extends Composite implements DBCScriptContextListener, FocusListener {

    private void createControls() {
        mainEditor.getGlobalScriptContext().addListener(this);
        addDisposeListener(e -> mainEditor.getGlobalScriptContext().removeListener(this));

        StyledText editorControl = valueEditor.getEditorControl();
        TextEditorUtils.enableHostEditorKeyBindingsSupport(mainEditor.getSite(), editorControl);
        if (editorControl != null) {
            editorControl.addFocusListener(this);
        }
    }

    private void saveVariableValue() {
        StyledText editorControl = valueEditor.getEditorControl();
        if (editorControl == null) {
            return;
        }
        String varValue = editorControl.getText();
        if (curVariable != null) {
            saveInProgress = true;
            try {
                curVariable.value = varValue;
                mainEditor.getGlobalScriptContext().setVariable(
                    curVariable.name,
                    varValue);
                varsTable.refresh();
            } finally {
                saveInProgress = false;
            }
        }
    }

    @Override
    public void focusGained(FocusEvent e) {
        // Do nothing
    }

    @Override
    public void focusLost(FocusEvent e) {
        saveVariableValue();
    }
}
