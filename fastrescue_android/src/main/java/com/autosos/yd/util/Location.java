package com.autosos.yd.util;

import android.content.Context;
import android.content.SharedPreferences;
import android.os.Environment;
import android.util.Log;
import android.webkit.CookieManager;
import android.webkit.CookieSyncManager;
import android.widget.Toast;

import com.autosos.yd.Constants;
import com.autosos.yd.R;
import com.autosos.yd.model.User;
import com.baidu.location.BDLocation;
import com.baidu.location.BDLocationListener;
import com.baidu.location.LocationClient;
import com.baidu.location.LocationClientOption;
import com.baidu.mapapi.SDKInitializer;
import com.baidu.mapapi.model.LatLng;
import com.baidu.mapapi.utils.DistanceUtil;

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
import java.util.ArrayList;
import java.util.LinkedList;
import java.util.List;

/**
 * Created by Administrator on 2015/7/30.
 */
public class Location {
    public final static String path_arive = Environment.getExternalStorageDirectory()+"/com.autosos.jd/location.txt";
    public final static String path_drag = Environment.getExternalStorageDirectory()+"/com.autosos.jd/locationdrag.txt";
    private String myPath;
    final String TAG = "Write Location" ;
    private double test = 0.000005;
    private float distace = 0;
    private Context mcontext;
    private LocationClient client;
    private double la = 0;
    private double lo = 0;
    private boolean first;
    private int point;
    private SharedPreferences sharedPreferences;
    public String getTrance(String path,Context context){
        if(context == null){
            Toast.makeText(context,context.getResources().getString(R.string.msg_unknow_error),Toast.LENGTH_LONG).show();
        }
        String x =readJWD(path,context);
        if(x == null) {
            long time = System.currentTimeMillis()/1000;
            x = UpdateStateServe.latitude + "," + UpdateStateServe.longitude + "#"+ time + "|";
        }
        Log.e(TAG, "wjd: " + x);
        x = x.substring(0, x.length()-1);
        return x;
    }
    public String getTrance(String path,Context context ,int type){
        if(context == null){
            Toast.makeText(context,context.getResources().getString(R.string.msg_unknow_error),Toast.LENGTH_LONG).show();
        }
        String x =readJWD(path,context,2);
        if(x == null) {
            long time = System.currentTimeMillis()/1000;
            x = UpdateStateServe.latitude + "," + UpdateStateServe.longitude + "#"+ time + "|";
        }
        Log.e(TAG, "wjd: " + x);
        x = x.substring(0, x.length()-1);
        return x;
    }
    public int getPoint(){
        return point;
    }
    public double getLo(){
        return lo;
    }
    public double getLa(){
        return la;
    }
    public String  readJWD(String path,Context context) {
        if(context == null){
            Toast.makeText(context,"error",Toast.LENGTH_LONG).show();
        }
        try {
            File file = new File(path);
            if(!file.exists()){
                return null;
            }
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
            return null;
        } catch (IOException e) {
            return null;
        }
    }

    public String  readJWD(String path,Context context, int type) {
        if(context == null){
            Toast.makeText(context,"error",Toast.LENGTH_LONG).show();
        }
        try {
            InputStream in = context.openFileInput(Constants.LOCATION_ARRIVE);
            ByteArrayOutputStream stream = new ByteArrayOutputStream();
            byte[] buffer = new byte[1024];
            int length;
            while ((length = in.read(buffer)) != -1) {
                stream.write(buffer, 0, length);
            }
            String wjs = stream.toString();
            return wjs;
        } catch (FileNotFoundException e) {
            return null;
        } catch (IOException e) {
            return null;
        }
    }

