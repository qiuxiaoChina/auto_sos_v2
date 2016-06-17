package com.autosos.yd.fragment;

import android.app.AlertDialog;
import android.app.Dialog;
import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.DialogInterface;
import android.content.Intent;
import android.content.IntentFilter;
import android.content.SharedPreferences;
import android.graphics.BitmapFactory;
import android.graphics.Color;
import android.graphics.Point;
import android.media.MediaPlayer;
import android.media.RingtoneManager;
import android.net.Uri;
import android.os.Bundle;
import android.os.Handler;
import android.os.IBinder;
import android.os.Message;
import android.os.RemoteException;
import android.os.SystemClock;
import android.support.annotation.Nullable;
import android.support.v4.app.Fragment;
import android.support.v4.widget.TextViewCompat;
import android.text.Layout;
import android.text.method.ScrollingMovementMethod;
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
import android.view.Window;
import android.view.WindowManager;
import android.view.animation.Animation;
import android.view.animation.AnimationUtils;
import android.widget.Button;
import android.widget.CompoundButton;
import android.widget.ImageView;
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
import com.amap.api.maps.model.Circle;
import com.amap.api.maps.model.CircleOptions;
import com.amap.api.maps.model.LatLng;
import com.amap.api.maps.model.LatLngBounds;
import com.amap.api.maps.model.Marker;
import com.amap.api.maps.model.MarkerOptions;
import com.amap.api.maps.model.MarkerOptionsCreator;
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
import com.autosos.yd.Layout.MyButton;
import com.autosos.yd.Layout.MyRaletiveLayout;
import com.autosos.yd.R;
import com.autosos.yd.task.NewHttpPostTask;
import com.autosos.yd.task.OnHttpRequestListener;
import com.autosos.yd.util.DensityUtil;
import com.autosos.yd.util.DistanceUtil;
import com.autosos.yd.util.JSONUtil;
import com.autosos.yd.util.Utils;
import com.autosos.yd.view.MainActivity;
import com.autosos.yd.view.NewTakePhotoActivity;
import com.autosos.yd.viewpager.ContentViewPager;
import com.autosos.yd.widget.RoundProgressBar;
import com.iflytek.cloud.SpeechConstant;
import com.iflytek.cloud.SpeechError;
import com.iflytek.cloud.SpeechRecognizer;
import com.iflytek.cloud.SpeechSynthesizer;
import com.iflytek.cloud.SynthesizerListener;
import com.iflytek.thridparty.G;


import org.json.JSONException;
import org.json.JSONObject;

import java.io.IOException;
import java.math.BigDecimal;
import java.util.ArrayList;
import java.util.Date;
import java.util.HashMap;
import java.util.Iterator;
import java.util.List;
import java.util.Map;
import java.util.Timer;
import java.util.TimerTask;
import java.util.logging.LogRecord;


public class FragmentForWork extends BasicFragment {

    private String TAG = this.getClass().getSimpleName();
    private static FragmentForWork fragment = null;
    AMapNaviView mAMapNaviView;
    AMapNavi mAMapNavi;
    //TTSController mTtsManager;
    NaviLatLng mEndLatlng = new NaviLatLng(29.825846, 121.432765);
    NaviLatLng mStartLatlng = new NaviLatLng(29.808191, 121.556015);
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
    private View stopNavi, checkRoute;
    TextureMapView mapView, mapViewForShow;
    private AMap amap, amapForShow;
    // 规划线路
    private RouteOverLay mRouteOverLay;

    private static ArrayList<LatLng> latLngs = new ArrayList<LatLng>();
    private UiSettings mUiSettings;
    private Marker marker;
    private Button startNavi, coursePreview, cancelOrder;
    private TextView distance_route, distance_moved;

    private Handler handler;

    private MyBroadcastReciever myBroadcastReciever;
    private View mainPage;
    private MyRaletiveLayout newOrder;
    //  private Button intoOrder;
    //private RoundProgressBar roundProgressBar;
    private Timer timer;
    private int progress = 0;
    private boolean isOnline = false;
    private View closeNewOrder;
    private View intoOrder;
    private boolean canGetNewOrder = true;
    private TextView destination;

