package com.autosos.yd.model;

import com.autosos.yd.util.JSONUtil;

import org.json.JSONObject;

/**
 * Created by Administrator on 2015/10/26.
 */
public class TuoChe implements Identifiable {


    /**
     * income : 9220.00
     * outgo : -8466.00
     * month : 201510
     */

    private String distance;
    private String price;
    private String starting_km;
    private String km_price;

    public TuoChe(JSONObject jsonObject) {
        if (jsonObject != null) {

            this.distance =  JSONUtil.getString(jsonObject, "distance");
            this.price =  JSONUtil.getString(jsonObject, "price");
            this.starting_km =  JSONUtil.getString(jsonObject, "starting_km");
            this.km_price =  JSONUtil.getString(jsonObject, "km_price");

        }
    }

    @Override
    public Long getId() {
        return null;
    }


    public String getdistance() {
        return distance;
    }
    public String getprice() {
        return price;
    }
    public String getstarting_km() {
        return starting_km;
    }
    public String getkm_price() {
        return km_price;
    }
}
