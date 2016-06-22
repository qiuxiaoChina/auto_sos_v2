package com.autosos.yd.util;

import android.app.Activity;
import android.content.Context;
import android.content.res.Resources;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.graphics.Canvas;
import android.graphics.Color;
import android.graphics.Matrix;
import android.graphics.Paint;
import android.graphics.Path;
import android.util.AttributeSet;
import android.view.MotionEvent;
import android.widget.ImageView;

import com.autosos.yd.R;
import com.autosos.yd.view.AppraiseActivity;

import java.io.ByteArrayInputStream;
import java.io.ByteArrayOutputStream;
import java.io.File;
import java.io.FileNotFoundException;
import java.io.FileOutputStream;
import java.io.IOException;

/**
 * Created by Administrator on 2016/3/4.
 */
public class SignPaintView extends ImageView{


    private Resources myResources;

    // 画笔，定义绘制属性
    private Paint myPaint;
    private Paint mBitmapPaint;

    // 绘制路径
    private Path myPath;

    // 画布及其底层位图
    private Bitmap myBitmap;
    private Canvas myCanvas;

    private float mX, mY;
    private static final float TOUCH_TOLERANCE = 4;

    // 记录宽度和高度
    private int mWidth;
    private int mHeight;

    public static boolean isSign = false;

    private AppraiseActivity activity;

    public AppraiseActivity getActivity() {
        return activity;
    }

    public void setActivity(AppraiseActivity activity) {
        this.activity = activity;
    }

    public SignPaintView(Context context)
    {
        super(context);
        initialize();
    }

    public SignPaintView(Context context, AttributeSet attrs, int defStyle)
    {
        super(context, attrs, defStyle);
        initialize();
    }

    public SignPaintView(Context context, AttributeSet attrs)
    {
        super(context, attrs);
        initialize();
    }

    /**
     * 初始化工作
     */
    private void initialize()
    {
        // Get a reference to our resource table.
        myResources = getResources();

        // 绘制自由曲线用的画笔
        myPaint = new Paint();
        myPaint.setAntiAlias(true);
        myPaint.setDither(true);
        myPaint.setColor(myResources.getColor(R.color.sign_color));
        myPaint.setStyle(Paint.Style.STROKE);
        myPaint.setStrokeJoin(Paint.Join.ROUND);
        myPaint.setStrokeCap(Paint.Cap.ROUND);
        myPaint.setStrokeWidth(10);

        myPath = new Path();

        mBitmapPaint = new Paint(Paint.DITHER_FLAG);

    }

    @Override
    protected void onSizeChanged(int w, int h, int oldw, int oldh)
    {
        super.onSizeChanged(w, h, oldw, oldh);
        mWidth = w;
        mHeight = h;
       //myBitmap = Bitmap.createBitmap(w, h, Bitmap.Config.ARGB_8888).copy();
        int[] colors = new int[w*h];
        int count=0;
        for(int i=0;i<h;i++){
            for(int j=0;j<w;j++){

                colors[count]=Color.WHITE;
                count++;
            }
        }
        myBitmap = Bitmap.createBitmap(colors,w, h, Bitmap.Config.ARGB_8888).copy(Bitmap.Config.ARGB_8888, true);
        myCanvas = new Canvas(myBitmap);
    }


//    @Override
//    public boolean onTouchEvent(MotionEvent event)
//    {
//
//        float x = event.getX();
//        float y = event.getY();
//
//        switch (event.getAction()) {
//            case MotionEvent.ACTION_DOWN:
//                mX = x;
//                mY = y;
//                myPath.moveTo(mX, mY);
//                break;
//            case MotionEvent.ACTION_MOVE:
//                mX = x;
//                mY = y;
//                myPath.quadTo(mX, mY, x, y); // 画线
//                break;
//            case MotionEvent.ACTION_UP:
//                myCanvas.drawPath(myPath, myPaint);
//                break;
//        }
//
//        invalidate();
//        return true;
//    }

