package com.autosos.yd.view;

import android.app.Activity;
import android.app.Dialog;
import android.content.ComponentName;
import android.content.Context;
import android.content.Intent;
import android.graphics.Point;
import android.graphics.drawable.Drawable;
import android.os.Bundle;
import android.os.Handler;
import android.util.Log;
import android.view.Gravity;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.view.Window;
import android.view.WindowManager;
import android.widget.Button;
import android.widget.CheckBox;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.PopupWindow;
import android.widget.RadioGroup;
import android.widget.TextView;
import android.widget.Toast;

import com.autosos.yd.model.Pay;
import com.autosos.yd.util.JSONUtil;
import com.autosos.yd.util.MusicUtil;
import com.pingplusplus.android.PingppLog;
import com.pingplusplus.android.PaymentActivity;
import com.umeng.analytics.MobclickAgent;
import com.unionpay.UPPayAssistEx;

import org.json.JSONException;
import org.json.JSONObject;

import java.util.HashMap;
import java.util.Map;

import com.autosos.yd.Constants;
import com.autosos.yd.R;
import com.autosos.yd.model.OrderInfo;
import com.autosos.yd.task.NewHttpPostTask;
import com.autosos.yd.task.OnHttpRequestListener;
import com.autosos.yd.view.*;
import com.autosos.yd.view.AutososBackActivity;

public class PayActivity extends AutososBackActivity {

