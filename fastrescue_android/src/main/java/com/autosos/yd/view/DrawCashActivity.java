package com.autosos.yd.view;

import android.app.Activity;
import android.app.Dialog;
import android.content.DialogInterface;
import android.content.Intent;
import android.graphics.Point;
import android.os.AsyncTask;
import android.os.Bundle;
import android.util.Log;
import android.view.View;
import android.view.Window;
import android.view.WindowManager;
import android.widget.Button;
import android.widget.EditText;
import android.widget.TextView;
import android.widget.Toast;

import com.autosos.yd.Constants;
import com.autosos.yd.R;
import com.autosos.yd.model.BankInfo;
import com.autosos.yd.task.AsyncBitmapDrawable;
import com.autosos.yd.task.ImageLoadTask;
import com.autosos.yd.util.JSONUtil;
import com.makeramen.rounded.RoundedImageView;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import java.io.IOException;
import java.util.HashMap;
import java.util.Map;

/**
 * Created by Administrator on 2015/10/20.
 */
public class DrawCashActivity extends AutososBackActivity {
    private String TAG = "DrawCashActivity";
    private BankInfo bankInfo;
//    private RoundedImageView bank_icon;
    private String logo_url;
    private TextView tv_bank_name;
    private String bankNumber;
    private TextView tv_bank_number;
    private TextView et_money;
    private String balance;
    private View empty;
    private View progressBar;
    private Button bt_takemoney;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_draw_cash);
//        bank_icon = (RoundedImageView) findViewById(R.id.bank_icon);
        tv_bank_name = (TextView) findViewById(R.id.tv_bank_name);
        tv_bank_number = (TextView) findViewById(R.id.tv_bank_number);
        et_money = (TextView) findViewById(R.id.et_money);
        balance = getIntent().getStringExtra("balance");
        et_money.setText(balance);
        empty = findViewById(R.id.empty);
        progressBar = findViewById(R.id.progressBar);
        bt_takemoney = (Button) findViewById(R.id.bt_takemoney);
    }



    public void takeMoney(View view){
//        bt_takemoney.setVisibility(View.GONE);
        bt_takemoney.setClickable(false);
        Map<String, Object> map = new HashMap<>();
        if (Constants.DEBUG){
            map.put("money", 10);
        }else {
            map.put("money", balance);
        }

        Log.e("drawcash", "balance === "+balance);
        progressBar.setVisibility(View.VISIBLE);
        new com.autosos.yd.task.NewHttpPostTask(DrawCashActivity.this, new com.autosos.yd.task.OnHttpRequestListener() {
            @Override
            public void onRequestCompleted(Object obj) {
                try {
                    JSONObject jsonObject = new JSONObject(obj.toString());
                    Log.e("ChangPSD", jsonObject.toString());
                    if (!jsonObject.isNull("result")) {
                        int result = jsonObject.optInt("result");
                        if(result == 1){
                            Log.e("drawcash", "failed");
                            progressBar.setVisibility(View.GONE);
                            showDrawCashDialog(R.string.msg_enter_cash_success);
                        }else{
                            Log.e("drawcash", "success");
                            String msg = jsonObject.optString("msg");
                            findViewById(R.id.progressBar).setVisibility(View.GONE);
                            showDrawCashDialog(R.string.msg_enter_cash_failed);
                            Toast.makeText(DrawCashActivity.this, msg, Toast.LENGTH_LONG).show();
                        }
                    }
                }catch (Exception e ){
                    e.printStackTrace();
                }
            }

            @Override
            public void onRequestFailed(Object obj) {
                Log.e("drawcash", "failed");

            }
        }).execute(String.format(Constants.TAKECASH), map);
//        bt_takemoney.setVisibility(View.VISIBLE);

    }

    public void showDrawCashDialog(final int msg){
        View v2 = getLayoutInflater().inflate(R.layout.dialog_msg_content,
                null);
        final Dialog dialog = new Dialog(v2.getContext(), R.style.bubble_dialog);
        Button tvConfirm = (Button) v2.findViewById(R.id.btn_notice_confirm);
        TextView tvMsg = (TextView) v2.findViewById(R.id.tv_notice_msg);
        tvMsg.setText(String.format(getString(msg)));
        tvConfirm.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                dialog.dismiss();
                bt_takemoney.setClickable(true);
                if(msg == R.string.msg_enter_cash_success){
                    Intent intent = new Intent(DrawCashActivity.this,AccountActivity.class);
                    startActivity(intent);
                    overridePendingTransition(R.anim.slide_in_right, R.anim.activity_anim_default);
                }
            }
        });
        dialog.setContentView(v2);
        dialog.setCanceledOnTouchOutside(false);
        Window window = dialog.getWindow();
        WindowManager.LayoutParams params = window.getAttributes();
        Point point = JSONUtil.getDeviceSize(DrawCashActivity.this);
        params.width = Math.round(point.x * 5 / 7);
        window.setAttributes(params);
        dialog.show();
        dialog.setOnCancelListener(new DialogInterface.OnCancelListener() {
            @Override
            public void onCancel(DialogInterface dialog) {

            }
        });
    }

    @Override
    protected void onResume() {
        super.onResume();
        new GetAccountInfoTask().execute(Constants.GET_BANK_INFO);

    }

    private class GetAccountInfoTask extends AsyncTask<String,Void,JSONObject>{

        @Override
        protected JSONObject doInBackground(String... params) {
            try {
                String jsonStr = JSONUtil.getStringFromUrl(DrawCashActivity.this, params[0]);
                if (JSONUtil.isEmpty(jsonStr)) {
                    return null;
                }
                Log.e(TAG, jsonStr);
                return new JSONObject(jsonStr);
            } catch (IOException | JSONException e) {

            }
            return null;
        }

        @Override
        protected void onPostExecute(JSONObject result) {
            super.onPostExecute(result);
            progressBar.setVisibility(View.GONE);
            bankInfo = new BankInfo(result);
            logo_url = bankInfo.getLogo();
            bankNumber = bankInfo.getCard_no();
            String a = bankNumber.substring(0,4);
            String b = bankNumber.substring(4,8);
            String c = bankNumber.substring(8,12);
            String d = bankNumber.substring(12,bankNumber.length());
            String f = a + " " + b + " " + c + " " + d;
            tv_bank_number.setText(f);
            tv_bank_name.setText(bankInfo.getName());
//            ImageLoadTask task = new ImageLoadTask(bank_icon, null,0);
//            AsyncBitmapDrawable image = new AsyncBitmapDrawable(getResources(), Constants.PLACEHOLDER_AVATAR2, task);
//            task.loadImage(logo_url, 180, com.autosos.yd.util.ScaleMode.WIDTH, image);

//            progressBar.setVisibility(View.GONE);
            empty.setVisibility(View.GONE);
        }

    }

    public void onBackPressed() {
        super.onBackPressed();
        overridePendingTransition(0, R.anim.slide_out_right);
    }
}
