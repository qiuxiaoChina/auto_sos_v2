package com.autosos.rescue.view;

import android.annotation.SuppressLint;
import android.app.Activity;
import android.content.Context;
import android.content.Intent;
import android.hardware.Camera;
import android.hardware.SensorManager;
import android.net.Uri;
import android.os.Bundle;
import android.os.Handler;
import android.os.Message;
import android.provider.MediaStore;
import android.util.Log;
import android.view.Display;
import android.view.KeyEvent;
import android.view.OrientationEventListener;
import android.view.SurfaceHolder;
import android.view.SurfaceView;
import android.view.View;
import android.view.View.OnClickListener;
import android.view.WindowManager;
import android.widget.Button;
import android.widget.ImageView;
import android.widget.TextView;
import android.widget.Toast;

import com.autosos.rescue.R;
import com.autosos.rescue.util.FileUtil;


import java.io.File;
import java.io.FileNotFoundException;
import java.io.FileOutputStream;
import java.io.IOException;
import java.util.Arrays;
import java.util.HashMap;
import java.util.Iterator;
import java.util.List;
import java.util.Timer;
import java.util.TimerTask;


public class NewTakePhotoActivity extends Activity implements OnClickListener, SurfaceHolder.Callback {
    public static NewTakePhotoActivity instance = null;
    private Button takeButton, button_flash, button_cancel, takeButton2;
    private SurfaceView mPreview = null;
    private ImageView iv_cla;
    private ImageView preview_box;
    private TextView clockNum;
    String tuoche = null;
    String tuoche2 = null;
    private TextView photoTitle, photoTip;
    float previewRate = -1f;
    private Camera mCamera = null;
    private SurfaceHolder mHolder = null;
    private AlbumOrientationEventListener mAlbumOrientationEventListener = null;
    private boolean canShoot = false;
    private Camera.PictureCallback mPictureCallback = new Camera.PictureCallback() {
        @Override
        public void onPictureTaken(byte[] data, Camera camera) {

            try {
                String path = saveToSDCard(data); // 保存图片到SD卡中
                Toast.makeText(NewTakePhotoActivity.this, "成功拍照", Toast.LENGTH_SHORT).show();
//                //图片更新，很重要的一点，部分手机如果不发送这条广播图片不是实时更新
//                // camera.startPreview(); // 拍完照后，重新开始预览
                Intent intent = new Intent(NewTakePhotoActivity.this, NewUploadPhotoActivity.class);
                intent.putExtra("imagePath", path);
                startActivity(intent);
            } catch (Exception e) {
                e.printStackTrace();
            }
        }
    };

    //将拍下来的照片存放在SD卡中
    @SuppressLint("SimpleDateFormat")
    public static String saveToSDCard(byte[] data) {

        File jpgFile = FileUtil.createImageFile();
        FileOutputStream outputStream = null; // 文件输出流
        try {
            outputStream = new FileOutputStream(jpgFile);
            outputStream.write(data); // 写入SD卡中
            outputStream.close(); // 关闭输出流
        } catch (FileNotFoundException e) {
            e.printStackTrace();
        } catch (IOException e) {
            e.printStackTrace();
        }


        return jpgFile.getAbsolutePath();
    }


    Timer time_clock = new Timer();


