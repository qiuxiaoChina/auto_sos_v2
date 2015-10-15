package com.autosos.yd.model;

import org.json.JSONObject;

import com.autosos.yd.model.Identifiable;
import com.autosos.yd.util.JSONUtil;

public class Item implements Identifiable {

    private static final long serialVersionUID = -7601967341566018286L;

    private long id;
    private long bucketId;
    private String mediaPath;
    private int width;
    private int height;
    private String name;

    public Item(JSONObject json) {
        if (json != null) {
            this.id = json.optLong("id", 0);
            this.mediaPath = JSONUtil.isEmpty(JSONUtil
                    .getString(json, "media_path")) ? JSONUtil.getString(json,
                    "path") : JSONUtil.getString(json, "media_path");
            this.width = json.optInt("width", 0);
            this.height = json.optInt("height", 0);
            this.name = JSONUtil.getString(json, "name");
        }
    }

    @Override
    public Long getId() {
        return id;
    }

    public void setId(long id) {
        this.id = id;
    }

    public String getMediaPath() {
        return mediaPath;
    }

    public void setMediaPath(String mediaPath) {
        this.mediaPath = mediaPath;
    }

    public long getBucketId() {
        return bucketId;
    }

    public void setBucketId(long bucketId) {
        this.bucketId = bucketId;
    }

    public int getWidth() {
        return width;
    }

    public void setWidth(int width) {
        this.width = width;
    }

    public int getHeight() {
        return height;
    }

    public void setHeight(int height) {
        this.height = height;
    }

    public String getName() {
        return name;
    }

    public void setName(String name) {
        this.name = name;
    }
}
