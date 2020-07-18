package com.habraham.abes_car_dealership.models;

import com.parse.ParseClassName;
import com.parse.ParseException;
import com.parse.ParseObject;

@ParseClassName("Model")
public class Model extends ParseObject {
    public static final String KEY_NAME = "name";
    public static final String KEY_MAKE = "make";

    public String getName() {
        return getString(KEY_NAME);
    }

    public void setName(String name) {
        put(KEY_NAME, name);
    }

    public Make getMake() {
        return (Make) get(KEY_MAKE);
    }

    public void setMake(Make make) {
        put(KEY_MAKE, make);
    }
}
