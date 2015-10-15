package com.autosos.yd.util;

import android.content.Context;
import android.media.AudioManager;
import android.media.MediaPlayer;
import android.media.SoundPool;
import android.os.Handler;
import android.util.Log;
import android.widget.TextView;

import com.autosos.yd.R;
import com.baidu.mapapi.model.LatLng;
import com.baidu.mapapi.search.route.DrivingRouteResult;
import com.baidu.mapapi.search.route.OnGetRoutePlanResultListener;
import com.baidu.mapapi.search.route.PlanNode;
import com.baidu.mapapi.search.route.RoutePlanSearch;
import com.baidu.mapapi.search.route.TransitRoutePlanOption;
import com.baidu.mapapi.search.route.TransitRouteResult;
import com.baidu.mapapi.search.route.WalkingRouteResult;

import org.json.JSONObject;

/**
 * Created by Administrator on 2015/8/26.
 */
public class MusicUtil implements MediaPlayer.OnCompletionListener {
    private double lng,lat;
    private double distance;
    RoutePlanSearch routePlanSearch;
    private Context mycontext;
    private int type;
    private boolean is_order= false;
    private int[] ids;
    private int length;
    private final String TAG = "MusicUtil";
    private int i;
    public final int[] music2 = new int []{R.raw.zero,R.raw.one,R.raw.two,R.raw.three,R.raw.fore,R.raw.five
            ,R.raw.six,R.raw.seven,R.raw.eight,R.raw.nine,R.raw.ten,R.raw.point,R.raw.distance,R.raw.km,R.raw.alarm
    ,R.raw.dadian,R.raw.huantai,R.raw.tuoche,R.raw.order_new,R.raw.order,R.raw.orderorder,R.raw.realtimeorder};
    public final int[] music1 = new int[]{R.raw.dadian,R.raw.huantai,R.raw.tuoche,R.raw.order_new,R.raw.order};
    public void musicPlayer(String data,Context context){
        mycontext = context;
        //设置音量为多媒体
        //AudioManager am = (AudioManager)mycontext.getSystemService(Context.AUDIO_SERVICE);
        //am.setStreamVolume(AudioManager.STREAM_MUSIC,am.getStreamVolume(AudioManager.STREAM_MUSIC), AudioManager.FLAG_PLAY_SOUND);
        i= 0 ;
        try {
            JSONObject jsonObject1 = new JSONObject(data);
            JSONObject jsonObject = jsonObject1.optJSONObject("data");
            lat = jsonObject.optDouble("latitude");
            long x = jsonObject.optLong("order_type");
            if(x==2)
                is_order = true;
            else
                is_order=false;
            lng = jsonObject.optDouble("longitude");
            type = jsonObject.optInt("service_type");
            Log.e(TAG,"xxxxxxxxxxxxxxxlat"+lat+"      lng"+lng+"     type"+type);
            getDistance();
        }catch (Exception e){
            e.printStackTrace();
            Log.e(TAG,e.toString());
        }
    }


