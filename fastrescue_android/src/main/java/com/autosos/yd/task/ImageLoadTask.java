package com.autosos.yd.task;

import android.app.ActivityManager;
import android.content.Context;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.graphics.Canvas;
import android.graphics.ColorMatrix;
import android.graphics.ColorMatrixColorFilter;
import android.graphics.Paint;
import android.graphics.drawable.BitmapDrawable;
import android.graphics.drawable.ColorDrawable;
import android.graphics.drawable.Drawable;
import android.graphics.drawable.TransitionDrawable;
import android.media.ThumbnailUtils;
import android.os.AsyncTask;
import android.provider.MediaStore.Video;
import android.util.LruCache;
import android.view.View;
import android.widget.ImageView;
import android.widget.ProgressBar;

import com.jakewharton.DiskLruCache;
import com.makeramen.rounded.RoundedImageView;

import org.apache.http.HttpEntity;

import java.io.IOException;
import java.io.InputStream;
import java.io.OutputStream;
import java.lang.ref.WeakReference;

import com.autosos.yd.Constants;
import com.autosos.yd.entity.ImageLoadProgressListener;
import com.autosos.yd.task.*;
import com.autosos.yd.task.AsyncBitmapDrawable;
import com.autosos.yd.task.OnHttpRequestListener;
import com.autosos.yd.util.JSONUtil;
import com.autosos.yd.util.ScaleMode;

