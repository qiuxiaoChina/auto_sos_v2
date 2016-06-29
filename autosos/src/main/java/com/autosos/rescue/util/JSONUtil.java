package com.autosos.rescue.util;

import android.app.ProgressDialog;
import android.content.ContentResolver;
import android.content.Context;
import android.content.res.Resources;
import android.database.Cursor;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.graphics.Matrix;
import android.graphics.Point;
import android.graphics.drawable.Drawable;
import android.media.ExifInterface;
import android.net.ConnectivityManager;
import android.net.NetworkInfo;
import android.net.Uri;
import android.os.Build;
import android.os.ParcelFileDescriptor;
import android.telephony.TelephonyManager;
import android.util.Log;
import android.view.Display;
import android.view.WindowManager;
import android.widget.ImageView;


import com.autosos.rescue.Constants;
import com.autosos.rescue.R;
import com.autosos.rescue.entity.ImageLoadProgressListener;
import com.autosos.rescue.entity.ProgressEntityWrapper;
import com.autosos.rescue.entity.ProgressListener;
import com.autosos.rescue.model.User;
import com.autosos.rescue.task.HttpPatch;

import org.apache.http.HttpEntity;
import org.apache.http.HttpResponse;
import org.apache.http.NameValuePair;
import org.apache.http.client.ClientProtocolException;
import org.apache.http.client.HttpClient;
import org.apache.http.client.entity.UrlEncodedFormEntity;
import org.apache.http.client.methods.HttpDelete;
import org.apache.http.client.methods.HttpEntityEnclosingRequestBase;
import org.apache.http.client.methods.HttpGet;
import org.apache.http.client.methods.HttpPost;
import org.apache.http.client.methods.HttpPut;
import org.apache.http.entity.StringEntity;
import org.apache.http.entity.mime.HttpMultipartMode;
import org.apache.http.entity.mime.MultipartEntity;
import org.apache.http.entity.mime.content.FileBody;
import org.apache.http.entity.mime.content.StringBody;
import org.apache.http.impl.client.DefaultHttpClient;
import org.apache.http.message.AbstractHttpMessage;
import org.apache.http.message.BasicNameValuePair;
import org.apache.http.params.CoreConnectionPNames;
import org.apache.http.util.EntityUtils;
import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import java.io.ByteArrayInputStream;
import java.io.ByteArrayOutputStream;
import java.io.File;
import java.io.FileNotFoundException;
import java.io.FileOutputStream;
import java.io.IOException;
import java.io.InputStream;
import java.io.OutputStream;
import java.io.OutputStreamWriter;
import java.io.UnsupportedEncodingException;
import java.net.URLEncoder;
import java.nio.charset.Charset;
import java.security.MessageDigest;
import java.security.NoSuchAlgorithmException;
import java.sql.Timestamp;
import java.util.ArrayList;
import java.util.Calendar;
import java.util.Date;
import java.util.List;
import java.util.Map;
import java.util.Map.Entry;
import java.util.TimeZone;

import javax.microedition.khronos.egl.EGL10;
import javax.microedition.khronos.egl.EGLConfig;
import javax.microedition.khronos.egl.EGLContext;
import javax.microedition.khronos.egl.EGLDisplay;

public class JSONUtil {
    private static final String TAG = JSONUtil.class.getName();
    private static int MaximumTextureSize;
    private static Point deviceSize;
    private static String cha;

    public static String getString(JSONObject obj, String name) {
        return obj.isNull(name) ? null : obj.optString(name);
    }

    public static JSONArray getJSONArray(JSONObject obj, String name) {
        try {
            return obj.isNull(name) ? null : obj.getJSONArray(name);
        } catch (JSONException e) {
            e.printStackTrace();
        }
        return null;
    }

    public static String getImagePath(String str, int width, int height) {
        return isEmpty(str) ? null : (str.startsWith("http") ? (str + (str.contains("?") ? String
                .format(Constants.PHOTO_URL, width, height).replace("?", getURLEncoder()) : String.format
                (Constants.PHOTO_URL, width, height))) : Constants.HOST + str );
    }
    public static String getImagePath2(String str, int width, int height) {
        return isEmpty(str) ? null : (str.startsWith("http") ? (str + (str.contains("?") ? String
                .format(Constants.PHOTO_URL2, width, height).replace("?", getURLEncoder()) : String.format
                (Constants.PHOTO_URL2, width, height))) : Constants.HOST + str);
    }

    public static Date getDataFromTimStamp(JSONObject jsonObject, String name) {
        if (jsonObject.isNull(name)) {
            return null;
        }
        Long timeInMillis = jsonObject.optLong(name);
        Timestamp timestamp = new Timestamp(timeInMillis * 1000L);
        Calendar calendar = Calendar.getInstance(TimeZone.getDefault());
        calendar.setTimeInMillis(timestamp.getTime());

        return calendar.getTime();
    }

