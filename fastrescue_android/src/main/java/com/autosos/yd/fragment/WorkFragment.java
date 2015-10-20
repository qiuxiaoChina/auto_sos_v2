package com.autosos.yd.fragment;

import android.app.Dialog;
import android.app.NotificationManager;
import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.Intent;
import android.content.IntentFilter;
import android.content.SharedPreferences;
import android.graphics.Point;
import android.media.AudioManager;
import android.media.SoundPool;
import android.os.AsyncTask;
import android.os.Bundle;
import android.os.Handler;
import android.os.Message;
import android.os.SystemClock;
import android.support.annotation.Nullable;
import android.support.v4.app.Fragment;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.view.Window;
import android.view.WindowManager;
import android.view.animation.Animation;
import android.view.animation.AnimationUtils;
import android.widget.Button;
import android.widget.LinearLayout;
import android.widget.RelativeLayout;
import android.widget.ScrollView;
import android.widget.TextView;
import android.widget.Toast;

import com.autosos.yd.model.Notice;
import com.autosos.yd.util.GetuiSdkMsgReceiver;
import com.autosos.yd.util.UpdateStateServe;
import com.autosos.yd.view.AddressActivity;
import com.autosos.yd.view.NoticeInfoActivity;
import com.autosos.yd.view.PopActivity;
import com.autosos.yd.widget.CherkInternet;
import com.autosos.yd.widget.DialogView;
import com.baidu.location.BDLocation;
import com.baidu.location.BDLocationListener;
import com.baidu.location.LocationClient;
import com.baidu.location.LocationClientOption;
import com.handmark.pulltorefresh.library.PullToRefreshBase;
import com.handmark.pulltorefresh.library.PullToRefreshScrollView;

import org.json.JSONException;
import org.json.JSONObject;

import java.io.IOException;
import java.util.HashMap;
import java.util.Map;

import com.autosos.yd.Constants;
import com.autosos.yd.R;
import com.autosos.yd.model.Person;
import com.autosos.yd.task.AuthGetJSONObjectAsyncTask;
import com.autosos.yd.task.NewHttpPostTask;
import com.autosos.yd.task.OnHttpRequestListener;
import com.autosos.yd.util.JSONUtil;
import com.autosos.yd.util.Utils;