    @Override
    public boolean onTouchEvent(MotionEvent event)
    {
        float x = event.getX();
        float y = event.getY();

        switch (event.getAction())
        {
            case MotionEvent.ACTION_DOWN:
                touch_start(activity,x, y);
                invalidate();
                break;
            case MotionEvent.ACTION_MOVE:
                touch_move(x, y);
                invalidate();
                break;
            case MotionEvent.ACTION_UP:
                touch_up();
                invalidate();
                break;
        }
        return true;
    }

    @Override
    protected void onDraw(Canvas canvas)
    {
        super.onDraw(canvas);

        // 背景颜色
      //  canvas.drawColor(Color.WHITE);

        // 如果不调用这个方法，绘制结束后画布将清空
        canvas.drawBitmap(myBitmap, 0, 0, null);

        // 绘制路径
        canvas.drawPath(myPath, myPaint);

    }

    private void touch_start(Activity activity,float x, float y)
    {
        activity.findViewById(R.id.sign_hint).setVisibility(GONE);

        myPath.reset();
        myPath.moveTo(x, y);
        mX = x;
        mY = y;
    }

    private void touch_move(float x, float y)
    {
        isSign = true;
        float dx = Math.abs(x - mX);
        float dy = Math.abs(y - mY);
        if (dx >= TOUCH_TOLERANCE || dy >= TOUCH_TOLERANCE)
        {
            myPath.quadTo(mX, mY, (x + mX) / 2, (y + mY) / 2);
            mX = x;
            mY = y;
        }
    }

    private void touch_up()
    {
        myPath.lineTo(mX, mY);
        // commit the path to our offscreen
        // 如果少了这一句，笔触抬起时myPath重置，那么绘制的线将消失
        myCanvas.drawPath(myPath, myPaint);
        // kill this so we don't double draw
        myPath.reset();
    }

    /**
     * 清除整个图像
     */
    public void clear()
    {
        // 清除方法1：重新生成位图
        // myBitmap = Bitmap
        // .createBitmap(mWidth, mHeight, Bitmap.Config.ARGB_8888);
        // myCanvas = new Canvas(myBitmap);

        // 清除方法2：将位图清除为白色
        myBitmap.eraseColor(Color.WHITE);

        // 两种清除方法都必须加上后面这两步：
        // 路径重置
        myPath.reset();
        // 刷新绘制
        invalidate();

    }

    public Path getPath() {
        return myPath;
    }

    public Bitmap getPaintBitmap() {
        return resizeImage(myBitmap, 800, 600);
    }


    // 缩放
    public static Bitmap resizeImage(Bitmap bitmap, int width, int height) {
        int originWidth = bitmap.getWidth();
        int originHeight = bitmap.getHeight();

        float scaleWidth = ((float) width) / originWidth;
        float scaleHeight = ((float) height) / originHeight;

        Matrix matrix = new Matrix();
        matrix.postScale(scaleWidth, scaleHeight);
        Bitmap resizedBitmap = Bitmap.createBitmap(bitmap, 0, 0, originWidth,
                originHeight, matrix, true);

        return resizedBitmap;
    }

    private  Bitmap compressImage(Bitmap image) {
        ByteArrayOutputStream baos = new ByteArrayOutputStream();
        image.compress(Bitmap.CompressFormat.JPEG, 90, baos);
       /* int options = 100;
        while ( baos.toByteArray().length / 1024 > 60) {
            baos.reset();
            options -= 10;
            image.compress(Bitmap.CompressFormat.JPEG, options, baos);
        }
        */
        ByteArrayInputStream isBm = new ByteArrayInputStream(baos.toByteArray());
        Bitmap bitmap = BitmapFactory.decodeStream(isBm, null, null);
        return bitmap;
    }


    public  void save(){

        saveBiamtImate(myBitmap);
    }

    /**
     * 保存图片
     * @param bitmap1
     */
    public void saveBiamtImate(Bitmap bitmap1){
        try {

            File jpgFile = com.autosos.yd.util.FileUtil.createImageFile();
            FileOutputStream fos = new FileOutputStream(jpgFile);
            bitmap1.compress(Bitmap.CompressFormat.JPEG, 100, fos);
            fos.flush();
            fos.close();
        } catch (FileNotFoundException e) {
            e.printStackTrace();
        } catch (IOException e) {
            e.printStackTrace();
        }
    }

}


