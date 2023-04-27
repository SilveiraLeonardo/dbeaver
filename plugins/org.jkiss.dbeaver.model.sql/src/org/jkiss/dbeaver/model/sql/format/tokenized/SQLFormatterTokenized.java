
import org.owasp.html.Sanitizers;
import org.owasp.html.PolicyFactory;

public class SQLFormatterTokenized {
    public String format(String inputSql, SQLFormatterConfiguration configuration) {
        PolicyFactory policy = Sanitizers.FORMATTING;
        String sanitizedInputSql = policy.sanitize(inputSql);

        // Format the sanitized input SQL using the configuration provided
        // ...
    }
}
