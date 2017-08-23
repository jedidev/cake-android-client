package com.waracle.androidtest;

import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.support.v4.util.SimpleArrayMap;

import java.lang.ref.WeakReference;
import java.util.ArrayList;
import java.util.List;

class ImageCache {

    private static ImageCache sImageCacheInstance;

    static ImageCache sharedInstance() {
        if (sImageCacheInstance == null) {
            sImageCacheInstance = new ImageCache();
        }
        return sImageCacheInstance;
    }

    private final SimpleArrayMap<String, Bitmap> mBitmaps;
    private final List<String> mInFlightDownloads;
    private final SimpleArrayMap<WeakReference<ImageCacheCallback>, String> mQueuedRequests;

    private ImageCache() {
        mBitmaps = new SimpleArrayMap<>();
        mInFlightDownloads = new ArrayList<>();
        mQueuedRequests = new SimpleArrayMap<>();
    }

    void loadImage(final String url, WeakReference<ImageCacheCallback> callbackReference) {
        // If the image is already in the cache, return it straight away
        if (mBitmaps.containsKey(url)) {
            callbackReference.get().imageLoaded(mBitmaps.get(url), url);
        } else {
            // Queue the callback to be called when the image is retrieved
            mQueuedRequests.put(callbackReference, url);

            // If the image is not already currently being downloaded, start downloading it.
            if (!mInFlightDownloads.contains(url)) {
                downloadImage(url);
            }
        }
    }

    private void downloadImage(final String url) {
        mInFlightDownloads.add(url);
        APIRequest apiRequest = new APIRequest(url);
        apiRequest.loadData(new APIRequest.APIRequestCallback() {
            @Override
            public void onSuccess(byte[] data, String charset) {
                Bitmap bitmap = BitmapFactory.decodeByteArray(data, 0, data.length);
                processDownloadedImage(url, bitmap);
            }
        });
    }

    private void processDownloadedImage(String url, Bitmap bitmap) {
        mBitmaps.put(url, bitmap);
        mInFlightDownloads.remove(url);

        notifyAwaitingCallbacks(url);
    }

    private void notifyAwaitingCallbacks(String url) {
        for (int n = mQueuedRequests.size() - 1; n >= 0; n--) {
            if (mQueuedRequests.valueAt(n).equals(url)) {
                ImageCacheCallback itemCallback = mQueuedRequests.keyAt(n).get();
                if (itemCallback != null) {
                    itemCallback.imageLoaded(mBitmaps.get(url), url);
                    mQueuedRequests.removeAt(n);
                }
            }
        }
    }

    interface ImageCacheCallback {

        void imageLoaded(Bitmap bitmap, String url);
    }
}