    public static String getStringFromUrl(Context context,String url) throws IOException {
        HttpGet get = new HttpGet(url);
        HttpClient client = new DefaultHttpClient();
        auth(context, get);
        try {
            client.getParams().setParameter(CoreConnectionPNames.CONNECTION_TIMEOUT, 20000);
            HttpResponse response = client.execute(get);
            HttpEntity entity = response.getEntity();
            return EntityUtils.toString(entity, "UTF-8");
        } catch (Exception e) {
            return null;
        }
    }

    private static void auth(Context context,AbstractHttpMessage ahm) {
        Session.getInstance().cherkSession(context);
        String mtype = Build.MODEL;      //手机型号
        TelephonyManager tm = (TelephonyManager)context.getSystemService(Context.TELEPHONY_SERVICE);
        String DEVICE_ID = tm.getDeviceId();
        Log.e("imeid","DEVICE_ID === " + DEVICE_ID);
        User u = Session.getInstance().getCurrentUser(context);
        int sdkversion = Integer.valueOf(Build.VERSION.SDK_INT);
        String androidversion = Tools.getAndroidVersion(sdkversion);
        String clientId = context.getSharedPreferences(Constants.PREF_FILE,
                Context.MODE_PRIVATE).getString("clientid", null);
        String net_type = "";
        try{
            net_type = Tools.GetNetworkType(context);
        }catch (Exception  e ){
            e.printStackTrace();
        }
        ahm.setHeader("netkind",net_type);
        ahm.setHeader("getuicid",clientId);
        ahm.setHeader("deviceid", DEVICE_ID);
        ahm.setHeader("devicekind","android"+androidversion);
        ahm.setHeader("devicemodel",mtype);
        ahm.setHeader("appver", Constants.APP_VERSION);
        if (u != null) {
            /*
            if(Session.getInstance().cherkSession(context))
                ahm.setHeader("access-token", u.getToken());
            else{
                ahm.setHeader("access-token",Session.getInstance().getNewToken(context));
            }
            */
            ahm.setHeader("access-token", u.getToken());
            // Log.e(TAG, "token：" + u.getToken().toString()+"---mtype  :"+mtype+"---ID"+DEVICE_ID+"--------sdk"+sdkversion+"--------"+clientId);
        }
    }


    public static String delete(Context context,String url) throws IOException {
        HttpDelete delete = new HttpDelete(url);
        auth(context,delete);
        HttpClient client = new DefaultHttpClient();
        HttpResponse response = client.execute(delete);
        HttpEntity entity = response.getEntity();
        if (entity != null) {
            return EntityUtils.toString(entity, "UTF-8");
        }
        return null;
    }

    public static String newPutJson(Context context,String url, Map<String, Object> map) throws IOException {
        HttpPut put = new HttpPut(url);
        return upload(context,map, put);
    }

    public static String postJson(Context context,String url, Map<String, Object> map) throws IOException {
        HttpPost post = new HttpPost(url);
        return upload(context,map, post);
    }

    public static String postJsonTextWithAttach(Context context,String url, Map<String, Object> map,
                                                ProgressListener listener) throws IOException {
        Log.d(TAG, "prepare upload: " + System.currentTimeMillis());
        HttpPost post = new HttpPost(url);
        return upload(context,map, post, listener);
    }

    public static String putJsonTextWithAttach(Context context,String url, Map<String, Object> map,
                                               ProgressListener listener) throws IOException {
        Log.d(TAG, "prepare upload: " + System.currentTimeMillis());
        HttpPut put = new HttpPut(url);
        return upload(context,map, put, listener);
    }

    public static String patchTextWithAttach(Context context,String url, Map<String, Object> map,
                                             ProgressListener listener) throws IOException {
        HttpPatch patch = new HttpPatch(url);
        return upload(context,map, patch, listener);
    }

    public static String putJson(Context context,String url, Map<String, Object> map) throws IOException {
        HttpPut put = new HttpPut(url);
        return upload(context,map, put);
    }

    public static String patchJson(Context context,String url, Map<String, Object> map) throws IOException {
        HttpPatch put = new HttpPatch(url);
        return upload(context, map, put);
    }

    public static String patchJsonWithAttach(Context context,String url, String json, ProgressListener listener) throws IOException {
        HttpPatch patch = new HttpPatch(url);
        return uploadJson(context,json, patch, listener);
    }

    public static String postJsonWithAttach(Context context,String url, String json, ProgressListener listener) throws IOException {
        HttpPost post = new HttpPost(url);
        return uploadJson(context,json, post, listener);
    }

    public static String newPutJsonWithAttach(Context context,String url, String json, ProgressListener listener) throws IOException {
        HttpPut put = new HttpPut(url);
        return uploadJson(context, json, put, listener);
    }

