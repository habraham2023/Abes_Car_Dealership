package com.habraham.abes_car_dealership.models;

import android.util.Log;

import com.parse.ParseClassName;
import com.parse.ParseException;
import com.parse.ParseObject;
import com.parse.ParseUser;

@ParseClassName("Chat")
public class Chat extends ParseObject {
    public static final String KEY_INITIATOR = "initiator";
    public static final String KEY_CONTACTED = "contacted";
    public static final String KEY_LISTING = "listing";

    public ParseUser getInitiator() throws ParseException {
        return fetchIfNeeded().getParseUser(KEY_INITIATOR);
    }

    public void setInitiator(ParseUser initiator) {
        put(KEY_INITIATOR, initiator);
    }

    public ParseUser getContacted() throws ParseException {
        return fetchIfNeeded().getParseUser(KEY_CONTACTED);
    }

    public void setContacted(ParseUser contacted) {
        put(KEY_CONTACTED, contacted);
    }

    public Listing getListing() {
        return (Listing) get(KEY_LISTING);
    }

    public void setListing(Listing listing) {
        put(KEY_LISTING, listing);
    }

    public boolean isEqual(ParseUser initiator, ParseUser contacted, Listing listing) throws ParseException {
        if (!getInitiator().getObjectId().equals(initiator.getObjectId())) {
            return false;
        }

        if (!getContacted().getObjectId().equals(contacted.getObjectId())) {
            return false;
        }

        if (!getListing().getObjectId().equals(listing.getObjectId())) {
            return false;
        }

        return true;
    }
}
