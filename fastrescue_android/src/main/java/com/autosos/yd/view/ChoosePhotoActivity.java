package com.autosos.yd.view;

import android.annotation.TargetApi;
import android.content.ContentResolver;
import android.content.Context;
import android.content.Intent;
import android.content.SharedPreferences;
import android.database.Cursor;
import android.graphics.Point;
import android.graphics.drawable.Drawable;
import android.net.Uri;
import android.os.AsyncTask;
import android.os.Build;
import android.os.Bundle;
import android.os.Handler;
import android.provider.MediaStore;
import android.util.DisplayMetrics;
import android.util.Log;
import android.view.Gravity;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.AdapterView;
import android.widget.Button;
import android.widget.GridView;
import android.widget.ImageView;
import android.widget.PopupWindow;
import android.widget.Toast;

import com.autosos.yd.model.OrderInfo;
import com.umeng.analytics.MobclickAgent;

import org.json.JSONObject;

import java.io.File;
import java.util.ArrayList;
import java.util.List;

import com.autosos.yd.R;

public class ChoosePhotoActivity extends com.autosos.yd.view.AutososBackActivity implements com.autosos.yd.adapter.ObjectBindAdapter.ViewBinder<com.autosos.yd.model.Item>,
        AdapterView.OnItemClickListener {

    private List<com.autosos.yd.model.Item> list;
    private com.autosos.yd.adapter.ObjectBindAdapter<com.autosos.yd.model.Item> adapter;
    private int size;
    private Toast toast;
    private ArrayList<com.autosos.yd.model.Item> selectedItems;
    private ArrayList<String> selectedPhotos;
    private GridView gridView;
    private View chooseLayout;
    private Button chooseBtn;
    private int limit;
    private String currentPath;
    private OrderInfo orderInfo;



    @Override
    protected void onCreate(Bundle savedInstanceState) {
        limit = getIntent().getIntExtra("limit", 0);
        Point point = com.autosos.yd.util.JSONUtil.getDeviceSize(this);
        DisplayMetrics dm = getResources().getDisplayMetrics();
        size = Math.round((point.x - 8 * dm.density) / 3);
        list = new ArrayList<>();
        selectedItems = new ArrayList<>();
        selectedPhotos = new ArrayList<>();
        adapter = new com.autosos.yd.adapter.ObjectBindAdapter<>(this, list, R.layout.photo_gallery_item, this);
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_choose_photo);
        chooseLayout = findViewById(R.id.choose_layout);
        chooseBtn = (Button) findViewById(R.id.choose_ok);
        gridView = (GridView) findViewById(R.id.gallery);
        gridView.setOnItemClickListener(this);
        gridView.setAdapter(adapter);
        orderInfo = (OrderInfo) getIntent().getSerializableExtra("OrderInfo");
        setSuggest();
        getSuggest().setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                showPopupWindow();
            }
        });
        showPopupWindow();
        new PhotoTask(this).executeOnExecutor(com.autosos.yd.Constants.LISTTHEADPOOL);
        //MusicUtil.playmusics(ChoosePhotoActivity.this, MusicUtil.Take_three_photo);
    }

    @Override
    public void onBackPressed() {
        super.onBackPressed();
        overridePendingTransition(0, R.anim.slide_out_right);
    }

    public void onChooseOk(View view) {
        if (!selectedItems.isEmpty()) {
            Intent intent = getIntent();
            intent.putExtra("selectedPhotos", selectedItems);
            Log.e("upl", orderInfo.getOrder_type() + "");
            intent.putExtra("OrderInfo", orderInfo);
            setResult(RESULT_OK, intent);
            onBackPressed();
        }
    }

    private void showPopupWindow() {

        LayoutInflater inflater = (LayoutInflater) getSystemService(Context.LAYOUT_INFLATER_SERVICE);
        View vPopWindow =null;
        if (orderInfo.getService_type() == 1 || orderInfo.getService_type() == 2 ){
            Log.e("order",orderInfo.getOrder_type()+"        =================");
            Log.e("order","dadian suggest!");
            vPopWindow  = inflater.inflate(R.layout.pop_window, null);
        }else {
            Log.e("order",orderInfo.getOrder_type()+"        =================");
            Log.e("order","tuoche suggest!");
            vPopWindow  = inflater.inflate(R.layout.pop_window_drag1, null);
        }

        final PopupWindow popupWindow =new PopupWindow(vPopWindow, ViewGroup.LayoutParams.FILL_PARENT, ViewGroup.LayoutParams.FILL_PARENT,true);
        Drawable drawable = getResources().getDrawable(R.color.color_black_transparency);
        popupWindow.setBackgroundDrawable(drawable);
        popupWindow.setFocusable(true);
//        popupWindow.setAnimationStyle(R.style.popwin_anim_style);
        final View finalVPopWindow = vPopWindow;
        final Handler mHandler=new Handler();
        Runnable showPopWindowRunnable = new Runnable() {

            @Override
            public void run() {
                // 得到activity中的根元素
                View view = getWindow().getDecorView();
                // 如何根元素的width和height大于0说明activity已经初始化完毕

                if( view != null && view.getWidth() > 0 && view.getHeight() > 0) {
                    // 显示popwindow
//                    popupWindow.showAtLocation(view, Gravity.CENTER, 0, 0);
                    popupWindow.showAsDropDown(finalVPopWindow);
                    Button bt_dismiss = (Button) finalVPopWindow.findViewById(R.id.bt_dismiss);
                    bt_dismiss.setOnClickListener(new View.OnClickListener() {
                        @Override
                        public void onClick(View v) {
                            if (popupWindow.isShowing())
                                popupWindow.dismiss();
                        }
                    });
                    // 停止检测
                    mHandler.removeCallbacks(this);
                } else {
                    // 如果activity没有初始化完毕则等待5毫秒再次检测
                    mHandler.postDelayed(this, 100);
                }
            }
        };
        // 开始检测
        mHandler.post(showPopWindowRunnable);



    }

    @Override
    public void setViewValue(View view, com.autosos.yd.model.Item item, int position) {
        ViewHolder holder = (ViewHolder) view.getTag();
        if (holder == null) {
            holder = new ViewHolder();
            holder.imageView = (ImageView) view.findViewById(R.id.image);
            holder.selectedView = (com.autosos.yd.widget.CheckableLinearLayout) view.findViewById(R.id.selected_view);
            holder.takePhotoView = view.findViewById(R.id.take_photo_btn);
            view.getLayoutParams().width = size;
            view.getLayoutParams().height = size;
            view.setTag(holder);
        }
        if (com.autosos.yd.util.JSONUtil.isEmpty(item.getMediaPath())) {
            holder.takePhotoView.setVisibility(View.VISIBLE);
            holder.imageView.setVisibility(View.GONE);
            holder.selectedView.setVisibility(View.GONE);
        } else {
            holder.takePhotoView.setVisibility(View.GONE);
            holder.imageView.setVisibility(View.VISIBLE);
            holder.selectedView.setVisibility(View.VISIBLE);
            String path = item.getMediaPath();
            holder.selectedView.setChecked(selectedPhotos.contains(path));
            if (!item.getMediaPath().equals(holder.imageView.getTag())) {
                com.autosos.yd.task.ImageLoadTask task = new com.autosos.yd.task.ImageLoadTask(holder.imageView);
                holder.imageView.setTag(item.getMediaPath());
                task.loadImage(item.getMediaPath(), size / 2, com.autosos.yd.util.ScaleMode.ALL,
                        new com.autosos.yd.task.AsyncBitmapDrawable(getResources(),
                                com.autosos.yd.Constants.PLACEHOLDER, task));
            }
        }
    }

    private class ViewHolder {
        View takePhotoView;
        ImageView imageView;
        com.autosos.yd.widget.CheckableLinearLayout selectedView;
    }

    @Override
    public void onItemClick(AdapterView<?> parent, View view, int position, long id) {
        com.autosos.yd.model.Item item = (com.autosos.yd.model.Item) parent.getAdapter().getItem(position);
        if (item != null && !com.autosos.yd.util.JSONUtil.isEmpty(item.getMediaPath())) {
            if (selectedPhotos.contains(item.getMediaPath())) {
                int index = selectedPhotos.indexOf(item.getMediaPath());
                selectedPhotos.remove(index);
                selectedItems.remove(index);
                adapter.notifyDataSetChanged();
                if (selectedItems.size() < 1) {
                    chooseLayout.setVisibility(View.GONE);
                }
                chooseBtn.setText(getString(R.string.label_choose_ok, limit > 0 ? selectedItems.size() + "/" + limit : String.valueOf(selectedItems.size())));
            } else {
                if (limit > 0 && selectedItems.size() >= limit) {
                    if (toast == null) {
                        toast = Toast.makeText(this,
                                R.string.hint_choose_photo_limit_out,
                                Toast.LENGTH_SHORT);
                        toast.setGravity(Gravity.CENTER, 0, 0);
                    }
                    toast.show();
                    return;
                }
                if (item.getWidth() == 0 || item.getHeight() == 0) {
                    com.autosos.yd.util.Size size = com.autosos.yd.util.JSONUtil.getImageSizeFromPath(ChoosePhotoActivity.this,item
                            .getMediaPath(), 800, 800);
                    item.setHeight(size.getHeight());
                    item.setWidth(size.getWidth());
                }
                selectedPhotos.add(item.getMediaPath());
                selectedItems.add(item);
                adapter.notifyDataSetChanged();
                chooseLayout.setVisibility(View.VISIBLE);
                chooseBtn.setText(getString(R.string.label_choose_ok, limit > 0 ? selectedItems.size() + "/" + limit : String.valueOf(selectedItems.size())));

            }
        } else if (item != null && com.autosos.yd.util.JSONUtil.isEmpty(item.getMediaPath())) {
            Intent intent = new Intent(MediaStore.ACTION_IMAGE_CAPTURE);
            File f = com.autosos.yd.util.FileUtil.createImageFile();
            Uri imageUri = Uri.fromFile(f);
            currentPath = f.getAbsolutePath();
            SharedPreferences sharedPreferences =
                    getSharedPreferences("photo_root", Context.MODE_PRIVATE);
            SharedPreferences.Editor editor = sharedPreferences.edit();
            editor.putString("root", currentPath);
            editor.commit();
            intent.putExtra(MediaStore.EXTRA_OUTPUT, imageUri);
            startActivityForResult(intent,
                    com.autosos.yd.Constants.RequestCode.PHOTO_FROM_CAMERA);
        }
    }

    @TargetApi(Build.VERSION_CODES.JELLY_BEAN)
    private class PhotoTask extends AsyncTask<String, Integer, Integer> {
        Context mContext;

        private PhotoTask(Context context) {
            this.mContext = context;
        }

        @Override
        protected Integer doInBackground(String... params) {
            ContentResolver cr = mContext.getContentResolver();
            Cursor cursor = MediaStore.Images.Media.query(cr,
                    MediaStore.Images.Media.EXTERNAL_CONTENT_URI, new String[]{
                            MediaStore.Images.Media.DATA,
                            MediaStore.Images.Media.ORIENTATION,
                            MediaStore.Images.Media.WIDTH,
                            MediaStore.Images.Media.HEIGHT,
                            MediaStore.Images.Media.BUCKET_ID},
                    null, null,
                    MediaStore.Images.Media.DATE_ADDED + " desc");
            int i = 0;
            list.clear();
            list.add(new com.autosos.yd.model.Item(new JSONObject()));
            if (cursor.moveToFirst()) do {
                String path = cursor.getString(0);
                com.autosos.yd.model.Item item = new com.autosos.yd.model.Item(new JSONObject());
                item.setBucketId(cursor.getLong(4));
                item.setMediaPath(path);
                if (cursor.getInt(1) % 180 == 0) {
                    item.setWidth(cursor.getInt(2));
                    item.setHeight(cursor.getInt(3));
                } else {
                    item.setWidth(cursor.getInt(3));
                    item.setHeight(cursor.getInt(2));
                }

                list.add(item);
                i++;
                if (i == 5) {
                    i = 0;
                    publishProgress(0);
                }
            } while (cursor.moveToNext());
            cursor.close();
            return list.size();
        }

        @Override
        protected void onPostExecute(Integer result) {
            adapter.notifyDataSetChanged();
            super.onPostExecute(result);
        }

        @Override
        protected void onProgressUpdate(Integer... values) {
            adapter.notifyDataSetChanged();
            super.onProgressUpdate(values);
        }

        @Override
        protected void onPreExecute() {
            super.onPreExecute();
        }

    }

    @Override
    protected void onActivityResult(int requestCode, int resultCode, Intent data) {
        Log.e("UploadPhotoActivity", "2222222222222222222222222");
        if (resultCode == RESULT_OK) {
            switch (requestCode) {
                case com.autosos.yd.Constants.RequestCode.PHOTO_FROM_CAMERA:
                        com.autosos.yd.model.Item item = new com.autosos.yd.model.Item(new JSONObject());
                        if(currentPath == null || currentPath.length() < 3){
                            SharedPreferences sharedPreferences = getSharedPreferences("photo_root", Context.MODE_PRIVATE);
                            currentPath = sharedPreferences.getString("root", "");
                        }
                        com.autosos.yd.util.Size size = com.autosos.yd.util.JSONUtil.getImageSizeFromPath(ChoosePhotoActivity.this,currentPath, 400, 400);
                        item.setWidth(size.getWidth());
                        item.setHeight(size.getHeight());
                        item.setMediaPath(currentPath);
                        selectedItems.clear();
                        selectedItems.add(item);
                        selectedPhotos.clear();
                        Intent intent = getIntent();
                        intent.putExtra("selectedPhotos", selectedItems);
                        setResult(RESULT_OK, intent);
                        onBackPressed();
                    break;
            }
        }
        super.onActivityResult(requestCode, resultCode, data);

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

}
