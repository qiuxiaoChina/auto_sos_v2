package com.autosos.yd.view;

import android.content.DialogInterface;
import android.content.Intent;
import android.net.Uri;
import android.os.Bundle;
import android.app.Activity;
import android.support.v4.app.Fragment;
import android.support.v4.app.FragmentActivity;
import android.support.v4.app.FragmentManager;
import android.support.v4.app.FragmentPagerAdapter;
import android.support.v4.app.FragmentTabHost;
import android.support.v4.view.PagerAdapter;
import android.support.v4.view.ViewPager;
import android.view.KeyEvent;
import android.view.LayoutInflater;
import android.view.MotionEvent;
import android.view.View;
import android.view.ViewGroup;
import android.view.WindowManager;
import android.widget.ImageView;
import android.widget.RadioGroup;
import android.widget.TabHost;
import android.widget.TextView;
import android.widget.Toast;

import com.amap.api.navi.AMapNaviListener;
import com.amap.api.navi.model.AMapLaneInfo;
import com.amap.api.navi.model.AMapNaviCross;
import com.amap.api.navi.model.AMapNaviInfo;
import com.amap.api.navi.model.AMapNaviLocation;
import com.amap.api.navi.model.AMapNaviTrafficFacilityInfo;
import com.amap.api.navi.model.AimLessModeCongestionInfo;
import com.amap.api.navi.model.AimLessModeStat;
import com.amap.api.navi.model.NaviInfo;
import com.autonavi.tbt.TrafficFacilityInfo;
import com.autosos.yd.R;
import com.autosos.yd.fragment.FragmentForMine;
import com.autosos.yd.fragment.FragmentForOrder;
import com.autosos.yd.fragment.FragmentForSetting;
import com.autosos.yd.fragment.FragmentForWork;
import com.autosos.yd.viewpager.ContentViewPager;
import com.google.android.gms.appindexing.Action;
import com.google.android.gms.appindexing.AppIndex;
import com.google.android.gms.common.api.GoogleApiClient;
import com.iflytek.cloud.SpeechConstant;
import com.iflytek.cloud.SpeechUtility;
import com.igexin.sdk.PushManager;

import java.util.ArrayList;
import java.util.List;

public class MainActivity extends FragmentActivity implements TabHost.OnTabChangeListener{

//    private ContentViewPager contentViewPager;
//    private RadioGroup contentradiogroup;
//    private View menu_layout;
//    private View menu;


    private FragmentTabHost mTabHost;
    private LayoutInflater layoutInflater;
    private Class[] fragmentArray = {FragmentForWork.class,FragmentForOrder.class,FragmentForMine.class,FragmentForSetting.class};

    private int mImageViewArray[] = {R.drawable.icon_main_menu1,R.drawable.icon_main_menu2,R.drawable.icon_main_menu3,
            R.drawable.icon_main_menu4};

    private String mTextviewArray[] = {"工作","订单","我的","设置"};



    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        PushManager.getInstance().initialize(this.getApplicationContext());
       // SpeechUtility.createUtility(getApplicationContext(), SpeechConstant.APPID +"=5754c9e7"+ SpeechConstant.FORCE_LOGIN +"=true");
        SpeechUtility.createUtility(getApplicationContext(), SpeechConstant.APPID +"=5754c9e7,"+SpeechConstant.FORCE_LOGIN +"=true");
        setContentView(R.layout.activity_main3);
        getWindow().addFlags(WindowManager.LayoutParams.FLAG_SHOW_WHEN_LOCKED
                | WindowManager.LayoutParams.FLAG_DISMISS_KEYGUARD
                | WindowManager.LayoutParams.FLAG_TURN_SCREEN_ON
                | WindowManager.LayoutParams.FLAG_KEEP_SCREEN_ON);

