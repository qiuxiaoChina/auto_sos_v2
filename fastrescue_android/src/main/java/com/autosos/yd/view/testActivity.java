package com.autosos.yd.view;

import android.app.Activity;
import android.graphics.Bitmap;
import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.AdapterView;
import android.widget.BaseAdapter;
import android.widget.Button;
import android.widget.ImageView;
import android.widget.ListView;
import android.widget.RelativeLayout;
import android.widget.TextView;

import com.autosos.yd.R;
import com.autosos.yd.test.Utility;
import com.baidu.mapapi.map.MapView;
import com.makeramen.rounded.RoundedImageView;

import java.util.ArrayList;

/**
 * Created by Administrator on 2015/8/13.
 */
public class testActivity extends Activity{
    private RelativeLayout testView ;
    private final String TAG = "TestActivity";
    private Button test;
    private TextView timeView;
    private TextView test1;
    private TextView test2;
    private MapView mMapView ;
    private ImageView spirtView;
    private RelativeLayout cavasView;
    private int width,height;
    private boolean ismoving = false;
    private Bitmap bitmap;
    private RoundedImageView picture;
    private RoundedImageView avatarView;
    private ArrayList<MyData> myList = new ArrayList<MyData>();
    private myAdapter myAdapter;
    private myAdapter myAdapter2;
    private LayoutInflater inflater = null;
    private LayoutInflater inflater2 = null;
    private int oldPostion = -1;
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
//        setContentView(R.layout.test);
//        picture = (RoundedImageView) findViewById(R.id.picture);
//        new getImageTask().execute("http://www.qqai.net/fa/UploadPic/2012-7/20127417475611762.jpg");
        setContentView(R.layout.activity_main);
        final ListView list = (ListView) findViewById(R.id.draggable_list);

        MyData data = new MyData();
        data.name = "11";
        myList.add(data);

        data = new MyData();
        data.name = "22";
        myList.add(data);

        data = new MyData();
        data.name = "33";
        myList.add(data);

        data = new MyData();
        data.name = "44";
        myList.add(data);

        data = new MyData();
        data.name = "55";
        myList.add(data);

        data = new MyData();
        data.name = "66";
        myList.add(data);

        data = new MyData();
        data.name = "77";
        myList.add(data);

        inflater = LayoutInflater.from(this);
        myAdapter = new myAdapter();
        list.setAdapter(myAdapter);

        list.setOnItemClickListener(new AdapterView.OnItemClickListener() {

            @Override
            public void onItemClick(AdapterView<?> parent, View view, int position, long id) {
                MyData data = myList.get(position);
//                if (oldPostion == position) {
//                    if (data.expand)  {
//                        oldPostion = -1;
//                    }
//                    data.expand = !data.expand;
//                }else{
//                    oldPostion = position;
//                    data.expand = true;
//                }

                data.expand = !data.expand;

//                int totalHeight = 0;
//                for(int i=0;i<myAdapter.getCount();i++) {
//                    View viewItem = myAdapter.getView(i, null, list);//这个很重要，那个展开的item的measureHeight比其他的大
//                    viewItem.measure(0, 0);
//                    totalHeight += viewItem.getMeasuredHeight();
//                }
//
//                ViewGroup.LayoutParams params = list.getLayoutParams();
//                params.height = totalHeight
//                        + (list.getDividerHeight() * (list.getCount() - 1));
//                list.setLayoutParams(params);
                myAdapter.notifyDataSetChanged();
            }
        });
    }

    private class myAdapter extends BaseAdapter {


        public int getCount() {
            return myList.size();
        }

        @Override
        public Object getItem(int position) {
            return myList.get(position);
        }

        @Override
        public long getItemId(int position) {
            return position;
        }

        @Override
        public View getView(int position, View convertView, ViewGroup parent) {

            MyTag tag = new MyTag();
            MyData data = myList.get(position);

            if(convertView == null) {
                convertView = inflater.inflate(R.layout.item_layout, null);
                tag.item1 = (TextView)convertView.findViewById(R.id.item1);
                ListView list2 = (ListView) convertView.findViewById(R.id.list_test);
                list2.setAdapter(new BaseAdapter() {
                    @Override
                    public int getCount() {
                        return 3;
                    }

                    @Override
                    public Object getItem(int position) {
                        return null;
                    }

                    @Override
                    public long getItemId(int position) {
                        return 0;
                    }

                    @Override
                    public View getView(int position, View convertView, ViewGroup parent) {
                        View inflate = getLayoutInflater().inflate(R.layout.lisietm, null);
                        return inflate;
                    }
                });
                Utility.setListViewHeightBasedOnChildren(list2);
                tag.item2 = (RelativeLayout) convertView.findViewById(R.id.item2);
                convertView.setTag(tag);
            }else{
                tag = (MyTag)convertView.getTag();
            }
            if(data.expand) {
                tag.item2.setVisibility(View.VISIBLE);
            }else{
                tag.item2.setVisibility(View.GONE);
            }

            tag.item1.setText(data.name);
            return convertView;
        }

    }

    private class MyTag{
        private TextView item1;
        private RelativeLayout item2;
    }

    private class MyData{
        boolean expand;
        String name;
    }



