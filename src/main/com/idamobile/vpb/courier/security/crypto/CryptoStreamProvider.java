package com.idamobile.vpb.courier.security.crypto;

import com.idamobile.vpb.courier.controllers.LoginManager;
import com.idamobile.vpb.courier.util.Files;
import com.idamobile.vpb.courier.util.Logger;

import java.io.*;

public class CryptoStreamProvider implements Files.InputStreamProvider, Files.OutputStreamProvider {

    public static final String TAG = CryptoStreamProvider.class.getSimpleName();

    private final LoginManager loginManager;
    private final File file;

    public CryptoStreamProvider(LoginManager loginManager, File file) {
        this.loginManager = loginManager;
        this.file = file;
    }

    @Override
    public InputStream openInputStream() throws IOException {
        try {
            return CryptoUtil.getUncryptInputStream(new FileInputStream(file), loginManager.getSecretKey());
        } catch (Exception e) {
            if (e instanceof IOException) {
                throw (IOException) e;
            } else {
                Logger.debug(TAG, "failed to open uncrypted stream", e);
                throw new IOException("failed to open uncrypted stream: " + e.getMessage());
            }
        }
    }

    @Override
    public OutputStream openOutputStream() throws IOException {
        try {
            return CryptoUtil.getCryptOutputStream(new FileOutputStream(file), loginManager.getSecretKey());
        } catch (Exception e) {
            if (e instanceof IOException) {
                throw (IOException) e;
            } else {
                Logger.debug(TAG, "failed to open crypted stream", e);
                throw new IOException("failed to open crypted stream: " + e.getMessage());
            }
        }
    }
}
