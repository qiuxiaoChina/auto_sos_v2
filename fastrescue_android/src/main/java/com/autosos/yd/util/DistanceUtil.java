package com.autosos.yd.util;

import com.amap.api.maps.model.LatLng;

import java.math.BigDecimal;

/**
 * Created by Administrator on 2016/5/26.
 */
public class DistanceUtil {


    public static float checkDistance(float f){

        BigDecimal b  =   new BigDecimal(f);
        float route_distance   =  b.setScale(2, BigDecimal.ROUND_HALF_UP).floatValue();
        return route_distance;
    }
}
