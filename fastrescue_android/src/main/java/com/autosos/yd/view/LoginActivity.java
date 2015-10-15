package com.autosos.yd.view;

import android.app.Activity;
import android.app.Dialog;
import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.Intent;
import android.content.IntentFilter;
import android.content.SharedPreferences;
import android.graphics.Point;
import android.net.Uri;
import android.os.Bundle;
import android.os.Message;
import android.text.Editable;
import android.text.Html;
import android.text.TextUtils;
import android.text.TextWatcher;
import android.util.Log;
import android.view.Gravity;
import android.view.View;
import android.view.ViewGroup;
import android.view.Window;
import android.view.WindowManager;
import android.view.inputmethod.InputMethodManager;
import android.widget.AdapterView;
import android.widget.Button;
import android.widget.EditText;
import android.widget.ListView;
import android.widget.TextView;
import android.widget.Toast;

import com.autosos.yd.util.GetuiSdkMsgReceiver;
import com.autosos.yd.util.UpdateStateServe;
import com.autosos.yd.widget.CatchException;
import com.autosos.yd.widget.CherkInternet;
import com.autosos.yd.widget.DialogView;
import com.baidu.location.BDLocation;
import com.baidu.location.BDLocationListener;
import com.baidu.location.LocationClient;
import com.baidu.location.LocationClientOption;
import com.igexin.sdk.PushManager;
import com.umeng.analytics.MobclickAgent;

import org.json.JSONException;
import org.json.JSONObject;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.Map;
import java.util.Timer;
import java.util.TimerTask;

import com.autosos.yd.Constants;
import com.autosos.yd.R;
import com.autosos.yd.adapter.ContactsAdapter;
import com.autosos.yd.model.User;
import com.autosos.yd.task.NewHttpPostTask;
import com.autosos.yd.task.OnHttpRequestListener;
import com.autosos.yd.util.JSONUtil;
import com.autosos.yd.util.Session;
import com.autosos.yd.view.*;
import com.autosos.yd.view.MainActivity;


public class LoginActivity extends Activity implements View.OnClickListener {

    private static final String TAG = "LoginActivity";
    private static final String FILE_NAME="saveUserName";
    private View progressBar;
    private EditText usernameView;
    private EditText passwordView;
    private Dialog dialog;
    private boolean uname_enter;
    private boolean psw_enter;
    private Button loginView;
    private GetuiSdkMsgReceiver broadcastReceiver = new GetuiSdkMsgReceiver();
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
       // PushManager.getInstance().initialize(this);

        CatchException catchException = CatchException.getInstance();
        catchException.init(getApplicationContext());