    private static String upload(Context context,Map<String, Object> map, HttpEntityEnclosingRequestBase method) throws IOException {
        auth(context,method);
        if (map != null) {
            List<NameValuePair> params = new ArrayList<NameValuePair>();
            for (Entry<String, Object> entry : map.entrySet()) {
                NameValuePair param = new BasicNameValuePair(entry.getKey(),
                        String.valueOf(entry.getValue()));
                params.add(param);
            }
            method.setEntity(new UrlEncodedFormEntity(params, "UTF-8"));
        }
        HttpClient client = new DefaultHttpClient();
        // 请求超时
        client.getParams().setParameter(CoreConnectionPNames.CONNECTION_TIMEOUT, 20000);
        HttpResponse response = client.execute(method);
        HttpEntity entity = response.getEntity();
        return EntityUtils.toString(entity, "UTF-8");
    }

    private static String upload(Context context,Map<String, Object> map, HttpEntityEnclosingRequestBase method,
                                 ProgressListener listener) throws IOException {
        auth(context,method);

        if (map != null) {
            List<NameValuePair> params = new ArrayList<NameValuePair>();
            addParts(params, map);
            if (listener != null) {
                method.setEntity(new ProgressEntityWrapper(new UrlEncodedFormEntity(params,
                        "UTF-8"), listener));
            } else {
                method.setEntity(new UrlEncodedFormEntity(params, "UTF-8"));
            }
        }
        HttpClient client = new DefaultHttpClient();
        // 请求超时
        client.getParams().setParameter(CoreConnectionPNames.CONNECTION_TIMEOUT, 20000);
        HttpResponse response = client.execute(method);
        HttpEntity entity = response.getEntity();
        return EntityUtils.toString(entity, "UTF-8");
    }

    private static String uploadJson(Context context,String json, HttpEntityEnclosingRequestBase method,
                                     ProgressListener listener) throws IOException {
        auth(context, method);
        if (!JSONUtil.isEmpty(json)) {
            method.setHeader("Content-Type", "application/json");
            method.setHeader("Accept", "application/json");
            if (listener != null) {
                method.setEntity(new ProgressEntityWrapper(new StringEntity(json, "UTF-8"),
                        listener));
            } else {
                method.setEntity(new StringEntity(json, "UTF-8"));
            }
        }
        HttpClient client = new DefaultHttpClient();
        // 请求超时
        client.getParams().setParameter(CoreConnectionPNames.CONNECTION_TIMEOUT, 20000);
        HttpResponse response = client.execute(method);
        HttpEntity entity = response.getEntity();
        return EntityUtils.toString(entity, "UTF-8");
    }


    public static String postJsonWithAttach(Context context, String url, Map<String, Object> map,
                                            ProgressListener listener, ContentResolver cr) throws IOException {
        Log.d(TAG, "prepare upload: " + System.currentTimeMillis());
        HttpPost post = new HttpPost(url);
        return uploadWithAttach(context,map, listener, post, cr);
    }

    public static String putJsonWithAttach(Context context, String url, Map<String, Object> map,
                                           ProgressListener listener, ContentResolver cr) throws IOException {
        Log.d(TAG, "prepare upload: " + System.currentTimeMillis());
        HttpPut put = new HttpPut(url);
        return uploadWithAttach(context, map, listener, put, cr);
    }

    public static String putTextWithAttach(Context context,String url, Map<String, Object> map,
                                           ProgressListener listener) throws IOException {
        HttpPut put = new HttpPut(url);
        return upload(context,map, put, listener);
    }

    private static String uploadWithAttach(Context context,Map<String, Object> map, ProgressListener listener,
                                           HttpEntityEnclosingRequestBase method,
                                           ContentResolver cr) throws IOException {
        auth(context,method);
        MultipartEntity multiPart = new MultipartEntity(HttpMultipartMode.BROWSER_COMPATIBLE);
        List<File> temples = new ArrayList<File>();
        addParts(multiPart, map, temples, cr);
        if (listener == null) {
            method.setEntity(multiPart);
        } else {
            method.setEntity(new ProgressEntityWrapper(multiPart, listener));
        }
        HttpClient client = new DefaultHttpClient();
        // 请求超时
        client.getParams().setParameter(CoreConnectionPNames.CONNECTION_TIMEOUT, 3000);
        Log.d(TAG, "start upload: " + System.currentTimeMillis());
        Log.d(TAG, "uploading size: " + method.getEntity().getContentLength());
        HttpResponse response = client.execute(method);
        Log.d(TAG, "end upload: " + System.currentTimeMillis());
        for (File temple : temples) {
            temple.delete();
        }
        HttpEntity entity = response.getEntity();
        return EntityUtils.toString(entity, "UTF-8");
    }

