package com.autosos.rescue.fragment;

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
import com.autosos.rescue.Constants;
import com.autosos.rescue.Layout.MyButton;
import com.autosos.rescue.Layout.MyRaletiveLayout;
import com.autosos.rescue.R;
import com.autosos.rescue.application.MyApplication;
import com.autosos.rescue.model.NewOrder;
import com.autosos.rescue.model.OrderInfo;
import com.autosos.rescue.task.HttpGetTask;
import com.autosos.rescue.task.NewHttpPostTask;
import com.autosos.rescue.task.NewHttpPutTask;
import com.autosos.rescue.task.OnHttpRequestListener;
import com.autosos.rescue.util.DensityUtil;
import com.autosos.rescue.util.DistanceUtil;
import com.autosos.rescue.util.JSONUtil;
import com.autosos.rescue.util.OiUtil;
import com.autosos.rescue.util.Session;
import com.autosos.rescue.util.Utils;
import com.autosos.rescue.view.AppraiseActivity;
import com.autosos.rescue.view.LoginActivity;
import com.autosos.rescue.view.MainActivity;
import com.autosos.rescue.view.NewTakePhotoActivity;
import com.autosos.rescue.view.NewUploadPhotoActivity;
import com.autosos.rescue.view.PayActivity;
import com.autosos.rescue.viewpager.ContentViewPager;
import com.autosos.rescue.widget.RoundProgressBar;
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

    private  ArrayList<LatLng> latLngs = null;
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
    private Boolean speakOnce = false;

    private NewOrder newOrder_bean = null;
    private OrderInfo info = null;
    private int order_id = 0;
    private TextView order_type, address, address_tuoche1, address_tuoche2, tv_take_photo;
    private View otherService, trailerService;
    private View throw_price;
    private TextView tv_throw_price;


    //根据时间排轨迹点 通过onLocationChange实时补充
    private long timeStamp ;
    private float dis_moved;
    private float last_distance;
    private String trace_data = "";

    private AMapLocationClient locationClient = null;
    private AMapLocationClientOption locationOption = null;

    // private boolean mapClickStartReady = true;
    // private boolean mapClickEndReady = false;
    private boolean mapClickEndReady = true;

    private boolean isNavi = false;


    private SharedPreferences sp = null;
    private Boolean firstResume = false;
    private Boolean firstResume_tuoche = false;
    private Boolean shouldNotStartNavi = false;
    private ImageView tel_customer1;
    private Button tel_customer2;
    private String owner_mobile;

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

    private static OiUtil oiUtil = null;
    private boolean isPaodn = false;
    private boolean isAfterOrder = false;

    /**
     * 方法必须重写
     */
    @Override
    public void onResume() {
        super.onResume();
        mapView.onResume();
        mapViewForShow.onResume();
        amap=null;
        init();
        setUpMap();
//        SharedPreferences sharedPreference3 = getActivity().getSharedPreferences("isAfterOrder", Context.MODE_PRIVATE);
//        isAfterOrder =  sharedPreference3.getBoolean("isAfterOrder",false);
//        sharedPreference3.edit().remove("isAfterOrder").commit();
        Log.d("afterOrder",MyApplication.application.isAfterOrder+"");
        if( MyApplication.application.isAfterOrder){
            Log.d("afterOrder","ok");
            MyApplication.application.isAfterOrder = false;
            canGetNewOrder = true;
            getActivity().findViewById(android.R.id.tabhost).setVisibility(View.VISIBLE);
            head_map.setVisibility(View.VISIBLE);
            head_map_tel_navi.setVisibility(View.GONE);
            mAMapNaviView.setVisibility(View.GONE);
            mapView.setVisibility(View.VISIBLE);
            menu.setVisibility(View.GONE);
            startNavi.setVisibility(View.GONE);
            coursePreview.setVisibility(View.GONE);
            info = null;
            firstResume_tuoche = false;
            handler.sendEmptyMessage(12);

        }else {


        }
        if (oiUtil == null) {

            oiUtil = new OiUtil();
        }
        new HttpGetTask(getActivity().getApplication(), new OnHttpRequestListener() {
            @Override
            public void onRequestCompleted(Object obj) {

                JSONObject jsonObject;
                try {
                    jsonObject = new JSONObject(obj.toString());
                    Log.d("working_status", jsonObject.getInt("result") + "");
                    if (jsonObject.getInt("result") == 1) {//离线

                        handler.sendEmptyMessage(1);
                        progressBar.setVisibility(View.VISIBLE);

                    } else if (jsonObject.getInt("result") == 2) {//在线

                        isOnline = true;
                        switch_online.setText("在线");
                        switch_online.setBackground(getActivity().getResources().getDrawable(R.drawable.on_line));
                        newOrder.setVisibility(View.GONE);
                        SharedPreferences sp1 = getActivity().getSharedPreferences("newOrderComing", Context.MODE_PRIVATE);
                        boolean newOrderComing = sp1.getBoolean("newOrderComing", false);
                        firstResume = true;
                        if (newOrderComing) {
                            Log.d("working_status","1");
                            SharedPreferences sp3 = getActivity().getSharedPreferences("order", Context.MODE_PRIVATE);
                            String s_order = sp3.getString("order", null);
                            JSONObject order;
                            if (s_order != null) {
                                Log.d("working_status","2");
                                Animation animation = AnimationUtils.loadAnimation(getActivity().getApplicationContext(),
                                        R.anim.show);
                                newOrder.setVisibility(View.VISIBLE);
                                newOrder.startAnimation(animation);
                                head_map.setVisibility(View.GONE);
                                getActivity().findViewById(android.R.id.tabhost).setVisibility(View.GONE);
                                try {
                                    order = new JSONObject(s_order);
                                    newOrder_bean = new NewOrder(order);
                                    order_id = newOrder_bean.getOrderId();
                                    owner_mobile = newOrder_bean.getOwner_moblie();
                                    double lat = newOrder_bean.getLatitude();
                                    double lon = newOrder_bean.getLongitude();
                                    String destination = newOrder_bean.getAddress();
                                    NaviLatLng naviLatLng = new NaviLatLng(lat, lon);
                                    mEndList.clear();
                                    mEndList.add(naviLatLng);
                                    s_destination = destination;
                                    int serviceType = newOrder_bean.getService_type();
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

                                    if(newOrder_bean.getIsPaodan()==0){
                                        isPaodn = false;
                                        throw_price.setVisibility(View.GONE);
                                        order_type.setText(s_type + "订单");
                                        if(newOrder_bean.getIs_one_price()==2){

                                            //起步价模式

                                            if (serviceType == 1) {

                                                trailerService.setVisibility(View.VISIBLE);
                                                otherService.setVisibility(View.GONE);
                                                address_tuoche1.setText(s_destination);
                                                String s_address_tuoche2 = newOrder_bean.getAddress_tuoche();
                                                address_tuoche2.setText(s_address_tuoche2);

                                            } else {

                                                trailerService.setVisibility(View.GONE);
                                                otherService.setVisibility(View.VISIBLE);
                                                address.setText(s_destination);
                                            }
                                        }else{
                                           //一口价模式
                                            throw_price.setVisibility(View.VISIBLE);
                                            tv_throw_price.setText(newOrder_bean.getBase_price()+"");
                                            DisplayMetrics dm =getResources().getDisplayMetrics();
                                            float density = dm.density;

                                            if (serviceType == 1) {

                                                ViewGroup.MarginLayoutParams layoutParam = (ViewGroup.MarginLayoutParams) trailerService.getLayoutParams();
                                                layoutParam.topMargin =(int)(170*density);
                                                trailerService.setLayoutParams(layoutParam);
                                                trailerService.setVisibility(View.VISIBLE);
                                                otherService.setVisibility(View.GONE);
                                                address_tuoche1.setText(s_destination);
                                                String s_address_tuoche2 = newOrder_bean.getAddress_tuoche();
                                                address_tuoche2.setText(s_address_tuoche2);

                                            } else {

                                                ViewGroup.MarginLayoutParams layoutParam = (ViewGroup.MarginLayoutParams)  otherService.getLayoutParams();
                                                layoutParam.topMargin =(int)(170*density);
                                                otherService.setLayoutParams(layoutParam);
                                                trailerService.setVisibility(View.GONE);
                                                otherService.setVisibility(View.VISIBLE);
                                                address.setText(s_destination);
                                            }



                                        }



                                    }else{
                                        isPaodn = true;
                                        order_type.setText(s_type + "抛单");
                                        throw_price.setVisibility(View.VISIBLE);
                                        tv_throw_price.setText(newOrder_bean.getOnePrice()+"");
                                        DisplayMetrics dm =getResources().getDisplayMetrics();
                                        float density = dm.density;

                                        if (serviceType == 1) {

                                            ViewGroup.MarginLayoutParams layoutParam = (ViewGroup.MarginLayoutParams) trailerService.getLayoutParams();
                                            layoutParam.topMargin =(int)(170*density);
                                            trailerService.setLayoutParams(layoutParam);
                                            trailerService.setVisibility(View.VISIBLE);
                                            otherService.setVisibility(View.GONE);
                                            address_tuoche1.setText(s_destination);
                                            String s_address_tuoche2 = newOrder_bean.getAddress_tuoche();
                                            address_tuoche2.setText(s_address_tuoche2);

                                        } else {

                                            ViewGroup.MarginLayoutParams layoutParam = (ViewGroup.MarginLayoutParams)  otherService.getLayoutParams();
                                            layoutParam.topMargin =(int)(170*density);
                                            otherService.setLayoutParams(layoutParam);
                                            trailerService.setVisibility(View.GONE);
                                            otherService.setVisibility(View.VISIBLE);
                                            address.setText(s_destination);
                                        }

                                    }


                                } catch (JSONException e) {
                                    e.printStackTrace();
                                }


                            }

                        }


                    } else if (jsonObject.getInt("result") == 3) {//工作状态
                        isOnline = true;
                        switch_online.setText("在线");
                        switch_online.setBackground(getActivity().getResources().getDrawable(R.drawable.on_line));
                        getActivity().findViewById(android.R.id.tabhost).setVisibility(View.GONE);
                        int orderId = jsonObject.getInt("orderId");
                        sp = getActivity().getSharedPreferences("dis_moved", Context.MODE_PRIVATE);
                        Log.d("working_status", "3" + "---" + orderId);
                        new HttpGetTask(getActivity().getApplicationContext(), new OnHttpRequestListener() {//获取订单详情
                            @Override
                            public void onRequestCompleted(Object obj) {

                                JSONObject jsonObject;
                                try {
                                    Log.d("working_status", obj.toString());
                                    jsonObject = new JSONObject(obj.toString());
                                    info = new OrderInfo(jsonObject);
                                    SharedPreferences sp2 = getActivity().getSharedPreferences("orderInfo", Context.MODE_PRIVATE);
                                    sp2.edit().putString("orderInfo", obj.toString()).commit();
                                    order_id = info.getOrderId();
                                    owner_mobile = info.getOwnerMobile();
                                    Log.d("working_status", info.getServiceType() + "---" + info.getOrderStatus());
                                    if (info.getServiceType() == 1) {
                                        //拖车服务

                                        if (info.getOrderStatus() == 3) {
                                            //已经接单
                                            if (!firstResume) {
                                                String trace = oiUtil.readJWD(oiUtil.path_drag);
                                                NaviLatLng startPoint = null;
                                                if (trace != null) {
                                                    String[] array_trace = trace.trim().split("\\|");
                                                    String[] array_startPoint = (array_trace[array_trace.length - 1]).split(",");
                                                    startPoint = new NaviLatLng(Double.parseDouble(array_startPoint[1]), Double.parseDouble(array_startPoint[0]));
                                                }
                                                mStartList.clear();
                                                if (startPoint != null) {
                                                    mStartList.add(startPoint);
                                                }else{
                                                    if(mStartLatlng!=null){

                                                        mStartList.add(mStartLatlng);
                                                    }
                                                }
                                                double lat = info.getLatitude();
                                                double lon = info.getLongitude();
                                                NaviLatLng accidentPlace = new NaviLatLng(lat, lon);
                                                mEndList.clear();
                                                mEndList.add(accidentPlace);
                                                if (mStartList.isEmpty()) {
                                                    Toast.makeText(getActivity().getApplicationContext(), "没有定位到起点", Toast.LENGTH_SHORT).show();
                                                } else if (mEndList.isEmpty()) {
                                                    Toast.makeText(getActivity().getApplicationContext(), "没有定位到目的地", Toast.LENGTH_SHORT).show();
                                                } else {
                                                    progressBar.setVisibility(View.VISIBLE);
                                                    head_map.setVisibility(View.GONE);
                                                    head_map_tel_navi.setVisibility(View.VISIBLE);
                                                    menu.setVisibility(View.VISIBLE);
                                                    startNavi.setVisibility(View.VISIBLE);
                                                    coursePreview.setVisibility(View.VISIBLE);
                                                    mTts.startSpeaking("继续任务", mSynListener);
                                                    amap.clear(true);
                                                    float dis_moved_lastTime = sp.getFloat("dis_moved", 0.0f);
                                                    dis_moved = dis_moved_lastTime;
                                                    distance_moved.setText("已行驶:" + DistanceUtil.checkDistance(dis_moved_lastTime) + "km");
                                                    destination.setText(info.getAddress());
                                                    firstResume = true;
                                                    mAMapNavi.calculateDriveRoute(mStartList, mEndList, mWayPointList, PathPlanningStrategy.DRIVING_DEFAULT);

                                                }
                                            }

                                        } else if (info.getOrderStatus() == 4) {//到达救援现场 还未拍照
                                            NaviLatLng startPoint = null;
                                            NaviLatLng endPoint = null;
                                            NaviLatLng wayPoint = null;
                                            List<NaviLatLng> startList = new ArrayList<NaviLatLng>();
                                            List<NaviLatLng> endList = new ArrayList<NaviLatLng>();
                                            List<NaviLatLng> wayPointList = new ArrayList<NaviLatLng>();

                                            startPoint = new NaviLatLng(info.getReal_take_latitude(), info.getReal_take_longitude());
                                            endPoint = new NaviLatLng(info.getReal_latitude(), info.getReal_longitude());
                                            startList.add(startPoint);
                                            endList.add(endPoint);
                                            Log.d("real_info", info.toString());
                                            head_map.setVisibility(View.GONE);
                                            head_map_tel_navi.setVisibility(View.VISIBLE);
                                            menu.setVisibility(View.VISIBLE);
                                            startNavi.setVisibility(View.GONE);
                                            coursePreview.setVisibility(View.GONE);
                                            mTts.startSpeaking("到达救援现场，请按要求拍摄照片", mSynListener);
                                            amap.clear(true);
                                            float dis_moved_lastTime = (float) info.getReal_dis();
                                            distance_moved.setText("已行驶:" + DistanceUtil.checkDistance(dis_moved_lastTime) + "km");
                                            destination.setText(info.getReal_address());
                                            mapView.setVisibility(View.GONE);
                                            mAMapNaviView.setVisibility(View.VISIBLE);
                                            shouldNotStartNavi = true;
                                            mAMapNavi.calculateDriveRoute(startList, endList, wayPointList, PathPlanningStrategy.DRIVING_DEFAULT);


                                        } else if (info.getOrderStatus() == 5) {

                                            NaviLatLng startPoint = null;
                                            NaviLatLng endPoint = null;
                                            NaviLatLng wayPoint = null;
                                            List<NaviLatLng> startList = new ArrayList<NaviLatLng>();
                                            List<NaviLatLng> endList = new ArrayList<NaviLatLng>();
                                            List<NaviLatLng> wayPointList = new ArrayList<NaviLatLng>();

                                            startPoint = new NaviLatLng(info.getReal_take_latitude(), info.getReal_take_longitude());
                                            endPoint = new NaviLatLng(info.getReal_latitude(), info.getReal_longitude());
                                            startList.add(startPoint);
                                            endList.add(endPoint);
                                            Log.d("real_info", obj.toString());
                                            head_map.setVisibility(View.GONE);
                                            head_map_tel_navi.setVisibility(View.VISIBLE);
                                            menu.setVisibility(View.VISIBLE);
                                            startNavi.setVisibility(View.GONE);
                                            coursePreview.setVisibility(View.GONE);
                                            mTts.startSpeaking("请按要求拍摄照片,并开始拖车", mSynListener);
                                            amap.clear(true);
                                            float dis_moved_lastTime = (float) info.getReal_dis();
                                            distance_moved.setText("已行驶:" + DistanceUtil.checkDistance(dis_moved_lastTime) + "km");
                                            destination.setText(info.getReal_address());
                                            mapView.setVisibility(View.GONE);
                                            mAMapNaviView.setVisibility(View.VISIBLE);
                                            tv_take_photo.setText("拍摄照片,开始拖车");
                                            shouldNotStartNavi = true;
                                            mAMapNavi.calculateDriveRoute(startList, endList, wayPointList, PathPlanningStrategy.DRIVING_DEFAULT);

                                        } else if (info.getOrderStatus() == 11) {

                                            NaviLatLng startPoint = null;
                                            NaviLatLng endPoint = null;
                                            NaviLatLng wayPoint = null;
                                            List<NaviLatLng> startList = new ArrayList<NaviLatLng>();
                                            List<NaviLatLng> endList = new ArrayList<NaviLatLng>();
                                            List<NaviLatLng> wayPointList = new ArrayList<NaviLatLng>();

                                            startPoint = new NaviLatLng(info.getReal_take_latitude(), info.getReal_take_longitude());
                                            endPoint = new NaviLatLng(info.getReal_latitude(), info.getReal_longitude());
                                            startList.add(startPoint);
                                            endList.add(endPoint);

                                            head_map.setVisibility(View.GONE);
                                            head_map_tel_navi.setVisibility(View.VISIBLE);
                                            menu.setVisibility(View.VISIBLE);
                                            startNavi.setVisibility(View.GONE);
                                            coursePreview.setVisibility(View.GONE);
                                            mTts.startSpeaking("请按要求拍摄照片,并开始拖车", mSynListener);
                                            amap.clear(true);
                                            float dis_moved_lastTime = (float) info.getReal_dis();
                                            distance_moved.setText("已行驶:" + DistanceUtil.checkDistance(dis_moved_lastTime) + "km");
                                            destination.setText(info.getReal_address());
                                            mapView.setVisibility(View.GONE);
                                            mAMapNaviView.setVisibility(View.VISIBLE);
                                            tv_take_photo.setText("拍摄照片,开始拖车");
                                            shouldNotStartNavi = true;
                                            mAMapNavi.calculateDriveRoute(startList, endList, wayPointList, PathPlanningStrategy.DRIVING_DEFAULT);

                                        } else if (info.getOrderStatus() == 6) {
                                            //将要开始拖车
                                            if (!firstResume_tuoche) {
                                                progressBar.setVisibility(View.VISIBLE);
                                                String trace = oiUtil.readJWD(oiUtil.path_drag);
                                                NaviLatLng startPoint = null;
                                                if (trace != null) {
                                                    String[] array_trace = trace.trim().split("\\|");
                                                    String[] array_startPoint = (array_trace[array_trace.length - 1]).split(",");
                                                    startPoint = new NaviLatLng(Double.parseDouble(array_startPoint[1]), Double.parseDouble(array_startPoint[0]));
                                                    mStartList.clear();
                                                    if (startPoint != null) {
                                                        mStartList.add(startPoint);
                                                    }
                                                } else {
                                                    mStartList.clear();
                                                    if (mStartLatlng != null) {
                                                        mStartList.add(mStartLatlng);
                                                    }
                                                }

                                                double lat = info.getDest_lat();
                                                double lon = info.getDest_lng();
                                                NaviLatLng accidentPlace = new NaviLatLng(lat, lon);
                                                mEndList.clear();
                                                mEndList.add(accidentPlace);
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
                                                    mTts.startSpeaking("开始拖车,预定目的地" + info.getDest(), mSynListener);
                                                    amap.clear(true);
                                                    float dis_moved_lastTime = sp.getFloat("dis_moved", 0.0f);
                                                    dis_moved = dis_moved_lastTime;
                                                    distance_moved.setText("已行驶:" + DistanceUtil.checkDistance(dis_moved_lastTime) + "km");
                                                    destination.setText(info.getDest());
                                                    tv_take_photo.setText("结束拖车,按要求拍照");
                                                    cancelOrder.setVisibility(View.GONE);
                                                    cancelOrder.setClickable(false);
                                                    firstResume_tuoche = true;
                                                    mAMapNavi.calculateDriveRoute(mStartList, mEndList, mWayPointList, PathPlanningStrategy.DRIVING_DEFAULT);

                                                }


                                            } else {


                                            }
                                        } else if (info.getOrderStatus() == 21) {

                                            NaviLatLng startPoint = null;
                                            NaviLatLng endPoint = null;
                                            NaviLatLng wayPoint = null;
                                            List<NaviLatLng> startList = new ArrayList<NaviLatLng>();
                                            List<NaviLatLng> endList = new ArrayList<NaviLatLng>();
                                            List<NaviLatLng> wayPointList = new ArrayList<NaviLatLng>();

                                            startPoint = new NaviLatLng(info.getReal_take_latitude(), info.getReal_take_longitude());
                                            endPoint = new NaviLatLng(info.getReal_latitude(), info.getReal_longitude());
                                            startList.add(startPoint);
                                            endList.add(endPoint);

                                            head_map.setVisibility(View.GONE);
                                            head_map_tel_navi.setVisibility(View.VISIBLE);
                                            menu.setVisibility(View.VISIBLE);
                                            startNavi.setVisibility(View.GONE);
                                            coursePreview.setVisibility(View.GONE);
                                            mTts.startSpeaking("请按要求拍摄照片,结束拖车", mSynListener);
                                            amap.clear(true);
                                            float dis_moved_lastTime = (float) info.getReal_tuoche_dis();
                                            distance_moved.setText("已行驶:" + DistanceUtil.checkDistance(dis_moved_lastTime) + "km");
                                            destination.setText(info.getReal_dest());
                                            mapView.setVisibility(View.GONE);
                                            mAMapNaviView.setVisibility(View.VISIBLE);
                                            tv_take_photo.setText("拍摄照片,结束拖车");
                                            cancelOrder.setVisibility(View.GONE);
                                            cancelOrder.setClickable(false);
                                            shouldNotStartNavi = true;
                                            mAMapNavi.calculateDriveRoute(startList, endList, wayPointList, PathPlanningStrategy.DRIVING_DEFAULT);

                                        } else if (info.getOrderStatus() == 31) {

                                            Intent intent = new Intent(getActivity().getApplicationContext(), AppraiseActivity.class);
                                            startActivity(intent);
                                        } else if (info.getOrderStatus() == 32) {

                                            Intent intent = new Intent(getActivity().getApplicationContext(), PayActivity.class);
                                            startActivity(intent);

                                        }


                                    } else {
                                        //非拖车

                                        if (info.getOrderStatus() == 3) {
                                            //已经接单
                                            if (!firstResume) {
                                                String trace = oiUtil.readJWD(oiUtil.path_drag);
                                                NaviLatLng startPoint = null;
                                                if (trace != null) {
                                                    String[] array_trace = trace.trim().split("\\|");
                                                    String[] array_startPoint = (array_trace[array_trace.length - 1]).split(",");
                                                    startPoint = new NaviLatLng(Double.parseDouble(array_startPoint[1]), Double.parseDouble(array_startPoint[0]));
                                                }
                                                mStartList.clear();
                                                if (startPoint != null) {
                                                    mStartList.add(startPoint);
                                                }
                                                double lat = info.getLatitude();
                                                double lon = info.getLongitude();
                                                NaviLatLng accidentPlace = new NaviLatLng(lat, lon);
                                                mEndList.clear();
                                                mEndList.add(accidentPlace);
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
                                                    mTts.startSpeaking("继续任务", mSynListener);
                                                    amap.clear(true);
                                                    float dis_moved_lastTime = sp.getFloat("dis_moved", 0.0f);
                                                    dis_moved = dis_moved_lastTime;
                                                    distance_moved.setText("已行驶:" + DistanceUtil.checkDistance(dis_moved_lastTime) + "km");
                                                    destination.setText(info.getAddress());

                                                    firstResume = true;

                                                    mAMapNavi.calculateDriveRoute(mStartList, mEndList, mWayPointList, PathPlanningStrategy.DRIVING_DEFAULT);

                                                }


                                            }


                                        } else if (info.getOrderStatus() == 4) {//到达救援现场 还未拍照

                                            NaviLatLng startPoint = null;
                                            NaviLatLng endPoint = null;
                                            NaviLatLng wayPoint = null;
                                            List<NaviLatLng> startList = new ArrayList<NaviLatLng>();
                                            List<NaviLatLng> endList = new ArrayList<NaviLatLng>();
                                            List<NaviLatLng> wayPointList = new ArrayList<NaviLatLng>();
                                            startPoint = new NaviLatLng(info.getReal_take_latitude(), info.getReal_take_longitude());
                                            endPoint = new NaviLatLng(info.getReal_latitude(), info.getReal_longitude());
                                            startList.add(startPoint);
                                            endList.add(endPoint);
                                            Log.d("real_info", info.toString());
                                            head_map.setVisibility(View.GONE);
                                            head_map_tel_navi.setVisibility(View.VISIBLE);
                                            menu.setVisibility(View.VISIBLE);
                                            startNavi.setVisibility(View.GONE);
                                            coursePreview.setVisibility(View.GONE);
                                            mTts.startSpeaking("到达救援现场，请按要求拍摄照片", mSynListener);
                                            amap.clear(true);
                                            float dis_moved_lastTime = (float) info.getReal_dis();
                                            distance_moved.setText("已行驶:" + DistanceUtil.checkDistance(dis_moved_lastTime) + "km");
                                            destination.setText(info.getReal_address());
                                            mapView.setVisibility(View.GONE);
                                            mAMapNaviView.setVisibility(View.VISIBLE);
                                            shouldNotStartNavi = true;
                                            mAMapNavi.calculateDriveRoute(startList, endList, wayPointList, PathPlanningStrategy.DRIVING_DEFAULT);


                                        } else if (info.getOrderStatus() == 31) {//已经拍照 上传照片 订单完成 未评价

                                            Intent intent = new Intent(getActivity().getApplicationContext(), AppraiseActivity.class);
                                            getActivity().startActivity(intent);

                                        } else if (info.getOrderStatus() == 32) {//等待支付

                                            Intent intent = new Intent(getActivity().getApplicationContext(), PayActivity.class);
                                            startActivity(intent);
                                        }

                                    }

                                    if(info.getIsPaodan()==1){

                                        cancelOrder.setVisibility(View.GONE);
                                        cancelOrder.setClickable(false);
                                    }


                                } catch (Exception e) {


                                }

                            }

                            @Override
                            public void onRequestFailed(Object obj) {

                            }

                        }).execute(String.format(Constants.ORDER_INFO_URL, orderId));


                    } else {


                    }

                } catch (Exception e) {

                    e.printStackTrace();
                }


            }

            @Override
            public void onRequestFailed(Object obj) {

            }
        }).execute(Constants.CHECK_IF_ONLINE_URL);


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
       // amap = null;
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
//        SharedPreferences sp = getActivity().getSharedPreferences("newOrderComing", Context.MODE_PRIVATE);
//        sp.edit().remove("newOrderComing").commit();
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

        otherService = view.findViewById(R.id.other_service);
        trailerService = view.findViewById(R.id.trailer_service);
        order_type = (TextView) view.findViewById(R.id.order_type);
        address = (TextView) view.findViewById(R.id.address);
        address_tuoche1 = (TextView) view.findViewById(R.id.address_tuoche1);
        address_tuoche2 = (TextView) view.findViewById(R.id.address_tuoche2);

        tv_take_photo = (TextView) view.findViewById(R.id.tv_take_photo);

        tel_customer1 = (ImageView) view.findViewById(R.id.tel_customer1);
        tel_customer2 = (Button) view.findViewById(R.id.tel_customer2);

        tel_customer1.setOnClickListener(this);
        tel_customer2.setOnClickListener(this);

        throw_price = view.findViewById(R.id.throw_price);
        tv_throw_price = (TextView) view.findViewById(R.id.tv_throw_price);

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
                                    progressBar.setVisibility(View.GONE);
                                    if (!speakOnce) {

                                        mTts.startSpeaking("您已成功上线，开始接单", mSynListener);
                                        speakOnce = true;
                                    }


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
                        newOrder_bean = new NewOrder(data);
                        order_id = newOrder_bean.getOrderId();
                        owner_mobile = newOrder_bean.getOwner_moblie();
                        s_destination = newOrder_bean.getAddress();
                        String speak_destination = s_destination;
                        speak_destination = speak_destination.replaceFirst("区", "区,");
                        int serviceType = newOrder_bean.getService_type();
                        int is_appointed = newOrder_bean.getIs_appointed();
                        double lat = newOrder_bean.getLatitude();
                        double lon = newOrder_bean.getLongitude();
                        double take_distance = newOrder_bean.getTake_distance();
                        NaviLatLng accidentPlace = new NaviLatLng(lat, lon);
                        LatLng latLng_accidentPlace = new LatLng(lat, lon);
                        SharedPreferences sp2 = getActivity().getSharedPreferences("order", Context.MODE_PRIVATE);
                        sp2.edit().putString("order", data.toString()).commit();
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

                        if(newOrder_bean.getIsPaodan()==0){
                            isPaodn= false;
                            throw_price.setVisibility(View.GONE);
                            order_type.setText(s_type + "订单");
                            if(newOrder_bean.getIs_one_price()==2){

                                //起步价模式
                                if (serviceType == 1) {

                                    trailerService.setVisibility(View.VISIBLE);
                                    otherService.setVisibility(View.GONE);
                                    address_tuoche1.setText(s_destination);
                                    String s_address_tuoche2 = newOrder_bean.getAddress_tuoche();
                                    address_tuoche2.setText(s_address_tuoche2);

                                } else {

                                    trailerService.setVisibility(View.GONE);
                                    otherService.setVisibility(View.VISIBLE);
                                    address.setText(s_destination);
                                }

                                mTts.startSpeaking("您有新的" + s_type + "订单,地址" + speak_destination + ",距离约为" + take_distance + "公里", mSynListener);

                            }else{
                                //一口价模式
                                throw_price.setVisibility(View.VISIBLE);
                                tv_throw_price.setText(newOrder_bean.getBase_price()+"");
                                DisplayMetrics dm =getResources().getDisplayMetrics();
                                float density = dm.density;

                                if (serviceType == 1) {

                                    ViewGroup.MarginLayoutParams layoutParam = (ViewGroup.MarginLayoutParams) trailerService.getLayoutParams();
                                    layoutParam.topMargin =(int)(170*density);
                                    trailerService.setLayoutParams(layoutParam);
                                    trailerService.setVisibility(View.VISIBLE);
                                    otherService.setVisibility(View.GONE);
                                    address_tuoche1.setText(s_destination);
                                    String s_address_tuoche2 = newOrder_bean.getAddress_tuoche();
                                    address_tuoche2.setText(s_address_tuoche2);

                                } else {

                                    ViewGroup.MarginLayoutParams layoutParam = (ViewGroup.MarginLayoutParams)  otherService.getLayoutParams();
                                    layoutParam.topMargin =(int)(170*density);
                                    otherService.setLayoutParams(layoutParam);
                                    trailerService.setVisibility(View.GONE);
                                    otherService.setVisibility(View.VISIBLE);
                                    address.setText(s_destination);
                                }

                                mTts.startSpeaking("您有新的" + s_type + "订单,一口价"+newOrder_bean.getBase_price()+"元,地址" + speak_destination + ",距离约为" + take_distance + "公里", mSynListener);

                            }



                        }else{
                            isPaodn=true;
                            order_type.setText(s_type + "抛单");
                            throw_price.setVisibility(View.VISIBLE);
                            tv_throw_price.setText(newOrder_bean.getOnePrice()+"");
                            DisplayMetrics dm =getResources().getDisplayMetrics();
                            float density = dm.density;

                            if (serviceType == 1) {

                                ViewGroup.MarginLayoutParams layoutParam = (ViewGroup.MarginLayoutParams) trailerService.getLayoutParams();
                                layoutParam.topMargin =(int)(170*density);
                                trailerService.setLayoutParams(layoutParam);
                                trailerService.setVisibility(View.VISIBLE);
                                otherService.setVisibility(View.GONE);
                                address_tuoche1.setText(s_destination);
                                String s_address_tuoche2 = newOrder_bean.getAddress_tuoche();
                                address_tuoche2.setText(s_address_tuoche2);

                            } else {

                                ViewGroup.MarginLayoutParams layoutParam = (ViewGroup.MarginLayoutParams)  otherService.getLayoutParams();
                                layoutParam.topMargin =(int)(170*density);
                                otherService.setLayoutParams(layoutParam);
                                trailerService.setVisibility(View.GONE);
                                otherService.setVisibility(View.VISIBLE);
                                address.setText(s_destination);
                            }

                            mTts.startSpeaking("您有新的" + s_type + "抛单,一口价"+newOrder_bean.getOnePrice()+"元,地址" + speak_destination + ",距离约为" + take_distance + "公里", mSynListener);

                        }

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
                    oiUtil.deleteJWD(oiUtil.path_drag);
                    SharedPreferences sp = getActivity().getSharedPreferences("dis_moved", Context.MODE_PRIVATE);
                    sp.edit().remove("dis_moved").commit();

                } else if (msg.what == 8) {
                    progressBar.setVisibility(View.GONE);
                    oiUtil.deleteJWD(oiUtil.path_drag);
                    mTts.startSpeaking("到达救援现场,请按要求拍照", mSynListener);
                } else if (msg.what == 9) {

                    progressBar.setVisibility(View.GONE);
                    oiUtil.deleteJWD(oiUtil.path_drag);
                    mTts.startSpeaking("结束拖车,请按要求拍照", mSynListener);
                }else if(msg.what ==10){
                    mTts.startSpeaking("另一个设备正在登陆这个账号,3秒后应用将被强制退出", mSynListener);
                    try {
                        Thread.sleep(7000);
                    } catch (InterruptedException e) {
                        e.printStackTrace();
                    }
                    Session.getInstance().logout(getActivity().getApplicationContext());
                    Intent intent = new Intent(getActivity().getApplicationContext(), LoginActivity.class);
                    startActivity(intent);
                    getActivity().finish();
                }else if(msg.what==11){

                    mTts.startSpeaking("订单已被后台取消", mSynListener);
                }else if(msg.what ==12){
                    MyApplication.application.isAfterOrder = false;
                    mTts.startSpeaking("订单完成，请继续接单", mSynListener);

                }
            }
        };
        myBroadcastReciever = new MyBroadcastReciever();
        IntentFilter intentFilter = new IntentFilter();
        intentFilter.addAction("newOrder");
        intentFilter.addAction("closeOrder");
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
        SharedPreferences sp = getActivity().getSharedPreferences("order",Context.MODE_PRIVATE);
        SharedPreferences.Editor editor = sp.edit();
        editor.remove("order").commit();

        SharedPreferences sp2 = getActivity().getSharedPreferences("orderInfo", Context.MODE_PRIVATE);
        sp2.edit().remove("orderInfo").commit();

        timeStamp = 0l;
        dis_moved = 0.0f;
        last_distance = 0.0f;
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
        canCountGPSPoint = true;
        mAMapNavi.startAimlessMode(AimLessMode.NONE_DETECTED);
