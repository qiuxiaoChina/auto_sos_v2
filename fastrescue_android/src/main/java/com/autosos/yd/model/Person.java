package com.autosos.yd.model;

import org.json.JSONObject;

import com.autosos.yd.model.*;
import com.autosos.yd.model.Identifiable;
import com.autosos.yd.util.JSONUtil;

public class Person implements Identifiable {

    private static final long serialVersionUID = 4723889163553502490L;
    private long id;
    private String username;
    private String realname;
    private long company_id;
    private String company_name;
    private long accepted_count;
    private double average_rate;
    private long rank;
    private String mobile;
    private long money;
    private String avatar;
    private int online_status;
    private int is_manager;

    public Person(JSONObject jsonObject) {
        if (jsonObject != null) {
            this.id = jsonObject.optLong("uid");
            this.username = JSONUtil.getString(jsonObject, "username");
            this.realname = JSONUtil.getString(jsonObject, "realname");
            this.company_id = jsonObject.optLong("company_id");
            this.company_name = JSONUtil.getString(jsonObject, "company_name");
            this.accepted_count = jsonObject.optLong("accepted_count");
            this.average_rate = jsonObject.optDouble("average_rate");
            this.rank = jsonObject.optLong("rank");
            this.mobile = JSONUtil.getString(jsonObject, "mobile");
            this.money = jsonObject.optLong("money");
            this.avatar = JSONUtil.getString(jsonObject, "avatar");
            this.online_status = jsonObject.optInt("online_status");
            this.is_manager = jsonObject.optInt("is_manager");
        }
    }
    public void setOnline_status(int online_status){ this.online_status = online_status;}

    public int getOnline_status(){ return online_status; }

    public void setIs_manager(int online_status){ this.is_manager = is_manager;}

    public int getIs_manager(){ return is_manager; }


    public Long getId() {
        return id;
    }

    public void setId(Long id) {
        this.id = id;
    }

    public String getUsername() {
        return username;
    }

    public void setUsername(String username) {
        this.username = username;
    }

    public String getRealname() {
        return realname;
    }

    public void setRealname(String realname) {
        this.realname = realname;
    }

    public long getCompany_id() {
        return company_id;
    }

    public void setCompany_id(long company_id) {
        this.company_id = company_id;
    }

    public String getCompany_name() {
        return company_name;
    }

    public void setCompany_name(String company_name) {
        this.company_name = company_name;
    }

    public long getAccepted_count() {
        return accepted_count;
    }

    public void setAccepted_count(long accepted_count) {
        this.accepted_count = accepted_count;
    }

    public double getAverage_rate() {
        return average_rate;
    }

    public void setAverage_rate(double average_rate) {
        this.average_rate = average_rate;
    }

    public long getRank() {
        return rank;
    }

    public void setRank(long rank) {
        this.rank = rank;
    }

    public String getMobile() {
        return mobile;
    }

    public void setMobile(String mobile) {
        this.mobile = mobile;
    }

    public long getMoney() {
        return money;
    }

    public void setMoney(long money) {
        this.money = money;
    }

    public String getAvatar() {
        return avatar;
    }

    public void setAvatar(String avatar) {
        this.avatar = avatar;
    }
}
