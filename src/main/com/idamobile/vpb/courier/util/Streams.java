package com.idamobile.vpb.courier.util;

import java.io.FilterInputStream;
import java.io.IOException;
import java.io.InputStream;

public class Streams {

    public static class InputStreamWithTotalBytesCounter extends FilterInputStream {

        private static final String TAG = InputStreamWithTotalBytesCounter.class.getSimpleName();

        private long totalBytesRead;

        public InputStreamWithTotalBytesCounter(InputStream in) {
            super(in);
        }

        public long getTotalBytesRead() {
            return totalBytesRead;
        }

        @Override
        public int read() throws IOException {
            int res = super.read();
            if (res > 0) {
                totalBytesRead += res;
            }
            return res;
        }

        @Override
        public int read(byte[] buffer) throws IOException {
            int res = super.read(buffer);
            if (res > 0) {
                totalBytesRead += res;
            }
            return res;
        }

        @Override
        public int read(byte[] buffer, int offset, int count) throws IOException {
            int res = super.read(buffer, offset, count);
            if (res > 0) {
                totalBytesRead += res;
            }
            return res;
        }

        @Override
        public void close() throws IOException {
            Logger.debug(TAG, "total bytes read count = " + totalBytesRead);
            super.close();
        }
    }
}