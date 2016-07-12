package com.autosos.rescue.view;

import android.app.Activity;
import android.content.Intent;
import android.os.Bundle;
import android.util.Log;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.ImageView;
import android.widget.ProgressBar;
import android.widget.Toast;

import com.autosos.rescue.Constants;
import com.autosos.rescue.R;
import com.autosos.rescue.task.NewHttpPostTask;
import com.autosos.rescue.task.OnHttpRequestListener;
import com.autosos.rescue.util.Session;

import org.json.JSONException;
import org.json.JSONObject;

import java.util.HashMap;
import java.util.Map;

public class ChangePwdActivity extends Activity implements View.OnClickListener{

    private ImageView back_button;
    private Button changePwd;
    private EditText password,newpassword,newpassword1;
    private ProgressBar progressBar;
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_change_pwd);
        back_button = (ImageView) findViewById(R.id.back_button);
        back_button.setOnClickListener(this);

        changePwd = (Button) findViewById(R.id.btn_changePwd);
        changePwd.setOnClickListener(this);

        password = (EditText) findViewById(R.id.password);
        newpassword = (EditText) findViewById(R.id.new_password);
        newpassword1 = (EditText) findViewById(R.id.new_password1);

        progressBar = (ProgressBar) findViewById(R.id.progressBar);
    }

    @Override
    public void onClick(View v) {

        switch (v.getId()){
            case R.id.back_button:
                finish();
                break;
            case R.id.btn_changePwd:
                String s_new_p = newpassword.getText().toString();
                String s_new_p1 = newpassword1.getText().toString();
                String p = password.getText().toString();
                if("".equals(p)||"".equals(s_new_p)||"".equals(s_new_p1)){

                    Toast.makeText(this,"请输入完整的信息",Toast.LENGTH_SHORT).show();
                }else{

                    if(s_new_p.equals(s_new_p1)){
                        progressBar.setVisibility(View.VISIBLE);
                        Log.d("testP",p+"--"+s_new_p);
                        Map<String,String> map = new HashMap<String,String>();
                        map.put("origin_password",p);
                        map.put("new_password",s_new_p);
                        new NewHttpPostTask(getApplicationContext(), new OnHttpRequestListener() {
                            @Override
                            public void onRequestCompleted(Object obj) {

                                JSONObject jsonObject = null;
                                try {
                                    jsonObject = new JSONObject(obj.toString());
                                    if (!jsonObject.isNull("result")) {

                                        int result = jsonObject.optInt("result");
                                        if(result ==1){

                                            progressBar.setVisibility(View.GONE);
                                            Toast.makeText(getApplicationContext(),"修改成功,请重新登录",Toast.LENGTH_SHORT).show();
                                            Session.getInstance().logout(getApplicationContext());
                                            Intent i = new Intent(getApplicationContext(),SplashActivity.class);
                                            startActivity(i);
                                            finish();

                                        }else{

                                            progressBar.setVisibility(View.GONE);
                                            if(jsonObject.optInt("code")==1){

                                                Toast.makeText(getApplicationContext(),"原密码错误,修改失败",Toast.LENGTH_SHORT).show();

                                             }else if(jsonObject.optInt("code")==2){

                                                Toast.makeText(getApplicationContext(),"新密码格式错误,修改失败",Toast.LENGTH_SHORT).show();

                                            }else{

                                                Toast.makeText(getApplicationContext(),"出错啦,修改失败",Toast.LENGTH_SHORT).show();

                                            }

                                        }

                                    }
                                } catch (JSONException e) {
                                    e.printStackTrace();
                                }



                            }

                            @Override
                            public void onRequestFailed(Object obj) {

                                progressBar.setVisibility(View.GONE);
                                Toast.makeText(getApplicationContext(),"修改失败,请重新提交请求",Toast.LENGTH_SHORT).show();

                            }
                        }).execute(Constants.CHANGE_PASSWORD,map);

                    }else{

                        Toast.makeText(this,"新密码不相同,请重新输入",Toast.LENGTH_SHORT).show();

                    }
                }

                break;
            default:
                break;

        }
    }
}
