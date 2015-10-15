package com.autosos.yd.view;

import android.app.Dialog;
import android.content.Context;
import android.content.Intent;
import android.graphics.Point;
import android.net.Uri;
import android.os.Bundle;
import android.util.Log;
import android.view.View;
import android.view.Window;
import android.view.WindowManager;
import android.widget.Button;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.RatingBar;
import android.widget.TextView;

import com.umeng.analytics.MobclickAgent;

import org.json.JSONException;
import org.json.JSONObject;

import java.io.IOException;

import com.autosos.yd.Constants;
import com.autosos.yd.R;
import com.autosos.yd.model.RecordInfo;
import com.autosos.yd.task.AuthGetJSONObjectAsyncTask;
import com.autosos.yd.util.JSONUtil;
import com.autosos.yd.view.*;
import com.autosos.yd.view.AutososBackActivity;

public class RecordInfoActivity extends AutososBackActivity implements View.OnClickListener {

    private static final String TAG = "RecordInfoActivity";
    private View empty;
    private View progressBar;
    private RecordInfo recordInfo;
    private long id;
    private TextView nameView;
    private TextView phoneView;
    private TextView carNumberView;
    private TextView typeView;
    private TextView costView;
    private RatingBar rangeBar;
    private Dialog dialog;
    private TextView viewCommentBtn;
    private ImageView typeImageView;
    private LinearLayout typeImageBackgroundView;
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_record_info);
        id = getIntent().getLongExtra("id", 0);
        int state = getIntent().getIntExtra("state", 0);
        empty = findViewById(R.id.empty);
        progressBar = findViewById(R.id.progressBar);
        typeImageView = (ImageView)findViewById(R.id.type_image);
        typeImageBackgroundView = (LinearLayout)findViewById(R.id.type_image_background);
        nameView = (TextView) findViewById(R.id.name);
        phoneView = (TextView) findViewById(R.id.phone);
        carNumberView = (TextView) findViewById(R.id.carNumber);
        typeView = (TextView) findViewById(R.id.type);
        costView = (TextView) findViewById(R.id.cost);
        rangeBar = (RatingBar) findViewById(R.id.ratingbar);
        setBackground(R.color.color_gray4);
        setLineGone();
        viewCommentBtn = (TextView) findViewById(R.id.viewCommentBtn);
        viewCommentBtn.setOnClickListener(this);
        new GetRecordInfoTask(this).executeOnExecutor(Constants.INFOTHEADPOOL,
                String.format(Constants.HISTORY_ORDER_INFO_URL, id));
        if (state == 401 || state == 400) {
            findViewById(R.id.record_layout).setVisibility(View.GONE);
            findViewById(R.id.close).setVisibility(View.VISIBLE);
        }
    }

    @Override
    public void onClick(View v) {
        switch (v.getId()) {
            case R.id.viewCommentBtn:
                if (dialog != null && dialog.isShowing()) {
                    return;
                }
                dialog = new Dialog(com.autosos.yd.view.RecordInfoActivity.this, R.style.bubble_dialog);
                View view = getLayoutInflater().inflate(R.layout.dialog_msg_content,
                        null);
                TextView tvMsg = (TextView) view.findViewById(R.id.tv_notice_msg);
                Button tvConfirm = (Button) view.findViewById(R.id.btn_notice_confirm);

                tvMsg.setText(recordInfo.getComment());
                tvConfirm.setOnClickListener(new View.OnClickListener() {

                    @Override
                    public void onClick(View v) {
                        dialog.dismiss();
                    }
                });

                dialog.setContentView(view);
                dialog.setCanceledOnTouchOutside(false);
                Window window = dialog.getWindow();
                WindowManager.LayoutParams params = window.getAttributes();
                Point point = JSONUtil.getDeviceSize(com.autosos.yd.view.RecordInfoActivity.this);
                params.width = Math.round(point.x * 5 / 7);
                window.setAttributes(params);
                dialog.show();
                break;
        }
    }

    private class GetRecordInfoTask extends AuthGetJSONObjectAsyncTask {
        public GetRecordInfoTask(Context context) {
            super(context);
        }

        @Override
        protected JSONObject doInBackground(String... params) {
            try {
                String jsonStr = JSONUtil.getStringFromUrl(com.autosos.yd.view.RecordInfoActivity.this,params[0]);
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
            progressBar.setVisibility(View.GONE);
            recordInfo = new RecordInfo(result);
            if (recordInfo != null) {
                if((recordInfo.getRealname() == null ||recordInfo.getRealname().length() <2))
                     nameView.setText("车主");
                else
                    nameView.setText(recordInfo.getRealname());
                phoneView.setText(String.format(getString(R.string.label_phone_number),
                        recordInfo.getMobile()));
                carNumberView.setText(recordInfo.getCar_number());
                if (recordInfo.getService_type() == 1) {
                    typeView.setText(R.string.label_service_type1);
                    typeImageView.setImageResource(R.drawable.icon_service_type1);
                    typeImageBackgroundView.setBackgroundResource(R.drawable.bg_oval_plue);
                    findViewById(R.id.telephone).setBackgroundResource(R.drawable.bg_oval_plue);
                    findViewById(R.id.view_photo).setBackgroundResource(R.drawable.bg_btn_purple);
                } else if (recordInfo.getService_type() == 2) {
                    typeView.setText(R.string.label_service_type2);
                    typeImageView.setImageResource(R.drawable.icon_service_type2);
                    typeImageBackgroundView.setBackgroundResource(R.drawable.bg_oval_blue);
                    findViewById(R.id.telephone).setBackgroundResource(R.drawable.bg_oval_blue);
                    findViewById(R.id.view_photo).setBackgroundResource(R.drawable.bg_btn_blue2);
                } else {
                    typeView.setText(R.string.label_service_type3);
                    typeImageView.setImageResource(R.drawable.icon_service_type3);
                    typeImageBackgroundView.setBackgroundResource(R.drawable.bg_oval_green2);
                    findViewById(R.id.telephone).setBackgroundResource(R.drawable.bg_shape_second_green3);
                    findViewById(R.id.view_photo).setBackgroundResource(R.drawable.bg_second_btn_green2);
                }
                costView.setText(String.valueOf(recordInfo.getTotal_amount()));
                if (recordInfo.getRating() != null) {
                    rangeBar.setRating(Float.parseFloat(recordInfo.getRating()));
                } else {
                    rangeBar.setRating(0);
                }
                if (recordInfo.getComment() != null && !recordInfo.getComment().isEmpty()) {
                    viewCommentBtn.setVisibility(View.VISIBLE);
                } else {
                    viewCommentBtn.setVisibility(View.GONE);
                }
            }
            empty.setVisibility(View.GONE);
            super.onPostExecute(result);
        }
    }

    @Override
    public void onBackPressed() {
        super.onBackPressed();
        overridePendingTransition(0, R.anim.slide_out_right);
    }

    public void onCall(View v) {
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

    public void onViewPhotos(View v) {
        Intent intent = new Intent(com.autosos.yd.view.RecordInfoActivity.this, PhotosActivity.class);
        intent.putExtra("id", id);
        startActivity(intent);
        overridePendingTransition(R.anim.slide_in_right, R.anim.activity_anim_default);
    }

    @Override
    protected void onPause() {
        super.onPause();
        MobclickAgent.onPause(this);
    }

    @Override
    protected void onResume() {
        super.onResume();
        MobclickAgent.onResume(this);
    }

}
