package com.autosos.yd.view;

import android.os.AsyncTask;
import android.os.Bundle;
import android.util.Log;
import android.view.View;
import android.widget.AdapterView;
import android.widget.ImageView;
import android.widget.ListView;
import android.widget.TextView;

import com.autosos.yd.Constants;
import com.autosos.yd.R;
import com.autosos.yd.adapter.ObjectBindAdapter;
import com.autosos.yd.model.Balance;

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
public class BillDetailsActivity extends AutososBackActivity implements PullToRefreshBase.OnRefreshListener<ListView>,
        ObjectBindAdapter.ViewBinder<Balance>,AdapterView.OnItemClickListener {

    private ArrayList<Balance> banlances;
    private PullToRefreshListView listView;
    private ObjectBindAdapter<Balance> adapter;
    private String smonth;
    private String syear;
    private View progressBar;


    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_month_details);
        listView = (PullToRefreshListView) findViewById(R.id.list);
        banlances = new ArrayList<>();
        adapter = new ObjectBindAdapter<>(this, banlances,
                R.layout.lastestlog_item, this);
        listView.setAdapter(adapter);
        listView.setOnRefreshListener(this);
        smonth = getIntent().getStringExtra("month");
        syear = getIntent().getStringExtra("syear");
        setTitle(smonth + "月账单");
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

            if (JSONUtil.isNetworkConnected(com.autosos.yd.view.BillDetailsActivity.this)) {
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
                String.format(Constants.GET_MONTH_BILL,2015,10 ));

    }

    @Override
    public void setViewValue(View view, Balance balance, int position) {
        if (view.getTag() == null) {
            ViewHolder holder = new ViewHolder();
            holder.tv_type = (TextView) view.findViewById(R.id.tv_type);
            holder.tv_time = (TextView) view.findViewById(R.id.tv_time);
            holder.tv_money = (TextView) view.findViewById(R.id.tv_money);
            view.setTag(holder);
        }
        ViewHolder holder = (ViewHolder) view.getTag();
        holder.tv_money.setText(balance.getAmount());
        holder.tv_time.setText(balance.getCreated_at());
        holder.tv_type.setText(balance.getRemark());
    }

    private class GetAccountInfoTask extends AsyncTask<String, Object, JSONArray> {

        @Override
        protected JSONArray doInBackground(String... params) {
            try {
                String jsonStr = JSONUtil.getStringFromUrl(BillDetailsActivity.this, params[0]);
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

    }

    @Override
    public void onRefresh(PullToRefreshBase<ListView> refreshView) {
        new GetAccountInfoTask().executeOnExecutor(Constants.INFOTHEADPOOL,
                String.format(Constants.GET_MONTH_BILL,syear,smonth ));


    }

    public void onBackPressed() {
        super.onBackPressed();
        overridePendingTransition(0, R.anim.slide_out_right);
    }

    private class ViewHolder {
        TextView tv_type;
        TextView tv_time;
        TextView tv_money;
    }

}
