package com.autosos.yd.fragment;


import android.os.Bundle;
import android.os.Handler;
import android.support.v4.app.Fragment;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ListView;

import com.autosos.yd.R;
import com.autosos.yd.adapter.OrderAdapter;
import com.autosos.yd.model.OrderDetail;
import com.autosos.yd.widget.OrderListView;

import java.util.ArrayList;
import java.util.List;

/**
 * A simple {@link Fragment} subclass.
 */
public class FragmentForOrder extends Fragment implements OrderListView.IsRefreshing{

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

    private OrderListView orderList;
    private List<OrderDetail> mDatas = null;
    private OrderAdapter orderAdapter;


    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        // Inflate the layout for this fragment
        View view = inflater.inflate(R.layout.fragment_fragment_for_order, null);
        initOrders();
        initView(view);

        return view;
    }

    OrderDetail od;

    private void initOrders() {
        mDatas = new ArrayList<OrderDetail>();

        od = new OrderDetail("我的历史订单1", "拖车订单,目的地奉化大桥镇");
        mDatas.add(od);
        od = new OrderDetail("我的历史订单2", "拖车订单,目的地奉化大桥镇");
        mDatas.add(od);
        od = new OrderDetail("我的历史订单3", "拖车订单,目的地奉化大桥镇");
        mDatas.add(od);
        od = new OrderDetail("我的历史订单4", "拖车订单,目的地奉化大桥镇");
        mDatas.add(od);
        od = new OrderDetail("我的历史订单5", "拖车订单,目的地奉化大桥镇");
        mDatas.add(od);
        od = new OrderDetail("我的历史订单6", "拖车订单,目的地奉化大桥镇");
        mDatas.add(od);
        od = new OrderDetail("我的历史订单1", "拖车订单,目的地奉化大桥镇");
        mDatas.add(od);
        od = new OrderDetail("我的历史订单2", "拖车订单,目的地奉化大桥镇");
        mDatas.add(od);
        od = new OrderDetail("我的历史订单3", "拖车订单,目的地奉化大桥镇");
        mDatas.add(od);
        od = new OrderDetail("我的历史订单4", "拖车订单,目的地奉化大桥镇");
        mDatas.add(od);
        od = new OrderDetail("我的历史订单5", "拖车订单,目的地奉化大桥镇");
        mDatas.add(od);
        od = new OrderDetail("我的历史订单6", "拖车订单,目的地奉化大桥镇");
        mDatas.add(od);
        od = new OrderDetail("我的历史订单1", "拖车订单,目的地奉化大桥镇");
        mDatas.add(od);
        od = new OrderDetail("我的历史订单2", "拖车订单,目的地奉化大桥镇");
        mDatas.add(od);
        od = new OrderDetail("我的历史订单3", "拖车订单,目的地奉化大桥镇");
        mDatas.add(od);
        od = new OrderDetail("我的历史订单4", "拖车订单,目的地奉化大桥镇");
        mDatas.add(od);
        od = new OrderDetail("我的历史订单5", "拖车订单,目的地奉化大桥镇");
        mDatas.add(od);
        od = new OrderDetail("我的历史订单6", "拖车订单,目的地奉化大桥镇");
        mDatas.add(od);

        orderAdapter = new OrderAdapter(getActivity().getApplicationContext(),mDatas);
    }

    private void initView(View view) {

        orderList = (OrderListView) view.findViewById(R.id.orderList);
        orderList.setInterface(this);
        orderList.setAdapter(orderAdapter);

    }

    private void myAction(){


        for(int i =1 ;i<=5 ;i++){
            od = new OrderDetail("我的历史订单"+i*2, "拖车订单,目的地奉化大桥镇");
            mDatas.add(od);
        }

        orderAdapter.onDateChange(mDatas);
        orderList.refreshComplete();

    }

    @Override
    public void onRefresh() {

        // TODO Auto-generated method stub\
        Handler handler = new Handler();
        handler.postDelayed(new Runnable() {

            @Override
            public void run() {
               myAction();
            }
        }, 2000);
    }
}
