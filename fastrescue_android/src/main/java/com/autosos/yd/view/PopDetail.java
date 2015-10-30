package com.autosos.yd.view;

import android.app.Activity;
import android.content.Context;
import android.graphics.drawable.Drawable;
import android.os.AsyncTask;
import android.os.Bundle;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.view.WindowManager;
import android.widget.ListView;
import android.widget.PopupWindow;
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
 * Created by Administrator on 2015/10/29.
 */
public class PopDetail extends Activity implements ObjectBindAdapter.ViewBinder<Balance>{
    public Context mcontext;
    private ListView listView;
    private ObjectBindAdapter<Balance> adapter;
    private ArrayList<Balance> banlances;
    private Activity myActivity;
    public PopupWindow popWindow ;
    private View vPopWindow;

    public PopDetail(Context context,Activity activity){
        mcontext = context;
        myActivity = activity;
        LayoutInflater inflater = (LayoutInflater) mcontext.getSystemService(Context.LAYOUT_INFLATER_SERVICE);
        vPopWindow =null;
        vPopWindow  = inflater.inflate(R.layout.statement_pop_window, null);
        popWindow =new PopupWindow(vPopWindow,ViewGroup.LayoutParams.FILL_PARENT,ViewGroup.LayoutParams.WRAP_CONTENT,true);

    }

    public void showDetailPopwindow(View view,int id){
        Drawable drawable = mcontext.getResources().getDrawable(R.color.color_gray7);
        popWindow.setBackgroundDrawable(drawable);
        popWindow.setFocusable(true);
        final View finalVPopWindow = vPopWindow;
        popWindow.showAsDropDown(view);
        banlances = new ArrayList<>();
        listView = (ListView)vPopWindow.findViewById(R.id.list2);
        listView.setEnabled(false);
        adapter = new ObjectBindAdapter<>(mcontext, banlances,
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
        holder.title.setText(balance.getTitle()+":");
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
                String jsonStr = JSONUtil.getStringFromUrl(mcontext, params[0]);
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
//                        if (balance.getTuoche() != null){
//                            Log.e("pop","not null");
//                            for (int j = 0; j < balance.getTuoche().length(); j++) {
//                                Balance balance2 = new Balance(balance.getTuoche().optJSONObject(i));
//                                if (j == 0){
//                                    balance2.setTitle("拖车距离");
//                                    balance2.setFee(balance2.getdistance());
//                                }else if (j == 1){
//                                    balance2.setTitle("起步价");
//                                    balance2.setFee(balance2.getprice());
//                                }else if (j == 2){
//                                    balance2.setTitle("起步距离");
//                                    balance2.setFee(balance2.getstarting_km());
//                                }else {
//                                    balance2.setTitle("每公里价格");
//                                    balance2.setFee(balance2.getkm_price());
//                                }
//                                banlances.add(balance2);

//}
//        }
//        Log.e("pop"," null");