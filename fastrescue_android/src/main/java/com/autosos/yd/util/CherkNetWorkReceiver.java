package com.autosos.yd.util;

import android.app.ActivityManager;
import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.Intent;
import android.net.ConnectivityManager;
import android.net.NetworkInfo;
import android.os.Bundle;
import android.os.PowerManager;
import android.util.Log;
import android.widget.Toast;

import com.autosos.yd.R;

import java.util.List;

/**
 * Created by Administrator on 2015/9/9.
 */
public class CherkNetWorkReceiver extends BroadcastReceiver {
    public static int sendMessage = 0;
    private final String TAG = "Up BrodcasReceiver";
    @Override
    public void onReceive(Context context, Intent intent) {
        if (intent.getAction().equals(Intent.ACTION_TIME_TICK)) {
            sendMessage ++;
            Log.e(TAG,"1 min pass");
            if(sendMessage > 2){
                Intent i = new Intent(context, UpdateStateServe.class);
                i.setFlags(Intent.FLAG_ACTIVITY_NEW_TASK);
                context.startService(i);
            }
            else{
                Log.e(TAG,"serve  ok  -- !");
            }
        }
        else{
             ActivityManager  activityManager = (ActivityManager) context.getSystemService(context.ACTIVITY_SERVICE);
            List<ActivityManager.RunningServiceInfo> mserviceTasks = activityManager.getRunningServices(30);
            boolean serveractive = false;
            for (ActivityManager.RunningServiceInfo serinfo : mserviceTasks)
            {
               // Log.e("TaskServerInfo", serinfo.process+"("+serinfo.pid+")" + serinfo.clientPackage +"--"+serinfo.lastActivityTime);
                if("com.autosos.yd".equals(serinfo.process) || //在这里判断服务是否存活，还未找到方法
                        true){
                    serveractive = true;
                }
            }
            if(!serveractive){
                Log.e(TAG,"fuwu chong qi!");
                Intent i = new Intent(context, UpdateStateServe.class);
                i.setFlags(Intent.FLAG_ACTIVITY_NEW_TASK);
                context.startService(i);
            }
        }
    }
}