public class WorkFragment extends Fragment implements View.OnClickListener,
        PullToRefreshBase.OnRefreshListener<ScrollView>,BDLocationListener {
    private Dialog dialog;
    private View rootView;
    private static final String TAG = "WorkFragment";
    private View progressBar;
    private int nowState;  //0logout   1stop  2start  3inserve
    private View empty;
    private TextView countView;
    private TextView rateView;
    private Button startBtn;
    private Button stopBtn;
    private RelativeLayout slideView;
    private Animation translateAnimation;
    public static String url;
 //   private Button viewBtn;
    private TextView timeView;
    private int online_status = 0;
    private Handler stepTimeHandler;
    private long startTime;
    private Runnable ticker;
    private SoundPool sp;
    private Notice notice;
    private int startSoundId;
    private int stopSoundId;
    private final static int MSG_START_SOUND_EFFECT = 1;
    private final static int MSG_STOP_SOUND_EFFECT = 2;
    private Handler mHandler = new Handler() {
        @Override
        public void handleMessage(Message msg) {
            switch (msg.what) {
                case MSG_START_SOUND_EFFECT:
                    if(UpdateStateServe.Setting_Sound_OnOff)
                        playStartSoundEffect();
                    break;
                case MSG_STOP_SOUND_EFFECT:
                    if(UpdateStateServe.Setting_Sound_OnOff)
                        playStopSoundEffect();
                    break;
                default:
                    break;
            }
        }
    };
    private Person person = null;
    private PullToRefreshScrollView scrollView;
    private LocationClient client;
    public  double latitude;
    public  double longitude;
    private TextView addressView;
    private LinearLayout listinfoView;
    private int test_point;
    public static com.autosos.yd.fragment.WorkFragment newInstance() {
        return new com.autosos.yd.fragment.WorkFragment();
    }

    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);

    }
    private MyReceiver receiver;
    private class MyReceiver extends BroadcastReceiver {

        @Override
        public void onReceive(Context context, Intent intent) {
            try {
                Bundle bundle = intent.getExtras();
                String info = bundle.getString("info",null);
                latitude = UpdateStateServe.latitude;
                longitude = UpdateStateServe.longitude;
                if (info != null &&info.length() > 5)
                    addressView.setText(String.format(rootView.getContext().getString(R.string.label_current_place),
                            info) + "   ");
            }catch (Exception e){
                e.printStackTrace();
            }
        }
    }


    @Nullable
    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        rootView = inflater.inflate(R.layout.fragment_second_work, container, false);
        UpdateStateServe.UpdateStateServeActive = true;
        if(Constants.DEBUG)
             rootView.findViewById(R.id.test_label).setVisibility(View.VISIBLE);
        test_point = 1;
        longitude = UpdateStateServe.longitude;
        latitude = UpdateStateServe.latitude;
        nowState = 0;
        BDLocation bdLocation =new BDLocation();
        bdLocation.setLatitude(latitude);
        bdLocation.setLongitude(longitude);
        addressView = (TextView) rootView.findViewById(R.id.address);
        if(bdLocation.getAddrStr() !=null)
          addressView.setText(String.format(rootView.getContext().getString(R.string.label_address))
                + String.format(rootView.getContext().getString(R.string.label_current_place),
                bdLocation.getAddrStr())+"   ");

        receiver = new MyReceiver();
        IntentFilter filter = new IntentFilter();
        filter.addAction(".util.UpdateStateServe");

        rootView.getContext().registerReceiver(receiver, filter);

        sp = new SoundPool(1, AudioManager.STREAM_MUSIC, 0);
        startSoundId = sp.load(getActivity().getApplicationContext(), R.raw.order_start, 1);
        stopSoundId = sp.load(getActivity().getApplicationContext(), R.raw.order_stop, 1);
        stepTimeHandler = new Handler();
        listinfoView = (LinearLayout) rootView.findViewById(R.id.list_info);
        countView = (TextView) rootView.findViewById(R.id.count);
        rateView = (TextView) rootView.findViewById(R.id.rate);
        startBtn = (Button) rootView.findViewById(R.id.startBtn);
        stopBtn = (Button) rootView.findViewById(R.id.stopBtn);
        slideView = (RelativeLayout)rootView.findViewById(R.id.btn_action_up);
       // viewBtn = (Button) rootView.findViewById(R.id.viewBtn);
        timeView = (TextView) rootView.findViewById(R.id.timecounter);
        startBtn.setOnClickListener(this);
        stopBtn.setOnClickListener(this);
        popActivity = new PopActivity(rootView.getContext(),getActivity());
     //   viewBtn.setOnClickListener(this);
        scrollView = (PullToRefreshScrollView) rootView.findViewById(R.id.main_list);
        scrollView.setOnRefreshListener(this);
        progressBar = rootView.findViewById(R.id.include2);
        empty = rootView.findViewById(R.id.empty);
        startTime = System.currentTimeMillis();
        Log.e(TAG,"lo"+longitude+"la"+latitude);
        ticker = new Runnable() {
            public void run() {
                String content = Utils.showTimeCount(System.currentTimeMillis() - startTime);
                if (isAdded()) {
                    getResources().getString(R.string.label_time_counter);
                }
                timeView.setText(String.format(rootView.getContext().getString(R.string.label_time_counter), content));

                long now = SystemClock.uptimeMillis();
                long next = now + (1000 - now % 1000);
                stepTimeHandler.postAtTime(ticker, next);
            }
        };
        timeView.setText(String.format(rootView.getContext().getString(R.string.label_time_counter),"00:00:00"));
                //new GetPersonTask(getActivity().getApplicationContext()).executeOnExecutor(Constants.INFOTHEADPOOL,
                //      Constants.PERSON_URL);
               // handleUserLocationInfo(3);
        getSPloAndla();
        new GetLastNoticeTask().executeOnExecutor(Constants.LISTTHEADPOOL,
                Constants.NOTICE_LAST_NOTICE);
        try{
            NotificationManager manager = (NotificationManager) rootView.getContext().getSystemService(Context.NOTIFICATION_SERVICE);
            manager.cancel(0);
        }catch (Exception e ){
            e.printStackTrace();
        }



        if(Constants.DEBUG){
            addressView.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View v) {
                    Intent intent = new Intent(getActivity(), AddressActivity.class);
                    intent.putExtra("image_path",person.getAvatar());
                    intent.putExtra("name",person.getRealname());
                    startActivity(intent);
                    getActivity().overridePendingTransition(R.anim.slide_in_right, R.anim.activity_anim_default);
                }
            });
        }
        return rootView;
    }

    private class GetLastNoticeTask extends AsyncTask<String, Object, JSONObject> {

        private GetLastNoticeTask() {

        }

        @Override
        protected JSONObject doInBackground(String... params) {
            try {
                String jsonStr = JSONUtil.getStringFromUrl(getActivity().getApplicationContext(), params[0]);
                if (JSONUtil.isEmpty(jsonStr)) {
                    return null;
                }
                return new JSONObject(jsonStr);
            } catch (IOException | JSONException e) {

            }
            return null;
        }

        @Override
        protected void onPostExecute(JSONObject result) {
            progressBar.setVisibility(View.GONE);
            try {
                if (result != null && result.toString().length() > 2) {
                    Log.e(TAG,result.toString());
                  //  Log.e(TAG,"111:"+result.toString().length());
                    notice = new Notice(result);
                    dialog("Notice");
                    super.onPostExecute(result);
                }
            }catch (Exception e){
                e.printStackTrace();
            }
        }
    }

    @Override
    public void onHiddenChanged(boolean hidden) {
        Log.e("test", "2222222");
        if(!hidden){
            try{
                NotificationManager manager = (NotificationManager) rootView.getContext().getSystemService(Context.NOTIFICATION_SERVICE);
                manager.cancel(0);
            }catch (Exception e ){
                e.printStackTrace();
            }
        }
        GetuiSdkMsgReceiver.playMusic = false;
        super.onHiddenChanged(hidden);
        Log.e(TAG, "hidden" + hidden + "status    :" + nowState);
        if(hidden && popActivity.popWindow.isShowing()){
            popActivity.popWindow.dismiss();
            try{
                popActivity.unreigist();
            }catch (Exception e){
                e.printStackTrace();
            }
        }
        if(!hidden && !popActivity.popWindow.isShowing() && (nowState == 2 || nowState == 3)){
            popActivity.mypop(rootView.findViewById(R.id.info_view));
        }
        //baiduMapListenner_active = false;
    }

    @Override
    public void onPause() {
        super.onPause();
       Log.e(TAG, "onPause");
    }

    @Override
    public void onResume() {
        GetuiSdkMsgReceiver.playMusic = false;
        super.onResume();
        if(UpdateStateServe.latitude == 0 &&UpdateStateServe.longitude == 0){
            locationit();
        }
        progressBar.setVisibility(View.VISIBLE);
        if(!CherkInternet.cherkInternet(rootView.getContext())){
            DialogView dialogView = new DialogView();
            dialogView.dialogInternet(getActivity());
        }
        new GetPersonTask(getActivity().getApplicationContext()).executeOnExecutor(Constants.INFOTHEADPOOL,
                Constants.PERSON_URL);
        if(popActivity.popWindow.isShowing()){
            try{
                popActivity.onRefresh();
            }catch (Exception e){

            }
        }

    }
    private void changeViews(){
        if(online_status != nowState) {
            switch (online_status) {
                case 1:
                    slideIn();
                    //  ticker.run();
                    break;
                case 2:
                    slideOut();
                    ticker.run();
                    break;
                case 3:
                    slideOut();
                    ticker.run();
                    //  stopBtn.setVisibility(View.GONE);
                    break;
                default:
                    break;
            }
            nowState = online_status;
        }
    }
    private void changeViews( int online_status){
        Log.e(TAG,"-------------------------");
        switch (online_status) {
            case 1:
                if(UpdateStateServe.UpdateChangeTime == UpdateStateServe.UpdateNowStateTen)
                    UpdateStateServe.UpdateChangeTime = UpdateStateServe.UpdateChangeTimeSixty;
                //  ticker.run();
                break;
            case 2:
                if(UpdateStateServe.UpdateChangeTime == UpdateStateServe.UpdateNowStateTen)
                    UpdateStateServe.UpdateChangeTime = UpdateStateServe.UpdateChangeTimeSixty;
                break;
            case 3:
                if(!Constants.DEBUG && UpdateStateServe.UpdateChangeTime == UpdateStateServe.UpdateNowStateSixty ) {
                    UpdateStateServe.UpdateChangeTime = UpdateStateServe.UpdateChangeTimeTen;
                    Log.e(TAG,"change 3");
                }
                break;
            default:
                break;
        }
    }
    @Override
    public void onRefresh(PullToRefreshBase<ScrollView> refreshView) {
        if(!CherkInternet.cherkInternet(rootView.getContext())){
            DialogView dialogView = new DialogView();
            dialogView.dialogInternet(getActivity());
        }
        if(popActivity.popWindow.isShowing()){
            try{
                popActivity.onRefresh();
            }catch (Exception e){

            }
        }
        new GetPersonTask(getActivity().getApplicationContext()).executeOnExecutor(Constants.INFOTHEADPOOL,
                Constants.PERSON_URL);
        locationit();

    }


    private class GetPersonTask extends AuthGetJSONObjectAsyncTask {
        public GetPersonTask(Context context) {
            super(context);
        }

        @Override
        protected JSONObject doInBackground(String... params) {
            try {
                String jsonStr = JSONUtil.getStringFromUrl(getActivity().getApplicationContext(), params[0]);
                if (JSONUtil.isEmpty(jsonStr)) {
                    return null;
                }
                Log.e(TAG, jsonStr);
                return new JSONObject(jsonStr);
            } catch (IOException | JSONException e) {

            }
            return null;
        }

        @Override
        protected void onPostExecute(JSONObject jsonObject) {
            try {
                progressBar.setVisibility(View.GONE);
                scrollView.onRefreshComplete();
                if (jsonObject != null) {
                    person = new Person(jsonObject);
                    if (person != null) {
                        countView.setText(String.valueOf(person.getAccepted_count()));
                        rateView.setText(String.valueOf(person.getAverage_rate()));
                        online_status = person.getOnline_status();
                        handleUserLocationInfo(1);
                    }
                }
                changeViews();
                changeViews(online_status);
                empty.setVisibility(View.GONE);
            }catch (Exception e ){
                e.printStackTrace();
            }
            super.onPostExecute(jsonObject);
        }
    }

    @Override
    public void onDestroy() {
        Log.e(TAG, "destory !");
        popActivity.popWindow.dismiss();
        try{
            rootView.getContext().unregisterReceiver(receiver);
            popActivity.unreigist();
        }catch (Exception e){
            e.printStackTrace();
        }
        super.onDestroy();
    }

    private void playStartSoundEffect() {
        sp.play(startSoundId, 1, 1, 0, 0, 1.0f);
    }

    private void playStopSoundEffect() {
        sp.play(stopSoundId, 1, 1, 0, 0, 1.0f);
    }

    public void handleUserLocationInfo(final int url) {
        SharedPreferences sharedPreferences = rootView.getContext().getSharedPreferences(Constants
                .PREF_FILE, Context.MODE_PRIVATE);
        String cid = sharedPreferences.getString("clientid", "null");
        if (latitude > 0 && longitude > 0 && cid != null) {
            Map<String, Object> map = new HashMap<>();
            map.put("lat", String.valueOf(latitude));
            map.put("lng", String.valueOf(longitude));
            map.put("getui_cid", cid);
            Log.e(TAG,"cid"+cid);
           // map.put("status", String.valueOf(status));
           NewHttpPostTask httpPostTask = new NewHttpPostTask(rootView.getContext(), new OnHttpRequestListener() {
                @Override
                public void onRequestCompleted(Object obj) {
                    try {
                        JSONObject jsonObject = new JSONObject(obj.toString());
                        if(jsonObject.getString("result").equals("1")){
                            // Toast.makeText(rootView.getContext(),R.string.msg_logout_ok,Toast.LENGTH_LONG).show();
                            if(url  == 3){
                                slideIn();
                                nowState = 1;
                                mHandler.sendEmptyMessage(MSG_STOP_SOUND_EFFECT);
                                startBtn.setVisibility(View.VISIBLE);
                                stepTimeHandler.removeCallbacks(ticker);
                            }
                            if(url == 2){
                                slideOut();
                                nowState = 2;
                                mHandler.sendEmptyMessage(MSG_START_SOUND_EFFECT);
                                //startBtn.setVisibility(View.GONE);
                                //availLayout.setVisibility(View.VISIBLE);
                                startTime = System.currentTimeMillis();
                                ticker = new Runnable() {
                                    public void run() {
                                        String content = Utils.showTimeCount(System.currentTimeMillis() - startTime);
                                        timeView.setText(String.format(rootView.getContext().getString(R.string.label_time_counter), content));

                                        long now = SystemClock.uptimeMillis();
                                        long next = now + (1000 - now % 1000);
                                        stepTimeHandler.postAtTime(ticker, next);
                                    }
                                };
                                ticker.run();
                            }
                        }
                        else{
                            Toast.makeText(rootView.getContext(),R.string.msg_change_error,Toast.LENGTH_LONG).show();
                        }
                    }catch (Exception e){
                        e.printStackTrace();
                    }
                }

                @Override
                public void onRequestFailed(Object obj) {
                }
            });
            if(url == 1) {
                Log.e(TAG,"USER_LOCATION_URL!BU GAN LE!");
            }
            else if(url == 2){
                httpPostTask.execute(Constants.USER_START_WORK_URL, map);
                Log.e(TAG,"USER_START_WORK_URL!");
            }
            else if(url == 3){
                httpPostTask.execute(Constants.USER_STOP_WORK_URL, map);
                Log.e(TAG,"USER_STOP_WORK_URL!");
            }
        }
    }

    private void locationit() {
        LocationClientOption option = new LocationClientOption();
        option.setOpenGps(true);
        option.setLocationMode(LocationClientOption.LocationMode.Hight_Accuracy);
        option.setCoorType("bd09ll");
        option.setScanSpan(0);
        option.setProdName("com.autosos.yd");
        option.setIsNeedAddress(true);
        option.isLocationNotify();
        client = new LocationClient(rootView.getContext());
        client.setLocOption(option);
        client.registerLocationListener(this);
        client.start();
        Log.e(TAG,"locating ing!");
    }
    @Override
    public void onReceiveLocation(BDLocation bdLocation) {
        if (client.isStarted()) {
            Log.e(TAG, "BDLocation  " + bdLocation.toString() + "***********************"+"la:"
                    +bdLocation.getLatitude()+"**log"+bdLocation.getLongitude());
            if (bdLocation.getLocType() == 61 || bdLocation.getLocType() == 65 || bdLocation
                    .getLocType() == 161 && bdLocation.getLatitude() != 0 && bdLocation.getLongitude() != 0) {
                test_point = 100;
                latitude = bdLocation.getLatitude();
                longitude = bdLocation.getLongitude();
                UpdateStateServe.latitude = latitude;
                UpdateStateServe.longitude = longitude;
                addressView.setText(String.format(rootView.getContext().getString(R.string.label_current_place),
                        bdLocation.getAddrStr())+"   ");
             //   handleUserLocationInfo(1);
                savrSPloAndla(bdLocation.getAddrStr());
            }
            client.stop();
            client.unRegisterLocationListener(this);
        }
    }
    @Override
    public void onClick(View v) {
        Log.e(TAG, "now State  :" + online_status);
        switch (v.getId()) {
            case R.id.stopBtn:
                if(!CherkInternet.cherkInternet(rootView.getContext())){
                    DialogView dialogView = new DialogView();
                    dialogView.dialogInternet(getActivity());
                }else {
                    if (online_status == 3 && true) {
                        dialog2();
                    } else {
                        dialog("stop");
                    }
                    //timeView.setText(String.format(rootView.getContext().getString(R.string.label_time_counter), "00:00:00"));
                }
                break;
            case R.id.startBtn:
                if(!CherkInternet.cherkInternet(rootView.getContext())){
                    DialogView dialogView = new DialogView();
                    dialogView.dialogInternet(getActivity());
                }
                else {
                    start_work();
                }
                break;
        }
    }
    public void start_work(){
        try {
            locationit();
        }catch (Exception e){
            e.printStackTrace();
        }
        if(latitude ==0 && longitude == 0){
            getSPloAndla();
        }
        if(latitude == 0 && longitude == 0){
            dialog("location_again");
            return ;
        }
        if (progressBar.getVisibility() == View.VISIBLE) {
            return;
        }
        handleUserLocationInfo(2);
    }
    public void intent2Orders() {
        locationit();
    }
    public void dialog(String type){
        dialog = new Dialog(rootView.getContext(), R.style.bubble_dialog);
        View view = getActivity().getLayoutInflater().inflate(R.layout.dialog_msg_notice,
                null);
        Button tvConfirm = (Button) view.findViewById(R.id.btn_notice_confirm);
        Button tvCancel = (Button) view.findViewById(R.id.btn_notice_cancel);
        TextView tvMsg = (TextView) view.findViewById(R.id.tv_notice_msg);
        tvCancel.setText(String.format(getString(R.string.label_cancel)));
        if(type.equals("stop")) {
            tvMsg.setText(R.string.msg_stop_order);
            tvConfirm.setOnClickListener(new View.OnClickListener() {

                @Override
                public void onClick(View v) {
                    dialog.dismiss();
                    if (progressBar.getVisibility() == View.VISIBLE) {
                        return;
                    }
                    handleUserLocationInfo(3);

                }
            });
        }
        else if(type.equals("location_again")) {
            tvMsg.setText(R.string.msg_reloction);
            tvConfirm.setOnClickListener(new View.OnClickListener() {

                @Override
                public void onClick(View v) {
                    dialog.dismiss();
                    locationit();
                }
            });
        }
        else if(type.equals("Notice")){
            tvMsg.setText(String.format(getString(R.string.msg_notice)));
            tvConfirm.setText(String.format(getString(R.string.label_view_notice)));
            tvConfirm.setOnClickListener(new View.OnClickListener() {

                @Override
                public void onClick(View v) {
                    dialog.dismiss();
                    Intent intent = new Intent(rootView.getContext(), NoticeInfoActivity.class);
                    intent.putExtra("notice_id", notice.getId());
                    startActivity(intent);
                    getActivity().overridePendingTransition(R.anim.slide_in_right, R.anim.activity_anim_default);

                }
            });
            tvCancel.setText(String.format(getString(R.string.label_view_notice_no)));
        }
        else if(type.equals("nofinish")){
            tvMsg.setText(String.format(getString(R.string.msg_nofinish)));
            tvConfirm.setText(String.format(getString(R.string.label_view_nofinish)));
            tvConfirm.setOnClickListener(new View.OnClickListener() {

                @Override
                public void onClick(View v) {
                    dialog.dismiss();
                }
            });
            tvCancel.setText(String.format(getString(R.string.label_view_nofinish2)));
        }
        tvCancel.setOnClickListener(new View.OnClickListener() {

            @Override
            public void onClick(View v) {
                dialog.dismiss();
            }
        });

        dialog.setContentView(view);
        dialog.setCanceledOnTouchOutside(false);
        Window window = dialog.getWindow();
        WindowManager.LayoutParams params = window.getAttributes();
        Point point = JSONUtil.getDeviceSize(rootView.getContext());
        params.width = Math.round(point.x * 5 / 7);
        window.setAttributes(params);
        dialog.show();
    }
    public void getSPloAndla(){
        SharedPreferences sharedPreferences = rootView.getContext().getSharedPreferences("SaveLoAndLa",
                Context.MODE_PRIVATE);
        double la = Double.parseDouble(sharedPreferences.getString("latitude", "0.00"));
        double lo = Double.parseDouble(sharedPreferences.getString("longitude", "0.00"));
        String adddress = sharedPreferences.getString("address", "");
        Log.e(TAG, "spla:" + la + "---splo" + lo + "--address" + adddress);
        if (la != 0.00 && lo != 0.00) {
            latitude = la;
            longitude = lo;
            BDLocation bdLocation =new BDLocation();
            bdLocation.setLatitude(la);
            bdLocation.setLongitude(lo);
            addressView.setText(String.format(rootView.getContext().getString(R.string.label_address))
                    + String.format(rootView.getContext().getString(R.string.label_current_place),
                    adddress) + "   ");
            Log.e(TAG, "sp location");
        }
    }
    public void savrSPloAndla(String address){
        if(latitude != 0 && longitude != 0) {
            SharedPreferences sharedPreferences =
                    rootView.getContext().getSharedPreferences("SaveLoAndLa", Context.MODE_PRIVATE);
            SharedPreferences.Editor editor = sharedPreferences.edit();
            editor.putString("latitude", latitude + "");
            editor.putString("longitude", longitude + "");
            editor.putString("address", address);
            editor.commit();
        }
    }


    private void dialog2(){
        String msg =null;
        msg = String.format(getString(R.string.msg_download));
        View v2 = getActivity().getLayoutInflater().inflate(R.layout.dialog_msg_content,
                null);
        dialog = new Dialog(v2.getContext(), R.style.bubble_dialog);
        Button tvConfirm = (Button) v2.findViewById(R.id.btn_notice_confirm);
        TextView tvMsg = (TextView) v2.findViewById(R.id.tv_notice_msg);
        msg = String.format(getString(R.string.msg_nofinish));
        tvMsg.setText(msg);
        tvConfirm.setText(String.format(getString(R.string.label_view_nofinish)));
        tvConfirm.setOnClickListener(new View.OnClickListener() {
                    @Override
                    public void onClick(View v) {
                        dialog.dismiss();
                    }
                });
        dialog.setContentView(v2);
        dialog.setCanceledOnTouchOutside(false);
        Window window = dialog.getWindow();
        WindowManager.LayoutParams params = window.getAttributes();
        Point point = JSONUtil.getDeviceSize(rootView.getContext());
        params.width = Math.round(point.x * 5 / 7);
        window.setAttributes(params);
        dialog.show();
    }
    private PopActivity popActivity;
    private void slideIn(){
        if(popActivity.popWindow.isShowing()){
            popActivity.popWindow.dismiss();
            try{
                popActivity.unreigist();
            }catch (Exception e){
                e.printStackTrace();
            }
        }
        translateAnimation =AnimationUtils.loadAnimation(rootView.getContext(),R.anim.view_in);
        slideView.startAnimation(translateAnimation);
        translateAnimation.setFillAfter(true);
        startBtn.setClickable(true);
        stopBtn.setVisibility(View.GONE);
        listinfoView.setVisibility(View.VISIBLE);
      //  rootView.findViewById(R.id.work_content).setVisibility(View.GONE);
    }
    private void slideOut(){
       /* Animation translateAnimation2 = AnimationUtils.loadAnimation(rootView.getContext(),R.anim.alpha_change);
        startBtn.startAnimation(translateAnimation2);
        translateAnimation2.setFillAfter(true);
        */
        translateAnimation = AnimationUtils.loadAnimation(rootView.getContext(), R.anim.view_out);
        slideView.startAnimation(translateAnimation);
        translateAnimation.setFillAfter(true);
        startBtn.setClickable(false);

        translateAnimation.setAnimationListener(new Animation.AnimationListener() {
            @Override
            public void onAnimationStart(Animation animation) {
            }

            @Override
            public void onAnimationEnd(Animation animation) {
                stopBtn.setVisibility(View.VISIBLE);
                listinfoView.setVisibility(View.GONE);
                popActivity.mypop(rootView.findViewById(R.id.info_view));

            }

            @Override
            public void onAnimationRepeat(Animation animation) {
            }
        });
    }



}