    //arive
    public void writeJWD(Double latitude, Double longitude,Context context,double last_la,double last_lo) {
        if(context == null){
            Toast.makeText(context,"error",Toast.LENGTH_LONG).show();
        }

       // FileOutputStream fileOutputStream = context.openFileOutput(Constants.USER_FILE, Context.MODE_APPEND);

        double distance = Utils.getPoint2Double(Utils.getGeoDistance(longitude, latitude, last_lo, last_la) );
        int testC = 5;
        if(Constants.DEBUG) testC = 0;
        if(distance > 500 || distance < testC){
            Log.e(TAG,distance + ">420 or < 10,dont jisuan ");
        }else {
            try {
             //   File file = new File(Constants.LOCATION_ARRIVE);
               // if (!file.exists()) {
                 //   file.createNewFile();
                //}
                //BufferedWriter fileOutputStream = new BufferedWriter(new OutputStreamWriter(
                  //      new FileOutputStream(file, true)));
                FileOutputStream fileOutputStream = context.openFileOutput(Constants.LOCATION_ARRIVE, Context.MODE_APPEND);
                OutputStreamWriter out = new OutputStreamWriter(
                        fileOutputStream);
                if (fileOutputStream != null) {
                    long now_time = System.currentTimeMillis() / 1000;
                   // fileOutputStream.write(latitude + "," + longitude + "#" + now_time + "|");
                   // fileOutputStream.flush();
                   // fileOutputStream.close();
                    out.write(latitude + "," + longitude + "#" + now_time + "|");
                    out.flush();
                    out.close();
                    fileOutputStream.close();
                }
            } catch (IOException e) {
                e.printStackTrace();
            }
        }
        last_lo = lo;
        last_la = la;
    }
    //drag
    public void writeJWD(Double latitude, Double longitude,String path,Context context) {
        if (context == null) {
            return;
        }
        try {
            File file = new File(path);
            if(!file.exists()){
                file.createNewFile();
            }
            BufferedWriter fileOutputStream  =  new BufferedWriter(new OutputStreamWriter(
                    new FileOutputStream(file, true)));
            if (fileOutputStream != null) {
                long now_time = System.currentTimeMillis() / 1000;
                fileOutputStream.write(latitude + "," + longitude + "#" + now_time + "|");
                fileOutputStream.flush();
                fileOutputStream.close();
            }
        } catch (IOException e) {
            Log.e(TAG, e.toString());
            e.printStackTrace();
        }
    }
    public void deleteJWD(String path,Context context){
        if (context == null)
            return;
        File file = new File(path);
        if(file.exists()){
            file.delete();
        }
    }
    public void deleteJWD(String path,Context context ,int type){
        if (context == null)
            return;
        try {
            context.deleteFile(Constants.LOCATION_ARRIVE);
        }catch (Exception e ){
            e.printStackTrace();
        }
        Log.e(TAG,"arive.txt delect!");
        // context.deleteFile(Constants.LOCATION_FILE);
    }
    public String cutErrorPoint(String trance){
        String [] ss = trance.split("\\|");
        String [] ss2 = new String[ss.length];
        String [] time = new String[ss.length];
        String [] las = new String[ss.length];
        String [] los = new String[ss.length];
        String stra = "";
        double last_distance,now_distance,next_distance;
        if (ss.length < 10)
            return trance;
        for (int i = 0;i<ss.length;i++){
            String[] x = ss[i].split("#");
            ss2[i] = x[0];
            time[i] = x[1];
        }
        for (int i = 0;i<ss2.length;i++){
            String [] x = ss2[i].split(",");
            las [i] = x[0];
            los [i] = x[1];
        }

        last_distance = Utils.getPoint2Double(Utils.getGeoDistance(Double.valueOf(los[0]),
                Double.valueOf(las[0]), Double.valueOf(los[1]), Double.valueOf(las[1])));
        for (int i = 2, j = 0;i <las.length-2;i++){
            now_distance = Utils.getPoint2Double(Utils.getGeoDistance(Double.valueOf(los[i]),
                    Double.valueOf(las[i]), Double.valueOf(los[i + 1]), Double.valueOf(las[i + 1])));

            next_distance = Utils.getPoint2Double(Utils.getGeoDistance(Double.valueOf(los[i+1]),
                    Double.valueOf(las[i+1]), Double.valueOf(los[i+2]), Double.valueOf(las[i+2])));

      //      Log.e(TAG,"last_distance:"+ last_distance +"now_distance:"+ now_distance+"next_distance:"+ next_distance);
            if (cherk(last_distance,now_distance,next_distance)){
                stra +=las[i]+","+los[i]+"#"+time[i]+"|";
                Log.e(TAG,"i   :" + i);
                last_distance = now_distance;
            }else{
                j++;
            }
            if (j>6 && now_distance >10){
                last_distance = now_distance;
                j = 0;
            }
        }
        if(stra.length() > 2)
            stra = stra.substring(0,stra.length()-1);
        Log.e(TAG,stra + "\n\n");
        return stra;
    }
    public String cutErrorPoint2(String trance){
        String [] ss = trance.split("\\|");
        String [] ss2 = new String[ss.length];
        String [] time = new String[ss.length];
        String [] las = new String[ss.length];
        String [] los = new String[ss.length];
        String stra = "";
        if (ss.length < 10)
            return trance;
        for (int i = 0;i<ss.length;i++){
            String[] x = ss[i].split("#");
            ss2[i] = x[0];
            time[i] = x[1];
        }
        for (int i = 0;i<ss2.length;i++){
            String [] x = ss2[i].split(",");
            las [i] = x[0];
            los [i] = x[1];
        }
        String t = "";
        //double xielv;
        //for (int i = 1;i <las.length-1;i++){
            //xielv = cherkXeLv(Double.valueOf(las[i-1]),Double.valueOf(los[i-1]),
              //      Double.valueOf(las[i]),Double.valueOf(los[i]),
                //    Double.valueOf(las[i+1]),Double.valueOf(los[i+1]));

          //  Log.e(TAG,"xie lv       ****" + "      length   :"+i);
      //  }
        List<String> list1 = new LinkedList<String>();
        List<String> list2 = new LinkedList<String>();
        for(int i = 0; i < las.length; i++) {
            if((!list1.contains(las[i]))  && (!list2.contains((los[i])))) {
                list1.add(las[i]);
                list2.add((los[i]));
                t += las[i]+","+los[i]+"#"+time[i]+"|";
            }
            else{
                Log.e(TAG,"chong fu dian   :"+i);
            }
        }
        if(stra.length() > 2)
            t = t.substring(0,t.length()-1);
        return t;
    }
    public boolean cherk(double a,double b,double c){
        if (a<30 && b<30 && c<30)
            return true;
        if (a==0 || b==0 || c==0)
            return false;
        double min,max;
        max = c;
        min = a;
        if (a>c){
            max=a;
            min=c;
        }
        if (b/max > 5 || max/b > 5)
            return false;


        if (b/min > 5 || min/b > 5)
            return false;

        return true;
    }
    public double cherkXeLv(double last_la,double last_lo,double la,double lo,double nextla,double nextlo){
        double dx1,dx2,dy1,dy2;
        double angle;
        dx1 = la -last_la;
        dy1 = lo - last_lo;
        dx2 = nextla - la;
        dy2 = nextlo - lo;
        double c = Math.sqrt(dx1 * dx1 + dy1 * dy1) * Math.sqrt(dx2 * dx2 + dy2 * dy2);
        if(c == 0)
            return 90;
        angle = Math.acos((dx1 * dx2 + dy1 * dy2) / c);
        return angle * 180 / 3.14159;
    }
}
