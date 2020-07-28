package com.habraham.abes_car_dealership.models;

import com.parse.FindCallback;
import com.parse.ParseClassName;
import com.parse.ParseException;
import com.parse.ParseFile;
import com.parse.ParseGeoPoint;
import com.parse.ParseObject;
import com.parse.ParseQuery;
import com.parse.ParseUser;

import java.util.ArrayList;
import java.util.List;

@ParseClassName("Listing")
public class Listing extends ParseObject {
    public static final String KEY_TITLE = "title";
    public static final String KEY_DESCRIPTION = "description";
    public static final String KEY_SELLER = "seller";
    public static final String KEY_MAKE = "make";
    public static final String KEY_MODEL = "model";
    public static final String KEY_YEAR = "year";
    public static final String KEY_IMAGES = "images";
    public static final String KEY_PRICE = "price";
    public static final String KEY_EXTRA_INFORMATION = "extraInformation";
    public static final String KEY_CONTACT = "contact";
    public static final String KEY_ADDRESS = "address";
    public static final String KEY_LAT_LNG = "latLng";

    public static final List<String> listingsFavorited = new ArrayList<>();

    private double distance = 0;

    public static void getAllListingsFavorited(final FindCallback<Favorite> callback) {
        ParseQuery<Favorite> findFavorited = ParseQuery.getQuery(Favorite.class);
        findFavorited.whereEqualTo("user", ParseUser.getCurrentUser());
        findFavorited.findInBackground(callback);
    }

    public String getTitle() {
        return getString(KEY_TITLE);
    }

    public void setTitle(String title) {
        put(KEY_TITLE, title);
    }

    public String getDescription() {
        return getString(KEY_DESCRIPTION);
    }

    public void setDescription(String description) {
        put(KEY_DESCRIPTION, description);
    }

    public ParseUser getSeller() {
        return getParseUser(KEY_SELLER);
    }

    public void setSeller(ParseUser seller) {
        put(KEY_SELLER, seller);
    }

    public String getMake() {
        return getString(KEY_MAKE);
    }

    public void setMake(String make) {
        put(KEY_MAKE, make);
    }

    public String getModel() {
        return getString(KEY_MODEL);
    }

    public void setModel(String model) {
        put(KEY_MODEL, model);
    }

    public String getYear() {
        return getString(KEY_MAKE);
    }

    public void setYear(String year) {
        put(KEY_YEAR, year);
    }

    public List<ParseFile> getImages() {
        return (List<ParseFile>) get(KEY_IMAGES);
    }

    public void setImages(List<ParseFile> images) {
        put(KEY_IMAGES, images);
    }

    public int getPrice() {
        return (int) getNumber(KEY_PRICE);
    }

    public void setPrice(String price) {
        put(KEY_PRICE, Integer.parseInt(price));
    }

    public String getContact() {
        return getString(KEY_CONTACT);
    }

    public void setContact(String contact) {
        put(KEY_CONTACT, contact);
    }

    public String getExtraInformation() {
        return getString(KEY_EXTRA_INFORMATION);
    }

    public void setExtraInformation(String extraInformation) {
        put(KEY_EXTRA_INFORMATION, extraInformation);
    }

    public String getAddress() {
        return getString(KEY_ADDRESS);
    }

    public void setAddress(String address) {
        put(KEY_ADDRESS, address);
    }

    public ParseGeoPoint getLatLng() {
        return getParseGeoPoint(KEY_LAT_LNG);
    }

    public void setLatLng(ParseGeoPoint latLng) {
        put(KEY_LAT_LNG, latLng);
    }

    public double getDistance() {
        return distance;
    }

    public void setDistance(double distance) {
        this.distance = distance;
    }
}
