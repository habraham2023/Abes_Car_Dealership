package com.habraham.abes_car_dealership.models;

import com.parse.FindCallback;
import com.parse.ParseClassName;
import com.parse.ParseFile;
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
    public static final String KEY_YEAR = "year";
    public static final String KEY_IMAGES = "images";

    public static final List<String> listingsFavorited = new ArrayList<>();

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
}