    @Override
    protected void onCreate(Bundle savedInstanceState) {
        // TODO Auto-generated method stub
        super.onCreate(savedInstanceState);

        setContentView(R.layout.activity_newtakephoto);
        instance = this;
        iv_cla = (ImageView) findViewById(R.id.cla);

        mPreview = (SurfaceView) findViewById(R.id.surfaceView);


        preview_box = (ImageView) findViewById(R.id.preview_box);
        clockNum = (TextView) findViewById(R.id.clockNum);
        button_flash = (Button) findViewById(R.id.button_flash);
        button_cancel = (Button) findViewById(R.id.button_cancel);


        takeButton = (Button) findViewById(R.id.button1);
        takeButton2 = (Button) findViewById(R.id.button2);
        takeButton.setOnClickListener(this);
        takeButton2.setOnClickListener(this);
        preview_box.setOnClickListener(this);
        button_flash.setOnClickListener(this);
        button_cancel.setOnClickListener(this);

        TimerTask task_clock = new TimerTask() {
            public void run() {
                Message msg = new Message();
                msg.what = 1;
                mHandler.sendMessage(msg);
            }
        };

        time_clock.schedule(task_clock, 0, 1000);

        photoTitle = (TextView) findViewById(R.id.photo_title);
        photoTip = (TextView) findViewById(R.id.photo_tip);//如果是拖车服务的照片 这两个元素的内容要替换掉
        tuoche = getIntent().getStringExtra("tuoche");
        tuoche2 = getIntent().getStringExtra("tuoche2");//如果这个值不为空 那么是拍摄拖车照片2的
        if (tuoche != null) {

            photoTip.setText("请将拖车置于虚线框内,并将故障车置于拖车上方");
            photoTitle.setText("拖车照片1");
            iv_cla.setImageResource(R.drawable.tuoche1_new_way);
            preview_box.setImageResource(R.drawable.assistant2);
//            button_cancel.setClickable(false);
//            button_cancel.setVisibility(View.GONE);

        } else if (tuoche2 != null) {

            photoTip.setText("请将拖车置于虚线框内,并且背景清晰展现修理门店");
            photoTitle.setText("拖车照片2");
            iv_cla.setImageResource(R.drawable.tuoche2_new_way);
            preview_box.setImageResource(R.drawable.assistant3);
//            button_cancel.setClickable(false);
//            button_cancel.setVisibility(View.GONE);

        }

        mAlbumOrientationEventListener = new AlbumOrientationEventListener(this, SensorManager.SENSOR_DELAY_NORMAL);
        if (mAlbumOrientationEventListener.canDetectOrientation()) {
            mAlbumOrientationEventListener.enable();
        } else {

        }
    }

    private class AlbumOrientationEventListener extends OrientationEventListener {
        public AlbumOrientationEventListener(Context context) {
            super(context);
        }

        public AlbumOrientationEventListener(Context context, int rate) {
            super(context, rate);
        }

        @Override
        public void onOrientationChanged(int orientation) {
            if (orientation == OrientationEventListener.ORIENTATION_UNKNOWN) {
                return;
            }

            //保证只返回四个方向
            int newOrientation = ((orientation + 45) / 90 * 90) % 360;
            Log.d("orientation", newOrientation + "---" + orientation);
            //返回的mOrientation就是手机方向，为0°、90°、180°和270°中的一个
            if (newOrientation == 270) {

                canShoot = true;

            } else {

                canShoot = false;
            }
        }
    }


    int i_timer = 5;
    private Handler mHandler = new Handler() {

        @Override
        public void handleMessage(Message msg) {
            super.handleMessage(msg);
            switch (msg.what) {
                case 1:
                    i_timer--;
                    if (i_timer >= 0) {
                        clockNum.setText(i_timer + "");
                    } else {
                        iv_cla.setVisibility(View.GONE);
                        mPreview.setVisibility(View.VISIBLE);
                        takeButton.setVisibility(View.VISIBLE);
                        takeButton.setClickable(true);
                        takeButton2.setClickable(false);
                        takeButton2.setVisibility(View.GONE);
                        clockNum.setVisibility(View.GONE);
                        button_flash.setClickable(true);
                        button_flash.setVisibility(View.VISIBLE);

                        button_cancel.setClickable(true);
                        button_cancel.setVisibility(View.VISIBLE);

                        time_clock.cancel();
                    }
                default:
                    break;
            }
        }
    };

    private static int REQ_CODE = 1;
    private static String imagePath = "";


