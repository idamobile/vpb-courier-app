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

    public static interface ProgressListener {
        void onStreamProgress(long readBytes, long total);
    }

    public static InputStream addProgressListener(InputStream stream,
                                                  final long totalLength,
                                                  final ProgressListener progressListener) {
        return new FilterInputStream(stream) {
            private long read;
            private float oldProgress;

            @Override
            public int read(byte[] b, int offset, int length) throws IOException {
                int res = super.read(b, offset, length);
                if (res > 0) {
                    notifyProgress(res);
                }
                return res;
            }

            @Override
            public int read() throws IOException {
                int res = super.read();
                if (res >= 0) {
                    notifyProgress(1);
                }
                return res;
            }

            private void notifyProgress(int count) {
                if (count > 0 && totalLength > 0) {
                    read += count;
                    float progress = (float) read / totalLength;
                    if (oldProgress <= 0 || progress == 1 || progress - oldProgress > 0.1f) {
                        oldProgress = progress;
                        if (progressListener != null) {
                            progressListener.onStreamProgress(read, totalLength);
                        }
                    }
                }
            }
        };
    }
}