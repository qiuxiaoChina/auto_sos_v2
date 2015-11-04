package com.autosos.yd.view;

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
import android.widget.TextView;

import com.autosos.yd.Constants;
import com.autosos.yd.R;
import com.autosos.yd.model.Balance;
import com.autosos.yd.model.MoreDetail;
import com.autosos.yd.test.Utility;
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
    private PullToRefreshListView list;
    private MyAdapter myAdapter;
    BaseAdapter baseAdapter;
    private LayoutInflater inflater;
    private ArrayList<Balance> myList;
    private ArrayList<MoreDetail> myList2;
    private ArrayList<ArrayList> myList3;
    private int mposion;


    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_month_details);
        smonth = getIntent().getStringExtra("month");
        syear = getIntent().getStringExtra("year");
        setTitle(smonth + "月报表明细");
        progressBar = findViewById(R.id.include);
        list = (PullToRefreshListView) findViewById(R.id.list);
        list.setOnRefreshListener(this);
        myList = new ArrayList<>();
//        myList2 = new ArrayList<>();
        myList3 = new ArrayList<>();
        myAdapter = new MyAdapter();
        inflater = inflater.from(this);
        list.setAdapter(myAdapter);
        list.setOnItemClickListener(new AdapterView.OnItemClickListener() {
            @Override
            public void onItemClick(AdapterView<?> parent, View view, int position, long id) {
                Balance balance= myList.get(position-1);
                balance.expand = ! balance.expand;
                myAdapter.notifyDataSetChanged();
            }
        });

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
            return 0;
        }

        @Override
        public View getView(int position, View convertView, ViewGroup parent) {
            ViewHolder viewHolder = new ViewHolder();
            Balance balance = myList.get(position);
            mposion = position;
            if (convertView == null){
                convertView = inflater.inflate(R.layout.lastestlog_item2,null);
                viewHolder.tv_type = (TextView) convertView.findViewById(R.id.tv_type);
                viewHolder.tv_time = (TextView) convertView.findViewById(R.id.tv_time);
                viewHolder.tv_money = (TextView) convertView.findViewById(R.id.tv_money);
                viewHolder.minus = (TextView) convertView.findViewById(R.id.minus);
                viewHolder.list_test = (ListView) convertView.findViewById(R.id.list_test);

                baseAdapter = new BaseAdapter() {
                    @Override
                    public int getCount() {
                        Log.e("test","myList3.get(mposion).size() === " + myList3.get(mposion).size());
                        return myList3.get(mposion).size();

                    }

                    @Override
                    public Object getItem(int position) {
                        return null;
                    }

                    @Override
                    public long getItemId(int position) {
                        return 0;
                    }

                    @Override
                    public View getView(int position, View convertView, ViewGroup parent) {
                        ViewHolder2 viewHolder2 = new ViewHolder2();
                        if (convertView == null){
                            convertView = inflater.inflate(R.layout.pop_item,null);
                            viewHolder2.title = (TextView) convertView.findViewById(R.id.title);
                            viewHolder2.fee = (TextView) convertView.findViewById(R.id.fee);
                            convertView.setTag(viewHolder2);
                        }else {
                            viewHolder2 = (ViewHolder2) convertView.getTag();
                        }
                        MoreDetail detail = (MoreDetail) myList3.get(mposion).get(position);
                        viewHolder2.fee.setText(detail.getFee());
                        viewHolder2.title.setText(detail.getTitle());
                        return convertView;
                    }
                };
                viewHolder.list_test.setAdapter(baseAdapter);
                Utility.setListViewHeightBasedOnChildren(viewHolder.list_test);
                convertView.setTag(viewHolder);
            }else {
                viewHolder = (ViewHolder) convertView.getTag();
            }
            if (balance.expand){
                viewHolder.list_test.setVisibility(View.VISIBLE);
            }else {
                viewHolder.list_test.setVisibility(View.GONE);
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

    private class ViewHolder2 {
        TextView title;
        TextView fee;

    }

    private class ViewHolder {
        TextView tv_type;
        TextView tv_time;
        TextView tv_money;
        TextView minus;
        ListView list_test;
    }

    @Override
    public void onRefresh(PullToRefreshBase<ListView> refreshView) {
        new GetAccountInfoTask().executeOnExecutor(Constants.INFOTHEADPOOL,
                String.format(Constants.GET_MONTH_LIST,syear+smonth ));
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
                        myList.add(balance);
                        myList2 = new ArrayList();
                        Log.e("test2","balance.getDetail().length() === " + balance.getDetail().length());
                        for (int j = 0; j < balance.getDetail().length(); j++){
                            MoreDetail moreDetail = new MoreDetail(balance.getDetail().optJSONObject(j));
                            myList2.add(moreDetail);
                            Log.e("test", moreDetail.getFee() + "===" + moreDetail.getTitle());
                        }
                        myList3.add(myList2);

                    }
                    Log.e("test",myList3.size()+"");
                    myAdapter.notifyDataSetChanged();

                }
            }
            setEmptyView();
        }
    }

    public void onBackPressed() {
        super.onBackPressed();
        overridePendingTransition(0, R.anim.slide_out_right);
    }



}
