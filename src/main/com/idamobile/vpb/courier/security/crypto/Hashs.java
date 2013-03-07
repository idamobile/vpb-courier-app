package com.idamobile.vpb.courier.security.crypto;

import java.security.MessageDigest;
import java.security.NoSuchAlgorithmException;

public class Hashs {

    public static String getSHA1(String text) {
        try {
            MessageDigest md5 = MessageDigest.getInstance("SHA1");
            byte[] textBytes = text.getBytes();
            byte[] result = md5.digest(textBytes);
            return bytesToHex(result);
        } catch (NoSuchAlgorithmException e) {
            throw new RuntimeException(e);
        }
    }

    public static String bytesToHex(byte[] bytes) {
        final char[] hexArray = {'0','1','2','3','4','5','6','7','8','9','A','B','C','D','E','F'};
        char[] hexChars = new char[bytes.length * 2];
        int v;
        for ( int j = 0; j < bytes.length; j++ ) {
            v = bytes[j] & 0xFF;
            hexChars[j * 2] = hexArray[v >>> 4];
            hexChars[j * 2 + 1] = hexArray[v & 0x0F];
        }
        return new String(hexChars);
    }

}
