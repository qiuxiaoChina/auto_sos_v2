package com.autosos.yd.view;

import android.app.Activity;
import android.app.AlertDialog;
import android.content.DialogInterface;
import android.content.Intent;
import android.content.IntentFilter;
import android.os.Bundle;
import android.os.Environment;
import android.support.annotation.NonNull;
import android.support.v4.app.FragmentActivity;
import android.support.v4.app.FragmentManager;
import android.support.v4.app.FragmentTransaction;
import android.util.Log;
import android.view.View;
import android.widget.ImageView;
import android.widget.TabHost;
import android.widget.TextView;
import android.widget.Toast;

import com.autosos.yd.util.CherkNetWorkReceiver;
import com.autosos.yd.util.GetuiSdkMsgReceiver;
import com.autosos.yd.util.Session;
import com.autosos.yd.widget.CatchException;
import com.baidu.mapapi.SDKInitializer;
import com.igexin.sdk.PushManager;
import com.umeng.analytics.MobclickAgent;

import java.io.File;
import java.util.Timer;
import java.util.TimerTask;

import com.autosos.yd.R;
import com.autosos.yd.fragment.MoreFragment;
import com.autosos.yd.fragment.PersonFragment;
import com.autosos.yd.fragment.RecordsFragment;
import com.autosos.yd.fragment.WorkFragment;
import com.autosos.yd.util.JSONUtil;

public class MainActivity extends FragmentActivity implements TabHost.OnTabChangeListener,Thread.UncaughtExceptionHandler {
    public static Activity Mainactivity;
    private static final String TAG = "MainActivity";
    public static int msg = 0;
    private TabHost tabHost;
    private int lastPosition;
    private boolean isExit;
    private TextView tabView1;
    private TextView tabView2;
    private TextView tabView3;
    private TextView tabView4;
    private WorkFragment workFragment ;
    private IntentFilter filter;
    CherkNetWorkReceiver cherkNetWorkReceiver = new CherkNetWorkReceiver();
    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        SDKInitializer.initialize(this.getApplicationContext());
        //PushManager.getInstance().initialize(this.getApplicationContext());

        Mainactivity = this;
        if(Session.getInstance().getCurrentUser(MainActivity.this) == null){
            Intent i = new Intent(com.autosos.yd.view.MainActivity.this, LoginActivity.class);
            i.putExtra("logout", true);
            startActivity(i);
            finish();
            overridePendingTransition(R.anim.slide_in_right, R.anim.activity_anim_default);
        }
        File file2 =new File(Environment.getExternalStorageDirectory()+"/com.autosos.jd/");
        if(!file2.exists()){
            file2.mkdir();
        }


        filter=new IntentFilter();
        filter.addAction(Intent.ACTION_TIME_TICK);
        filter.addAction(Intent.ACTION_SCREEN_ON);
        filter.addAction(Intent.ACTION_SCREEN_OFF);
        filter.addAction(Intent.ACTION_USER_PRESENT);

        registerReceiver(cherkNetWorkReceiver, filter);
            //设置音量为多媒体
       // AudioManager am = (AudioManager)getSystemService(Context.AUDIO_SERVICE);
     //   am.setStreamVolume(AudioManager.STREAM_MUSIC,am.getStreamVolume(AudioManager.STREAM_MUSIC), AudioManager.FLAG_PLAY_SOUND);
        //透明状态栏
//        getWindow().addFlags(WindowManager.LayoutParams.FLAG_TRANSLUCENT_STATUS);
        //透明导航栏
//        getWindow().addFlags(WindowManager.LayoutParams.FLAG_TRANSLUCENT_NAVIGATION);
        String fileName = "crash-" + "Exception" + ".log";
        String path =  Environment.getExternalStorageDirectory()+"/com.autosos.jd/log/";
        File file = new File(path +fileName);
        if(file.exists()) {
            String log = CatchException.readLog(path + fileName);
            CatchException.sendtoServe(MainActivity.this, log);
        }

