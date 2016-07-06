package com.autosos.rescue.view;

import android.app.ActionBar;
import android.app.Activity;
import android.os.Bundle;
import android.text.Layout;
import android.util.DisplayMetrics;
import android.util.Log;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.ProgressBar;
import android.widget.RatingBar;
import android.widget.RelativeLayout;
import android.widget.TextView;

import com.autosos.rescue.Constants;
import com.autosos.rescue.R;
import com.autosos.rescue.model.OrderInfo;
import com.autosos.rescue.task.HttpGetTask;
import com.autosos.rescue.task.OnHttpRequestListener;
import com.umeng.analytics.MobclickAgent;

import org.json.JSONObject;

public class OrderInfoActivity extends Activity implements View.OnClickListener {

    private ImageView back_button;
    private TextView check_amount;
    private View afterPart, amount_detail;
    private boolean isClicked = false;
    private TextView pay_amount,base_price,more_amount,total_dis,bonus,night_price,edit_price,destination1,destination2;
    private RatingBar rating;
    private int orderId;
    private OrderInfo orderInfo;
    private ProgressBar progressBar;
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_order_info);
        orderId = getIntent().getIntExtra("id", 0);
      //  Log.d("orderinfo_activity", id + "");
        back_button = (ImageView) findViewById(R.id.back_button);
        back_button.setOnClickListener(this);
        check_amount = (TextView) findViewById(R.id.check_amount);
        check_amount.setOnClickListener(this);

        afterPart = findViewById(R.id.afterPart);
        amount_detail = findViewById(R.id.amount_detail);

        pay_amount = (TextView) findViewById(R.id.pay_amount);
        base_price = (TextView) findViewById(R.id.base_price);
        more_amount = (TextView) findViewById(R.id.more_amount);
        total_dis = (TextView) findViewById(R.id.distance);
        bonus = (TextView) findViewById(R.id.bonus);
        night_price = (TextView) findViewById(R.id.night_price);
        edit_price = (TextView) findViewById(R.id.edit_price);

        destination1 = (TextView) findViewById(R.id.destination1);
        destination2 = (TextView) findViewById(R.id.destination2);

        rating = (RatingBar) findViewById(R.id.rating);
        progressBar = (ProgressBar) findViewById(R.id.progressBar);
    }


    @Override
    protected void onResume() {
        super.onResume();
        progressBar.setVisibility(View.VISIBLE);
        new HttpGetTask(getApplicationContext(), new OnHttpRequestListener() {

            @Override
            public void onRequestCompleted(Object obj) {

                JSONObject jsonObject;
                try {
                    progressBar.setVisibility(View.GONE);
                    Log.d("orderInfo_activity", obj.toString());
                    jsonObject = new JSONObject(obj.toString());
                    orderInfo = new OrderInfo(jsonObject);
                    pay_amount.setText(orderInfo.getPay_amount()+"元");
                    more_amount.setText("+"+orderInfo.getMore_amount()+"元");
                    if (orderInfo.getDest() != null) {
                        destination1.setText(orderInfo.getAddress());
                        destination2.setText(orderInfo.getDest());
                    } else {

                        destination2.setText(orderInfo.getAddress());
                    }
                    if(orderInfo.getIs_support_free()==1){

                        base_price.setText("0.0元");

                    }else{

                        base_price.setText(orderInfo.getBase_price()+"元");
                    }
                    if(orderInfo.getBonus()==0){

                        bonus.setText("+0.0元");

                    }else{

                        bonus.setText("+"+orderInfo.getBonus()+"元");
                    }


                    if(orderInfo.getNight_price()==0){

                        night_price.setText("+0.0元");

                    }else{

                        night_price.setText("+"+orderInfo.getNight_price()+"元");
                    }

                    if(orderInfo.getEdit_price()  == 0){

                        edit_price.setText("+0.0元");

                    }else if(orderInfo.getEdit_price()>0){

                        edit_price.setText("+"+orderInfo.getEdit_price()+"元");
                    }else{

                        edit_price.setText(orderInfo.getEdit_price()+"元");
                    }
                    if(orderInfo.getServiceType()==1){

                        String dis = String.format("%.2f", orderInfo.getReal_tuoche_dis());
                        total_dis.setText(dis+"km");


                    }else {
                        Log.d("orderInfo_pay", orderInfo.getReal_dis()+"");
                        String dis = String.format("%.2f", orderInfo.getReal_dis());
                        total_dis.setText(dis+"km");
                    }

                    rating.setRating(Float.parseFloat(orderInfo.getCommentRate()));


                }catch (Exception e){

                    e.printStackTrace();
                }
            }

            @Override
            public void onRequestFailed(Object obj) {


            }

        }).execute(String.format(Constants.ORDER_INFO_URL, orderId));

        MobclickAgent.onResume(this);
    }

    @Override
    public void onClick(View v) {
        switch (v.getId()) {

            case R.id.back_button:
                finish();
                break;
            case R.id.check_amount:
                DisplayMetrics dm =getResources().getDisplayMetrics();
                float density = dm.density;
                if (!isClicked) {
                    check_amount.setText("-点击收起收费明细");
                    amount_detail.setVisibility(View.VISIBLE);
                    ViewGroup.MarginLayoutParams layoutParam = (ViewGroup.MarginLayoutParams) afterPart.getLayoutParams();
                    layoutParam.topMargin =(int)(350*density);
                    afterPart.setLayoutParams(layoutParam);
                    isClicked=true;
                } else {

                    check_amount.setText("+点击查看收费明细");
                    amount_detail.setVisibility(View.GONE);
                    ViewGroup.MarginLayoutParams layoutParam = (ViewGroup.MarginLayoutParams) afterPart.getLayoutParams();
                    layoutParam.topMargin = (int)(150*density);
                    afterPart.setLayoutParams(layoutParam);
                    isClicked=false;
                }
                break;
            default:
                break;
        }
    }
}
