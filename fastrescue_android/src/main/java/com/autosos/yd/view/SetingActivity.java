package com.autosos.yd.view;

import android.app.Activity;
import android.content.Context;
import android.content.Intent;
import android.media.AudioManager;
import android.media.MediaPlayer;
import android.media.SoundPool;
import android.os.Bundle;
import android.util.Log;
import android.view.View;
import android.view.animation.Animation;
import android.view.animation.AnimationUtils;
import android.widget.Button;
import android.widget.CompoundButton;
import android.widget.RelativeLayout;
import android.widget.Switch;

import com.autosos.yd.R;
import com.autosos.yd.util.GPSLocation;
import com.autosos.yd.util.UpdateStateServe;

import java.util.HashMap;

/**
 * Created by Administrator on 2015/8/13.
 */
public class SetingActivity extends AutososBackActivity implements View.OnClickListener{

    private RelativeLayout setting_password;
    private Switch soundView;
    private String TAG = "SettingActivity";
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_seting);
        setting_password = (RelativeLayout) findViewById(R.id.setting_password);
        setting_password.setOnClickListener(this);
        soundView =(Switch) findViewById(R.id.sound);
        if(UpdateStateServe.Setting_Sound_OnOff){
            soundView.setChecked(true);
        }
        else{
            soundView.setChecked(false);
        }
        soundView.setOnCheckedChangeListener(new CompoundButton.OnCheckedChangeListener() {
            @Override
            public void onCheckedChanged(CompoundButton buttonView,
                                         boolean isChecked) {
                // TODO Auto-generated method stub
                if (isChecked) {
                    Log.e(TAG,"kai");
                    UpdateStateServe.Setting_Sound_OnOff = true;
                } else {
                    Log.e(TAG, "guan");
                    UpdateStateServe.Setting_Sound_OnOff = false;
                }
            }
        });
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
