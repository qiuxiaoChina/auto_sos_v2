package com.autosos.yd.view;

import android.content.Intent;
import android.os.Bundle;
import android.view.View;
import android.widget.RelativeLayout;
import android.widget.TextView;

import com.autosos.yd.R;

/**
 * Created by Administrator on 2015/11/20.
 */
public class SettingActivity2 extends AutososBackActivity {
    private RelativeLayout rlt_changepswd;
    private RelativeLayout rlt_changephone;
    private TextView tv_number;
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_setting2);
        rlt_changepswd = (RelativeLayout) findViewById(R.id.rlt_changepswd);
        rlt_changephone = (RelativeLayout) findViewById(R.id.rlt_changephone);
        tv_number = (TextView) findViewById(R.id.tv_number);
        tv_number.setText(getIntent().getStringExtra("mobile"));
        rlt_changepswd.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Intent intent = new Intent(SettingActivity2.this, PasswordActivity.class);
                startActivity(intent);
                overridePendingTransition(R.anim.slide_in_right, R.anim.activity_anim_default);
            }
        });
        rlt_changephone.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Intent intent = new Intent(SettingActivity2.this, MobileActivity.class);
                startActivity(intent);
                overridePendingTransition(R.anim.slide_in_right, R.anim.activity_anim_default);
            }
        });
    }

    public void onBackPressed() {
        super.onBackPressed();
        overridePendingTransition(0, R.anim.slide_out_right);
    }
}
