package com.autosos.yd.util;

import android.app.ActivityManager;
import android.app.AlarmManager;
import android.app.Notification;
import android.app.PendingIntent;
import android.app.Service;
import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.Intent;
import android.content.IntentFilter;
import android.content.SharedPreferences;
import android.location.LocationManager;
import android.os.Bundle;
import android.os.IBinder;
import android.os.PowerManager;
import android.util.Log;
import com.autosos.yd.Constants;
import com.autosos.yd.R;
import com.autosos.yd.task.NewHttpPostTask;
import com.autosos.yd.task.OnHttpRequestListener;
import com.autosos.yd.view.LoginActivity;
import com.autosos.yd.view.MainActivity;
import com.autosos.yd.view.SplashActivity;
import com.baidu.location.BDLocation;
import com.baidu.location.BDLocationListener;
import com.baidu.location.LocationClient;
import com.baidu.location.LocationClientOption;
import com.igexin.sdk.PushManager;

import java.util.HashMap;
import java.util.Map;

/**
 * Created by Administrator on 2015/8/7.
 */

public class UpdateStateServe extends Service implements BDLocationListener {
    private Location location;
    public static final String TAG = "UpdateStateServe";
    public static final int UpdateChangeTimeSixty = 1;
    public static final int UpdateChangeTimeTen = 2;
    public static final int UpdateNowStateSixty = 3;
    public static final int UpdateNowStateTen = 4;
    public static LocationClient client;
    private double last_la = 0;
    private double last_lo = 0;
    public static  double latitude = 0;
    public static double longitude = 0;
    public static boolean UpdateStateServeActive = true;
    public static String address = "";
    public static long startUSTime = 0;
    public static int UpdateChangeTime = UpdateNowStateSixty;//0nochange 1: 60s->10s; 2:10s->60s ;3:60s;4:10s;
    public static PowerManager.WakeLock wakeLock = null;
    private int point;
    public static boolean Setting_Sound_OnOff = true;
    public static int CallClick = 0;//用来控制进入订单详情页面的“联系车主”的语音播报  为0时播放，>0不再播报


    public static void acquireWakeLock(Context context) {
        if (null == wakeLock) {
            PowerManager pm = (PowerManager) context.getSystemService(Context.POWER_SERVICE);
            wakeLock = pm.newWakeLock(PowerManager.PARTIAL_WAKE_LOCK
                    | PowerManager.ON_AFTER_RELEASE, context.getClass()
                    .getCanonicalName());
            if (null != wakeLock) {
                Log.e(TAG, "call acquireWakeLock");
                wakeLock.acquire();
            }
        }
    }

    @Override
    public void onCreate() {
        UpdateChangeTime = UpdateNowStateSixty;
        UpdateStateServeActive = true;
        location = new Location();
        point = 0;
        super.onCreate();
        acquireWakeLock(UpdateStateServe.this);
        if(startUSTime < 1440551528 )
             startUSTime = System.currentTimeMillis();
        location(60000);
        Log.e(TAG, "onCreate() executed");


    }

    @Override
    public int onStartCommand(Intent intent, int flags, int startId) {
        Log.e(TAG, "onStartCommand() executed");
        Notification notification = new Notification(R.drawable.icon_location2, getString(R.string.app_serve),System.currentTimeMillis());
        PendingIntent pendingintent = PendingIntent.getActivity(this, 0, new Intent(this, MainActivity.class), 0);
        notification.setLatestEventInfo(this, getResources().getString(R.string.app_name),
                getResources().getString(R.string.app_serve), pendingintent);
        startForeground(0x111, notification);
        notification.flags = Notification.FLAG_NO_CLEAR;
        // UpdateStateServeActive = true;
       // return super.onStartCommand(intent, flags, startId);
        return super.onStartCommand(intent, START_STICKY, startId);
    }

    @Override
    public void onDestroy() {
        super.onDestroy();
        client.unRegisterLocationListener(this);
        client.stop();
        Log.e(TAG, "onDestroy() executed");
        //防止服务停止运行
        stopForeground(true);
        Intent localIntent = new Intent(this,UpdateStateServe.class);
        this.startService(localIntent);
    }

