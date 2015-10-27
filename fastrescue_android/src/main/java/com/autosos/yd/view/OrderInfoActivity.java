package com.autosos.yd.view;

import android.app.Dialog;
import android.content.Context;
import android.content.Intent;
import android.content.SharedPreferences;
import android.graphics.Point;
import android.media.SoundPool;
import android.net.Uri;
import android.os.AsyncTask;
import android.os.Bundle;
import android.os.Handler;
import android.os.Message;
import android.util.Log;
import android.view.View;
import android.view.Window;
import android.view.WindowManager;
import android.view.animation.Animation;
import android.view.animation.AnimationUtils;
import android.view.animation.RotateAnimation;
import android.widget.Button;
import android.widget.ImageView;
import android.widget.TextView;
import android.widget.Toast;

import com.autosos.yd.model.Image;
import com.autosos.yd.task.NewHttpPostTask;
import com.autosos.yd.util.DistanceUtil;
import com.autosos.yd.util.GetuiSdkMsgReceiver;
import com.autosos.yd.util.Location;
import com.autosos.yd.util.MusicUtil;
import com.autosos.yd.util.MyUtils;
import com.autosos.yd.util.UpdateStateServe;
import com.autosos.yd.widget.CherkInternet;
import com.autosos.yd.widget.DialogView;
import com.baidu.location.BDLocation;
import com.baidu.location.BDLocationListener;
import com.baidu.location.LocationClient;
import com.baidu.location.LocationClientOption;
import com.baidu.mapapi.SDKInitializer;
import com.umeng.analytics.MobclickAgent;

import org.json.JSONException;
import org.json.JSONObject;

import java.io.IOException;
import java.util.HashMap;
import java.util.Map;
import java.util.Timer;
import java.util.TimerTask;

import com.autosos.yd.Constants;
import com.autosos.yd.R;
import com.autosos.yd.fragment.WorkFragment;
import com.autosos.yd.model.OrderInfo;
import com.autosos.yd.task.NewHttpPutTask;
import com.autosos.yd.task.OnHttpRequestListener;
import com.autosos.yd.util.JSONUtil;
import com.autosos.yd.util.Utils;
import com.autosos.yd.view.*;
import com.autosos.yd.view.AutososBackActivity;
import com.autosos.yd.view.DragActivity;
import com.autosos.yd.view.MainActivity;
import com.autosos.yd.view.PayActivity;
import com.autosos.yd.view.PayInfoActivity;
import com.autosos.yd.view.UploadPhotoActivity;

public class OrderInfoActivity extends AutososBackActivity{

