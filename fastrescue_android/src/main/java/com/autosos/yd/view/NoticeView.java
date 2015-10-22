package com.autosos.yd.view;


import android.content.Intent;
import android.os.AsyncTask;
import android.os.Bundle;
import android.util.Log;
import android.view.View;
import android.widget.AdapterView;
import android.widget.ImageView;
import android.widget.ListView;
import android.widget.RelativeLayout;
import android.widget.TextView;

import com.autosos.yd.Constants;
import com.autosos.yd.adapter.ObjectBindAdapter;
import com.autosos.yd.fragment.WorkFragment;
import com.autosos.yd.model.Notice;
import com.autosos.yd.model.Order;
import com.autosos.yd.util.JSONUtil;
import com.autosos.yd.util.Utils;
import com.handmark.pulltorefresh.library.PullToRefreshBase;
import com.handmark.pulltorefresh.library.PullToRefreshListView;
import com.umeng.analytics.MobclickAgent;

import java.io.IOException;
import java.util.ArrayList;
import com.autosos.yd.R;

import org.json.JSONArray;
import org.json.JSONException;

public class NoticeView extends AutososBackActivity implements ObjectBindAdapter.ViewBinder<Notice>,
        AdapterView.OnItemClickListener, PullToRefreshBase.OnRefreshListener<ListView>{
    private final String TAG= "NoticeActivity";
    private PullToRefreshListView listView;
    private View progressBar;
    private ArrayList<Notice> notices;
    private boolean onLoading;
    private ObjectBindAdapter<Notice> adapter;
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.notice);
        listView = (PullToRefreshListView) findViewById(R.id.list);
        notices = new ArrayList<>();
        adapter = new ObjectBindAdapter<>(this, notices,
                R.layout.notice_item, this);
        listView.setAdapter(adapter);
        listView.setOnRefreshListener(this);
        progressBar = findViewById(R.id.include);
        new GetNoticeTask().executeOnExecutor(Constants.LISTTHEADPOOL,
                Constants.NOTICE_URL);
    }

    private class GetNoticeTask extends AsyncTask<String, Object, JSONArray> {

        private GetNoticeTask() {
            onLoading = true;
        }

        @Override
        protected JSONArray doInBackground(String... params) {
            try {
                String jsonStr = JSONUtil.getStringFromUrl(com.autosos.yd.view.NoticeView.this, params[0]);
                if (JSONUtil.isEmpty(jsonStr)) {
                    return null;
                }
                Log.e(TAG, jsonStr);
                return new JSONArray(jsonStr);
            } catch (IOException | JSONException e) {
            }
            return null;
        }

        @Override
        protected void onPostExecute(JSONArray result) {
            progressBar.setVisibility(View.GONE);
            listView.onRefreshComplete();
            notices.clear();
            if (result != null) {
                if (result.length() > 0) {
                    int size = result.length();
                    for (int i = 0; i < size; i++) {
                        Notice order = new Notice(result.optJSONObject(i));
                        notices.add(order);

                    }


                    adapter.notifyDataSetChanged();
                }
            }
            onLoading = false;
            setEmptyView();
            super.onPostExecute(result);
        }
    }

    public void setEmptyView() {
        if (notices.isEmpty()) {
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

            if (JSONUtil.isNetworkConnected(com.autosos.yd.view.NoticeView.this)) {
                textEmptyHint.setText(R.string.msg_notice_empty);
            } else {
                textEmptyHint.setText(R.string.msg_net_disconnected);
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

    @Override
    public void onItemClick(AdapterView<?> parent, View view, int position, long id) {
    }

    @Override
    public void onRefresh(PullToRefreshBase<ListView> refreshView) {
        if (!onLoading) {
            new GetNoticeTask().executeOnExecutor(Constants.LISTTHEADPOOL,
                    Constants.NOTICE_URL);
        }
    }

    @Override
    public void setViewValue(View view, final Notice notice, int position) {
        if (view.getTag() == null) {
            ViewHolder holder = new ViewHolder();
            holder.titleView = (TextView) view.findViewById(R.id.title);
            holder.contentView = (TextView) view.findViewById(R.id.content);
            holder.noticeView = (RelativeLayout) view.findViewById(R.id.notice);
            view.setTag(holder);
        }
        ViewHolder holder = (ViewHolder) view.getTag();
        String title = notice.getTitle();
        if(title.length() > 17)
            title = title.substring(0,17)+"...";
        holder.titleView.setText(title);
        holder.contentView.setText(notice.getPublish_at());
        holder.noticeView.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Intent intent = new Intent(NoticeView.this, NoticeInfoActivity.class);
                intent.putExtra("notice_id",notice.getId());
                startActivity(intent);
                overridePendingTransition(R.anim.slide_in_right, R.anim.activity_anim_default);
            }
        });
    }
    private class ViewHolder {
        TextView titleView;
        TextView contentView;
        RelativeLayout noticeView;
    }

}
