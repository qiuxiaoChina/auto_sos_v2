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

    public Bill(JSONObject jsonObject) {
        if (jsonObject != null) {
            this.year =  JSONUtil.getString(jsonObject, "year");
            this.income =  JSONUtil.getString(jsonObject, "income");
            this.outgo =  JSONUtil.getString(jsonObject, "outgo");
            this.month =  JSONUtil.getString(jsonObject, "month");

        }
    }

    @Override
    public Long getId() {
        return null;
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
