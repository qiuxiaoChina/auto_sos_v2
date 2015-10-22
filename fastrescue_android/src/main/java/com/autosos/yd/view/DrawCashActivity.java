package com.autosos.yd.view;

import android.app.Activity;
import android.os.Bundle;
import android.util.Log;
import android.widget.TextView;

import com.autosos.yd.R;

/**
 * Created by Administrator on 2015/10/20.
 */
public class DrawCashActivity extends AutososBackActivity {


    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_draw_cash);
        //银行账号截取的方法
//        String account = "1234567891234567";
//        String a = account.substring(0,4);
//        String b = account.substring(4,8);
//        String c = account.substring(8,12);
//        String d = account.substring(12,account.length());
//        String f = a + " " + b + " " + c + " " + d;



    }



    public void onBackPressed() {
        super.onBackPressed();
        overridePendingTransition(0, R.anim.slide_out_right);
    }
}
