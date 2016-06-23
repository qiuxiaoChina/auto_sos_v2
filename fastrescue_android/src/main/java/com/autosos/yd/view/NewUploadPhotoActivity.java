package com.autosos.yd.view;

import android.app.Activity;
import android.content.Context;
import android.content.Intent;
import android.content.SharedPreferences;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.os.AsyncTask;
import android.os.Bundle;
import android.os.Handler;
import android.os.Message;
import android.util.Log;
import android.view.KeyEvent;
import android.view.View;
import android.widget.Button;
import android.widget.ImageView;
import android.widget.Toast;

import com.autosos.yd.Constants;
import com.autosos.yd.R;
import com.autosos.yd.model.NewOrder;
import com.autosos.yd.model.OrderDetail;
import com.autosos.yd.model.OrderInfo;
import com.autosos.yd.task.HttpGetTask;
import com.autosos.yd.task.NewHttpPostTask;
import com.autosos.yd.task.OnHttpRequestListener;
import com.autosos.yd.util.BitmapCompressUtil;
import com.autosos.yd.util.JSONUtil;
import com.autosos.yd.widget.MyProgress_QX;
import com.qiniu.android.http.ResponseInfo;
import com.qiniu.android.storage.UpCompletionHandler;
import com.qiniu.android.storage.UpProgressHandler;
import com.qiniu.android.storage.UploadManager;
import com.qiniu.android.storage.UploadOptions;
import com.umeng.analytics.MobclickAgent;

import org.json.JSONException;
import org.json.JSONObject;

import java.io.ByteArrayInputStream;
import java.io.ByteArrayOutputStream;
import java.io.File;
import java.io.FileInputStream;
import java.io.FileNotFoundException;
import java.io.IOException;
import java.util.HashMap;
import java.util.Map;

/**
 * Created by Administrator on 2016/3/2.
 */
public class NewUploadPhotoActivity extends Activity implements View.OnClickListener {

