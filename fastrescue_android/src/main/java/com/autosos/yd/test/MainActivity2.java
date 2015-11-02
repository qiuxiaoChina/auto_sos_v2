package com.autosos.yd.test;

import android.os.Bundle;
import android.app.Activity;
import android.view.Menu;
import android.view.View;
import android.view.ViewGroup;
import android.widget.BaseAdapter;
import android.widget.ListView;

import com.autosos.yd.R;

public class MainActivity2 extends Activity {

	private ListView mListview;

	@Override
	protected void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		setContentView(R.layout.activity_main2);
		
		mListview = (ListView) findViewById(R.id.items_listView1);
		mListview.setAdapter(new BaseAdapter() {
		
			@Override
			public View getView(int position, View convertView, ViewGroup parent) {
				//*******************************************************************************
				//第一种方法
				View inflate = getLayoutInflater().inflate(R.layout.items2, null);
				ListView listView = (ListView) inflate.findViewById(R.id.expandedListView1);
				
				//*******************************************************************************
				/**第二中方法
				 * View inflate = getLayoutInflater().inflate(R.layout.items, null);
				 * ListView listView = (ListView) inflate.findViewById(R.id.items_myListView1);
				 * mListview.setListViewHeightBasedOnChildren(listView);
				 */
				
				
				listView.setAdapter(new BaseAdapter() {
					
					@Override
					public View getView(int position, View convertView, ViewGroup parent) {
						View inflate = getLayoutInflater().inflate(R.layout.lisietm, null);
						
						return inflate;
					}
					
					@Override
					public long getItemId(int position) {
						// TODO Auto-generated method stub
						return 0;
					}
					
					@Override
					public Object getItem(int position) {
						// TODO Auto-generated method stub
						return null;
					}
					
					@Override
					public int getCount() {
						// TODO Auto-generated method stub
						return 2;
					}
				});
//				Utility.setListViewHeightBasedOnChildren(listView);
				return inflate;
			}
			
			@Override
			public long getItemId(int position) {
				// TODO Auto-generated method stub
				return 0;
			}
			
			@Override
			public Object getItem(int position) {
				// TODO Auto-generated method stub
				return null;
			}
			
			@Override
			public int getCount() {
				// TODO Auto-generated method stub
				return 8;
			}
		});
//		Utility.setListViewHeightBasedOnChildren(mListview);
	}

	@Override
	public boolean onCreateOptionsMenu(Menu menu) {
		// Inflate the menu; this adds items to the action bar if it is present.
		getMenuInflater().inflate(R.menu.main, menu);
		return true;
	}

}
