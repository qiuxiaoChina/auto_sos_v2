package com.autosos.yd.view;

import android.os.Bundle;


import com.umeng.analytics.MobclickAgent;

import com.autosos.yd.R;
import com.autosos.yd.model.OrderInfo;
import com.autosos.yd.view.*;
import com.autosos.yd.view.AutososBackActivity;

public class MapNavActivity extends AutososBackActivity {
    private OrderInfo orderInfo;
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_pay_info);

    }

    @Override
    public void onBackPressed() {
        super.onBackPressed();
        overridePendingTransition(0, R.anim.slide_out_right);
    }

    @Override
    protected void onPause() {
        super.onPause();
        MobclickAgent.onPause(this);
    }

    @Override
    protected void onResume() {
        super.onResume();
        MobclickAgent.onResume(this);
    }

}
