package com.autosos.rescue.view;

import android.app.Activity;
import android.app.Dialog;
import android.content.ActivityNotFoundException;
import android.content.Context;
import android.content.Intent;
import android.content.SharedPreferences;
import android.graphics.Point;
import android.graphics.drawable.TransitionDrawable;
import android.location.LocationManager;
import android.net.ConnectivityManager;
import android.net.NetworkInfo;
import android.net.Uri;
import android.os.Bundle;
import android.os.Environment;
import android.os.Handler;
import android.os.Message;
import android.provider.Settings;
import android.util.Log;
import android.view.View;
import android.view.Window;
import android.view.WindowManager;
import android.widget.Button;
import android.widget.ImageView;
import android.widget.ProgressBar;
import android.widget.TextView;
import android.widget.Toast;

import com.autosos.rescue.Constants;
import com.autosos.rescue.R;
import com.igexin.sdk.PushManager;

import java.io.File;
import java.io.FileOutputStream;
import java.io.InputStream;
import java.net.HttpURLConnection;
import java.net.URL;
import java.util.UUID;

public class SplashActivity extends Activity {
    private int now_verCode;
    private final String TAG = "SpalashActivity";
    private Dialog dialog;
    private ProgressBar progressBarView;
    private File APKFile;
    private TextView progress_textView;
    public static Activity splashActivity;
    //private boolean interceptFlag = true;
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        PushManager.getInstance().initialize(this.getApplicationContext());
        setContentView(R.layout.activity_splash);
//        SharedPreferences sp = getSharedPreferences("online",Context.MODE_PRIVATE);
//        SharedPreferences.Editor editor = sp.edit();
//        editor.remove("online").commit();

        SharedPreferences sp1 = getSharedPreferences("working",Context.MODE_PRIVATE);
        SharedPreferences.Editor editor1 = sp1.edit();
        editor1.remove("working").commit();
        if(Constants.DEBUG){
            ((TextView)findViewById(R.id.debug)).setText(R.string.label_debug);
        }
        progressBarView =(ProgressBar)findViewById(R.id.progressBar_download);
        progressBarView.setVisibility(View.GONE);

    }
    private String updateMessage;
    @Override
    protected void onResume() {
        super.onResume();
        // 在这里重置 HOSTS
        if (Constants.DEBUG) {
            setupHosts();
        }
        PushManager.getInstance().initialize(this);
        TransitionDrawable transitionDrawable = (TransitionDrawable) getResources().
                getDrawable(R.drawable.splash_drawable);
        ((ImageView) findViewById(R.id.imageView)).setImageDrawable(transitionDrawable);
        progress_textView = (TextView)findViewById(R.id.progress_text);
        transitionDrawable.startTransition(800);
        ((TextView)findViewById(R.id.version)).setText(Constants.APP_VERSION + "");
        loadLogin();
    }

    public void setupHosts() {
        // 从配置文件中提取 host 值
        SharedPreferences preferences = getSharedPreferences(
                Constants.PREF_FILE, Context.MODE_PRIVATE);
        String host = preferences.getString("HOST", Constants.HOST);
        Constants.setHOST(host);
    }




    private void loadLogin(){
        new Handler().postDelayed(new Runnable() {
            public void run() {
                if (isFinishing()) {
                    return;
                }
                if(cherkGPSandNetWork()){

                    Intent i = new Intent(SplashActivity.this, LoginActivity.class);
                    startActivity(i);
                    overridePendingTransition(R.anim.fade_in, R.anim.fade_out);
                    finish();
                }
            }
        }, 2000);
    }

    private void installApk() {
        Intent intent = new Intent();
        intent.setAction(Intent.ACTION_VIEW);
        intent.setFlags(Intent.FLAG_ACTIVITY_NEW_TASK);
        intent.setDataAndType(Uri.fromFile(APKFile), "application/vnd.android.package-archive");
        startActivity(intent);
        android.os.Process.killProcess(android.os.Process.myPid());
    }



    public  boolean cherkInternet(Context context) {
        ConnectivityManager cwjManager = (ConnectivityManager) context.getSystemService(Context.CONNECTIVITY_SERVICE);
        if (cwjManager != null){
            NetworkInfo info = cwjManager.getActiveNetworkInfo();
            if (info != null && info.isConnected()){
//                if (info.getState() == NetworkInfo.State.CONNECTED) {
//                    return true;
//                }
                if(info.getType()==ConnectivityManager.TYPE_WIFI){

                    return true;
                }else {

                    if(info.getType()==ConnectivityManager.TYPE_MOBILE){

                        return true;
                    }
                }
            }
        }
        return false;
    }

    private boolean cherkGPSandNetWork() {
        boolean GPS_status = ((LocationManager) getSystemService(Context.LOCATION_SERVICE)).isProviderEnabled(LocationManager.GPS_PROVIDER);

        if (cherkInternet(this) == false) {
            Toast.makeText(SplashActivity.this, String.format(getString(R.string.msg_noInterner)), Toast.LENGTH_SHORT).show();
            Intent intent = new Intent();
            intent.setAction(Settings.ACTION_WIRELESS_SETTINGS);
            intent.setFlags(Intent.FLAG_ACTIVITY_NEW_TASK);
            try {
                startActivity(intent);

            } catch (ActivityNotFoundException ex) {

                // The Android SDK doc says that the location settings activity
                // may not be found. In that case show the general settings.
                // General settings activity
                intent.setAction(Settings.ACTION_SETTINGS);
                try {
                    startActivity(intent);
                } catch (Exception e) {
                }
            }
            return false;

        } else if (GPS_status == false) {
            Toast.makeText(SplashActivity.this, String.format(getString(R.string.msg_noGPS)), Toast.LENGTH_SHORT).show();
            Intent intent = new Intent();
            intent.setAction(Settings.ACTION_LOCATION_SOURCE_SETTINGS);
            intent.setFlags(Intent.FLAG_ACTIVITY_NEW_TASK);
            try {
                startActivity(intent);

            } catch (ActivityNotFoundException ex) {

                // The Android SDK doc says that the location settings activity
                // may not be found. In that case show the general settings.
                // General settings activity
                intent.setAction(Settings.ACTION_SETTINGS);
                try {
                    startActivity(intent);
                } catch (Exception e) {
                }
            }
            return false;
        }
        return true;
    }

}
