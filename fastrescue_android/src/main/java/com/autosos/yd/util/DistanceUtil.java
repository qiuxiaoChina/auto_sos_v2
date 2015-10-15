package com.autosos.yd.util;

import android.content.Context;
import android.util.Log;
import android.widget.TextView;

import com.autosos.yd.Constants;
import com.autosos.yd.R;
import com.autosos.yd.fragment.WorkFragment;
import com.autosos.yd.model.Order;
import com.baidu.mapapi.model.LatLng;
import com.baidu.mapapi.search.route.DrivingRoutePlanOption;
import com.baidu.mapapi.search.route.DrivingRouteResult;
import com.baidu.mapapi.search.route.OnGetRoutePlanResultListener;
import com.baidu.mapapi.search.route.PlanNode;
import com.baidu.mapapi.search.route.RoutePlanSearch;
import com.baidu.mapapi.search.route.TransitRoutePlanOption;
import com.baidu.mapapi.search.route.TransitRouteResult;
import com.baidu.mapapi.search.route.WalkingRouteResult;

/**
 * Created by YAO on 2015/8/10.
 */
public class DistanceUtil {
    public double distance = -1;
    RoutePlanSearch routePlanSearch;
    public void getDistance(final double latitude1, final double longitude1, final double latitude2, final double longitude2,Context context,final TextView tv){
        distance = -1;
        routePlanSearch = RoutePlanSearch.newInstance();
        routePlanSearch.setOnGetRoutePlanResultListener(new OnGetRoutePlanResultListener() {
            @Override
            public void onGetWalkingRouteResult(WalkingRouteResult walkingRouteResult) {

            }

            @Override
            public void onGetTransitRouteResult(TransitRouteResult transitRouteResult) {
                if (transitRouteResult != null && transitRouteResult.getTaxiInfo() != null) {
                    distance = transitRouteResult.getTaxiInfo().getDistance();
                    double distance2 = Utils.getPoint2Double(Utils.getGeoDistance(longitude1, latitude1, longitude2, latitude2) / 1000);
                    if (distance / 1000 > 100) {
                        tv.setText(R.string.msg_distance_error);
                    }
                    else if(distance2 > distance){
                        distance = distance2 *1.2;
                        tv.setText(distance / 1000+"");
                        if(Constants.DEBUG)
                            tv.setText("直线距离（1.2倍数）:" +distance / 1000 +"");
                    }
                    else
                        tv.setText(Utils.getPoint2Double(distance / 1000) + "");
                    Log.e("gongjiao", "zui you ju li :" + distance);
                    routePlanSearch.destroy();
                } else {
                    Log.e("zuiyoulujin", "shibai le !");
                    distance = Utils.getPoint2Double(Utils.getGeoDistance(longitude1, latitude1, longitude2, latitude2) / 1000 *1.2);
                    if (distance > 100) {
                        tv.setText(R.string.msg_distance_error);
                    } else
                        tv.setText(distance + "");
                }
            }

            @Override
            public void onGetDrivingRouteResult(DrivingRouteResult drivingRouteResult) {

            }
        });
        LatLng startLatLng = new LatLng(latitude1,longitude1);
        LatLng endLatLng = new LatLng(latitude2, longitude2);
        PlanNode stNode = PlanNode.withLocation(startLatLng);
        PlanNode enNode = PlanNode.withLocation(endLatLng);
        routePlanSearch.transitSearch((new TransitRoutePlanOption())
                .from(stNode)
                .city(context.getResources().getString(R.string.label_city))
                .to(enNode));

    }
}