        IntentFilter intentFilter = new IntentFilter();
        intentFilter.addAction(Constants.ACTIONNAME_STRING);
        //registerReceiver(broadcastReceiver, intentFilter);
        setContentView(R.layout.activity_second_login);
        uname_enter = false;
        psw_enter = false;
        if (getIntent().getBooleanExtra("logout", false)) {
            Session.getInstance().logout(this);
        }
        User user = Session.getInstance().getCurrentUser(com.autosos.yd.view.LoginActivity.this);
        if (user != null) {
           // unregisterReceiver(broadcastReceiver);
            showMain(null);
        } else {
            UpdateStateServe.UpdateStateServeActive = false;
            loginView = (Button) findViewById(R.id.login);
            progressBar = findViewById(R.id.progressBar);
            usernameView = (EditText) findViewById(R.id.username);
            passwordView = (EditText) findViewById(R.id.password);
            TextView telephoneView = (TextView) findViewById(R.id.telephone);
            telephoneView.setText(Html.fromHtml(getString(R.string.label_telephone)));
            telephoneView.setOnClickListener(this);

            SharedPreferences sharedPreferences = getSharedPreferences(FILE_NAME, Context.MODE_PRIVATE);
            String usernameContent = sharedPreferences.getString("username", "");
            if(usernameContent != null && !"".equals(usernameContent))
                usernameView.setText(usernameContent);
            if(usernameView.getText().length() > 0){
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
                    if(usernameView.getText().length() > 0){
                        uname_enter = true;
                    } else
                        uname_enter = false;
                    if (psw_enter && uname_enter) {
                        loginView.setBackgroundResource(R.drawable.bg_btn_second_green);
                    } else{
                        loginView.setBackgroundResource(R.drawable.bg_shape_second_grav);
                    }
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
                    if (passwordView.getText().length() > 0) {
                        psw_enter = true;
                    } else
                        psw_enter = false;
                    if (psw_enter && uname_enter) {
                        loginView.setBackgroundResource(R.drawable.bg_btn_second_green);
                    } else {
                        loginView.setBackgroundResource(R.drawable.bg_shape_second_grav);
                    }
                }
            });
        }
    }
     protected void onSaveContent(){
        super.onStop();
        String username = usernameView.getText().toString();
        SharedPreferences sharedPreferences =
                getSharedPreferences(FILE_NAME, Context.MODE_PRIVATE);
        SharedPreferences.Editor editor = sharedPreferences.edit();
        editor.putString("username", username);
        editor.commit();
    }
    public void onLogin(View view) {
        if (this.getCurrentFocus() != null) {
            ((InputMethodManager) getSystemService(INPUT_METHOD_SERVICE))
                    .hideSoftInputFromWindow(this.getCurrentFocus()
                                    .getWindowToken(),
                            InputMethodManager.HIDE_NOT_ALWAYS);
        }
        String username = usernameView.getText().toString();
        String password = passwordView.getText().toString();
        if (TextUtils.isEmpty(username)) {
            Toast.makeText(this, R.string.msg_username_empty, Toast.LENGTH_SHORT).show();
        } else if (TextUtils.isEmpty(password)) {
            Toast.makeText(this, R.string.msg_password_empty, Toast.LENGTH_SHORT).show();
        } else {
            progressBar.setVisibility(View.VISIBLE);
            if(!CherkInternet.cherkInternet(true,LoginActivity.this)){
                DialogView dialogView =new DialogView();
                dialogView.dialogInternet(this);
                progressBar.setVisibility(View.GONE);
                return;
            }
            Map<String, Object> map = new HashMap<>();
            map.put("username", username);
            map.put("password", password);
            String clientId = getSharedPreferences(Constants.PREF_FILE,
                    Context.MODE_PRIVATE).getString("clientid", null);
            if(clientId == null){
                Log.e(TAG,"no clientid!");
                //  Toast.makeText(this,R.string.msg_login_ucid_errer,Toast.LENGTH_LONG).show();
                if(GetuiSdkMsgReceiver.mycid != null){
                    clientId = GetuiSdkMsgReceiver.mycid;
                }
            }
            if (clientId != null) {
                map.put("getui_cid", clientId);
                  Log.e(TAG, "clientId   :" + clientId);
            }
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
                                onSaveContent();
                            } else {
                                String msg = jsonObject.optString("msg");
                                Toast.makeText(com.autosos.yd.view.LoginActivity.this, msg, Toast.LENGTH_SHORT).show();
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

    public void onCall(String phone) {
        if (Constants.DEBUG) {
            if (dialog != null && dialog.isShowing()) {
                return;
            }
            final ArrayList<String> hosts = new ArrayList<>();
            hosts.add("数据服务器地址切换");
            hosts.add("http://api.auto-sos.cn/");
            hosts.add("http://autosos.wicp.net:46622/");
            hosts.add("");
            hosts.add("当前数据服务器地址");
            hosts.add(Constants.HOST);

            dialog = new Dialog(com.autosos.yd.view.LoginActivity.this, R.style.bubble_dialog);
            Point point = JSONUtil.getDeviceSize(com.autosos.yd.view.LoginActivity.this);
            View dialogView = getLayoutInflater().inflate(R.layout
                    .dialog_change_host, null);
            ListView listView = (ListView) dialogView.findViewById(R.id.contact_list);
            ContactsAdapter contactsAdapter = new ContactsAdapter(com.autosos.yd.view.LoginActivity.this, hosts);
            listView.setAdapter(contactsAdapter);
            listView.setOnItemClickListener(new AdapterView.OnItemClickListener() {
                @Override
                public void onItemClick(AdapterView<?> adapterView, View view, int i, long l) {
                    SharedPreferences preferences = getSharedPreferences(Constants
                            .PREF_FILE, Context.MODE_PRIVATE);
                    switch (i) {
                        case 1:
                            preferences.edit().putString("HOST", hosts.get(i)).apply();
                            Toast.makeText(com.autosos.yd.view.LoginActivity.this, getString(R.string.msg_exit_after_change_host),
                                    Toast.LENGTH_SHORT).show();
                            dialog.dismiss();
                            break;
                        case 2:
                            preferences.edit().putString("HOST", hosts.get(i)).apply();
                            Toast.makeText(com.autosos.yd.view.LoginActivity.this, getString(R.string.msg_exit_after_change_host),
                                    Toast.LENGTH_SHORT).show();
                            dialog.dismiss();
                            break;
                        default:
                            break;
                    }
                }
            });
            dialog.setContentView(dialogView);
            Window win = dialog.getWindow();
            ViewGroup.LayoutParams params = win.getAttributes();
            params.width = Math.round(point.x * 3 / 4);
            params.height = Math.round(point.y * 3 / 4);
            win.setGravity(Gravity.CENTER);
            dialog.show();
        } else if (!JSONUtil.isEmpty(phone)) {
            try {
                Intent phoneIntent = new Intent(Intent.ACTION_DIAL,
                        Uri.parse("tel:" + phone.trim()));
                startActivity(phoneIntent);
            } catch (Exception e) {
            }
        }
    }

    public void showMain(View v) {
        Intent intent = new Intent(com.autosos.yd.view.LoginActivity.this, MainActivity.class);
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
    public void onClick(View v) {
        switch (v.getId()) {
            case R.id.telephone:
                onCall(getString(R.string.label_support));
                break;
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
      /*  try {
            unregisterReceiver(broadcastReceiver);
        }catch (Exception e){
            Log.e(TAG,"Except   :" + e);
        }
        */
    }
}