//    @Override
//    protected void onResume() {
//        super.onResume();
//        new GetAccountInfoTask().execute(String.format(Constants.GET_ORDER_FEE,1910));
//    }
//
//    public void talk(View v){
//        PushManager.getInstance().turnOffPush(SplashActivity.splashActivity);
//        Log.e(TAG, "---guan le ----");
//        Toast.makeText(this,"getui turn off !",Toast.LENGTH_LONG).show();
//    }
//    public void talk2(View v){
//        PushManager.getInstance().initialize(SplashActivity.splashActivity);
////        PushManager.getInstance().turnOnPush(SplashActivity.splashActivity);
//        Log.e("getui ","======================   getui serve turnon     =====================");
//    }
//
//    private class getImageTask extends AsyncTask<String, Void, Bitmap>{
//
//
//        @Override
//        protected Bitmap doInBackground(String... params) {
//            try {
//                URL url = new URL("http://www.qqai.net/fa/UploadPic/2012-7/20127417475611762.jpg"); //path图片的网络地址
//                HttpURLConnection httpURLConnection = (HttpURLConnection) url.openConnection();
//                if(httpURLConnection.getResponseCode() == HttpURLConnection.HTTP_OK){
//                    bitmap  = BitmapFactory.decodeStream(httpURLConnection.getInputStream());
//
//                    System.out.println("加载网络图片完成");
//                }else{
//                    System.out.println("加载网络图片失败");
//                }
//            } catch (IOException e) {
//                e.printStackTrace();
//            }
//            return bitmap;
//        }
//
//        @Override
//        protected void onPostExecute(Bitmap bitmap) {
//            super.onPostExecute(bitmap);
////            com.autosos.yd.task.ImageLoadTask task = new com.autosos.yd.task.ImageLoadTask(avatarView, null, 0);
////            com.autosos.yd.task.AsyncBitmapDrawable image = new com.autosos.yd.task.AsyncBitmapDrawable(rootView.getResources(),
////                    com.autosos.yd.Constants.PLACEHOLDER_AVATAR, task);
////            task.loadImage(person.getAvatar(), avatarView.getMeasuredWidth(), com.autosos.yd.util.ScaleMode.WIDTH, image);
//            ImageLoadTask task = new ImageLoadTask(picture, null,0);
//            AsyncBitmapDrawable image = new AsyncBitmapDrawable(getResources(), Constants.PLACEHOLDER_AVATAR, task);
//            task.loadImage("http://www.qqai.net/fa/UploadPic/2012-7/20127417475611762.jpg",180,com.autosos.yd.util.ScaleMode.WIDTH, image);
//
////            picture.setImageBitmap(bitmap);
//        }
//    }
//
//    private class GetAccountInfoTask extends AsyncTask<String, Object, JSONObject> {
//
//        @Override
//        protected JSONObject doInBackground(String... params) {
//            try {
//                String jsonStr = JSONUtil.getStringFromUrl(testActivity.this, params[0]);
//                jsonStr.length();
//                if (JSONUtil.isEmpty(jsonStr)){
//                    return null;
//                }
//                Log.e("test", jsonStr);
//                return new JSONObject(jsonStr);
//            } catch (IOException | JSONException e) {
//
//            }
//            return null;
//
//        }
//
//        @Override
//        protected void onPostExecute(JSONObject result) {
//            super.onPostExecute(result);//前后不知道一不一样，先试试
//
//        }
//    }
}
