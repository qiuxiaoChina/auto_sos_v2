package com.autosos.yd.view;

import android.content.Intent;
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
import com.autosos.yd.model.Bill;
import com.autosos.yd.util.JSONUtil;
import com.handmark.pulltorefresh.library.PullToRefreshBase;
import com.handmark.pulltorefresh.library.PullToRefreshListView;

import org.json.JSONArray;
import org.json.JSONException;

import java.io.IOException;
import java.util.ArrayList;

/**
 * Created by Administrator on 2015/10/19.
 */
public class AccountOfMonthActivity extends AutososBackActivity implements ObjectBindAdapter.ViewBinder<Bill>,
        AdapterView.OnItemClickListener, PullToRefreshBase.OnRefreshListener<ListView> {

    private PullToRefreshListView listView;
    private View progressBar;
    private ArrayList<Bill> bills;
    private boolean onLoading;
    private ObjectBindAdapter<Bill> adapter;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_account_month);
        listView = (PullToRefreshListView) findViewById(R.id.list);
        bills = new ArrayList<>();
        adapter = new ObjectBindAdapter<>(this, bills,
                R.layout.account_item, this);
        listView.setAdapter(adapter);
        listView.setOnRefreshListener(this);
//        progressBar = findViewById(R.id.include);
    }

    @Override
    protected void onResume() {
        super.onResume();
        new GetNoticeTask().execute(Constants.MONTH_BILL);
    }

    @Override
    public void setViewValue(View view, Bill bill, int position) {
        if (view.getTag() == null) {
            ViewHolder holder = new ViewHolder();
            holder.month = (TextView) view.findViewById(R.id.month);
            holder.tv_outgo = (TextView) view.findViewById(R.id.tv_outgo);
            holder.tv_in = (TextView) view.findViewById(R.id.tv_in);
            holder.rly_month_bill = (RelativeLayout) view.findViewById(R.id.rly_month_bill);
            view.setTag(holder);
        }
        ViewHolder holder = (ViewHolder) view.getTag();
        final String month = bill.getMonth();
        final String year = bill.getYear();
        Log.e("accountof","year === " +year);
        holder.month.setText(month);
        holder.tv_outgo.setText(bill.getOutgo());
        holder.tv_in.setText(bill.getIncome());
        holder.rly_month_bill.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Intent intent = new Intent(AccountOfMonthActivity.this, BillDetailsActivity.class);
                intent.putExtra("month",month);
                intent.putExtra("year",year);
                startActivity(intent);
                overridePendingTransition(R.anim.slide_in_right, R.anim.activity_anim_default);
            }
        });
    }

    private class ViewHolder {
        TextView month;
        TextView tv_outgo;
        TextView tv_in;
        RelativeLayout rly_month_bill;
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

            if (JSONUtil.isNetworkConnected(com.autosos.yd.view.AccountOfMonthActivity.this)) {
                textEmptyHint.setText(R.string.msg_bill_empty);
            } else {
                textEmptyHint.setText(R.string.msg_net_disconnected);
            }
        }
    }

    private class GetNoticeTask extends AsyncTask<String, Object, JSONArray> {

        @Override
        protected JSONArray doInBackground(String... params) {
            try {
                String jsonStr = JSONUtil.getStringFromUrl(AccountOfMonthActivity.this, params[0]);
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
//            onLoading = false;
//            setEmptyView();
            super.onPostExecute(result);
        }
    }






    public void onBackPressed() {
        super.onBackPressed();
        overridePendingTransition(0, R.anim.slide_out_right);
    }

    @Override
    public void onItemClick(AdapterView<?> parent, View view, int position, long id) {

    }

    @Override
    public void onRefresh(PullToRefreshBase<ListView> refreshView) {
        new GetNoticeTask().execute(Constants.MONTH_BILL);
    }

}
