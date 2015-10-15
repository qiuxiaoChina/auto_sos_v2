package com.autosos.yd.util;

import android.content.Context;
import android.os.Environment;
import android.os.Handler;
import android.os.Message;
import android.util.Log;
import android.widget.Toast;

import com.autosos.yd.R;

import org.apache.http.Header;
import org.apache.http.HttpResponse;
import org.apache.http.client.HttpClient;
import org.apache.http.client.methods.HttpGet;
import org.apache.http.impl.client.DefaultHttpClient;
import org.apache.http.message.BasicHeader;

import java.io.BufferedWriter;
import java.io.ByteArrayOutputStream;
import java.io.File;
import java.io.FileInputStream;
import java.io.FileNotFoundException;
import java.io.FileOutputStream;
import java.io.IOException;
import java.io.InputStream;
import java.io.OutputStreamWriter;
import java.io.RandomAccessFile;
import java.net.HttpURLConnection;
import java.net.URL;

/**
 * Created by Administrator on 2015/8/28.
 */
public class DownLoad {
    public static final String download_path = Environment.getExternalStorageDirectory() + "/com.autosos.jd/download_record.txt";
    public int count = 0;
    public final String TAG = "DownLoad";
    public void downLoad(String strPath,
                               String filename, Handler handler ,Context context) {
        try {
            URL url = new URL(strPath);
            HttpURLConnection conn = (HttpURLConnection) url.openConnection();
            //设置断点续传的开始位置
            File download_record = new File(download_path);
            String from = "0";
            if(download_record.exists()) {
                from = readTXT();
            }
            Log.e(TAG,"22222");
            count = Integer.parseInt(from);
            conn.setAllowUserInteraction(true);
            conn.setRequestProperty("User-Agent", "NetFox");// 设置User-Agent
        //    conn.setRequestProperty("RANGE", "bytes=0");// 设置断点续传的开始位置
            conn.setRequestMethod("POST");
            conn.connect();
            int length = conn.getContentLength();
            InputStream is = conn.getInputStream();
            File file = new File(Environment.getExternalStorageDirectory() + "/com.autosos.jd/");
            if (!file.exists()) {
                file.mkdir();
            }
            String apkFile = Environment.getExternalStorageDirectory() + "/com.autosos.jd/" + filename;
            File APKFile = new File(apkFile);
            FileOutputStream fos = new FileOutputStream(APKFile);
            byte buff[] = new byte[1024];
            Log.e(TAG,"1111");
            while (true) {
                int numread = is.read(buff);
                count += numread;
                setTXT(count);
                int progress = (int) (((float) count / length) * 100);
                Message msg = new Message();
                msg.what = 2;
                msg.obj = progress;
                handler.sendMessage(msg);
                //Handler.sendEmptyMessage(DOWN_UPDATE);
                if (numread <= 0) {
                    delectTXT();
                    Message msg2 = new Message();
                    msg2.what = 3;
                    handler.sendMessage(msg2);
                    break;
                }
                fos.write(buff, 0, numread);
            }//while(!interceptFlag);
            fos.close();
            is.close();
        } catch (Exception e) {
            Log.e("DOWNLOAD", "ERRor" + e.toString());
           // Toast.makeText(context, R.string.msg_update_error, Toast.LENGTH_SHORT).show();
        }
    }
    public String readTXT(){
        try {
            File file = new File(download_path);
            InputStream in = new FileInputStream(file);
            ByteArrayOutputStream stream = new ByteArrayOutputStream();
            byte[] buffer = new byte[1024];
            int length;
            while ((length = in.read(buffer)) != -1) {
                stream.write(buffer, 0, length);
            }
            String wjs = stream.toString();
            return wjs;
        } catch (FileNotFoundException e) {
            return "0";
        } catch (IOException e) {
            return "0";
        }
    }
    public void setTXT(int count){
        try {
            File file = new File(download_path);
            if(!file.exists()){
                file.mkdir();
            }
            BufferedWriter fileOutputStream  =    new BufferedWriter(new OutputStreamWriter(
                    new FileOutputStream(file, false)));
            if (fileOutputStream != null) {
                long now_time = System.currentTimeMillis() / 1000;
                fileOutputStream.write(count+"");
                fileOutputStream.flush();
                fileOutputStream.close();
            }
        } catch (IOException e) {
            e.printStackTrace();
        }
    }
    public void delectTXT(){
        File file = new File(download_path);
        if(file.exists()){
            file.delete();
        }
    }
}
