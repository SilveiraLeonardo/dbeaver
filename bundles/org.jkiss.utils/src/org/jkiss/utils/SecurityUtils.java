
package org.jkiss.utils;

import java.nio.charset.StandardCharsets;
import java.security.MessageDigest;
import java.security.NoSuchAlgorithmException;
import java.security.SecureRandom;
import java.util.Random;
import java.util.UUID;

public class SecurityUtils {

    public static String ENCRYPTION_ALGORITHM = "SHA-256";

    private static SecureRandom secureRandom = new SecureRandom();

    public static String generateGUID(boolean secure) {
        if (secure) {
            return UUID.randomUUID().toString();
        } else {
            return java.util.UUID.randomUUID().toString();
        }
    }

    public static String generateUniqueId() {
        long curTime = System.currentTimeMillis();
        int random = secureRandom.nextInt();
        if (random < 0) {
            random = -random;
        }

        return
                Long.toString(curTime, Character.MAX_RADIX) +
                        Integer.toString(random, Character.MAX_RADIX);
    }

    public static String makeDigest(String userAlias, String userPassword) {
        try {
            if (userPassword == null) {
                userPassword = "";
            }
            MessageDigest sha256 = MessageDigest.getInstance(ENCRYPTION_ALGORITHM);
            sha256.update(userAlias.getBytes(StandardCharsets.UTF_8));

            return CommonUtils.toHexString(sha256.digest(userPassword.getBytes(StandardCharsets.UTF_8)));
        } catch (NoSuchAlgorithmException toCatch) {
            return "*";
        }
    }

    public static String makeDigest(String userPassword) {
        try {
            MessageDigest sha256 = MessageDigest.getInstance(ENCRYPTION_ALGORITHM);

            return CommonUtils.toHexString(sha256.digest(userPassword.getBytes(StandardCharsets.UTF_8)));
        } catch (NoSuchAlgorithmException toCatch) {
            return "*";
        }
    }

    public static String generatePassword(int length) {
        StringBuilder pass = new StringBuilder(length);
        for (int i = 0; i < length; i++) {
            pass.append(
                    PASSWORD_ALPHABET[secureRandom.nextInt(PASSWORD_ALPHABET.length)]
            );
        }
        return (pass.toString());
    }

    public static long generateRandomLong() {
        return secureRandom.nextLong();
    }

    public static byte[] generateRandomBytes(int length) {
        byte[] bytes = new byte[length];
        secureRandom.nextBytes(bytes);
        return bytes;
    }

    public static String generatePassword() {
        return generatePassword(DEFAULT_PASSWORD_LENGTH);
    }

    public static Random getRandom() {
        return secureRandom;
    }

    public static final int DEFAULT_PASSWORD_LENGTH = 12;

    public static final char[] PASSWORD_ALPHABET = {
            'A', 'B', 'C', 'D', 'E', 'F', 'G', 'H',
            'I', 'J', 'K', 'L', 'M', 'N', 'O', 'P',
            'Q', 'R', 'S', 'T', 'U', 'V', 'W', 'X',
            'Y', 'Z', 'a', 'b', 'c', 'd', 'e', 'f',
            'g', 'h', 'i', 'j', 'k', 'l', 'm', 'n',
            'o', 'p', 'q', 'r', 's', 't', 'u', 'v',
            'w', 'x', 'y', 'z', '0', '1', '2', '3',
            '4', '5', '6', '7', '8', '9', '!', '@',
            '#', '$', '%', '^', '&', '*', '(', ')',
            '-', '_', '+', '=', '[', ']', '{', '}',
            ';', ':', ',', '<', '.', '>', '/', '?', '|', '~'
    };
}
