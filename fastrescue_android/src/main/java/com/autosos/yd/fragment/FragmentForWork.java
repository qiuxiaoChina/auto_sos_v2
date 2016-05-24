package com.autosos.yd.fragment;

import android.content.Context;
import android.content.Intent;
import android.graphics.BitmapFactory;
import android.graphics.Color;
import android.net.Uri;
import android.os.Bundle;
import android.support.annotation.Nullable;
import android.support.v4.app.Fragment;
import android.support.v4.widget.TextViewCompat;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.Menu;
import android.view.MenuInflater;
import android.view.MenuItem;
import android.view.MotionEvent;
import android.view.VelocityTracker;
import android.view.View;
import android.view.ViewGroup;
import android.widget.CompoundButton;
import android.widget.Switch;
import android.widget.TextView;
import android.widget.Toast;

import com.amap.api.location.AMapLocation;
import com.amap.api.location.AMapLocationClient;
import com.amap.api.location.AMapLocationClientOption;
import com.amap.api.location.AMapLocationListener;
import com.amap.api.maps.AMap;
import com.amap.api.maps.CameraUpdateFactory;
import com.amap.api.maps.LocationSource;
import com.amap.api.maps.MapView;
import com.amap.api.maps.TextureMapView;
import com.amap.api.maps.UiSettings;
import com.amap.api.maps.model.BitmapDescriptorFactory;
import com.amap.api.maps.model.CircleOptions;
import com.amap.api.maps.model.LatLng;
import com.amap.api.maps.model.Marker;
import com.amap.api.maps.model.MarkerOptions;
import com.amap.api.maps.model.MyLocationStyle;
import com.amap.api.maps.model.PolygonOptions;
import com.amap.api.maps.model.Polyline;
import com.amap.api.maps.model.PolylineOptions;
import com.amap.api.maps.overlay.DrivingRouteOverlay;
import com.amap.api.navi.AMapNavi;
import com.amap.api.navi.AMapNaviListener;
import com.amap.api.navi.AMapNaviView;
import com.amap.api.navi.AMapNaviViewListener;
import com.amap.api.navi.AMapNaviViewOptions;
import com.amap.api.navi.enums.NaviType;
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
import com.amap.api.navi.view.RouteOverLay;
import com.amap.api.services.core.LatLonPoint;
import com.amap.api.services.route.DrivePath;
import com.autonavi.tbt.TrafficFacilityInfo;
import com.autosos.yd.R;
import com.autosos.yd.util.TTSController;
import com.autosos.yd.view.MainActivity;
import com.autosos.yd.viewpager.ContentViewPager;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.Iterator;
import java.util.List;


public class FragmentForWork extends BasicFragment {

    private String TAG = this.getClass().getSimpleName();
    private static FragmentForWork fragment = null;
    AMapNaviView mAMapNaviView;
    AMapNavi mAMapNavi;
    TTSController mTtsManager;
    NaviLatLng mEndLatlng = new NaviLatLng(29.825846, 121.432765);
    NaviLatLng mStartLatlng = new NaviLatLng(29.807633, 121.555842);
    List<NaviLatLng> mStartList = new ArrayList<NaviLatLng>();
    List<NaviLatLng> mEndList = new ArrayList<NaviLatLng>();
    List<NaviLatLng> mWayPointList;
    private Marker mStartMarker;
    private Marker mWayMarker;
    private Marker mEndMarker;

    private OnLocationChangedListener mListener;

    private TextView switch_online;
    private View head_map;
    private View menu_layout;
    private View menu;
    private View startNavi;
    private View startRoute, stopNavi, checkRoute;
    TextureMapView mapView,mapViewForShow;
    private AMap amap,amapForShow;
    // 规划线路
    private RouteOverLay mRouteOverLay;

    private static ArrayList<LatLng> latLngs = new ArrayList<LatLng>();
    private UiSettings mUiSettings;
    private Marker marker;


    public static Fragment newInstance() {
        if (fragment == null) {
            synchronized (FragmentForWork.class) {
                if (fragment == null) {
                    fragment = new FragmentForWork();

                }
            }
        }
        return fragment;
    }

