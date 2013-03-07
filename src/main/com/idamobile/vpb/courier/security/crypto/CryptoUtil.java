package com.idamobile.vpb.courier.security.crypto;

import com.idamobile.vpb.courier.util.Logger;
import com.idamobile.vpb.courier.util.Versions;

import javax.crypto.*;
import javax.crypto.spec.PBEKeySpec;
import javax.crypto.spec.SecretKeySpec;
import java.io.InputStream;
import java.io.OutputStream;
import java.security.NoSuchAlgorithmException;
import java.security.NoSuchProviderException;
import java.security.SecureRandom;
import java.security.spec.KeySpec;

public class CryptoUtil {

    public static final String TAG = CryptoUtil.class.getSimpleName();

    public static InputStream getUncryptInputStream(InputStream is, SecretKeySpec key) throws Exception{
        Cipher cipher = Cipher.getInstance("AES");
        cipher.init(Cipher.DECRYPT_MODE, key);
        return new CipherInputStream(is, cipher);
    }

    public static OutputStream getCryptOutputStream(OutputStream os, SecretKeySpec key) throws Exception{
        Cipher cipher = Cipher.getInstance("AES");
        cipher.init(Cipher.ENCRYPT_MODE, key);
        return new CipherOutputStream(os, cipher);
    }

    public static SecretKeySpec getKey(String key, byte[] salt, int iterationCount, int keyLength) throws Exception {
        KeySpec keySpec = new PBEKeySpec(key.toCharArray(), salt, iterationCount, keyLength);
        SecretKeyFactory keyFactory = SecretKeyFactory.getInstance("PBEWITHSHAAND128BITAES-CBC-BC");
        byte[] keyBytes = keyFactory.generateSecret(keySpec).getEncoded();
        return new SecretKeySpec(keyBytes, "AES");
    }

    public byte[] generateSalt(int length) {
        SecureRandom sr = null;
        try {
            if (Versions.isApiLevelAvailable(16)) {
                sr = SecureRandom.getInstance("SHA1PRNG", "Crypto");
            } else {
                sr = SecureRandom.getInstance("SHA1PRNG");
            }
            byte[] salt = new byte[length];
            sr.nextBytes(salt);
            return salt;
        } catch (NoSuchAlgorithmException e) {
            Logger.error(TAG, "generating salt fails", e);
        } catch (NoSuchProviderException e) {
            Logger.error(TAG, "generating salt fails", e);
        }
        return null;
    }

}
