package com.autosos.yd.view;

import android.app.Dialog;
import android.content.DialogInterface;
import android.content.Intent;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.graphics.Point;
import android.os.AsyncTask;
import android.os.Bundle;


import android.os.Handler;
import android.os.Message;
import android.os.StrictMode;
import android.util.Log;
import android.view.View;
import android.view.Window;
import android.view.WindowManager;
import android.widget.AdapterView;
import android.widget.Button;
import android.widget.CompoundButton;
import android.widget.ImageView;
import android.widget.ListView;
import android.widget.RelativeLayout;
import android.widget.ScrollView;
import android.widget.Switch;
import android.widget.TextView;

import com.autosos.yd.Constants;
import com.autosos.yd.R;
import com.autosos.yd.adapter.ObjectBindAdapter;
import com.autosos.yd.model.Balance;
import com.autosos.yd.model.LastestLog;
import com.autosos.yd.model.Notice;
import com.autosos.yd.util.JSONUtil;
import com.autosos.yd.util.UpdateStateServe;
import com.handmark.pulltorefresh.library.PullToRefreshBase;
import com.handmark.pulltorefresh.library.PullToRefreshListView;
import com.handmark.pulltorefresh.library.PullToRefreshScrollView;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import java.io.IOException;
import java.net.HttpURLConnection;
import java.net.URL;
import java.util.ArrayList;
import java.util.Timer;
import java.util.TimerTask;

/**
 * Created by Administrator on 2015/8/13.
 */
public class AccountActivity extends AutososBackActivity implements PullToRefreshBase.OnRefreshListener<ListView>,
        ObjectBindAdapter.ViewBinder<LastestLog>,AdapterView.OnItemClickListener{

    private String TAG = "AccountActivity";
    private PullToRefreshScrollView account_main;
    private TextView tv_balance;
    private Balance balance;
    private ArrayList<LastestLog> lastestLogs;
    private PullToRefreshListView listView;
    private ObjectBindAdapter<LastestLog> adapter;
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_account);
        tv_balance = (TextView) findViewById(R.id.tv_balance);
//        account_main = (PullToRefreshScrollView) findViewById(R.id.account_main);

//        account_main.setOnRefreshListener(this);
        listView = (PullToRefreshListView) findViewById(R.id.list);
        lastestLogs = new ArrayList<>();
        adapter = new ObjectBindAdapter<>(this, lastestLogs,
                R.layout.lastestlog_item, this);
        listView.setAdapter(adapter);
        listView.setOnRefreshListener(this);

        setBill();
        getBill().setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Intent intent = new Intent(AccountActivity.this, AccountOfMonthActivity.class);
                startActivity(intent);
                overridePendingTransition(R.anim.slide_in_right, R.anim.activity_anim_default);
            }
        });

    }

    @Override
    protected void onResume() {
        super.onResume();
        new GetAccountInfoTask().execute(Constants.GET_BALANCE);
    }

    @Override
    public void onItemClick(AdapterView<?> parent, View view, int position, long id) {

    }


    @Override
    public void onRefresh(PullToRefreshBase<ListView> refreshView) {
        new GetAccountInfoTask().execute(Constants.GET_BALANCE);
    }

    @Override
    public void setViewValue(View view, LastestLog lastestLog, int position) {
        if (view.getTag() == null) {
            ViewHolder holder = new ViewHolder();
            holder.tv_type = (TextView) view.findViewById(R.id.tv_type);
            holder.tv_time = (TextView) view.findViewById(R.id.tv_time);
            holder.tv_money = (TextView) view.findViewById(R.id.tv_money);
            view.setTag(holder);
        }
        ViewHolder holder = (ViewHolder) view.getTag();
        holder.tv_money.setText(lastestLog.getAmount());
        holder.tv_time.setText(lastestLog.getCreated_at());
        holder.tv_type.setText(lastestLog.getRemark());
    }

    private class ViewHolder {
        TextView tv_type;
        TextView tv_time;
        TextView tv_money;
    }

    //
    private class GetAccountInfoTask extends AsyncTask<String, Object, JSONObject> {

        @Override
        protected JSONObject doInBackground(String... params) {
            try {
                String jsonStr = JSONUtil.getStringFromUrl(AccountActivity.this, params[0] );
                if (JSONUtil.isEmpty(jsonStr)){
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
            super.onPostExecute(result);//前后不知道一不一样，先试试
//            account_main.onRefreshComplete();
            listView.onRefreshComplete();
            balance = new Balance(result);
            balance.getBalance();
            tv_balance.setText(balance.getBalance() + "");
            lastestLogs.clear();

            JSONArray jsonArray = balance.getLastest_log();
            if (jsonArray != null) {
                if (jsonArray.length() > 0) {
                    int size = jsonArray.length();
                    for (int i = 0; i < size; i++) {
                        LastestLog lastestLog = new LastestLog(jsonArray.optJSONObject(i));
                        lastestLogs.add(lastestLog);
                    }


                    adapter.notifyDataSetChanged();
                }
            }
        }
    }

    public void showDrawCashDialog(final int msg){
        View v2 = getLayoutInflater().inflate(R.layout.dialog_msg_content,
                null);
        final Dialog dialog = new Dialog(v2.getContext(), R.style.bubble_dialog);
        Button tvConfirm = (Button) v2.findViewById(R.id.btn_notice_confirm);
        TextView tvMsg = (TextView) v2.findViewById(R.id.tv_notice_msg);
        tvMsg.setText(String.format(getString(msg)));
        tvConfirm.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                dialog.dismiss();

            }
        });
        dialog.setContentView(v2);
        dialog.setCanceledOnTouchOutside(false);
        Window window = dialog.getWindow();
        WindowManager.LayoutParams params = window.getAttributes();
        Point point = JSONUtil.getDeviceSize(AccountActivity.this);
        params.width = Math.round(point.x * 5 / 7);
        window.setAttributes(params);
        dialog.show();
        dialog.setOnCancelListener(new DialogInterface.OnCancelListener() {
            @Override
            public void onCancel(DialogInterface dialog) {

            }
        });
    }

    public void drawCash(View view){
        Double limitMoney = Double.valueOf(1);
        Log.e("account", "limitMoney" + limitMoney);
        Double balance_long = Double.valueOf(balance.getBalance());
        Log.e("account", "balance_long" + balance_long);
        if (balance_long > limitMoney){
            Intent intent = new Intent(AccountActivity.this,DrawCashActivity.class);
            intent.putExtra("balance",balance.getBalance());
            startActivity(intent);
            overridePendingTransition(R.anim.slide_in_right, R.anim.activity_anim_default);
        }else {
            showDrawCashDialog(R.string.msg_enter_no_cash);
        }


    }

    public void onBackPressed() {
        super.onBackPressed();
        overridePendingTransition(0, R.anim.slide_out_right);
    }


}
