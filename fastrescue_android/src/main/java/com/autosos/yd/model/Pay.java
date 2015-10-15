package com.autosos.yd.model;

import org.json.JSONObject;

import com.autosos.yd.util.JSONUtil;

public class Pay {

    private int amount;
    private String channel;
    private String charge_id;

    public Pay(JSONObject jsonObject) {
        if (jsonObject != null) {
            this.amount = jsonObject.optInt("amount");
            this.channel = JSONUtil.getString(jsonObject, "channel");
            this.charge_id = JSONUtil.getString(jsonObject, "charge_id");

        }
    }

    public String getChannel() {
        return channel;
    }

    public void setChannel(String channel) {
        this.channel = channel;
    }

    public String getCharge_id() {
        return charge_id;
    }

    public void setCharge_id(String charge_id) {
        this.charge_id = charge_id;
    }

    public int getAmount() {
        return amount;
    }

    public void setAmount(int amount) {
        this.amount = amount;
    }
}
