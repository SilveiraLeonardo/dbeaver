
import javax.crypto.SecretKey;
import javax.crypto.SecretKeyFactory;
import javax.crypto.spec.PBEKeySpec;
import java.security.NoSuchAlgorithmException;
import java.security.spec.InvalidKeySpecException;
import java.security.spec.KeySpec;
import java.util.Base64;

//... rest of the imports and class implementation

public class ExasolConnection implements DBPRefreshableObject, DBPNamedObject2, DBPSaveableObject, DBPScriptObject {
    // Other fields
    private SecretKey encryptedPassword;

    // Constructor updates for handling password securely
    public ExasolConnection(ExasolDataSource dataSource, String name, String url, String comment, String user, String password) {
        this.persisted = false;
        this.connectionName = name;
        this.connectionString = url;
        this.comment = comment;
        this.userName = user;
        setPassword(password);
        this.dataSource = dataSource;
    }

    // Other methods and properties

    // New method to securely store the password
    private void storeEncryptedPassword(char[] password) {
        try {
            SecretKeyFactory factory = SecretKeyFactory.getInstance("PBKDF2WithHmacSHA256");
            KeySpec spec = new PBEKeySpec(password, getSalt(), 65536, 256);
            this.encryptedPassword = factory.generateSecret(spec);
        } catch (NoSuchAlgorithmException | InvalidKeySpecException e) {
            throw new IllegalStateException("Failed to encrypt password.", e);
        }
    }

    // New method to generate salt for password encryption
    private byte[] getSalt() {
        // Replace with real salt generation, e.g. fetch from secure storage or generate randomly
        return "someSaltValue".getBytes();
    }

    @Property(viewable = true, editable = true, updatable = true, password = true, order = 35)
    public String getPassword() {
        return encryptedPassword != null ? Base64.getEncoder().encodeToString(encryptedPassword.getEncoded()) : "";
    }

    public void setPassword(String password) {
        // Convert encrypted string to SecretKey
        storeEncryptedPassword(password != null ? password.toCharArray() : null);
    }

    // Other methods and properties
}
