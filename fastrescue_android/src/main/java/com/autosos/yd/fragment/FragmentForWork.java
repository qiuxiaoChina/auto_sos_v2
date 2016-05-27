package com.autosos.yd.fragment;

import android.content.Context;
import android.content.Intent;
import android.graphics.BitmapFactory;
import android.graphics.Color;
import android.net.Uri;
import android.os.Bundle;
import android.os.Handler;
import android.os.Message;
import android.os.SystemClock;
import android.support.annotation.Nullable;
import android.support.v4.app.Fragment;
import android.support.v4.widget.TextViewCompat;
import android.util.DisplayMetrics;
import android.util.Log;
import android.view.Display;
import android.view.LayoutInflater;
import android.view.Menu;
import android.view.MenuInflater;
import android.view.MenuItem;
import android.view.MotionEvent;
import android.view.VelocityTracker;
import android.view.View;
import android.view.ViewGroup;
import android.view.WindowManager;
import android.widget.CompoundButton;
import android.widget.RelativeLayout;
import android.widget.Switch;
import android.widget.TextView;
import android.widget.Toast;

import com.amap.api.location.AMapLocation;
import com.amap.api.location.AMapLocationClient;
import com.amap.api.location.AMapLocationClientOption;
import com.amap.api.location.AMapLocationListener;
import com.amap.api.maps.AMap;
import com.amap.api.maps.AMapUtils;
import com.amap.api.maps.CameraUpdateFactory;
import com.amap.api.maps.LocationSource;
import com.amap.api.maps.MapView;
import com.amap.api.maps.TextureMapView;
import com.amap.api.maps.UiSettings;
import com.amap.api.maps.model.BitmapDescriptorFactory;
import com.amap.api.maps.model.CircleOptions;
import com.amap.api.maps.model.LatLng;
import com.amap.api.maps.model.LatLngBounds;
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
import com.amap.api.navi.enums.AimLessMode;
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
import com.autosos.yd.Constants;
import com.autosos.yd.R;
import com.autosos.yd.task.NewHttpPostTask;
import com.autosos.yd.task.OnHttpRequestListener;
import com.autosos.yd.util.DensityUtil;
import com.autosos.yd.util.DistanceUtil;
import com.autosos.yd.util.TTSController;
import com.autosos.yd.util.Utils;
import com.autosos.yd.view.MainActivity;
import com.autosos.yd.viewpager.ContentViewPager;

import org.json.JSONObject;