    private SpeechSynthesizer mTts = null;
    private boolean canCountGPSPoint = false;
    private View progressBar;
    private ImageView takePhoto;

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
        init();
        setUpMap();
        //mapView.setVisibility(View.VISIBLE);
        SharedPreferences sp = getActivity().getSharedPreferences("online", Context.MODE_PRIVATE);
        boolean online = sp.getBoolean("online", false);
        SharedPreferences sp1 = getActivity().getSharedPreferences("newOrderComing", Context.MODE_PRIVATE);
        boolean newOrderComing = sp1.getBoolean("newOrderComing", false);
        handler.sendEmptyMessage(1);
        progressBar.setVisibility(View.VISIBLE);
        if (newOrderComing) {

                Animation animation = AnimationUtils.loadAnimation(getActivity().getApplicationContext(),
                        R.anim.show);
                newOrder.setVisibility(View.VISIBLE);
                newOrder.startAnimation(animation);
                head_map.setVisibility(View.GONE);
                getActivity().findViewById(android.R.id.tabhost).setVisibility(View.GONE);
                SharedPreferences sp3 = getActivity().getSharedPreferences("end", Context.MODE_PRIVATE);
                String end_lat = sp3.getString("end_lat", null);
                String end_lon = sp3.getString("end_lon", null);
                String destination = sp3.getString("destination", null);
                if ((end_lat != null && !"".equals(end_lat)) && (end_lon != null && !"".equals(end_lon)) && destination != null) {
                    double lat = Double.parseDouble(end_lat);
                    double lon = Double.parseDouble(end_lon);
                    NaviLatLng naviLatLng = new NaviLatLng(lat, lon);
                    mEndList.clear();
                    mEndList.add(naviLatLng);
                    s_destination = destination;

                }
            }

        if (mAMapNavi == null) {

            mAMapNavi = AMapNavi.getInstance(this.getActivity().getApplicationContext());
            mAMapNavi.addAMapNaviListener(this);
            //   mAMapNavi.addAMapNaviListener(mTtsManager);
            mAMapNavi.setEmulatorNaviSpeed(200);
        }

