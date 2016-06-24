package com.autosos.yd.task;

import android.content.Context;
import android.os.AsyncTask;

import com.autosos.yd.util.JSONUtil;

import org.json.JSONException;
import org.json.JSONObject;

import java.io.IOException;

public class HttpGetTask extends AsyncTask<Object, Integer, JSONObject> {
    private Context context;
    private com.autosos.yd.task.OnHttpRequestListener requestListener;

    public HttpGetTask(Context context, OnHttpRequestListener requestListener) {
        super();
        this.context = context;
        this.requestListener = requestListener;
    }

    @Override
    protected JSONObject doInBackground(Object... params) {
        String url = (String) params[0];
        try {
            String jsonStr;
            jsonStr = JSONUtil.getStringFromUrl(context,url);
            if (!JSONUtil.isEmpty(jsonStr)) {
                try {
                    return new JSONObject(jsonStr);
                } catch (JSONException e) {
                    // TODO Auto-generated catch block
                    e.printStackTrace();
                }
            } else {
                return new JSONObject();
            }
        } catch (IOException e) {
            e.printStackTrace();
        }
        return null;
    }

    @Override
    protected void onPostExecute(JSONObject obj) {
        if(requestListener!=null) {
            if (obj != null) {
                requestListener.onRequestCompleted(obj);
            } else {
                requestListener.onRequestFailed(obj);
            }
        }
        super.onPostExecute(obj);
    }

}
