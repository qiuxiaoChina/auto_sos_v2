package com.autosos.rescue.util;

import android.content.res.Resources;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;

import java.io.ByteArrayOutputStream;

public class BitmapCompressUtil {

    public static Bitmap decodeSampledBitmapFromFile(
            Resources res, String filePath,
            Bitmap.Config config, int reqWidth, int reqHeight){

        BitmapFactory.Options opts = new BitmapFactory.Options();
        opts.inJustDecodeBounds = true;
        BitmapFactory.decodeFile(filePath, opts);
        opts.inSampleSize = caculateInSampleSize(opts, reqWidth, reqHeight);
        opts.inJustDecodeBounds = false;
        opts.inPreferredConfig = config;
        return BitmapFactory.decodeFile(filePath, opts);
    }

    public static Bitmap decodeSampledBitmapFromResource(Resources res, int resId, Bitmap.Config config, int reqWidth, int reqHeight){

        BitmapFactory.Options opts = new BitmapFactory.Options();

        opts.inJustDecodeBounds = true;
        BitmapFactory.decodeResource(res, resId, opts);
        opts.inSampleSize = caculateInSampleSize(opts, reqWidth, reqHeight);
        opts.inJustDecodeBounds = false;
        opts.inPreferredConfig = config;
        return BitmapFactory.decodeResource(res, resId, opts);
    }

    public static int caculateInSampleSize(BitmapFactory.Options opts, int reqWidth, int reqHeight){

        int outWidth = opts.outWidth;
        int outHeight = opts.outHeight;

        int inSampleSize = 1;

        if (outWidth > reqWidth || outHeight > reqWidth){

            int widthScale = Math.round(outWidth / reqWidth);
            int heightScale = Math.round(outHeight / reqHeight);
            inSampleSize = widthScale < heightScale ? widthScale : heightScale;
        }

        return inSampleSize;
    }
    public static byte[] bitmap2Bytes(Bitmap bm, int quality) {
        ByteArrayOutputStream baos = new ByteArrayOutputStream();
        bm.compress(Bitmap.CompressFormat.JPEG, quality, baos);
        return baos.toByteArray();
    }

}