        if (mTts == null) {
            //1.创建SpeechSynthesizer对象, 第二个参数：本地合成时传InitListener
            mTts = SpeechSynthesizer.createSynthesizer(getActivity().getApplicationContext(), null);
            //2.合成参数设置，详见《科大讯飞MSC API手册(Android)》SpeechSynthesizer 类
            mTts.setParameter(SpeechConstant.VOICE_NAME, "xiaoqi");//设置发音人
            mTts.setParameter(SpeechConstant.SPEED, "50");//设置语速
            mTts.setParameter(SpeechConstant.VOLUME, "80");//设置音量，范围0~100
            mTts.setParameter(SpeechConstant.ENGINE_TYPE, SpeechConstant.TYPE_CLOUD); //设置云端
            // mTts.startSpeaking("科大讯飞，让世界聆听我们的声音", mSynListener);
        }
        startLocation();


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
        amap = null;
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
        getActivity().unregisterReceiver(myBroadcastReciever);
        Log.i(TAG, "destroy");
        deactivate();
        mapView.onDestroy();
        mapViewForShow.onDestroy();
        amap = null;
        amapForShow = null;
        mAMapNavi.stopNavi();
        //mTtsManager.destroy();
        mTts.destroy();
        mAMapNavi.destroy();
    }

    private String s_destination = "";

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
        //  options.setReCalculateRouteForYaw(false);//设置偏航时是否重新计算路径
        //options.setLayoutVisible(true);
        mAMapNaviView.setViewOptions(options);


        switch_online = (TextView) view.findViewById(R.id.switch_online);
        head_map = view.findViewById(R.id.head_map);
        head_map.setOnClickListener(this);

        head_map_tel_navi = view.findViewById(R.id.head_map_tel_navi);
        startNavi = (Button) view.findViewById(R.id.starNavi);
        startNavi.setOnClickListener(this);

        coursePreview = (Button) view.findViewById(R.id.coursePreview);
        coursePreview.setOnClickListener(this);

        cancelOrder = (Button) view.findViewById(R.id.cancel_order);
        cancelOrder.setOnClickListener(this);


        menu = view.findViewById(R.id.menu);
        takePhoto = (ImageView) view.findViewById(R.id.take_photo);
        takePhoto.setOnClickListener(this);

        mapView = (TextureMapView) view.findViewById(R.id.mapview);
        mapView.onCreate(savedInstanceState);// 此方法必须重写

        mapViewForShow = (TextureMapView) view.findViewById(R.id.mapviewForShow);
        mapViewForShow.onCreate(savedInstanceState);// 此方法必须重写

        mRouteOverLay = new RouteOverLay(amap, null);

        mStartList.add(mStartLatlng);

        distance_route = (TextView) view.findViewById(R.id.distance_route);
        distance_moved = (TextView) view.findViewById(R.id.distance_moved);

        newOrder = (MyRaletiveLayout) view.findViewById(R.id.newOrder);
        newOrder.setLayerType(View.LAYER_TYPE_SOFTWARE, null);
        mainPage = view.findViewById(R.id.mainPage);

        intoOrder = view.findViewById(R.id.intoOrder);
        intoOrder.setOnClickListener(this);

        // roundProgressBar = (RoundProgressBar) view.findViewById(R.id.roundProgressBar);
        closeNewOrder = view.findViewById(R.id.closeNewOrder);
        closeNewOrder.setOnClickListener(this);

        destination = (TextView) view.findViewById(R.id.destination);

        progressBar = view.findViewById(R.id.progressBar);

        handler = new Handler() {
            @Override
            public void handleMessage(Message msg) {
                if (msg.what == 0) {
                    mAMapNaviView.displayOverview();//显示导航地图全览状态
                    // mTtsManager.stopSpeaking();

                } else if (msg.what == 1) {


                    Log.d("start", "尝试上线");
                    Map<String, Object> map = new HashMap<>();
                    map.put("lat", String.valueOf(mStartLatlng.getLatitude()));
                    map.put("lng", String.valueOf(mStartLatlng.getLongitude()));
                    new NewHttpPostTask(getActivity().getApplicationContext(), new OnHttpRequestListener() {
                        @Override
                        public void onRequestCompleted(Object obj) {
                            try {
                                JSONObject jsonObject = new JSONObject(obj.toString());
                                if (jsonObject.getString("result").equals("1")) {
                                    Log.d("start", "上线成功");
                                    isOnline = true;
                                    switch_online.setText("在线");
                                    switch_online.setBackground(getActivity().getResources().getDrawable(R.drawable.on_line));
                                    SharedPreferences sp = getActivity().getSharedPreferences("online", Context.MODE_PRIVATE);
                                    SharedPreferences.Editor editor = sp.edit();
                                    editor.putBoolean("online", true).commit();
                                    progressBar.setVisibility(View.GONE);
                                    mTts.startSpeaking("您已成功上线，开始接单", mSynListener);

                                } else {
                                    //Toast.makeText(getActivity().getApplicationContext(), R.string.msg_change_error, Toast.LENGTH_LONG).show();
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


                } else if (msg.what == 2) {

                    Map<String, Object> map = new HashMap<>();
                    map.put("lat", String.valueOf(mStartLatlng.getLatitude()));
                    map.put("lng", String.valueOf(mStartLatlng.getLongitude()));
                    new NewHttpPostTask(getActivity().getApplicationContext(), new OnHttpRequestListener() {
                        @Override
                        public void onRequestCompleted(Object obj) {
                            try {
                                JSONObject jsonObject = new JSONObject(obj.toString());
                                if (jsonObject.getString("result").equals("1")) {
                                    Log.d("start", "下线成功");
                                    isOnline = false;
                                    switch_online.setText("离线");
                                    switch_online.setBackground(getActivity().getResources().getDrawable(R.drawable.off_line));
                                    SharedPreferences sp = getActivity().getSharedPreferences("online", Context.MODE_PRIVATE);
                                    SharedPreferences.Editor editor = sp.edit();
                                    editor.remove("online").commit();
                                    //menu.setVisibility(View.GONE);
                                    progressBar.setVisibility(View.GONE);
                                    mTts.startSpeaking("您已停止接单,再见", mSynListener);

                                } else {
                                    // Toast.makeText(getActivity().getApplicationContext(), R.string.msg_change_error, Toast.LENGTH_LONG).show();
                                }
                            } catch (Exception e) {
                                e.printStackTrace();
                            }
                        }

                        @Override
                        public void onRequestFailed(Object obj) {

                            // Toast.makeText(getActivity().getApplicationContext(), "网络环境不太好，更新订单失败", Toast.LENGTH_LONG).show();
                        }
                    }).execute(Constants.USER_STOP_WORK_URL, map);

                } else if (msg.what == 5) {

                    Animation animation = AnimationUtils.loadAnimation(getActivity().getApplicationContext(),
                            R.anim.show);
                    newOrder.setVisibility(View.VISIBLE);
                    newOrder.startAnimation(animation);
                    head_map.setVisibility(View.GONE);
                    SharedPreferences sp = getActivity().getSharedPreferences("work", Context.MODE_PRIVATE);
                    boolean working = sp.getBoolean("working", false);
                    if (working) {
                        getActivity().findViewById(android.R.id.tabhost).setVisibility(View.GONE);
                    }
                    try {
                        JSONObject data = (JSONObject) msg.obj;
                        s_destination = data.getString("address");
                        int serviceType = data.getInt("service_type");
                        double lat = data.getDouble("latitude");
                        double lon = data.getDouble("longitude");
                        NaviLatLng accidentPlace = new NaviLatLng(lat, lon);
                        SharedPreferences sp2 = getActivity().getSharedPreferences("end", Context.MODE_PRIVATE);
                        sp2.edit().putString("end_lat", String.valueOf(accidentPlace.getLatitude())).commit();
                        sp2.edit().putString("end_lon", String.valueOf(accidentPlace.getLongitude())).commit();
                        sp2.edit().putString("destination", s_destination).commit();
                        mEndList.clear();
                        mEndList.add(accidentPlace);
                        String s_type = null;
                        if (serviceType == 4) {
                            s_type = "搭电";
                        } else if (serviceType == 2) {

                            s_type = "换胎";
                        } else if (serviceType == 1) {

                            s_type = "拖车";
                        } else if (serviceType == 9) {

                            s_type = "快修";

                        }
                        mTts.startSpeaking("您有新的" + s_type + "订单，地址" + s_destination, mSynListener);
//                        SharedPreferences sp1 = getActivity().getSharedPreferences("newOrderComing",Context.MODE_PRIVATE);
//                        sp1.edit().putBoolean("newOrderComing",true).commit();

                    } catch (Exception e) {

                    }

                } else if (msg.what == 6) {

                    if (isNavi) {

                        mAMapNaviView.recoverLockMode();
                        mAMapNaviView.setLockZoom(20);
                        startNavi.setBackground(getActivity().getResources().getDrawable(R.drawable.navigation_sel));
                        //startNavi.setText("停止导航");
                        mTts.startSpeaking("您已开启导航模式", mSynListener);

                    } else {

                        mAMapNaviView.displayOverview();
                        startNavi.setBackground(getActivity().getResources().getDrawable(R.drawable.navigation_nor));
                        //startNavi.setText("开启导航");
                        mTts.startSpeaking("您已关闭导航模式", mSynListener);

                    }
                } else if (msg.what == 7) {
                    mTts.startSpeaking("订单已被取消", mSynListener);
                    getActivity().findViewById(android.R.id.tabhost).setVisibility(View.VISIBLE);
                    mAMapNaviView.setVisibility(View.GONE);
                    mapView.setVisibility(View.VISIBLE);
                    mapViewForShow.setVisibility(View.GONE);
                    head_map.setVisibility(View.VISIBLE);
                    head_map_tel_navi.setVisibility(View.GONE);
                    menu.setVisibility(View.GONE);
                    startNavi.setVisibility(View.GONE);
                    coursePreview.setVisibility(View.GONE);
                    isNavi = false;
                    canGetNewOrder = true;
                    canCountGPSPoint = false;
                }else if(msg.what==8){

                    mTts.startSpeaking("到达救援现场,请按要求拍照", mSynListener);
                }
            }
        };
        myBroadcastReciever = new MyBroadcastReciever();
        IntentFilter intentFilter = new IntentFilter();
        intentFilter.addAction("newOrder");
        getActivity().registerReceiver(myBroadcastReciever, intentFilter);

//        mAMapNavi.startGPS();
//        mAMapNavi.startAimlessMode(AimLessMode.NONE_DETECTED);

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

       // latLngs.remove(latLngs.size() - 1);
      //  mAMapNavi.startAimlessMode(AimLessMode.NONE_DETECTED);
//        AMapNaviViewOptions option = mAMapNaviView.getViewOptions();
//        option.setZoom(13);
//        mAMapNaviView.setViewOptions(option);
      //  mAMapNaviView.recoverLockMode();
       // mAMapNaviView.setLockZoom(13);
        //handler.sendEmptyMessage(0);


    }

    @Override
    public void onEndEmulatorNavi() {

        latLngs.remove(latLngs.size() - 1);
        distance_moved.setText("已行驶:" + DistanceUtil.checkDistance(dis_moved - last_distance) + "公里");
        //AMapNaviViewOptions option = mAMapNaviView.getViewOptions();
        //option.setPointToCenter(0.5, 0.5);
        //option.setZoom(13);
        mAMapNaviView.recoverLockMode();
        mAMapNaviView.setLockZoom(13);
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

        //  head_map.setVisibility(View.GONE);

        float route_distance = DistanceUtil.checkDistance(naviPath.getAllLength() / 1000f);
        distance_route.setText("总路程：" + route_distance + "km");
        // menu_layout.setVisibility(View.VISIBLE);
        //deactivate();
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
                distance_moved.setText("已行驶:" + DistanceUtil.checkDistance(dis_moved) + "km");
            }

            Log.d("distance", timeStamp + ":  " + currentLatLng.latitude + "---" + currentLatLng.longitude + "---" + last_distance);
            timeStamp = timeNow;
            latLngs.add(currentLatLng);

        }

    }

    @Override
    public void onLocationChange(AMapNaviLocation aMapNaviLocation) {

        if (canCountGPSPoint) {
            if (aMapNaviLocation.getAccuracy() <= 20) {

                long timeNow = new Date().getTime();
                if (timeNow > timeStamp) {
                    LatLng currentLatLng = new LatLng(aMapNaviLocation.getCoord().getLatitude(), aMapNaviLocation.getCoord().getLongitude());
                    if (latLngs.size() > 2) {
                        LatLng positionLatLng = latLngs.get(latLngs.size() - 1);
                        int distance = (int) AMapUtils.calculateLineDistance(currentLatLng, positionLatLng);
                        float f_dis = DistanceUtil.checkDistance(distance / 1000f);
                        dis_moved += f_dis;
                        distance_moved.setText("已行驶:" + DistanceUtil.checkDistance(dis_moved) + "公里");
                    }
                    latLngs.add(currentLatLng);
                    // Toast.makeText(getActivity(), "navi listener ---" + currentLatLng.longitude + "---" + currentLatLng.latitude, Toast.LENGTH_SHORT).show();
                    timeStamp = timeNow;
                }
            }
        }
    }

    private boolean isNavi = false;

    @Override
    public void onGetNavigationText(int arg0, String arg1) {
        // TODO Auto-generated method stub
        if (!isNavi) {


        } else {

            mTts.startSpeaking(arg1, mSynListener);

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
                .fromResource(R.drawable.location_marker)).anchor(0.5f, 0.5f).radiusFillColor(Color.TRANSPARENT).strokeColor(Color.TRANSPARENT);// 设置小蓝点的图标
        amap.setMyLocationStyle(myLocationStyle);
        amap.setLocationSource(this);// 设置定位监听
        amap.getUiSettings().setMyLocationButtonEnabled(true);// 设置默认定位按钮是否显示
        amap.moveCamera(CameraUpdateFactory.zoomTo(16));
        amap.moveCamera(CameraUpdateFactory.changeTilt(0));
        amap.setMyLocationEnabled(true);// 设置为true表示显示定位层并可触发定位，false表示隐藏定位层并不可触发定位，默认是false
        // aMap.setMyLocationType()
        amap.setMyLocationType(AMap.LOCATION_TYPE_MAP_FOLLOW);

    }

    @Override
    public void onLocationChanged(AMapLocation aMapLocation) {

        if (mListener != null && aMapLocation != null) {
            if (aMapLocation != null
                    && aMapLocation.getErrorCode() == 0) {

                Log.d("location", aMapLocation.getLocationType() + "");
                mStartLatlng = new NaviLatLng(aMapLocation.getLatitude(), aMapLocation.getLongitude());
                LatLng latLng = new LatLng(aMapLocation.getLatitude(), aMapLocation.getLongitude());

                amap.moveCamera(CameraUpdateFactory.changeTilt(0));
                if (mListener != null) {
                    mListener.onLocationChanged(aMapLocation);// 显示系统小蓝点
                }
                //Toast.makeText(this.getActivity().getApplicationContext(), latLng.latitude+"--"+latLng.longitude, Toast.LENGTH_SHORT).show();
                // Log.d(TAG,latLng.latitude+"--"+latLng.longitude);

            } else {
                String errText = "定位失败," + aMapLocation.getErrorCode() + ": " + aMapLocation.getErrorInfo();
                Log.d("location", errText);
                //Toast.makeText(this.getActivity().getApplicationContext(), errText, Toast.LENGTH_SHORT).show();
            }
        }

    }

    private void startLocation() {

        if (locationClient == null) {
            locationClient = new AMapLocationClient(this.getActivity());
            locationOption = new AMapLocationClientOption();
            //设置定位监听
            locationClient.setLocationListener(this);
            //设置为高精度定位模式
            locationOption.setLocationMode(AMapLocationClientOption.AMapLocationMode.Hight_Accuracy);

            //  locationOption.setGpsFirst(true);
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
    public void activate(OnLocationChangedListener onLocationChangedListener) {

        mListener = onLocationChangedListener;
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


    @Override
    public void onMapLoaded() {
        // Toast.makeText(getActivity().getApplicationContext(),"map load over",Toast.LENGTH_SHORT).show();
        Log.d(TAG, "loadMap");
        amap.moveCamera(CameraUpdateFactory.zoomTo(16));
        amap.moveCamera(CameraUpdateFactory.changeLatLng(new LatLng(29.807633, 121.555842)));
    }

    private Dialog dialog;

    @Override
    public void onClick(View v) {
        switch (v.getId()) {
            case R.id.starNavi:
                if (!isNavi) {

                    isNavi = true;
                    handler.sendEmptyMessage(6);

                } else {

                    isNavi = false;
                    handler.sendEmptyMessage(6);

                }

                break;
            case R.id.head_map:

                if ("离线".equals(switch_online.getText())) {

                    // getActivity().findViewById(android.R.id.tabhost).setVisibility(View.GONE);
//                    init();
//                    setUpMap();
                    handler.sendEmptyMessage(1);
                    progressBar.setVisibility(View.VISIBLE);


                } else {
                    handler.sendEmptyMessage(2);
                    // getActivity().findViewById(android.R.id.tabhost).setVisibility(View.VISIBLE);
                    //menu_layout.setVisibility(View.GONE);
                    // menu.setVisibility(View.GONE);
                    progressBar.setVisibility(View.VISIBLE);
                    // switch_online.setText("离线>>>");
//                    deactivate();
//                    amap.clear();
//                    amap = null;
                }
                break;


//            case R.id.stop_navi:
//                canGetStartPoint = true;
//                mAMapNavi.stopNavi();
//                getActivity().findViewById(android.R.id.tabhost).setVisibility(View.VISIBLE);
//                mAMapNaviView.setVisibility(View.GONE);
//                mapView.setVisibility(View.VISIBLE);
//                mapViewForShow.setVisibility(View.GONE);
//                amap.clear(true);
//                setUpMap();
//                head_map.setVisibility(View.VISIBLE);
//                head_map_tel_navi.setVisibility(View.GONE);
//                isNavi = false;
//                canGetNewOrder = true;
//                canCountGPSPoint= false;
//                break;
//            case R.id.check_route:
//                try {
//                    amap.clear(true);
//                    amapForShow.clear(true);
//                    mapViewForShow.setVisibility(View.VISIBLE);
//                    mapView.setVisibility(View.GONE);
//
//                    latLngs.remove(latLngs.size()-1);
//
//                    if (latLngs.size() % 2 == 0) {
//                        //amapForShow.moveCamera(CameraUpdateFactory.newLatLngBounds(latLngBounds,13));
//                        amapForShow.moveCamera(CameraUpdateFactory.newLatLngZoom(latLngs.get(latLngs.size() / 2), 13));
//
//                    } else {
//                        //amapForShow.moveCamera(CameraUpdateFactory.newLatLngBounds(latLngBounds,13));
//                        amapForShow.moveCamera(CameraUpdateFactory.newLatLngZoom(latLngs.get((latLngs.size() - 1) / 2), 13));
//
//                    }
//
//                    for (int i = 0; i < latLngs.size(); i++) {
//                        List<LatLng> l = new ArrayList<LatLng>();
//                        if(i%4==0){
//                            l.add(latLngs.get(i));
//                            l.add(latLngs.get(i + 4));
//                        }
//                        Polyline polyLine = amapForShow.addPolyline((new PolylineOptions()).color(Color.BLUE).width(10));
//                        polyLine.setPoints(l);
//                    }
//
//
//                } catch (Exception e) {
//
//                    Log.i(TAG, e.getMessage());
//                } finally {
//
//                    dis_moved = 0;
//                    latLngs.clear();
//                }
//
//                break;
            case R.id.cancel_order:
                mTts.startSpeaking("您真的要取消订单吗", mSynListener);
                dialog = new Dialog(getActivity(), R.style.bubble_dialog);
                View view = getActivity().getLayoutInflater().inflate(R.layout.dialog_msg_notice,
                        null);
                Button tvConfirm = (Button) view.findViewById(R.id.btn_notice_confirm);
                Button tvCancel = (Button) view.findViewById(R.id.btn_notice_cancel);
                TextView tvMsg = (TextView) view.findViewById(R.id.tv_notice_msg);
                tvMsg.setText("是否取消订单");
                tvConfirm.setOnClickListener(new View.OnClickListener() {

                    @Override
                    public void onClick(View v) {
                        dialog.dismiss();
                        mAMapNavi.stopNavi();
                        handler.sendEmptyMessage(7);
                        amap.clear(true);
                        setUpMap();

                    }
                });
                tvCancel.setOnClickListener(new View.OnClickListener() {

                    @Override
                    public void onClick(View v) {
                        dialog.dismiss();
                    }
                });

                dialog.setContentView(view);
                dialog.setCanceledOnTouchOutside(false);
                Window window = dialog.getWindow();
                WindowManager.LayoutParams params = window.getAttributes();
                Point point = JSONUtil.getDeviceSize(getActivity());
                params.width = Math.round(point.x * 5 / 7);
                window.setAttributes(params);
                try {
                    dialog.show();
                } catch (Exception e) {
                    e.printStackTrace();
                }
                break;
            case R.id.coursePreview:
                mAMapNaviView.displayOverview();
                break;
            case R.id.closeNewOrder:
                newOrder.clearAnimation();
                newOrder.setVisibility(View.GONE);
                head_map.setVisibility(View.VISIBLE);
                getActivity().findViewById(android.R.id.tabhost).setVisibility(View.VISIBLE);
                mTts.startSpeaking("您已放弃订单", mSynListener);
                SharedPreferences sp = getActivity().getSharedPreferences("newOrderComing", Context.MODE_PRIVATE);
                sp.edit().remove("newOrderComing").commit();
                SharedPreferences sp2 = getActivity().getSharedPreferences("end", Context.MODE_PRIVATE);
                sp2.edit().clear().commit();
                canGetNewOrder = true;
                break;
            case R.id.intoOrder:
                newOrder.clearAnimation();
                newOrder.setVisibility(View.GONE);
                SharedPreferences sp3 = getActivity().getSharedPreferences("end", Context.MODE_PRIVATE);
                sp3.edit().clear().commit();
                //  mapViewForShow.setVisibility(View.GONE);
                // mapView.setVisibility(View.VISIBLE);
                mStartList.clear();
                if (mStartLatlng != null) {
                    mStartList.add(mStartLatlng);
                }

                if (mStartList.isEmpty()) {
                    Toast.makeText(getActivity().getApplicationContext(), "没有定位到起点", Toast.LENGTH_SHORT).show();
                } else if (mEndList.isEmpty()) {
                    Toast.makeText(getActivity().getApplicationContext(), "没有定位到目的地", Toast.LENGTH_SHORT).show();
                } else {
                    head_map.setVisibility(View.GONE);
                    head_map_tel_navi.setVisibility(View.VISIBLE);
                    menu.setVisibility(View.VISIBLE);
                    startNavi.setVisibility(View.VISIBLE);
                    coursePreview.setVisibility(View.VISIBLE);
                    mTts.startSpeaking("开始任务", mSynListener);
                    SharedPreferences sp4 = getActivity().getSharedPreferences("newOrderComing", Context.MODE_PRIVATE);
                    sp4.edit().remove("newOrderComing").commit();
                    amap.clear(true);
                    canCountGPSPoint = true;
                    distance_moved.setText("已行驶:0.0km");
                    destination.setText(s_destination);
                    mAMapNavi.calculateDriveRoute(mStartList, mEndList, mWayPointList, PathPlanningStrategy.DRIVING_DEFAULT);

                }
                break;
            case R.id.take_photo:
                handler.sendEmptyMessage(8);
                Intent i = new Intent(getActivity(), NewTakePhotoActivity.class);
                getActivity().startActivity(i);
                break;

        }
    }


    public class MyBroadcastReciever extends BroadcastReceiver {


        @Override
        public void onReceive(Context context, Intent intent) {

            if ("newOrder".equals(intent.getAction())) {
                if (isOnline) {

                    if (canGetNewOrder) {

                        Log.i("newOrder", "有新的订单过来啦----" + intent.getStringExtra("order"));

                        try {
                            JSONObject jsonObject1 = new JSONObject(intent.getStringExtra("order"));
                            JSONObject jsonObject = jsonObject1.optJSONObject("data");
                            Message msg = new Message();
                            SharedPreferences sp = getActivity().getSharedPreferences("newOrderComing", Context.MODE_PRIVATE);
                            sp.edit().putBoolean("newOrderComing", true).commit();
                            canGetNewOrder = false;
                            msg.what = 5;
                            msg.obj = jsonObject;
                            handler.sendMessage(msg);

                        } catch (Exception e) {


                        }

                    }

                }

            }
        }
    }


    //合成监听器
    private SynthesizerListener mSynListener = new SynthesizerListener() {

        //会话结束回调接口，没有错误时，error为null
        public void onCompleted(SpeechError error) {
        }

        //缓冲进度回调
        //percent为缓冲进度0~100，beginPos为缓冲音频在文本中开始位置，endPos表示缓冲音频在文本中结束位置，info为附加信息。
        public void onBufferProgress(int percent, int beginPos, int endPos, String info) {
        }

        //开始播放
        public void onSpeakBegin() {
        }

        //暂停播放
        public void onSpeakPaused() {
        }

        //播放进度回调
        //percent为播放进度0~100,beginPos为播放音频在文本中开始位置，endPos表示播放音频在文本中结束位置.
        public void onSpeakProgress(int percent, int beginPos, int endPos) {
        }

        //恢复播放回调接口
        public void onSpeakResumed() {
        }

        //会话事件回调接口
        public void onEvent(int arg0, int arg1, int arg2, Bundle arg3) {
        }

    };


}
