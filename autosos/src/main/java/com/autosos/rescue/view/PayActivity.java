package com.autosos.rescue.view;

import android.app.Activity;
import android.app.Application;
import android.app.Dialog;
import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.Intent;
import android.content.IntentFilter;
import android.content.SharedPreferences;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.graphics.Point;
import android.media.Image;
import android.os.Bundle;
import android.os.Handler;
import android.os.Message;
import android.util.DisplayMetrics;
import android.util.Log;
import android.view.KeyEvent;
import android.view.View;
import android.view.ViewGroup;
import android.view.Window;
import android.view.WindowManager;
import android.widget.Button;
import android.widget.ImageView;
import android.widget.ProgressBar;
import android.widget.TextView;
import android.widget.Toast;

import com.autosos.rescue.Constants;
import com.autosos.rescue.R;
import com.autosos.rescue.application.MyApplication;
import com.autosos.rescue.fragment.FragmentForWork;
import com.autosos.rescue.model.NewOrder;
import com.autosos.rescue.model.OrderInfo;
import com.autosos.rescue.task.HttpGetTask;
import com.autosos.rescue.task.NewHttpPutTask;
import com.autosos.rescue.task.OnHttpRequestListener;
import com.autosos.rescue.util.CreateQRImage;
import com.autosos.rescue.util.JSONUtil;
import com.iflytek.thridparty.G;
import com.umeng.analytics.MobclickAgent;

import org.json.JSONException;
import org.json.JSONObject;

import java.util.HashMap;
import java.util.Map;
import java.util.Timer;
import java.util.TimerTask;

/**
 * Created by Administrator on 2016/6/28.
 */
public class PayActivity extends Activity implements View.OnClickListener{

