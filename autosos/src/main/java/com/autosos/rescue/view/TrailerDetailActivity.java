package com.autosos.rescue.view;

import android.app.Activity;
import android.content.Intent;
import android.os.Bundle;
import android.util.Log;
import android.view.View;
import android.view.animation.Animation;
import android.view.animation.AnimationUtils;
import android.widget.AdapterView;
import android.widget.ArrayAdapter;
import android.widget.Button;
import android.widget.CheckBox;
import android.widget.EditText;
import android.widget.ListView;
import android.widget.ProgressBar;
import android.widget.RadioButton;
import android.widget.RadioGroup;
import android.widget.SimpleAdapter;
import android.widget.Spinner;
import android.widget.TextView;
import android.widget.Toast;

import com.autosos.rescue.Constants;
import com.autosos.rescue.R;
import com.autosos.rescue.model.TrailerInfo;
import com.autosos.rescue.task.HttpGetTask;
import com.autosos.rescue.task.NewHttpPostTask;
import com.autosos.rescue.task.OnHttpRequestListener;
import com.autosos.rescue.util.MyService_one;
import com.autosos.rescue.util.MyService_two;
import com.autosos.rescue.widget.CatchException;

import org.json.JSONException;
import org.json.JSONObject;

import java.util.HashMap;
import java.util.Map;

public class TrailerDetailActivity extends Activity implements View.OnClickListener{


    private Button detail_confirm;
    private Button planeMenu;
    private View countryName,tonnageMenu;
    private ListView lv_countryName,lv_tonnage;
    private View trailer_page,showTonnage;
    private EditText planeNum,moblie;
    private  TextView tonnage;
    private RadioGroup rg_ground,rg_aw,rg_arm;
    private RadioButton rb_ground_no,rb_ground_yes,rb_aw_no,rb_aw_yes,rb_arm_yes,rb_arm_no;
    private CheckBox tuoche,huantai,kuaixiu,dadian;
    private int is_ground,is_aw,is_arm;
    private ProgressBar progressBar;

    @Override
    protected void onCreate(Bundle savedInstanceState) {

        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_trailer);

        detail_confirm = (Button) findViewById(R.id.detail_confirm);
        detail_confirm.setOnClickListener(this);

        planeMenu = (Button) findViewById(R.id.planeMenu);
        planeMenu.setOnClickListener(this);

        planeNum = (EditText) findViewById(R.id.planeNum);
        moblie = (EditText) findViewById(R.id.mobile);

        showTonnage =findViewById(R.id.showTonnage);
        showTonnage.setOnClickListener(this);

        trailer_page = findViewById(R.id.trailer_page);
        trailer_page.setOnClickListener(this);

        rg_ground = (RadioGroup) findViewById(R.id.rg_ground);
        rg_arm = (RadioGroup) findViewById(R.id.rg_arm);
        rg_aw = (RadioGroup) findViewById(R.id.rg_aw);

        rb_ground_yes = (RadioButton) findViewById(R.id.rb_ground_yes);
        rb_ground_no  = (RadioButton) findViewById(R.id.rb_ground_no);

        rb_arm_yes = (RadioButton) findViewById(R.id.rb_arm_yes);
        rb_arm_no = (RadioButton) findViewById(R.id.rb_arm_no);

        rb_aw_yes = (RadioButton) findViewById(R.id.rb_aw_yes);
        rb_aw_no = (RadioButton) findViewById(R.id.rb_aw_no);

        tuoche = (CheckBox) findViewById(R.id.tuoche);
        dadian = (CheckBox) findViewById(R.id.dadian);
        huantai = (CheckBox) findViewById(R.id.huantai);
        kuaixiu = (CheckBox) findViewById(R.id.kuaixiu);

        progressBar = (ProgressBar) findViewById(R.id.progressBar);

        rg_ground.setOnCheckedChangeListener(new RadioGroup.OnCheckedChangeListener() {
            @Override
            public void onCheckedChanged(RadioGroup group, int checkedId) {
                if(checkedId ==rb_ground_yes.getId()){

                     is_ground = 1;
                }else{

                    is_ground = 0;
                }
            }
        });