import java.math.BigDecimal;
import java.util.ArrayList;
import java.util.Date;
import java.util.HashMap;
import java.util.Iterator;
import java.util.List;
import java.util.Map;
import java.util.logging.LogRecord;


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
    private View head_map, head_map_tel_navi;
    //  private View menu_layout;
    private View menu;
    // private View startNavi;
    private View startRoute, stopNavi, checkRoute;
    TextureMapView mapView, mapViewForShow;
    private AMap amap, amapForShow;
    // 规划线路
    private RouteOverLay mRouteOverLay;

    private static ArrayList<LatLng> latLngs = new ArrayList<LatLng>();
    private UiSettings mUiSettings;
    private Marker marker;
    private TextView startNavi;
    private TextView distance_route, distance_moved;

    private Handler handler;

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
        mapViewForShow.onResume();
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
        mapViewForShow.onPause();
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
        mapViewForShow.onSaveInstanceState(outState);
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
        mapViewForShow.onDestroy();
        amap = null;
        amapForShow = null;
        mAMapNavi.stopNavi();
        mTtsManager.destroy();
        mAMapNavi.destroy();
    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        // Inflate the layout for this fragment
        Log.d(TAG, "createView");
        final View view = inflater.inflate(R.layout.fragment_fragment_for_work, null);
        mAMapNaviView = (AMapNaviView) view.findViewById(R.id.navi_view);
        mAMapNaviView.onCreate(savedInstanceState);
        mAMapNaviView.setAMapNaviViewListener(this);

        AMapNaviViewOptions options = mAMapNaviView.getViewOptions();
        options.setCrossDisplayEnabled(false);
        options.setTilt(0);
        options.setLayoutVisible(false);
        options.setRouteListButtonShow(true);
        //options.setLayoutVisible(true);
        mAMapNaviView.setViewOptions(options);


        switch_online = (TextView) view.findViewById(R.id.switch_online);
        head_map = view.findViewById(R.id.head_map);
        head_map.setOnClickListener(this);

        head_map_tel_navi = view.findViewById(R.id.head_map_tel_navi);
        startNavi = (TextView) view.findViewById(R.id.starNavi);
        startNavi.setOnClickListener(this);

        //  menu_layout = view.findViewById(R.id.menu_layout);
        // menu_layout.setOnTouchListener(this);

        menu = view.findViewById(R.id.menu);
        menu.setOnTouchListener(this);

        //startNavi = view.findViewById(R.id.start_navi);
        // startNavi.setOnClickListener(this);

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

        distance_route = (TextView) view.findViewById(R.id.distance_route);
        distance_moved = (TextView) view.findViewById(R.id.distance_moved);

        handler = new Handler() {
            @Override
            public void handleMessage(Message msg) {
                if (msg.what == 0) {
                    mAMapNaviView.displayOverview();
                    // mTtsManager.stopSpeaking();

                }else if(msg.what==1){
                    Log.d("start","尝试上线");
                    Map<String, Object> map = new HashMap<>();
                    map.put("lat", String.valueOf(mStartLatlng.getLatitude()));
                    map.put("lng", String.valueOf(mStartLatlng.getLongitude()));
                    new NewHttpPostTask(getActivity().getApplicationContext(), new OnHttpRequestListener() {
                        @Override
                        public void onRequestCompleted(Object obj) {
                            try {
                                JSONObject jsonObject = new JSONObject(obj.toString());
                                if (jsonObject.getString("result").equals("1")) {
                                    Log.d("start","上线成功");
                                } else {
                                    Toast.makeText(getActivity().getApplicationContext(), R.string.msg_change_error, Toast.LENGTH_LONG).show();
                                }
                            } catch (Exception e) {
                                e.printStackTrace();
                            }
                        }

                        @Override
                        public void onRequestFailed(Object obj) {

                            Toast.makeText(getActivity().getApplicationContext(), "网络环境不太好，更新订单失败", Toast.LENGTH_LONG).show();
                        }
                    }).execute(Constants.USER_START_WORK_URL, map);
                }
            }
        };



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
            Log.d(TAG, "get AmapForShow");
            amapForShow = mapViewForShow.getMap();
        }
    }


    @Override
    public void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);

        // mTtsManager.startSpeaking();
        mAMapNavi = AMapNavi.getInstance(this.getActivity().getApplicationContext());
        mAMapNavi.addAMapNaviListener(this);
        mAMapNavi.addAMapNaviListener(mTtsManager);
        mAMapNavi.setEmulatorNaviSpeed(200);
        Log.d(TAG, "create");

    }

    @Override
    public void onStartNavi(int i) {

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
    public void onArriveDestination() {

        latLngs.remove(latLngs.size() - 1);
        mAMapNavi.startAimlessMode(AimLessMode.NONE_DETECTED);
        AMapNaviViewOptions option = mAMapNaviView.getViewOptions();
        option.setPointToCenter(0.5, 0.5);
        mAMapNaviView.setViewOptions(option);
        //handler.sendEmptyMessage(0);


    }

    @Override
    public void onEndEmulatorNavi() {

        latLngs.remove(latLngs.size() - 1);
        distance_moved.setText("实际行进：" + DistanceUtil.checkDistance(dis_moved - last_distance) + "公里");
        AMapNaviViewOptions option = mAMapNaviView.getViewOptions();
        option.setPointToCenter(0.5, 0.5);
        mAMapNaviView.setViewOptions(option);
       // handler.sendEmptyMessage(0);
        Log.d("distance", "模拟导航结束");
    }


    @Override
    public void onCalculateRouteSuccess() {

        timeStamp = new Date().getTime();
        AMapNaviPath naviPath = mAMapNavi.getNaviPath();
        if (naviPath == null) {
            return;
        }

        ViewGroup.MarginLayoutParams margin2 = new ViewGroup.MarginLayoutParams(menu.getLayoutParams());
        margin2.setMargins(5, 0, 5, 0 - (menu.getLayoutParams().height / 4 * 3));
        RelativeLayout.LayoutParams layoutParams = new RelativeLayout.LayoutParams(margin2);
        layoutParams.addRule(RelativeLayout.ALIGN_PARENT_BOTTOM);
        menu.setLayoutParams(layoutParams);
        head_map.setVisibility(View.GONE);
        head_map_tel_navi.setVisibility(View.VISIBLE);
        float route_distance = DistanceUtil.checkDistance(naviPath.getAllLength() / 1000f);
        distance_route.setText("规划距离：" + route_distance + "公里");
        // menu_layout.setVisibility(View.VISIBLE);
        deactivate();
        mapView.setVisibility(View.GONE);
        mAMapNaviView.setVisibility(View.VISIBLE);
        mAMapNavi.startNavi(NaviType.GPS);
        handler.sendEmptyMessage(0);
        Log.d("distance", timeStamp + "");

    }

    //根据时间排轨迹点 通过onLocationChange实时补充
    private long timeStamp = 0;
    private float dis_moved = 0;
    private float last_distance = 0;

    @Override
    public void onNaviInfoUpdate(NaviInfo naviInfo) {
        long timeNow = new Date().getTime();
        LatLng currentLatLng = new LatLng(naviInfo.getCoord().getLatitude(), naviInfo.getCoord().getLongitude());
        if (timeNow > timeStamp) {

            if (latLngs.size() >= 2) {
                LatLng positionLatLng = latLngs.get(latLngs.size() - 1);
                int distance = (int) AMapUtils.calculateLineDistance(currentLatLng, positionLatLng);
                float f_dis = DistanceUtil.checkDistance(distance / 1000f);
                last_distance = f_dis;
                dis_moved += f_dis;
                distance_moved.setText("实际行进：" + DistanceUtil.checkDistance(dis_moved) + "公里");
            }

            Log.d("distance", timeStamp + ":  " + currentLatLng.latitude + "---" + currentLatLng.longitude + "---" + last_distance);
            timeStamp = timeNow;
            latLngs.add(currentLatLng);

        }

    }

    @Override
    public void onLocationChange(AMapNaviLocation aMapNaviLocation) {
        if (aMapNaviLocation.getAccuracy()<=50) {

            long timeNow = new Date().getTime();
            if (timeNow > timeStamp) {
                LatLng currentLatLng = new LatLng(aMapNaviLocation.getCoord().getLatitude(), aMapNaviLocation.getCoord().getLongitude());
                if (latLngs.size() > 2) {
                    LatLng positionLatLng = latLngs.get(latLngs.size() - 1);
                    int distance = (int) AMapUtils.calculateLineDistance(currentLatLng, positionLatLng);
                    float f_dis = DistanceUtil.checkDistance(distance / 1000f);
                    dis_moved += f_dis;
                    distance_moved.setText("实际行进：" + DistanceUtil.checkDistance(dis_moved) + "公里");
                }
                latLngs.add(currentLatLng);
               // Toast.makeText(getActivity(), "" + currentLatLng.longitude + "---" + currentLatLng.latitude, Toast.LENGTH_SHORT).show();
                timeStamp = timeNow;
            }
        }


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
    private float yDown;

    //记录手指移动时的横坐标。
    private float yMove;


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

            case R.id.menu:
                switch (event.getAction()) {

                    case MotionEvent.ACTION_DOWN:
                        yDown = event.getRawY();
                        Log.d("move", yDown + "down");
                        break;
                    case MotionEvent.ACTION_MOVE:
                        yMove = event.getRawY();
                        //Log.d("move",yMove+"");
                        ViewGroup.MarginLayoutParams margin2 = new ViewGroup.MarginLayoutParams(menu.getLayoutParams());
                        if (yDown - yMove > 0) {
                            int move = (int) (yDown - yMove);
                            // Log.d("move",margin2.bottomMargin+":"+margin2.topMargin+":margin");
                            if (yDown - yMove <= 250) {

                                margin2.setMargins(5, 0, 5, move - 250);

                            } else {

                                margin2.setMargins(5, 0, 5, 0);
                            }
                            RelativeLayout.LayoutParams layoutParams = new RelativeLayout.LayoutParams(margin2);
                            layoutParams.addRule(RelativeLayout.ALIGN_PARENT_BOTTOM);
                            menu.setLayoutParams(layoutParams);

                        } else if (yDown - yMove < 0) {

                            int move = (int) (yDown - yMove);
                            ;
                            Log.d("move", menu.getLayoutParams().height + "");
                            if (yDown - yMove > -75) {

                                margin2.setMargins(5, 0, 5, move * 2);
                            } else {

                                margin2.setMargins(5, 0, 5, 0 - (menu.getLayoutParams().height / 4 * 3));
                            }

                            RelativeLayout.LayoutParams layoutParams = new RelativeLayout.LayoutParams(margin2);
                            layoutParams.addRule(RelativeLayout.ALIGN_PARENT_BOTTOM);
                            menu.setLayoutParams(layoutParams);

                        }

                    default:
                        break;
                }


                break;

        }
        return true;
    }


    @Override
    public void onMapLoaded() {
        // Toast.makeText(getActivity().getApplicationContext(),"map load over",Toast.LENGTH_SHORT).show();
        amap.moveCamera(CameraUpdateFactory.zoomTo(16));
        amap.moveCamera(CameraUpdateFactory.changeLatLng(new LatLng(29.807633, 121.555842)));
    }

    @Override
    public void onClick(View v) {
        switch (v.getId()) {
            case R.id.starNavi:
                if ("开启导航".equals(startNavi.getText())) {

                    mTtsManager = TTSController.getInstance(this.getActivity().getApplicationContext());
                    mTtsManager.init();
                    mTtsManager.startSpeaking();
                    mAMapNavi.addAMapNaviListener(mTtsManager);
                    mAMapNaviView.recoverLockMode();
                    startNavi.setText("停止导航");
                } else {
                    mAMapNaviView.displayOverview();
                    mTtsManager.stopSpeaking();
                    mAMapNavi.removeAMapNaviListener(mTtsManager);
                    mTtsManager = null;
                    startNavi.setText("开启导航");
                }

                break;
            case R.id.head_map:

                if ("离线>>>".equals(switch_online.getText())) {
                    handler.sendEmptyMessage(1);
                    getActivity().findViewById(R.id.content_radiogroup).setVisibility(View.GONE);
                    init();
                    setUpMap();
                    menu.setVisibility(View.VISIBLE);
                    switch_online.setText("在线>>>");



                } else {
                    getActivity().findViewById(R.id.content_radiogroup).setVisibility(View.VISIBLE);
                    //menu_layout.setVisibility(View.GONE);
                    menu.setVisibility(View.GONE);
                    switch_online.setText("离线>>>");
                    deactivate();
                    amap.clear();
                    amap = null;
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
                mapViewForShow.setVisibility(View.GONE);
                amap.clear(true);
                setUpMap();
                head_map.setVisibility(View.VISIBLE);
                head_map_tel_navi.setVisibility(View.GONE);
                break;
            case R.id.check_route:
                try {
                    amap.clear(true);
                    amapForShow.clear(true);
                    mapViewForShow.setVisibility(View.VISIBLE);
                    mapView.setVisibility(View.GONE);

                    boolean flag = true;
                    List<LatLng> newLatLngs = new ArrayList<LatLng>();
                    for (LatLng l : latLngs) {
                        if (flag) {

                            newLatLngs.add(l);
                            flag = false;

                        } else {

                            flag = true;
                        }
                    }

                    if (newLatLngs.size() % 2 == 0) {
                        //amapForShow.moveCamera(CameraUpdateFactory.newLatLngBounds(latLngBounds,13));
                        amapForShow.moveCamera(CameraUpdateFactory.newLatLngZoom(newLatLngs.get(newLatLngs.size() / 2), 13));

                    } else {
                        //amapForShow.moveCamera(CameraUpdateFactory.newLatLngBounds(latLngBounds,13));
                        amapForShow.moveCamera(CameraUpdateFactory.newLatLngZoom(newLatLngs.get((newLatLngs.size() - 1) / 2), 13));

                    }

                    for (int i = 0; i < newLatLngs.size(); i++) {
                        List<LatLng> l = new ArrayList<LatLng>();

                        l.add(newLatLngs.get(i));
                        l.add(newLatLngs.get(i + 1));
                        Polyline polyLine = amapForShow.addPolyline((new PolylineOptions()).color(Color.BLUE).width(10));
                        polyLine.setPoints(l);
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
                    // menu.setVisibility(View.GONE);
                    //  menu_layout.setVisibility(View.VISIBLE);
                    dis_moved = 0;
                    latLngs.clear();

                } catch (Exception e) {

                    Log.i(TAG, e.getMessage());
                }

                break;

        }
    }


}
