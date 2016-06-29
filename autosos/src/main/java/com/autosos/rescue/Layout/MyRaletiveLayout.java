package com.autosos.rescue.Layout;

import android.content.Context;
import android.util.AttributeSet;
import android.view.MotionEvent;
import android.widget.RelativeLayout;

/**
 * Created by Administrator on 2016/5/31.
 */
public class MyRaletiveLayout extends RelativeLayout {

    public MyRaletiveLayout(Context context, AttributeSet attrs) {
        super(context, attrs);
    }

    //去子控件,如果没有子控件则不触发 触摸事件
    public static boolean GO_TOUTH_CHILD=true;
    //拦截方法
    @Override
    public boolean onInterceptTouchEvent(MotionEvent ev) {
        return super.onInterceptTouchEvent(ev);

    }

    //触摸方法 传给父控件
    @Override
    public boolean onTouchEvent(MotionEvent ev) {

        return true;
    }
}
