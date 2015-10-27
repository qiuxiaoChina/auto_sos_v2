package com.autosos.yd.view;

import android.app.Dialog;
import android.content.Context;
import android.content.Intent;
import android.content.SharedPreferences;
import android.graphics.Point;
import android.os.AsyncTask;
import android.os.Bundle;
import android.os.Handler;
import android.text.Editable;
import android.text.TextWatcher;
import android.util.Log;
import android.view.MotionEvent;
import android.view.View;
import android.view.Window;
import android.view.WindowManager;
import android.view.inputmethod.InputMethodManager;
import android.widget.Button;
import android.widget.EditText;
import android.widget.ImageView;
import android.widget.TextView;
import android.widget.Toast;

import com.autosos.yd.util.DistanceUtil;
import com.autosos.yd.util.Location;
import com.autosos.yd.util.MusicUtil;
import com.autosos.yd.util.UpdateStateServe;
import com.autosos.yd.widget.CherkInternet;
import com.autosos.yd.widget.DialogView;
import com.umeng.analytics.MobclickAgent;

import org.json.JSONException;
import org.json.JSONObject;

import java.io.IOException;
import java.math.BigDecimal;
import java.util.HashMap;
import java.util.Map;

import com.autosos.yd.Constants;
import com.autosos.yd.R;
import com.autosos.yd.model.OrderInfo;
import com.autosos.yd.task.NewHttpPutTask;
import com.autosos.yd.task.OnHttpRequestListener;
import com.autosos.yd.util.JSONUtil;
import com.autosos.yd.util.Utils;
import com.autosos.yd.view.AutososBackActivity;
import com.autosos.yd.view.MapDragActivity;
import com.autosos.yd.view.OrderInfoActivity;

