package com.autosos.rescue.view;

import android.app.Activity;
import android.content.Context;
import android.content.Intent;
import android.content.SharedPreferences;
import android.graphics.Bitmap;
import android.graphics.Canvas;
import android.graphics.Paint;
import android.os.AsyncTask;
import android.os.Bundle;
import android.os.Handler;
import android.os.Message;
import android.util.Log;
import android.view.KeyEvent;
import android.view.View;
import android.widget.Button;
import android.widget.ProgressBar;
import android.widget.RatingBar;
import android.widget.TextView;
import android.widget.Toast;

import com.autosos.rescue.Constants;
import com.autosos.rescue.R;
import com.autosos.rescue.model.NewOrder;
import com.autosos.rescue.model.OrderInfo;
import com.autosos.rescue.task.NewHttpPostTask;
import com.autosos.rescue.task.OnHttpRequestListener;
import com.autosos.rescue.util.BitmapCompressUtil;
import com.autosos.rescue.util.FileUtil;
import com.autosos.rescue.util.JSONUtil;
import com.autosos.rescue.util.SignPaintView;
import com.autosos.rescue.widget.MyProgress_QX;
import com.qiniu.android.http.ResponseInfo;
import com.qiniu.android.storage.UpCompletionHandler;
import com.qiniu.android.storage.UpProgressHandler;
import com.qiniu.android.storage.UploadManager;
import com.qiniu.android.storage.UploadOptions;
import com.umeng.analytics.MobclickAgent;

import org.json.JSONException;
import org.json.JSONObject;

import java.io.File;
import java.io.FileNotFoundException;
import java.io.FileOutputStream;
import java.io.IOException;
import java.util.HashMap;
import java.util.Map;


/**
 * Created by Administrator on 2016/1/8.
 */
public class AppraiseActivity extends Activity implements View.OnClickListener {

    Button btn_submit;

    private int orderId;
    private SignPaintView signPaintView;
    private RatingBar rb_level;
    private int comment ;
    private Bitmap bitmap;
    private Handler mHandler;
    private Paint paint;
    private Canvas canvas;
    private View uploadLayout;
    private MyProgress_QX pgsBar;

    private ProgressBar progressBar;

    private SharedPreferences sp;
    private boolean isClicked = false;