    public void getDistance(){
        distance = -1;
        routePlanSearch = RoutePlanSearch.newInstance();
        routePlanSearch.setOnGetRoutePlanResultListener(new OnGetRoutePlanResultListener() {
            @Override
            public void onGetWalkingRouteResult(WalkingRouteResult walkingRouteResult) {

            }

            @Override
            public void onGetTransitRouteResult(TransitRouteResult transitRouteResult) {
                if(transitRouteResult != null && transitRouteResult.getTaxiInfo() !=null){
                    distance = transitRouteResult.getTaxiInfo().getDistance();
                    distance = Utils.getPoint2Double(distance / 1000) ;
                    if(distance == 0){
                        distance = Utils.getPoint1Double(Utils.getGeoDistance(UpdateStateServe.longitude, UpdateStateServe.latitude
                                , lng, lat) / 1000);
                    }
                    double distance2 = Utils.getGeoDistance(UpdateStateServe.longitude, UpdateStateServe.latitude
                            , lng, lat)/1000;
                    Log.e(TAG,distance + "--"+distance2);
                    if(distance2 > distance){
                        distance = distance2 * 1.2;
                    }
                    Log.e(TAG, "zui you ju li :" + distance);
                    routePlanSearch.destroy();
                }
                else{
                    Log.e(TAG,"shibai le !");
                    distance = Utils.getPoint1Double(Utils.getGeoDistance(UpdateStateServe.longitude, UpdateStateServe.latitude
                            , lng, lat) / 1000 * 1.2);
                    routePlanSearch.destroy();
                }
                Log.e(TAG,distance+"");
                change(distance);
            }

            @Override
            public void onGetDrivingRouteResult(DrivingRouteResult drivingRouteResult) {

            }
        });
        LatLng startLatLng = new LatLng(UpdateStateServe.latitude,UpdateStateServe.longitude);
        LatLng endLatLng = new LatLng(lat, lng);
        PlanNode stNode = PlanNode.withLocation(startLatLng);
        PlanNode enNode = PlanNode.withLocation(endLatLng);
        routePlanSearch.transitSearch((new TransitRoutePlanOption())
                .from(stNode)
                .city(mycontext.getResources().getString(R.string.label_city))
                .to(enNode));
    }
    private void change(double distance){
        ids = new int[13];
        int dis = (int ) (distance * 10);
        if(dis > 1000){
            return;
        }
        Log.e(TAG,"2222");
        int a = dis % 10 ;dis /= 10;
        int b = dis % 10 ;dis /= 10;
        int c = dis % 10;
        if(c != 0){
            ids[4] = 12;
            ids[5] = c;
            ids[6] = 10;
            if(b != 0){
                ids[7] = b;
                ids[8] = 11;
                ids[9] = a;
                ids[10] = 13;
                length = 11;
            }
            else{
                ids [7] = 11;
                ids [8] = a;
                ids [9] = 13;
                length = 10;
            }
        }
        else{
                ids[4] = 12;
                ids[5] = b;
                ids[6] = 11;
                ids[7] = a;
                ids[8] = 13;
                length = 9;
        }
        if(is_order)
            ids [0] = 20;
        else
            ids [0] = 21;
        ids [2] = type -1 + 15;
        ids [3] = 19;
        ids [1] = 18;
        playmusic2();
    }
    private void playmusic(){
        Log.e(TAG,"111");
        MediaPlayer mp;
        mp = MediaPlayer.create(mycontext, music1[3]);
        mp.start();
        mp.setOnCompletionListener(this);
    }
    @Override
    public void onCompletion(MediaPlayer mp) {
        mp.stop();
        mp.release();
        if(i<length){
            Log.e(TAG,"--------------");
            try{
                mp.prepare();
            }catch (Exception e ){
                e.printStackTrace();
            }
            MediaPlayer   mp2 = MediaPlayer.create(mycontext, music2[ids[i]]);
            mp2.start();
            mp2.setOnCompletionListener(this);
        }
       /* else if(i == length){
        }
        */
        else{
            GetuiSdkMsgReceiver.playMusic = false;
        }
        i++;
    }
    SoundPool soundPool;
    int [] soundid = new int[12];
    int j , x;
    public void playmusic2(){
        soundPool = new SoundPool(1, AudioManager.STREAM_MUSIC, 0);
        x = 0;
        for( j =0;j<length;j++) {
            int delay = j * 1000;
            if(j == 2)
                delay += 600;
            if(j == 3 || j== 4 ||j==5)
                delay += 600;
            if(j > 5)
                delay += 600;
            new Handler().postDelayed(new Runnable() {
                public void run() {
                    soundid[j] = soundPool.load(mycontext, music2[ids[x]], x);
                    soundPool.setOnLoadCompleteListener(new SoundPool.OnLoadCompleteListener() {
                        @Override
                        public void onLoadComplete(SoundPool soundPool, int sampleId, int status) {
                            soundPool.play(sampleId, 1, 1, 0, 0, 1);
                        }
                    });
                    x++;
                    if(x >= length -2){
                        GetuiSdkMsgReceiver.playMusic = false;
                    }
                }
            }, delay);
        }
    }
    public static int[] musics = new int[]{
            R.raw.after_help_pay,R.raw.arive,R.raw.start_drag,R.raw.take_three_photo,R.raw.take_photo_send
            ,R.raw.click_bottom,R.raw.call,R.raw.get_cash
    };
    public static final int After_help_pay = 1;
    public static final int Arive = 2;
    public static final int Start_drag = 3;
    public static final int Take_three_photo = 4;
    public static final int Take_photo_send = 5;
    public static final int Click_bottom = 6;
    public static final int Call = 7;
    public static final int Get_cash = 8;
    public static void playmusics(Context context ,int type){
        if(UpdateStateServe.Setting_Sound_OnOff){
          // SoundPool soundPool = new SoundPool(1, AudioManager.STREAM_MUSIC, 0);
          //  soundPool.load(context, musics[type-1], 1);
          //  soundPool.setOnLoadCompleteListener(new SoundPool.OnLoadCompleteListener() {
           //     @Override
           //     public void onLoadComplete(SoundPool soundPool, int sampleId, int status) {
         //           soundPool.play(sampleId, 1, 1, 0, 0, 1);
          //      }
           // });
            MediaPlayer   mp2 = MediaPlayer.create(context, musics[type -1 ]);
            mp2.start();
        }
    }
}