        initView();

    }


    private void initView(){


        layoutInflater = LayoutInflater.from(this);

        mTabHost = (FragmentTabHost)findViewById(android.R.id.tabhost);
        mTabHost.setup(this, getSupportFragmentManager(), R.id.realtabcontent);

        int count = fragmentArray.length;

        for(int i = 0; i < count; i++){

            TabHost.TabSpec tabSpec = mTabHost.newTabSpec(mTextviewArray[i]).setIndicator(getTabItemView(i));
            mTabHost.addTab(tabSpec, fragmentArray[i], null);
           // mTabHost.getTabWidget().getChildAt(i).setBackgroundResource(R.drawable.selector_tab_background);
        }


    }

    private View getTabItemView(int index){
        View view = layoutInflater.inflate(R.layout.tab_item_view, null);

        ImageView imageView = (ImageView) view.findViewById(R.id.icon);
        imageView.setImageResource(mImageViewArray[index]);

        TextView textView = (TextView) view.findViewById(R.id.tip);
        textView.setText(mTextviewArray[index]);

        return view;
    }



//    private ArrayList<Fragment> content_list = null;
//
//    //加载fragments
//    private void initData() {
//        content_list = new ArrayList<>();
//
//        content_list.add(FragmentForWork.newInstance());
//        content_list.add(FragmentForOrder.newInstance());
//        content_list.add(FragmentForMine.newInstance());
//        content_list.add(FragmentForSetting.newInstance());
//
//    }
//
//    private void initView() {
//
//
//        if (content_list == null) {
//            return;
//        }
//        contentViewPager = (ContentViewPager) findViewById(R.id.content_viewpager);
//       // contentViewPager.setScanScroll(false);//设置为不能滚动翻页
//
//        contentradiogroup = (RadioGroup) findViewById(R.id.content_radiogroup);
//        //给ViewPager设置适配器
//        contentViewPager.setAdapter(new MyFragmentPagerAdapter(getSupportFragmentManager(), content_list));
//        contentViewPager.setCurrentItem(0);//设置当前显示标签页为第一页
//        contentViewPager.setScrollble(false);
//        contentradiogroup.setOnCheckedChangeListener(new RadioGroup.OnCheckedChangeListener() {
//            @Override
//            public void onCheckedChanged(RadioGroup radioGroup, int i) {
//                switch (i) {
//                    case R.id.rb_work:
//                        contentViewPager.setCurrentItem(0);
//                        break;
//                    case R.id.rb_order:
//                        contentViewPager.setCurrentItem(1);
//                        break;
//                    case R.id.rb_mine:
//                        contentViewPager.setCurrentItem(2);
//                        break;
//                    case R.id.rb_setting:
//                        contentViewPager.setCurrentItem(3);
//                        break;
//                }
//            }
//        });
//        contentradiogroup.check(R.id.rb_work);
//
//    }
//
//    @Override
//    public boolean onTouch(View v, MotionEvent event) {
//        return false;
//    }
//
//
//    private class MyFragmentPagerAdapter extends FragmentPagerAdapter {
//        ArrayList<Fragment> list;
//
//        public MyFragmentPagerAdapter(FragmentManager fm, ArrayList<Fragment> list) {
//            super(fm);
//            this.list = list;
//
//        }
//        @Override
//        public int getCount() {
//            return list.size();
//        }
//
//        @Override
//        public Fragment getItem(int arg0) {
//            return list.get(arg0);
//        }
//
//
//    }


    private long timeMillis;//自动初始化为0

    @Override
    public boolean onKeyDown(int keyCode, KeyEvent event) {
        if (keyCode == KeyEvent.KEYCODE_BACK && event.getAction() == KeyEvent.ACTION_DOWN) {
            if ((System.currentTimeMillis() - timeMillis) > 2000) {
                Toast.makeText(getApplicationContext(), "再按一次退出程序", Toast.LENGTH_SHORT).show();
                timeMillis = System.currentTimeMillis();
            } else {
                finish();
                System.exit(0);
            }
            return true;
        }
        return super.onKeyDown(keyCode, event);
    }

    @Override
    public void onTabChanged(String tabId) {

    }
}