    private Button btn_weixinpay;
    private View erweima_layout;
    private ImageView close_erweima;
    private OrderInfo orderInfo;
    private int orderId;
    private ProgressBar progressBar;
    private ImageView qr_image;
    private TextView pay_amount,base_price,more_amount,total_dis,bonus,night_price,edit_price;
    Timer time_clock = new Timer();
    private MyBroadcastReciever  myBroadcastReciever;
    private Dialog dialog;
    private TextView check_detail;
    private Boolean isClicked = false;
    private View price_detail,bottomPart;
    private TextView tv_price_title;
    private View hint_pay_detail;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_pay);
        btn_weixinpay = (Button) findViewById(R.id.btn_weixinpay);
        btn_weixinpay.setOnClickListener(this);

        erweima_layout = findViewById(R.id.erweima_layout);
        close_erweima = (ImageView) findViewById(R.id.close_erweima);
        close_erweima.setOnClickListener(this);

        progressBar = (ProgressBar) findViewById(R.id.progressBar);

        qr_image = (ImageView) findViewById(R.id.qr_image);

        pay_amount = (TextView) findViewById(R.id.pay_amount);
        base_price = (TextView) findViewById(R.id.base_price);
        more_amount = (TextView) findViewById(R.id.more_amount);
        total_dis = (TextView) findViewById(R.id.total_dis);
        bonus = (TextView) findViewById(R.id.bonus);
        night_price = (TextView) findViewById(R.id.night_price);
        edit_price = (TextView) findViewById(R.id.edit_price);
        check_detail = (TextView) findViewById(R.id.check_detail);
        check_detail.setOnClickListener(this);

        price_detail = findViewById(R.id.price_detail);
        bottomPart = findViewById(R.id.bottomPart);

        tv_price_title = (TextView) findViewById(R.id.tv_price_title);

        hint_pay_detail = findViewById(R.id.hint_pay_detail);

        myBroadcastReciever = new MyBroadcastReciever();
        IntentFilter intentFilter = new IntentFilter();
        intentFilter.addAction("editPrice");
        registerReceiver(myBroadcastReciever, intentFilter);

        if (time_clock == null) {

            time_clock = new Timer();
        }
        TimerTask task_clock = new TimerTask() {
            public void run() {
                Message msg = new Message();
                mHandler.sendEmptyMessage(0);
            }
        };

        time_clock.schedule(task_clock, 0, 500);
    }

    //按返回键 没有办法返回
    @Override
    public boolean onKeyDown(int keyCode, KeyEvent event) {

        if (keyCode == KeyEvent.KEYCODE_BACK) {
            return true;
        }
        return false;
    }

    @Override
    public void onClick(View v) {

        switch (v.getId()){

            case R.id.btn_weixinpay:

                if(orderInfo.getPay_amount()==0){

                    new HttpGetTask(PayActivity.this, new OnHttpRequestListener() {
                        @Override
                        public void onRequestCompleted(Object obj) {
                            try {
                                JSONObject jsonObject = new JSONObject(obj.toString());

                                if (!jsonObject.isNull("result")) {

                                    int result = jsonObject.optInt("result");
                                    Log.d("nopay",obj.toString());
                                    if (result == 1) {
                                        //  Toast.makeText(NewWayActivity.this, "支付成功!", Toast.LENGTH_SHORT).show();
                                        SharedPreferences sharedPreference = getSharedPreferences("order", Context.MODE_PRIVATE);
                                        sharedPreference.edit().remove("order").commit();
                                        SharedPreferences sharedPreference2 = getSharedPreferences("orderInfo", Context.MODE_PRIVATE);
                                        sharedPreference2.edit().remove("orderInfo").commit();
                                        MyApplication.application.isAfterOrder = true;
                                        finish();
                                    } else {

                                    }

                                }

                            } catch (JSONException e) {

                            }

                        }
                        @Override
                        public void onRequestFailed(Object obj) {

                            Toast.makeText(PayActivity.this, "网络环境不太好,请重新点击", Toast.LENGTH_SHORT).show();
                            PayActivity.this.recreate();
                        }

                    }).execute(String.format(Constants.NO_NEED_PAY, orderId));

                }else {

                    erweima_layout.setVisibility(View.VISIBLE);
                    Bitmap logo_weixin = BitmapFactory.decodeResource(PayActivity.this.getResources(), R.drawable.icon45_200x200);
                    CreateQRImage.createImage(orderInfo.getPay_ewm(), qr_image, logo_weixin);
//                    if(time_clock == null){
//
//                        time_clock = new Timer();
//                    }
//                    TimerTask task_clock = new TimerTask() {
//                        public void run() {
//                            Message msg = new Message();
//                            mHandler.sendEmptyMessage(0);
//                        }
//                    };
//
//                    time_clock.schedule(task_clock, 0, 500);
                }

                break;
            case R.id.close_erweima:
                erweima_layout.setVisibility(View.GONE);
//                try {
//
//                    if (time_clock != null) {
//
//                        time_clock.cancel();
//                    }
//
//                } catch (Exception e) {
//
//                    time_clock = null;
//
//                }finally {
//
//                    time_clock = null;
//                }
                break;
            case R.id.check_detail:
                DisplayMetrics dm =getResources().getDisplayMetrics();
                float density = dm.density;
                if(!isClicked){
                    check_detail.setText("-点击收起收费明细");
                    price_detail.setVisibility(View.VISIBLE);
                    ViewGroup.MarginLayoutParams layoutParam = (ViewGroup.MarginLayoutParams) bottomPart.getLayoutParams();
                    layoutParam.topMargin =(int)(350*density);
                    bottomPart.setLayoutParams(layoutParam);
                    isClicked = true;
                }else {
                    check_detail.setText("+点击查看收费明细");
                    price_detail.setVisibility(View.GONE);
                    ViewGroup.MarginLayoutParams layoutParam = (ViewGroup.MarginLayoutParams) bottomPart.getLayoutParams();
                    layoutParam.topMargin =(int)(150*density);
                    bottomPart.setLayoutParams(layoutParam);
                    isClicked = false;
                }
                break;
            default:
                break;
        }
    }

    @Override
    protected void onResume() {
        super.onResume();
        progressBar.setVisibility(View.VISIBLE);
        SharedPreferences sp = getSharedPreferences("order", Context.MODE_PRIVATE);
        String s_order = sp.getString("order", null);
        final JSONObject order;
        if (s_order != null) {
            try {
                order = new JSONObject(s_order);
                NewOrder od = new NewOrder(order);
                orderId = od.getOrderId();
                Log.d("orderId",orderId+"");

            } catch (Exception e) {


            }

        }else{

            SharedPreferences sp2 = getSharedPreferences("orderInfo", Context.MODE_PRIVATE);
            String s_orderInfo = sp2.getString("orderInfo", null);
            try {
                order = new JSONObject(s_orderInfo);
                OrderInfo od = new OrderInfo(order);
                orderId = od.getOrderId();
                Log.d("orderId",orderId+"");

            } catch (Exception e) {


            }
        }

        new HttpGetTask(getApplicationContext(), new OnHttpRequestListener() {


            @Override
            public void onRequestCompleted(Object obj) {

                JSONObject jsonObject;
                try {
                    progressBar.setVisibility(View.GONE);
                    Log.d("orderInfo_pay", obj.toString());
                    jsonObject = new JSONObject(obj.toString());
                    orderInfo = new OrderInfo(jsonObject);
                    if(orderInfo.getIsPaodan() ==1){

                        DisplayMetrics dm =getResources().getDisplayMetrics();
                        float density = dm.density;

                        ViewGroup.MarginLayoutParams layoutParam = (ViewGroup.MarginLayoutParams) hint_pay_detail.getLayoutParams();
                        layoutParam.topMargin =(int)(30*density);
                        hint_pay_detail.setLayoutParams(layoutParam);
                        check_detail.setVisibility(View.GONE);
                        pay_amount.setText("代收"+orderInfo.getPay_amount()+"元");

                    }else{

                        check_detail.setVisibility(View.VISIBLE);

                        if(orderInfo.getIs_one_price()==1){

                            tv_price_title.setText("一口价");

                        }else{

                            tv_price_title.setText("起步价(15km)");
                        }
                        pay_amount.setText(orderInfo.getPay_amount()+"元");
                        more_amount.setText("+"+orderInfo.getMore_amount()+"元");
                        if(orderInfo.getPay_amount()==0){

                            btn_weixinpay.setText("无需收款,继续接单");

                        }else{

                            btn_weixinpay.setText("微信扫码支付");
                        }
                        if(orderInfo.getIs_support_free()==1){

                            base_price.setText("0.0元");

                        }else{

                            base_price.setText(orderInfo.getBase_price()+"元");
                        }
                        if(orderInfo.getBonus()==0){

                            bonus.setText("+0.0元");

                        }else{

                            bonus.setText("+"+orderInfo.getBonus()+"元");
                        }


                        if(orderInfo.getNight_price()==0){

                            night_price.setText("+0.0元");

                        }else{

                            night_price.setText("+"+orderInfo.getNight_price()+"元");
                        }

                        if(orderInfo.getEdit_price()  == 0){

                            edit_price.setText("+0.0元");

                        }else if(orderInfo.getEdit_price()>0){

                            edit_price.setText("+"+orderInfo.getEdit_price()+"元");
                        }else{

                            edit_price.setText(orderInfo.getEdit_price()+"元");
                        }
                        if(orderInfo.getServiceType()==1){

                            String dis = String.format("%.2f", orderInfo.getReal_tuoche_dis());
                            total_dis.setText(dis+"km");


                        }else {
                            Log.d("orderInfo_pay", orderInfo.getReal_dis()+"");
                            String dis = String.format("%.2f", orderInfo.getReal_dis());
                            total_dis.setText(dis+"km");
                        }

                    }



                }catch (Exception e){

                    e.printStackTrace();
                }
            }

            @Override
            public void onRequestFailed(Object obj) {


            }

        }).execute(String.format(Constants.ORDER_INFO_URL, orderId));

        MobclickAgent.onResume(this);
    }



    @Override
    protected void onDestroy() {
        super.onDestroy();
        unregisterReceiver(myBroadcastReciever);
        try {

            if (time_clock != null) {

                time_clock.cancel();
            }


        } catch (Exception e) {

            time_clock = null;
            finish();
        }
    }

    private Handler mHandler = new Handler() {

        public void handleMessage(Message msg) {

            if (msg.what == 0) {

                new HttpGetTask(PayActivity.this, new OnHttpRequestListener() {
                    @Override
                    public void onRequestCompleted(Object obj) {
                        try {
                            JSONObject jsonObject = new JSONObject(obj.toString());

                            if (!jsonObject.isNull("result")) {

                                int result = jsonObject.optInt("result");
                                if (result == 1) {
                                    time_clock.cancel();
                                    //  Toast.makeText(NewWayActivity.this, "支付成功!", Toast.LENGTH_SHORT).show();
                                    SharedPreferences sharedPreference = getSharedPreferences("order", Context.MODE_PRIVATE);
                                    sharedPreference.edit().remove("order").commit();
                                    SharedPreferences sharedPreference2 = getSharedPreferences("orderInfo", Context.MODE_PRIVATE);
                                    sharedPreference2.edit().remove("orderInfo").commit();
                                    MyApplication.application.isAfterOrder = true;
                                    finish();

                                } else {

                                    // Toast.makeText(NewWayActivity.this, "支付未完成!"+result, Toast.LENGTH_SHORT).show();
                                }

                            }

                        } catch (JSONException e) {

                        }

                    }

                    @Override
                    public void onRequestFailed(Object obj) {

                        Toast.makeText(PayActivity.this, "网络环境不太好,支付没有成功", Toast.LENGTH_SHORT).show();
                        PayActivity.this.recreate();
                    }

                }).execute(String.format(Constants.CHECK_IS_PAID, orderId));

            }

        }
    };


    public class MyBroadcastReciever extends BroadcastReceiver {

        @Override
        public void onReceive(Context context, Intent intent) {

            if("editPrice".equals(intent.getAction())){
                Log.d("editPrice","ok");
                dialog = new Dialog(PayActivity.this, R.style.bubble_dialog);
                View view = getLayoutInflater().inflate(R.layout.dialog_msg_content,
                        null);
                Button tvConfirm = (Button) view.findViewById(R.id.btn_notice_confirm);
                TextView tvMsg = (TextView) view.findViewById(R.id.tv_notice_msg);
                tvMsg.setText("订单被后台改价了,请刷新");

                tvConfirm.setOnClickListener(new View.OnClickListener() {

                    @Override
                    public void onClick(View v) {
                        dialog.dismiss();
                        if(erweima_layout.getVisibility()==View.VISIBLE){

                            erweima_layout.setVisibility(View.GONE);
                            try {

                                if (time_clock != null) {

                                    time_clock.cancel();
                                }

                            } catch (Exception e) {

                                time_clock = null;

                            }finally {

                                time_clock = null;
                            }
                        }
                        progressBar.setVisibility(View.VISIBLE);
                        new Handler().postDelayed(new Runnable() {
                            @Override
                            public void run() {

                                progressBar.setVisibility(View.GONE);
                                PayActivity.this.recreate();
                            }
                        },1000);
                    }


                });

                dialog.setContentView(view);
                dialog.setCanceledOnTouchOutside(false);
                Window window = dialog.getWindow();
                WindowManager.LayoutParams params = window.getAttributes();
                Point point = JSONUtil.getDeviceSize(getApplicationContext());
                params.width = Math.round(point.x * 5 / 7);
                window.setAttributes(params);
                try {
                    dialog.show();
                } catch (Exception e) {
                    e.printStackTrace();
                }

            }
        }


      }

    }
