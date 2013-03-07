package com.idamobile.vpb.courier.security.crypto;

import com.idamobile.vpb.courier.util.Files;
import lombok.Cleanup;

import javax.crypto.spec.SecretKeySpec;
import java.io.*;

public class CryptoCoder {

    public static void cryptFile(File from, File to, SecretKeySpec key) throws Exception {
        @Cleanup InputStream inputStream = new FileInputStream(from);
        OutputStream outputStream = new FileOutputStream(to);
        @Cleanup OutputStream cryptOutputStream = CryptoUtil.getCryptOutputStream(outputStream, key);
        Files.copy(inputStream, cryptOutputStream);
    }

    public static void decryptFile(File from, File output, SecretKeySpec secretKey) throws Exception {
        @Cleanup InputStream inputStream = new FileInputStream(from);
        @Cleanup OutputStream outputStream = new FileOutputStream(output);
        InputStream uncryptInputStream = CryptoUtil.getUncryptInputStream(inputStream, secretKey);
        Files.copy(uncryptInputStream, outputStream);
    }
}
