package com.autosos.yd.view;

import android.app.Activity;
import android.app.Dialog;
import android.content.DialogInterface;
import android.content.Intent;
import android.graphics.Point;
import android.os.Bundle;
import android.util.Log;
import android.view.KeyEvent;
import android.view.View;
import android.view.Window;
import android.view.WindowManager;
import android.widget.Button;
import android.widget.EdgeEffect;
import android.widget.EditText;
import android.widget.TextView;
import android.widget.Toast;

import com.autosos.yd.Constants;
import com.autosos.yd.R;
import com.autosos.yd.util.JSONUtil;
import com.autosos.yd.util.UpdateStateServe;
import com.autosos.yd.widget.DialogView;

import org.json.JSONObject;

import java.util.HashMap;
import java.util.Map;

/**
 * Created by Administrator on 2015/8/13.
 */
public class PasswordActivity extends AutososBackActivity implements View.OnClickListener{
    private EditText old_passwordView;
    private EditText new_passwordView;
    private EditText new_passwordAgainView;
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_seting_password);
        old_passwordView = (EditText)findViewById(R.id.old_password);
        new_passwordView = (EditText)findViewById(R.id.new_password);
        new_passwordAgainView = (EditText)findViewById(R.id.new_password_again);
    }
    public void changePassword(View view){
        String old_psd ="";
        old_psd  = old_passwordView.getText().toString();
        String new_psd ="";
        new_psd  = new_passwordView.getText().toString();
        String new_psd_t ="";
        new_psd_t= new_passwordAgainView.getText().toString();
        if(old_psd.length() == 0 ||old_psd == null){
            showNoPsdDialog(R.string.msg_enter_psd);
        }
        else if(new_psd == null || new_psd.length() == 0 ||new_psd_t ==null || new_psd_t.length() ==0){
            showNoPsdDialog(R.string.msg_psd_error1);
        }
        else if(!new_psd.equals(new_psd_t)){
            showNoPsdDialog(R.string.msg_psd_error2);
        }
        else if(new_psd.length() < 6 || new_psd_t.length() < 6){
            showNoPsdDialog(R.string.msg_psd_error3);
        }
        else if(old_psd.equals(new_psd)){
            showNoPsdDialog(R.string.msg_psd_error4);
        }
        else {
            findViewById(R.id.progressBar).setVisibility(View.VISIBLE);
            changepassword(old_psd,new_psd);
        }
    }

    public void showNoPsdDialog(final int msg){
        View v2 = getLayoutInflater().inflate(R.layout.dialog_msg_content,
                null);
        final Dialog dialog = new Dialog(v2.getContext(), R.style.bubble_dialog);
        Button  tvConfirm = (Button) v2.findViewById(R.id.btn_notice_confirm);
        TextView tvMsg = (TextView) v2.findViewById(R.id.tv_notice_msg);
        tvMsg.setText(String.format(getString(msg)));
        tvConfirm.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                dialog.dismiss();
                if(msg == R.string.msg_psd_succeed){
                    exit();
                }
            }
        });
        dialog.setContentView(v2);
        dialog.setCanceledOnTouchOutside(false);
        Window window = dialog.getWindow();
        WindowManager.LayoutParams params = window.getAttributes();
        Point point = JSONUtil.getDeviceSize(PasswordActivity.this);
        params.width = Math.round(point.x * 5 / 7);
        window.setAttributes(params);
        dialog.show();
        dialog.setOnCancelListener(new DialogInterface.OnCancelListener() {
            @Override
            public void onCancel(DialogInterface dialog) {
                exit();
            }
        });
    }

    private void changepassword(String old_psd,String new_psd){
        Map<String, Object> map = new HashMap<>();
        map.put("origin_password",old_psd);
        map.put("new_password", new_psd);
        new com.autosos.yd.task.NewHttpPutTask(PasswordActivity.this, new com.autosos.yd.task.OnHttpRequestListener() {
            @Override
            public void onRequestCompleted(Object obj) {
                try {
                    JSONObject jsonObject = new JSONObject(obj.toString());
                    Log.e("ChangPSD", jsonObject.toString());
                    if (!jsonObject.isNull("result")) {
                        int result = jsonObject.optInt("result");
                        if(result == 1){
                          showNoPsdDialog(R.string.msg_psd_succeed);
                        }else{
                            String msg = jsonObject.optString("msg");
                            findViewById(R.id.progressBar).setVisibility(View.GONE);
                            Toast.makeText(PasswordActivity.this,msg,Toast.LENGTH_LONG).show();
                        }
                    }
                }catch (Exception e ){
                    e.printStackTrace();
                }
            }
            @Override
            public void onRequestFailed(Object obj) {

            }
        }).execute(String.format(Constants.CHANGE_PASSWORD), map);
    }

    @Override
    public void onClick(View v) {
        switch (v.getId()) {

        }
    }

    public void onBackPressed() {
        super.onBackPressed();
        overridePendingTransition(0, R.anim.slide_out_right);
    }
 private void exit(){
     findViewById(R.id.progressBar).setVisibility(View.GONE);
     //Toast.makeText(PasswordActivity.this,R.string.msg_psd_succeed,Toast.LENGTH_LONG).show();
     Intent intent = new Intent(PasswordActivity.this, LoginActivity.class);
     intent.setFlags(Intent.FLAG_ACTIVITY_CLEAR_TOP);
     intent.putExtra("logout", true);
     Intent stopIntent = new Intent(PasswordActivity.this, UpdateStateServe.class);
     stopIntent.setFlags(Intent.FLAG_ACTIVITY_CLEAR_TOP);
     stopService(stopIntent);
     startActivity(intent);
     overridePendingTransition(R.anim.slide_in_up, R.anim.activity_anim_default);
     //setResult(RESULT_OK, intent);
     MainActivity.Mainactivity.finish();
     finish();
 }
}
