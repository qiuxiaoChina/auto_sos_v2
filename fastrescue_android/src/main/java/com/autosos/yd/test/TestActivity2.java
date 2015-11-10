package com.autosos.yd.test;

import android.app.Activity;
import android.app.Dialog;
import android.graphics.Point;
import android.os.Bundle;
import android.os.Environment;
import android.os.Handler;
import android.os.Message;
import android.view.View;
import android.view.Window;
import android.view.WindowManager;
import android.widget.Button;
import android.widget.TextView;

import com.autosos.yd.R;
import com.autosos.yd.util.JSONUtil;

import java.io.BufferedWriter;
import java.io.File;
import java.io.FileInputStream;
import java.io.FileNotFoundException;
import java.io.FileOutputStream;
import java.io.IOException;
import java.io.OutputStreamWriter;

/**
 * Created by Administrator on 2015/11/9.
 */
public class TestActivity2 extends Activity{
    TextView tv;
    private Dialog dialog;
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.test2_activity);
        tv = (TextView) findViewById(R.id.tv);

    }

    public void write(View view){
        try {
            FileOutputStream mOutput = openFileOutput("data.txt",
                    Activity.MODE_PRIVATE);
            String data = "THIS DATA WRITTEN TO A FILE";
            mOutput.write(data.getBytes());
            mOutput.close();
        } catch(FileNotFoundException e) {
            e.printStackTrace();
        } catch(IOException e) {
            e.printStackTrace();
        }
    }

    public void read(){
        try {
                FileInputStream mInput = openFileInput("data.txt");
            byte[] data = new byte[128];
            mInput.read(data);
            mInput.close();
            String display = new String(data);
            tv.setText(display.trim());
        } catch(FileNotFoundException e) {
            e.printStackTrace();
        } catch(IOException e) {
            e.printStackTrace();
        }
    }

    public void delete(View view){
        deleteFile("data.txt");
    }


    Handler handler = new Handler(){

        @Override
        public void handleMessage(Message msg) {
            super.handleMessage(msg);
            switch (msg.what){
                case 1:
//                            这边写上你要做的事
                    break;
                case 2:
//                            这边写上你要做的事
                    break;
                case 3:
//                            这边写上你要做的事
            }

        }
    };
    public void creat(View view) {
        View inflate = getLayoutInflater().inflate(R.layout.dialog_msg_notice,null);
        dialog = new Dialog(inflate.getContext(), R.style.bubble_dialog);
        Button tvConfirm ;
        Button tvCancel ;
        TextView tvMsg ;
        tvConfirm = (Button) inflate.findViewById(R.id.btn_notice_confirm);
        tvCancel = (Button) inflate.findViewById(R.id.btn_notice_cancel);
        tvMsg = (TextView) inflate.findViewById(R.id.tv_notice_msg);
        tvMsg.setText("这是一个自定义的对话框");
        tvCancel.setOnClickListener(new View.OnClickListener() {

            @Override
            public void onClick(View v) {
                dialog.dismiss();
            }
        });
        tvConfirm.setOnClickListener(new View.OnClickListener() {

            @Override
            public void onClick(View v) {
                dialog.dismiss();
            }
        });
        dialog.setContentView(inflate);
        dialog.setCanceledOnTouchOutside(false);
        Window window = dialog.getWindow();
        WindowManager.LayoutParams params = window.getAttributes();
        Point point = JSONUtil.getDeviceSize(TestActivity2.this);
        params.width = Math.round(point.x * 5 / 7);
        window.setAttributes(params);
        dialog.show();
//        Message msg2 = new Message();
//        msg2.what = 4;
//        handler.sendMessage(msg2);
//        String path = Environment.getExternalStorageDirectory() + "/test/";
//        String name = "test2.txt";
//        if (Environment.getExternalStorageState().equals(Environment.MEDIA_MOUNTED)){
//            File dir = new File(path+name);
//            if (!dir.exists()){
//                try {
//                    dir.createNewFile();
//                } catch (IOException e) {
//                    e.printStackTrace();
//                }
//            }
//            BufferedWriter bufferedWriter = null;
//            try {
//                bufferedWriter = new BufferedWriter(new OutputStreamWriter(new FileOutputStream(path+name,true)));
//            } catch (FileNotFoundException e) {
//                e.printStackTrace();
//            }
//            try {
//                bufferedWriter.write("22222");
//                bufferedWriter.flush();
//                bufferedWriter.close();
//            } catch (IOException e) {
//                e.printStackTrace();
//            }
        }




//        File file = new File("sdcard/info.txt");
//        try {
//            String path =  Environment.getExternalStorageDirectory()+"/11111111111/";
//            String name = "info.txt";
//            File file = new File(path + name);
//            File file = new File(Environment.getExternalStorageDirectory(), "info.txt");
//            FileOutputStream fos = new FileOutputStream(file);
//            //写入用户名和密码，以name##passwd的格式写入
//            fos.write(("").getBytes());
//            //关闭输出流
//            fos.close();
//        } catch (Exception e) {
//            // TODO Auto-generated catch block
//            e.printStackTrace();
//        }


//        String fileName = "crash-" + "Exception" + ".log";
//        String path =  Environment.getExternalStorageDirectory()+"/com.autosos.jd/log/";
//        if (Environment.getExternalStorageState().equals(Environment.MEDIA_MOUNTED)) {          // 判断SD的状态
//            File dir = new File(path+fileName);
//            if (!dir.exists()) {
//                dir.mkdirs();
//            }
//            BufferedWriter fileOutputStream  = null;
//            try {
//                fileOutputStream = new BufferedWriter(new OutputStreamWriter(
//                        new FileOutputStream(path + fileName, true)));
//            } catch (FileNotFoundException e) {
//                e.printStackTrace();
//            }
//            try {
//                fileOutputStream.write("111111111111");
//            } catch (IOException e) {
//                e.printStackTrace();
//            }
//            try {
//                fileOutputStream.close();
//            } catch (IOException e) {
//                e.printStackTrace();
//            }
//        }
//    }
}
