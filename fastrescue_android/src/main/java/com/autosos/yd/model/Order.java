package com.autosos.yd.model;

import org.json.JSONObject;

import com.autosos.yd.model.*;
import com.autosos.yd.model.Identifiable;
import com.autosos.yd.util.JSONUtil;

public class Order implements Identifiable {

    private static final long serialVersionUID = 4723889163553502490L;
    private long id;
    private double latitude;
    private double longitude;
    private String address;
    private int service_type;
    private int price;
    private int status;
    private int is_appointed;
    private int order_type;
    private String st_reserved_order_at;
    private String reserved_time;


    public Order(JSONObject jsonObject) {
        if (jsonObject != null) {
            this.id = jsonObject.optLong("id");
            this.latitude = jsonObject.optDouble("latitude");
            this.longitude = jsonObject.optDouble("longitude");
            this.address = JSONUtil.getString(jsonObject, "address");
            this.service_type = jsonObject.optInt("service_type");
            this.price=jsonObject.optInt("price");
            this.status=jsonObject.optInt("status");
            this.is_appointed = jsonObject.optInt("is_appointed");
            this.order_type = jsonObject.optInt("order_type");
            this.st_reserved_order_at = JSONUtil.getString(jsonObject, "st_reserved_order_at");
            this.reserved_time =  JSONUtil.getString(jsonObject, "reserved_time");
        }
    }

    public String getReserved_time() {
        return reserved_time;
    }

    public void setReserved_time(String reserved_time) {
        this.reserved_time = reserved_time;
    }

    public String getSt_reserved_order_at() {
        return st_reserved_order_at;
    }

    public void setSt_reserved_order_at(String st_reserved_order_at) {
        this.st_reserved_order_at = st_reserved_order_at;
    }

    public int getOrder_type() {
        return order_type;
    }

    public void setOrder_type(int order_type) {
        this.order_type = order_type;
    }

    public int getIs_appointed() {
        return is_appointed;
    }

    public void setIs_appointed(int is_appointed) {
        this.is_appointed = is_appointed;
    }

    public int getStatus(){return status;}

    public void setStatus(int status){this.status=status;}

    public Long getId() {
        return id;
    }

    public void setId(Long id) {
        this.id = id;
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

    public String getAddress() {
        return address;
    }

    public void setAddress(String address) {
        this.address = address;
    }

    public int getService_type() {
        return service_type;
    }

    public void setService_type(int service_type) {
        this.service_type = service_type;
    }

    public int getPrice() {
        return price;
    }

    public void setPrice(int price) {
        this.price = price;
    }
}
