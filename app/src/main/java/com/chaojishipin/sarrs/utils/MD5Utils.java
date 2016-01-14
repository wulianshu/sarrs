package com.chaojishipin.sarrs.utils;


import java.io.UnsupportedEncodingException;
import java.security.MessageDigest;
import java.security.NoSuchAlgorithmException;

public class MD5Utils {
    public static String md5(String string) {
        byte[] hash;
        try {
            hash = string.getBytes("UTF-8");
            return md5(hash);
        } catch (UnsupportedEncodingException e1) {
            e1.printStackTrace();
        }
        return "unknown";
    }
    public static String md5(byte[] bytes) {
        byte[] hash;
        try {
            hash = MessageDigest.getInstance("MD5").digest(bytes);
        } catch (NoSuchAlgorithmException e) {
            throw new RuntimeException("Huh, MD5 should be supported?", e);
        }

        StringBuilder hex = new StringBuilder(hash.length * 2);
        for (byte b : hash) {
            if ((b & 0xFF) < 0x10)
                hex.append("0");
            hex.append(Integer.toHexString(b & 0xFF));
        }
        return hex.toString();
    }

//    public static String signByMD5(String unsigned) {
//        // 增补上防反编译破解的最后一位
//        return signByMD5(unsigned, MoviesConstant.SPREAD_SECRET + "7");
//    }

    public static String signByMD5(String unsigned, String secret) {
        String cipher = md5(unsigned + secret);
        return cipher;
    }
}
