
import java.io.IOException;
import java.io.InputStream;
import java.io.OutputStream;
import java.net.*;
import java.nio.file.Files;
import java.nio.file.Path;
import java.text.NumberFormat;
import java.util.Base64;
import java.util.regex.Pattern;

public class WebUtils {
    private static final Log log = Log.getLog(WebUtils.class);
    private static final int MAX_RETRY_COUNT = 10;
    /* Add this line to the code */
    private static final Pattern HTTPS_URL_PATTERN = Pattern.compile("^https://.*", Pattern.CASE_INSENSITIVE);

    @NotNull
    public static URLConnection openConnection(String urlString, String referrer) throws IOException {
        return openConnection(urlString, null, referrer);
    }

    @NotNull
    public static URLConnection openConnection(String urlString, DBPAuthInfo authInfo, String referrer) throws IOException {
        // Add condition to verify if urlString matches HTTPS URL pattern
        if (authInfo != null && !HTTPS_URL_PATTERN.matcher(urlString).matches()) {
            throw new IOException("Basic Authentication should only be used with HTTPS URLs");
        }
        return openURLConnection(urlString, authInfo, referrer, 1);
    }

    /* rest of the code remains the same */
}
