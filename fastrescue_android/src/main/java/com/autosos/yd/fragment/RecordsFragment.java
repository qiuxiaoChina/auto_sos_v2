package com.autosos.yd.fragment;

import android.content.Context;
import android.content.Intent;
import android.content.SharedPreferences;

import android.graphics.drawable.ColorDrawable;
import android.net.ConnectivityManager;
import android.net.NetworkInfo;
import android.os.AsyncTask;
import android.os.Bundle;
import android.os.Handler;
import android.os.Message;
import android.support.annotation.Nullable;
import android.support.v4.app.Fragment;
import android.support.v4.view.PagerAdapter;
import android.support.v4.view.ViewPager;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.view.WindowManager;
import android.view.animation.AccelerateInterpolator;
import android.widget.AbsListView;
import android.widget.AdapterView;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.ListView;
import android.widget.RelativeLayout;
import android.widget.Scroller;
import android.widget.TextView;
import android.widget.Toast;

import com.autosos.yd.model.FixedSpeedScroller;
import com.autosos.yd.widget.CherkInternet;
import com.handmark.pulltorefresh.library.PullToRefreshBase;
import com.handmark.pulltorefresh.library.PullToRefreshListView;

import org.json.JSONArray;
import org.json.JSONException;

import java.io.IOException;
import java.lang.reflect.Field;
import java.text.DateFormat;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Timer;
import java.util.TimerTask;

import com.autosos.yd.Constants;
import com.autosos.yd.R;
import com.autosos.yd.adapter.ObjectBindAdapter;
import com.autosos.yd.model.Record;
import com.autosos.yd.util.JSONUtil;
import com.autosos.yd.view.OrderInfoActivity;
import com.autosos.yd.view.RecordInfoActivity;


