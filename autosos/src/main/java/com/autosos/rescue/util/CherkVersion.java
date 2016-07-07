package com.autosos.rescue.util;

import android.content.Context;
import android.content.pm.PackageManager;
import android.util.Log;


import com.autosos.rescue.Constants;
import com.autosos.rescue.model.Version;

import java.io.InputStream;
import java.net.HttpURLConnection;
import java.net.URL;

/**
 * Created by Administrator on 2015/8/6.
 */
public class CherkVersion {
    private static Version version;
    private int now_verCode;
    private final String TAG = "cherkVersion";
    private Context mcontext;
    private Version v;
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
            now_verCode = mcontext.getPackageManager().getPackageInfo("com.autosos.rescue", 0).versionCode;
        }catch (PackageManager.NameNotFoundException e) {
            Log.e(TAG, e.getMessage());
        }
        Log.e(TAG, "now_verCode:" + now_verCode);
        return getContent();
    }

    public String getContent(){
        long time = System.currentTimeMillis() / 1000;
        String path = "https://dn-autosos.qbox.me/autosos_v2.xml"+"?v="+time;
        v = new Version();
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
