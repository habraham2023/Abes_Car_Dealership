package com.habraham.abes_car_dealership.models;

import android.graphics.Bitmap;

public class SliderItem {
    private String imageUrl;
    private Bitmap bitmap;

    public SliderItem(String imageUrl) {
        this.imageUrl = imageUrl;
    }

    public SliderItem(Bitmap bitmap) {
        this.bitmap = bitmap;
    }

    public String getImageUrl() {
        return imageUrl;
    }

    public Bitmap getBitmap() {
        return bitmap;
    }

    @Override
    public String toString() {
        return "SliderItem{" +
                "imageUrl='" + imageUrl + '\'' +
                '}';
    }
}
