package com.autosos.yd.widget;

import android.app.Activity;
import android.app.Dialog;
import android.content.Context;
import android.content.Intent;
import android.content.pm.PackageManager;
import android.graphics.Point;
import android.net.Uri;
import android.os.Environment;
import android.os.Handler;
import android.os.Message;
import android.util.Log;
import android.view.View;
import android.view.Window;
import android.view.WindowManager;
import android.widget.Button;
import android.widget.ProgressBar;
import android.widget.TextView;
import android.widget.Toast;

import com.autosos.yd.Constants;
import com.autosos.yd.R;
import com.autosos.yd.model.Version;
import com.autosos.yd.util.JSONUtil;
import com.autosos.yd.util.UpdateStateServe;
import com.autosos.yd.util.XmlParse;
import com.autosos.yd.view.LoginActivity;

import java.io.File;
import java.io.FileOutputStream;
import java.io.InputStream;
import java.net.HttpURLConnection;
import java.net.URL;
import java.util.UUID;

/**
 * Created by Administrator on 2015/8/6.
 */
public class CherkVersion {
    private static com.autosos.yd.model.Version version;
    private int now_verCode;
    private final String TAG = "cherkVersion";
    private Context mcontext;
    private com.autosos.yd.model.Version v;
    private boolean flag;
    public String UpdateMessage = No_Update;
    public static final String Must_Update = "Must_Update";
    public static final String Can_Update = "Can_Update";
    public static final String No_Update = "No_Update";
    public String cherkVersion(Context context){
        UpdateMessage = No_Update;
        this.mcontext= context;
        flag = true;
        try {
            now_verCode = mcontext.getPackageManager().getPackageInfo("com.autosos.yd", 0).versionCode;
        }catch (PackageManager.NameNotFoundException e) {
            Log.e(TAG, e.getMessage());
        }
        Log.e(TAG, "now_verCode:" + now_verCode);
        return getContent();
    }

    public String getContent(){
        long time = System.currentTimeMillis() / 1000;
        String path = "https://dn-autosos.qbox.me/autosos.xml"+"?v="+time;
        v = new com.autosos.yd.model.Version();
        try {
            URL url = new URL(path);
            HttpURLConnection conn = (HttpURLConnection) url.openConnection();
            conn.setRequestMethod("GET");
            conn.setReadTimeout(6 * 1000);
            if (conn.getResponseCode() == 200) {
                InputStream is = conn.getInputStream();
                v = XmlParse.parseXml(is);
                if((v.getVerCode() > now_verCode&&!Constants.DEBUG) ||(v.getDebug_versioncode() > now_verCode && Constants.DEBUG)){
                    UpdateMessage = Must_Update;
                }
                else if(v.getCanUpdateVersion() > now_verCode){
                    UpdateMessage = Can_Update;
                }
                else{
                    UpdateMessage = No_Update;
                }
                Log.e(TAG,v.getUpdate_data() + v.getDebug_versioncode());
                flag = false;
            }
        }catch (Exception e){
            Log.e(TAG,e.toString());
        }

        return UpdateMessage;
    }
    public Version getVersion(){
        return v;
    }
}
