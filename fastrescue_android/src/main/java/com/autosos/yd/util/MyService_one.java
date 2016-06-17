package com.autosos.yd.util;

import android.app.ActivityManager;
import android.app.AlarmManager;
import android.app.Notification;
import android.app.PendingIntent;
import android.app.Service;
import android.content.Context;
import android.content.Intent;
import android.os.IBinder;
import android.os.PowerManager;
import android.support.annotation.Nullable;
import android.util.Log;

import com.autosos.yd.R;
import com.autosos.yd.view.MainActivity;

import java.util.ArrayList;
import java.util.Timer;
import java.util.TimerTask;

public class MyService_one extends Service {


    private static final int NOTIFICATION_ID = 1; // 如果id设置为0,会导致不能设置为前台service

    PowerManager.WakeLock wakeLock = null;
    public MyService_one() {
    }

    @Override
    public void  onCreate() {
        // TODO: Return the communication channel to the service.

        super.onCreate();
        acquireWakeLock(MyService_one.this);
        Log.d("MyTag", "onCreate");

    }

    public void acquireWakeLock(Context context) {
        if (null == wakeLock) {
            PowerManager pm = (PowerManager) context.getSystemService(Context.POWER_SERVICE);
            wakeLock = pm.newWakeLock(PowerManager.PARTIAL_WAKE_LOCK
                    | PowerManager.ON_AFTER_RELEASE, context.getClass()
                    .getCanonicalName());
            if (null != wakeLock) {
                Log.e("MyTag", "call acquireWakeLock");
                wakeLock.acquire();
            }
        }
    }
    //释放设备电源锁
    private void releaseWakeLock()
    {
        if (null != wakeLock)
        {
            wakeLock.release();
            wakeLock = null;
        }
    }

    @Override
    public int onStartCommand(Intent intent, int flags, int startId) {

        Notification.Builder builder = new Notification.Builder(this);
        PendingIntent contentIntent = PendingIntent.getActivity(this, 0,
                new Intent(this, MainActivity.class), 0);
        builder.setContentIntent(contentIntent);
        builder.setSmallIcon(R.drawable.icon_marker);
        builder.setTicker("Foreground Service Start");
        builder.setContentTitle("Foreground Service");
        builder.setContentText("Make this service run in the foreground.");
        Notification notification = builder.build();

        startForeground(NOTIFICATION_ID, notification);

        //创建Intent对象，action为ELITOR_CLOCK，附加信息为字符串“你该打酱油了”
        Intent intent1 = new Intent("ELITOR_CLOCK");
        intent1.putExtra("msg", "你该打酱油了");

        //定义一个PendingIntent对象，PendingIntent.getBroadcast包含了sendBroadcast的动作。
        //也就是发送了action 为"ELITOR_CLOCK"的intent
        PendingIntent pi = PendingIntent.getBroadcast(this, 0, intent1, 0);

        //AlarmManager对象,注意这里并不是new一个对象，Alarmmanager为系统级服务
        AlarmManager am = (AlarmManager) getSystemService(ALARM_SERVICE);

        //设置闹钟从当前时间开始，每隔5s执行一次PendingIntent对象pi，注意第一个参数与第二个参数的关系
        // 5秒后通过PendingIntent pi对象发送广播
        am.setRepeating(AlarmManager.RTC_WAKEUP, System.currentTimeMillis(), 5*1000, pi);
       // new Thread(runnable).start();
        return super.onStartCommand(intent, START_STICKY, startId);
    }

    @Nullable
    @Override
    public IBinder onBind(Intent intent) {
        return null;
    }




    @Override
    public void onDestroy() {
        super.onDestroy();
        Log.d("MyTag", "onDestroy");
        releaseWakeLock();
        stopForeground(true);
        Intent intent = new Intent(this,MyService_one.class);
        startService(intent);
    }
    Runnable runnable = new Runnable() {
        @Override
        public void run() {
            Timer timer = new Timer();
            TimerTask task = new TimerTask() {

                @Override
                public void run() {
                    Log.e("MyTag", "ServiceOne Run: "+ System.currentTimeMillis());
                    boolean b = isServiceWorked(MyService_one.this, "qiuxiao.com.servicedemo.MyService_two");
                    if(!b) {
                        Intent service = new Intent(MyService_one.this, MyService_two.class);
                        startService(service);
                        Log.e("MyTag", "Start ServiceTwo");
                    }
                }
            };
            timer.schedule(task, 0, 1000);
        }
    };



    public static boolean isServiceWorked(Context context, String serviceName) {
        ActivityManager myManager = (ActivityManager) context.getSystemService(Context.ACTIVITY_SERVICE);
        ArrayList<ActivityManager.RunningServiceInfo> runningService = (ArrayList<ActivityManager.RunningServiceInfo>) myManager.getRunningServices(Integer.MAX_VALUE);
        for (int i = 0; i < runningService.size(); i++) {
            if (runningService.get(i).service.getClassName().toString().equals(serviceName)) {
                return true;
            }
        }
        return false;
    }


}
