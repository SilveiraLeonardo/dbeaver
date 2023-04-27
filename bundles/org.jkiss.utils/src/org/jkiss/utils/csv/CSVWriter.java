
import org.apache.commons.validator.routines.StringValidator;

// ...

private static final int MAX_STRING_LENGTH = 1024;

// Add this method to the CSVWriter class
private boolean isValidString(String input) {
    StringValidator validator = StringValidator.getInstance();
    return validator.isValid(input, 0, MAX_STRING_LENGTH);
}

public void writeNext(String[] nextLine, boolean applyQuotesToAll) {
    if (nextLine == null) {
        return;
    }

    StringBuilder sb = new StringBuilder(INITIAL_STRING_SIZE);
    for (int i = 0; i < nextLine.length; i++) {
        if (i != 0) {
            sb.append(separator);
        }

        String nextElement = nextLine[i];

        if (nextElement == null || !isValidString(nextElement)) {
            continue;
        }

        Boolean stringContainsSpecialCharacters = stringContainsSpecialCharacters(nextElement);

        if ((applyQuotesToAll || stringContainsSpecialCharacters) && quotechar != NO_QUOTE_CHARACTER) {
            sb.append(quotechar);
        }

        if (stringContainsSpecialCharacters) {
            sb.append(processLine(nextElement));
        } else {
            sb.append(nextElement);
        }

        if ((applyQuotesToAll || stringContainsSpecialCharacters) && quotechar != NO_QUOTE_CHARACTER) {
            sb.append(quotechar);
        }
    }

    sb.append(lineEnd);
    pw.write(sb.toString());
}
