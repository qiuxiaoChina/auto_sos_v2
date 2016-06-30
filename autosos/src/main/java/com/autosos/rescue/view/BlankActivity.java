package com.autosos.rescue.view;

import android.app.Activity;
import android.os.Bundle;
import android.util.Log;
import android.view.Window;
import android.widget.LinearLayout;

import com.autosos.rescue.R;

/**
 * Created by Administrator on 2016/6/30.
 */
public class BlankActivity extends Activity {

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        requestWindowFeature(Window.FEATURE_NO_TITLE);
        LinearLayout layout2;
        layout2 = new LinearLayout(this);
        layout2.setBackgroundDrawable(null);
        layout2.setBackground(null);
        setContentView(R.layout.activity_blank);
        Log.i("Blank","exists");
    }
}
