package com.autosos.rescue.fragment;


import android.content.Intent;
import android.os.AsyncTask;
import android.os.Bundle;
import android.os.Handler;
import android.support.v4.app.Fragment;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.AbsListView;
import android.widget.AdapterView;
import android.widget.ImageView;
import android.widget.ListView;
import android.widget.ProgressBar;
import android.widget.TextView;


import com.autosos.rescue.Constants;
import com.autosos.rescue.R;
import com.autosos.rescue.adapter.ObjectBindAdapter;
import com.autosos.rescue.adapter.OrderAdapter;
import com.autosos.rescue.model.OrderDetail;
import com.autosos.rescue.model.OrderInfo;
import com.autosos.rescue.util.JSONUtil;
import com.autosos.rescue.view.OrderInfoActivity;
import com.autosos.rescue.widget.OrderListView;
import com.handmark.pulltorefresh.library.PullToRefreshBase;
import com.handmark.pulltorefresh.library.PullToRefreshListView;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import java.io.IOException;
import java.util.ArrayList;
import java.util.List;


/**
 * A simple {@link Fragment} subclass.
 */
public class FragmentForOrder extends Fragment implements ObjectBindAdapter.ViewBinder<OrderInfo>,
        AdapterView.OnItemClickListener, PullToRefreshBase.OnRefreshListener<ListView>, View.OnClickListener,
        AbsListView.OnScrollListener {

    private static FragmentForOrder fragment = null;

    public static Fragment newInstance() {
        if (fragment == null) {
            synchronized (FragmentForOrder.class) {
                if (fragment == null) {
                    fragment = new FragmentForOrder();

                }
            }
        }
        return fragment;
    }


    public FragmentForOrder() {
        // Required empty public constructor
    }

    private PullToRefreshListView lv_order;
//    private OrderListView orderList;
//    private List<OrderDetail> mDatas = null;
//    private OrderAdapter orderAdapter;

    private ObjectBindAdapter<OrderInfo> orderAdapter;
    private ArrayList<OrderInfo> orderList;
    private View footView;
    private View loadView;
    private View noMoreView;
    private ProgressBar progressBar;
    private boolean isLoad;
    private boolean noMore;
    private int currentPage;
    int last_order_id;
    private int guester;//1为上拉，2为下拉

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        // Inflate the layout for this fragment
        View view = inflater.inflate(R.layout.fragment_fragment_for_order, container, false);
        //initOrders();
        //initView(view);
        lv_order = (PullToRefreshListView) view.findViewById(R.id.lv_order);
        orderList = new ArrayList<>();
        orderAdapter = new ObjectBindAdapter<OrderInfo>(view.getContext(), orderList, R.layout.record_item, this);
        footView = getActivity().getLayoutInflater().inflate(R.layout.list_foot_no_more, null);
        loadView = footView.findViewById(R.id.loading);
        noMoreView = footView.findViewById(R.id.no_more_hint);
        progressBar = (ProgressBar) view.findViewById(R.id.progressBar);
        currentPage = 1;
        isLoad = false;
        noMore = false;
        last_order_id = 0;
        //lv_order.setMode(PullToRefreshBase.Mode.PULL_FROM_END);
        lv_order.getRefreshableView().addFooterView(footView);
        lv_order.setOnScrollListener(this);
        lv_order.setOnItemClickListener(this);
        lv_order.setOnRefreshListener(this);
        lv_order.setAdapter(orderAdapter);
        if (orderList.isEmpty() && !isLoad) {
            new GetRecordsTask(1).executeOnExecutor(Constants.LISTTHEADPOOL,
                    String.format(Constants.HISTORY_ORDERS_URL, currentPage, 10));
        }
        return view;
    }


    private class GetRecordsTask extends AsyncTask<String, Object, JSONArray> {
        private int url;

        private GetRecordsTask(int url) {
            this.url = url;
            if (url == 1) {
                isLoad = true;
            }

        }

        @Override
        protected JSONArray doInBackground(String... params) {
            try {
                String jsonStr = JSONUtil.getStringFromUrl(getActivity().getApplicationContext(), params[0]);
                if (JSONUtil.isEmpty(jsonStr)) {
                    return null;
                }

                return new JSONArray(jsonStr);
            } catch (IOException | JSONException e) {

            }
            return null;
        }

        @Override
        protected void onPostExecute(JSONArray result) {

//            aaLog.e(TAG,"type  :"+type);

            progressBar.setVisibility(View.GONE);

            if (url == 1)
                lv_order.onRefreshComplete();
            int size = 0;
            OrderInfo r = null;
            if(result !=null){
                r = new OrderInfo(result.optJSONObject(result.length() - 1));
            }

            if(((last_order_id  == r.getOrderId()) && url == 1&&(guester==2))) {
                Log.d("info_fresh", "error"+"---"+r.getOrderId());
                if (url == 1) {
                    noMore = true;
                    lv_order.onRefreshComplete();
                }
                return;
            }else {
                if (result != null) {
                    Log.d("info_fresh", "ok");
                    if (result.length() > 0) {
                        if (url == 1 && currentPage == 1)
                            orderList.clear();
                        size = result.length();
                        if (size > 0) {
                            for (int i = 0; i < size; i++) {
                                OrderInfo record = new OrderInfo(result.optJSONObject(i));
                                Log.d("info_fresh", result.optJSONObject(i).toString());
                                if (url == 1)
                                    orderList.add(record);

                            }
                        }
                    }
                    if (url == 1 && result.length() > 0) {
                        last_order_id = orderList.get(orderList.size() - 1).getId();
                        orderAdapter.notifyDataSetChanged();
                    }
                }

           }
            if (size < 10) {
                if (url == 1) {
                    noMore = true;
                    noMoreView.setVisibility(View.VISIBLE);
                    loadView.setVisibility(View.GONE);
                }

            } else {
                if (url == 1) {
                    noMoreView.setVisibility(View.GONE);
                    noMore = false;
                }

                // loadView.setVisibility(View.GONE);
            }
            if ((orderList.isEmpty() && url == 1)) {
                // View footView = getActivity().getLayoutInflater().inflate(R.layout.list_foot_no_more, null);
                View emptyView = lv_order.getRefreshableView().getEmptyView();
                if (emptyView == null) {
                    emptyView = getActivity().getLayoutInflater().inflate(R.layout.list_empty_view, null);
                    lv_order.getRefreshableView().setEmptyView(emptyView);
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


            if (url == 1)
                isLoad = false;

            super.onPostExecute(result);
        }
    }

    @Override
    public void setViewValue(View view, OrderInfo orderInfo, int position) {

        if (view.getTag() == null) {
            ViewHolder holder = new ViewHolder();
            holder.date = (TextView) view.findViewById(R.id.date);
            holder.order_status = (TextView) view.findViewById(R.id.order_status);
            holder.destination1 = (TextView) view.findViewById(R.id.destination1);
            holder.destination2 = (TextView) view.findViewById(R.id.destination2);
            holder.price = (TextView) view.findViewById(R.id.price);
            holder.service_type = (TextView) view.findViewById(R.id.service_type);
            holder.order_type = (TextView) view.findViewById(R.id.order_type);
            holder.car_number = (TextView) view.findViewById(R.id.carNumber);
            view.setTag(holder);
        }
        ViewHolder holder = (ViewHolder) view.getTag();

        holder.car_number.setText(orderInfo.getCar_number());
        holder.order_type.setText(orderInfo.getType());
        holder.service_type.setText(orderInfo.getService());
        holder.destination1.setText("");
        holder.destination2.setText("");
        if (orderInfo.getCar_dst_addr() != null) {
            holder.destination1.setText(orderInfo.getCar_addr());
            holder.destination2.setText(orderInfo.getCar_dst_addr());
        } else {

            holder.destination2.setText(orderInfo.getCar_addr());
        }

        if (orderInfo.getPrice() != null) {

            holder.price.setText(orderInfo.getPrice() + "元");
        }

        holder.date.setText(orderInfo.getDate());

        holder.order_status.setText(orderInfo.getStatus_desc());
    }

    private class ViewHolder {
        TextView date;
        TextView order_status;
        TextView destination1;
        TextView destination2;
        TextView price;
        TextView service_type;
        TextView order_type;
        TextView car_number;

    }

    @Override
    public void onClick(View v) {

    }

    @Override
    public void onItemClick(AdapterView<?> parent, View view, int position, long id) {


        OrderInfo record = (OrderInfo) parent.getAdapter().getItem(position);
        Log.d("orderinfo_activity", "ok");
        if (record != null) {
            Intent intent;
            intent = new Intent(getActivity().getApplicationContext(), OrderInfoActivity.class);
            intent.putExtra("id", record.getOrderId());
            startActivity(intent);
            getActivity().overridePendingTransition(R.anim.slide_in_right, R.anim.activity_anim_default);

        }

    }

    @Override
    public void onRefresh(PullToRefreshBase<ListView> refreshView) {
        if (!isLoad) {
            Log.d("action_refresh","onRefresh");
            currentPage =1;
            guester =1 ;
            new GetRecordsTask(1).executeOnExecutor(Constants.LISTTHEADPOOL,
                    String.format(Constants.HISTORY_ORDERS_URL, currentPage, 10));
        }
    }

    @Override
    public void onScrollStateChanged(AbsListView view, int scrollState) {

        switch (scrollState) {
            case AbsListView.OnScrollListener.SCROLL_STATE_IDLE:
                if (view.getLastVisiblePosition() >= (view.getCount() - 5)) {
                    if (!noMore && !isLoad) {
                        Log.d("action_refresh","onScrollStateChanged");
                        lv_order.getRefreshableView().removeFooterView(footView);
                        lv_order.getRefreshableView().addFooterView(footView);
                        noMoreView.setVisibility(View.GONE);
                        currentPage++;
                        guester = 2;
                        new GetRecordsTask(1).executeOnExecutor(Constants.LISTTHEADPOOL,
                                String.format(Constants.HISTORY_ORDERS_URL, currentPage, 10));
                    }
                }
                break;
        }

    }

    @Override
    public void onScroll(AbsListView view, int firstVisibleItem, int visibleItemCount, int totalItemCount) {

    }

//    OrderDetail od;
//
//    private void initOrders() {
//        mDatas = new ArrayList<OrderDetail>();
//
//        od = new OrderDetail("我的历史订单1", "拖车订单,目的地奉化大桥镇");
//        mDatas.add(od);
//        od = new OrderDetail("我的历史订单2", "拖车订单,目的地奉化大桥镇");
//        mDatas.add(od);
//        od = new OrderDetail("我的历史订单3", "拖车订单,目的地奉化大桥镇");
//        mDatas.add(od);
//        od = new OrderDetail("我的历史订单4", "拖车订单,目的地奉化大桥镇");
//        mDatas.add(od);
//        od = new OrderDetail("我的历史订单5", "拖车订单,目的地奉化大桥镇");
//        mDatas.add(od);
//        od = new OrderDetail("我的历史订单6", "拖车订单,目的地奉化大桥镇");
//        mDatas.add(od);
//        od = new OrderDetail("我的历史订单1", "拖车订单,目的地奉化大桥镇");
//        mDatas.add(od);
//        od = new OrderDetail("我的历史订单2", "拖车订单,目的地奉化大桥镇");
//        mDatas.add(od);
//        od = new OrderDetail("我的历史订单3", "拖车订单,目的地奉化大桥镇");
//        mDatas.add(od);
//        od = new OrderDetail("我的历史订单4", "拖车订单,目的地奉化大桥镇");
//        mDatas.add(od);
//        od = new OrderDetail("我的历史订单5", "拖车订单,目的地奉化大桥镇");
//        mDatas.add(od);
//        od = new OrderDetail("我的历史订单6", "拖车订单,目的地奉化大桥镇");
//        mDatas.add(od);
//        od = new OrderDetail("我的历史订单1", "拖车订单,目的地奉化大桥镇");
//        mDatas.add(od);
//        od = new OrderDetail("我的历史订单2", "拖车订单,目的地奉化大桥镇");
//        mDatas.add(od);
//        od = new OrderDetail("我的历史订单3", "拖车订单,目的地奉化大桥镇");
//        mDatas.add(od);
//        od = new OrderDetail("我的历史订单4", "拖车订单,目的地奉化大桥镇");
//        mDatas.add(od);
//        od = new OrderDetail("我的历史订单5", "拖车订单,目的地奉化大桥镇");
//        mDatas.add(od);
//        od = new OrderDetail("我的历史订单6", "拖车订单,目的地奉化大桥镇");
//        mDatas.add(od);
//
//        orderAdapter = new OrderAdapter(getActivity().getApplicationContext(),mDatas);
//    }
//
//    private void initView(View view) {
//
//        orderList = (OrderListView) view.findViewById(R.id.orderList);
//        orderList.setInterface(this);
//        orderList.setAdapter(orderAdapter);
//
//    }
//
//    private void myAction(){
//
//
//        for(int i =1 ;i<=5 ;i++){
//            od = new OrderDetail("我的历史订单"+i*2, "拖车订单,目的地奉化大桥镇");
//            mDatas.add(od);
//        }
//
//        orderAdapter.onDateChange(mDatas);
//        orderList.refreshComplete();
//
//    }
//
//    @Override
//    public void onRefresh() {
//
//        // TODO Auto-generated method stub\
//        Handler handler = new Handler();
//        handler.postDelayed(new Runnable() {
//
//            @Override
//            public void run() {
//               myAction();
//            }
//        }, 2000);
//    }
}
