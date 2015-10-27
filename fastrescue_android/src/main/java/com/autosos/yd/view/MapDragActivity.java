package com.autosos.yd.view;

import android.app.Activity;
import android.app.Dialog;
import android.content.Context;
import android.content.Intent;
import android.content.SharedPreferences;
import android.graphics.Color;
import android.graphics.Point;
import android.location.GpsStatus;
import android.location.LocationManager;
import android.net.ConnectivityManager;
import android.net.NetworkInfo;
import android.os.Bundle;
import android.os.Debug;
import android.os.Handler;
import android.os.Message;
import android.util.Log;
import android.view.KeyEvent;
import android.view.View;
import android.view.Window;
import android.view.WindowManager;
import android.widget.Button;
import android.widget.RelativeLayout;
import android.widget.TextView;
import android.widget.Toast;

import com.autosos.yd.Constants;
import com.autosos.yd.task.NewHttpPostTask;
import com.autosos.yd.task.OnHttpRequestListener;
import com.autosos.yd.util.Location;
import com.autosos.yd.util.MusicUtil;
import com.autosos.yd.util.UpdateStateServe;
import com.autosos.yd.widget.CatchException;
import com.autosos.yd.widget.CherkInternet;
import com.autosos.yd.widget.DialogView;
import com.baidu.location.BDLocation;
import com.baidu.location.LocationClientOption;
import com.baidu.mapapi.map.*;
import com.baidu.location.BDLocationListener;
import com.baidu.location.LocationClient;
import com.baidu.mapapi.SDKInitializer;
import com.baidu.mapapi.map.MapView;
import com.baidu.mapapi.model.LatLng;
import com.baidu.mapapi.utils.DistanceUtil;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.Random;
import java.util.Timer;
import java.util.TimerTask;

import com.autosos.yd.R;
import com.autosos.yd.fragment.WorkFragment;
import com.autosos.yd.model.OrderInfo;
import com.autosos.yd.util.JSONUtil;
import com.autosos.yd.view.*;
import com.autosos.yd.view.AutososBackActivity;
import com.autosos.yd.view.DragActivity;

import org.json.JSONException;
import org.json.JSONObject;


public class MapDragActivity extends Activity implements BDLocationListener{
    private boolean ispause;//solve in map-drap while user have a phone-call
    private String trance;
    private Dialog dialog;
    private OrderInfo orderInfo =null;
    public static Location locationdrag;
    public static int isfirst;
    public static int isfirst_drag;
    private MapView mMapView = null;
    private TextView drap_distanceView;
    private double latitude;
    private double longitude;
    private TextView test_distance;
    private double last_latitude = 0.00;
    private double last_longitude = 0.00;
    private double tot_distance;
    private LocationClient client;
    private List<LatLng> pts;
    private PolylineOptions lines;
    private int point=1;
    private static final String TAG = "MapDragActivity";
    private Button btn_drop_arriveView;
    private  double test_point=0.00001;
    private View progressBar;
    private boolean firstLocation = true;
    public Button btn_drop_arrive;

    private Handler handler = new Handler(){
        @Override
        public void handleMessage(Message msg) {
            super.handleMessage(msg);
            switch (msg.what){
                case 1:
                    progressBar.setVisibility(View.INVISIBLE);
                    location();
                    break;
            }
        }
    };

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        SDKInitializer.initialize(getApplicationContext());
        CatchException catchException = CatchException.getInstance();
        catchException.init(getApplicationContext());

