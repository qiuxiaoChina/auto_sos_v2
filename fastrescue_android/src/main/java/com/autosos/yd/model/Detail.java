package com.autosos.yd.model;

import com.autosos.yd.util.JSONUtil;

import org.json.JSONException;
import org.json.JSONObject;

/**
 * Created by Administrator on 2015/11/3.
 */
public class Detail implements Identifiable {
    private String title;
    private String fee;
    public static boolean expand;

    public Detail(JSONObject jsonObject) {
        if (jsonObject != null) {

            this.title =  JSONUtil.getString(jsonObject, "title");
            this.fee =  JSONUtil.getString(jsonObject, "fee");

        }
    }

    public String getFee(){
        return fee;
    }
    public String getTitle(){
        return title;
    }

    @Override
    public Long getId() {
        return null;
    }
}
