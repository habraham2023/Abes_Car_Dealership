package com.habraham.abes_car_dealership.models;

import com.parse.ParseClassName;
import com.parse.ParseObject;
import com.parse.ParseUser;

@ParseClassName("Favorite")
public class Favorite extends ParseObject {
    public static final String KEY_USER = "user";
    public static final String KEY_LISTING = "listing";

    public ParseUser getUser() {
        return getParseUser(KEY_USER);
    }

    public void setUser(ParseUser user) {
        put(KEY_USER, user);
    }

    public Listing getListing() {
        return (Listing) get(KEY_LISTING);
    }

    public void setListing(Listing listing) {
        put(KEY_LISTING, listing);
    }
}