    public static void addParts(List<NameValuePair> params, Map<String,
            Object> map) throws IOException {
        for (Entry<String, Object> entry : map.entrySet()) {
            if (entry.getValue() != null) {
                if (entry.getValue() instanceof ArrayList<?>) {
                    ArrayList<?> maps = (ArrayList<?>) entry.getValue();
                    if (maps != null) {
                        for (Object childMap : maps) {
                            if (childMap instanceof Map<?, ?>) {
                                Map<String, Object> map2 = (Map<String, Object>) childMap;
                                if (map2 != null) {
                                    addParts(params, map2);
                                }
                            }
                        }
                    }
                } else {
                    NameValuePair param = new BasicNameValuePair(entry.getKey(),
                            String.valueOf(entry.getValue()));
                    params.add(param);
                }
            }
        }
    }

    public static void addParts(MultipartEntity multiPart, Map<String, Object> map,
                                List<File> tmpfiles, ContentResolver cr) throws IOException {
        for (Entry<String, Object> entry : map.entrySet()) {
            if (entry.getValue() != null) {
                if (entry.getValue() instanceof File) {
                    File file = (File) entry.getValue();
                    Log.d(TAG, "file: " + file.getName() + ", size: " + file.length());
                    if (file.getName().contains(".jpg") || file.getName().contains(".JPG")) {
                        File tmpfile = getimage(cr, file);
                        if (tmpfile != null) {
                            tmpfiles.add(tmpfile);
                            multiPart.addPart(entry.getKey(), new FileBody(tmpfile));
                        } else {
                            multiPart.addPart(entry.getKey(), new FileBody(file));
                        }
                    } else {
                        multiPart.addPart(entry.getKey(), new FileBody(file));
                    }
                } else if (entry.getValue() instanceof ArrayList<?>) {
                    ArrayList<?> maps = (ArrayList<?>) entry.getValue();
                    if (maps != null) {
                        for (Object childMap : maps) {
                            if (childMap instanceof Map<?, ?>) {
                                Map<String, Object> map2 = (Map<String, Object>) childMap;
                                if (map2 != null) {
                                    addParts(multiPart, map2, tmpfiles, cr);
                                }
                            } else {
                                multiPart.addPart(entry.getKey(),
                                        new StringBody((String) childMap,
                                                Charset.forName("UTF-8")));
                            }
                        }
                    }
                } else {
                    multiPart.addPart(entry.getKey(), new StringBody((String) entry.getValue(),
                            Charset.forName("UTF-8")));
                }
            }
        }
    }

    public static File createTempFile(ContentResolver cr, File file) throws IOException {
        Log.d(TAG, "original file: " + file.getName() + ", size: " + file.length());
        int degree = getOrientation(file.getAbsolutePath());
        Bitmap nbitmap = scaleImage(cr, file.getAbsolutePath(), degree);
        File tmpfile = File.createTempFile("img-", file.getName());
        FileOutputStream out = new FileOutputStream(tmpfile);
        nbitmap.compress(Bitmap.CompressFormat.JPEG, 95, out);
        out.close();
        Log.d(TAG, "scaled file: " + tmpfile.getName() + ", size: " + tmpfile.length());
        nbitmap.recycle();
        return tmpfile;
    }

    private static File getimage(ContentResolver cr, File file) throws IOException {
        boolean change = false;
        Bitmap bitmap = null;
        BitmapFactory.Options opts = new BitmapFactory.Options();
        opts.inPurgeable = true;
        opts.inInputShareable = true;
        opts.inPreferredConfig = Bitmap.Config.ARGB_8888;
        opts.inMutable = true;
        opts.inJustDecodeBounds = true;
        BitmapFactory.decodeFile(file.getAbsolutePath(), opts);
        int rate = Math.max(opts.outHeight / 2048, opts.outWidth / 1024);
        rate = Math.max(rate, 1);
        if (rate > 1) {
            change = true;
        }
        opts.inJustDecodeBounds = false;
        opts.inSampleSize = rate;
        if (cr != null) {
            ParcelFileDescriptor pfd = cr.openFileDescriptor(Uri.fromFile(file), "r");
            bitmap = BitmapFactory.decodeFileDescriptor(pfd.getFileDescriptor(), null, opts);
            pfd.close();
        } else {
            bitmap = BitmapFactory.decodeFile(file.getAbsolutePath(), opts);
        }
        int degree = JSONUtil.getOrientation(file.getAbsolutePath());
        if (degree > 0) {
            Matrix matrix = new Matrix();
            matrix.postRotate(degree);
            try {
                bitmap = Bitmap.createBitmap(bitmap, 0, 0, bitmap.getWidth(), bitmap.getHeight(),
                        matrix, false);
            } catch (OutOfMemoryError e) {
            }
        }
        if (bitmap != null) {
            ByteArrayOutputStream baos = new ByteArrayOutputStream();
            bitmap.compress(Bitmap.CompressFormat.JPEG, 100, baos);
            int options = 100;
            while ((baos.toByteArray().length / 1024 > 300) && (options > 70)) {
                change = true;
                baos.reset();
                options -= 10;
                bitmap.compress(Bitmap.CompressFormat.JPEG, options, baos);
            }
            baos.close();
            if (!change) {
                bitmap.recycle();
                return null;
            }
            try {
                File out = File.createTempFile("img-", file.getName());
                Uri uri = Uri.fromFile(out);
                OutputStream imageOut = cr.openOutputStream(uri);
                bitmap.compress(Bitmap.CompressFormat.JPEG, options, imageOut);
                imageOut.flush();
                imageOut.close();
                return out;
            } catch (IOException e) {
                // TODO Auto-generated catch block
                e.printStackTrace();
            } finally {
                bitmap.recycle();
            }
        }
        return null;
    }