    public FragmentForWork() {

    }

    /**
     * 方法必须重写
     */
    @Override
    public void onResume() {
        super.onResume();
        mapView.onResume();
        //mapView.setVisibility(View.VISIBLE);
        Log.d(TAG, "resume");

    }

    /**
     * 方法必须重写
     */
    @Override
    public void onPause() {
        super.onPause();
        mapView.onPause();
        //mapView.setVisibility(View.GONE);
        Log.d(TAG, "pause");
        //deactivate();
    }

    /**
     * 方法必须重写
     */
    @Override
    public void onSaveInstanceState(Bundle outState) {
        super.onSaveInstanceState(outState);
        mapView.onSaveInstanceState(outState);
    }

    /**
     * 方法必须重写
     */
    @Override
    public void onDestroy() {
        super.onDestroy();
        Log.i(TAG, "destroy");
        deactivate();
        mapView.onDestroy();
        amap = null;
        mAMapNavi.stopNavi();
        mTtsManager.destroy();
        mAMapNavi.destroy();
    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        // Inflate the layout for this fragment
        Log.d(TAG, "createView");
        View view = inflater.inflate(R.layout.fragment_fragment_for_work, null);
        mAMapNaviView = (AMapNaviView) view.findViewById(R.id.navi_view);
        mAMapNaviView.onCreate(savedInstanceState);
        mAMapNaviView.setAMapNaviViewListener(this);

        AMapNaviViewOptions options = mAMapNaviView.getViewOptions();
        options.setCrossDisplayEnabled(false);
        options.setTilt(0);
        mAMapNaviView.setViewOptions(options);


        switch_online = (TextView) view.findViewById(R.id.switch_online);
        head_map = view.findViewById(R.id.head_map);
        head_map.setOnTouchListener(this);

        menu_layout = view.findViewById(R.id.menu_layout);
        menu_layout.setOnTouchListener(this);

        menu = view.findViewById(R.id.menu);
        menu.setOnTouchListener(this);

        startNavi = view.findViewById(R.id.start_navi);
        startNavi.setOnClickListener(this);

        startRoute = view.findViewById(R.id.start_route);
        startRoute.setOnClickListener(this);

        stopNavi = view.findViewById(R.id.stop_navi);
        stopNavi.setOnClickListener(this);

        checkRoute = view.findViewById(R.id.check_route);
        checkRoute.setOnClickListener(this);

        mapView = (TextureMapView) view.findViewById(R.id.mapview);
        mapView.onCreate(savedInstanceState);// 此方法必须重写

        mapViewForShow = (TextureMapView) view.findViewById(R.id.mapviewForShow);
        mapViewForShow.onCreate(savedInstanceState);// 此方法必须重写
        init();
        mRouteOverLay = new RouteOverLay(amap, null);

        mStartList.add(mStartLatlng);
        return view;
    }

    /**
     * 初始化AMap对象
     */
    private void init() {
        Log.d(TAG, "init");
        if (amap == null) {
            Log.d(TAG, "get Amap");
            amap = mapView.getMap();
            amap.setOnMapLoadedListener(this);
        }

        if (amapForShow == null) {
            Log.d(TAG, "get Amap");
            amapForShow = mapViewForShow.getMap();
        }
    }


