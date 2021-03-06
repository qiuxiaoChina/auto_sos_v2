package com.autosos.rescue.model;

import com.autosos.rescue.util.JSONUtil;

import org.json.JSONObject;

/**
 * Created by Administrator on 2016/6/24.
 */
public class TrailerInfo {

    private  String planeNum;
    private  String mobile;
    private double tonnage;
    private int is_aw;
    private int is_ground;
    private int is_arm;
    private String serviceType;
    private double diaoche_tonnage;

    public TrailerInfo(JSONObject jsonObject) {
        this.planeNum = JSONUtil.getString(jsonObject,"license");
        this.mobile = JSONUtil.getString(jsonObject,"mobile");
        this.tonnage = jsonObject.optDouble("tonnage",0);
        this.is_aw = jsonObject.optInt("is_aw",0);
        this.is_ground = jsonObject.optInt("is_ground",0);
        this.is_arm = jsonObject.optInt("is_arm",0);
        this.serviceType = JSONUtil.getString(jsonObject,"service_type");
        this.diaoche_tonnage = jsonObject.optDouble("diaoche_tonnage",0);
    }

    public double getDiaoche_tonnage() {
        return diaoche_tonnage;
    }

    public String getPlaneNum() {
        return planeNum;
    }

    public String getMobile() {
        return mobile;
    }

    public double getTonnage() {
        return tonnage;
    }

    public int getIs_aw() {
        return is_aw;
    }

    public int getIs_ground() {
        return is_ground;
    }

    public int getIs_arm() {
        return is_arm;
    }

    public String getServiceType() {
        return serviceType;
    }
}