    public static Bitmap scaleImage(ContentResolver cr, String path,
                                    int degree) throws IOException {
        Bitmap bitmap = getImageFromPath(cr, path, 0);
        if (Math.min(bitmap.getWidth(), bitmap.getHeight()) > Constants.DEFAULT_IMAGE_SIZE) {
            int w = bitmap.getWidth();
            int h = bitmap.getHeight();
            if (w > h) {
                w = Math.round((w * (float) Constants.DEFAULT_IMAGE_SIZE / (float) h));
                h = Constants.DEFAULT_IMAGE_SIZE;
            } else {
                h = Math.round((h * (float) Constants.DEFAULT_IMAGE_SIZE) / (float) w);
                w = Constants.DEFAULT_IMAGE_SIZE;
            }
            Bitmap nbitmap;
            nbitmap = Bitmap.createScaledBitmap(bitmap, w, h, true);
            bitmap.recycle();
            bitmap = null;
            return nbitmap;
        }
        return bitmap;
    }





    public static int getMaximumTextureSize() {
        if (MaximumTextureSize == 0) {
            EGL10 egl = (EGL10) EGLContext.getEGL();

            EGLDisplay display = egl.eglGetDisplay(EGL10.EGL_DEFAULT_DISPLAY);

            // Initialise
            int[] version = new int[2];
            egl.eglInitialize(display, version);

            // Query total number of configurations
            int[] totalConfigurations = new int[1];
            egl.eglGetConfigs(display, null, 0, totalConfigurations);

            // Query actual list configurations
            EGLConfig[] configurationsList = new EGLConfig[totalConfigurations[0]];
            egl.eglGetConfigs(display, configurationsList, totalConfigurations[0],
                    totalConfigurations);

            int[] textureSize = new int[1];
            int maximumTextureSize = 0;

            // Iterate through all the configurations to located the maximum
            // texture
            // size
            for (int i = 0; i < totalConfigurations[0]; i++) {
                // Only need to check for width since opengl textures are always
                // squared
                egl.eglGetConfigAttrib(display, configurationsList[i],
                        EGL10.EGL_MAX_PBUFFER_WIDTH, textureSize);

                // Keep track of the maximum texture size
                if (maximumTextureSize > textureSize[0] || maximumTextureSize == 0) {
                    maximumTextureSize = textureSize[0];
                }

                Log.i("GLHelper", Integer.toString(textureSize[0]));
            }

            // Release
            egl.eglTerminate(display);
            Log.i("GLHelper", "Maximum GL texture size: " + Integer.toString(maximumTextureSize));
            MaximumTextureSize = maximumTextureSize;
        }

        return MaximumTextureSize;
    }

    public static byte[] readStreamToByteArray(InputStream is) {
        return readStreamToByteArray(is, null, null);
    }

    public static byte[] readStreamToByteArray(InputStream is, ImageLoadProgressListener
            listener, String url) {
        ByteArrayOutputStream baos = new ByteArrayOutputStream();
        byte[] buffer = new byte[1024];
        int length = -1;
        byte[] data = null;
        try {
            while ((length = is.read(buffer)) != -1) {
                baos.write(buffer, 0, length);
                if (listener != null) {
                    listener.transferred(length, url);
                }
            }
            baos.flush();
        } catch (IOException e) {
            e.printStackTrace();
        }
        data = baos.toByteArray();
        try {
            is.close();
            baos.close();
        } catch (IOException e) {
            e.printStackTrace();
        }
        return data;
    }

    public static byte[] readStreamToByteArray(InputStream is, ImageLoadProgressListener
            listener, String url, long size) {
        ByteArrayOutputStream baos = new ByteArrayOutputStream();
        byte[] buffer = new byte[1024];
        int length = -1;
        long mHasRead = 0;
        byte[] data = null;
        try {
            while ((length = is.read(buffer)) != -1) {
                baos.write(buffer, 0, length);
                mHasRead += length;
                if (listener != null) {
                    listener.transferred(length, url);
                }
            }
            baos.flush();
        } catch (IOException e) {
            e.printStackTrace();
        }
        if (mHasRead == size) {
            data = baos.toByteArray();
        }
        try {
            is.close();
            baos.close();
        } catch (IOException e) {
            e.printStackTrace();
        }
        return data;
    }