//        AMapNaviViewOptions option = mAMapNaviView.getViewOptions();
//        option.setZoom(13);
//        mAMapNaviView.setViewOptions(option);
        mAMapNaviView.recoverLockMode();
        mAMapNaviView.setLockZoom(13);
        //  handler.sendEmptyMessage(0);


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
        AMapNaviPath naviPath = mAMapNavi.getNaviPath();
        if (naviPath == null) {
            return;
        }
        if (!shouldNotStartNavi) {

            progressBar.setVisibility(View.GONE);
            timeStamp = new Date().getTime();

            float route_distance = DistanceUtil.checkDistance(naviPath.getAllLength() / 1000f);
            distance_route.setText("总路程：" + route_distance + "km");
            mapView.setVisibility(View.GONE);
            mAMapNaviView.setVisibility(View.VISIBLE);
            mAMapNavi.startNavi(NaviType.GPS);
            handler.sendEmptyMessage(0);
            latLngs = new ArrayList<LatLng>();

        } else {

            float route_distance = DistanceUtil.checkDistance(naviPath.getAllLength() / 1000f);
            distance_route.setText("总路程：" + route_distance + "km");
            handler.sendEmptyMessage(0);
            shouldNotStartNavi = false;
        }

        Log.d("distance", timeStamp + "");

    }


    @Override
    public void onNaviInfoUpdate(NaviInfo naviInfo) {
        long timeNow = new Date().getTime();
        LatLng currentLatLng = new LatLng(naviInfo.getCoord().getLatitude(), naviInfo.getCoord().getLongitude());
        sp = getActivity().getSharedPreferences("dis_moved", Context.MODE_PRIVATE);

        if (timeNow > timeStamp) {

            if (latLngs.size() >= 2) {
                LatLng positionLatLng = latLngs.get(latLngs.size() - 1);
                int distance = (int) AMapUtils.calculateLineDistance(currentLatLng, positionLatLng);
                float f_dis = DistanceUtil.checkDistance(distance / 1000f);
                last_distance = f_dis;
                dis_moved += f_dis;
                sp.edit().putFloat("dis_moved", dis_moved).commit();
                distance_moved.setText("已行驶:" + DistanceUtil.checkDistance(dis_moved) + "km");
            }
            try {
                Map<String, Object> map = new HashMap<>();
                map.put("lat", String.valueOf(currentLatLng.latitude));
                map.put("lng", String.valueOf(currentLatLng.longitude));
                // map.put("status", String.valueOf(status));
                new NewHttpPostTask(getActivity().getApplicationContext(), new OnHttpRequestListener() {
                    @Override
                    public void onRequestCompleted(Object obj) {
                        Log.e(TAG, "breaf !" + obj.toString());

                    }

                    @Override
                    public void onRequestFailed(Object obj) {
                    }
                }).execute(Constants.USER_LOCATION_URL, map);
            } catch (Exception e) {

                e.printStackTrace();
            }

            Log.d("distance", timeStamp + ":  " + currentLatLng.latitude + "---" + currentLatLng.longitude + "---" + last_distance + "---"
                    + distance_moved.getText() + "---" + dis_moved);
            Toast.makeText(getActivity(),timeStamp + ":" + currentLatLng.latitude + "---" + currentLatLng.longitude + "---" + last_distance + "---"
                            + distance_moved.getText() + "---" + dis_moved,Toast.LENGTH_SHORT).show();
            timeStamp = timeNow;
            latLngs.add(currentLatLng);
            trace_data += currentLatLng.longitude + "," + currentLatLng.latitude + "|";
            oiUtil.writeJWD(currentLatLng.latitude, currentLatLng.longitude, oiUtil.path_drag);

        }
    }


    @Override
    public void onLocationChange(AMapNaviLocation aMapNaviLocation) {

        if (canCountGPSPoint) {
            LatLng currentLatLng = new LatLng(aMapNaviLocation.getCoord().getLatitude(), aMapNaviLocation.getCoord().getLongitude());
             sp = getActivity().getSharedPreferences("dis_moved", Context.MODE_PRIVATE);
            if (aMapNaviLocation.getAccuracy() <= 20) {
                long timeNow = new Date().getTime();
                if (timeNow > timeStamp) {
                   // LatLng currentLatLng = new LatLng(aMapNaviLocation.getCoord().getLatitude(), aMapNaviLocation.getCoord().getLongitude());
                    if (latLngs.size() > 2) {
                        LatLng positionLatLng = latLngs.get(latLngs.size() - 1);
                        int distance = (int) AMapUtils.calculateLineDistance(currentLatLng, positionLatLng);
                        float f_dis = DistanceUtil.checkDistance(distance / 1000f);
                        dis_moved += f_dis;
                        sp.edit().putFloat("dis_moved", dis_moved).commit();
                        distance_moved.setText("已行驶:" + DistanceUtil.checkDistance(dis_moved) + "公里");
                    }
                    try {
                        Map<String, Object> map = new HashMap<>();
                        map.put("lat", String.valueOf(currentLatLng.latitude));
                        map.put("lng", String.valueOf(currentLatLng.longitude));
                        // map.put("status", String.valueOf(status));
                        new NewHttpPostTask(getActivity().getApplicationContext(), new OnHttpRequestListener() {
                            @Override
                            public void onRequestCompleted(Object obj) {
                                Log.e(TAG, "breaf !" + obj.toString());

                            }

                            @Override
                            public void onRequestFailed(Object obj) {
                            }
                        }).execute(Constants.USER_LOCATION_URL, map);
                    } catch (Exception e) {

                        e.printStackTrace();
                    }

                    latLngs.add(currentLatLng);
                    // Toast.makeText(getActivity(), "navi listener ---" + currentLatLng.longitude + "---" + currentLatLng.latitude, Toast.LENGTH_SHORT).show();
                    Toast.makeText(getActivity(),"GPS"+"---"+timeStamp + ":" + currentLatLng.latitude + "---" + currentLatLng.longitude + "---" + last_distance + "---"
                            + distance_moved.getText() + "---" + dis_moved,Toast.LENGTH_SHORT).show();
                    timeStamp = timeNow;
                    trace_data += currentLatLng.longitude + "," + currentLatLng.latitude + "|";
                    oiUtil.writeJWD(currentLatLng.latitude,currentLatLng.longitude,oiUtil.path_drag);
                }
            }
        }
    }


    @Override
    public void onGetNavigationText(int arg0, String arg1) {
        // TODO Auto-generated method stub
        if (!isNavi) {


        } else {

            mTts.startSpeaking(arg1, mSynListener);

        }

    }


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

    private Boolean isGPSReady = false;
    @Override
    public void onLocationChanged(AMapLocation aMapLocation) {

        if (mListener != null && aMapLocation != null) {
            if (aMapLocation != null
                    && aMapLocation.getErrorCode() == 0) {

                isGPSReady = true;

                Log.d("location", aMapLocation.getLocationType() + "");
                mStartLatlng = new NaviLatLng(aMapLocation.getLatitude(), aMapLocation.getLongitude());
                LatLng latLng = new LatLng(aMapLocation.getLatitude(), aMapLocation.getLongitude());

                amap.moveCamera(CameraUpdateFactory.changeTilt(0));
                if (mListener != null) {
                    mListener.onLocationChanged(aMapLocation);// 显示系统小蓝点
                }
                try {
                    Map<String, Object> map = new HashMap<>();
                    map.put("lat", String.valueOf(aMapLocation.getLatitude()));
                    map.put("lng", String.valueOf(aMapLocation.getLongitude()));
                    // map.put("status", String.valueOf(status));
                    new NewHttpPostTask(getActivity().getApplicationContext(), new OnHttpRequestListener() {
                        @Override
                        public void onRequestCompleted(Object obj) {
                            Log.e(TAG, "breaf !" + obj.toString());
                            JSONObject jsonObject = null;
                            try {
                                jsonObject = new JSONObject(obj.toString());
                                int result = jsonObject.getInt("result");
                                Log.d("user_result",result+"---1");
                                if(result == 2){
                                    handler.sendEmptyMessage(10);

                                }
                            } catch (JSONException e) {
                                e.printStackTrace();
                            }
                        }
                        @Override
                        public void onRequestFailed(Object obj) {
                        }
                    }).execute(Constants.USER_LOCATION_URL, map);
                } catch (Exception e) {

                    e.printStackTrace();
                }
                String type = "";
                if (aMapLocation.getLocationType() == 1) {

                    type = "GPS";
                } else if (aMapLocation.getLocationType() == 2) {
                    type = "前次定位";
                } else if (aMapLocation.getLocationType() == 4) {
                    type = "缓存定位";

                } else if (aMapLocation.getLocationType() == 5) {
                    type = "WIFI定位";

                } else if (aMapLocation.getLocationType() == 6) {

                    type = "基站定位";
                }
               //Toast.makeText(this.getActivity().getApplicationContext(),type+":::"+latLng.latitude + "--" + latLng.longitude, Toast.LENGTH_SHORT).show();
                // Log.d(TAG,latLng.latitude+"--"+latLng.longitude);

            } else {

                isGPSReady = false;
                String errText = "定位失败," + aMapLocation.getErrorCode() + ": " + aMapLocation.getErrorInfo();
                Log.d("location", errText);
              //  Toast.makeText(this.getActivity().getApplicationContext(), "定位失败无法接单,请到开阔地带继续接单", Toast.LENGTH_SHORT).show();
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
            locationOption.setLocationCacheEnable(false);
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
                    speakOnce = false;
                    // switch_online.setText("离线>>>");
//                    deactivate();
//                    amap.clear();
//                    amap = null;
                }
                break;

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
                        progressBar.setVisibility(View.VISIBLE);
                        String trace = oiUtil.readJWD(oiUtil.path_drag);
                        Map<String, Object> map = new HashMap<>();
                        map.put("lat", String.valueOf(mStartLatlng.getLatitude()));
                        map.put("lng", String.valueOf(mStartLatlng.getLongitude()));
                        map.put("distance", dis_moved);
                        map.put("trace_data", trace);
                        progressBar.setVisibility(View.VISIBLE);
                        new NewHttpPutTask(getActivity().getApplicationContext(), new OnHttpRequestListener() {
                            @Override
                            public void onRequestCompleted(Object obj) {

                                JSONObject jsonObject;
                                try {
                                    jsonObject = new JSONObject(obj.toString());
                                    if (jsonObject.getInt("result") == 1) {

                                        new HttpGetTask(getActivity().getApplicationContext(), new OnHttpRequestListener() {
                                            @Override
                                            public void onRequestCompleted(Object obj) {
                                                progressBar.setVisibility(View.GONE);
                                                mAMapNavi.stopNavi();
                                                SharedPreferences sp = getActivity().getSharedPreferences("newOrderComing", Context.MODE_PRIVATE);
                                                sp.edit().remove("newOrderComing").commit();
                                                // latLngs.clear();
                                                dis_moved = 0;
                                                handler.sendEmptyMessage(7);
                                                amap.clear(true);
                                                setUpMap();
                                            }

                                            @Override
                                            public void onRequestFailed(Object obj) {

                                            }
                                        }).execute(String.format(Constants.CLOSE_ORDER_URL, order_id));

                                    } else {
                                        mAMapNavi.stopNavi();
                                        progressBar.setVisibility(View.GONE);
                                        Toast.makeText(getActivity().getApplicationContext(), "轨迹提交失败,请重新提交", Toast.LENGTH_SHORT).show();
                                    }
                                } catch (Exception e) {


                                }

                            }

                            @Override
                            public void onRequestFailed(Object obj) {

                            }
                        }).execute(String.format(Constants.ARRIVE_SUBMIT_URL, order_id), map);
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
                canGetNewOrder = true;
                break;
            case R.id.intoOrder:
                newOrder.clearAnimation();
                newOrder.setVisibility(View.GONE);

                mStartList.clear();
                if (mStartLatlng != null) {
                    mStartList.add(mStartLatlng);
                }

                if (mStartList.isEmpty()) {
                    Toast.makeText(getActivity().getApplicationContext(), "没有定位到起点", Toast.LENGTH_SHORT).show();
                } else if (mEndList.isEmpty()) {
                    Toast.makeText(getActivity().getApplicationContext(), "没有定位到目的地", Toast.LENGTH_SHORT).show();
                } else {

                    progressBar.setVisibility(View.VISIBLE);

                    Map<String, Object> map = new HashMap<>();
                    map.put("lat", String.valueOf(mStartLatlng.getLatitude()));
                    map.put("lng", String.valueOf(mStartLatlng.getLongitude()));

                    new NewHttpPutTask(getActivity().getApplicationContext(), new OnHttpRequestListener() {

                        @Override
                        public void onRequestCompleted(Object obj) {
                            JSONObject jsonObject;
                            try {
                                jsonObject = new JSONObject(obj.toString());
                                Log.d("take_info", obj.toString());
                                if (jsonObject.getInt("result") == 1) {

                                    head_map.setVisibility(View.GONE);
                                    head_map_tel_navi.setVisibility(View.VISIBLE);
                                    menu.setVisibility(View.VISIBLE);
                                    startNavi.setVisibility(View.VISIBLE);
                                    coursePreview.setVisibility(View.VISIBLE);
                                    if(isPaodn){

                                        cancelOrder.setVisibility(View.GONE);
                                        cancelOrder.setClickable(false);
                                    }else{
                                        cancelOrder.setVisibility(View.VISIBLE);
                                        cancelOrder.setClickable(true);
                                    }

                                    tv_take_photo.setText("到达救援现场,并拍照");
                                    mTts.startSpeaking("开始任务", mSynListener);
                                    SharedPreferences sp4 = getActivity().getSharedPreferences("newOrderComing", Context.MODE_PRIVATE);
                                    sp4.edit().remove("newOrderComing").commit();
                                    SharedPreferences sp = getActivity().getSharedPreferences("dis_moved", Context.MODE_PRIVATE);
                                    sp.edit().remove("dis_moved").commit();
                                    //latLngs.clear();
                                    dis_moved = 0;
                                    amap.clear(true);
                                    distance_moved.setText("已行驶:0.0km");
                                    destination.setText(s_destination);
                                    mAMapNavi.calculateDriveRoute(mStartList, mEndList, mWayPointList, PathPlanningStrategy.DRIVING_DEFAULT);

                                } else {
                                    progressBar.setVisibility(View.GONE);
                                    newOrder.clearAnimation();
                                    newOrder.setVisibility(View.GONE);
                                    head_map.setVisibility(View.VISIBLE);
                                    getActivity().findViewById(android.R.id.tabhost).setVisibility(View.VISIBLE);
                                    mTts.startSpeaking("订单已被接走了", mSynListener);
                                    SharedPreferences sp1 = getActivity().getSharedPreferences("newOrderComing", Context.MODE_PRIVATE);
                                    sp1.edit().remove("newOrderComing").commit();
                                    SharedPreferences sp = getActivity().getSharedPreferences("dis_moved", Context.MODE_PRIVATE);
                                    sp.edit().remove("dis_moved").commit();

                                }
                            } catch (JSONException e) {
                                e.printStackTrace();
                            }

                        }

                        @Override
                        public void onRequestFailed(Object obj) {

                            progressBar.setVisibility(View.GONE);
                            newOrder.clearAnimation();
                            newOrder.setVisibility(View.GONE);
                            head_map.setVisibility(View.VISIBLE);
                            getActivity().findViewById(android.R.id.tabhost).setVisibility(View.VISIBLE);
                            mTts.startSpeaking("接单失败", mSynListener);

                        }
                    }).execute(String.format(Constants.ACCEPT_ORDER_URL, order_id), map);

                }
                break;
            case R.id.take_photo:
                Log.d("liuchen",order_id+"");
                if(info==null){
                    new HttpGetTask(getActivity().getApplicationContext(), new OnHttpRequestListener() {

                        @Override
                        public void onRequestCompleted(Object obj) {

                            JSONObject jsonObject;
                            try {
                                Log.d("orderInfo_activity", obj.toString());
                                jsonObject = new JSONObject(obj.toString());
                                info = new OrderInfo(jsonObject);
                                Log.d("liuchen",order_id+"--"+info.getId()+"--"+info.getOrderStatus());

                            }catch (Exception e){

                                e.printStackTrace();
                            }
                        }

                        @Override
                        public void onRequestFailed(Object obj) {


                        }

                    }).execute(String.format(Constants.ORDER_INFO_URL, order_id));

                }
                if (info != null && info.getOrderStatus() == 4) {

                    progressBar.setVisibility(View.VISIBLE);
                    new Handler().postDelayed(new Runnable() {
                        @Override
                        public void run() {

                            progressBar.setVisibility(View.GONE);
                            Intent intent = new Intent(getActivity().getApplicationContext(), NewTakePhotoActivity.class);
                            startActivity(intent);
                        }
                    }, 2000);


                } else if (info != null && info.getOrderStatus() == 5) {//拖车第二张照片，开始拖车

                    progressBar.setVisibility(View.VISIBLE);
                    Map<String, Object> map = new HashMap<>();
                    map.put("lat", String.valueOf(mStartLatlng.getLatitude()));
                    map.put("lng", String.valueOf(mStartLatlng.getLongitude()));
                    new NewHttpPutTask(getActivity().getApplicationContext(), new OnHttpRequestListener() {
                        @Override
                        public void onRequestCompleted(Object obj) {

                            //  Log.d("start_tuoche",obj.toString());

                            JSONObject jsonObject;
                            try {
                                jsonObject = new JSONObject(obj.toString());
                                if (jsonObject.getInt("result") == 1) {

                                    progressBar.setVisibility(View.GONE);
                                    SharedPreferences sp = getActivity().getSharedPreferences("dis_moved", Context.MODE_PRIVATE);
                                    sp.edit().remove("dis_moved").commit();
                                   // latLngs.clear();
                                    Intent intent = new Intent(getActivity().getApplicationContext(), NewTakePhotoActivity.class);
                                    intent.putExtra("tuoche", "tuoche");
                                    startActivity(intent);


                                } else {

                                    progressBar.setVisibility(View.GONE);
                                    Toast.makeText(getActivity().getApplicationContext(), "请求提交失败,请重新提交", Toast.LENGTH_SHORT).show();

                                }
                            } catch (Exception e) {


                            }

                        }

                        @Override
                        public void onRequestFailed(Object obj) {

                            //  Log.d("start_tuoche",obj.toString());

                        }

                    }).execute(String.format(Constants.DRAG_START, order_id), map);


                } else if (info != null && info.getOrderStatus() == 11) {

                    progressBar.setVisibility(View.VISIBLE);
                    new Handler().postDelayed(new Runnable() {
                        @Override
                        public void run() {

                            progressBar.setVisibility(View.GONE);
                            Intent intent = new Intent(getActivity().getApplicationContext(), NewTakePhotoActivity.class);
                            intent.putExtra("tuoche", "tuoche");
                            startActivity(intent);
                        }
                    }, 2000);

                } else if (info != null && info.getOrderStatus() == 21) {


                    progressBar.setVisibility(View.VISIBLE);
                    new Handler().postDelayed(new Runnable() {
                        @Override
                        public void run() {

                            progressBar.setVisibility(View.GONE);
                            Intent intent = new Intent(getActivity().getApplicationContext(), NewTakePhotoActivity.class);
                            intent.putExtra("tuoche2", "tuoche2");
                            startActivity(intent);
                        }
                    }, 2000);


                } else if (info != null && info.getOrderStatus() == 6) {//结束拖车，拍最后一张照片


                    dialog = new Dialog(getActivity(), R.style.bubble_dialog);
                    View view1 = getActivity().getLayoutInflater().inflate(R.layout.dialog_msg_notice,
                            null);
                    Button tvConfirm1 = (Button) view1.findViewById(R.id.btn_notice_confirm);
                    Button tvCancel1 = (Button) view1.findViewById(R.id.btn_notice_cancel);
                    TextView tvMsg1 = (TextView) view1.findViewById(R.id.tv_notice_msg);
                    tvMsg1.setText("是否结束拖车");
                    tvConfirm1.setOnClickListener(new View.OnClickListener() {

                        @Override
                        public void onClick(View v) {
                            dialog.dismiss();
                            progressBar.setVisibility(View.VISIBLE);
                            String trace = oiUtil.readJWD(oiUtil.path_drag);
                            Map<String, Object> map = new HashMap<>();
                            map.put("lat", String.valueOf(mStartLatlng.getLatitude()));
                            map.put("lng", String.valueOf(mStartLatlng.getLongitude()));
                            map.put("distance", dis_moved);
                            map.put("trace_data", trace);
                            progressBar.setVisibility(View.VISIBLE);
                            new NewHttpPutTask(getActivity().getApplicationContext(), new OnHttpRequestListener() {
                                @Override
                                public void onRequestCompleted(Object obj) {

                                    JSONObject jsonObject;
                                    try {
                                        jsonObject = new JSONObject(obj.toString());
                                        if (jsonObject.getInt("result") == 1) {

                                            mAMapNavi.stopNavi();
                                            handler.sendEmptyMessage(9);
                                            canCountGPSPoint = false;
                                            SharedPreferences sp = getActivity().getSharedPreferences("dis_moved", Context.MODE_PRIVATE);
                                            sp.edit().remove("dis_moved").commit();
                                            //latLngs.clear();
                                            Intent intent = new Intent(getActivity().getApplicationContext(), NewTakePhotoActivity.class);
                                            intent.putExtra("tuoche2", "tuoche2");
                                            startActivity(intent);


                                        } else {
                                            mAMapNavi.stopNavi();
                                            progressBar.setVisibility(View.GONE);
                                            Toast.makeText(getActivity().getApplicationContext(), "轨迹提交失败,请重新提交", Toast.LENGTH_SHORT).show();
                                        }
                                    } catch (Exception e) {
                                        e.printStackTrace();

                                    }

                                }

                                @Override
                                public void onRequestFailed(Object obj) {

                                }
                            }).execute(String.format(Constants.SUBMIT_TUOCHE_URL, order_id), map);

                        }


                    });
                    tvCancel1.setOnClickListener(new View.OnClickListener() {

                        @Override
                        public void onClick(View v) {
                            dialog.dismiss();
                        }
                    });

                    dialog.setContentView(view1);
                    dialog.setCanceledOnTouchOutside(false);
                    Window window1 = dialog.getWindow();
                    WindowManager.LayoutParams params1 = window1.getAttributes();
                    Point point1 = JSONUtil.getDeviceSize(getActivity());
                    params1.width = Math.round(point1.x * 5 / 7);
                    window1.setAttributes(params1);
                    try {
                        dialog.show();
                    } catch (Exception e) {
                        e.printStackTrace();
                    }

                } else {
                    String trace = oiUtil.readJWD(oiUtil.path_drag);
                    Map<String, Object> map = new HashMap<>();
                    map.put("lat", String.valueOf(mStartLatlng.getLatitude()));
                    map.put("lng", String.valueOf(mStartLatlng.getLongitude()));
                    map.put("distance", dis_moved);
                    map.put("trace_data", trace);
                    progressBar.setVisibility(View.VISIBLE);
                    new NewHttpPutTask(getActivity().getApplicationContext(), new OnHttpRequestListener() {
                        @Override
                        public void onRequestCompleted(Object obj) {

                            JSONObject jsonObject;
                            try {
                                jsonObject = new JSONObject(obj.toString());
                                if (jsonObject.getInt("result") == 1) {

                                    mAMapNavi.stopNavi();
                                    handler.sendEmptyMessage(8);
                                    canCountGPSPoint = false;
                                    SharedPreferences sp = getActivity().getSharedPreferences("dis_moved", Context.MODE_PRIVATE);
                                    sp.edit().remove("dis_moved").commit();
                                   // latLngs.clear();
                                    Intent intent = new Intent(getActivity().getApplicationContext(), NewTakePhotoActivity.class);
                                    startActivity(intent);


                                } else {
                                    mAMapNavi.stopNavi();
                                    progressBar.setVisibility(View.GONE);
                                    Toast.makeText(getActivity().getApplicationContext(), "轨迹提交失败,请重新提交", Toast.LENGTH_SHORT).show();
                                }
                            } catch (Exception e) {


                            }

                        }

                        @Override
                        public void onRequestFailed(Object obj) {

                        }
                    }).execute(String.format(Constants.ARRIVE_SUBMIT_URL, order_id), map);
                }

                break;

            case R.id.tel_customer1:
                Log.d("mobile",owner_mobile);
                Intent intent = new Intent(Intent.ACTION_CALL,Uri.parse("tel:"+owner_mobile));
                startActivity(intent);
                break;

            case R.id.tel_customer2:
                Log.d("mobile",owner_mobile);
                Intent intent2 = new Intent(Intent.ACTION_CALL,Uri.parse("tel:"+owner_mobile));
                startActivity(intent2);
                break;

        }
    }


    public class MyBroadcastReciever extends BroadcastReceiver {


        @Override
        public void onReceive(Context context, Intent intent) {

            if ("newOrder".equals(intent.getAction())) {

                if (isOnline) {

                    if(isGPSReady) {

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

            }else if("closeOrder".equals(intent.getAction())){
                Log.e("getui_result","个推信息----"+"close");
                progressBar.setVisibility(View.VISIBLE);
              //
                handler.sendEmptyMessage(11);
                handler.postDelayed(new Runnable() {
                    @Override
                    public void run() {
                        progressBar.setVisibility(View.GONE);
                        mAMapNavi.stopNavi();
                        SharedPreferences sp = getActivity().getSharedPreferences("newOrderComing", Context.MODE_PRIVATE);
                        sp.edit().remove("newOrderComing").commit();
                        // latLngs.clear();
                        dis_moved = 0;
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
                        oiUtil.deleteJWD(oiUtil.path_drag);
                        SharedPreferences sp1 = getActivity().getSharedPreferences("dis_moved", Context.MODE_PRIVATE);
                        sp1.edit().remove("dis_moved").commit();
                        amap.clear(true);
                        setUpMap();

                    }
                },3000);

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
