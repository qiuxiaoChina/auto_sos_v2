package com.autosos.yd.view;

import android.app.Activity;
import android.app.NotificationManager;
import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.Intent;
import android.content.IntentFilter;
import android.content.SharedPreferences;
import android.graphics.drawable.BitmapDrawable;
import android.graphics.drawable.Drawable;
import android.media.AudioManager;
import android.media.SoundPool;
import android.os.AsyncTask;
import android.os.Bundle;
import android.os.Handler;
import android.os.Message;
import android.os.Vibrator;
import android.util.Log;
import android.view.Gravity;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.view.WindowManager;
import android.view.animation.Animation;
import android.view.animation.AnimationUtils;
import android.widget.AdapterView;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.ListView;
import android.widget.PopupWindow;
import android.widget.RelativeLayout;
import android.widget.TextView;
import android.widget.Toast;

import com.autosos.yd.Constants;
import com.autosos.yd.R;
import com.autosos.yd.adapter.ObjectBindAdapter;
import com.autosos.yd.model.Order;
import com.autosos.yd.task.NewHttpPostTask;
import com.autosos.yd.task.NewHttpPutTask;
import com.autosos.yd.task.OnHttpRequestListener;
import com.autosos.yd.util.DistanceUtil;
import com.autosos.yd.util.JSONUtil;
import com.autosos.yd.util.Location;
import com.autosos.yd.util.UpdateStateServe;
import com.autosos.yd.widget.CherkInternet;
import com.baidu.location.BDLocation;
import com.baidu.mapapi.search.route.RoutePlanSearch;
import com.handmark.pulltorefresh.library.PullToRefreshBase;
import com.handmark.pulltorefresh.library.PullToRefreshListView;
import com.igexin.getuiext.data.Consts;
import com.igexin.sdk.PushConsts;
import com.igexin.sdk.PushManager;
import com.umeng.analytics.MobclickAgent;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import java.io.IOException;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.Map;

/**
 * Created by Administrator on 2015/8/14.
 */
