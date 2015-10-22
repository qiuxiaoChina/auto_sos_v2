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

import com.autosos.yd.R;
import com.autosos.yd.util.JSONUtil;
import com.autosos.yd.util.UpdateStateServe;
import com.handmark.pulltorefresh.library.PullToRefreshBase;
import com.handmark.pulltorefresh.library.PullToRefreshScrollView;

import org.json.JSONArray;

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

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_account);
        StrictMode.setThreadPolicy(new StrictMode.ThreadPolicy.Builder().detectDiskReads().detectDiskWrites().detectNetwork().penaltyLog().build());
        StrictMode.setVmPolicy(new StrictMode.VmPolicy.Builder().detectLeakedSqlLiteObjects().detectLeakedClosableObjects().penaltyLog().penaltyDeath().build());
        iv_picture = (ImageView) findViewById(R.id.iv_picture);
        account_main = (PullToRefreshScrollView) findViewById(R.id.account_main);
        account_main.setOnRefreshListener(this);

//        new Thread(downloadRun).start();

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

//    Runnable downloadRun = new Runnable() {
//
//        @Override
//        public void run() {
//            // TODO Auto-generated method stub
//            try {
//                URL url = new URL("http://www.qqai.net/fa/UploadPic/2012-7/20127417475611762.jpg"); //path图片的网络地址
//                HttpURLConnection httpURLConnection = (HttpURLConnection) url.openConnection();
//                if(httpURLConnection.getResponseCode() == HttpURLConnection.HTTP_OK){
//                    bitmap  = BitmapFactory.decodeStream(httpURLConnection.getInputStream());
//
//                    System.out.println("加载网络图片完成");
//                }else{
//                    System.out.println("加载网络图片失败");
//                }
//            } catch (IOException e) {
//                e.printStackTrace();
//            }
//            Message msg = new Message();
//                msg.what = 1;
//                handler.sendMessage(msg);
//
//        }
//    };
//        private Handler handler = new Handler(){
//
//        @Override
//        public void handleMessage(Message msg) {
//            super.handleMessage(msg);
//            if (msg.what == 1){
//                iv_picture.setImageBitmap(bitmap);
//            }
//        }
//    };


    private class GetAccountInfoTask extends AsyncTask<String, Object, JSONArray> {

        @Override
        protected JSONArray doInBackground(String... params) {
            try {
                String jsonStr = JSONUtil.getStringFromUrl(AccountActivity.this, params[0] );
                if (JSONUtil.isEmpty(jsonStr)){
                    return null;
                }
                Log.e(TAG, jsonStr);
            } catch (IOException e) {
                e.printStackTrace();
            }
            return null;
        }

        @Override
        protected void onPostExecute(JSONArray jsonArray) {
            super.onPostExecute(jsonArray);//前后不知道一不一样，先试试


        }
    }

    public void drawCash(View view){
        Intent intent = new Intent(AccountActivity.this,DrawCashActivity.class);
        startActivity(intent);
        overridePendingTransition(R.anim.slide_in_right, R.anim.activity_anim_default);
    }

    public void onBackPressed() {
        super.onBackPressed();
        overridePendingTransition(0, R.anim.slide_out_right);
    }

    @Override
    public void onRefresh(PullToRefreshBase<ScrollView> refreshView) {
            account_main.onRefreshComplete();
    }
}
