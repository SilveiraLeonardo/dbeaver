
import java.util.Set;
import java.util.concurrent.ConcurrentHashMap;

...

public class DisplayedContent implements StyledTextContent {

    ...

    private Set<TextChangeListener> textListeners = null;

    ...

    DisplayedContent(int numberOfColumns, int numberOfLines) {
        data = new StringBuilder(numberOfColumns * numberOfLines * 2);  // account for replacements
        textListeners = ConcurrentHashMap.newKeySet(); // Updated to use a thread-safe collection
        setDimensions(numberOfColumns, numberOfLines);
    }

    ...

    /**
     * Shifts full lines of text and fills the new empty space with text
     *
     * @param text    to replace new empty lines. Its size determines the number of lines to shift
     * @param forward shifts lines either forward or backward
     */
    public void shiftLines(String text, boolean forward) {
        if (text == null || text.length() == 0) return; // Added null check and length check

        int linesInText = (text.length() - 1) / numberOfColumns + 1;
        
        ...

    }

}