public class RecordsFragment extends Fragment implements ObjectBindAdapter.ViewBinder<Record>,
        AdapterView.OnItemClickListener, PullToRefreshBase.OnRefreshListener<ListView>,View.OnClickListener,
        AbsListView.OnScrollListener {
    private static final String TAG = "RecordsFragment";
    private PullToRefreshListView listView1;
    private PullToRefreshListView listView2;
    private ArrayList<Record> records1;
    private ArrayList<Record> records2;
    private ObjectBindAdapter<Record> adapter1;
    private ObjectBindAdapter<Record> adapter2;
    private View progressBar;
    private View loadView1;
    private View noMoreView1;
    private View loadView2;
    private View noMoreView2;
    private boolean isLoad1;
    private boolean isLoad2;
    private boolean noMore1;
    private boolean noMore2;
    private int currentPage1;
    private int currentPage2;
    private int guester;//1 is up ,2 is down
    Long last_order_id1;  //solve while the push order's number is 10
    Long last_order_id2;
    private SharedPreferences preferences;
    private View rootView;
    private int type;//1 mean list1,2mean list2
    private LinearLayout recordsView;
    private LinearLayout pushView;
    private View gray_viewView;
    private ViewPager pagerView;
    FixedSpeedScroller mScroller;
    private View footView1;
    private View footView2;
    private View record_image;
    public static com.autosos.yd.fragment.RecordsFragment newInstance() {
        return new com.autosos.yd.fragment.RecordsFragment();
    }

    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
    }

    @Nullable
    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        rootView = inflater.inflate(R.layout.fragment_orders, container, false);
        type = 2;
        guester = 1;
        noMore2 = false;
        noMore1 = false;
        last_order_id1 = last_order_id2 = (long)0;
        records1 = new ArrayList<>();
        records2 = new ArrayList<>();
        adapter1 = new ObjectBindAdapter<>(rootView.getContext(), records1,
                R.layout.record_item, this);
        adapter2 = new ObjectBindAdapter<>(rootView.getContext(), records2,
                R.layout.record_item, this);
        footView1 = getActivity().getLayoutInflater().inflate(R.layout.list_foot_no_more, null);
        footView2 = getActivity().getLayoutInflater().inflate(R.layout.list_foot_no_more2, null);
        recordsView = (LinearLayout) rootView.findViewById(R.id.records);
        pushView = (LinearLayout) rootView.findViewById(R.id.push);
        gray_viewView = rootView.findViewById(R.id.gray_view);
        recordsView.setOnClickListener(this);
        pushView.setOnClickListener(this);
        loadView1 = footView1.findViewById(R.id.loading);
        noMoreView1 = footView1.findViewById(R.id.no_more_hint);
        loadView2 = footView2.findViewById(R.id.loading2);
        noMoreView2 = footView2.findViewById(R.id.no_more_hint2);
        progressBar = rootView.findViewById(R.id.include);
        currentPage1 =1;
        currentPage2 = 1;
        initPaperView();
        if (records1.isEmpty() && !isLoad1 && !isLoad2 && records2.isEmpty()) {
            new GetRecordsTask(2).executeOnExecutor(Constants.LISTTHEADPOOL,
                    String.format(Constants.HISTORY_ORDERS_PUSH, currentPage2, 10));
            new GetRecordsTask(1).executeOnExecutor(Constants.LISTTHEADPOOL,
                    String.format(Constants.HISTORY_ORDERS_URL, currentPage1, 10));
        }
        preferences = getActivity().getSharedPreferences(Constants
                .PREF_FILE, Context.MODE_PRIVATE);

        return rootView;
    }

    private void initPaperView(){
        pagerView =(ViewPager) rootView.findViewById(R.id.viewpage);
        LayoutInflater mLi = LayoutInflater.from(rootView.getContext());
        View view1 = mLi.inflate(R.layout.fragment_orders_list1, null);
        View view2 = mLi.inflate(R.layout.fragment_orders_list2, null);
        listView1 = (PullToRefreshListView) view1.findViewById(R.id.list1);
        listView1.getRefreshableView().addFooterView(footView1);
        listView1.setOnScrollListener(com.autosos.yd.fragment.RecordsFragment.this);
        listView1.setOnItemClickListener(com.autosos.yd.fragment.RecordsFragment.this);
        listView1.setOnRefreshListener(com.autosos.yd.fragment.RecordsFragment.this);
        listView2 = (PullToRefreshListView) view2.findViewById(R.id.list2);
        listView2.getRefreshableView().addFooterView(footView2);
        listView2.setOnScrollListener(com.autosos.yd.fragment.RecordsFragment.this);
        listView2.setOnRefreshListener(com.autosos.yd.fragment.RecordsFragment.this);
        listView1.setAdapter(adapter1);
        listView2.setAdapter(adapter2);
        final ArrayList<View> views = new ArrayList<View>();
        views.add(view2);
        views.add(view1);
        PagerAdapter mPagerAdapter = new PagerAdapter() {

            @Override
            public boolean isViewFromObject(View arg0, Object arg1) {
                return arg0 == arg1;
            }

            @Override
            public int getCount() {
                return views.size();
            }

            @Override
            public void destroyItem(View container, int position, Object object) {
                ((ViewPager)container).removeView(views.get(position));
            }


            @Override
            public Object instantiateItem(View container, int position) {
                ((ViewPager)container).addView(views.get(position));
                return views.get(position);
            }
        };

        pagerView.setAdapter(mPagerAdapter);

        try {
            Field mField = ViewPager.class.getDeclaredField("mScroller");
            mField.setAccessible(true);
            mScroller = new FixedSpeedScroller(pagerView.getContext(),new AccelerateInterpolator());
            mField.set(pagerView, mScroller);
        } catch (Exception e) {
            e.printStackTrace();
        }

        pagerView.setOnPageChangeListener(new ViewPager.OnPageChangeListener() {
            @Override
            public void onPageScrolled(int position, float positionOffset, int positionOffsetPixels) {
               // if(positionOffsetPixels < 4){

             //   }else
              //      changeColor(type,positionOffsetPixels);
            }

            @Override
            public void onPageSelected(int position) {
                if(type == 1) {
                    type ++;
                    changeColor(type);
                }
                else {
                    type --;
                    changeColor(type);
                }
            }
            @Override
            public void onPageScrollStateChanged(int state) {

            }
        });


    }


    @Override
    public void onHiddenChanged(boolean hidden) {
        if(!hidden){
            if(CherkInternet.cherkInternet(rootView.getContext())){
                progressBar.setVisibility(View.VISIBLE);
                if (type == 1) {
                    if (!isLoad1) {
                        currentPage1 = 1;
                        guester =1 ;
                        new GetRecordsTask(1).executeOnExecutor(Constants.LISTTHEADPOOL,
                                String.format(Constants.HISTORY_ORDERS_URL, currentPage1, 10));
                    }
                } else if(type == 2){
                    if (!isLoad2) {
                        guester = 1;
                        currentPage2 = 1;
                        new GetRecordsTask(2).executeOnExecutor(Constants.LISTTHEADPOOL,
                                String.format(Constants.HISTORY_ORDERS_PUSH, currentPage2, 10));
                    }
                }
            }
        }
        super.onHiddenChanged(hidden);
    }

    @Override
    public void onPause() {
        super.onPause();
    }

    @Override
    public void onResume() {
        super.onResume();
    }

    @Override
    public void onClick(View v) {
        switch (v.getId()){
            case R.id.records:
                pagerView.setCurrentItem(1);
                mScroller.setmDuration(1 * 200);
                type = 1;
                changeColor(type);
                //onRefresh(listView1);
                break;
            case R.id.push:
                pagerView.setCurrentItem(0);
                mScroller.setmDuration(1 * 200);
                type =2;
               changeColor(type);
                // onRefresh(listView2);
                break;
        }
    }
    private void changeColor(int change_type){
        switch (change_type){
            case 1:
                ((TextView)rootView.findViewById(R.id.record_text)).setTextColor(getResources().getColor(R.color.color_orange));
                (rootView.findViewById(R.id.record_view)).setBackgroundColor(getResources().getColor(R.color.color_orange));
                (rootView.findViewById(R.id.record_view)).setVisibility(View.VISIBLE);
                ((TextView)rootView.findViewById(R.id.push_text)).setTextColor(getResources().getColor(R.color.color_black));
                (rootView.findViewById(R.id.push_view)).setVisibility(View.INVISIBLE);
                break;
            case 2:
                ((TextView)rootView.findViewById(R.id.record_text)).setTextColor(getResources().getColor(R.color.color_black));
                (rootView.findViewById(R.id.record_view)).setVisibility(View.INVISIBLE);
                ((TextView)rootView.findViewById(R.id.push_text)).setTextColor(getResources().getColor(R.color.color_orange));
                (rootView.findViewById(R.id.push_view)).setBackgroundColor(getResources().getColor(R.color.color_orange));
                (rootView.findViewById(R.id.push_view)).setVisibility(View.VISIBLE);

                break;
        }
    }
    private void changeColor(int change_type,int positionOffsetPixels){
        int wid =  ((WindowManager) rootView.getContext().getSystemService(Context.WINDOW_SERVICE)).getDefaultDisplay().getWidth();
        int al = positionOffsetPixels * 255 / wid ;
        al = 255 - al;
        Log.e(TAG,"al  :"+al);
        String color1,color2;
        if(al < 16)
             color1 ="0"+ Integer.toHexString(al)+"fe7f30";
        else
             color1 =""+ Integer.toHexString(al)+"fe7f30";
        if((255 - al) < 16)
             color2 ="0" +Integer.toHexString(255 - al)+"000000";
        else
             color2 ="" +Integer.toHexString(255 - al)+"000000";
       // Log.e(TAG,"colol 1 :"+Integer.parseInt("0x"+color1, 16));

        //((TextView)rootView.findViewById(R.id.push_text)).setTextColor(Integer.parseInt(color1, 16));
        //((TextView) rootView.findViewById(R.id.push_text)).setTextColor(Integer.parseInt(color1, 16));
        //(rootView.findViewById(R.id.push_view)).setBackgroundColor(Integer.parseInt(color1, 16));


        //  (rootView.findViewById(R.id.push_view)).setVisibility(View.VISIBLE);
             //   ((TextView)rootView.findViewById(R.id.record_text)).setTextColor(Integer.parseInt(color1, 16));
               // (rootView.findViewById(R.id.record_view)).setBackgroundColor(Integer.parseInt(color1 , 16));
    }

    private class GetRecordsTask extends AsyncTask<String, Object, JSONArray> {
        private int url;
        private GetRecordsTask(int url) {
            this.url = url;
            if(url == 1) {
                isLoad1 = true;
            }
            else
                isLoad2 = true;
        }

        @Override
        protected JSONArray doInBackground(String... params) {
            try {
                String jsonStr = JSONUtil.getStringFromUrl(getActivity().getApplicationContext(), params[0]);
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
//            Log.e(TAG,"result---"+result.toString());
//            aaLog.e(TAG,"type  :"+type);
            progressBar.setVisibility(View.GONE);
            gray_viewView.setVisibility(View.INVISIBLE);
            if(url == 1)
                listView1.onRefreshComplete();
            else
                listView2.onRefreshComplete();
            int size = 0;
            Record r = null;
            if(guester == 2 && result !=null)
                r = new Record(result.optJSONObject(result.length() - 1));
            if(cherkInternet(false) && guester == 2&&(((last_order_id2 - r.getId()) == 0 && url == 2)
                    ||((last_order_id1 - r.getId()) == 0 && url == 1))) {
                Log.e(TAG, "stop**********" + r.getId());
                if (url == 1) {
                    noMore1 = true;
                  //  noMoreView1.setVisibility(View.VISIBLE);
                }
                else {
                    isLoad2 = false;
                    noMore2 = true;
                    noMoreView2.setVisibility(View.VISIBLE);
                }
               // loadView.setVisibility(View.GONE);
                return;
            }
            else {
                if (result != null) {
                    if (result.length() > 0) {
                        if (url == 2 && currentPage2 == 1)
                            records2.clear();
                        else if (url == 1 && currentPage1 == 1)
                            records1.clear();
                        size = result.length();
                        if (size > 0) {
                            for (int i = 0; i < size; i++) {
                                Record record = new Record(result.optJSONObject(i));
                                if (url == 1)
                                    records1.add(record);
                                else
                                    records2.add(record);
                            }
                        }
                    }
                    if (url == 2 && result.length() > 0) {
                        last_order_id2 = records2.get(records2.size() - 1).getId();
                        adapter2.notifyDataSetChanged();
                    } else if(url == 1 && result.length() > 0){
                        last_order_id1 = records1.get(records1.size() - 1).getId();
                        adapter1.notifyDataSetChanged();
                    }
                }
                if (size < 10) {
                    if (url == 1) {
                        noMore1 = true;
                        noMoreView1.setVisibility(View.VISIBLE);
                        loadView1.setVisibility(View.GONE);
                    }
                    else {
                        noMore2 = true;
                        noMoreView2.setVisibility(View.VISIBLE);
                        loadView2.setVisibility(View.GONE);
                    }
                } else {
                    if (url == 1) {
                        noMoreView1.setVisibility(View.GONE);
                        noMore1 = false;
                    }
                    else {
                        noMore2 = false;
                        noMoreView2.setVisibility(View.GONE);
                    }
                   // loadView.setVisibility(View.GONE);
                }
                if ((records1.isEmpty() && url == 1)) {
                    // View footView = getActivity().getLayoutInflater().inflate(R.layout.list_foot_no_more, null);
                    View emptyView = listView1.getRefreshableView().getEmptyView();
                    if (emptyView == null) {
                        emptyView = getActivity().getLayoutInflater().inflate(R.layout.list_empty_view, null);
                        listView1.getRefreshableView().setEmptyView(emptyView);
                    }
                    ImageView imgEmptyHint = (ImageView) emptyView.findViewById(R.id
                            .img_empty_hint);
                    TextView textEmptyHint = (TextView) emptyView.findViewById(R.id.text_empty_hint);

                    imgEmptyHint.setVisibility(View.VISIBLE);
                    textEmptyHint.setVisibility(View.VISIBLE);

                    if (JSONUtil.isNetworkConnected(getActivity().getApplicationContext())) {
                        textEmptyHint.setText(R.string.msg_record_empty);
                    } else {
                        textEmptyHint.setText(R.string.msg_net_disconnected);
                    }
                }
                if((records2.isEmpty() && url == 2)){
                    View emptyView = listView2.getRefreshableView().getEmptyView();
                    if (emptyView == null) {
                        emptyView = getActivity().getLayoutInflater().inflate(R.layout.list_empty_view, null);
                        listView2.getRefreshableView().setEmptyView(emptyView);
                    }
                    ImageView imgEmptyHint = (ImageView) emptyView.findViewById(R.id
                            .img_empty_hint);
                    TextView textEmptyHint = (TextView) emptyView.findViewById(R.id.text_empty_hint);

                    imgEmptyHint.setVisibility(View.VISIBLE);
                    textEmptyHint.setVisibility(View.VISIBLE);

                    if (JSONUtil.isNetworkConnected(getActivity().getApplicationContext())) {
                        textEmptyHint.setText(R.string.msg_push_empty);
                    } else {
                        textEmptyHint.setText(R.string.msg_net_disconnected);
                    }
                }
            }
            if(url == 1)
                isLoad1 = false;
            else
                isLoad2 = false;
            super.onPostExecute(result);
        }
    }

    @Override
    public void onItemClick(AdapterView<?> adapterView, View view, int position, long id) {
        switch (type){
            case 1:
                Record record = (Record) adapterView.getAdapter().getItem(position);
                if (record != null) {
                    preferences.edit().putInt("type", 1).apply();
                    Intent intent;
                    if (record.getStatus() == 400 || record.getStatus() == 401 ||(record.getStatus() == 300 &&record.getIs_uploadpic() ==1) ) {
                        intent = new Intent(getActivity(), RecordInfoActivity.class);
                        intent.putExtra("id", record.getId());
                        intent.putExtra("state", record.getStatus());
                    } else {
                        intent = new Intent(getActivity(), OrderInfoActivity.class);
                        intent.putExtra("id", record.getId());
                    }
                    startActivity(intent);
                    getActivity().overridePendingTransition(R.anim.slide_in_right, R.anim.activity_anim_default);

                }
                break;
            case 2:
                break;
        }
    }

    @Override
    public void onScrollStateChanged(AbsListView absListView, int scrollState) {
        if(!cherkInternet(false)){

        }
        if(type == 2 && noMore2){
            loadView2.setVisibility(View.GONE);
        }
        {
            switch (scrollState) {
                case AbsListView.OnScrollListener.SCROLL_STATE_IDLE:
                    if (absListView.getLastVisiblePosition() >= (absListView.getCount() - 5)
                            ) {
                        if (type == 1 && !noMore1 && !isLoad1) {
                            listView1.getRefreshableView().removeFooterView(footView1);
                            listView1.getRefreshableView().addFooterView(footView1);
                            //loadView1.setVisibility(View.VISIBLE);
                            currentPage1++;
                            guester = 2;
                            new GetRecordsTask(1).executeOnExecutor(Constants.LISTTHEADPOOL,
                                    String.format(Constants.HISTORY_ORDERS_URL, currentPage1, 10));
                        } else if (type == 2 && !noMore2 && !isLoad2) {
                            listView2.getRefreshableView().removeFooterView(footView2);
                            listView2.getRefreshableView().addFooterView(footView2);
                            loadView2.setVisibility(View.VISIBLE);
                            currentPage2++;
                            if (currentPage2 < 5) {
                                guester = 2;
                                new GetRecordsTask(2).executeOnExecutor(Constants.LISTTHEADPOOL,
                                        String.format(Constants.HISTORY_ORDERS_PUSH, currentPage2, 10));
                            } else {
                                noMore2 = true;
                                noMoreView2.setVisibility(View.VISIBLE);
                            }
                        }
                    }
                    break;
            }
        }
    }

    @Override
    public void onScroll(AbsListView view, int firstVisibleItem, int visibleItemCount, int totalItemCount) {

    }

    @Override
    public void setViewValue(View view, final Record record, int position) {
            if (view.getTag() == null) {
                ViewHolder holder = new ViewHolder();
                holder.dateView = (TextView) view.findViewById(R.id.time);
                holder.statusDesView = (TextView) view.findViewById(R.id.status);
                holder.addressView = (TextView) view.findViewById(R.id.address);
                holder.typeView = (TextView) view.findViewById(R.id.type);
                holder.carNumberView = (TextView) view.findViewById(R.id.carNumber);
                holder.record_imageView = (ImageView) view.findViewById(R.id.record_image);
                view.setTag(holder);
            }
            ViewHolder holder = (ViewHolder) view.getTag();
            DateFormat dateFormat = new SimpleDateFormat(Constants.DATE_FORMAT_LONG);
             holder.dateView.setText(dateFormat.format(record.getCreated_at()));
            if(record.getStatus_desc() != null) {
                holder.statusDesView.setText(String.valueOf(record.getStatus_desc()));
                if (record.getStatus() == 400 || record.getStatus() == 401 ||( record.getStatus() == 300 && record.getIs_uploadpic() ==1)) {
                    holder.statusDesView.setTextColor(getResources().getColor(R.color.color_gray));
                } else {
                    holder.statusDesView.setTextColor(getResources().getColor(R.color.color_green));
                }
            }
            else {
                holder.record_imageView.setVisibility(View.GONE);
                holder.statusDesView.setText(R.string.label_order_pushed);
                holder.statusDesView.setTextColor(getResources().getColor(R.color.color_gray));
            }
            holder.carNumberView.setText(String.valueOf(record.getCar_number()));

            holder.addressView.setText(record.getAddress());
            if (record.getService_type() == 1) {
                holder.typeView.setText(R.string.label_service_type1);
                holder.typeView.setBackgroundResource(R.drawable.bg_shape_purle);
            } else if (record.getService_type() == 2) {
                holder.typeView.setText(R.string.label_service_type2);
                holder.typeView.setBackgroundResource(R.drawable.bg_shape_purle);
            } else if (record.getService_type() == 3){
                holder.typeView.setText(R.string.label_service_type3);
                holder.typeView.setBackgroundResource(R.drawable.bg_shape_purle);
            } else {
                holder.typeView.setText(R.string.label_service_type4);
                holder.typeView.setBackgroundResource(R.drawable.bg_shape_purle);
            }
    }

    private class ViewHolder {
        TextView dateView;
        TextView statusDesView;
        TextView addressView;
        TextView typeView;
        TextView carNumberView;
        ImageView record_imageView;
    }

    @Override
    public void onRefresh(PullToRefreshBase<ListView> refreshView) {
            if (type == 1) {
                if (!isLoad1) {
                    currentPage1 = 1;
                    guester =1 ;
                    new GetRecordsTask(1).executeOnExecutor(Constants.LISTTHEADPOOL,
                            String.format(Constants.HISTORY_ORDERS_URL, currentPage1, 10));
                }
            } else if(type == 2){
                if (!isLoad2) {
                    guester = 1;
                    currentPage2 = 1;
                    new GetRecordsTask(2).executeOnExecutor(Constants.LISTTHEADPOOL,
                            String.format(Constants.HISTORY_ORDERS_PUSH, currentPage2, 10));
                }
            }
        cherkInternet(true);

    }
    private boolean cherkInternet(boolean toast) {
        ConnectivityManager cwjManager = (ConnectivityManager) rootView.getContext().getSystemService(Context.CONNECTIVITY_SERVICE);
        if (cwjManager != null){
            NetworkInfo info = cwjManager.getActiveNetworkInfo();
            if (info != null && info.isConnected()){
                if (info.getState() == NetworkInfo.State.CONNECTED) {

                    return true;
                }
            }
        }
        if(toast)
            Toast.makeText(rootView.getContext(),R.string.msg_net_disconnected,Toast.LENGTH_SHORT).show();
        return false;
    }
}
