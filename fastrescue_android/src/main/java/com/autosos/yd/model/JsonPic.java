package com.autosos.yd.model;

import org.json.JSONObject;

import com.autosos.yd.model.Identifiable;
import com.autosos.yd.util.JSONUtil;

public class JsonPic implements Identifiable {

    private String path;
    private int width;
    private int height;

    public JsonPic(JSONObject jsonObject) {
        if (jsonObject != null) {
            this.path = JSONUtil.getString(jsonObject, "path");
            this.width = jsonObject.optInt("width", 0);
            this.height = jsonObject.optInt("height", 0);
        }
    }

    public int getHeight() {
        return height;
    }

    public void setHeight(int height) {
        this.height = height;
    }

    public String getPath() {
        return path;
    }

    public void setPath(String path) {
        this.path = path;
    }

    public int getWidth() {
        return width;
    }

    public void setWidth(int width) {
        this.width = width;
    }


    @Override
    public Long getId() {
        return 0l;
    }
}
