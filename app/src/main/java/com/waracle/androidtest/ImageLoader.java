package com.waracle.androidtest;

import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.os.AsyncTask;
import android.support.annotation.NonNull;
import android.util.Log;

import java.io.BufferedInputStream;
import java.io.IOException;
import java.io.InputStream;
import java.net.HttpURLConnection;
import java.net.URL;

class ImageLoader extends AsyncTask<String, Void, Bitmap> {

    private static final String TAG = ImageLoader.class.getSimpleName();

    private LoadImageTaskCallback mCallback;

    ImageLoader(@NonNull LoadImageTaskCallback callback) {
        mCallback = callback;
    }

    @Override
    protected Bitmap doInBackground(String... params) {
        Bitmap bitmap = null;
        String url = params[0];
        try {
            byte[] imageData = loadImageData(url);
            if (imageData != null) {
                bitmap = convertToBitmap(imageData);
            }
        } catch (Exception e) {
            if (e.getMessage() != null) {
                Log.e(TAG, e.getMessage());
            }
        }
        return bitmap;
    }

    @Override
    protected void onPostExecute(Bitmap bitmap) {
        mCallback.imageLoaded(bitmap);
    }

    interface LoadImageTaskCallback {

        void imageLoaded(Bitmap bitmap);
    }

    private static byte[] loadImageData(String url) throws IOException {
        HttpURLConnection connection = (HttpURLConnection) new URL(url).openConnection();
        InputStream inputStream = null;
        try {
            try {
                // Read data from workstation
                inputStream = connection.getInputStream();
            } catch (IOException e) {
                // Read the error from the workstation
                inputStream = connection.getErrorStream();
            }

            // Can you think of a way to make the entire
            // HTTP more efficient using HTTP headers??

            return StreamUtils.readUnknownFully(inputStream);
        } finally {
            // Close the input stream if it exists.
            StreamUtils.close(inputStream);

            // Disconnect the connection
            connection.disconnect();
        }
    }

    private static Bitmap convertToBitmap(byte[] data) {
        return BitmapFactory.decodeByteArray(data, 0, data.length);
    }
}