public class ImageLoadTask extends AsyncTask<Object, Object, Bitmap> implements
        ImageLoadProgressListener {
    private static LruCache<String, Bitmap> cache = null;
    private static DiskLruCache diskCache = null;
    private final WeakReference<ImageView> imageViewReference;
    private String url;
    private com.autosos.yd.task.OnHttpRequestListener requestListener;
    private Context context;
    private ProgressBar progressBar;
    private int transferred;
    private int durationMillis;
    private boolean isPng;

    public ImageLoadTask(ImageView imageView) {
        this(imageView, null, null, 300, false);
    }

    public ImageLoadTask(ImageView imageView,
                         com.autosos.yd.task.OnHttpRequestListener requestListener) {
        this(imageView, requestListener, null, 300, false);

    }


    public ImageLoadTask(ImageView imageView,
                         com.autosos.yd.task.OnHttpRequestListener requestListener, boolean isPng) {
        this(imageView, requestListener, null, 300, isPng);

    }

    public ImageLoadTask(ImageView imageView,
                         com.autosos.yd.task.OnHttpRequestListener requestListener, int durationMillis) {
        this(imageView, requestListener, null, durationMillis, false);

    }

    public ImageLoadTask(ImageView imageView, boolean isPng) {
        this(imageView, null, null, 300, isPng);

    }

    public ImageLoadTask(ImageView imageView, int durationMillis, boolean isPng) {
        this(imageView, null, null, durationMillis, isPng);

    }

    public ImageLoadTask(ImageView imageView, int durationMillis) {
        this(imageView, null, null, durationMillis, false);

    }

    /**
     * @param imageView
     */
    public ImageLoadTask(ImageView imageView,
                         OnHttpRequestListener requestListener, ProgressBar progressBar,
                         int durationMillis, boolean isPng) {
        super();
        context = imageView.getContext();
        this.durationMillis = durationMillis;
        this.isPng = isPng;
        this.progressBar = progressBar;
        imageViewReference = new WeakReference<ImageView>(imageView);
        this.requestListener = requestListener;
        if (cache == null) {
            final int memClass = ((ActivityManager) imageView.getContext()
                    .getSystemService(Context.ACTIVITY_SERVICE))
                    .getMemoryClass();
            cache = new LruCache<String, Bitmap>(1024 * 1024 * memClass / 8) {
                @Override
                protected int sizeOf(String key, Bitmap value) {

                    return value.getByteCount();
                }

                @Override
                protected void entryRemoved(boolean evicted, String key,
                                            Bitmap oldValue, Bitmap newValue) {
                    // TODO Auto-generated method stub
                    super.entryRemoved(evicted, key, oldValue, newValue);
                }
            };

            if (diskCache == null) {
                try {
                    diskCache = DiskLruCache.open(imageView.getContext()
                            .getCacheDir(), 1, 1, 1024 * 1024 * 50);
                } catch (IOException e) {
                    e.printStackTrace();
                }
            }
        }
    }

    private static com.autosos.yd.task.ImageLoadTask getBitmapWorkerTask(ImageView imageView) {
        if (imageView != null) {
            final Drawable drawable = imageView.getDrawable();
            if (drawable instanceof AsyncBitmapDrawable) {
                final AsyncBitmapDrawable asyncDrawable = (AsyncBitmapDrawable) drawable;
                return asyncDrawable.getImageLoadTask();
            }
        }
        return null;
    }

    public static Bitmap toGrayscale(Bitmap bmpOriginal) {
        int width, height;
        height = bmpOriginal.getHeight();
        width = bmpOriginal.getWidth();
        Bitmap bmpGrayscale = Bitmap.createBitmap(width, height,
                Bitmap.Config.RGB_565);
        Canvas c = new Canvas(bmpGrayscale);
        Paint paint = new Paint();
        ColorMatrix cm = new ColorMatrix();
        cm.setSaturation(0);
        ColorMatrixColorFilter f = new ColorMatrixColorFilter(cm);
        paint.setColorFilter(f);
        c.drawBitmap(bmpOriginal, 0, 0, paint);
        bmpOriginal.recycle();
        return bmpGrayscale;
    }

    /**
     * @return the url
     */
    public String getUrl() {
        return url;
    }

    @Override
    protected Bitmap doInBackground(Object... params) {
        url = (String) params[0];
        int size = 0;
        if (params.length > 1) {
            size = (Integer) params[1];
        }
        ScaleMode mode = ScaleMode.ALL;
        if (params.length > 2) {
            mode = (ScaleMode) params[2];
        }
        Bitmap image = null;
        String key = url + size;
        if (diskCache != null && !isCancelled()
                && (getAttachedImageView() != null)) {
            image = getBitmapFromCache(key);
        }
        if (image == null && !isCancelled() && (getAttachedImageView() != null)) {
            try {
                if (!JSONUtil.isEmpty(url)) {
                    if (url.startsWith("http://") || url.startsWith("https://")) {
                        InputStream is = getInputStreamFromDiskCache(key);
                        HttpEntity entity;
                        byte[] data;
                        if (is == null) {
                            entity = JSONUtil.getEntityFromUrl(url, this);
                            if (entity == null) {
                                return null;
                            }
                            is = entity.getContent();
                            if (is == null) {
                                return null;
                            }
                            data = JSONUtil.readStreamToByteArray(is, this,
                                    url, entity.getContentLength());
                            if (data == null) {
                                return null;
                            }
                            image = JSONUtil
                                    .getImageFromBytes(data, size, mode);
                            if (image != null && image.getWidth() > 1 && image.getHeight() > 1) {
                                addBitmapToMemoryCache(key, data);
                            }
                        } else {
                            try {
                                BitmapFactory.Options opt = new BitmapFactory.Options();
                                opt.inPreferredConfig = Bitmap.Config.ARGB_8888;
                                opt.inPurgeable = true;
                                opt.inInputShareable = true;
                                image = BitmapFactory.decodeStream(is, null,
                                        opt);
                                is.close();
                            } catch (OutOfMemoryError e) {
                                System.gc();
                                if (image != null && !image.isRecycled()) {
                                    image.recycle();
                                }
                                return null;
                            }
                        }
                    } else {
                        if (isPng) {
                            image = JSONUtil.getImageFromPath(
                                    context.getContentResolver(), url, size,
                                    Bitmap.Config.ARGB_8888, false);
                        } else {
                            image = JSONUtil.getImageFromPath(
                                    context.getContentResolver(), url, size);
                        }
                    }
                    if (image != null && image.getWidth() > 1 && image.getHeight() > 1) {
                        addBitmapToCache(key, image);
                    }
                }
            } catch (IOException e) {
                e.printStackTrace();
            }
        }
        return image;
    }

    @Override
    protected void onCancelled() {
        if (progressBar != null) {
            progressBar.setVisibility(View.GONE);
        }
        super.onCancelled();
    }

    /*
     * (non-Javadoc)
     *
     * @see android.os.AsyncTask#onPostExecute(java.lang.Object)
     */
    @Override
    protected void onPostExecute(Bitmap result) {
        super.onPostExecute(result);
        ImageView imageView = this.imageViewReference.get();
        if (imageView != null && result != null) {
            if (imageView.getTag() != null) {
                if (imageView.getTag().equals(url)) {
                    imageView.setImageDrawable(getTransitionDrawable(result));
                    if (requestListener != null) {
                        this.requestListener.onRequestCompleted(result);
                    }
                }
            } else {
                imageView.setImageDrawable(getTransitionDrawable(result));
                if (requestListener != null) {
                    this.requestListener.onRequestCompleted(result);
                }
            }
        } else if (imageView != null && result == null) {
            if (imageView.getTag() != null) {
                if (imageView.getTag().equals(url) && requestListener != null) {
                    this.requestListener.onRequestFailed(result);
                }
            } else if (requestListener != null) {
                this.requestListener.onRequestFailed(result);
            }
        }

    }

    private Drawable getTransitionDrawable(Bitmap bitmap) {
        Drawable drawable = new BitmapDrawable(context.getResources(), bitmap);
        if (durationMillis == 0) {
            return drawable;
        }
        TransitionDrawable td = new TransitionDrawable(new Drawable[]{
                new ColorDrawable(android.R.color.transparent), drawable});
        td.startTransition(200);
        return td;
    }

    /*
     * (non-Javadoc)
     *
     * @see android.os.AsyncTask#onPreExecute()
     */
    @Override
    protected void onPreExecute() {
        if (progressBar != null) {
            progressBar.setVisibility(View.VISIBLE);
            progressBar.setProgress(0);
        }
        super.onPreExecute();
    }

    /*
     * (non-Javadoc)
     *
     * @see android.os.AsyncTask#onProgressUpdate(Progress[])
     */
    @Override
    protected void onProgressUpdate(Object... values) {
        if (progressBar != null) {
            int bytes = (Integer) values[0];
            progressBar.setProgress(bytes);

        }
        super.onProgressUpdate(values);
    }

    public Bitmap getBitmapFromCache(String key) {
        Bitmap bitmap = getBitmapFromMemCache(key);
        if (bitmap == null) {
            bitmap = getBitmapFromDiskCache(key);
            if (bitmap != null) {
                addBitmapToMemoryCache(key, bitmap);
            }
        }
        return bitmap;
    }

    public void addBitmapToCache(String key, Bitmap bitmap) {
        if (key == null || bitmap == null) {
            return;
        }
        if (getBitmapFromMemCache(key) == null) {
            addBitmapToMemoryCache(key, bitmap);
        }

        if (!isDiskCacheHasBitmap(key)) {
            addBitmapToDiskCache(bitmap, key);
        }
    }

    public void addBitmapToMemoryCache(String key, Bitmap bitmap) {
        if (getBitmapFromMemCache(key) == null) {
            cache.put(key, bitmap);
        }
    }


    public void addBitmapToMemoryCache(String key, byte[] data) {
        if (key == null || data == null || data.length == 0) {
            return;
        }
        if (!isDiskCacheHasBitmap(key)) {
            addBitmapToDiskCache(data, key);
        }
    }

    public void addBitmapToDiskCache(byte[] data, String key) {
        try {
            DiskLruCache.Editor editor = diskCache.edit(key.hashCode() + "");
            if (editor != null) {
                OutputStream os = editor.newOutputStream(0);
                os.write(data);
                os.close();
                editor.commit();
            }
        } catch (IOException e) {
            e.printStackTrace();
        }
    }

    public void addBitmapToDiskCache(Bitmap bitmap, String key) {
        try {
            DiskLruCache.Editor editor = diskCache.edit(key.hashCode() + "");
            if (editor != null) {
                OutputStream os = editor.newOutputStream(0);
                if (isPng) {
                    bitmap.compress(Bitmap.CompressFormat.PNG, 100, os);
                } else {
                    bitmap.compress(Bitmap.CompressFormat.JPEG, 100, os);
                }
                os.close();
                editor.commit();
            }
        } catch (IOException e) {
            e.printStackTrace();
        }
    }

    public Bitmap getBitmapFromMemCache(String key) {
        return cache.get(key);
    }

    private InputStream getInputStreamFromDiskCache(String key) {
        try {
            DiskLruCache.Snapshot snapshot = diskCache.get(key.hashCode() + "");
            if (snapshot != null) {
                return snapshot.getInputStream(0);
            }
        } catch (IOException e) {
            e.printStackTrace();
        }
        return null;
    }

    public Bitmap getBitmapFromDiskCache(String key) {
        Bitmap bitmap = null;
        try {
            DiskLruCache.Snapshot snapshot = diskCache.get(key.hashCode() + "");
            if (snapshot != null) {
                try {
                    BitmapFactory.Options opt = new BitmapFactory.Options();
                    opt.inPreferredConfig = Bitmap.Config.ARGB_8888;
                    opt.inPurgeable = true;
                    opt.inInputShareable = true;
                    InputStream is = snapshot.getInputStream(0);
                    // byte[] data = JSONUtil.readStreamToByteArray(is);
                    // bitmap = JSONUtil.getImageFromBytes(data);
                    bitmap = BitmapFactory.decodeStream(is, null, opt);
                    is.close();
                } catch (OutOfMemoryError e) {
                    System.gc();
                    if (bitmap != null && !bitmap.isRecycled()) {
                        bitmap.recycle();
                    }
                    bitmap = null;
                }
            }
        } catch (IOException e) {
            bitmap = null;
        }

        return bitmap;
    }

    public boolean isDiskCacheHasBitmap(String key) {
        try {
            DiskLruCache.Snapshot snapshot = diskCache.get(key.hashCode() + "");
            if (snapshot != null) {
                return true;
            }
        } catch (IOException e) {
        }
        return false;
    }

    public void loadImage(String url, int size, ScaleMode mode,
                          Drawable placeholder) {
        if (url == null) {
            return;
        }
        ImageView imageView = this.imageViewReference.get();
        String key = url + size;
        Bitmap value = getBitmapFromMemCache(key);
        if (value != null && value.getWidth() > 1 && value.getHeight() > 1) {
            imageView.setImageBitmap(value);
            if (requestListener != null) {
                this.requestListener.onRequestCompleted(value);
            }
        } else if (JSONUtil.cancelPotentialWork(url, imageView)) {
            imageView.setImageDrawable(placeholder);
            executeOnExecutor(Constants.THEADPOOL, url, size, mode);
        }
    }

    public void loadThumbforPath(String url) {
        if (url == null) {
            return;
        }
        ImageView imageView = this.imageViewReference.get();
        Bitmap value = getBitmapFromMemCache(url);
        if (value != null) {
            imageView.setImageBitmap(value);
            if (requestListener != null) {
                this.requestListener.onRequestCompleted(value);
            }
        } else {
            Bitmap bm = ThumbnailUtils.createVideoThumbnail(url,
                    Video.Thumbnails.MINI_KIND);
            imageView.setImageBitmap(bm);
            addBitmapToCache(url, bm);
            if (requestListener != null) {
                this.requestListener.onRequestCompleted(bm);
            }
        }
    }

    private ImageView getAttachedImageView() {
        final ImageView imageView = imageViewReference.get();
        if (imageView instanceof RoundedImageView) {
            return imageView;
        }
        final com.autosos.yd.task.ImageLoadTask bitmapWorkerTask = getBitmapWorkerTask(imageView);
        if (this == bitmapWorkerTask) {
            return imageView;
        }
        return null;
    }

    @Override
    public void transferred(int transferedBytes, String url) {
        transferred += transferedBytes;
        publishProgress(transferred, url);
    }

    @Override
    public void setContentLength(long contentLength, String url) {
        if (progressBar != null) {
            progressBar.setMax((int) contentLength);
        }
    }
}
