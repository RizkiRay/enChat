package net.enjoystudio.enchat;

import android.util.Base64;
import android.util.Log;

import java.nio.charset.Charset;
import java.security.Key;
import java.security.SecureRandom;
import java.util.Random;

import javax.crypto.Cipher;
import javax.crypto.spec.SecretKeySpec;

public class AES {
    // Method Enkripsi AES
    public static String encryptAES(String Data, String kunciEnkripsi)
            throws Exception {
        Cipher c = Cipher.getInstance("AES");
        Key key = generateKey(kunciEnkripsi);
        c.init(Cipher.ENCRYPT_MODE, key);
        byte[] encVal = c.doFinal(Data.getBytes());
        String encryptedValue = android.util.Base64.encodeToString(encVal,16);
        return encryptedValue;
    }

    // Method Dekripsi AES
    public static String decryptAES(String encryptedData, String kunciEnkripsi)
            throws Exception {
        Cipher c = Cipher.getInstance("AES");
        Key key = generateKey(kunciEnkripsi);
        c.init(Cipher.DECRYPT_MODE, key);
        byte[] decordedValue = android.util.Base64.decode(encryptedData,Base64.DEFAULT);
        byte[] decValue = c.doFinal(decordedValue);
        String decryptedValue = new String(decValue);
        return decryptedValue;
    }

    // Method untuk membangkitkan kunci AES dari String
    private static Key generateKey(String kunciEnkripsi) throws Exception {
        Key key = new SecretKeySpec(kunciEnkripsi.getBytes(), "AES");
        return key;
    }
    public static String randomString() {
        char[] characterSet = C.ALPHANUMERIK;
        int length = 32;
        Random random = new SecureRandom();
        char[] result = new char[length];
        for (int i = 0; i < result.length; i++) {
            int randomCharIndex = random.nextInt(characterSet.length);
            result[i] = characterSet[randomCharIndex];
        }
        return new String(result);
    }
}