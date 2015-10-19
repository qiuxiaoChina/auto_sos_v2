package com.autosos.yd.view;

import android.content.Intent;
import android.os.Bundle;
import android.util.Log;
import android.view.View;
import android.widget.CompoundButton;
import android.widget.RelativeLayout;
import android.widget.Switch;

import com.autosos.yd.R;
import com.autosos.yd.util.UpdateStateServe;

/**
 * Created by Administrator on 2015/8/13.
 */
public class AccountActivity extends AutososBackActivity implements View.OnClickListener{


    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_account);
        setBill();

    }
    @Override
    public void onClick(View v) {
        switch (v.getId()) {
            case R.id.setting_password:
                Intent intent = new Intent(this,PasswordActivity.class);
                startActivity(intent);
                overridePendingTransition(R.anim.slide_in_right, R.anim.activity_anim_default);
               break;
        }
    }

    public void onBackPressed() {
        super.onBackPressed();
        overridePendingTransition(0, R.anim.slide_out_right);
    }

}
