package com.autosos.rescue.widget;

import android.os.CountDownTimer;
import android.widget.TextView;

import com.autosos.rescue.R;


/**
 * Created by Administrator on 2015/11/19.
 */

/**
 * 参数 millisInFuture         倒计时总时间（如60S，120s等）
 * 参数 countDownInterval    渐变时间（每次倒计1s）

 * 参数 btn               点击的按钮(因为Button是TextView子类，为了通用我的参数设置为TextView）

 * 参数 endStrRid   倒计时结束后，按钮对应显示的文字
 */
public class MyCountTimer extends CountDownTimer {
    public static final int TIME_COUNT = 60000;//时间防止从119s开始显示（以倒计时120s为例子）
    private TextView btn;
    private int endStrRid;
    private int normalColor, timingColor;//未计时的文字颜色，计时期间的文字颜色

    public MyCountTimer(long millisInFuture, long countDownInterval, TextView btn, int endStrRid) {
        super(millisInFuture, countDownInterval);
        this.btn = btn;
        this.endStrRid = endStrRid;
    }
    public MyCountTimer(TextView btn, int endStrRid) {
        super(TIME_COUNT, 1000);
        this.btn = btn;
        this.endStrRid = endStrRid;
    }

    public MyCountTimer(TextView btn) {
        super(TIME_COUNT, 1000);
        this.btn = btn;
        this.endStrRid = R.string.txt_getMsgCode_validate;
    }


    public MyCountTimer(TextView tv_varify, int normalColor, int timingColor) {
        this(tv_varify);
        this.normalColor = normalColor;
        this.timingColor = timingColor;
    }

    @Override
    public void onTick(long millisUntilFinished) {
//        if(timingColor > 0){
//            btn.setTextColor(android.graphics.Color.RED);
//        }
        btn.setTextColor(timingColor);
        btn.setEnabled(false);
        btn.setText("已发送 " + millisUntilFinished / 1000);
    }

    @Override
    public void onFinish() {

        if(normalColor > 0){
            btn.setTextColor(normalColor);
        }
        btn.setText(endStrRid);
        btn.setTextColor(normalColor);
        btn.setEnabled(true);
    }
}
