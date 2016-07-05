package com.autosos.rescue.model;

/**
 * Created by Administrator on 2016/6/2.
 */
public class OrderDetail implements  Identifiable{

    private int id;
    private String title;
    private String desc;



    public OrderDetail(String title, String desc,int id) {
        this.title = title;
        this.desc = desc;
        this.id = id;

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


    @Override
    public Integer getId() {
        return id;
    }
}
