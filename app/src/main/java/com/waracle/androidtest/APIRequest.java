package com.waracle.androidtest;

import android.os.AsyncTask;
import android.support.annotation.NonNull;

import java.io.BufferedInputStream;
import java.io.IOException;
import java.net.HttpURLConnection;
import java.net.URL;

class APIRequest {

    private final String mUrl;
    private String charset;

    APIRequest(@NonNull String url) {
        mUrl = url;
    }

    void loadData(final APIRequestCallback callback) {

        new AsyncTask<Void, Void, byte[]>() {

            @Override
            protected byte[] doInBackground(Void... params) {

                byte[] bytes = null;
                try {
                    URL url = new URL(mUrl);
                    HttpURLConnection urlConnection = (HttpURLConnection) url.openConnection();
                    urlConnection.setRequestProperty("Accept-Encoding", "identity");
                    try {
                        BufferedInputStream in = new BufferedInputStream(urlConnection.getInputStream());
                        bytes = StreamUtils.readUnknownFully(in);
                        charset = parseCharset(urlConnection.getRequestProperty("Content-Type"));

                    } finally {
                        urlConnection.disconnect();
                    }
                } catch (IOException e) {
                    e.printStackTrace();
                }
                return bytes;
            }

            @Override
            protected void onPostExecute(byte[] data) {
                super.onPostExecute(data);
                callback.onSuccess(data, charset);
            }
        }.execute();
    }

    private String parseCharset(String contentType) {
        if (contentType != null) {
            String[] params = contentType.split(",");
            for (int i = 1; i < params.length; i++) {
                String[] pair = params[i].trim().split("=");
                if (pair.length == 2) {
                    if (pair[0].equals("charset")) {
                        return pair[1];
                    }
                }
            }
        }
        return "UTF-8";
    }

    interface APIRequestCallback {

        void onSuccess(byte[] data, String charset);
    }
}
