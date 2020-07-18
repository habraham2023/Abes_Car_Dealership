package com.habraham.abes_car_dealership.models;

import com.parse.ParseClassName;
import com.parse.ParseObject;

import java.util.List;

@ParseClassName("Make")
public class Make extends ParseObject {
    public static final String KEY_NAME = "name";
    public static final String KEY_MODELS = "models";

    public String getName() {
        return getString(KEY_NAME);
    }

    public void setName(String name) {
        put(KEY_NAME, name);
    }

    public List<Model> getModels() {
        return (List<Model>) get(KEY_MODELS);
    }

    public void setModels(List<Model> models) {
        put(KEY_MODELS, models);
    }
}
