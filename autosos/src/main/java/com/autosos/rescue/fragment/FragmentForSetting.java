package com.autosos.rescue.fragment;


import android.app.Activity;
import android.app.Dialog;
import android.content.Context;
import android.content.Intent;
import android.content.pm.PackageManager;
import android.graphics.Point;
import android.net.Uri;
import android.os.Bundle;
import android.os.Handler;
import android.os.Message;
import android.support.v4.app.Fragment;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.view.Window;
import android.view.WindowManager;
import android.widget.Button;
import android.widget.TextView;

import com.autosos.rescue.Constants;
import com.autosos.rescue.R;
import com.autosos.rescue.model.Version;
import com.autosos.rescue.task.NewHttpPostTask;
import com.autosos.rescue.task.OnHttpRequestListener;
import com.autosos.rescue.util.CherkVersion;
import com.autosos.rescue.util.JSONUtil;
import com.autosos.rescue.util.Session;
import com.autosos.rescue.util.XmlParse;
import com.autosos.rescue.view.AboutUsActivity;
import com.autosos.rescue.view.FeedBackActivity;
import com.autosos.rescue.view.LoginActivity;
import com.autosos.rescue.view.SplashActivity;

import org.json.JSONException;
import org.json.JSONObject;

import java.io.InputStream;
import java.net.HttpURLConnection;
import java.net.URL;


/**
 * A simple {@link Fragment} subclass.
 */
public class FragmentForSetting extends Fragment implements View.OnClickListener {

    private static FragmentForSetting fragment = null;

    public static Fragment newInstance() {
        if (fragment == null) {
            synchronized (FragmentForSetting.class) {
                if (fragment == null) {
                    fragment = new FragmentForSetting();

                }
            }
        }
        return fragment;
    }


    public FragmentForSetting() {
        // Required empty public constructor
    }

    private View logout, checkVersion,feedBack,contact_us,about_us;
    private Handler handler;

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        // Inflate the layout for this fragment
        View view = inflater.inflate(R.layout.fragment_fragment_for_setting, null);
        logout = view.findViewById(R.id.logout);
        logout.setOnClickListener(this);
        checkVersion = view.findViewById(R.id.checkVersion);
        checkVersion.setOnClickListener(this);
        feedBack = view.findViewById(R.id.feedBack);
        feedBack.setOnClickListener(this);
        contact_us = view.findViewById(R.id.contact_us);
        contact_us.setOnClickListener(this);

        about_us = view.findViewById(R.id.about_us);
        about_us.setOnClickListener(this);

