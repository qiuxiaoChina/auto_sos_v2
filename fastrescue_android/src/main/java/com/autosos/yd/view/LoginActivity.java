package com.autosos.yd.view;

import android.app.Activity;
import android.app.Dialog;
import android.content.Context;
import android.content.Intent;
import android.content.SharedPreferences;
import android.graphics.Point;
import android.net.Uri;
import android.os.Bundle;
import android.text.Editable;
import android.text.Html;
import android.text.TextUtils;
import android.text.TextWatcher;
import android.util.Log;
import android.view.Gravity;
import android.view.View;
import android.view.ViewGroup;
import android.view.Window;
import android.view.inputmethod.InputMethodManager;
import android.widget.AdapterView;
import android.widget.Button;
import android.widget.EditText;
import android.widget.ListView;
import android.widget.TextView;
import android.widget.Toast;

import com.autosos.yd.Constants;
import com.autosos.yd.R;

import com.autosos.yd.model.User;
import com.autosos.yd.task.NewHttpPostTask;
import com.autosos.yd.task.OnHttpRequestListener;
import com.autosos.yd.util.GetuiSdkMsgReceiver;
import com.autosos.yd.util.JSONUtil;
import com.autosos.yd.util.Session;
import com.umeng.analytics.MobclickAgent;

import org.json.JSONException;
import org.json.JSONObject;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.Map;


public class LoginActivity extends Activity implements View.OnClickListener {

