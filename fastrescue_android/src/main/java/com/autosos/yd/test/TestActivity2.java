package com.autosos.yd.test;

import android.app.Activity;
import android.app.Dialog;
import android.content.Context;
import android.content.Intent;
import android.content.SharedPreferences;
import android.graphics.Point;
import android.os.AsyncTask;
import android.os.Bundle;
import android.os.Environment;
import android.os.Handler;
import android.os.Message;
import android.util.Log;
import android.view.View;
import android.view.Window;
import android.view.WindowManager;
import android.widget.Button;
import android.widget.ImageView;
import android.widget.TextView;

import com.autosos.yd.Constants;
import com.autosos.yd.R;
import com.autosos.yd.model.Balance;
import com.autosos.yd.model.LastestLog;
import com.autosos.yd.model.OrderInfo;
import com.autosos.yd.model.User;
import com.autosos.yd.util.DistanceUtil;
import com.autosos.yd.util.JSONUtil;
import com.autosos.yd.util.MusicUtil;
import com.autosos.yd.util.UpdateStateServe;
import com.autosos.yd.view.MainActivity;
import com.google.gson.Gson;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import java.io.BufferedWriter;
import java.io.ByteArrayOutputStream;
import java.io.File;
import java.io.FileInputStream;
import java.io.FileNotFoundException;
import java.io.FileOutputStream;
import java.io.IOException;
import java.io.InputStream;
import java.io.OutputStreamWriter;

/**
 * Created by Administrator on 2015/11/9.
 */
public class TestActivity2 extends Activity implements Thread.UncaughtExceptionHandler{

    private Button ontest;


    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.test2_activity);
        ontest = (Button) findViewById(R.id.test);



    }

    public void ontest(){

    }

    @Override
    protected void onResume() {
        super.onResume();
//        new GetAccountInfoTask().execute(Constants.GET_BALANCE);
    }

    @Override
    public void uncaughtException(Thread thread, Throwable ex) {
        System.out.println("uncaughtException");
        System.exit(0);
        Intent intent = new Intent(this, MainActivity.class);
        intent.addFlags(Intent.FLAG_ACTIVITY_CLEAR_TOP |
                Intent.FLAG_ACTIVITY_NEW_TASK);
        startActivity(intent);
    }

    private class GetAccountInfoTask extends AsyncTask<String, Object, JSONObject> {

        @Override
        protected JSONObject doInBackground(String... params) {
            try {
                String jsonStr = JSONUtil.getStringFromUrl(TestActivity2.this, params[0] );
                if (JSONUtil.isEmpty(jsonStr)){
                    return null;
                }
                Log.e("test", jsonStr);
                return new JSONObject(jsonStr);
            } catch (IOException | JSONException e) {

            }
            return null;

        }

        @Override
        protected void onPostExecute(JSONObject result) {
            super.onPostExecute(result);
            Log.e("test", result.toString());
            Gson gson = new Gson();
            Balance balance = gson.fromJson(result.toString(),Balance.class);
            balance.getBalance();
            Log.e("test",balance.getBalance());
        }
    }

}
