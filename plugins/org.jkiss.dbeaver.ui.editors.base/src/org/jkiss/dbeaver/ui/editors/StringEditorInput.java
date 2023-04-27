
public class StringEditorInput {
    private String name;
    private StringBuilder buffer;
    private boolean readOnly;
    private Charset charset;

    public StringEditorInput(String name, CharSequence value, boolean readOnly, String charset) {
        if (name == null || value == null || charset == null) {
            throw new IllegalArgumentException("Invalid input: name, value, or charset cannot be null.");
        }

        this.name = name;
        this.buffer = new StringBuilder(value);
        this.readOnly = readOnly;
        this.charset = Charset.forName(charset);
    }

    // Other methods and logic here
}