    private static final String TAG = "LoginActivity";
    private static final String FILE_NAME = "saveUserName";
    private View progressBar;
    private EditText usernameView;
    private EditText passwordView;
    private Dialog dialog;
    private boolean uname_enter;
    private boolean psw_enter;
    private Button loginView;
    private String code;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_login);
        uname_enter = false;
        psw_enter = false;
        if (getIntent().getBooleanExtra("logout", false)) {
            Session.getInstance().logout(this);
        }
        loginView = (Button) findViewById(R.id.login);
        loginView.setOnClickListener(this);
        progressBar = findViewById(R.id.progressBar);
        code = getIntent().getStringExtra("code");
        String username = getIntent().getStringExtra("username");
        String password = getIntent().getStringExtra("password");
        usernameView = (EditText) findViewById(R.id.username);
        passwordView = (EditText) findViewById(R.id.password);

        User user = Session.getInstance().getCurrentUser(com.autosos.yd.view.LoginActivity.this);
        if (user != null) {
            // unregisterReceiver(broadcastReceiver);
            showMain(null);
        } else {

            SharedPreferences sharedPreferences = getSharedPreferences(FILE_NAME, Context.MODE_PRIVATE);
            String usernameContent = sharedPreferences.getString("username", "");
            if (usernameContent != null && !"".equals(usernameContent))
                usernameView.setText(usernameContent);
            if (usernameView.getText().length() > 0) {
                uname_enter = true;
            }
            usernameView.addTextChangedListener(new TextWatcher() {
                @Override
                public void beforeTextChanged(CharSequence s, int start, int count, int after) {

                }

                @Override
                public void onTextChanged(CharSequence s, int start, int before, int count) {
                }

                @Override
                public void afterTextChanged(Editable s) {
//                    if (usernameView.getText().length() > 0) {
//                        uname_enter = true;
//                    } else
//                        uname_enter = false;

                }
            });
            passwordView.addTextChangedListener(new TextWatcher() {
                @Override
                public void beforeTextChanged(CharSequence s, int start, int count, int after) {

                }

                @Override
                public void onTextChanged(CharSequence s, int start, int before, int count) {
                }

                @Override
                public void afterTextChanged(Editable s) {
//                    if (passwordView.getText().length() > 0) {
//                        psw_enter = true;
//                    } else
//                        psw_enter = false;
//                    if (psw_enter && uname_enter) {
//                        loginView.setBackgroundResource(R.drawable.bg_btn_second_green);
//                    } else {
//                        loginView.setBackgroundResource(R.drawable.bg_shape_second_grav);
//                    }
                }
            });
        }
        if (getIntent().getBooleanExtra("fillcode", false)) {
            login();
        }

    }


    public void login() {
        if (this.getCurrentFocus() != null) {
            ((InputMethodManager) getSystemService(INPUT_METHOD_SERVICE))
                    .hideSoftInputFromWindow(this.getCurrentFocus()
                                    .getWindowToken(),
                            InputMethodManager.HIDE_NOT_ALWAYS);
        }
        String username = usernameView.getText().toString();
        String password = passwordView.getText().toString();

        if (getIntent().getBooleanExtra("fillcode", false)) {
            username = getIntent().getStringExtra("username");
            password = getIntent().getStringExtra("password");
        }
        if (TextUtils.isEmpty(username)) {
            Toast.makeText(this, R.string.msg_username_empty, Toast.LENGTH_SHORT).show();
        } else if (TextUtils.isEmpty(password)) {
            Toast.makeText(this, R.string.msg_password_empty, Toast.LENGTH_SHORT).show();
        } else {

            progressBar.setVisibility(View.VISIBLE);

            Map<String, Object> map = new HashMap<>();
            map.put("username", username);
            map.put("password", password);
            map.put("sms_code", code);
            Log.e("code", "code === " + code);
            Log.e("code", "username === " + username);
            Log.e("code", "password === " + password);
            String clientId = getSharedPreferences("cid",
                    Context.MODE_PRIVATE).getString("cid", null);
            map.put("getui_cid", clientId);
            Log.e("code", "cid === " + clientId);


            new NewHttpPostTask(this, new OnHttpRequestListener() {
                @Override
                public void onRequestCompleted(Object obj) {
                    progressBar.setVisibility(View.GONE);
                    try {
                        JSONObject jsonObject = new JSONObject(obj.toString());
                        if (!jsonObject.isNull("result")) {
                            int result = jsonObject.optInt("result");
                            if (result == 1) {
                                Session.getInstance().setCurrentUser(com.autosos.yd.view.LoginActivity.this, jsonObject);
                                Log.e(TAG, jsonObject.toString());
                                showMain(null);
                                finish();
                            } else {
                                if (jsonObject.optInt("code") == 9 || jsonObject.optInt("code") == 10) {
                                    if(jsonObject.optInt("code") == 9 ){
                                        String username = usernameView.getText().toString();
                                        String password = passwordView.getText().toString();
                                        Log.e("login", "mobile === " + jsonObject.optString("mobile"));
                                        Log.e("login", "username === " + username);
                                        Log.e("login", "password === " + password);
                                        Intent intent = new Intent(LoginActivity.this, CheckActivity.class);
                                        intent.putExtra("mobile", jsonObject.optString("mobile"));
                                        Log.e("log", "mobile === " + jsonObject.optString("mobile"));
                                        Log.e("log", "username === " + username);
                                        Log.e("log", "password === " + password);
                                        intent.putExtra("username", username);
                                        intent.putExtra("password", password);
                                        startActivity(intent);
                                    }else{
                                        Toast.makeText(com.autosos.yd.view.LoginActivity.this, "短信验证码不正确,60秒后重新登录", Toast.LENGTH_SHORT).show();
                                    }
                                } else {
                                    String msg = jsonObject.optString("msg");
                                    Toast.makeText(com.autosos.yd.view.LoginActivity.this, msg, Toast.LENGTH_SHORT).show();
                                }

                            }
                        }

                    } catch (JSONException e) {
                    }
                    Log.e(TAG, obj.toString());
                }

                @Override
                public void onRequestFailed(Object obj) {

                }
            }).execute(Constants.ACCESS_TOKEN_URL, map);
        }


    }


    public void showMain(View v) {
        Intent intent = new Intent(com.autosos.yd.view.LoginActivity.this, TrailerDetailActivity.class);
        startActivity(intent);
        finish();
    }

    public void onDismiss(View v) {
        if (this.getCurrentFocus() != null) {
            ((InputMethodManager) getSystemService(INPUT_METHOD_SERVICE))
                    .hideSoftInputFromWindow(this.getCurrentFocus()
                                    .getWindowToken(),
                            InputMethodManager.HIDE_NOT_ALWAYS);
        }
    }


    @Override
    protected void onPause() {
        super.onPause();
        MobclickAgent.onPause(this);
    }

    @Override
    protected void onResume() {
        super.onResume();
        MobclickAgent.onResume(this);
    }

    @Override
    protected void onDestroy() {
        super.onDestroy();

    }

    @Override
    public void onClick(View v) {

        switch (v.getId()) {

            case R.id.login:
                login();
                break;
            default:
                break;
        }

    }
}
