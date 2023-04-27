
import java.io.IOException;
import java.nio.ByteBuffer;
import java.nio.ByteOrder;
import java.util.Arrays;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

public class BinaryTextFinder {

    public static final int MAP_SIZE = 64 * 1024;
    public static final int MAX_SEQUENCE_SIZE = 2 * 1024; // a search string of 2K should be enough

    private long bufferPosition = -1L;
    private ByteBuffer byteBuffer = null;
    private int currentPartFound = -1; // relative positions
    private boolean currentPartFoundIsUnicode = false;
    private long currentPosition = 0L; // absolute value, start of forward finds, end(exclusive) of backward finds
    private byte[] byteFindSequence = null;
    private boolean caseSensitive = true;
    private BinaryContent content = null;
    private boolean directionForward = true;
    private CharSequence literal = null;
    private int literalByteLength = -1;
    private Pattern pattern = null;
    private volatile boolean stopSearching = false; // Declared as volatile

    // Helper method to safely escape special characters in a regex
    private static String escapeRegex(String input) {
        StringBuilder escaped = new StringBuilder();
        Arrays.stream(input.split("")).forEach(s -> escaped.append(Pattern.quote(s)));
        return escaped.toString();
    }

    public BinaryTextFinder(CharSequence literal, BinaryContent aContent) {
        if (literal.length() > MAX_SEQUENCE_SIZE / 2) {
            literal = literal.subSequence(0, MAX_SEQUENCE_SIZE / 2);
        }
        this.literal = literal;
        initSearchUnicodeAscii();
        content = aContent;
        bufferPosition = 0L;
        currentPosition = 0L;
    }

    public BinaryTextFinder(byte[] sequence, BinaryContent aContent) {
        if (sequence.length > MAX_SEQUENCE_SIZE) {
            byteFindSequence = new byte[MAX_SEQUENCE_SIZE];
            System.arraycopy(sequence, 0, byteFindSequence, 0, MAX_SEQUENCE_SIZE);
        } else {
            byteFindSequence = sequence;
        }

        content = aContent;
        bufferPosition = 0L;
        currentPosition = 0L;
    }

    // (Remaining unchanged code)

    void initSearchUnicodeAscii() {
        StringBuilder regex = new StringBuilder();
        for (int i = 0; i < literal.length(); ++i) {
            char aChar = literal.charAt(i);
            regex.append(escapeRegex(String.valueOf(aChar)));
        }

        int ignoreCaseFlags = 0;
        if (!caseSensitive)
            ignoreCaseFlags = Pattern.CASE_INSENSITIVE | Pattern.UNICODE_CASE;
        pattern = Pattern.compile(regex.toString(), ignoreCaseFlags);

        // (Remaining unchanged code)
    }

    // (Remaining unchanged code)
}