        rg_aw.setOnCheckedChangeListener(new RadioGroup.OnCheckedChangeListener() {
            @Override
            public void onCheckedChanged(RadioGroup group, int checkedId) {
                if(checkedId ==rb_aw_yes.getId()){

                    is_aw = 1;

                }else{

                    is_aw = 0;
                }
            }
        });

        rg_arm.setOnCheckedChangeListener(new RadioGroup.OnCheckedChangeListener() {
            @Override
            public void onCheckedChanged(RadioGroup group, int checkedId) {
                if(checkedId ==rb_arm_yes.getId()){

                     is_arm = 1;
                }else{

                     is_arm = 0;
                }
            }
        });

        tonnage = (TextView) findViewById(R.id.tonnage);
        countryName = findViewById(R.id.countryName);
        lv_countryName = (ListView) findViewById(R.id.lv_countryName);
        String[] array = getResources().getStringArray(R.array.spinnername);
        ArrayAdapter<String> arrayAdapter = new ArrayAdapter<String>(this,R.layout.item_country_name,array);
        lv_countryName.setAdapter(arrayAdapter);
        lv_countryName.setOnItemClickListener(new AdapterView.OnItemClickListener() {
            @Override
            public void onItemClick(AdapterView<?> parent, View view, int position, long id) {
                TextView textView = (TextView) view.findViewById(R.id.shengfenming);
                planeMenu.setText(textView.getText());
                Animation animation = AnimationUtils.loadAnimation(
                        TrailerDetailActivity.this, R.anim.plane_menu_out);
                countryName.startAnimation(animation);
                countryName.setVisibility(View.GONE);
                Log.d("shengfenming",textView.getText().toString());
            }
        });

