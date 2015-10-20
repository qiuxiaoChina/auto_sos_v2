package com.autosos.yd.util;

import android.app.Notification;
import android.app.NotificationManager;
import android.app.PendingIntent;
import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.Intent;
import android.net.Uri;
import android.os.Build;
import android.os.Bundle;
import android.os.Environment;
import android.telephony.TelephonyManager;
import android.util.Log;

import com.igexin.sdk.PushConsts;

import org.json.JSONException;
import org.json.JSONObject;

import com.autosos.yd.Constants;
import com.autosos.yd.R;
import com.autosos.yd.util.*;
import com.autosos.yd.util.JSONUtil;
import com.autosos.yd.view.MainActivity;

import java.io.BufferedWriter;
import java.io.File;
import java.io.FileOutputStream;
import java.io.IOException;
import java.io.OutputStreamWriter;
import java.text.SimpleDateFormat;
import java.util.Date;

public class GetuiSdkMsgReceiver extends BroadcastReceiver {
    public static boolean playMusic = false;
    public static  int msg = 0;
    public static String mycid;
    @Override
    public void onReceive(Context context, Intent intent) {
            String mylocaluid = context.getSharedPreferences(Constants.PREF_FILE, Context.MODE_PRIVATE).getString("clientid", null);
            mycid = intent.getExtras().getString("clientid");

            if (mycid != null){
                if (mycid != mylocaluid){
                    Log.e("Up yoyo","mycid ====  " + mycid + "  ||   " + "mylocaluid ===== "+mylocaluid );
                    Bundle mybundle = intent.getExtras();
                    mycid = mybundle.getString("clientid");
                    context.getSharedPreferences(Constants.PREF_FILE,Context.MODE_PRIVATE).edit().putString("clientid",mycid).commit();
                }
            }

//            if( mylocaluid == null || mylocaluid != mycid ){
//                Log.e("Up yoyo2","mycid ====  " + mycid + "  ||   " + "mylocaluid ===== "+mylocaluid );
//                Bundle mybundle = intent.getExtras();
//                mycid = mybundle.getString("clientid");
//                context.getSharedPreferences(Constants.PREF_FILE,Context.MODE_PRIVATE).edit().putString("clientid",mycid).commit();

//                Log.e("Up yoyo","------");
//                String mylocaluid = context.getSharedPreferences(Constants.PREF_FILE, Context.MODE_PRIVATE).getString("clientid", null);
//                mycid = intent.getExtras().getString("clientid");
//                if(mylocaluid == null){
//                    Bundle mybundle = intent.getExtras();
//                    mycid = mybundle.getString("clientid");
//                    context.getSharedPreferences(Constants.PREF_FILE,Context.MODE_PRIVATE).edit().putString("clientid",mycid).commit();
                //注释掉的代码是之前出    现问题offline的代码
//            }





        if (Session.getInstance().getCurrentUser(context) == null) {
            return;
        }
        Bundle bundle = intent.getExtras();
        switch (bundle.getInt(PushConsts.CMD_ACTION)) {
            case PushConsts.GET_MSG_DATA:
                byte[] payload = bundle.getByteArray("payload");
                if (payload != null) {
                    String data = new String(payload);
                    try {
                        JSONObject jsonObject = new JSONObject(data);
                        int msg_type = jsonObject.optInt("msg_type");
                        String env = jsonObject.optString("env");
                        if (msg_type == 1 && !jsonObject.isNull("data") && (env.equals("dev")&&Constants.DEBUG)||(env.equals("prod")&&!Constants.DEBUG)) {
                            GetuiWrite(data.toString());
                            String title = "e救援服务商";
                            String content = "您有新订单啦";
                            NotificationManager manager = (NotificationManager) context
                                    .getSystemService(Context.NOTIFICATION_SERVICE);
                            Intent i = new Intent(context, com.autosos.yd.view.MainActivity.class);
                            i.putExtra("fromMsgReceiver",true);
                            i.putExtra("message", data);
                            Log.e("MMMMMMMMMMMM", data.toString() + "isplaying "+playMusic);

                            PendingIntent pendingIntent = PendingIntent
                                    .getActivity(context, 0, i,
                                            PendingIntent.FLAG_UPDATE_CURRENT);
                            Notification notification;
                            Notification.Builder builder = new Notification.Builder(context);
                            builder.setContentIntent(pendingIntent)
                                    .setSmallIcon(R.drawable.icon_logo)
                                    .setTicker(content).setContentTitle(title)
                                    .setContentText(content);
                            if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.JELLY_BEAN) {
                                notification = builder.build();
                            } else {
                                notification = builder.getNotification();
                            }

                           /* notification.sound = Uri.parse("android.resource://" +
                                   "com.autosos.yd" + "/" + R.raw.order_new);
                                   */
                            notification.defaults = Notification.DEFAULT_LIGHTS;
                            notification.flags |= Notification.FLAG_AUTO_CANCEL | Notification
                                    .FLAG_ONGOING_EVENT;

                            manager.notify(0, notification);
                            if(!playMusic &&UpdateStateServe.Setting_Sound_OnOff) {
                                playMusic = true;
                                MusicUtil musicUtil = new MusicUtil();
                                musicUtil.musicPlayer(data, context);
                            }
                            else{
                                new Thread(new Runnable(){
                                    public void run(){
                                        try {
                                            Thread.sleep(7000);
                                            playMusic = false;
                                        }catch (Exception e ){
                                            e.printStackTrace();
                                            playMusic = false;
                                        }
                                    }

                                }).start();
                            }
                        }

                    } catch (JSONException e) {
                        e.printStackTrace();
                    }
                }
                break;
            case PushConsts.GET_CLIENTID:
                String cid = bundle.getString("clientid");
                if (com.autosos.yd.util.JSONUtil.isEmpty(cid)) {
                    break;
                }
                String cidFromLocal = context.getSharedPreferences(Constants.PREF_FILE,
                        Context.MODE_PRIVATE).getString("clientid", null);
                if (JSONUtil.isEmpty(cidFromLocal)) {
                    // 只有本地没有 cid 的时候才去发送 cid 在服务器注册
                    context.getSharedPreferences(Constants.PREF_FILE,
                            Context.MODE_PRIVATE).edit().putString("clientid", cid).commit();
                }
                break;
            default:
                break;
        }
    }
    public void GetuiWrite(String content) {
        try {
            File file = new File( Environment.getExternalStorageDirectory()+"/com.autosos.jd/getui.txt");
            if(!file.exists()){
                file.createNewFile();
            }
            BufferedWriter fileOutputStream  =    new BufferedWriter(new OutputStreamWriter(
                    new FileOutputStream(file, true)));
            if (fileOutputStream != null) {
                long now_time = System.currentTimeMillis() / 1000;
                SimpleDateFormat sdf = new SimpleDateFormat("yyyy-MM-dd HH:mm:ss");
                String date = sdf.format(new Date(now_time * 1000L));
                fileOutputStream.write(date +"**************************************" + content);
                fileOutputStream.flush();
                fileOutputStream.close();
            }
        } catch (IOException e) {
            e.printStackTrace();
        }
    }
}
