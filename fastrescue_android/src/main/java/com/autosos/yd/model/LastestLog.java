package com.autosos.yd.model;

import com.autosos.yd.util.JSONUtil;

import org.json.JSONArray;
import org.json.JSONObject;

/**
 * Created by Administrator on 2015/10/26.
 */
public class LastestLog implements Identifiable {

    private String amount;
    private String created_at;
    private String remark;
    private int type;


    public LastestLog(JSONObject jsonObject) {
        if (jsonObject != null) {
            this.amount =  JSONUtil.getString(jsonObject, "amount");
            this.created_at =  JSONUtil.getString(jsonObject, "created_at");
            this.remark =  JSONUtil.getString(jsonObject, "remark");
            this.type = jsonObject.optInt("type");
        }
    }


    public void setAmount(String amount) {
        this.amount = amount;
    }




    public void setCreated_at(String created_at) {
        this.created_at = created_at;
    }

    public void setRemark(String remark) {
        this.remark = remark;
    }

    public void setType(int type) {
        this.type = type;
    }

    public String getAmount() {
        return amount;
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
