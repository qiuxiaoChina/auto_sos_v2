package com.autosos.yd.view;

import android.content.Context;
import android.content.Intent;
import android.graphics.Point;
import android.graphics.drawable.Drawable;
import android.os.AsyncTask;
import android.os.Bundle;
import android.os.Handler;
import android.util.DisplayMetrics;
import android.util.Log;
import android.view.Gravity;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.AdapterView;
import android.widget.Button;
import android.widget.ImageView;
import android.widget.PopupWindow;
import android.widget.TextView;

import com.etsy.android.grid.StaggeredGridView;
import com.handmark.pulltorefresh.library.PullToRefreshBase;
import com.handmark.pulltorefresh.library.PullToRefreshStaggeredGridView;
import com.umeng.analytics.MobclickAgent;

import org.json.JSONArray;
import org.json.JSONException;

import java.io.IOException;
import java.util.ArrayList;

import com.autosos.yd.Constants;
import com.autosos.yd.R;
import com.autosos.yd.adapter.ObjectBindAdapter;
import com.autosos.yd.model.Image;
import com.autosos.yd.task.AsyncBitmapDrawable;
import com.autosos.yd.task.ImageLoadTask;
import com.autosos.yd.util.JSONUtil;
import com.autosos.yd.util.ScaleMode;
import com.autosos.yd.view.*;
import com.autosos.yd.view.AutososBackActivity;
import com.autosos.yd.view.WatchPhotoActivity;

