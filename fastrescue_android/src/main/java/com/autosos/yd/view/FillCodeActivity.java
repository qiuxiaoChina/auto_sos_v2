package com.autosos.yd.view;

import android.content.Intent;
import android.net.Uri;
import android.os.Bundle;
import android.os.Handler;
import android.text.Editable;
import android.text.TextWatcher;
import android.util.Log;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.TextView;
import android.widget.Toast;

import com.autosos.yd.Constants;
import com.autosos.yd.R;
import com.autosos.yd.task.NewHttpPostTask;
import com.autosos.yd.task.OnHttpRequestListener;
import com.autosos.yd.widget.MyCountTimer;
import com.autosos.yd.widget.SmsContent;

import org.json.JSONException;
import org.json.JSONObject;

import java.util.HashMap;
import java.util.Map;

/**
 * Created by Administrator on 2015/11/25.
 */
public class FillCodeActivity extends AutososBackActivity{

    private TextView TextView_mobile;
    private TextView send_code;
    private View progressBar;
    private String mobile;
    private String username;
    private String password;
    private EditText Edit_code;
    private Button Button_modification;
    private Boolean number_enter;


    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_fillcode);
        number_enter = false;
        mobile = getIntent().getStringExtra("mobile");
        username = getIntent().getStringExtra("username");
        password = getIntent().getStringExtra("password");
        Edit_code = (EditText) findViewById(R.id.Edit_code);
        Button_modification = (Button) findViewById(R.id.Button_modification);
        TextView_mobile = (TextView) findViewById(R.id.TextView_mobile);
        String mobile = getIntent().getStringExtra("mobile");
        progressBar = findViewById(R.id.progressBar);
        send_code = (TextView) findViewById(R.id.send_code);
        Edit_code.addTextChangedListener(new TextWatcher() {
            @Override
            public void beforeTextChanged(CharSequence s, int start, int count, int after) {

            }

            @Override
            public void onTextChanged(CharSequence s, int start, int before, int count) {

            }

            @Override
            public void afterTextChanged(Editable s) {
                if (s != null && s.length() > 0) {
                    number_enter = true;
                } else {
                    number_enter = false;
                }
                if (number_enter) {
                    Button_modification.setBackgroundResource(R.drawable.bg_shape_second_green);
                } else {
                    Button_modification.setBackgroundResource(R.drawable.bg_shape_second_grav);
                }
            }
        });
        SmsContent content = new SmsContent(FillCodeActivity.this, new Handler(), Edit_code);
        // 注册短信变化监听
        this.getContentResolver().registerContentObserver(Uri.parse("content://sms/"), true, content);
        send_code.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                getCode();
            }
        });
        if (mobile.length()<11){
            String hide_mobile = "*******"+ mobile.substring(mobile.length()-3, mobile.length());
            TextView_mobile.setText(hide_mobile);
        }else {
            String hide_mobile = mobile.substring(0, 3) +"*******"+ mobile.substring(9, mobile.length());
            TextView_mobile.setText(hide_mobile);
        }

    }

    public void getCode(){
        progressBar.setVisibility(View.VISIBLE);
        Map<String, Object> map = new HashMap<>();
        map.put("username", username);
        map.put("password", password);
        new NewHttpPostTask(this, new OnHttpRequestListener() {
            @Override
            public void onRequestCompleted(Object obj) {
                try {
                    JSONObject jsonObject = new JSONObject(obj.toString());
                    if (!jsonObject.isNull("result")) {
                        int result = jsonObject.optInt("result");
                        if (result == 1) {
                            progressBar.setVisibility(View.GONE);
                            Log.e("test", jsonObject.toString());
                            MyCountTimer myCountTimer = new MyCountTimer(send_code, 0xff4b8d0e, 0xffaaaaaa);
                            myCountTimer.start();
                        } else {
                            progressBar.setVisibility(View.GONE);
                            String msg = jsonObject.optString("info");
                            Toast.makeText(com.autosos.yd.view.FillCodeActivity.this, msg, Toast.LENGTH_SHORT).show();
                        }
                    }

                } catch (JSONException e) {
                }
                Log.e("test", obj.toString());
            }

            @Override
            public void onRequestFailed(Object obj) {

            }
        }).execute(Constants.GET_AUTH_NUMBER, map);
    }

    public void onCommit(View view){
        String code = Edit_code.getText().toString();
        if (code != null && code.length() > 0){
            Intent intent = new Intent(FillCodeActivity.this,LoginActivity.class);
            intent.putExtra("username", username);
            intent.putExtra("password", password);
            intent.putExtra("code", code);
            intent.putExtra("fillcode",true);
            startActivity(intent);
        }else {
            Toast.makeText(FillCodeActivity.this, "验证码不能为空", Toast.LENGTH_SHORT).show();
        }

    }


}
