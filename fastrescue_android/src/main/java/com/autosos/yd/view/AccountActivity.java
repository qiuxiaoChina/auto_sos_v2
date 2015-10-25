package com.autosos.yd.view;

import android.content.Intent;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.os.AsyncTask;
import android.os.Bundle;


import android.os.Handler;
import android.os.Message;
import android.os.StrictMode;
import android.util.Log;
import android.view.View;
import android.widget.CompoundButton;
import android.widget.ImageView;
import android.widget.RelativeLayout;
import android.widget.ScrollView;
import android.widget.Switch;
import android.widget.TextView;

import com.autosos.yd.Constants;
import com.autosos.yd.R;
import com.autosos.yd.model.Balance;
import com.autosos.yd.util.JSONUtil;
import com.autosos.yd.util.UpdateStateServe;
import com.handmark.pulltorefresh.library.PullToRefreshBase;
import com.handmark.pulltorefresh.library.PullToRefreshScrollView;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import java.io.IOException;
import java.net.HttpURLConnection;
import java.net.URL;
import java.util.Timer;
import java.util.TimerTask;

/**
 * Created by Administrator on 2015/8/13.
 */
public class AccountActivity extends AutososBackActivity implements PullToRefreshBase.OnRefreshListener<ScrollView>{

    private String TAG = "AccountActivity";
    private PullToRefreshScrollView account_main;
    private ImageView iv_picture;
    private Bitmap bitmap;
    private TextView tv_balance;
    private Balance balance;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_account);
        StrictMode.setThreadPolicy(new StrictMode.ThreadPolicy.Builder().detectDiskReads().detectDiskWrites().detectNetwork().penaltyLog().build());
        StrictMode.setVmPolicy(new StrictMode.VmPolicy.Builder().detectLeakedSqlLiteObjects().detectLeakedClosableObjects().penaltyLog().penaltyDeath().build());
        iv_picture = (ImageView) findViewById(R.id.iv_picture);
        tv_balance = (TextView) findViewById(R.id.tv_balance);
        account_main = (PullToRefreshScrollView) findViewById(R.id.account_main);

        account_main.setOnRefreshListener(this);
        setBill();
        getBill().setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Intent intent = new Intent(AccountActivity.this, AccountOfMonthActivity.class);
                startActivity(intent);
                overridePendingTransition(R.anim.slide_in_right, R.anim.activity_anim_default);
            }
        });

    }

    @Override
    protected void onResume() {
        super.onResume();
        new GetAccountInfoTask().execute(Constants.GET_BALANCE_ONLY);
    }

    //
    private class GetAccountInfoTask extends AsyncTask<String, Object, JSONObject> {

        @Override
        protected JSONObject doInBackground(String... params) {
            try {
                String jsonStr = JSONUtil.getStringFromUrl(AccountActivity.this, params[0] );
                if (JSONUtil.isEmpty(jsonStr)){
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
            super.onPostExecute(result);//前后不知道一不一样，先试试
            account_main.onRefreshComplete();
            balance = new Balance(result);
            balance.getBalance();
            tv_balance.setText(balance.getBalance() + "");

        }
    }

    public void drawCash(View view){

        Intent intent = new Intent(AccountActivity.this,DrawCashActivity.class);
        intent.putExtra("balance",balance.getBalance());
        startActivity(intent);
        overridePendingTransition(R.anim.slide_in_right, R.anim.activity_anim_default);
    }

    public void onBackPressed() {
        super.onBackPressed();
        overridePendingTransition(0, R.anim.slide_out_right);
    }

    @Override
    public void onRefresh(PullToRefreshBase<ScrollView> refreshView) {
        new GetAccountInfoTask().execute(Constants.GET_BALANCE_ONLY);
    }
}