    @Override
    public void onClick(View v) {
        // TODO Auto-generated method stub
        switch (v.getId()) {
            case R.id.button2:
                iv_cla.setVisibility(View.GONE);
                mPreview.setVisibility(View.VISIBLE);
                takeButton.setVisibility(View.VISIBLE);
                takeButton.setClickable(true);
                takeButton2.setClickable(false);
                takeButton2.setVisibility(View.GONE);
                clockNum.setVisibility(View.GONE);
                button_flash.setClickable(true);
                button_flash.setVisibility(View.VISIBLE);

                button_cancel.setClickable(true);
                button_cancel.setVisibility(View.VISIBLE);

                time_clock.cancel();
                break;

            case R.id.button1:
                if (!canShoot) {

                    Toast.makeText(NewTakePhotoActivity.this, "请按要求横屏拍摄照片。", Toast.LENGTH_SHORT).show();

                } else {

                    try {
                        Camera.Parameters parameters = mCamera.getParameters();
                        //parameters.setPictureFormat(ImageFormat.JPEG);
                        parameters.setFocusMode(Camera.Parameters.FOCUS_MODE_AUTO);
                        // Camera.Size pictureSize = getOptimalPictureSize(parameters.getSupportedPictureSizes(), 800, 480);
                        if ("打开".equals(button_flash.getText())) {

                            parameters.setFlashMode(Camera.Parameters.FLASH_MODE_ON);
                        } else {

                            parameters.setFlashMode(Camera.Parameters.FLASH_MODE_OFF);
                        }
                        WindowManager windowManager = getWindowManager();
                        Display display = windowManager.getDefaultDisplay();
                        int screenWidth = display.getWidth();
                        int screenHeight = display.getHeight();
                        int PreviewWidth = 0;
                        int PreviewHeight = 0;
                        List<Camera.Size> sizeList = parameters.getSupportedPictureSizes();
                        HashMap<Integer, Camera.Size> map = new HashMap<Integer, Camera.Size>();
                        // 如果sizeList只有一个我们也没有必要做什么了，因为就他一个别无选择
                        if (sizeList.size() > 1) {
                            Iterator<Camera.Size> itor = sizeList.iterator();
                            while (itor.hasNext()) {
                                Camera.Size cur = itor.next();
                                if (cur.height < screenHeight) {
                                    map.put(cur.height, cur);
                                    Log.i("sizeInList", cur.width + ":" + cur.height);
                                }
                            }
                        }
                        Object[] key = map.keySet().toArray();
                        Arrays.sort(key);
                        if (key.length >= 1) {

                            PreviewHeight = map.get(key[key.length - 1]).height;
                            PreviewWidth = map.get(key[key.length - 1]).width;
                            parameters.setPictureSize(PreviewWidth, PreviewHeight);

                        }

                        Log.i("size", "Screen:" + screenWidth + ":" + screenHeight);
                        Log.i("size", "Screen:" + PreviewWidth + ":" + PreviewHeight);
                        for (Camera.Size size : parameters.getSupportedPictureSizes()) {

                            Log.i("size", size.width + ":" + size.height);
                        }

                        mCamera.setParameters(parameters);

                        mCamera.autoFocus(new Camera.AutoFocusCallback() {
                            @Override
                            public void onAutoFocus(boolean success, Camera camera) {
                                if (success) {

                                    mCamera.takePicture(null, null, mPictureCallback);
                                }
                            }
                        });
                    } catch (Exception e) {
                        e.printStackTrace();
                        // Toast.makeText(NewTakePhotoActivity.this, "由于手机分辨率适配问题报错，已经帮你自动调用系统自带相机，请重新拍摄。", Toast.LENGTH_SHORT).show();
//                        Intent intent = new Intent(MediaStore.ACTION_IMAGE_CAPTURE);
//                        File file = com.autosos.yd.util.FileUtil.createImageFile();
//                        imagePath = file.getAbsolutePath();
//                        Uri photoUri = Uri.fromFile(file);
//                        intent.putExtra(MediaStore.EXTRA_OUTPUT, photoUri);
//                        startActivityForResult(intent, REQ_CODE);

                    } finally {

                        takeButton.setClickable(false);
                        takeButton.setVisibility(View.GONE);
                    }


                }

                break;

            case R.id.preview_box:
                iv_cla.setVisibility(View.GONE);
                mPreview.setVisibility(View.VISIBLE);
                takeButton.setClickable(true);
                clockNum.setVisibility(View.GONE);
                button_flash.setClickable(true);
                button_flash.setVisibility(View.VISIBLE);
                if (tuoche == null) {
                    button_cancel.setClickable(true);
                    button_cancel.setVisibility(View.VISIBLE);
                }
                time_clock.cancel();
                break;

            case R.id.button_flash:
                if (("打开").equals(button_flash.getText())) {
                    button_flash.setText("关闭");
                } else {
                    button_flash.setText("打开");
                }
                break;

            case R.id.button_cancel:
                releaseCamera();
                finish();
                break;
            default:
                break;
        }

    }


