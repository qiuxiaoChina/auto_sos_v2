package com.autosos.rescue.view;

import android.app.Activity;
import android.os.Bundle;
import android.view.KeyEvent;

import com.autosos.rescue.R;

/**
 * Created by Administrator on 2016/6/28.
 */
public class PayActivity extends Activity {

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_pay);
    }

    //按返回键 没有办法返回
    @Override
    public boolean onKeyDown(int keyCode, KeyEvent event) {

        if (keyCode == KeyEvent.KEYCODE_BACK) {
            return true;
        }
        return false;
    }
}
