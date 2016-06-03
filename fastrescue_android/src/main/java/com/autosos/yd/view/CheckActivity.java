package com.autosos.yd.view;

import android.app.Activity;
import android.app.Dialog;
import android.content.Intent;
import android.graphics.Point;
import android.net.Uri;
import android.os.Bundle;
import android.util.Log;
import android.view.View;
import android.view.Window;
import android.view.WindowManager;
import android.widget.Button;
import android.widget.TextView;

import com.autosos.yd.R;
import com.autosos.yd.util.JSONUtil;

/**
 * Created by Administrator on 2015/11/24.
 */
public class CheckActivity extends Activity {

    private Dialog dialog;
    private String mobile;
    private String username;
    private String password;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
//        requestWindowFeature(Window.FEATURE_NO_TITLE);
        setContentView(R.layout.activity_check);
//        setBackgroundVisibal();
        mobile = getIntent().getStringExtra("mobile");
        username = getIntent().getStringExtra("username");
        password = getIntent().getStringExtra("password");

    }

    public void showSuggest(View view){
        View inflate = getLayoutInflater().inflate(R.layout.dialog_msg_notice2,null);
        dialog = new Dialog(inflate.getContext(), R.style.bubble_dialog);
        Button tvConfirm ;
        Button tvCancel ;
        TextView tvMsg ;
        tvConfirm = (Button) inflate.findViewById(R.id.btn_notice_confirm);
        tvCancel = (Button) inflate.findViewById(R.id.btn_notice_cancel);
        tvCancel.setText("拨打");
        tvCancel.setTextColor(0xff31aa27);
        tvConfirm.setText("取消");
        tvMsg = (TextView) inflate.findViewById(R.id.tv_notice_msg);
        tvMsg.setText("拨打客服热线");
        tvCancel.setOnClickListener(new View.OnClickListener() {

            @Override
            public void onClick(View v) {
                Intent phoneIntent = new Intent(Intent.ACTION_DIAL,
                Uri.parse("tel:" + getString(R.string.label_support)));
                startActivity(phoneIntent);
                dialog.dismiss();
            }
        });
        tvConfirm.setOnClickListener(new View.OnClickListener() {

            @Override
            public void onClick(View v) {
                dialog.dismiss();
            }
        });
        dialog.setContentView(inflate);
        dialog.setCanceledOnTouchOutside(false);
        Window window = dialog.getWindow();
        WindowManager.LayoutParams params = window.getAttributes();
        Point point = JSONUtil.getDeviceSize(CheckActivity.this);
        params.width = Math.round(point.x * 5 / 7);
        window.setAttributes(params);
        dialog.show();
    }

    public void sendMessage(View view){
        Intent intent = new Intent(CheckActivity.this,FillCodeActivity.class);
        intent.putExtra("mobile",mobile);
        intent.putExtra("username", username);
        intent.putExtra("password", password);
        Log.e("log", "mobile === " + mobile);
        Log.e("log", "username === " + username);
        Log.e("log", "password === " + password);
        startActivity(intent);
    }
}
