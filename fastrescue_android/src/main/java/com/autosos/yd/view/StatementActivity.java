package com.autosos.yd.view;

import android.content.Intent;
import android.content.SharedPreferences;
import android.os.AsyncTask;
import android.os.Bundle;
import android.util.Log;
import android.view.View;
import android.widget.AdapterView;
import android.widget.ImageView;
import android.widget.ListView;
import android.widget.RelativeLayout;
import android.widget.TextView;

import com.autosos.yd.Constants;
import com.autosos.yd.R;
import com.autosos.yd.adapter.ObjectBindAdapter;
import com.autosos.yd.model.Balance;
import com.autosos.yd.model.BankInfo;
import com.autosos.yd.model.Bill;
import com.autosos.yd.util.JSONUtil;
import com.handmark.pulltorefresh.library.PullToRefreshBase;
import com.handmark.pulltorefresh.library.PullToRefreshListView;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import java.io.IOException;
import java.util.ArrayList;

/**
 * Created by Administrator on 2015/10/29.
 */
public class StatementActivity extends AutososBackActivity implements ObjectBindAdapter.ViewBinder<Bill>,
        AdapterView.OnItemClickListener, PullToRefreshBase.OnRefreshListener<ListView>{

    private String TAG = "StatementActivity";
    private Balance balance;
    private TextView tv_money;
    private PullToRefreshListView listView;
    private ArrayList<Bill> bills;
    private ObjectBindAdapter<Bill> adapter;
    private View empty;
    private View progressBar;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_statement);
        tv_money = (TextView) findViewById(R.id.tv_money);
        SharedPreferences sharedPreferences = getSharedPreferences("statement", this.MODE_PRIVATE);
        String mMoney = sharedPreferences.getString("income", "0.0");
        tv_money.setText(mMoney);
        listView = (PullToRefreshListView) findViewById(R.id.list);
        bills = new ArrayList<>();
        empty = findViewById(R.id.empty);
        progressBar = findViewById(R.id.progressBar);
        adapter = new ObjectBindAdapter<>(this, bills,
                R.layout.statement_item, this);
        listView.setAdapter(adapter);
        listView.setOnRefreshListener(this);

    }

    @Override
    protected void onResume() {
        super.onResume();
        new GetTotalAmountInfoTask().execute(Constants.GET_TOTAL_AMOUNT);
        new GetListTask().execute(Constants.GET_SETTLE_LIST);
    }

    @Override
    public void onItemClick(AdapterView<?> parent, View view, int position, long id) {

    }

    @Override
    public void onRefresh(PullToRefreshBase<ListView> refreshView) {
        new GetTotalAmountInfoTask().execute(Constants.GET_TOTAL_AMOUNT);
        new GetListTask().execute(Constants.GET_SETTLE_LIST);
    }

    @Override
    public void setViewValue(View view, Bill bill, int position) {
        if (view.getTag() == null) {
            ViewHolder holder = new ViewHolder();
            holder.month = (TextView) view.findViewById(R.id.month);
            holder.tv_status = (TextView) view.findViewById(R.id.tv_status);
            holder.tv_in = (TextView) view.findViewById(R.id.tv_outgo);
            holder.rly_month_bill = (RelativeLayout) view.findViewById(R.id.rly_month_bill);
            view.setTag(holder);
        }
        ViewHolder holder = (ViewHolder) view.getTag();
        final String month = bill.getMonth();
        final String year = bill.getYear();
        Log.e("accountof", "year === " + year);
        holder.month.setText(month);
        holder.tv_in.setText(bill.getAmount());
        if (bill.getStatus() == 0 ){
            holder.tv_status.setText("未结算");
            holder.tv_status.setTextColor(getResources().getColor(R.color.color_red2));
        }else {
            holder.tv_status.setText("已结算");
            holder.tv_status.setTextColor(getResources().getColor(R.color.color_green6));
        }
        holder.rly_month_bill.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Intent intent = new Intent(StatementActivity.this, StatementDetailsActivity.class);
                intent.putExtra("month",month);
                intent.putExtra("year",year);
                startActivity(intent);
                overridePendingTransition(R.anim.slide_in_right, R.anim.activity_anim_default);
            }
        });
    }

    private class ViewHolder {
        TextView month;
        TextView tv_status;
        TextView tv_in;
        RelativeLayout rly_month_bill;
    }

    private class GetListTask extends AsyncTask<String, Object, JSONArray> {

        @Override
        protected JSONArray doInBackground(String... params) {
            try {
                String jsonStr = JSONUtil.getStringFromUrl(StatementActivity.this, params[0]);
                if (JSONUtil.isEmpty(jsonStr)) {
                    return null;
                }
                Log.e("account", jsonStr);
                return new JSONArray(jsonStr);
            } catch (IOException | JSONException e) {
            }
            return null;
        }

        @Override
        protected void onPostExecute(JSONArray result) {
            listView.onRefreshComplete();
            progressBar.setVisibility(View.GONE);
            empty.setVisibility(View.GONE);
            bills.clear();
            if (result != null) {
                if (result.length() > 0) {
                    int size = result.length();
                    for (int i = 0; i < size; i++) {
                        Bill bill = new Bill(result.optJSONObject(i));
                        bills.add(bill);
                    }
                    adapter.notifyDataSetChanged();
                }
            }
            setEmptyView();

            super.onPostExecute(result);
        }
    }

    public void onBackPressed() {
        super.onBackPressed();
        overridePendingTransition(0, R.anim.slide_out_right);
    }

    private class GetTotalAmountInfoTask extends AsyncTask<String,Void,JSONObject> {

        @Override
        protected JSONObject doInBackground(String... params) {
            try {
                String jsonStr = JSONUtil.getStringFromUrl(StatementActivity.this, params[0]);
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
        protected void onPostExecute(JSONObject result) {
            super.onPostExecute(result);
            if (result != null){
                balance = new Balance(result);
                SharedPreferences sharedPreferences = getSharedPreferences("statement", StatementActivity.MODE_PRIVATE);
                SharedPreferences.Editor editor = sharedPreferences.edit();
                editor.putString("income", balance.getAmount());
                editor.commit();
                if (balance.getAmount() == null ){
                    tv_money.setText("0.00");
                }else {
                    tv_money.setText(balance.getAmount());
                }

            }

        }

    }

    public void setEmptyView() {
        if (bills.isEmpty()) {
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

            if (JSONUtil.isNetworkConnected(com.autosos.yd.view.StatementActivity.this)) {
                textEmptyHint.setText(R.string.msg_bill_empty);
            } else {
                textEmptyHint.setText(R.string.msg_net_disconnected);
            }
        }
    }
}
