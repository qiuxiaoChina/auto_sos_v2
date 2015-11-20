package com.autosos.yd.view;

import android.os.Bundle;
import android.util.Log;
import android.view.View;
import android.widget.Button;

import com.autosos.yd.R;
import com.autosos.yd.widget.MyCountTimer;

/**
 * Created by Administrator on 2015/11/20.
 */
public class MobileActivity extends AutososBackActivity {
    private Button bt_security_code;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_seting_mobile);
        bt_security_code = (Button) findViewById(R.id.bt_security_code);

    }

    public void sendCode(View view){
        Log.e("mobile","========");
        MyCountTimer myCountTimer = new MyCountTimer(bt_security_code,0xff4b8d0e,0xffaaaaaa);
        myCountTimer.start();
    }
}