    private static final String TAG = "OrderInfoActivity";
    public int point;
    private boolean palyonce = false;
    private View progressBar;
    private View empty;
    private TextView nameView;
    private TextView phoneView;
    private TextView carNumberView;
    private TextView typeView;
    private ImageView typeImgView;
    private TextView addressView;
    private TextView distanceView;
    private ImageView telphoneView;
    private TextView remarkView;
    private OrderInfo orderInfo;
    private long id;
    private Button dragBtn;
    private Button payBtn;
    private Button arriveBtn;
    private Dialog dialog;
    private ImageView qr_codeView;
    private LocationClient client;
    public  double latitude;
    public  double longitude;
    private TextView order_orderView;
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        SDKInitializer.initialize(getApplicationContext());
        setContentView(R.layout.activity_order_info);
        client = null;point = 0;
        location = new Location();
        dragBtn = (Button) findViewById(R.id.btn_drag);
        payBtn = (Button) findViewById(R.id.btn_pay);
        arriveBtn = (Button) findViewById(R.id.btn_arrive);
        id = getIntent().getLongExtra("id", 0);
        progressBar = findViewById(R.id.progressBar);
        qr_codeView =(ImageView) findViewById(R.id.qr_code);
        empty = findViewById(R.id.empty);
        telphoneView = (ImageView)findViewById(R.id.telephone);
        nameView = (TextView) findViewById(R.id.name);
//        phoneView = (TextView) findViewById(R.id.phone);
        carNumberView = (TextView) findViewById(R.id.carNumber);
        typeView = (TextView) findViewById(R.id.type);
        typeImgView = (ImageView) findViewById(R.id.type_img);
        addressView = (TextView) findViewById(R.id.address);
        distanceView = (TextView) findViewById(R.id.distance);
        remarkView = (TextView) findViewById(R.id.remark);
        order_orderView = (TextView)findViewById(R.id.order_order);
        TimerTask task = new TimerTask() {
            public void run() {
                Message msg = new Message();
                msg.what = 1;
                handler.sendMessage(msg);
            }
        };
        Timer timer = new Timer();
        timer.schedule(task, 1000);
    }

    private Handler handler = new Handler(){
        @Override
        public void handleMessage(Message msg) {
            super.handleMessage(msg);
            switch (msg.what){
                case 1:
//                    Animation shake = AnimationUtils.loadAnimation(OrderInfoActivity.this, R.anim.view_shake2);
//                    telphoneView.startAnimation(shake);
                    RotateAnimation animation = new RotateAnimation(360, 0, telphoneView.getWidth()/2, telphoneView.getHeight()/2);
                    animation.setDuration(1000);
                    animation.setFillAfter(true);
                    animation.setRepeatCount(10);
                    animation.setStartOffset(1000);

                    telphoneView.startAnimation(animation);
                    break;
            }
        }
    };
    @Override
    protected void onNewIntent(Intent intent) {
        OrderInfo mOrderInfo = (OrderInfo) intent.getSerializableExtra("OrderInfo");
        if (mOrderInfo != null) {
            orderInfo = mOrderInfo;
        }
        super.onNewIntent(intent);
    }

    private class GetOrderInfoTask extends AsyncTask<String, Object, JSONObject> {

        @Override
        protected JSONObject doInBackground(String... params) {
            try {
                String jsonStr = JSONUtil.getStringFromUrl(com.autosos.yd.view.OrderInfoActivity.this,params[0]);
                if (JSONUtil.isEmpty(jsonStr)) {
                    return null;
                }
                Log.e(TAG,"11"+ jsonStr);
                return new JSONObject(jsonStr);
            } catch (IOException | JSONException e) {
            }

            return null;
        }

        @Override
        protected void onPostExecute(JSONObject result) {
            Log.e("json","result ===== "+result);
            progressBar.setVisibility(View.GONE);
            orderInfo = new OrderInfo(result);
            Log.e("json","orderInfo ===== "+orderInfo.getId());
            SharedPreferences sharedPreferences = getSharedPreferences("isfirst", Context.MODE_PRIVATE);
            int isfirst_drag = sharedPreferences.getInt("isfirst", 0);
            Log.e("tuoch","===orderinfo fist coming==="+isfirst_drag);
            if (orderInfo != null) {

//                if (orderInfo.getService_type() == 3 ){
//                    if (orderInfo.getTuoche_distance() > 0){
//
//                    }else {
//                        if (isfirst_drag == 1){
//                            Intent intent;
//                            intent = new Intent();
//                            intent.setClass(OrderInfoActivity.this, MapDragActivity.class);
//                            intent.putExtra("OrderInfo", orderInfo);
//                            startActivity(intent);
//                        }
//
//
//                    }
//                }

                if(orderInfo.getStatus() == 403) {
                    showCancelView();
                    return;
                }
//                phoneView.setText(String.format(getString(R.string.label_phone_number), orderInfo.getOwner_mobile()));
                carNumberView.setText(orderInfo.getCar_number());
                if(orderInfo.getOwner_realname() != null && orderInfo.getOwner_realname().length()>0){
                    carNumberView.setText(orderInfo.getOwner_realname());
                    nameView.setVisibility(View.VISIBLE);
                    nameView.setText(orderInfo.getCar_number());
                }
                if(orderInfo.getRemark()!=null && orderInfo.getRemark().length() > 2){
                    remarkView.setVisibility(View.VISIBLE);
                    remarkView.setText(getResources().getString(R.string.label_else)+orderInfo.getRemark());
                }
                else{
                    remarkView.setVisibility(View.GONE);
                }
                if (orderInfo.getService_type() == 1) {
                    typeView.setText(R.string.label_service_type1);
                    typeImgView.setImageResource(R.drawable.icon_service_type1);
//                    typeImgView.setBackgroundResource(R.drawable.bg_oval_plue);
                    setBackground(R.color.color_purple);
                    setLineGone();
                    setArrow(R.drawable.icon48);
                    setTitleLabelColor(R.color.color_white);

                    findViewById(R.id.user_info_layout).setBackgroundResource(R.color.color_purple);
                    ((ImageView)findViewById(R.id.telephone)).setImageResource(R.drawable.icon_telephont_plue);
                    findViewById(R.id.type_icon).setBackgroundResource(R.drawable.bg_oval_plue);
                    findViewById(R.id.btn_arrive).setBackgroundResource(R.drawable.bg_btn_purple);
                    findViewById(R.id.btn_drag).setBackgroundResource(R.drawable.bg_btn_plue);
                    findViewById(R.id.btn_pay).setBackgroundResource(R.drawable.bg_btn_plue);
                    findViewById(R.id.btn_photo).setBackgroundResource(R.drawable.bg_btn_plue);
                    ((TextView)findViewById(R.id.btn_drag)).setTextColor(getResources().getColor(R.color.color_white));
                    ((Button)findViewById(R.id.btn_photo)).setTextColor(getResources().getColor(R.color.btn_text_color_orange_plue));
                    ((Button)findViewById(R.id.btn_pay)).setTextColor(getResources().getColor(R.color.btn_text_color_orange_plue));
                    dragBtn.setEnabled(false);

                } else if (orderInfo.getService_type() == 2) {
                    typeView.setText(R.string.label_service_type2);
                    typeImgView.setImageResource(R.drawable.icon_service_type2);
//                    typeImgView.setBackgroundResource(R.drawable.bg_oval_blue);

                    setBackground(R.color.color_blue);
                    setLineGone();
                    setArrow(R.drawable.icon48);
                    setTitleLabelColor(R.color.color_white);

                    findViewById(R.id.user_info_layout).setBackgroundResource(R.color.color_blue);
                    ((ImageView)findViewById(R.id.telephone)).setImageResource(R.drawable.icon_telephone_blue);
                    findViewById(R.id.type_icon).setBackgroundResource(R.drawable.bg_oval_blue);
                    findViewById(R.id.btn_arrive).setBackgroundResource(R.drawable.bg_btn_blue2);
                    findViewById(R.id.btn_drag).setBackgroundResource(R.drawable.bg_btn_blue);
                    ((TextView)findViewById(R.id.btn_drag)).setTextColor(getResources().getColor(R.color.color_white));
                    findViewById(R.id.btn_pay).setBackgroundResource(R.drawable.bg_btn_blue);
                    findViewById(R.id.btn_photo).setBackgroundResource(R.drawable.bg_btn_blue);
                    ((Button)findViewById(R.id.btn_photo)).setTextColor(getResources().getColor(R.color.btn_text_color_orange_blue));
                    ((Button)findViewById(R.id.btn_pay)).setTextColor(getResources().getColor(R.color.btn_text_color_orange_blue));
                    dragBtn.setEnabled(false);
                } else {
                    typeView.setText(R.string.label_service_type3);

                    setBackground(R.color.color_green2);
                    setLineGone();
                    setArrow(R.drawable.icon48);
                    setTitleLabelColor(R.color.color_white);

//                    typeImgView.setBackgroundResource(R.drawable.bg_oval_green2);
                    typeImgView.setImageResource(R.drawable.icon_service_type3);
                    findViewById(R.id.user_info_layout).setBackgroundResource(R.color.color_green2);
                    ((ImageView)findViewById(R.id.telephone)).setImageResource(R.drawable.icon_telephone_green);
                    findViewById(R.id.type_icon).setBackgroundResource(R.drawable.bg_oval_green);
                    findViewById(R.id.btn_arrive).setBackgroundResource(R.drawable.bg_second_btn_green2);
                    findViewById(R.id.btn_drag).setBackgroundResource(R.drawable.bg_btn_green2);
                    findViewById(R.id.btn_pay).setBackgroundResource(R.drawable.bg_btn_green2);
                    findViewById(R.id.btn_photo).setBackgroundResource(R.drawable.bg_btn_green2);
                    ((Button)findViewById(R.id.btn_photo)).setTextColor(getResources().getColor(R.color.btn_text_color_orange_green));
                    ((Button)findViewById(R.id.btn_drag)).setTextColor(getResources().getColor(R.color.btn_text_color_orange_green));
                    ((Button)findViewById(R.id.btn_pay)).setTextColor(getResources().getColor(R.color.btn_text_color_orange_green));
                }
                if(orderInfo.getOrder_type() == 2){
                    order_orderView.setVisibility(View.VISIBLE);
                    findViewById(R.id.order_order_line).setVisibility(View.VISIBLE);
                    order_orderView.setText(String.format(getResources().getString(R.string.msg_order_order),orderInfo.getReserved_time()));
                }
                else{
                    order_orderView.setVisibility(View.GONE);
                    findViewById(R.id.order_order_line).setVisibility(View.GONE);
                }
                addressView.setText(orderInfo.getAddress());
                DistanceUtil distanceUtil = new DistanceUtil();
                distanceUtil.getDistance(UpdateStateServe.latitude, UpdateStateServe.longitude,
                        orderInfo.getLatitude(), orderInfo.getLongitude() , OrderInfoActivity.this,distanceView);
               // distanceView.setText(Utils.getFloatFromDouble(Utils.getGeoDistance(UpdateStateServe.longitude,
               //           UpdateStateServe.latitude, orderInfo.getLongitude(), orderInfo.getLatitude()) / 1000));
                //预约订单未开始
                if(orderInfo.getOrder_type() ==2 &&(orderInfo.getSt_reserved_order_at()==null)){
                    arriveBtn.setText(getResources().getString(R.string.label_start_order_order));
                }
                //救援商已经到达现场
                else if (!JSONUtil.isEmpty(orderInfo.getArrive_submit_at())) {
                    showSuccessView(1000);
                    arriveBtn.setText(getResources().getString(R.string.label_rescued));
                }
                //救援商还未到达现场，开始记录经纬度
                else{
                    arriveBtn.setText(getResources().getString(R.string.label_rescued));
                    if(UpdateStateServe.CallClick == 0) {
                        new Handler().postDelayed(new Runnable() {
                            public void run() {
                                MusicUtil.playmusics(OrderInfoActivity.this, MusicUtil.Call);
                            }
                        }, 2000);
                    }
                    UpdateStateServe.UpdateChangeTime = UpdateStateServe.UpdateChangeTimeTen;
                }
            }
            empty.setVisibility(View.GONE);
            super.onPostExecute(result);
        }
    }
    private  Location location ;

    public void onDrag(View v) {
        if(!CherkInternet.cherkInternet(OrderInfoActivity.this)){
            DialogView dialogView = new DialogView();
            dialogView.dialogInternet(this);
        }else
            new GetOrderInfoDrageTask(1).executeOnExecutor(Constants.INFOTHEADPOOL,
                String.format(Constants.HISTORY_ORDER_INFO_URL, id));

    }

    public void onPay(View v) {
        if(!CherkInternet.cherkInternet(OrderInfoActivity.this)){
            DialogView dialogView = new DialogView();
            dialogView.dialogInternet(this);
        }else
            new GetOrderInfoDrageTask(2).executeOnExecutor(Constants.INFOTHEADPOOL,
                String.format(Constants.HISTORY_ORDER_INFO_URL, id));
    }

    public void onPhoto(View v) {
        if(!CherkInternet.cherkInternet(OrderInfoActivity.this)){
            DialogView dialogView = new DialogView();
            dialogView.dialogInternet(this);
        }else
            new GetOrderInfoDrageTask(3).executeOnExecutor(Constants.INFOTHEADPOOL,
                String.format(Constants.HISTORY_ORDER_INFO_URL, id));
    }

    public void showQr_code(View v){
        DialogView dialogView = new DialogView();
        dialogView.dialogImage(R.string.label_close,this);
    }
    public void onNav(View v) {
        if (orderInfo != null) {
            try {
                Uri url = Uri.parse("geo:" + "0,0" + "?q=" + orderInfo.getAddress());
                Intent intent = new Intent(Intent.ACTION_VIEW, url);
                startActivity(intent);
            } catch (Exception e) {
                Toast.makeText(com.autosos.yd.view.OrderInfoActivity.this, R.string.label_no_map, Toast.LENGTH_SHORT).show();
            }
        }
    }

    public void onCall(View v) {
        if(!CherkInternet.cherkInternet(OrderInfoActivity.this)){
            DialogView dialogView = new DialogView();
            dialogView.dialogInternet(this);
        }else
            new GetOrderInfoDrageTask(4).executeOnExecutor(Constants.INFOTHEADPOOL,
                    String.format(Constants.HISTORY_ORDER_INFO_URL, id));

    }

    @Override
    protected void onActivityResult(int requestCode, int resultCode, Intent data) {
        super.onActivityResult(requestCode, resultCode, data);
        Log.e(TAG,data.toString());
    }

    public void onArrive(View v) {
        if (progressBar.getVisibility() == View.VISIBLE) {
            return;
        }
        if (dialog != null && dialog.isShowing()) {
            return;
        }
        if(!CherkInternet.cherkInternet(OrderInfoActivity.this)){
            DialogView dialogView = new DialogView();
            dialogView.dialogInternet(this);
            return;
        }
        //开始预约订单
        if(orderInfo.getOrder_type() ==2 &&(JSONUtil.isEmpty(orderInfo.getSt_reserved_order_at()))){
            dialog = new Dialog(com.autosos.yd.view.OrderInfoActivity.this, R.style.bubble_dialog);
            View view = getLayoutInflater().inflate(R.layout.dialog_msg_notice,
                    null);
            Button tvConfirm = (Button) view.findViewById(R.id.btn_notice_confirm);
            Button tvCancel = (Button) view.findViewById(R.id.btn_notice_cancel);
            TextView tvMsg = (TextView) view.findViewById(R.id.tv_notice_msg);
            tvMsg.setText(R.string.msg_order_arrive);

            tvConfirm.setOnClickListener(new View.OnClickListener() {

                @Override
                public void onClick(View v) {
                    dialog.dismiss();
                    requestOrderTask();
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
            Point point = JSONUtil.getDeviceSize(com.autosos.yd.view.OrderInfoActivity.this);
            params.width = Math.round(point.x * 5 / 7);
            window.setAttributes(params);
            try {
                dialog.show();
            } catch (Exception e) {
                e.printStackTrace();
            }
        }
        //到达救援现场
        else {
            dialog = new Dialog(com.autosos.yd.view.OrderInfoActivity.this, R.style.bubble_dialog);
            View view = getLayoutInflater().inflate(R.layout.dialog_msg_notice,
                    null);
            Button tvConfirm = (Button) view.findViewById(R.id.btn_notice_confirm);
            Button tvCancel = (Button) view.findViewById(R.id.btn_notice_cancel);
            TextView tvMsg = (TextView) view.findViewById(R.id.tv_notice_msg);
            tvMsg.setText(R.string.msg_ok_arrive);

            tvConfirm.setOnClickListener(new View.OnClickListener() {

                @Override
                public void onClick(View v) {
                    dialog.dismiss();
                    requestArriveTask();
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
            Point point = JSONUtil.getDeviceSize(com.autosos.yd.view.OrderInfoActivity.this);
            params.width = Math.round(point.x * 5 / 7);
            window.setAttributes(params);
            try {
                dialog.show();
            } catch (Exception e) {
                e.printStackTrace();
            }
        }
    }

    public void requestArriveTask() {
        progressBar.setVisibility(View.VISIBLE);
        arriveBtn.setEnabled(false);
        String jwd = location.getTrance(Constants.LOCATION_ARRIVE,OrderInfoActivity.this,2);
        try {
            jwd = location.cutErrorPoint(jwd);
            jwd = location.cutErrorPoint2(jwd);
        }catch (Exception e ){
            e.printStackTrace();
        }
        //jwd = location.cutErrorPoint(jwd);
        jwd += "|"+UpdateStateServe.latitude +","+UpdateStateServe.longitude+"#" +System.currentTimeMillis();
        double la = UpdateStateServe.latitude;
        double lo = UpdateStateServe.longitude;
        Map<String, Object> map = new HashMap<>();
        map.put("trace_data",jwd);
        map.put("lat", la);
        map.put("lng", lo);
        Log.e(TAG, "distance total:" + "trace_data :" + jwd + "  --point :" + location.getPoint());
        new NewHttpPutTask(com.autosos.yd.view.OrderInfoActivity.this, new OnHttpRequestListener() {
            @Override
            public void onRequestCompleted(Object obj) {
                progressBar.setVisibility(View.GONE);
                try {
                    JSONObject jsonObject = new JSONObject(obj.toString());
                    Log.e(TAG,jsonObject.toString());
                    if (!jsonObject.isNull("result")) {
                        int result = jsonObject.optInt("result");
                        if (result == 1) {
                            palyonce = true;
                            showSuccessView(8500);
                            MusicUtil.playmusics(OrderInfoActivity.this,MusicUtil.Arive);
                        } else {
                            arriveBtn.setEnabled(true);
                            int code = jsonObject.optInt("code");
                            if (code == 1 ||code ==5) {
                                //订单被取消
                                showCancelView();
                            }
                        }
                    }

                } catch (JSONException e) {
                }
            }

            @Override
            public void onRequestFailed(Object obj) {

            }
        }).execute(String.format(Constants.ARRIVE_SUBMIT_URL, id), map);
    }
    //点击了到达救援现场按钮
    public void showSuccessView(int delay) {
        if(orderInfo.getService_type() == 3 && orderInfo.getTuoche_distance() == 0) {
            new Handler().postDelayed(new Runnable() {
                public void run() {
                    MusicUtil.playmusics(OrderInfoActivity.this, MusicUtil.Click_bottom);
                }
            }, delay);
        }else if(palyonce && orderInfo.getService_type() != 3){
            palyonce = false;
            new Handler().postDelayed(new Runnable() {
                public void run() {
                    MusicUtil.playmusics(OrderInfoActivity.this, MusicUtil.After_help_pay);
                }
            }, delay);
        }
        UpdateStateServe.UpdateChangeTime = UpdateStateServe.UpdateChangeTimeSixty;
        location.deleteJWD(Constants.LOCATION_ARRIVE, OrderInfoActivity.this,2);
        findViewById(R.id.bottom_layout).setVisibility(View.VISIBLE);
        qr_codeView.setVisibility(View.VISIBLE);
        arriveBtn.setVisibility(View.INVISIBLE);
        if (orderInfo.getService_type() != 3) {
            dragBtn.setEnabled(false);
        }
    }

    public void showCancelView() {
        if (progressBar.getVisibility() == View.VISIBLE) {
            return;
        }
        if (dialog != null && dialog.isShowing()) {
            return;
        }
        dialog = new Dialog(com.autosos.yd.view.OrderInfoActivity.this, R.style.bubble_dialog);
        View view = getLayoutInflater().inflate(R.layout.dialog_msg_content,
                null);
        Button tvConfirm = (Button) view.findViewById(R.id.btn_notice_confirm);
        TextView tvMsg = (TextView) view.findViewById(R.id.tv_notice_msg);
        tvMsg.setText(R.string.msg_order_close);

        tvConfirm.setOnClickListener(new View.OnClickListener() {

            @Override
            public void onClick(View v) {
                dialog.dismiss();
                Intent intent = new Intent();
                intent.setClass(com.autosos.yd.view.OrderInfoActivity.this, MainActivity.class);
                intent.setFlags(Intent.FLAG_ACTIVITY_CLEAR_TOP);
                startActivity(intent);
                finish();
                UpdateStateServe.UpdateChangeTime = UpdateStateServe.UpdateChangeTimeSixty;
                overridePendingTransition(0, R.anim.slide_out_right);
            }
        });

        dialog.setContentView(view);
        dialog.setCanceledOnTouchOutside(false);
        Window window = dialog.getWindow();
        WindowManager.LayoutParams params = window.getAttributes();
        Point point = JSONUtil.getDeviceSize(com.autosos.yd.view.OrderInfoActivity.this);
        params.width = Math.round(point.x * 5 / 7);
        window.setAttributes(params);
        try {
            dialog.show();
        }catch (Exception  e){
            e.printStackTrace();
        }

    }

    public void showToastView(int resId,int type) {
        if (progressBar.getVisibility() == View.VISIBLE) {
            return;
        }
        if (dialog != null && dialog.isShowing()) {
            return;
        }
        dialog = new Dialog(OrderInfoActivity.this, R.style.bubble_dialog);
        View view;
        if(type == 2 )
            view= getLayoutInflater().inflate(R.layout.dialog_msg_qr_content,
                null);
        else
            view= getLayoutInflater().inflate(R.layout.dialog_msg_content,
                    null);
        Button tvConfirm = (Button) view.findViewById(R.id.btn_notice_confirm);
        TextView tvMsg = (TextView) view.findViewById(R.id.tv_notice_msg);
        tvMsg.setText(resId);

        tvConfirm.setOnClickListener(new View.OnClickListener() {

            @Override
            public void onClick(View v) {
                dialog.dismiss();
            }
        });

        dialog.setContentView(view);
        dialog.setCanceledOnTouchOutside(false);
        Window window = dialog.getWindow();
        WindowManager.LayoutParams params = window.getAttributes();
        Point point = JSONUtil.getDeviceSize(OrderInfoActivity.this);
        params.width = Math.round(point.x * 5 / 7);
        window.setAttributes(params);
        try {
            dialog.show();
        }catch (Exception  e){
            e.printStackTrace();
        }
    }

    private class GetPayInfoTask extends AsyncTask<String, Object, JSONObject> {

        @Override
        protected JSONObject doInBackground(String... params) {
            try {
                String jsonStr = JSONUtil.getStringFromUrl(OrderInfoActivity.this,params[0]);
                if (JSONUtil.isEmpty(jsonStr)) {
                    return null;
                }
                Log.e(TAG,"xxxxxxx" + jsonStr);
                return new JSONObject(jsonStr);
            } catch (IOException | JSONException e) {
            }
            return null;
        }

        @Override
        protected void onPostExecute(JSONObject result) {
            progressBar.setVisibility(View.GONE);
            payBtn.setEnabled(true);
            orderInfo = new OrderInfo(result);
           /* if (orderInfo != null) {
                Log.e(TAG,"staus  ---- :"+orderInfo.getPay_status());
                if (orderInfo.getPay_status() > 0) {
                    Intent intent = null;
                    //拖车超出免费距离代支付
                    if( !orderInfo.getIs_paid() && orderInfo.getIs_own_expense() ==0 && orderInfo.getPay_amount() >0 &&orderInfo.getIs_completed() == 1){
                        intent = new Intent(OrderInfoActivity.this, PayActivity.class);
                        intent.putExtra("OrderInfo", orderInfo);
                        intent.putExtra("other_free", true);
                    }
                    else if ((orderInfo.getPay_status() == 1 || orderInfo.getPay_status() == 2) && orderInfo.getIs_paid()) {
                        //免费支付显示结果页面
                        intent = new Intent(OrderInfoActivity.this, PayInfoActivity.class);
                        intent.putExtra("OrderInfo", orderInfo);
                        intent.putExtra("free", true);
                    } else if (orderInfo.getPay_status() == 3 && !orderInfo.getIs_paid()) {
                        //微信支付
                        intent = new Intent(OrderInfoActivity.this, PayActivity.class);
                        intent.putExtra("OrderInfo", orderInfo);
                        intent.putExtra("other_free", false);
                    } else if (orderInfo.getPay_status() == 3 && orderInfo.getIs_paid()) {
                        //代付显示结果页面
                        intent = new Intent(OrderInfoActivity.this, PayInfoActivity.class);
                        intent.putExtra("OrderInfo", orderInfo);
                        intent.putExtra("free", false);
                    }
                    startActivity(intent);
                    overridePendingTransition(R.anim.slide_in_right, R.anim.activity_anim_default);
                } else {
                        showToastView(R.string.msg_pls_wait,1);
                }
                */
            if(orderInfo == null)
                return;
            if (orderInfo.getIs_completed() == 1) {
                //完成救援
                Intent intent = null;
                if (orderInfo.getPay_amount() > 0) {
                    //需要支付费用
                    if (orderInfo.getIs_paid()) {
                        intent = new Intent(OrderInfoActivity.this, PayInfoActivity.class);
                        intent.putExtra("OrderInfo", orderInfo);
                        intent.putExtra("free", true);
                        if(orderInfo.getIs_cash() == 1){
                            intent.putExtra("free", false);
                        }
                    } else {
                        //未支付
                        if (orderInfo.getIs_cash() == 1) {
                            //go代付
                            intent = new Intent(OrderInfoActivity.this, PayActivity.class);
                            intent.putExtra("OrderInfo", orderInfo);
                            intent.putExtra("other_free", false);
                            if(orderInfo.getInsurance_id() > 0)
                                intent.putExtra("other_free", true);

                        } else {
                            showToastView(R.string.msg_pls_wait2,3);
                        }
                    }
                } else {
                    //无需支付费用
                    intent = new Intent(OrderInfoActivity.this, PayInfoActivity.class);
                    intent.putExtra("OrderInfo", orderInfo);
                    intent.putExtra("free", true);
                }
                if(intent != null) {
                    startActivity(intent);
                    overridePendingTransition(R.anim.slide_in_right, R.anim.activity_anim_default);
                }
            } else {
                showToastView(R.string.msg_pls_wait,1);
            }

            super.onPostExecute(result);
        }
    }


    @Override
    public void onBackPressed() {
        super.onBackPressed();
        overridePendingTransition(0, R.anim.slide_out_right);

    }

    @Override
    protected void onPause() {
        super.onPause();
        MobclickAgent.onPause(this);
    }

    @Override
    protected void onResume() {
        super.onResume();
        Log.e(TAG, "id    " + id);
        if(!CherkInternet.cherkInternet(OrderInfoActivity.this)){
            DialogView dialogView = new DialogView();
            dialogView.dialogInternet(this);
        }
        else
            new GetOrderInfoTask().executeOnExecutor(Constants.INFOTHEADPOOL,
                String.format(Constants.HISTORY_ORDER_INFO_URL, id));
        MobclickAgent.onResume(this);
    }

    @Override
    protected void onDestroy() {
        super.onDestroy();
    }
    private class GetOrderInfoDrageTask extends AsyncTask<String, Object, JSONObject> {
        private int todo;
        private GetOrderInfoDrageTask(int todo) {
            this.todo = todo;
        }
        @Override
        protected JSONObject doInBackground(String... params) {
            try {
                String jsonStr = JSONUtil.getStringFromUrl(OrderInfoActivity.this,params[0]);
                if (JSONUtil.isEmpty(jsonStr)) {
                    return null;
                }
                Log.e(TAG,"-----------" + jsonStr );
                return new JSONObject(jsonStr);
            } catch (IOException | JSONException e) {
            }

            return null;
        }

        @Override
        protected void onPostExecute(JSONObject result) {
            progressBar.setVisibility(View.GONE);
            orderInfo = new OrderInfo(result);
            if (orderInfo != null) {
                if(orderInfo.getStatus() == 401 || orderInfo.getStatus() == 400 || orderInfo.getStatus() == 403){
                    showCancelView();
                }
                else{
                    switch (todo){
                        case 1:

                            if (orderInfo.getService_type() == 3) {
                                Intent intent;
                                intent = new Intent();
                                if (orderInfo.getTuoche_distance() > 0){
                                    intent.setClass(OrderInfoActivity.this, DragActivity.class);
                                }
                                else {
                                    intent.setClass(OrderInfoActivity.this, MapDragActivity.class);
                                }
                                intent.putExtra("OrderInfo", orderInfo);
                                startActivity(intent);
                                overridePendingTransition(R.anim.slide_in_right, R.anim.activity_anim_default);
                            }
                            break;
                        case 2:
                            progressBar.setVisibility(View.VISIBLE);
                            payBtn.setEnabled(false);
                            new GetPayInfoTask().executeOnExecutor(Constants.INFOTHEADPOOL,
                                    String.format(Constants.HISTORY_ORDER_INFO_URL, id));
                            break;
                        case 3:
                            if (true || orderInfo.getIs_completed() == 1&&((orderInfo.getPay_amount() > 0 && orderInfo.getIs_paid())
                                    || (orderInfo.getPay_amount() == 0))) {
                                Intent intent = new Intent(com.autosos.yd.view.OrderInfoActivity.this, UploadPhotoActivity.class);
                                intent.putExtra("OrderInfo", orderInfo);
                                intent.putExtra("id", orderInfo.getId());
                                startActivity(intent);
                                overridePendingTransition(R.anim.slide_in_right, R.anim.activity_anim_default);
                            } else {
                                showToastView(R.string.msg_pls_confirm,1);
                            }
                            break;
                        case 4:
                            if (orderInfo != null) {
                                Map<String, Object> map = new HashMap<>();
                                map.put("lat", UpdateStateServe.latitude);
                                map.put("lng",UpdateStateServe.longitude);
                                new com.autosos.yd.task.NewHttpPutTask(OrderInfoActivity.this, new com.autosos.yd.task.OnHttpRequestListener() {
                                    @Override
                                    public void onRequestCompleted(Object obj) {
                                        Log.e(TAG,"You have a call  just now !");
                                        UpdateStateServe.CallClick ++;
                                    }
                                    @Override
                                    public void onRequestFailed(Object obj) {

                                    }
                                }).execute(String.format(Constants.ORDERS_DIAL, orderInfo.getId()), map);


                                if (!JSONUtil.isEmpty(orderInfo.getOwner_mobile())) {
                                    try {
                                        Intent phoneIntent = new Intent(Intent.ACTION_DIAL,
                                                Uri.parse("tel:" + orderInfo.getOwner_mobile().trim()));
                                        startActivity(phoneIntent);
                                    } catch (Exception e) {
                                    }
                                }
                            }
                    }
                }
            }
            super.onPostExecute(result);
        }
    }


    public void requestOrderTask() {
        progressBar.setVisibility(View.VISIBLE);
        double la = UpdateStateServe.latitude;
        double lo = UpdateStateServe.longitude;
        Map<String, Object> map = new HashMap<>();
        map.put("lat", la);
        map.put("lng", lo);
        new NewHttpPutTask(com.autosos.yd.view.OrderInfoActivity.this, new OnHttpRequestListener() {
            @Override
            public void onRequestCompleted(Object obj) {
                progressBar.setVisibility(View.GONE);
                try {
                    JSONObject jsonObject = new JSONObject(obj.toString());
                    Log.e(TAG,jsonObject.toString());
                    if (!jsonObject.isNull("result")) {
                        int result = jsonObject.optInt("result");
                        if (result == 1) {
                            new GetOrderInfoTask().executeOnExecutor(Constants.INFOTHEADPOOL,
                                    String.format(Constants.HISTORY_ORDER_INFO_URL, id));
                        } else {
                            int code = jsonObject.optInt("code");
                            if (code == 1 ||code ==5) {
                                //订单被取消
                                showCancelView();
                            }
                            else {
                                Log.e(TAG, jsonObject.toString());
                                String msg = jsonObject.optString("msg");
                                Toast.makeText(OrderInfoActivity.this, msg, Toast.LENGTH_LONG).show();
                            }
                        }
                    }
                    else{

                    }

                } catch (JSONException e) {
                }
            }

            @Override
            public void onRequestFailed(Object obj) {

            }
        }).execute(String.format(Constants.ORDER_ORDER_START_URL, id), map);
    }
}