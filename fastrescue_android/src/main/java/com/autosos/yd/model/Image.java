package com.autosos.yd.model;

import android.content.Context;
import android.graphics.Point;
import android.util.DisplayMetrics;
import android.util.Log;

import org.json.JSONObject;

import com.autosos.yd.model.Identifiable;
import com.autosos.yd.util.JSONUtil;

public class Image implements Identifiable{

    private static final long serialVersionUID = -7601967341566018286L;

    private long id;
    private String img;
    private String fileid;
    private int width , height;
    public Image(JSONObject jsonObject) {
        if (jsonObject != null) {
            this.img = JSONUtil.getString(jsonObject, "img");
            this.fileid = JSONUtil.getString(jsonObject, "fileid");

        }
    }


    @Override
    public Long getId() {
        return id;
    }

    public void setId(long id) {
        this.id = id;
    }

    public String getImg() {
        return img;
    }

    public void setImg(String img) {
        this.img = img;
    }

    public String getFileid() {
        return fileid;
    }

    public void setFileid(String fileid) {
        this.fileid = fileid;
    }
}