public class DragActivity extends com.autosos.yd.view.AutososBackActivity {
    private String trance;
    private View progressBar;
    private TextView priceView;
    private long price;
    private double distance = 0;
    private com.autosos.yd.model.OrderInfo orderInfo;
    private Dialog dialog;
    private TextView statusView;
    private TextView carDistanceView;
    private TextView realPrice;
    private TextView startDragPriceView2;
    private TextView startDragPriceView;
    private TextView outofDistanceView;
    private Button sure_freeView;
    private TextView baoxianView;
    private TextView baoxianView2;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_drag);
        orderInfo = (com.autosos.yd.model.OrderInfo) getIntent().getSerializableExtra("OrderInfo");
        trance = getIntent().getStringExtra("trance");
        progressBar = findViewById(R.id.progressBar);
        statusView = (TextView) findViewById(R.id.status);
        Log.e("DragActivity","11");
        realPrice = (TextView)findViewById(R.id.drag_real_price2);
        priceView = (TextView) findViewById(R.id.price);
        carDistanceView = (TextView) findViewById(R.id.drag_distance);
        startDragPriceView = (TextView)findViewById(R.id.drag_start_price);
        startDragPriceView2 = (TextView)findViewById(R.id.drag_start_price_2);
        outofDistanceView = (TextView)findViewById(R.id.out_of_free);
        sure_freeView = (Button)findViewById(R.id.sure_free);
        baoxianView = (TextView)findViewById(R.id.baoxian);
        baoxianView2 = (TextView)findViewById(R.id.baoxian_2);

        distance=getIntent().getDoubleExtra("tot_distance", 0.00);
        String result = String .format("%.2f", distance / 1000);
        distance =Double.parseDouble(result);
        if(orderInfo.getTuoche_distance()>0){
            showResult();
        }
        else {
            requestTuocheTask();
        }
    }

    public void onSubmit(View v) {
        if (progressBar.getVisibility() == View.VISIBLE) {
            return;
        }
        if (this.getCurrentFocus() != null) {
            ((InputMethodManager) getSystemService(INPUT_METHOD_SERVICE))
                    .hideSoftInputFromWindow(this.getCurrentFocus()
                                    .getWindowToken(),
                            InputMethodManager.HIDE_NOT_ALWAYS);
        }
        if(!CherkInternet.cherkInternet(DragActivity.this)){
            DialogView dialogView = new DialogView();
            dialogView.dialogInternet(this);
            return;
        }
    }

    public void requestTuocheTask() {

        SharedPreferences sharedPreferences = this.getSharedPreferences("Location", Context.MODE_PRIVATE);
        SharedPreferences.Editor editor = sharedPreferences.edit();
        editor.putFloat("Location_drag_distance", 0);
        editor.commit();
        Location location = new Location();
        location.deleteJWD(Location.path_drag, DragActivity.this);

        progressBar.setVisibility(View.VISIBLE);
        carDistanceView.setText(String.format(getString(R.string.label_kilometre_s),
                distance));
        Map<String, Object> map = new HashMap<>();
        map.put("total_distance", String.valueOf(distance));
        map.put("trace_data",trance);
        new com.autosos.yd.task.NewHttpPutTask(DragActivity.this, new com.autosos.yd.task.OnHttpRequestListener() {
            @Override
            public void onRequestCompleted(Object obj) {
                progressBar.setVisibility(View.GONE);
                try {
                    JSONObject jsonObject = new JSONObject(obj.toString());
                    if (!jsonObject.isNull("result")) {
                        int result = jsonObject.optInt("result");
                        if (result == 1) {
                            Log.e("DrageActivity",jsonObject.toString());
                            statusView.setText(R.string.msg_drag_ok);
                            new GetOrderInfoTask().executeOnExecutor(Constants.INFOTHEADPOOL,
                                    String.format(Constants.HISTORY_ORDER_INFO_URL, orderInfo.getId()));
                        } else {
                            if (!jsonObject.isNull("msg")) {
                                String msg = jsonObject.optString("msg");
                                Toast.makeText(DragActivity.this, msg, Toast.LENGTH_SHORT).show();
                            }
                        }
                    }

                } catch (JSONException e) {
                }
            }

            @Override
            public void onRequestFailed(Object obj) {

            }
        }).execute(String.format(com.autosos.yd.Constants.SUBMIT_FEE_URL, orderInfo.getId()), map);
    }

    private class GetOrderInfoTask extends AsyncTask<String, Object, JSONObject> {

        @Override
        protected JSONObject doInBackground(String... params) {
            try {
                String jsonStr = JSONUtil.getStringFromUrl(com.autosos.yd.view.DragActivity.this,params[0]);
                if (JSONUtil.isEmpty(jsonStr)) {
                    return null;
                }
                Log.e("DragActivity","11"+ jsonStr);
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
                showResult();
            }
        }
    }


    private void showResult(){
        findViewById(R.id.empty_view).setVisibility(View.GONE);
        progressBar.setVisibility(View.GONE);
        //显示拖车结果
        if (orderInfo.getTuoche_distance() > 0) {
            MusicUtil.playmusics(DragActivity.this, MusicUtil.After_help_pay);
            statusView.setText(R.string.msg_drag_ok);
            carDistanceView.setText(String.format(getString(R.string.label_kilometre_s),
                    orderInfo.getTuoche_distance()));
          /*  if (orderInfo.getTuoche_distance() > orderInfo.getStarting_km()) {
                price = (int) (Math.ceil(orderInfo.getTuoche_distance() - orderInfo.getStarting_km()) *
                        orderInfo.getKm_price()) + orderInfo.getPrice();
            }
            else*/
                price = orderInfo.getPay_amount();
            priceView.setText(String.valueOf(price));
            realPrice.setText(String.valueOf(orderInfo.getTotal_amount()));

            outofDistanceView.setText(String.format(getString(R.string.label_rmb_s),
                    orderInfo.getTuoche_distance() - orderInfo.getStarting_km() > 0
                            ? new BigDecimal(""+Math.ceil((orderInfo.getTuoche_distance() - orderInfo.getStarting_km())) * orderInfo.getKm_price()).stripTrailingZeros() : 0.00));
            //以上这个方法可以把8.0这个.0去掉，new BigDecimal("11.0").stripTrailingZeros()
            startDragPriceView2.setText(String.format(getString(R.string.label_car_fee_service2),orderInfo.getPrice()));
            startDragPriceView.setText(String.format(getString(R.string.label_car_fee_service1),orderInfo.getStarting_km()));
            if(orderInfo.getIs_own_expense() == 1){
                baoxianView.setVisibility(View.GONE);
                baoxianView2.setVisibility(View.GONE);
            }
            else{
                baoxianView.setText(orderInfo.getInsurance_name() + String.format(getString(R.string.label_baoxian), orderInfo.getTuoche_free_km()) );
                baoxianView2.setText(String.format(getString(R.string.label_car_fee_service2),-(orderInfo.getTotal_amount() - orderInfo.getPay_amount())));
            }
        } else {
            statusView.setText(R.string.msg_type_distance);
        }
    }


    public void onDismiss(View view) {
        if (this.getCurrentFocus() != null) {
            ((InputMethodManager) getSystemService(INPUT_METHOD_SERVICE))
                    .hideSoftInputFromWindow(this.getCurrentFocus()
                                    .getWindowToken(),
                            InputMethodManager.HIDE_NOT_ALWAYS);
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
        MobclickAgent.onResume(this);
    }

    public void onDown(View view){
        if(!CherkInternet.cherkInternet(DragActivity.this)){
            DialogView dialogView = new DialogView();
            dialogView.dialogInternet(this);
            return;
        }
        else {
            new GetPayInfoTask().executeOnExecutor(Constants.INFOTHEADPOOL,
                    String.format(Constants.HISTORY_ORDER_INFO_URL, orderInfo.getId()));
            Log.e("DragActivity", "id:" + orderInfo.getId());
        }
    }

    private class GetPayInfoTask extends AsyncTask<String, Object, JSONObject> {

        @Override
        protected JSONObject doInBackground(String... params) {
            try {
                String jsonStr = JSONUtil.getStringFromUrl(DragActivity.this,params[0]);
                if (JSONUtil.isEmpty(jsonStr)) {
                    return null;
                }
                Log.e("DragActivity", jsonStr);
                return new JSONObject(jsonStr);
            } catch (IOException | JSONException e) {
            }
            return null;
        }

        @Override
        protected void onPostExecute(JSONObject result) {
            progressBar.setVisibility(View.GONE);
            sure_freeView.setEnabled(true);
            orderInfo = new OrderInfo(result);
            if(orderInfo == null){
                return;
            }
            if (orderInfo.getIs_completed() == 1) {
                //完成救援
                Intent intent = null;
                if (orderInfo.getPay_amount() > 0) {
                    //需要支付费用
                    if (orderInfo.getIs_paid()) {
                        intent = new Intent(DragActivity.this, PayInfoActivity.class);
                        intent.putExtra("OrderInfo", orderInfo);
                        intent.putExtra("free", false);
                        if(orderInfo.getIs_own_expense() == 1) {
                            intent.putExtra("free", true);
                        }
                    } else {
                        //未支付
                        if (orderInfo.getIs_cash() == 1) {
                            //go代付
                            intent = new Intent(DragActivity.this, PayActivity.class);
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
                    intent = new Intent(DragActivity.this, PayInfoActivity.class);
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
    public void showToastView(int resId,int type) {
        if (dialog != null && dialog.isShowing()) {
            return;
        }
        dialog = new Dialog(DragActivity.this, R.style.bubble_dialog);
        View view;
        if(type == 2)
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
        Point point = JSONUtil.getDeviceSize(DragActivity.this);
        params.width = Math.round(point.x * 5 / 7);
        window.setAttributes(params);
        dialog.show();
    }
}
