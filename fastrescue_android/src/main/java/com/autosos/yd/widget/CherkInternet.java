package com.autosos.yd.widget;

import android.app.Activity;
import android.content.Context;
import android.net.ConnectivityManager;
import android.net.NetworkInfo;
import android.os.Message;
import android.widget.Toast;

import com.autosos.yd.R;
import com.baidu.location.LocationClient;

import java.util.Timer;
import java.util.TimerTask;

/**
 * Created by Administrator on 2015/8/10.
 */
public class CherkInternet {
    public static boolean cherkInternet(boolean toast, Activity activity) {
        ConnectivityManager cwjManager = (ConnectivityManager) activity.getSystemService(Context.CONNECTIVITY_SERVICE);
        if (cwjManager != null){
            NetworkInfo info = cwjManager.getActiveNetworkInfo();
            if (info != null && info.isConnected()){
                if (info.getState() == NetworkInfo.State.CONNECTED) {
                    TimerTask task = new TimerTask(){
                        public void run(){
                        }
                    };
                    Timer timer = new Timer();
                    timer.schedule(task, 2000);
                    return true;
                }
            }
        }
        if(toast)
            Toast.makeText(activity, R.string.msg_net_disconnected, Toast.LENGTH_SHORT).show();
        return false;
    }
    public static boolean cherkInternet(Context context) {
        ConnectivityManager cwjManager = (ConnectivityManager) context.getSystemService(Context.CONNECTIVITY_SERVICE);
        if (cwjManager != null){
            NetworkInfo info = cwjManager.getActiveNetworkInfo();
            if (info != null && info.isConnected()){
                if (info.getState() == NetworkInfo.State.CONNECTED) {
                    return true;
                }
            }
        }
        return false;
    }
}
