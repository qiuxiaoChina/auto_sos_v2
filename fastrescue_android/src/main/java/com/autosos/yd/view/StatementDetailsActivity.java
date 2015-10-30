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
import android.widget.ImageView;
import android.widget.ListView;
import android.widget.PopupWindow;
import android.widget.RelativeLayout;
import android.widget.TextView;

import com.autosos.yd.Constants;
import com.autosos.yd.R;
import com.autosos.yd.adapter.ObjectBindAdapter;
import com.autosos.yd.model.Balance;
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
public class StatementDetailsActivity extends AutososBackActivity implements PullToRefreshBase.OnRefreshListener<ListView>,
        ObjectBindAdapter.ViewBinder<Balance>,AdapterView.OnItemClickListener {

    private ArrayList<Balance> banlances;
    private ArrayList<Balance> banlances2;
    private PullToRefreshListView listView;
    private ObjectBindAdapter<Balance> adapter;
    private ObjectBindAdapter<Balance> adapter2;
    private String smonth;
    private String syear;
    private View progressBar;
    public PopupWindow popWindow ;
    private ListView listView2;
    private static Context Context = null;



    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_month_details);
        listView = (PullToRefreshListView) findViewById(R.id.list);
        banlances = new ArrayList<>();
        banlances2 = new ArrayList<>();
        adapter = new ObjectBindAdapter<>(this, banlances,
                R.layout.lastestlog_item, this);
        adapter2 = new ObjectBindAdapter<>(this, banlances2,
                R.layout.pop_item, this);
        listView.setAdapter(adapter);
        listView.setOnItemClickListener(this);
        listView.setOnRefreshListener(this);
        smonth = getIntent().getStringExtra("month");
        syear = getIntent().getStringExtra("year");
        setTitle(smonth + "月报表明细");
        progressBar = findViewById(R.id.include);

    }




    public void setEmptyView() {
        if (banlances.isEmpty()) {
            View emptyView = listView.getRefreshableView().getEmptyView();
            if (emptyView == null) {
                emptyView = getLayoutInflater().inflate(R.layout.list_empty_view, null);
                listView.getRefreshableView().setEmptyView(emptyView);
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
        new GetAccountInfoTask2().execute(String.format(Constants.GET_ORDER_FEE, 1910));

    }

    @Override
    public void setViewValue(View view, Balance balance, int position) {
        if (view.getTag() == null) {
            ViewHolder holder = new ViewHolder();
            holder.tv_type = (TextView) view.findViewById(R.id.tv_type);
            holder.tv_time = (TextView) view.findViewById(R.id.tv_time);
            holder.tv_money = (TextView) view.findViewById(R.id.tv_money);
            holder.minus = (TextView) view.findViewById(R.id.minus);
            holder.rly_detail = (RelativeLayout) view.findViewById(R.id.rly_detail);
            view.setTag(holder);
        }
        ViewHolder holder = (ViewHolder) view.getTag();
        holder.tv_money.setText(balance.getSettle_fee());
        holder.tv_time.setText(balance.getCreated_at());
        holder.minus.setVisibility(View.VISIBLE);
//        holder.rly_detail.setOnClickListener(new View.OnClickListener() {
//            @Override
//            public void onClick(View v) {

//
//            }
//        });
        String serverType;
        if (balance.getService_type() == 1){
             serverType = "搭电";
        }else if (balance.getService_type() == 2){
             serverType = "换胎";
        }else {
             serverType = "拖车";
        }
        String all = "订单号"+balance.getOrder_id() +":" + serverType +"(" +balance.getCar_number() + ")";
        holder.tv_type.setText(all);
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
            listView.onRefreshComplete();

            progressBar.setVisibility(View.GONE);
            banlances.clear();
            if (result != null) {
                if (result.length() > 0) {
                    int size = result.length();
                    for (int i = 0; i < size; i++) {
                        Balance balance = new Balance(result.optJSONObject(i));
                        banlances.add(balance);
                    }
                    adapter.notifyDataSetChanged();
                }
            }
            setEmptyView();
        }
    }

    @Override
    public void onItemClick(AdapterView<?> parent, View view, int position, long id) {

//        Balance b = (Balance) parent.getAdapter().getItem(position);
//        Log.e("test","position === " + position +"     " +"id === " +id);
//        PopDetail popDetail = new PopDetail(getApplicationContext(),this);
//        popDetail.showDetailPopwindow(view,b.getOrder_id());
//        showDetailPopwindow(view);


    }

    public void showDetailPopwindow(View view){
        LayoutInflater inflater = (LayoutInflater) getSystemService(Context.LAYOUT_INFLATER_SERVICE);
        View vPopWindow =null;
        vPopWindow  = inflater.inflate(R.layout.statement_pop_window, null);
        popWindow =new PopupWindow(vPopWindow,ViewGroup.LayoutParams.FILL_PARENT, 150,true);
        Drawable drawable = getResources().getDrawable(R.color.color_gray7);
        popWindow.setBackgroundDrawable(drawable);
        popWindow.setFocusable(true);
        final View finalVPopWindow = vPopWindow;
        popWindow.showAsDropDown(view);
        listView2 = (ListView)findViewById(R.id.list2);

        listView2.setAdapter(adapter2);


    }

    private class GetAccountInfoTask2 extends AsyncTask<String, Object, JSONArray> {

        @Override
        protected JSONArray doInBackground(String... params) {
            try {
                String jsonStr = JSONUtil.getStringFromUrl(StatementDetailsActivity.this, params[0]);
                jsonStr.length();
                if (JSONUtil.isEmpty(jsonStr)){
                    return null;
                }
                Log.e("test", jsonStr);
                return new JSONArray(jsonStr);
            } catch (IOException | JSONException e) {

            }
            return null;

        }

        @Override
        protected void onPostExecute(JSONArray result) {
            super.onPostExecute(result);//前后不知道一不一样，先试试
            banlances2.clear();
            if (result != null) {
                if (result.length() > 0) {
                    int size = result.length();
                    for (int i = 0; i < size; i++) {
                        Balance balance = new Balance(result.optJSONObject(i));
                        banlances2.add(balance);
                    }
                    adapter2.notifyDataSetChanged();
                }
            }
        }
    }

    @Override
    public void onRefresh(PullToRefreshBase<ListView> refreshView) {
        new GetAccountInfoTask().executeOnExecutor(Constants.INFOTHEADPOOL,
                String.format(Constants.GET_MONTH_LIST,syear+smonth ));

    }

    public void onBackPressed() {
        super.onBackPressed();
        overridePendingTransition(0, R.anim.slide_out_right);
    }

    private class ViewHolder {
        TextView tv_type;
        TextView tv_time;
        TextView tv_money;
        TextView minus;
        RelativeLayout rly_detail;
    }

}
