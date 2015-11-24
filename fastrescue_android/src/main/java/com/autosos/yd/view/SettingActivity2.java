package com.autosos.yd.view;

import android.content.Intent;
import android.os.Bundle;
import android.util.Log;
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
    private String mobile;
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_setting2);
        rlt_changepswd = (RelativeLayout) findViewById(R.id.rlt_changepswd);
        rlt_changephone = (RelativeLayout) findViewById(R.id.rlt_changephone);
        mobile = getIntent().getStringExtra("mobile");
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
                startActivityForResult(intent, 999);

//                startActivity(intent);
                overridePendingTransition(R.anim.slide_in_right, R.anim.activity_anim_default);
            }
        });
    }

    public void onBackPressed() {
        super.onBackPressed();
        overridePendingTransition(0, R.anim.slide_out_right);
    }

    @Override
    protected void onActivityResult(int requestCode, int resultCode, Intent data) {
        super.onActivityResult(requestCode, resultCode, data);
        Log.e("set", "requestCode === " + requestCode + "-------" + "resultCode === " + resultCode);
        String newMobile = data.getStringExtra("mobile");
        if (newMobile != null && newMobile.length() > 8){
            tv_number.setText(newMobile);
        }
    }
}
