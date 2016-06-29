package com.autosos.rescue.model;

import com.autosos.rescue.util.JSONUtil;

import org.json.JSONObject;

public class OrderInfo{

    private static final long serialVersionUID = 4723889163553502490L;
    private int orderId;
    private int orderStatus;
    private int serviceType;
    private String address;
    private double latitude;
    private double longitude;
    private String ownerMobile;
    private String car_number;
    private double base_price;
    private int start_km;
    private double km_price;
    private String remark;
    private int to_uid;
    private double dest_lat;
    private double dest_lng;
    private String dest;

    private String real_address;
    private double real_latitude;
    private double real_longitude;
    private double real_take_latitude;
    private double real_take_longitude;
    private double real_dis;

    private double real_dest_lat;
    private double real_dest_lng;
    private double real_start_lat;
    private double real_start_lng;
    private String real_dest;
    private double real_tuoche_dis;


    public OrderInfo(JSONObject json) {
        if (json != null) {
            this.orderId = json.optInt("id");
            this.orderStatus = json.optInt("status");
            this.address = JSONUtil.getString(json,"address");
            this.latitude = json.optDouble("latitude",0);
            this.longitude = json.optDouble("longitude",0);
            this.ownerMobile = JSONUtil.getString(json,"mobile");
            this.car_number = JSONUtil.getString(json,"car_number");
            this.base_price = json.optDouble("price",0);
            this.start_km = json.optInt("starting_km",0);
            this.km_price = json.optDouble("km_price",0);
            this.remark = JSONUtil.getString(json,"remark");
            this.to_uid = json.optInt("service_uid",0);
            this.dest = JSONUtil.getString(json,"dest");
            this.dest_lat = json.optDouble("dest_lat",0);
            this.dest_lng = json.optDouble("dest_lng",0);
            this.serviceType = json.optInt("service_type",0);


            this.real_address = JSONUtil.getString(json,"real_arrive_addr");
            this.real_latitude = json.optDouble("real_arrive_lat",0);
            this.real_longitude = json.optDouble("real_arrive_lng",0);
            this.real_dis = json.optDouble("real_take_distance",0);
            this.real_take_latitude = json.optDouble("real_take_lat",0);
            this.real_take_longitude = json.optDouble("real_take_lng",0);

            this.real_dest = JSONUtil.getString(json,"real_stop_addr");
            this.real_dest_lat = json.optDouble("real_stop_lat",0);
            this.real_dest_lng = json.optDouble("real_stop_lng",0);
            this.real_start_lat = json.optDouble("real_start_lat",0);
            this.real_start_lng = json.optDouble("real_start_lng",0);
            this.real_tuoche_dis = json.optDouble("real_tuoche_distance",0);


        }
    }

    public double getReal_take_latitude() {
        return real_take_latitude;
    }

    public double getReal_take_longitude() {
        return real_take_longitude;
    }

    public double getReal_start_lat() {
        return real_start_lat;
    }

    public double getReal_start_lng() {
        return real_start_lng;
    }

    public String getReal_address() {
        return real_address;
    }

    public double getReal_latitude() {
        return real_latitude;
    }

    public double getReal_longitude() {
        return real_longitude;
    }

    public double getReal_dis() {
        return real_dis;
    }

    public double getReal_dest_lat() {
        return real_dest_lat;
    }

    public double getReal_dest_lng() {
        return real_dest_lng;
    }

    public double getReal_tuoche_dis() {
        return real_tuoche_dis;
    }

    public String getReal_dest() {
        return real_dest;
    }

    public double getDest_lng() {
        return dest_lng;
    }

    public double getDest_lat() {
        return dest_lat;
    }

    public String getDest() {
        return dest;
    }

    public double getLongitude() {
        return longitude;
    }

    public double getLatitude() {
        return latitude;
    }

    public static long getSerialVersionUID() {
        return serialVersionUID;
    }


    public int getTo_uid() {
        return to_uid;
    }

    public String getRemark() {
        return remark;
    }

    public int getStart_km() {
        return start_km;
    }

    public double getKm_price() {
        return km_price;
    }

    public double getBase_price() {
        return base_price;
    }

    public String getCar_number() {
        return car_number;
    }

    public String getOwnerMobile() {
        return ownerMobile;
    }


    public String getAddress() {
        return address;
    }

    public int getServiceType() {
        return serviceType;
    }

    public int getOrderId() {
        return orderId;
    }

    public int getOrderStatus() {
        return orderStatus;
    }
}
