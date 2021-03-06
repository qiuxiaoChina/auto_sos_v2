package com.autosos.rescue.model;

import com.autosos.rescue.util.JSONUtil;


import org.json.JSONObject;

public class User{

    private static final long serialVersionUID = 4723889163553502490L;
    private long id;
    private String token;
    private long expire;
    private long timetoken;
    public User(JSONObject jsonObject) {
        if (jsonObject != null) {
            this.id = jsonObject.optLong("id");
            this.token = JSONUtil.getString(jsonObject, "access_token");
            this.expire = jsonObject.optLong("expired");
            this.timetoken = jsonObject.optLong("");
        }
    }

    public long getTimetoken() {
        return timetoken;
    }

    public void setTimetoken(long timetoken) {
        this.timetoken = timetoken;
    }

    public long getExpire() {
        return expire;
    }

    public void setExpire(long expire) {
        this.expire = expire;
    }

    public String getToken() {
        return token;
    }

    public void setToken(String token) {
        this.token = token;
    }

    public Long getId() {
        return id;
    }

    public void setId(Long id) {
        this.id = id;
    }

}
