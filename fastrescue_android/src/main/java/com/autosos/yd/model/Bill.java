package com.autosos.yd.model;

import com.autosos.yd.util.JSONUtil;

import org.json.JSONObject;

/**
 * Created by Administrator on 2015/10/26.
 */
public class Bill implements Identifiable {


    /**
     * income : 9220.00
     * outgo : -8466.00
     * month : 201510
     */

    private String income;
    private String outgo;
    private String month;
    private String year;
    private int id;
    private String amount;
    private int status;

    public Bill(JSONObject jsonObject) {
        if (jsonObject != null) {
            this.id = jsonObject.optInt("id");
            this.status = jsonObject.optInt("status");
            this.year =  JSONUtil.getString(jsonObject, "year");
            this.amount =  JSONUtil.getString(jsonObject, "amount");
            this.income =  JSONUtil.getString(jsonObject, "income");
            this.outgo =  JSONUtil.getString(jsonObject, "outgo");
            this.month =  JSONUtil.getString(jsonObject, "month");

        }
    }

    @Override
    public Long getId() {
        return null;
    }

    public int getIntId() {
        return id;
    }

    public int getStatus() {
        return status;
    }
    public String getAmount() {
        return amount;
    }

    public void setIncome(String income) {
        this.income = income;
    }

    public void setOutgo(String outgo) {
        this.outgo = outgo;
    }

    public void setMonth(String month) {
        this.month = month;
    }

    public String getIncome() {
        return income;
    }

    public String getOutgo() {
        return outgo;
    }

    public String getMonth() {
        return month;
    }
    public String getYear() {
        return year;
    }
}
