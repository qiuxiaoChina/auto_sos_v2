package com.autosos.rescue.view;

import android.app.Activity;
import android.content.Intent;
import android.net.Uri;
import android.os.Bundle;
import android.view.TouchDelegate;
import android.view.View;
import android.widget.Button;
import android.widget.ImageView;
import android.widget.Toast;

import com.autosos.rescue.R;

public class FeedBackActivity extends Activity implements View.OnClickListener{

    private ImageView back_button;
    private Button btn_feedback;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_feed_back);
        back_button = (ImageView) findViewById(R.id.back_button);
        back_button.setOnClickListener(this);

        btn_feedback = (Button) findViewById(R.id.btn_feedback);
        btn_feedback.setOnClickListener(this);
    }

    @Override
    public void onClick(View v) {
        switch (v.getId()){

            case R.id.back_button:
                finish();
                break;
            case R.id.btn_feedback:
                Toast.makeText(this,"此功能正在开发,敬请期待",Toast.LENGTH_SHORT).show();
                break;

        }
    }
}
