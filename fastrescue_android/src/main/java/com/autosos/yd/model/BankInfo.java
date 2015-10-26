package com.autosos.yd.model;

import android.util.Log;

import com.autosos.yd.util.JSONUtil;

import org.json.JSONObject;

/**
 * Created by YAO on 2015/10/25.
 */
public class BankInfo implements Identifiable {


    private String code;
    private String card_no;
    private String name;
    private String logo;
    private String account;

    public BankInfo(JSONObject jsonObject) {
        if (jsonObject != null) {
            this.code =  JSONUtil.getString(jsonObject, "code");
            this.card_no =  JSONUtil.getString(jsonObject, "card_no");
            this.name =  JSONUtil.getString(jsonObject, "name");
            this.logo =  JSONUtil.getString(jsonObject, "logo");
            this.account =  JSONUtil.getString(jsonObject, "account");
        }
    }

    @Override
    public Long getId() {
        return null;
    }

    public void setCode(String code) {
        this.code = code;
    }

    public void setCard_no(String card_no) {
        this.card_no = card_no;
    }

    public void setName(String name) {
        this.name = name;
    }

    public void setLogo(String logo) {
        this.logo = logo;
    }

    public void setAccount(String account) {
        this.account = account;
    }

    public String getCode() {
        return code;
    }

    public String getCard_no() {
        return card_no;
    }

    public String getName() {
        return name;
    }

    public String getLogo() {
        return logo;
    }

    public String getAccount() {
        return account;
    }
}
