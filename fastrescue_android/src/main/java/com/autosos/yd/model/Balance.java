package com.autosos.yd.model;

import android.util.Log;

import com.autosos.yd.util.JSONUtil;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

public class Balance implements Identifiable{

    private String balance;
    private String amount;
    private String created_at;
    private String remark;
    private int type;
    private JSONArray lastest_log;
    private JSONArray detail;
    private int order_id;
    private int service_type;
    private String car_number;
    private String settle_fee;
    private String title;
    private String fee;
    private JSONObject tuoche;
    private String distance;
    private String price;
    private String starting_km;
    private String km_price;





    public Balance(JSONObject jsonObject) {
        if (jsonObject != null) {
            this.distance =  JSONUtil.getString(jsonObject, "distance");
            this.price =  JSONUtil.getString(jsonObject, "price");
            this.starting_km =  JSONUtil.getString(jsonObject, "starting_km");
            this.km_price =  JSONUtil.getString(jsonObject, "km_price");
            this.lastest_log =  JSONUtil.getJSONArray(jsonObject, "lastest_log");
            this.detail =  JSONUtil.getJSONArray(jsonObject, "detail");
            try {
                this.tuoche =  jsonObject.getJSONObject("tuoche");
            } catch (JSONException e) {
                e.printStackTrace();
            }
            this.title =  JSONUtil.getString(jsonObject, "title");
            this.fee =  JSONUtil.getString(jsonObject, "fee");
            this.balance =  JSONUtil.getString(jsonObject, "balance");
            this.amount =  JSONUtil.getString(jsonObject, "amount");
            this.created_at =  JSONUtil.getString(jsonObject, "created_at");
            this.remark =  JSONUtil.getString(jsonObject, "remark");
            this.type = jsonObject.optInt("type");
            this.order_id = jsonObject.optInt("order_id");
            this.service_type = jsonObject.optInt("service_type");
            this.car_number =  JSONUtil.getString(jsonObject, "car_number");
            this.settle_fee =  JSONUtil.getString(jsonObject, "settle_fee");

        }
    }
    public String getdistance(){
        return distance;
    }
    public String getprice(){
        return price;
    }
    public String getstarting_km(){
        return starting_km;
    }
    public String getkm_price(){
        return km_price;

    }
    public int getOrder_id(){
        return order_id;
    }
    public int getService_type(){
        return service_type;
    }
    public String getCar_number(){
        return car_number;
    }
    public String getSettle_fee(){
        return settle_fee;
    }
    public String getFee(){
        return fee;
    }
    public String getTitle(){
        return title;
    }
    public void setTitle(String title) {
        this.title = title;
    }
    public void setFee(String fee) {
        this.fee = fee;
    }
    public void setAmount(String amount) {
        this.amount = amount;
    }



    public void setBalance(String balance) {
        this.balance = balance;
    }

    public void setCreated_at(String created_at) {
        this.created_at = created_at;
    }

    public void setRemark(String remark) {
        this.remark = remark;
    }

    public void setdistance(String distance) {
        this.distance = distance;
    }
    public void setprice(String price) {
        this.price = price;
    }
    public void setstarting_km(String starting_km) {
        this.starting_km = starting_km;
    }
    public void setkm_price(String km_price) {
        this.km_price = km_price;
    }

    public void setType(int type) {
        this.type = type;
    }

    public String getAmount() {
        return amount;
    }

    public String getBalance() {
        return balance;
    }

    public JSONArray getLastest_log(){
        return lastest_log;
    }

    public JSONArray getDetail(){
        return detail;
    }

    public JSONObject getTuoche(){
        return tuoche;
    }

    public String getCreated_at() {
        return created_at;
    }

    public String getRemark() {
        return remark;
    }

    public int getType() {
        return type;
    }


    @Override
    public Long getId() {
        return null;
    }
}
