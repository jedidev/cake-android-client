package com.waracle.androidtest.models;

import android.os.Parcel;
import android.os.Parcelable;

public class Cake implements Parcelable {

    private String imageUrl;
    private String description;
    private String title;

    public String getImageUrl() {
        return imageUrl;
    }

    public String getDescription() {
        return description;
    }

    public String getTitle() {
        return title;
    }

    public void setImageUrl(String imageUrl) {
        this.imageUrl = imageUrl;
    }

    public void setDescription(String description) {
        this.description = description;
    }

    public void setTitle(String title) {
        this.title = title;
    }

    @Override
    public int describeContents() {
        return 0;
    }

    @Override
    public void writeToParcel(Parcel dest, int flags) {
        dest.writeString(imageUrl);
        dest.writeString(description);
        dest.writeString(title);
    }

    public static Creator<Cake> CREATOR = new Creator<Cake>() {
        @Override
        public Cake createFromParcel(Parcel source) {
            Cake cake = new Cake();
            cake.setImageUrl(source.readString());
            cake.setDescription(source.readString());
            cake.setTitle(source.readString());
            return cake;
        }

        @Override
        public Cake[] newArray(int size) {
            return new Cake[0];
        }
    };
}