    public static String readStreamToString(InputStream is) {
        return new String(readStreamToByteArray(is));
    }

    public static void saveStreamToFile(InputStream in, OutputStream out) {
        saveStreamToFile(in, out, null);
    }

    public static void saveStreamToFile(InputStream in, OutputStream out,
                                        ImageLoadProgressListener listener) {
        byte[] buffer = new byte[1024];
        int length = -1;
        try {
            while ((length = in.read(buffer)) != -1) {
                out.write(buffer, 0, length);
                if (listener != null) {
                    listener.transferred(length, null);
                }
            }
            out.flush();
            out.close();
            in.close();
        } catch (IOException e) {
            e.printStackTrace();
        }
    }

    public static void saveStringToFile(String content, OutputStream out) {
        OutputStreamWriter writer = new OutputStreamWriter(out);
        try {
            writer.write(content);
            writer.close();
            out.close();
        } catch (IOException e) {
            e.printStackTrace();
        }
    }



    public static InputStream getContentFromUrl(String url) throws ClientProtocolException,
            IOException {
        return getContentFromUrl(url, null);
    }

    public static HttpEntity getEntityFromUrl(String url) throws ClientProtocolException,
            IOException {
        return getEntityFromUrl(url, null);
    }

    public static InputStream getContentFromUrl(String url, ImageLoadProgressListener listener)
            throws ClientProtocolException, IOException {
        HttpGet get = new HttpGet(url);
        HttpClient client = new DefaultHttpClient();
        HttpResponse response = client.execute(get);
        HttpEntity entity = response.getEntity();
        if (listener != null) {
            listener.setContentLength(entity.getContentLength(), url);
        }
        return entity.getContent();
    }

    public static HttpEntity getEntityFromUrl(String url, ImageLoadProgressListener listener)
            throws ClientProtocolException, IOException {
        HttpGet get = new HttpGet(url);
        HttpClient client = new DefaultHttpClient();
        HttpResponse response = client.execute(get);
        HttpEntity entity = response.getEntity();
        if (listener != null) {
            listener.setContentLength(entity.getContentLength(), url);
        }
        return entity;
    }


    public static int caculateInSampleSize(BitmapFactory.Options opts, int reqWidth, int reqHeight) {
        int outWidth = opts.outWidth;
        int outHeight = opts.outHeight;
        int inSampleSize = 1;
        if (outWidth > reqWidth || outHeight > reqWidth) {
            int widthScale = Math.round(outWidth / reqWidth);
            int heightScale = Math.round(outHeight / reqHeight);
            inSampleSize = widthScale < heightScale ? widthScale : heightScale;
        }
        return inSampleSize;
    }

    public static Bitmap getImageFromPath(ContentResolver cr, String path, int size,
                                          Bitmap.Config config, boolean isMaxLimit) throws
            IOException {

        BitmapFactory.Options opts = new BitmapFactory.Options();
        opts.inPreferredConfig = config;
        opts.inPurgeable = true;
        opts.inInputShareable = true;
        if (size > 0) {
            opts.inMutable = true;
            opts.inJustDecodeBounds = true;
            BitmapFactory.decodeFile(path, opts);
            int msize = Math.min(opts.outHeight, opts.outWidth);
            if (isMaxLimit) {
                msize = Math.max(opts.outHeight, opts.outWidth);
            }
            int rate = msize / size;
            rate = Math.max(rate, 1);
            opts.inJustDecodeBounds = false;
            opts.inSampleSize = rate;
        }
        Bitmap image;
        if (cr != null) {
            ParcelFileDescriptor pfd = cr.openFileDescriptor(Uri.fromFile(new File(path)), "r");
            image = BitmapFactory.decodeFileDescriptor(pfd.getFileDescriptor(), null, opts);
            pfd.close();
        } else {
            image = BitmapFactory.decodeFile(path, opts);
        }
        if (image == null) {
            return null;
        }
        Matrix matrix = null;
        if (getMaximumTextureSize() > 0 && (image.getWidth() > getMaximumTextureSize() || image
                .getHeight() > getMaximumTextureSize())) {
            float scale = Math.min(getMaximumTextureSize() / (float) image.getWidth(),
                    getMaximumTextureSize() / (float) image.getHeight());
            if (matrix == null) {
                matrix = new Matrix();
                matrix.postScale(scale, scale);
            }
        }
        int degree = getOrientation(path);
        if (degree > 0) {
            if (matrix == null) {
                matrix = new Matrix();
            }
            matrix.postRotate(degree);
        }
        if (matrix != null) {
            try {
                Bitmap bm = Bitmap.createBitmap(image, 0, 0, image.getWidth(), image.getHeight(),
                        matrix, false);
                image.recycle();
                image = null;
                return bm;
            } catch (OutOfMemoryError e) {
                return image;
            }
        }
        return image;
    }

