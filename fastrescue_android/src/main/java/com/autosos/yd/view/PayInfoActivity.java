package com.autosos.yd.view;

import android.content.Intent;
import android.os.Bundle;
import android.os.Handler;
import android.view.View;
import android.widget.Button;
import android.widget.RelativeLayout;
import android.widget.TextView;

import com.autosos.yd.model.Pay;
import com.autosos.yd.util.MusicUtil;
import com.umeng.analytics.MobclickAgent;

import com.autosos.yd.R;
import com.autosos.yd.model.OrderInfo;
import com.autosos.yd.view.*;
import com.autosos.yd.view.AutososBackActivity;

public class PayInfoActivity extends AutososBackActivity {
    private OrderInfo orderInfo;
    private Button take_photoView;
    private TextView statusView;
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_pay_info);
        take_photoView= (Button)findViewById(R.id.take_photo);
        orderInfo = (OrderInfo) getIntent().getSerializableExtra("OrderInfo");
        statusView = (TextView)findViewById(R.id.status);
        boolean free = getIntent().getBooleanExtra("free", false);
        if (!free) {
            TextView statusView = (TextView) findViewById(R.id.status);
            statusView.setText(R.string.label_server_pay_ok);
        }
        MusicUtil.playmusics(PayInfoActivity.this, MusicUtil.Take_photo_send);
        TextView typeView = (TextView) findViewById(R.id.type);
        if (orderInfo.getService_type() == 1) {
            typeView.setText(R.string.label_service_type1);
        } else if (orderInfo.getService_type() == 2) {
            typeView.setText(R.string.label_service_type2);
        } else {
            typeView.setText(R.string.label_service_type3);
        }
        take_photoView.setVisibility(View.VISIBLE);
        TextView costView = (TextView) findViewById(R.id.cost);
        if(orderInfo.getIs_own_expense() == 0 && free){
            ((RelativeLayout)findViewById(R.id.cost_layout)).setVisibility(View.INVISIBLE);
            statusView.setText(R.string.label_not_owner_payed);
        }
        else
            costView.setText(String.valueOf(orderInfo.getPay_amount()));
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
    public void takePhoto(View view){
        if (orderInfo.getPay_amount() == 0 || orderInfo.getIs_paid()) {
            Intent intent = new Intent(com.autosos.yd.view.PayInfoActivity.this, UploadPhotoActivity.class);
            intent.putExtra("OrderInfo", orderInfo);

            intent.putExtra("id", orderInfo.getId());
            startActivity(intent);
            finish();
            overridePendingTransition(R.anim.slide_in_right, R.anim.activity_anim_default);
        }
    }
}
