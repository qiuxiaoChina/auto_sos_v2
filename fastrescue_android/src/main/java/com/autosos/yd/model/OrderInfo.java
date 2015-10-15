package com.autosos.yd.model;

import org.json.JSONObject;

import com.autosos.yd.model.Identifiable;
import com.autosos.yd.util.JSONUtil;

public class OrderInfo implements Identifiable {

    private static final long serialVersionUID = 4723889163553502490L;
    private long id;
    private long status;
    private double longitude;
    private double latitude;
    private String address;
    private String owner_realname;
    private int service_type;
    private long car_id;
    private String car_number;
    private String owner_mobile;
    private int price;
    private int starting_km;
    private int km_price;
    private boolean is_paid;
    private double tuoche_distance;
    private int pay_status;
    private String arrive_submit_at;
    private long total_amount;
    private long pay_amount;
    private int add_type;
    private int is_own_expense;
    private String remark;
    private int is_appointed;
    private String insurance_name;
    private long insurance_id;
    private String hotline;
    private long tuoche_free_km;
    private long is_completed;
    private long is_cash;
    private int order_type;
    private String reserved_time;
    private String st_reserved_order_at;

    public OrderInfo(JSONObject json) {
        if (json != null) {
            this.id = json.optLong("id");
            this.status = json.optLong("status");
            this.longitude = json.optDouble("longitude");
            this.latitude = json.optDouble("latitude");
            this.address = JSONUtil.getString(json, "address");
            this.owner_realname = JSONUtil.getString(json, "realname");
            this.service_type = json.optInt("service_type");
            this.car_id = json.optLong("car_id");
            this.car_number = JSONUtil.getString(json, "car_number");
            this.owner_mobile = JSONUtil.getString(json, "mobile");
            this.price = json.optInt("price");
            this.starting_km = json.optInt("starting_km");
            this.km_price = json.optInt("km_price");
            this.is_paid = json.optInt("is_paid") > 0;
            this.tuoche_distance = json.optDouble("tuoche_distance");
            this.pay_status = json.optInt("pay_status");
            this.arrive_submit_at = JSONUtil.getString(json, "arrive_submit_at");
            this.total_amount = json.optLong("total_amount");
            this.add_type = json.optInt("add_type");
            this.is_own_expense = json.optInt("is_own_expense");
            this.remark =  JSONUtil.getString(json, "remark");
            this.is_appointed = json.optInt("is_appointed");
            this.pay_amount = json.optLong("pay_amount");
            this.tuoche_free_km = json.optInt("tuoche_free_km");
            this.insurance_name = JSONUtil.getString(json, "insurance_name");
            this.hotline = JSONUtil.getString(json, "hotline");
            this.is_completed = json.optLong("is_completed");
            this.is_cash = json.optLong("is_cash");
            this.insurance_id = json.optLong("insurance_id");
            this.order_type = json.optInt("order_type");
            this.reserved_time = JSONUtil.getString(json, "reserved_time");
            this.st_reserved_order_at = JSONUtil.getString(json,"st_reserved_order_at");
        }
    }

    public String getSt_reserved_order_at() {
        return st_reserved_order_at;
    }

    public void setSt_reserved_order_at(String st_reserved_order_at) {
        this.st_reserved_order_at = st_reserved_order_at;
    }

    public String getReserved_time() {
        return reserved_time;
    }

    public void setReserved_time(String reserved_time) {
        this.reserved_time = reserved_time;
    }

    public int getOrder_type() {
        return order_type;
    }

    public void setOrder_type(int order_type) {
        this.order_type = order_type;
    }

    public long getInsurance_id() {
        return insurance_id;
    }

    public void setInsurance_id(long insurance_id) {
        this.insurance_id = insurance_id;
    }

    public long getIs_cash() {
        return is_cash;
    }

    public void setIs_cash(long is_cash) {
        this.is_cash = is_cash;
    }

    public long getIs_completed() {
        return is_completed;
    }

