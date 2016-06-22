package com.autosos.yd.util;

import android.content.Context;
import android.content.Intent;
import android.net.Uri;
import android.os.Environment;

import java.io.File;
import java.text.SimpleDateFormat;
import java.util.Date;

public class FileUtil {

    public static void deleteTempFile(String path) {
        File file = new File(path);
        if (file.exists()) {
            file.delete();
        }
    }

    public static void galleryAddPic(Context context, String path) {
        Intent mediaScanIntent = new Intent(
                Intent.ACTION_MEDIA_SCANNER_SCAN_FILE);
        File f = new File(path);
        Uri contentUri = Uri.fromFile(f);
        mediaScanIntent.setData(contentUri);
        context.sendBroadcast(mediaScanIntent);
    }

    public static File getAlbumDir() {
      //  long time = System.currentTimeMillis();
       // File dir = new File(Environment.getExternalStorageDirectory(),getAlbumName());
       File dir = new File(Environment.getExternalStorageDirectory()+"/com.autosos.jd/img_v2");
        if (!dir.exists()) {
           // long time = System.currentTimeMillis();
           // dir = new File(Environment.getExternalStorageDirectory()+"/com.autosos.jd/"+time+".jpg");
            dir.mkdirs();
        }
        return dir;
    }

    public static File getCropAlbumDir() {
        File dir = new File(Environment.getExternalStorageDirectory(),
                getCropName());
        if (!dir.exists()) {
            dir.mkdirs();
        }
        return dir;
    }

    public static String getAlbumName() {
        return "hunliji";
    }

    public static String getCropName() {
        return "crop";
    }

    public static File createImageFile() {

        SimpleDateFormat format = new SimpleDateFormat("yyyyMMdd_HHmmss");
        String timeStamp = format.format(new Date());
        String imageFileName = timeStamp + ".jpg";

        return new File(getAlbumDir(), imageFileName);
    }


    public static File createCropImageFile() {

        SimpleDateFormat format = new SimpleDateFormat("yyyyMMdd_HHmmss");
        String timeStamp = format.format(new Date());
        String imageFileName = timeStamp + ".jpg";

        return new File(getCropAlbumDir(), imageFileName);
    }

    public static File createVideoFile() {

        SimpleDateFormat format = new SimpleDateFormat("yyyyMMdd_HHmmss");
        String timeStamp = format.format(new Date());
        String videoFileName = timeStamp + ".mp4";

        return new File(getAlbumDir(), videoFileName);
    }


    public static File createSoundFile() {

        SimpleDateFormat format = new SimpleDateFormat("yyyyMMdd_HHmmss");
        String timeStamp = format.format(new Date());
        String musicFileName = timeStamp + ".mp3";

        return new File(getAlbumDir(), musicFileName);
    }
}
