package com.autosos.rescue.model;

import com.autosos.rescue.util.JSONUtil;

import org.json.JSONObject;

/**
 * Created by Administrator on 2016/6/21.
 */
public class NewOrder {
    private int orderId;
    private double latitude;
    private double longitude;
    private double latitude_tuoche;
    private double longitude_tuoche;

    private String address;
    private String address_tuoche;
    private int is_appointed;
    private double plan_km;
    private int service_type;
    private double take_distance;
    private String owner_moblie;

    private int isPaodan;//是否是抛单
    private double onePrice;//抛单一口价

    public String getOwner_moblie() {
        return owner_moblie;
    }

    public NewOrder(JSONObject jsonObject) {
        this.orderId = jsonObject.optInt("id");
        this.latitude = jsonObject.optDouble("latitude",0);
        this.longitude = jsonObject.optDouble("longitude",0);
        this.latitude_tuoche = jsonObject.optDouble("dest_lat",0);
        this.longitude_tuoche = jsonObject.optDouble("dest_lng",0);
        this.address = JSONUtil.getString(jsonObject,"address");
        this.address_tuoche = JSONUtil.getString(jsonObject,"dest_addr");
        this.is_appointed = jsonObject.optInt("is_appointed");
        this.plan_km = jsonObject.optDouble("plan_km",0);
        this.service_type = jsonObject.optInt("service_type",0);
        this.take_distance = jsonObject.optDouble("take_distance",0);
        this.owner_moblie = JSONUtil.getString(jsonObject,"owner_mobile");

        this.isPaodan = jsonObject.optInt("is_throw",0);
        this.onePrice = jsonObject.optDouble("throw_price",0.0);

    }

    public int getIsPaodan() {
        return isPaodan;
    }

    public double getOnePrice() {
        return onePrice;
    }

    public double getTake_distance() {
        return take_distance;
    }

    public int getService_type() {
        return service_type;
    }

    public int getOrderId() {
        return orderId;
    }

    public double getLatitude() {
        return latitude;
    }

    public double getLongitude() {
        return longitude;
    }

    public double getLatitude_tuoche() {
        return latitude_tuoche;
    }

    public double getLongitude_tuoche() {
        return longitude_tuoche;
    }

    public String getAddress() {
        return address;
    }

    public String getAddress_tuoche() {
        return address_tuoche;
    }

    public int getIs_appointed() {
        return is_appointed;
    }

    public double getPlan_km() {
        return plan_km;
    }
}
