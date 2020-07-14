package com.habraham.abes_car_dealership.models;

import com.parse.FindCallback;
import com.parse.ParseClassName;
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
}
