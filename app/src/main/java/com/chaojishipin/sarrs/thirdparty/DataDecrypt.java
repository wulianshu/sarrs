package com.chaojishipin.sarrs.thirdparty;

import javax.crypto.Cipher;
import javax.crypto.spec.SecretKeySpec;

import android.text.TextUtils;
import android.util.Base64;

public class DataDecrypt {



    public static String decrypt(String ciphertext) {
        if (TextUtils.isEmpty(ciphertext)) {
            return "";
        }
        try {
            String plaintext;
            byte[] base64plain = decodeBase64(ciphertext);
            plaintext = decodeAES(base64plain, Constant.SECRET.AES_KEY);
            return plaintext;
        } catch (Exception e) {
            e.printStackTrace();
        }
        return "unknown";
    }

    private static String decodeAES(byte[] base64plain, byte[] aesKey) throws Exception {
        Cipher c = Cipher.getInstance("AES/ECB/PKCS5Padding");
        SecretKeySpec k = new SecretKeySpec(aesKey, "AES");
        byte[] byteContent = base64plain;
        c.init(Cipher.DECRYPT_MODE, k);
        byte[] result = c.doFinal(byteContent);
        return new String(result);
    }

    private static String decodeAES(byte[] base64plain, String key) throws Exception {
        return decodeAES(base64plain, key.getBytes());
    }

    public static String decodeAES(String input, String key) throws Exception {
        return decodeAES(input.getBytes(), key);
    }

    public static byte[] decodeBase64(String input) {
        return Base64.decode(input, Base64.DEFAULT);
    }
    public static String encodeBase64(String input){
        return Base64.encodeToString(input.getBytes(),Base64.DEFAULT);
    }
}
