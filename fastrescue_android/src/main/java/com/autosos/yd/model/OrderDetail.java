package com.autosos.yd.model;

/**
 * Created by Administrator on 2016/6/2.
 */
public class OrderDetail {

    private String title;
    private String desc;

    public OrderDetail(String title, String desc) {
        this.title = title;
        this.desc = desc;
    }

    public String getTitle() {
        return title;
    }

    public void setTitle(String title) {
        this.title = title;
    }

    public String getDesc() {
        return desc;
    }

    public void setDesc(String desc) {
        this.desc = desc;
    }
}