       /* if (!Constants.DEBUG) {
            UmengUpdateAgent.setUpdateAutoPopup(true);
            UmengUpdateAgent.update(getApplicationContext());
            UmengUpdateAgent.setUpdateOnlyWifi(false);
        }
        */
        lastPosition = -1;
        setContentView(R.layout.bottom_buttons);
        tabsInit();

    }


    @Override
    protected void onNewIntent(Intent intent) {
        selectChange(0);

        super.onNewIntent(intent);
    }

    @Override
    protected void onSaveInstanceState(Bundle outState) {
        super.onSaveInstanceState(outState);
    }

    @Override
    protected void onRestoreInstanceState(@NonNull Bundle savedInstanceState) {
        if (getIntent() != null) {
            super.onRestoreInstanceState(savedInstanceState);
        } else {
            Intent i = getBaseContext().getPackageManager().getLaunchIntentForPackage
                    (getBaseContext().getPackageName());
            i.addFlags(Intent.FLAG_ACTIVITY_CLEAR_TOP);
            startActivity(i);
            finish();
        }
    }

    @Override
    protected void onDestroy() {
        try {
            registerReceiver(cherkNetWorkReceiver, filter);
        }catch (Exception e ){
            e.printStackTrace();
        }
        super.onDestroy();
    }

    public void tabsInit() {
        View tab1 = getLayoutInflater().inflate(R.layout.tab_view, null);
        ImageView icon = (ImageView) tab1.findViewById(R.id.icon);
        icon.setImageResource(R.drawable.icon_main_menu1);
        tabView1 = (TextView) tab1.findViewById(R.id.text);
        tabView1.setText(R.string.label_main_menu1);
        View tab2 = getLayoutInflater().inflate(R.layout.tab_view, null);
        icon = (ImageView) tab2.findViewById(R.id.icon);
        icon.setImageResource(R.drawable.icon_main_menu2);
        tabView2 = (TextView) tab2.findViewById(R.id.text);
        tabView2.setText(R.string.label_main_menu2);
        View tab3 = getLayoutInflater().inflate(R.layout.tab_view, null);
        icon = (ImageView) tab3.findViewById(R.id.icon);
        icon.setImageResource(R.drawable.icon_main_menu3);
        tabView3 = (TextView) tab3.findViewById(R.id.text);
        tabView3.setText(R.string.label_main_menu3);
        View tab4 = getLayoutInflater().inflate(R.layout.tab_view, null);
        icon = (ImageView) tab4.findViewById(R.id.icon);
        icon.setImageResource(R.drawable.icon_main_menu4);
        tabView4 = (TextView) tab4.findViewById(R.id.text);
        tabView4.setText(R.string.label_main_menu4);
            tabHost = (TabHost) findViewById(android.R.id.tabhost);
            tabHost.setOnTabChangedListener(this);
            tabHost.setup();
            tabHost.addTab(tabHost.newTabSpec("workFragment").setIndicator(tab1)
                    .setContent(R.id.tab1));
            tabHost.addTab(tabHost.newTabSpec("recordsFragment").setIndicator(tab2)
                    .setContent(R.id.tab2));
            tabHost.addTab(tabHost.newTabSpec("personFragment").setIndicator(tab3)
                    .setContent(R.id.tab3));
            tabHost.addTab(tabHost.newTabSpec("moreFragment").setIndicator(tab4)
                    .setContent(R.id.tab4));
    }

    public void selectChange(int position) {
        tabView1.setTextColor(getResources().getColor(R.color.color_gray));
        tabView2.setTextColor(getResources().getColor(R.color.color_gray));
        tabView3.setTextColor(getResources().getColor(R.color.color_gray));
        tabView4.setTextColor(getResources().getColor(R.color.color_gray));
        if (lastPosition != position) {
            lastPosition = position;
        } else {
            return;
        }
        FragmentManager fm = getSupportFragmentManager();
         workFragment = (WorkFragment) fm
                .findFragmentByTag("workFragment");
        RecordsFragment orderFragment = (RecordsFragment) fm
                .findFragmentByTag("recordsFragment");
        PersonFragment messageFragment = (PersonFragment) fm
                .findFragmentByTag("personFragment");
        MoreFragment settingFragment = (MoreFragment) fm
                .findFragmentByTag("moreFragment");
        FragmentTransaction ft = fm.beginTransaction();
        if (workFragment != null && !workFragment.isHidden())
            ft.hide(workFragment);
        if (orderFragment != null && !orderFragment.isHidden())
            ft.hide(orderFragment);
        if (messageFragment != null && !messageFragment.isHidden())
            ft.hide(messageFragment);
        if (settingFragment != null && !settingFragment.isHidden())
            ft.hide(settingFragment);
        switch (position) {
            case 0:
                tabView1.setTextColor(getResources().getColor(R.color.color_blue2));
                if (workFragment == null) {
                    ft.add(R.id.tabContent, WorkFragment.newInstance(),
                            "workFragment");
                } else {
                    ft.show(workFragment);
                }
                break;
            case 1:
                tabView2.setTextColor(getResources().getColor(R.color.color_blue2));
                if (orderFragment == null) {
                    ft.add(R.id.tabContent, RecordsFragment.newInstance(),
                            "recordsFragment");
                } else {
                    ft.show(orderFragment);
                }
                break;
               case 2:
                   tabView3.setTextColor(getResources().getColor(R.color.color_blue2));
                if (messageFragment == null) {
                    ft.add(R.id.tabContent, PersonFragment.newInstance(),
                            "personFragment");
                } else {
                    ft.show(messageFragment);
                }
                break;
            case 3:
                tabView4.setTextColor(getResources().getColor(R.color.color_blue2));
                if (settingFragment == null) {
                    ft.add(R.id.tabContent, MoreFragment.newInstance(),
                            "moreFragment");
                } else {
                    ft.show(settingFragment);
                }
                break;
            default:
                break;
        }
        ft.commitAllowingStateLoss();
    }
    @Override
    public void onTabChanged(String tabId) {
        if (!JSONUtil.isEmpty(tabId)) {
            switch (tabId) {
                case "workFragment":
                    selectChange(0);
                    break;
                case "recordsFragment":
                    selectChange(1);
                    break;
                case "personFragment":
                    selectChange(2);
                    break;
                case "moreFragment":
                    selectChange(3);
                    break;
                default:
                    break;
            }
        }
    }


    @Override
    public void onBackPressed() {
        exitBy2Click();
    }

    private void exitBy2Click() {
        Timer tExit;
        if (!isExit) {
            isExit = true;
            Toast.makeText(this, R.string.label_quit, Toast.LENGTH_SHORT).show();
            tExit = new Timer();
            tExit.schedule(new TimerTask() {
                @Override
                public void run() {
                    isExit = false;
                }
            }, 2000);

        } else {
            moveTaskToBack(false);
            //finish();
        }

    }

    @Override
    protected void onPause() {
        super.onPause();
        MobclickAgent.onPause(this);
    }

    @Override
    protected void onResume() {
        //PushManager.getInstance().initialize(this.getApplicationContext());
       // selectChange(0);
        super.onResume();
        //MobclickAgent.onResume(this);
        Log.e("test", "11111111");
    }
    public void test(View v){
        final String items[]={"男","女"};
        AlertDialog.Builder builder=new AlertDialog.Builder(this);  //先得到构造器
        builder.setTitle("提示"); //设置标题
        builder.setIcon(R.drawable.icon19);//设置图标，图片id即可
        builder.setSingleChoiceItems(items,0,new DialogInterface.OnClickListener() {
            @Override
            public void onClick(DialogInterface dialog, int which) {
                //dialog.dismiss();
                Toast.makeText(MainActivity.this, items[which], Toast.LENGTH_SHORT).show();
            }
        });
        builder.setPositiveButton("确定",new DialogInterface.OnClickListener() {
            @Override
            public void onClick(DialogInterface dialog, int which) {
                dialog.dismiss();
                Toast.makeText(MainActivity.this, "确定", Toast.LENGTH_SHORT).show();
            }
        });
        builder.create().show();

//        Intent i =new Intent(MainActivity.this,testActivity.class);
//        startActivity(i);
    }

    public void test2(View v){
        PushManager.getInstance().initialize(SplashActivity.splashActivity);
    }

    @Override

    public void uncaughtException(Thread thread, Throwable ex) {
        System.out.println("uncaughtException");
        System.exit(0);
        Intent intent = new Intent(this, MainActivity.class);
        intent.addFlags(Intent.FLAG_ACTIVITY_CLEAR_TOP |
                Intent.FLAG_ACTIVITY_NEW_TASK);
        startActivity(intent);
    }

}
