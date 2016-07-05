package com.autosos.rescue.util;

import android.app.Notification;
import android.app.NotificationManager;
import android.app.PendingIntent;
import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.Intent;
import android.content.SharedPreferences;
import android.os.Build;
import android.os.Bundle;
import android.util.Log;

import com.autosos.rescue.R;
import com.autosos.rescue.view.MainActivity;
import com.igexin.sdk.PushConsts;

/**
 * Created by Administrator on 2016/5/27.
 */
public class GetuiSdkMsgReceiver extends BroadcastReceiver {
    @Override
    public void onReceive(Context context, Intent intent) {

        Bundle bundle = intent.getExtras();

        switch (bundle.getInt(PushConsts.CMD_ACTION)) {
            case PushConsts.GET_CLIENTID:

                String cid = bundle.getString("clientid");
                // TODO:处理cid返回
                Log.e("login",cid);
                context.getSharedPreferences("cid",Context.MODE_PRIVATE).edit().putString("cid",cid).commit();
                break;
            case PushConsts.GET_MSG_DATA:

                String taskid = bundle.getString("taskid");
                String messageid = bundle.getString("messageid");
                byte[] payload = bundle.getByteArray("payload");
                if (payload != null) {
                    String data = new String(payload);
                    // TODO:接收处理透传（payload）数据
                    Log.e("getui_result","个推信息----"+data);

                    if(data.indexOf("你的订单被取消了")>-1){

                        Log.e("getui_result","个推信息----"+"ok");
                        Intent intentFilter = new Intent();
                        intentFilter.setAction("closeOrder");
                        context.sendBroadcast(intentFilter);

                    }else if(data.indexOf("你的订单被改价了")>-1){

                        Log.d("editPrice","个推信息----"+"改价");
                        Intent intentFilter = new Intent();
                        intentFilter.setAction("editPrice");
                        context.sendBroadcast(intentFilter);


                    }else {
                        String title = "e救援服务商";
                        String content = "您有新订单啦";
                        NotificationManager manager = (NotificationManager) context
                                .getSystemService(Context.NOTIFICATION_SERVICE);
                        Intent i = new Intent(context, MainActivity.class);
                        i.putExtra("fromMsgReceiver", true);
                        i.putExtra("message", data);

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

                        notification.defaults = Notification.DEFAULT_LIGHTS;
                        notification.flags |= Notification.FLAG_AUTO_CANCEL | Notification
                                .FLAG_ONGOING_EVENT;

                        manager.notify(0, notification);

                        Intent intentFilter = new Intent();
                        intentFilter.setAction("newOrder");
                        intentFilter.putExtra("order", data);
                        context.sendBroadcast(intentFilter);
                    }

                }
                break;
            default:
                break;
        }

    }
}
