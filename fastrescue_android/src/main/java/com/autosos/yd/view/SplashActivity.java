package com.autosos.yd.view;

import android.app.Activity;
import android.app.Dialog;
import android.content.Context;
import android.content.Intent;
import android.content.SharedPreferences;
import android.content.pm.PackageManager;
import android.graphics.Point;
import android.graphics.drawable.TransitionDrawable;
import android.net.Uri;
import android.os.Bundle;
import android.os.Environment;
import android.os.Handler;
import android.os.Message;
import android.text.format.Time;
import android.util.Config;
import android.util.Log;
import android.view.View;
import android.view.Window;
import android.view.WindowManager;
import android.widget.Button;
import android.widget.ImageView;
import android.widget.ProgressBar;
import android.widget.TextView;
import android.widget.Toast;

import com.autosos.yd.fragment.WorkFragment;
import com.autosos.yd.util.JSONUtil;
import com.autosos.yd.util.UpdateStateServe;
import com.autosos.yd.util.XmlParse;
import com.autosos.yd.widget.CatchException;
import com.autosos.yd.widget.CherkInternet;
import com.autosos.yd.widget.CherkVersion;
import com.autosos.yd.widget.DialogView;
import com.baidu.location.BDLocation;
import com.baidu.location.BDLocationListener;
import com.baidu.location.LocationClient;
import com.baidu.location.LocationClientOption;
import com.igexin.sdk.PushManager;

import com.autosos.yd.Constants;
import com.autosos.yd.R;
import com.autosos.yd.model.User;
import com.autosos.yd.util.Session;
import com.autosos.yd.view.LoginActivity;

import junit.runner.Version;

import org.apache.http.protocol.HTTP;

import java.io.File;
import java.io.FileOutputStream;
import java.io.InputStream;
import java.net.HttpURLConnection;
import java.net.URL;
import java.util.UUID;

public class SplashActivity extends Activity {
    private static com.autosos.yd.model.Version version;
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
        setContentView(R.layout.activity_splash);

        splashActivity = this;
        Intent startIntent = new Intent(SplashActivity.this, UpdateStateServe.class);//开启心跳服务
        startService(startIntent);

