package com.waracle.androidtest;

import android.util.Log;

import java.io.BufferedInputStream;
import java.io.ByteArrayOutputStream;
import java.io.Closeable;
import java.io.DataInputStream;
import java.io.IOException;
import java.io.InputStream;
import java.util.ArrayList;

class StreamUtils {
    private static final String TAG = StreamUtils.class.getSimpleName();

    // Can you see what's wrong with this???
    static byte[] readUnknownFully(InputStream stream) throws IOException {
        int len;
        int size = 1024;
        byte[] buf;

        ByteArrayOutputStream byteArrayOutputStream = new ByteArrayOutputStream();
        buf = new byte[size];
        while ((len = stream.read(buf, 0, size)) != -1) {
            byteArrayOutputStream.write(buf, 0, len);
        }
        return byteArrayOutputStream.toByteArray();
    }

    static void close(Closeable closeable) {
        if (closeable != null) {
            try {
                closeable.close();
            } catch (IOException e) {
                Log.e(TAG, e.getMessage());
            }
        }
    }
}