    @Override
    public void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        mTtsManager = TTSController.getInstance(this.getActivity().getApplicationContext());
        mTtsManager.init();
        // mTtsManager.startSpeaking();
        mAMapNavi = AMapNavi.getInstance(this.getActivity().getApplicationContext());
        mAMapNavi.addAMapNaviListener(this);
        mAMapNavi.addAMapNaviListener(mTtsManager);
        mAMapNavi.setEmulatorNaviSpeed(300);
        Log.d(TAG, "create");

    }


    @Override
    public void onNaviSetting() {

    }

    @Override
    public void onNaviCancel() {
        // mAMapNavi.stopNavi();
//        mAMapNaviView.setVisibility(View.GONE);
//        mapView.setVisibility(View.VISIBLE);
//        menu_layout.setVisibility(View.VISIBLE);
        Toast.makeText(getActivity().getApplicationContext(), "现在不能关闭导航。", Toast.LENGTH_SHORT).show();

    }

    @Override
    public boolean onNaviBackClick() {
        return true;
    }


    @Override
    public void onCalculateRouteSuccess() {
        AMapNaviPath naviPath = mAMapNavi.getNaviPath();

        if (naviPath == null) {
            return;
        }
        amap.moveCamera(CameraUpdateFactory.newLatLngZoom(new LatLng(mStartLatlng.getLatitude(), mStartLatlng.getLongitude()),16));
        // 获取路径规划线路，显示到地图上
        mRouteOverLay.setAMapNaviPath(naviPath);
        mRouteOverLay.addToMap();

    }


    @Override
    public void onNaviInfoUpdate(NaviInfo naviInfo) {

        latLngs.add(new LatLng(naviInfo.getCoord().getLatitude(), naviInfo.getCoord().getLongitude()));

        //Toast.makeText(getActivity().getApplicationContext(),naviInfo.getCoord().getLatitude()+"::::"+naviInfo.getCoord().getLongitude()+"::::"+naviInfo.getCameraCoord().getLongitude()+"::::"+naviInfo.getCameraCoord().getLatitude(), Toast.LENGTH_SHORT).show();
    }


    private AMapLocationClient locationClient = null;
    private AMapLocationClientOption locationOption = null;

    // private boolean mapClickStartReady = true;
    // private boolean mapClickEndReady = false;
    private boolean mapClickEndReady = true;

    /**
     * 设置一些amap的属性
     */
    private void setUpMap() {
        Log.d(TAG, "setUpmap");
        // 自定义系统定位小蓝点
        MyLocationStyle myLocationStyle = new MyLocationStyle();
        myLocationStyle.myLocationIcon(BitmapDescriptorFactory
                .fromResource(R.drawable.location_marker));// 设置小蓝点的图标
//        myLocationStyle.strokeColor(Color.BLACK);// 设置圆形的边框颜色
//        myLocationStyle.radiusFillColor(Color.argb(100, 0, 0, 180));// 设置圆形的填充颜色
//        // myLocationStyle.anchor(int,int)//设置小蓝点的锚点
//        myLocationStyle.strokeWidth(1.0f);// 设置圆形的边框粗细
        amap.setMyLocationStyle(myLocationStyle);
        amap.setLocationSource(this);// 设置定位监听
        amap.getUiSettings().setMyLocationButtonEnabled(true);// 设置默认定位按钮是否显示
        amap.moveCamera(CameraUpdateFactory.zoomTo(16));
        //amap.moveCamera(CameraUpdateFactory.changeTilt(0));
        amap.setMyLocationEnabled(true);// 设置为true表示显示定位层并可触发定位，false表示隐藏定位层并不可触发定位，默认是false
        // aMap.setMyLocationType()
        // amap.setMyLocationType(AMap.MAP_TYPE_NAVI);
        // 初始化Marker添加到地图
        mStartMarker = amap.addMarker(new MarkerOptions()
                .icon(BitmapDescriptorFactory.fromBitmap(BitmapFactory
                        .decodeResource(getResources(), R.drawable.start))));
        mWayMarker = amap.addMarker(new MarkerOptions()
                .icon(BitmapDescriptorFactory.fromBitmap(BitmapFactory
                        .decodeResource(getResources(), R.drawable.way))));

        // amap.setOnMarkerClickListener(this);
        amap.setOnMapClickListener(new AMap.OnMapClickListener() {
            @Override
            public void onMapClick(LatLng latLng) {

                mRouteOverLay.removeFromMap();
//                if (mapClickStartReady && !mapClickEndReady) {
//                    mStartLatlng = new NaviLatLng(latLng.latitude, latLng.longitude);
//                    mStartMarker.setPosition(latLng);
//                    mStartList.clear();
//                    mStartList.add(mStartLatlng);
//                    mapClickStartReady=false;
//                    mapClickEndReady = true;
//                    return;
//                }


//                if (mapClickEndReady && !mapClickStartReady) {
//                    mEndLatlng = new NaviLatLng(latLng.latitude, latLng.longitude);
//                    mEndMarker.setPosition(latLng);
//                    mEndList.clear();
//                    mEndList.add(mEndLatlng);
//                    mapClickStartReady=true;
//                    mapClickEndReady=false;
//                }
                if (mapClickEndReady) {
                    mEndMarker = amap.addMarker(new MarkerOptions()
                            .icon(BitmapDescriptorFactory.fromBitmap(BitmapFactory
                                    .decodeResource(getResources(), R.drawable.end))));
                    mEndLatlng = new NaviLatLng(latLng.latitude, latLng.longitude);
                    mEndMarker.setPosition(latLng);
                    mEndList.clear();
                    mEndList.add(mEndLatlng);
                    mapClickEndReady = false;
                } else {
                    mEndMarker.remove();
                    mapClickEndReady = true;
                }
            }
        });


    }


    @Override
    public void onLocationChanged(AMapLocation aMapLocation) {

        if (mListener != null && aMapLocation != null) {
            if (aMapLocation != null
                    && aMapLocation.getErrorCode() == 0) {

                mStartList.clear();
                mStartLatlng = new NaviLatLng(aMapLocation.getLatitude(), aMapLocation.getLongitude());
                mStartList.add(mStartLatlng);
                amap.moveCamera(CameraUpdateFactory.changeTilt(0));
                mListener.onLocationChanged(aMapLocation);// 显示系统小蓝点
//                if(marker!=null)marker.destroy();
//                LatLng latLng = new LatLng(aMapLocation.getLatitude(),
//                        aMapLocation.getLongitude());
//                // 定位成功后把地图移动到当前可视区域内
//
//                amap.moveCamera(CameraUpdateFactory.newLatLngZoom(latLng,16));
//                // 自定义定位成功后的小圆点
//                marker=amap.addMarker(new MarkerOptions().position(latLng)
//                        .anchor(0.5f,0.5f).icon(BitmapDescriptorFactory
//                        .fromResource(R.drawable.location_marker)));

                //Toast.makeText(this.getActivity().getApplicationContext(), aMapLocation.getLatitude()+"", Toast.LENGTH_SHORT).show();
            } else {
                String errText = "定位失败," + aMapLocation.getErrorCode() + ": " + aMapLocation.getErrorInfo();
                //  Log.d(TAG,errText);
                //Toast.makeText(this.getActivity().getApplicationContext(), errText, Toast.LENGTH_SHORT).show();
            }
        }

    }

    @Override
    public void activate(OnLocationChangedListener onLocationChangedListener) {

        mListener = onLocationChangedListener;
        if (locationClient == null) {
            locationClient = new AMapLocationClient(this.getActivity());
            locationOption = new AMapLocationClientOption();
            //设置定位监听
            locationClient.setLocationListener(this);
            //设置为高精度定位模式
            locationOption.setLocationMode(AMapLocationClientOption.AMapLocationMode.Hight_Accuracy);
            //设置定位参数
            locationClient.setLocationOption(locationOption);
            // 此方法为每隔固定时间会发起一次定位请求，为了减少电量消耗或网络流量消耗，
            // 注意设置合适的定位时间的间隔（最小间隔支持为2000ms），并且在合适时间调用stopLocation()方法来取消定位请求
            // 在定位结束后，在合适的生命周期调用onDestroy()方法
            // 在单次定位情况下，定位无论成功与否，都无需调用stopLocation()方法移除请求，定位sdk内部会移除
            locationClient.startLocation();
        }

    }

    @Override
    public void deactivate() {


        mListener = null;
        if (locationClient != null) {
            locationClient.stopLocation();
            locationClient.onDestroy();
        }
        locationClient = null;

    }

    //手指向右滑动时的最小速度
    private static final int XSPEED_MIN = 200;

    //手指向右滑动时的最小距离
    private static final int XDISTANCE_MIN = 30;


    //记录手指按下时的横坐标。
    private float xDown;

    //记录手指移动时的横坐标。
    private float xMove;


    //用于计算手指滑动的速度。
    private VelocityTracker mVelocityTracker;

    /**
     * 创建VelocityTracker对象，并将触摸content界面的滑动事件加入到VelocityTracker当中。
     *
     * @param event
     */
    private void createVelocityTracker(MotionEvent event) {
        if (mVelocityTracker == null) {
            mVelocityTracker = VelocityTracker.obtain();
        }
        mVelocityTracker.addMovement(event);
    }

    /**
     * 回收VelocityTracker对象。
     */
    private void recycleVelocityTracker() {
        mVelocityTracker.recycle();
        mVelocityTracker = null;
    }

    /**
     * 获取手指在content界面滑动的速度。
     *
     * @return 滑动速度，以每秒钟移动了多少像素值为单位。
     */
    private int getScrollVelocity() {
        mVelocityTracker.computeCurrentVelocity(1000);
        int velocity = (int) mVelocityTracker.getXVelocity();
        return Math.abs(velocity);
    }

    @Override
    public boolean onTouch(View v, MotionEvent event) {
        switch (v.getId()) {

            case R.id.menu_layout:
                if (event.getAction() == MotionEvent.ACTION_UP) {

                    menu.setVisibility(View.VISIBLE);
                    menu_layout.setVisibility(View.GONE);


                }
                break;
            case R.id.menu:
                switch (event.getAction()) {

                    case MotionEvent.ACTION_UP:
                        menu.setVisibility(View.GONE);
                        menu_layout.setVisibility(View.VISIBLE);
                        break;


                    default:
                        break;
                }


                break;

            case R.id.head_map:
                createVelocityTracker(event);
                //ContentViewPager cvp = (ContentViewPager) getActivity().findViewById(R.id.content_viewpager);
                switch (event.getAction()) {
                    case MotionEvent.ACTION_DOWN:
                        xDown = event.getRawX();

                        break;

                    case MotionEvent.ACTION_UP:
                        break;
                    case MotionEvent.ACTION_MOVE:
                        xMove = event.getRawX();
                        //活动的距离
                        int distanceX = (int) (xMove - xDown);

                        //获取顺时速度
                        int xSpeed = getScrollVelocity();

                        //当滑动的距离大于我们设定的最小距离且滑动的瞬间速度大于我们设定的速度时，返回到上一个activity
                        if (distanceX > XDISTANCE_MIN && xSpeed > XSPEED_MIN) {
                            if ("离线>>>".equals(switch_online.getText())) {

                                getActivity().findViewById(R.id.content_radiogroup).setVisibility(View.GONE);

                                //  cvp.setScrollble(false);
                                // amap.clear();
                                init();
                                setUpMap();
                                //  mapView.invalidate();
                                //amap.reloadMap();
                                menu_layout.setVisibility(View.VISIBLE);
                                switch_online.setText("在线>>>");

                            } else {
                                getActivity().findViewById(R.id.content_radiogroup).setVisibility(View.VISIBLE);
                                menu_layout.setVisibility(View.GONE);
                                menu.setVisibility(View.GONE);
                                switch_online.setText("离线>>>");
                                deactivate();
                                amap.clear();
                                amap = null;
                            }

                            recycleVelocityTracker();
                            break;

                        }

                        break;


                    default:
                        break;
                }
                break;

        }
        return true;
    }


    @Override
    public void onMapLoaded() {
        amap.moveCamera(CameraUpdateFactory.zoomTo(16));
        amap.moveCamera(CameraUpdateFactory.changeLatLng(new LatLng(29.807633, 121.555842)));
    }

    @Override
    public void onClick(View v) {
        switch (v.getId()) {
            case R.id.start_navi:
                latLngs.clear();
                //route_latLngs.clear();
                // navi_route_latLngs.clear();

                if (mStartList.isEmpty() || mEndList.isEmpty()) {

                    Toast.makeText(getActivity().getApplicationContext(), "设定起点和终点", Toast.LENGTH_SHORT).show();
                } else {

                    menu.setVisibility(View.GONE);
                    menu_layout.setVisibility(View.VISIBLE);
                    deactivate();
                    amap.animateCamera(CameraUpdateFactory.zoomTo(20), 2000, new AMap.CancelableCallback() {


                        @Override
                        public void onFinish() {


                            mapView.setVisibility(View.GONE);
                            mAMapNaviView.setVisibility(View.VISIBLE);
                            mAMapNavi.startNavi(NaviType.EMULATOR);

                        }

                        @Override
                        public void onCancel() {

                        }
                    });


                }

                break;
            case R.id.start_route:
                amap.clear(true);
                mapViewForShow.setVisibility(View.GONE);
                mapView.setVisibility(View.VISIBLE);
                if (mStartList.isEmpty()) {

                    Toast.makeText(getActivity().getApplicationContext(), "没有定位到起点", Toast.LENGTH_SHORT).show();
                } else {

                    mAMapNavi.calculateDriveRoute(mStartList, mEndList, mWayPointList, PathPlanningStrategy.DRIVING_DEFAULT);
                }

                break;

            case R.id.stop_navi:
                mAMapNavi.stopNavi();
                mAMapNaviView.setVisibility(View.GONE);
                mapView.setVisibility(View.VISIBLE);
                amap.clear(true);
                setUpMap();
//                // 初始化Marker添加到地图
//                mStartMarker = amap.addMarker(new MarkerOptions()
//                        .icon(BitmapDescriptorFactory.fromBitmap(BitmapFactory
//                                .decodeResource(getResources(), R.drawable.start))));
//                mWayMarker = amap.addMarker(new MarkerOptions()
//                        .icon(BitmapDescriptorFactory.fromBitmap(BitmapFactory
//                                .decodeResource(getResources(), R.drawable.way))));
//                mEndMarker = amap.addMarker(new MarkerOptions()
//                        .icon(BitmapDescriptorFactory.fromBitmap(BitmapFactory
//                                .decodeResource(getResources(), R.drawable.end))));
                break;
            case R.id.check_route:

                try {

                    amap.clear(true);
                    amapForShow.clear(true);
                    mapViewForShow.setVisibility(View.VISIBLE);
                    mapView.setVisibility(View.GONE);
                    boolean flag = true;
                    List<LatLng> newLatLngs = new ArrayList<LatLng>();
                    for(LatLng l : latLngs){
                        if(flag){

                            newLatLngs.add(l);
                            flag = false;

                        }else{

                            flag = true;
                        }
                    }

                    if (newLatLngs.size() % 2 == 0) {
                        amapForShow.moveCamera(CameraUpdateFactory.newLatLngZoom(latLngs.get(latLngs.size()/2),13));
                        for (int i = 0; i < newLatLngs.size() - 2; i++) {
                            List<LatLng> l = new ArrayList<LatLng>();

                                l.add(latLngs.get(i));
                                l.add(latLngs.get(i+1));
                            Polyline polyLine = amapForShow.addPolyline((new PolylineOptions()).color(Color.BLUE).width(10));
                            polyLine.setPoints(l);
                        }

                    } else {
                        amapForShow.moveCamera(CameraUpdateFactory.newLatLngZoom(latLngs.get((latLngs.size()-1)/2),13));

                        for (int i = 0; i < newLatLngs.size() - 3; i++) {
                            List<LatLng> l = new ArrayList<LatLng>();

                                l.add(latLngs.get(i));
                                l.add(latLngs.get(i+1));

                            Polyline polyLine = amapForShow.addPolyline((new PolylineOptions()).color(Color.BLUE).width(10));
                            polyLine.setPoints(l);
                        }

                    }

                    // 初始化Marker添加到地图
                    mStartMarker = amap.addMarker(new MarkerOptions()
                            .icon(BitmapDescriptorFactory.fromBitmap(BitmapFactory
                                    .decodeResource(getResources(), R.drawable.start))));
                    mWayMarker = amap.addMarker(new MarkerOptions()
                            .icon(BitmapDescriptorFactory.fromBitmap(BitmapFactory
                                    .decodeResource(getResources(), R.drawable.way))));
                    mEndMarker = amap.addMarker(new MarkerOptions()
                            .icon(BitmapDescriptorFactory.fromBitmap(BitmapFactory
                                    .decodeResource(getResources(), R.drawable.end))));
                    menu.setVisibility(View.GONE);
                    menu_layout.setVisibility(View.VISIBLE);
                }catch (Exception e){

                    Log.i(TAG,e.getMessage());
                }

                break;

        }
    }


}
