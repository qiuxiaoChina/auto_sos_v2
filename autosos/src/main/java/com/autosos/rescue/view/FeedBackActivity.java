package com.autosos.rescue.view;

import android.app.Activity;
import android.content.Intent;
import android.net.Uri;
import android.os.Bundle;
import android.os.Handler;
import android.view.TouchDelegate;
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

import org.json.JSONObject;

import java.util.HashMap;
import java.util.Map;

public class FeedBackActivity extends Activity implements View.OnClickListener{

    private ImageView back_button;
    private Button btn_feedback;
    private EditText et_feedback;
    private View progressBar;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_feed_back);
        et_feedback = (EditText) findViewById(R.id.et_feedback);
        progressBar = findViewById(R.id.progressBar);
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
                String s_feedBack = et_feedback.getText().toString();
                progressBar.setVisibility(View.VISIBLE);
                if (s_feedBack != null && !"".equals(s_feedBack)) {

                    Toast.makeText(this,"正在提交,请稍等...",Toast.LENGTH_SHORT).show();
                    Map<String,String> map = new HashMap<String,String>();
                    map.put("feedback",s_feedBack);
                    new NewHttpPostTask(getApplicationContext(), new OnHttpRequestListener() {
                        @Override
                        public void onRequestCompleted(Object obj) {


                            try {
                                JSONObject jsonObject = new JSONObject(obj.toString());

                                if (!jsonObject.isNull("result")) {

                                    int result = jsonObject.optInt("result");
                                    if (result == 1) {
                                        Toast.makeText(FeedBackActivity.this, "提交成功,我们将马上答复您", Toast.LENGTH_SHORT).show();
                                        new Handler().postDelayed(
                                                new Runnable() {
                                                    @Override
                                                    public void run() {
                                                        progressBar.setVisibility(View.GONE);
                                                        finish();
                                                    }
                                                }
                                        ,1500);

                                    }else {
                                        progressBar.setVisibility(View.GONE);
                                        Toast.makeText(FeedBackActivity.this, "网络环境不太好,没有提交成功", Toast.LENGTH_SHORT).show();
                                        FeedBackActivity.this.recreate();

                                    }

                                }else{
                                    progressBar.setVisibility(View.GONE);
                                    Toast.makeText(FeedBackActivity.this, "网络环境不太好,没有提交成功", Toast.LENGTH_SHORT).show();
                                    FeedBackActivity.this.recreate();

                                }

                            }catch (Exception e){


                            }

                        }

                        @Override
                        public void onRequestFailed(Object obj) {

                        }
                    }).execute(Constants.FEED_BACK_URL,map);

                }else{
                    progressBar.setVisibility(View.GONE);
                    Toast.makeText(this,"反馈内容不能为空",Toast.LENGTH_SHORT).show();
                }

                break;

        }
    }
}
