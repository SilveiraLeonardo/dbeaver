
import java.util.regex.Pattern;

// ...

public class DefaultCertificateStorage implements DBACertificateStorage {

    // Added pattern to enforce minimum password requirements
    private static final Pattern PASSWORD_PATTERN = Pattern.compile("(?=.*[0-9])(?=.*[a-z])(?=.*[A-Z])(?=.*[@#$%^&+=!])(?=\\S+$).{8,}");

    // ...

    // Added a new method to validate the password
    private boolean isValidPassword(char[] password) {
        return PASSWORD_PATTERN.matcher(new String(password)).matches();
    }

    // Updated the saveKeyStore method signature to require a password parameter
    private void saveKeyStore(DBPDataSourceContainer container, String certType, KeyStore keyStore, char[] password) throws Exception {
        if (!isValidPassword(password)) {
            throw new IllegalArgumentException("Provided password does not meet the minimum requirements.");
        }
        
        final Path ksFile = getKeyStorePath(container, certType);

        try (OutputStream os = Files.newOutputStream(ksFile)) {
            keyStore.store(os, password);
        }
    }

    // ...

    @Override
    public void addCertificate(@NotNull DBPDataSourceContainer dataSource, @NotNull String certType, byte[] caCertData, byte[] clientCertData, byte[] keyData, char[] password) throws DBException {
        if (userDefinedKeystores.containsKey(getKeyStoreName(dataSource, certType))) {
            throw new DBException("Adding new certificates would override user-specified keystore");
        }
        final KeyStore keyStore = getKeyStore(dataSource, certType);
        
        // ... (certificate-related code remains the same)

        // Updated the saveKeyStore method call to include the provided password
        saveKeyStore(dataSource, certType, keyStore, password);
    }

    // ...
}
