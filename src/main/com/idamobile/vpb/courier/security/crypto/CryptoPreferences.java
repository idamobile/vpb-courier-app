package com.idamobile.vpb.courier.security.crypto;

public class CryptoPreferences {

    private byte[] salt = {
            66, 12, 32, 99, 11, 36, 14, 0,
            33, 56, 10, 92, 18,  4,  1, 74,
            61, 30, 83, 37, 49, 63, 13, 17,
            9,  96, 50, 44, 32, 24, 53, 1,
        };

    public int getSaltLength() {
        return salt.length;
    }

    public int getIterationsCount() {
        return 1000;
    }

    public byte[] getSalt() {
        return salt;
    }

    public int getKeyLength() {
        return 128;
    }
}