package com.autosos.yd.view;

import android.app.Activity;
import android.content.Context;
import android.content.Intent;
import android.content.SharedPreferences;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.graphics.Point;
import android.graphics.drawable.Drawable;
import android.os.AsyncTask;
import android.os.Bundle;
import android.os.Handler;
import android.os.Message;
import android.util.DisplayMetrics;
import android.util.Log;
import android.view.Gravity;
import android.view.LayoutInflater;
import android.view.MotionEvent;
import android.view.View;
import android.view.ViewGroup;
import android.widget.AdapterView;
import android.widget.Button;
import android.widget.GridView;
import android.widget.ImageView;
import android.widget.PopupWindow;
import android.widget.ProgressBar;
import android.widget.TextView;
import android.widget.Toast;

import com.autosos.yd.model.OrderInfo;
import com.autosos.yd.util.MusicUtil;
import com.autosos.yd.widget.CherkInternet;
import com.autosos.yd.widget.DialogView;
import com.qiniu.android.http.ResponseInfo;
import com.qiniu.android.storage.UpCompletionHandler;
import com.qiniu.android.storage.UpProgressHandler;
import com.qiniu.android.storage.UploadManager;
import com.qiniu.android.storage.UploadOptions;
import com.umeng.analytics.MobclickAgent;

import org.json.JSONObject;

import java.io.ByteArrayInputStream;
import java.io.ByteArrayOutputStream;
import java.io.IOException;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.HashSet;
import java.util.Iterator;
import java.util.List;
import java.util.Map;
import java.util.Set;
import java.util.UUID;

import com.autosos.yd.Constants;
import com.autosos.yd.R;
import com.autosos.yd.adapter.ObjectBindAdapter;
import com.autosos.yd.fragment.WorkFragment;
import com.autosos.yd.model.Item;
import com.autosos.yd.model.JsonPic;
import com.autosos.yd.task.AsyncBitmapDrawable;
import com.autosos.yd.task.ImageLoadTask;
import com.autosos.yd.task.NewHttpPostTask;
import com.autosos.yd.task.OnHttpRequestListener;
import com.autosos.yd.util.BitmapCompressUtil;
import com.autosos.yd.util.JSONUtil;
import com.autosos.yd.util.ScaleMode;
import com.autosos.yd.view.*;
import com.autosos.yd.view.AutososBackActivity;
import com.autosos.yd.view.MainActivity;

public class UploadPhotoActivity extends AutososBackActivity implements ObjectBindAdapter.ViewBinder<JsonPic>
        , AdapterView.OnItemClickListener, View.OnClickListener {

    private static final String TAG = "UploadPhotoActivity";
    private final static int MSG_PHOTO_UPLOADED = 1;
    private ArrayList<JsonPic> photos;
    private ArrayList<JsonPic> jsonPics;
    private ObjectBindAdapter<JsonPic> adapter;
    private JsonPic emptyPic;
    private int imageSize;
    private View imagesLayout;
    private int count = 0;
    private StringBuilder sbpics;
    private View progressBar;
    private long id;
    private int pics_length;
    private Button submitBtn;
    private Map<String, byte[]> bitmapMaps;
    private TextView progressView;
    private ProgressBar progressBarView;
    private int chaju;
    private OrderInfo orderInfo;
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_upload_photo);
        id = getIntent().getLongExtra("id", 0);
        sbpics = new StringBuilder();
        orderInfo = (OrderInfo) getIntent().getSerializableExtra("OrderInfo");
        chaju = 0;
        progressBarView = (ProgressBar)findViewById(R.id.progressBar_upload);
        progressView = (TextView)findViewById(R.id.progress_text);

        progressBar = findViewById(R.id.progressBar);
        DisplayMetrics dm = getResources().getDisplayMetrics();
        Point point = JSONUtil.getDeviceSize(this);
        imageSize = Math.round((point.x - 44 * dm.density) / 3);
        int imagesHeight = Math.round(point.x + 5 * dm.density);
        GridView imagesView = (GridView) findViewById(R.id.imagesView);
        View emptyView = findViewById(R.id.empty_view);
        imagesLayout = findViewById(R.id.images_layout);
        emptyView.setOnClickListener(this);
        photos = new ArrayList<>();
        jsonPics = new ArrayList<>();
        emptyPic = new JsonPic(new JSONObject());
        adapter = new ObjectBindAdapter<>(this, photos, R.layout.post_image_item, this);
        imagesLayout.getLayoutParams().height = imagesHeight;
        imagesView.setEmptyView(emptyView);
        imagesView.setOnItemClickListener(this);
        imagesView.setAdapter(adapter);
        submitBtn = (Button) findViewById(R.id.submit_btn);
       // Toast.makeText(this,R.string.msg_waring,Toast.LENGTH_LONG).show();
