
import java.util.regex.Pattern;
// ...

public class EditVirtualAttributePage {

    // Add two patterns for name and expression validation
    private static final Pattern NAME_PATTERN = Pattern.compile("^[a-zA-Z0-9-_]+");
    private static final Pattern EXPRESSION_PATTERN = Pattern.compile("^[a-zA-Z0-9-_()\\s.]+");

    // ...

    private boolean validateInput() {
        String attributeName = nameText.getText();
        String attributeExpression = expressionText.getText();

        // Validate name and expression using regex patterns
        if (!NAME_PATTERN.matcher(attributeName).matches()) {
            // Show an error message dialog informing about the invalid name format
            return false;
        }
        if (!EXPRESSION_PATTERN.matcher(attributeExpression).matches()) {
            // Show an error message dialog informing about the invalid expression format
            return false;
        }

        return true;
    }

    // Call validateInput() before saving the edited or created attribute
    public boolean edit(Shell parent) {
        // ...

        if (!validateInput()) {
            return false;
        }

        // ...
    }
}
