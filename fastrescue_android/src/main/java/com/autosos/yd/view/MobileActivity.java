package com.autosos.yd.view;

import android.app.Dialog;
import android.content.Intent;
import android.graphics.Point;
import android.net.Uri;
import android.os.Bundle;
import android.os.Handler;
import android.text.Editable;
import android.text.TextWatcher;
import android.util.Log;
import android.view.View;
import android.view.Window;
import android.view.WindowManager;
import android.widget.Button;
import android.widget.EditText;
import android.widget.TextView;
import android.widget.Toast;

import com.autosos.yd.Constants;
import com.autosos.yd.R;
import com.autosos.yd.task.NewHttpPostTask;
import com.autosos.yd.task.OnHttpRequestListener;
import com.autosos.yd.util.JSONUtil;
import com.autosos.yd.util.Session;
import com.autosos.yd.widget.MyCountTimer;
import com.autosos.yd.widget.SmsContent;

import org.json.JSONException;
import org.json.JSONObject;

import java.util.HashMap;
import java.util.Map;

/**
 * Created by Administrator on 2015/11/20.
 */
public class MobileActivity extends AutososBackActivity implements View.OnClickListener {
    private Button bt_security_code;
    private EditText Edit_number;
    private EditText Edit_code;
    private Button Button_modification;
    private String TAG = "MobileActivity";
    private String mobile;
    private boolean number_enter;
    private boolean code_enter;
    private Dialog dialog;
    private View empty;
    private View progressBar;


    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_seting_mobile);
        number_enter = false;
        code_enter = false;
        mobile = null;
        empty = findViewById(R.id.empty);
        progressBar = findViewById(R.id.progressBar);
        bt_security_code = (Button) findViewById(R.id.bt_security_code);
        Edit_number = (EditText) findViewById(R.id.Edit_number);
        Edit_code = (EditText) findViewById(R.id.Edit_code);
        Button_modification = (Button) findViewById(R.id.Button_modification);

        SmsContent content = new SmsContent(MobileActivity.this, new Handler(), Edit_code);
        // 注册短信变化监听
        this.getContentResolver().registerContentObserver(Uri.parse("content://sms/"), true, content);

        Button_modification.setOnClickListener(this);
        bt_security_code.setOnClickListener(this);
        Edit_number.addTextChangedListener(new TextWatcher() {
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
                if (number_enter && code_enter) {
                    Button_modification.setBackgroundResource(R.drawable.bg_shape_second_green);
                } else {
                    Button_modification.setBackgroundResource(R.drawable.bg_shape_second_grav);
                }
            }
        });
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
                    code_enter = true;
                } else {
                    code_enter = false;
                }
                if (number_enter && code_enter) {
                    Button_modification.setBackgroundResource(R.drawable.bg_shape_second_green);
                } else {
                    Button_modification.setBackgroundResource(R.drawable.bg_shape_second_grav);
                }
            }
        });
    }


    public void onBackPressed() {
        Intent intent = new Intent(MobileActivity.this, SettingActivity2.class);
        intent.putExtra("mobile",mobile);
        setResult(1, intent);
        super.onBackPressed();
        overridePendingTransition(0, R.anim.slide_out_right);
    }

    @Override
    public void onClick(View v) {
        switch (v.getId()) {
            case R.id.Button_modification:
                if (code_enter && number_enter) {
                    progressBar.setVisibility(View.VISIBLE);                                                    //这边写提交操作
                    Map<String, Object> map = new HashMap<>();
                    map.put("mobile", Edit_number.getText().toString());
                    map.put("code", Edit_code.getText().toString());
                    Log.e(TAG, Edit_code.getText() + "");
                    new NewHttpPostTask(this, new OnHttpRequestListener() {
                        @Override
                        public void onRequestCompleted(Object obj) {
                            try {
                                JSONObject jsonObject = new JSONObject(obj.toString());
                                if (!jsonObject.isNull("result")) {
                                    int result = jsonObject.optInt("result");
                                    if (result == 1) {
                                        Log.e(TAG, jsonObject.toString());
                                        progressBar.setVisibility(View.GONE);
                                        mobile = Edit_number.getText().toString();
                                        showDialog("修改成功！", 2);
                                    } else {
                                        progressBar.setVisibility(View.GONE);
                                        String msg = jsonObject.optString("info");
                                        Toast.makeText(com.autosos.yd.view.MobileActivity.this, msg, Toast.LENGTH_SHORT).show();
                                    }
                                }

                            } catch (JSONException e) {
                            }
                            Log.e(TAG, obj.toString());
                        }

                        @Override
                        public void onRequestFailed(Object obj) {

                        }
                    }).execute(Constants.GET_CHANGE_NUMBER, map);
                } else {
                    Toast.makeText(MobileActivity.this, "新号码或验证码不能为空", Toast.LENGTH_SHORT).show();
                }
                break;
            case R.id.bt_security_code:
                //这边写发送验证码的请求
                Log.e("mobile", "========");
                if (number_enter) {
                    progressBar.setVisibility(View.VISIBLE);
                    mobile = Edit_number.getText().toString();
                    Map<String, Object> map = new HashMap<>();
                    map.put("mobile", Edit_number.getText().toString());
                    new NewHttpPostTask(this, new OnHttpRequestListener() {
                        @Override
                        public void onRequestCompleted(Object obj) {
                            try {
                                JSONObject jsonObject = new JSONObject(obj.toString());
                                if (!jsonObject.isNull("result")) {
                                    int result = jsonObject.optInt("result");
                                    if (result == 1) {
                                        progressBar.setVisibility(View.GONE);
                                        Log.e(TAG, jsonObject.toString());
                                        MyCountTimer myCountTimer = new MyCountTimer(bt_security_code, 0xff4b8d0e, 0xffaaaaaa);
                                        myCountTimer.start();
//                                        showDialog("发送成功！", 1);
                                    } else {
                                        progressBar.setVisibility(View.GONE);
                                        String msg = jsonObject.optString("info");
                                        Toast.makeText(com.autosos.yd.view.MobileActivity.this, msg, Toast.LENGTH_SHORT).show();
                                    }
                                }

                            } catch (JSONException e) {
                            }
                            Log.e(TAG, obj.toString());
                        }

                        @Override
                        public void onRequestFailed(Object obj) {

                        }
                    }).execute(Constants.GET_SEND_CODE, map);
                } else {
                    Toast.makeText(MobileActivity.this, "请输入新号码", Toast.LENGTH_SHORT).show();
                }

                break;
        }
    }

    public void showDialog(String s, final int i) {
        View inflate = getLayoutInflater().inflate(R.layout.dialog_msg_content, null);
        dialog = new Dialog(inflate.getContext(), R.style.bubble_dialog);
        Button tvConfirm;
        TextView tvMsg;
        tvConfirm = (Button) inflate.findViewById(R.id.btn_notice_confirm);
        tvMsg = (TextView) inflate.findViewById(R.id.tv_notice_msg);
        tvMsg.setText(s);
        tvConfirm.setOnClickListener(new View.OnClickListener() {

            @Override
            public void onClick(View v) {
                dialog.dismiss();
                if (i == 2) {
                    onBackPressed();

                }

            }
        });
        dialog.setContentView(inflate);
        dialog.setCanceledOnTouchOutside(false);
        Window window = dialog.getWindow();
        WindowManager.LayoutParams params = window.getAttributes();
        Point point = JSONUtil.getDeviceSize(MobileActivity.this);
        params.width = Math.round(point.x * 5 / 7);
        window.setAttributes(params);
        dialog.show();
    }
}
