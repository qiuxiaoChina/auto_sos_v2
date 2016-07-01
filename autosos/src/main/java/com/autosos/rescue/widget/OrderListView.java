package com.autosos.rescue.widget;

import android.content.Context;
import android.util.AttributeSet;
import android.view.LayoutInflater;
import android.view.MotionEvent;
import android.view.View;
import android.view.ViewGroup;
import android.view.animation.RotateAnimation;
import android.widget.AbsListView;
import android.widget.ImageView;
import android.widget.ListView;
import android.widget.ProgressBar;
import android.widget.TextView;


import com.autosos.rescue.R;

import java.text.SimpleDateFormat;
import java.util.Date;
import java.util.logging.SimpleFormatter;

/**
 * Created by Administrator on 2016/6/2.
 */
public class OrderListView extends ListView implements AbsListView.OnScrollListener {
    View header;
    int headerHeight;//定义顶部布局文件的高度
    int firstVisibleItem;
    boolean isRemark;
    int startY;//按下时的y轴值
    int scrollState;
    int state = 0;
    final int NONE = 0;
    final int PULL = 1;
    final int RELEASE = 2;
    final int REFRESHING = 3;
    IsRefreshing isRefreshing;

    public OrderListView(Context context) {
        super(context);
        initView(context);//添加顶部布局文件到listview里面
    }

    public OrderListView(Context context, AttributeSet attrs) {
        super(context, attrs);
        initView(context);
    }

    public OrderListView(Context context, AttributeSet attrs, int defStyleAttr) {
        super(context, attrs, defStyleAttr);
        initView(context);
    }

    private void initView(Context context) {
        LayoutInflater layoutInflater = LayoutInflater.from(context);
        header = layoutInflater.inflate(R.layout.order_listview_header, null);
        measureView(header);
        headerHeight = header.getMeasuredHeight();
        topPadding(-headerHeight);
        this.addHeaderView(header);
        this.setOnScrollListener(this);
    }

    /*
     通知父布局 子布局的大小
     */
    private void measureView(View view) {
        ViewGroup.LayoutParams layoutParams = view.getLayoutParams();
        if (layoutParams == null) {

            layoutParams = new ViewGroup.LayoutParams(ViewGroup.LayoutParams.MATCH_PARENT, ViewGroup.LayoutParams.WRAP_CONTENT);

        }

        int width = ViewGroup.getChildMeasureSpec(0, 0, layoutParams.width);
        int height;
        int tempHeight = layoutParams.height;
        if (tempHeight > 0) {

            height = MeasureSpec.makeMeasureSpec(tempHeight, MeasureSpec.EXACTLY);
        } else {

            height = MeasureSpec.makeMeasureSpec(0, MeasureSpec.UNSPECIFIED);
        }
        view.measure(width, height);
    }

    private void topPadding(int topPadding) {

        header.setPadding(header.getPaddingLeft(), topPadding, header.getPaddingRight(), header.getPaddingBottom());
        header.invalidate();
    }

    @Override
    public void onScrollStateChanged(AbsListView view, int scrollState) {
        this.scrollState = scrollState;
    }

    @Override
    public void onScroll(AbsListView view, int firstVisibleItem, int visibleItemCount, int totalItemCount) {

        this.firstVisibleItem = firstVisibleItem;
    }

    @Override
    public boolean onTouchEvent(MotionEvent ev) {
        switch (ev.getAction()) {

            case MotionEvent.ACTION_DOWN:
                if (firstVisibleItem == 0) {
                    isRemark = true;
                    startY = (int) ev.getY();
                }
                break;
            case MotionEvent.ACTION_MOVE:
                onMove(ev);
                break;

            case MotionEvent.ACTION_UP:
                if (state == RELEASE) {

                    state = REFRESHING;
                    refreshViewByState();
                    isRefreshing.onRefresh();

                } else if (state == PULL) {
                    state = NONE;
                    isRemark = false;
                    refreshViewByState();
                }
                break;
        }
        return super.onTouchEvent(ev);
    }

    public  void refreshComplete(){

        state = NONE;
        isRemark = false;
        refreshViewByState();
        TextView time  = (TextView) header.findViewById(R.id.lastUpdate_time);
        SimpleDateFormat sdf = new SimpleDateFormat("yyyy-MM-dd kk:mm:ss");
        Date date = new Date(System.currentTimeMillis());
        String s_time = sdf.format(date);
        time.setText("最后一次刷新时间:"+s_time);
    }

    public  void setInterface(IsRefreshing isRefreshing){
         this.isRefreshing = isRefreshing;
    }

    public interface IsRefreshing{

        public void onRefresh();
    }

    private void refreshViewByState(){
        TextView tip = (TextView) header.findViewById(R.id.tip);
        ImageView arrow = (ImageView) header.findViewById(R.id.arrow_down);
        ProgressBar progressBar = (ProgressBar) header.findViewById(R.id.header_progress);
        RotateAnimation rotateAnimation1 = new RotateAnimation(0,180,RotateAnimation.RELATIVE_TO_SELF,0.5f,
                                                                     RotateAnimation.RELATIVE_TO_SELF,0.5f);
        rotateAnimation1.setDuration(500);
        RotateAnimation rotateAnimation2 = new RotateAnimation(180,0,RotateAnimation.RELATIVE_TO_SELF,0.5f,
                                                                 RotateAnimation.RELATIVE_TO_SELF,0.5f);
        rotateAnimation2.setDuration(500);
        switch (state){
            case NONE:
                topPadding(-headerHeight);
                break;
            case PULL:
                progressBar.setVisibility(GONE);
                arrow.setVisibility(VISIBLE);
                arrow.clearAnimation();
                arrow.startAnimation(rotateAnimation2);
                tip.setText("下拉可以刷新");
                break;

            case RELEASE:
                progressBar.setVisibility(GONE);
                arrow.setVisibility(VISIBLE);
                arrow.clearAnimation();
                arrow.startAnimation(rotateAnimation1);
                tip.setText("松开可以刷新");
                break;

            case REFRESHING:
                topPadding(50);
                progressBar.setVisibility(VISIBLE);
                arrow.setVisibility(GONE);
                arrow.clearAnimation();
                tip.setText("数据加载中");
                break;
        }
    }

    private void onMove(MotionEvent event) {
        if (!isRemark) {

            return;
        }
        int tempY = (int) event.getY();
        int space = tempY - startY;
        int topPadding = space - headerHeight;
        switch (state) {

            case NONE:
                if (space > 0) {

                    state = PULL;
                    refreshViewByState();
                }
                break;
            case PULL:
                topPadding(topPadding);
                if (space > headerHeight + 30 && scrollState == SCROLL_STATE_TOUCH_SCROLL) {

                    state = RELEASE;
                    refreshViewByState();

                }
                break;
            case RELEASE:
                topPadding(topPadding);
                if (space < headerHeight + 30) {

                    state = PULL;
                    refreshViewByState();
                } else if (space <= 0) {

                    state = NONE;
                    isRemark = false;
                    refreshViewByState();

                }
                break;
            case REFRESHING:
                break;


        }

    }
}