    public static Bitmap getImageFromPath(ContentResolver cr, String path,
                                          int size) throws IOException {
        return getImageFromPath(cr, path, size, Bitmap.Config.RGB_565, false);
    }

    public static Bitmap getImageFromPath(ContentResolver cr, String path, int size,
                                          boolean isMaxLimit) throws IOException {
        return getImageFromPath(cr, path, size, Bitmap.Config.RGB_565, isMaxLimit);
    }


    public static Bitmap getThumbImageForPath(ContentResolver cr, String path, int size) throws FileNotFoundException {
        BitmapFactory.Options opts = new BitmapFactory.Options();
        opts.inPreferredConfig = Bitmap.Config.RGB_565;
        opts.inPurgeable = true;
        opts.inInputShareable = true;
        if (size > 0) {
            opts.inMutable = true;
            opts.inJustDecodeBounds = true;
            BitmapFactory.decodeFile(path, opts);
            int rate = Math.max(opts.outHeight, opts.outWidth) / size;
            rate = Math.max(rate, 1);
            opts.inJustDecodeBounds = false;
            opts.inSampleSize = rate;
        }
        Bitmap image;
        if (cr != null) {
            ParcelFileDescriptor pfd = cr.openFileDescriptor(Uri.fromFile(new File(path)), "r");
            image = BitmapFactory.decodeFileDescriptor(pfd.getFileDescriptor(), null, opts);
        } else {
            image = BitmapFactory.decodeFile(path, opts);
        }
        if (image == null) {
            return null;
        }
        Matrix matrix = null;
        ByteArrayOutputStream baos = new ByteArrayOutputStream();
        image.compress(Bitmap.CompressFormat.JPEG, 100, baos);
        boolean isChange = false;
        int options = 95;
        while (baos.toByteArray().length / 1024 > 32) {
            isChange = true;
            baos.reset();
            image.compress(Bitmap.CompressFormat.JPEG, options, baos);
            options -= 5;
        }
        if (isChange) {
            ByteArrayInputStream isBm = new ByteArrayInputStream(baos.toByteArray());
            image = BitmapFactory.decodeStream(isBm, null, null);
            try {
                isBm.close();
            } catch (IOException e) {
                e.printStackTrace();
            }
        }
        try {
            baos.close();
        } catch (IOException e) {
            e.printStackTrace();
        }
        int degree = getOrientation(path);
        if (degree > 0) {
            if (matrix == null) {
                matrix = new Matrix();
            }
            matrix.postRotate(degree);
        }
        if (matrix != null) {
            try {
                Bitmap bm = Bitmap.createBitmap(image, 0, 0, image.getWidth(), image.getHeight(),
                        matrix, false);
                image.recycle();
                image = null;
                return bm;
            } catch (OutOfMemoryError e) {
                return image;
            }
        }
        return image;
    }

    public static int getOrientation(String path) {
        try {
            ExifInterface exifInterface = new ExifInterface(path);
            int orientation = exifInterface.getAttributeInt(ExifInterface.TAG_ORIENTATION,
                    ExifInterface.ORIENTATION_NORMAL);
            switch (orientation) {
                case ExifInterface.ORIENTATION_ROTATE_90:
                    return 90;
                case ExifInterface.ORIENTATION_ROTATE_180:
                    return 180;
                case ExifInterface.ORIENTATION_ROTATE_270:
                    return 270;
            }
        } catch (IOException e) {
            e.printStackTrace();
        }
        return 0;
    }

    public static String getImagePathForUri(Uri uri, Context context) {
        if (uri.toString().startsWith("file")) {
            return uri.getPath();
        } else {
            Cursor cursor = context.getContentResolver().query(uri, null, null, null, null);
            if (cursor == null) {
                return null;
            }
            cursor.moveToFirst();
            String path = cursor.getString(1);
            int orientation = 0;
            if (path.endsWith(".jpg")) {
                orientation = cursor.getInt(14);
            }
            cursor.close();
            if (orientation != 0) {
                try {
                    ExifInterface exifInterface = new ExifInterface(path);
                    switch (orientation) {
                        case 90:
                            exifInterface.setAttribute(ExifInterface.TAG_ORIENTATION,
                                    String.valueOf(ExifInterface.ORIENTATION_ROTATE_90));
                            break;
                        case 180:
                            exifInterface.setAttribute(ExifInterface.TAG_ORIENTATION,
                                    String.valueOf(ExifInterface.ORIENTATION_ROTATE_180));
                            break;
                        case 270:
                            exifInterface.setAttribute(ExifInterface.TAG_ORIENTATION,
                                    String.valueOf(ExifInterface.ORIENTATION_ROTATE_270));
                            break;
                    }
                    exifInterface.saveAttributes();
                } catch (IOException e) {
                    e.printStackTrace();
                }
            }
            return path;
        }

    }