//        MusicUtil.playmusics(UploadPhotoActivity.this, MusicUtil.Take_three_photo);
//        showPopupWindow();
    }



    @Override
    public void onClick(View v) {
        switch (v.getId()) {
            case R.id.empty_view:
                Intent intent = new Intent(this, ChoosePhotoActivity.class);
                if (orderInfo.getService_type() == 1 || orderInfo.getService_type() == 2 && orderInfo != null){
                    intent.putExtra("limit", 1- jsonPics.size());
                }else{
                    intent.putExtra("limit", 3 - jsonPics.size());
                }
//                intent.putExtra("limit", 3 - jsonPics.size());
                intent.putExtra("OrderInfo", orderInfo);
                startActivityForResult(intent, Constants.RequestCode.PHOTO_FROM_GALLERY);
                overridePendingTransition(R.anim.slide_in_right, R.anim.activity_anim_default);
                break;
        }
    }

    @Override
    public void setViewValue(View view, JsonPic jsonPic, int position) {
        if (jsonPics.size() == 0) {
            submitBtn.setBackgroundResource(R.drawable.bg_shape_second_grav);
        }
        else{
            submitBtn.setBackgroundResource(R.drawable.bg_btn_second_green);
        }
        ViewHolder holder = (ViewHolder) view.getTag();
        if (holder == null) {
            holder = new ViewHolder();
            holder.addView = view.findViewById(R.id.add_btn);
            holder.deleteView = view.findViewById(R.id.delete);
            holder.imageView = (ImageView) view.findViewById(R.id.image);
            view.getLayoutParams().width = imageSize;
            view.getLayoutParams().height = imageSize;
            view.setTag(holder);
        }
        if (JSONUtil.isEmpty(jsonPic.getPath())) {
            holder.imageView.setVisibility(View.GONE);
            holder.deleteView.setVisibility(View.GONE);
            holder.addView.setVisibility(View.VISIBLE);
        } else {
            holder.addView.setVisibility(View.GONE);
            holder.imageView.setVisibility(View.VISIBLE);
            holder.deleteView.setVisibility(View.VISIBLE);
            holder.deleteView.setOnClickListener(new OnPhotoDeleteClickListener(jsonPic));
            String path = jsonPic.getPath();
            if (!path.equals(holder.imageView.getTag())) {
                ImageLoadTask task = new ImageLoadTask(holder.imageView, 0);
                holder.imageView.setTag(path);
                task.loadImage(path, imageSize, ScaleMode.WIDTH, new AsyncBitmapDrawable(getResources(),
                        Constants.PLACEHOLDER, task));
            }
        }
    }

    private class ViewHolder {
        View deleteView;
        View addView;
        ImageView imageView;
    }






    @Override
    public void onItemClick(AdapterView<?> parent, View view, int position, long id) {
        JsonPic jsonPic = (JsonPic) parent.getAdapter().getItem(position);
        if (jsonPic != null && JSONUtil.isEmpty(jsonPic.getPath())) {
            Intent intent = new Intent(this, ChoosePhotoActivity.class);
            if (orderInfo.getService_type() == 1 || orderInfo.getService_type() == 2 && orderInfo != null){
                intent.putExtra("limit", 1- jsonPics.size());
            }else{
                intent.putExtra("limit", 3 - jsonPics.size());
            }
//            intent.putExtra("limit", 3 - jsonPics.size());
            intent.putExtra("OrderInfo", orderInfo);
            startActivityForResult(intent, Constants.RequestCode.PHOTO_FROM_GALLERY);
            overridePendingTransition(R.anim.slide_in_right, R.anim.activity_anim_default);
        }
    }

    private Handler mHandler = new Handler() {
        @Override
        public void handleMessage(Message msg) {
            switch (msg.what) {
                case MSG_PHOTO_UPLOADED:
                    ++count;
                    if (count == 1) {
                        sbpics.append((String) msg.obj);
                    } else {
                        sbpics.append("," + msg.obj);
                    }
                    if (count == jsonPics.size()) {
                        Log.e(TAG, sbpics.toString());
                        Map<String, Object> map = new HashMap<>();
                        map.put("pics", sbpics.toString());
                      //  Log.e(TAG,"buzhidao :"+sbpics.toString());
                        map.put("cloud_type", "2");
                        new NewHttpPostTask(com.autosos.yd.view.UploadPhotoActivity.this, new OnHttpRequestListener() {
                            @Override
                            public void onRequestCompleted(Object obj) {
                                Log.e("photo", obj.toString());
                                try {
                                    JSONObject jsonObject = new JSONObject(obj.toString());
                                    if (!jsonObject.isNull("result")) {
                                        int result = jsonObject.optInt("result");
                                        Log.e("photo",result + "");
                                        if (result == 1) {
                                            progressBar.setVisibility(View.GONE);
                                            submitBtn.setEnabled(true);
                                            Intent intent;
                                            intent = new Intent();
                                            intent.putExtra("id",id);
                                            intent.setClass(com.autosos.yd.view.UploadPhotoActivity.this, FinishActivity.class);
                                            intent.setFlags(Intent.FLAG_ACTIVITY_CLEAR_TOP);
                                            startActivity(intent);
                                            finish();
                                            overridePendingTransition(0, R.anim.slide_out_right);
                                        }
                                    }
                                } catch (Exception e) {

                                }
                            }

                            @Override
                            public void onRequestFailed(Object obj) {
                                Log.e(TAG,"qingqiushibai");
                            }
                        }).execute(String.format(Constants.UPLOAD_PICS_URL, id), map);


                    }
                    break;
                case  2:
                    double per =(double) msg.obj;
                    int percent = (int) per;
                    /*
                    if(progressBarView.getProgress() < percent ){
                        if(percent >95){
                        }
                        else {
                            progressBarView.setProgress(percent);
                            progressView.setText(percent + "%");
                        }
                    }
                    */
                    //util2
                    if(percent > progressBarView.getProgress() || progressBarView.getProgress() < 98){
                        progressBarView.setProgress(percent);
                        progressView.setText(percent+"%");
                    }
                    break;
                case 3:
                    progressBarView.setProgress(100);
                    progressView.setText("100" + "%" + "OK");
                    break;
            }
        }
    };

    public void onSubmit(View view) {
        if (jsonPics.size() == 0) {
            Toast.makeText(com.autosos.yd.view.UploadPhotoActivity.this,
                    R.string.msg_choose_photo, Toast.LENGTH_SHORT).show();
            return;
        }
        if(!CherkInternet.cherkInternet(UploadPhotoActivity.this)){
            DialogView dialogView = new DialogView();
            dialogView.dialogInternet(this);
            return;
        }
       // progressBar.setVisibility(View.VISIBLE);
        submitBtn.setVisibility(View.GONE);
        progressView.setVisibility(View.VISIBLE);
        progressBarView.setVisibility(View.VISIBLE);
        submitBtn.setEnabled(false);
        pics_length = jsonPics.size();
        new GetQiniuTokenTask().executeOnExecutor(Constants.INFOTHEADPOOL,
                Constants.QINIU_TOKEN_URL);
    }

    private class GetQiniuTokenTask extends AsyncTask<String, Object, String> {

        @Override
        protected String doInBackground(String... params) {
            try {
                String jsonStr = JSONUtil.getStringFromUrl(com.autosos.yd.view.UploadPhotoActivity.this, params[0]);
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
                Log.e(TAG,"result   :"+ result);
                UploadManager upManager = new UploadManager();
                try {
                    for (JsonPic jsonPic : jsonPics) {
                        Bitmap bitmap = getimage(jsonPic.getPath());
                        byte[] picByte = BitmapCompressUtil.bitmap2Bytes(bitmap, 60);
                        upManager.put(picByte,null, result, new UpCompletionHandler() {
                            @Override
                            public void complete(String key, ResponseInfo responseInfo, JSONObject jsonObject) {
                                Log.e(TAG,"responseInfo   :"+responseInfo.toString());
                                if (!jsonObject.isNull("hash")) {
                                    String hash = jsonObject.optString("hash");
                                    Message message = new Message();
                                    message.what = MSG_PHOTO_UPLOADED;
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
                    }
                } catch (Exception e) {
                    e.printStackTrace();
                }
            }
            super.onPostExecute(result);
        }
    }


    private class OnPhotoDeleteClickListener implements View.OnClickListener {

        private JsonPic jsonPic;

        private OnPhotoDeleteClickListener(JsonPic jsonPic) {
            this.jsonPic = jsonPic;
        }

        @Override
        public void onClick(View v) {
            bitmapMaps.remove(jsonPic.getPath());
            photos.remove(jsonPic);
            jsonPics.remove(jsonPic);
            if (!photos.contains(emptyPic)) {
                photos.add(emptyPic);
            } else if (photos.size() < 2) {
                photos.clear();
            }
            adapter.notifyDataSetChanged();
            if (jsonPics.size() == 0) {
                submitBtn.setBackgroundResource(R.drawable.bg_shape_second_grav);
            }
            else{
                submitBtn.setBackgroundResource(R.drawable.bg_btn_second_green);
            }
        }
    }


    @Override
    public void onActivityResult(int requestCode, int resultCode, Intent data) {

//        Log.e(TAG,orderInfo.getOrder_type()+"");
        Log.e(TAG,"1111111111111111111111");
        if (resultCode == Activity.RESULT_OK) {
            switch (requestCode) {
                case Constants.RequestCode.PHOTO_FROM_GALLERY:
                    if (data != null) {
                        Log.e(TAG,"data != null");
                        orderInfo = (OrderInfo) data.getSerializableExtra("OrderInfo");
                        if (orderInfo !=null){
                            Log.e(TAG,orderInfo.getOrder_type()+"");
                        }
                        ArrayList<Item> selectedPhotos = (ArrayList<Item>) data
                                .getSerializableExtra("selectedPhotos");
                        if (selectedPhotos == null) {

                        } else {
                            bitmapMaps = new HashMap<>();
                            if (!photos.isEmpty()) {
                                photos.remove(emptyPic);
                            }
                            for (Item item : selectedPhotos) {
                                JsonPic jsonPic = new JsonPic(new JSONObject());
                                jsonPic.setHeight(item.getHeight());
                                jsonPic.setWidth(item.getWidth());
                                if(item.getMediaPath() == null || item.getMediaPath().length() <3 ){
                                    SharedPreferences sharedPreferences = getSharedPreferences("photo_root", Context.MODE_PRIVATE);
                                    String currentPath = sharedPreferences.getString("root", "");
                                    jsonPic.setPath(currentPath);
                                }
                                else
                                    jsonPic.setPath(item.getMediaPath());
                                photos.add(jsonPic);
                                jsonPics.add(jsonPic);
                            }
                            cherk();
                            if (orderInfo.getService_type() == 1 || orderInfo.getService_type() == 2){
                                if (!photos.isEmpty() && photos.size() < 1) {       //此处控制照片上传数量
                                    photos.add(emptyPic);
                                }
                            }else {
                                if (!photos.isEmpty() && photos.size() < 3) {
                                    photos.add(emptyPic);
                                }
                            }

                            adapter.notifyDataSetChanged();
                            progressBar.setVisibility(View.GONE);
                        }
                    }
                    break;
            }
        }
        super.onActivityResult(requestCode, resultCode, data);
    }
    private void cherk(){
        int size = photos.size();
        if(size == 3){
            String path1 = photos.get(0).getPath();
            String path2 = photos.get(1).getPath();
            String path3 = photos.get(2).getPath();
            if(path1.equals(path3)){
                photos.remove(2);
                jsonPics.remove(2);
                Toast.makeText(com.autosos.yd.view.UploadPhotoActivity.this,String.format(getString(R.string.msg_delect_pic)),Toast.LENGTH_SHORT).show();
            }
            else if(path1.equals(path2)){
                photos.remove(1);
                jsonPics.remove(1);
                Toast.makeText(com.autosos.yd.view.UploadPhotoActivity.this,String.format(getString(R.string.msg_delect_pic)),Toast.LENGTH_SHORT).show();
            }
            else if(path2.equals(path3)){
                photos.remove(2);
                jsonPics.remove(2);
                Toast.makeText(com.autosos.yd.view.UploadPhotoActivity.this,String.format(getString(R.string.msg_delect_pic)),Toast.LENGTH_SHORT).show();
            }
        }
        if(size == 2){
            String path1 = photos.get(0).getPath();
            String path2 = photos.get(1).getPath();
            if(path1.equals(path2)){
                photos.remove(1);
                jsonPics.remove(1);
                Toast.makeText(com.autosos.yd.view.UploadPhotoActivity.this,String.format(getString(R.string.msg_delect_pic)),Toast.LENGTH_SHORT).show();
            }
        }
    }
    private Bitmap getCompressedPictures(String filePath) {
        return BitmapCompressUtil.decodeSampledBitmapFromFile(
                getResources(), filePath, Bitmap.Config.RGB_565, 600, 450);
    }

    @Override
    public void onBackPressed() {
        super.onBackPressed();
        overridePendingTransition(0, R.anim.slide_out_right);
    }


    @Override
    protected void onDestroy() {
        super.onDestroy();
        mHandler.removeCallbacks(null);
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

    private Bitmap getimage(String srcPath) {
        BitmapFactory.Options newOpts = new BitmapFactory.Options();

        newOpts.inJustDecodeBounds = true;
        Bitmap bitmap = BitmapFactory.decodeFile(srcPath,newOpts);

        newOpts.inJustDecodeBounds = false;
        int w = newOpts.outWidth;
        int h = newOpts.outHeight;
        float hh = 600f;
        float ww = 800f;
        int be = 1;
        int b2 = 1;
        if (w > h && w > ww) {
            be = (int) (newOpts.outWidth / ww)+1;
        }
        if (w < h && h > hh) {
            b2 = (int) (newOpts.outHeight / hh)+1;
        }
        if(be < b2){
            be = b2;
        }
        if (be <= 0)
            be = 1;
        newOpts.inSampleSize = be;

        bitmap = BitmapFactory.decodeFile(srcPath, newOpts);
        return compressImage(bitmap);
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
