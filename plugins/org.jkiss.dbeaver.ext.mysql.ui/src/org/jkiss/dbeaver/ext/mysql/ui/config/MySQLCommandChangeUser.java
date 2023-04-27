
import javax.xml.bind.DatatypeConverter;
import java.nio.charset.StandardCharsets;
import java.security.MessageDigest;
import java.security.NoSuchAlgorithmException;
import java.security.SecureRandom;

private String generatePasswordSet() {
    Object passwordValue = getProperties().get(UserPropertyHandler.PASSWORD.name());
    if (passwordValue == null) {
        return null;
    }
    MySQLUser user = getObject();
    String password = passwordValue.toString();

    // Use SecureRandom for generating a cryptographic salt
    SecureRandom random = new SecureRandom();
    byte[] salt = new byte[16];
    random.nextBytes(salt);
    String saltHex = DatatypeConverter.printHexBinary(salt);

    // Use password and salt to generate the final hashed password
    String hashedPassword = "{SHA256}" + DatatypeConverter.printHexBinary(getHashWithSalt(password, saltHex));

    return "SET PASSWORD FOR '" + user.userName() + "'@'" + user.getHost() +
        "' = " + SQLUtils.quoteString(user, hashedPassword) + ";"; // Use hashed password instead of plaintext
}

private byte[] getHashWithSalt(String password, String salt) {
    try {
        MessageDigest messageDigest = MessageDigest.getInstance("SHA-256");
        messageDigest.update(salt.getBytes(StandardCharsets.UTF_8));
        return messageDigest.digest(password.getBytes(StandardCharsets.UTF_8));
    } catch (NoSuchAlgorithmException e) {
        throw new IllegalStateException("SHA-256 not available", e);
    }
}
