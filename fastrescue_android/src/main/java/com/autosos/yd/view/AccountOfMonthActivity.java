package com.autosos.yd.view;

import android.app.Activity;
import android.os.Bundle;
import android.view.View;
import android.widget.AdapterView;
import android.widget.ListView;

import com.autosos.yd.R;
import com.autosos.yd.adapter.ObjectBindAdapter;
import com.autosos.yd.model.Notice;
import com.handmark.pulltorefresh.library.PullToRefreshBase;

/**
 * Created by Administrator on 2015/10/19.
 */
public class AccountOfMonthActivity extends AutososBackActivity implements ObjectBindAdapter.ViewBinder<Notice>,
        AdapterView.OnItemClickListener, PullToRefreshBase.OnRefreshListener<ListView> {

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_account_month);
    }

    public void onBackPressed() {
        super.onBackPressed();
        overridePendingTransition(0, R.anim.slide_out_right);
    }

    @Override
    public void onItemClick(AdapterView<?> parent, View view, int position, long id) {

    }

    @Override
    public void onRefresh(PullToRefreshBase<ListView> refreshView) {

    }

    @Override
    public void setViewValue(View view, Notice notice, int position) {

    }
}
