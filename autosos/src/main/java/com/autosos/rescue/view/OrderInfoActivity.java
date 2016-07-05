package com.autosos.rescue.view;

import android.app.Activity;
import android.os.Bundle;
import android.util.Log;
import android.view.View;
import android.widget.ImageView;

import com.autosos.rescue.R;

public class OrderInfoActivity extends Activity implements View.OnClickListener{

    private ImageView back_button;
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_order_info);
        int id = getIntent().getIntExtra("id",0);
        Log.d("orderinfo_activity",id+"");
        back_button = (ImageView) findViewById(R.id.back_button);
        back_button.setOnClickListener(this);
    }

    @Override
    public void onClick(View v) {
        switch (v.getId()){

            case R.id.back_button:
                finish();
                break;
            default:
                break;
        }
    }
}
