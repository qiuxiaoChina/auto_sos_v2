package com.autosos.yd.view;

import android.app.Activity;
import android.content.Context;
import android.content.Intent;
import android.net.Uri;
import android.os.Bundle;
import android.util.Log;
import android.view.View;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.TextView;

import com.autosos.yd.Constants;
import com.autosos.yd.R;
import com.autosos.yd.model.RecordInfo;
import com.autosos.yd.task.AuthGetJSONObjectAsyncTask;
import com.autosos.yd.util.JSONUtil;
import com.autosos.yd.util.UpdateStateServe;

import org.json.JSONException;
import org.json.JSONObject;

import java.io.IOException;
import java.text.SimpleDateFormat;
import java.util.Date;

/**
 * Created by Administrator on 2015/8/18.
 */
public class FinishActivity extends AutososBackActivity {
    private long id;
    private View progressBar;
    private View finish_emptyView;
    private TextView nameView;
    private TextView phoneView;
    private TextView carNumberView;
    private TextView typeView;
    private TextView costView;
    private RecordInfo recordInfo;
    private ImageView typeImageView;
    private TextView info1_detailView;
    private TextView info2_detailView;
    private TextView info3_detailView;
    private TextView info4_detailView;
    private LinearLayout type_image_backgroundView;
    private final String TAG = "FinishActivity";
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_order_finish);
        setHomeButtonGone();
        id = getIntent().getLongExtra("id", 351);
        nameView = (TextView) findViewById(R.id.name);
        phoneView = (TextView) findViewById(R.id.phone);
        carNumberView = (TextView) findViewById(R.id.carNumber);
        typeView = (TextView) findViewById(R.id.type);
        costView = (TextView) findViewById(R.id.cost);
        progressBar = findViewById(R.id.finish_progress);
        finish_emptyView = findViewById(R.id.finish_empty_view);
        info1_detailView = (TextView)findViewById(R.id.info1_detail);
        info2_detailView = (TextView)findViewById(R.id.info2_detail);
        info3_detailView = (TextView)findViewById(R.id.info3_detail);
        info4_detailView = (TextView)findViewById(R.id.info4_detail);
        typeImageView = (ImageView)findViewById(R.id.type_image);
        type_image_backgroundView = (LinearLayout)findViewById(R.id.type_image_background);

        new GetRecordInfoTask(this).executeOnExecutor(Constants.INFOTHEADPOOL,
                String.format(Constants.HISTORY_ORDER_INFO_URL, id));
    }
    private class GetRecordInfoTask extends AuthGetJSONObjectAsyncTask {
        public GetRecordInfoTask(Context context) {
            super(context);
        }

        @Override
        protected JSONObject doInBackground(String... params) {
            try {
                String jsonStr = JSONUtil.getStringFromUrl(FinishActivity.this, params[0]);
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
            recordInfo = new RecordInfo(result);
            if (recordInfo != null) {
                progressBar.setVisibility(View.GONE);
                finish_emptyView.setVisibility(View.GONE);
                if((recordInfo.getRealname() == null ||recordInfo.getRealname().length() <2))
                    nameView.setText("车主");
                else
                    nameView.setText(recordInfo.getRealname());
                phoneView.setText(String.format(getString(R.string.label_phone_number),
                        recordInfo.getMobile()));
                carNumberView.setText(recordInfo.getCar_number());
                info1_detailView.setText(recordInfo.getAddress());
                long time = recordInfo.getArrive_submit_at();
                long now_time = System.currentTimeMillis() / 1000;
                SimpleDateFormat sdf = new SimpleDateFormat("yyyy-MM-dd HH:mm:ss");
                String date = sdf.format(new Date(time * 1000L));
                info2_detailView.setText(date);
                info3_detailView.setText((now_time - time)/60 + getResources().getString(R.string.label_minute));
                info4_detailView.setText(UpdateStateServe.address);
                setBackground(R.color.color_gray4);
                if (recordInfo.getService_type() == 1) {
                    typeView.setText(R.string.label_service_type1);
                    typeImageView.setImageResource(R.drawable.icon_service_type1);
                    type_image_backgroundView.setBackgroundResource(R.drawable.bg_oval_plue);
                    findViewById(R.id.telephone).setBackgroundResource(R.drawable.bg_oval_plue);
                    findViewById(R.id.finish).setBackgroundResource(R.drawable.bg_shape_second_plue);
                } else if (recordInfo.getService_type() == 2) {
                    typeView.setText(R.string.label_service_type2);
                    typeImageView.setImageResource(R.drawable.icon_service_type2);
                    type_image_backgroundView.setBackgroundResource(R.drawable.bg_oval_blue);
                    findViewById(R.id.telephone).setBackgroundResource(R.drawable.bg_oval_blue);
                    findViewById(R.id.finish).setBackgroundResource(R.drawable.bg_shape_second_blue);
                } else {
                    typeImageView.setImageResource(R.drawable.icon_service_type3);
                    type_image_backgroundView.setBackgroundResource(R.drawable.bg_oval_green);
                    typeView.setText(R.string.label_service_type3);
                    findViewById(R.id.telephone).setBackgroundResource(R.drawable.bg_oval_green);
                    findViewById(R.id.finish).setBackgroundResource(R.drawable.bg_shape_second_green);
                }
                costView.setText(String.valueOf(recordInfo.getTotal_amount()));
            }
            super.onPostExecute(result);
        }
    }
    public void onCall(View v){
        if (recordInfo != null) {
            if (!JSONUtil.isEmpty(recordInfo.getMobile())) {
                try {
                    Intent phoneIntent = new Intent(Intent.ACTION_DIAL,
                            Uri.parse("tel:" + recordInfo.getMobile().trim()));
                    startActivity(phoneIntent);
                } catch (Exception e) {
                }
            }
        }
    }
    public void finish(View v){
        Intent intent = new Intent();
        intent.setClass(FinishActivity.this, MainActivity.class);
        intent.setFlags(Intent.FLAG_ACTIVITY_CLEAR_TOP);
        startActivity(intent);
        finish();
        overridePendingTransition(0, R.anim.slide_out_right);
    }
    @Override
    public void onBackPressed() {
    }
}
