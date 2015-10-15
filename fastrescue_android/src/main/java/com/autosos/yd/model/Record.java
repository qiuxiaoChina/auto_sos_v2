package com.autosos.yd.model;

import org.json.JSONObject;

import java.util.Date;

import com.autosos.yd.model.Identifiable;
import com.autosos.yd.util.JSONUtil;

public class Record implements Identifiable {

    private static final long serialVersionUID = 6468645596736353380L;

    private long id;
    private int status;
    private String address;
    private Date created_at;
    private String status_desc;
    private int is_uploadpic;
    private String tuoche_distance;
    private String price;
    private String starting_km;
    private String km_price;
    private int is_comment;
    private int is_paid;
    private int service_type;
    private String car_number;
    private int is_completed;
    public Record(JSONObject json) {
        if (json != null) {
            this.id = json.optLong("id", 0);
            this.status = json.optInt("status");
            this.address = JSONUtil.getString(json, "address");
            this.created_at = JSONUtil.getDataFromTimStamp(json, "created_at");
            this.status_desc = JSONUtil.getString(json, "status_desc");
            this.is_uploadpic = json.optInt("is_uploadpic");
            this.tuoche_distance = JSONUtil.getString(json, "tuoche_distance");
            this.price = JSONUtil.getString(json, "price");
            this.starting_km = JSONUtil.getString(json, "starting_km");
            this.km_price = JSONUtil.getString(json, "km_price");
            this.is_comment = json.optInt("is_comment");
            this.is_paid = json.optInt("is_paid");
            this.service_type = json.optInt("service_type");
            this.car_number=JSONUtil.getString(json, "car_number");
            this.is_completed = json.optInt("is_completed");
        }
    }

    public int getIs_completed() {
        return is_completed;
    }

    public void setIs_completed(int is_completed) {
        this.is_completed = is_completed;
    }

    public Record() {

    }

    @Override

    public Long getId() {
        return id;
    }

    public void setId(long id) {
        this.id = id;
    }

    public int getStatus() {
        return status;
    }

    public void setStatus(int status) {
        this.status = status;
    }

    public String getAddress() {
        return address;
    }

    public void setAddress(String address) {
        this.address = address;
    }

    public Date getCreated_at() {
        return created_at;
    }

    public void setCreated_at(Date created_at) {
        this.created_at = created_at;
    }

    public String getStatus_desc() {
        return status_desc;
    }

    public void setStatus_desc(String status_desc) {
        this.status_desc = status_desc;
    }

    public int getIs_uploadpic() {
        return is_uploadpic;
    }

    public void setIs_uploadpic(int is_uploadpic) {
        this.is_uploadpic = is_uploadpic;
    }

    public String getTuoche_distance() {
        return tuoche_distance;
    }

    public void setTuoche_distance(String tuoche_distance) {
        this.tuoche_distance = tuoche_distance;
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

    public int getIs_comment() {
        return is_comment;
    }

    public void setIs_comment(int is_comment) {
        this.is_comment = is_comment;
    }

    public int getIs_paid() {
        return is_paid;
    }

    public void setIs_paid(int is_paid) {
        this.is_paid = is_paid;
    }

    public int getService_type() {
        return service_type;
    }

    public void setService_type(int service_type) {
        this.service_type = service_type;
    }
    public void setCar_number(String car_number){
        this.car_number = car_number;
    }
    public String getCar_number(){
        return car_number;
    }
}
