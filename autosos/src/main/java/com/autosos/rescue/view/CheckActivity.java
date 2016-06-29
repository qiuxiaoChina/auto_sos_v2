package com.autosos.rescue.view;

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
import android.widget.ImageView;
import android.widget.TextView;

import com.autosos.rescue.R;
import com.autosos.rescue.util.JSONUtil;

/**
 * Created by Administrator on 2015/11/24.
 */
public class CheckActivity extends Activity implements View.OnClickListener{

    private String mobile;
    private String username;
    private String password;
    private ImageView back_button;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
//        requestWindowFeature(Window.FEATURE_NO_TITLE);
        setContentView(R.layout.activity_check);
//        setBackgroundVisibal();
        mobile = getIntent().getStringExtra("mobile");
        username = getIntent().getStringExtra("username");
        password = getIntent().getStringExtra("password");
        back_button = (ImageView) findViewById(R.id.back_button);
        back_button.setOnClickListener(this);

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
        finish();
    }

    @Override
    public void onClick(View v) {
        switch (v.getId()){
            case R.id.back_button:
                finish();
                break;
            default:
                break;
        }
    }
}
