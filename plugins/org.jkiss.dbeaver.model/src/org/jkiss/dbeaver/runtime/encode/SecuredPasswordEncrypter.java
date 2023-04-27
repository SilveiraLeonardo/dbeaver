
import org.jkiss.utils.Base64;

import javax.crypto.Cipher;
import javax.crypto.KeyGenerator;
import javax.crypto.NoSuchPaddingException;
import javax.crypto.SecretKey;
import javax.crypto.SecretKeyFactory;
import javax.crypto.spec.IvParameterSpec;
import javax.crypto.spec.PBEKeySpec;
import javax.crypto.spec.SecretKeySpec;
import java.nio.charset.StandardCharsets;
import java.security.InvalidAlgorithmParameterException;
import java.security.InvalidKeyException;
import java.security.NoSuchAlgorithmException;
import java.security.SecureRandom;
import java.security.spec.InvalidKeySpecException;
import java.security.spec.KeySpec;

public class SecuredPasswordEncrypter implements PasswordEncrypter {

    public static final String SCHEME_AES = "AES";

    private static final String TRANSFORMATION = "AES/CBC/PKCS5Padding";
    private static final String CHARSET = "UTF8";
    private static final int KEY_SIZE = 256;
    private static final int IV_SIZE = 16;

    private SecretKey secretKey;
    private Cipher cipher;

    public SecuredPasswordEncrypter(char[] password, byte[] salt) throws EncryptionException {
        this(password, salt, SCHEME_AES);
    }

    public SecuredPasswordEncrypter(char[] password, byte[] salt, String encryptionScheme) throws EncryptionException {
        try {
            SecretKeyFactory factory = SecretKeyFactory.getInstance("PBKDF2WithHmacSHA256");
            KeySpec pbeKeySpec = new PBEKeySpec(password, salt, 65536, KEY_SIZE);
            SecretKey tmp = factory.generateSecret(pbeKeySpec);
            secretKey = new SecretKeySpec(tmp.getEncoded(), encryptionScheme);

            cipher = Cipher.getInstance(TRANSFORMATION);
        } catch (NoSuchAlgorithmException | NoSuchPaddingException | InvalidKeySpecException e) {
            throw new EncryptionException(e);
        }
    }

    @Override
    public String encrypt(String unencryptedString) throws EncryptionException {
        if (unencryptedString == null || unencryptedString.trim().length() == 0 || unencryptedString.length() > 4096) {
            throw new IllegalArgumentException("Invalid string");
        }

        try {
            byte[] iv = new byte[IV_SIZE];
            SecureRandom random = new SecureRandom();
            random.nextBytes(iv);
            IvParameterSpec ivParameterSpec = new IvParameterSpec(iv);

            cipher.init(Cipher.ENCRYPT_MODE, secretKey, ivParameterSpec);
            byte[] cleartext = unencryptedString.getBytes(CHARSET);
            byte[] ciphertext = cipher.doFinal(cleartext);

            byte[] encryptedDataWithIV = new byte[IV_SIZE + ciphertext.length];
            System.arraycopy(iv, 0, encryptedDataWithIV, 0, IV_SIZE);
            System.arraycopy(ciphertext, 0, encryptedDataWithIV, IV_SIZE, ciphertext.length);

            return Base64.encode(encryptedDataWithIV);
        } catch (Exception e) {
            throw new EncryptionException(e);
        }
    }

    @Override
    public String decrypt(String encryptedString) throws EncryptionException {
        if (encryptedString == null || encryptedString.trim().length() <= 0) {
            throw new IllegalArgumentException("Empty encrypted string");
        }

        try {
            byte[] encryptedDataWithIV = Base64.decode(encryptedString);
            byte[] iv = new byte[IV_SIZE];
            System.arraycopy(encryptedDataWithIV, 0, iv, 0, IV_SIZE);

            byte[] encryptedData = new byte[encryptedDataWithIV.length - IV_SIZE];
            System.arraycopy(encryptedDataWithIV, IV_SIZE, encryptedData, 0, encryptedData.length);

            IvParameterSpec ivParameterSpec = new IvParameterSpec(iv);
            cipher.init(Cipher.DECRYPT_MODE, secretKey, ivParameterSpec);
            byte[] cleartext = cipher.doFinal(encryptedData);

            return new String(cleartext, CHARSET);
        } catch (Exception e) {
            throw new EncryptionException(e);
        }
    }

}
