package com.autosos.yd.task;

import android.content.Context;
import android.os.AsyncTask;
import android.util.Log;

import com.autosos.yd.R;

import org.json.JSONException;
import org.json.JSONObject;

import java.io.IOException;
import java.util.Map;

public class NewHttpPutTask extends AsyncTask<Object, Integer, JSONObject> {
    private Context context;
    private com.autosos.yd.task.OnHttpRequestListener requestListener;
    private boolean b;

    public NewHttpPutTask(Context context, com.autosos.yd.task.OnHttpRequestListener requestListener) {
        super();
        this.context = context;
        this.requestListener = requestListener;
    }


    public NewHttpPutTask(Context context, com.autosos.yd.task.OnHttpRequestListener requestListener, boolean b) {
        super();
        this.context = context;
        this.requestListener = requestListener;
        this.b = b;
    }

    @SuppressWarnings("unchecked")
    @Override
    protected JSONObject doInBackground(Object... params) {
        String url = (String) params[0];
        Map<String, Object> map = null;
        String objectString = null;
        if (params.length > 1) {
            if(params[1] instanceof Map) {
                map = (Map<String, Object>) params[1];
            }else {
                objectString = (String) params[1];
            }
        }
        if (!com.autosos.yd.util.JSONUtil.isNetworkConnected(context)) {
            return null;
        }
        try {
            String jsonStr;
            if ((map == null || map.isEmpty()) && com.autosos.yd.util.JSONUtil.isEmpty(objectString)) {
                jsonStr = com.autosos.yd.util.JSONUtil.newPutJson(context, url, map);
            } else {
                com.autosos.yd.entity.ProgressListener progressListener=new com.autosos.yd.entity.ProgressListener() {
                    @Override
                    public void transferred(int transferedBytes) {
                        publishProgress(transferedBytes);
                    }

                    @Override
                    public void setContentLength(long contentLength) {

                    }
                };

                if(map == null || map.isEmpty()){
                    jsonStr = com.autosos.yd.util.JSONUtil.newPutJsonWithAttach(context, url, objectString,
                            progressListener);
                }else {
                    jsonStr = com.autosos.yd.util.JSONUtil.putJsonTextWithAttach(context, url, map,
                            progressListener);
                }
            }
            JSONObject json;
            try {
                json = new JSONObject(jsonStr);
                return json;
            } catch (JSONException e) {
                Log.e("HttpPostTask", jsonStr);
            }
        } catch (IOException e) {
            e.printStackTrace();
        }
        return null;
    }

    @Override
    protected void onPreExecute() {

        super.onPreExecute();
    }

    /*
     * (non-Javadoc)
     *
     * @see android.os.AsyncTask#onPostExecute(java.lang.Object)
     */
    @Override
    protected void onPostExecute(JSONObject obj) {
        if (requestListener != null) {
            if (obj != null) {
                requestListener.onRequestCompleted(obj);
            } else {
                requestListener.onRequestFailed(obj);
            }
        }
        super.onPostExecute(obj);
    }

    /*
     * (non-Javadoc)
     *
     * @see android.os.AsyncTask#onProgressUpdate(Progress[])
     */
    @Override
    protected void onProgressUpdate(Integer... values) {

        super.onProgressUpdate(values);
    }

}
