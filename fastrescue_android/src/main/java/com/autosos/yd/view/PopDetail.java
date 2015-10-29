package com.autosos.yd.view;

import android.app.Activity;
import android.content.Context;
import android.os.AsyncTask;
import android.os.Bundle;
import android.util.Log;
import android.view.View;
import android.widget.ListView;
import android.widget.PopupWindow;
import android.widget.TextView;

import com.autosos.yd.Constants;
import com.autosos.yd.R;
import com.autosos.yd.adapter.ObjectBindAdapter;
import com.autosos.yd.model.Balance;
import com.autosos.yd.model.Fee;
import com.autosos.yd.model.Order;
import com.autosos.yd.util.JSONUtil;
import com.handmark.pulltorefresh.library.PullToRefreshListView;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import java.io.IOException;
import java.util.ArrayList;

/**
 * Created by Administrator on 2015/10/29.
 */
public class PopDetail extends Activity implements ObjectBindAdapter.ViewBinder<Balance>{
    public Context context;
    private ListView listView;
    private ObjectBindAdapter<Balance> adapter;
    private ArrayList<Balance> banlances;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        listView = (ListView)findViewById(R.id.list);
        adapter = new ObjectBindAdapter<>(context, banlances,
                R.layout.pop_item, this);
        listView.setAdapter(adapter);
        new GetAccountInfoTask().execute(String.format(Constants.GET_ORDER_FEE, 1910));

    }




    @Override
    public void setViewValue(View view, Balance balance, int position) {
        if (view.getTag() == null) {
            ViewHolder holder = new ViewHolder();
            holder.title = (TextView) view.findViewById(R.id.title);
            holder.fee = (TextView) view.findViewById(R.id.fee);
            view.setTag(holder);
        }
        ViewHolder holder = (ViewHolder) view.getTag();
        holder.title.setText(balance.getTitle());
        holder.fee.setText(balance.getFee());

    }



    private class ViewHolder {
        TextView title;
        TextView fee;

    }

    private class GetAccountInfoTask extends AsyncTask<String, Object, JSONArray> {

        @Override
        protected JSONArray doInBackground(String... params) {
            try {
                String jsonStr = JSONUtil.getStringFromUrl(context, params[0]);
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
        }
    }
}
