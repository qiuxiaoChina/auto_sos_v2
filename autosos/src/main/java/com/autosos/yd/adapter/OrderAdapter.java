package com.autosos.yd.adapter;

import android.content.Context;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.BaseAdapter;
import android.widget.ListView;
import android.widget.TextView;

import com.autosos.yd.R;
import com.autosos.yd.model.OrderDetail;

import java.util.List;

/**
 * Created by Administrator on 2016/6/2.
 */
public class OrderAdapter extends BaseAdapter {

    private LayoutInflater layoutInflater;
    private List<OrderDetail> orderDetails;

    public OrderAdapter(Context context, List<OrderDetail> orderDetailList) {

        layoutInflater = LayoutInflater.from(context);
        orderDetails = orderDetailList;
    }

    public void onDateChange(List<OrderDetail> orderDetails) {
        this.orderDetails = orderDetails;
        this.notifyDataSetChanged();
    }

    @Override
    public int getCount() {
        return orderDetails.size();
    }

    @Override
    public Object getItem(int position) {
        return orderDetails.get(position);
    }

    @Override
    public long getItemId(int position) {
        return position;
    }

    @Override
    public View getView(int position, View convertView, ViewGroup parent) {
        ViewHolder viewHolder = null;
        if(convertView ==null){

            convertView = layoutInflater.inflate(R.layout.order_item,parent,false);
            viewHolder = new ViewHolder();
            viewHolder.mTile = (TextView) convertView.findViewById(R.id.title);
            viewHolder.mDesc = (TextView) convertView.findViewById(R.id.desc);
            convertView.setTag(viewHolder);
        }else{

           viewHolder = (ViewHolder) convertView.getTag();
        }

        OrderDetail od = orderDetails.get(position);
        viewHolder.mTile.setText(od.getTitle());
        viewHolder.mDesc.setText(od.getDesc());

        return convertView;
    }

    private class ViewHolder{

        TextView mTile;
        TextView mDesc;
    }
}
