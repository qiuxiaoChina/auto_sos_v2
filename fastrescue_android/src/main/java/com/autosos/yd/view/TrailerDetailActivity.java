package com.autosos.yd.view;

import android.app.Activity;
import android.content.Intent;
import android.os.Bundle;
import android.view.View;
import android.widget.AdapterView;
import android.widget.ArrayAdapter;
import android.widget.Button;
import android.widget.Spinner;
import android.widget.Toast;

import com.autosos.yd.R;

public class TrailerDetailActivity extends Activity implements View.OnClickListener{


    private Button detail_confirm;
    private Button planeMenu;
    @Override
    protected void onCreate(Bundle savedInstanceState) {

        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_trailer);
        detail_confirm = (Button) findViewById(R.id.detail_confirm);
        detail_confirm.setOnClickListener(this);

    }

    @Override
    public void onClick(View v) {
        switch (v.getId()){

            case R.id.detail_confirm:
                Intent intent = new Intent(TrailerDetailActivity.this, MainActivity.class);
                startActivity(intent);
                finish();
                break;

            default:
                break;
        }
    }
}
