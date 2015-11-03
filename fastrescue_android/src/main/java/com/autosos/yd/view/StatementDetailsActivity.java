package com.autosos.yd.view;

import android.content.Context;
import android.graphics.drawable.Drawable;
import android.os.AsyncTask;
import android.os.Bundle;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.AdapterView;
import android.widget.BaseAdapter;
import android.widget.ImageView;
import android.widget.ListView;
import android.widget.PopupWindow;
import android.widget.RelativeLayout;
import android.widget.SimpleAdapter;
import android.widget.TextView;

import com.autosos.yd.Constants;
import com.autosos.yd.R;
import com.autosos.yd.adapter.ObjectBindAdapter;
import com.autosos.yd.model.Balance;
import com.autosos.yd.model.Detail;
import com.autosos.yd.model.Fee;
import com.autosos.yd.util.JSONUtil;
import com.handmark.pulltorefresh.library.PullToRefreshBase;
import com.handmark.pulltorefresh.library.PullToRefreshListView;

import org.json.JSONArray;
import org.json.JSONException;

import java.io.IOException;
import java.util.ArrayList;

/**
 * Created by Administrator on 2015/10/26.
 */
public class StatementDetailsActivity extends AutososBackActivity implements PullToRefreshBase.OnRefreshListener<ListView>{

    private String smonth;
    private String syear;
    private View progressBar;
    public PopupWindow popWindow ;
    private ArrayList<Balance> myList;
    private ArrayList<Detail> myList2;
    private PullToRefreshListView list;
    private ListView list2;
    private LayoutInflater inflater;
    private MyAdapter myAdapter;
    private Detail dd;


    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_month_details);
        smonth = getIntent().getStringExtra("month");
        syear = getIntent().getStringExtra("year");
        setTitle(smonth + "月报表明细");
        myList = new ArrayList<Balance>();
        myList2 = new ArrayList<Detail>();
        list = (PullToRefreshListView) findViewById(R.id.list);

        inflater = LayoutInflater.from(this);
        myAdapter = new MyAdapter();
        list.setAdapter(myAdapter);
        list.setOnRefreshListener(this);
        progressBar = findViewById(R.id.include);
        list.setOnItemClickListener(new AdapterView.OnItemClickListener() {
            @Override
            public void onItemClick(AdapterView<?> parent, View view, int position, long id) {
                Balance balance= myList.get(position);

            }
        });


    }

    @Override
    public void onRefresh(PullToRefreshBase<ListView> refreshView) {
        new GetAccountInfoTask().executeOnExecutor(Constants.INFOTHEADPOOL,
                String.format(Constants.GET_MONTH_LIST,syear+smonth ));
    }


    private class MyAdapter extends BaseAdapter{

        @Override
        public int getCount() {
            return myList.size();
        }

        @Override
        public Object getItem(int position) {
            return myList.get(position);
        }

        @Override
        public long getItemId(int position) {
            return position;
        }

        @Override
        public View getView(int position, View convertView, ViewGroup parent) {
            ViewHolder viewHolder = new ViewHolder();
            if (convertView == null){
                convertView = inflater.inflate(R.layout.lastestlog_item,null);
                viewHolder.tv_type = (TextView) convertView.findViewById(R.id.tv_type);
                viewHolder.tv_time = (TextView) convertView.findViewById(R.id.tv_time);
                viewHolder.tv_money = (TextView) convertView.findViewById(R.id.tv_money);
                viewHolder.minus = (TextView) convertView.findViewById(R.id.minus);
                viewHolder.rly_detail = (RelativeLayout) convertView.findViewById(R.id.rly_detail);
                ListView list2 = (ListView) convertView.findViewById(R.id.list_test);
                list2.setAdapter(new BaseAdapter() {
                    @Override
                    public int getCount() {
                        return myList2.size();
                    }

                    @Override
                    public Object getItem(int position) {
                        return myList2.get(position);
                    }

                    @Override
                    public long getItemId(int position) {
                        return position;
                    }

                    @Override
                    public View getView(int position, View convertView, ViewGroup parent) {
                        ViewHolder2 viewHolder2 = new ViewHolder2();
                        dd = myList2.get(position);
                        if (convertView == null){
                            viewHolder2.title = (TextView) convertView.findViewById(R.id.title);
                            viewHolder2.fee = (TextView) convertView.findViewById(R.id.fee);
                            convertView.setTag(viewHolder2);
                        }else {
                            viewHolder2 = (ViewHolder2) convertView.getTag();
                        }
                        viewHolder2.fee.setText(myList2.get(position).getFee());
                        viewHolder2.title.setText(myList2.get(position).getTitle());
                        return convertView;
                    }
                });
                viewHolder.list2 = (ListView) convertView.findViewById(R.id.list2);
                convertView.setTag(viewHolder);
            }else {
                viewHolder = (ViewHolder) convertView.getTag();
            }
            if (dd.expand){
                viewHolder.list2.setVisibility(View.VISIBLE);
            }else {
                viewHolder.list2.setVisibility(View.GONE);
            }
            String serverType;
            if (myList.get(position).getService_type() == 1){
                 serverType = "搭电";
            }else if (myList.get(position).getService_type() == 2){
                 serverType = "换胎";
            }else {
                 serverType = "拖车";
            }
            String all = "订单号"+myList.get(position).getOrder_id() +":" + serverType +"(" +myList.get(position).getCar_number() + ")";
            viewHolder.tv_type.setText(all);
            viewHolder.tv_money.setText(myList.get(position).getSettle_fee());
            viewHolder.tv_time.setText(myList.get(position).getCreated_at());
            viewHolder.minus.setVisibility(View.VISIBLE);
            return convertView;
        }
    }

    private class ViewHolder {
        TextView tv_type;
        TextView tv_time;
        TextView tv_money;
        TextView minus;
        RelativeLayout rly_detail;
        ListView list2;
    }

    private class ViewHolder2 {
        TextView title;
        TextView fee;
    }

    public void setEmptyView() {
        if (myList.isEmpty()) {
            View emptyView = list.getRefreshableView().getEmptyView();
            if (emptyView == null) {
                emptyView = getLayoutInflater().inflate(R.layout.list_empty_view, null);
                list.getRefreshableView().setEmptyView(emptyView);
            }
            ImageView imgEmptyHint = (ImageView) emptyView.findViewById(R.id
                    .img_empty_hint);
            TextView textEmptyHint = (TextView) emptyView.findViewById(R.id.text_empty_hint);

            imgEmptyHint.setVisibility(View.VISIBLE);
            textEmptyHint.setVisibility(View.VISIBLE);

            if (JSONUtil.isNetworkConnected(StatementDetailsActivity.this)) {
                textEmptyHint.setText(R.string.msg_bill_empty);
            } else {
                textEmptyHint.setText(R.string.msg_net_disconnected);
            }
        }
    }


    @Override
    protected void onResume() {
        super.onResume();
        new GetAccountInfoTask().executeOnExecutor(Constants.INFOTHEADPOOL,
                String.format(Constants.GET_MONTH_LIST, syear + smonth));
//        new GetAccountInfoTask2().execute(String.format(Constants.GET_ORDER_FEE, 1910));

    }

    private class GetAccountInfoTask extends AsyncTask<String, Object, JSONArray> {

        @Override
        protected JSONArray doInBackground(String... params) {
            try {
                String jsonStr = JSONUtil.getStringFromUrl(StatementDetailsActivity.this, params[0]);
                if (JSONUtil.isEmpty(jsonStr)){
                    return null;
                }
                Log.e("Bill", jsonStr);
                return new JSONArray(jsonStr);
            } catch (IOException | JSONException e) {

            }
            return null;
        }

        @Override
        protected void onPostExecute(JSONArray result) {
            super.onPostExecute(result);//前后不知道一不一样，先试试
            list.onRefreshComplete();
            progressBar.setVisibility(View.GONE);
            myList.clear();
            if (result != null) {
                if (result.length() > 0) {
                    int size = result.length();
                    for (int i = 0; i < size; i++) {
                        Balance balance = new Balance(result.optJSONObject(i));
                        for (int j = 0; j < balance.getDetail().length(); j++){
                            Detail detail = new Detail(balance.getDetail().optJSONObject(j));
                            myList2.add(detail);
                        }
                        myList.add(balance);
                    }
                    myAdapter.notifyDataSetChanged();
                }
            }
            setEmptyView();
        }
    }