        handler = new Handler(){

            @Override
            public void handleMessage(Message msg) {

                if (msg.what == 0) {
                    CherkVersion cherkVersion = new CherkVersion();
                    //updateMessage = getContent(getActivity().getApplicationContext());
                    new Thread(runnable).start();
                    Log.d("new_version",updateMessage);
                   // version = cherkVersion.getVersion();
                    if(updateMessage.equals(CherkVersion.Must_Update) || updateMessage.equals(CherkVersion.Can_Update)){
                        showDialog("Install");
                    }else {

                        showDialog("NoUpdate");
                    }
                }

            }
        };
        return view;
    }

    Runnable runnable = new Runnable() {
        @Override
        public void run() {

          updateMessage = getContent(getActivity().getApplicationContext());
        }
    };

    private String getContent(Context context){
        long time = System.currentTimeMillis() / 1000;
        String path = "https://dn-autosos.qbox.me/autosos_v2.xml"+"?v="+time;
        Version v = new Version();
        int now_verCode=0;
        try {
            now_verCode = context.getPackageManager().getPackageInfo("com.autosos.rescue", 0).versionCode;
        }catch (PackageManager.NameNotFoundException e) {

        }
        String UpdateMessage="";
        try {
            URL url = new URL(path);
            HttpURLConnection conn = (HttpURLConnection) url.openConnection();
            conn.setRequestMethod("GET");
            conn.setReadTimeout(6 * 1000);
            if (conn.getResponseCode() == 200) {
                InputStream is = conn.getInputStream();
                v = XmlParse.parseXml(is);
                if((v.getVerCode() > now_verCode&&!Constants.DEBUG) ||(v.getDebug_versioncode() > now_verCode && Constants.DEBUG)){
                    UpdateMessage = "Must_Update";
                }
                else if(v.getCanUpdateVersion() > now_verCode){
                    UpdateMessage = "Can_Update";
                }
                else{
                    UpdateMessage = "No_Update";
                }

            }
        }catch (Exception e){
            Log.d("new_version",e.toString());
            UpdateMessage = "No_Update";
            return UpdateMessage;
        }

        return UpdateMessage;
    }


    String updateMessage = "";
    private Version version;
    @Override
    public void onClick(View v) {

        switch (v.getId()) {

            case R.id.logout:
                new NewHttpPostTask(getActivity().getApplicationContext(), new OnHttpRequestListener() {
                    @Override
                    public void onRequestCompleted(Object obj) {

                        JSONObject jsonObject = null;
                        try {
                            jsonObject = new JSONObject(obj.toString());
                            int result = jsonObject.getInt("result");
                            if (result == 1) {

                                Session.getInstance().logout(getActivity().getApplicationContext());
                                Intent intent = new Intent(getActivity().getApplicationContext(), LoginActivity.class);
                                startActivity(intent);
                                getActivity().finish();
                            }
                        } catch (JSONException e) {
                            e.printStackTrace();
                        }
                    }

                    @Override
                    public void onRequestFailed(Object obj) {

                    }
                }
                ).execute(Constants.USER_LOGOUT_URL);
                break;
            case R.id.checkVersion:
                handler.sendEmptyMessage(0);

                break;
            case R.id.feedBack:
                Intent i = new Intent(getActivity().getApplicationContext(), FeedBackActivity.class);
                startActivity(i);
                break;
            case R.id.contact_us:
                Intent intent = new Intent(Intent.ACTION_CALL, Uri.parse("tel:"+"4008820019"));
                startActivity(intent);
                break;
            case  R.id.about_us:
                Intent ii = new Intent(getActivity().getApplicationContext(), AboutUsActivity.class);
                startActivity(ii);
                break;
            default:
                break;
        }
    }

   private Dialog dialog;
    private void showDialog(String type){
        String msg =null;
        View v2 = getActivity().getLayoutInflater().inflate(R.layout.dialog_msg_content,
                null);
        dialog = new Dialog(v2.getContext(), R.style.bubble_dialog);
        Button tvConfirm = (Button) v2.findViewById(R.id.btn_notice_confirm);
        TextView tvMsg = (TextView) v2.findViewById(R.id.tv_notice_msg);
        switch (type){
            case "NoUpdate":
                msg = "为最新版本,版本号"+Constants.APP_VERSION;
                tvMsg.setText(msg);
                tvConfirm.setOnClickListener(new View.OnClickListener() {
                    @Override
                    public void onClick(View v) {
                        dialog.dismiss();

                    }
                });
                break;
            case "Install":
                msg = "有新的版本,请重启APP后更新";
                tvMsg.setText(msg);
                tvConfirm.setOnClickListener(new View.OnClickListener() {
                    @Override
                    public void onClick(View v) {
                        dialog.dismiss();
                        Intent i = new Intent(getActivity().getApplicationContext(),SplashActivity.class);
                        startActivity(i);
                        getActivity().finish();

                    }
                });
                break;
        }

        dialog.setContentView(v2);
        dialog.setCanceledOnTouchOutside(false);
        Window window = dialog.getWindow();
        WindowManager.LayoutParams params = window.getAttributes();
        Point point = JSONUtil.getDeviceSize(getActivity());
        params.width = Math.round(point.x * 5 / 7);
        window.setAttributes(params);
        dialog.show();
    }
}
