package com.autosos.yd.widget;

import android.app.Activity;
import android.app.Dialog;
import android.content.Context;
import android.content.Intent;
import android.graphics.Point;
import android.net.Uri;
import android.provider.Settings;
import android.view.View;
import android.view.Window;
import android.view.WindowManager;
import android.widget.Button;
import android.widget.TextView;

import com.autosos.yd.R;
import com.autosos.yd.util.JSONUtil;

/**
 * Created by Administrator on 2015/8/8.
 */
public class DialogView implements View.OnClickListener{
    private Dialog dialog;
    public Button tvCancel;
    public Button tvConfirm;
    private boolean choose;
    public void dialog(int MsgText,int ConfirmText,int CancelText,Activity activity){
        View v2 = activity.getLayoutInflater().inflate(R.layout.dialog_msg_notice,
                null);
        dialog = new Dialog(v2.getContext(), R.style.bubble_dialog);
        tvConfirm = (Button) v2.findViewById(R.id.btn_notice_confirm);
        tvCancel = (Button) v2.findViewById(R.id.btn_notice_cancel);
        TextView tvMsg = (TextView) v2.findViewById(R.id.tv_notice_msg);
        tvMsg.setText(String.format(activity.getString(MsgText)));
        tvConfirm.setText(String.format(activity.getString(ConfirmText)));
        tvCancel.setText(String.format(activity.getString(CancelText)));
        dialog.setContentView(v2);
        dialog.setCanceledOnTouchOutside(false);
        Window window = dialog.getWindow();
        WindowManager.LayoutParams params = window.getAttributes();
        Point point = JSONUtil.getDeviceSize(activity);
        params.width = Math.round(point.x * 5 / 7);
        window.setAttributes(params);
        dialog.show();
    }
    public void dialog(int MsgText,int ConfirmText,Activity activity) {
        View v2 = activity.getLayoutInflater().inflate(R.layout.dialog_msg_content,
                null);
        dialog = new Dialog(v2.getContext(), R.style.bubble_dialog);
        Button tvConfirm = (Button) v2.findViewById(R.id.btn_notice_confirm);
        TextView tvMsg = (TextView) v2.findViewById(R.id.tv_notice_msg);
        tvMsg.setText(String.format(activity.getString(MsgText)));
        tvConfirm.setText(String.format(activity.getString(ConfirmText)));
        tvConfirm.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                dialog.dismiss();
                choose = true;
            }
        });
        dialog.setContentView(v2);
        dialog.setCanceledOnTouchOutside(false);
        Window window = dialog.getWindow();
        WindowManager.LayoutParams params = window.getAttributes();
        Point point = JSONUtil.getDeviceSize(activity);
        params.width = Math.round(point.x * 5 / 7);
        window.setAttributes(params);
        dialog.show();
    }
    public void dialogImage(int ConfirmText,Activity activity) {
        View v2 = activity.getLayoutInflater().inflate(R.layout.dialog_msg_qr_content,
                null);
        dialog = new Dialog(v2.getContext(), R.style.bubble_dialog);
        Button tvConfirm = (Button) v2.findViewById(R.id.btn_notice_confirm);
        tvConfirm.setText(String.format(activity.getString(ConfirmText)));
        tvConfirm.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                dialog.dismiss();
            }
        });
        dialog.setContentView(v2);
        dialog.setCanceledOnTouchOutside(false);
        Window window = dialog.getWindow();
        WindowManager.LayoutParams params = window.getAttributes();
        Point point = JSONUtil.getDeviceSize(activity);
        params.width = Math.round(point.x * 5 / 7);
        window.setAttributes(params);
        dialog.show();
    }
    public void dialogInternet(final Activity activity){
        View v2 = activity.getLayoutInflater().inflate(R.layout.dialog_msg_content,
                null);
        dialog = new Dialog(v2.getContext(), R.style.bubble_dialog);
        tvConfirm = (Button) v2.findViewById(R.id.btn_notice_confirm);
        TextView tvMsg = (TextView) v2.findViewById(R.id.tv_notice_msg);
        tvMsg.setText(String.format(activity.getString(R.string.msg_internet_error)));
        tvConfirm.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                dialog.dismiss();
                activity.startActivity(new Intent(Settings.ACTION_WIRELESS_SETTINGS));
                activity.overridePendingTransition(R.anim.slide_in_right, R.anim.activity_anim_default);
            }
        });
        dialog.setContentView(v2);
        dialog.setCanceledOnTouchOutside(false);
        Window window = dialog.getWindow();
        WindowManager.LayoutParams params = window.getAttributes();
        Point point = JSONUtil.getDeviceSize(activity);
        params.width = Math.round(point.x * 5 / 7);
        window.setAttributes(params);
        dialog.show();
    }

    @Override
    public void onClick(View v) {

    }
}
