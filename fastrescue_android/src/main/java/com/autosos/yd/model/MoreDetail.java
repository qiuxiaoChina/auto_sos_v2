package com.autosos.yd.model;

import com.autosos.yd.util.JSONUtil;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

/**
 * Created by Administrator on 2015/11/3.
 */
public class MoreDetail implements Identifiable {
    private String title;
    private String fee;
    private JSONArray tuoche;
    public static boolean expand;

    public MoreDetail(JSONObject jsonObject) {
        if (jsonObject != null) {
            this.tuoche =  JSONUtil.getJSONArray(jsonObject, "tuoche");
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