//    @Override
//    public void onItemClick(AdapterView<?> parent, View view, int position, long id) {

//        Balance b = (Balance) parent.getAdapter().getItem(position);
//        Log.e("test","position === " + position +"     " +"id === " +id);
//        PopDetail popDetail = new PopDetail(getApplicationContext(),this);
//        popDetail.showDetailPopwindow(view,b.getOrder_id());
//        showDetailPopwindow(view);

//        view.getLayoutParams();


//    }

//    public void showDetailPopwindow(View view){
//        LayoutInflater inflater = (LayoutInflater) getSystemService(Context.LAYOUT_INFLATER_SERVICE);
//        View vPopWindow =null;
//        vPopWindow  = inflater.inflate(R.layout.statement_pop_window, null);
//        popWindow =new PopupWindow(vPopWindow,ViewGroup.LayoutParams.FILL_PARENT, 150,true);
//        Drawable drawable = getResources().getDrawable(R.color.color_gray7);
//        popWindow.setBackgroundDrawable(drawable);
//        popWindow.setFocusable(true);
//        final View finalVPopWindow = vPopWindow;
//        popWindow.showAsDropDown(view);
//        listView2 = (ListView)findViewById(R.id.list2);
//
//        listView2.setAdapter(adapter2);
//
//
//    }

//    private class GetAccountInfoTask2 extends AsyncTask<String, Object, JSONArray> {
//
//        @Override
//        protected JSONArray doInBackground(String... params) {
//            try {
//                String jsonStr = JSONUtil.getStringFromUrl(StatementDetailsActivity.this, params[0]);
//                jsonStr.length();
//                if (JSONUtil.isEmpty(jsonStr)){
//                    return null;
//                }
//                Log.e("test", jsonStr);
//                return new JSONArray(jsonStr);
//            } catch (IOException | JSONException e) {
//
//            }
//            return null;
//
//        }
//
//        @Override
//        protected void onPostExecute(JSONArray result) {
//            super.onPostExecute(result);//前后不知道一不一样，先试试
//            banlances2.clear();
//            if (result != null) {
//                if (result.length() > 0) {
//                    int size = result.length();
//                    for (int i = 0; i < size; i++) {
//                        Balance balance = new Balance(result.optJSONObject(i));
//                        banlances2.add(balance);
//                    }
//                    adapter2.notifyDataSetChanged();
//                }
//            }
//        }
//    }

//    @Override
//    public void onRefresh(PullToRefreshBase<ListView> refreshView) {
//        new GetAccountInfoTask().executeOnExecutor(Constants.INFOTHEADPOOL,
//                String.format(Constants.GET_MONTH_LIST,syear+smonth ));
//
//    }

    public void onBackPressed() {
        super.onBackPressed();
        overridePendingTransition(0, R.anim.slide_out_right);
    }



}
