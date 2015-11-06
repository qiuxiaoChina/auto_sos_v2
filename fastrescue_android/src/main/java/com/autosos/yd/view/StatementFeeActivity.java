package com.autosos.yd.view;

import android.os.AsyncTask;
import android.os.Bundle;
import android.util.Log;
import android.view.View;
import android.widget.AdapterView;
import android.widget.ListView;
import android.widget.TextView;

import com.autosos.yd.Constants;
import com.autosos.yd.R;
import com.autosos.yd.adapter.ObjectBindAdapter;
import com.autosos.yd.model.Balance;
import com.autosos.yd.model.TuoChe;
import com.autosos.yd.util.JSONUtil;

import org.json.JSONArray;
import org.json.JSONException;

import java.io.IOException;
import java.util.ArrayList;

/**
 * Created by Administrator on 2015/11/6.
 */
public class StatementFeeActivity extends AutososBackActivity implements ObjectBindAdapter.ViewBinder<Balance>,
        AdapterView.OnItemClickListener {

    private ListView list_fee;
    private ObjectBindAdapter<Balance> adapter;
    private ArrayList<Balance> banlances;
    private int id;


    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.fee_activity);
        list_fee = (ListView) findViewById(R.id.list_fee);
        banlances = new ArrayList<>();
        adapter = new ObjectBindAdapter<>(this, banlances,
                R.layout.pop_item, this);
        list_fee.setAdapter(adapter);
        id = getIntent().getIntExtra("id",0);

    }

    @Override
    protected void onResume() {
        super.onResume();
        new GetAccountInfoTask().execute(String.format(Constants.GET_ORDER_FEE, id));
    }

    private class GetAccountInfoTask extends AsyncTask<String, Object, JSONArray> {

        @Override
        protected JSONArray doInBackground(String... params) {
            try {
                String jsonStr = JSONUtil.getStringFromUrl(StatementFeeActivity.this, params[0]);
                jsonStr.length();
                if (JSONUtil.isEmpty(jsonStr)){
                    return null;
                }
                Log.e("pop", jsonStr);
                return new JSONArray(jsonStr);
            } catch (IOException | JSONException e) {

            }
            return null;

        }

        @Override
        protected void onPostExecute(JSONArray result) {
            super.onPostExecute(result);//前后不知道一不一样，先试试


//            Log.e("pop","b === " + b.getTuoche());
            banlances.clear();
            if (result != null) {
                if (result.length() > 0) {
                    int size = result.length();
                    for (int i = 0; i < size; i++) {
                        Balance balance = new Balance(result.optJSONObject(i));
                        banlances.add(balance);
                    }
                    Balance b = new Balance(result.optJSONObject(0));
                    if (b.getTuoche() != null){
                        TuoChe tuoChe = new TuoChe(b.getTuoche());
                        Balance b1 = new Balance(result.optJSONObject(0));
                        Balance b2 = new Balance(result.optJSONObject(0));
                        Balance b3 = new Balance(result.optJSONObject(0));
                        Balance b4 = new Balance(result.optJSONObject(0));
                        b1.setTitle("拖车距离");
                        b1.setFee(tuoChe.getdistance());
                        b2.setTitle("起步价");
                        b2.setFee(tuoChe.getprice());
                        b3.setTitle("起步距离");
                        b3.setFee(tuoChe.getstarting_km());
                        b4.setTitle("每公里价格");
                        b4.setFee(tuoChe.getkm_price());
                        banlances.add(b1);
                        banlances.add(b2);
                        banlances.add(b3);
                        banlances.add(b4);
                    }

                    adapter.notifyDataSetChanged();
                }
            }
        }
    }



    @Override
    public void onItemClick(AdapterView<?> parent, View view, int position, long id) {

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
        holder.title.setText(balance.getTitle()+":");
        holder.fee.setText(balance.getFee());
    }

    private class ViewHolder {
        TextView title;
        TextView fee;

    }

}