        CatchException catchException = CatchException.getInstance();//开启异常捕捉
        catchException.init(getApplicationContext());
        if(Constants.DEBUG){
            ((TextView)findViewById(R.id.debug)).setText(R.string.label_debug);
        }
        version = new com.autosos.yd.model.Version();
        version.setVerCode(0);
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
        PushManager.getInstance().initialize(splashActivity);
        TransitionDrawable transitionDrawable = (TransitionDrawable) getResources().
                getDrawable(R.drawable.splash_drawable);
        ((ImageView) findViewById(R.id.imageView)).setImageDrawable(transitionDrawable);
        progress_textView = (TextView)findViewById(R.id.progress_text);
        transitionDrawable.startTransition(800);
        ((TextView)findViewById(R.id.version)).setText(Constants.APP_VERSION + "");
        if (Constants.DEBUG && false) {
            loadLogin();
        }
        else if(!CherkInternet.cherkInternet(SplashActivity.this)){
            DialogView dialogView = new DialogView();
            dialogView.dialogInternet(SplashActivity.this);
        }
        else{
            new Thread(new Runnable() {
                @Override
                public void run() {
                    CherkVersion cherkVersion = new CherkVersion();
                    updateMessage = cherkVersion.cherkVersion(SplashActivity.this);
                    version = cherkVersion.getVersion();
                    Message message = new Message();
                    if(updateMessage.equals(CherkVersion.Must_Update)){
                        message.what = 0;
                    }
                    else if(updateMessage.equals(CherkVersion.No_Update)){
                        message.what = 1;
                    }
                    else if(updateMessage.equals(CherkVersion.Can_Update)){
                        message.what = -1;
                    }
                    handler.sendMessage(message);
                }
            }).start();
            if(Constants.DEBUG){
                ((TextView)findViewById(R.id.version)).setText("just test !");
            }
        }
    }

    public void setupHosts() {
        // 从配置文件中提取 host 值
        SharedPreferences preferences = getSharedPreferences(
                Constants.PREF_FILE, Context.MODE_PRIVATE);
        String host = preferences.getString("HOST", Constants.HOST);
        Constants.setHOST(host);
    }


    Handler handler = new Handler() {
        @Override
        public void handleMessage(Message msg) {
            super.handleMessage(msg);
            switch (msg.what){
                case -1:
                    showUpdaeDialog(-1);
                    break;
                case 0:
                    showUpdaeDialog(0);
                    break;
                case 1:
                    loadLogin();
                    break;
                case 2:
                    int progress = (int )msg.obj;
                    progressBarView.setProgress(progress);
                    progress_textView.setText("" + progress + "%");
                    //Log.e(TAG, "progress" + "%");
                    if(progress>98) {
                        progress_textView.setVisibility(View.GONE);
                    }
                    break;
                case 3:
                    Log.e(TAG, "progress" + "ok");
                    progressBarView.setVisibility(View.GONE);
                    Session.getInstance().logout(SplashActivity.this);
                    installApk();
                    break;
                case 4:
                    Toast.makeText(SplashActivity.this,R.string.msg_update_error,Toast.LENGTH_LONG).show();
            }
        }
    };

    private void showUpdaeDialog(int type){
        View v2 = getLayoutInflater().inflate(R.layout.dialog_msg_notice,
                null);
        dialog = new Dialog(v2.getContext(), R.style.bubble_dialog);
        Button tvConfirm ;
        Button tvCancel ;
        TextView tvMsg ;
        if(type == 0){
            v2 = getLayoutInflater().inflate(R.layout.dialog_msg_update,
                    null);
            dialog = new Dialog(v2.getContext(), R.style.bubble_dialog);
            tvConfirm = (Button) v2.findViewById(R.id.btn_notice_confirm);
            tvMsg = (TextView) v2.findViewById(R.id.update_msg);
            tvMsg.setText(version.getUpdate_data());
            tvCancel = (Button) v2.findViewById(R.id.btn_notice_cancel);
            (v2.findViewById(R.id.update_close)).setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View v) {
                    dialog.dismiss();
                    exitDialog();
                }
            });
            tvCancel.setOnClickListener(new View.OnClickListener() {

                @Override
                public void onClick(View v) {
                    dialog.dismiss();
                    exitDialog();
                }
            });
        }
        else {
            dialog = new Dialog(v2.getContext(), R.style.bubble_dialog);
            tvConfirm = (Button) v2.findViewById(R.id.btn_notice_confirm);
            tvCancel = (Button) v2.findViewById(R.id.btn_notice_cancel);
            tvMsg = (TextView) v2.findViewById(R.id.tv_notice_msg);
            tvMsg.setText(String.format(getString(R.string.msg_can_update)));
            tvCancel.setOnClickListener(new View.OnClickListener() {

                @Override
                public void onClick(View v) {
                    dialog.dismiss();
                    loadLogin();
                }
            });
        }

        tvConfirm.setOnClickListener(new View.OnClickListener() {

            @Override
            public void onClick(View v) {
                dialog.dismiss();
                progressBarView.setVisibility(View.VISIBLE);
                download();
            }
        });


        dialog.setContentView(v2);
        dialog.setCanceledOnTouchOutside(false);
        Window window = dialog.getWindow();
        WindowManager.LayoutParams params = window.getAttributes();
        Point point = JSONUtil.getDeviceSize(SplashActivity.this);
        params.width = Math.round(point.x * 5 / 7);
        window.setAttributes(params);
        dialog.show();
    }
    private void exitDialog(){
        View v2 = getLayoutInflater().inflate(R.layout.dialog_msg_content,
                null);
        dialog = new Dialog(v2.getContext(), R.style.bubble_dialog);
        Button tvConfirm = (Button) v2.findViewById(R.id.btn_notice_confirm);
        TextView tvMsg = (TextView) v2.findViewById(R.id.tv_notice_msg);
        tvMsg.setText(String.format(getString(R.string.msg_update_cancel)));
        tvConfirm.setOnClickListener(new View.OnClickListener() {

            @Override
            public void onClick(View v) {
                dialog.dismiss();
                finish();
            }
        });
        dialog.setContentView(v2);
        dialog.setCanceledOnTouchOutside(false);
        Window window = dialog.getWindow();
        WindowManager.LayoutParams params = window.getAttributes();
        Point point = JSONUtil.getDeviceSize(SplashActivity.this);
        params.width = Math.round(point.x * 5 / 7);
        window.setAttributes(params);
        dialog.show();
    }
    private void loadLogin(){
        new Handler().postDelayed(new Runnable() {
            public void run() {
                if (isFinishing()) {
                    return;
                }
                Intent i = new Intent(com.autosos.yd.view.SplashActivity.this, LoginActivity.class);
                com.autosos.yd.view.SplashActivity.this.startActivity(i);
                com.autosos.yd.view.SplashActivity.this.finish();
                overridePendingTransition(R.anim.fade_in, R.anim.fade_out);
            }
        }, 2000);
    }
    private void download(){
        Runnable runnable= new Runnable() {
            @Override
            public void run() {
              try{
                  UUID uuid  =  UUID.randomUUID();
                  String s = UUID.randomUUID().toString();
                  version.setUrl(version.getUrl()+"?v="+s);
                  URL url = new URL(version.getUrl());
                  HttpURLConnection conn = (HttpURLConnection) url.openConnection();
                  conn.connect();
                  int length = conn.getContentLength();
                  InputStream is =conn.getInputStream();
                  File file =new File(Environment.getExternalStorageDirectory()+"/com.autosos.jd/");
                  if(!file.exists()){
                      file.mkdir();
                  }
                  String apkFile = Environment.getExternalStorageDirectory()+"/com.autosos.jd/"+version.getName()+".apk";
                  APKFile = new File(apkFile);
                  FileOutputStream fos = new FileOutputStream(APKFile);
                  int count = 0;
                  byte buff[] = new byte[1024];
                  while(true){
                      int numread = is.read(buff);
                      count += numread;
                      int progress =(int)(((float)count / length) * 100);
                      Message msg = new Message();
                      msg.what = 2;
                      msg.obj = progress;
                      handler.sendMessage(msg);
                       //Handler.sendEmptyMessage(DOWN_UPDATE);
                      if(numread <= 0){
                          Message msg2 = new Message();
                          msg2.what = 3;
                          handler.sendMessage(msg2);
                          break;
                      }
                      fos.write(buff,0,numread);
                  }//while(!interceptFlag);
                    fos.close();
                    is.close();
              }catch (Exception e){
                  Log.e(TAG,"ERRor"+e.toString());
                  Message msg2 = new Message();
                  msg2.what = 4;
                  handler.sendMessage(msg2);
              }
            }
        };
        new Thread(runnable).start();
    }
    private void installApk() {
        Intent intent = new Intent();
        intent.setAction(Intent.ACTION_VIEW);
        intent.setFlags(Intent.FLAG_ACTIVITY_NEW_TASK);
        intent.setDataAndType(Uri.fromFile(APKFile), "application/vnd.android.package-archive");
        startActivity(intent);
        android.os.Process.killProcess(android.os.Process.myPid());
    }

}