    public static String removeFileSeparator(String filePath) {
        if (filePath.startsWith(File.separator)) {
            filePath = filePath.substring(1);
        }
        return filePath.replace(File.separator, "-");
    }

    public static Point getDeviceSize(Context context) {
        if (deviceSize == null || deviceSize.x == 0 || deviceSize.y == 0) {
            WindowManager wm = (WindowManager) context.getSystemService(Context.WINDOW_SERVICE);
            Display display = wm.getDefaultDisplay();
            deviceSize = new Point();
            display.getSize(deviceSize);
        }
        return deviceSize;
    }

    public static boolean isEmpty(String str) {
        return str == null || str.equals("") || str.equals("null");
    }

    public static String getWebPath(String str) {
        return JSONUtil.isEmpty(str) ? null : (str.startsWith("http") || str.startsWith("https") ? str : "http://" + str);
    }


    public static String getAudioPath(String path) {
        return JSONUtil.isEmpty(path) ? null : !path.contains("?") ? path +
                "?avthumb/mp3/ab/32k/ar/22050" : path;

    }

    public static String getURLEncoder() {
        if (JSONUtil.isEmpty(cha)) {
            try {
                cha = URLEncoder.encode("|", "UTF-8");
            } catch (UnsupportedEncodingException e) {
                cha = "%7C";
                e.printStackTrace();
            }
        }
        return cha;
    }

    public static Bitmap getPlaceHolder(Resources resources) {
        return BitmapFactory.decodeResource(resources, Constants.PLACEHOLDER);
    }


    public static String getMD5(String str) {
        try {
            MessageDigest digest = MessageDigest.getInstance("MD5");
            digest.update(str.getBytes());
            byte[] bytes = digest.digest();
            StringBuilder sb = new StringBuilder();
            for (byte b : bytes) {
                int temp = 0xff & b;
                if (temp <= 0x0F) {
                    sb.append("0").append(Integer.toHexString(temp));
                } else {
                    sb.append(Integer.toHexString(temp));
                }
            }
            return sb.toString();
        } catch (NoSuchAlgorithmException e) {
            e.printStackTrace();
        }
        return null;
    }

    private static String convertToHex(byte[] data) {
        StringBuilder buf = new StringBuilder();
        for (byte b : data) {
            int halfbyte = (b >>> 4) & 0x0F;
            int two_halfs = 0;
            do {
                buf.append((0 <= halfbyte) && (halfbyte <= 9) ? (char) ('0' + halfbyte) : (char) ('a' + (halfbyte - 10)));
                halfbyte = b & 0x0F;
            } while (two_halfs++ < 1);
        }
        return buf.toString();
    }

    public static String SHA1(String text) throws NoSuchAlgorithmException, UnsupportedEncodingException {
        MessageDigest md = MessageDigest.getInstance("SHA1");
        md.update(text.getBytes("utf-8"));
        byte[] sha1hash = md.digest();
        return convertToHex(sha1hash);
    }

    public static int getAge(Date birthday) {
        if (birthday == null) {
            return 0;
        }
        Calendar old = Calendar.getInstance();
        old.setTime(birthday);
        return Calendar.getInstance().get(Calendar.YEAR) - old.get(Calendar.YEAR);
    }

    public static ProgressDialog getProgress(Context context) {
        ProgressDialog progressDialog = new ProgressDialog(context);
        // progressDialog.setContentView(R.layout.progress_dialog);
        progressDialog.setProgressNumberFormat(null);
        progressDialog.setMessage(context.getString(R.string.msg_preparing));
        progressDialog.setProgressStyle(ProgressDialog.STYLE_HORIZONTAL);
        progressDialog.setCanceledOnTouchOutside(false);
        progressDialog.show();
        return progressDialog;
    }



    public static boolean isNetworkConnected(Context context) {
        if (context != null) {
            ConnectivityManager mConnectivityManager = (ConnectivityManager) context
                    .getSystemService(Context.CONNECTIVITY_SERVICE);
            NetworkInfo mNetworkInfo = mConnectivityManager.getActiveNetworkInfo();
            if (mNetworkInfo != null) {
                return mNetworkInfo.isAvailable();
            }
        }
        return false;
    }

    public static int getConnectedType(Context context) {
        if (context != null) {
            ConnectivityManager mConnectivityManager = (ConnectivityManager) context
                    .getSystemService(Context.CONNECTIVITY_SERVICE);
            NetworkInfo mNetworkInfo = mConnectivityManager.getActiveNetworkInfo();
            if (mNetworkInfo != null && mNetworkInfo.isAvailable()) {
                return mNetworkInfo.getType();
            }
        }
        return -1;
    }

}
