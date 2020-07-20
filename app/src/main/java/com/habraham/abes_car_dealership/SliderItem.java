package com.habraham.abes_car_dealership;

public class SliderItem {
    private String imageUrl;

    public SliderItem(String imageUrl) {
        this.imageUrl = imageUrl;
    }

    public String getImageUrl() {
        return imageUrl;
    }

    @Override
    public String toString() {
        return "SliderItem{" +
                "imageUrl='" + imageUrl + '\'' +
                '}';
    }
}
