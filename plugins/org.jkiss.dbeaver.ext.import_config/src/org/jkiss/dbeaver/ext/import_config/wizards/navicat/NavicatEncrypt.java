
import org.jkiss.utils.CommonUtils;

import javax.crypto.Cipher;
import javax.crypto.SecretKey;
import javax.crypto.spec.IvParameterSpec;
import javax.crypto.spec.SecretKeySpec;
import java.security.SecureRandom;
import java.util.Arrays;

public class NavicatEncrypt {
    private SecretKeySpec secretKeySpec;
    private IvParameterSpec ivSpec;
    private Cipher cipherEncrypt;
    private Cipher cipherDecrypt;

    public NavicatEncrypt() {
        initKey();
        initIV();
        initChiperEncrypt();
        initChiperDecrypt();
    }

    private void initKey() {
        try {
            // Generate a new secret key for each instance
            SecureRandom random = new SecureRandom();
            byte[] key = new byte[16];
            random.nextBytes(key);
            secretKeySpec = new SecretKeySpec(key, "AES");
        } catch (Exception e) {
            System.out.println(e.getMessage());
            e.printStackTrace();
        }
    }

    private void initIV() {
        try {
            // Generate a new IV for each instance
            SecureRandom random = new SecureRandom();
            byte[] iv = new byte[16];
            random.nextBytes(iv);
            ivSpec = new IvParameterSpec(iv);
        } catch (Exception e) {
            System.out.println(e.getMessage());
            e.printStackTrace();
        }
    }

    private void initChiperEncrypt() {
        try {
            // Use a more secure cipher mode (AES/CBC/PKCS5Padding)
            cipherEncrypt = Cipher.getInstance("AES/CBC/PKCS5Padding");
            cipherEncrypt.init(Cipher.ENCRYPT_MODE, secretKeySpec, ivSpec);
        } catch (Exception e) {
            System.out.println(e.getMessage());
            e.printStackTrace();
        }
    }

    private void initChiperDecrypt() {
        try {
            // Use a more secure cipher mode (AES/CBC/PKCS5Padding)
            cipherDecrypt = Cipher.getInstance("AES/CBC/PKCS5Padding");
            cipherDecrypt.init(Cipher.DECRYPT_MODE, secretKeySpec, ivSpec);
        } catch (Exception e) {
            System.out.println(e.getMessage());
            e.printStackTrace();
        }
    }

    public String encrypt(String inputString) {
        try {
            byte[] inData = inputString.getBytes("UTF-8");
            byte[] outData = cipherEncrypt.doFinal(inData);
            return CommonUtils.toHexString(outData);
        } catch (Exception e) {
            System.out.println(e.getMessage());
            e.printStackTrace();
            return "";
        }
    }

    public String decrypt(String hexString) {
        try {
            byte[] inData = CommonUtils.parseHexString(hexString);
            byte[] outData = cipherDecrypt.doFinal(inData);
            return new String(outData, "UTF-8");
        } catch (Exception e) {
            System.out.println(e.getMessage());
            e.printStackTrace();
            return "";
        }
    }
}