    public void setIs_completed(long is_completed) {
        this.is_completed = is_completed;
    }

    public String getInsurance_name() {
        return insurance_name;
    }

    public void setInsurance_name(String insurance_name) {
        this.insurance_name = insurance_name;
    }

    public String getHotline() {
        return hotline;
    }

    public void setHotline(String hotline) {
        this.hotline = hotline;
    }

    public long getTuoche_free_km() {
        return tuoche_free_km;
    }

    public void setTuoche_free_km(long tuoche_free_km) {
        this.tuoche_free_km = tuoche_free_km;
    }

    public long getPay_amount() {
        return pay_amount;
    }

    public void setPay_amount(long pay_amount) {
        this.pay_amount = pay_amount;
    }

    public boolean is_paid() {
        return is_paid;
    }

    public int getIs_appointed() {
        return is_appointed;
    }

    public void setIs_appointed(int is_appointed) {
        this.is_appointed = is_appointed;
    }

    public String getRemark() {
        return remark;
    }

    public void setRemark(String remark) {
        this.remark = remark;
    }

    public int getIs_own_expense() {
        return is_own_expense;
    }

    public void setIs_own_expense(int is_own_expense) {
        this.is_own_expense = is_own_expense;
    }

    public int getAdd_type() {
        return add_type;
    }

    public void setAdd_type(int add_type) {
        this.add_type = add_type;
    }

    public boolean getIs_paid() {
        return is_paid;
    }

    public void setIs_paid(boolean is_paid) {
        this.is_paid = is_paid;
    }

    public Long getId() {
        return id;
    }

    public void setId(Long id) {
        this.id = id;
    }

    public long getStatus() {
        return status;
    }

    public void setStatus(long status) {
        this.status = status;
    }

    public double getLongitude() {
        return longitude;
    }

    public void setLongitude(double longitude) {
        this.longitude = longitude;
    }

    public double getLatitude() {
        return latitude;
    }

    public void setLatitude(double latitude) {
        this.latitude = latitude;
    }

    public String getAddress() {
        return address;
    }

    public void setAddress(String address) {
        this.address = address;
    }


    public String getOwner_realname() {
        return owner_realname;
    }

    public void setOwner_realname(String owner_realname) {
        this.owner_realname = owner_realname;
    }


    public int getService_type() {
        return service_type;
    }

    public void setService_type(int service_type) {
        this.service_type = service_type;
    }

    public long getCar_id() {
        return car_id;
    }

    public void setCar_id(long car_id) {
        this.car_id = car_id;
    }

    public String getCar_number() {
        return car_number;
    }

    public void setCar_number(String car_number) {
        this.car_number = car_number;
    }

    public String getOwner_mobile() {
        return owner_mobile;
    }

    public void setOwner_mobile(String owner_mobile) {
        this.owner_mobile = owner_mobile;
    }

    public int getPrice() {
        return price;
    }

    public void setPrice(int price) {
        this.price = price;
    }

    public int getPay_status() {
        return pay_status;
    }

    public void setPay_status(int pay_status) {
        this.pay_status = pay_status;
    }

    public double getTuoche_distance() {
        return tuoche_distance;
    }

    public void setTuoche_distance(double tuoche_distance) {
        this.tuoche_distance = tuoche_distance;
    }

    public String getArrive_submit_at() {
        return arrive_submit_at;
    }

    public void setArrive_submit_at(String arrive_submit_at) {
        this.arrive_submit_at = arrive_submit_at;
    }

    public int getKm_price() {
        return km_price;
    }

    public void setKm_price(int km_price) {
        this.km_price = km_price;
    }

    public int getStarting_km() {
        return starting_km;
    }

    public void setStarting_km(int starting_km) {
        this.starting_km = starting_km;
    }

    public long getTotal_amount() {
        return total_amount;
    }

    public void setTotal_amount(long total_amount) {
        this.total_amount = total_amount;
    }
}