    @Override
    public IBinder onBind(Intent intent) {
        return null;
    }
    private void location(int time) {
        LocationClientOption option = new LocationClientOption();
        option.setOpenGps(true);
        option.setLocationMode(LocationClientOption.LocationMode.Hight_Accuracy);
        option.setCoorType("bd09ll");
        option.setScanSpan(time);
        option.setProdName("com.autosos.yd");
        option.setIsNeedAddress(true);
        option.isLocationNotify();
        client = new LocationClient(this);
        client.setLocOption(option);
        client.registerLocationListener(this);
        client.start();
        Log.e(TAG, "locating ing!");
    }
    @Override
    public void onReceiveLocation(BDLocation bdLocation) {
        Log.e(TAG, "received!");
        cherkGetuiServe();
        if ( UpdateStateServeActive ) {
            if(UpdateChangeTime == 1 || UpdateChangeTime == 2){
                changeClientTime();
            }
            Log.e(TAG, "BDLocation  " + bdLocation.toString() + "***********************" + "la:"
                    + bdLocation.getLatitude() + "**log" + bdLocation.getLongitude() + "****address :" + bdLocation.getAddrStr() +"jiange :"+UpdateChangeTime +
            "Point:"+point);
            if ((bdLocation.getLocType() == 61 || bdLocation.getLocType() == 65 || bdLocation
                    .getLocType() == 161 )&& bdLocation.getLatitude() != 0 && bdLocation.getLongitude() != 0 ) {
                latitude = bdLocation.getLatitude();
                longitude = bdLocation.getLongitude();
                if(bdLocation.getAddrStr() != null )
                    address = bdLocation.getAddrStr();
                savrSPloAndla(bdLocation.getAddrStr());
                if( UpdateChangeTime == UpdateNowStateSixty){
                    UpdateMessage(bdLocation);
                }
                else if(UpdateChangeTime == UpdateNowStateTen){
                    if(point % 6 == 0)
                        UpdateMessage(bdLocation);
                    if(point >1000)
                        point = 0;
                    point++;
                    location.writeJWD(latitude, longitude,this ,last_la,last_lo);
                    last_la = latitude;
                    last_lo = longitude;
                }
            }
            else{
                handleUserLocationInfo();
            }
        }

    }
    public void UpdateMessage(BDLocation bdLocation){
        CherkNetWorkReceiver.sendMessage = 0;
        GetuiSdkMsgReceiver.msg = 0;
        handleUserLocationInfo();
        Intent intent = new Intent(".util.UpdateStateServe");
        Bundle bundle = new Bundle();
        if(bdLocation.getAddrStr()!= null)
            bundle.putString("info", bdLocation.getAddrStr());
        else
            bundle.putString("info", "");
        intent.putExtras(bundle);
        sendBroadcast(intent);
        address = bdLocation.getAddrStr();
    }
    public void handleUserLocationInfo() {
        try {
            if (latitude > 0 && longitude > 0) {
                Map<String, Object> map = new HashMap<>();
                map.put("lat", String.valueOf(latitude));
                map.put("lng", String.valueOf(longitude));
                // map.put("status", String.valueOf(status));
                new NewHttpPostTask(this, new OnHttpRequestListener() {
                    @Override
                    public void onRequestCompleted(Object obj) {
                        Log.e(TAG, "breaf !" + obj.toString());

                    }

                    @Override
                    public void onRequestFailed(Object obj) {
                    }
                }).execute(Constants.USER_LOCATION_URL, map);
            }
        }catch (Exception e ){
            e.printStackTrace();
        }
    }

    public void savrSPloAndla(String address){
        if(latitude != 0 && longitude != 0 && address != null ) {
            SharedPreferences sharedPreferences =
                    getSharedPreferences("SaveLoAndLa", Context.MODE_PRIVATE);
            SharedPreferences.Editor editor = sharedPreferences.edit();
            editor.putString("latitude", latitude + "");
            editor.putString("longitude", longitude + "");
            editor.putString("address", address);
            editor.commit();
        }
    }
    public void changeClientTime(){
        if(client!=null){
            if(client.isStarted()){
                client.stop();
                client.unRegisterLocationListener(this);
            }
        }
        if(UpdateChangeTime == UpdateChangeTimeTen) {
            UpdateChangeTime = UpdateNowStateTen;
            location(10000);
            Log.e(TAG, "Change to 10");
        }
        else if(UpdateChangeTime == UpdateChangeTimeSixty) {
            UpdateChangeTime = UpdateNowStateSixty;
            location(60000);
            Log.e(TAG,"Change to 60");
        }
    }
    private void sendMessage(){
        Intent intent = new Intent();
        intent.setAction("com.mybroadcast.sendmessage");
        Bundle bundle = new Bundle();
        intent.putExtras(bundle);
        sendBroadcast(intent);
    }
    private void cherkGetuiServe(){
        if(!PushManager.getInstance().isPushTurnedOn(SplashActivity.splashActivity)){
//            PushManager.getInstance().initialize(SplashActivity.splashActivity);
            PushManager.getInstance().turnOnPush(SplashActivity.splashActivity);
            Log.e("getui ","======================   getui serve turnon     =====================");
        }
    }
}