        UpdateStateServe.UpdateStateServeActive = true;
        locationdrag = new Location();
        orderInfo = (OrderInfo) getIntent().getSerializableExtra("OrderInfo");
        firstLocation = true; //first location
        setContentView(R.layout.activity_second_drag_map);
       // bdmap_Lout =(RelativeLayout) findViewById(R.id.bmapView);
        mMapView = (MapView)findViewById(R.id.bmapView);
     //   bdmap_Lout.addView(mMapView);
        btn_drop_arrive = (Button) findViewById(R.id.btn_drop_arrive);
        drap_distanceView = (TextView)findViewById(R.id.drap_distance);
        btn_drop_arriveView = (Button)findViewById(R.id.btn_drop_arrive);
//        test_distance = (TextView) findViewById(R.id.test_distance);
        progressBar=findViewById(R.id.drag_map_include);
        isfirst = 0;
        ispause = false;
        tot_distance = 0;
        point = 1;
        SharedPreferences sharedPreferences = this.getSharedPreferences("isfirst", Context.MODE_PRIVATE);
        isfirst_drag = sharedPreferences.getInt("isfirst",0);
        Log.e(TAG, "isfist oncreat :" + isfirst);
       // progressBar.setVisibility(View.VISIBLE);
        if(cherkGPSandNetWork())
            location();
      //  initMap();
        //progressBar.setVisibility(View.GONE);
        MusicUtil.playmusics(MapDragActivity.this, MusicUtil.Start_drag);
    }
    private void initMap(){
        if(longitude == 0 && latitude == 0){
            latitude = UpdateStateServe.latitude;
            longitude = UpdateStateServe.longitude;
        }
        LatLng currentCityLoc = new LatLng(latitude,
                longitude);
        MapStatusUpdate u = MapStatusUpdateFactory
                .newLatLngZoom(currentCityLoc,16);
        mMapView.getMap().animateMapStatus(u);
        LatLng pt = new LatLng(latitude, longitude);
        DotOptions dot=new DotOptions();

        dot.center(currentCityLoc);
        dot.color(getResources().getColor(R.color.color_red));
        dot.radius(15);
        mMapView.getMap().addOverlay(dot);
       // progressBar.setVisibility(View.INVISIBLE);
    }
    private void location() {
            LocationClientOption option = new LocationClientOption();
            option.setOpenGps(true);
            option.setLocationMode(LocationClientOption.LocationMode.Hight_Accuracy);
            option.setCoorType("bd09ll");
            option.setScanSpan(5000);
            option.setProdName("com.autosos.yd");
            option.setIsNeedAddress(true);
            option.setOpenGps(true);
            option.isLocationNotify();
            client = new LocationClient(this);
            client.setLocOption(option);
            client.registerLocationListener(this);
            client.start();
    }
    @Override
    public void onReceiveLocation(BDLocation bdLocation) {
        if (client.isStarted()) {
                Log.e(TAG, "BDLocation  " + bdLocation.toString() + "***********************" + "la:"
                        + bdLocation.getLatitude() + "**log" + bdLocation.getLongitude() + "***point:" + point++);
                if (bdLocation.getLocType() == 61 || bdLocation.getLocType() == 65 || bdLocation.getLocType() == 161) {
                    Log.e(TAG, "1111111111111111111111111111111111");
                    double la = latitude;
                    double lo = longitude;
                    latitude = bdLocation.getLatitude() ;
                    test_point += 0.00005 ;
                    longitude = bdLocation.getLongitude();
                    if (firstLocation) {
                        Log.e(TAG, "222222222222222222222222222222");
                        last_latitude = latitude;
                        last_longitude = longitude;
                        firstLocation = false;
                        initMap();
                        client.stop();
                        client.unRegisterLocationListener(this);
                        progressBar.setVisibility(View.GONE);
                    }else if(point < 5){
                        Log.e(TAG,"point <3 ,dont suan!");
                        last_longitude = lo;
                        last_latitude = la;
                    }else if (cherkGPSandNetWork()) {
                        Log.e(TAG, "3333333333333333333333333333");
                        LatLng lastpt = new LatLng(last_latitude, last_longitude);
                        LatLng nowpt = new LatLng(latitude, longitude);
                        if(DistanceUtil.getDistance(lastpt,nowpt) == -1||DistanceUtil.getDistance(lastpt,nowpt) < 5.00 || DistanceUtil.getDistance(lastpt,nowpt) > 300){
                            Log.e(TAG, DistanceUtil.getDistance(lastpt,nowpt)+"< 5m or > 200m dont utils distance!!!");
                            last_longitude = lo;
                            last_latitude = la;
                        }else {
                                //5 * 12s = 60s update the address
                                if(point % 12 == 0){
                                        if (latitude > 0 && longitude > 0) {
                                            Map<String, Object> map = new HashMap<>();
                                            map.put("lat", String.valueOf(latitude));
                                            map.put("lng", String.valueOf(longitude));
                                            // map.put("status", String.valueOf(status));
                                            new NewHttpPostTask(this, new OnHttpRequestListener() {
                                                @Override
                                                public void onRequestCompleted(Object obj) {
                                                    Log.e(TAG,"pu tong !");
                                                }

                                                @Override
                                                public void onRequestFailed(Object obj) {
                                                }
                                            }).execute(Constants.USER_LOCATION_URL, map);
                                        }
                            }
                            UpdateStateServe.latitude = latitude;
                            UpdateStateServe.longitude = longitude;
                            locationdrag.writeJWD(latitude,longitude,Location.path_drag,MapDragActivity.this);
                            PaintRoute();
                        }
                    }else{

                    }
                }
        }
    }
    private boolean cherkGPSandNetWork(){
        boolean GPS_status = ((LocationManager) getSystemService(Context.LOCATION_SERVICE)).isProviderEnabled(LocationManager.GPS_PROVIDER);

        if(CherkInternet.cherkInternet(this) == false){
            Toast.makeText(com.autosos.yd.view.MapDragActivity.this,String.format(getString(R.string.msg_noInterner)),Toast.LENGTH_SHORT).show();
            return false;
        }
       else if(GPS_status == false){
            Toast.makeText(com.autosos.yd.view.MapDragActivity.this,String.format(getString(R.string.msg_noGPS)),Toast.LENGTH_SHORT).show();
            return true;
        }
        return true;
    }
    //绘制两点间的直线
    private void PaintRoute(){
        LatLng lastpt=new LatLng(last_latitude,last_longitude);
        LatLng nowpt=new LatLng(latitude,longitude);

        //focas move here
        LatLng currentCityLoc = new LatLng(latitude,
                longitude);
        MapStatusUpdate u = MapStatusUpdateFactory
                .newLatLngZoom(currentCityLoc,16);
        mMapView.getMap().animateMapStatus(u);

        pts=new ArrayList<LatLng>();
        pts.add(lastpt);
        pts.add(nowpt);
        PolylineOptions plts=new PolylineOptions();
        plts.points(pts).color(getResources().getColor(R.color.color_orange)).width(15);
        mMapView.getMap().addOverlay(plts);
        double mile=DistanceUtil.getDistance(lastpt, nowpt);
        tot_distance+=mile;
        Log.e(TAG, "total distance: " + tot_distance);
        SharedPreferences sharedPreferences = this.getSharedPreferences("Location", Context.MODE_PRIVATE);
        SharedPreferences.Editor editor = sharedPreferences.edit();
        editor.putFloat("Location_drag_distance", (float) tot_distance);
        editor.commit();
        drap_distanceView.setText(String.format("%.2f", tot_distance / 1000));
        pts.clear();
        last_longitude=longitude;
        last_latitude=latitude;

    }

    private Double getRealDistance(){
        String trance2 = locationdrag.getTrance(Location.path_drag, MapDragActivity.this);
        String [] ss = trance2.split("\\|");
        String [] ss2 = new String[ss.length];
        String [] time = new String[ss.length];
        String [] las = new String[ss.length];
        String [] los = new String[ss.length];
        for (int i = 0;i<ss.length;i++){
            String[] x = ss[i].split("#");
            ss2[i] = x[0];
            time[i] = x[1];
        }
        for (int i = 0;i<ss2.length;i++){
            String [] x = ss2[i].split(",");
            las [i] = x[0];
            los [i] = x[1];
        }
        double lal = Double.parseDouble(las[0]);
        double lol = Double.parseDouble(los[0]);
        Double amendment_ditance=null;
        Double sum_amendment_ditance = 0.0000001;
        for(int i = 1; i < las.length; i++) {
            LatLng lastpt = new LatLng(lal, lol);
            LatLng nowpt = new LatLng(Double.parseDouble(las[i]), Double.parseDouble(los[i]));
            amendment_ditance = DistanceUtil.getDistance(lastpt, nowpt);
            if (amendment_ditance < 5000 && amendment_ditance > 5) {
                sum_amendment_ditance += amendment_ditance;
                Log.e("mapd","sum_amendment_ditance === "+sum_amendment_ditance );
            }
            lal = Double.parseDouble(las[i]);
            lol = Double.parseDouble(los[i]);
        }

        return sum_amendment_ditance;

    }

    private void rePaintRoute(String [] las,String [] los ,String[] times){
        if(las.length < 2){
            return;
        }
        double lal = Double.parseDouble(las[0]);
        double lol = Double.parseDouble(los[0]);
        Double amendment_ditance=null;
        Double sum_amendment_ditance = 0.0000001;
        for(int i = 1; i < las.length; i++){
            LatLng lastpt=new LatLng(lal,lol);
            LatLng nowpt = new LatLng(Double.parseDouble(las[i]),Double.parseDouble(los[i]));
            amendment_ditance = DistanceUtil.getDistance(lastpt, nowpt);
            if (amendment_ditance < 5000 && amendment_ditance >5 ){
                sum_amendment_ditance += amendment_ditance;
            }

            Log.e(TAG, "amendment_ditance  =====  " + amendment_ditance);

            pts=new ArrayList<LatLng>();
            pts.add(lastpt);
            pts.add(nowpt);
            PolylineOptions plts=new PolylineOptions();
            plts.points(pts).color(getResources().getColor(R.color.color_gray)).width(15);
            mMapView.getMap().addOverlay(plts);
            pts.clear();
            lal = Double.parseDouble(las[i]);
            lol = Double.parseDouble(los[i]);
        }
        Log.e("test","tot_distance === "+tot_distance);
        tot_distance = sum_amendment_ditance;
        Log.e("test","sum_amendment_ditance === "+sum_amendment_ditance);
        drap_distanceView.setText(String.format("%.2f", sum_amendment_ditance / 1000));


        Log.e(TAG, "amendment_ditance  =====  "+amendment_ditance);
       // progressBar.setVisibility(View.GONE);
        //TEST from gongsi
        //TEST from bijiben2
        ((TextView)findViewById(R.id.repain)).setVisibility(View.GONE);
    }


    public void startDrag(View v){
        Log.e("tuoche","=======map======="+isfirst );
        if (isfirst == 0) {
            if(cherkGPSandNetWork()) {
                final View v2 = v;
                Map<String, Object> map = new HashMap<>();
                map.put("lat", UpdateStateServe.latitude);
                map.put("lng", UpdateStateServe.longitude);
                new com.autosos.yd.task.NewHttpPutTask(MapDragActivity.this, new com.autosos.yd.task.OnHttpRequestListener() {
                    @Override
                    public void onRequestCompleted(Object obj) {
                        progressBar.setVisibility(View.GONE);
                        try {
                            JSONObject jsonObject = new JSONObject(obj.toString());
                            Log.e(TAG,obj.toString());
                            if (!jsonObject.isNull("result")) {
                                int result = jsonObject.optInt("result");
                                if (result == 1) {
                                    UpdateStateServe.UpdateStateServeActive = false;
                                    ((Button)v2).setTextColor(getResources().getColor(R.color.color_black));
                                    ((Button)v2).setBackgroundResource(R.drawable.bg_second_btn_white2);
                                    ((Button) v2).setText(String.format(getString(R.string.label_carDrag_stop)));
                                    isfirst = 1;
                                    SharedPreferences sharedPreferences = getSharedPreferences("isfirst", Context.MODE_PRIVATE);
                                    SharedPreferences.Editor editor = sharedPreferences.edit();
                                    editor.putInt("isfirst", isfirst);
                                    editor.commit();
                                    isfirst_drag = sharedPreferences.getInt("isfirst",0);
                                    Log.e(TAG, "after start tuoche" + isfirst);
                                    progressBar.setVisibility(View.VISIBLE);
                                    TimerTask task = new TimerTask() {
                                        public void run() {
                                            Message msg = new Message();
                                            msg.what = 1;
                                            handler.sendMessage(msg);
                                        }
                                    };
                                    Timer timer = new Timer();
                                    timer.schedule(task, 2000);
                                } else {
                                    if (!jsonObject.isNull("msg")) {
                                        String msg = jsonObject.optString("msg");
                                        Toast.makeText(MapDragActivity.this, msg, Toast.LENGTH_SHORT).show();
                                    }
                                }
                            }

                        } catch (JSONException e) {
                        }
                    }

                    @Override
                    public void onRequestFailed(Object obj) {

                    }
                }).execute(String.format(com.autosos.yd.Constants.DRAG_START, orderInfo.getId()), map);
            }
        } else {

            UpdateStateServe.UpdateStateServeActive = true;
            View v2 = getLayoutInflater().inflate(R.layout.dialog_msg_notice,
                    null);
            dialog = new Dialog(v2.getContext(), R.style.bubble_dialog);
            Button tvConfirm = (Button) v2.findViewById(R.id.btn_notice_confirm);
            Button tvCancel = (Button) v2.findViewById(R.id.btn_notice_cancel);
            TextView tvMsg = (TextView) v2.findViewById(R.id.tv_notice_msg);
            tvMsg.setText(String.format(getString(R.string.msg_ok_tuoche)));
//            Double test = getRealDistance();
//            Log.e("mapdra", "zui zhong jie guo === " + test);
            tvConfirm.setOnClickListener(new View.OnClickListener() {

                @Override
                public void onClick(View v) {
                    dialog.dismiss();
//                    if(Constants.DEBUG)
//                        tot_distance = 95300;
                    if(tot_distance == 0){
                        DialogView dialogView =new DialogView();
                        dialogView.dialog(R.string.msg_type_distance,R.string.label_ok2,MapDragActivity.this);
                        }
                    else {
//                        tot_distance = getRealDistance();
                        Log.e("mapdra","zui zhong jie guo === " + tot_distance);
                        SharedPreferences sharedPreferences = getSharedPreferences("isfirst", Context.MODE_PRIVATE);
                        SharedPreferences.Editor editor = sharedPreferences.edit();
                        editor.clear();
                        editor.commit();
                        client.unRegisterLocationListener(com.autosos.yd.view.MapDragActivity.this);
                        client.stop();
                        trance = locationdrag.getTrance(Location.path_drag, MapDragActivity.this);
//                        if(Constants.DEBUG) {
//                            trance = locationdrag.cutErrorPoint2(trance);
//                            trance = locationdrag.cutErrorPoint(trance);
//                        }
                        //trance = changeTrance(trance);
                        Log.e(TAG, "trance    :" + trance.toString());
                        //locationdrag.deleteJWD(Location.path_drag);
                        Intent intent = new Intent(com.autosos.yd.view.MapDragActivity.this, DragActivity.class);
                        intent.putExtra("OrderInfo", orderInfo);
                        intent.putExtra("tot_distance", tot_distance);
                        //intent.putExtra("tot_distance", dis);
                        Log.e(TAG, "totDistance:" + tot_distance);
                        intent.putExtra("trance", trance);
                        startActivity(intent);
                        overridePendingTransition(R.anim.slide_in_up, R.anim.activity_anim_default);
                        finish();
                    }
                }
            });
            tvCancel.setOnClickListener(new View.OnClickListener() {

                @Override
                public void onClick(View v) {
                    dialog.dismiss();
                }
            });

            dialog.setContentView(v2);
            dialog.setCanceledOnTouchOutside(false);
            Window window = dialog.getWindow();
            WindowManager.LayoutParams params = window.getAttributes();
            Point point = JSONUtil.getDeviceSize(v.getContext());
            params.width = Math.round(point.x * 5 / 7);
            window.setAttributes(params);
            dialog.show();
        }
    }
    @Override
    protected void onResume() {

        super.onResume();

        SharedPreferences sharedPreferences = this.getSharedPreferences("Location", Context.MODE_PRIVATE);
        float dis = sharedPreferences.getFloat("Location_drag_distance", 0);
        SharedPreferences sharedPreferences1 = this.getSharedPreferences("isfirst", Context.MODE_PRIVATE);
        isfirst_drag = sharedPreferences1.getInt("isfirst",0);
        tot_distance = dis;

        Log.e(TAG, "isfist onresume :" + isfirst);
     //   if(Constants.DEBUG)
       //     tot_distance = 99999;
//        drap_distanceView.setText(String.format("%.2f", tot_distance / 1000));
        Log.e(TAG, "Now TOTdistance :" + dis);
            if(tot_distance > 0){
                ((TextView)findViewById(R.id.repain)).setVisibility(View.VISIBLE);

                trance = locationdrag.getTrance(Location.path_drag, MapDragActivity.this);
                String [] ss = trance.split("\\|");
                String [] ss2 = new String[ss.length];
                String [] time = new String[ss.length];
                String [] las = new String[ss.length];
                String [] los = new String[ss.length];
                for (int i = 0;i<ss.length;i++){
                    String[] x = ss[i].split("#");
                    ss2[i] = x[0];
                    time[i] = x[1];
            }
            for (int i = 0;i<ss2.length;i++){
                String [] x = ss2[i].split(",");
                las [i] = x[0];
                los [i] = x[1];
            }
            rePaintRoute(las,los,time);
        }
        else{
            locationdrag.deleteJWD(Location.path_drag,MapDragActivity.this);
            progressBar.setVisibility(View.GONE);
        }
        if(isfirst == 0) {
            btn_drop_arriveView.setText(String.format(getString(R.string.label_carDrag_start)));
        }
       if(!client.isStarted()){
           client.start();
        }
       //mMapView.onResume();
    }

    @Override
    protected void onDestroy() {
        super.onDestroy();
        ispause = false;
        Log.e(TAG,"xiaohui le le le ");
        UpdateStateServe.UpdateStateServeActive = true;
        if(client!=null && client.isStarted()){
            client.stop();client.unRegisterLocationListener(this);
        }
       // mMapView.onDestroy();
    }

    @Override
    protected void onPause() {
        super.onPause();
        //client.stop();
      //  client.unRegisterLocationListener(this);
       // ispause = true;
        Log.e(TAG, "pause le le le ");
        //mMapView.onPause();
    }
    @Override
    public void onBackPressed() {
        /*
        super.onBackPressed();
        if (client.isStarted()) {
                client.stop();
                client.unRegisterLocationListener(this);
        }
        overridePendingTransition(0, R.anim.slide_out_right);
        */
    }
    public boolean onKeyDown(int keyCode, KeyEvent event)
    {
        if(keyCode==KeyEvent.KEYCODE_BACK)
        {
            return true;
        }
        return false;
    }
    private String changeTrance(String trance){
        String tra = "";
        String [] ss = trance.split("\n");
        if(ss.length < 2){
            return latitude+","+longitude+"|";
        }
        for (int i = 0; i < ss.length; i+=2) {
            long time = System.currentTimeMillis() / 1000;
            tra = tra + ss[i] +"," +ss[i+1] + "#" + time +"|";
        }
        return tra;
    }
}
