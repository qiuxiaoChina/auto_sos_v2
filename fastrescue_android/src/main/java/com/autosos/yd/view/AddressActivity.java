package com.autosos.yd.view;

import android.app.Activity;
import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.Intent;
import android.content.IntentFilter;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.graphics.Canvas;
import android.graphics.Paint;
import android.net.Uri;
import android.os.Bundle;
import android.os.Handler;
import android.os.Message;
import android.util.Log;
import android.view.View;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.TextView;

import com.autosos.yd.Constants;
import com.autosos.yd.R;
import com.autosos.yd.model.RecordInfo;
import com.autosos.yd.task.AuthGetJSONObjectAsyncTask;
import com.autosos.yd.util.JSONUtil;
import com.autosos.yd.util.MyUtils;
import com.autosos.yd.util.UpdateStateServe;
import com.baidu.mapapi.SDKInitializer;
import com.baidu.mapapi.map.BitmapDescriptor;
import com.baidu.mapapi.map.BitmapDescriptorFactory;
import com.baidu.mapapi.map.MapStatusUpdate;
import com.baidu.mapapi.map.MapStatusUpdateFactory;
import com.baidu.mapapi.map.MapView;
import com.baidu.mapapi.map.MarkerOptions;
import com.baidu.mapapi.map.OverlayOptions;
import com.baidu.mapapi.model.LatLng;

import org.json.JSONException;
import org.json.JSONObject;

import java.io.IOException;
import java.text.SimpleDateFormat;
import java.util.Date;

/**
 * Created by Administrator on 2015/8/18.
 */
public class AddressActivity extends Activity {
    private final String TAG = "AddressActivity";
    private String image_path;
    private MapView  mMapView;
    private TextView addressView;
    private MyReceiver receiver;
    private String name;
    private class MyReceiver extends BroadcastReceiver {

        @Override
        public void onReceive(Context context, Intent intent) {
            Log.e(TAG,"-----------------------");
            try {
                Bundle bundle = intent.getExtras();
                String info = bundle.getString("info",null);
                if (info != null &&info.length() > 5) {
                    addressView.setText(String.format(getString(R.string.label_current_place),
                            info) + "   ");
                    mMapView.getMap().clear();
                    paint(bm);
                }
            }catch (Exception e){
                e.printStackTrace();
            }
        }
    }

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        SDKInitializer.initialize(getApplicationContext());
        super.onCreate(savedInstanceState);
        setContentView(R.layout.my_address);
        findViewById(R.id.back2).setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                finished(v);
            }
        });
        addressView = (TextView)findViewById(R.id.address);
        image_path = getIntent().getStringExtra("image_path");
        name = getIntent().getStringExtra("name");
        mMapView = (MapView)findViewById(R.id.bmapView);
        LatLng currentCityLoc = new LatLng(UpdateStateServe.latitude,
                UpdateStateServe.longitude);
        MapStatusUpdate u = MapStatusUpdateFactory
                .newLatLngZoom(currentCityLoc, 16);
        mMapView.getMap().animateMapStatus(u);
        addressView.setText(String.format(getString(R.string.label_current_place),
                UpdateStateServe.address) + "   ");
        new Thread(new Runnable() {
            @Override
            public void run() {
                MyUtils.getNetImageBytes(image_path, handler);
            }
        }).start();
        receiver = new MyReceiver();
        IntentFilter filter = new IntentFilter();
        filter.addAction(".util.UpdateStateServe");
        registerReceiver(receiver, filter);
    }


    private Bitmap bm = null;
   private Handler handler = new Handler(){
        @Override
        public void handleMessage(Message msg) {
            super.handleMessage(msg);
            switch (msg.what){
                case 1:
                    byte[] data =(byte []) msg.obj;
                    int length = data.length;
                    bm = BitmapFactory.decodeByteArray(data, 0, length).copy(Bitmap.Config.ARGB_8888, true);
                    paint(bm);
                    break;
            }
        }
    };
    public void paint(Bitmap bitMap){
        LatLng point = new LatLng(UpdateStateServe.latitude, UpdateStateServe.longitude);
        Paint p = new Paint();
        p.setAntiAlias(true); //去锯齿
        p.setColor(getResources().getColor(R.color.color_red));
        p.setStyle(Paint.Style.STROKE);
        Canvas canvas = new Canvas(bitMap);
        p.setStyle(Paint.Style.FILL);
        canvas.drawCircle(bitMap.getWidth()/2, bitMap.getHeight() - 10,10, p);// 小圆


        BitmapDescriptor bitmap = BitmapDescriptorFactory
                .fromBitmap(bitMap);
        OverlayOptions option = new MarkerOptions()
                .position(point)
                .title(name)
                .icon(bitmap);
        mMapView.getMap().addOverlay(option);
    }
    public void finished(View v){
        finish();
        overridePendingTransition(0, R.anim.slide_out_right);
    }
    @Override
    public void onBackPressed() {
    }

    @Override
    protected void onDestroy() {
        try{
            unregisterReceiver(receiver);
        }catch (Exception e){
            e.printStackTrace();
        }
        super.onDestroy();
    }
}
