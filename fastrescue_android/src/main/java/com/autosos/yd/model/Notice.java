package com.autosos.yd.model;

import com.autosos.yd.util.JSONUtil;

import org.json.JSONObject;

/**
 * Created by Administrator on 2015/7/31.
 */
public class Notice implements Identifiable{
    private long id;
    private int type;
    private int category;
    private String title ;
    private String content;
    private String creat_at;
    private String update_at;
    private String publish_at;
    private int created_uid;
    private int update_uid;
    private int state;
    public Notice(JSONObject jsonObject){
        if (jsonObject != null) {
            this.id = jsonObject.optLong("id");
            this.type = jsonObject.optInt("type");
            this.category = jsonObject.optInt("category");
            this.title = jsonObject.optString("title");
            this.content = jsonObject.optString("content");
            this.creat_at = jsonObject.optString("creat_at");
            this.update_at = jsonObject.optString("update_at");
            this.publish_at = jsonObject.optString("publish_at");
            this.created_uid = jsonObject.optInt("created_uid");
            this.update_uid = jsonObject.optInt("update_uid");
            this.state = jsonObject.optInt("state");
        }
    }
    public String getAll(){
        return id+type+category+title+content+creat_at+update_at+publish_at+created_uid+update_uid+state;
    }
    public void setId(long id) {
        this.id = id;
    }

    public int getType() {
        return type;
    }

    public void setType(int type) {
        this.type = type;
    }

    public int getCategory() {
        return category;
    }

    public void setCategory(int category) {
        this.category = category;
    }

    public String getTitle() {
        return title;
    }

    public void setTitle(String title) {
        this.title = title;
    }

    public String getContent() {
        return content;
    }

    public void setContent(String content) {
        this.content = content;
    }

    public String getCreat_at() {
        return creat_at;
    }

    public void setCreat_at(String creat_at) {
        this.creat_at = creat_at;
    }

    public String getUpdate_at() {
        return update_at;
    }

    public void setUpdate_at(String update_at) {
        this.update_at = update_at;
    }

    public String getPublish_at() {
        return publish_at;
    }

    public void setPublish_at(String publish_at) {
        this.publish_at = publish_at;
    }

    public int getCreated_uid() {
        return created_uid;
    }

    public void setCreated_uid(int created_uid) {
        this.created_uid = created_uid;
    }

    public int getUpdate_uid() {
        return update_uid;
    }

    public void setUpdate_uid(int update_uid) {
        this.update_uid = update_uid;
    }

    public int getState() {
        return state;
    }

    public void setState(int state) {
        this.state = state;
    }

    public Long getId() {
        return id;
    }
}