    private ImageView photo_preview;
    private Button rephoto, uploadPhoto;
    private View uploadLayout;
    private MyProgress_QX pgsBar;
    private Handler mHandler;
    private int orderId;
    private String tuocheTaken = null;
    private String tuocheTaken2 = null;
    private Bitmap bitmap;
    private String path;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_uploadphoto);
        final Intent intent = getIntent();
        path = intent.getStringExtra("imagePath");
        photo_preview = (ImageView) findViewById(R.id.photo_preview);
        bitmap =  getBitmap(path);
        photo_preview.setImageBitmap(bitmap);

        rephoto = (Button) findViewById(R.id.rephoto);
        uploadPhoto = (Button) findViewById(R.id.upload_photo);
        rephoto.setOnClickListener(this);
        uploadPhoto.setOnClickListener(this);

        uploadLayout = findViewById(R.id.upload_layout);
        pgsBar = (MyProgress_QX) findViewById(R.id.pgsBar);


        mHandler = new Handler(new Handler.Callback() {

            @Override
            public boolean handleMessage(Message msg) {

                switch (msg.what) {
                    case 1:

                        Map<String, Object> map = new HashMap<>();
                        map.put("pics", msg.obj.toString());
                        //  Log.e(TAG,"buzhidao :"+sbpics.toString());
                        map.put("cloud_type", "2");
                        new NewHttpPostTask(NewUploadPhotoActivity.this, new OnHttpRequestListener() {
                            @Override
                            public void onRequestCompleted(Object obj) {
                                Log.e("photo", obj.toString());
                                try {
                                    JSONObject jsonObject = new JSONObject(obj.toString());
                                    if (!jsonObject.isNull("result")) {
                                        int result = jsonObject.optInt("result");
                                        Log.e("photo", result + "");
                                        if (result == 1) {
                                            new HttpGetTask(NewUploadPhotoActivity.this, new OnHttpRequestListener() {
                                                @Override
                                                public void onRequestCompleted(Object obj) {
                                                    JSONObject jsonObject;
                                                    try {
                                                        jsonObject = new JSONObject(obj.toString());
                                                        int result = jsonObject.optInt("result");
                                                        int code = jsonObject.optInt("code");
                                                        Log.d("upload_status",code+"---"+result);
                                                        if(code == 0){//code 为 0 不是拖车服务

                                                            if(result == 5 ){//拍完照片跳到评价页面

                                                                Intent intent = new Intent(NewUploadPhotoActivity.this,AppraiseActivity.class);
                                                                startActivity(intent);
                                                                finish();

                                                            }

                                                        }else{//code 为 1 拖车服务

                                                            if(result == 5){


                                                            }else if(result == 6){


                                                            }else  if(result == 22){


                                                            }

                                                        }

                                                    } catch (JSONException e) {
                                                        e.printStackTrace();
                                                    }
                                                }

                                                @Override
                                                public void onRequestFailed(Object obj) {

                                                }
                                            }).execute(String.format(Constants.CHECK_ORDER_STATUS_URL,orderId));
                                        }
                                    }
                                } catch (Exception e) {

                                }
                            }

                            @Override
                            public void onRequestFailed(Object obj) {
                                Toast.makeText(NewUploadPhotoActivity.this, "网络环境不太好,请重新上传", Toast.LENGTH_SHORT).show();
                                NewUploadPhotoActivity.this.recreate();
                            }
                        }).execute(String.format(Constants.UPLOAD_PICS_URL, orderId), map);


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

    /**
     * 加载本地图片
     *
     * @param url
     * @return
     */
    public static Bitmap getLoacalBitmap(String url) {
        try {
            FileInputStream fis = new FileInputStream(url);
            return BitmapFactory.decodeStream(fis);  ///把流转化为Bitmap图片

        } catch (FileNotFoundException e) {
            e.printStackTrace();
            return null;
        }
    }

    @Override
    public void onClick(View v) {

        switch (v.getId()) {
            case R.id.rephoto:
                finish();
                break;

            case R.id.upload_photo:
                uploadLayout.setVisibility(View.VISIBLE);
                rephoto.setClickable(false);
                uploadPhoto.setClickable(false);
                new GetQiniuTokenTask().executeOnExecutor(Constants.THEADPOOL,
                        Constants.QINIU_TOKEN_URL);

                break;
            default:
                break;

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





    private class GetQiniuTokenTask extends AsyncTask<String, Object, String> {

        @Override
        protected String doInBackground(String... params) {
            try {
                String jsonStr = JSONUtil.getStringFromUrl(com.autosos.yd.view.NewUploadPhotoActivity.this, params[0]);
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

                    Bitmap bm = getBitmap(path);
                   // Bitmap bm = getBitmap(path);
                    byte[] picByte = BitmapCompressUtil.bitmap2Bytes(bm, 75);
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
                            new UpProgressHandler() {
                                public void progress(String key, double percent) {
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

    private Bitmap getBitmap(String path)   {

        Bitmap bm = null;

        BitmapFactory.Options bfOptions=new BitmapFactory.Options();
        bfOptions.inDither=false;
        bfOptions.inPurgeable=true;
        bfOptions.inInputShareable=true;
        bfOptions.inTempStorage=new byte[12 * 1024];


        File file=new File(path);
        FileInputStream fs=null;
        try {
            fs = new FileInputStream(file);
        } catch (FileNotFoundException e) {
            e.printStackTrace();
        }

        try {
            if(fs!=null)
                bm = BitmapFactory.decodeFileDescriptor(fs.getFD(), null, bfOptions);
        } catch (IOException e) {
            e.printStackTrace();
        } finally{
            if(fs!=null) {
                try {
                    fs.close();
                } catch (IOException e) {
                    e.printStackTrace();
                }
            }
        }
        return bm;
    }


    private Bitmap getimage(String srcPath) {
        BitmapFactory.Options newOpts = new BitmapFactory.Options();
//
//        newOpts.inJustDecodeBounds = true;
//        newOpts.inJustDecodeBounds = false;
//        int w = newOpts.outWidth;
//        int h = newOpts.outHeight;
//        float hh = 576f;
//        float ww = 1024f;
//        int be = 1;
//        int b2 = 1;
//        if (w > h && w > ww) {
//            be = (int) (newOpts.outWidth / ww) + 1;
//        }
//        if (w < h && h > hh) {
//            b2 = (int) (newOpts.outHeight / hh) + 1;
//        }
//        if (be < b2) {
//            be = b2;
//        }
//        if (be <= 0)
//            be = 1;
//        newOpts.inSampleSize = be;

         Bitmap bm = BitmapFactory.decodeFile(srcPath, newOpts);
        return bm;
    }

    private Bitmap compressImage(Bitmap image) {
        ByteArrayOutputStream baos = new ByteArrayOutputStream();
        image.compress(Bitmap.CompressFormat.JPEG, 90, baos);
       /* int options = 100;
        while ( baos.toByteArray().length / 1024 > 60) {
            baos.reset();
            options -= 10;
            image.compress(Bitmap.CompressFormat.JPEG, options, baos);
        }
        */
        ByteArrayInputStream isBm = new ByteArrayInputStream(baos.toByteArray());
        Bitmap bitmap = BitmapFactory.decodeStream(isBm, null, null);
        return bitmap;
    }
}
