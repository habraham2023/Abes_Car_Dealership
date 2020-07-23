package com.habraham.abes_car_dealership.models;

import com.parse.ParseClassName;
import com.parse.ParseObject;
import com.parse.ParseUser;

@ParseClassName("Message")
public class Message extends ParseObject {
    public static final String KEY_USER = "user";
    public static final String KEY_MESSAGE = "message";
    public static final String KEY_CHAT_ID = "chatID";

    public ParseUser getUser() {
        return getParseUser(KEY_USER);
    }

    public void setUser(ParseUser user) {
        put(KEY_USER, user);
    }

    public String getMessage() {
        return getString(KEY_MESSAGE);
    }

    public void setMessage(String message) {
        put(KEY_MESSAGE, message);
    }

    public String getChatID() {
        return getString(KEY_CHAT_ID);
    }

    public void setChatID(String chatID) {
        put(KEY_CHAT_ID, chatID);
    }
}
