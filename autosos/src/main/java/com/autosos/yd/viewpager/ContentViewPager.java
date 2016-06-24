package com.autosos.yd.viewpager;

import android.content.Context;
import android.support.v4.view.ViewPager;
import android.util.AttributeSet;
import android.view.MotionEvent;

/**
 * Created by Administrator on 2015/11/9.
 */
public class ContentViewPager extends ViewPager {

    public ContentViewPager(Context context, AttributeSet attrs) {
        super(context, attrs);
    }

    private boolean scrollble = true;

    public ContentViewPager(Context context) {
        super(context);
    }

    @Override
    public boolean onTouchEvent(MotionEvent ev) {
        if (!scrollble) {
            return false;
        }
        return super.onTouchEvent(ev);
    }


    public void setScrollble(boolean scrollble) {
        this.scrollble = scrollble;
    }

    @Override
    public boolean onInterceptTouchEvent(MotionEvent arg0) {
        // TODO Auto-generated method stub
        if (scrollble) {
            return super.onInterceptTouchEvent(arg0);
        } else {
            return false;
        }

    }




}