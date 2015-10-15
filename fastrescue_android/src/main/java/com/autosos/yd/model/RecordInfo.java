package com.autosos.yd.model;

import org.json.JSONObject;

import com.autosos.yd.model.*;
import com.autosos.yd.model.Identifiable;
import com.autosos.yd.util.JSONUtil;

public class RecordInfo implements Identifiable {

    private static final long serialVersionUID = 6468645596736353380L;

    private long id;
    private int service_type;
    private String address;
    private double latitude;
    private double longitude;
    private String car_number;
    private String total_distance;
    private String price;
    private String starting_km;
    private String km_price;
    private String mobile;
    private String realname;
    private String rating;
    private String comment;
    private long total_amount;
    private long arrive_submit_at;
    public RecordInfo(JSONObject json) {
        if (json != null) {
            this.id = json.optLong("id", 0);
            this.service_type = json.optInt("service_type");
            this.address = JSONUtil.getString(json, "address");
            this.latitude = json.optDouble("latitude");
            this.longitude = json.optDouble("longitude");
            this.car_number = JSONUtil.getString(json, "car_number");
            this.total_distance = JSONUtil.getString(json, "total_distance");
            this.price = JSONUtil.getString(json, "price");
            this.starting_km = JSONUtil.getString(json, "starting_km");
            this.km_price = JSONUtil.getString(json, "km_price");
            this.mobile = JSONUtil.getString(json, "mobile");
            this.realname = JSONUtil.getString(json, "realname");
            this.rating = JSONUtil.getString(json, "rating");
            this.comment = JSONUtil.getString(json, "comment");
            this.total_amount = json.optLong("total_amount");
            this.arrive_submit_at = json.optInt("arrive_submit_at");
        }
    }

    public long getArrive_submit_at() {
        return arrive_submit_at;
    }

    public void setArrive_submit_at(long arrive_submit_at) {
        this.arrive_submit_at = arrive_submit_at;
    }

    @Override
    public Long getId() {
        return id;
    }

    public void setId(long id) {
        this.id = id;
    }

    public int getService_type() {
        return service_type;
    }

    public void setService_type(int service_type) {
        this.service_type = service_type;
    }

    public String getAddress() {
        return address;
    }

    public void setAddress(String address) {
        this.address = address;
    }

    public double getLatitude() {
        return latitude;
    }

    public void setLatitude(double latitude) {
        this.latitude = latitude;
    }

    public double getLongitude() {
        return longitude;
    }

    public void setLongitude(double longitude) {
        this.longitude = longitude;
    }

    public String getCar_number() {
        return car_number;
    }

    public void setCar_number(String car_number) {
        this.car_number = car_number;
    }

    public String getTotal_distance() {
        return total_distance;
    }

    public void setTotal_distance(String total_distance) {
        this.total_distance = total_distance;
    }

    public String getPrice() {
        return price;
    }

    public void setPrice(String price) {
        this.price = price;
    }

    public String getStarting_km() {
        return starting_km;
    }

    public void setStarting_km(String starting_km) {
        this.starting_km = starting_km;
    }

    public String getKm_price() {
        return km_price;
    }

    public void setKm_price(String km_price) {
        this.km_price = km_price;
    }

    public String getMobile() {
        return mobile;
    }

    public void setMobile(String mobile) {
        this.mobile = mobile;
    }

    public String getRealname() {
        return realname;
    }

    public void setRealname(String realname) {
        this.realname = realname;
    }

    public String getRating() {
        return rating;
    }

    public void setRating(String rating) {
        this.rating = rating;
    }

    public String getComment() {
        return comment;
    }

    public void setComment(String comment) {
        this.comment = comment;
    }

    public long getTotal_amount() {
        return total_amount;
    }

    public void setTotal_amount(long total_amount) {
        this.total_amount = total_amount;
    }
}
