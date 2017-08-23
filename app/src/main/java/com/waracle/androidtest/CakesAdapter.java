package com.waracle.androidtest;

import android.graphics.Bitmap;
import android.support.v7.widget.RecyclerView;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.ProgressBar;
import android.widget.TextView;

import com.waracle.androidtest.models.Cake;

import java.lang.ref.WeakReference;
import java.util.ArrayList;
import java.util.List;

class CakesAdapter extends RecyclerView.Adapter {

    private final ImageCache mImageCache;

    private List<Cake> mCakes;

    CakesAdapter() {
        mCakes = new ArrayList<>();
        mImageCache = ImageCache.sharedInstance();
    }

    void setCakes(List<Cake> cakes) {
        mCakes = cakes;
        notifyDataSetChanged();
    }

    @Override
    public RecyclerView.ViewHolder onCreateViewHolder(ViewGroup parent, int viewType) {
        View itemView = View.inflate(parent.getContext(), R.layout.list_item_layout, null);
        return new CakeViewHolder(itemView);
    }

    @Override
    public void onBindViewHolder(RecyclerView.ViewHolder holder, int position) {
        final CakeViewHolder cakeViewHolder = (CakeViewHolder) holder;
        final Cake cake = mCakes.get(position);

        cakeViewHolder.getImageView().setImageBitmap(null);
        cakeViewHolder.getProgressBar().setVisibility(View.VISIBLE);
        cakeViewHolder.setUrl(cake.getImageUrl());
        cakeViewHolder.setCallback(new ImageCache.ImageCacheCallback() {
            @Override
            public void imageLoaded(Bitmap bitmap, String url) {
                if (url.equals(cakeViewHolder.getUrl())) {
                    cakeViewHolder.getImageView().setImageBitmap(bitmap);
                    cakeViewHolder.getProgressBar().setVisibility(View.GONE);
                }
            }
        });
        mImageCache.loadImage(cake.getImageUrl(), new WeakReference<>(cakeViewHolder.getCallback()));
        cakeViewHolder.getTitleView().setText(cake.getTitle());
        cakeViewHolder.getDescriptionView().setText(cake.getDescription());
    }

    @Override
    public int getItemCount() {
        return mCakes.size();
    }

    private class CakeViewHolder extends RecyclerView.ViewHolder {

        private ImageView imageView;
        private TextView titleView;
        private TextView descriptionView;
        private ProgressBar progressBar;
        private ImageCache.ImageCacheCallback callback;
        private String url;

        CakeViewHolder(View itemView) {
            super(itemView);

            imageView = (ImageView) itemView.findViewById(R.id.image);
            titleView = (TextView) itemView.findViewById(R.id.title);
            descriptionView = (TextView) itemView.findViewById(R.id.desc);
            progressBar = (ProgressBar) itemView.findViewById(R.id.item_progress);
        }

        ImageView getImageView() {
            return imageView;
        }

        TextView getTitleView() {
            return titleView;
        }

        TextView getDescriptionView() {
            return descriptionView;
        }

        ProgressBar getProgressBar() {
            return progressBar;
        }

        ImageCache.ImageCacheCallback getCallback() {
            return callback;
        }

        void setCallback(ImageCache.ImageCacheCallback callback) {
            this.callback = callback;
        }

        String getUrl() {
            return url;
        }

        void setUrl(String url) {
            this.url = url;
        }
    }
}