public class PopActivity extends Activity implements ObjectBindAdapter.ViewBinder<Order>,
        AdapterView.OnItemClickListener, PullToRefreshBase.OnRefreshListener<ListView>{
    public PopupWindow popWindow ;
    private RoutePlanSearch routePlanSearch;
    private static final String TAG = "PopWindowActivity";
    private PullToRefreshListView listView;
    private View progressBar;
    private ArrayList<Order> orders;
    private ObjectBindAdapter<Order> adapter;
    private boolean onLoading;
    private SoundPool sp;
    private Boolean firstCreat;
    private int okSoundId;
    private Context myContext;
    private Activity myActivity;
    private final static int MSG_OK_SOUND_EFFECT = 1;
    private Handler mHandler = new Handler() {
        @Override
        public void handleMessage(Message msg) {
            switch (msg.what) {
                case MSG_OK_SOUND_EFFECT:
                    playOkSoundEffect();
                    break;
            }
        }
    };
    public static Location location;
    private View vPopWindow;
    private SharedPreferences sharedPreferences;
    public PopActivity(Context context,Activity activity){
        myContext = context;
        myActivity = activity;
        LayoutInflater inflater = (LayoutInflater) myContext.getSystemService(Context.LAYOUT_INFLATER_SERVICE);
        vPopWindow =null;
        vPopWindow  = inflater.inflate(R.layout.activity_orders, null);
       int height = ((WindowManager) myContext
                .getSystemService(Context.WINDOW_SERVICE)).getDefaultDisplay().getHeight();
       // popWindow = new PopupWindow(vPopWindow, ViewGroup.LayoutParams.FILL_PARENT, height * 4/9,true);
        popWindow = new PopupWindow(vPopWindow, ViewGroup.LayoutParams.FILL_PARENT, height * 35/62 , true);
    }

    public void mypop(View v){
        popWindow.setOutsideTouchable(true);
        popWindow.setFocusable(false);
        popWindow.setAnimationStyle(R.style.popWindow_slide_in_out);
      //  popWindow.showAsDropDown(v);
        int[] location = new int[2];
        v.getLocationOnScreen(location);

        popWindow.showAtLocation(v, Gravity.BOTTOM, location[0], popWindow.getHeight() * 7/ 40);
        //   popWindow.showAtLocation(v, Gravity.NO_GRAVITY, 0, 0 - popWindow.getHeight());
        initView();
    }
    public void unreigist(){
        try {
            myContext.unregisterReceiver(broadcastReceiver);
        }catch (Exception e){
            e.printStackTrace();
        }
    }
    private void initView(){
        firstCreat = true;
        vPopWindow.findViewById(R.id.gray_view).setVisibility(View.INVISIBLE
        );
        //PushManager.getInstance().initialize(myContext.getApplicationContext());
        IntentFilter intentFilter = new IntentFilter();
        intentFilter.addAction(Constants.ACTIONNAME_STRING);
        myContext.registerReceiver(broadcastReceiver, intentFilter);
        sharedPreferences = myContext.getSharedPreferences(Constants.PREF_FILE,
                Context.MODE_PRIVATE);
        sp = new SoundPool(1, AudioManager.STREAM_MUSIC, 0);
        okSoundId = sp.load(myContext, R.raw.order_ok, 1);
        listView = (PullToRefreshListView) vPopWindow.findViewById(R.id.list);
        listView.setOverScrollMode(View.OVER_SCROLL_NEVER);
        orders = new ArrayList<>();
        adapter = new ObjectBindAdapter<>(myContext, orders,
                R.layout.order_item, this);
        listView.setAdapter(adapter);
        listView.setOnRefreshListener(this);
        progressBar = vPopWindow.findViewById(R.id.include);
         new GetOrdersTask().executeOnExecutor(Constants.LISTTHEADPOOL,
               Constants.ORDERS_URL);
        Log.e(TAG, UpdateStateServe.latitude + "----" + UpdateStateServe.longitude);
        // getDistance(0);
    }

    @Override
    public void onRefresh(PullToRefreshBase<ListView> refreshView) {
        Log.e(TAG,"shua xin le !");
        if (!onLoading) {
            new GetOrdersTask().executeOnExecutor(Constants.LISTTHEADPOOL,
                    Constants.ORDERS_URL);
            try{
                NotificationManager manager = (NotificationManager) myContext.getSystemService(Context.NOTIFICATION_SERVICE);
                manager.cancel(0);
            }catch (Exception e ){
                e.printStackTrace();
            }
        }
    }
    public void onRefresh() {
        if (!onLoading) {
            new GetOrdersTask().executeOnExecutor(Constants.LISTTHEADPOOL,
                    Constants.ORDERS_URL);
            try{
                NotificationManager manager = (NotificationManager) myContext.getSystemService(Context.NOTIFICATION_SERVICE);
                manager.cancel(0);
            }catch (Exception e ){
                e.printStackTrace();
            }
        }
    }
    private class GetOrdersTask extends AsyncTask<String, Object, JSONArray> {

        private GetOrdersTask() {
            onLoading = true;
        }

        @Override
        protected JSONArray doInBackground(String... params) {
            try {
                String jsonStr = JSONUtil.getStringFromUrl(myContext, params[0]);
                if (JSONUtil.isEmpty(jsonStr)) {
                    return null;
                }
                Log.e(TAG, jsonStr);
                return new JSONArray(jsonStr);
            } catch (IOException | JSONException e) {

            }
            return null;
        }

        @Override
        protected void onPostExecute(JSONArray result) {
            progressBar.setVisibility(View.GONE);
            listView.onRefreshComplete();
            orders.clear();
            if (result != null) {
                if (result.length() > 0) {
                    int size = result.length();
                    for (int i = 0; i < size; i++) {
                        Order order = new Order(result.optJSONObject(i));
                        orders.add(order);
                    }
                    adapter.notifyDataSetChanged();
                }
                else{
                    adapter.notifyDataSetChanged();
                }
            }
            onLoading = false;
            setEmptyView();
            super.onPostExecute(result);

        }
    }

    public void setEmptyView() {
        if (orders.isEmpty()) {
            View emptyView = listView.getRefreshableView().getEmptyView();
            if (emptyView == null) {
                LayoutInflater inflater = (LayoutInflater) myContext.getSystemService(Context.LAYOUT_INFLATER_SERVICE);
                emptyView = inflater.inflate(R.layout.list_empty_view, null);
                emptyView.setBackgroundColor(myContext.getResources().getColor(R.color.color_white));
                listView.getRefreshableView().setEmptyView(emptyView);
            }
            ImageView imgEmptyHint = (ImageView) emptyView.findViewById(R.id
                    .img_empty_hint);
            TextView textEmptyHint = (TextView) emptyView.findViewById(R.id.text_empty_hint);

            imgEmptyHint.setVisibility(View.VISIBLE);
            textEmptyHint.setVisibility(View.VISIBLE);

            if (JSONUtil.isNetworkConnected(myContext)) {
                textEmptyHint.setText(R.string.msg_order_empty);
            } else {
                textEmptyHint.setText(R.string.msg_net_disconnected);
            }
        }
    }

    @Override
    public void onItemClick(AdapterView<?> parent, View view, int position, long id) {
    }
    private double latitude;
    private double longitude;
    @Override
    public void setViewValue(View view, final Order order, int position) {
        if (view.getTag() == null) {
            ViewHolder holder = new ViewHolder();
            holder.addressView = (TextView) view.findViewById(R.id.address);
            holder.distanceView = (TextView) view.findViewById(R.id.distance);
            holder.statusView = (TextView) view.findViewById(R.id.btn_status);
            holder.typeView = (TextView) view.findViewById(R.id.type);
            holder.nofinishView = (LinearLayout)view.findViewById(R.id.nofinish);
            holder.nofinish_contentView = (TextView)view.findViewById(R.id.nofinish_content);
            view.setTag(holder);
        }
        final ViewHolder holder = (ViewHolder) view.getTag();
        //指派订单
        if(order.getIs_appointed() == 1){
            view.findViewById(R.id.poin1).setVisibility(View.VISIBLE);
            ((TextView) view.findViewById(R.id.poin2)).setText(" " + myContext.getResources().getString(R.string.label_m) + " ");
        }
        else{
            view.findViewById(R.id.poin1).setVisibility(View.GONE);
        }
        //预约订单
        if( order.getOrder_type() == 2 ){
            view.findViewById(R.id.poin2).setVisibility(View.VISIBLE);
            ((TextView) view.findViewById(R.id.poin2)).setText(" " + myContext.getResources().getString(R.string.label_prepare_order )
                    +" "+ order.getReserved_time()+" ");
        }
        else{
            view.findViewById(R.id.poin2).setVisibility(View.GONE);
        }

        if (order.getStatus() > 100) {
            if(order.getOrder_type() == 2 && order.getSt_reserved_order_at() == null){
                holder.nofinish_contentView.setText(myContext.getResources().getString(R.string.label_prepare_order));
            }
            holder.nofinishView.setVisibility(View.VISIBLE);
            view.findViewById(R.id.btn_status).setVisibility(View.GONE);
        }
        else{
            view.findViewById(R.id.btn_status).setVisibility(View.VISIBLE);
            holder.nofinishView.setVisibility(View.INVISIBLE);
        }
        holder.addressView.setText(order.getAddress());
        //  holder.distanceView.setText(Utils.getFloatFromDouble(Utils.getGeoDistance(UpdateStateServe.longitude,
        //         UpdateStateServe.latitude, order.getLongitude(), order.getLatitude()) / 1000));
        latitude= UpdateStateServe.latitude;
        longitude = UpdateStateServe.longitude;
        if(latitude == 0 && longitude == 0){
            getSPloAndla();
        }
        DistanceUtil distanceUtil =new DistanceUtil();
        distanceUtil.getDistance(latitude,longitude,
                order.getLatitude(),order.getLongitude(),myContext,holder.distanceView);


        if (order.getService_type() == 1) {
            holder.typeView.setText(R.string.label_service_type1);
            holder.typeView.setBackgroundResource(R.drawable.bg_shape_purle);

        } else if (order.getService_type() == 2) {
            holder.typeView.setText(R.string.label_service_type2);
            holder.typeView.setBackgroundResource(R.drawable.bg_shape_blue2);
        } else {
            holder.typeView.setText(R.string.label_service_type3);
            holder.typeView.setBackgroundResource(R.drawable.bg_shape_green2);
        }
        holder.statusView.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                status_infos(order,holder.statusView);
            }
        });
        holder.nofinishView.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                status_infos(order,holder.statusView);
            }
        });

    };
    public void getSPloAndla(){
        SharedPreferences sharedPreferences = myContext.getSharedPreferences("SaveLoAndLa",
                Context.MODE_PRIVATE);
        double la = Double.parseDouble(sharedPreferences.getString("latitude", "0.00"));
        double lo = Double.parseDouble(sharedPreferences.getString("longitude", "0.00"));
        String adddress = sharedPreferences.getString("address", "");
        Log.e(TAG, "spla:" + la + "---splo" + lo + "--address" + adddress);
        if (la != 0.00 && lo != 0.00) {
            latitude = la;
            longitude = lo;
        }
    }
    private void status_infos(final  Order order,final View v){
        if(!CherkInternet.cherkInternet(true, myActivity)){

        }
        else if (order.getStatus() != 100 && order.getStatus() > 0) {
            Intent intent = new Intent(myContext, OrderInfoActivity.class);
            intent.putExtra("id", order.getId());
            myContext.startActivity(intent);
            myActivity.overridePendingTransition(R.anim.slide_in_right, R.anim.activity_anim_default);
        } else {
            progressBar.setVisibility(View.VISIBLE);
            Map<String, Object> map = new HashMap<>();
            new NewHttpPutTask(myContext, new OnHttpRequestListener() {
                @Override
                public void onRequestCompleted(Object obj) {
                    Log.e(TAG, obj.toString());
                    progressBar.setVisibility(View.GONE);
                    try {
                        JSONObject jsonObject = new JSONObject(obj.toString());
                        if (!jsonObject.isNull("result")) {
                            int result = jsonObject.optInt("result");
                            if (result == 1) {
                                String clientId = sharedPreferences.getString("clientid", null);
                                if (clientId != null) {
                                    Map<String, Object> map = new HashMap<>();
                                    map.put("id", order.getId());
                                    map.put("getui_cid", clientId);
                                    map.put("status", "3");
                                    new NewHttpPostTask(myContext, new OnHttpRequestListener() {
                                        @Override
                                        public void onRequestCompleted(Object obj) {
                                            Log.e(TAG, obj.toString());
                                            try {
                                                JSONObject jsonObject = new JSONObject(obj.toString());
                                                if(jsonObject.getInt("result") == 1) {
                                                    sharedPreferences.edit().putInt("type", 0).apply();
                                                    mHandler.sendEmptyMessage(MSG_OK_SOUND_EFFECT);
                                                    if(order.getOrder_type() != 2) {
                                                        location = new Location();
                                                        location.deleteJWD(Constants.LOCATION_ARRIVE, myContext, 2);
                                                    }
                                                    Log.e(TAG,"------"+order.getStatus());
                                                    if(order.getStatus()==100){
                                                        UpdateStateServe.CallClick = 0;
                                                    }else{
                                                        UpdateStateServe.CallClick ++ ;
                                                    }
                                                    Intent intent = new Intent(myContext, OrderInfoActivity.class);
                                                    intent.putExtra("id", order.getId());
                                                    myActivity.startActivity(intent);
                                                }
                                                else{
                                                    Log.e(TAG,"eerror");
                                                }
                                            }catch (Exception e){
                                                e.printStackTrace();Log.e(TAG,"x" + e.toString());
                                            }
                                        }

                                        @Override
                                        public void onRequestFailed(Object obj) {

                                        }
                                    }).execute(Constants.USER_LOCATION_URL, map);
                                }


                            } else {
                                if (!jsonObject.isNull("msg")) {
                                    String msg = jsonObject.optString("msg");
                                    Toast.makeText(myContext, msg, Toast.LENGTH_SHORT).show();
                                    Animation shake = AnimationUtils.loadAnimation(myContext, R.anim.view_shake);
                                    v.startAnimation(shake);
                                    Vibrator vibrator = (Vibrator)myContext.getSystemService(myContext.VIBRATOR_SERVICE);
                                    vibrator.vibrate(new long[]{100, 200, 100, 400, 100, 600}, -1);
                                    if(msg.length() == 13){
                                        new GetOrdersTask().executeOnExecutor(Constants.LISTTHEADPOOL,
                                                           Constants.ORDERS_URL);
                                    }
                                    Log.e(TAG,"--------"+msg.length());
                                }
                            }
                            myActivity.overridePendingTransition(R.anim.slide_in_right, R.anim.activity_anim_default);

                        }

                    } catch (JSONException e) {
                    }
                }

                @Override
                public void onRequestFailed(Object obj) {

                }
            }).execute(String.format(Constants.ACCEPT_ORDER_URL, order.getId()), map);

        }
    }


    private class ViewHolder {
        TextView addressView;
        TextView distanceView;
        TextView statusView;
        TextView typeView;
        LinearLayout nofinishView;
        TextView nofinish_contentView;

    }

    private void playOkSoundEffect() {
        sp.play(okSoundId, 1, 1, 0, 0, 1.0f);
    }

    @Override
    public void onBackPressed() {
        super.onBackPressed();
        overridePendingTransition(0, R.anim.slide_out_right);
    }

    private BroadcastReceiver broadcastReceiver = new BroadcastReceiver() {

        @Override
        public void onReceive(Context context, Intent intent) {
           // new GetOrdersTask().executeOnExecutor(Constants.LISTTHEADPOOL,
             //           Constants.ORDERS_URL);
            Bundle bundle = intent.getExtras();
            switch (bundle.getInt(Consts.CMD_ACTION)) {
                case PushConsts.GET_MSG_DATA:
                    byte[] payload = bundle.getByteArray("payload");
                    if (payload != null) {
                        String data = new String(payload);
                        Order order;
                        try {
                            JSONObject jsonObject = new JSONObject(data);
                            int msg_type = jsonObject.optInt("msg_type");
                            String env = jsonObject.optString("env");
                            if (msg_type == 1 && !jsonObject.isNull("data") && (env.equals("dev")&&Constants.DEBUG)||(env.equals("prod")&&!Constants.DEBUG)) {
                                order = new Order(jsonObject.optJSONObject("data"));
                                order.setStatus(100);
                                for(Order order2:orders){
                                    if(order2.getId() == order.getId())
                                        return;
                                }
                                orders.add(order);
                                Log.e(TAG, order.toString());
                                adapter.notifyDataSetChanged();
                            }
                        } catch (JSONException e) {

                        }

                    }
                    break;
                default:
                    break;
            }

        }

    };
    @Override
    protected void onDestroy() {
        super.onDestroy();

    }

    @Override
    protected void onPause() {
        super.onPause();
        MobclickAgent.onPause(this);
    }
    @Override
    protected void onResume() {
        super.onResume();
        MobclickAgent.onResume(this);
    }

}
