package com.idamobile.vpb.courier.util;

import android.content.Context;
import android.os.Environment;
import lombok.Cleanup;

import java.io.*;
import java.util.ArrayList;
import java.util.List;

public class Files {

    public static final String TAG = Files.class.getSimpleName();

    public interface InputStreamProvider {
        InputStream openInputStream() throws IOException;
    }

    public interface OutputStreamProvider {
        OutputStream openOutputStream() throws IOException;
    }

    public static abstract class OutputTask<T extends Closeable> {
        public void performWrite() throws IOException {
            T stream = null;
            try {
                stream = openStream();
                doWrite(stream);
            } finally {
                if (stream != null) {
                    try {
                        stream.close();
                    } catch (IOException e) {
                    }
                }
            }
        }
        
        protected abstract T openStream() throws IOException;
        
        protected abstract void doWrite(T stream) throws IOException;
    }
    
    public static File getHomeDir(Context context) {
        File dir = new File(Environment.getExternalStorageDirectory(), "Android/data/" + context.getPackageName());
        if (!dir.exists()) {
            dir.mkdirs();
        }
        return dir;
    }

    public static File getImagesDir(Context context) {
        File dir = new File(getHomeDir(context), ".images");
        if (!dir.exists()) {
            dir.mkdirs();
        }
        return dir;
    }

    public static File getNotesDir(Context context) {
        File dir = new File(getHomeDir(context), ".notes");
        if (!dir.exists()) {
            dir.mkdirs();
        }
        return dir;
    }

    public static boolean createNoMedia(Context context) {
        File nomedia = new File(getHomeDir(context), ".nomedia");
        if (!nomedia.exists()) {
            try {
                return nomedia.createNewFile();
            } catch (IOException e) {
                Logger.warn(TAG, "unable to create nomedia", e);
                return false;
            }
        } else {
            return true;
        }
    }
    
    public static void startCleanUp(final Context context) {
        new Thread() {
            @Override
            public void run() {
                List<File> files = searchFiles(getHomeDir(context), null);
                for (File file : files) {
                    file.delete();
                }
            }
        }.start();
    }
    
    public static List<File> searchFiles(File where, FilenameFilter filter) {
        List<File> result = new ArrayList<File>();
        String[] files = where.list();
        if (files != null) {
            for (String name : files) {
                if (".".equals(name) || "..".equals(name)) {
                    continue;
                }

                File file = new File(where, name);
                if (file.isDirectory()) {
                    result.addAll(searchFiles(file, filter));
                } else {
                    if (filter == null || (filter != null && filter.accept(where, name))) {
                        result.add(file);
                    }
                }
            }
        }
        return result;
    }

    public static void copy(InputStream input, OutputStream output) throws IOException {
        byte[] buffer = new byte[1024 * 50];
        int len;
        while ((len = input.read(buffer)) >= 0) {
            if (len > 0) {
                output.write(buffer, 0, len);
            }
        }
    }

    public interface LineHanler {
        void onLineRead(String line);
    }

    public static String readAllLines(InputStreamProvider streamProvider) throws IOException {
        final StringBuilder builder = new StringBuilder();
        readAllLines(streamProvider, new LineHanler() {
            @Override
            public void onLineRead(String line) {
                if (builder.length() > 0) {
                    builder.append("\n");
                }
                builder.append(line);
            }
        });
        return builder.toString();
    }

    public static void readAllLines(InputStreamProvider stream, LineHanler hanler) throws IOException {
        @Cleanup InputStream inputStream = stream.openInputStream();
        BufferedInputStream bufferedInputStream = new BufferedInputStream(inputStream);
        BufferedReader reader = new BufferedReader(new InputStreamReader(bufferedInputStream));
        String line = null;
        while ((line = reader.readLine()) != null) {
            hanler.onLineRead(line);
        }
    }

    public static void saveAllLines(OutputStreamProvider outputStreamProvider, String text) throws IOException {
        OutputStream outputStream = outputStreamProvider.openOutputStream();
        BufferedOutputStream bufferedOutputStream = new BufferedOutputStream(outputStream);
        @Cleanup BufferedWriter writer = new BufferedWriter(new OutputStreamWriter(bufferedOutputStream));
        writer.write(text);
    }
}