package com.autosos.rescue.fragment;


import android.content.Intent;
import android.os.Bundle;
import android.support.v4.app.Fragment;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ProgressBar;
import android.widget.RatingBar;
import android.widget.TextView;

import com.autosos.rescue.Constants;
import com.autosos.rescue.R;
import com.autosos.rescue.task.HttpGetTask;
import com.autosos.rescue.task.OnHttpRequestListener;
import com.autosos.rescue.util.JSONUtil;
import com.autosos.rescue.view.ChangePwdActivity;
import com.autosos.rescue.view.LoginActivity;
import com.autosos.rescue.view.TrailerDetailActivity;
import com.iflytek.thridparty.G;

import org.json.JSONException;
import org.json.JSONObject;


/**
 * A simple {@link Fragment} subclass.
 */
public class FragmentForMine extends Fragment implements View.OnClickListener {
    private static FragmentForMine fragment = null;
    public static Fragment newInstance() {
        if (fragment == null) {
            synchronized (FragmentForMine.class) {
                if (fragment == null) {
                    fragment = new FragmentForMine();

                }
            }
        }
        return fragment;
    }

    public FragmentForMine() {
        // Required empty public constructor
    }

    private View progressBar;
    private TextView username,companyName,rating,orderNum;
    private RatingBar ratingBar;
    private View changePwd,service_info;
    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        // Inflate the layout for this fragment
        View view = inflater.inflate(R.layout.fragment_fragment_for_mine, null);
        progressBar = view.findViewById(R.id.progressBar);

        username = (TextView) view.findViewById(R.id.username);
        rating = (TextView) view.findViewById(R.id.rating);
        companyName = (TextView) view.findViewById(R.id.companyName);
        orderNum =  (TextView) view.findViewById(R.id.orderNum);

        ratingBar = (RatingBar) view.findViewById(R.id.ratingBar);
        changePwd = view.findViewById(R.id.changePwd);
        changePwd.setOnClickListener(this);
        service_info = view.findViewById(R.id.service_info);
        service_info.setOnClickListener(this);
        return  view;
    }

    @Override
    public void onResume() {
        super.onResume();
        progressBar.setVisibility(View.VISIBLE);
        new HttpGetTask(getActivity().getApplicationContext(), new OnHttpRequestListener() {
            @Override
            public void onRequestCompleted(Object obj) {

                Log.d("userinfo",obj.toString());
                progressBar.setVisibility(View.GONE);
                JSONObject jsonObject;
                try {
                    jsonObject = new JSONObject(obj.toString());
                    String s_username = JSONUtil.getString(jsonObject,"username");
                    if(s_username!=null){
                        username.setText(s_username);
                    }
                    float f_rate = (float) jsonObject.optDouble("average",0.0);
                    ratingBar.setRating(f_rate);
                    rating.setText(f_rate+"分");
                    String s_company = JSONUtil.getString(jsonObject,"company_name");
                    companyName.setText(s_company);
                    int orders = jsonObject.optInt("total_accepted",0);
                    orderNum.setText(orders+"");
                } catch (JSONException e) {
                    e.printStackTrace();
                }
            }

            @Override
            public void onRequestFailed(Object obj) {

            }
        }).execute(Constants.USER_INFO);

    }

    @Override
    public void onClick(View v) {
        switch (v.getId()){

            case R.id.changePwd:
                Intent i = new Intent(getActivity().getApplicationContext(), ChangePwdActivity.class);
                startActivity(i);
                break;
            case R.id.service_info:
                Intent ii = new Intent(getActivity().getApplicationContext(), TrailerDetailActivity.class);
                startActivity(ii);
                break;
            default:
                break;
        }
    }
}
