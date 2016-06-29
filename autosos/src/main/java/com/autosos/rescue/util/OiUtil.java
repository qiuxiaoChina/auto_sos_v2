package com.autosos.rescue.util;

import android.content.Context;
import android.os.Environment;
import android.util.Log;
import android.widget.Toast;

import java.io.BufferedWriter;
import java.io.ByteArrayOutputStream;
import java.io.File;
import java.io.FileInputStream;
import java.io.FileNotFoundException;
import java.io.FileOutputStream;
import java.io.IOException;
import java.io.InputStream;
import java.io.OutputStreamWriter;

/**
 * Created by Administrator on 2016/6/27.
 */
public class OiUtil {

    public final static String path_drag = Environment.getExternalStorageDirectory()+"/com.autosos.rescue/";

    public String  readJWD(String path) {
//        if(context == null){
//            Toast.makeText(context,"error",Toast.LENGTH_LONG).show();
//        }
        try {
            File file = new File(path +"locationdrag.txt");
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


    public  File getFilePath(String filePath,
                                   String fileName) {
        File file = null;
        makeRootDirectory(filePath);
        try {
            file = new File(filePath + fileName);
        } catch (Exception e) {
            // TODO Auto-generated catch block
            e.printStackTrace();
        }
        return file;
    }

    public  void makeRootDirectory(String filePath) {
        File file = null;
        try {
            file = new File(filePath);
            if (!file.exists()) {
                file.mkdir();
            }
        } catch (Exception e) {

        }
    }


    public void writeJWD(Double latitude, Double longitude,String path) {

        try {
            File file = getFilePath(path,"locationdrag.txt");
            BufferedWriter fileOutputStream  =  new BufferedWriter(new OutputStreamWriter(
                    new FileOutputStream(file, true)));
            if (fileOutputStream != null) {
                long now_time = System.currentTimeMillis() / 1000;
                fileOutputStream.write(longitude + "," + latitude + "|");
                fileOutputStream.flush();
                fileOutputStream.close();
            }
        } catch (IOException e) {
            e.printStackTrace();
        }
    }

    public void deleteJWD(String path){

        File file = new File(path+"locationdrag.txt");
        if(file.exists()){
            file.delete();
        }
         Log.d("OiUtil","deleted");
    }
}
