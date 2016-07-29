package com.autosos.rescue.util;

import com.amap.api.maps.model.LatLng;
import com.amap.api.navi.AMapNavi;
import com.amap.api.navi.AMapNaviListener;
import com.amap.api.navi.enums.PathPlanningStrategy;
import com.amap.api.navi.model.AMapLaneInfo;
import com.amap.api.navi.model.AMapNaviCross;
import com.amap.api.navi.model.AMapNaviInfo;
import com.amap.api.navi.model.AMapNaviLocation;
import com.amap.api.navi.model.AMapNaviPath;
import com.amap.api.navi.model.AMapNaviTrafficFacilityInfo;
import com.amap.api.navi.model.AimLessModeCongestionInfo;
import com.amap.api.navi.model.AimLessModeStat;
import com.amap.api.navi.model.NaviInfo;
import com.amap.api.navi.model.NaviLatLng;
import com.autonavi.tbt.TrafficFacilityInfo;

import java.util.ArrayList;
import java.util.List;

/**
 * Created by Administrator on 2016/7/25.
 */
public class AddPath implements AMapNaviListener {

    private LatLng latLng1;
    private  LatLng latLng2;
    private AMapNavi aMapNavi;
    private OiUtil oiUtil = null;
    public static float dis_added = 0;

    public AddPath(LatLng latLng1 ,LatLng latLng2,AMapNavi aMapNavi) {

        this.latLng1 = latLng1;
        this.latLng2 = latLng2;
        this.aMapNavi = aMapNavi;
        List<NaviLatLng> startList = new ArrayList<NaviLatLng>();
        startList.add(new NaviLatLng(latLng1.latitude,latLng1.longitude));
        List<NaviLatLng> endList = new ArrayList<NaviLatLng>();
        endList.add(new NaviLatLng(latLng2.latitude,latLng2.longitude));
        List<NaviLatLng> wayList = new ArrayList<NaviLatLng>();
        this.aMapNavi.calculateDriveRoute(startList,endList,wayList, PathPlanningStrategy.DRIVING_DEFAULT);
        if (oiUtil == null) {

            oiUtil = new OiUtil();
        }
    }

    @Override
    public void onInitNaviFailure() {

    }

    @Override
    public void onInitNaviSuccess() {

    }

    @Override
    public void onStartNavi(int i) {

    }

    @Override
    public void onTrafficStatusUpdate() {

    }

    @Override
    public void onLocationChange(AMapNaviLocation aMapNaviLocation) {

    }

    @Override
    public void onGetNavigationText(int i, String s) {

    }

    @Override
    public void onEndEmulatorNavi() {

    }

    @Override
    public void onArriveDestination() {

    }

    @Override
    public void onCalculateRouteSuccess() {

        AMapNaviPath naviPath = aMapNavi.getNaviPath();
        if (naviPath == null) {
            return;
        }
        float route_distance = DistanceUtil.checkDistance(naviPath.getAllLength() / 1000f);
        List<NaviLatLng> naviLatLngs  = naviPath.getCoordList();
        for(NaviLatLng ll : naviLatLngs){

            oiUtil.writeJWD(ll.getLatitude(), ll.getLongitude(), oiUtil.path_drag);
        }

        dis_added = route_distance;

    }

    @Override
    public void onCalculateRouteFailure(int i) {

    }

    @Override
    public void onReCalculateRouteForYaw() {

    }

    @Override
    public void onReCalculateRouteForTrafficJam() {

    }

    @Override
    public void onArrivedWayPoint(int i) {

    }

    @Override
    public void onGpsOpenStatus(boolean b) {

    }

    @Override
    public void onNaviInfoUpdated(AMapNaviInfo aMapNaviInfo) {

    }

    @Override
    public void onNaviInfoUpdate(NaviInfo naviInfo) {

    }

    @Override
    public void OnUpdateTrafficFacility(AMapNaviTrafficFacilityInfo aMapNaviTrafficFacilityInfo) {

    }

    @Override
    public void OnUpdateTrafficFacility(TrafficFacilityInfo trafficFacilityInfo) {

    }

    @Override
    public void showCross(AMapNaviCross aMapNaviCross) {

    }

    @Override
    public void hideCross() {

    }

    @Override
    public void showLaneInfo(AMapLaneInfo[] aMapLaneInfos, byte[] bytes, byte[] bytes1) {

    }

    @Override
    public void hideLaneInfo() {

    }

    @Override
    public void onCalculateMultipleRoutesSuccess(int[] ints) {

    }

    @Override
    public void notifyParallelRoad(int i) {

    }

    @Override
    public void OnUpdateTrafficFacility(AMapNaviTrafficFacilityInfo[] aMapNaviTrafficFacilityInfos) {

    }

    @Override
    public void updateAimlessModeStatistics(AimLessModeStat aimLessModeStat) {

    }

    @Override
    public void updateAimlessModeCongestionInfo(AimLessModeCongestionInfo aimLessModeCongestionInfo) {

    }
}