    //private static boolean isCommentSubmit;



    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        super.setContentView(R.layout.activity_appraise);
        initWiget();
        initListener();

    }


    private void initWiget() {

        btn_submit = (Button) findViewById(R.id.btn_submit);

        rb_level = (RatingBar) findViewById(R.id.rb_level);
        rb_level.setProgress(5);

        uploadLayout = findViewById(R.id.upload_layout);
        pgsBar = (MyProgress_QX) findViewById(R.id.pgsBar);


        signPaintView = (SignPaintView) findViewById(R.id.sign_tablet);
        signPaintView.setActivity(this);


        mHandler = new Handler(new Handler.Callback() {

            @Override
            public boolean handleMessage(Message msg) {

                switch (msg.what) {
                    case 1:
                        Map<String, Object> map = new HashMap<>();
                        map.put("pics", msg.obj.toString());
                        map.put("rating",comment);
                        map.put("cloud_type", "2");
                        new NewHttpPostTask(AppraiseActivity.this, new OnHttpRequestListener() {
                            @Override
                            public void onRequestCompleted(Object obj) {
                                Log.e("photo", obj.toString());
                                try {
                                    JSONObject jsonObject = new JSONObject(obj.toString());
                                    if (!jsonObject.isNull("result")) {
                                        int result = jsonObject.optInt("result");
                                        Log.e("photo", result + "");
                                        if (result == 1) {

                                            Intent intent = new Intent(AppraiseActivity.this,PayActivity.class);
                                            startActivity(intent);

                                        }else{


                                        }
                                    }
                                } catch (Exception e) {

                                }

                            }

                            @Override
                            public void onRequestFailed(Object obj) {

                                Toast.makeText(AppraiseActivity.this,"网络环境不太好,请重新提交",Toast.LENGTH_SHORT).show();
                                AppraiseActivity.this.recreate();

                            }
                        }).execute(String.format(Constants.UPLOAD_COMMENT_SIGN, orderId), map);

                        break;
                    case 2:
                        double per = (double) msg.obj;
                        int percent = (int) per;

                        if (percent > pgsBar.getProgress() || pgsBar.getProgress() < 98) {
                            pgsBar.setProgress(percent);
                        }
                        break;
                    case 3:
                        pgsBar.setProgress(100);
                        break;
                }

                return false;
            }
        });




    }

    private void initListener() {

        btn_submit.setOnClickListener(this);
    }




    @Override
    public void onClick(View v) {

        switch (v.getId()) {

            case R.id.btn_submit:

                comment = (int)rb_level.getRating();
                //saveSignImage(bitmap);
             //   bitmap = signPaintView.getPaintBitmap();
                bitmap = signPaintView.getPaintBitmap();
               // if(!isClicked){

                    if(signPaintView.isSign){
                       //  isCommentSubmit = true;
                        //  Toast.makeText(this,"签名上传"+ comment,Toast.LENGTH_SHORT).show();
                        new GetQiniuTokenTask().executeOnExecutor(Constants.THEADPOOL,
                                Constants.QINIU_TOKEN_URL);
                        uploadLayout.setVisibility(View.VISIBLE);
                        Toast.makeText(this,"提交成功,请稍等。",Toast.LENGTH_SHORT).show();
                        btn_submit.setClickable(false);
                        //isClicked =true;
                    }else{
                        btn_submit.setBackgroundResource(R.drawable.bg_btn_selector_new_way);
                        Toast.makeText(this,"请您评价并签名,谢谢！",Toast.LENGTH_SHORT).show();
                    }

//                }else{
//
//                    Toast.makeText(this,"请不要重复提交。",Toast.LENGTH_SHORT).show();
//                }


                break;


            default:
                break;
        }

    }

    @Override
    protected void onResume() {
        super.onResume();
        SharedPreferences sp = getSharedPreferences("order", Context.MODE_PRIVATE);
        String s_order = sp.getString("order", null);
        JSONObject order;
        if (s_order != null) {
            try {
                order = new JSONObject(s_order);
                NewOrder od = new NewOrder(order);
                orderId = od.getOrderId();
                Log.d("orderId",orderId+"");

            } catch (Exception e) {


            }

        }else{

            SharedPreferences sp2 = getSharedPreferences("orderInfo", Context.MODE_PRIVATE);
            String s_orderInfo = sp2.getString("orderInfo", null);
            try {
                order = new JSONObject(s_orderInfo);
                OrderInfo od = new OrderInfo(order);
                orderId = od.getOrderId();
                Log.d("orderId",orderId+"");

            } catch (Exception e) {


            }
        }
        MobclickAgent.onResume(this);
    }


    private class GetQiniuTokenTask extends AsyncTask<String, Object, String> {

        @Override
        protected String doInBackground(String... params) {
            try {
                String jsonStr = JSONUtil.getStringFromUrl(AppraiseActivity.this, params[0]);
                if (JSONUtil.isEmpty(jsonStr)) {
                    return null;
                }
                return jsonStr;
            } catch (IOException e) {

            }
            return null;
        }

        @Override
        protected void onPostExecute(String result) {
            if (result != null) {
                UploadManager upManager = new UploadManager();
                try {
                    byte[] picByte = BitmapCompressUtil.bitmap2Bytes(bitmap, 60);
                    upManager.put(picByte, null, result, new UpCompletionHandler() {
                        @Override
                        public void complete(String key, ResponseInfo responseInfo, JSONObject jsonObject) {

                            if (!jsonObject.isNull("hash")) {
                                String hash = jsonObject.optString("hash");
                                Message message = new Message();
                                message.what = 1;
                                message.obj = hash;
                                mHandler.sendMessage(message);

                                Message message2 = new Message();
                                message2.what = 3;
                                message2.obj = hash;
                                mHandler.sendMessage(message2);
                            }
                        }
                }, new UploadOptions(null, "image/jpeg", false,
                            new UpProgressHandler(){
                                public void progress(String key, double percent){
                                    Message message = new Message();
                                    message.what = 2;
                                    message.obj = percent * 100;
                                    mHandler.sendMessage(message);
                                }
                            }, null));

                } catch (Exception e) {
                    e.printStackTrace();
                }
            }
            super.onPostExecute(result);
        }
    }






    //按返回键 没有办法返回
    @Override
    public boolean onKeyDown(int keyCode, KeyEvent event) {

        if (keyCode == KeyEvent.KEYCODE_BACK) {
            return true;
        }
        return false;
    }

    /**
     * 保存图片
     * @param bitmap
     */
    public void saveSignImage(Bitmap bitmap){
        try {

            File jpgFile = FileUtil.createImageFile();
            FileOutputStream fos = new FileOutputStream(jpgFile);
            bitmap.compress(Bitmap.CompressFormat.JPEG, 100, fos);
            fos.flush();
            fos.close();
        } catch (FileNotFoundException e) {
            e.printStackTrace();
        } catch (IOException e) {
            e.printStackTrace();
        }
    }
}
