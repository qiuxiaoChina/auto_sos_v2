package com.autosos.yd.view;

import android.app.Activity;
import android.content.Intent;
import android.os.Bundle;
import android.view.View;
import android.view.animation.Animation;
import android.view.animation.AnimationUtils;
import android.widget.AdapterView;
import android.widget.ArrayAdapter;
import android.widget.Button;
import android.widget.ListView;
import android.widget.SimpleAdapter;
import android.widget.Spinner;
import android.widget.Toast;

import com.autosos.yd.R;
import com.autosos.yd.util.MyService_one;
import com.autosos.yd.util.MyService_two;

public class TrailerDetailActivity extends Activity implements View.OnClickListener{


    private Button detail_confirm;
    private Button planeMenu;
    private View countryName;
    private ListView lv_countryName;
    private View trailer_page;

    @Override
    protected void onCreate(Bundle savedInstanceState) {

        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_trailer);
        detail_confirm = (Button) findViewById(R.id.detail_confirm);
        detail_confirm.setOnClickListener(this);

        planeMenu = (Button) findViewById(R.id.planeMenu);
        planeMenu.setOnClickListener(this);

        trailer_page = findViewById(R.id.trailer_page);
        trailer_page.setOnClickListener(this);

        countryName = findViewById(R.id.countryName);
        lv_countryName = (ListView) findViewById(R.id.lv_countryName);
        String[] array = getResources().getStringArray(R.array.spinnername);
        ArrayAdapter<String> arrayAdapter = new ArrayAdapter<String>(this,R.layout.item_country_name,array);
        lv_countryName.setAdapter(arrayAdapter);
    }

    boolean isClicked = false;
    @Override
    public void onClick(View v) {
        switch (v.getId()){

            case R.id.detail_confirm:
                Intent intent = new Intent(TrailerDetailActivity.this, MainActivity.class);
                startActivity(intent);
                finish();
                break;
            case R.id.planeMenu:
                if(!isClicked){

                    Animation animation = AnimationUtils.loadAnimation(
                            TrailerDetailActivity.this, R.anim.plane_menu_in);
                    countryName.startAnimation(animation);
                    countryName.setVisibility(View.VISIBLE);
                    isClicked = true;
                }else{


                    Animation animation = AnimationUtils.loadAnimation(
                            TrailerDetailActivity.this, R.anim.plane_menu_out);
                    countryName.startAnimation(animation);
                    countryName.setVisibility(View.GONE);
                    isClicked = false;

                }

                break;

            case R.id.trailer_page:
                if(countryName.getVisibility()==View.VISIBLE){

                    Animation animation = AnimationUtils.loadAnimation(
                            TrailerDetailActivity.this, R.anim.plane_menu_out);
                    countryName.startAnimation(animation);
                    countryName.setVisibility(View.GONE);
                    isClicked = false;
                }

                break;

            default:
                break;
        }
    }
}