        tonnageMenu = findViewById(R.id.tonnageMenu);
        lv_tonnage = (ListView) findViewById(R.id.lv_tonnage);
        String[] array1 = getResources().getStringArray(R.array.tonnage);
        ArrayAdapter<String> arrayAdapter1 = new ArrayAdapter<String>(this,R.layout.item_country_name,array1);
        lv_tonnage.setAdapter(arrayAdapter1);
        lv_tonnage.setOnItemClickListener(new AdapterView.OnItemClickListener() {
            @Override
            public void onItemClick(AdapterView<?> parent, View view, int position, long id) {
                TextView textView = (TextView) view.findViewById(R.id.shengfenming);
                tonnage.setText(textView.getText());
                Animation animation = AnimationUtils.loadAnimation(
                        TrailerDetailActivity.this, R.anim.plane_menu_out);
                tonnageMenu.startAnimation(animation);
                tonnageMenu.setVisibility(View.GONE);

            }
        });

    }

    boolean isClicked = false;
    @Override
    public void onClick(View v) {
        switch (v.getId()){
            case R.id.showTonnage:
                Animation animation1 = AnimationUtils.loadAnimation(
                        TrailerDetailActivity.this, R.anim.plane_menu_in);
                tonnageMenu.startAnimation(animation1);
                tonnageMenu.setVisibility(View.VISIBLE);
                break;
            case R.id.detail_confirm:
                String plane = (planeMenu.getText().toString()+planeNum.getText().toString()).toUpperCase();
                String mobile = moblie.getText().toString();
                String s_tonnage = tonnage.getText().toString().replace("吨","");
                service_type = "";
                if(tuoche.isChecked()){

                    service_type+="1,";
                }

                if(dadian.isChecked()){

                    service_type +="4,";
                }

                if(kuaixiu.isChecked()){

                    service_type +="9,";
                }
                if(huantai.isChecked()){

                    service_type +="2,";
                }
                progressBar.setVisibility(View.VISIBLE);
                Map map = new HashMap<String,Object>();
                map.put("license",plane);
                map.put("mobile",mobile);
                map.put("tonnage",s_tonnage);
                map.put("is_ground",is_ground);
                map.put("is_aw",is_aw);
                map.put("is_arm",is_arm);
                map.put("service_type",service_type);
                new NewHttpPostTask(TrailerDetailActivity.this, new OnHttpRequestListener() {
                    @Override
                    public void onRequestCompleted(Object obj) {
                        progressBar.setVisibility(View.GONE);
                        JSONObject jsonObject;
                        try {
                            jsonObject = new JSONObject(obj.toString());
                            if (jsonObject.getInt("result") == 1) {
                                Intent intent = new Intent(TrailerDetailActivity.this, MainActivity.class);
                                startActivity(intent);
                                finish();
                            }else if(jsonObject.getInt("result") == 0){
                                Toast.makeText(getApplicationContext(),"提交失败,请补全车辆信息再提交",Toast.LENGTH_SHORT).show();
                                Log.d("trail","失败");
                            }

                        }catch (Exception e){


                        }

                    }
                    @Override
                    public void onRequestFailed(Object obj) {
                        progressBar.setVisibility(View.GONE);
                        Toast.makeText(getApplicationContext(),"网络环境不太好,提交失败",Toast.LENGTH_SHORT).show();
                        Log.d("trail","404");

                    }
                }).execute(Constants.SUBMIT_TRAILER_INFO_URL,map);

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
                if(countryName.getVisibility()==View.VISIBLE || tonnageMenu.getVisibility() == View.VISIBLE){

                    Animation animation = AnimationUtils.loadAnimation(
                            TrailerDetailActivity.this, R.anim.plane_menu_out);
                    countryName.startAnimation(animation);
                    countryName.setVisibility(View.GONE);
                    tonnageMenu.startAnimation(animation);
                    tonnageMenu.setVisibility(View.GONE);
                    isClicked = false;
                }

                break;

            default:
                break;
        }
    }
    private String service_type ="";
    @Override
    protected void onResume() {
        super.onResume();
        progressBar.setVisibility(View.VISIBLE);
        new HttpGetTask(getApplicationContext(), new OnHttpRequestListener() {
            @Override
            public void onRequestCompleted(Object obj) {

                JSONObject jsonObject1 = null;
                JSONObject jsonObject = null;
                int result;
                progressBar.setVisibility(View.GONE);
                try {
                    jsonObject1 = new JSONObject(obj.toString());
                    result = jsonObject1.getInt("result");
                    if(result==1){

                        jsonObject = jsonObject1.getJSONObject("data");
                        TrailerInfo ti = new TrailerInfo(jsonObject);
                        String s_countryName = ti.getPlaneNum().substring(0,1);
                        String s_planeNum = ti.getPlaneNum().substring(1);
                        planeMenu.setText(s_countryName);
                        planeNum.setText(s_planeNum);

                        String s_mobile = ti.getMobile();
                        moblie.setText(s_mobile);

                        int i_tonnage = (int)ti.getTonnage();
                        tonnage.setText(i_tonnage+"吨");

                        is_ground = ti.getIs_ground();
                        if(is_ground==1){

                            rb_ground_yes.setChecked(true);
                        }else{

                            rb_ground_no.setChecked(true);
                        }


                        is_aw = ti.getIs_aw();
                        if(is_aw==1){

                            rb_aw_yes.setChecked(true);
                        }else{

                            rb_aw_no.setChecked(true);
                        }

                        is_arm = ti.getIs_arm();
                        if(is_arm==1){

                            rb_arm_yes.setChecked(true);
                        }else{

                            rb_arm_no.setChecked(true);
                        }

                        service_type = ti.getServiceType();
                        if(service_type.indexOf("1")>-1){
                           Log.d("tuoche_t","tuoche");
                            tuoche.setChecked(true);

                        }

                        if(service_type.indexOf("2")>-1){

                            huantai.setChecked(true);
                        }

                        if(service_type.indexOf("4")>-1){
                            Log.d("tuoche_t","dadian");
                           dadian.setChecked(true);
                        }

                        if(service_type.indexOf("9")>-1){

                            kuaixiu.setChecked(true);
                        }

                    }else if(result ==0){

                        Toast.makeText(getApplicationContext(),"请把下列车辆信息填写完整",Toast.LENGTH_SHORT).show();
                    }

                   // Log.d("detail_trailer",result+"----"+jsonObject.toString());

                } catch (JSONException e) {
                    e.printStackTrace();

                }

            }

            @Override
            public void onRequestFailed(Object obj) {
                progressBar.setVisibility(View.GONE);
                Toast.makeText(getApplicationContext(),"网络信号不好,请重启APP",Toast.LENGTH_SHORT).show();
            }

        }).execute(Constants.TRAILER_INFO_URL);
    }
}