    private static final int REQUEST_CODE_PAYMENT = 1;
    private OrderInfo orderInfo;
    private TextView statusView;
    private View include_payView;
    private Dialog dialog;
    private LinearLayout descrip;
    private TextView free_distance;
    private TextView phonenumber;
    private Boolean bWX;
    private Boolean bZFB;
    private View empty;
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_pay);
        bWX = false;
        bZFB = false;
        empty = findViewById(R.id.empty);
        orderInfo = (OrderInfo) getIntent().getSerializableExtra("OrderInfo");
        boolean other_free = getIntent().getBooleanExtra("other_free", false);
        new Handler().postDelayed(new Runnable() {
                public void run() {
                    MusicUtil.playmusics(PayActivity.this, MusicUtil.Get_cash);}
            }, 500);
        statusView = (TextView) findViewById(R.id.status);
        descrip = (LinearLayout) findViewById(R.id.descrip);
        free_distance = (TextView) findViewById(R.id.free_distance);
        phonenumber = (TextView) findViewById(R.id.phonenumber);
        include_payView = findViewById(R.id.include_pay);
        include_payView.setVisibility(View.GONE);
        TextView typeView = (TextView) findViewById(R.id.type);
        if (!other_free){
            descrip.setVisibility(View.GONE);
        }
        if (orderInfo.getService_type() == 1) {
            typeView.setText(R.string.label_service_type1);
        } else if (orderInfo.getService_type() == 2) {
            typeView.setText(R.string.label_service_type2);
        } else {
            typeView.setText(R.string.label_service_type3);
            if (!other_free){
                descrip.setVisibility(View.GONE);
            }else {
                free_distance.setText(""+ orderInfo.getTuoche_free_km() +getResources().getString(R.string.label_m));
                phonenumber.setText(orderInfo.getInsurance_name() + orderInfo.getHotline());
            }
        }
        TextView costView = (TextView) findViewById(R.id.cost);
        costView.setText(String.valueOf(orderInfo.getPay_amount()));
    }

    public void onThirdPay(View v) {
//         openWX();
        empty.setVisibility(View.VISIBLE);
        setEmptyVisibal();
        showPop();
    }

    public void openWX(){
        include_payView.setVisibility(View.VISIBLE);
         findViewById(R.id.payBtn).setClickable(false);
         Map<String, Object> map = new HashMap<>();
         map.put("channel", "wx");
         Log.e("Pay",map.toString());
         new NewHttpPostTask(this, new OnHttpRequestListener() {
            @Override
             public void onRequestCompleted(Object obj) {
                    findViewById(R.id.payBtn).setClickable(false);
                    try {
                     JSONObject jsonObject = new JSONObject(obj.toString());
                        Log.e("xxxxxxxxxx",jsonObject.toString());
                      if (!jsonObject.isNull("result")) {
                      int result = jsonObject.optInt("result");
                      if (result == 1) {
                        String charge = jsonObject.optString("charge");
                        Intent intent = new Intent();
                        String packageName = getPackageName();
                        ComponentName componentName = new ComponentName("com.autosos.yd", "com.autosos.yd.wxapi.WXPayEntryActivity");
                        intent.setComponent(componentName);
                        intent.putExtra(PaymentActivity.EXTRA_CHARGE, charge);
                        startActivityForResult(intent, REQUEST_CODE_PAYMENT);
                     } else if(result == 0){
                          findViewById(R.id.payBtn).setClickable(true);
                          include_payView.setVisibility(View.GONE);
                          String message =jsonObject.optString("info");
                          JSONObject msg =new JSONObject(message.toString());
                          String me = msg.optString("error");
                          msg = new JSONObject(me.toString());
                          String m = msg.optString("message");
                          View v2 = getLayoutInflater().inflate(R.layout.dialog_msg_content,
                                  null);
                          dialog = new Dialog(v2.getContext(), R.style.bubble_dialog);
                          Button tvConfirm = (Button) v2.findViewById(R.id.btn_notice_confirm);
                          TextView tvMsg = (TextView) v2.findViewById(R.id.tv_notice_msg);
                          tvMsg.setText(String.format(getString(R.string.msg_pay_error))+"  :"+m.toString());
                          tvConfirm.setOnClickListener(new View.OnClickListener() {

                              @Override
                              public void onClick(View v) {
                                  dialog.dismiss();
                              }
                          });
                          dialog.setContentView(v2);
                          dialog.setCanceledOnTouchOutside(false);
                          Window window = dialog.getWindow();
                          WindowManager.LayoutParams params = window.getAttributes();
                          Point point = JSONUtil.getDeviceSize(PayActivity.this);
                          params.width = Math.round(point.x * 5 / 7);
                          window.setAttributes(params);
                          dialog.show();
                      }
                }
            } catch (JSONException e) {
                        Log.e("Pay",e.toString());
            }

         }

            @Override
            public void onRequestFailed(Object obj) {
         }
         }).execute(String.format(Constants.PAY_URL, orderInfo.getId()), map);

    }
    public void openZFB(){
        include_payView.setVisibility(View.VISIBLE);
        findViewById(R.id.payBtn).setClickable(false);
        Map<String, Object> map = new HashMap<>();
        map.put("channel", "alipay");
        Log.e("Pay",map.toString());
        new NewHttpPostTask(this, new OnHttpRequestListener() {
            @Override
            public void onRequestCompleted(Object obj) {
                findViewById(R.id.payBtn).setClickable(false);
                try {
                    JSONObject jsonObject = new JSONObject(obj.toString());
                    Log.e("xxxxxxxxxx",jsonObject.toString());
                    if (!jsonObject.isNull("result")) {
                        int result = jsonObject.optInt("result");
                        if (result == 1) {
                            String charge = jsonObject.optString("charge");
                            Intent intent = new Intent();
                            String packageName = getPackageName();
                            ComponentName componentName = new ComponentName("com.autosos.yd", "com.autosos.yd.wxapi.WXPayEntryActivity");
                            intent.setComponent(componentName);
                            intent.putExtra(PaymentActivity.EXTRA_CHARGE, charge);
                            startActivityForResult(intent, REQUEST_CODE_PAYMENT);
                        } else if(result == 0){
                            findViewById(R.id.payBtn).setClickable(true);
                            include_payView.setVisibility(View.GONE);
                            String message =jsonObject.optString("info");
                            JSONObject msg =new JSONObject(message.toString());
                            String me = msg.optString("error");
                            msg = new JSONObject(me.toString());
                            String m = msg.optString("message");
                            View v2 = getLayoutInflater().inflate(R.layout.dialog_msg_content,
                                    null);
                            dialog = new Dialog(v2.getContext(), R.style.bubble_dialog);
                            Button tvConfirm = (Button) v2.findViewById(R.id.btn_notice_confirm);
                            TextView tvMsg = (TextView) v2.findViewById(R.id.tv_notice_msg);
                            tvMsg.setText(String.format(getString(R.string.msg_pay_error))+"  :"+m.toString());
                            tvConfirm.setOnClickListener(new View.OnClickListener() {

                                @Override
                                public void onClick(View v) {
                                    dialog.dismiss();
                                }
                            });
                            dialog.setContentView(v2);
                            dialog.setCanceledOnTouchOutside(false);
                            Window window = dialog.getWindow();
                            WindowManager.LayoutParams params = window.getAttributes();
                            Point point = JSONUtil.getDeviceSize(PayActivity.this);
                            params.width = Math.round(point.x * 5 / 7);
                            window.setAttributes(params);
                            dialog.show();
                        }
                    }
                } catch (JSONException e) {
                    Log.e("Pay",e.toString());
                }

            }

            @Override
            public void onRequestFailed(Object obj) {
            }
        }).execute(String.format(Constants.PAY_URL, orderInfo.getId()), map);

    }

    public void showPop(){
        LayoutInflater inflater = (LayoutInflater) getSystemService(Context.LAYOUT_INFLATER_SERVICE);
        View vPopWindow =null;
        vPopWindow  = inflater.inflate(R.layout.popwindow_pay, null);
        final PopupWindow popupWindow =new PopupWindow(vPopWindow, ViewGroup.LayoutParams.FILL_PARENT, ViewGroup.LayoutParams.WRAP_CONTENT,true);
        Drawable drawable = getResources().getDrawable(R.color.color_white);
        popupWindow.setBackgroundDrawable(drawable);
        popupWindow.setFocusable(true);
        popupWindow.setOnDismissListener(new PopupWindow.OnDismissListener() {
            @Override
            public void onDismiss() {
                Log.e("pay","dismiss");
                empty.setVisibility(View.GONE);
                setEmptyGo();
            }
        });
        popupWindow.setAnimationStyle(R.style.popWindow_slide_in_out);
        final ImageView hook_wx = (ImageView) vPopWindow.findViewById(R.id.hook_wx);
        final ImageView hook_zfb = (ImageView) vPopWindow.findViewById(R.id.hook_zfb);
        LinearLayout bt_wx = (LinearLayout) vPopWindow.findViewById(R.id.bt_wx);
        LinearLayout bt_zfb = (LinearLayout) vPopWindow.findViewById(R.id.bt_zfb);
        bt_wx.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
            hook_wx.setVisibility(View.VISIBLE);
            hook_zfb.setVisibility(View.GONE);
                bWX = true;
                bZFB = false;

            Log.e("pay","=== wx ====");
            }
        });
        bt_zfb.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                hook_wx.setVisibility(View.GONE);
                hook_zfb.setVisibility(View.VISIBLE);
                bWX = false;
                bZFB = true;
                Log.e("pay","=== zfb ====");
            }
        });

        final View finalVPopWindow = vPopWindow;
        View view = getWindow().getDecorView();
        popupWindow.showAtLocation(view, Gravity.BOTTOM, 0, 0);
    }

    public void surePay(View view){
        if (!bZFB && !bWX){
            Toast.makeText(PayActivity.this,"请选择支付方式",Toast.LENGTH_SHORT).show();
        }else if (bWX){
            openWX();
        }else {
            openZFB();
        }
    }

    protected void onActivityResult(int requestCode, int resultCode, Intent data) {
        PingppLog.DEBUG = true ;
        findViewById(R.id.payBtn).setClickable(true);
        include_payView.setVisibility(View.GONE);
        if (requestCode == REQUEST_CODE_PAYMENT) {
            if (resultCode == Activity.RESULT_OK) {
                String result = data.getExtras().getString("pay_result");
                Log.e("PAYACTIVITY","request :"+requestCode+"***  resultCode  :"+requestCode+" **  data  :"
                        +data.getExtras().toString()+" --pat_result :"+result);
                if (result.equals("success")) {
                   /* findViewById(R.id.take_photo).setVisibility(View.VISIBLE);
                    orderInfo.setIs_paid(true);
                    orderInfo.setPay_status(3);
                    statusView.setText(R.string.label_server_pay_ok);
                    MusicUtil.playmusics(PayActivity.this,MusicUtil.Take_photo_send);
                    findViewById(R.id.payBtn).setVisibility(View.GONE);
                    */
                    orderInfo.setIs_paid(true);
                    orderInfo.setPay_status(3);
                    Intent intent = new Intent(PayActivity.this, PayInfoActivity.class);
                    intent.putExtra("OrderInfo", orderInfo);
                    intent.putExtra("free", false);
                    startActivity(intent);
                    overridePendingTransition(R.anim.slide_in_right, R.anim.activity_anim_default);
                    finish();
                }
                else if (result.equals("cancel")) {
                    Toast.makeText(this, R.string.msg_pay_cancel, Toast.LENGTH_SHORT).show();
                }
                else if (result.equals("fail")) {
                    Toast.makeText(this, R.string.msg_pay_fail, Toast.LENGTH_SHORT).show();
                }  else {
                    UPPayAssistEx.installUPPayPlugin(this);
                }
            } else if (resultCode == Activity.RESULT_CANCELED) {
                Toast.makeText(this, R.string.msg_pay_cancel+"2", Toast.LENGTH_SHORT).show();
            }// else if (resultCode == PaymentActivity.RESULT_EXTRAS_INVALID) {
             //   Toast.makeText(this, "An invalid Credential was submitted.", Toast.LENGTH_SHORT).show();
         //   }
        }
    }

    @Override
    public void onBackPressed() {
        Intent intent = new Intent();
        intent.putExtra("OrderInfo", orderInfo);
        intent.setClass(com.autosos.yd.view.PayActivity.this, OrderInfoActivity.class);
        intent.setFlags(Intent.FLAG_ACTIVITY_CLEAR_TOP);
        startActivity(intent);
        finish();
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
        MobclickAgent.onResume(this);
    }
    public void takePhoto(View view){
        if (((orderInfo.getPay_amount() > 0 && orderInfo.getIs_paid())
                || (orderInfo.getPay_amount() == 0))) {
            Intent intent = new Intent(com.autosos.yd.view.PayActivity.this, UploadPhotoActivity.class);
            intent.putExtra("OrderInfo", orderInfo);
            intent.putExtra("id", orderInfo.getId());
            startActivity(intent);
            finish();
            overridePendingTransition(R.anim.slide_in_right, R.anim.activity_anim_default);
        }
    }
}
