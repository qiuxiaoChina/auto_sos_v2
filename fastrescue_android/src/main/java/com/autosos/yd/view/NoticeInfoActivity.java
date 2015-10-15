package com.autosos.yd.view;

import android.os.AsyncTask;
import android.os.Bundle;
import android.util.Log;
import android.view.View;
import android.widget.TextView;

import com.autosos.yd.Constants;
import com.autosos.yd.R;
import com.autosos.yd.model.Notice;
import com.autosos.yd.task.NewHttpPostTask;
import com.autosos.yd.task.OnHttpRequestListener;
import com.autosos.yd.util.JSONUtil;
import com.umeng.analytics.MobclickAgent;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import java.io.IOException;
import java.util.HashMap;
import java.util.Map;

/**
 * Created by Administrator on 2015/7/31.
 */
public class NoticeInfoActivity extends  AutososBackActivity {
    private TextView titleView;
    private TextView contentView;
    private TextView dateView;
    private Long notice_id;
    private View progressBar;
    private View emptyView;
    private Notice notice;
    private final String TAG ="NoticeInfoActivity";
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.notice_info);
        titleView = (TextView)findViewById(R.id.title);
        contentView = (TextView)findViewById(R.id.content);
        emptyView = findViewById(R.id.empty);
        dateView =(TextView)findViewById(R.id.date);
        notice_id = getIntent().getLongExtra("notice_id", 0);
        progressBar = findViewById(R.id.include);
        new GetNoticeInfoTask().executeOnExecutor(Constants.INFOTHEADPOOL,
                String.format(Constants.NOTICE_CONTENT_URL, notice_id));
        Map<String, Object> map = new HashMap<>();
        new NewHttpPostTask(NoticeInfoActivity.this, new OnHttpRequestListener() {
            @Override
            public void onRequestCompleted(Object obj) {
                Log.e(TAG,obj.toString());
            }

            @Override
            public void onRequestFailed(Object obj) {
                Log.e(TAG,obj.toString());
            }
        }).execute(String.format(Constants.NOTICE_I_GOT, notice_id), map);
    }

    private class GetNoticeInfoTask extends AsyncTask<String, Object, JSONObject> {

        private GetNoticeInfoTask() {

        }

        @Override
        protected JSONObject doInBackground(String... params) {
            try {
                String jsonStr = JSONUtil.getStringFromUrl(com.autosos.yd.view.NoticeInfoActivity.this, params[0]);
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
            emptyView.setVisibility(View.GONE);
            if (result != null) {
                notice = new Notice(result);
                titleView.setText(notice.getTitle());
                dateView.setText(notice.getPublish_at());
                contentView.setText(notice.getContent());
                super.onPostExecute(result);
            }
        }
    }
    @Override
    public void onBackPressed() {
        super.onBackPressed();
        overridePendingTransition(0, R.anim.slide_out_right);
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
