package com.autosos.yd.view;

import android.os.Bundle;
import android.view.View;
import android.widget.AdapterView;
import android.widget.ListView;

import com.autosos.yd.R;
import com.autosos.yd.adapter.ObjectBindAdapter;
import com.autosos.yd.model.LastestLog;
import com.handmark.pulltorefresh.library.PullToRefreshBase;
import com.handmark.pulltorefresh.library.PullToRefreshListView;

import java.util.ArrayList;

/**
 * Created by Administrator on 2015/10/26.
 */
public class BillDetailsActivity extends AutososBackActivity implements PullToRefreshBase.OnRefreshListener<ListView>,
        ObjectBindAdapter.ViewBinder<LastestLog>,AdapterView.OnItemClickListener {

    private ArrayList<LastestLog> lastestLogs;
    private PullToRefreshListView listView;
    private ObjectBindAdapter<LastestLog> adapter;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_month_details);
        listView = (PullToRefreshListView) findViewById(R.id.list);
        lastestLogs = new ArrayList<>();
        adapter = new ObjectBindAdapter<>(this, lastestLogs,
                R.layout.lastestlog_item, this);
        listView.setAdapter(adapter);
        listView.setOnRefreshListener(this);
    }

    @Override
    public void onItemClick(AdapterView<?> parent, View view, int position, long id) {

    }

    @Override
    public void onRefresh(PullToRefreshBase<ListView> refreshView) {

    }

    @Override
    public void setViewValue(View view, LastestLog lastestLog, int position) {

    }
}
