package com.autosos.yd.view;

import android.content.Context;
import android.content.Intent;
import android.graphics.Bitmap;
import android.graphics.Point;
import android.os.Bundle;
import android.support.v4.view.PagerAdapter;
import android.support.v4.view.ViewPager;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;

import com.umeng.analytics.MobclickAgent;

import java.util.ArrayList;

import com.autosos.yd.Constants;
import com.autosos.yd.R;
import com.autosos.yd.model.Image;
import com.autosos.yd.model.User;
import com.autosos.yd.task.AsyncBitmapDrawable;
import com.autosos.yd.task.ImageLoadTask;
import com.autosos.yd.task.OnHttpRequestListener;
import com.autosos.yd.util.JSONUtil;
import com.autosos.yd.util.ScaleMode;
import com.autosos.yd.util.Session;
import com.autosos.yd.util.Utils;
import com.autosos.yd.view.*;
import com.autosos.yd.view.AutososBackActivity;
import com.autosos.yd.widget.HackyViewPager;
import uk.co.senab.photoview.PhotoView;

public class WatchPhotoActivity extends AutososBackActivity {

    private ArrayList<Image> photos;
    private int width;
    private int height;
    private Point point;
    private User user;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_watch_photo);
        ViewPager mViewPager = (HackyViewPager) findViewById(R.id.pager);
        findViewById(R.id.progressBar).setVisibility(View.GONE);
        point = JSONUtil.getDeviceSize(this);
        width = Math.round(point.x * 3 / 2);
        height = Math.round((float) width);
        if (height > JSONUtil.getMaximumTextureSize() && JSONUtil.getMaximumTextureSize() > 0) {
            height = JSONUtil.getMaximumTextureSize();
        }
        Intent intent = getIntent();
        photos = (ArrayList<Image>) intent.getSerializableExtra("photos");
        int position = intent.getIntExtra("position", 0);
        mViewPager.setAdapter(new SamplePagerAdapter(this));
        mViewPager.setCurrentItem(position);
        user = Session.getInstance().getCurrentUser(com.autosos.yd.view.WatchPhotoActivity.this);
    }

    public class SamplePagerAdapter extends PagerAdapter {

        private Context mContext;

        public SamplePagerAdapter(Context context) {
            mContext = context;
        }

        @Override
        public int getCount() {
            return photos.size();
        }

        @Override
        public View instantiateItem(ViewGroup container, int position) {
            View view = LayoutInflater.from(mContext).inflate(
                    R.layout.thread_photos_view, null, false);
            final Image image = photos.get(position);
            final ViewHolder holder = new ViewHolder();
            holder.image = (PhotoView) view.findViewById(R.id.image);
            holder.progressBar = view.findViewById(R.id.progressBar);
            holder.image.setScaleType(ImageView.ScaleType.CENTER);
            ImageLoadTask task = new ImageLoadTask(holder.image,
                    new OnHttpRequestListener() {

                        @Override
                        public void onRequestCompleted(Object obj) {
                            holder.progressBar.setVisibility(View.GONE);
                            Bitmap bitmap = Utils.comp((Bitmap) obj);
                            float rate = (float) point.x
                                    / bitmap.getWidth();
                            int w = point.x;
                            int h = Math.round(bitmap.getHeight() * rate);
                            if (h > point.y || (h * point.x > w * point.y)) {
                                holder.image
                                        .setScaleType(ImageView.ScaleType.CENTER_CROP);
                            } else {
                                holder.image
                                        .setScaleType(ImageView.ScaleType.CENTER_INSIDE);
                            }
                        }

                        @Override
                        public void onRequestFailed(Object obj) {

                        }
                    });

            if (!JSONUtil.isEmpty(image.getImg())) {
                holder.progressBar.setVisibility(View.VISIBLE);
                String url = JSONUtil.getImagePath2(image.getImg(), width, height);
                holder.image.setTag(url);
                AsyncBitmapDrawable drawable = new AsyncBitmapDrawable(getResources(),
                        Constants.PLACEHOLDER, task);
                task.loadImage(url, width, ScaleMode.WIDTH, drawable);
            }
            container.addView(view);
            return view;
        }

        @Override
        public void destroyItem(ViewGroup container, int position, Object object) {
            container.removeView((View) object);
        }

        @Override
        public boolean isViewFromObject(View view, Object object) {
            return view == object;
        }

        private class ViewHolder {
            PhotoView image;
            View progressBar;
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