public class PhotosActivity extends com.autosos.yd.view.AutososBackActivity implements
        AdapterView.OnItemClickListener, com.autosos.yd.adapter.ObjectBindAdapter.ViewBinder<com.autosos.yd.model.Image>
        , PullToRefreshBase.OnRefreshListener<StaggeredGridView> {

    private static final String TAG = "PhotosActivity";
    private View progressBar;
    private boolean onLoading;
    private long id;
    private ArrayList<com.autosos.yd.model.Image> images;
    private com.autosos.yd.adapter.ObjectBindAdapter<com.autosos.yd.model.Image> adapter;
    private PullToRefreshStaggeredGridView gridView;
    private int width;
    private int height;
    private int itemMargin;


    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_photos);
        Log.e(TAG,"========================");
        Point point = com.autosos.yd.util.JSONUtil.getDeviceSize(this);
        DisplayMetrics dm = getResources().getDisplayMetrics();
        width = Math.round((point.x - 24 * dm.density) / 2);
        height = Math.round((float) width);
        itemMargin = Math.round(8 * dm.density);
        id = getIntent().getLongExtra("id", 0);
        images = new ArrayList<>();
        adapter = new com.autosos.yd.adapter.ObjectBindAdapter<>(this, images, R.layout.list_photo_item, this);
        gridView = (PullToRefreshStaggeredGridView) findViewById(R.id.list);
        gridView.getRefreshableView().setOnItemClickListener(this);
        gridView.getRefreshableView().setItemMargin(itemMargin);
        gridView.getRefreshableView().setAdapter(adapter);
        gridView.setOnRefreshListener(this);
        progressBar = findViewById(R.id.progressBar);
        new GetPhotosTask().executeOnExecutor(com.autosos.yd.Constants.LISTTHEADPOOL,
                String.format(com.autosos.yd.Constants.PHOTOS_URL, id));

    }



    @Override
    public void onRefresh(PullToRefreshBase<StaggeredGridView> refreshView) {
        if (!onLoading) {
            new GetPhotosTask().executeOnExecutor(com.autosos.yd.Constants.LISTTHEADPOOL,
                    String.format(com.autosos.yd.Constants.PHOTOS_URL, id));
        }

    }


    private class GetPhotosTask extends AsyncTask<String, Object, JSONArray> {

        private GetPhotosTask() {
            onLoading = true;
        }

        @Override
        protected JSONArray doInBackground(String... params) {
            try {
                String jsonStr = com.autosos.yd.util.JSONUtil.getStringFromUrl(PhotosActivity.this, params[0]);
                if (com.autosos.yd.util.JSONUtil.isEmpty(jsonStr)) {
                    return null;
                }
                Log.e(TAG, jsonStr);
                return new JSONArray(jsonStr);
            } catch (IOException | JSONException e) {

            }
            return null;
        }

        @Override
        protected void onPostExecute(JSONArray result) {
            progressBar.setVisibility(View.GONE);
            gridView.onRefreshComplete();
            images.clear();
            if (result != null) {
                if (result.length() > 0) {
                    int size = result.length();
                    for (int i = 0; i < size; i++) {
                        com.autosos.yd.model.Image image = new com.autosos.yd.model.Image(result.optJSONObject(i));
                        Log.e(TAG,"++++++++++"+image.getFileid().toString()+"----"+image.getImg().toString());
                        images.add(image);
                    }
                    adapter.notifyDataSetChanged();
                }
            }
            onLoading = false;
            if (images.isEmpty()) {
                View emptyView = gridView.getRefreshableView().getEmptyView();
                if (emptyView == null) {
                    emptyView = findViewById(R.id.empty_hint_layout);
                    gridView.getRefreshableView().setEmptyView(emptyView);
                }
                ImageView imgEmptyHint = (ImageView) emptyView.findViewById(R.id
                        .img_empty_hint);
                TextView textEmptyHint = (TextView) emptyView.findViewById(R.id.text_empty_hint);

                imgEmptyHint.setVisibility(View.VISIBLE);
                textEmptyHint.setVisibility(View.VISIBLE);

                if (com.autosos.yd.util.JSONUtil.isNetworkConnected(PhotosActivity.this)) {
                    textEmptyHint.setText(R.string.msg_photo_empty);
                } else {
                    textEmptyHint.setText(R.string.msg_net_disconnected);
                }

            }
            super.onPostExecute(result);
        }
    }

    @Override
    public void setViewValue(View view, com.autosos.yd.model.Image image, int position) {
        if (view.getTag() == null) {
            ViewHolder holder = new ViewHolder();
            holder.photo = (ImageView) view.findViewById(R.id.photo);
            ViewGroup.MarginLayoutParams params;
            params = (ViewGroup.MarginLayoutParams) holder.photo.getLayoutParams();
            params.height = height;

            view.setTag(holder);
        }
        ViewHolder holder = (ViewHolder) view.getTag();
        String url = com.autosos.yd.util.JSONUtil.getImagePath(image.getImg(), width, height);
        Log.e(TAG,"xxxxxxxxxxxxxURL:  "+url.toString());
        holder.photo.setTag(url);
        com.autosos.yd.task.ImageLoadTask task = new com.autosos.yd.task.ImageLoadTask(holder.photo, null, 0);
        com.autosos.yd.task.AsyncBitmapDrawable asyncBitmapDrawable = new com.autosos.yd.task.AsyncBitmapDrawable(getResources(),
                com.autosos.yd.Constants.PLACEHOLDER_NORMAL, task);
        task.loadImage(url, width, com.autosos.yd.util.ScaleMode.WIDTH, asyncBitmapDrawable);

    }

    private class ViewHolder {
        ImageView photo;
    }

    @Override
    public void onItemClick(AdapterView<?> adapterView, View view, int position, long id) {
        if (!images.isEmpty()) {
            Intent intent = new Intent(PhotosActivity.this, com.autosos.yd.view.WatchPhotoActivity.class);
            intent.putExtra("photos", images);
            intent.putExtra("position", position);
            startActivity(intent);
            overridePendingTransition(
                    R.anim.slide_in_right, R.anim.activity_anim_default);
        }

    }


    @Override
    public void onBackPressed() {
        super.onBackPressed();
        overridePendingTransition(0, R.anim.slide_out_right);
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