    //按返回键 没有办法返回
    @Override
    public boolean onKeyDown(int keyCode, KeyEvent event) {

        if (keyCode == KeyEvent.KEYCODE_BACK) {
            return true;
        }
        return false;
    }

    private Camera getCamera() {
        Camera camera;

        try {
            camera = Camera.open();
        } catch (Exception e) {

            camera = null;
            e.printStackTrace();
        }
        return camera;

    }

    private void releaseCamera() {

        if (mCamera != null) {
            mCamera.setPreviewCallback(null);
            mCamera.stopPreview();
            mCamera.release();
            mCamera = null;
        }

    }

    private void setStartPreview(Camera camera, SurfaceHolder holder) {

        try {
            camera.setPreviewDisplay(holder);
            camera.startPreview();
        } catch (IOException e) {
            e.printStackTrace();
        }

    }

    @Override
    protected void onDestroy() {
        mAlbumOrientationEventListener.disable();
        super.onDestroy();
    }

    @Override
    protected void onPause() {
        super.onPause();
        releaseCamera();
    }

    @Override
    protected void onResume() {
        super.onResume();
        if (takeButton.getVisibility() == View.GONE) {
            takeButton.setClickable(true);
            takeButton.setVisibility(View.VISIBLE);
        }
        if (mHolder == null) {

            mHolder = mPreview.getHolder();
            mHolder.setType(SurfaceHolder.SURFACE_TYPE_PUSH_BUFFERS);
            mHolder.addCallback(this);
        }
        if (mCamera == null) {
            mCamera = getCamera();
            if (mHolder != null) {
                setStartPreview(mCamera, mHolder);
            }
        }
    }

    @Override
    public void surfaceCreated(SurfaceHolder holder) {
        setStartPreview(mCamera, mHolder);
    }

    @Override
    public void surfaceChanged(SurfaceHolder holder, int format, int width, int height) {
        mCamera.stopPreview();
        setStartPreview(mCamera, mHolder);

    }

    @Override
    public void surfaceDestroyed(SurfaceHolder holder) {
        releaseCamera();
    }


    @Override
    protected void onActivityResult(int requestCode, int resultCode, Intent data) {
        super.onActivityResult(requestCode, resultCode, data);
        if (resultCode == RESULT_OK) {

            if (requestCode == REQ_CODE) {
                // Bundle bundle = data.getExtras();
                // Bitmap bm = (Bitmap) bundle.get("data");
                try {
                    // ByteArrayOutputStream baos = new ByteArrayOutputStream();
                    // bm.compress(Bitmap.CompressFormat.JPEG, 100, baos);
                    // String path = saveToSDCard(baos.toByteArray()); // 保存图片到SD卡中
                    Toast.makeText(NewTakePhotoActivity.this, "成功拍照", Toast.LENGTH_SHORT).show();
                    //图片更新，很重要的一点，部分手机如果不发送这条广播图片不是实时更新
                    // camera.startPreview(); // 拍完照后，重新开始预览
                    Intent intent = new Intent();
                    intent.setAction("NewUploadPhotoActivity");
                    intent.putExtra("imagePath", imagePath);
                    if (tuoche != null) {
                        intent.putExtra("tuocheTaken", "tuocheTaken");

                    } else if (tuoche2 != null) {
                        intent.putExtra("tuocheTaken2", "tuocheTaken2");
                    }
                    startActivity(intent);
                    finish();
                } catch (Exception e) {
                    e.printStackTrace();

                }

            }
        }
    }
}
