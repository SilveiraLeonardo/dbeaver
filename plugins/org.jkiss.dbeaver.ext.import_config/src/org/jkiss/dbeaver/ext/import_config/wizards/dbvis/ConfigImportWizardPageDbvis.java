
import javax.crypto.Cipher;
import javax.crypto.SecretKey;
import javax.crypto.spec.IvParameterSpec;
import javax.crypto.spec.SecretKeySpec;

String password = null;
String passwordEncoded = XMLUtils.getChildElementBody(dbElement, "Password");
if (!CommonUtils.isEmpty(passwordEncoded)) {
    try {
        password = decryptPassword(passwordEncoded, "your_secret_key_here");
    } catch (Exception e) {
        // Log and handle the error in a way appropriate for your application
        System.err.println("Error decrypting password: " + e.getMessage());
    }
}

private String decryptPassword(String encryptedPassword, String secretKey) throws Exception {
    byte[] keyBytes = secretKey.getBytes(StandardCharsets.UTF_8);
    SecretKey key = new SecretKeySpec(keyBytes, "AES");

    Cipher cipher = Cipher.getInstance("AES/CBC/PKCS5Padding");
    byte[] iv = new byte[cipher.getBlockSize()];
    IvParameterSpec ivSpec = new IvParameterSpec(iv);

    cipher.init(Cipher.DECRYPT_MODE, key, ivSpec);

    byte[] decryptedBytes = cipher.doFinal(Base64.getDecoder().decode(encryptedPassword));
    return new String(decryptedBytes, StandardCharsets.UTF_8);
}
